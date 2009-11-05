package org.prolog4j.jtrolog;

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
public final class JTrologProverFactory implements IProverFactory {

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
	 * Stores the provers assigned to their names.
	 */
	private Map<String, Prover> proverMap = new HashMap<String, Prover>();

	/**
	 * Private constructor to prevent instantiation.
	 */
	private JTrologProverFactory() {
	}
	
	@Override
	public Prover getProver(String name) {
		Prover prover = null;
		// protect against concurrent access of the proverMap
		synchronized (this) {
			prover = proverMap.get(name);
			if (prover == null) {
				prover = new JTrologProver(name);
				proverMap.put(name, prover);
			}
		}
		return prover;
	}
}
