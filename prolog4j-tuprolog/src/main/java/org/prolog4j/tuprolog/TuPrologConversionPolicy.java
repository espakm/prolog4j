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

/**
 * tuProlog implementation of the conversion policy.
 */
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

	/**
	 * Constructs a conversion policy for tuProlog.
	 */
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
		addTermConverter(alice.tuprolog.Float.class, FLOAT_TERM_CONVERTER);
		addTermConverter(alice.tuprolog.Double.class, DOUBLE_TERM_CONVERTER);
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
	public Object term(int value) {
		return new Int(value);
	}

	@Override
	public Object term(double value) {
		return new alice.tuprolog.Double(value);
	}

	@Override
	public Object term(String name) {
		return new Struct(name);
	}

	@Override
	public Object term(String name, Object... args) {
		Term[] tArgs = new Term[args.length];
		for (int i = 0; i < tArgs.length; ++i) {
			tArgs[i] = (Term) convertObject(args[i]);
//			tArgs[i] = (Term) args[i];
		}
		return new Struct(name, tArgs);
	}

//	@Override
//	public org.prolog4j.Term pattern(String term) {
//		return null;
////		return new TuPrologTerm(term);
//	}

	@Override
	public int intValue(Object term) {
		return ((alice.tuprolog.Number) term).intValue();
	}

	@Override
	public double doubleValue(Object term) {
		return ((alice.tuprolog.Number) term).doubleValue();
	}

	@Override
	protected String getName(Object compound) {
		return ((Struct) compound).getName();
	}

	@Override
	protected int getArity(Object compound) {
		return ((Struct) compound).getArity();
	}


	@Override
	protected Object getArg(Object compound, int index) {
//		return ((Struct) compound).getArg(index).getTerm();
		return convertTerm(((Struct) compound).getArg(index).getTerm());
	}

	@Override
	public boolean isAtom(Object term) {
		return ((Term) term).isAtom();
	}

	@Override
	public boolean isCompound(Object term) {
		return term instanceof Struct;
	}

	@Override
	public boolean isDouble(Object term) {
		return term instanceof alice.tuprolog.Double;
	}

	@Override
	public boolean isInteger(Object term) {
		return term instanceof Int;
	}

}
