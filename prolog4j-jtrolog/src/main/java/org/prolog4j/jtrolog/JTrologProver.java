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
package org.prolog4j.jtrolog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.prolog4j.AbstractProver;
import org.prolog4j.ConversionPolicy;
import org.prolog4j.Query;

import jTrolog.errors.InvalidLibraryException;
import jTrolog.errors.PrologException;
import jTrolog.engine.Prolog;

/**
 * Represents a Prolog knowledge base and provides methods for solving queries
 * on it. The prover itself is not responsible for processing the solutions.
 */
public class JTrologProver extends AbstractProver {

	// static {
	// GLOBAL.engine.addWarningListener(new WarningListener() {
	// public void onWarning(WarningEvent e) {
	// System.out.println(e.getMsg());
	// }
	// });
	// GLOBAL.engine.addOutputListener(new OutputListener() {
	// public void onOutput(alice.tuprolog.event.OutputEvent e) {
	// System.out.println(e.getMsg());
	// };
	// });
	// }

	/** Class version for serialization. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The jTrolog engine that is used for storing the knowledge base and 
	 * solving queries on it.
	 */
	private final Prolog engine;

	/**
	 * Creates a jTrolog prover.
	 */
	JTrologProver() {
		super();
		engine = new Prolog();
	}

	/**
	 * Returns the jTrolog engine used by the prover.
	 * @return the jTrolog engine
	 */
	public Prolog getEngine() {
		return engine;
	}

	@Override
	public Query query(String goal) {
		return new JTrologQuery(this, goal);
	}
	
	@Override
	public void loadLibrary(String className) {
		try {
			engine.loadLibrary(className);
		} catch (InvalidLibraryException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void loadTheory(InputStream input) throws IOException {
		try {
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line).append('\n');
			}
			engine.addTheory(sb.toString());
		} catch (PrologException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addTheory(String theory) {
		try {
			engine.addTheory(theory);
		} catch (PrologException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addTheory(String... theory) {
		StringBuilder sb = new StringBuilder();
		for (String factOrRule : theory) {
			sb.append(factOrRule).append('\n');
		}
		try {
			engine.addTheory(sb.toString());
		} catch (PrologException e) {
			e.printStackTrace();
		}
	}

}
