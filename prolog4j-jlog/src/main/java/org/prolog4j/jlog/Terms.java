package org.prolog4j.jlog;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.prolog4j.Compound;
import org.prolog4j.annotations.Goal;

import ubc.cs.JLog.Terms.jAtom;
import ubc.cs.JLog.Terms.jCompoundTerm;
import ubc.cs.JLog.Terms.jInteger;
import ubc.cs.JLog.Terms.jList;
import ubc.cs.JLog.Terms.jListPair;
import ubc.cs.JLog.Terms.jNullList;
import ubc.cs.JLog.Terms.jPredicate;
import ubc.cs.JLog.Terms.jReal;
import ubc.cs.JLog.Terms.jTerm;
import ubc.cs.JLog.Terms.jVariable;

/**
 * Utility class for performing transformations between POJOs and terms.
 * Intended for internal use from within Prolog4J-JLog.
 */
@SuppressWarnings("unchecked")
final class Terms {

	/**
	 * Private constructor for disallowing instantiation of this utility class.
	 */
	private Terms() {
	}

//	static jTerm term(String repr) {
//		try {
//			Parser p = new Parser(repr);
//			return p.nextTerm(false);
//		} catch (InvalidTermException e) {
//			throw new RuntimeException(repr);
//		}
//	}

	static jInteger toTerm(Integer i) {
		return new jInteger(i);
	}

	static Integer toInteger(jTerm t) {
		return ((jInteger) t).getIntegerValue();
	}

	static jTerm toTerm(Float f) {
		return new jReal(f);
	}

	static Float toFloat(jTerm t) {
		return ((jReal) t).getRealValue();
	}

	static jTerm toTerm(String s) {
		return s == null ? jNullList.NULL_LIST : new jAtom(s);
	}

	static String toString(jTerm t) {
		if (t instanceof jAtom)
			return ((jAtom) t).getName();
		return null;
	}

	static jTerm toTerm(Object o) {
		if (o == null) {
			return new jVariable();
		}
		if (o instanceof jTerm) {
			return (jTerm) o;
		}
		if (o instanceof Integer) {
			return toTerm((Integer) o);
		}
		if (o instanceof Long) {
			return toTerm((Long) o);
		}
		if (o instanceof Float) {
			return toTerm((Float) o);
		}
		if (o instanceof Double) {
			return toTerm((Double) o);
		}
		if (o instanceof String) {
			return toTerm((String) o);
		}
		if (o instanceof Object[]) {
			return toTerm((Object[]) o);
		}
		if (o instanceof List) {
			return toTerm((List) o);
		}
		if (o instanceof Compound) {
			Compound c = (Compound) o;
			jCompoundTerm ct = new jCompoundTerm(c.getArity());
			for (int i = 0; i < c.getArity(); ++i) {
				ct.addTerm(toTerm(c.getArg(i)));
			}
			return new jPredicate(c.getFunctor(), ct);
		}
		Class<?> c = o.getClass();
		if (c.isAnnotationPresent(org.prolog4j.annotations.Term.class)) {
			return toTerm(o, c);
		}

		throw new ClassCastException();
		// return toTerm(o.toString());
	}

