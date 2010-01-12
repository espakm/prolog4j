/* 
 * Copyright (c) 2010 Miklos Espak
 * All rights reserved.
 * 
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 * 
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 * 
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.prolog4j;

/**
 * This exception is thrown when a syntactically incorrect Prolog query is
 * processed.
 */
public class InvalidQueryException extends RuntimeException {

	/** Class version for serialization. */
	private static final long serialVersionUID = 1L;

	/** The query that caused the exception. */
	private final String query;
	
	/**
	 * Constructs an InvalidQueryException. It takes as its argument the Prolog
	 * query that cannot be processed.
	 * 
	 * @param query the syntactically incorrect Prolog query
	 */
	public InvalidQueryException(String query) {
		super(String.format("The following query is syntactically incorrect: \"%s\".", query));
		this.query = query;
	}
	
	/**
	 * Constructs an InvalidQueryException exception. It takes as its first 
	 * argument the Prolog query that cannot be processed. Its second argument
	 * is the original exception (that has been thrown by the inherent 
	 * Prolog implementation).
	 * 
	 * @param query the syntactically incorrect Prolog query
	 * @param cause the original exception thrown by the implementation
	 */
	public InvalidQueryException(String query, Throwable cause) {
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
