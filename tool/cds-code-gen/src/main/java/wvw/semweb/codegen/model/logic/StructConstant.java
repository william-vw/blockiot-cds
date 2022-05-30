package wvw.semweb.codegen.model.logic;

import wvw.semweb.codegen.model.adt.ModelElement;
import wvw.semweb.codegen.model.adt.ModelADT;

public class StructConstant implements Operand {

	private ModelADT adt;
	private ModelElement constant;

	public StructConstant(ModelADT adt, ModelElement constant) {
		this.adt = adt;
		this.constant = constant;
	}

	public ModelADT getStruct() {
		return adt;
	}

	public ModelElement getConstant() {
		return constant;
	}

	@Override
	public Operands getType() {
		return Operands.STRUCT_CONSTANT;
	}

	@Override
	public String toString() {
		return adt.getString() + ":" + constant.getString();
	}
}
