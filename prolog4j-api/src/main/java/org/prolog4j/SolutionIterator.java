package org.prolog4j;

import java.util.Iterator;

/**
 * The <tt>SolutionIterator</tt> interface servers for traversing the solutions
 * of a Prolog query. In a solution several variables can get bound. <tt>S</tt>
 * is the type of the values of the variable that the is of special interest.
 * Its values are returned by <tt>next()</tt>. The values of the other variables
 * can be accessed by the <tt>get()</tt> methods.
 * 
 * @param <S>
 *            the type of the values of the variable that is of special interest
 */
public interface SolutionIterator<S> extends Iterator<S> {

	/**
	 * Returns the value of the variable bound by the current solution.
	 * 
	 * @param <A>
	 *            the type of the value
	 * @param variable
	 *            the name of the variable
	 * @return the value of the variable in the current solution
	 */
	<A> A get(String variable);

	/**
	 * Returns the value of the variable bound by the current solution. The
	 * required type of the value can be specified explicitly. This is useful
	 * when not the default type is desired.
	 * 
	 * @param <A>
	 *            the type of the value
	 * @param variable
	 *            the name of the variable
	 * @param type
	 *            the type which the value should be converted to
	 * @return the value of the variable in the current solution
	 */
	<A> A get(String variable, Class<A> type);

}
