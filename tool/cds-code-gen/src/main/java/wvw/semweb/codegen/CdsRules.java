package wvw.semweb.codegen;

import static org.apache.jen3.n3.N3ModelSpec.Types.N3_MEM_FP_INF;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import wvw.utils.IOUtils;
import wvw.utils.rdf.NS;

public class CdsRules {

	private static final Logger log = LogManager.getLogger(CdsRules.class);

	public static void main(String[] args) throws Exception {
		JenaSystem.init();

//		testCase("data/case1.csv", "diabetes-case1.n3", "DMTO:DMTO_0001701", (record, m) -> patientCase1(record, m));
//		testCase("data/case2.csv", "diabetes-case2.n3", "DMTO:DMTO_0001701", (record, m) -> patientCase2(record, m));
		testCase("data/case3.csv", "diabetes-case3.n3", "DMTO:recommend_test", (record, m) -> patientCase3(record, m));
	}

	public static void testCase(String casePath, String rulesPath, String predQName,
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

			Iterator<Statement> results = m.listStatements(null, m.createResource(NS.toUri(predQName)),
					(Resource) null);
			if (results.hasNext())
				matched.add(id);

			log.info("record " + ++cnt + (results.hasNext() ? " (matched)" : ""));
		}

		log.info("matched: " + matched);
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

		int dias = Double.valueOf(record.get("SYSTOLIC")).intValue();
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
}
