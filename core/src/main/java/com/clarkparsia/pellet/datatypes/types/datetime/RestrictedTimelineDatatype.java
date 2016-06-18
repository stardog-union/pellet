package com.clarkparsia.pellet.datatypes.types.datetime;

import static java.lang.String.format;

import aterm.ATermAppl;
import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.EmptyRestrictedDatatype;
import com.clarkparsia.pellet.datatypes.Facet;
import com.clarkparsia.pellet.datatypes.Facet.XSD;
import com.clarkparsia.pellet.datatypes.OWLRealUtils;
import com.clarkparsia.pellet.datatypes.RestrictedDatatype;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidConstrainingFacetException;
import com.clarkparsia.pellet.datatypes.types.real.ContinuousRealInterval;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import openllet.shared.tools.Log;
import org.mindswap.pellet.exceptions.InternalReasonerException;

/**
 * <p>
 * Title: Restricted time line datatype
 * </p>
 * <p>
 * Description: A base implementation for datatypes based on the XSD 7 property date time model. Calendar objects are converted to real numbers based on the XML
 * 1.1 datatype spec. This implementation uses two real number interval collections (one with time zone present, one with time zone absent).
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
public class RestrictedTimelineDatatype implements RestrictedDatatype<XMLGregorianCalendar>
{

	/*
	 * TODO: Evaluate storing intervals in a tree to improve the efficiency of
	 * #contains calls
	 */

	private static final DatatypeFactory dtFactory;
	private static final Logger _logger = Log.getLogger(RestrictedTimelineDatatype.class);
	private static final BigInteger SEC_PER_DAY = BigInteger.valueOf(86400);
	private static final BigInteger SEC_PER_YEAR = BigInteger.valueOf(31536000);
	private static final int TZ_SHIFT = 14 * 60 * 60;

	static
	{
		try
		{
			dtFactory = DatatypeFactory.newInstance();
		}
		catch (final DatatypeConfigurationException e)
		{
			final String msg = "Failure initializing restricted timeline datatype support.";
			_logger.severe(msg);
			throw new InternalReasonerException(msg, e);
		}
	}

	private static Number calendarToReal(final XMLGregorianCalendar c)
	{

		BigInteger yrPlusOne = c.getEonAndYear();
		final BigInteger y = (yrPlusOne == null) ? BigInteger.valueOf(1971) : yrPlusOne.subtract(BigInteger.ONE);
		if (yrPlusOne == null)
			yrPlusOne = BigInteger.valueOf(1972);

		final int month = (c.getMonth() == DatatypeConstants.FIELD_UNDEFINED) ? 12 : c.getMonth();

		final int day = (c.getDay() == DatatypeConstants.FIELD_UNDEFINED) ? daysInMonth(yrPlusOne, month) - 1 : c.getDay() - 1;

		final int hour = (c.getHour() == DatatypeConstants.FIELD_UNDEFINED) ? 0 : c.getHour();

		int minute = (c.getMinute() == DatatypeConstants.FIELD_UNDEFINED) ? 0 : c.getMinute();

		final int second = (c.getSecond() == DatatypeConstants.FIELD_UNDEFINED) ? 0 : c.getSecond();

		final BigDecimal fractionalSecond = c.getFractionalSecond();

		final int tz = c.getTimezone();
		if (tz != DatatypeConstants.FIELD_UNDEFINED)
			minute = minute - tz;

		BigInteger toTi;

		/*
		 * Seconds in complete years
		 */
		toTi = SEC_PER_YEAR.multiply(y);

		/*
		 * Plus leap days
		 */
		toTi = toTi.add(SEC_PER_DAY.multiply(y.divide(BigInteger.valueOf(400)).subtract(y.divide(BigInteger.valueOf(100))).add(y.divide(BigInteger.valueOf(4)))));

		int daySum = day;
		for (int m = 1; m < month; m++)
			daySum += daysInMonth(yrPlusOne, m);

		/*
		 * Seconds in complete days of _current year
		 */
		toTi = toTi.add(BigInteger.valueOf(86400L * daySum));

		/*
		 * Seconds in incomplete day
		 */
		toTi = toTi.add(BigInteger.valueOf(3600L * hour + 60L * minute + second));

		if (fractionalSecond == null || BigDecimal.ZERO.equals(fractionalSecond))
			return OWLRealUtils.getCanonicalObject(toTi);
		else
			return new BigDecimal(toTi).add(fractionalSecond);
	}

	private static int daysInMonth(final BigInteger year, final int month)
	{
		if (month == 2)
		{
			if (year.remainder(BigInteger.valueOf(4)).equals(BigInteger.ZERO))
			{
				if (year.remainder(BigInteger.valueOf(100)).equals(BigInteger.ZERO))
				{
					if (year.remainder(BigInteger.valueOf(400)).equals(BigInteger.ZERO))
						return 29;
					else
						return 28;
				}
				else
					return 29;
			}
			else
				return 28;
		}
		else
			switch (month)
			{
				case 4:
				case 6:
				case 9:
				case 11:
					return 30;
				case 1:
				case 3:
				case 5:
				case 7:
				case 8:
				case 10:
				case 12:
					return 31;
				default:
					throw new IllegalArgumentException();
			}
	}

	public static DatatypeFactory getDatatypeFactory()
	{
		return dtFactory;
	}

	private static ContinuousRealInterval zoneShrink(final ContinuousRealInterval i)
	{
		final Number lower = i.boundLower() ? OWLRealUtils.sum(i.getLower(), TZ_SHIFT) : null;
		final Number upper = i.boundUpper() ? OWLRealUtils.sum(i.getUpper(), -TZ_SHIFT) : null;
		if (lower != null && upper != null)
		{
			final int cmp = OWLRealUtils.compare(lower, upper);
			if (cmp > 0)
				return null;
			else
				if (cmp == 0)
					if (!i.inclusiveLower() && !i.inclusiveUpper())
						return null;
					else
						return new ContinuousRealInterval(lower);
		}
		return new ContinuousRealInterval(lower, upper, i.inclusiveLower(), i.inclusiveUpper());
	}

	protected final Datatype<? extends XMLGregorianCalendar> _datatype;

	protected final RestrictedDatatype<XMLGregorianCalendar> _empty;
	protected final boolean _enumerable;
	protected final boolean _finite;
	protected final List<ContinuousRealInterval> _nzIntervals;
	protected final QName _schemaType;
	protected final List<ContinuousRealInterval> _wzIntervals;

	public RestrictedTimelineDatatype(final Datatype<? extends XMLGregorianCalendar> datatype, final QName schemaType, final boolean requireTz)
	{
		this._datatype = datatype;
		this._schemaType = schemaType;
		this._empty = new EmptyRestrictedDatatype<>(datatype);
		this._wzIntervals = Collections.singletonList(ContinuousRealInterval.allReals());
		this._nzIntervals = requireTz ? Collections.<ContinuousRealInterval> emptyList() : Collections.singletonList(ContinuousRealInterval.allReals());
		this._finite = false;
		this._enumerable = false;
	}

	private RestrictedTimelineDatatype(final RestrictedTimelineDatatype other, final List<ContinuousRealInterval> wzIntervals, final List<ContinuousRealInterval> nzIntervals)
	{
		this._datatype = other._datatype;
		this._empty = other._empty;
		this._schemaType = other._schemaType;
		this._wzIntervals = Collections.unmodifiableList(wzIntervals);
		this._nzIntervals = Collections.unmodifiableList(nzIntervals);
		if (other._finite)
			this._finite = true;
		else
		{
			boolean allFinite = true;
			for (final ContinuousRealInterval i : wzIntervals)
				if (!i.isPoint())
				{
					allFinite = false;
					break;
				}
			if (allFinite)
				for (final ContinuousRealInterval i : wzIntervals)
					if (!i.isPoint())
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
			boolean allEnumerable = nzIntervals.isEmpty();
			if (allEnumerable)
				for (final ContinuousRealInterval i : wzIntervals)
					if (!i.isPoint())
					{
						allEnumerable = false;
						break;
					}
			this._enumerable = allEnumerable;
		}
	}

	@Override
	public RestrictedDatatype<XMLGregorianCalendar> applyConstrainingFacet(final ATermAppl facet, final Object value) throws InvalidConstrainingFacetException
	{

		/*
		 * Check the facet
		 */
		final Facet f = Facet.Registry.get(facet);
		if (f == null)
		{
			final String msg = format("Attempt to constrain datatype (%s) with unsupported constraining facet ('%s' , '%s')", getDatatype(), facet, value);
			_logger.severe(msg);
			throw new InvalidConstrainingFacetException(msg, facet, value);
		}

		/*
		 * Check the value
		 */
		XMLGregorianCalendar c = null;
		if (value instanceof XMLGregorianCalendar)
			c = (XMLGregorianCalendar) value;
		if (c == null || !isValidValue(c))
		{
			final String msg = format("Attempt to constrain datatype (%s) using constraining facet ('%s') with an unsupported value ('%s')", getDatatype(), f, value);
			_logger.severe(msg);
			throw new InvalidConstrainingFacetException(msg, facet, value);
		}

		Number lower, upper;
		boolean inclusiveLower, inclusiveUpper;
		if (XSD.MAX_EXCLUSIVE.equals(f))
		{
			lower = null;
			inclusiveLower = false;
			upper = calendarToReal(c);
			inclusiveUpper = false;
		}
		else
			if (XSD.MAX_INCLUSIVE.equals(f))
			{
				lower = null;
				inclusiveLower = false;
				upper = calendarToReal(c);
				inclusiveUpper = true;
			}
			else
				if (XSD.MIN_EXCLUSIVE.equals(f))
				{
					lower = calendarToReal(c);
					inclusiveLower = false;
					upper = null;
					inclusiveUpper = false;
				}
				else
					if (XSD.MIN_INCLUSIVE.equals(f))
					{
						lower = calendarToReal(c);
						inclusiveLower = true;
						upper = null;
						inclusiveUpper = false;
					}
					else
						throw new IllegalStateException();

		ContinuousRealInterval wzRestriction, nzRestriction;
		{
			final ContinuousRealInterval restriction = new ContinuousRealInterval(lower, upper, inclusiveLower, inclusiveUpper);
			if (c.getTimezone() == DatatypeConstants.FIELD_UNDEFINED)
			{
				nzRestriction = restriction;
				wzRestriction = zoneShrink(nzRestriction);
			}
			else
			{
				wzRestriction = restriction;
				nzRestriction = zoneShrink(wzRestriction);
			}
		}

		boolean changes = false;

		final List<ContinuousRealInterval> revisedWz = new ArrayList<>();
		if (wzRestriction == null)
			changes = _wzIntervals.isEmpty();
		else
			for (final ContinuousRealInterval i : _wzIntervals)
			{
				final ContinuousRealInterval j = i.intersection(wzRestriction);
				if (j != null)
				{
					revisedWz.add(j);
					if (i != j)
						changes = true;
				}
				else
					changes = true;
			}

		final List<ContinuousRealInterval> revisedNz = new ArrayList<>();
		if (nzRestriction == null)
			changes |= _nzIntervals.isEmpty();
		else
			for (final ContinuousRealInterval i : _nzIntervals)
			{
				final ContinuousRealInterval j = i.intersection(nzRestriction);
				if (j != null)
				{
					revisedNz.add(j);
					if (i != j)
						changes = true;
				}
				else
					changes = true;
			}

		if (changes)
		{
			if (revisedWz.isEmpty() && revisedNz.isEmpty())
				return _empty;
			else
				return create(this, revisedWz, revisedNz);
		}
		else
			return this;
	}

	@Override
	public boolean contains(final Object value)
	{
		if (value instanceof XMLGregorianCalendar)
		{
			final XMLGregorianCalendar c = (XMLGregorianCalendar) value;
			if (isValidValue(c))
			{
				final Number n = calendarToReal(c);
				/*
				 * TODO: This could be made more efficient by looking at how
				 * each contained check fails (e.g., if intervals is sorted by
				 * boundaries and n is not contained, but less than upper, there
				 * is no need to look further).
				 */
				for (final ContinuousRealInterval i : c.getTimezone() == DatatypeConstants.FIELD_UNDEFINED ? _nzIntervals : _wzIntervals)
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
		/*
		 * TODO: This ignores excluded values, so may miscount
		 */
		for (int i = 0; i < _wzIntervals.size(); i++)
		{
			sum = OWLRealUtils.integerSum(sum, 28 * 60 + 1);
			if (OWLRealUtils.compare(n, sum) <= 0)
				return true;
		}

		return false;
	}

	protected RestrictedTimelineDatatype create(final RestrictedTimelineDatatype other, final List<ContinuousRealInterval> wzIntervals, final List<ContinuousRealInterval> nzIntervals)
	{
		return new RestrictedTimelineDatatype(other, wzIntervals, nzIntervals);
	}

	@Override
	public RestrictedDatatype<XMLGregorianCalendar> exclude(final Collection<?> values)
	{
		boolean changes = false;
		final List<ContinuousRealInterval> revisedNz = new ArrayList<>(_nzIntervals);

		for (final Object o : values)
			if (o instanceof XMLGregorianCalendar)
			{
				final XMLGregorianCalendar c = (XMLGregorianCalendar) o;
				if (c.getTimezone() == DatatypeConstants.FIELD_UNDEFINED)
				{
					final Number n = calendarToReal(c);
					for (final Iterator<ContinuousRealInterval> it = revisedNz.iterator(); it.hasNext();)
					{
						final ContinuousRealInterval i = it.next();
						if (i.contains(n))
						{

							changes = true;
							it.remove();

							final ContinuousRealInterval less = i.less(n);
							if (less != null)
								revisedNz.add(less);

							final ContinuousRealInterval greater = i.greater(n);
							if (greater != null)
								revisedNz.add(greater);

							break;
						}
					}
				}
				else
					/*
					 * TODO: Exclusion of tz'd individuals requires storing the
					 * individuals since the tz is used for identity
					 */
					_logger.warning("Exclusion of time zoned constants is not supported");
			}

		if (changes)
		{
			if (revisedNz.isEmpty() && _wzIntervals.isEmpty())
				return _empty;
			else
				return create(this, _wzIntervals, revisedNz);
		}
		else
			return this;
	}

	@Override
	public Datatype<? extends XMLGregorianCalendar> getDatatype()
	{
		return _datatype;
	}

	@Override
	public RestrictedDatatype<XMLGregorianCalendar> intersect(final RestrictedDatatype<?> other, final boolean negated)
	{

		if (other instanceof RestrictedTimelineDatatype)
		{
			final RestrictedTimelineDatatype otherRRD = (RestrictedTimelineDatatype) other;

			/*
			 * Intersect zoned intervals
			 */
			final List<ContinuousRealInterval> revisedWz = new ArrayList<>();

			List<ContinuousRealInterval> intersectWithWz;
			if (negated)
			{
				intersectWithWz = new ArrayList<>(Arrays.asList(ContinuousRealInterval.allReals()));
				for (final ContinuousRealInterval i : otherRRD._wzIntervals)
				{
					final List<ContinuousRealInterval> tmp = new ArrayList<>();
					for (final ContinuousRealInterval j : intersectWithWz)
						tmp.addAll(j.remove(i));
					intersectWithWz = tmp;
				}
			}
			else
				intersectWithWz = otherRRD._wzIntervals;

			for (final ContinuousRealInterval i : this._wzIntervals)
				for (final ContinuousRealInterval j : intersectWithWz)
				{
					final ContinuousRealInterval k = i.intersection(j);
					if (k != null)
						revisedWz.add(k);
				}

			/*
			 * Intersect unzoned intervals
			 */
			final List<ContinuousRealInterval> revisedNz = new ArrayList<>();

			List<ContinuousRealInterval> intersectWithNz;
			if (negated)
			{
				intersectWithNz = new ArrayList<>(Arrays.asList(ContinuousRealInterval.allReals()));
				for (final ContinuousRealInterval i : otherRRD._nzIntervals)
				{
					final List<ContinuousRealInterval> tmp = new ArrayList<>();
					for (final ContinuousRealInterval j : intersectWithNz)
						tmp.addAll(j.remove(i));
					intersectWithNz = tmp;
				}
			}
			else
				intersectWithNz = otherRRD._nzIntervals;

			for (final ContinuousRealInterval i : this._nzIntervals)
				for (final ContinuousRealInterval j : intersectWithNz)
				{
					final ContinuousRealInterval k = i.intersection(j);
					if (k != null)
						revisedNz.add(k);
				}

			/*
			 * Return a _data range based on the intersections
			 */

			if (revisedWz.equals(this._wzIntervals) && revisedNz.equals(this._nzIntervals))
				return this;
			else
				if (revisedWz.isEmpty() && revisedNz.isEmpty())
					return _empty;
				else
					return create(this, revisedWz, revisedNz);

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

	protected boolean isValidValue(final XMLGregorianCalendar c)
	{
		return _schemaType.equals(c.getXMLSchemaType());
	}

	@Deprecated
	@Override
	public int size()
	{
		if (!_finite)
			throw new IllegalStateException();

		Number sum = 0;
		for (int i = 0; i < _wzIntervals.size(); i++)
		{
			sum = OWLRealUtils.integerSum(sum, 28 * 60 + 1);
			if (OWLRealUtils.compare(Integer.MAX_VALUE, sum) <= 0)
				return Integer.MAX_VALUE;
		}
		return sum.intValue();
	}

	@Override
	public String toString()
	{
		/*
		 * TODO: Reverse the date time to real mapping for debug printing
		 */
		return format("{%s,%s,%s}", _datatype, _wzIntervals, _nzIntervals);
	}

	@Override
	public RestrictedDatatype<XMLGregorianCalendar> union(final RestrictedDatatype<?> other)
	{
		if (other instanceof RestrictedTimelineDatatype)
		{
			final RestrictedTimelineDatatype otherRRD = (RestrictedTimelineDatatype) other;

			/*
			 * Union zoned intervals
			 */

			final List<ContinuousRealInterval> revisedWz = new ArrayList<>(this._wzIntervals);
			for (final ContinuousRealInterval i : otherRRD._wzIntervals)
			{
				final List<ContinuousRealInterval> unionWith = new ArrayList<>();
				for (final Iterator<ContinuousRealInterval> jt = revisedWz.iterator(); jt.hasNext();)
				{
					final ContinuousRealInterval j = jt.next();
					if (i.canUnionWith(j))
					{
						jt.remove();
						unionWith.add(j);
					}
				}
				if (unionWith.isEmpty())
					revisedWz.add(i);
				else
				{
					final Set<ContinuousRealInterval> tmp = new HashSet<>();
					for (final ContinuousRealInterval j : unionWith)
						tmp.addAll(i.union(j));
					revisedWz.addAll(tmp);
				}
			}

			/*
			 * Union free intervals
			 */

			final List<ContinuousRealInterval> revisedNz = new ArrayList<>(this._nzIntervals);
			for (final ContinuousRealInterval i : otherRRD._nzIntervals)
			{
				final List<ContinuousRealInterval> unionWith = new ArrayList<>();
				for (final Iterator<ContinuousRealInterval> jt = revisedNz.iterator(); jt.hasNext();)
				{
					final ContinuousRealInterval j = jt.next();
					if (i.canUnionWith(j))
					{
						jt.remove();
						unionWith.add(j);
					}
				}
				if (unionWith.isEmpty())
					revisedNz.add(i);
				else
				{
					final Set<ContinuousRealInterval> tmp = new HashSet<>();
					for (final ContinuousRealInterval j : unionWith)
						tmp.addAll(i.union(j));
					revisedNz.addAll(tmp);
				}
			}

			return create(this, revisedWz, revisedNz);
		}
		else
			throw new IllegalArgumentException();
	}

	@Override
	public Iterator<XMLGregorianCalendar> valueIterator()
	{
		/*
		 * TODO: For point intervals of zone date times, this can be done
		 */
		throw new UnsupportedOperationException();
	}

}
