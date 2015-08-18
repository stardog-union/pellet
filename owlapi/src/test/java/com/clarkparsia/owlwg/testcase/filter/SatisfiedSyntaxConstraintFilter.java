package com.clarkparsia.owlwg.testcase.filter;

import com.clarkparsia.owlwg.testcase.SyntaxConstraint;
import com.clarkparsia.owlwg.testcase.TestCase;

/**
 * <p>
 * Title: Satisfied Syntax Constraint Filter Condition
 * </p>
 * <p>
 * Description: Filter condition to match tests for which a particular syntax
 * constraint is satisfied.
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
public class SatisfiedSyntaxConstraintFilter implements FilterCondition {

	public static final SatisfiedSyntaxConstraintFilter	DL, EL, QL, RL;

	static {
		DL = new SatisfiedSyntaxConstraintFilter( SyntaxConstraint.DL );
		EL = new SatisfiedSyntaxConstraintFilter( SyntaxConstraint.EL );
		QL = new SatisfiedSyntaxConstraintFilter( SyntaxConstraint.QL );
		RL = new SatisfiedSyntaxConstraintFilter( SyntaxConstraint.RL );
	}

	final private SyntaxConstraint						constraint;

	/**
	 * @throws NullPointerException
	 *             if <code>constraint == null</code>
	 */
	public SatisfiedSyntaxConstraintFilter(SyntaxConstraint constraint) {
		if( constraint == null )
			throw new NullPointerException();

		this.constraint = constraint;
	}

	public boolean accepts(TestCase testcase) {
		return testcase.getSatisfiedConstraints().contains( constraint );
	}

	@Override
	public String toString() {
		return constraint.toString();
	}

}
