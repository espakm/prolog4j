package org.prolog4j;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Represents a Prolog compound term. Prolog compound terms which cannot be
 * transformed to an instance of a term class will be converted to a
 * <tt>Compound</tt>.
 */
public class Compound implements Serializable {

	/**
	 * Class version for serialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The functor of the compound term.
	 */
	private final String functor;
	
	/**
	 * The arguments of the compound term.
	 */
	private final Object[] args;

	/**
	 * Creates a compound term. A compound term must have at least one argument.
	 * Please note, that the array is not cloned.
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
