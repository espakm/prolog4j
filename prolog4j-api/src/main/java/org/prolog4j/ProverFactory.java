/*
 * Copyright (c) 2004-2008 QOS.ch
 * All rights reserved.
 *.
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *.
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *.
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.prolog4j;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.prolog4j.helpers.SubstituteProverFactory;
import org.prolog4j.impl.StaticProverBinder;

/**
 * The <code>ProverFactory</code> is a utility class producing Provers for
 * various Prolog APIs, most notably for tuProlog, jTrolog and JLog. Other
 * implementations such as {@link org.prolog4j.impl.NOPProver NOPProver} and
 * {@link org.slf4j.impl.SimpleProver SimpleProver} are also supported. TODO
 * 
 * <p>
 * <code>ProverFactory</code> is essentially a wrapper around an
 * {@link IProverFactory} instance bound with <code>ProverFactory</code> at
 * compile time.
 * 
 * <p>
 * Please note that all methods in <code>ProverFactory</code> are static.
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @author Robert Elliot
 */
public final class ProverFactory {

	static final String NO_STATICPROVERBINDER_URL = "http://www.slf4j.org/codes.html#StaticLoggerBinder";
	static final String MULTIPLE_BINDINGS_URL = "http://www.slf4j.org/codes.html#multiple_bindings";
	static final String NULL_LF_URL = "http://www.slf4j.org/codes.html#null_LF";
	static final String VERSION_MISMATCH = "http://www.slf4j.org/codes.html#version_mismatch";
	static final String SUBSTITUTE_PROVER_URL = "http://www.slf4j.org/codes.html#substituteLogger";

	static final String UNSUCCESSFUL_INIT_URL = "http://www.slf4j.org/codes.html#unsuccessfulInit";
	static final String UNSUCCESSFUL_INIT_MSG = "org.prolog4j.ProverFactory could not be successfully initialized. See also "
			+ UNSUCCESSFUL_INIT_URL;

	static enum InitializationState {
		UNINITIALIZED,
		ONGOING,
		FAILED,
		SUCCESSFUL
	}

	static InitializationState initState = InitializationState.UNINITIALIZED;
	static SubstituteProverFactory TEMP_FACTORY = new SubstituteProverFactory();

	/**
	 * It is our responsibility to track version changes and manage the
	 * compatibility list.
	 */
	static private final String[] API_COMPATIBILITY_LIST = new String[] {
			"1.5.5", "1.5.6", "1.5.7", "1.5.8", "1.5.9" };

	// private constructor prevents instantiation
	private ProverFactory() {
	}

	/**
	 * Force ProverFactory to consider itself uninitialized.
	 * 
	 * <p>
	 * This method is intended to be called by classes (in the same package) for
	 * testing purposes. This method is internal. It can be modified, renamed or
	 * removed at any time without notice.
	 * 
	 * <p>
	 * You are strongly discouraged from calling this method in production code.
	 */
	static void reset() {
		initState = InitializationState.UNINITIALIZED;
		TEMP_FACTORY = new SubstituteProverFactory();
	}

	private final static void performInitialization() {
		bind();
		versionSanityCheck();
		singleImplementationSanityCheck();

	}

	private final static void bind() {
		try {
			// the next line does the binding
			getSingleton();
			initState = InitializationState.SUCCESSFUL;
			emitSubstituteProverWarning();
		} catch (NoClassDefFoundError ncde) {
			initState = InitializationState.FAILED;
			String msg = ncde.getMessage();
			if (msg != null
					&& msg.indexOf("org/prolog4j/impl/StaticProverBinder") != -1) {
				Util.reportFailure("Failed to load class \"org.prolog4j.impl.StaticProverBinder\".");
				Util.reportFailure("See " + NO_STATICPROVERBINDER_URL
						+ " for further details.");

			}
			throw ncde;
		} catch (Exception e) {
			initState = InitializationState.FAILED;
			// we should never get here
			Util.reportFailure("Failed to instantiate prover ["
					+ getSingleton().getProverFactoryClassName() + "]", e);
		}
	}

