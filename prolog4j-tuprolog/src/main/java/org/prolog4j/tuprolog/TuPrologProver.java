package org.prolog4j.tuprolog;

import org.prolog4j.AbstractProver;
import org.prolog4j.Query;

import alice.tuprolog.InvalidLibraryException;
import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.Prolog;
import alice.tuprolog.Theory;

/**
 * Represents a Prolog knowledge base and provides methods for solving queries
 * on it. The prover itself is not responsible for processing the solutions.
 */
public class TuPrologProver extends AbstractProver {

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

	/** Class version for serialization. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The tuProlog engine that is used for storing the knowledge base and 
	 * solving queries on it.
	 */
	private final Prolog engine;

	/**
	 * Creates a tuProlog prover of the given name.
	 */
	TuPrologProver() {
		engine = new Prolog();
	}

	/**
	 * Returns the tuProlog engine used by the prover.
	 * @return the tuProlog engine
	 */
	public Prolog getEngine() {
		return engine;
	}
	
	@Override
	public Query query(String goal) {
		return new TuPrologQuery(engine, goal);
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
		for (String factOrRule : theory) {
			sb.append(factOrRule).append('\n');
		}
		try {
			engine.addTheory(new Theory(sb.toString()));
		} catch (InvalidTheoryException e) {
			e.printStackTrace();
		}
	}

}
