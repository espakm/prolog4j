package org.prolog4j;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * An instance of this class represents how terms are converted to regular Java
 * objects (POJOs) and vice versa. The transformation is performed by 
 * converters. The implementations of the Prolog4J API should provide a fully 
 * functional instance of this class.
 * 
 * TODO
 * 
 * @see Converter
 */
public abstract class ConversionPolicy {

	/**
	 * Stores the converters for transforming terms to regular objects. The keys
	 * of the map are the patterns. If a pattern matches a term then its 
	 * converter can be applied.
	 */
	@SuppressWarnings("unchecked")
	private HashMap<Object, Converter> termConverters;
	
	/**
	 * Represents the reverse insertion order of the keys to 
	 * {@link #termConverters}.
	 * (Note that LinkedHashMap is not suitable for two reasons. At first, it
	 * cannot be iterated through reversely, at second the insertion order is
	 * not affected if a key is re-inserted into the map.)
	 */
	private Map<String, Converter<Object>> termPatterns;

	/**
	 * Stores the converters for transforming regular objects to terms. The keys
	 * of the map are the patterns. If a pattern matches an object then its 
	 * converter can be applied.
	 */
	@SuppressWarnings("unchecked")
	private HashMap<Class, Converter> objectConverters;
	
	/**
	 * Constructs an empty <code>ConversionPolicy</code>.
	 */
	@SuppressWarnings("unchecked")
	protected ConversionPolicy() {
		termConverters = new HashMap<Object, Converter>();
		termPatterns = new HashMap<String, Converter<Object>>();
		objectConverters = new HashMap<Class, Converter>();
	}
	
	/**
	 * Registers a new term converter into the policy. The converter will have
	 * priority over other converters whose pattern matches the same objects.
	 * 
	 * The pattern matches a term if the class of the term is a subclass of
	 * the pattern.
	 * 
	 * @param <T> the type of the terms to convert
	 * @param pattern the pattern to select the converter to use later
	 * @param converter the converter
	 */
	protected <T> void addTermConverter(Class<T> pattern, Converter<T> converter) {
		termConverters.put(pattern, converter);
	}
	
	/**
	 * Registers a new term converter into the policy. The converter will have
	 * priority over other converters whose pattern matches the same terms.
	 * 
	 * The matching of the pattern is decided by 
	 * {@link Prover#match(Object, Object)}.
	 * 
	 * @param pattern the pattern to select the converter to use later
	 * @param converter the converter
	 */
	public void addTermConverter(String pattern, Converter<Object> converter) {
		StringBuilder sb = new StringBuilder();
		StringTokenizer st = new StringTokenizer(pattern, "(),");
		int i = 0;
		String functor = st.nextToken();
		while (st.hasMoreTokens()) {
			++i;
			st.nextToken();
		}
		String spec = sb.append(functor).append('/').append(i).toString();
		termPatterns.put(spec, converter);
	}

	/**
	 * Registers a new object converter into the policy. The converter will have
	 * priority over other converters whose pattern matches the same objects.
	 * 
	 * The pattern matches an object if the class of the object is a subclass of
	 * the pattern.
	 * 
	 * @param <T> the type of the objects to convert
	 * @param pattern the pattern to select the converter to use later
	 * @param converter the converter
	 */
	public <T> void addObjectConverter(Class<T> pattern, Converter<T> converter) {
		objectConverters.put(pattern, converter);
	}
	
	/**
	 * Converts a term to a regular Java object. If the term is <code>null
	 * </code> (that represents an unbound Prolog variable) then 
	 * <code>null</code> is returned.
	 * 
	 * The method starts to match the term with the patterns of the term 
	 * converters in their reverse insertion order. If the term matches one
	 * then it will be converted by the converter assigned to the pattern. If
	 * the converter returns <code>null</code> then another applicable converter
	 * will be looked for.
	 * 
	 * An exception is thrown if there is no applicable converter. (The 
	 * implementations of the Prolog4J API should prevent this situation.)
	 * 
	 * @param term the term to convert
	 * @return the result of the conversion
	 */
	@SuppressWarnings("unchecked")
	public Object convertTerm(Object term) {
		if (term == null) {
			return null;
		}
		if (isCompound(term)) {
			String functor = getName(term);
			StringBuilder specB = new StringBuilder(functor.length() + 2);
			String spec = specB.append(functor).append('/').append(getArity(term)).toString();
			Converter converter = termPatterns.get(spec);
			if (converter != null) {
				Object result = converter.convert(term);
				if (result != null) {
					return result;
				}
			}
		}
		Class termClass = term.getClass();
		do {
			Converter converter = termConverters.get(termClass);
			if (converter != null) {
				Object result = converter.convert(term);
				if (result != null) {
					return result;
				}
			}
			termClass = termClass.getSuperclass();
		} while (termClass != null);
		throw new RuntimeException("No suitable converter found for " + term);
	}

	/**
	 * Converts a term to a regular Java object. If the term is <code>null
	 * </code> (that represents an unbound Prolog variable) then 
	 * <code>null</code> is returned.
	 * 
	 * The method starts to match the term with the patterns of the term 
	 * converters in their reverse insertion order. If the term matches one
	 * then it will be converted by the converter assigned to the pattern. If
	 * the converter returns <code>null</code> then another applicable converter
	 * will be looked for.
	 * 
	 * An exception is thrown if there is no applicable converter. (The 
	 * implementations of the Prolog4J API should prevent this situation.)
	 * 
	 * @param <T> the type to convert to
	 * @param term the term to convert
	 * @param type the type to convert to
	 * @return the result of the conversion
	 */
	@SuppressWarnings("unchecked")
	public <T> T convertTerm(Object term, Class<T> type) {
		if (term == null) {
			return null;
		}
		Class termClass = term.getClass();
		do {
			Converter converter = termConverters.get(termClass);
			if (converter != null) {
				return (T) converter.convert(term, type);
			}
//			for (Class interf: termClass.getInterfaces()) {
//				converter = termConverters.get(interf);
//				if (converter != null) {
//					return converter.convert(term);
//				}
//			}
			termClass = termClass.getSuperclass();
		} while (termClass != null);
		throw new RuntimeException("No suitable converter found for " + term);
	}
	
