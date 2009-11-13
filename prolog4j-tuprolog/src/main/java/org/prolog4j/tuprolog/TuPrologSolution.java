package org.prolog4j.tuprolog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.prolog4j.Prover;
import org.prolog4j.Solution;
import org.prolog4j.SolutionIterator;
import org.prolog4j.UnknownVariable;

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

	/** The tuProlog prover that is used for solving this query. */
	private Prover prover;

//	private static final Terms terms = Terms.getInstance();

	/** The tuProlog engine that is used for solving the query. */
	private final Prolog prolog;
	
	/** The list of variables occurring in the query. */
	private List<Var> vars;

	/** This object provides the bindings for one solution of the query. */
	private SolveInfo solution;
	
	/** True if the query has a solution, otherwise false. */
	private final boolean success;

	/**
	 * Creates an object, using which the solutions of a query can be accessed.
	 * 
	 * @param prover the tuProlog prover
	 * @param goal the goal to be solved
	 */
	TuPrologSolution(TuPrologProver prover, Term goal) {
		this.prover = prover;
		this.prolog = prover.getEngine();
		solution = prolog.solve(goal);
		success = solution.isSuccess();
		if (!success) {
			return;
		}
		try {
			vars = solution.getBindingVars();
		} catch (NoSolutionException e) {
			// It cannot happen.
		}
		if (vars.size() > 0) {
			defaultOutputVariable = varName(vars.size() - 1);
		}
	}

	@Override
	public boolean isSuccess() {
		return success;
	}

	/**
	 * Returns the name of the variable of the specified index.
	 * 
	 * @param varIndex the index of the variable
	 * @return the name of the variable
	 */
	private String varName(int varIndex) {
		return vars.get(varIndex).getOriginalName();
	}

	@Override
	public <A> A get(String variable) {
		try {
			if (clazz == null) {
				return (A) prover.getConversionPolicy().convertTerm(solution.getVarValue(variable));
			}
			return (A) get(variable, clazz);
		} catch (NoSolutionException e) {
			throw new UnknownVariable(variable, e);
		}
	}

	@Override
	public <A> A get(String variable, Class<A> type) {
		try {
			return (A) prover.getConversionPolicy().
				convertTerm(solution.getVarValue(variable), type);
		} catch (NoSolutionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void collect(Collection... collections) {
		SolutionIterator<S> it = iterator();
		while (it.hasNext()) {
			it.next();
			for (int i = 0; i < collections.length; ++i) {
				collections[i].add(it.get(varName(i)));
			}
		}
	}

	@Override
	public List<?>[] toLists() {
		List<?>[] lists = new List<?>[vars.size() - 1];
		for (int i = 0; i < lists.length; ++i) {
			lists[i] = new ArrayList();
		}
		collect(lists);
		return lists;
	}

	@Override
	protected void fetch() {
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
