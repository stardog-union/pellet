package com.clarkparsia.owlwg.testcase.filter;

import com.clarkparsia.owlwg.testcase.TestCase;

/**
 * <p>
 * Title: Filter Condition
 * </p>
 * <p>
 * Description: Filter test cases based on arbitrary criteria.
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
public interface FilterCondition {

	/**
	 * Test a {@link TestCase} against a _condition
	 * 
	 * @param testcase
	 *            The {@link TestCase} to evaluate
	 * @return <code>true</code> if the filter _condition accepts the test case,
	 *         <code>false</code> otherwise
	 */
	public boolean accepts(TestCase testcase);

	/**
	 * Filter _condition which accepts all test cases. Useful as a default
	 * _condition.
	 */
	public final FilterCondition	ACCEPT_ALL	= new FilterCondition() {
													public boolean accepts(TestCase testcase) {
														return true;
													}
												};
}
