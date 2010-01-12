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
public class UnknownVariableException extends RuntimeException {

	/** Class version for serialization. */
	private static final long serialVersionUID = 1L;

	/** The variable that caused the exception. */
	private final String variable;
	
	/**
	 * Constructs an UnknownVariableException. It takes the name of the absent
	 * variable as its argument. The exception is thrown when you want to get 
	 * the value of a variable that does not occur in the query being processed.
	 * 
	 * @param variable
	 * 				the name of the variable whose value was required but does
	 * 				not exist in the query
	 */
	public UnknownVariableException(String variable) {
		super(String.format("The following variable does not occur in the query: %s.", variable));
		this.variable = variable;
	}
	
	/**
	 * Constructs an UnknownVariableException. It takes the name of the absent
	 * variable as its first argument. The exception is thrown when you want to
	 * get the value of a variable that does not occur in the query being
	 * processed. It takes the original exception (that has been thrown by the
	 * inherent Prolog implementation) as another argument.
	 * 
	 * @param variable
	 * 				the name of the variable whose value was required but does
	 * 				not exist in the query
	 * @param cause the original exception thrown by the implementation
	 */
	public UnknownVariableException(String variable, Throwable cause) {
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
