package wvw.semweb.codegen.model.logic;

public class Comparison extends Condition {

	public static enum Comparators {
		LT, LE, EQ, GT, GE, NEQ, NGT, NLT, EX, NEX, ONE_OF;
	}

	private Operand op1;
	private Operand op2;
	private Comparators cmp;

	public Comparison(Operand op1, Operand op2, Comparators cmp) {
		this.op1 = op1;
		this.op2 = op2;
		this.cmp = cmp;
	}

	public Comparison(Operand op1, Comparators cmp) {
		this.op1 = op1;
		this.cmp = cmp;
	}
	
	@Override
	public Conditions getConditionType() {
		return Conditions.CMP;
	}

	public Operand getOp1() {
		return op1;
	}

	public Operand getOp2() {
		return op2;
	}

	public Comparators getCmp() {
		return cmp;
	}

	@Override
	public String toString() {
		return op1 + " " + cmp + (op2 != null ? " " + op2 : "");
	}
}
