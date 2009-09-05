package org.prolog4j.jTrolog;

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

import jTrolog.parser.Parser;
import jTrolog.terms.Int;
import jTrolog.terms.Struct;
import jTrolog.terms.StructAtom;
import jTrolog.terms.Term;
import jTrolog.terms.Var;
import jTrolog.errors.InvalidTermException;

/**
 * Utility class for performing transformations between POJOs and terms.
 * Intended for internal use from within Prolog4J-tuProlog.
 */
class Terms {

	static Term term(String repr) {
		try {
			Parser p = new Parser(repr);
			return p.nextTerm(false);
		} catch (InvalidTermException e) {
			throw new RuntimeException(repr);
		}
	}

	static Int toTerm(Integer i) {
		return new Int(i);
	}

	static Integer toInteger(Term t) {
		return ((Int) t).intValue();
	}

	static Term toTerm(Long l) {
		return new jTrolog.terms.Long(l);
	}

	static Long toLong(Term t) {
		return ((jTrolog.terms.Long) t).longValue();
	}

	static Term toTerm(Float f) {
		return new jTrolog.terms.Float(f);
	}

	static Float toFloat(Term t) {
		return ((jTrolog.terms.Float) t).floatValue();
	}

	static Term toTerm(Double d) {
		return new jTrolog.terms.Double(d);
	}

	static Double toDouble(Term t) {
		return ((jTrolog.terms.Double) t).doubleValue();
	}

	static Term toTerm(String s) {
		return s == null ? Term.emptyList : new StructAtom(s);
	}

	static String toString(Term t) {
		if (t instanceof StructAtom)
			return ((Struct) t).name;
		return null;
	}

	static Term toTerm(Object o) {
		if (o == null)
			return new Var("_", 1);
		if (o instanceof Term)
			return (Term) o;
		if (o instanceof Integer)
			return toTerm((Integer) o);
		if (o instanceof Long)
			return toTerm((Long) o);
		if (o instanceof Float)
			return toTerm((Float) o);
		if (o instanceof Double)
			return toTerm((Double) o);
		if (o instanceof String)
			return toTerm((String) o);
		if (o instanceof Object[])
			return toTerm((Object[]) o);
		if (o instanceof List)
			return toTerm((List) o);
		if (o instanceof Compound) {
			Compound c = (Compound) o;
			Term[] args = new Term[c.getArity()];
			for (int i = 0; i < args.length; ++i)
				args[i] = toTerm(c.getArg(i));
			return new Struct(c.getFunctor(), args);
		}
		Class<?> c = o.getClass();
		if (c.isAnnotationPresent(org.prolog4j.annotations.Term.class))
			return toTerm(o, c);

		throw new ClassCastException();
		// return toTerm(o.toString());
	}

	private static Term toTerm(Object o, Class<?> c) {
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
		ArrayList<Term> args2 = new ArrayList<Term>(fields.size());
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
		return new Struct(c.getName(), args2.toArray(new Term[0]));
	}

	// TODO
	static <A> A toObject(Term t, Class<A> type) {
		if (t instanceof Var)
			return null;
		if (t instanceof Int)
			return (A) toInteger(t);
		if (t instanceof jTrolog.terms.Long)
			return (A) toLong(t);
		if (t instanceof jTrolog.terms.Float)
			return (A) toFloat(t);
		if (t instanceof jTrolog.terms.Double)
			return (A) toDouble(t);
		if (isList(t)) {
			if (type.isArray())
				return (A) toArray(t, type.getComponentType());
			if (List.class.isAssignableFrom(type))
				return (A) toList(t);
		}
		if (t instanceof StructAtom)
			return (A) toString(t);
		if (t instanceof Struct) {
			Struct s = (Struct) t;
			int argNo = s.arity;
			String className = s.name;
			try {
				Class<?> c = Class.forName(className);
				Constructor<A>[] ctrs = (Constructor<A>[]) c.getConstructors();
				constructorLoop: for (Constructor<A> ctr : ctrs) {
					if (!ctr.isAnnotationPresent(Goal.class))
						continue;
					Class[] parameterTypes = ctr.getParameterTypes();
					if (parameterTypes.length != argNo)
						continue;
					Object[] args = new Object[argNo];
					for (int i = 0; i < argNo; ++i)
						args[i] = toObject(s.getArg(i), parameterTypes[i]);
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

	static <A> A toObject(Term t) {
		if (t instanceof Var)
			return null;
		if (t instanceof Int)
			return (A) toInteger(t);
		if (t instanceof jTrolog.terms.Long)
			return (A) toLong(t);
		if (t instanceof jTrolog.terms.Float)
			return (A) toFloat(t);
		if (t instanceof jTrolog.terms.Double)
			return (A) toDouble(t);
		if (t instanceof StructAtom)
			return (A) toString(t);
		if (isList(t))
			return (A) toArray(t);
		if (t instanceof Struct) {
			Struct s = (Struct) t;
			String className = s.name;
			try {
				return (A) toObject(t, Class.forName(className));
			} catch (ClassNotFoundException e) {
				return (A) t;
			}
		}
		throw new RuntimeException("Cannot convert to any POJO: " + t);
	}

	static <E> Term toTerm(List<E> list) {
		Struct pList = Term.emptyList;
		ListIterator<E> it = list.listIterator(list.size());
		while (it.hasPrevious())
			pList = new Struct(".", new Term[]{toTerm(it.previous()), pList});
		return pList;
	}

	static Term toTerm(Object[] array) {
		Struct pList = Term.emptyList;
		for (int i = array.length - 1; i >= 0; --i)
			pList = new Struct(".", new Term[]{toTerm(array[i]), pList});
		return pList;
	}

	static Object[] toArray(Term tList) {
		Struct list = (Struct) tList;
		int length = listSize(list);
		Object[] array = new Object[length];
		for (int i = 0; i < length; ++i) {
			Term t = list.getArg(0);
			array[i] = toObject(t);
			list = (Struct) list.getArg(1);
		}
		return array;
	}

	static <A> A[] toArray(Term tList, Class<A> componentType) {
		Struct list = (Struct) tList;
		int length = listSize(list);
		A[] array = (A[]) Array.newInstance(componentType, length);

		for (int i = 0; i < length; ++i) {
			Term t = list.getArg(0);
			array[i] = toObject(t, componentType);
			list = (Struct) list.getArg(1);
		}
		return array;
	}

	static List<?> toList(Term tList) {
		Struct list = (Struct) tList;
		int length = listSize(list);
		List aList = new ArrayList(length);
		for (int i = 0; i < length; ++i) {
			aList.add(toObject(list.getArg(0)));
			list = (Struct) list.getArg(1);
		}
		return aList;
	}
	
	static boolean isList(Term term) {
		if (!(term instanceof Struct))
			return false;
		Struct list = (Struct) term;
		while (list != Term.emptyList) {
			if (!list.name.equals(".") || list.arity != 2 || !(list.getArg(1) instanceof Struct))
				return false;
			list = (Struct) list.getArg(1);
		}
		return true;
	}

	static int listSize(Struct list) {
		int L = 0;
		while (list != Term.emptyList) {
			if (!list.name.equals(".") || list.arity != 2 || !(list.getArg(1) instanceof Struct))
				throw new RuntimeException("It's not a list!");
			++L;
			list = (Struct) list.getArg(1);
		}
		return L;
	}

}
