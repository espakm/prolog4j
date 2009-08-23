/*.
 * Copyright (c) 2004-2005 SLF4J.ORG
 * Copyright (c) 2004-2005 QOS.ch
 *
 * All rights reserved.
 *.
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute, and/or sell copies of  the Software, and to permit persons
 * to whom  the Software is furnished  to do so, provided  that the above
 * copyright notice(s) and this permission notice appear in all copies of
 * the  Software and  that both  the above  copyright notice(s)  and this
 * permission notice appear in supporting documentation.
 *.
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR  A PARTICULAR PURPOSE AND NONINFRINGEMENT
 * OF  THIRD PARTY  RIGHTS. IN  NO EVENT  SHALL THE  COPYRIGHT  HOLDER OR
 * HOLDERS  INCLUDED IN  THIS  NOTICE BE  LIABLE  FOR ANY  CLAIM, OR  ANY
 * SPECIAL INDIRECT  OR CONSEQUENTIAL DAMAGES, OR  ANY DAMAGES WHATSOEVER
 * RESULTING FROM LOSS  OF USE, DATA OR PROFITS, WHETHER  IN AN ACTION OF
 * CONTRACT, NEGLIGENCE  OR OTHER TORTIOUS  ACTION, ARISING OUT OF  OR IN
 * CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *.
 * Except as  contained in  this notice, the  name of a  copyright holder
 * shall not be used in advertising or otherwise to promote the sale, use
 * or other dealings in this Software without prior written authorization
 * of the copyright holder.
 *
 */

package org.prolog4j.helpers;

import java.util.ArrayList;
import java.util.List;

import org.prolog4j.Prover;
import org.prolog4j.IProverFactory;

/**
 * SubstituteProverFactory is a trivial implementation of
 * {@link IProverFactory} which always returns the unique instance of NOPProver.
 * 
 * <p>
 * It used as a temporary substitute for the real IProverFactory during its
 * auto-configuration which may re-enter ProverFactory to obtain prover
 * instances. See also http://bugzilla.slf4j.org/show_bug.cgi?id=106
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class SubstituteProverFactory implements IProverFactory {

	// keep a record of requested prover names
	final List<String> proverNameList = new ArrayList<String>();

	public Prover getProver(String name) {
		proverNameList.add(name);
		return NOPProver.NOP_PROVER;
	}

	public List<String> getProverNameList() {
		return proverNameList;
	}

}
