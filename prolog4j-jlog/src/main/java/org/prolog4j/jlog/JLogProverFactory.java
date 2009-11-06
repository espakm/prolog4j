package org.prolog4j.jlog;

import org.prolog4j.AbstractProverFactory;
import org.prolog4j.Prover;

/**
 * An implementation of {@link IProverFactory} which always returns
 * {@link TuPrologLogger} instances.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public final class JLogProverFactory extends AbstractProverFactory {

	/**
	 * The unique instance of this class.
	 */
	private static final JLogProverFactory INSTANCE = new JLogProverFactory();
	
	/**
	 * Returns the single instance of this class.
	 * 
	 * @return the only one JLogProverFactory instance
	 */
	public static JLogProverFactory getInstance() {
		return INSTANCE;
	}

	/**
	 * Private constructor to prevent instantiation.
	 */
	private JLogProverFactory() {
	}

	@Override
	protected Prover createProver() {
		return new JLogProver();
	}
}
