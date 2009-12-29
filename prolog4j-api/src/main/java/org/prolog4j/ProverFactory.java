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

/**
 * The <code>ProverFactory</code> is a utility class producing Provers for
 * various Prolog APIs, most notably for tuProlog, jTrolog and JLog.
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
 * @author Miklós Espák
 */
public final class ProverFactory {

//	static final String NO_PROVER_FACTORY_BINDER_URL = 
//		"http://prolog4j.org/codes.html#ProverFactoryBinder";
//	static final String MULTIPLE_BINDINGS_URL = "http://prolog4j.org/codes.html#multiple_bindings";
//	static final String NULL_LF_URL = "http://prolog4j.org/codes.html#null_LF";
//	static final String VERSION_MISMATCH = "http://prolog4j.org/codes.html#version_mismatch";

//	static final String UNSUCCESSFUL_INIT_URL = "http://prolog4j.org/codes.html#unsuccessfulInit";
	
	/**
	 * The error message when the initialization of the prover factory was 
	 * unsuccessful.
	 */
	static final String UNSUCCESSFUL_INIT_MSG = 
		"org.prolog4j.ProverFactory could not be successfully initialized.";
//		+ " See also " + UNSUCCESSFUL_INIT_URL;

	/**
	 * The possible states of the initialization of the ProverFactory instance.
	 */
	private static enum InitializationState {
		/** The initialization has not been started yet. */
		UNINITIALIZED,
		/** The initialization has been started. */
		ONGOING,
		/** The initialization has been failed. */
		FAILED,
		/** The initialization has been finished successfully. */
		SUCCESSFUL
	}

	/**
	 * The state of the initialization of the ProverFactory instance.
	 */
	private static InitializationState initState = InitializationState.UNINITIALIZED;

	/**
	 * It is our responsibility to track version changes and manage the
	 * compatibility list.
	 */
	private static final String[] API_COMPATIBILITY_LIST = {
		"0.1.0", "0.1.1", "0.1.2" };

	/**
	 * Private constructor to prevent instantiation.
	 */
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
	}

	/**
	 * Initializes the factory. Creates the instance and performs checks.
	 */
	private static void performInitialization() {
		bind();
		versionSanityCheck();
		singleImplementationSanityCheck();

	}

	/**
	 * Creates the ProverFactory instance.
	 */
	private static void bind() {
		try {
			// the next line does the binding
			getSingleton();
			initState = InitializationState.SUCCESSFUL;
		} catch (NoClassDefFoundError ncde) {
			initState = InitializationState.FAILED;
			String msg = ncde.getMessage();
			if (msg != null
					&& msg.indexOf("org/prolog4j/ProverFactoryBinder") != -1) {
				reportFailure("Failed to load class \"org.prolog4j.ProverFactoryBinder\".");
//				reportFailure("See " + NO_PROVER_FACTORY_BINDER_URL
//						+ " for further details.");
			}
			throw ncde;
		} catch (Exception e) {
			initState = InitializationState.FAILED;
			// we should never get here
			reportFailure("Failed to instantiate prover ["
					+ getSingleton().getProverFactoryClassName() + "]", e);
		}
	}

	/**
	 * Checks if the current binding is compatible with the version of the API.
	 */
	private static void versionSanityCheck() {
		try {
			String requested = ProverFactoryBinder.REQUESTED_API_VERSION;

			for (String compatibleVersion : API_COMPATIBILITY_LIST) {
				if (compatibleVersion.equals(requested)) {
					return;
				}
			}
			reportFailure("The requested version " + requested
					+ " by your Prolog4J binding is not compatible with "
					+ Arrays.toString(API_COMPATIBILITY_LIST));
//			reportFailure("See " + VERSION_MISMATCH + " for further details.");
		} catch (java.lang.NoSuchFieldError nsfe) {
			reportFailure("The binding does not declare its compatibility list.");
		} catch (Throwable e) {
			// we should never reach here
			reportFailure("Unexpected problem occured during version sanity check", e);
		}
	}

	/**
	 * We need to use the name of the ProverFactoryBinder class, we can't
	 * reference the class itself.
	 */
	private static final String PROVER_FACTORY_BINDER_PATH =
			"org/prolog4j/ProverFactoryBinder.class";

	/**
	 * Checks if there is only one Prolog4J binding in the class path.
	 */
	private static void singleImplementationSanityCheck() {
		try {
			Enumeration<URL> paths = ProverFactory.class.getClassLoader()
					.getResources(PROVER_FACTORY_BINDER_PATH);
			List<URL> implementationList = new ArrayList<URL>();
			while (paths.hasMoreElements()) {
				implementationList.add(paths.nextElement());
			}
			if (implementationList.size() > 1) {
				reportFailure("Class path contains multiple Prolog4J bindings.");
				for (URL implementation : implementationList) {
					reportFailure("Found binding in [" + implementation + "]");
				}
//				reportFailure("See " + MULTIPLE_BINDINGS_URL + " for an explanation.");
			}
		} catch (IOException ioe) {
			reportFailure("Error getting resources from path", ioe);
		}
	}

	/**
	 * Returns the ProverFactory instance.
	 * @return the ProverFactory instance
	 */
	private static ProverFactoryBinder getSingleton() {
		return ProverFactoryBinder.getSingleton();
	}

	/**
	 * Creates a prover using the statically bound {@link IProverFactory} 
	 * instance.
	 * 
	 * @return prover
	 */
	public static Prover getProver() {
		return getIProverFactory().getProver();
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
		return getIProverFactory().getProver(name);
	}

	/**
	 * Return a prover named corresponding to the class passed as parameter,
	 * using the statically bound {@link IProverFactory} instance.
	 * 
	 * @param clazz
	 *            the returned prover will be named after clazz
	 * @return prover
	 */
	public static Prover getProver(final Class<?> clazz) {
		return getProver(clazz.getName());
	}
	
	/**
	 * Returns the global conversion policy. Every prover use this policy by
	 * default.
	 * 
	 * @return the global conversion policy
	 */
	public static ConversionPolicy getConversionPolicy() {
		return getIProverFactory().getConversionPolicy();
	}

	/**
	 * Return the {@link IProverFactory} instance in use.
	 * 
	 * <p>
	 * IProverFactory instance is bound with this class at compile time.
	 * 
	 * @return the IProverFactory instance in use
	 */
	private static IProverFactory getIProverFactory() {
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
			throw new IllegalStateException(UNSUCCESSFUL_INIT_MSG);
		default:
			throw new IllegalStateException("Unreachable code");
		}
	}

	/**
	 * Reports a failure to the standard error.
	 * 
	 * @param message the message
	 * @param t the exception or error that caused the failure
	 */
	private static void reportFailure(String message, Throwable t) {
		System.err.println(message);
		System.err.println("Reported exception:");
		t.printStackTrace();
	}

	/**
	 * Reports a failure to the standard error.
	 * 
	 * @param message the message
	 */
	private static void reportFailure(String message) {
		System.err.printf("Prolog4J: %s\n", message);
	}

}
