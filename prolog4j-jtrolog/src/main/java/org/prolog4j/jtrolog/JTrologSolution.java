package org.prolog4j.jtrolog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.prolog4j.SolutionIterator;

import jTrolog.errors.NoMorePrologSolutions;
import jTrolog.engine.Prolog;
import jTrolog.engine.Solution;
import jTrolog.terms.Struct;

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
	 * Creates a <tt>JTrologSolution</tt> object for traversing through the solutions
	 * for a Prolog query.
	 * 
	 * @param prolog jTrolog engine
	 * @param sGoal a Prolog goal
	 * @param defaultVarName the name of the output variable of special interest
	 * @param outputVarNames the name of each output variable
	 */
	JTrologSolution(Prolog prolog, Struct sGoal, String defaultVarName, String[] outputVarNames) {
		this.prolog = prolog;
		this.defaultOutputVariable = defaultVarName;
		this.outputVarNames = outputVarNames;
		try {
			solution = prolog.solve(sGoal);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		success = solution.success();
	}

	@Override
	public boolean isSuccess() {
		return success;
	}

	@Override
	public <A> A get(String variable) {
		if (clazz == null) {
			return Terms.<A> toObject(solution.getBinding(variable));
		}
		return (A) get(variable, clazz);
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
			for (int i = 0; i < collections.length; ++i) {
				collections[i].add(it.get(outputVarNames[i]));
			}
		}
	}

	@Override
	public List<?>[] toLists() {
		List<?>[] lists = new List<?>[outputVarNames.length];
		for (int i = 0; i < lists.length; ++i) {
			lists[i] = new ArrayList();
		}
		collect(lists);
		return lists;
	}

	@Override
	protected void fetch() {
		try {
			hasNext = prolog.hasOpenAlternatives() && (solution = prolog.solveNext()).success();
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

}
