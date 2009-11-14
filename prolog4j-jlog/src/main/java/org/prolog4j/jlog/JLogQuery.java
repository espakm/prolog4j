package org.prolog4j.jlog;

import java.util.Hashtable;

import org.prolog4j.Query;
import org.prolog4j.Solution;

/**
 * The JLog implementation of the Query class.
 */
public class JLogQuery extends Query {

	/** The JLog prover used to process this query. */
	private final JLogProver prover;
	
	/** Stores the initial binding of variables. */
	private Hashtable<String, Object> bindings;

	/**
	 * Creates an object that represents a Prolog query in JLog.
	 * 
	 * @param prover the JLog prover
	 * @param goal the Prolog goal
	 */
	JLogQuery(JLogProver prover, String goal) {
		super(goal);
		this.prover = prover;
		this.bindings = new Hashtable<String, Object>(getPlaceholderNames().size());
	}

	@Override
	public <A> Solution<A> solve(Object... actualArgs) {
		int i = 0;
		for (String var: getPlaceholderNames()) {
			if (!bindings.contains(var)) {
				bindings.put(var, actualArgs[i++]);
			}
		}
		return new JLogSolution<A>(prover, getGoal(), bindings);
	}

	@Override
	public Query bind(int argument, Object value) {
		bindings.put(getPlaceholderNames().get(argument), value);
		return this;
	}

	@Override
	public Query bind(String variable, Object value) {
		bindings.put(variable, value);
		return this;
	}

}
