package org.prolog4j.jtrolog;

import org.prolog4j.AbstractProverFactory;
import org.prolog4j.Prover;

/**
 * An implementation of {@link IProverFactory} which always returns
 * {@link TuPrologLogger} instances.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public final class JTrologProverFactory extends AbstractProverFactory {

	/**
	 * The unique instance of this class.
	 */
	private static final JTrologProverFactory INSTANCE = new JTrologProverFactory();

	/**
	 * Returns the single instance of this class.
	 * 
	 * @return the only one JTrologProverFactory instance
	 */
	public static JTrologProverFactory getInstance() {
		return INSTANCE;
	}

	/**
	 * Private constructor to prevent instantiation.
	 */
	private JTrologProverFactory() {
		super();
	}

	@Override
	protected Prover createProver() {
		return new JTrologProver();
	}
}
