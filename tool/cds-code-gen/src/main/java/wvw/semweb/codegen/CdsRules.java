package wvw.semweb.codegen;

import static org.apache.jen3.n3.N3ModelSpec.Types.N3_MEM_FP_INF;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.rdf.model.ModelFactory;
import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.rdf.model.Statement;
import org.apache.jen3.sys.JenaSystem;
import org.apache.jen3.vocabulary.RDF;
import org.apache.jena.ext.com.google.common.io.Files;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import wvw.utils.IOUtils;
import wvw.utils.rdf.NS;

public class CdsRules {

	private static final Logger log = LogManager.getLogger(CdsRules.class);

	public static void main(String[] args) throws Exception {
		JenaSystem.init();

		// - for n3 rules, determine "matching" patients
		// (i.e., for whom recommendations are inferred)
//		testCases();

		// - compare n3 output with solidity output
		compareCases();
	}

	public static void testCases() throws Exception {
		testCase("data/case1.csv", "diabetes-case1.n3", "results/n3/case1", "DMTO:DMTO_0001701", null,
				(record, m) -> patientCase1(record, m));
		testCase("data/case2.csv", "diabetes-case2.n3", "results/n3/case2.1", "DMTO:DMTO_0001701", "DMTO:DMTO_0001712",
				(record, m) -> patientCase2(record, m));
		testCase("data/case2.csv", "diabetes-case2.n3", "results/n3/case2.2", "DMTO:DMTO_0001701", "DMTO:DMTO_0001710",
				(record, m) -> patientCase2(record, m));
		testCase("data/case3.csv", "diabetes-case3.n3", "results/n3/case3", "DMTO:recommend_test", null,
				(record, m) -> patientCase3(record, m));
	}

	private static void testCase(String casePath, String rulesPath, String outPath, String predUri, String typeUri,
			BiConsumer<CSVRecord, N3Model> genPatient) throws Exception {

		List<String> matched = new ArrayList<>();

		int cnt = 0;

		Reader in = new InputStreamReader(IOUtils.getResourceStream(CdsRules.class, casePath));
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build()
				.parse(in);
		for (CSVRecord record : records) {
			String id = record.get("HADM_ID");

			N3ModelSpec spec = N3ModelSpec.get(N3_MEM_FP_INF);
			N3Model m = ModelFactory.createN3Model(spec);

			// - patient rdf
			genPatient.accept(record, m);

			// - rules
			m.read(IOUtils.getResourceStream(CdsRules.class, rulesPath), "N3");

			boolean success = false;

			Iterator<Statement> results = m.listStatements(null, m.createResource(NS.toUri(predUri)), (Resource) null);
			if (results.hasNext()) {

				if (typeUri != null) {
					while (results.hasNext()) {
						Resource result = results.next().getObject();

						if (result.hasProperty(RDF.type, m.createResource(NS.toUri(typeUri)))) {
							success = true;
							matched.add(id);

							break;
						}
					}

				} else {
					success = true;
					matched.add(id);
				}
			}

			log.info("record " + ++cnt + " (" + id + ")" + (success ? " (success)" : ""));
		}

		log.info("matched: " + matched);

		String out = matched.stream().collect(Collectors.joining("\n"));
		Files.write(out.getBytes(), new File("src/main/resources/" + outPath));
	}

	private static void patientCase1(CSVRecord record, N3Model m) {
		String id = record.get("HADM_ID");

		String bmi = record.get("BMI");
		Resource examRes = m.createResource(NS.toUri("cg:exam" + id));

		m.add(examRes, RDF.type, m.createResource(NS.toUri("DDO:DDO_0000230")));
		m.add(examRes, m.createResource(NS.toUri("DDO:DDO_0000134")), m.createLiteral(bmi, XSDDatatype.XSDint));

		Resource patRes = m.createResource(NS.toUri("cg:patient" + id));
		Resource profRes = m.createResource(NS.toUri("cg:profile" + id));

		m.add(patRes, RDF.type, m.createResource(NS.toUri("DMTO:DMTO_0000021")));
		m.add(patRes, m.createResource(NS.toUri("DMTO:DMTO_0001667")), profRes);
		m.add(profRes, m.createResource(NS.toUri("DMTO:DMTO_0001671")), m.createResource("cg:plan" + id));

		Resource diagRes = m.createResource(NS.toUri("cg:diagnosis" + id));
		Resource diabRes = m.createResource(NS.toUri("cg:diabetes" + id));

		m.add(profRes, m.createResource(NS.toUri("DDO:DDO_0000114")), diagRes);
		m.add(diagRes, m.createResource(NS.toUri("DMTO:DMTO_0001673")), diabRes);
		m.add(diabRes, RDF.type, m.createResource(NS.toUri("DDO:DDO_0003905")));
	}

