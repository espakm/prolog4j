package org.prolog4j.tuprolog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.prolog4j.Solution;
import org.prolog4j.SolutionIterator;

import alice.tuprolog.NoMoreSolutionException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
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
	private List<Var> vars;

	private SolveInfo solution;
	private final boolean success;

	/**
	 * @param prolog
	 * @param goalTerms
	 * @param actualArgs
	 */
	TuPrologSolution(Prolog prolog, Term goal) {
		this.prolog = prolog;
		solution = prolog.solve(goal);
		success = solution.isSuccess();
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
		List<?>[] lists = new List<?>[vars.size() - 1];
		for (int i = 0; i < lists.length; ++i)
			lists[i] = new ArrayList();
		collect(lists);
		return lists;
	}

	protected void fetchNext() {
		try {
			hasNext = prolog.hasOpenAlternatives()
			&& (solution = prolog.solveNext()).isSuccess();
			// if (!hasNext)
			// prolog.solveHalt();
			fetched = true;
		} catch (NoMoreSolutionException e) {
			// Should not happen.
		}
	}

}
