package org.prolog4j.tuprolog;

import java.util.Map;

import org.prolog4j.Solution;
import org.prolog4j.helpers.NamedProverBase;

import alice.tuprolog.InvalidLibraryException;
import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.Prolog;
import alice.tuprolog.Theory;

/**
 * Represents a Prolog knowledge base and provides methods for solving queries
 * on it. The prover itself is not responsible for processing the solutions.
 * 
 * @see org.prolog4j.impl.Solution
 */
public class TuPrologProver extends NamedProverBase {

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

	private static final long serialVersionUID = 1L;
	
	protected final Prolog engine;

	public TuPrologProver(String name) {
		super(name);
		engine = new Prolog();
	}

	public Prolog getEngine() {
		return engine;
	}
	
	@Override
	public <A> Solution<A> solve(String goal) {
		// return new Solution2<A>(engine, Solution2.goalTerms(goal, 0));
		return new TuPrologSolution<A>(engine, goal);
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
	protected <A> Solution<A> solve(String goal, int inputArgs,
			Object... actualArgs) {
		return new TuPrologSolution<A>(engine, goal, inputArgs, actualArgs);
	}

	@Override
	public <A> Solution<A> solve(String goal, Object... actualArgs) {
		return new TuPrologSolution<A>(engine, goal, actualArgs);
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

	@Override
	public <A> Solution<A> solve(String goal, String[] inputArgs,
			Object[] actualArgs) {
		return new TuPrologSolution<A>(engine, TuPrologSolution.goalTerms(goal,
				inputArgs), actualArgs);
	}

	@Override
	public <A> Solution<A> solve(String goal, Map<String, Object> actualArgs) {
		return new TuPrologSolution<A>(engine, TuPrologSolution.goalTerms(goal,
				actualArgs.keySet()), actualArgs);
	}

	@Override
	public void loadLibrary(String className) {
		try {
			engine.loadLibrary(className);
		} catch (InvalidLibraryException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addTheory(String theory) {
		try {
			engine.addTheory(new Theory(theory));
		} catch (InvalidTheoryException e) {
			e.printStackTrace();
		}
	}

	@Override
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
