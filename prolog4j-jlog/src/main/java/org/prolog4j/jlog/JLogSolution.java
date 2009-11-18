package org.prolog4j.jlog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.prolog4j.ConversionPolicy;
import org.prolog4j.InvalidQuery;
import org.prolog4j.SolutionIterator;
import org.prolog4j.UnknownVariable;

import ubc.cs.JLog.Foundation.jPrologAPI;
import ubc.cs.JLog.Parser.SyntaxErrorException;

/**
 * The <tt>Solution</tt> class is responsible for traversing through the
 * solutions of a query.
 * 
 * @param <S>
 *            the type of the values of the variable that is of special interest
 */
public class JLogSolution<S> extends org.prolog4j.Solution<S> {

	/** The conversion policy used by the JLog prover that is used for solving the query. */
	private final ConversionPolicy conversionPolicy;
	
	/** The JLog engine that is used for solving the query. */
	private final jPrologAPI prolog;

	/** The name of the output variables of the query. */
	private String[] outputVarNames;

	/** Stores the bindings of one solution of the query. */
	private Hashtable<String, Object> solution;

	/** True if the query has a solution, otherwise false. */
	private final boolean success;

	/**
	 * Constructs a JLogSolution instance.
	 * 
	 * @param prover the JLog prover, using which the solutions have to be found
	 * @param goal the Prolog goal to solve
	 * @param initialBindings the initial bindings of the variables
	 */
	JLogSolution(JLogProver prover, String goal, Hashtable<String, Object> initialBindings) {
		super();
		this.conversionPolicy = prover.getConversionPolicy();
		this.prolog = prover.getEngine();
		try {
			solution = prolog.query(goal, initialBindings);
		} catch (SyntaxErrorException e) {
			throw new InvalidQuery(goal, e);
		}
		success = solution != null;
		if (!success || solution.size() == 0) {
			return;
		}
		outputVarNames = new String[solution.size()];
		int i = 0;
		for (String var: solution.keySet()) {
			outputVarNames[i++] = var;
		}
		defaultOutputVariable = outputVarNames[outputVarNames.length - 1];
	}

	@Override
	public boolean isSuccess() {
		return success;
	}

	@Override
	public <A> A get(String variable) {
		if (clazz == null) {
			Object term = solution.get(variable);
			if (term == null) {
				throw new UnknownVariable(variable);
			}
			return (A) conversionPolicy.convertTerm(term);
		}
		return (A) get(variable, clazz);
	}

	@Override
	public <A> A get(String variable, Class<A> type) {
		Object term = solution.get(variable);
		if (term == null) {
			throw new UnknownVariable(variable);
		}
		return (A) conversionPolicy.convertTerm(term, type);
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
	protected boolean fetch() {
		solution = prolog.retry();
		return solution != null;
	}

}
