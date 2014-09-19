package soot.validation;


public class ValidationException extends RuntimeException {
	private Object concerned;
	private String strMessage;
	private String strCompatibilityMessage;
	private boolean warning;
	
	/**
	 * Creates a new ValidationException.
	 * @param concerned the object which is concerned and could be highlighted in an IDE; for example an unit, a SootMethod, a SootClass or a local.
	 * @param strMessage the message to display in an IDE supporting the concerned feature
	 * @param strCompatibilityMessage the compatibility message containing useful information without supporting the concerned object
	 * @param isWarning whether the exception can be considered as a warning message
	 */
	public ValidationException(Object concerned, String strMessage, String strCompatibilityMessage, boolean isWarning) {
		super(strMessage);
		this.strCompatibilityMessage = strCompatibilityMessage;
		this.strMessage = strMessage;
		this.concerned = concerned;
		this.warning = isWarning;
	}

	/**
	 * Creates a new ValidationException, treated as an error.
	 * @param concerned the object which is concerned and could be highlighted in an IDE; for example an unit, a SootMethod, a SootClass or a local.
	 * @param strMessage the message to display in an IDE supporting the concerned feature
	 * @param strCompatibilityMessage the compatibility message containing useful information without supporting the concerned object
	 */
	public ValidationException(Object concerned, String strMessage, String strCompatibilityMessage) {
		this(concerned, strMessage, strCompatibilityMessage, false);
	}
	/**
	 * Creates a new ValidationException, treated as an error.
	 * @param concerned the object which is concerned and could be highlighted in an IDE; for example an unit, a SootMethod, a SootClass or a local.
	 * @param strCompatibilityMessage the compatibility message containing useful information without supporting the concerned object
	 */
	public ValidationException(Object concerned, String strCompatibilityMessage) {
		this(concerned, strCompatibilityMessage, strCompatibilityMessage, false);
	}
	
	public boolean isWarning() {
		return warning;
	}
	
	public String getRawMessage() {
		return strMessage;
	}
	
	public Object getConcerned() {
		return concerned;
	}
	
	@Override
	public String toString() {
		return strCompatibilityMessage;
	}

	private static final long serialVersionUID = 1L;

}
