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

package org.prolog4j.impl;

import org.prolog4j.IProverFactory;
import org.prolog4j.jlog.JLogProverFactory;

/**
 * The binding of {@link ProverFactory} class with an actual instance of
 * {@link IProverFactory} is performed using information returned by this class.
 * 
 * This class is meant to provide a dummy StaticProverBinder to the prolog4j-api
 * module. Real implementations are found in each Prolog4J binding project, e.g.
 * prolog4j-tuprolog, prolog4j-jtrolog etc.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class StaticProverBinder {

	/**
	 * The unique instance of this class.
	 * 
	 */
	private static final StaticProverBinder SINGLETON = new StaticProverBinder();

	/**
	 * Return the singleton of this class.
	 * 
	 * @return the StaticProverBinder singleton
	 */
	public static final StaticProverBinder getSingleton() {
		return SINGLETON;
	}

	/**
	 * Declare the version of the Prolog4J API this implementation is compiled
	 * against. The value of this field is usually modified with each release.
	 */
	// to avoid constant folding by the compiler, this field must *not* be final
	public static String REQUESTED_API_VERSION = "0.1.2"; // !final

	private static final String proverFactoryClassName = JLogProverFactory.class.getName();

	/**
	 * The IProverFactory instance returned by the {@link #getProverFactory}
	 * method should always be the same object
	 */
	private final IProverFactory proverFactory;

	private StaticProverBinder() {
		// Note: JCL gets substituted at build time by an appropriate Ant task
		proverFactory = new JLogProverFactory();
	}

	public IProverFactory getProverFactory() {
		return proverFactory;
	}

	public String getProverFactoryClassName() {
		return proverFactoryClassName;
	}
}