	/**
	 * Converts a regular Java object to a term. The <code>null</code> value
	 * will be converted to <code>null</code> (that represents an unbound Prolog
	 * variable).
	 * 
	 * The method starts to match the class of the object with the patterns of
	 * the object converters in their reverse insertion order. If the class is
	 * the subclass of a pattern (equality is allowed) then the object will be
	 * converted by the converter assigned to the pattern. If the converter
	 * returns <code>null</code> then another applicable converter will be
	 * looked for.
	 * 
	 * An exception is thrown if there is no applicable converter. (The 
	 * implementations of the Prolog4J API should prevent this situation.)
	 * 
	 * @param object the object to convert
	 * @return the result of the conversion
	 */
	@SuppressWarnings("unchecked")
	public Object convertObject(Object object) {
		if (object == null) {
			return null;
		}
		Class objectClass = object.getClass();
		if (objectClass.isArray()) {
			Converter converter = objectConverters.get(Object[].class);
			return converter.convert(object);
		} else {
			do {
				Converter converter = objectConverters.get(objectClass);
				if (converter != null) {
					return converter.convert(object);
				}
				if (!objectClass.isArray()) {
					for (Class interf: objectClass.getInterfaces()) {
						converter = objectConverters.get(interf);
						if (converter != null) {
							return converter.convert(object);
						}
					}	
					objectClass = objectClass.getSuperclass();
				} else {
					// TODO It does not handle interfaces and dimensions correctly.
//					Class componentType = objectClass.getComponentType();
//					componentType = componentType.getSuperclass();
//					if (componentType == null) {
//						break;
//					}
//					objectClass = Array.newInstance(componentType, new int[]{0}).getClass();
				}
			} while (objectClass != null);
		}
		throw new RuntimeException("No suitable converter found for " + object);
	}

	/**
	 * Decides whether two terms match or not. The comparison is similar to
	 * {@link Object#equals(Object)} apart of that <code>null</code> matches any
	 * object.
	 * 
	 * @param term1 the first term
	 * @param term2 the second object
	 * @return <code>true</code> if the terms match, otherwise <code>false</code>
	 */
	public abstract boolean match(Object term1, Object term2);
	
	/**
	 * Determines whether the Prolog term is an integer value.
	 * 
	 * @param term the term
	 * @return <code>true</code> if the value is integer, otherwise 
	 * 		<code>false</code>
	 */
	public abstract boolean isInteger(Object term);

	/**
	 * Determines whether the Prolog term is a double value.
	 * 
	 * @param term the term
	 * @return <code>true</code> if the value is double, otherwise 
	 * 		<code>false</code>
	 */
	public abstract boolean isDouble(Object term);
	
	/**
	 * Determines whether the Prolog term is an atom.
	 * 
	 * @param term the term
	 * @return <code>true</code> if the value is integer, otherwise 
	 * 		<code>false</code>
	 */
	public abstract boolean isAtom(Object term);
	
	/**
	 * Determines whether the Prolog term is compound. Atoms are regarded as
	 * compound values with 0 arity.
	 * 
	 * @param term the term
	 * @return <code>true</code> if the value is compound, otherwise 
	 * 		<code>false</code>
	 */
	public abstract boolean isCompound(Object term);
	
	/**
	 * Creates a integer term according to the actual implementation.
	 * 
	 * @param value the integer value
	 * @return the created Prolog integer term
	 */
	public abstract Object term(int value);

//	public abstract Object term(long value);
//	public abstract Object term(float value);

	/**
	 * Creates a real term according to the actual implementation.
	 * 
	 * @param value the double value
	 * @return the created Prolog real term
	 */
	public abstract Object term(double value);

	/**
	 * Creates an atom according to the actual implementation.
	 * 
	 * @param name the name of the atom
	 * @return the created atom
	 */
	public abstract Object term(String name);

	/**
	 * Creates a compound term according to the actual implementation.
	 * 
	 * @param functor the functor of the compound term
	 * @param args the arguments of the compound term
	 * @return the created compound term
	 */
	public abstract Object term(String functor, Object... args);

//	public abstract Term pattern(String term);

	/**
	 * Converts an integer term to an int value.
	 * 
	 * @param term a term representing an integer value
	 * @return the int value of the term
	 */
	public abstract int intValue(Object term);
//	public abstract long longValue(Object term);
//	public abstract float floatValue(Object term);
	/**
	 * Converts a floating point term to a double value.
	 * 
	 * @param term a term representing a floating point value
	 * @return the double value of the term
	 */
	public abstract double doubleValue(Object term);
	
	/**
	 * Returns the functor of a compound term or the name of an atom.
	 * 
	 * @param compound the compound term or atom
	 * @return the functor of the term
	 */
	protected abstract String getName(Object compound);
	
	/**
	 * Returns the arity of a compound term or atom. For atoms it returns zero.
	 * 
	 * @param compound the compound term or atom
	 * @return the arity of the term
	 */
	protected abstract int getArity(Object compound);
	
	/**
	 * Returns an argument of a compound term. The numbering starts from zero.
	 * 
	 * @param compound the compound
	 * @param index the index of the argument (>= 0)
	 * @return the <tt>arg</tt>th argument of the compound
	 */
	protected abstract Object getArg(Object compound, int index);

}
