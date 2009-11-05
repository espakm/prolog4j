package org.prolog4j.jtrolog;

import java.util.LinkedList;

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
	private LinkedList<Var> inputVars;
	private String defaultVarName;
	
	protected JTrologQuery(Prolog engine, String goal) {
		super(goal);
		this.engine = engine;
		Parser parser = new Parser(getGoal());
		sGoal = (Struct) parser.nextTerm(false);
		vars = sGoal.getVarList();
		inputVars = new LinkedList<Var>();
		for (Var var: vars) {
			if (inputVarNames.contains(var.toString())) {
				inputVars.add(var);
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
		for (Var var: inputVars) {
			sGoal = new Struct(
						",", 
						new Term[]{
								new Struct("=", new Term[]{var, Terms.toTerm(actualArgs[i++])}),
								sGoal});
		}
		return new JTrologSolution<A>(engine, sGoal, defaultVarName, outputVarNames);
	}

	@Override
	public Query bind(int argument, Object value) {
		Var var = vars[argument];
		sGoal = new Struct(
					",", 
					new Term[]{new Struct("=", new Term[]{var, Terms.toTerm(value)}), sGoal});
		inputVars.remove(var);
		return this;
	}

	@Override
	public Query bind(String variable, Object value) {
		for (Var inputVar: inputVars) {
			if (inputVar.toString().equals(variable)) {
				sGoal = new Struct(
						",", 
						new Term[]{
								new Struct("=", 
										new Term[]{inputVar, Terms.toTerm(value)}), sGoal});
				inputVars.remove(inputVar);
				return this;
			}
		}
		throw new RuntimeException("No such variable.");
	}

}
