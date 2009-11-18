package org.prolog4j.jtrolog;

import org.prolog4j.AbstractProver;
import org.prolog4j.Query;

import jTrolog.errors.InvalidLibraryException;
import jTrolog.errors.PrologException;
import jTrolog.engine.Prolog;

/**
 * Represents a Prolog knowledge base and provides methods for solving queries
 * on it. The prover itself is not responsible for processing the solutions.
 */
public class JTrologProver extends AbstractProver {

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
	 * The jTrolog engine that is used for storing the knowledge base and 
	 * solving queries on it.
	 */
	private final Prolog engine;

	/**
	 * Creates a jTrolog prover of the given name.
	 */
	JTrologProver() {
		super();
		engine = new Prolog();
	}

	/**
	 * Returns the jTrolog engine used by the prover.
	 * @return the jTrolog engine
	 */
	public Prolog getEngine() {
		return engine;
	}

	@Override
	public Query query(String goal) {
		return new JTrologQuery(this, goal);
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
		for (String factOrRule : theory) {
			sb.append(factOrRule).append('\n');
		}
		try {
			engine.addTheory(sb.toString());
		} catch (PrologException e) {
			e.printStackTrace();
		}
	}

}
