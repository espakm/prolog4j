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
package org.prolog4j.jlog;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.prolog4j.Compound;
import org.prolog4j.ConversionPolicy;
import org.prolog4j.Converter;

import ubc.cs.JLog.Foundation.jUnifiedVector;
import ubc.cs.JLog.Terms.iName;
import ubc.cs.JLog.Terms.iNameArity;
import ubc.cs.JLog.Terms.jAtom;
import ubc.cs.JLog.Terms.jCompoundTerm;
import ubc.cs.JLog.Terms.jInteger;
import ubc.cs.JLog.Terms.jList;
import ubc.cs.JLog.Terms.jListPair;
import ubc.cs.JLog.Terms.jNullList;
import ubc.cs.JLog.Terms.jPredicate;
import ubc.cs.JLog.Terms.jReal;
import ubc.cs.JLog.Terms.jTerm;

/**
 * JLog implementation of the conversion policy.
 */
public class JLogConversionPolicy extends ConversionPolicy {

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
	
	/** Converts an jInteger term to an Integer object. */
	private static final Converter<jInteger> INT_TERM_CONVERTER = new Converter<jInteger>() {
		@Override
		public Object convert(jInteger value) {
			return value.getIntegerValue();
		}
	};

	/** 
	 * Converts an jReal term to a Float object. 
	 * Note that, although JLog does not support double values (only float), 
	 * here a double value is returned. This is for compatibility with other
	 * implementations.
	 */
	private static final Converter<jReal> FLOAT_TERM_CONVERTER = new Converter<jReal>() {
		@Override
		public Object convert(jReal value) {
			return new Double(value.getRealValue());
		}
	};
	
	/** Converts an jAtom term to a String object. */
	private static final Converter<jAtom> ATOM_TERM_CONVERTER = new Converter<jAtom>() {
		@Override
		public Object convert(jAtom value) {
			return value.getName();
		}
	};

	/**
	 * Constructs a conversion policy for JLog.
	 */
	public JLogConversionPolicy() {
		super();
		addObjectConverter(Long.class, LONG_CONVERTER);
		addObjectConverter(Float.class, FLOAT_CONVERTER);
		addObjectConverter(Double.class, DOUBLE_CONVERTER);
		addObjectConverter(Integer.class, INTEGER_CONVERTER);
		addObjectConverter(String.class, STRING_CONVERTER);
		addObjectConverter(Object[].class, new Converter<Object[]>() {
			@Override
			public Object convert(Object[] array) {
				jList pList = jNullList.NULL_LIST;
				for (int i = array.length - 1; i >= 0; --i) {
					pList = new jListPair((jTerm) convertObject(array[i]), pList);
				}
				return pList;
			}
		});
		addObjectConverter(List.class, new Converter<List>() {
			@Override
			public Object convert(List list) {
				jList pList = jNullList.NULL_LIST;
				ListIterator<?> it = list.listIterator(list.size());
				while (it.hasPrevious()) {
					pList = new jListPair((jTerm) convertObject(it
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
				Vector<jTerm> tArgs = new Vector<jTerm>(value.getArity());
				for (int i = 0; i < args.length; ++i) {
					tArgs.add((jTerm) convertObject(args[i]));
				}
				return new jPredicate(functor, new jCompoundTerm(tArgs));
			}
		});
		addObjectConverter(jTerm.class, new Converter<jTerm>() {
			@Override
			public Object convert(jTerm value) {
				return value;
			}
		});

		addTermConverter(jInteger.class, INT_TERM_CONVERTER);
		addTermConverter(jReal.class, FLOAT_TERM_CONVERTER);
		addTermConverter(jAtom.class, ATOM_TERM_CONVERTER);
		addTermConverter(jList.class, new Converter<jList>() {
			@Override
			public Object convert(jList value) {
				int length = listSize(value);
				Object[] array = new Object[length];
				for (int i = 0; i < length; ++i) {
					jListPair listPair = (jListPair) value;
					array[i] = convertTerm((listPair.getHead().getTerm()));
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
						list.add(convertTerm((listPair.getHead().getTerm())));
						value = (jList) listPair.getTail().getTerm();
					}
					return (R) list;
				}
				return null;
			}
		});
		addTermConverter(jPredicate.class, new Converter<jPredicate>() {
			@Override
			public Object convert(jPredicate value) {
				int length = value.getArity();
				Object[] array = new Object[length];
				jCompoundTerm arguments = value.getArguments();
				for (int i = 0; i < length; ++i) {
					array[i] = convertTerm(arguments.elementAt(i).getTerm());
				}
				return new Compound(value.getName(), array);
			}
		});
	}
	
	@Override
	public boolean match(Object term1, Object term2) {
		if (term1 instanceof jTerm && term2 instanceof jTerm) {
			jUnifiedVector v = new jUnifiedVector();
			boolean match = ((jTerm) term1).unify((jTerm) term2, v);
			v.restoreVariables();
			return match;
		}
		return false;
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

	@Override
	public Object term(int value) {
		return new jInteger(value);
	}

	@Override
	public Object term(double value) {
		return new jReal((float) value);
	}

	@Override
	public Object term(String name) {
		return new jAtom(name);
	}

	@Override
	public Object term(String name, Object... args) {
		if (args.length == 0) {
			return new jAtom(name);
		}
		Vector tArgs = new Vector(args.length);
		for (Object arg: args) {
			tArgs.add(convertObject(arg));
		}
		return new jPredicate(name, new jCompoundTerm(tArgs));
	}

	@Override
	public int intValue(Object term) {
		return ((jInteger) term).getIntegerValue();
	}

	@Override
	public double doubleValue(Object term) {
		return ((jReal) term).getRealValue();
	}

	@Override
	protected String getName(Object compound) {
		return ((iName) compound).getName();
	}

	@Override
	protected int getArity(Object compound) {
		return ((iNameArity) compound).getArity();
	}

	@Override
	protected Object getArg(Object compound, int index) {
		return convertTerm(((jPredicate) compound).getArguments().elementAt(index));
	}

	@Override
	public boolean isInteger(Object term) {
		return term instanceof jInteger;
	}

	@Override
	public boolean isDouble(Object term) {
		return term instanceof jReal;
	}

	@Override
	public boolean isAtom(Object term) {
		return term instanceof jAtom;
	}

	@Override
	public boolean isCompound(Object term) {
		return term instanceof iNameArity;
	}

//	@Override
//	public org.prolog4j.Term pattern(String term) {
//		// TODO
//		throw new UnsupportedOperationException();
//	}

}
