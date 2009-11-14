/* 
 * Copyright (c) 2004-2007 QOS.ch
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

import org.prolog4j.jtrolog.JTrologProverFactory;

/**
 * The binding of {@link ProverFactory} class with an actual instance of
 * {@link IProverFactory} is performed using information returned by this class.
 * 
 * This is the jTrolog binding for the Prolog4J API.
 */
public final class ProverFactoryBinder {

	/**
	 * The unique instance of this class.
	 */
	private static final ProverFactoryBinder SINGLETON = new ProverFactoryBinder();

	/**
	 * Returns the single instance of this class.
	 * 
	 * @return the ProverFactoryBinder instance for jTrolog
	 */
	public static ProverFactoryBinder getSingleton() {
		return SINGLETON;
	}

	/**
	 * Declare the version of the Prolog4J API this implementation is compiled
	 * against. The value of this field is usually modified with each release.
	 * To avoid constant folding by the compiler, this field must *not* be final!
	 */
	public static String REQUESTED_API_VERSION = "0.1.2";

	/** The name of the ProverFactory class provided by this binding. */
	private static final String PROVER_FACTORY_CLASS_NAME = JTrologProverFactory.class.getName();

	/**
	 * The IProverFactory instance returned by the {@link #getProverFactory}.
	 */
	private final IProverFactory proverFactory = JTrologProverFactory.getInstance();

	/**
	 * Private constructor to prevent instantiation.
	 */
	private ProverFactoryBinder() {
	}

	/**
	 * The IProverFactory instance returned by the method should always be the same object.
	 * @return a factory for creating provers
	 */
	public IProverFactory getProverFactory() {
		return proverFactory;
	}

	/**
	 * Returns the name of the factory prover class.
	 * @return the name of the factory prover class
	 */
	public String getProverFactoryClassName() {
		return PROVER_FACTORY_CLASS_NAME;
	}
}
