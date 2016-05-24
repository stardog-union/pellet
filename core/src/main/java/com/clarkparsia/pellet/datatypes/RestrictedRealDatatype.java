package com.clarkparsia.pellet.datatypes;

import static java.lang.String.format;

import aterm.ATermAppl;
import com.clarkparsia.pellet.datatypes.Facet.XSD;
import java.util.ArrayList;
import java.util.Arrays;
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
 * Title: Restricted Real Datatype
 * </p>
 * <p>
 * Description: A subset of the value space of owl:real.
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
public class RestrictedRealDatatype implements RestrictedDatatype<Number>
{

	private final static Logger log = Log.getLogger(RestrictedRealDatatype.class);

	/*
	 * TODO: Evaluate storing _intervals in a tree to improve the efficiency of
	 * #contains calls
	 */

	private final Datatype<? extends Number> _datatype;
	private final RestrictedDatatype<Number> _empty;
	private final boolean _enumerable;
	private final boolean _finite;
	private final List<OWLRealInterval> _intervals;

	public RestrictedRealDatatype(final Datatype<? extends Number> datatype, final OWLRealInterval interval)
	{
		this._datatype = datatype;
		this._empty = new EmptyRestrictedDatatype<>(datatype);
		this._intervals = Collections.singletonList(interval);
		this._finite = interval.isFinite();
		this._enumerable = interval.isPoint() || interval.getType().equals(OWLRealInterval.LineType.INTEGER_ONLY);
	}

	private RestrictedRealDatatype(final RestrictedRealDatatype other, final List<OWLRealInterval> intervals)
	{
		this._datatype = other._datatype;
		this._empty = other._empty;
		this._intervals = Collections.unmodifiableList(intervals);
		if (other._finite)
			this._finite = true;
		else
		{
			boolean allFinite = true;
			for (final OWLRealInterval i : intervals)
				if (!i.isFinite())
				{
					allFinite = false;
					break;
				}
			this._finite = allFinite;
		}
		if (other._enumerable)
			this._enumerable = true;
		else
		{
			boolean allEnumerable = true;
			for (final OWLRealInterval i : intervals)
				if (!i.isPoint() && !i.getType().equals(OWLRealInterval.LineType.INTEGER_ONLY))
				{
					allEnumerable = false;
					break;
				}
			this._enumerable = allEnumerable;
		}
	}

