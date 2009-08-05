package org.prolog4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProverTest {

	private static final Prover p = Prover.get();
	static {
		p.addTheory(
			"mortal(X) :- human(X).", 
			"human(socrates).",
			"human(plato).");
	}

	public static boolean isMortal(String somebody) {
		return p.solve("mortal(X).", somebody).isSuccess();
	}

	public static List<String> getMortals() {
		List<String> mortals = new ArrayList<String>();
//		for (String s : p.<String>solve("mortal(X).", (Object) null))
		for (String s : p.<String>solve("mortal(X)."))
			mortals.add(s);
		return mortals;
	}

	public static void main(String[] args) {
		System.out.println(isMortal("socrates")); // true
		System.out.println(getMortals()); // socrates, plato

		List<String> philosophers = Arrays.asList("socrates", "plato");
		Solution<?> solution = p.solve("member(X, List).", null, philosophers);
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
