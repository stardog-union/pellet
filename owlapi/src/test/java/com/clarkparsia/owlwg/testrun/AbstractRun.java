package com.clarkparsia.owlwg.testrun;

import com.clarkparsia.owlwg.runner.TestRunner;
import com.clarkparsia.owlwg.testcase.TestCase;

/**
 * <p>
 * Title: Abstract Run
 * </p>
 * <p>
 * Description: Base implementation used by other {@link TestRunResult} implementations
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
public abstract class AbstractRun implements TestRunResult
{

	private final Throwable _cause;
	private final String _details;
	private final RunResultType _resultType;
	private final TestRunner<?> _runner;
	private final TestCase<?> _testcase;
	private final RunTestType _testType;

	public AbstractRun(TestCase<?> testcase, RunResultType resultType, RunTestType testType, TestRunner<?> runner, String details, Throwable cause)
	{
		if (testcase == null)
			throw new NullPointerException();
		if (resultType == null)
			throw new NullPointerException();
		if (testType == null)
			throw new NullPointerException();
		if (runner == null)
			throw new NullPointerException();

		this._testcase = testcase;
		this._resultType = resultType;
		this._testType = testType;
		this._runner = runner;
		this._details = details;
		this._cause = cause;
	}

	@Override
	public Throwable getCause()
	{
		return _cause;
	}

	@Override
	public String getDetails()
	{
		return _details;
	}

	@Override
	public RunResultType getResultType()
	{
		return _resultType;
	}

	@Override
	public TestCase<?> getTestCase()
	{
		return _testcase;
	}

	@Override
	public TestRunner<?> getTestRunner()
	{
		return _runner;
	}

	@Override
	public RunTestType getTestType()
	{
		return _testType;
	}

}
