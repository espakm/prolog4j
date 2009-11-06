package org.prolog4j.jlog;

import ubc.cs.JLog.Terms.iObjectToTerm;
import ubc.cs.JLog.Terms.iTermToObject;
import ubc.cs.JLog.Terms.jTerm;
import ubc.cs.JLog.Terms.jTermTranslation;

/**
 * This class is responsible for translating POJOs to terms and vice versa.
 * The original way of JLog is not suitable, so the term to object
 * transformation is disabled here (an idempotent transformation is used 
 * instead of that), and it is done by the JTrologSolution class directly. 
 * The object to term transformations are initiated by this class, using
 * Terms.toTerm(Object).
 */
class TermTranslation extends jTermTranslation {

	/**
	 * Performs no translation at all. Returns the original term as represented
	 * in JLog. This disables the automatic translation of JLog, so terms has to
	 * be converted manually later.
	 */
	private static final iTermToObject IDEMPOTENT_TERM_TRANSLATOR = new iTermToObject() {
		@Override
		public Object createObjectFromTerm(jTerm term) {
			return term;
		}
	};

	/**
	 * Transforms objects to terms by {@link Terms.toTerm(Object)}.
	 */
	private static final iObjectToTerm OBJECT_CONVERTER = new iObjectToTerm() {
		@Override
		public jTerm createTermFromObject(Object object) {
			return Terms.toTerm(object);
		}
	};

	/**
	 * Creates a TermTranslation object and register the default translators
	 * for converting terms to objects and vice versa.
	 */
	public TermTranslation() {
		RegisterDefaultTermToObjectConverter(IDEMPOTENT_TERM_TRANSLATOR);
		RegisterDefaultObjectToTermConverter(OBJECT_CONVERTER);
	}
	
}
