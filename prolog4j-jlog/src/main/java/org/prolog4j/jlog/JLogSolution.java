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
package org.prolog4j.jlog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.prolog4j.ConversionPolicy;
import org.prolog4j.InvalidQueryException;
import org.prolog4j.SolutionIterator;
import org.prolog4j.UnknownVariableException;

import ubc.cs.JLog.Foundation.jPrologAPI;
import ubc.cs.JLog.Parser.SyntaxErrorException;

/**
 * The <tt>Solution</tt> class is responsible for traversing through the
 * solutions of a query.
 * 
 * @param <S>
 *            the type of the values of the variable that is of special interest
 */
public class JLogSolution<S> extends org.prolog4j.Solution<S> {

	/** The conversion policy used by the JLog prover that is used for solving the query. */
	private final ConversionPolicy conversionPolicy;

//	private static final ConversionPolicy conversionPolicy = ProverFactory.getConversionPolicy();
	
	/** The JLog engine that is used for solving the query. */
	private final jPrologAPI prolog;

	/** The name of the output variables of the query. */
	private String[] outputVarNames;

	/** Stores the bindings of one solution of the query. */
	private Hashtable<String, Object> solution;

	/** True if the query has a solution, otherwise false. */
	private final boolean success;

	/**
	 * Constructs a JLogSolution instance.
	 * 
	 * @param prover the JLog prover, using which the solutions have to be found
	 * @param goal the Prolog goal to solve
	 * @param initialBindings the initial bindings of the variables
	 */
	JLogSolution(JLogProver prover, String goal, Hashtable<String, Object> initialBindings) {
		super();
		this.conversionPolicy = prover.getConversionPolicy();
		this.prolog = prover.getEngine();
		try {
			solution = prolog.query(goal, initialBindings);
		} catch (SyntaxErrorException e) {
			throw new InvalidQueryException(goal, e);
		}
		success = solution != null;
		if (!success || solution.size() == 0) {
			return;
		}
		outputVarNames = new String[solution.size()];
		int i = 0;
		for (String var: solution.keySet()) {
			outputVarNames[i++] = var;
		}
		//defaultOutputVariable = outputVarNames[outputVarNames.length - 1];
		on(outputVarNames[outputVarNames.length - 1]);
	}

	@Override
	public boolean isSuccess() {
		return success;
	}

	@Override
	public <A> A get(String variable) {
		if (clazz == null) {
			Object term = solution.get(variable);
			if (term == null) {
				throw new UnknownVariableException(variable);
			}
			return (A) conversionPolicy.convertTerm(term);
		}
		return (A) get(variable, clazz);
	}

	@Override
	public <A> A get(String variable, Class<A> type) {
		Object term = solution.get(variable);
		if (term == null) {
			throw new UnknownVariableException(variable);
		}
		return (A) conversionPolicy.convertTerm(term, type);
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
		solution = prolog.retry();
		return solution != null;
	}

}
