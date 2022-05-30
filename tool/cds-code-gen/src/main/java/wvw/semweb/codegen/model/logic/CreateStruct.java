package wvw.semweb.codegen.model.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import wvw.semweb.codegen.model.adt.ModelADT;

public class CreateStruct implements Operand {

	private ModelADT adt;
	private List<Assignment> constructParams = new ArrayList<>();

	public CreateStruct(ModelADT adt) {
		this.adt = adt;
	}

	public ModelADT getStruct() {
		return adt;
	}

	public void add(Assignment constructParam) {
		constructParams.add(constructParam);
	}

	public List<Assignment> getConadtParams() {
		return constructParams;
	}

	@Override
	public Operands getType() {
		return Operands.CREATE_STRUCT;
	}

	@Override
	public String toString() {
		return "create " + adt.getString() + "("
				+ constructParams.stream().map(p -> p.toString()).collect(Collectors.joining(", ")) + ")";
	}
}
