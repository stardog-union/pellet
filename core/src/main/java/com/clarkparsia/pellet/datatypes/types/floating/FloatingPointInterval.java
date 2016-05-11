package com.clarkparsia.pellet.datatypes.types.floating;

import com.clarkparsia.pellet.datatypes.DiscreteInterval;

/**
 * <p>
 * Title: Floating Point Interval
 * </p>
 * <p>
 * Description: An immutable interval representation supporting the value space of floating point numbers.
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
public class FloatingPointInterval<T extends Number & Comparable<T>> extends DiscreteInterval<T, FloatingPointInterval<T>>
{

	public static <U extends Number & Comparable<U>> FloatingPointInterval<U> unconstrained(final FloatingPointType<U> type)
	{
		return new FloatingPointInterval<>(type, type.getNegativeInfinity(), type.getPositiveInfinity());
	}

	private final FloatingPointType<T> _type;

	public FloatingPointInterval(final FloatingPointType<T> type, final T point)
	{
		super(point);

		if (type == null)
			throw new NullPointerException();

		if (type.isNaN(point))
			throw new IllegalArgumentException();

		this._type = type;

		if (!valid(point))
			throw new IllegalArgumentException();
	}

	public FloatingPointInterval(final FloatingPointType<T> type, final T lower, final T upper)
	{
		super(lower, upper);

		if (type == null)
			throw new NullPointerException();

		if (type.isNaN(lower))
			throw new IllegalArgumentException();
		if (type.isNaN(upper))
			throw new IllegalArgumentException();

		this._type = type;

		if (!valid(lower))
			throw new IllegalArgumentException();
		if (!valid(upper))
			throw new IllegalArgumentException();
	}

	@Override
	protected FloatingPointInterval<T> cast(final DiscreteInterval<T, FloatingPointInterval<T>> i)
	{
		if (i instanceof FloatingPointInterval)
			return (FloatingPointInterval<T>) i;
		else
			throw new IllegalArgumentException();
	}

	@Override
	protected int compare(final T a, final NullSemantics na, final T b, final NullSemantics nb)
	{
		if (a == null)
			throw new NullPointerException();
		if (b == null)
			throw new NullPointerException();

		return a.compareTo(b);
	}

	@Override
	public boolean contains(final T n)
	{
		if (_type.isNaN(n))
			return false;

		return super.contains(n);
	}

	@Override
	protected FloatingPointInterval<T> create(final T lower, final T upper)
	{
		return new FloatingPointInterval<>(_type, lower, upper);
	}

	@Override
	protected T decrement(final T t)
	{
		return _type.decrement(t);
	}

	@Override
	protected boolean equal(final T a, final T b)
	{
		return a.equals(b);
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
		final FloatingPointInterval<?> other = (FloatingPointInterval<?>) obj;
		if (!getLower().equals(other.getLower()))
			return false;
		if (!getUpper().equals(other.getUpper()))
			return false;

		return true;
	}

	@Override
	public FloatingPointInterval<T> greater(final T n)
	{
		if (_type.isNaN(n))
			throw new IllegalArgumentException();
		return super.greater(n);
	}

	@Override
	protected T increment(final T t)
	{
		return _type.increment(t);
	}

	@Override
	public FloatingPointInterval<T> less(final T n)
	{
		if (_type.isNaN(n))
			throw new IllegalArgumentException();
		return super.less(n);
	}

	@Override
	public Number size()
	{
		return _type.intervalSize(getLower(), getUpper());
	}

	@Override
	protected boolean valid(final T t)
	{
		/*
		 * The _type == null check here is necessary because this method is
		 * called by the super's constructor before the _type field is
		 * initialized
		 */
		return t != null && (_type == null || _type.isInstance(t));
	}
}
