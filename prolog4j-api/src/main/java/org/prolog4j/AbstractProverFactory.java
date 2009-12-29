package org.prolog4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract implementation of the IProverFactory interface. It creates a prover
 * with a given name on demand, at the first time when requested, and then
 * stores it in a map. If a prover with the same name is requested again, then
 * it returns that from the map.
 */
public abstract class AbstractProverFactory implements IProverFactory {

	/**
	 * Stores the provers assigned to their names.
	 */
	private Map<String, Prover> proverMap = new HashMap<String, Prover>();

//	@Override
//	public Prover getProver() {
//		return createProver();
//	}
	
	@Override
	public Prover getProver(String name) {
		Prover prover = null;
		// protect against concurrent access of the proverMap
		synchronized (this) {
			prover = proverMap.get(name);
			if (prover == null) {
				prover = getProver();
				proverMap.put(name, prover);
			}
		}
		return prover;
	}
	
}
