package org.prolog4j;

import java.util.HashMap;
import java.util.LinkedList;

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
	private LinkedList<Object> termPatterns;
	
	/**
	 * Stores the converters for transforming regular objects to terms. The keys
	 * of the map are the patterns. If a pattern matches an object then its 
	 * converter can be applied.
	 */
	@SuppressWarnings("unchecked")
	private HashMap<Object, Converter> objectConverters;
	
	/**
	 * Represents the reverse insertion order of the keys to 
	 * {@link #objectConverters}.
	 * (Note that LinkedHashMap is not suitable for two reasons. At first, it
	 * cannot be iterated through reversely, at second the insertion order is
	 * not affected if a key is re-inserted into the map.)
	 */
	private LinkedList<Object> objectPatterns;
	
	/**
	 * Constructs an empty <code>ConversionPolicy</code>.
	 */
	@SuppressWarnings("unchecked")
	protected ConversionPolicy() {
		termConverters = new HashMap<Object, Converter>();
		termPatterns = new LinkedList<Object>();
		objectConverters = new HashMap<Object, Converter>();
		objectPatterns = new LinkedList<Object>();
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
	public <T> void addTermConverter(Class<T> pattern, Converter<T> converter) {
		termConverters.put(pattern, converter);
		termPatterns.addFirst(pattern);
	}
	
	/**
	 * Registers a new term converter into the policy. The converter will have
	 * priority over other converters whose pattern matches the same terms.
	 * 
	 * The matching of the pattern is decided by 
	 * {@link Prover#match(Object, Object)}.
	 * 
	 * @param <T> the type of the objects to convert
	 * @param pattern the pattern to select the converter to use later
	 * @param converter the converter
	 */
	public <T> void addTermConverter(T pattern, Converter<T> converter) {
		termConverters.put(pattern, converter);
		termPatterns.addFirst(pattern);
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
		objectPatterns.addFirst(pattern);
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
	public <T> void addObjectConverter(T pattern, Converter<T> converter) {
		objectConverters.put(pattern, converter);
		objectPatterns.addFirst(pattern);
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
		for (Object pattern: termPatterns) {
			if (pattern instanceof Class && ((Class) pattern).isAssignableFrom(term.getClass())) {
				Object result = termConverters.get(pattern).convert(term);
				if (result != null) {
					return result;
				}
			}
		}
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
		for (Object pattern: termPatterns) {
			if (pattern instanceof Class && ((Class) pattern).isAssignableFrom(term.getClass())) {
				T result = (T) termConverters.get(pattern).convert(term, type);
				if (result != null) {
					return result;
				}
			}
		}
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
		for (Object pattern: objectPatterns) {
			if ((pattern instanceof Class) 
					&& ((Class) pattern).isAssignableFrom(objectClass)) {
				return objectConverters.get(pattern).convert(object);
			}
		}
//		do {
//			Converter converter = objectConverters.get(objectClass);
//			if (converter != null) {
//				return converter.convert(object);
//			}
//			for (Class interf: objectClass.getInterfaces()) {
//				converter = objectConverters.get(interf);
//				if (converter != null) {
//					return converter.convert(object);
//				}
//			}	
//			objectClass = objectClass.getSuperclass();
//		} while (objectClass != null);
		return null;
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

	public abstract Object compound(String name, Object... args);
	
}
