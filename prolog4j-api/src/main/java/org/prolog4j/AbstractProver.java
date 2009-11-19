package org.prolog4j;

import java.io.Serializable;

/**
 * Serves as base class for prover implementation.
 */
public abstract class AbstractProver implements Prover, Serializable {

	/** Class version for serialization. */
	private static final long serialVersionUID = 1L;

	@Override
	public final <A> Solution<A> solve(String goal, Object... actualArgs) {
		return query(goal).solve(actualArgs);
	}

	/** The default conversion policy used by the current implementation. */
	private static final ConversionPolicy GLOBAL_POLICY = ProverFactory.getConversionPolicy();

	@Override
	public ConversionPolicy getConversionPolicy() {
		return conversionPolicy;
	}

	/** 
	 * This policy tries to do the conversion by themself at first, and if it is
	 * not possible then delegates the task to the default conversion policy. 
	 */
	private final ConversionPolicy conversionPolicy = new ConversionPolicy() {
		
		@Override
		public Object convertObject(Object object) {
			try {
				return super.convertObject(object);
			} catch (RuntimeException e) {
				return GLOBAL_POLICY.convertObject(object);
			}
		}
		
		@Override
		public Object convertTerm(Object term) {
			try {
				return super.convertTerm(term);
			} catch (RuntimeException e) {
				return GLOBAL_POLICY.convertTerm(term);
			}
		}
		
		@Override
		public <T> T convertTerm(Object term, java.lang.Class<T> type) {
			try {
				return super.convertTerm(term, type);
			} catch (RuntimeException e) {
				return GLOBAL_POLICY.convertTerm(term, type);
			}
		}

		@Override
		public Object compound(String name, Object... args) {
			return GLOBAL_POLICY.compound(name, args);
		}

		@Override
		public boolean match(Object term1, Object term2) {
			return GLOBAL_POLICY.match(term1, term2);
		}

		@Override
		protected String getSpecification(Object term) {
			return GLOBAL_POLICY.getSpecification(term);
		}

		@Override
		protected Object[] getArgs(Object compound) {
			return GLOBAL_POLICY.getArgs(compound);
		}
	};
	
	// /**
	// * Replace this instance with a homonymous (same name) prover returned by
	// * ProverFactory. Note that this method is only called during
	// * deserialization.
	// *
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
