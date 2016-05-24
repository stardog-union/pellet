package com.clarkparsia.owlwg.testrun;

import com.clarkparsia.owlwg.runner.TestRunner;
import com.clarkparsia.owlwg.testcase.SyntaxConstraint;
import com.clarkparsia.owlwg.testcase.TestCase;

/**
 * <p>
 * Title: Syntax Constraint Run
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
public class SyntaxConstraintRun extends AbstractRun
{

	final private SyntaxConstraint _constraint;

	public SyntaxConstraintRun(TestCase<?> testcase, RunResultType type, SyntaxConstraint constraint, TestRunner<?> runner)
	{
		this(testcase, type, constraint, runner, null, null);
	}

	public SyntaxConstraintRun(TestCase<?> testcase, RunResultType type, SyntaxConstraint constraint, TestRunner<?> runner, String details)
	{
		this(testcase, type, constraint, runner, details, null);
	}

	public SyntaxConstraintRun(TestCase<?> testcase, RunResultType type, SyntaxConstraint constraint, TestRunner<?> runner, String details, Throwable cause)
	{
		super(testcase, type, RunTestType.SYNTAX_CONSTRAINT, runner, details, cause);
		if (constraint == null)
			throw new NullPointerException();
		this._constraint = constraint;
	}

	@Override
	public void accept(TestRunResultVisitor visitor)
	{
		visitor.visit(this);
	}

	public SyntaxConstraint getConstraint()
	{
		return _constraint;
	}
}
