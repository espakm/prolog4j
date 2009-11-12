package org.prolog4j.jlog;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.prolog4j.AbstractProver;
import org.prolog4j.ConversionPolicy;
import org.prolog4j.Converter;
import org.prolog4j.Query;

import ubc.cs.JLog.Foundation.jPrologAPI;
import ubc.cs.JLog.Terms.iObjectToTerm;
import ubc.cs.JLog.Terms.iTermToObject;
import ubc.cs.JLog.Terms.jAtom;
import ubc.cs.JLog.Terms.jInteger;
import ubc.cs.JLog.Terms.jList;
import ubc.cs.JLog.Terms.jListPair;
import ubc.cs.JLog.Terms.jNullList;
import ubc.cs.JLog.Terms.jReal;
import ubc.cs.JLog.Terms.jTerm;
import ubc.cs.JLog.Terms.jTermTranslation;

/**
 * Represents a Prolog knowledge base and provides methods for solving queries
 * on it. The prover itself is not responsible for processing the solutions.
 */
public class JLogProver extends AbstractProver {

	/** Converts an Integer object to a term. */
	private static final Converter<Integer> INTEGER_CONVERTER = new Converter<Integer>() {
		@Override
		public Object convert(Integer i) {
			return new jInteger(i);
		}
	};
	/** Converts a Long object to a term. */
	private static final Converter<Long> LONG_CONVERTER = new Converter<Long>() {
		@Override
		public Object convert(Long value) {
			return new jInteger(value.intValue());
		}
	};
	/** Converts a Float object to a term. */
	private static final Converter<Float> FLOAT_CONVERTER = new Converter<Float>() {
		@Override
		public Object convert(Float value) {
			return new jReal(value);
		}
	};
	/** Converts a Double object to a term. */
	private static final Converter<Double> DOUBLE_CONVERTER = new Converter<Double>() {
		@Override
		public Object convert(Double value) {
			return new jReal(value.floatValue());
		}
	};
	/** Converts a String object to a term. */
	private static final Converter<String> STRING_CONVERTER = new Converter<String>() {
		@Override
		public Object convert(String s) {
			return new jAtom(s);
		}
	};
	/** Converts an jTrolog.terms.Int term to an Integer object. */
	private static final Converter<jInteger> INT_TERM_CONVERTER = new Converter<jInteger>() {
		@Override
		public Object convert(jInteger value) {
			return value.getIntegerValue();
		}
	};
	/** Converts an jTrolog.terms.Float term to a Float object. */
	private static final Converter<jReal> FLOAT_TERM_CONVERTER = new Converter<jReal>() {
		@Override
		public Object convert(jReal value) {
			return value.getRealValue();
		}
	};
	/** Converts an jTrolog.terms.StructAtom term to a String object. */
	private static final Converter<jAtom> ATOM_TERM_CONVERTER = new Converter<jAtom>() {
		@Override
		public Object convert(jAtom value) {
			return value.getName();
		}
	};

	/** Class version for serialization. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The JLog engine that is used for storing the knowledge base and 
	 * solving queries on it.
	 */
	private final jPrologAPI engine;

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
	private final iObjectToTerm OBJECT_CONVERTER = new iObjectToTerm() {
		@Override
		public jTerm createTermFromObject(Object object) {
			return (jTerm) getConversionPolicy().convertObject(object);
		}
	};

	/**
	 * Creates a JLog prover.
	 */
	JLogProver() {
		engine = new jPrologAPI("");
		final ConversionPolicy policy = getConversionPolicy();
		policy.addObjectConverter(Long.class, LONG_CONVERTER);
		policy.addObjectConverter(Float.class, FLOAT_CONVERTER);
		policy.addObjectConverter(Double.class, DOUBLE_CONVERTER);
		policy.addObjectConverter(Integer.class, INTEGER_CONVERTER);
		policy.addObjectConverter(String.class, STRING_CONVERTER);
		policy.addObjectConverter(Object[].class, new Converter<Object[]>() {
			@Override
			public Object convert(Object[] array) {
				jList pList = jNullList.NULL_LIST;
				for (int i = array.length - 1; i >= 0; --i) {
					pList = new jListPair((jTerm) policy.convertObject(array[i]), pList);
				}
				return pList;
			}
		});
		policy.addObjectConverter(List.class, new Converter<List>() {
			@Override
			public Object convert(List list) {
				jList pList = jNullList.NULL_LIST;
				ListIterator<?> it = list.listIterator(list.size());
				while (it.hasPrevious()) {
					pList = new jListPair((jTerm) policy.convertObject(it
							.previous()), pList);
				}
				return pList;
			}
		});

		policy.addTermConverter(jInteger.class, INT_TERM_CONVERTER);
		policy.addTermConverter(jReal.class, FLOAT_TERM_CONVERTER);
		policy.addTermConverter(jAtom.class, ATOM_TERM_CONVERTER);
		policy.addTermConverter(jList.class, new Converter<jList>() {
			@Override
			public Object convert(jList value) {
				int length = listSize(value);
				Object[] array = new Object[length];
				for (int i = 0; i < length; ++i) {
					jListPair listPair = (jListPair) value;
					array[i] = policy.convertTerm((listPair.getHead().getTerm()));
					value = (jList) listPair.getTail().getTerm();
				}
				return array;
//				int arity = value.arity;
//				Object[] args = new Object[arity];
//				for (int i = 0; i < arity; ++i) {
////					args[i] = policy.convertTerm(value.getArg(i).getTerm());
//					args[i] = policy.convertTerm(value.getArg(i));
//				}
//				return new Compound(value.name, args);
//				// return Terms.getInstance().toObject(value);
			}

			@Override
			public <R> R convert(jList value, java.lang.Class<R> to) {
				if (isList(value) && to == List.class) {
					int length = listSize(value);
					List list = new ArrayList(length);
					for (int i = 0; i < length; ++i) {
						jListPair listPair = (jListPair) value;
						list.add(policy.convertTerm((listPair.getHead().getTerm())));
						value = (jList) listPair.getTail().getTerm();
					}
					return (R) list;
				}
				return null;
			}
		});
		
		jTermTranslation tt = new jTermTranslation();
		tt.RegisterDefaultTermToObjectConverter(IDEMPOTENT_TERM_TRANSLATOR);
		tt.RegisterDefaultObjectToTermConverter(OBJECT_CONVERTER);
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

	/**
	 * Determines whether a term is a list or not.
	 * 
	 * @param term the term
	 * @return <code>true</code> if the term is a list, otherwise <code>false</code>
	 */
	private static boolean isList(jTerm term) {
		return term instanceof jList;
	}

	/**
	 * Returns the size of a list.
	 * 
	 * @param list the list
	 * @return the size of the list
	 */
	private static int listSize(jList list) {
		int size = 0;
		while (list != jNullList.NULL_LIST) {
			++size;
			list = (jList) ((jListPair) list).getTail().getTerm();
		}
		return size;
	}
}
