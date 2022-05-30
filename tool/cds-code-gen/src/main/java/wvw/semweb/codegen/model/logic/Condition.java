package wvw.semweb.codegen.model.logic;

public abstract class Condition implements CodeStatement {

	public enum Conditions {
		CONJ, DISJ, CMP
	}

	@Override
	public Codes getStatementType() {
		return Codes.COND;
	}

	public abstract Conditions getConditionType();
}
