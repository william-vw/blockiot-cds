package wvw.semweb.codegen.model.logic;

import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;

import wvw.semweb.codegen.model.adt.ModelProperty;

public class NodePath implements Operand {

	private Operand start;
	private LinkedList<ModelProperty> path = new LinkedList<>();

	public NodePath() {
	}

	public NodePath(Operand start) {
		this.start = start;
	}

	private NodePath(Operand start, LinkedList<ModelProperty> path) {
		this.start = start;
		this.path = path;
	}

	public boolean hasStart() {
		return start != null;
	}

	public Operand getStart() {
		return start;
	}

	public void add(ModelProperty prp) {
		path.add(prp);
	}

	public LinkedList<ModelProperty> getPath() {
		return path;
	}
	
	public ModelProperty getLast() {
		return path.getLast();
	}

	public int size() {
		return path.size();
	}

	public NodePath copy() {
		return new NodePath(start, new LinkedList<>(path));
	}

	public NodePath subPath(int end) {
		return new NodePath(start, new LinkedList<>(path.subList(0, end)));
	}

	@Override
	public Operands getType() {
		return Operands.NODE_PATH;
	}

	@Override
	public int hashCode() {
		return Objects.hash(path, start);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodePath other = (NodePath) obj;
		return Objects.equals(path, other.path) && Objects.equals(start, other.start);
	}

	@Override
	public String toString() {
		return (start != null ? start + "." : "") + path.stream().map(p -> p.print()).collect(Collectors.joining("."));
	}
}
