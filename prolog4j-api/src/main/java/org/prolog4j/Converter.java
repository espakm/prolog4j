package org.prolog4j;

/**
 * Converter<T> instances can convert objects of type T to another object.
 * 
 * @param <T> the type of the objects to convert
 */
public interface Converter<T> {

	/**
	 * Converts an object to another one. If the conversion is not appliable
	 * then it returns <code>null</code>.
	 * 
	 * @param object the object to convert
	 * @return the result of the conversion or <code>null</code> if the object
	 *         cannot be converted
	 */
	Object convert(T object);
	
}
