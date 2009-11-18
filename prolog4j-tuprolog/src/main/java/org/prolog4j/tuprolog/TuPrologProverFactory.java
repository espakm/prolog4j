package org.prolog4j.tuprolog;

import org.prolog4j.AbstractProverFactory;
import org.prolog4j.ConversionPolicy;
import org.prolog4j.Prover;

/**
 * An implementation of {@link IProverFactory} which always returns
 * {@link TuPrologLogger} instances.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public final class TuPrologProverFactory extends AbstractProverFactory {

	/**
	 * The unique instance of this class.
	 */
	private static final TuPrologProverFactory INSTANCE = new TuPrologProverFactory();
	
	/** The default conversion policy. */
	private final ConversionPolicy conversionPolicy = new TuPrologConversionPolicy();

	/**
	 * Returns the single instance of this class.
	 * 
	 * @return the only one TuPrologProverFactory instance
	 */
	public static TuPrologProverFactory getInstance() {
		return INSTANCE;
	}

	/**
	 * Private constructor to prevent instantiation.
	 */
	private TuPrologProverFactory() {
	}
	
	@Override
	protected Prover createProver() {
		return new TuPrologProver();
	}

	@Override
	public ConversionPolicy getConversionPolicy() {
		return conversionPolicy;
	}
}
