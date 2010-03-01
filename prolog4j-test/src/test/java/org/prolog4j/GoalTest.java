package org.prolog4j;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.prolog4j.annotations.Bind;
import org.prolog4j.annotations.Goal;
import org.prolog4j.annotations.Theory;

@Theory({
	"membr(X, [X|_]).",
	"membr(X, [_|L]) :- membr(X, L)."})
public class GoalTest {
	
	@Test
	public void testGoal() {
		Solution member = member(1, Arrays.asList(1, 2, 3));
		Assert.assertNotNull(member);
		Assert.assertTrue(member.isSuccess());
		Solution member2 = member(5, Arrays.asList(1, 2, 3));
		Assert.assertNotNull(member);
		Assert.assertFalse(member2.isSuccess());
		Solution goal3 = goal3("one", 2, 3L);
		Assert.assertNotNull(goal3);
		Assert.assertFalse(goal3.isSuccess());
		Assert.assertTrue(goalBoolean(3));
		Assert.assertFalse(goalBoolean(5));
		Assert.assertTrue(goalBoolean2(3));
		Assert.assertFalse(goalBoolean2(5));
		Assert.assertEquals(goalPrimitive(), 2);
		Assert.assertEquals(goalOutNamed(), 2);
		Assert.assertEquals(goalOutNamedX(), 2);
		Assert.assertEquals(goalOutNamedY(), 1);
		
		Iterator it;
		Solution onX = goalOnX();
		it = onX.iterator();
		Assert.assertEquals(2, it.next());
		Assert.assertEquals(3, it.next());
		Assert.assertEquals(4, it.next());
		Assert.assertFalse(it.hasNext());

		Solution onY = goalOnY();
		it = onY.iterator();
		Assert.assertEquals(1, it.next());
		Assert.assertEquals(1, it.next());
		Assert.assertEquals(1, it.next());
		Assert.assertFalse(it.hasNext());
		
//		assertSuccess(goalBindArgXY(1, 2));
//		assertFailure(goalBindArgXY(2, 1));
//		assertSuccess(goalBindArgYX(2, 1));
//		assertFailure(goalBindArgYX(1, 2));
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
     * Asserts that there is no solution. Equivalent with
     * <code>assertFalse(solution.isSuccess());</code>.
     * 
     * @param solution the solution
     */
    public final void assertFailure(final Solution<?> solution) {
    	assertFalse(solution.isSuccess());
    }

	
	@Goal("membr(?, ?).")
	public Solution member(Object o, List<?> list) {
		return null;
	}
	
	@Goal("? = 1, ? = 2, ? = 3.")
	public Solution goal3(Object o, Integer i, Long l) {
		return null;
	}
	
	@Goal("goal3(?, ?, ?).")
	public Solution goal7(Object o, Integer i, Long l, Object o4, Object o5, Object o6, Object o7) {
		return null;
	}
	
	@Goal("goalPrimitives(?, ?).")
	public Solution goalPrimitives(Object o, int i, boolean b, char c, short s, long l, float f, double d, byte by) {
		return null;
	}

	@Goal("membr(?, [1, 2, 3]).")
	public boolean goalBoolean(Object o) {
		throw null;
	}

	@Goal("membr(?, [1, 2, 3]).")
	public Boolean goalBoolean2(Object o) {
		return null;
	}
	
	@Goal(cache = false, value = "member(?, [2, 3, 4]).")
	public Solution goalPrecompiled(Object o) {
		return null;
	}

	@Goal(value = "member(X, [2, 3, 4]).")
	public Object goalOut() {
		return null;
	}

	@Goal(value = "member(X, [2, 3, 4]).")
	public int goalPrimitive() {
		return 0;
	}

	@Goal(value = "member(X, [2, 3, 4]).")
	public @Bind("X") Object goalOutNamed() {
		return null;
	}

	@Goal(value = "member(X, [2, 3, 4]), Y = 1.")
	public @Bind("X") Object goalOutNamedX() {
		return null;
	}

	@Goal(value = "member(X, [2, 3, 4]), Y = 1.")
	public @Bind("Y") Object goalOutNamedY() {
		return null;
	}

	@Goal(value = "member(X, [2, 3, 4]), Y = 1.")
	public @Bind("X") Solution goalOnX() {
		return null;
	}

	@Goal(value = "member(X, [2, 3, 4]), Y = 1.")
	public @Bind("Y") Solution goalOnY() {
		return null;
	}

//	@Goal(value = "?X = 1, ?Y = 2.")
//	public Solution goalBindArgXY(@Bind("X") Object x, @Bind("Y") Object y) {
//		return null;
//	}

//	@Goal(value = "?X = 1, ?Y = 2.")
//	public Solution goalBindArgYX(@Bind("Y") Object x, @Bind("X") Object y) {
//		return null;
//	}

}
