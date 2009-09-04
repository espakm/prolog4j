package org.prolog4j;

public abstract class Prover {

	private static final String[] EMPTY_ARRAY = new String[0];
	
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
	public <A> Solution<A> solve(String goal) {
		return solve(goal, EMPTY_ARRAY, EMPTY_ARRAY);
	}

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
	public <A> Solution<A> solve(String goal, Object... actualArgs) {
		String[] variables = new String[actualArgs.length];
		StringBuilder goalB = new StringBuilder(goal);
		for (int i = 0; i < variables.length; ++i) {
			int end = goalB.indexOf("{}");
			if (end == -1)
				throw new RuntimeException("Invalid format string.");
			int start;
			for (start = end - 1; start >= 0 && Character.isJavaIdentifierPart(goalB.charAt(start)); --start);
			if (start < 0)
				continue;
			variables[i] = goalB.substring(start + 1, end);
			goalB.delete(end, end + 2);
		}
		return solve(goalB.toString(), variables, actualArgs);
	}

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
	protected abstract <A> Solution<A> solve(String goal, String[] inputArgs, Object[] actualArgs);

	/**
	 * Loads in a Prolog library of the specified name.
	 * 
	 * @param library the name of the library
	 */
	public abstract void loadLibrary(String library);

	/**
	 * Adds a Prolog theory to the knowledge base.
	 * 
	 * @param theory
	 *            the Prolog theory
	 */
	public abstract void addTheory(String theory);

	/**
	 * Adds a Prolog theory to the knowledge base. The elements of the arguments
	 * must represent individual Prolog facts and rules.
	 * 
	 * @param theory
	 *            the Prolog theory
	 */
	public abstract void addTheory(String... theory);

}
