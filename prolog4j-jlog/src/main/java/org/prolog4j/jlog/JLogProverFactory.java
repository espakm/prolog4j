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
package org.prolog4j.jlog;

import org.prolog4j.AbstractProverFactory;
import org.prolog4j.ConversionPolicy;
import org.prolog4j.Prover;

/**
 * An implementation of {@link IProverFactory} which always returns
 * {@link JLogProver} instances.
 */
public final class JLogProverFactory extends AbstractProverFactory {

	/** The unique instance of this class. */
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
		super();
	}

	@Override
	public Prover getProver() {
		return new JLogProver();
	}

	@Override
	public ConversionPolicy createConversionPolicy() {
		return new JLogConversionPolicy();
	}

}
