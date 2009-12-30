/*
 * Copyright 2010 by Miklós Espák <espakm@gmail.com>
 * 
 * This file is part of Prolog4J.
 * 
 * Prolog4J is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Prolog4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Prolog4J.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.prolog4j.jlog;

import java.util.Hashtable;

import org.prolog4j.ConversionPolicy;
import org.prolog4j.Query;
import org.prolog4j.Solution;

/**
 * The JLog implementation of the Query class.
 */
public class JLogQuery extends Query {

	/** The JLog prover used to process this query. */
	private final JLogProver prover;
	
	/** The conversion policy of the prover that is used for solving this query. */
	private final ConversionPolicy cp;
	
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
		cp = prover.getConversionPolicy();
		this.bindings = new Hashtable<String, Object>(getPlaceholderNames().size());
	}

	@Override
	public <A> Solution<A> solve(Object... actualArgs) {
		int i = 0;
		for (String var: getPlaceholderNames()) {
			if (!bindings.contains(var)) {
				bindings.put(var, cp.convertObject(actualArgs[i++]));
			}
		}
		return new JLogSolution<A>(prover, getGoal(), bindings);
	}

	@Override
	public Query bind(int argument, Object value) {
		bindings.put(getPlaceholderNames().get(argument), cp.convertObject(value));
		return this;
	}

	@Override
	public Query bind(String variable, Object value) {
		bindings.put(variable, cp.convertObject(value));
		return this;
	}

}