	private static void patientCase2(CSVRecord record, N3Model m) {
		String id = record.get("HADM_ID");

		int sys = Double.valueOf(record.get("SYSTOLIC")).intValue();
		Resource sysRes = m.createResource(NS.toUri("cg:sys" + id));

		m.add(sysRes, RDF.type, m.createResource(NS.toUri("DDO:DDO_0000239")));
		m.add(sysRes, m.createResource(NS.toUri("DDO:DDO_0000134")), m.createTypedLiteral(sys));

		int dias = Double.valueOf(record.get("DIASTOLIC")).intValue();
		Resource diasRes = m.createResource(NS.toUri("cg:dias" + id));

		m.add(diasRes, RDF.type, m.createResource(NS.toUri("DDO:DDO_0000241")));
		m.add(diasRes, m.createResource(NS.toUri("DDO:DDO_0000134")), m.createTypedLiteral(dias));

		Resource patRes = m.createResource(NS.toUri("cg:patient" + id));
		Resource profRes = m.createResource(NS.toUri("cg:profile" + id));

		m.add(patRes, RDF.type, m.createResource(NS.toUri("DMTO:DMTO_0000021")));
		m.add(patRes, m.createResource(NS.toUri("DMTO:DMTO_0001667")), profRes);
		m.add(profRes, m.createResource(NS.toUri("DMTO:DMTO_0001671")), m.createResource(NS.toUri("cg:plan" + id)));
	}

	private static void patientCase3(CSVRecord record, N3Model m) {
		String id = record.get("HADM_ID");

		Resource patRes = m.createResource(NS.toUri("cg:patient" + id));
		Resource profRes = m.createResource(NS.toUri("cg:profile" + id));

		m.add(patRes, RDF.type, m.createResource(NS.toUri("DMTO:DMTO_0000021")));
		m.add(patRes, m.createResource(NS.toUri("DMTO:DMTO_0001667")), profRes);

		String bmi = record.get("BMI");
		Resource examRes = m.createResource(NS.toUri("cg:exam" + id));

		m.add(profRes, m.createResource(NS.toUri("DDO:DDO_0000238")), examRes);

		m.add(examRes, RDF.type, m.createResource(NS.toUri("DDO:DDO_0000230")));
		m.add(examRes, m.createResource(NS.toUri("DDO:DDO_0000134")), m.createLiteral(bmi, XSDDatatype.XSDint));

		String ethn = toEthnicityCode(record.get("ETHNICITY"));
		if (ethn != null) {
			Resource ethnRes = m.createResource(NS.toUri(ethn));
			m.add(profRes, m.createResource(NS.toUri("DDO:DDO_0000140")), ethnRes);
		}
	}

	private static String toEthnicityCode(String ethnicity) {
		if (ethnicity.contains("HISPANIC") || ethnicity.contains("LATINO"))
			return "DTO:DTO:0001937";
		if (ethnicity.contains("AFRICAN AMERICAN"))
			return "DTO:DTO:0001936";
		else
			return null;
	}

	public static void compareCases() throws Exception {
//		compareCase("results/solidity/case1.csv", "MANAGEMENT", "results/n3/case1");
//		compareCase("results/solidity/case2.csv", "LIFESTYLE_THERAPY", "results/n3/case2.1");
//		compareCase("results/solidity/case2.csv", "PHARMACOLOGIC", "results/n3/case2.2");
		compareCase("results/solidity/case3.csv", "SCREENING", "results/n3/case3");
	}

	private static void compareCase(String solResPath, String recommCol, String n3ResPath) throws Exception {
		List<String> solOutput = new ArrayList<>();
		Reader in = new InputStreamReader(IOUtils.getResourceStream(CdsRules.class, solResPath));
		CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build().parse(in).forEach(record -> {
			if (record.get(recommCol).equals("1"))
				solOutput.add(record.get("HADM_ID"));
		});

		List<String> n3Output = new BufferedReader(
				new InputStreamReader(IOUtils.getResourceStream(CdsRules.class, n3ResPath))).lines()
						.collect(Collectors.toList());

		log.info("solidity (#" + solOutput.size() + "): " + solOutput);
		log.info("n3 (#" + n3Output.size() + "): " + n3Output);
		log.info("");

		for (String n3Res : n3Output) {
			if (!solOutput.contains(n3Res))
				log.error("n3 result " + n3Res + " not found in solidity output");
		}

		for (String solRes : solOutput) {
			if (!n3Output.contains(solRes))
				log.error("solidity result " + solRes + " not found in n3 output");
		}
	}
}
