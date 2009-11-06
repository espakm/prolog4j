package org.prolog4j;

/**
 * This exception is thrown when a syntactically incorrect Prolog query is
 * processed.
 */
public class InvalidQuery extends RuntimeException {

	/** Class version for serialization. */
	private static final long serialVersionUID = 1L;

	/**
	 * The query that caused the exception.
	 */
	private final String query;
	
	/**
	 * Constructs an InvalidQuery exception. It takes as its argument the Prolog
	 * query that cannot be processed.
	 * 
	 * @param query the syntactically incorrect Prolog query
	 */
	public InvalidQuery(String query) {
		super(String.format("The following query is syntactically incorrect: \"%s\".", query));
		this.query = query;
	}
	
	/**
	 * Constructs an InvalidQuery exception. It takes as its argument the Prolog
	 * query that cannot be processed.
	 * It takes the original exception (that has been thrown by the inherent 
	 * Prolog implementation) as another argument.
	 * 
	 * @param query the syntactically incorrect Prolog query
	 * @param cause the original exception thrown by the implementation
	 */
	public InvalidQuery(String query, Throwable cause) {
		super(String.format("The following query is syntactically incorrect: \"%s\".", query),
				cause);
		this.query = query;
	}
	
	/**
	 * Returns the query that caused the exception.
	 * 
	 * @return the syntactically incorrect Prolog query
	 */
	public String getQuery() {
		return query;
	}
	
}
