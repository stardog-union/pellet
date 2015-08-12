package com.clarkparsia.owlwg.testrun;

/**
 * <p>
 * Title: Test Run Result Visitor
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
public interface TestRunResultVisitor {

	public void visit(SyntaxConstraintRun result);

	public void visit(ReasoningRun result);

	public void visit(SyntaxTranslationRun result);

}
