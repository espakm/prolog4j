package org.prolog4j.jlog;

import org.prolog4j.AbstractProver;
import org.prolog4j.Query;

import ubc.cs.JLog.Foundation.jPrologAPI;

/**
 * Represents a Prolog knowledge base and provides methods for solving queries
 * on it. The prover itself is not responsible for processing the solutions.
 * 
 * @see org.prolog4j.impl.Solution
 */
public class JLogProver extends AbstractProver {

	/** Class version for serialization. */
	private static final long serialVersionUID = 1L;
	
	protected final jPrologAPI prolog;

	public JLogProver(String name) {
		super(name);
		prolog = new jPrologAPI("");
		prolog.setTranslation(new TermTranslation());
	}

	@Override
	public Query query(String goal) {
		return new JLogQuery(prolog, goal);
	}

	@Override
	public void loadLibrary(String className) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addTheory(String theory) {
		prolog.consultSource(theory);
	}

	@Override
	public void addTheory(String... theory) {
		StringBuilder sb = new StringBuilder();
		for (String factOrRule : theory)
			sb.append(factOrRule).append('\n');
		prolog.consultSource(sb.toString());
	}

}
