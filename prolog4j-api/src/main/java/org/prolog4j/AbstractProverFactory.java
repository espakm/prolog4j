/* 
 * Copyright (c) 2010 Miklos Espak
 * All rights reserved.
 * 
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 * 
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 * 
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
	
	/** Stores the provers assigned to their names. */
	private Map<String, Prover> proverMap = new HashMap<String, Prover>();

	@Override
	public Prover getProver(String name) {
		Prover prover = null;
		// protects against concurrent access of the proverMap
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
