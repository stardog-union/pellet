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
public class FloatingPointInterval<T extends Number & Comparable<T>>
{

	public class ValueIterator implements Iterator<T>
	{
		private final T _last;
		private T _next;

		public ValueIterator(final T lower, final T upper)
		{
			if (lower == null)
				throw new NullPointerException();
			if (upper == null)
				throw new NullPointerException();

			this._next = lower;
			this._last = upper;
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
			if (_next.equals(_last))
				_next = null;
			else
				_next = _type.increment(_next);
			return ret;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}

	}

	private static final Logger _logger = Log.getLogger(FloatingPointInterval.class);

	public static <U extends Number & Comparable<U>> FloatingPointInterval<U> unconstrained(final FloatingPointType<U> type)
	{
		return new FloatingPointInterval<>(type, type.getNegativeInfinity(), type.getPositiveInfinity());
	}

	private final T _lower;
	private final FloatingPointType<T> _type;
	private final T _upper;

	/**
	 * Create a point interval. This is equivalent to {@link #IEEEFloatInterval(Float, Float)} with arguments <code>point,point,true,true</code>
	 *
	 * @param point Value of point interval
	 */
	public FloatingPointInterval(final FloatingPointType<T> type, final T point)
	{
		if (type == null)
			throw new NullPointerException();

		if (point == null)
			throw new NullPointerException();
		if (type.isNaN(point))
			throw new IllegalArgumentException();

		this._type = type;
		this._lower = point;
		this._upper = point;
	}

	/**
	 * Create an interval.
	 *
	 * @param _lower Interval _lower bound
	 * @param _upper Interval _upper bound
	 */
	public FloatingPointInterval(final FloatingPointType<T> type, final T lower, final T upper)
	{
		if (type == null)
			throw new NullPointerException();

		if (lower == null)
			throw new NullPointerException();
		if (upper == null)
			throw new NullPointerException();
		if (type.isNaN(lower))
			throw new IllegalArgumentException();
		if (type.isNaN(upper))
			throw new IllegalArgumentException();

		final int cmp = lower.compareTo(upper);
		if (cmp > 0)
		{
			final String msg = format("Lower bound of interval (%s) should not be greater than _upper bound of interval (%s)", lower, upper);
			_logger.severe(msg);
			throw new IllegalArgumentException(msg);
		}

		this._type = type;
		this._lower = lower;
		this._upper = upper;
	}

	public boolean canUnionWith(final FloatingPointInterval<T> other)
	{
		final int ll = this._lower.compareTo(other._lower);
		final int uu = this._upper.compareTo(other._upper);
		if (ll <= 0)
		{
			if (uu < 0)
			{
				if (this._upper.compareTo(other._lower) < 0)
				{
					if (_type.increment(this._upper).equals(other._lower))
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
				if (this._lower.compareTo(other._upper) > 0)
				{
					if (_type.increment(other._upper).equals(this._lower))
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

	public boolean contains(final T n)
	{
		if (_type.isNaN(n))
			return false;

		final int lcmp = getLower().compareTo(n);
		if (lcmp > 0)
			return false;
		if (lcmp == 0)
			return true;

		final int ucmp = getUpper().compareTo(n);
		if (ucmp < 0)
			return false;

		return true;
	}

	private FloatingPointInterval<T> create(final T lower, final T upper)
	{
		return new FloatingPointInterval<>(_type, lower, upper);
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
		if (!_lower.equals(other._lower))
			return false;
		if (!_upper.equals(other._upper))
			return false;

		return true;
	}

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
	public FloatingPointInterval<T> greater(final T n)
	{
		if (n == null)
			throw new NullPointerException();

		if (_type.isNaN(n))
			throw new IllegalArgumentException();

		if (getLower().compareTo(n) >= 0)
			return this;
		else
			if (getUpper().compareTo(n) <= 0)
				return null;
			else
				return create(_type.increment(n), getUpper());
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_type == null) ? 0 : _type.hashCode());
		result = prime * result + ((_lower == null) ? 0 : _lower.hashCode());
		result = prime * result + ((_upper == null) ? 0 : _upper.hashCode());
		return result;
	}

	public FloatingPointInterval<T> intersection(final FloatingPointInterval<T> that)
	{

		final int ll = this._lower.compareTo(that._lower);
		final int uu = this._upper.compareTo(that._upper);
		if (ll <= 0)
		{
			if (uu < 0)
			{
				if (this._upper.compareTo(that._lower) < 0)
					return null;
				else
					return create(that._lower, this._upper);
			}
			else
				return that;
		}
		else
			if (uu > 0)
			{
				if (this._lower.compareTo(that._upper) > 0)
					return null;
				else
					return create(this._lower, that._upper);
			}
			else
				return this;
	}

	/**
	 * Get the subinterval less than n
	 *
	 * @param n
	 * @return a new interval, formed by intersecting this interval with (-inf,n) or <code>null</code> if that intersection is empty
	 */
	public FloatingPointInterval<T> less(final T n)
	{
		if (n == null)
			throw new NullPointerException();

		if (_type.isNaN(n))
			throw new IllegalArgumentException();

		if (getUpper().compareTo(n) <= 0)
			return this;
		else
			if (getLower().compareTo(n) >= 0)
				return null;
			else
				return create(getLower(), _type.decrement(n));
	}

	public List<FloatingPointInterval<T>> remove(final FloatingPointInterval<T> other)
	{

		FloatingPointInterval<T> before, after;

		final int ll = this._lower.compareTo(other._lower);
		final int lu = this._lower.compareTo(other._upper);
		final int ul = this._upper.compareTo(other._lower);
		final int uu = this._upper.compareTo(other._upper);

		if (ll < 0)
		{
			if (ul < 0)
			{
				before = this;
				after = null;
			}
			else
			{
				{
					final T f = _type.decrement(other._lower);
					if (f.equals(_type.getNegativeInfinity()))
						before = null;
					else
						before = create(this._lower, f);
				}
				if (uu <= 0)
					after = null;
				else
				{
					final T f = _type.increment(other._upper);
					if (f.equals(_type.getPositiveInfinity()))
						after = null;
					else
						after = create(_type.increment(other._upper), this._upper);
				}
			}
		}
		else
			if (lu > 0)
			{
				before = null;
				after = this;
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
					final T f = _type.increment(other._upper);
					if (f.equals(_type.getPositiveInfinity()))
						after = create(_type.increment(other._upper), this._upper);
					else
						after = null;
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
				return Arrays.asList(before, after);
	}

	public Number size()
	{
		return _type.intervalSize(_lower, _upper);
	}

	public List<FloatingPointInterval<T>> union(final FloatingPointInterval<T> other)
	{
		FloatingPointInterval<T> first, second;

		final int ll = this._lower.compareTo(other._lower);
		final int lu = this._lower.compareTo(other._upper);
		final int ul = this._upper.compareTo(other._lower);
		final int uu = this._upper.compareTo(other._upper);

		if (ll < 0)
		{
			if (ul < 0)
			{
				first = this;
				second = other;
			}
			else
			{
				second = null;
				if (uu < 0)
					first = create(this._lower, other._upper);
				else
					first = this;
			}
		}
		else
			if (lu > 0)
			{
				first = other;
				second = this;
			}
			else
			{
				second = null;
				if (uu <= 0)
					first = other;
				else
					first = create(other._lower, this._upper);
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

	public Iterator<T> valueIterator()
	{
		return new ValueIterator(_lower, _upper);
	}
}
