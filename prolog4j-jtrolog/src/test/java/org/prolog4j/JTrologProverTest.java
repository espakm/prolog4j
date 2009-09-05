package org.prolog4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class JTrologProverTest {

	private static Prover p;

	@BeforeClass
	public static void setUpBeforeClass() {
		p = ProverFactory.getProver(JTrologProverTest.class);
		p.addTheory("mortal(X) :- human(X).", "human(socrates).",
				"human(plato).");
	}

	public static boolean isMortal(String somebody) {
		return p.solve("mortal(X{}).", somebody).isSuccess();
	}

	public static List<String> getMortals() {
		List<String> mortals = new ArrayList<String>();
		// for (String s : p.<String>solve("mortal(X).", (Object) null))
		for (String s : p.<String> solve("mortal(X)."))
			mortals.add(s);
		return mortals;
	}

	@Test
	public void testIsSuccess() {
		assertTrue(isMortal("socrates"));
	}

	@Test
	public void testIterable() {
		assertEquals(getMortals(), Arrays.asList("socrates", "plato"));
	}

	@Test
	public void testIsMember() {
		List<String> philosophers = Arrays.asList("socrates", "plato");
		Solution<String> solution = p.solve("member(X, List{}).", philosophers);
		assertTrue(solution.isSuccess());
	}

	@Test
	public void testTestOn() {
		List<String> philosophers = Arrays.asList("socrates", "plato");
		List<String> list = new ArrayList<String>(2);
		Solution<String> solution = p.solve("member(X, List{}).", philosophers);
		for (String s : solution.<String> on("X"))
			list.add(s);
		assertEquals(list, Arrays.asList("socrates", "plato"));
	}

	@Test
	public void testTestArrayResult() {
		List<String> h1 = Arrays.asList("socrates");
		List<String> h2 = Arrays.asList("thales", "plato");

		Solution<Object[]> solution = p.solve("append(L1{}, L2{}, L12).", h1, h2);

		Iterator<Object[]> it = solution.<Object[]>on("L12").iterator();
		assertTrue(it.hasNext());
		Object[] sol = it.next();
		assertArrayEquals(sol, new Object[]{"socrates", "thales", "plato"});
		assertFalse(it.hasNext());
	}
	
	@Test
	public void testTestListResult() {
		List<String> h1 = Arrays.asList("socrates");
		List<String> h2 = Arrays.asList("thales", "plato");
		List<String> h3 = Arrays.asList("socrates", "homeros", "demokritos");
		for (List<String> humans : p.solve("append(L1{}, L2, L12{}).", h1, h3).on("L2", List.class))
			for (String h : humans)
				System.out.println(h); // homeros and demokritos
	}

}
