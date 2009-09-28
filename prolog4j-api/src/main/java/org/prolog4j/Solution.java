package org.prolog4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public abstract class Solution<S> implements Iterable<S> {

	protected String defaultOutputVariable;
	protected Class<S> clazz;
	
	protected boolean fetched;
	protected boolean hasNext;
	
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
	@SuppressWarnings("unchecked")
	public <A> Iterable<A> on(final String variable) {
		defaultOutputVariable = variable;
		clazz = null;
		return (Solution<A>) this;
	}
	
	@SuppressWarnings("unchecked")
	public <A> Iterable<A> on(final String variable, final Class<A> clazz) {
		defaultOutputVariable = variable;
		this.clazz = (Class<S>) clazz;
		return (Solution<A>) this;
	}
	
	/**
	 * Returns the value of the variable last occurring in the goal bound to by
	 * the first solution of the goal.
	 * 
	 * @return the value of the last variable occurring in the goal
	 */
	public S get() {
		return this.<S> get(defaultOutputVariable);
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

	@Override
	public SolutionIterator<S> iterator() {
		fetched = true;
		hasNext = isSuccess();
		return new SolutionIterator<S>() {

			@Override
			public boolean hasNext() {
				if (!fetched)
					fetch();
				return hasNext;
			}

			@Override
			public S next() {
				if (!hasNext())
					throw new NoSuchElementException();
				fetched = false;
				return get(defaultOutputVariable);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			@SuppressWarnings("unchecked")
			@Override
			public S get(String variable) {
				if (clazz == null)
					return Solution.this.get(variable);
				return Solution.this.get(variable, clazz);
			}

			@Override
			public <A> A get(String variable, Class<A> type) {
				return Solution.this.get(variable, type);
			}

		};
	}

	/**
	 * Collects the values of the variables into the given collections.
	 * 
	 * @param collections
	 *            the collections which will store the solutions
	 */
	@SuppressWarnings("unchecked")
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

	/**
	 * Fetches the next solution.
	 */
	protected abstract void fetch();
	
}
