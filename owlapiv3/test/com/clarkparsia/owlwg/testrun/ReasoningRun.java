package com.clarkparsia.owlwg.testrun;

import static com.clarkparsia.owlwg.testrun.RunTestType.CONSISTENCY;
import static com.clarkparsia.owlwg.testrun.RunTestType.INCONSISTENCY;
import static com.clarkparsia.owlwg.testrun.RunTestType.NEGATIVE_ENTAILMENT;
import static com.clarkparsia.owlwg.testrun.RunTestType.POSITIVE_ENTAILMENT;

import java.util.EnumSet;

import com.clarkparsia.owlwg.runner.TestRunner;
import com.clarkparsia.owlwg.testcase.TestCase;

/**
 * <p>
 * Title: Reasoning Run
 * </p>
 * <p>
 * Description:
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
public class ReasoningRun extends AbstractRun {

	public ReasoningRun(TestCase testcase, RunResultType resultType, RunTestType testType,
			TestRunner runner) {
		this( testcase, resultType, testType, runner, null, null );
	}

	public ReasoningRun(TestCase testcase, RunResultType resultType, RunTestType testType,
			TestRunner runner, String details ) {
		this( testcase, resultType, testType, runner, details, null );
	}
	
	public ReasoningRun(TestCase testcase, RunResultType resultType, RunTestType testType,
			TestRunner runner, String details, Throwable cause ) {
		super( testcase, resultType, testType, runner, details, cause );
		if( !EnumSet.of( CONSISTENCY, INCONSISTENCY, NEGATIVE_ENTAILMENT, POSITIVE_ENTAILMENT )
				.contains( testType ) )
			throw new IllegalArgumentException();
	}

	public void accept(TestRunResultVisitor visitor) {
		visitor.visit( this );
	}

	@Override
	public String toString() {
		String details = getDetails();
		if( details == null )
			return String.format( "Result( %s , %s, %s)", getTestCase(), getResultType(),
					getTestType() );
		else
			return String.format( "Result( %s , %s, %s (%s))", getTestCase(), getResultType(),
					getTestType(), details );
	}
}
