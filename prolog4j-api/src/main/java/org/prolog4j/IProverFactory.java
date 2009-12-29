/*.
 * Copyright (c) 2004-2007 QOS.ch
 * All rights reserved.
 *.
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *.
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *.
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
 * <code>IProverFactory</code> instances manufacture {@link Prover} instances by
 * name.
 * 
 * <p>
 * Most users retrieve {@link Prover} instances through the static
 * {@link ProverFactory#getProver(String)} method. An instance of of this
 * interface is bound internally with {@link ProverFactory} class at compile
 * time.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public interface IProverFactory {

	/**
	 * Creates a new prover.
	 * 
	 * @return a new prover
	 */
	Prover getProver();

	/**
	 * Return an appropriate {@link Prover} instance as specified by the
	 * <code>name</code> parameter.
	 * 
	 * <p>
	 * If the name parameter is equal to {@link Prover#ROOT_PROVER_NAME}, that
	 * is the string value "ROOT" (case insensitive), then the root prover of
	 * the underlying Prolog system is returned. TODO
	 * 
	 * <p>
	 * Null-valued name arguments are considered invalid.
	 * 
	 * <p>
	 * Certain extremely simple Prolog systems, may always return the same
	 * prover instance regardless of the requested name.
	 * 
	 * @param name
	 *            the name of the Prover to return
	 * @return a prover instance with the specified name
	 */
	Prover getProver(String name);

	/**
	 * Returns the conversion policy used by provers at default. The 
	 * modification of this policy effects all provers. The policy can also be
	 * customized on a per prover base by getting their own policy.
	 * 
	 * @return the default conversion policy
	 */
	ConversionPolicy getConversionPolicy();

}
