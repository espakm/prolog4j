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

import java.io.IOException;
import java.io.InputStream;

/**
 * A Prover object represents a Prolog knowledge base, on which you can create
 * and solve queries. The implementations of this interface should not provide
 * public constructors. The Prover instances should be created through
 * {@link ProverFactory#getProver()}.
 */
public interface Prover {

	/**
	 * Solves a Prolog goal and returns an object using which the individual
	 * solutions can be iterated over. It is equivalent with the following:
	 * <code>query(goal).solve(actualArgs)</code>
	 * 
	 * @param <A>
	 *            the type of an element of the solutions
	 * @param goal
	 *            the Prolog goal
	 * @param actualArgs
	 *            the actual arguments of the goal
	 * @return an object for traversing the solutions
	 * @see Query#solve(Object...)
	 */
	<A> Solution<A> solve(String goal, Object... actualArgs);

	/**
	 * Creates a Prolog query that can be solved later.
	 * 
	 * @param goal
	 *            the Prolog goal
	 * @return a query object to be solved later
	 */
	Query query(String goal);

	/**
	 * Loads in a Prolog library of the specified name.
	 * 
	 * @param library
	 *            the name of the library
	 */
	void loadLibrary(String library);

	/**
	 * Loads a Prolog theory from a stream.
	 * 
	 * @param input
	 *            the stream
	 */
	void loadTheory(InputStream input) throws IOException;

	/**
	 * Adds a Prolog theory to the knowledge base.
	 * 
	 * @param theory
	 *            the Prolog theory
	 */
	void addTheory(String theory);

	/**
	 * Adds a Prolog theory to the knowledge base. The elements of the arguments
	 * must represent individual Prolog facts and rules.
	 * 
	 * @param theory
	 *            the Prolog theory
	 */
	void addTheory(String... theory);

	/**
	 * Asserts a Prolog fact or rule to the knowledge base.
	 * 
	 * @param fact the Prolog fact
	 * @param args the arguments of the fact            
	 */
	void assertz(String fact, Object... args);

	/**
	 * Retracts a Prolog fact or rule from the knowledge base.
	 * 
	 * @param fact the Prolog fact
	 */
	void retract(String fact);

	/**
	 * Returns the conversion policy used by the prover.
	 * 
	 * @return the conversion policy
	 */
	ConversionPolicy getConversionPolicy();

	/**
	 * Sets the conversion policy used by the prover.
	 * 
	 * @param conversionPolicy the conversion policy to be used
	 */
	void setConversionPolicy(ConversionPolicy conversionPolicy);

}
