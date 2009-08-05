package org.prolog4j;

import java.util.Iterator;

public interface SolutionIterator<S> extends Iterator<S> {

    <A> A get(String argName);

    <A> A get(String argName, Class<A> type);

}
