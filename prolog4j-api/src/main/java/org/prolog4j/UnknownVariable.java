package org.prolog4j;

/**
 * This exception is thrown when a syntactically incorrect Prolog query is
 * processed.
 */
public class UnknownVariable extends RuntimeException {

	/** Class version for serialization. */
	private static final long serialVersionUID = 1L;

	/**
	 * The variable that caused the exception.
	 */
	private final String variable;
	
	/**
	 * Constructs an UnknownVariable exception. It takes the name of the absent
	 * variable as its argument. The exception is thrown when you want to get 
	 * the value of a variable that does not occur in the query being processed.
	 * 
	 * @param variable
	 * 				the name of the variable whose value was required but does
	 * 				not exist in the query
	 */
	public UnknownVariable(String variable) {
		super(String.format("The following variable does not occur in the query: %s.", variable));
		this.variable = variable;
	}
	
	/**
	 * Returns the name of the variable that caused the exception.
	 * 
	 * @return the name of the variable that is absent in the query being processed
	 */
	public String getVariable() {
		return variable;
	}

}
