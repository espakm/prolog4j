package org.prolog4j.tuprolog;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.prolog4j.AbstractProver;
import org.prolog4j.Compound;
import org.prolog4j.ConversionPolicy;
import org.prolog4j.Converter;
import org.prolog4j.Query;

import alice.tuprolog.Int;
import alice.tuprolog.InvalidLibraryException;
import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.Prolog;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Theory;

/**
 * Represents a Prolog knowledge base and provides methods for solving queries
 * on it. The prover itself is not responsible for processing the solutions.
 */
public class TuPrologProver extends AbstractProver {

	/** Converts an Integer object to a term. */
	private static final Converter<Integer> INTEGER_CONVERTER = new Converter<Integer>() {
		@Override
		public Object convert(Integer i) {
			return new Int(i);
		}
	};
	/** Converts a Long object to a term. */
	private static final Converter<Long> LONG_CONVERTER = new Converter<Long>() {
		@Override
		public Object convert(Long value) {
			return new alice.tuprolog.Long(value);
		}
	};
	/** Converts a Float object to a term. */
	private static final Converter<Float> FLOAT_CONVERTER = new Converter<Float>() {
		@Override
		public Object convert(Float value) {
			return new alice.tuprolog.Float(value);
		}
	};
	/** Converts a Double object to a term. */
	private static final Converter<Double> DOUBLE_CONVERTER = new Converter<Double>() {
		@Override
		public Object convert(Double value) {
			return new alice.tuprolog.Double(value);
		}
	};
	/** Converts a String object to a term. */
	private static final Converter<String> STRING_CONVERTER = new Converter<String>() {
		@Override
		public Object convert(String value) {
			return new Struct(value);
		}
	};
	/** Converts an alice.tuprolog.Int term to an Integer object. */
	private static final Converter<Int> INT_TERM_CONVERTER = new Converter<Int>() {
		@Override
		public Object convert(Int value) {
			return value.intValue();
		}
	};
	/** Converts an alice.tuprolog.Long term to a Long object. */
	private static final Converter<alice.tuprolog.Long> LONG_TERM_CONVERTER =
		new Converter<alice.tuprolog.Long>() {
			@Override
			public Object convert(alice.tuprolog.Long value) {
				return value.longValue();
			}
		};
	/** Converts an alice.tuprolog.Float term to a Float object. */
	private static final Converter<alice.tuprolog.Float> FLOAT_TERM_CONVERTER =
		new Converter<alice.tuprolog.Float>() {
			@Override
			public Object convert(alice.tuprolog.Float value) {
				return value.floatValue();
			}
		};
	/** Converts an alice.tuprolog.Double term to a Double object. */
	private static final Converter<alice.tuprolog.Double> DOUBLE_TERM_CONVERTER =
		new Converter<alice.tuprolog.Double>() {
			@Override
			public Object convert(alice.tuprolog.Double value) {
				return value.doubleValue();
			}
		};

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
	 * The tuProlog engine that is used for storing the knowledge base and
	 * solving queries on it.
	 */
	private final Prolog engine;

	/**
	 * Creates a tuProlog prover of the given name.
	 */
	TuPrologProver() {
		super();
		engine = new Prolog();
		final ConversionPolicy policy = getConversionPolicy();
		policy.addObjectConverter(Long.class, LONG_CONVERTER);
		policy.addObjectConverter(Float.class, FLOAT_CONVERTER);
		policy.addObjectConverter(Double.class, DOUBLE_CONVERTER);
		policy.addObjectConverter(Integer.class, INTEGER_CONVERTER);
		policy.addObjectConverter(String.class, STRING_CONVERTER);
		policy.addObjectConverter(Object[].class, new Converter<Object[]>() {
			@Override
			public Object convert(Object[] array) {
				Struct pList = new Struct();
				for (int i = array.length - 1; i >= 0; --i) {
					pList = new Struct((Term) policy.convertObject(array[i]),
							pList);
				}
				return pList;
			}
		});
		policy.addObjectConverter(List.class, new Converter<List>() {
			@Override
			public Object convert(List list) {
				Struct pList = new Struct();
				ListIterator<?> it = list.listIterator(list.size());
				while (it.hasPrevious()) {
					pList = new Struct((Term) policy.convertObject(it
							.previous()), pList);
				}
				return pList;
			}
		});
		policy.addObjectConverter(Compound.class, new Converter<Compound>() {
			@Override
			public Object convert(Compound value) {
				String functor = value.getFunctor();
				Object[] args = value.getArgs();
				Term[] tArgs = new Term[value.getArity()];
				for (int i = 0; i < args.length; ++i) {
					tArgs[i] = (Term) policy.convertObject(args[i]);
				}
				return new Struct(functor, tArgs);
			}
		});
		policy.addTermConverter(Int.class, INT_TERM_CONVERTER);
		policy.addTermConverter(alice.tuprolog.Long.class, LONG_TERM_CONVERTER);
		policy.addTermConverter(alice.tuprolog.Float.class,
				FLOAT_TERM_CONVERTER);
		policy.addTermConverter(alice.tuprolog.Double.class,
				DOUBLE_TERM_CONVERTER);
		policy.addTermConverter(Struct.class, new Converter<Struct>() {
			@Override
			public Object convert(Struct value) {
				if (value.isAtom()) {
					return value.getName();
				}
				if (value.isList()) {
					int length = value.listSize();
					Object[] array = new Object[length];
					for (int i = 0; i < length; ++i) {
						Term t = value.getArg(0);
						array[i] = policy.convertTerm(t.getTerm());
						value = (Struct) value.getArg(1).getTerm();
					}
					return array;
				}
				int arity = value.getArity();
				Object[] args = new Object[arity];
				for (int i = 0; i < arity; ++i) {
					args[i] = policy.convertTerm(value.getArg(i).getTerm());
				}
				return new Compound(value.getName(), args);
				// return Terms.getInstance().toObject(value);
			}

			@Override
			public <R> R convert(Struct value, java.lang.Class<R> to) {
				if (value.isList() && to == List.class) {
					int length = value.listSize();
					List list = new ArrayList(length);
					for (int i = 0; i < length; ++i) {
						Term t = value.getArg(0);
						list.add(policy.convertTerm(t.getTerm()));
						value = (Struct) value.getArg(1).getTerm();
					}
					return (R) list;
				}
				return null;
			}
		});
	}

	/**
	 * Returns the tuProlog engine used by the prover.
	 * 
	 * @return the tuProlog engine
	 */
	public Prolog getEngine() {
		return engine;
	}

	@Override
	public Query query(String goal) {
		return new TuPrologQuery(this, goal);
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
	public void addTheory(String theory) {
		try {
			engine.addTheory(new Theory(theory));
		} catch (InvalidTheoryException e) {
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
			engine.addTheory(new Theory(sb.toString()));
		} catch (InvalidTheoryException e) {
			e.printStackTrace();
		}
	}

}
