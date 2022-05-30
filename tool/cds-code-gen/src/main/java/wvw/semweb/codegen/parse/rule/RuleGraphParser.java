package wvw.semweb.codegen.parse.rule;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jen3.graph.Node;
import org.apache.jen3.n3.impl.N3Rule;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.ClauseEntry;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import wvw.semweb.codegen.parse.ParseModelException;
import wvw.semweb.codegen.parse.rule.RuleGraph.ClauseTypes;
import wvw.semweb.codegen.parse.rule.ann.ParameterAnnotation;
import wvw.semweb.codegen.parse.rule.ann.RuleAnnotation;
import wvw.semweb.codegen.parse.rule.ann.RuleAnnotation.AnnotationTypes;

public class RuleGraphParser {

	private static final Logger log = LogManager.getLogger(RuleGraphParser.class);

	private RuleGraph graph = new RuleGraph();

	public RuleGraph createGraph(N3Rule rule, List<RuleAnnotation> annotations) throws ParseModelException {
		buildGraph(rule);
		checkGraph(rule, annotations);

		return graph;
	}

	protected void buildGraph(N3Rule rule) {
		buildGraph(rule.getBody(), ClauseTypes.BODY);
		buildGraph(rule.getHead(), ClauseTypes.HEAD);
	}

	protected void buildGraph(ClauseEntry[] clauses, ClauseTypes type) {
		for (ClauseEntry c : clauses) {
			if (!(c instanceof TriplePattern))
				return;

			TriplePattern tp = (TriplePattern) c;

			if (!tp.getPredicate().isURI())
				log.error("found non-URI predicate: " + tp.getPredicate());

			GraphNode node = graph.getOrCreate(tp.getSubject());

			GraphEdge edge = new GraphEdge(tp.getPredicate());
			edge.setSource(node);

			edge.setData(type);

			GraphNode node2 = null;
			if (!tp.getObject().isLiteral())
				node2 = graph.getOrCreate(tp.getObject(), true);
			else
				node2 = new GraphNode(tp.getObject());

			edge.setTarget(node2);

			node.addOut(edge);
			node2.addIn(edge);
		}
	}

	// so, inverse triples kind of suck
	// (inverse as in having the entry-point as object)

	// initially we inverted the actual property and added it as an "out" edge;
	// (in code generation, we created an inverse name for it)
	// so all code paths originated nicely from the entry-point

	// this worked but was unintuitive in practice
	// it also caused issues in solidity: e.g., exam.isPhysicalExaminationOf ..
	// caused the following error: "TypeError: Types in storage containing (nested)
	// mappings cannot be assigned to."

	protected void checkGraph(N3Rule rule, List<RuleAnnotation> annotations) throws ParseModelException {
		Set<GraphNode> found = new HashSet<>();

		List<Node> params = annotations.stream().filter(a -> a.getType() == AnnotationTypes.PARAM)
				.map(a -> ((ParameterAnnotation) a).getNode()).collect(Collectors.toList());

		for (Node param : params) {

			if (!graph.contains(param))
				throw new ParseModelException("parameter " + param + " not found in rule:\n" + rule);

			GraphNode node = graph.get(param);
			coverGraph(found, node);
		}

		Collection<Node> roots = graph.getRoots();
		for (Node root : roots) {
			GraphNode node = graph.get(root);

			if (!params.contains(root))
				throw new ParseModelException("found root that is not a parameter (" + root + ") for rule:\n" + rule);

			coverGraph(found, node);
		}

		annotations.forEach(a -> graph.add(a));
	}

	protected void coverGraph(Set<GraphNode> found, GraphNode curNode) {
		if (curNode.isLiteral())
			return;

		found.add(curNode);

		for (GraphEdge out : curNode.getOut()) {

			if (!found.contains(out.getTarget()))
				coverGraph(found, out.getTarget());
		}
	}
}
