package org.prolog4j.jTrolog;

import org.prolog4j.Solution;
import org.prolog4j.helpers.NamedProverBase;

import jTrolog.errors.InvalidLibraryException;
import jTrolog.errors.PrologException;
import jTrolog.engine.Prolog;

/**
 * Represents a Prolog knowledge base and provides methods for solving queries
 * on it. The prover itself is not responsible for processing the solutions.
 * 
 * @see org.prolog4j.impl.Solution
 */
public class JTrologProver extends NamedProverBase {

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

	public JTrologProver(String name) {
		super(name);
		engine = new Prolog();
	}

	public Prolog getEngine() {
		return engine;
	}
	
	@Override
	protected <A> Solution<A> solve(String goal, String[] inputArgs, Object[] actualArgs) {
		return new JTrologSolution<A>(engine, goal, inputArgs, actualArgs);
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
			engine.addTheory(theory);
		} catch (PrologException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addTheory(String... theory) {
		StringBuilder sb = new StringBuilder();
		for (String factOrRule : theory)
			sb.append(factOrRule).append('\n');
		try {
			engine.addTheory(sb.toString());
		} catch (PrologException e) {
			e.printStackTrace();
		}
	}

}
