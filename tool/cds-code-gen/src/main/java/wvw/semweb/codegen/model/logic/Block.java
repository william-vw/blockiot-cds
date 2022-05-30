package wvw.semweb.codegen.model.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Block implements CodeStatement {

	private List<CodeStatement> statements = new ArrayList<>();

	@Override
	public Codes getStatementType() {
		return Codes.BLOCK;
	}
	
	public void add(CodeStatement stmt) {
		statements.add(stmt);
	}

	public List<CodeStatement> getStatements() {
		return statements;
	}
	
	public void clear() {
		statements.clear();
	}

	@Override
	public String toString() {
		return statements.stream().map(o -> o.toString()).collect(Collectors.joining("\n"));
	}
}
