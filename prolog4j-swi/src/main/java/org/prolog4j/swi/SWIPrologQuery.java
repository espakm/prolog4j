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

import java.util.LinkedList;
import java.util.List;

import jpl.PrologException;
import jpl.Term;
import jpl.Util;
import jpl.Variable;
import jpl.fli.Prolog;

import org.prolog4j.ConversionPolicy;
import org.prolog4j.InvalidQueryException;
import org.prolog4j.Query;
import org.prolog4j.Solution;
import org.prolog4j.UnknownVariableException;

/**
 * The tuProlog implementation of the Query class.
 */
public class SWIPrologQuery extends Query {
	
	/** The tuProlog prover used to process this query. */
	private final SWIPrologProver prover;
	
	/** The conversion policy of the prover that is used for solving this query. */
	private final ConversionPolicy cp;
	
	/** The SWI-Prolog representation of the goal to be solved. */
	private jpl.Term sGoal;

	/** The SWI-Prolog variables representing the input variables of the goal. */
	private Variable[] inputVars;
	
	/** 
	 * This field stores those elements of {@link inputVars} that are still not
	 * bound.
	 */
	private List<Variable> unboundVars;
	
	/**
	 * Creates a SWI-Prolog query object.
	 * 
	 * @param prover the SWI-Prolog prover to process the query
	 * @param goal the SWI-Prolog goal to be solved
	 */
	SWIPrologQuery(SWIPrologProver prover, String goal) {
		super(goal);
		this.prover = prover;
		this.cp = prover.getConversionPolicy();
//		List<PlaceHolder> placeholders = getPlaceholders();
		List<String> placeholderNames = getPlaceholderNames();
		int placeholderNo = placeholderNames.size();
		inputVars = new Variable[placeholderNo];

		try {
		sGoal = Util.textToTerm(getGoal());
		} catch (PrologException exc) {
			throw new InvalidQueryException(getGoal());
		}
		for (int i = 0, index = 0; i < placeholderNo; ++i, ++index) {
			Variable argVar = new Variable(placeholderNames.get(i));
			Variable arg = new Variable("J__" + argVar.name());
			sGoal = new jpl.Compound(",", new Term[]{new jpl.Compound("=", new Term[]{argVar, arg}), sGoal});
			inputVars[index] = arg;
		}
//		sGoal.resolveTerm();
	}

	@Override
	public <A> Solution<A> solve(Object... actualArgs) {
//		prover.reclaimObsoleteFacts();
		int i = 0;
//		for (Variable var: unboundVars) {
		jpl.Term g = sGoal;
		for (String ph: getPlaceholderNames()) {
			g = new jpl.Compound(
						",", 
						new Term[]{
								new jpl.Compound("=", new Term[]{new Variable(ph), 
										(Term) cp.convertObject(actualArgs[i++])}),
								g});
		}
		return new SWIPrologSolution<A>(prover, g);
	}

	@Override
	public Query bind(int argument, Object value) {
		throw new UnsupportedOperationException();
//		inputVars[argument].free();
//		engine.unify(inputVars[argument], (Term) cp.convertObject(value));
//		return this;
	}

	@Override
	public Query bind(String variable, Object value) {
		throw new UnsupportedOperationException();
//		for (Variable v: inputVars) {
//			if (v.isBound()) {
//				continue;
//			}
//			if (v.name().equals(variable)) {
//				v.free();
//				Prolog.unify(v, (Term) cp.convertObject(value));
//				return this;
//			}
//		}
//		throw new UnknownVariableException(variable);
	}

}
