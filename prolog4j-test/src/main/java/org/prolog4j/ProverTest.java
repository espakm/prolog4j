package org.prolog4j;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JUnit test for Prolog4J API and bindings.
 */
public class ProverTest {

	/**
	 * The prover.
	 */
	private static Prover p;

	/**
	 * Retrieves a prover from the factory and defines some rules (member,
	 * append, mortal) and some facts (human(socrates). and human(plato).).
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		p = ProverFactory.getProver(ProverTest.class);
		p.addTheory(
		"member(X, [X|_]).",
		"member(X, [_|L]) :- member(X, L).",
		"append([], R, R).",
		"append([H|T], L, [H|R]) :-",
		"	append(T, L, R).");
		
		p.addTheory(
				"mortal(X) :- human(X).", 
				"human(socrates).",
				"human(plato).");
	}

	/**
	 * Determines whether somebody is mortal or not.
	 * 
	 * @param somebody a name
	 * @return <code>true</code> if the entity is mortal, otherwise 
	 * 		<code>false</code>
	 */
	public static boolean isMortal(String somebody) {
		return p.solve("mortal(X{}).", somebody).isSuccess();
	}

	/**
	 * Returns the name of all mortal beings.
	 * 
	 * @return the list of the name of all mortal beings
	 */
	public static List<String> getMortals() {
		List<String> mortals = new ArrayList<String>();
		// for (String s : p.<String>solve("mortal(X).", (Object) null))
		for (String s : p.<String> solve("mortal(X).")) {
			mortals.add(s);
		}
		return mortals;
	}

	/**
	 * Tests {@link Solution#isSuccess()}. 
	 */
	@Test
	public void testIsSuccess() {
		assertTrue(isMortal("socrates"));
	}

	/**
	 * Tests whether the place holders are handled correctly by 
	 * {@link Query#Query(String)}.
	 */
	@Test
	public void testArgumentPlace() {
		assertTrue(p.solve("mortal({}).", "socrates").isSuccess());
		assertFalse(p.solve("mortal({}).", "zeus").isSuccess());
		assertTrue(p.solve("member(X, {}).", Arrays.asList(0, 1, 2)).isSuccess());
		for (Integer i: p.solve("member(X, {}).", Arrays.asList(0, 1, 2)).<Integer>on("X")) {
			System.out.println("ProverTest.testIsSuccess2() " + i);
		}
	}

	/**
	 * Tests the iteration over solutins.
	 */
	@Test
	public void testIterable() {
		List<String> mortals = getMortals();
		assertEquals(mortals, Arrays.asList("socrates", "plato"));

		int i = 0;
		for (Object o: p.solve("member(X, [0, 1, 2]).")) {
			++i;
		}
		assertTrue(i == 3);
		
		for (Object o: p.solve("member(X, 1).")) {
		}
		for (Object o: p.solve("member(X, 1).").on("X")) {
		}
	}

	/**
	 * Tests the correct behaviour of the member/2 predicate.
	 */
	@Test
	public void testIsMember() {
		List<String> philosophers = Arrays.asList("socrates", "plato");
		Solution<String> solution = p.solve("member(X, List{}).", philosophers);
		assertTrue(solution.isSuccess());
	}

	/**
	 * Tests {@link Solution#on(String)}.
	 */
	@Test
	public void testTestOn() {
		List<String> philosophers = Arrays.asList("socrates", "plato");
		List<String> list = new ArrayList<String>(2);
		Solution<String> solution = p.solve("member(X, List{}).", philosophers);
		for (String s : solution.<String> on("X")) {
			list.add(s);
		}
		assertEquals(list, Arrays.asList("socrates", "plato"));
	}

	/**
	 * Tests {@link Solution#on(String)} and the conversion of the result 
	 * into an array.
	 */
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
	
	/**
	 * Tests {@link Solution#on(String, Class)}.
	 * Tests converting the result into lists instead of arrays.
	 */
	@Test
	public void testTestListResult() {
		List<String> h1 = Arrays.asList("socrates");
//		List<String> h2 = Arrays.asList("thales", "plato");
		List<String> h3 = Arrays.asList("socrates", "homeros", "demokritos");
		for (List<String> humans : p.solve("append(L1{}, L2, L12{}).", h1, h3).
				on("L2", List.class)) {
			for (String h : humans) {
				System.out.println(h); // homeros and demokritos
			}
		}
	}

	/**
	 * Tests adding theories to the prover.
	 */
	@Test
	public void theoryTest() {
		p.addTheory("greek(socrates).");
		p.addTheory("greek(plato).");
		p.addTheory("greek(demokritos).");
		Set<Object> greeks = p.solve("greek(H).").toSet();
		Set<Object> greeksExpected = new HashSet<Object>();
		greeksExpected.add("socrates");
		greeksExpected.add("plato");
		greeksExpected.add("demokritos");
		assertEquals(greeksExpected, greeks);
	}
	
	/**
	 * Tests the dynamic assertion of theories.
	 */
	@Test
	public void assertTest() {
		p.solve("assertz(roman(michelangelo)).");
		p.solve("assertz(roman(davinci)).");
		p.solve("assertz(roman(iulius)).");
		Set<Object> romans = p.solve("roman(H).").toSet();
		Set<Object> romansExpected = new HashSet<Object>();
		romansExpected.add("michelangelo");
		romansExpected.add("davinci");
		romansExpected.add("iulius");
		assertEquals(romansExpected, romans);
	}
	
}
