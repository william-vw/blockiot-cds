package wvw.semweb.codegen.model.logic;

public class Variable implements Operand {

	private static int cnt = 0;

	private String name;

	public Variable() {
		name = "v" + cnt++;
	}

	public Variable(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public Operands getType() {
		return Operands.VAR;
	}

	@Override
	public String toString() {
		return name;
	}
}
