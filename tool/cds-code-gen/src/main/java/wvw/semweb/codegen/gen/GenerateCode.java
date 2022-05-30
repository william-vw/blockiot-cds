package wvw.semweb.codegen.gen;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

import org.apache.commons.text.CaseUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import wvw.semweb.codegen.model.adt.CodeModel;
import wvw.semweb.codegen.model.adt.ModelProperty;
import wvw.semweb.codegen.model.logic.Assignment;
import wvw.semweb.codegen.model.logic.Block;
import wvw.semweb.codegen.model.logic.CodeLogic;
import wvw.semweb.codegen.model.logic.CodeStatement;
import wvw.semweb.codegen.model.logic.Comparison;
import wvw.semweb.codegen.model.logic.Condition;
import wvw.semweb.codegen.model.logic.Conjunction;
import wvw.semweb.codegen.model.logic.Disjunction;
import wvw.semweb.codegen.model.logic.IfThen;
import wvw.semweb.codegen.model.logic.MultiCondition;
import wvw.semweb.codegen.model.adt.ModelADT;
import wvw.semweb.codegen.parse.post.ModelPostprocessor.PostprocessTypes;

public abstract class GenerateCode {

	protected static final Logger log = LogManager.getLogger(GenerateCode.class);

	public enum CodeTypes {

		JAVASCRIPT("js"), SOLIDITY("sol", PostprocessTypes.MERGE_STRUCTS_W_ARRAYS_IN_ROOT);

		private String ext;
		private PostprocessTypes[] requirements;

		private CodeTypes(String ext, PostprocessTypes... requirements) {
			this.ext = ext;
			this.requirements = requirements;
		}

		public String getExt() {
			return ext;
		}

		public PostprocessTypes[] getRequirements() {
			return requirements;
		}
	}

	public static GenerateCode create(CodeTypes type) {
		switch (type) {

		case JAVASCRIPT:
			return new GenerateJavaScript();

		case SOLIDITY:
			return new GenerateSolidity();
		}

		return null;
	}

	public abstract void generate(CodeModel model, CodeLogic logic, File output) throws IOException;

	protected boolean isInitField(ModelProperty prp, ModelADT ofStruct) {
		return includeField(prp, ofStruct) && !prp.requiresArray();
	}

	// type property only needed/ possible if adt has constants
	// (e.g., in solidity, these will be added to a separate enum)

	protected boolean includeField(ModelProperty prp, ModelADT ofStruct) {
		return (!prp.isTypePrp() || ofStruct.hasConstants());
	}

	protected String printLiteral(Object o) {
		if (o instanceof String) {
			String str = (String) o;

			if (str.contains("\n"))
				str = str.replace("\n", "\\n");

			return "\"" + str + "\"";

		} else
			return o.toString();
	}

	protected String genStatement(CodeStatement stmt, int level) {
		switch (stmt.getStatementType()) {

		case COND:
			return genCondition((Condition) stmt, level);

		case IF_THEN:
			return genIfThen((IfThen) stmt, level);

		case BLOCK:
			return genBlock((Block) stmt);

		case ASSIGN:
			return genAssignment((Assignment) stmt);

		default:
			return null;
		}
	}

	protected abstract String genIfThen(IfThen ifThen, int level);

	protected abstract String genBlock(Block block);

	protected abstract String genAssignment(Assignment assign);

	protected String genCondition(Condition cond, int level) {
		switch (cond.getConditionType()) {

		case CONJ:
			return genConjunction((Conjunction) cond, level);

		case DISJ:
			return genDisjunction((Disjunction) cond, level);

		case CMP:
			return genComparison((Comparison) cond);

		default:
			return null;
		}
	}

	protected String genConjunction(Conjunction conj, int level) {
		return genConditionList(conj, "&&", level);
	}

	protected String genDisjunction(Disjunction disj, int level) {
		return genConditionList(disj, "||", level);
	}

	protected String genConditionList(MultiCondition list, String conn, int level) {
		String ret = list.getConditions().stream().map(c -> genCondition(c, level + 1))
				.collect(Collectors.joining("\n\t" + conn + " "));

		if (level > 0)
			return "(" + ret + ")";
		else
			return ret;
	}

	protected abstract String genComparison(Comparison cmp);

	protected String safeName(String str, boolean clsName) {
		str = str.replace(".", "pt").replace("-", "_").replace(",", "and");

		return CaseUtils.toCamelCase(str, clsName, new char[] { ' ', '_', '-' });

	}
}
