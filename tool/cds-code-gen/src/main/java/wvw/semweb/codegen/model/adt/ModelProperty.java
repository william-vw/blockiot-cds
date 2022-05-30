package wvw.semweb.codegen.model.adt;

import wvw.semweb.codegen.model.logic.StructConstant;

public class ModelProperty extends ModelElement {

	private ModelType target;
	private Integer maxCardinality;
	private boolean isTypePrp;

	private StructConstant keyType;

	public ModelProperty(String name) {
		super(name);
	}

	public ModelProperty(String name, Integer maxCardinality) {
		super(name);

		this.maxCardinality = maxCardinality;
	}

	public static ModelProperty typeProperty() {
		ModelProperty typePrp = new ModelProperty("type", 1);
		typePrp.setTypePrp(true);

		return typePrp;
	}

	public ModelProperty(String name, ModelType target) {
		super(name);

		this.target = target;
	}

	public ModelProperty(String name, ModelType target, Integer maxCardinality) {
		super(name);

		this.target = target;
		this.maxCardinality = maxCardinality;
	}

	private ModelProperty(String name, String label, ModelType target, Integer maxCardinality, boolean isTypePrp,
			StructConstant keyType) {

		super(name, label);

		this.target = target;
		this.maxCardinality = maxCardinality;
		this.isTypePrp = isTypePrp;
		this.keyType = keyType;
	}

	public boolean hasTarget() {
		return target != null;
	}

	public ModelType getTarget() {
		return target;
	}

	public void setTarget(ModelType target) {
		this.target = target;
	}

	// semweb4j determines whether get() vs. getAll()
	// depending on existence of maxCardinality = 1
	// (unqualified!)

	public Integer getMaxCardinality() {
		return maxCardinality;
	}

	public void setMaxCardinality(Integer maxCardinality) {
		this.maxCardinality = maxCardinality;
	}

	public boolean hasMaxCardinality() {
		return maxCardinality != null;
	}

	public boolean requiresArray() {
		return !hasMaxCardinality() || getMaxCardinality() > 1;
	}

	public void setTypePrp(boolean isTypePrp) {
		this.isTypePrp = isTypePrp;
	}

	public boolean isTypePrp() {
		return isTypePrp;
	}

	public boolean hasKeyType() {
		return keyType != null;
	}

	public StructConstant getKeyType() {
		return keyType;
	}

	public void setKeyType(StructConstant keyType) {
		this.keyType = keyType;
	}

	public ModelProperty copy() {
		return new ModelProperty(name, label, target, maxCardinality, isTypePrp, keyType);
	}

	// for debugging
	@Override
	public String print() {
		return (isTypePrp ? "#type" : (super.toString() + (hasKeyType() ? "[" + keyType.getConstant() + "]" : "")));
	}

	@Override
	public String toString() {
		String cardStr = (hasMaxCardinality() && maxCardinality == 1 ? "[1]" : "");

		return print() + cardStr + (hasTarget() ? " -> " + target : "");
	}
}
