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
package org.prolog4j.tuprolog;

import java.util.List;

import org.prolog4j.ConversionPolicy;
import org.prolog4j.InvalidQuery;
import org.prolog4j.Query;
import org.prolog4j.Solution;
import org.prolog4j.UnknownVariable;

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
		List<PlaceHolder> placeholders = getPlaceholders();
		int placeholderNo = placeholders.size();
		inputVars = new Var[placeholderNo];
		try {
			Parser parser = new Parser(getGoal());
			sGoal = (Struct) parser.nextTerm(true);
			for (int i = 0, index = 0; i < placeholderNo; ++i, ++index) {
				Var argVar = new Var(placeholders.get(i).name);
				Var arg = new Var("J$" + argVar.getOriginalName());
				sGoal = new Struct(",", new Struct("=", argVar, arg), sGoal);
				inputVars[index] = arg;
			}
			sGoal.resolveTerm();
		} catch (InvalidTermException e) {
			throw new InvalidQuery(goal, e);
		}
	}

	@Override
	public <A> Solution<A> solve(Object... actualArgs) {
//		prover.reclaimObsoleteFacts();
		int i = 0;
		for (Var var: inputVars) {
			if (var.isBound()) {
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
		engine.unify(inputVars[argument], (Term) cp.convertObject(value));
		return this;
	}

	@Override
	public Query bind(String variable, Object value) {
		for (Var v: inputVars) {
			if (v.isBound()) {
				continue;
			}
			if (v.getName().equals(variable)) {
				v.free();
				engine.unify(v, (Term) cp.convertObject(value));
				return this;
			}
		}
		throw new UnknownVariable(variable);
	}

}
