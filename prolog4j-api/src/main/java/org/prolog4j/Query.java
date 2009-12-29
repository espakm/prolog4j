package org.prolog4j;

//import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Prolog query. It is supposed to be created by 
 * {@link Prover#query(String)}.
 */
public abstract class Query {

	/**
	 * Represents a place holder in a query.
	 */
	protected static class PlaceHolder {
		static final int CONVERT = 0, STRONG = 1, WEAK = 2;
		
		/** The name of the place holder. */
		public final String name;
		/** The type of the place holder. */
		public final int type;
		
		/**
		 * Constructs a place holder object.
		 * 
		 * @param name the name
		 * @param type the type
		 */
		public PlaceHolder(String name, int type) {
			super();
			this.name = name;
			this.type = type;
		}
		
	}
	
	/** The Prolog goal to be solved. */
	private final String goal;
	
	/** The name of the named placeholders of the goal. */
	private final ArrayList<String> placeholderNames;

	/** The named placeholders of the goal. */
	private final ArrayList<PlaceHolder> placeholders;

//	/**
//	 * Weak references to the objects referred by the query.
//	 */
//	List<Reference> weakReferences;
	
	/**
	 * Creates a query object.
	 * 
	 * @param goal the Prolog goal
	 */
	protected Query(String goal) {
		placeholderNames = new ArrayList<String>();
		placeholders = new ArrayList<PlaceHolder>();
		StringBuilder goalB = new StringBuilder(goal);
		String newVarPrefix = null;
		for (int i = 0, end = 0; true; ++i) {
			end = goalB.indexOf("{", end);
			if (end == -1) {
				break;
			}
			int formatElementEnd = goalB.indexOf("}", end + 1);
			if (formatElementEnd == -1) {
				++end;
				continue;
			}
			String formatElement = goalB.substring(end + 1, formatElementEnd);
			int type = validFormat(formatElement);
			if (type == -1) {
				++end;
				continue;
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
				placeholderNames.add(variable);
				placeholders.add(new PlaceHolder(variable, type));
				goalB.replace(end, end + formatElement.length() + 2, variable);
			} else {
				placeholderNames.add(goalB.substring(start + 1, end));
				placeholders.add(new PlaceHolder(goalB.substring(start + 1, end), type));
				goalB.delete(end, end + formatElement.length() + 2);
			}
		}
		this.goal = goalB.toString();
		placeholderNames.trimToSize();
		placeholders.trimToSize();
	}
	
	/**
	 * Validates the format of the place holder.
	 * @param formatElement the format element of the place holder 
	 *             (without the braces)
	 * @return the type of the format element (-1 if it is invalid)  
	 */
	private int validFormat(String formatElement) {
		if (formatElement.equals("")) {
			return 0;
		}
		if (formatElement.equals("strong")) {
			return 1;
		}
		if (formatElement.equals("weak")) {
			return 2;
		}
		return -1;
	}

	/**
	 * Returns the Prolog goal to be solved. The placeholders are removed from
	 * it, so it may differ from the original goal passed to the constructor.
	 * 
	 * @return the Prolog goal to be solved
	 */
	protected String getGoal() {
		return goal;
	}

	/**
	 * Returns a list with the name of the place holders in the query.
	 * @return the placeholderNames
	 */
	protected List<String> getPlaceholderNames() {
		return placeholderNames;
	}

	/**
	 * Returns a list with the place holders in the query.
	 * @return the place holders
	 */
	protected List<PlaceHolder> getPlaceholders() {
		return placeholders;
	}

	/**
	 * Creates a new variable name that does not occurs in the goal.
	 * 
	 * @param goal the goal
	 * @return a new, not conflicting variable name
	 */
	private String findNewVarPrefix(String goal) {
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
