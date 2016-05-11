package com.clarkparsia.pellet.datatypes.types.floating;

/**
 * <p>
 * Title: IEEE Double Type
 * </p>
 * <p>
 * Description: Implementation of {@link FloatingPointType} to support <code>xsd:double</code>
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Mike Smith
 */
public class IEEEDoubleType implements FloatingPointType<Double>
{

	private static final IEEEDoubleType INSTANCE;

	static
	{
		INSTANCE = new IEEEDoubleType();
	}

	public static IEEEDoubleType getInstance()
	{
		return INSTANCE;
	}

	private IEEEDoubleType()
	{
		//
	}

	@Override
	public Double cast(final Object o)
	{
		return Double.class.cast(o);
	}

	@Override
	public Double decrement(final Double n)
	{
		return FloatingPointUtils.decrement(n);
	}

	@Override
	public Double getNaN()
	{
		return Double.NaN;
	}

	@Override
	public Double getNegativeInfinity()
	{
		return Double.NEGATIVE_INFINITY;
	}

	@Override
	public Double getPositiveInfinity()
	{
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public Double increment(final Double n)
	{
		return FloatingPointUtils.increment(n);
	}

	@Override
	public Number intervalSize(final Double lower, final Double upper)
	{
		return FloatingPointUtils.intervalSize(lower, upper);
	}

	@Override
	public boolean isInstance(final Object o)
	{
		return Double.class.isInstance(o);
	}

	@Override
	public boolean isNaN(final Double f)
	{
		return f.isNaN();
	}

}
