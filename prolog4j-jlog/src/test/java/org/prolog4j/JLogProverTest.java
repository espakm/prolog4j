package org.prolog4j;

/**
 * JUnit test for the JLog binding. Inherits the common test class.
 * The test for adding theories is disabled because it is not supported by
 * this implementation for  now.
 */
public class JLogProverTest extends ProverTest {
	
	@Override
	public void testAddTheory() {
		// Temporarily disabled.
	}
	
}
