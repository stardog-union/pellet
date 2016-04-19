package com.clarkparsia.pellet.datatypes.types.real;

import com.clarkparsia.pellet.datatypes.DiscreteInterval;
import com.clarkparsia.pellet.datatypes.OWLRealUtils;

public class IntegerInterval extends DiscreteInterval<Number, IntegerInterval>
{

	private static final IntegerInterval unconstrained;

	static
	{
		unconstrained = new IntegerInterval(null, null);
	}

	public static IntegerInterval allIntegers()
	{
		return unconstrained;
	}

	public IntegerInterval(final Number point)
	{
		super(point);
	}

	public IntegerInterval(final Number lower, final Number upper)
	{
		super(lower, upper);
	}

	@Override
	protected IntegerInterval cast(final DiscreteInterval<Number, IntegerInterval> i)
	{
		if (IntegerInterval.class.isInstance(i))
			return IntegerInterval.class.cast(i);
		else
			throw new IllegalArgumentException();
	}

	@Override
	protected int compare(final Number a, final NullSemantics na, final Number b, final NullSemantics nb)
	{

		if (a == null && NA.equals(na))
			throw new NullPointerException();

		if (b == null && NA.equals(nb))
			throw new NullPointerException();

		if (a == null)
		{
			if (b == null)
			{
				if (na.equals(nb))
					return 0;
				else
					if (LEAST.equals(na))
						return -1;
					else
						return 1;
			}
			else
				if (LEAST.equals(na))
					return -1;
				else
					return 1;
		}
		else
			if (b == null)
			{
				if (GREATEST.equals(nb))
					return -1;
				else
					return 1;
			}
			else
				return OWLRealUtils.compare(a, b);
	}

	@Override
	protected IntegerInterval create(final Number lower, final Number upper)
	{
		return new IntegerInterval(lower, upper);
	}

	@Override
	protected Number decrement(final Number t)
	{
		return OWLRealUtils.integerDecrement(t);
	}

	@Override
	protected boolean equal(final Number a, final Number b)
	{
		if (a == null)
			throw new NullPointerException();
		if (b == null)
			throw new NullPointerException();
		return OWLRealUtils.compare(a, b) == 0;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final IntegerInterval other = (IntegerInterval) obj;
		if (getLower() == null)
		{
			if (other.getLower() != null)
				return false;
		}
		else
		{
			if (other.getLower() == null)
				return false;
			if (OWLRealUtils.compare(getLower(), other.getLower()) != 0)
				return false;
		}
		if (getUpper() == null)
		{
			if (other.getUpper() != null)
				return false;
		}
		else
		{
			if (other.getUpper() == null)
				return false;
			if (OWLRealUtils.compare(getUpper(), other.getUpper()) != 0)
				return false;
		}

		return true;
	}

	@Override
	protected Number increment(final Number n)
	{
		return OWLRealUtils.integerIncrement(n);
	}

	@Override
	public Number size()
	{
		if (getLower() == null || getUpper() == null)
			throw new IllegalStateException();
		return OWLRealUtils.integerIncrement(OWLRealUtils.integerDifference(getUpper(), getLower()));
	}

	@Override
	protected boolean valid(final Number n)
	{
		if (n == null)
			return true;

		return OWLRealUtils.acceptable(n.getClass()) && OWLRealUtils.isInteger(n);
	}

}
