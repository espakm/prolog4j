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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jTrolog.errors.InvalidTermException;
import jTrolog.parser.Parser;
import jTrolog.terms.Struct;
import jTrolog.terms.Term;
import jTrolog.terms.Var;

import org.prolog4j.ConversionPolicy;
import org.prolog4j.InvalidQueryException;
import org.prolog4j.Query;
import org.prolog4j.Solution;
import org.prolog4j.UnknownVariableException;

/**
 * The jTrolog implementation of the Query class.
 */
public class JTrologQuery extends Query {

	/** The jTrolog prover used to process this query. */
	private JTrologProver prover;

	/** The conversion policy of the prover that is used for solving this query. */
	private final ConversionPolicy cp;

	/** The names of the output variables of the goal. */
	private String[] outputVarNames;

	/** The jTrolog representation of the goal to be solved. */
	private Struct sGoal;

	/** The jTrolog variables representing the input variables of the goal. */
	private Var[] vars;
	
	/** 
	 * This field stores those elements of {@link inputVars} that are still not
	 * bound.
	 */
	private List<Var> unboundVars;
	
	/** The name of the variable that is of special interest when solving the goal. */
	private String defaultVarName;
	
	/**
	 * Creates a JTrolog query object.
	 * 
	 * @param prover the jTrolog prover to process the query
	 * @param goal the Prolog goal to be solved
	 */
	protected JTrologQuery(JTrologProver prover, String goal) {
		super(goal);
		this.prover = prover;
		this.cp = prover.getConversionPolicy();
		Parser parser = new Parser(getGoal());
		try {
			sGoal = (Struct) parser.nextTerm(true);
		} catch (InvalidTermException e) {
			throw new InvalidQueryException(getGoal());
		}
		vars = sGoal.getVarList();
		unboundVars = new LinkedList<Var>();
		for (Var var: vars) {
			if (getPlaceholderNames().contains(var.toString())) {
				unboundVars.add(var);
			}
		}
		outputVarNames = new String[vars.length];
		int i;
		for (i = 0; i < vars.length; ++i) {
			outputVarNames[i] = vars[i].toString();
		}
		while (--i >= 0) {
			if (!outputVarNames[i].startsWith("P4J_")) {
				defaultVarName = outputVarNames[i];
				break;
			}
		}
	}

	@Override
	public <A> Solution<A> solve(Object... actualArgs) {
		int i = 0;
		Struct g = sGoal;
		for (Var var: unboundVars) {
			g = new Struct(
						",", 
						new Term[]{
								new Struct("=", new Term[]{var, 
										(Term) cp.convertObject(actualArgs[i++])}),
								g});
		}
		return new JTrologSolution<A>(prover, g, defaultVarName, outputVarNames);
	}

	@Override
	public Query bind(int argument, Object value) {
		Var var = vars[argument];
		sGoal = new Struct(
					",", 
					new Term[]{new Struct("=", new Term[]{var, 
							(Term) cp.convertObject(value)}), sGoal});
		unboundVars.remove(var);
		return this;
	}

	@Override
	public Query bind(String variable, Object value) {
		Iterator<Var> it = unboundVars.iterator();
		while (it.hasNext()) {
			Var v = it.next();
			if (v.toString().equals(variable)) {
				sGoal = new Struct(
						",", 
						new Term[]{
								new Struct("=", 
										new Term[]{v, 
										(Term) cp.convertObject(value)}), sGoal});
				it.remove();
				return this;
			}
		}
		throw new UnknownVariableException(variable);
	}

}
