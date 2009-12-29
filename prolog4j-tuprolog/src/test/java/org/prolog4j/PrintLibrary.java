package org.prolog4j;

import alice.tuprolog.Library;

public class PrintLibrary extends Library {
	public boolean println_1(alice.tuprolog.Term t) {
		System.out.println(t.getTerm());
		return true;
	}
}
