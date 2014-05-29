package com.clarkparsia.owlwg.testrun;

import com.clarkparsia.owlwg.runner.TestRunner;
import com.clarkparsia.owlwg.testcase.TestCase;

/**
 * <p>
 * Title: Abstract Run
 * </p>
 * <p>
 * Description: Base implementation used by other {@link TestRunResult}
 * implementations
 * </p>
 * <p>
 * Copyright: Copyright &copy; 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <a
 * href="http://clarkparsia.com/"/>http://clarkparsia.com/</a>
 * </p>
 * 
 * @author Mike Smith &lt;msmith@clarkparsia.com&gt;
 */
public abstract class AbstractRun implements TestRunResult {

	private final Throwable		cause;
	private final String		details;
	private final RunResultType	resultType;
	private final TestRunner	runner;
	private final TestCase		testcase;
	private final RunTestType	testType;

	public AbstractRun(TestCase testcase, RunResultType resultType, RunTestType testType,
			TestRunner runner, String details, Throwable cause ) {
		if( testcase == null )
			throw new NullPointerException();
		if( resultType == null )
			throw new NullPointerException();
		if( testType == null )
			throw new NullPointerException();
		if( runner == null )
			throw new NullPointerException();

		this.testcase = testcase;
		this.resultType = resultType;
		this.testType = testType;
		this.runner = runner;
		this.details = details;
		this.cause = cause;
	}

	public Throwable getCause() {
		return cause;
	}
	
	public String getDetails() {
		return details;
	}

	public RunResultType getResultType() {
		return resultType;
	}

	public TestCase getTestCase() {
		return testcase;
	}

	public TestRunner getTestRunner() {
		return runner;
	}

	public RunTestType getTestType() {
		return testType;
	}

}
