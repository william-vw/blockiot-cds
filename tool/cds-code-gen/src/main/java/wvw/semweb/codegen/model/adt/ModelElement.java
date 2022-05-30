package wvw.semweb.codegen.model.adt;

import java.util.Objects;

public class ModelElement {

	protected String name;
	protected String label;
	protected Object id;

	public ModelElement(String name) {
		this.name = name;
	}

	protected ModelElement(String name, String label) {
		this.name = name;
		this.label = label;
	}

	public boolean hasName() {
		return this.name != null;
	}

	public String getName() {
		return name;
	}

	public boolean hasLabel() {
		return label != null;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public String getString() {
		return (hasLabel() ? label : name);
	}

	// for debugging
	public String print() {
		return getString();
	}

	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}

	public void setString(String str) {
		if (hasLabel())
			this.label = str;
		else
			this.name = str;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModelElement other = (ModelElement) obj;
		return Objects.equals(name, other.name);
	}

	@Override
	public String toString() {
		return getString();
	}
}
