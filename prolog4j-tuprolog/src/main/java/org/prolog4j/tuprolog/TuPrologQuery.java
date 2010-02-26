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
package org.prolog4j.tuprolog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.prolog4j.ConversionPolicy;
import org.prolog4j.InvalidQueryException;
import org.prolog4j.Query;
import org.prolog4j.Solution;
import org.prolog4j.UnknownVariableException;

import alice.tuprolog.InvalidTermException;
import alice.tuprolog.Parser;
import alice.tuprolog.Prolog;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Var;

/**
 * The tuProlog implementation of the Query class.
 */
public class TuPrologQuery extends Query {
	
	/** The tuProlog prover used to process this query. */
	private final TuPrologProver prover;
	
	/** The conversion policy of the prover that is used for solving this query. */
	private final ConversionPolicy cp;
	
	/** The tuProlog engine used to process this query. */
	private final Prolog engine;

	/** The tuProlog representation of the goal to be solved. */
	private Struct sGoal;

	/** The tuProlog variables representing the input variables of the goal. */
	private Var[] inputVars;
	
	/** Contains the variables explicitly bound through the bind methods. */
	private Set<Var> explicitlyBoundVars = new HashSet<Var>();
	
	/**
	 * Creates a TuProlog query object.
	 * 
	 * @param prover the tuProlog prover to process the query
	 * @param goal the Prolog goal to be solved
	 */
	TuPrologQuery(TuPrologProver prover, String goal) {
		super(goal);
		this.prover = prover;
		this.cp = prover.getConversionPolicy();
		this.engine = prover.getEngine();
		List<String> placeholderNames = getPlaceholderNames();
//		List<PlaceHolder> placeholders = getPlaceholders();
		int placeholderNo = placeholderNames.size();
		inputVars = new Var[placeholderNo];
		try {
			Parser parser = new Parser(getGoal());
			sGoal = (Struct) parser.nextTerm(true);
			for (int i = 0, index = 0; i < placeholderNo; ++i, ++index) {
				Var argVar = new Var(placeholderNames.get(i));
				Var arg = new Var("J$" + argVar.getOriginalName());
				sGoal = new Struct(",", new Struct("=", argVar, arg), sGoal);
				inputVars[index] = arg;
			}
			sGoal.resolveTerm();
		} catch (InvalidTermException e) {
			throw new InvalidQueryException(goal, e);
		}
	}

	@Override
	public <A> Solution<A> solve(Object... actualArgs) {
//		prover.reclaimObsoleteFacts();
		int i = 0;
		for (Var var: inputVars) {
			if (explicitlyBoundVars.contains(var)) {
				continue;
			}
			var.free();
			engine.unify(var, (Term) cp.convertObject(actualArgs[i++]));
		}
		return new TuPrologSolution<A>(prover, sGoal);
	}

	@Override
	public Query bind(int argument, Object value) {
		inputVars[argument].free();
		explicitlyBoundVars.add(inputVars[argument]);
		engine.unify(inputVars[argument], (Term) cp.convertObject(value));
		return this;
	}

	@Override
	public Query bind(String variable, Object value) {
		for (Var v: inputVars) {
//			if (v.isBound()) {
//				continue;
//			}
			if (v.getName().equals(variable)) {
				v.free();
				explicitlyBoundVars.add(v);
				engine.unify(v, (Term) cp.convertObject(value));
				return this;
			}
		}
		throw new UnknownVariableException(variable);
	}

}
