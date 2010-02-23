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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
        "append([H|T], L, [H|R]) :-" +
        "    append(T, L, R).");
        
        p.addTheory(
                "mortal(X) :- human(X).",
                "human(socrates).",
                "human(plato).");
    }

    /**
     * Asserts that the goal has a solution for the given arguments.
     * 
	 * @param goal the Prolog goal
	 * @param args the actual arguments of the goal
     */
    public final void assertSuccess(final String goal, final Object... args) {
    	assertSuccess(p.solve(goal, args));
    }
    
    /**
     * Asserts that there is a solution. Equivalent with
     * <code>assertTrue(solution.isSuccess());</code>.
     * 
     * @param solution the solution
     */
    public final void assertSuccess(final Solution<?> solution) {
    	assertTrue(solution.isSuccess());
    }
    
    /**
     * Asserts that the goal has no solution for the given arguments.
     * 
	 * @param goal the Prolog goal
	 * @param args the actual arguments of the goal
     */
    public final void assertFailure(final String goal, final Object... args) {
    	assertFailure(p.solve(goal, args));
    }

    /**
     * Asserts that there is no solution. Equivalent with
     * <code>assertFalse(solution.isSuccess());</code>.
     * 
     * @param solution the solution
     */
    public final void assertFailure(final Solution<?> solution) {
    	assertFalse(solution.isSuccess());
    }

    /**
     * Tests {@link InvalidQueryException}.
     */
    @Test(expected = InvalidQueryException.class)
    public void testInvalidQuery1() {
        p.solve("mortal(socrates");
    }

    /**
     * Tests {@link InvalidQueryException}.
     */
    @Test(expected = InvalidQueryException.class)
    public void testInvalidQuery2() {
        p.solve("mortal(socrates)");
    }

    /**
     * Tests {@link Solution#isSuccess()}.
     */
    @Test
    public void testIsSuccess() {
        assertSuccess("mortal(socrates).");
        assertFailure("mortal(zeus).");
    }

    /**
     * Tests whether the place holders are handled correctly by
     * {@link Query#Query(String)}.
     */
    @Test
    public void testPlaceHolders() {
        assertSuccess("mortal(?).", "socrates");
        assertFailure("mortal(?).", "zeus");
        assertSuccess("mortal(?X).", "socrates");
        assertFailure("mortal(?X).", "zeus");
        assertSuccess("mortal(?LongVariable).", "socrates");
        assertFailure("mortal(?LongVariable).", "zeus");
        assertSuccess("_ = 'Question ??Mark'.");
        assertSuccess("? = '??Mark'.", "?Mark");
        assertSuccess("'Is it OK??' = ?.", "Is it OK?");
        assertSuccess("'????' = ?.", "??");
        assertSuccess("member(X, ?).", Arrays.asList(0, 1, 2));
        List<Object> list = new ArrayList<Object>();
        for (Object o: p.solve("member(X, ?).", Arrays.asList(0, 1, 2))) {
            list.add(o);
        }
        assertEquals(Arrays.asList(0, 1, 2), list);
    }

    /**
     * Tests {@link UnknownVariableException}.
     */
    @Test(expected = UnknownVariableException.class)
    public void testUnknownVariable() {
        p.solve("mortal(socrates).").get("X");
    }

    /**
     * Tests the iteration over solutions.
     */
    @Test
    public void testIterable() {
        List<String> mortals = new ArrayList<String>();
        for (String s : p.<String> solve("mortal(X).")) {
            mortals.add(s);
        }
        assertEquals(Arrays.asList("socrates", "plato"), mortals);

        int i = 0;
        for (Object o: p.solve("member(X, [0, 1, 2]).")) {
            ++i;
        }
        assertEquals(i, 3);
        
        for (Object o: p.solve("member(X, 1).")) {
            fail();
        }
        for (Object o: p.solve("member(X, 1).").on("X")) {
            fail();
        }
    }

    /**
     * Tests the correct behavior of the member/2 predicate.
     */
    @Test
    public void testIsMember() {
        List<String> philosophers = Arrays.asList("socrates", "plato");
        assertSuccess("member(X, ?List).", philosophers);
    }

    /**
     * Tests {@link Solution#on(String)}.
     */
    @Test
    public void testTestOn() {
        List<String> philosophers = Arrays.asList("socrates", "plato");
        List<String> list = new ArrayList<String>(2);
        Solution<String> solution = p.solve("member(X, ?List).", philosophers);
        for (String s: solution.<String> on("X")) {
            list.add(s);
        }
        assertEquals(Arrays.asList("socrates", "plato"), list);

        Solution<String> solution2 = p.solve("member(X, ?List).", philosophers);
        list.clear();
        for (String s: solution2.on("X", String.class)) {
            list.add(s);
        }
        assertEquals(Arrays.asList("socrates", "plato"), list);
    }
    
    /**
     * Tests the default term converters added to the prover.
     */
    @Test
    public void testTermPattern() {
        final ConversionPolicy cp = ProverFactory.getConversionPolicy();
        assertSuccess("? = a.", cp.term("a"));
        assertSuccess("? = 1.", cp.term("1"));
        assertSuccess("? = 1.", cp.term("?", 1));
        assertSuccess("? = cpd(a).", cp.term("cpd(a)"));
        assertSuccess("? = cpd(a).", cp.term("cpd(?)", "a"));
        assertSuccess("? = [a, b, c].", cp.term("[a, b, c]"));
        assertSuccess("? = [a, b, c].", cp.term("[?, b, c]", "a"));
        assertSuccess("? = [a, b, c].", cp.term("[?, ?, c]", "a", "b"));
    }
    
    /**
     * Tests the default term converters added to the prover.
     */
    @Test
    public void testTermConverters() {
        int iVal = p.<Integer>solve("X=1.").get();
        assertEquals(1, iVal);
//        long lVal = p.<Long>solve("X=1L.").get();
//        assertEquals(1L, lVal);
//        float fVal = p.<Float>solve("X=1.0f.").get();
//        assertEquals(1.0f, fVal, 0.0);
        double dVal = p.<Double>solve("X=1.0.").get();
        assertEquals(1.0, dVal, 0.0);
        String sVal = p.<String>solve("X=prolog4j.").get();
        assertEquals("prolog4j", sVal);
        
//        Object[] iaVal = p.<Integer[]>solve("X = [0, 1, 2].").get();
//        assertArrayEquals(new Object[]{0, 1, 2}, iaVal);
//        Object[] saVal = p.<Object[]>solve("X = [a, b, c].").get();
//        assertArrayEquals(new Object[]{"a", "b", "c"}, saVal);
        
        List<Integer> liVal = p.<List<Integer>>solve("X = [0, 1, 2].").get();
        assertEquals(Arrays.asList(0, 1, 2), liVal);
        List<String> lsVal = p.<List<String>>solve("X = [a, b, c].").get();
        assertEquals(Arrays.asList("a", "b", "c"), lsVal);
        
        Object cVal = p.solve("X = functor(arg1, arg2).").get();
        assertEquals(new Compound("functor", "arg1", "arg2"), cVal);
    }

    /**
     * Tests the default object converters added to the prover.
     */
    @Test
    public void testObjectConverters() {
        System.out.println("ProverTest.testObjectConverters()");
        System.out.println(List[].class.getSuperclass());
        System.out.println(List.class.getSuperclass());
        assertSuccess("?=1.", 1);
        assertFailure("?=1.", 1.0);
        assertFailure("?=1.", 2);
//        assertSuccess("?=1L.", 1L);
//        assertSuccess("?=1.0f.", 1.0f);
        assertSuccess("?=1.0.", 1.0);
        assertFailure("?=1.0.", 1);
        assertFailure("?=1.0.", 2.0);
        assertSuccess("?=prolog4j.", "prolog4j");
        assertSuccess("?='Prolog4J'.", "Prolog4J");
        assertFailure("?=prolog4j.", "Prolog4j");
        assertFailure("?=prolog4j.", "'prolog4j'");
        assertSuccess("?='2'.", "2");
        assertFailure("?=2.", "2");
        assertFailure("?='2'.", 2);

//        assertSuccess("?=[0, 1, 2].", (Object) new Integer[]{0, 1, 2});
//        assertSuccess("?=[a, b, c].", (Object) new String[]{"a", "b", "c"});
        assertSuccess("?=[0, 1, 2].", Arrays.asList(0, 1, 2));
        assertSuccess("?=[a, b, c].", Arrays.asList("a", "b", "c"));

        assertSuccess("?=f(1, 2).", new Compound("f", 1, 2));
    }

    /**
     * Tests the user defined object converters added to the prover.
     */
    @Test
    public void testCustomObjectConverters() {
        final ConversionPolicy cp = p.getConversionPolicy();
//        final ConversionPolicy cp = ProverFactory.getConversionPolicy();
        class Human {
            private final String name;
            Human(String name) {
                this.name = name;
            }
        }
        cp.addObjectConverter(Human.class, new Converter<Human>() {
            @Override
            public Object convert(Human human) {
                return cp.term("human(?)", human.name);
            }
        });
        Human socrates = new Human("socrates");
        assertSuccess("?=human(socrates).", socrates);
        assertSuccess("?=human(_).", socrates);
        assertFailure("?=human(socrates, plato).", socrates);
        assertFailure("?=socrates.", socrates);
    }

    /**
     * Tests the user defined term converters added to the prover.
     */
    @Test
    public void testCustomTermConverters() {
        final ConversionPolicy cp = p.getConversionPolicy();
//        final ConversionPolicy cp = ProverFactory.getConversionPolicy();
        class Human {
            private final String name;
            Human(String name) {
                this.name = name;
            }
            @Override
            public boolean equals(Object obj) {
                return obj instanceof Human && name.equals(((Human) obj).name);
            }
        }
        cp.addTermConverter("human", new Converter<Object>() {
            @Override
            public Object convert(Object term) {
                if (cp.getArity(term) == 1) {
                    return new Human((String) cp.getArg(term, 0));
                }
                return null;
            }
        });
        Human socrates = p.<Human>solve("H=human(socrates).").get();
        assertEquals(new Human("socrates"), socrates);
    }

    /**
     * Tests {@link Solution#on(String)} and the conversion of the result 
     * into a list.
     */
    @Test
    public void testTestListResult() {
        List<String> h1 = Arrays.asList("socrates");
        List<String> h2 = Arrays.asList("thales", "plato");

        Solution<List<Object>> solution = p.solve("append(?L1, ?L2, L12).", h1, h2);

        Iterator<List<Object>> it = solution.<List<Object>>on("L12").iterator();
        assertTrue(it.hasNext());
        List<Object> sol = it.next();
        assertEquals(Arrays.asList("socrates", "thales", "plato"), sol);
        assertFalse(it.hasNext());
    }
    
    /**
     * Tests {@link Solution#on(String, Class)}.
     * Tests converting the result into arrays instead of lists.
     */
    @Test
    public void testTestArrayResult() {
        List<String> h1 = Arrays.asList("socrates");
        List<String> h3 = Arrays.asList("socrates", "homeros", "demokritos");
        for (String[] humans: p.solve("append(?L1, L2, ?L12).", h1, h3).
                on("L2", String[].class)) {
            for (String h: humans) {
                System.out.println(h); // homeros and demokritos
            }
        }
    }

    /**
     * Tests adding theories to the prover.
     */
    @Test
    public void testAddTheory() {
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
    public void testAssert() {
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
    
    /**
     * Tests the dynamic assertion of theories.
     */
    @Test
    public void testWeakFacts() {
        System.out.println("ProverTest.assertWeakTest()");
        // TODO
        p.assertz("roman2(michelangelo).");
        p.assertz("roman2(davinci).");
        p.assertz("roman2(iulius).");
        Set<Object> romans = p.solve("roman2(H).").toSet();
        Set<Object> romansExpected = new HashSet<Object>();
        romansExpected.add("michelangelo");
        romansExpected.add("davinci");
        romansExpected.add("iulius");
        assertEquals(romansExpected, romans);
        p.retract("roman2(iulius).");
        romans = p.solve("roman2(H).").toSet();
        romansExpected.remove("iulius");
        assertEquals(romansExpected, romans);
    }

    /**
     * Tests the format elements.
     */
    @Test
    public void testFormatElements() {
        assertSuccess("member(?, [1, 2, 3]).", 1);
    }

//    /**
//     * Tests the format elements
//     */
//    @Test(expected = InvalidQueryException.class)
//    public void testFormatElements2() {
//        assertSuccess("member({xy}, [1, 2, 3]).", 1);
//    }
}
