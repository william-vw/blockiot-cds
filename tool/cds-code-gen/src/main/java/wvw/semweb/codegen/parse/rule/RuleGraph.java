package wvw.semweb.codegen.parse.rule;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jen3.graph.Node;

import wvw.semweb.codegen.parse.rule.ann.Annotated;

public class RuleGraph extends Annotated {

	public static enum ClauseTypes {
		BODY, HEAD
	}

	private Map<Node, GraphNode> termNode = new HashMap<>();
	private Set<Node> roots = new HashSet<>();


	public boolean contains(Node term) {
		return termNode.containsKey(term);
	}

	public GraphNode get(Node term) {
		return termNode.get(term);
	}

	public Collection<GraphNode> getAllNodes() {
		return termNode.values();
	}

	public GraphNode getOrCreate(Node term) {
		return getOrCreate(term, false);
	}

	public GraphNode getOrCreate(Node term, boolean isTarget) {
		GraphNode node = termNode.get(term);
		if (node == null) {
			node = new GraphNode(term);
			termNode.put(term, node);

			if (!isTarget)
				roots.add(term);

		} else {
			if (isTarget)
				roots.remove(term);
		}

		return node;
	}

	public Collection<Node> getRoots() {
		return roots;
	}

	public Collection<GraphNode> getGraphRoots() {
		return roots.stream().map(r -> get(r)).collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return getAllNodes().stream().map(v -> v.toString()).collect(Collectors.joining("\n"));
	}
}
