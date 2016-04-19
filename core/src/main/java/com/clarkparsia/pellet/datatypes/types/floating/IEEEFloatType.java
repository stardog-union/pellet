package com.clarkparsia.pellet.datatypes.types.floating;

/**
 * <p>
 * Title: IEEE Float Type
 * </p>
 * <p>
 * Description: Implementation of {@link FloatingPointType} to support <code>xsd:float</code>
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
public class IEEEFloatType implements FloatingPointType<Float>
{

	private static final IEEEFloatType INSTANCE;

	static
	{
		INSTANCE = new IEEEFloatType();
	}

	public static IEEEFloatType getInstance()
	{
		return INSTANCE;
	}

	private IEEEFloatType()
	{
		;
	}

	@Override
	public Float cast(final Object o)
	{
		return Float.class.cast(o);
	}

	@Override
	public Float decrement(final Float n)
	{
		return FloatingPointUtils.decrement(n);
	}

	@Override
	public Float getNaN()
	{
		return Float.NaN;
	}

	@Override
	public Float getNegativeInfinity()
	{
		return Float.NEGATIVE_INFINITY;
	}

	@Override
	public Float getPositiveInfinity()
	{
		return Float.POSITIVE_INFINITY;
	}

	@Override
	public Float increment(final Float n)
	{
		return FloatingPointUtils.increment(n);
	}

	@Override
	public Number intervalSize(final Float lower, final Float upper)
	{
		return FloatingPointUtils.intervalSize(lower, upper);
	}

	@Override
	public boolean isInstance(final Object o)
	{
		return Float.class.isInstance(o);
	}

	@Override
	public boolean isNaN(final Float f)
	{
		return f.isNaN();
	}

}
