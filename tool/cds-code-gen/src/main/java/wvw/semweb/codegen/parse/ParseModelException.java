package wvw.semweb.codegen.parse;

public class ParseModelException extends Exception {

	private static final long serialVersionUID = 1L;

	public ParseModelException() {
		super();
	}

	public ParseModelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ParseModelException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParseModelException(String message) {
		super(message);
	}

	public ParseModelException(Throwable cause) {
		super(cause);
	}

}
