package wvw.semweb.codegen.gen;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

import org.apache.jen3.util.IOUtils;

import wvw.semweb.codegen.model.adt.CodeModel;
import wvw.semweb.codegen.model.adt.ModelElement;
import wvw.semweb.codegen.model.adt.ModelProperty;
import wvw.semweb.codegen.model.logic.Assignment;
import wvw.semweb.codegen.model.logic.Block;
import wvw.semweb.codegen.model.logic.CodeLogic;
import wvw.semweb.codegen.model.logic.Comparison;
import wvw.semweb.codegen.model.logic.CreateStruct;
import wvw.semweb.codegen.model.logic.IfThen;
import wvw.semweb.codegen.model.logic.Literal;
import wvw.semweb.codegen.model.logic.NodePath;
import wvw.semweb.codegen.model.logic.Operand;
import wvw.semweb.codegen.model.logic.StructConstant;
import wvw.semweb.codegen.model.logic.Variable;
import wvw.semweb.codegen.model.logic.Comparison.Comparators;
import wvw.semweb.codegen.model.logic.Condition.Conditions;
import wvw.semweb.codegen.model.adt.ModelADT;
import wvw.semweb.codegen.parse.rule.ann.ParameterAnnotation;
import wvw.semweb.codegen.parse.rule.ann.ParameterAnnotation.ParameterTypes;
import wvw.semweb.codegen.parse.rule.ann.RuleAnnotation.AnnotationTypes;

public class GenerateJavaScript extends GenerateCode {

	private StringBuffer classes = new StringBuffer();
	private StringBuffer logic = new StringBuffer();

	@Override
	public void generate(CodeModel codeModel, CodeLogic codeLogic, File output) throws IOException {
		codeModel.getAllStructs().forEach(s -> genClass(s));

		// also include load-param type (in JS, this is also given as input parameter)
		String fnParams = codeLogic.getAnnotations().getAll().stream().filter(a -> a.getType() == AnnotationTypes.PARAM)
				.sorted((a1, a2) -> ((ParameterAnnotation) a1).getParameterType() == ParameterTypes.FUNCTION ? -1 : 1)
				.map(a -> a.getNode().getName()).collect(Collectors.joining(", "));

		logic.append("function execute(").append(fnParams).append(") {\n");

		String contents = codeLogic.getStatements().stream().map(it -> genStatement(it, 0))
				.collect(Collectors.joining("\n\n"));
		// indent contents of the function
		contents = "\t" + contents.replace("\n", "\n\t");
		logic.append(contents);

		logic.append("\n}");

//		log.info(classes);
//		log.info(ifThens);

		IOUtils.writeToFile(classes.toString() + "\n\n", output);
		IOUtils.writeToFile(logic.toString(), output, true);
	}

	@Override
	protected String genComparison(Comparison cmp) {
		String cpr = genCmp(cmp.getCmp());
		String op2 = genOperand(cmp.getOp2());

		String part2;
		switch (cmp.getCmp()) {

		case EX:
			part2 = genCmp(Comparators.NEQ) + " " + "undefined";
			break;

		case NEX:
			part2 = genCmp(Comparators.EQ) + " " + "undefined";
			break;

		default:
			part2 = cpr + " " + op2;
			break;
		}

		String op1 = genOperand(cmp.getOp1());
		return op1 + " " + part2;

	}

	@Override
	protected String genIfThen(IfThen ifThen, int level) {
		String ifContents = genCondition(ifThen.getCondition(), level);

		String thenContents = genStatement(ifThen.getThen(), level);
		// indent 'then' part of the ifThen
		thenContents = "\t" + thenContents.replace("\n", "\n\t").trim();

		String ret = "if (" + ifContents + ") {\n";
		// add extra newline after conditions
		if (ifThen.getCondition().getConditionType() == Conditions.CONJ)
			ret += "\n";
		ret += thenContents + "\n}";

		return ret;
	}

	@Override
	protected String genBlock(Block block) {
		return block.getStatements().stream().map(stmt -> genStatement(stmt, 0)).collect(Collectors.joining("\n"))
				+ "\n";
	}

