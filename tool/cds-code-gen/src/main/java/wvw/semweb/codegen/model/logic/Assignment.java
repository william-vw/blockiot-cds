package wvw.semweb.codegen.model.logic;

public class Assignment implements CodeStatement {

	private Operand op1;
	private Operand op2;

	public Assignment(Operand op1, Operand op2) {
		this.op1 = op1;
		this.op2 = op2;
	}
	
	@Override
	public Codes getStatementType() {
		return Codes.ASSIGN;
	}

	public Operand getOp1() {
		return op1;
	}

	public Operand getOp2() {
		return op2;
	}

	@Override
	public String toString() {
		return op1 + " = " + op2;
	}
}
