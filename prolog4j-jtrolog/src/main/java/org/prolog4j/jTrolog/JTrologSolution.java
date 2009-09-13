package org.prolog4j.jTrolog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import org.prolog4j.SolutionIterator;

import jTrolog.errors.InvalidTermException;
import jTrolog.errors.NoMorePrologSolutions;
import jTrolog.parser.Parser;
import jTrolog.engine.Prolog;
import jTrolog.engine.Solution;
import jTrolog.terms.Struct;
import jTrolog.terms.Term;
import jTrolog.terms.Var;

/**
 * The <tt>Solution</tt> class is responsible for traversing through the
 * solutions of a query.
 * 
 * @param <S>
 *            the type of the values of the variable that is of special interest
 */
public class JTrologSolution<S> extends org.prolog4j.Solution<S> {

	private final Prolog prolog;
	private String[] outputVarNames;

	private Solution solution;
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
	JTrologSolution(Prolog prolog, String goal) {
		this.prolog = prolog;
		try {
			Parser parser = new Parser(goal);
			Struct sGoal = (Struct) parser.nextTerm(false);
			Var[] vars = sGoal.getVarList();
			outputVarNames = new String[vars.length];
			for (int i = 0; i < vars.length; ++i)
				outputVarNames[i] = vars[i].toString();
			solution = prolog.solve(sGoal);
		} catch (InvalidTermException e) {
			throw new RuntimeException(e);
		} catch (ClassCastException e) {
			throw new RuntimeException(e);
		} catch (NoMorePrologSolutions e) {
			success = false;
			return;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		success = true;
	}

	JTrologSolution(Prolog engine, String goal, String[] varNames, Object[] actualArgs) {
		this.prolog = engine;
		try {
			Parser parser = new Parser(goal);
			Struct sGoal = (Struct) parser.nextTerm(false);
			Var[] vars = sGoal.getVarList();
			outputVarNames = new String[vars.length];
			for (int i = 0; i < vars.length; ++i)
				outputVarNames[i] = vars[i].toString();
			for (int i = 0; i < varNames.length; ++i)
				for (Var var: vars)
					if (var.toString().equals(varNames[i])) {
						sGoal = new Struct(",", new Term[]{new Struct("=", new Term[]{var, Terms.toTerm(actualArgs[i])}), sGoal});
						break;
					}
			solution = prolog.solve(sGoal);
			success = solution.success();
		} catch (InvalidTermException e) {
			throw new RuntimeException(e);
		} catch (ClassCastException e) {
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isSuccess() {
		return success;
	}

	@Override
	public SolutionIterator<S> iterator() {
//		if (success)
			return new SolutionIteratorImpl<S>(outputVarNames[outputVarNames.length - 1]);
//		return (SolutionIterator<S>) NO_SOLUTIONS;
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

	@Override
	public S get() {
		return this.<S> get(outputVarNames[outputVarNames.length - 1]);
	}

	@Override
	public <A> A get(String variable) {
		return Terms.<A> toObject(solution
				.getBinding(variable));
	}

	@Override
	public <A> A get(String variable, Class<A> type) {
		return Terms.toObject(solution.getBinding(variable), type);
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
			this.variable = variable;
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
				hasNext = prolog.hasOpenAlternatives() && 
					(solution = prolog.solveNext()).success();
				// if (!hasNext)
				// prolog.solveHalt();
				fetched = true;
			} catch (NoMorePrologSolutions e) {
				// Should not happen.
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
				return JTrologSolution.this.get(variable);
			return JTrologSolution.this.get(variable, clazz);
		}

		@Override
		public <A> A get(String variable, Class<A> type) {
			return JTrologSolution.this.get(variable, type);
		}

	}

}