	private final static void emitSubstituteProverWarning() {
		List<String> proverNameList = TEMP_FACTORY.getProverNameList();
		if (proverNameList.size() == 0)
			return;
		Util.reportFailure("The following provers will not work becasue they were created");
		Util.reportFailure("during the default configuration phase of the underlying Prolog system.");
		Util.reportFailure("See also " + SUBSTITUTE_PROVER_URL);
		for (String proverName: proverNameList)
			Util.reportFailure(proverName);
	}

	private final static void versionSanityCheck() {
		try {
			String requested = StaticProverBinder.REQUESTED_API_VERSION;

			boolean match = false;
			for (int i = 0; i < API_COMPATIBILITY_LIST.length; i++) {
				if (API_COMPATIBILITY_LIST[i].equals(requested)) {
					match = true;
				}
			}
			if (!match) {
				Util.reportFailure("The requested version " + requested
						+ " by your prolog4j binding is not compatible with "
						+ Arrays.asList(API_COMPATIBILITY_LIST).toString());
				Util.reportFailure("See " + VERSION_MISMATCH
						+ " for further details.");
			}
		} catch (java.lang.NoSuchFieldError nsfe) {
			// given our large user base and Prolog4J's commitment to backward
			// compatibility, we cannot cry here. Only for implementations
			// which willingly declare a REQUESTED_API_VERSION field do we
			// emit compatibility warnings.
		} catch (Throwable e) {
			// we should never reach here
			Util.reportFailure("Unexpected problem occured during version sanity check", e);
		}
	}

	// We need to use the name of the StaticProverBinder class, we can't
	// reference the class itself.
	private static String STATIC_PROVER_BINDER_PATH = "org/prolog4j/impl/StaticProverBinder.class";

	private static void singleImplementationSanityCheck() {
		try {
			Enumeration<URL> paths = ProverFactory.class.getClassLoader()
					.getResources(STATIC_PROVER_BINDER_PATH);
			List<URL> implementationList = new ArrayList<URL>();
			while (paths.hasMoreElements())
				implementationList.add(paths.nextElement());
			if (implementationList.size() > 1) {
				Util.reportFailure("Class path contains multiple Prolog4J bindings.");
				for (URL implementation: implementationList)
					Util.reportFailure("Found binding in [" + implementation + "]");
				Util.reportFailure("See " + MULTIPLE_BINDINGS_URL + " for an explanation.");
			}
		} catch (IOException ioe) {
			Util.reportFailure("Error getting resources from path", ioe);
		}
	}

	private final static StaticProverBinder getSingleton() {
		return StaticProverBinder.getSingleton();
	}

	/**
	 * Return a prover named according to the name parameter using the
	 * statically bound {@link IProverFactory} instance.
	 * 
	 * @param name
	 *            The name of the prover.
	 * @return prover
	 */
	public static Prover getProver(String name) {
		IProverFactory iProverFactory = getIProverFactory();
		return iProverFactory.getProver(name);
	}

	/**
	 * Return a prover named corresponding to the class passed as parameter,
	 * using the statically bound {@link IProverFactory} instance.
	 * 
	 * @param clazz
	 *            the returned prover will be named after clazz
	 * @return prover
	 */
	public static Prover getProver(Class<?> clazz) {
		return getProver(clazz.getName());
	}

	/**
	 * Return the {@link IProverFactory} instance in use.
	 * 
	 * <p>
	 * IProverFactory instance is bound with this class at compile time.
	 * 
	 * @return the IProverFactory instance in use
	 */
	public static IProverFactory getIProverFactory() {
		if (initState == InitializationState.UNINITIALIZED) {
			initState = InitializationState.ONGOING;
			performInitialization();
		}
		switch (initState) {
		case SUCCESSFUL:
			return getSingleton().getProverFactory();
		case FAILED:
			throw new IllegalStateException(UNSUCCESSFUL_INIT_MSG);
		case ONGOING:
			// support re-entrant behavior.
			// See also http://bugzilla.slf4j.org/show_bug.cgi?id=106
			return TEMP_FACTORY;
		}
		throw new IllegalStateException("Unreachable code");
	}
}
