/*
 * Copyright 2010 by Miklós Espák <espakm@gmail.com>
 * 
 * This file is part of Prolog4J.
 * 
 * Prolog4J is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Prolog4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Prolog4J.  If not, see <http://www.gnu.org/licenses/>.
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
		if (to == Object.class) {
			return (R) convert(object);
		}
		return null;
	}
	
}
