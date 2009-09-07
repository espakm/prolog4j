package org.prolog4j.jlog;

import ubc.cs.JLog.Terms.iObjectToTerm;
import ubc.cs.JLog.Terms.iTermToObject;
import ubc.cs.JLog.Terms.jTerm;
import ubc.cs.JLog.Terms.jTermTranslation;

public class TermTranslation extends jTermTranslation {

	private static final iTermToObject idempotentTermTranslator = new iTermToObject() {
		@Override
		public Object createObjectFromTerm(jTerm term) {
			return term;
		}
	};

	private static final iObjectToTerm objectConverter = new iObjectToTerm() {
		@Override
		public jTerm createTermFromObject(Object object) {
			return Terms.toTerm(object);
		}
	};

	public TermTranslation() {
		RegisterDefaultTermToObjectConverter(idempotentTermTranslator);
		RegisterDefaultObjectToTermConverter(objectConverter);
	}
	
}
