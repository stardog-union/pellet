package com.clarkparsia.pellet.datatypes.types.real;

import static java.lang.String.format;

import com.clarkparsia.pellet.datatypes.IntervalRelations;
import com.clarkparsia.pellet.datatypes.OWLRealUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import net.katk.tools.Log;

/**
 * <p>
 * Title: <code>owl:real</code> Interval
 * </p>
 * <p>
 * Description: An immutable interval representation supporting continuous (decimal and rational) number lines in <code>owl:real</code> value space.
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
public class ContinuousRealInterval
{
	private static final Logger log = Log.getLogger(ContinuousRealInterval.class);

	private static ContinuousRealInterval _unconstrained = new ContinuousRealInterval(null, null, true, true);

	public static ContinuousRealInterval allReals()
	{
		return _unconstrained;
	}

	private static IntervalRelations compare(final ContinuousRealInterval a, final ContinuousRealInterval b)
	{
		final int ll = compareLowerLower(a, b);

		if (ll < 0)
		{
			final int ul = compareUpperLower(a, b);
			if (ul < 0)
				return IntervalRelations.PRECEDES;
			else
				if (ul == 0)
				{
					if (a.inclusiveUpper())
					{
						if (b.inclusiveLower())
							return IntervalRelations.OVERLAPS;
						else
							return IntervalRelations.MEETS;
					}
					else
						if (b.inclusiveLower())
							return IntervalRelations.MEETS;
						else
							return IntervalRelations.PRECEDES;
				}
				else
				{
					final int uu = compareUpperUpper(a, b);
					if (uu < 0)
						return IntervalRelations.OVERLAPS;
					else
						if (uu == 0)
							return IntervalRelations.FINISHED_BY;
						else
							return IntervalRelations.CONTAINS;
				}
		}
		else
			if (ll == 0)
			{
				final int uu = compareUpperUpper(a, b);
				if (uu < 0)
					return IntervalRelations.STARTS;
				else
					if (uu == 0)
						return IntervalRelations.EQUALS;
					else
						return IntervalRelations.STARTED_BY;
			}
			else
			{
				final int lu = -compareUpperLower(b, a);
				if (lu < 0)
				{
					final int uu = compareUpperUpper(a, b);
					if (uu < 0)
						return IntervalRelations.DURING;
					else
						if (uu == 0)
							return IntervalRelations.FINISHES;
						else
							return IntervalRelations.OVERLAPPED_BY;
				}
				else
					if (lu == 0)
					{
						if (b.inclusiveUpper())
						{
							if (a.inclusiveLower())
								return IntervalRelations.OVERLAPPED_BY;
							else
								return IntervalRelations.MET_BY;
						}
						else
							if (a.inclusiveLower())
								return IntervalRelations.MET_BY;
							else
								return IntervalRelations.PRECEDED_BY;
					}
					else
						return IntervalRelations.PRECEDED_BY;
			}
	}

	private static int compareLowerLower(final ContinuousRealInterval a, final ContinuousRealInterval other)
	{
		int ll;
		if (!a.boundLower())
		{
			if (!other.boundLower())
				ll = 0;
			else
				ll = -1;
		}
		else
			if (!other.boundLower())
				ll = 1;
			else
			{
				ll = OWLRealUtils.compare(a.getLower(), other.getLower());
				if (ll == 0)
					if (a.inclusiveLower())
					{
						if (!other.inclusiveLower())
							ll = -1;
					}
					else
						if (other.inclusiveLower())
							ll = 1;
			}
		return ll;
	}

	private static int compareUpperLower(final ContinuousRealInterval a, final ContinuousRealInterval b)
	{
		int ul;
		if (!a.boundUpper())
			ul = 1;
		else
			if (!b.boundLower())
				ul = 1;
			else
				ul = OWLRealUtils.compare(a.getUpper(), b.getLower());
		return ul;
	}

	private static int compareUpperUpper(final ContinuousRealInterval a, final ContinuousRealInterval b)
	{
		int uu;
		if (!a.boundUpper())
		{
			if (!b.boundUpper())
				uu = 0;
			else
				uu = 1;
		}
		else
			if (!b.boundUpper())
				uu = -1;
			else
			{
				uu = OWLRealUtils.compare(a.getUpper(), b.getUpper());
				if (uu == 0)
					if (a.inclusiveUpper())
					{
						if (!b.inclusiveUpper())
							uu = 1;
					}
					else
						if (b.inclusiveUpper())
							uu = -1;
			}
		return uu;
	}

	private final boolean _inclusiveLower;
	private final boolean _inclusiveUpper;
	private final Number _lower;
	private final boolean _point;
	private final Number _upper;

	/**
	 * Create a _point interval. This is equivalent to {@link #OWLRealInterval(Number, Number, boolean, boolean)} with arguments
	 * <code>_point,_point,true,true</code>
	 *
	 * @param _point Value of _point interval
	 */
	public ContinuousRealInterval(final Number point)
	{
		this._lower = point;
		this._upper = point;
		this._point = true;
		this._inclusiveLower = true;
		this._inclusiveUpper = true;
	}

	/**
	 * Create an interval. <code>null</code> should be used to indicate unbound (i.e., infinite intervals).
	 *
	 * @param _lower Interval _lower bound
	 * @param _upper Interval _upper bound
	 * @param _inclusiveLower <code>true</code> if _lower bound is inclusive, <code>false</code> for exclusive. Ignored if <code>_lower == null</code>.
	 * @param _inclusiveUpper <code>true</code> if _upper bound is inclusive, <code>false</code> for exclusive. Ignored if <code>_upper == null</code>.
	 */
	public ContinuousRealInterval(final Number lower, final Number upper, final boolean inclusiveLower, final boolean inclusiveUpper)
	{
		if (lower != null && upper != null)
		{
			final int cmp = OWLRealUtils.compare(lower, upper);
			if (cmp > 0)
			{
				final String msg = format("Lower bound of interval (%s) should not be greater than _upper bound of interval (%s)", lower, upper);
				log.severe(msg);
				throw new IllegalArgumentException(msg);
			}
			else
				if (cmp == 0)
					if ((!inclusiveLower || !inclusiveUpper))
					{
						final String msg = "Point intervals must be inclusive";
						log.severe(msg);
						throw new IllegalArgumentException(msg);
					}
		}

		this._lower = lower;
		this._upper = upper;
		this._inclusiveLower = (lower == null) ? false : inclusiveLower;
		this._inclusiveUpper = (upper == null) ? false : inclusiveUpper;

		this._point = (lower != null && upper != null && lower.equals(upper));
	}

	public boolean boundLower()
	{
		return (_lower != null);
	}

	public boolean boundUpper()
	{
		return (_upper != null);
	}

	public boolean canUnionWith(final ContinuousRealInterval other)
	{
		return EnumSet.complementOf(EnumSet.of(IntervalRelations.PRECEDES, IntervalRelations.PRECEDED_BY)).contains(compare(other));
	}

	public IntervalRelations compare(final ContinuousRealInterval other)
	{
		return compare(this, other);
	}

	public boolean contains(final Number n)
	{

		int comp;
		if (boundLower())
		{
			comp = OWLRealUtils.compare(getLower(), n);
			if (comp > 0)
				return false;
			if ((comp == 0) && !inclusiveLower())
				return false;
		}

		if (boundUpper())
		{
			comp = OWLRealUtils.compare(getUpper(), n);
			if (comp < 0)
				return false;
			if ((comp == 0) && !inclusiveUpper())
				return false;
		}

		return true;
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
		final ContinuousRealInterval other = (ContinuousRealInterval) obj;
		if (_inclusiveLower != other._inclusiveLower)
			return false;
		if (_inclusiveUpper != other._inclusiveUpper)
			return false;
		if (_lower == null)
		{
			if (other._lower != null)
				return false;
		}
		else
			if (other._lower == null)
				return false;
			else
				if (OWLRealUtils.compare(_lower, other._lower) != 0)
					return false;
		if (_upper == null)
		{
			if (other._upper != null)
				return false;
		}
		else
			if (other._upper == null)
				return false;
			else
				if (OWLRealUtils.compare(_upper, other._upper) != 0)
					return false;
		return true;
	}

	public Number getLower()
	{
		return _lower;
	}

	public Number getUpper()
	{
		return _upper;
	}

	/**
	 * Get the subinterval greater than n
	 *
	 * @param n
	 * @return a new interval, formed by intersecting this interval with (n,+inf) or <code>null</code> if that intersection is empty
	 */
	public ContinuousRealInterval greater(final Number n)
	{
		if (boundLower() && OWLRealUtils.compare(n, getLower()) < 0)
			return this;
		else
			if (boundUpper() && OWLRealUtils.compare(n, getUpper()) >= 0)
				return null;
		return new ContinuousRealInterval(n, getUpper(), false, inclusiveUpper());
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (_inclusiveLower ? 1231 : 1237);
		result = prime * result + (_inclusiveUpper ? 1231 : 1237);
		result = prime * result + ((_lower == null) ? 0 : _lower.hashCode());
		result = prime * result + ((_upper == null) ? 0 : _upper.hashCode());
		return result;
	}

	public boolean inclusiveLower()
	{
		return _inclusiveLower;
	}

	public boolean inclusiveUpper()
	{
		return _inclusiveUpper;
	}

	public ContinuousRealInterval intersection(final ContinuousRealInterval that)
	{
		Number lower, upper;
		boolean inclusiveUpper, inclusiveLower;

		switch (compare(that))
		{

			case CONTAINS:
			case STARTED_BY:

				lower = that.getLower();
				inclusiveLower = that.inclusiveLower();
				upper = that.getUpper();
				inclusiveUpper = that.inclusiveUpper();
				break;

			case EQUALS:

				lower = this.getLower();
				inclusiveLower = this.inclusiveLower();
				upper = this.getUpper();
				inclusiveUpper = this.inclusiveUpper();
				break;

			case DURING:
			case STARTS:

				lower = this.getLower();
				inclusiveLower = this.inclusiveLower();
				upper = this.getUpper();
				inclusiveUpper = this.inclusiveUpper();
				break;

			case FINISHED_BY:
				lower = that.getLower();
				inclusiveLower = that.inclusiveLower();
				upper = that.getUpper();
				inclusiveUpper = (this.inclusiveUpper() && that.inclusiveUpper());
				break;

			case FINISHES:
				lower = this.getLower();
				inclusiveLower = this.inclusiveLower();
				upper = this.getUpper();
				inclusiveUpper = (this.inclusiveUpper() && that.inclusiveUpper());
				break;

			case MEETS:
			case MET_BY:
				return null;

			case OVERLAPPED_BY:
				lower = this.getLower();
				inclusiveLower = this.inclusiveLower();
				upper = that.getUpper();
				inclusiveUpper = that.inclusiveUpper();
				break;

			case OVERLAPS:
				lower = that.getLower();
				inclusiveLower = that.inclusiveLower();
				upper = this.getUpper();
				inclusiveUpper = this.inclusiveUpper();
				break;

			case PRECEDED_BY:
			case PRECEDES:
				return null;

			default:
				throw new IllegalStateException();
		}

		return new ContinuousRealInterval(lower, upper, inclusiveLower, inclusiveUpper);
	}

	public boolean isPoint()
	{
		return _point;
	}

	/**
	 * Get the subinterval less than n
	 *
	 * @param n
	 * @return a new interval, formed by intersecting this interval with (-inf,n) or <code>null</code> if that intersection is empty
	 */
	public ContinuousRealInterval less(final Number n)
	{
		if (boundUpper() && OWLRealUtils.compare(n, getUpper()) > 0)
			return this;
		else
			if (boundLower() && OWLRealUtils.compare(n, getLower()) <= 0)
				return null;
		return new ContinuousRealInterval(getLower(), n, inclusiveLower(), false);
	}

	public List<ContinuousRealInterval> remove(final ContinuousRealInterval other)
	{

		ContinuousRealInterval before, after;
		switch (compare(other))
		{

			case CONTAINS:
				before = new ContinuousRealInterval(this.getLower(), other.getLower(), inclusiveLower(), !other.inclusiveLower());
				after = new ContinuousRealInterval(other.getUpper(), this.getUpper(), !other.inclusiveUpper(), this.inclusiveUpper());
				break;

			case DURING:
			case EQUALS:
			case FINISHES:
			case STARTS:
				return Collections.emptyList();

			case MEETS:
				before = new ContinuousRealInterval(this.getLower(), this.getUpper(), this.inclusiveLower(), false);
				after = null;
				break;

			case MET_BY:
				before = null;
				after = new ContinuousRealInterval(this.getLower(), this.getUpper(), false, this.inclusiveUpper());
				break;

			case OVERLAPPED_BY:
			case STARTED_BY:
				before = null;
				after = new ContinuousRealInterval(other.getUpper(), this.getUpper(), !other.inclusiveUpper(), this.inclusiveUpper());
				break;

			case OVERLAPS:
			case FINISHED_BY:
				before = new ContinuousRealInterval(this.getLower(), other.getLower(), this.inclusiveLower(), !other.inclusiveLower());
				after = null;
				break;

			case PRECEDED_BY:
			case PRECEDES:
				return Collections.singletonList(this);

			default:
				throw new IllegalStateException();
		}

		final List<ContinuousRealInterval> ret = new ArrayList<>();
		if (before != null)
			ret.add(before);
		if (after != null)
			ret.add(after);

		return ret;
	}

	public Number size()
	{
		if (!_point)
			throw new IllegalStateException();
		else
			return 1;
	}

	@Override
	public String toString()
	{
		return format("%s%s,%s%s", inclusiveLower() ? "[" : "(", boundLower() ? getLower() : "-Inf", boundUpper() ? getUpper() : "+Inf", inclusiveUpper() ? "]" : ")");
	}

	public List<ContinuousRealInterval> union(final ContinuousRealInterval other)
	{

		switch (compare(other))
		{
			case CONTAINS:
			case EQUALS:
			case FINISHED_BY:
			case STARTED_BY:
				return Collections.singletonList(this);

			case DURING:
			case FINISHES:
			case STARTS:
				return Collections.singletonList(other);

			case MEETS:
				return Collections.singletonList(new ContinuousRealInterval(getLower(), other.getUpper(), inclusiveLower(), other.inclusiveUpper()));

			case MET_BY:
				return Collections.singletonList(new ContinuousRealInterval(other.getLower(), getUpper(), other.inclusiveLower(), inclusiveUpper()));

			case OVERLAPPED_BY:
				return Collections.singletonList(new ContinuousRealInterval(other.getLower(), getUpper(), other.inclusiveLower(), inclusiveUpper()));

			case OVERLAPS:
				return Collections.singletonList(new ContinuousRealInterval(getLower(), other.getUpper(), inclusiveLower(), other.inclusiveUpper()));

			case PRECEDED_BY:
			case PRECEDES:
				return Arrays.asList(this, other);

			default:
				throw new IllegalStateException();
		}
	}

	public Iterator<Number> valueIterator()
	{
		if (isPoint())
			return Collections.singletonList(getUpper()).iterator();
		else
			throw new IllegalStateException();

	}
}
