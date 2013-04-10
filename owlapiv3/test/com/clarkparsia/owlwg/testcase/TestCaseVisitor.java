package com.clarkparsia.owlwg.testcase;

/**
 * <p>
 * Title: Test Case Visitor
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

public interface TestCaseVisitor<O> {

	public void visit(ConsistencyTest<O> testcase);

	public void visit(InconsistencyTest<O> testcase);

	public void visit(PositiveEntailmentTest<O> testcase);

	public void visit(NegativeEntailmentTest<O> testcase);
}
