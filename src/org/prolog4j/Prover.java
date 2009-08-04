package org.prolog4j;

import alice.tuprolog.InvalidLibraryException;
import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.Prolog;
import alice.tuprolog.Theory;

public class Prover {

	public static final Prover GLOBAL = new CachingProver();
//	static {
//		GLOBAL.loadLibrary("japlo.lib.OOLibrary");
//		try {
//			GLOBAL.engine.loadLibrary(new MetaLibrary(GLOBAL.engine));
//		} catch (InvalidLibraryException e1) {
//			e1.printStackTrace();
//		}
//		GLOBAL.engine.addWarningListener(new WarningListener() {
//			public void onWarning(WarningEvent e) {
//				System.out.println(e.getMsg());
//			}
//		});
//		GLOBAL.engine.addOutputListener(new OutputListener() {
//			public void onOutput(alice.tuprolog.event.OutputEvent e) {
//				System.out.println(e.getMsg());
//			};
//		});
//	}
	
	public final Prolog engine;

	public Prover() {
		engine = new Prolog();
	}
	
	public void loadLibrary(String className) {
		try {
			engine.loadLibrary(className);
		} catch (InvalidLibraryException e) {
			throw new RuntimeException(e);
		}
	}
	
	public <A> Solution<A> solve(String goal) {
		System.out.println("Prover.solve() " + goal);
//		return new Solution2<A>(engine, Solution2.goalTerms(goal, 0));
		return new Solution<A>(engine, goal);
	}
	
	public <A> Solution<A> solve(String goal, int inputArgs, Object... actualArgs) {
		return new Solution<A>(engine, Solution.goalTerms(goal, inputArgs), actualArgs);
	}
	
	public <A> Solution<A> solve(String goal, Object... actualArgs) {
		System.out.println("Prover.solve() 2");
		return new Solution<A>(engine, Solution.goalTerms(goal, (1 << actualArgs.length) - 1), actualArgs);
//		return new Solution2<A>(engine, goal, actualArgs);
	}

//	public <A> Solution<A> solve(String goal, Map<String, Object> args) {
//		int argNo = args.size();
//		String[] inputArgs = args.keySet().toArray(new String[argNo]);
//		Arrays.sort(inputArgs);
//		Object[] actualArgs = new Object[argNo];
//		for (int i = 0; i < argNo; ++i)
//			actualArgs[i] = args.get(inputArgs[i]);
//		return new Solution<A>(engine, Solution.goalTerms(goal, inputArgs), actualArgs);
//	}

	public <A> Solution<A> solve(String goal, String[] inputArgs, Object[] actualArgs) {
		return new Solution<A>(engine, Solution.goalTerms(goal, inputArgs), actualArgs);
	}

	public void addTheory(String theory) {
		try {
			engine.addTheory(new Theory(theory));
		} catch (InvalidTheoryException e) {
			e.printStackTrace();
		}
	}

	public static Prover get() {
		return GLOBAL;
	}

	public void addTheory(String... theories) {
		StringBuilder sb = new StringBuilder();
		for (String theory: theories)
			sb.append(theory).append('\n');
		try {
			engine.addTheory(new Theory(sb.toString()));
		} catch (InvalidTheoryException e) {
			e.printStackTrace();
		}
	}

}
