package org.prolog4j.helpers;

import java.io.ObjectStreamException;
import java.io.Serializable;

import org.prolog4j.Prover;
import org.prolog4j.ProverFactory;

/**
 * Serves as base class for named prover implementation. More significantly,
 * this class establishes deserialization behavior. See @see #readResolve.
 * 
 * @author Ceki Gulcu
 */
public abstract class NamedProverBase extends Prover implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String name;

	protected NamedProverBase(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
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
		// using getName() instead of this.name works even for
		// NOPLogger
		return ProverFactory.getProver(getName());
	}

	public String toString() {
		return this.getClass().getName() + "(" + getName() + ")";
	}

}
