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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.prolog4j.AbstractProver;
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
	public void loadTheory(InputStream input) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(input));
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line).append('\n');
		}
		engine.consultSource(sb.toString());
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
