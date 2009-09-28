package org.prolog4j;

import java.io.ObjectStreamException;
import java.io.Serializable;


/**
 * Serves as base class for named prover implementation. More significantly,
 * this class establishes deserialization behavior. See @see #readResolve.
 * 
 * @author Ceki Gulcu
 * @author Miklós Espák
 */
public abstract class AbstractProver implements Prover, Serializable {

	private static final long serialVersionUID = 1L;
	
	protected String name;

	protected AbstractProver(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public <A> Solution<A> solve(String goal, Object... actualArgs) {
		return query(goal).solve(actualArgs);
	}
	
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
		// TODO The knowledge base is not restored this way.
		return ProverFactory.getProver(getName());
	}

	public String toString() {
		return getClass().getName() + "(" + getName() + ")";
	}

}
