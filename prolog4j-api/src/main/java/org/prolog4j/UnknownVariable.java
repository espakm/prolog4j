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
	 * Constructs an UnknownVariable exception. It takes the name of the absent
	 * variable as its argument. The exception is thrown when you want to get 
	 * the value of a variable that does not occur in the query being processed.
	 * It takes the original exception (that has been thrown by the inherent 
	 * Prolog implementation) as another argument.
	 * 
	 * @param variable
	 * 				the name of the variable whose value was required but does
	 * 				not exist in the query
	 * @param cause the original exception thrown by the implementation
	 */
	public UnknownVariable(String variable, Throwable cause) {
		super(String.format("The following variable does not occur in the query: %s.", variable),
				cause);
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
