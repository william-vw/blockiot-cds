package wvw.semweb.codegen.parse;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.n3.Node_QuickVariable;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.n3.N3ModelSpec.Types;
import org.apache.jen3.n3.impl.N3ModelImpl.N3EventListener;
import org.apache.jen3.n3.impl.N3Rule;
import org.apache.jen3.rdf.model.ModelFactory;
import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.rdf.model.Statement;
import org.apache.jen3.util.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import wvw.semweb.codegen.model.adt.CodeModel;
import wvw.semweb.codegen.model.logic.CodeLogic;
import wvw.semweb.codegen.model.logic.IfThen;
import wvw.semweb.codegen.parse.post.ModelPostprocessor;
import wvw.semweb.codegen.parse.post.ModelPostprocessor.PostprocessTypes;
import wvw.semweb.codegen.parse.rule.RuleGraph;
import wvw.semweb.codegen.parse.rule.RuleGraphParser;
import wvw.semweb.codegen.parse.rule.ann.EventAnnotation;
import wvw.semweb.codegen.parse.rule.ann.ParameterAnnotation;
import wvw.semweb.codegen.parse.rule.ann.ParameterAnnotation.ParameterTypes;
import wvw.semweb.codegen.parse.rule.ann.RuleAnnotation;
import wvw.semweb.codegen.parse.rule.ann.RuleAnnotation.AnnotationTypes;
import wvw.semweb.codegen.visit.ModelVisitor;
import wvw.semweb.codegen.visit.ModelVisitorImpl;
import wvw.semweb.codegen.visit.VisitModelException;
import wvw.utils.rdf.NS;

public class ParseModelLogic implements N3EventListener {

	private static final Logger log = LogManager.getLogger(ParseModelLogic.class);

	private List<N3Rule> parsedRules = new ArrayList<>();
	private N3Model ontology;

	private CodeModel model = new CodeModel();
	private CodeLogic logic = new CodeLogic();

	@Override
	public void newRule(N3Rule r) {
		parsedRules.add(r);
	}

	public CodeModel getModel() {
		return model;
	}

	public CodeLogic getLogic() {
		return logic;
	}

	public void parse(File rulesFile, File ontologyFile, PostprocessTypes... postprocesses)
			throws IOException, URISyntaxException, ParseModelException, VisitModelException {

		logic.setRulesName(FilenameUtils.removeExtension(rulesFile.getName()));

		ontology = ModelFactory.createN3Model(N3ModelSpec.get(Types.N3_MEM_FP_INF));
		ontology.read(IOUtils.getResourceInputStream(getClass(), ontologyFile.getPath()), null);

		N3Model ruleset = ModelFactory.createN3Model(N3ModelSpec.get(Types.N3_MEM_FP_INF));
		ruleset.setListener(this);
		ruleset.read(IOUtils.getResourceInputStream(getClass(), rulesFile.getPath()), null);

		if (parsedRules.isEmpty())
			log.error("no rules found in " + rulesFile.getPath());

		for (N3Rule r : parsedRules) {
//			log.info("- parsed rule:\n" + r);

			List<RuleAnnotation> annotations = getRuleAnnotations(r, ruleset);
			checkAnnotations(annotations, r);
			logic.getAnnotations().addAll(annotations);

			processRule(r, ontology, annotations, postprocesses);
		}
	}

	private List<RuleAnnotation> getRuleAnnotations(N3Rule r, N3Model ruleset) {
		List<RuleAnnotation> ret = new ArrayList<>();

		ruleset.listStatements(ruleset.asResource(r.getBodyNode()),
				ruleset.createResource(NS.toUri("cg:functionParam")), (Resource) null).forEachRemaining(
						stmt -> ret.add(new ParameterAnnotation(getAnnotationNode(stmt, r), ParameterTypes.FUNCTION)));

		ruleset.listStatements(ruleset.asResource(r.getBodyNode()), ruleset.createResource(NS.toUri("cg:loadParam")),
				(Resource) null).forEachRemaining(
						stmt -> ret.add(new ParameterAnnotation(getAnnotationNode(stmt, r), ParameterTypes.LOAD)));

		ruleset.listStatements(ruleset.asResource(r.getBodyNode()), ruleset.createResource(NS.toUri("cg:event")),
				(Resource) null).forEachRemaining(stmt -> ret.add(new EventAnnotation(stmt.getObject().asNode())));

		return ret;
	}

	private void checkAnnotations(List<RuleAnnotation> annotations, N3Rule r) throws ParseModelException {
		if (annotations.isEmpty())
			throw new ParseModelException("no annotations found for rule:\n" + r);

		if (annotations.stream().filter(a -> a.getType() == AnnotationTypes.PARAM
				&& ((ParameterAnnotation) a).getParameterType() == ParameterTypes.LOAD).count() > 1)

			throw new ParseModelException("only 1 loadParam can be specified for rule:\n" + r);

		if (annotations.stream().filter(a -> a.getType() == AnnotationTypes.EVENT).count() > 1)
			throw new ParseModelException("only 1 event can be specified for rule:\n" + r);
	}

	private Node getAnnotationNode(Statement stmt, N3Rule r) {
		Node o = stmt.getObject().asNode();
		if (o.isQuickVariable())
			return r.toRuleVar((Node_QuickVariable) o);

		return o;
	}

	private void processRule(N3Rule r, N3Model ontology, List<RuleAnnotation> annotations,
			PostprocessTypes... postprocesses) throws ParseModelException, VisitModelException {

		RuleGraphParser ruleParser = new RuleGraphParser();

		RuleGraph ruleGraph = ruleParser.createGraph(r, annotations);

		log.info("> processing new rule");

//		log.info("- rule graph:\n");
//		log.info(ruleGraph + "\n");

		log.info("- annotations: " + annotations);

		ModelVisitor visitor = new ModelVisitorImpl(ontology);
		visitor.visit(ruleGraph);

		CodeModel newModel = visitor.getModel();

		IfThen newIt = new IfThen(visitor.getCondition(), visitor.getBlock());
		newIt.addAll(ruleGraph);

		postprocess(newModel, newIt, ruleGraph, postprocesses);

		model.mergeWith(newModel);
		logic.add(newIt);

		log.info("- code model:\n");
		log.info(model + "\n");

		log.info("- condition:");
		log.info(visitor.getCondition() + "\n");

		log.info("- code:");
		log.info(visitor.getBlock() + "\n");
	}

	private void postprocess(CodeModel newModel, IfThen newIt, RuleGraph ruleGraph, PostprocessTypes... postprocesses) {
		for (PostprocessTypes type : PostprocessTypes.values()) {
			if (type.isDefault())
				ModelPostprocessor.create(type).postprocess(newModel, newIt, ruleGraph, ontology);
		}

		for (PostprocessTypes type : postprocesses) {
			log.info("(postprocess: " + type + ")");
			ModelPostprocessor.create(type).postprocess(newModel, newIt, ruleGraph, ontology);
		}
	}
}