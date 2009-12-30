/*
 * Copyright 2010 by Miklós Espák <espakm@gmail.com>
 * 
 * This file is part of Prolog4J.
 * 
 * Prolog4J is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Prolog4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Prolog4J.  If not, see <http://www.gnu.org/licenses/>.
 */
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

	/** The default conversion policy. */
	private final ConversionPolicy conversionPolicy = createConversionPolicy();
	
	/**
	 * Stores the provers assigned to their names.
	 */
	private Map<String, Prover> proverMap = new HashMap<String, Prover>();

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
	
	@Override
	public ConversionPolicy getConversionPolicy() {
		return conversionPolicy;
	}
	
}
