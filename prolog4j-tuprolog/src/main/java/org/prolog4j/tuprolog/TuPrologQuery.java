package org.prolog4j.tuprolog;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.prolog4j.InvalidQuery;
import org.prolog4j.Query;
import org.prolog4j.Solution;
import org.prolog4j.UnknownVariable;

import alice.tuprolog.InvalidTermException;
import alice.tuprolog.Parser;
import alice.tuprolog.Prolog;
import alice.tuprolog.Struct;
import alice.tuprolog.Var;

/**
 * The tuProlog implementation of the Query class.
 */
class TuPrologQuery extends Query {

	/** The tuProlog engine used to process this query. */
	private final Prolog engine;

	/** The tuProlog representation of the goal to be solved. */
	private Struct sGoal;

	/** The tuProlog variables representing the input variables of the goal. */
	private Var[] inputVars;
	
	/** 
	 * This field stores those elements of {@link inputVars} that are still not
	 * bound.
	 */
	private List<Var> unboundVars;
	
	/**
	 * Creates a TuProlog query object.
	 * 
	 * @param engine the tuProlog engine to process the query
	 * @param goal the Prolog goal to be solved
	 */
	TuPrologQuery(Prolog engine, String goal) {
		super(goal);
		this.engine = engine;
		inputVars = new Var[getPlaceholderNames().size()];
		try {
			Parser parser = new Parser(getGoal());
			sGoal = (Struct) parser.nextTerm(false);
			for (int i = 0, index = 0; i < getPlaceholderNames().size(); ++i, ++index) {
				Var argVar = new Var(getPlaceholderNames().get(i));
				Var arg = new Var("J$" + argVar.getOriginalName());
				sGoal = new Struct(",", new Struct("=", argVar, arg), sGoal);
				inputVars[index] = arg;
			}
			sGoal.resolveTerm();
		} catch (InvalidTermException e) {
			throw new InvalidQuery(goal, e);
		}
		unboundVars = new LinkedList<Var>(Arrays.asList(inputVars));
	}

	@Override
	public <A> Solution<A> solve(Object... actualArgs) {
		int i = 0;
		for (Var var: unboundVars) {
			var.free();
			engine.unify(var, Terms.toTerm(actualArgs[i++]));
		}
		return new TuPrologSolution<A>(engine, sGoal);
	}

	@Override
	public Query bind(int argument, Object value) {
		inputVars[argument].free();
		engine.unify(inputVars[argument], Terms.toTerm(value));
		unboundVars.remove(inputVars[argument]);
		return this;
	}

	@Override
	public Query bind(String variable, Object value) {
		Iterator<Var> it = unboundVars.iterator();
		while (it.hasNext()) {
			Var v = it.next();
			if (v.getName().equals(variable)) {
				v.free();
				engine.unify(v, Terms.toTerm(value));
				it.remove();
				return this;
			}
		}
		throw new UnknownVariable(variable);
	}

}
