package com.clarkparsia.owlwg.testcase.filter;

import com.clarkparsia.owlwg.testcase.TestCase;

/**
 * <p>
 * Title: Negation Filter Condition
 * </p>
 * <p>
 * Description: Filter condition that acts as a negation of another filter
 * condition
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
public class NegationFilter implements FilterCondition {

	public final static NegationFilter not(FilterCondition condition) {
		return new NegationFilter( condition );
	}

	final private FilterCondition	condition;

	public NegationFilter(FilterCondition condition) {
		if( condition == null )
			throw new NullPointerException();

		this.condition = condition;
	}

	public boolean accepts(TestCase testcase) {
		return !condition.accepts( testcase );
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append( condition.toString() );
		buf.append( " not" );
		return buf.toString();
	}
}
