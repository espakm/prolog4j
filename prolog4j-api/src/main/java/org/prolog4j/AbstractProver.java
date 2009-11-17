package org.prolog4j;

import java.io.Serializable;


/**
 * Serves as base class for prover implementation.
 */
public abstract class AbstractProver implements Prover, Serializable {

	/** Class version for serialization. */
	private static final long serialVersionUID = 1L;

	@Override
	public final <A> Solution<A> solve(final String goal, final Object... actualArgs) {
		return query(goal).solve(actualArgs);
	}
	
//	/**
//	 * Replace this instance with a homonymous (same name) prover returned by
//	 * ProverFactory. Note that this method is only called during
//	 * deserialization.
//	 * 
//	 * <p>
//	 * This approach will work well if the desired IProverFactory is the one
//	 * references by ProverFactory. However, if the user manages its prover
//	 * hierarchy through a different (non-static) mechanism, e.g. dependency
//	 * injection, then this approach would be mostly counterproductive.
//	 * 
//	 * @return prover with same name as returned by ProverFactory
//	 */
//	protected Object readResolve() {
//		// TODO The knowledge base is not restored this way.
//		return ProverFactory.getProver(getName());
//	}

}
