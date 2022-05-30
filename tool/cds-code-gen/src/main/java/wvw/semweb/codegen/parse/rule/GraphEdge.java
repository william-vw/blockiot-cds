package wvw.semweb.codegen.parse.rule;

import java.util.Objects;

import wvw.semweb.codegen.parse.rule.RuleGraph.ClauseTypes;

public class GraphEdge extends GraphElement {

	private GraphNode source;
	private GraphNode target;
	private boolean inverse;

	public GraphEdge(Object id) {
		super(id);
	}

	public GraphEdge(Object id, GraphNode source, GraphNode target, ClauseTypes clauseType) {
		super(id);
		this.source = source;
		this.target = target;

		setData(clauseType);
	}

	public GraphNode getSource() {
		return source;
	}

	public void setSource(GraphNode source) {
		this.source = source;
	}

	public GraphNode getTarget() {
		return target;
	}

	public void setTarget(GraphNode target) {
		this.target = target;
	}

	public boolean isInverse() {
		return inverse;
	}

	public void setInverse(boolean inverse) {
		this.inverse = inverse;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(target);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphEdge other = (GraphEdge) obj;
		return Objects.equals(target, other.target);
	}

	@Override
	public String toString() {
		return source.prettyPrint() + "--" + super.toString() + (isInverse() ? "(i)" : "") + "-> "
				+ target.prettyPrint();
	}
}