	@Override
	public RestrictedDatatype<Number> applyConstrainingFacet(final ATermAppl facet, final Object value)
	{

		/*
		 * FIXME throw correct exception type here
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
		Number n = null;
		if (value instanceof Number)
		{
			n = (Number) value;
			if (!OWLRealUtils.acceptable(n.getClass()))
				n = null;
		}
		if (n == null)
		{
			final String msg = format("Attempt to constrain _datatype (%s) using constraining facet ('%s') with an unsupported value ('%s')", getDatatype(), f, value);
			log.severe(msg);
			throw new IllegalArgumentException(msg);
		}

		Number lower, upper;
		boolean inclusiveLower, inclusiveUpper;
		if (XSD.MAX_EXCLUSIVE.equals(f))
		{
			lower = null;
			inclusiveLower = false;
			upper = n;
			inclusiveUpper = false;
		}
		else
			if (XSD.MAX_INCLUSIVE.equals(f))
			{
				lower = null;
				inclusiveLower = false;
				upper = n;
				inclusiveUpper = true;
			}
			else
				if (XSD.MIN_EXCLUSIVE.equals(f))
				{
					lower = n;
					inclusiveLower = false;
					upper = null;
					inclusiveUpper = false;
				}
				else
					if (XSD.MIN_INCLUSIVE.equals(f))
					{
						lower = n;
						inclusiveLower = true;
						upper = null;
						inclusiveUpper = false;
					}
					else
						throw new IllegalStateException();

		final OWLRealInterval restriction = new OWLRealInterval(lower, upper, inclusiveLower, inclusiveUpper, OWLRealInterval.LineType.CONTINUOUS);

		final List<OWLRealInterval> revisedIntervals = new ArrayList<>();
		boolean changes = false;

		for (final OWLRealInterval i : _intervals)
		{
			final OWLRealInterval j = i.intersection(restriction);
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
				return new RestrictedRealDatatype(this, revisedIntervals);
		}
		else
			return this;
	}

	@Override
	public boolean contains(final Object value)
	{
		if (value instanceof Number)
		{
			final Number n = (Number) value;
			if (OWLRealUtils.acceptable(n.getClass()))
			{
				/*
				 * TODO: This could be made more efficient by looking at how
				 * each contained check fails (e.g., if _intervals is sorted by
				 * boundaries and n is not contained, but less than upper, there
				 * is no need to look further).
				 */
				for (final OWLRealInterval i : _intervals)
					if (i.contains(n))
						return true;
				return false;
			}
			else
				return false;
		}
		else
			return false;
	}

	@Override
	public boolean containsAtLeast(final int n)
	{
		if (!_finite || n <= 0)
			return true;

		Number sum = 0;
		for (final OWLRealInterval i : _intervals)
		{
			sum = OWLRealUtils.integerSum(sum, i.size());
			if (OWLRealUtils.compare(n, sum) <= 0)
				return true;
		}

		return false;
	}

	@Override
	public RestrictedDatatype<Number> exclude(final Collection<?> values)
	{
		boolean changes = false;
		final List<OWLRealInterval> revisedIntervals = new ArrayList<>(_intervals);

		for (final Object o : values)
			if (o instanceof Number)
			{
				final Number n = (Number) o;
				for (final Iterator<OWLRealInterval> it = revisedIntervals.iterator(); it.hasNext();)
				{
					final OWLRealInterval i = it.next();
					if (i.contains(n))
					{

						changes = true;
						it.remove();

						final OWLRealInterval less = i.less(n);
						if (less != null)
							revisedIntervals.add(less);

						final OWLRealInterval greater = i.greater(n);
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
				return new RestrictedRealDatatype(this, revisedIntervals);
		}
		else
			return this;
	}

	@Override
	public Datatype<? extends Number> getDatatype()
	{
		return _datatype;
	}

	@Override
	public RestrictedDatatype<Number> intersect(final RestrictedDatatype<?> other, final boolean negated)
	{

		if (other instanceof RestrictedRealDatatype)
		{
			final RestrictedRealDatatype otherRRD = (RestrictedRealDatatype) other;

			final List<OWLRealInterval> revisedIntervals = new ArrayList<>();

			List<OWLRealInterval> intersectWith;
			if (negated)
			{
				intersectWith = new ArrayList<>(Arrays.asList(OWLRealInterval.allReals()));
				for (final OWLRealInterval i : otherRRD._intervals)
				{
					final List<OWLRealInterval> tmp = new ArrayList<>();
					for (final OWLRealInterval j : intersectWith)
						tmp.addAll(j.remove(i));
					intersectWith = tmp;
				}
			}
			else
				intersectWith = otherRRD._intervals;

			for (final OWLRealInterval i : this._intervals)
				for (final OWLRealInterval j : intersectWith)
				{
					final OWLRealInterval k = i.intersection(j);
					if (k != null)
						revisedIntervals.add(k);
				}

			if (revisedIntervals.equals(this._intervals))
				return this;
			else
				if (revisedIntervals.isEmpty())
					return _empty;
				else
					return new RestrictedRealDatatype(this, revisedIntervals);

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
		return _enumerable;
	}

	@Override
	public boolean isFinite()
	{
		return _finite;
	}

	@Deprecated
	@Override
	public int size()
	{
		if (!_finite)
			throw new IllegalStateException();

		Number sum = 0;
		for (final OWLRealInterval i : _intervals)
		{
			sum = OWLRealUtils.integerSum(sum, i.size());
			if (OWLRealUtils.compare(Integer.MAX_VALUE, sum) <= 0)
				return Integer.MAX_VALUE;
		}
		return sum.intValue();
	}

	@Override
	public RestrictedDatatype<Number> union(final RestrictedDatatype<?> other)
	{
		if (other instanceof RestrictedRealDatatype)
		{
			final RestrictedRealDatatype otherRRD = (RestrictedRealDatatype) other;

			final List<OWLRealInterval> revisedIntervals = new ArrayList<>(this._intervals);
			final EnumSet<IntervalRelations> connected = EnumSet.complementOf(EnumSet.of(IntervalRelations.PRECEDED_BY, IntervalRelations.PRECEDES));
			for (final OWLRealInterval i : otherRRD._intervals)
			{
				final List<OWLRealInterval> unionWith = new ArrayList<>();
				for (final Iterator<OWLRealInterval> jt = revisedIntervals.iterator(); jt.hasNext();)
				{
					final OWLRealInterval j = jt.next();
					final IntervalRelations rel = i.compare(j);
					if (connected.contains(rel))
					{
						jt.remove();
						unionWith.add(j);
					}
				}
				if (unionWith.isEmpty())
					revisedIntervals.add(i);
				else
				{
					final Set<OWLRealInterval> tmp = new HashSet<>();
					for (final OWLRealInterval j : unionWith)
						tmp.addAll(i.union(j));
					revisedIntervals.addAll(tmp);
				}
			}

			return new RestrictedRealDatatype(this, revisedIntervals);
		}
		else
			throw new IllegalArgumentException();
	}

	@Override
	public Iterator<Number> valueIterator()
	{
		if (!_enumerable)
			throw new IllegalStateException();

		/*
		 * This implementation avoids allocating the value iterators for the
		 * _intervals until (and only if) they are needed. This is a
		 * micro-optimization relative to using
		 * org.mindswap.pellet.utils.iterator.MultiIterator
		 */
		return new Iterator<Number>()
				{
			final Iterator<OWLRealInterval> iit = _intervals.iterator();
			Iterator<Number> nit = null;

			@Override
			public boolean hasNext()
			{

				while (nit == null || !nit.hasNext())
					if (iit.hasNext())
						nit = iit.next().valueIterator();
					else
						return false;

				return true;
			}

			@Override
			public Number next()
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

	@Override
	public String toString()
	{
		return format("{%s,%s}", _datatype, _intervals);
	}

}
