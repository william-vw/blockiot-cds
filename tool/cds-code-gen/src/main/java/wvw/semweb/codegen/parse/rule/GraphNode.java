package wvw.semweb.codegen.parse.rule;

import java.util.Collection;
import java.util.HashSet;

public class GraphNode extends GraphElement {

	private Collection<GraphEdge> in = new HashSet<>();
	private Collection<GraphEdge> out = new HashSet<>();

	private boolean literal;

	public GraphNode(Object id) {
		super(id);
	}

	public boolean isLiteral() {
		return literal;
	}

	public void setLiteral(boolean literal) {
		this.literal = literal;
	}

	public void addOut(GraphEdge edge) {
		out.add(edge);
	}

	public Collection<GraphEdge> getOut() {
		return out;
	}

	public void addIn(GraphEdge edge) {
		in.add(edge);
	}

	public Collection<GraphEdge> getIn() {
		return in;
	}

	@Override
	public String toString() {
		return super.toString() + (!in.isEmpty() ? "\n\tin:" + in : "") + (!out.isEmpty() ? "\n\tout: " + out : "");
	}
}
