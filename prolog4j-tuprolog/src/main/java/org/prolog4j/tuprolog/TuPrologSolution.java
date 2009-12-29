package org.prolog4j.tuprolog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import org.prolog4j.ConversionPolicy;
import org.prolog4j.Prover;
import org.prolog4j.ProverFactory;
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

	/** The conversion policy of the tuProlog prover that is used for solving this query. */
	private final ConversionPolicy cp;

//	/** The conversion policy of the tuProlog prover that is used for solving this query. */
//	private static final ConversionPolicy cp = ProverFactory.getConversionPolicy();

//	private static final Terms terms = Terms.getInstance();

	/** The tuProlog engine that is used for solving the query. */
	private final Prolog engine;
	
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
		this.cp = prover.getConversionPolicy();
		this.engine = prover.getEngine();
		solution = engine.solve(goal);
		success = solution.isSuccess();
		if (!success) {
			return;
		}
		try {
			vars = solution.getBindingVars();
		} catch (NoSolutionException e) {
			throw new IllegalStateException(e);
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
				Term term = solution.getVarValue(variable);
				if (term == null) {
					throw new UnknownVariable(variable);
				}
				return (A) cp.convertTerm(term);
			}
			return (A) get(variable, clazz);
		} catch (NoSolutionException e) {
			throw new NoSuchElementException();
		}
	}

	@Override
	public <A> A get(String variable, Class<A> type) {
		try {
			Term term = solution.getVarValue(variable);
			if (term == null) {
				throw new UnknownVariable(variable);
			}
			return (A) cp.convertTerm(term, type);
		} catch (NoSolutionException e) {
			throw new NoSuchElementException();
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
	protected boolean fetch() {
		try {
			boolean hasNext = engine.hasOpenAlternatives()
					&& (solution = engine.solveNext()).isSuccess();
//			if (!hasNext)
//				engine.solveHalt();
			return hasNext;
		} catch (NoMoreSolutionException e) {
			// Should not happen.
			throw new IllegalStateException(e);
		}
	}

}
