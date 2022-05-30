package wvw.semweb.codegen.model.logic;

import wvw.semweb.codegen.parse.rule.ann.Annotated;

public class IfThen extends Annotated implements CodeStatement {

	private Condition cond;
	private CodeStatement then;

	public IfThen(Condition cond, CodeStatement then) {
		this.cond = cond;
		this.then = then;
	}

	@Override
	public Codes getStatementType() {
		return Codes.IF_THEN;
	}

	public Condition getCondition() {
		return cond;
	}

	public CodeStatement getThen() {
		return then;
	}

	@Override
	public String toString() {
		return "if: " + cond + "\nthen: " + then;
	}
}
