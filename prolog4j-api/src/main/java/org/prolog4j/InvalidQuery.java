/*
 * Copyright 2010 by Miklós Espák <espakm@gmail.com>
 * 
 * This file is part of Prolog4J.
 * 
 * Prolog4J is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Prolog4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Prolog4J.  If not, see <http://www.gnu.org/licenses/>.
 */
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
