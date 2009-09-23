package org.prolog4j.jlog;

import org.prolog4j.Query;
import org.prolog4j.Solution;

import ubc.cs.JLog.Foundation.jPrologAPI;

public class JLogQuery extends Query {

	private final jPrologAPI engine;

	protected JLogQuery(jPrologAPI engine, String goal) {
		super(goal);
		this.engine = engine;
	}

	@Override
	public <A> Solution<A> solve(Object... actualArgs) {
		return new JLogSolution<A>(engine, goal, inputVariables, actualArgs);
	}

}
