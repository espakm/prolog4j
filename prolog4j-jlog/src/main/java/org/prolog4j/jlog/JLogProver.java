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

	private final ConversionPolicy conversionPolicy;
	
	/**
	 * Performs no translation at all. Returns the original term as represented
	 * in JLog. This disables the automatic translation of JLog, so terms have 
	 * to be converted manually later.
	 */
	private static final iTermToObject IDEMPOTENT_TERM_TRANSLATOR = new iTermToObject() {
		@Override
		public Object createObjectFromTerm(jTerm term) {
			return term;
		}
	};

	/**
	 * Performs no translation at all. Expects that the object is a jTerm,
	 * indeed, and returns it simply. This disables the automatic translation of
	 * JLog, so objects have to be converted manually, in advance.
	 */
	private static final iObjectToTerm IDEMPOTENT_OBJECT_TRANSLATOR = new iObjectToTerm() {
		@Override
		public jTerm createTermFromObject(Object object) {
			return (jTerm) object;
		}
	};

	/**
	 * Creates a JLog prover.
	 */
	JLogProver() {
		super();
		engine = new jPrologAPI("");
		conversionPolicy = new JLogConversionPolicy();
		
		jTermTranslation tt = new jTermTranslation();
		tt.RegisterDefaultTermToObjectConverter(IDEMPOTENT_TERM_TRANSLATOR);
		tt.RegisterDefaultObjectToTermConverter(IDEMPOTENT_OBJECT_TRANSLATOR);
		engine.setTranslation(tt);
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

	@Override
	public ConversionPolicy getConversionPolicy() {
		return conversionPolicy;
	}

}
