package org.prolog4j.jlog;

import org.prolog4j.AbstractProver;
import org.prolog4j.Query;

import ubc.cs.JLog.Foundation.jPrologAPI;

/**
 * Represents a Prolog knowledge base and provides methods for solving queries
 * on it. The prover itself is not responsible for processing the solutions.
 */
public class JLogProver extends AbstractProver {

	/** Class version for serialization. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The JLog engine that is used for storing the knowledge base and 
	 * solving queries on it.
	 */
	private final jPrologAPI engine;

	/**
	 * Creates a JLog prover.
	 */
	JLogProver() {
		engine = new jPrologAPI("");
		engine.setTranslation(new TermTranslation());
	}

	/**
	 * Returns the jTrolog engine used by the prover.
	 * @return the jTrolog engine
	 */
	public jPrologAPI getEngine() {
		return engine;
	}

	@Override
	public Query query(String goal) {
		return new JLogQuery(engine, goal);
	}

	@Override
	public void loadLibrary(String className) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addTheory(String theory) {
		engine.consultSource(theory);
	}

	@Override
	public void addTheory(String... theory) {
		StringBuilder sb = new StringBuilder();
		for (String factOrRule : theory) {
			sb.append(factOrRule).append('\n');
		}
		engine.consultSource(sb.toString());
	}

}
