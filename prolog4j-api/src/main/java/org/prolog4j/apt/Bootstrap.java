package org.prolog4j.apt;

import java.lang.instrument.Instrumentation;

public class Bootstrap {

	public static void premain(String agentArguments, Instrumentation instrumentation) {
		instrumentation.addTransformer(new GoalTransformer());
	}

}
