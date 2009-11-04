package org.prolog4j.tuprolog;

import java.util.Arrays;
import java.util.LinkedList;

import org.prolog4j.Query;
import org.prolog4j.Solution;

import alice.tuprolog.InvalidTermException;
import alice.tuprolog.Parser;
import alice.tuprolog.Prolog;
import alice.tuprolog.Struct;
import alice.tuprolog.Var;

/**
 * TuProlog implementation of the Query class.
 */
class TuPrologQuery extends Query {

	private final Prolog prolog;
	private Struct sGoal;
	private Var[] inputVars;
	private LinkedList<Var> varss;
	
	TuPrologQuery(Prolog prolog, String goal) {
		super(goal);
		this.prolog = prolog;
		inputVars = new Var[inputVarNames.size()];
		try {
			Parser parser = new Parser(getGoal());
			sGoal = (Struct) parser.nextTerm(false);
			for (int i = 0, index = 0; i < inputVarNames.size(); ++i, ++index) {
				Var argVar = new Var(inputVarNames.get(i));
				Var arg = new Var("J$" + argVar.getOriginalName());
				sGoal = new Struct(",", new Struct("=", argVar, arg), sGoal);
				inputVars[index] = arg;
			}
			sGoal.resolveTerm();
		} catch (InvalidTermException e) {
			throw new RuntimeException(e);
		}
		varss = new LinkedList<Var>();
		varss.addAll(Arrays.asList(inputVars));
	}

	@Override
	public <A> Solution<A> solve(Object... actualArgs) {
		int i = 0;
		for (Var var: varss) {
			var.free();
			prolog.unify(var, Terms.toTerm(actualArgs[i++]));
		}
		return new TuPrologSolution<A>(prolog, sGoal);
	}

	@Override
	public Query bind(int argument, Object value) {
		inputVars[argument].free();
		prolog.unify(inputVars[argument], Terms.toTerm(value));
		varss.remove(inputVars[argument]);
		return this;
	}

	@Override
	public Query bind(String variable, Object value) {
		for (Var inputVar: inputVars) {
			if (inputVar.getName().equals(variable)) {
				inputVar.free();
				prolog.unify(inputVar, Terms.toTerm(value));
				varss.remove(inputVar);
				return this;
			}
		}
		throw new RuntimeException("No such variable.");
	}

}
