package org.prolog4j.tuprolog;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.prolog4j.Compound;
import org.prolog4j.ConversionPolicy;
import org.prolog4j.Converter;

import alice.tuprolog.Int;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;

public class TuPrologConversionPolicy extends ConversionPolicy {

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
	private static final Converter<alice.tuprolog.Long> LONG_TERM_CONVERTER = new Converter<alice.tuprolog.Long>() {
		@Override
		public Object convert(alice.tuprolog.Long value) {
			return value.longValue();
		}
	};
	/** Converts an alice.tuprolog.Float term to a Float object. */
	private static final Converter<alice.tuprolog.Float> FLOAT_TERM_CONVERTER = new Converter<alice.tuprolog.Float>() {
		@Override
		public Object convert(alice.tuprolog.Float value) {
			return value.floatValue();
		}
	};
	/** Converts an alice.tuprolog.Double term to a Double object. */
	private static final Converter<alice.tuprolog.Double> DOUBLE_TERM_CONVERTER = new Converter<alice.tuprolog.Double>() {
		@Override
		public Object convert(alice.tuprolog.Double value) {
			return value.doubleValue();
		}
	};

	public TuPrologConversionPolicy() {
		super();
		addObjectConverter(Long.class, LONG_CONVERTER);
		addObjectConverter(Float.class, FLOAT_CONVERTER);
		addObjectConverter(Double.class, DOUBLE_CONVERTER);
		addObjectConverter(Integer.class, INTEGER_CONVERTER);
		addObjectConverter(String.class, STRING_CONVERTER);
		addObjectConverter(Object[].class, new Converter<Object[]>() {
			@Override
			public Object convert(Object[] array) {
				Struct pList = new Struct();
				for (int i = array.length - 1; i >= 0; --i) {
					pList = new Struct((Term) convertObject(array[i]),
							pList);
				}
				return pList;
			}
		});
		addObjectConverter(List.class, new Converter<List>() {
			@Override
			public Object convert(List list) {
				Struct pList = new Struct();
				ListIterator<?> it = list.listIterator(list.size());
				while (it.hasPrevious()) {
					pList = new Struct((Term) convertObject(it
							.previous()), pList);
				}
				return pList;
			}
		});
		addObjectConverter(Compound.class, new Converter<Compound>() {
			@Override
			public Object convert(Compound value) {
				String functor = value.getFunctor();
				Object[] args = value.getArgs();
				Term[] tArgs = new Term[value.getArity()];
				for (int i = 0; i < args.length; ++i) {
					tArgs[i] = (Term) convertObject(args[i]);
				}
				return new Struct(functor, tArgs);
			}
		});
		addObjectConverter(Term.class, new Converter<Term>() {
			@Override
			public Object convert(Term value) {
				return value;
			}
		});
		addTermConverter(Int.class, INT_TERM_CONVERTER);
		addTermConverter(alice.tuprolog.Long.class, LONG_TERM_CONVERTER);
		addTermConverter(alice.tuprolog.Float.class,
				FLOAT_TERM_CONVERTER);
		addTermConverter(alice.tuprolog.Double.class,
				DOUBLE_TERM_CONVERTER);
		addTermConverter(Struct.class, new Converter<Struct>() {
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
						array[i] = convertTerm(t.getTerm());
						value = (Struct) value.getArg(1).getTerm();
					}
					return array;
				}
				int arity = value.getArity();
				Object[] args = new Object[arity];
				for (int i = 0; i < arity; ++i) {
					args[i] = convertTerm(value.getArg(i).getTerm());
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
						list.add(convertTerm(t.getTerm()));
						value = (Struct) value.getArg(1).getTerm();
					}
					return (R) list;
				}
				return null;
			}
		});
	}

	@Override
	public boolean match(Object term1, Object term2) {
		if (term1 instanceof Term && term2 instanceof Term) {
			return ((Term) term1).match((Term) term2);
		}
		return false;
	}

//	@Override
//	public Object variable() {
//		return new Var();
//	}

	@Override
	public Object compound(String name, Object... args) {
		Term[] tArgs = new Term[args.length];
		for (int i = 0; i < tArgs.length; ++i) {
			tArgs[i] = (Term) convertObject(args[i]);
		}
		return new Struct(name, tArgs);
	}

	@Override
	protected String getSpecification(Object term) {
		if (((Term) term).isAtom()) {
			return ((Struct) term).getName();
		}
		if (term instanceof Struct) {
			Struct struct = (Struct) term;
			return String.format("%s/%d", struct.getName(), struct.getArity());
		}
		return null;
	}

	@Override
	protected Object[] getArgs(Object compound) {
		if (compound instanceof Struct) {
			Struct struct = (Struct) compound;
			Object[] args = new Object[struct.getArity()];
			for (int i = 0; i < args.length; ++i) {
				args[i] = struct.getArg(i);
			}
			return args;
		}
		return null;
	}

}
