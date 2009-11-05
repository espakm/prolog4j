package org.prolog4j.jlog;

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
public final class JLogProverFactory implements IProverFactory {

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
	 * Stores the provers assigned to their names.
	 */
	private Map<String, Prover> proverMap = new HashMap<String, Prover>();

	/**
	 * Private constructor to prevent instantiation.
	 */
	private JLogProverFactory() {
	}
	
	@Override
	public Prover getProver(String name) {
		Prover prover = null;
		// protect against concurrent access of the proverMap
		synchronized (this) {
			prover = proverMap.get(name);
			if (prover == null) {
				prover = new JLogProver(name);
				proverMap.put(name, prover);
			}
		}
		return prover;
	}
}
