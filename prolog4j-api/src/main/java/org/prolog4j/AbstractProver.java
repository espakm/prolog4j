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
package org.prolog4j;

import java.io.Serializable;
//import java.lang.ref.Reference;
//import java.lang.ref.ReferenceQueue;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;

/**
 * Serves as base class for prover implementation.
 */
public abstract class AbstractProver implements Prover, Serializable {

//	/**
//	 * Weak facts refers to Java objects by weak references.
//	 */
//	private Map<Reference, List<Query>> weakFacts = new HashMap<Reference, List<Query>>();
//	
//	/**
//	 * Reference queue for the objects referred by weak facts.
//	 */
//	private ReferenceQueue<Object> obsoleteObjects = new ReferenceQueue<Object>();
	
	/** Class version for serialization. */
	private static final long serialVersionUID = 1L;

	@Override
	public final <A> Solution<A> solve(String goal, Object... actualArgs) {
//		reclaimObsoleteFacts();
		return query(goal).solve(actualArgs);
	}

//	/**
//	 * Retracts weak rules that refer to objects reclaimed by the garbage 
//	 * collector.
//	 */
//	public void reclaimObsoleteFacts() {
//		Reference ref = obsoleteObjects.poll();
//		while (ref != null) {
//			List<Query> rules = weakFacts.get(ref);
//			for (Query rule: rules) {
//				rule.retract();
//			}
//			ref = obsoleteObjects.poll();
//		}
//	}

	@Override
	public void assertz(String fact, Object... args) {
		Query q = query("assertz(" + fact.substring(0, fact.lastIndexOf('.')) + ").");
		q.solve(args);
//		for (Reference ref: q.getWeakReferences()) {
//			List<Query> rules = weakFacts.get(ref);
//			if (rules == null) {
//				rules = new LinkedList<Query>();
//				weakFacts.put(ref, rules);
//			}
//			rules.add(q);
//		}
	}

	@Override
	public void retract(String fact) {
		int lastDot = fact.lastIndexOf('.');
		int length = fact.length();
		if (lastDot == -1 || fact.substring(lastDot, length).trim().length() > 1) {
			lastDot = length;
		}
		query("retract(" + fact.substring(0, lastDot) + ").").solve();
	}
	
	/** The default conversion policy used by the current implementation. */
	private static final ConversionPolicy GLOBAL_POLICY = ProverFactory.getConversionPolicy();

	/** The conversion policy of the prover. */
	private ConversionPolicy conversionPolicy = new LazyConversionPolicy();

	@Override
	public ConversionPolicy getConversionPolicy() {
		return conversionPolicy;
	}
	
	@Override
	public void setConversionPolicy(ConversionPolicy conversionPolicy) {
		this.conversionPolicy = conversionPolicy;
	}
	
	/** 
	 * By default this policy delegates method calls to the global policy.
	 * At the first time when the policy is customized (a converter is added),
	 * it creates a new policy, and it will delegate the subsequent calls to
	 * this new policy.
	 */
	private class LazyConversionPolicy extends ConversionPolicy {
		
		/** 
		 * The conversion requests will be delegated to this policy. 
		 * Its value is the global policy by default.
		 */
		private ConversionPolicy delegate = GLOBAL_POLICY;
		
		@Override
		public <T> void addObjectConverter(Class<T> pattern, Converter<T> converter) {
			if (delegate == GLOBAL_POLICY) {
				delegate = ProverFactory.createConversionPolicy();
			}
			delegate.addObjectConverter(pattern, converter);
		}

		@Override
		public void addTermConverter(String pattern, Converter<Object> converter) {
			if (delegate == GLOBAL_POLICY) {
				delegate = ProverFactory.createConversionPolicy();
			}
			delegate.addTermConverter(pattern, converter);
		}
		
		@Override
		public Object convertObject(Object object) {
			return delegate.convertObject(object);
		}
		
		@Override
		public Object convertTerm(Object term) {
			return delegate.convertTerm(term);
		}
		
		@Override
		public <T> T convertTerm(Object term, java.lang.Class<T> type) {
			return delegate.convertTerm(term, type);
		}

		@Override
		public boolean match(Object term1, Object term2) {
			return delegate.match(term1, term2);
		}

		@Override
		public Object term(int value) {
			return delegate.term(value);
		}
		@Override
		public Object term(double value) {
			return delegate.term(value);
		}
		@Override
		public Object term(String name) {
			return delegate.term(name);
		}

		@Override
		public Object term(String name, Object... args) {
			return delegate.term(name, args);
		}
		@Override
		public int intValue(Object term) {
			return delegate.intValue(term);
		}
		@Override
		public double doubleValue(Object term) {
			return delegate.doubleValue(term);
		}
		@Override
		protected String getName(Object compound) {
			return delegate.getName(compound);
		}
		@Override
		protected int getArity(Object compound) {
			return delegate.getArity(compound);
		}
		@Override
		protected Object getArg(Object compound, int index) {
			return delegate.getArg(compound, index);
		}

		@Override
		public boolean isAtom(Object term) {
			return delegate.isAtom(term);
		}

		@Override
		public boolean isCompound(Object term) {
			return delegate.isCompound(term);
		}

		@Override
		public boolean isDouble(Object term) {
			return delegate.isDouble(term);
		}

		@Override
		public boolean isInteger(Object term) {
			return delegate.isInteger(term);
		}

//		@Override
//		public Term pattern(String term) {
//			return delegate.pattern(term);
//		}
	}

	// /**
	// * Replace this instance with a homonymous (same name) prover returned by
	// * ProverFactory. Note that this method is only called during
	// * deserialization.
	// * <p>
	// * This approach will work well if the desired IProverFactory is the one
	// * references by ProverFactory. However, if the user manages its prover
	// * hierarchy through a different (non-static) mechanism, e.g. dependency
	// * injection, then this approach would be mostly counterproductive.
	// *
	// * @return prover with same name as returned by ProverFactory
	// */
	// protected Object readResolve() {
	// // TODO The knowledge base is not restored this way.
	// return ProverFactory.getProver(getName());
	// }

}
