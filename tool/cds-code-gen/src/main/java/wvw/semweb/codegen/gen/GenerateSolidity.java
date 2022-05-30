package wvw.semweb.codegen.gen;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.jen3.datatypes.RDFDatatype;
import org.apache.jen3.util.IOUtils;
import org.atteo.evo.inflector.English;

import wvw.semweb.codegen.model.adt.CodeModel;
import wvw.semweb.codegen.model.adt.ModelElement;
import wvw.semweb.codegen.model.adt.ModelProperty;
import wvw.semweb.codegen.model.adt.ModelADT;
import wvw.semweb.codegen.model.adt.ModelType;
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
import wvw.semweb.codegen.model.logic.Operand.Operands;
import wvw.semweb.codegen.parse.rule.ann.ParameterAnnotation;
import wvw.semweb.codegen.parse.rule.ann.ParameterAnnotation.ParameterTypes;
import wvw.semweb.codegen.parse.rule.ann.RuleAnnotation;
import wvw.semweb.codegen.parse.rule.ann.RuleAnnotation.AnnotationTypes;

public class GenerateSolidity extends GenerateCode {

	private StringBuffer model = new StringBuffer();
	private StringBuffer logic = new StringBuffer();

	@Override
	public void generate(CodeModel codeModel, CodeLogic codeLogic, File output) throws IOException {
		generateEventDeclr(codeLogic);
		generateStructs(codeModel);
		generateFunction(codeModel, codeLogic);

		StringBuffer out = new StringBuffer();
		// @formatter:off
		out.append("// Specifies the version of Solidity, using semantic versioning.\n")
			.append("// Learn more: https://solidity.readthedocs.io/en/v0.5.10/layout-of-source-files.html#pragma\n")
			.append("pragma solidity ^0.7.0;")
			.append("\n\n");

		out.append("contract ").append(contractName(codeLogic.getRulesName())).append(" {\n");
		
		StringBuffer contents = new StringBuffer()
			.append("string public message;\n\n")
			.append("constructor(string memory initMessage) {\n")
			.append("	message = initMessage;\n")
			.append("}");
		contents.append("\n\n");
		
		contents.append(model).append("\n\n").append(logic).append("\n\n");
		
		contents.append("function update(string memory newMessage) public {\n")
			.append("	message = newMessage;\n")
			.append("}");
		// @formatter:on

		String contentStr = contents.toString();
		contentStr = "\t" + contentStr.replace("\n", "\n\t");

		out.append(contentStr);

		out.append("}");

		IOUtils.writeToFile(out.toString(), output);
	}

	private void generateEventDeclr(CodeLogic codeLogic) {
		String eventStr = codeLogic.getAnnotations().getAll().stream().filter(a -> a.getType() == AnnotationTypes.EVENT)
				.map(e -> "event " + e.getNode().getLocalName() + "(uint time);\n").collect(Collectors.joining(""));

		if (!eventStr.isEmpty())
			model.append("\n").append(eventStr);
	}

	private void generateStructs(CodeModel codeModel) {
		codeModel.getAllStructs().forEach(s -> genStruct(s));
	}

	private void generateFunction(CodeModel codeModel, CodeLogic codeLogic) {
		Optional<RuleAnnotation> loadParam = codeLogic.getAnnotations().getAll().stream()
				.filter(a -> a.getType() == AnnotationTypes.PARAM
						&& ((ParameterAnnotation) a).getParameterType() == ParameterTypes.LOAD)
				.findFirst();

		if (loadParam.isPresent()) {
			String name = loadParam.get().getNode().getName() + "s";
			String type = adtName(codeModel.getStruct(loadParam.get().getNode()));

			logic.append("\nmapping(address => ").append(type).append(") ").append(name).append(";\n\n");
		}

		String fnParams = codeLogic.getAnnotations().getAll().stream().filter(a -> a.getType() == AnnotationTypes.PARAM)
				.map(a -> (ParameterAnnotation) a).filter(a -> a.getParameterType() == ParameterTypes.FUNCTION)
				.map(a -> adtName(codeModel.getStruct(a.getNode())) + " memory " + a.getNode().getName())
				.collect(Collectors.joining(", "));

		logic.append("function execute(").append(fnParams).append(") public {\n");

		if (loadParam.isPresent()) {
			logic.append("\t").append(adtName(codeModel.getStruct(loadParam.get().getNode())) + " storage "
					+ loadParam.get().getNode().getName() + " = patients[msg.sender];\n\n");
		}

		String contents = codeLogic.getStatements().stream().map(it -> genStatement(it, 0))
				.collect(Collectors.joining("\n\n"));
		// indent contents of the function
		contents = "\t" + contents.replace("\n", "\n\t");
		logic.append(contents);

		logic.append("\n}");
	}

