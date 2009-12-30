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

import org.prolog4j.AbstractProver;
import org.prolog4j.ConversionPolicy;
import org.prolog4j.Query;

import ubc.cs.JLog.Foundation.jPrologAPI;
import ubc.cs.JLog.Terms.iObjectToTerm;
import ubc.cs.JLog.Terms.iTermToObject;
import ubc.cs.JLog.Terms.jTerm;
import ubc.cs.JLog.Terms.jTermTranslation;

/**
 * Represents a Prolog knowledge base and provides methods for solving queries
 * on it. The prover itself is not responsible for processing the solutions.
 */
public class JLogProver extends AbstractProver {

	/** Class version for serialization. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The JLog engine that is used for storing the knowledge base and 
	 * solving queries on it.
	 */
	private final transient jPrologAPI engine;

	/**
	 * Performs no translation at all. For terms it returns the original term as
	 * represented in JLog. This disables the automatic translation of JLog, so 
	 * terms have to be converted manually later.
	 * <p> For objects, it expects that the object is a <tt>jTerm</tt> instance,
	 * indeed, and returns it simply. This disables the automatic translation of
	 * JLog, so objects have to be converted manually, in advance.
	 */
	private static final jTermTranslation IDEMPOTENT_TRANSLATION;
	static {
		IDEMPOTENT_TRANSLATION = new jTermTranslation();
		IDEMPOTENT_TRANSLATION.RegisterDefaultTermToObjectConverter(new iTermToObject() {
			@Override
			public Object createObjectFromTerm(jTerm term) {
				return term;
			}
		});
		IDEMPOTENT_TRANSLATION.RegisterDefaultObjectToTermConverter(new iObjectToTerm() {
			@Override
			public jTerm createTermFromObject(Object object) {
				return (jTerm) object;
			}
		});
	}

	/**
	 * Creates a JLog prover.
	 */
	JLogProver() {
		super();
		engine = new jPrologAPI("");
		engine.setTranslation(IDEMPOTENT_TRANSLATION);
	}

	/**
	 * Returns the jTrolog engine used by the prover.
	 * @return the jTrolog engine
	 */
	public jPrologAPI getEngine() {
		return engine;
	}

	@Override
	public Query query(String goal) {
		return new JLogQuery(this, goal);
	}

	@Override
	public void loadLibrary(String className) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addTheory(String theory) {
		engine.consultSource(theory);
	}

	@Override
	public void addTheory(String... theory) {
		StringBuilder sb = new StringBuilder();
		for (String factOrRule : theory) {
			sb.append(factOrRule).append('\n');
		}
		engine.consultSource(sb.toString());
	}

}
