package com.clarkparsia.owlwg.testcase.filter;

import com.clarkparsia.owlwg.testcase.ConsistencyTest;
import com.clarkparsia.owlwg.testcase.InconsistencyTest;
import com.clarkparsia.owlwg.testcase.NegativeEntailmentTest;
import com.clarkparsia.owlwg.testcase.PositiveEntailmentTest;
import com.clarkparsia.owlwg.testcase.TestCase;

/**
 * <p>
 * Title: Test Type Filter Condition
 * </p>
 * <p>
 * Description: Filter condition to match tests with a particular type (e.g.,
 * consistency, negative entailment).
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
public class TestTypeFilter implements FilterCondition {

	public static final TestTypeFilter		CONSISTENCY, INCONSISTENCY, NEGATIVE_ENTAILMENT,
			POSITIVE_ENTAILMENT;

	static {
		CONSISTENCY = new TestTypeFilter( ConsistencyTest.class );
		INCONSISTENCY = new TestTypeFilter( InconsistencyTest.class );
		NEGATIVE_ENTAILMENT = new TestTypeFilter( NegativeEntailmentTest.class );
		POSITIVE_ENTAILMENT = new TestTypeFilter( PositiveEntailmentTest.class );
	}

	private final Class<? extends TestCase>	cls;

	public TestTypeFilter(Class<? extends TestCase> cls) {
		this.cls = cls;
	}

	public boolean accepts(TestCase testcase) {
		return cls.isAssignableFrom( testcase.getClass() );
	}

}
