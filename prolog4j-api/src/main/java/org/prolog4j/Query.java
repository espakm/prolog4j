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

//import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Prolog query. It is supposed to be created by 
 * {@link Prover#query(String)}.
 */
public abstract class Query {

	/** The Prolog goal to be solved. */
	private final String goal;
	
	/** The name of the named placeholders of the goal. */
	private final ArrayList<String> placeholderNames;

//	/**
//	 * Weak references to the objects referred by the query.
//	 */
//	List<Reference> weakReferences;
	
	/**
	 * Creates a query object.
	 * 
	 * @param goalPattern the Prolog goal
	 */
	protected Query(final String goalPattern) {
		placeholderNames = new ArrayList<String>();
		StringBuilder goalB = new StringBuilder(goalPattern);
		String newVarPrefix = null;
		for (int i = 0, start = 0; true; ++i) {
			start = goalB.indexOf("?", start);
			if (start == -1) {
				break;
			}
			if (start < goalB.length() - 1 && goalB.charAt(start + 1) == '?') {
				goalB.deleteCharAt(start);
				++start;
				continue;
			}
			int end = start + 1;
			while (end < goalB.length() 
				   && Character.isLetterOrDigit(goalB.charAt(end))) {
				++end;
			}
			if (end == start + 1) {
				if (newVarPrefix == null) {
					newVarPrefix = findNewVarPrefix(goalPattern);
				}
				String variable = newVarPrefix + i;
				placeholderNames.add(variable);
				goalB.replace(start, start + 1, variable);
			} else {
				placeholderNames.add(goalB.substring(start + 1, end));
				goalB.delete(start, start + 1);
			}
			start = end;
		}
		this.goal = goalB.toString();
		placeholderNames.trimToSize();
	}
	
	/**
	 * Returns the Prolog goal to be solved. The placeholders are removed from
	 * it, so it may differ from the original goal passed to the constructor.
	 * 
	 * @return the Prolog goal to be solved
	 */
	protected final String getGoal() {
		return goal;
	}

	/**
	 * Returns a list with the name of the place holders in the query.
	 * @return the placeholderNames
	 */
	protected final List<String> getPlaceholderNames() {
		return placeholderNames;
	}

//	/**
//	 * Returns a list with the place holders in the query.
//	 * 
//	 * @return the place holders
//	 */
//	protected List<PlaceHolder> getPlaceholders() {
//		return placeholders;
//	}

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
	 * placeholders before solving the goal.
	 * 
	 * @param <A>
	 *            the type of an element of the solutions
	 * @param actualArgs
	 *            the actual arguments of the goal
	 * @return an object for traversing the solutions
	 */
	public abstract <A> Solution<A> solve(Object... actualArgs);

	/**
	 * Binds a value to the specified argument of the goal. The argument is 
	 * specified by its position. Numbering starts from zero.
	 * <p>
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

//	public void assertz(Object... args) {
//		solve("assertz(" + goal.substring(0, goal.lastIndexOf('.')) + ").", args);
//	}
//	
//	public void retract() {
//		solve("retract(" + goal.substring(0, goal.lastIndexOf('.')) + ").");
//	}
//
//	List<Reference> getWeakReferences() {
//		return new ArrayList(0);
//	}
	
}
