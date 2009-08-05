package org.prolog4j;

import java.util.HashMap;
import java.util.Map;

import alice.tuprolog.Term;

public class CachingProver extends Prover {

    private Map<String, Term[]> goalTermsCache = new HashMap<String, Term[]>();

    @Override
    public <A> Solution<A> solve(String goal) {
        Term[] goalTerms = goalTermsCache.get(goal);
        if (goalTerms == null)
            goalTermsCache.put(goal, goalTerms = Solution.goalTerms(goal, 0));
        return new Solution<A>(engine, goalTerms);
    }

    @Override
    public <A> Solution<A> solve(String goal, int inputArgs, Object... actualArgs) {
        Term[] goalTerms = goalTermsCache.get(goal);
        if (goalTerms == null)
            goalTermsCache.put(goal, goalTerms = Solution.goalTerms(goal, inputArgs));
        return new Solution<A>(engine, goalTerms, actualArgs);
    }

    @Override
    public <A> Solution<A> solve(String goal, String[] inputArgs, Object[] actualArgs) {
        Term[] goalTerms = goalTermsCache.get(goal);
        if (goalTerms == null)
            goalTermsCache.put(goal, goalTerms = Solution.goalTerms(goal, inputArgs));
        return new Solution<A>(engine, goalTerms, actualArgs);
    }

}
