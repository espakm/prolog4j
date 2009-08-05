package org.prolog4j;

import alice.tuprolog.InvalidLibraryException;
import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.Prolog;
import alice.tuprolog.Theory;

/**
 * Represents a Prolog knowledge base and provides methods for solving queries
 * on it. The prover itself is not responsible for processing the solutions.
 * 
 * @see org.prolog4j.Solution
 */
public class Prover {

	// public static final Prover GLOBAL = new CachingProver();
	public static final Prover GLOBAL = new Prover();

	// static {
	// GLOBAL.engine.addWarningListener(new WarningListener() {
	// public void onWarning(WarningEvent e) {
	// System.out.println(e.getMsg());
	// }
	// });
	// GLOBAL.engine.addOutputListener(new OutputListener() {
	// public void onOutput(alice.tuprolog.event.OutputEvent e) {
	// System.out.println(e.getMsg());
	// };
	// });
	// }

	/**
	 * Returns the global prover.
	 * 
	 * @return the global prover
	 */
	@Deprecated
	public static Prover get() {
		return GLOBAL;
	}

	protected final Prolog engine;

	public Prover() {
		engine = new Prolog();
	}

	public void loadLibrary(String className) {
		try {
			engine.loadLibrary(className);
		} catch (InvalidLibraryException e) {
			throw new RuntimeException(e);
		}
	}

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
		System.out.println("Prover.solve() " + goal);
		// return new Solution2<A>(engine, Solution2.goalTerms(goal, 0));
		return new Solution<A>(engine, goal);
	}

	/**
	 * Solves a Prolog goal and returns an object using which the individual
	 * solutions can be iterated over. The goal must be a single compound term
	 * whose arguments are variables. The inputArgs argument is a bit vector
	 * that show which arguments of the goal term are regarded as inputs.
	 * 
	 * @param <A>
	 *            the type of an element of the solutions
	 * @param goal
	 *            the Prolog goal
	 * @param inputArgs
	 *            denotes the input arguments of the goal as a bit vector
	 * @param actualArgs
	 *            the actual arguments of the goal
	 * @return an object for traversing the solutions
	 */
	@Deprecated
	public <A> Solution<A> solve(String goal, int inputArgs,
			Object... actualArgs) {
		return new Solution<A>(engine, Solution.goalTerms(goal, inputArgs),
				actualArgs);
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
		return new Solution<A>(engine, Solution.goalTerms(goal,
				(1 << actualArgs.length) - 1), actualArgs);
		// return new Solution2<A>(engine, goal, actualArgs);
	}

	// public <A> Solution<A> solve(String goal, Map<String, Object> args) {
	// int argNo = args.size();
	// String[] inputArgs = args.keySet().toArray(new String[argNo]);
	// Arrays.sort(inputArgs);
	// Object[] actualArgs = new Object[argNo];
	// for (int i = 0; i < argNo; ++i)
	// actualArgs[i] = args.get(inputArgs[i]);
	// return new Solution<A>(engine, Solution.goalTerms(goal, inputArgs),
	// actualArgs);
	// }

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
	public <A> Solution<A> solve(String goal, String[] inputArgs,
			Object[] actualArgs) {
		return new Solution<A>(engine, Solution.goalTerms(goal, inputArgs),
				actualArgs);
	}

	/**
	 * Adds a Prolog theory to the knowledge base.
	 * 
	 * @param theory
	 *            the Prolog theory
	 */
	public void addTheory(String theory) {
		try {
			engine.addTheory(new Theory(theory));
		} catch (InvalidTheoryException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a Prolog theory to the knowledge base. The elements of the arguments
	 * must represent individual Prolog facts and rules.
	 * 
	 * @param theory
	 *            the Prolog theory
	 */
	public void addTheory(String... theory) {
		StringBuilder sb = new StringBuilder();
		for (String factOrRule : theory)
			sb.append(factOrRule).append('\n');
		try {
			engine.addTheory(new Theory(sb.toString()));
		} catch (InvalidTheoryException e) {
			e.printStackTrace();
		}
	}

}
