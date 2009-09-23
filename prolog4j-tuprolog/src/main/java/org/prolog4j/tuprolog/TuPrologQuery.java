package org.prolog4j.tuprolog;

import org.prolog4j.Query;
import org.prolog4j.Solution;

import alice.tuprolog.InvalidTermException;
import alice.tuprolog.Parser;
import alice.tuprolog.Prolog;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Var;

public class TuPrologQuery extends Query {

	private final Prolog prolog;
	private final Term[] goalTerms;
	
	public TuPrologQuery(Prolog prolog, String goal) {
		super(goal);
		this.prolog = prolog;
		this.goalTerms = goalTerms();
	}

	@Override
	public <A> Solution<A> solve(Object... actualArgs) {
		return new TuPrologSolution<A>(prolog, goalTerms, actualArgs);
	}

	private Term[] goalTerms() {
		int inputArgNumber = inputVariables.length;
		Term[] ruleTerms = new Term[inputArgNumber + 1];
		Struct sGoal;
		try {
			Parser parser = new Parser(goal);
			sGoal = (Struct) parser.nextTerm(false);
			int index = 0;
			for (int i = 0; i < inputVariables.length; ++i) {
				Var argVar = new Var(inputVariables[i]);
				Var arg = new Var("J$" + argVar.getOriginalName());
				sGoal = new Struct(",", new Struct("=", argVar, arg), sGoal);
				ruleTerms[++index] = arg;
//				ruleTerms[++index] = argVar;
			}
			sGoal.resolveTerm();
		} catch (InvalidTermException e) {
			throw new RuntimeException(e);
		}
		ruleTerms[0] = sGoal;
		return ruleTerms;
	}

}