	@Override
	protected String genComparison(Comparison cmp) {
		String cpr = genCmp(cmp.getCmp());
		String op2 = genOperand(cmp.getOp2());

		String part2;
		switch (cmp.getCmp()) {

		case EX:
			return genOperand(cmp.getOp1()) + ".exists";

		case NEX:
			// TODO
			part2 = genCmp(Comparators.EQ) + " " + "0";
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
		ret += thenContents + "\n";

		if (ifThen.has(AnnotationTypes.EVENT)) {
			RuleAnnotation event = ifThen.get(AnnotationTypes.EVENT).iterator().next();
			ret += "\n\temit " + event.getNode().getLocalName() + "(block.timestamp);\n";
		}

		ret += "}";

		return ret;
	}
	
	@Override
	protected String genBlock(Block block) {
		StringBuffer out = new StringBuffer();

		out.append(block.getStatements().stream().map(stmt -> genStatement(stmt, 0)).collect(Collectors.joining("\n")));
		if (block.getStatements().size() > 1)
			out.append("\n");

		return out.toString();
	}

	@Override
	protected String genAssignment(Assignment assign) {
		String op1 = genOperand(assign.getOp1());

		// assigning to variable

		if (assign.getOp1() instanceof Variable) {
			String typeName = null;

			switch (assign.getOp2().getType()) {

			case CREATE_STRUCT:
				// get type of new adt that we're creating; this will be the variable's type
				ModelADT adt = ((CreateStruct) assign.getOp2()).getStruct();
				typeName = adtName(adt);

				break;

			case NODE_PATH:
				NodePath path = (NodePath) assign.getOp2();
				ModelType lastTarget = path.getPath().getLast().getTarget();
				if (lastTarget.hasObjectType())
					typeName = adtName(lastTarget.getObjectType());
				else
					typeName = solDatatype(lastTarget.getDataType());

				break;

			default:
				log.error("unsupported operand type for assignment: " + assign.getOp1().getType());
				break;
			}

			op1 = typeName + " memory " + op1;
		}

		String op2 = genOperand(assign.getOp2());

		if (Util.involvesArrayAssign(assign.getOp1()))
			return op1 + "[" + op2 + "." + fieldName(ModelProperty.typeProperty()) + "] = " + op2 + ";";
		else
			return op1 + " = " + op2 + ";";
	}

	private String genOperand(Operand op) {
		if (op == null)
			return null;

		switch (op.getType()) {

		case LITERAL:
			return printLiteral(((Literal) op).getValue());

		case VAR:
			return varName((Variable) op);

		case STRUCT_CONSTANT:
			StructConstant cnst = (StructConstant) op;

			// get corresponding enum name
			String enumName = enumName(cnst.getStruct());
			// refer to enum field
			return enumName + "." + enumFieldName(cnst.getConstant());

		case CREATE_STRUCT:
			CreateStruct cnstr = (CreateStruct) op;
			String params = cnstr.getConadtParams().stream().map(p -> {
				if (p.getOp1().getType() != Operands.NODE_PATH)
					log.error("expecting nodepath of size 1 for constructor parameter: " + p.getOp1());

				String prpName = fieldName(((NodePath) p.getOp1()).getPath().getFirst());
				return prpName + ": " + genOperand(p.getOp2());

			}).collect(Collectors.joining(", "));
			params += ", exists: true";

			return adtName(cnstr.getStruct()) + "({ " + params + " })";

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

	private void genStruct(ModelADT adt) {
		if (!model.isEmpty())
			model.append("\n\n");

		// add any required enum to keep types & uris

		Iterator<ModelElement> cnsts = adt.getConstants();
		if (cnsts.hasNext()) {
			String enumName = enumName(adt);
			model.append("enum ").append(enumName).append("{ ");

			int cnt = 0;
			while (cnsts.hasNext()) {
				if (cnt++ > 0)
					model.append(", ");

				ModelElement cnst = cnsts.next();
				model.append(enumFieldName(cnst));
			}

			model.append(" }");
			model.append("\n\n");
		}

		// add the actual adt

		model.append("struct ").append(adtName(adt)).append(" {\n");

		for (ModelProperty prp : adt.getProperties())
			genField(prp, adt);

		// yes this is actually needed
		model.append("\tbool exists;\n");

		model.append("}");
	}

	private void genField(ModelProperty prp, ModelADT ofAdt) {
		if (!includeField(prp, ofAdt))
			return;

		model.append("\t");

		String typeName = null;

		if (prp.isTypePrp()) {
			String adtName = enumName(ofAdt);
			typeName = adtName;

		} else {
			ModelType target = prp.getTarget();
			if (target.hasObjectType())
				typeName = adtName(target.getObjectType());
			else
				typeName = solDatatype(target.getDataType());
		}

		if (prp.requiresArray()) {
			String keyName = null;

			ModelType target = prp.getTarget();
			// key will be the type property of the target adt
			// so put the type property's static type (i.e., enum w/ constants)
			if (target.hasObjectType())
				keyName = enumName(target.getObjectType());
			// key will be literal
			else
				keyName = solDatatype(target.getDataType());

			model.append("mapping(").append(keyName).append(" => ").append(typeName).append(")");

		} else
			model.append(typeName);

		model.append(" ").append(fieldName(prp));

		model.append(";\n");
	}

	private String contractName(String name) {
		return solName(name, true);
	}

	private String adtName(ModelADT adt) {
		return solName(adt, true);
	}

	private String enumName(ModelADT adt) {
		return English.plural(adtName(adt));
	}

	private String fieldName(ModelProperty prp) {
		if (prp.isTypePrp())
			return "hasType";
		else
			return solName(prp, false);
	}

	private String enumFieldName(ModelElement el) {
		return StringUtils.capitalize(solName(el, false)); // .toUpperCase();
	}

	private String varName(Variable var) {
		return solName(var.getName(), false);
	}

	private String solName(ModelElement el, boolean clsName) {
		return solName(el.getString(), clsName);
	}

	private String solName(String str, boolean clsName) {
		return safeName(str, clsName);
	}

	private String solDatatype(RDFDatatype dt) {
		log.info("rdf datatype: " + dt.getURI());

		String ln = dt.getURI().substring(dt.getURI().lastIndexOf("#") + 1);
		if (ln.equalsIgnoreCase("string"))
			return "string";
		if (ln.equalsIgnoreCase("integer") || ln.equalsIgnoreCase("int"))
			return "int";
		if (ln.equalsIgnoreCase("boolean"))
			return "bool";
		if (ln.equals("float")) {
			log.warn("no float support in solidity, returning int");
			return "int";
		}

		log.error("currently unsupported datatype: " + dt.getURI());

		return ln;
	}
}