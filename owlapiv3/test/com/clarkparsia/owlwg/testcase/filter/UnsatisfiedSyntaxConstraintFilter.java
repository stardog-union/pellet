package com.clarkparsia.owlwg.testcase.filter;

import com.clarkparsia.owlwg.testcase.SyntaxConstraint;
import com.clarkparsia.owlwg.testcase.TestCase;

/**
 * <p>
 * Title: Unsatisfied Syntax Constraint Filter Condition
 * </p>
 * <p>
 * Description: Filter condition to match tests for which a particular syntax
 * constraint is not satisfied.
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
public class UnsatisfiedSyntaxConstraintFilter implements FilterCondition {

	final private SyntaxConstraint	constraint;

	/**
	 * @throws NullPointerException
	 *             if <code>constraint == null</code>
	 */
	public UnsatisfiedSyntaxConstraintFilter(SyntaxConstraint constraint) {
		if( constraint == null )
			throw new NullPointerException();

		this.constraint = constraint;
	}

	public boolean accepts(TestCase testcase) {
		return testcase.getUnsatisfiedConstraints().contains( constraint );
	}

	@Override
	public String toString() {
		return "!" + constraint.toString();
	}

}
