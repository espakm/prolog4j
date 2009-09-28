package org.prolog4j;

import java.util.ArrayList;

public abstract class Query {

	protected final String goal;
	protected final ArrayList<String> inputVarNames;
	
	protected Query(String goal) {
		inputVarNames = new ArrayList<String>();
		StringBuilder goalB = new StringBuilder(goal);
		String newVarPrefix = null;
		int end = 0;
		for (int i = 0; ; ++i) {
			end = goalB.indexOf("{}", end);
			if (end == -1)
				break;
			int start = end - 1;
			while (start >= 0 && Character.isJavaIdentifierPart(goalB.charAt(start)))
				--start;
			if (start == end - 1) {
				if (start >= 0 && goalB.charAt(start) == '\\') {
					goalB.deleteCharAt(start);
					continue;
				}
				if (newVarPrefix == null)
					newVarPrefix = findNewVarPrefix(goal);
				String variable = newVarPrefix + i;
				inputVarNames.add(variable);
				goalB.replace(end, end + 2, variable);
			}
			else {
				inputVarNames.add(goalB.substring(start + 1, end));
				goalB.delete(end, end + 2);
			}
		}
		this.goal = goalB.toString();
		inputVarNames.trimToSize();
	}
	
	private String findNewVarPrefix(String goal) {
		if (!goal.contains("P4J_"))
			return "P4J_";
		for (int i = 0; true; ++i) {
			String s = String.valueOf(i);
			if (!goal.contains(s))
				return "P4J_" + s;
		}
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
	public abstract <A> Solution<A> solve(Object... actualArgs);

	public abstract Query bind(int argument, Object value);

	public abstract Query bind(String variable, Object value);
	
}
