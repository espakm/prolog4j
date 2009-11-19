package org.prolog4j;

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

//	/**
//	 * Returns the conversion policy used by the prover.
//	 * 
//	 * @return the conversion policy
//	 */
//	ConversionPolicy getConversionPolicy();

}
