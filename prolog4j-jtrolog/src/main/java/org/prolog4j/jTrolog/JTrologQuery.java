package org.prolog4j.jTrolog;

import jTrolog.engine.Prolog;
import jTrolog.parser.Parser;
import jTrolog.terms.Struct;
import jTrolog.terms.Term;
import jTrolog.terms.Var;

import org.prolog4j.Query;
import org.prolog4j.Solution;

public class JTrologQuery extends Query {

	private Prolog engine;
	private String[] outputVarNames;
	private Struct sGoal;
	private Var[] vars;
	
	protected JTrologQuery(Prolog engine, String goal) {
		super(goal);
		this.engine = engine;
		Parser parser = new Parser(this.goal);
		sGoal = (Struct) parser.nextTerm(false);
		vars = sGoal.getVarList();
		outputVarNames = new String[vars.length];
		for (int i = 0; i < vars.length; ++i)
			outputVarNames[i] = vars[i].toString();
	}

	@Override
	public <A> Solution<A> solve(Object... actualArgs) {
		for (int i = 0; i < inputVariables.length; ++i)
			for (Var var: vars)
				if (var.toString().equals(inputVariables[i])) {
					sGoal = new Struct(",", new Term[]{new Struct("=", new Term[]{var, Terms.toTerm(actualArgs[i])}), sGoal});
					break;
				}
		return new JTrologSolution<A>(engine, sGoal, outputVarNames);
	}

}
