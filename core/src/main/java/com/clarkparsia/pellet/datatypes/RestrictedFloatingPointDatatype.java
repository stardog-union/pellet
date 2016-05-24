package com.clarkparsia.pellet.datatypes;

import static java.lang.String.format;

import aterm.ATermAppl;
import com.clarkparsia.pellet.datatypes.Facet.XSD;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Logger;
import net.katk.tools.Log;

/**
 * <p>
 * Title: Restricted Float Datatype
 * </p>
 * <p>
 * Description: A subset of the value space of xsd:float.
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
public class RestrictedFloatingPointDatatype<T extends Number & Comparable<T>> implements RestrictedDatatype<T>
{

	private final static Logger log = Log.getLogger(RestrictedFloatingPointDatatype.class);

	/*
	 * TODO: Evaluate storing _intervals in a tree to improve the efficiency of
	 * #contains calls
	 */

	private final boolean _containsNaN;
	private final Datatype<? extends T> _datatype;
	private final RestrictedDatatype<T> _empty;
	private final List<FloatingPointInterval<T>> _intervals;
	private final FloatingPointType<T> _type;

	public RestrictedFloatingPointDatatype(final Datatype<? extends T> datatype, final FloatingPointType<T> type)
	{
		this._datatype = datatype;
		this._type = type;
		this._empty = new EmptyRestrictedDatatype<>(datatype);
		this._intervals = Collections.singletonList(FloatingPointInterval.unconstrained(type));
		this._containsNaN = true;
	}

	private RestrictedFloatingPointDatatype(final RestrictedFloatingPointDatatype<T> other, final List<FloatingPointInterval<T>> intervals, final boolean containsNaN)
	{
		this._datatype = other._datatype;
		this._type = other._type;
		this._empty = other._empty;
		this._intervals = Collections.unmodifiableList(intervals);
		this._containsNaN = containsNaN;
	}

	@Override
	public RestrictedDatatype<T> applyConstrainingFacet(final ATermAppl facet, final Object value)
	{

		/*
		 * FIXME throw correct exception _type here
		 */

		/*
		 * Check the facet
		 */
		final Facet f = Facet.Registry.get(facet);
		if (f == null)
		{
			final String msg = format("Attempt to constrain _datatype (%s) with unsupported constraining facet ('%s' , '%s')", getDatatype(), facet, value);
			log.severe(msg);
			throw new IllegalArgumentException(msg);
		}

		/*
		 * Check the value
		 */
		T n;
		if (_type.isInstance(value))
			n = _type.cast(value);
		else
		{
			final String msg = format("Attempt to constrain _datatype (%s) using constraining facet ('%s') with an unsupported value ('%s')", getDatatype(), f, value);
			log.severe(msg);
			throw new IllegalArgumentException(msg);
		}

		T lower, upper;
		if (EnumSet.of(XSD.MAX_EXCLUSIVE, XSD.MAX_INCLUSIVE, XSD.MIN_EXCLUSIVE, XSD.MIN_INCLUSIVE).contains(f))
		{

			if (_type.isNaN(n))
				return _empty;

			if (XSD.MAX_EXCLUSIVE.equals(f))
			{
				lower = _type.getNegativeInfinity();
				if (n.equals(_type.getNegativeInfinity()))
					return _empty;
				else
					upper = _type.decrement(n);
			}
			else
				if (XSD.MAX_INCLUSIVE.equals(f))
				{
					lower = _type.getNegativeInfinity();
					upper = n;
				}
				else
					if (XSD.MIN_EXCLUSIVE.equals(f))
					{
						if (n.equals(_type.getPositiveInfinity()))
							return _empty;
						else
							lower = n;
						upper = _type.getPositiveInfinity();
					}
					else
						if (XSD.MIN_INCLUSIVE.equals(f))
						{
							lower = n;
							upper = _type.getPositiveInfinity();
						}
						else
							throw new IllegalStateException();
		}
		else
			throw new IllegalStateException();

		final FloatingPointInterval<T> restriction = new FloatingPointInterval<>(_type, lower, upper);

		final List<FloatingPointInterval<T>> revisedIntervals = new ArrayList<>();

		/*
		 * As described in XSD 1.1 part 2, NaN is incomparable with all float
		 * values, so if any facet is applied it is excluded from the value
		 * space.
		 */
		boolean changes = _containsNaN;

		for (final FloatingPointInterval<T> i : _intervals)
		{
			final FloatingPointInterval<T> j = i.intersection(restriction);
			if (j != null)
			{
				revisedIntervals.add(j);
				if (i != j)
					changes = true;
			}
			else
				changes = true;
		}

		if (changes)
		{
			if (revisedIntervals.isEmpty())
				return _empty;
			else
				return new RestrictedFloatingPointDatatype<>(this, revisedIntervals, false);
		}
		else
			return this;
	}

	@Override
	public boolean contains(final Object value)
	{
		if (_type.isInstance(value))
		{
			final T n = _type.cast(value);
			if (_type.isNaN(n))
				return _containsNaN;
			else
			{
				for (final FloatingPointInterval<T> i : _intervals)
					if (i.contains(n))
						return true;
				return false;
			}
		}
		else
			return false;
	}

	@Override
	public boolean containsAtLeast(final int n)
	{
		if (n <= 0)
			return true;

		Number sum = _containsNaN ? 1 : 0;
		for (final FloatingPointInterval<T> i : _intervals)
		{
			sum = OWLRealUtils.integerSum(sum, i.size());
			if (OWLRealUtils.compare(sum, n) >= 0)
				return true;
		}

		return false;
	}

	@Override
	public RestrictedDatatype<T> exclude(final Collection<?> values)
	{
		boolean changes = false;
		final List<FloatingPointInterval<T>> revisedIntervals = new ArrayList<>(_intervals);

		for (final Object o : values)
			if (_type.isInstance(o))
			{
				final T n = _type.cast(o);
				for (final Iterator<FloatingPointInterval<T>> it = revisedIntervals.iterator(); it.hasNext();)
				{
					final FloatingPointInterval<T> i = it.next();
					if (i.contains(n))
					{

						changes = true;
						it.remove();

						final FloatingPointInterval<T> less = i.less(n);
						if (less != null)
							revisedIntervals.add(less);

						final FloatingPointInterval<T> greater = i.greater(n);
						if (greater != null)
							revisedIntervals.add(greater);

						break;
					}
				}
			}

		if (changes)
		{
			if (revisedIntervals.isEmpty())
				return _empty;
			else
				return new RestrictedFloatingPointDatatype<>(this, revisedIntervals, _containsNaN);
		}
		else
			return this;
	}

	@Override
	public Datatype<? extends T> getDatatype()
	{
		return _datatype;
	}

	@Override
	public RestrictedDatatype<T> intersect(final RestrictedDatatype<?> other, final boolean negated)
	{

		if (other instanceof RestrictedFloatingPointDatatype<?>)
		{
			if (!_type.equals(((RestrictedFloatingPointDatatype<?>) other)._type))
				throw new IllegalArgumentException();
			@SuppressWarnings({ "unchecked", "rawtypes" })
			final RestrictedFloatingPointDatatype<T> otherRRD = (RestrictedFloatingPointDatatype) other;

			boolean changes = false;
			final List<FloatingPointInterval<T>> revisedIntervals = new ArrayList<>();

			List<FloatingPointInterval<T>> intersectWith;
			if (negated)
			{
				intersectWith = Collections.singletonList(FloatingPointInterval.unconstrained(_type));
				for (final FloatingPointInterval<T> i : otherRRD._intervals)
				{
					final List<FloatingPointInterval<T>> tmp = new ArrayList<>();
					for (final FloatingPointInterval<T> j : intersectWith)
						tmp.addAll(j.remove(i));
					intersectWith = tmp;
				}
			}
			else
				intersectWith = otherRRD._intervals;

			for (final FloatingPointInterval<T> i : this._intervals)
				for (final FloatingPointInterval<T> j : intersectWith)
				{
					final FloatingPointInterval<T> k = i.intersection(j);
					if (k != i)
					{
						changes = true;
						if (k != null)
							revisedIntervals.add(k);
					}
				}

			boolean toContainNaN;
			if (this._containsNaN)
			{
				if (otherRRD._containsNaN)
				{
					if (negated)
					{
						changes = true;
						toContainNaN = false;
					}
					else
						toContainNaN = true;
				}
				else
					if (negated)
						toContainNaN = true;
					else
					{
						changes = true;
						toContainNaN = false;
					}
			}
			else
				toContainNaN = false;

			if (changes)
			{
				if (revisedIntervals.isEmpty())
					return _empty;
				else
					return new RestrictedFloatingPointDatatype<>(this, revisedIntervals, toContainNaN);
			}
			else
				return this;

		}
		else
			throw new IllegalArgumentException();
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}

	@Override
	public boolean isEnumerable()
	{
		return true;
	}

	@Override
	public boolean isFinite()
	{
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int size()
	{
		long sum = _containsNaN ? 1 : 0;
		for (final FloatingPointInterval<T> i : _intervals)
		{
			sum += i.size().longValue();
			if (sum >= Integer.MAX_VALUE)
				return Integer.MAX_VALUE;
		}
		return (int) sum;
	}

	@Override
	public String toString()
	{
		return format("{%s,%s}", _datatype, _intervals);
	}

	@Override
	public RestrictedDatatype<T> union(final RestrictedDatatype<?> other)
	{
		if (other instanceof RestrictedFloatingPointDatatype<?>)
		{
			if (!_type.equals(((RestrictedFloatingPointDatatype<?>) other)._type))
				throw new IllegalArgumentException();
			@SuppressWarnings({ "unchecked", "rawtypes" })
			final RestrictedFloatingPointDatatype<T> otherRRD = (RestrictedFloatingPointDatatype) other;

			final List<FloatingPointInterval<T>> revisedIntervals = new ArrayList<>(this._intervals);
			for (final FloatingPointInterval<T> i : otherRRD._intervals)
			{
				final List<FloatingPointInterval<T>> unionWith = new ArrayList<>();
				for (final Iterator<FloatingPointInterval<T>> jt = revisedIntervals.iterator(); jt.hasNext();)
				{
					final FloatingPointInterval<T> j = jt.next();
					if (i.canUnionWith(j))
					{
						jt.remove();
						unionWith.add(j);
					}
				}
				if (unionWith.isEmpty())
					revisedIntervals.add(i);
				else
				{
					final Set<FloatingPointInterval<T>> tmp = new HashSet<>();
					for (final FloatingPointInterval<T> j : unionWith)
						tmp.addAll(i.union(j));
					revisedIntervals.addAll(tmp);
				}
			}
			final boolean toContainNaN = this._containsNaN || otherRRD._containsNaN;

			return new RestrictedFloatingPointDatatype<>(this, revisedIntervals, toContainNaN);
		}
		else
			throw new IllegalArgumentException();
	}

	@Override
	public Iterator<T> valueIterator()
	{
		/*
		 * This implementation avoids allocating the value iterators for the
		 * _intervals until (and only if) they are needed. This is a
		 * micro-optimization relative to using
		 * org.mindswap.pellet.utils.iterator.MultiIterator
		 */
		return new Iterator<T>()
				{
			final Iterator<FloatingPointInterval<T>> iit = _intervals.iterator();
			Iterator<T> nit = null;

			@Override
			public boolean hasNext()
			{

				/*
				 * TODO: This implementation will never return NaN but should if
				 * _containsNaN is true
				 */

				while (nit == null || !nit.hasNext())
					if (iit.hasNext())
						nit = iit.next().valueIterator();
					else
						return false;

				return true;
			}

			@Override
			public T next()
			{
				if (!hasNext())
					throw new NoSuchElementException();

				return nit.next();
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
				};
	}

}
