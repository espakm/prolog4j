/* 
 * Copyright (c) 2010 Miklos Espak
 * All rights reserved.
 * 
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 * 
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 * 
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.prolog4j.jtrolog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.prolog4j.ConversionPolicy;
import org.prolog4j.SolutionIterator;
import org.prolog4j.UnknownVariableException;

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
		// this.defaultOutputVariable = defaultVarName;
		on(defaultVarName);
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
				throw new UnknownVariableException(variable);
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
			throw new UnknownVariableException(variable);
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
