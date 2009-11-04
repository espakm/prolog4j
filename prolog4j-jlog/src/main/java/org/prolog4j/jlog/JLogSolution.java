package org.prolog4j.jlog;

import java.util.ArrayList;
import java.util.Collection;
//import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.prolog4j.SolutionIterator;

import ubc.cs.JLog.Foundation.jPrologAPI;
//import ubc.cs.JLog.Terms.iTermToObject;
//import ubc.cs.JLog.Terms.jAtom;
//import ubc.cs.JLog.Terms.jList;
//import ubc.cs.JLog.Terms.jListPair;
//import ubc.cs.JLog.Terms.jNullList;
import ubc.cs.JLog.Terms.jTerm;
//import ubc.cs.JLog.Terms.jTermTranslation;

/**
 * The <tt>Solution</tt> class is responsible for traversing through the
 * solutions of a query.
 * 
 * @param <S>
 *            the type of the values of the variable that is of special interest
 */
public class JLogSolution<S> extends org.prolog4j.Solution<S> {

	private final jPrologAPI prolog;
	private String[] outputVarNames;

	private Hashtable<String, Object> solution;
	private final boolean success;

//	final static jTermTranslation translator;
//	static {
//		translator = new jTermTranslation();
//		translator.setDefaults();
//		iTermToObject atomToString = new iTermToObject() {
//			@Override
//			public Object createObjectFromTerm(jTerm term) {
//				if (term instanceof jAtom)
//					return term.getName();
//				return null;
//			}
//		};
//		translator.RegisterTermToObjectConverter(jAtom.class, atomToString);
//		iTermToObject listToArray = new iTermToObject() {
//			public Object createObjectFromTerm(jTerm term) {
//				if (term instanceof jList) {
//					jList list = (jList) term;
//					Enumeration e = list.elements(translator);
//					ArrayList<Object> al = new ArrayList<Object>();
//
//					while (e.hasMoreElements())
//						al.add(e.nextElement());
//
//					return al.toArray();		
//				}
//
//				throw new RuntimeException("Expected jList term."); 
//			}
//		};
//		translator.RegisterTermToObjectConverter(jListPair.class, listToArray);
//		translator.RegisterTermToObjectConverter(jNullList.class, listToArray);
//	}
	
	JLogSolution(jPrologAPI prolog, String goal, Hashtable<String, Object> initialBindings) {
		this.prolog = prolog;
		solution = prolog.query(goal, initialBindings);
		success = solution != null;
		if (!success || solution.size() == 0) {
			return;
		}
		outputVarNames = new String[solution.size()];
		int i = 0;
		for (String var: solution.keySet())
			outputVarNames[i++] = var;
		defaultOutputVariable = outputVarNames[outputVarNames.length - 1];
	}

	@Override
	public boolean isSuccess() {
		return success;
	}

	@Override
	public <A> A get(String variable) {
		if (clazz == null) {
			return Terms.<A> toObject((jTerm) solution.get(variable));
		}
		return (A) get(variable, clazz);
	}

	@Override
	public <A> A get(String variable, Class<A> type) {
		return Terms.toObject((jTerm) solution.get(variable), type);
	}

	@Override
	public void collect(Collection... collections) {
		SolutionIterator<S> it = iterator();
		while (it.hasNext()) {
			it.next();
			for (int i = 0; i < collections.length; ++i)
				collections[i].add(it.get(outputVarNames[i]));
		}
	}

	@Override
	public List<?>[] toLists() {
		List<?>[] lists = new List<?>[outputVarNames.length];
		for (int i = 0; i < lists.length; ++i)
			lists[i] = new ArrayList();
		collect(lists);
		return lists;
	}

	@Override
	protected void fetch() {
		solution = prolog.retry();
		hasNext = solution != null;
		fetched = true;
	}

}
