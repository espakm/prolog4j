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
import org.prolog4j.InvalidQuery;
import org.prolog4j.Query;
import org.prolog4j.Solution;
import org.prolog4j.UnknownVariable;

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
			throw new InvalidQuery(getGoal());
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
		for (Var var: unboundVars) {
			sGoal = new Struct(
						",", 
						new Term[]{
								new Struct("=", new Term[]{var, 
										(Term) cp.convertObject(actualArgs[i++])}),
								sGoal});
		}
		return new JTrologSolution<A>(prover, sGoal, defaultVarName, outputVarNames);
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
		throw new UnknownVariable(variable);
	}

}
