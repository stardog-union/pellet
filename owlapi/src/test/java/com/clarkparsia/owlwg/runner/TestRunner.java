package com.clarkparsia.owlwg.runner;

import com.clarkparsia.owlwg.testcase.TestCase;
import com.clarkparsia.owlwg.testrun.TestRunResult;
import java.util.Collection;
import org.semanticweb.owlapi.model.IRI;

/**
 * <p>
 * Title: Test Runner
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright &copy; 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <a href="http://clarkparsia.com/"/>http://clarkparsia.com/</a>
 * </p>
 * 
 * @author Mike Smith &lt;msmith@clarkparsia.com&gt;
 */
public interface TestRunner<O>
{

	/**
	 * Get the test runner name.
	 * 
	 * @return {@link String} identifying the test runner
	 */
	public String getName();

	/**
	 * Get a IRI identifying the test runner. Used as the target of {@code results:runner} object property assertions.
	 * 
	 * @return {@link IRI} identifying the test runner
	 */
	public IRI getIRI();

	/**
	 * Run a test case with this runner. May produce multiple results because a single test case can be used in multiple ways. E.g., a single positive
	 * entailment test can be used as a profile identification test, a consistency test, and a positive entailment test.
	 * 
	 * @param testcase
	 * @param timeout in milliseconds
	 * @return a collection of {@link TestRunResult} objects describing all tests attempted.
	 */
	public Collection<TestRunResult> run(TestCase<O> testcase, long timeout);

	/**
	 * Dispose the test runner.
	 * 
	 * @throws OWLReasonerException if the runner cannot be disposed.
	 */
	default void dispose()
	{
		// Nothing to do in most of case.
	}
}
