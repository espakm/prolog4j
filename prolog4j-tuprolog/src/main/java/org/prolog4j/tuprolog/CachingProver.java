package org.prolog4j.tuprolog;

import java.util.HashMap;
import java.util.Map;

import org.prolog4j.Solution;


import alice.tuprolog.Term;

public class CachingProver extends TuPrologProver {

	private static final long serialVersionUID = 1L;

	public CachingProver(String name) {
		super(name);
	}

	private Map<String, Term[]> goalTermsCache = new HashMap<String, Term[]>();

    @Override
    protected <A> Solution<A> solve(String goal, String[] inputArgs, Object[] actualArgs) {
        Term[] goalTerms = goalTermsCache.get(goal);
        if (goalTerms == null)
            goalTermsCache.put(goal, goalTerms = TuPrologSolution.goalTerms(goal, inputArgs));
        return new TuPrologSolution<A>(engine, goalTerms, actualArgs);
    }

}
