package wvw.semweb.codegen.model.adt;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.jen3.util.iterator.WrappedIterator;

public class ModelADT extends ModelElement implements Comparable<ModelADT> {

	private int order;

	private Set<ModelElement> types = new HashSet<>();
	private Set<ModelElement> values = new HashSet<>();
	private Set<ModelProperty> properties = new HashSet<>();
	
	public ModelADT(String name, int order) {
		super(name);

		this.order = order;
		properties.add(ModelProperty.typeProperty());
	}

	public int getOrder() {
		return order;
	}

	public void addType(ModelElement type) {
//		if (!type.getName().equals(name))
		types.add(type);
	}

	public Collection<ModelElement> getTypes() {
		return types;
	}

	public void addValue(ModelElement value) {
		values.add(value);
	}

	public Collection<ModelElement> getValues() {
		return values;
	}

	public boolean hasConstants() {
		return !types.isEmpty() || !values.isEmpty();
	}

	public Iterator<ModelElement> getConstants() {
		return WrappedIterator.create(types.iterator()).andThen(values.iterator());
	}

	public ModelElement getConstant(String name) {
		return Stream.concat(types.stream(), values.stream()).filter(p -> p.getName().equals(name)).findAny().get();
	}

	public void addProperty(ModelProperty prp) {
		properties.add(prp);
	}

	public ModelProperty getProperty(String name) {
		return properties.stream().filter(p -> p.getName().equals(name)).findAny().get();
	}

	public Collection<ModelProperty> getProperties() {
		return properties;
	}

	public void replacing(ModelADT adt2) {
		mergeWith(adt2);
		adt2.replaceWith(this);
	}

	private void mergeWith(ModelADT adt2) {
		types.addAll(adt2.getTypes());
		values.addAll(adt2.getValues());
		properties.addAll(adt2.getProperties());
	}

	private void replaceWith(ModelADT adt) {
		// TODO ideally we update all "incoming" properties
		// (i.e., with this adt as target) with the given adt
		// but that requires more housekeeping and is currently not needed
	}

	@Override
	public int compareTo(ModelADT o) {
		return Integer.valueOf(order).compareTo(o.getOrder());
	}

	@Override
	public String toString() {
		String id = getString();

		return id + (!types.isEmpty() ? "\n\ttypes: " + types : "") + (!values.isEmpty() ? "\n\tvalues: " + values : "")
				+ (!properties.isEmpty() ? "\n\tproperties: " + properties : "");
	}
}
