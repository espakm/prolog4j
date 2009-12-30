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
