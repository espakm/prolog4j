/*
 * Copyright (c) 2004-2005 SLF4J.ORG
 * Copyright (c) 2004-2005 QOS.ch
 *
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute, and/or sell copies of  the Software, and to permit persons
 * to whom  the Software is furnished  to do so, provided  that the above
 * copyright notice(s) and this permission notice appear in all copies of
 * the  Software and  that both  the above  copyright notice(s)  and this
 * permission notice appear in supporting documentation.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR  A PARTICULAR PURPOSE AND NONINFRINGEMENT
 * OF  THIRD PARTY  RIGHTS. IN  NO EVENT  SHALL THE  COPYRIGHT  HOLDER OR
 * HOLDERS  INCLUDED IN  THIS  NOTICE BE  LIABLE  FOR ANY  CLAIM, OR  ANY
 * SPECIAL INDIRECT  OR CONSEQUENTIAL DAMAGES, OR  ANY DAMAGES WHATSOEVER
 * RESULTING FROM LOSS  OF USE, DATA OR PROFITS, WHETHER  IN AN ACTION OF
 * CONTRACT, NEGLIGENCE  OR OTHER TORTIOUS  ACTION, ARISING OUT OF  OR IN
 * CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 * Except as  contained in  this notice, the  name of a  copyright holder
 * shall not be used in advertising or otherwise to promote the sale, use
 * or other dealings in this Software without prior written authorization
 * of the copyright holder.
 *
 */

package org.prolog4j.spi;

import org.prolog4j.IProverFactory;

/**
 * An internal interface which helps the static
 * {@link org.prolog4j.ProverFactory} class bind with the appropriate
 * {@link IProverFactory} instance.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public interface ProverFactoryBinder {

	/**
	 * Return the instance of {@link IProverFactory} that
	 * {@link org.prolog4j.ProverFactory} class should bind to.
	 * 
	 * @return the instance of {@link IProverFactory} that
	 *         {@link org.prolog4j.ProverFactory} class should bind to.
	 */
	public IProverFactory getProverFactory();

	/**
	 * The String form of the {@link IProverFactory} object that this
	 * <code>ProverFactoryBinder</code> instance is <em>intended</em> to return.
	 * 
	 * <p>
	 * This method allows the developer to interrogate this binder's intention
	 * which may be different from the {@link IProverFactory} instance it is
	 * able to yield in practice. The discrepency should only occur in case of
	 * errors.
	 * 
	 * @return the class name of the intended {@link IProverFactory} instance
	 */
	public String getProverFactoryClassName();

}
