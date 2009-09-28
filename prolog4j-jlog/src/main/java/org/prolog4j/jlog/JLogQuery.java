package org.prolog4j.jlog;

import java.util.Hashtable;

import org.prolog4j.Query;
import org.prolog4j.Solution;

import ubc.cs.JLog.Foundation.jPrologAPI;

public class JLogQuery extends Query {

	private final jPrologAPI engine;
	private Hashtable<String, Object> bindings;

	protected JLogQuery(jPrologAPI engine, String goal) {
		super(goal);
		this.engine = engine;
		this.bindings = new Hashtable<String, Object>(inputVarNames.size());
	}

	@Override
	public <A> Solution<A> solve(Object... actualArgs) {
		int i = 0;
		for (String var: inputVarNames)
			if (!bindings.contains(var))
				bindings.put(var, actualArgs[i++]);
		return new JLogSolution<A>(engine, goal, bindings);
	}

	@Override
	public Query bind(int argument, Object value) {
		bindings.put(inputVarNames.get(argument), value);
		return this;
	}

	@Override
	public Query bind(String variable, Object value) {
		bindings.put(variable, value);
		return this;
	}

}
