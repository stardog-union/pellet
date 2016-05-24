package com.clarkparsia.pellet.datatypes;

import static java.lang.String.format;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import net.katk.tools.Log;

/**
 * <p>
 * Title: Discrete Interval
 * </p>
 * <p>
 * Description: An abstract base class interval representation of discrete value spaces. Instances are immutable.
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
public abstract class DiscreteInterval<T extends Number, U extends DiscreteInterval<T, U>>
{

	protected static enum NullSemantics
	{
		@SuppressWarnings("hiding")
		GREATEST, //
		@SuppressWarnings("hiding")
		LEAST, //
		@SuppressWarnings("hiding")
		NA
	}

	public class ValueIterator implements Iterator<T>
	{
		private final T _last;
		private T _next;
		private final boolean _increment;

		public ValueIterator(final T lower, final T upper, final boolean increment)
		{
			if (lower == null)
				throw new NullPointerException();

			this._next = lower;
			this._last = upper;
			this._increment = increment;
		}

		@Override
		public boolean hasNext()
		{
			return _next != null;
		}

		@Override
		public T next()
		{
			final T ret = _next;
			if (getUpper() != null && equal(_next, _last))
				_next = null;
			else
				_next = _increment ? increment(_next) : decrement(_next);
				return ret;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}

	}

	/*
	 * These constants are setup so that reference to the NullSemantics enum
	 * does not need qualification within this class.
	 */
	protected final static NullSemantics// 
			GREATEST = NullSemantics.GREATEST,//
			LEAST = NullSemantics.LEAST,//
			NA = NullSemantics.NA;

	private static final Logger log = Log.getLogger(DiscreteInterval.class);

	private final T _lower;
	private final T _upper;

	/**
	 * Create a point interval. This is equivalent to {@link #DiscreteInterval(T, T)} with arguments <code>point,point</code>
	 *
	 * @param point Value of point interval
	 */
	public DiscreteInterval(final T point)
	{
		if (point == null)
			throw new NullPointerException();

		this._lower = point;
		this._upper = point;
	}

	/**
	 * Create an interval.
	 *
	 * @param _lower Interval _lower bound
	 * @param _upper Interval _upper bound
	 */
	public DiscreteInterval(final T lower, final T upper)
	{
		if (!valid(lower))
			throw new IllegalArgumentException();
		if (!valid(upper))
			throw new IllegalArgumentException();

		final int cmp = compare(lower, LEAST, upper, GREATEST);
		if (cmp > 0)
		{
			final String msg = format("Lower bound of interval (%s) should not be greater than _upper bound of interval (%s)", lower, upper);
			log.severe(msg);
			throw new IllegalArgumentException(msg);
		}

		this._lower = lower;
		this._upper = upper;
	}

	public boolean canUnionWith(final U other)
	{
		final int ll = compare(this.getLower(), LEAST, other.getLower(), LEAST);
		final int uu = compare(this.getUpper(), GREATEST, other.getUpper(), GREATEST);
		if (ll <= 0)
		{
			if (uu < 0)
			{
				if (compare(this.getUpper(), GREATEST, other.getLower(), LEAST) < 0)
				{
					if (equal(increment(this.getUpper()), other.getLower()))
						return true;
					else
						return false;
				}
				else
					return true;
			}
			else
				return true;
		}
		else
			if (uu > 0)
			{
				if (compare(this.getLower(), LEAST, other.getUpper(), GREATEST) > 0)
				{
					if (equal(increment(other.getUpper()), this.getLower()))
						return true;
					else
						return false;
				}
				else
					return true;
			}
			else
				return true;
	}

	protected abstract U cast(DiscreteInterval<T, U> i);

	protected abstract int compare(T a, NullSemantics na, T b, NullSemantics nb);

	public boolean contains(final T n)
	{
		if (!valid(n))
			throw new IllegalArgumentException();

		final int lcmp = compare(getLower(), LEAST, n, NA);
		if (lcmp > 0)
			return false;
		if (lcmp == 0)
			return true;

		final int ucmp = compare(getUpper(), GREATEST, n, NA);
		if (ucmp < 0)
			return false;

		return true;
	}

	protected abstract U create(T lower, T upper);

	protected abstract T decrement(T t);

	protected abstract boolean equal(T a, T b);

	@Override
	public abstract boolean equals(Object obj);

	public T getLower()
	{
		return _lower;
	}

	public T getUpper()
	{
		return _upper;
	}

	/**
	 * Get the subinterval greater than n
	 *
	 * @param n
	 * @return a new interval, formed by intersecting this interval with (n,+inf) or <code>null</code> if that intersection is empty
	 */
	public U greater(final T n)
	{
		if (n == null)
			throw new NullPointerException();
		if (!valid(n))
			throw new IllegalArgumentException();

		if (compare(getLower(), LEAST, n, NA) > 0)
			return cast(this);
		else
			if (compare(getUpper(), GREATEST, n, NA) <= 0)
				return null;
			else
				return create(increment(n), getUpper());
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getLower() == null) ? 0 : getLower().hashCode());
		result = prime * result + ((getUpper() == null) ? 0 : getUpper().hashCode());
		return result;
	}

	protected abstract T increment(T t);

	public U intersection(final U that)
	{

		final int ll = compare(this.getLower(), LEAST, that.getLower(), LEAST);
		final int uu = compare(this.getUpper(), GREATEST, that.getUpper(), GREATEST);
		if (ll <= 0)
		{
			if (uu < 0)
			{
				if (compare(this.getUpper(), GREATEST, that.getLower(), LEAST) < 0)
					return null;
				else
					return create(that.getLower(), this.getUpper());
			}
			else
				return that;
		}
		else
			if (uu > 0)
			{
				if (compare(this.getLower(), LEAST, that.getUpper(), GREATEST) > 0)
					return null;
				else
					return create(this.getLower(), that.getUpper());
			}
			else
				return cast(this);
	}

	public boolean isFinite()
	{
		return getLower() != null && getUpper() != null;
	}

	/**
	 * Get the subinterval less than n
	 *
	 * @param n
	 * @return a new interval, formed by intersecting this interval with (-inf,n) or <code>null</code> if that intersection is empty
	 */
	public U less(final T n)
	{
		if (n == null)
			throw new NullPointerException();
		if (!valid(n))
			throw new IllegalArgumentException();

		if (compare(getUpper(), GREATEST, n, NA) < 0)
			return cast(this);
		else
			if (compare(getLower(), LEAST, n, NA) >= 0)
				return null;
			else
				return create(getLower(), decrement(n));
	}

	public List<U> remove(final U other)
	{

		U before, after;

		final int ll = compare(this.getLower(), LEAST, other.getLower(), LEAST);
		final int lu = compare(this.getLower(), LEAST, other.getUpper(), GREATEST);
		final int ul = compare(this.getUpper(), GREATEST, other.getLower(), LEAST);
		final int uu = compare(this.getUpper(), GREATEST, other.getUpper(), GREATEST);

		if (ll < 0)
		{
			if (ul < 0)
			{
				before = cast(this);
				after = null;
			}
			else
			{
				before = create(this.getLower(), decrement(other.getLower()));
				if (uu <= 0)
					after = null;
				else
					after = create(increment(other.getUpper()), this.getUpper());
			}
		}
		else
			if (lu > 0)
			{
				before = null;
				after = cast(this);
			}
			else
				if (uu <= 0)
				{
					before = null;
					after = null;
				}
				else
				{
					before = null;
					after = create(increment(other.getUpper()), this.getUpper());
				}

		if (before == null)
			if (after == null)
				return Collections.emptyList();
			else
				return Collections.singletonList(after);
		else
			if (after == null)
				return Collections.singletonList(before);
			else
			{
				return Arrays.asList(before, after);
			}
	}

	public abstract Number size();

	public List<U> union(final U other)
	{
		U first, second;

		final int ll = compare(this.getLower(), LEAST, other.getLower(), LEAST);
		final int lu = compare(this.getLower(), LEAST, other.getUpper(), GREATEST);
		final int ul = compare(this.getUpper(), GREATEST, other.getLower(), LEAST);
		final int uu = compare(this.getUpper(), GREATEST, other.getUpper(), GREATEST);

		if (ll < 0)
		{
			if (ul < 0)
			{
				first = cast(this);
				second = other;
			}
			else
			{
				second = null;
				if (uu < 0)
					first = create(this.getLower(), other.getUpper());
				else
					first = cast(this);
			}
		}
		else
			if (lu > 0)
			{
				first = other;
				second = cast(this);
			}
			else
			{
				second = null;
				if (uu <= 0)
					first = other;
				else
					first = create(other.getLower(), this.getUpper());
			}

		if (first == null)
			if (second == null)
				return Collections.emptyList();
			else
				return Collections.singletonList(second);
		else
			if (second == null)
				return Collections.singletonList(first);
			else
				return Arrays.asList(first, second);
	}

	protected abstract boolean valid(T t);

	public Iterator<T> valueIterator()
	{
		if (getLower() == null)
		{
			if (getUpper() == null)
				throw new IllegalStateException();
			else
				return new ValueIterator(getUpper(), getLower(), false);
		}
		else
			return new ValueIterator(getLower(), getUpper(), true);
	}

	@Override
	public String toString()
	{
		return format("[%s,%s]", getLower() != null ? getLower() : "-Inf", getUpper() != null ? getUpper() : "+Inf");
	}
}
