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
package org.prolog4j.swi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.NoSuchElementException;

import jpl.Term;
import jpl.Variable;

import org.prolog4j.ConversionPolicy;
import org.prolog4j.Prover;
import org.prolog4j.ProverFactory;
import org.prolog4j.Solution;
import org.prolog4j.SolutionIterator;
import org.prolog4j.UnknownVariableException;

/**
 * The <tt>Solution</tt> class is responsible for traversing through the
 * solutions of a query.
 * 
 * @param <S>
 *            the type of the values of the variable that is of special interest
 */
public class SWIPrologSolution<S> extends Solution<S> {

	/** The SWI-Prolog prover that is used for solving this query. */
	private Prover prover;

	/** The conversion policy of the SWI-Prolog prover that is used for solving this query. */
	private final ConversionPolicy cp;

//	private static final Terms terms = Terms.getInstance();

	/** The list of variables occurring in the query. */
	private List<String> vars;

	/** This object provides the bindings for one solution of the query. */
	private jpl.Query query;
	
	/** This object provides the bindings for one solution of the query. */
	private Hashtable<String, Term> solution;
	
	/** True if the query has a solution, otherwise false. */
	private final boolean success;

	/**
	 * Creates an object, using which the solutions of a query can be accessed.
	 * 
	 * @param prover the SWI-Prolog prover
	 * @param goal the goal to be solved
	 */
	SWIPrologSolution(Prover prover, Term goal) {
		this.prover = prover;
		this.cp = prover.getConversionPolicy();
		query = new jpl.Query(goal);
		success = query.hasMoreSolutions();
		if (!success) {
			return;
		}
		solution = query.nextSolution();
		vars = new ArrayList(solution.keySet());
		if (vars.size() > 0) {
			// defaultOutputVariable = varName(vars.size() - 1);
			on(varName(vars.size() - 1));
		}
	}

	@Override
	public boolean isSuccess() {
		return success;
	}

	/**
	 * Returns the name of the variable of the specified index.
	 * 
	 * @param varIndex the index of the variable
	 * @return the name of the variable
	 */
	private String varName(int varIndex) {
		return vars.get(varIndex);
	}

	@Override
	public <A> A get(String variable) {
		if (clazz == null) {
			Term term = solution.get(variable);
			if (term == null) {
				throw new UnknownVariableException(variable);
			}
			return (A) cp.convertTerm(term);
		}
		return (A) get(variable, clazz);
	}

	@Override
	public <A> A get(String variable, Class<A> type) {
		Term term = solution.get(variable);
		if (term == null) {
			throw new UnknownVariableException(variable);
		}
		return (A) cp.convertTerm(term, type);
	}

	@Override
	public void collect(Collection... collections) {
		SolutionIterator<S> it = iterator();
		while (it.hasNext()) {
			it.next();
			for (int i = 0; i < collections.length; ++i) {
				collections[i].add(it.get(varName(i)));
			}
		}
	}

	@Override
	public List<?>[] toLists() {
		List<?>[] lists = new List<?>[vars.size() - 1];
		for (int i = 0; i < lists.length; ++i) {
			lists[i] = new ArrayList();
		}
		collect(lists);
		return lists;
	}

	@Override
	protected boolean fetch() {
		boolean hasNext = query.hasMoreSolutions();
		if (hasNext) {
			solution = query.nextSolution();
		}
		return hasNext;
	}

}
