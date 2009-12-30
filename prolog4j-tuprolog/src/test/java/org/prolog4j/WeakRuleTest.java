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
		Prover p = ProverFactory.getProver(WeakRuleTest.class);
		class Human {
			String name;
			Human(String name) {
				this.name = name;
			}
		}
		Human socrates = new Human("socrates");
		Prolog engine = ((TuPrologProver) p).getEngine();
		JavaLibrary lib = (JavaLibrary) engine.loadLibrary("alice.tuprolog.lib.JavaLibrary");
//		engine.loadLibrary(new PrintLibrary());
		engine.solve("assertz(human(aristotle)).");
		engine.solve("assertz(human(demokritos)).");
//		engine.addTheory(new Theory("human(plato).\n"));
//		engine.solve(new Struct("assertz", new Struct("human", register)));
		ReferenceQueue<Human> queue = new ReferenceQueue<Human>();
		WeakReference<Human> weakSocrates = new WeakReference<Human>(socrates, queue);
		Struct tWeakSocrates = lib.register(weakSocrates);
		Struct socratesFact = new Struct("human", tWeakSocrates);
		engine.solve(new Struct("assertz", socratesFact));

		SolveInfo si = engine.solve("human(X).");
		while (si.isSuccess()) {
			System.out.println(si.getVarValue("X"));
			if (!si.hasOpenAlternatives()) {
				break;
			}
			si = engine.solveNext();
		}

		socrates = null;
		System.gc();
		Reference r = queue.poll();
		if (r == null) {
			engine.solve(new Struct("retract", socratesFact));
		}
		
		si = engine.solve("human(X).");
		while (si.isSuccess()) {
			System.out.println(si.getVarValue("X"));
			if (!si.hasOpenAlternatives()) {
				break;
			}
			si = engine.solveNext();
		}
	}

}