	private static jTerm toTerm(Object o, Class<?> c) {
		// Constructor[] ctrs = c.getConstructors();
		// Constructor ctr = null;
		// for (int i = 0; i < ctrs.length; ++i) {
		// ctr = ctrs[i];
		// if (ctr.isAnnotationPresent(Rule.class))
		// break;
		// }
		// Struct classType = type(ctr);
		// StringBuilder sb = new StringBuilder("class('").
		// append(c.getCanonicalName()).
		// append("', ").
		// append(classType.toString()).
		// append(", FieldNames, _, _).");
		// Struct fieldNames;
		// block: try {
		// SolveInfo sInfo = Prover.GLOBAL.engine.solve(sb.toString());
		// if (!sInfo.isSuccess())
		// break block;
		// fieldNames = (Struct) sInfo.getVarValue("FieldNames");
		// } catch (MalformedGoalException e1) {
		// e1.printStackTrace();
		// } catch (NoSolutionException e) {
		// e.printStackTrace();
		// }
		List<Field> fields = new LinkedList<Field>();
		Class cc = c;
		while (cc != Object.class) {
			fields.addAll(0, Arrays.asList(cc.getDeclaredFields()));
			cc = cc.getSuperclass();
		}
		// Term[] args = new Term[fields.size()];
		ArrayList<jTerm> args2 = new ArrayList<jTerm>(fields.size());
		try {
			for (int i = 0; i < fields.size(); ++i) {
				Field field = fields.get(i);
				if (Modifier.isStatic(field.getModifiers()))
					continue;
				field.setAccessible(true);
				// args[i] = toTerm(field.get(o));
				args2.add(toTerm(field.get(o)));
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		jCompoundTerm args3 = new jCompoundTerm(args2.size());
		for (jTerm t: args2)
			args3.addTerm(t);
		return new jPredicate(c.getName(), args3);
	}

	// TODO
	static <A> A toObject(jTerm t, Class<A> type) {
		if (t instanceof jVariable) {
			if (!((jVariable) t).isBound())
				return null;
			t = t.getTerm();
		}
		if (t instanceof jInteger)
			return (A) toInteger(t);
		if (t instanceof jReal)
			return (A) toFloat(t);
		if (t instanceof jList) {
			if (type.isArray())
				return (A) toArray(t, type.getComponentType());
			if (List.class.isAssignableFrom(type))
				return (A) toList(t);
		}
		if (t instanceof jAtom)
			return (A) toString(t);
		if (t instanceof jPredicate) {
			jPredicate s = (jPredicate) t;
			int argNo = s.getArity();
			String className = s.getName();
			try {
				Class<?> c = Class.forName(className);
				Constructor<A>[] ctrs = (Constructor<A>[]) c.getConstructors();
//				constructorLoop:
				for (Constructor<A> ctr : ctrs) {
					if (!ctr.isAnnotationPresent(Goal.class))
						continue;
					Class[] parameterTypes = ctr.getParameterTypes();
					if (parameterTypes.length != argNo)
						continue;
					Object[] args = new Object[argNo];
					jCompoundTerm arguments = s.getArguments();
					for (int i = 0; i < argNo; ++i)
						args[i] = toObject(arguments.elementAt(i), parameterTypes[i]);
					// for (int i = 0; i < argNo; ++i)
					// if (args[i] != null &&
					// !parameterTypes[i].isAssignableFrom(args[i].getClass()))
					// {
					// continue constructorLoop;
					// }
					try {
						return ctr.newInstance(args);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
						throw new RuntimeException(
								"Cannot convert to any POJO: " + t);
					} catch (InstantiationException e) {
						e.printStackTrace();
						throw new RuntimeException(
								"Cannot convert to any POJO: " + t);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
						throw new RuntimeException(
								"Cannot convert to any POJO: " + t);
					} catch (InvocationTargetException e) {
						e.printStackTrace();
						throw new RuntimeException(
								"Cannot convert to any POJO: " + t);
					}
					// break;
				}
			} catch (ClassNotFoundException e) {
				// return (A) new Compound(className, args);
			}
		}
		throw new RuntimeException("Cannot convert to any POJO: " + t);
	}

	static <A> A toObject(jTerm t) {
		if (t instanceof jVariable) {
			if (!((jVariable) t).isBound())
				return null;
			t = t.getTerm();
		}
		if (t instanceof jInteger)
			return (A) toInteger(t);
		if (t instanceof jReal)
			return (A) toFloat(t);
		if (t instanceof jAtom)
			return (A) toString(t);
		if (t instanceof jList)
			return (A) toArray(t);
		if (t instanceof jPredicate) {
			jPredicate s = (jPredicate) t;
			String className = s.getName();
			try {
				return (A) toObject(t, Class.forName(className));
			} catch (ClassNotFoundException e) {
				return (A) t;
			}
		}
		throw new RuntimeException("Cannot convert to any POJO: " + t);
	}

	static <E> jTerm toTerm(List<E> list) {
		jList pList = jNullList.NULL_LIST;
		ListIterator<E> it = list.listIterator(list.size());
		while (it.hasPrevious())
			pList = new jListPair(toTerm(it.previous()), pList);
		return pList;
	}

	static jTerm toTerm(Object[] array) {
		jList pList = jNullList.NULL_LIST;
		for (int i = array.length - 1; i >= 0; --i)
			pList = new jListPair(toTerm(array[i]), pList);
		return pList;
	}

	static Object[] toArray(jTerm tList) {
		jList list = (jList) tList;
		int length = listSize(list);
		Object[] array = new Object[length];
		for (int i = 0; i < length; ++i) {
			jListPair listPair = (jListPair) list;
			jTerm t = listPair.getHead().getTerm();
			array[i] = toObject(t);
			list = (jList) listPair.getTail().getTerm();
		}
		return array;
	}

	static <A> A[] toArray(jTerm tList, Class<A> componentType) {
		jList list = (jList) tList;
		int length = listSize(list);
		A[] array = (A[]) Array.newInstance(componentType, length);

		for (int i = 0; i < length; ++i) {
			jListPair listPair = (jListPair) list;
			jTerm t = listPair.getHead().getTerm();
			array[i] = toObject(t, componentType);
			list = (jList) listPair.getTail().getTerm();
		}
		return array;
	}

	static List<?> toList(jTerm tList) {
		jList list = (jList) tList;
		int length = listSize(list);
		List aList = new ArrayList(length);
		for (int i = 0; i < length; ++i) {
			jListPair listPair = (jListPair) list;
			aList.add(toObject(listPair.getHead().getTerm()));
			list = (jList) listPair.getTail().getTerm();
		}
		return aList;
	}
	
	static boolean isList(jTerm term) {
		return term instanceof jList;
	}

	static int listSize(jList list) {
		int size = 0;
		while (list != jNullList.NULL_LIST) {
			++size;
			list = (jList) ((jListPair) list).getTail().getTerm();
		}
		return size;
	}

}
