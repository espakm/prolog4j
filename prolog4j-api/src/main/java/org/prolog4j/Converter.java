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
	 * @param <R> the type 
	 * @param object the object to convert
	 * @param to the type to convert to
	 * @return the result of the conversion or <code>null</code> if the object
	 *         cannot be converted
	 */
	@SuppressWarnings("unchecked")
	public <R> R convert(T object, Class<R> to) {
		return to.cast(convert(object));
	}
	
}
