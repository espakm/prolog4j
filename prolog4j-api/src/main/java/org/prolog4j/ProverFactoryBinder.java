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

/**
 * The binding of {@link ProverFactory} class with an actual instance of
 * {@link IProverFactory} is performed using information returned by this class.
 * 
 * This class is meant to provide a dummy ProverFactoryBinder to the prolog4j-api
 * module. Real implementations are found in each Prolog4J binding project, e.g.
 * prolog4j-tuprolog, prolog4j-jtrolog etc.
 */
public final class ProverFactoryBinder {

	/**
	 * The unique instance of this class.
	 */
	private static final ProverFactoryBinder SINGLETON = new ProverFactoryBinder();

	/**
	 * Returns the single instance of this class.
	 * 
	 * @return the ProverFactoryBinder instance for a specific implementation
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

	/**
	 * Private constructor to prevent instantiation.
	 */
	private ProverFactoryBinder() {
		// Note: JCL gets substituted at build time by an appropriate Ant task
		throw new UnsupportedOperationException("This code should have never made it into the jar.");
	}

	/**
	 * The IProverFactory instance returned by the method should always be the same object.
	 * @return a factory for creating provers
	 */
	public IProverFactory getProverFactory() {
		throw new UnsupportedOperationException("This code should never make it into the jar.");
	}

	/**
	 * Returns the name of the factory prover class.
	 * @return the name of the factory prover class
	 */
	public String getProverFactoryClassName() {
		throw new UnsupportedOperationException("This code should never make it into the jar.");
	}
}
