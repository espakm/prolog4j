package org.prolog4j.helpers;

import java.io.ObjectStreamException;
import java.io.Serializable;

import org.prolog4j.Prover;
import org.prolog4j.ProverFactory;
import org.prolog4j.Solution;

/**
 * Serves as base class for named prover implementation. More significantly,
 * this class establishes deserialization behavior. See @see #readResolve.
 * 
 * @author Ceki Gulcu
 */
public abstract class NamedProverBase implements Prover, Serializable {

	private static final long serialVersionUID = 1L;

	protected String name;

	protected NamedProverBase(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public <A> Solution<A> solve(String goal, Object... actualArgs) {
		String[] variables = new String[actualArgs.length];
		StringBuilder goalB = new StringBuilder(goal);
		for (int i = 0; i < variables.length; ++i) {
			int end = goalB.indexOf("{}");
			if (end == -1)
				throw new RuntimeException("Invalid format string.");
			int start;
			for (start = end - 1; start >= 0 && Character.isJavaIdentifierPart(goalB.charAt(start)); --start);
			if (start < 0)
				continue;
			variables[i] = goalB.substring(start + 1, end);
			goalB.delete(end, end + 2);
		}
		return solve(goalB.toString(), variables, actualArgs);
	}

	/**
	 * Solves a Prolog goal and returns an object using which the individual
	 * solutions can be iterated over. The goal has to contain variables with
	 * the names specified by the second argument. The actual arguments will be
	 * bound to these variables before solving the goal.
	 * 
	 * @param <A>
	 *            the type of an element of the solutions
	 * @param goal
	 *            the Prolog goal
	 * @param inputArgs
	 *            the names of the input variables of the goal
	 * @param actualArgs
	 *            the actual arguments of the goal
	 * @return an object for traversing the solutions
	 */
	protected abstract <A> Solution<A> solve(String goal, String[] inputArgs, Object[] actualArgs);

	/**
	 * Replace this instance with a homonymous (same name) prover returned by
	 * ProverFactory. Note that this method is only called during
	 * deserialization.
	 * 
	 * <p>
	 * This approach will work well if the desired IProverFactory is the one
	 * references by ProverFactory. However, if the user manages its prover
	 * hierarchy through a different (non-static) mechanism, e.g. dependency
	 * injection, then this approach would be mostly counterproductive.
	 * 
	 * @return prover with same name as returned by ProverFactory
	 * @throws ObjectStreamException
	 */
	protected Object readResolve() throws ObjectStreamException {
		// using getName() instead of this.name works even for
		// NOPLogger
		return ProverFactory.getProver(getName());
	}

	public String toString() {
		return this.getClass().getName() + "(" + getName() + ")";
	}

}
