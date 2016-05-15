package com.clarkparsia.owlwg.testcase.filter;

import java.util.Arrays;
import java.util.Collection;

import com.clarkparsia.owlwg.testcase.TestCase;

/**
 * <p>
 * Title: Conjunction Filter Condition
 * </p>
 * <p>
 * Description: Filter _condition that acts as a conjunction of other filter
 * conditions
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
public class ConjunctionFilter implements FilterCondition {

	public static ConjunctionFilter and(Collection<? extends FilterCondition> conditions) {
		return new ConjunctionFilter( conditions );
	}

	public static ConjunctionFilter and(FilterCondition... conditions) {
		return and( Arrays.asList( conditions ) );
	}

	final private FilterCondition[]	conditions;

	public ConjunctionFilter(Collection<? extends FilterCondition> conditions) {
		if( conditions == null )
			throw new NullPointerException();

		this.conditions = conditions.toArray( new FilterCondition[0] );
	}

	public ConjunctionFilter(FilterCondition... conditions) {
		final int n = conditions.length;

		this.conditions = new FilterCondition[n];
		System.arraycopy( conditions, 0, this.conditions, 0, n );
	}

	public boolean accepts(TestCase testcase) {
		for( FilterCondition c : conditions )
			if( !c.accepts( testcase ) )
				return false;

		return true;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for( int i = 0; i < conditions.length; i++ ) {
			buf.append( conditions[i].toString() );
			buf.append( " " );
		}
		buf.append( "and" );
		return buf.toString();
	}
}
