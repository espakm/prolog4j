package org.prolog4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Logger;

import alice.tuprolog.InvalidTermException;
import alice.tuprolog.NoMoreSolutionException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Parser;
import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Var;

public class Solution<S> implements Iterable<S> {
    private static final Logger logger = Logger.getLogger("japlo.lang.Solution");

    private final Prolog prolog;
    private Term[] goalTerms;

    private SolveInfo solution;
    private boolean success;

    Solution(Prolog prolog, Term[] goalTerms, Object... actualArgs) {
        logger.entering("japlo.lang.Solution", "Solution");
        this.prolog = prolog;
        this.goalTerms = goalTerms;
        for (int i = 0; i < actualArgs.length; ++i) {
            Var v = (Var) goalTerms[i + 1];
            v.free();
            prolog.unify(v, Terms.toTerm(actualArgs[i]));
        }
        solution = prolog.solve(goalTerms[0]);
        success = solution.isSuccess();
    }

    public Solution(Prolog prolog, String goal) {
        this.prolog = prolog;
        System.out.println("Solution.Solution()");
        Parser parser = new Parser(goal);
        Term tGoal;
        try {
            tGoal = parser.nextTerm(false);
            solution = prolog.solve(tGoal);
            List<Term> terms = solution.getBindingVars();
            System.out.println("Solution2.Solution2() " + terms.size());
            this.goalTerms = new Term[terms.size() + 1];
            this.goalTerms[0] = tGoal;
            for (int i = 0; i < terms.size(); ++i)
                this.goalTerms[i + 1] = terms.get(i);
            success = solution.isSuccess();
        } catch (InvalidTermException e) {
            e.printStackTrace();
        } catch (NoSolutionException e) {
            e.printStackTrace();
        }
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public SolutionIterator<S> iterator() {
        return new SolutionIteratorImpl<S>(argName(goalTerms.length - 2));
    }

    public <A> Iterable<A> on(final String argName) {
        return new Iterable<A>() {
            public java.util.Iterator<A> iterator() {
                return new SolutionIteratorImpl<A>(capitalize(argName));
            }
        };
    }

    private String argName(int argIndex) {
        // return ((Var) goalTerms[argIndex +
        // 1]).getOriginalName().substring(2);
        System.out.println(argIndex);
        System.out.println(goalTerms[argIndex + 1]);
        return ((Var) goalTerms[argIndex + 1]).getOriginalName();
    }

    private static String capitalize(String string) {
        char firstLetter = string.charAt(0);
        if (Character.isUpperCase(firstLetter))
            return string;
        StringBuilder sb = new StringBuilder(string);
        sb.setCharAt(0, Character.toUpperCase(firstLetter));
        return sb.toString();
    }

    public S get() {
        return this.<S> get(argName(goalTerms.length - 2));
    }

    public <A> A get(String argName) {
        try {
            return Terms.<A> toObject(solution.getVarValue(capitalize(argName)));
        } catch (NoSolutionException e) {
            throw new RuntimeException(e);
        }
    }

    public <A> A get(String argName, Class<A> type) {
        try {
            return Terms.toObject(solution.getVarValue(capitalize(argName)), type);
        } catch (NoSolutionException e) {
            throw new RuntimeException(e);
        }
    }

    public <C extends Collection<? super S>> C collect(C collection) {
        for (S s : this)
            collection.add(s);
        return collection;
    }

    public void collect(Collection... collections) {
        SolutionIterator<S> it = iterator();
        while (it.hasNext()) {
            it.next();
            for (int i = 0; i < collections.length; ++i)
                collections[i].add(it.get(argName(i)));
        }
    }

    public Set<S> toSet() {
        return collect(new HashSet<S>());
    }

    public List<S> toList() {
        return collect(new ArrayList<S>());
    }

    public List<?>[] toLists() {
        List<?>[] lists = new List<?>[goalTerms.length - 1];
        for (int i = 0; i < lists.length; ++i)
            lists[i] = new ArrayList();
        collect(lists);
        return lists;
    }

    private class SolutionIteratorImpl<E> implements SolutionIterator<E> {

        private String argName;
        private boolean fetched = true;
        private boolean hasNext = success;

        SolutionIteratorImpl(String argName) {
            logger.entering("japlo.lang.SolutionIteratorImpl", "SolutionIteratorImpl", argName);
            this.argName = capitalize(argName);
        }

        private void fetch() {
            try {
                hasNext = solution.hasOpenAlternatives() && (solution = prolog.solveNext()).isSuccess();
                // if (!hasNext)
                // prolog.solveHalt();
                fetched = true;
            } catch (NoMoreSolutionException e) {
                // Should not happen.
            }
        }

        @Override
        public boolean hasNext() {
            if (!fetched)
                fetch();
            return hasNext;
        }

        @Override
        public E next() {
            if (!hasNext())
                throw new NoSuchElementException();
            fetched = false;
            // return get(argName);
            return this.<E> get(argName);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <A> A get(String argName) {
            // return Solution.this.get(argName);
            return (A) Solution.this.get(argName);
        }

        @Override
        public <A> A get(String argName, Class<A> type) {
            return Solution.this.get(argName, type);
        }

    }

    static Term[] goalTerms(String goal, int inputArgs) {
        int inputArgNumber = 0;
        for (int args = inputArgs; args != 0; args >>= 1)
            if (args % 2 == 1)
                ++inputArgNumber;
        Term[] ruleTerms = new Term[inputArgNumber + 1];
        Struct sGoal, originalRule;
        try {
            Parser parser = new Parser(goal);
            originalRule = sGoal = (Struct) parser.nextTerm(false);
            int index = 0;
            for (int i = 0; inputArgs != 0; ++i, inputArgs >>= 1)
                if (inputArgs % 2 == 1) {
                    Var argVar = (Var) originalRule.getArg(index);
                    Var arg = new Var("J$" + argVar.getOriginalName());
                    sGoal = new Struct(",", new Struct("=", argVar, arg), sGoal);
                    ruleTerms[++index] = arg;
                }
        } catch (InvalidTermException e) {
            throw new RuntimeException(e);
        }
        ruleTerms[0] = sGoal;
        return ruleTerms;
    }

    static Term[] goalTerms(String goal, String... argNames) {
        int inputArgNumber = argNames.length;
        Term[] ruleTerms = new Term[inputArgNumber + 1];
        Struct sGoal;
        try {
            Parser parser = new Parser(goal);
            sGoal = (Struct) parser.nextTerm(false);
            int index = 0;
            for (int i = 0; i < argNames.length; ++i) {
                Var argVar = new Var(argNames[i]);
                Var arg = new Var("J$" + argVar.getOriginalName());
                sGoal = new Struct(",", new Struct("=", argVar, arg), sGoal);
                ruleTerms[++index] = arg;
            }
            sGoal.resolveTerm();
        } catch (InvalidTermException e) {
            throw new RuntimeException(e);
        }
        ruleTerms[0] = sGoal;
        return ruleTerms;
    }
}