	@Override
	protected String genAssignment(Assignment assign) {
		String op1 = genOperand(assign.getOp1());
		if (assign.getOp1() instanceof Variable)
			op1 = "var " + op1;

		String op2 = genOperand(assign.getOp2());

		String ret = null;
		if (Util.involvesArrayAssign(assign.getOp1()))
			ret = op1 + "[" + op2 + "." + fieldName(ModelProperty.typeProperty()) + "] = " + op2 + ";";
		else
			ret = op1 + " = " + op2 + ";";

		return ret;
	}

	private String genOperand(Operand op) {
		if (op == null)
			return null;

		switch (op.getType()) {

		case LITERAL:
			return printLiteral(((Literal) op).getValue());

		case VAR:
			return jsName(((Variable) op).getName(), false);

		case STRUCT_CONSTANT:
			StructConstant cnst = (StructConstant) op;
			return jsName(cnst.getStruct(), true) + "." + jsName(cnst.getConstant(), false);

		case CREATE_STRUCT:
			CreateStruct cnstr = (CreateStruct) op;

			String params = cnstr.getConadtParams().stream().map(p -> genOperand(p.getOp2()))
					.collect(Collectors.joining(", "));
			return "new " + jsName(cnstr.getStruct(), true) + "(" + params + ")";

		case NODE_PATH:
			NodePath np = (NodePath) op;

			return genOperand(np.getStart()) + (!np.getPath().isEmpty() ? "." : "") + np.getPath().stream().map(p -> {
				if (p.requiresArray() && p.hasKeyType())
					return fieldName(p) + "[" + genOperand(p.getKeyType()) + "]";
				else
					return fieldName(p);

			}).collect(Collectors.joining("."));

		default:
			return null;
		}
	}

	private String genCmp(Comparators cpr) {
		switch (cpr) {
		case LT:
			return "<";
		case LE:
			return "<=";
		case EQ:
			return "==";
		case GE:
			return ">=";
		case GT:
			return ">";
		case NEQ:
			return "!=";
		case NGT:
			return "<=";
		case NLT:
			return ">=";
		default:
			return null;
		}
	}

	private void genClass(ModelADT adt) {
		if (!classes.isEmpty())
			classes.append("\n\n");

		String clsName = jsName(adt, true);
		classes.append("class ").append(clsName).append(" {\n");

		for (ModelElement type : adt.getTypes())
			genStaticField(type);

		if (!adt.getTypes().isEmpty())
			classes.append("\n");

		for (ModelElement value : adt.getValues())
			genStaticField(value);

		if (!adt.getValues().isEmpty())
			classes.append("\n");

		if (!adt.getProperties().isEmpty()) {
			// @formatter:off
			String params = adt.getProperties().stream()
					.sorted((e1, e2) -> (e1.isTypePrp() ? -1 : 1))
					.filter(p -> isInitField(p, adt))
					.map(p -> fieldName(p))
					.collect(Collectors.joining(", "));
			if (!params.isEmpty()) {
				classes.append("\tconstructor(").append(params).append(") {\n")
					.append(
						adt.getProperties().stream()
							.filter(p -> isInitField(p, adt))
							.map(p -> {
								String field = fieldName(p);
								return "\t\tthis." + field + " = " + field + ";";
							
							}).collect(Collectors.joining("\n"))
					)
					.append("\n\t}\n");
			}
			// @formatter:on

			classes.append("\n");

			for (ModelProperty prp : adt.getProperties())
				genField(prp, adt);
		}

		classes.append("}");
	}

	private void genStaticField(ModelElement el) {
		String jsName = jsName(el, false);

		classes.append("\tstatic ").append(jsName).append(" = '").append(jsName).append("';\n");
	}

	private void genField(ModelProperty prp, ModelADT ofStruct) {
		if (!includeField(prp, ofStruct))
			return;

		classes.append("\t").append(fieldName(prp));
		if (prp.requiresArray())
			classes.append(" = {}");
		classes.append(";\n");
	}

	private String fieldName(ModelProperty prp) {
		if (prp.isTypePrp()) {
			return "type";
		} else
			return jsName(prp, false);
	}

	private String jsName(ModelElement el, boolean cls) {
		return jsName(el.getString(), cls);
	}

	private String jsName(String str, boolean cls) {
		return safeName(str, cls);
	}
}
