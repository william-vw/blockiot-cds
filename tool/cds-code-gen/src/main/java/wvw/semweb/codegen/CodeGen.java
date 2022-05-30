package wvw.semweb.codegen;

import java.io.File;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import wvw.semweb.codegen.gen.GenerateCode;
import wvw.semweb.codegen.gen.GenerateCode.CodeTypes;
import wvw.semweb.codegen.parse.ParseModelLogic;

//(important)

// TODO in paper, consider talking about InsertAllOntologyConstants:
// i.e., add all sub-types of a given type as constants
// (should ideally also include URIs of the given type)

// TODO add createPatient methods to smart contracts
// (unfortunately, these need to be created separately *per* scenario)

// (future work)

//TODO limitations of adt merging for solidity: 
//combination of lifestyle with drug subplans will likely not work

// TODO assuming that the rule ordering reflects the chaining sequence

// TODO can only create new adt with *non* array-like properties
// (e.g., try DrugSubPlan; remove functional property type)
// (in solidity: "TypeError: Struct containing a (nested) mapping cannot be constructed")

// TODO have separate post-processing for adt model
// (most are only needed after all the rules are processed; currently doing duplicate work)

//TODO post-processing where adts sharing a (non-trivial) superclass (i.e., not owl:Thing, entity, ..)
//are merged together

//(minor)

//TODO properly parametrize ModelVisitorImpl code (e.g., CodeLogicVisitor, CodeModelVisitor)

public class CodeGen {

	private static final Logger log = LogManager.getLogger(CodeGen.class);

	public static void main(String[] args) throws Exception {
		for (CodeTypes codeType : CodeTypes.values()) {
			log.info("> converting into " + codeType + "\n");
			
			generateCode(new File("diabetes-case1.n3"), new File("DMTO2.n3"), new File("src/main/resources/"),
					codeType);
			generateCode(new File("diabetes-case2.n3"), new File("DMTO2.n3"), new File("src/main/resources/"),
					codeType);
			generateCode(new File("diabetes-case3.n3"), new File("DMTO2.n3"), new File("src/main/resources/"),
					codeType);
			generateCode(new File("diabetes-case4.n3"), new File("DMTO2.n3"), new File("src/main/resources/"),
					codeType);
			
			log.info("\n");
		}
	}

	public static void generateCode(File ruleFile, File ontologyFile, File outFolder, CodeTypes codeType)
			throws Exception {

		log.info("-- parsing model and logic");

		ParseModelLogic parser = new ParseModelLogic();
		parser.parse(ruleFile, ontologyFile, codeType.getRequirements());

		log.info("\n");
		log.info("-- generating code");

		GenerateCode genCode = GenerateCode.create(codeType);

		String outFile = ruleFile.getName().substring(0, ruleFile.getName().lastIndexOf(".")) + "." + codeType.getExt();
		outFolder = new File(outFolder, outFile);
		genCode.generate(parser.getModel(), parser.getLogic(), outFolder);

		log.info("\ncode written to: " + outFolder.getAbsolutePath());
	}
}
