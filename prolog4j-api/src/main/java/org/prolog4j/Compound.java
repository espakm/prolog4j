package org.prolog4j;

import java.io.Serializable;

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
	public Compound(final String functor, final Object... args) {
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
	Object[] getArgs() {
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
	public Object getArg(final int index) {
		return args[index];
	}
}
