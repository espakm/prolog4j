/*
 * Copyright (c) 2004-2005 SLF4J.ORG
 * Copyright (c) 2004-2005 QOS.ch
 *
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute, and/or sell copies of  the Software, and to permit persons
 * to whom  the Software is furnished  to do so, provided  that the above
 * copyright notice(s) and this permission notice appear in all copies of
 * the  Software and  that both  the above  copyright notice(s)  and this
 * permission notice appear in supporting documentation.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR  A PARTICULAR PURPOSE AND NONINFRINGEMENT
 * OF  THIRD PARTY  RIGHTS. IN  NO EVENT  SHALL THE  COPYRIGHT  HOLDER OR
 * HOLDERS  INCLUDED IN  THIS  NOTICE BE  LIABLE  FOR ANY  CLAIM, OR  ANY
 * SPECIAL INDIRECT  OR CONSEQUENTIAL DAMAGES, OR  ANY DAMAGES WHATSOEVER
 * RESULTING FROM LOSS  OF USE, DATA OR PROFITS, WHETHER  IN AN ACTION OF
 * CONTRACT, NEGLIGENCE  OR OTHER TORTIOUS  ACTION, ARISING OUT OF  OR IN
 * CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 * Except as  contained in  this notice, the  name of a  copyright holder
 * shall not be used in advertising or otherwise to promote the sale, use
 * or other dealings in this Software without prior written authorization
 * of the copyright holder.
 *
 */

package org.prolog4j.tuprolog;

import org.prolog4j.IProverFactory;
import org.prolog4j.ProverFactory;
import org.prolog4j.spi.ProverFactoryBinder;

/**
 * The binding of {@link ProverFactory} class with an actual instance of
 * {@link IProverFactory} is performed using information returned by this class.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class StaticProverBinder implements ProverFactoryBinder {

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
	public static String REQUESTED_API_VERSION = "1.5.9"; // !final

	private static final String proverFactoryClassName = TuPrologProverFactory.class
			.getName();

	/**
	 * The IProverFactory instance returned by the {@link #getProverFactory}
	 * method should always be the same object
	 */
	private final IProverFactory proverFactory;

	private StaticProverBinder() {
		// Note: JCL gets substituted at build time by an appropriate Ant task
		proverFactory = new TuPrologProverFactory();
	}

	public IProverFactory getProverFactory() {
		return proverFactory;
	}

	public String getProverFactoryClassName() {
		return proverFactoryClassName;
	}
}
