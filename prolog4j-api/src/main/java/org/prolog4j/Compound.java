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

import java.io.Serializable;
import java.util.Arrays;

/**
 * Represents a Prolog compound term. Prolog compound terms for which no default
 * way of conversion exists will be converted to a <tt>Compound</tt> object.
 */
public class Compound implements Serializable {

	/** Class version for serialization. */
	private static final long serialVersionUID = 1L;

	/** The functor of the compound term. */
	private final String functor;
	
	/** The arguments of the compound term. */
	private final Object[] args;

	/**
	 * Constructs a compound term. A compound term must have at least one 
	 * argument. Please note, that the array is not cloned.
	 * 
	 * @param functor
	 *            the functor of the compound term
	 * @param args
	 *            the arguments of the compound term
	 */
	public Compound(String functor, Object... args) {
		this.functor = functor;
		this.args = args;
	}

	/**
	 * Returns the functor of the compound term.
	 * 
	 * @return the functor of the compound term
	 */
	public String getFunctor() {
		return functor;
	}

	/**
	 * Returns the arity (the number of arguments) of the compound term.
	 * 
	 * @return the arity of the compound term
	 */
	public int getArity() {
		return args.length;
	}

	/**
	 * Returns the arguments of the compound term.
	 * 
	 * @return the arguments of the compound term
	 */
	public Object[] getArgs() {
		return args;
	}

	/**
	 * Returns the index-th argument of the compound term. Numbering starts from
	 * <tt>0</tt>.
	 * 
	 * @param index
	 *            the number of the required argument
	 * @return the index-th argument of the compound term
	 */
	public Object getArg(int index) {
		return args[index];
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(args);
		result = prime * result + functor.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Compound)) {
			return false;
		}
		Compound other = (Compound) obj;
		return functor.equals(other.functor) && Arrays.equals(args, other.args);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(functor);
		builder.append('(').append(args[0]);
		for (int i = 1; i < args.length; ++i) {
			builder.append(", ").append(args[i]);
		}
		return builder.append(')').toString();
	}

}
