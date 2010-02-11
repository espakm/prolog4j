/* 
 * Copyright (c) 2010 Miklos Espak
 * All rights reserved.
 * 
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 * 
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 * 
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.prolog4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Represents the solutions of a query.
 *
 * @param <S> The type of the values of the variable that is of special
 *            interest
 */
public abstract class Solution<S> implements Iterable<S> {

	/**
	 * The name of the variable that is of special interest.
	 */
	private String defaultOutputVariable;

	/**
	 * The values that get bound to the default output variable will be
	 * converted to this type.
	 */
	protected Class<S> clazz;
	
	/**
	 * Returns whether there exists a solution or not. Does not depend on the
	 * state of the traversal, only one solution should exist.
	 * 
	 * @return <tt>true</tt> if the goal can be satisfied, otherwise
	 *         <tt>false</tt>
	 */
	public abstract boolean isSuccess();

	/**
	 * Changes the state of this solution object so that it supports
	 * traversing the solutions according to another variable.
	 * 
	 * @param <A>
	 *            the type of the values of the variable that is of special
	 *            interest
	 * @param variable
	 *            the name of the variable
	 * @return the same object
	 */
	@SuppressWarnings("unchecked")
	public <A> Solution<A> on(final String variable) {
		defaultOutputVariable = variable;
		clazz = null;
		return (Solution<A>) this;
	}
	
	/**
	 * Changes the state of this solution object so that it supports
	 * traversing the solutions according to another variable. The returned 
	 * object will convert the traversed elements to the specified type.
	 * 
	 * @param <A>
	 *            the type of the values of the variable that is of special
	 *            interest
	 * @param variable
	 *            the name of the variable
	 * @param clazz
	 *            the type to which the elements have to be converted
	 * @return the same object
	 */
	@SuppressWarnings("unchecked")
	public <A> Solution<A> on(final String variable, final Class<A> clazz) {
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
	 * Returns the value of the variable last occurring in the goal bound to by
	 * the first solution of the goal. The required type of the value can be 
	 * specified explicitly. This is useful when not the default type is desired.
	 * 
	 * @param <A>
	 *            the type of the value
	 * @param type
	 *            the type which the value should be converted to
	 * @return the value bound to the variable
	 */
	public <A> A get(Class<A> type) {
		return this.<A> get(defaultOutputVariable, type);
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
	 * Fetches the next solution if there is one.
	 * 
	 * @return <code>true</code> if there was another solution, otherwise
	 * 		<code>false</code>
	 */
	protected abstract boolean fetch();
	
	@Override
	public SolutionIterator<S> iterator() {
		return new SolutionIterator<S>() {
			/** Stores whether the next solution has already been fetched or not. */
			private boolean fetched = true;
			
			/** Stores whether there is another solution or not. */
			private boolean hasNext = isSuccess();
			
			@Override
			public boolean hasNext() {
				if (!fetched) {
					hasNext = fetch();
					fetched = true;
				}
				return hasNext;
			}

			@Override
			public S next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
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
				if (clazz == null) {
					return Solution.this.<S>get(variable);
				}
				return Solution.this.get(variable, clazz);
			}

			@Override
			public <A> A get(String variable, Class<A> type) {
				return Solution.this.get(variable, type);
			}

		};
	}

	/**
	 * Collects the values of the primary variable into the given collection.
	 * Returns its parameter.
	 * 
	 * @param <C>
	 *            the type of the collection
	 * @param collection
	 *            the collection which will store the solutions
	 * @return <tt>collection</tt>
	 */
	public final <C extends Collection<? super S>> C collect(final C collection) {
		for (S s : this) {
			collection.add(s);
		}
		return collection;
	}

	/**
	 * Collects the values of the specified variables into the given collections.
	 * The size of the arrays is expected to be the same.
	 * 
	 * @param variables the name of the output variables
	 * @param collections the collections where to store the solutions
	 */
	@SuppressWarnings("unchecked")
	public final void collect(String[] variables, Collection[] collections) {
		SolutionIterator it = iterator();
		while (it.hasNext()) {
			it.next();
			for (int i = 0; i < variables.length; ++i) {
				collections[i].add(it.get(variables[i]));
			}
		}
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

}
