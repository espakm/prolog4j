package org.prolog4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

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
public class Solution<S> implements Iterable<S> {

	private final Prolog prolog;
	private Term[] goalTerms;

	private SolveInfo solution;
	private final boolean success;

	/**
	 * @param prolog
	 * @param goalTerms
	 * @param actualArgs
	 */
	Solution(Prolog prolog, Term[] goalTerms, Object... actualArgs) {
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

	/**
	 * @param prolog
	 * @param goalTerms
	 * @param actualArgs
	 */
	Solution(Prolog prolog, Term[] goalTerms, Map<String, Object> actualArgs) {
		this.prolog = prolog;
		this.goalTerms = goalTerms;
		for (String varName: actualArgs.keySet()) {
			for (int i = 1; i < goalTerms.length; ++i) {
				Var v = (Var) goalTerms[i];
				String vName = v.getName();
				if (vName.startsWith("J$") && vName.substring(2).equals(varName)) {
					v.free();
					prolog.unify(v, Terms.toTerm(actualArgs.get(varName)));
					break;
				}
			}
		}
		solution = prolog.solve(goalTerms[0]);
		success = solution.isSuccess();
	}

	/**
	 * Creates a <tt>Solution</tt> object for traversing through the solutions
	 * for a Prolog query.
	 * 
	 * @param prolog
	 *            tuProlog engine
	 * @param goal
	 *            a Prolog goal
	 */
	Solution(Prolog prolog, String goal) {
		this.prolog = prolog;
		Parser parser = new Parser(goal);
		Term tGoal;
		List<Term> terms;
		try {
			tGoal = parser.nextTerm(false);
			solution = prolog.solve(tGoal);
			terms = solution.getBindingVars();
		} catch (InvalidTermException e) {
			throw new RuntimeException(e);
		} catch (NoSolutionException e) {
			success = false;
			return;
		}
		// Otherwise a NoSolutionException would have been thrown:
		success = true;
		this.goalTerms = new Term[terms.size() + 1];
		this.goalTerms[0] = tGoal;
		for (int i = 0; i < terms.size(); ++i)
			this.goalTerms[i + 1] = terms.get(i);
	}

	Solution(Prolog engine, String goal, Object[] actualArgs) {
		this(engine, Solution.goalTerms(goal, (1 << actualArgs.length) - 1),
				actualArgs);
	}

	Solution(Prolog engine, String goal, int inputArgs, Object[] actualArgs) {
		this(engine, goalTerms(goal, inputArgs), actualArgs);
	}

	/**
	 * Returns whether there exists a solution or not. Does not depend on the
	 * state of the traversal, only one solution should exist.
	 * 
	 * @return <tt>true</tt> if the goal can be satisfied, otherwise
	 *         <tt>false</tt>
	 */
	public boolean isSuccess() {
		return success;
	}

	@Override
	public SolutionIterator<S> iterator() {
		return new SolutionIteratorImpl<S>(varName(goalTerms.length - 2));
	}

	/**
	 * Returns another {@link java.util.Iterable Iterable} object that supports
	 * traversing the solutions according to another variable.
	 * 
	 * @param <A>
	 *            the type of the values of the variable that is of special
	 *            interest
	 * @param variable
	 *            the name of the variable
	 * @return an <tt>Iterable<A></tt> object
	 */
	public <A> Iterable<A> on(final String variable) {
		return new Iterable<A>() {
			@Override
			public java.util.Iterator<A> iterator() {
				return new SolutionIteratorImpl<A>(capitalize(variable));
			}
		};
	}

	public <A> Iterable<A> on(final String variable, final Class<A> clazz) {
		return new Iterable<A>() {
			@Override
			public java.util.Iterator<A> iterator() {
				return new SolutionIteratorImpl<A>(capitalize(variable), clazz);
			}
		};
	}

	/**
	 * @param varIndex
	 * @return
	 */
	private String varName(int varIndex) {
		// return ((Var) goalTerms[argIndex +
		// 1]).getOriginalName().substring(2);
		return ((Var) goalTerms[varIndex + 1]).getOriginalName();
	}

	/**
	 * @param string
	 * @return
	 */
	private static String capitalize(String string) {
		char firstLetter = string.charAt(0);
		if (Character.isUpperCase(firstLetter))
			return string;
		StringBuilder sb = new StringBuilder(string);
		sb.setCharAt(0, Character.toUpperCase(firstLetter));
		return sb.toString();
	}

	/**
	 * Returns the value of the variable last occurring in the goal bound to by
	 * the first solution of the goal.
	 * 
	 * @return the value of the last variable occurring in the goal
	 */
	public S get() {
		return this.<S> get(varName(goalTerms.length - 2));
	}

	/**
	 * Returns the value of the given variable bound to by the first solution of
	 * the goal.
	 * 
	 * @param <A>
	 *            the type of the value
	 * @param variable
	 *            the name of the variable
	 * @return the value bound to the variable
	 */
	public <A> A get(String variable) {
		try {
			return Terms.<A> toObject(solution
					.getVarValue(capitalize(variable)));
		} catch (NoSolutionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the value of the given variable bound by the first solution. The
	 * required type of the value can be specified explicitly. This is useful
	 * when not the default type is desired.
	 * 
	 * @param <A>
	 *            the type of the value
	 * @param variable
	 *            the name of the variable
	 * @param type
	 *            the type which the value should be converted to
	 * @return the value bound to the variable
	 */
	public <A> A get(String variable, Class<A> type) {
		try {
			return Terms.toObject(solution.getVarValue(capitalize(variable)),
					type);
		} catch (NoSolutionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Collects the values of the primary variable into the given collection.
	 * Returns its parameter
	 * 
	 * @param <C>
	 *            the type of the collection
	 * @param collection
	 *            the collection which will store the solutions
	 * @return <tt>collection</tt>
	 */
	public <C extends Collection<? super S>> C collect(C collection) {
		for (S s : this)
			collection.add(s);
		return collection;
	}

	/**
	 * Collects the values of the variables into the given collections.
	 * 
	 * @param collections
	 *            the collections which will store the solutions
	 */
	public void collect(Collection... collections) {
		SolutionIterator<S> it = iterator();
		while (it.hasNext()) {
			it.next();
			for (int i = 0; i < collections.length; ++i)
				collections[i].add(it.get(varName(i)));
		}
	}

	/**
	 * Collects the values of the primary variable into a {@link java.util.Set
	 * Set}.
	 * 
	 * @return a set containing the values of the variable of interest
	 */
	public Set<S> toSet() {
		return collect(new HashSet<S>());
	}

	/**
	 * Collects the values of the primary variable into a {@link java.util.List
	 * List}.
	 * 
	 * @return a list containing the values of the variable of interest
	 */
	public List<S> toList() {
		return collect(new ArrayList<S>());
	}

	/**
	 * Collects the values of the variables into a {@link java.util.List List}
	 * array.
	 * 
	 * @return a list array containing the values of the variables
	 */
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
		SolutionIteratorImpl(String variable) {
			this.variable = capitalize(variable);
		}

		SolutionIteratorImpl(String variable, Class<E> clazz) {
			this.variable = capitalize(variable);
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
			// return this.<E> get(variable);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public <A> A get(String variable) {
			return Solution.this.get(variable);
			// return (A) Solution.this.get(variable);
		}

		@Override
		public <A> A get(String variable, Class<A> type) {
			return Solution.this.get(variable, type);
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

	public static Term[] goalTerms(String goal, Set<String> variables) {
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
