package org.prolog4j;

import java.util.ArrayList;

/**
 * Represents a Prolog query. It is supposed to be created by 
 * {@link Prover#query(String)}.
 */
public abstract class Query {

	/** The goal to be solved. */
	private final String goal;
	
	/** The name of the input variables of the goal. */
	protected final ArrayList<String> inputVarNames;
	
	/**
	 * Creates a query object.
	 * 
	 * @param goal the prolog goal
	 */
	protected Query(final String goal) {
		inputVarNames = new ArrayList<String>();
		StringBuilder goalB = new StringBuilder(goal);
		String newVarPrefix = null;
		int end = 0;
		for (int i = 0; true; ++i) {
			end = goalB.indexOf("{}", end);
			if (end == -1) {
				break;
			}
			int start = end - 1;
			while (start >= 0 && Character.isJavaIdentifierPart(goalB.charAt(start))) {
				--start;
			}
			if (start == end - 1) {
				if (start >= 0 && goalB.charAt(start) == '\\') {
					goalB.deleteCharAt(start);
					continue;
				}
				if (newVarPrefix == null) {
					newVarPrefix = findNewVarPrefix(goal);
				}
				String variable = newVarPrefix + i;
				inputVarNames.add(variable);
				goalB.replace(end, end + 2, variable);
			} else {
				inputVarNames.add(goalB.substring(start + 1, end));
				goalB.delete(end, end + 2);
			}
		}
		this.goal = goalB.toString();
		inputVarNames.trimToSize();
	}
	
	/**
	 * Returns the Prolog goal to be solved. It may differ from the original 
	 * goal passed to the constructor.
	 * 
	 * @return the goal to be solved
	 */
	protected String getGoal() {
		return goal;
	}

	/**
	 * Creates a new variable name that does not occurs in the goal.
	 * 
	 * @param goal the goal
	 * @return a new, not conflicting variable name
	 */
	private String findNewVarPrefix(final String goal) {
		if (!goal.contains("P4J_")) {
			return "P4J_";
		}
		for (int i = 0; true; ++i) {
			String s = String.valueOf(i);
			if (!goal.contains(s)) {
				return "P4J_" + s;
			}
		}
	}

	/**
	 * Solves the Prolog goal and returns an object using which the individual
	 * solutions can be iterated over. The actual arguments will be bound to the
	 * variables before solving the goal.
	 * 
	 * @param <A>
	 *            the type of an element of the solutions
	 * @param actualArgs
	 *            the actual arguments of the goal
	 * @return an object for traversing the solutions
	 */
	public abstract <A> Solution<A> solve(Object... actualArgs);

	/**
	 * Binds a value to the specified argument of the goal. The
	 * argument is specified by its position. Numbering starts
	 * from zero.
	 * 
	 * The method returns the same query instance.
	 * 
	 * @param argument the number of the argument of the goal
	 * @param value the value to be bound to the argument
	 * @return the same query instance
	 */
	public abstract Query bind(int argument, Object value);

	/**
	 * Binds a value to the specified argument of the goal. The
	 * argument is specified by its name.
	 * 
	 * @param variable the name of the variable of the goal
	 * @param value the value to be bound to the variable
	 * @return the same query instance
	 */
	public abstract Query bind(String variable, Object value);
	
}
