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
package org.prolog4j.swi;

import java.io.File;
import java.util.StringTokenizer;

import jpl.JPL;

import org.prolog4j.AbstractProverFactory;
import org.prolog4j.ConversionPolicy;
import org.prolog4j.Prover;

/**
 * An implementation of {@link IProverFactory} which always returns
 * the same {@link SWIPrologProver} instance. SWI-Prolog does not support
 * multiple Prolog engines.
 */
public final class SWIPrologProverFactory extends AbstractProverFactory {

	static {
		// This is a workaround. If I run the test from Maven, then it won't find
		// the JPL library, although running the JUnit test directly succeeds.
		String libraryPath = System.getProperty("java.library.path");
		StringTokenizer st = new StringTokenizer(libraryPath, File.pathSeparator);
		while (st.hasMoreTokens()) {
			try {
				JPL.setNativeLibraryDir(st.nextToken());
				JPL.init();
				break;
			}
			catch (UnsatisfiedLinkError e) {
				continue;
			}
		}
	}
	
	/**
	 * The unique instance of this class.
	 */
	private static final SWIPrologProverFactory INSTANCE = new SWIPrologProverFactory();
	
	private static final SWIPrologProver prover = null;
	
	/**
	 * Returns the single instance of this class.
	 * 
	 * @return the only one SWIPrologProverFactory instance
	 */
	public static SWIPrologProverFactory getInstance() {
		return INSTANCE;
	}

	/**
	 * Private constructor to prevent instantiation.
	 */
	private SWIPrologProverFactory() {
		super();
	}
	
	@Override
	public Prover getProver() {
		return new SWIPrologProver();
	}

	@Override
	public ConversionPolicy createConversionPolicy() {
		return new SWIPrologConversionPolicy();
	}

}
