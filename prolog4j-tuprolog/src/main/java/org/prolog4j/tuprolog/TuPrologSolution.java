package org.prolog4j.tuprolog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.prolog4j.Solution;
import org.prolog4j.SolutionIterator;

import alice.tuprolog.InvalidTermException;
import alice.tuprolog.NoMoreSolutionException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Parser;
import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Var;

/**
 * The <tt>Solution</tt> class is responsible for traversing through the
 * solutions of a query.
 * 
 * @param <S>
 *            the type of the values of the variable that is of special interest
 */
public class TuPrologSolution<S> extends Solution<S> {

	private final Prolog prolog;
	private Term[] goalTerms;
	private List<Var> vars;

	private SolveInfo solution;
	private final boolean success;

	/**
	 * @param prolog
	 * @param goalTerms
	 * @param actualArgs
	 */
	TuPrologSolution(Prolog prolog, Term[] goalTerms, Object... actualArgs) {
		this.prolog = prolog;
		this.goalTerms = goalTerms;
		for (int i = 0; i < actualArgs.length; ++i) {
			Var v = (Var) goalTerms[i + 1];
			v.free();
			prolog.unify(v, Terms.toTerm(actualArgs[i]));
		}
		solution = prolog.solve(goalTerms[0]);
		success = solution.isSuccess();
//		if (!success)
//			return;
		try {
			vars = solution.getBindingVars();
		} catch (NoSolutionException e) {
		}
	}

	@Override
	public boolean isSuccess() {
		return success;
	}

	@Override
	public SolutionIterator<S> iterator() {
		if (success)
			return new SolutionIteratorImpl<S>(varName(vars.size() - 1));
		return (SolutionIterator<S>) NO_SOLUTIONS;
	}

	@Override
	public <A> Iterable<A> on(final String variable) {
		return new Iterable<A>() {
			@Override
			public java.util.Iterator<A> iterator() {
				return new SolutionIteratorImpl<A>(variable);
			}
		};
	}

	@Override
	public <A> Iterable<A> on(final String variable, final Class<A> clazz) {
		return new Iterable<A>() {
			@Override
			public java.util.Iterator<A> iterator() {
				return new SolutionIteratorImpl<A>(variable, clazz);
			}
		};
	}

	/**
	 * @param varIndex
	 * @return
	 */
	private String varName(int varIndex) {
		return vars.get(varIndex).getOriginalName();
	}

	@Override
	public S get() {
		return this.<S> get(varName(vars.size() - 1));
	}

	@Override
	public <A> A get(String variable) {
		try {
			return Terms.<A> toObject(solution
					.getVarValue(variable));
		} catch (NoSolutionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <A> A get(String variable, Class<A> type) {
		try {
			return Terms.toObject(solution.getVarValue(variable),
					type);
		} catch (NoSolutionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void collect(Collection... collections) {
		SolutionIterator<S> it = iterator();
		while (it.hasNext()) {
			it.next();
			for (int i = 0; i < collections.length; ++i)
				collections[i].add(it.get(varName(i)));
		}
	}

	@Override
	public List<?>[] toLists() {
		List<?>[] lists = new List<?>[goalTerms.length - 1];
		for (int i = 0; i < lists.length; ++i)
			lists[i] = new ArrayList();
		collect(lists);
		return lists;
	}

	/**
	 * Internal implementation for the {@link org.prolog4j.SolutionIterator
	 * SolutionIterator} interface.
	 * 
	 * @param <E>
	 *            the type of the values of the variable that is of special
	 *            interest
	 */
	private class SolutionIteratorImpl<E> implements SolutionIterator<E> {

		private String variable;
		private boolean fetched = true;
		private boolean hasNext = success;
		private Class<E> clazz;

		/**
		 * Creates a new SolutionIteratorImpl object.
		 * 
		 * @param variable
		 *            the name of the variable that is of special interest
		 */
		@SuppressWarnings("unchecked")
		SolutionIteratorImpl(String variable) {
			this.variable = variable;
//			clazz = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		}

		SolutionIteratorImpl(String variable, Class<E> clazz) {
			this.variable = variable;
			this.clazz = clazz;
		}

		/**
		 * Fetches the next solution.
		 */
		private void fetch() {
			try {
				hasNext = solution.hasOpenAlternatives()
						&& (solution = prolog.solveNext()).isSuccess();
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
			return get(variable);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public E get(String variable) {
			if (clazz == null)
				return TuPrologSolution.this.get(variable);
			return TuPrologSolution.this.get(variable, clazz);
		}

		@Override
		public <A> A get(String variable, Class<A> type) {
			return TuPrologSolution.this.get(variable, type);
		}

	}

	/**
	 * @param goal
	 * @param inputArgs
	 * @return
	 */
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
					// Var arg = new Var(argVar.getOriginalName());
					sGoal = new Struct(",", new Struct("=", argVar, arg), sGoal);
					ruleTerms[++index] = arg;
				}
		} catch (InvalidTermException e) {
			throw new RuntimeException(e);
		}
		ruleTerms[0] = sGoal;
		return ruleTerms;
	}

	/**
	 * @param goal
	 * @param variables
	 * @return
	 */
	static Term[] goalTerms(String goal, String... variables) {
		int inputArgNumber = variables.length;
		Term[] ruleTerms = new Term[inputArgNumber + 1];
		Struct sGoal;
		try {
			Parser parser = new Parser(goal);
			sGoal = (Struct) parser.nextTerm(false);
			int index = 0;
			for (int i = 0; i < variables.length; ++i) {
				Var argVar = new Var(variables[i]);
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

	static Term[] goalTerms(String goal, Set<String> variables) {
		int inputArgNumber = variables.size();
		Term[] ruleTerms = new Term[inputArgNumber + 1];
		Struct sGoal;
		try {
			Parser parser = new Parser(goal);
			sGoal = (Struct) parser.nextTerm(false);
			int index = 0;
			for (String variable : variables) {
				Var argVar = new Var(variable);
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
