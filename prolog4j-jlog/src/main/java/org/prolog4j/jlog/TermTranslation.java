package org.prolog4j.jlog;

import ubc.cs.JLog.Terms.iObjectToTerm;
import ubc.cs.JLog.Terms.iTermToObject;
import ubc.cs.JLog.Terms.jTerm;
import ubc.cs.JLog.Terms.jTermTranslation;

public class TermTranslation extends jTermTranslation {

	private static final iTermToObject IDEMPOTENT_TERM_TRANSLATOR = new iTermToObject() {
		@Override
		public Object createObjectFromTerm(jTerm term) {
			return term;
		}
	};

	private static final iObjectToTerm OBJECT_CONVERTER = new iObjectToTerm() {
		@Override
		public jTerm createTermFromObject(Object object) {
			return Terms.toTerm(object);
		}
	};

	public TermTranslation() {
		RegisterDefaultTermToObjectConverter(IDEMPOTENT_TERM_TRANSLATOR);
		RegisterDefaultObjectToTermConverter(OBJECT_CONVERTER);
	}
	
}
