package org.prolog4j;

import java.io.Serializable;

/**
 * Represents a Prolog compound term. Prolog compound terms which cannot be
 * transformed to an instance of a term class will be converted to a
 * <tt>Compound</tt>.
 */
public class Compound implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String functor;
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
	Object[] getArgs() {
		return args;
	}

	/**
	 * Returns the n-th argument of the compound term. Numbering starts from
	 * <tt>0</tt>.
	 * 
	 * @return the n-th argument of the compound term
	 */
	public Object getArg(int index) {
		return args[index];
	}
}
