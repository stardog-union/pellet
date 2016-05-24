package com.clarkparsia.owlwg.testcase.filter;

import com.clarkparsia.owlwg.testcase.TestCase;
import java.util.Arrays;
import java.util.Collection;

/**
 * <p>
 * Title: Disjunction Filter Condition
 * </p>
 * <p>
 * Description: Filter _condition that acts as a _disjunction of other filter _conditions
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
public class DisjunctionFilter implements FilterCondition
{

	public static DisjunctionFilter or(Collection<? extends FilterCondition> conditions)
	{
		return new DisjunctionFilter(conditions);
	}

	public static DisjunctionFilter or(FilterCondition... conditions)
	{
		return or(Arrays.asList(conditions));
	}

	final private FilterCondition[] _conditions;

	public DisjunctionFilter(Collection<? extends FilterCondition> conditions)
	{
		if (conditions == null)
			throw new NullPointerException();

		this._conditions = conditions.toArray(new FilterCondition[0]);
	}

	public DisjunctionFilter(FilterCondition... conditions)
	{
		final int n = conditions.length;

		this._conditions = new FilterCondition[n];
		System.arraycopy(conditions, 0, this._conditions, 0, n);
	}

	@Override
	public boolean accepts(TestCase<?> testcase)
	{
		for (final FilterCondition c : _conditions)
			if (c.accepts(testcase))
				return true;

		return false;
	}

	@Override
	public String toString()
	{
		final StringBuffer buf = new StringBuffer();
		for (int i = 0; i < _conditions.length; i++)
		{
			buf.append(_conditions[i].toString());
			buf.append(" ");
		}
		buf.append("or");
		return buf.toString();
	}
}
