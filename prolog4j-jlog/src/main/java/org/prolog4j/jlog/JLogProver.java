package org.prolog4j.jlog;

import org.prolog4j.Solution;
import org.prolog4j.helpers.NamedProverBase;

import ubc.cs.JLog.Foundation.jPrologAPI;
import ubc.cs.JLog.Terms.jTermTranslation;

/**
 * Represents a Prolog knowledge base and provides methods for solving queries
 * on it. The prover itself is not responsible for processing the solutions.
 * 
 * @see org.prolog4j.impl.Solution
 */
public class JLogProver extends NamedProverBase {

	private static final long serialVersionUID = 1L;
	
	protected final jPrologAPI prolog;
	private jTermTranslation translator = new TermTranslation();

	public JLogProver(String name) {
		super(name);
		prolog = new jPrologAPI("");
//		prolog.setTranslation(translator);
	}

	@Override
	public <A> Solution<A> solve(String goal) {
		return new JLogSolution<A>(prolog, goal);
	}

	@Override
	protected <A> Solution<A> solve(String goal, String[] inputArgs, Object[] actualArgs) {
		return new JLogSolution<A>(prolog, goal, inputArgs, actualArgs);
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
