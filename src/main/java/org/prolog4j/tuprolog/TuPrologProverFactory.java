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
public class TuPrologProverFactory implements IProverFactory {

	final static TuPrologProverFactory INSTANCE = new TuPrologProverFactory();

	private Map<String, Prover> proverMap = new HashMap<String, Prover>();

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
