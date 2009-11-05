package org.prolog4j.tuprolog;

import java.util.HashMap;
import java.util.Map;

import org.prolog4j.Prover;
import org.prolog4j.IProverFactory;

/**
 * An implementation of {@link IProverFactory} which always returns
 * {@link TuPrologLogger} instances.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public final class TuPrologProverFactory implements IProverFactory {

	/**
	 * The unique instance of this class.
	 */
	private static final TuPrologProverFactory INSTANCE = new TuPrologProverFactory();
	
	/**
	 * Returns the single instance of this class.
	 * 
	 * @return the only one TuPrologProverFactory instance
	 */
	public static TuPrologProverFactory getInstance() {
		return INSTANCE;
	}

	/**
	 * Stores the provers assigned to their names.
	 */
	private final Map<String, Prover> proverMap = new HashMap<String, Prover>();

	/**
	 * Private constructor to prevent instantiation.
	 */
	private TuPrologProverFactory() {
	}
	
	@Override
	public Prover getProver(String name) {
		Prover prover = null;
		// protect against concurrent access of the proverMap
		synchronized (this) {
			prover = proverMap.get(name);
			if (prover == null) {
				prover = new TuPrologProver(name);
				proverMap.put(name, prover);
			}
		}
		return prover;
	}
}
