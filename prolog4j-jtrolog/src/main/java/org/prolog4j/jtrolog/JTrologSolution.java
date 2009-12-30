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
package org.prolog4j.jtrolog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.prolog4j.ConversionPolicy;
import org.prolog4j.SolutionIterator;
import org.prolog4j.UnknownVariable;

import jTrolog.errors.NoMorePrologSolutions;
import jTrolog.engine.Prolog;
import jTrolog.engine.Solution;
import jTrolog.terms.Struct;
import jTrolog.terms.Term;

/**
 * The <tt>Solution</tt> class is responsible for traversing through the
 * solutions of a query.
 * 
 * @param <S>
 *            the type of the values of the variable that is of special interest
 */
public class JTrologSolution<S> extends org.prolog4j.Solution<S> {

	/** The conversion policy of the prover that is used for solving this query. */
	private final ConversionPolicy cp;
	
	/** The jTrolog engine that is used for solving the query. */
	private final Prolog engine;
	
	/** The name of the output variables of the query. */
	private String[] outputVarNames;

	/** 
	 * This jTrolog object provides the bindings of one solution of a query.
	 */
	private Solution solution;
	
	/** True if the query has a solution, otherwise false. */
	private final boolean success;

	/**
	 * Creates a <tt>JTrologSolution</tt> object for traversing through the solutions
	 * for a Prolog query.
	 * 
	 * @param prover jTrolog prover
	 * @param sGoal a Prolog goal
	 * @param defaultVarName the name of the output variable of special interest
	 * @param outputVarNames the name of each output variable
	 */
	JTrologSolution(JTrologProver prover, Struct sGoal, String defaultVarName, 
			String[] outputVarNames) {
		this.cp = prover.getConversionPolicy();
		this.engine = prover.getEngine();
		this.defaultOutputVariable = defaultVarName;
		this.outputVarNames = outputVarNames;
		try {
			solution = engine.solve(sGoal);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		success = solution.success();
	}

	@Override
	public boolean isSuccess() {
		return success;
	}

	@Override
	public <A> A get(String variable) {
		if (clazz == null) {
			Term binding = solution.getBinding(variable);
			if (binding == null) {
				throw new UnknownVariable(variable);
			}
//			return (A) prover.getConversionPolicy().convertTerm(binding);
			return (A) cp.convertTerm(binding);
		}
		return (A) get(variable, clazz);
	}

	@Override
	public <A> A get(String variable, Class<A> type) {
		Term binding = solution.getBinding(variable);
		if (binding == null) {
			throw new UnknownVariable(variable);
		}
//		return (A) prover.getConversionPolicy().convertTerm(binding, type);
		return (A) cp.convertTerm(binding, type);
	}

	@Override
	public void collect(Collection... collections) {
		SolutionIterator<S> it = iterator();
		while (it.hasNext()) {
			it.next();
			for (int i = 0; i < collections.length; ++i) {
				collections[i].add(it.get(outputVarNames[i]));
			}
		}
	}

	@Override
	public List<?>[] toLists() {
		List<?>[] lists = new List<?>[outputVarNames.length];
		for (int i = 0; i < lists.length; ++i) {
			lists[i] = new ArrayList();
		}
		collect(lists);
		return lists;
	}

	@Override
	protected boolean fetch() {
		try {
			return engine.hasOpenAlternatives()
						&& (solution = engine.solveNext()).success();
		} catch (NoMorePrologSolutions e) {
			// Should not happen.
			throw new IllegalStateException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

}
