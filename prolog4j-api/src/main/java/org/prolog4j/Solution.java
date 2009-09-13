package org.prolog4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public abstract class Solution<S> implements Iterable<S> {

	/**
	 * Returns whether there exists a solution or not. Does not depend on the
	 * state of the traversal, only one solution should exist.
	 * 
	 * @return <tt>true</tt> if the goal can be satisfied, otherwise
	 *         <tt>false</tt>
	 */
	public abstract boolean isSuccess();
	
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
	public abstract <A> Iterable<A> on(final String variable);
	
	public abstract <A> Iterable<A> on(final String variable, final Class<A> clazz);
	
	/**
	 * Returns the value of the variable last occurring in the goal bound to by
	 * the first solution of the goal.
	 * 
	 * @return the value of the last variable occurring in the goal
	 */
	public abstract S get();

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
	public abstract <A> A get(String variable);
	
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
	public abstract <A> A get(String variable, Class<A> type);
	
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
	public abstract void collect(Collection... collections);
	
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
	public abstract List<?>[] toLists();

	@SuppressWarnings("unchecked")
	protected static final SolutionIterator NO_SOLUTIONS = new SolutionIterator() {
		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public Object next() {
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object get(String variable) {
			throw new NoSuchElementException();
		}

		@Override
		public Object get(String variable, Class type) {
			throw new NoSuchElementException();
		}

	};
	
}
