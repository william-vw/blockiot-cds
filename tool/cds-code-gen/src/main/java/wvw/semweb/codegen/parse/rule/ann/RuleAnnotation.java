package wvw.semweb.codegen.parse.rule.ann;

import java.util.Objects;

import org.apache.jen3.graph.Node;

public class RuleAnnotation {

	public static enum AnnotationTypes {
		PARAM, EVENT;
	}

	protected Node node;

	protected AnnotationTypes type;

	protected RuleAnnotation(Node node, AnnotationTypes type) {
		this.node = node;
		this.type = type;
	}

	public Node getNode() {
		return node;
	}

	public AnnotationTypes getType() {
		return type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(node, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RuleAnnotation other = (RuleAnnotation) obj;
		return Objects.equals(node, other.node) && type == other.type;
	}
}
