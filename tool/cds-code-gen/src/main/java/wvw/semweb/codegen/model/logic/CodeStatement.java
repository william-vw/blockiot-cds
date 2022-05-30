package wvw.semweb.codegen.model.logic;

public interface CodeStatement {

	public enum Codes {
		COND, IF_THEN, BLOCK, ASSIGN
	}
	
	public Codes getStatementType();
}
