package wvw.semweb.codegen;

import java.io.File;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import wvw.semweb.codegen.gen.GenerateCode;
import wvw.semweb.codegen.gen.GenerateCode.CodeTypes;
import wvw.semweb.codegen.parse.ParseModelLogic;

public class CodeGen {

	private static final Logger log = LogManager.getLogger(CodeGen.class);

	/** 
	 * Will generate Solidity and JavaScript code for 4 cases found in src/main/resources/. 
	 * 
	 * Generated code will appear under src/main/resources.
	 * 
	 * @param args
	 * @throws Exception
	 */
	
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
