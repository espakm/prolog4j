package org.prolog4j;

/**
 * Converter<T> instances can convert objects of type T to another object.
 * 
 * @param <T> the type of the objects to convert
 */
public abstract class Converter<T> {

	/**
	 * Converts an object to another one. If the conversion is not applicable
	 * then it returns <code>null</code>.
	 * 
	 * @param object the object to convert
	 * @return the result of the conversion or <code>null</code> if the object
	 *         cannot be converted
	 */
	public abstract Object convert(T object);
	
	/**
	 * Converts an object to another one of a specific type. If the conversion
	 * is not applicable then it returns <code>null</code>.
	 * 
	 * <R> the type 
	 * @param object the object to convert
	 * @return the result of the conversion or <code>null</code> if the object
	 *         cannot be converted
	 */
	public <R> R convert(T object, Class<R> to) {
		if (to == Object.class) {
			return (R) convert(object);
		}
		return null;
	}
	
}
