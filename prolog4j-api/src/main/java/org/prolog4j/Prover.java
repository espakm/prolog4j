package org.prolog4j;

import java.util.Map;

public interface Prover {

	/**
	 * Solves a Prolog goal and returns an object using which the individual
	 * solutions can be iterated over.
	 * 
	 * @param <A>
	 *            the type of an element of the solutions
	 * @param goal
	 *            the Prolog goal
	 * @return an object for traversing the solutions
	 */
	public <A> Solution<A> solve(String goal);

	/**
	 * Solves a Prolog goal and returns an object using which the individual
	 * solutions can be iterated over. The goal must be a single compound term
	 * whose arguments are variables. The arity of the goal term has to equal
	 * the number of actual arguments. The actual arguments will be bound to the
	 * variables before solving the goal.
	 * 
	 * @param <A>
	 *            the type of an element of the solutions
	 * @param goal
	 *            the Prolog goal
	 * @param actualArgs
	 *            the actual arguments of the goal
	 * @return an object for traversing the solutions
	 */
	public <A> Solution<A> solve(String goal, Object... actualArgs);

	/**
	 * Solves a Prolog goal and returns an object using which the individual
	 * solutions can be iterated over. The goal has to contain variables with
	 * the names specified by the second argument. The actual arguments will be
	 * bound to these variables before solving the goal.
	 * 
	 * @param <A>
	 *            the type of an element of the solutions
	 * @param goal
	 *            the Prolog goal
	 * @param inputArgs
	 *            the names of the input variables of the goal
	 * @param actualArgs
	 *            the actual arguments of the goal
	 * @return an object for traversing the solutions
	 */
	public <A> Solution<A> solve(String goal, String[] inputArgs, Object[] actualArgs);

	/**
	 * Solves a Prolog goal and returns an object using which the individual
	 * solutions can be iterated over. The second argument contains values bound
	 * to variable names. These actual arguments will be bound to the variables
	 * before solving the goal.
	 * 
	 * @param <A>
	 *            the type of an element of the solutions
	 * @param goal
	 *            the Prolog goal
	 * @param actualArgs
	 *            variable->term bindings (input)
	 * @return an object for traversing the solutions
	 */
	public <A> Solution<A> solve(String goal, Map<String, Object> actualArgs);

	/**
	 * Loads in a Prolog library of the specified name.
	 * 
	 * @param library the name of the library
	 */
	public void loadLibrary(String library);

	/**
	 * Adds a Prolog theory to the knowledge base.
	 * 
	 * @param theory
	 *            the Prolog theory
	 */
	public void addTheory(String theory);

	/**
	 * Adds a Prolog theory to the knowledge base. The elements of the arguments
	 * must represent individual Prolog facts and rules.
	 * 
	 * @param theory
	 *            the Prolog theory
	 */
	public void addTheory(String... theory);

}
