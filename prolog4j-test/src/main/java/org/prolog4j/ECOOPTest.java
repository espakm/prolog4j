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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ECOOPTest {

	static Prover p = ProverFactory.getProver();

	static {
		p.addTheory("mortal(X) :- human(X).", "human(socrates).",
				"human(plato).");
	}

	public static boolean isMortal(String somebody) {
		return p.solve("mortal(X).", somebody).isSuccess();
	}

	public static List<String> getMortals() {
		List<String> mortals = new ArrayList<String>();
		for (String s : p.<String> solve("mortal(X)."))
			mortals.add(s);
		return mortals;
	}

	public static void main(String[] args) {
		System.out.println(isMortal("socrates")); // true
		System.out.println(getMortals()); // socrates, plato

		List<String> philosophers = Arrays.asList("socrates", "plato");
		Solution<?> solution = p.solve("member(X, {}).", philosophers);
		System.out.println(solution.isSuccess()); // true

		for (String s : solution.<String> on("X"))
			System.out.println(s); // socrates, plato

		List<String> h1 = Arrays.asList("socrates");
		List<String> h2 = Arrays.asList("thales", "plato");
		for (List<String> humans : p.<List<String>> solve(
				"append(L1, L2, L12).", h1, h2))
			for (String h : humans)
				System.out.println(h); // socrates, thales and plato

		List<String> h3 = Arrays.asList("socrates", "homeros", "demokritos");
		for (List<String> humans : p
				.solve("append(L1, L2, L12).", h1, null, h3).<List<String>> on(
						"L2"))
			for (String h : humans)
				System.out.println(h); // homeros and demokritos
	}

}
