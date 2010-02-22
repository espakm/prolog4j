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

import static org.junit.Assert.*;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import org.junit.Test;
import org.prolog4j.tuprolog.TuPrologProver;

import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Struct;
import alice.tuprolog.lib.JavaLibrary;

public class WeakRuleTest {

	@Test
	public void testWeakRule() throws Exception {
//		Prover p = ProverFactory.getProver(WeakRuleTest.class);
//		class Human {
//			String name;
//			Human(String name) {
//				this.name = name;
//			}
//		}
//		Human socrates = new Human("socrates");
//		Prolog engine = ((TuPrologProver) p).getEngine();
//		JavaLibrary lib = (JavaLibrary) engine.loadLibrary("alice.tuprolog.lib.JavaLibrary");
////		engine.loadLibrary(new PrintLibrary());
//		engine.solve("assertz(human(aristotle)).");
//		engine.solve("assertz(human(demokritos)).");
////		engine.addTheory(new Theory("human(plato).\n"));
////		engine.solve(new Struct("assertz", new Struct("human", register)));
//		ReferenceQueue<Human> queue = new ReferenceQueue<Human>();
//		WeakReference<Human> weakSocrates = new WeakReference<Human>(socrates, queue);
//		Struct tWeakSocrates = lib.register(weakSocrates);
//		Struct socratesFact = new Struct("human", tWeakSocrates);
//		engine.solve(new Struct("assertz", socratesFact));
//
//		SolveInfo si = engine.solve("human(X).");
//		while (si.isSuccess()) {
//			System.out.println(si.getVarValue("X"));
//			if (!si.hasOpenAlternatives()) {
//				break;
//			}
//			si = engine.solveNext();
//		}
//
//		socrates = null;
//		System.gc();
//		Reference r = queue.poll();
//		if (r == null) {
//			engine.solve(new Struct("retract", socratesFact));
//		}
//		
//		si = engine.solve("human(X).");
//		while (si.isSuccess()) {
//			System.out.println(si.getVarValue("X"));
//			if (!si.hasOpenAlternatives()) {
//				break;
//			}
//			si = engine.solveNext();
//		}
	}

}
