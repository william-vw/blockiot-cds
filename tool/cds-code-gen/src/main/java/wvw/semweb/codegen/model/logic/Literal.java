package wvw.semweb.codegen.model.logic;

public class Literal implements Operand {

	private Object value;

	public Literal(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public Operands getType() {
		return Operands.LITERAL;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
