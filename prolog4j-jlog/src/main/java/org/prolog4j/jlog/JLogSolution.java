package org.prolog4j.jlog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.NoSuchElementException;

import org.prolog4j.SolutionIterator;

import ubc.cs.JLog.Foundation.jPrologAPI;
import ubc.cs.JLog.Terms.jVariable;

/**
 * The <tt>Solution</tt> class is responsible for traversing through the
 * solutions of a query.
 * 
 * @param <S>
 *            the type of the values of the variable that is of special interest
 */
public class JLogSolution<S> extends org.prolog4j.Solution<S> {

	private final jPrologAPI prolog;
	private String[] outputVarNames;

	private Hashtable<jVariable, Object> solution;
	private final boolean success;

	/**
	 * Creates a <tt>Solution</tt> object for traversing through the solutions
	 * for a Prolog query.
	 * 
	 * @param prolog
	 *            tuProlog engine
	 * @param goal
	 *            a Prolog goal
	 */
	JLogSolution(jPrologAPI prolog, String goal) {
		this.prolog = prolog;
		solution = prolog.query(goal);
		success = solution == null;
		if (!success)
			return;
	}

	JLogSolution(jPrologAPI prolog, String goal, String[] varNames, Object[] actualArgs) {
		this.prolog = prolog;
		solution = prolog.query(goal);
		success = solution == null;
		if (!success)
			return;
		outputVarNames = new String[solution.size()];
		int i = 0;
		for (jVariable var: solution.keySet())
			outputVarNames[i] = var.getName();
	}

	@Override
	public boolean isSuccess() {
		return success;
	}

	@Override
	public SolutionIterator<S> iterator() {
		return new SolutionIteratorImpl<S>(outputVarNames[outputVarNames.length - 1]);
	}

	@Override
	public <A> Iterable<A> on(final String variable) {
		return new Iterable<A>() {
			@Override
			public java.util.Iterator<A> iterator() {
				return new SolutionIteratorImpl<A>(capitalize(variable));
			}
		};
	}

	@Override
	public <A> Iterable<A> on(final String variable, final Class<A> clazz) {
		return new Iterable<A>() {
			@Override
			public java.util.Iterator<A> iterator() {
				return new SolutionIteratorImpl<A>(capitalize(variable), clazz);
			}
		};
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

	@Override
	public S get() {
		return this.<S> get(outputVarNames[outputVarNames.length - 1]);
	}

	@Override
	public <A> A get(String variable) {
		return (A) solution.get(capitalize(variable));
	}

	@Override
	public <A> A get(String variable, Class<A> type) {
		throw new UnsupportedOperationException();
//		return solution.get(capitalize(variable)), type);
	}

	@Override
	public void collect(Collection... collections) {
		SolutionIterator<S> it = iterator();
		while (it.hasNext()) {
			it.next();
			for (int i = 0; i < collections.length; ++i)
				collections[i].add(it.get(outputVarNames[i]));
		}
	}

	@Override
	public List<?>[] toLists() {
		List<?>[] lists = new List<?>[outputVarNames.length];
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
			solution = prolog.retry();
			hasNext = solution != null;
			fetched = true;
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
				return JLogSolution.this.get(variable);
			return JLogSolution.this.get(variable, clazz);
		}

		@Override
		public <A> A get(String variable, Class<A> type) {
			return JLogSolution.this.get(variable, type);
		}

	}

}
