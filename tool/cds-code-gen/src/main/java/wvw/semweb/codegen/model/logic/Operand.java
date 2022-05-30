package wvw.semweb.codegen.model.logic;

public interface Operand {

	public enum Operands {
		CREATE_STRUCT, LITERAL, NODE_PATH, STRUCT_CONSTANT, VAR
	}
	
	public Operands getType();
}
