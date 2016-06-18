// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.builtins;

import static org.mindswap.pellet.utils.Namespaces.XSD;

import aterm.ATermAppl;
import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import com.clarkparsia.pellet.datatypes.types.datetime.XSDDate;
import com.clarkparsia.pellet.datatypes.types.datetime.XSDDateTime;
import com.clarkparsia.pellet.datatypes.types.datetime.XSDTime;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import openllet.shared.tools.Log;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * <p>
 * Title: Date Time Operators
 * </p>
 * <p>
 * Description: Implementations for each of the SWRL date-time operators.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Ron Alford
 */
public class DateTimeOperators
{
	private static Logger _logger = Log.getLogger(DateTimeOperators.class);

	private static class Date implements GeneralFunction, StringToStringFunction
	{

		StringFunctionAdapter allBound = new StringFunctionAdapter(this, XSD + "date");

		@Override
		public boolean apply(final ABox abox, final Literal[] args)
		{
			// Assume applicability check means we have the right number of arguments.
			if (args[0] != null && args[0].getValue() instanceof XMLGregorianCalendar)
			{
				final XMLGregorianCalendar value = (XMLGregorianCalendar) args[0].getValue();
				final Literal[] results = new Literal[5];
				results[0] = args[0];
				results[1] = createInteger(abox, value.getYear());
				results[2] = createInteger(abox, value.getMonth());
				results[3] = createInteger(abox, value.getDay());
				if (value.getTimezone() != DatatypeConstants.FIELD_UNDEFINED)
					results[4] = abox.addLiteral(ATermUtils.makePlainLiteral(toTZ(value.getTimezone())));
				else
					results[4] = abox.addLiteral(ATermUtils.makePlainLiteral(""));

				if (mergeResults(args, results))
					return true;
			}
			else
				if (restBound(args))
				{
					final Literal result = allBound.apply(abox, args[0], argList(args));
					if (result != null)
					{
						if (args[0] == null)
							args[0] = result;
						return true;
					}
				}

			return false;
		}

		@Override
		public String apply(final String... args)
		{
			if (args.length < 3 || args.length > 4)
				return null;

			String tz = "";
			if (args.length == 4)
				tz = args[3];

			final String dateString = toDate(args[0], args[1], args[2]) + tz;

			if (!checkValue(dateString, XSDDate.getInstance()))
				return null;

			return dateString;
		}

		@Override
		public boolean isApplicable(final boolean[] boundPositions)
		{
			return applicability(4, 5, boundPositions);
		}

	}

	private static class DateTime implements GeneralFunction, StringToStringFunction
	{

		StringFunctionAdapter allBound = new StringFunctionAdapter(this, XSD + "dateTime");

		@Override
		public boolean apply(final ABox abox, final Literal[] args)
		{
			// Assume applicability check means we have the right number of arguments.
			if (restBound(args))
			{
				final Literal result = allBound.apply(abox, args[0], argList(args));
				if (result != null)
				{
					if (args[0] == null)
						args[0] = result;
					return true;
				}
				return false;
			}

			if (args[0].getValue() instanceof XMLGregorianCalendar)
			{
				final XMLGregorianCalendar value = (XMLGregorianCalendar) args[0].getValue();
				final Literal[] results = new Literal[8];
				results[0] = args[0];
				results[1] = createInteger(abox, value.getYear());
				results[2] = createInteger(abox, value.getMonth());
				results[3] = createInteger(abox, value.getDay());
				results[4] = createInteger(abox, value.getHour());
				results[5] = createInteger(abox, value.getMinute());
				final BigDecimal fractionalSeconds = value.getFractionalSecond();
				results[6] = createDecimal(abox, (fractionalSeconds == null) ? value.getSecond() : fractionalSeconds.add(BigDecimal.valueOf(value.getSecond())));
				if (value.getTimezone() != DatatypeConstants.FIELD_UNDEFINED)
					results[7] = abox.addLiteral(ATermUtils.makePlainLiteral(toTZ(value.getTimezone())));

				if (mergeResults(args, results))
					return true;
			}

			return false;
		}

		@Override
		public String apply(final String... args)
		{
			if (args.length < 6 || args.length > 7)
				return null;

			String tz = "";
			if (args.length == 7)
				tz = args[6];

			final String dateTimeString = toDate(args[0], args[1], args[2]) + "T" + toTime(args[3], args[4], args[5]) + tz;

			if (!checkValue(dateTimeString, XSDDateTime.getInstance()))
				return null;

			return dateTimeString;
		}

		@Override
		public boolean isApplicable(final boolean[] boundPositions)
		{
			return applicability(7, 8, boundPositions);
		}
	}

	/**
	 * YearMonthDuration and DayTimeDuration creation are combined into this single class. The swrl spec seems a bit restrictive in not allowing the creation of
	 * negative time durations, so if the first argument is prefixed with a minus sign, we create a negative duration.
	 */
	private static class Duration implements GeneralFunction, StringToStringFunction
	{

		public static enum DURATIONTYPE
		{
			FULL(0, 5), YEARMONTH(0, 2), DAYTIME(2, 5);

			private int _start;
			@SuppressWarnings("unused")
			private int _end;

			DURATIONTYPE(final int start, final int end)
			{
				_start = start;
				_end = end;
			}

		}

		private static final char[] SEP = { 'Y', 'M', 'D', 'H', 'M', 'S' };

		private final StringFunctionAdapter allBound;
		private final DURATIONTYPE granularity;

		public Duration(final DURATIONTYPE dur)
		{
			granularity = dur;
			allBound = new StringFunctionAdapter(this, XSD + "duration");
		}

		@Override
		public boolean apply(final ABox abox, final Literal[] args)
		{
			if (restBound(args))
			{
				final Literal result = allBound.apply(abox, args[0], argList(args));
				if (result != null)
				{
					if (args[0] == null)
						args[0] = result;
					return true;
				}
				return false;
			}

			// Can't do anything with duration values until XSDLib is upgraded.

			return false;

		}

		@Override
		public String apply(final String... args)
		{
			if (args.length > SEP.length + granularity._start)
				return null;

			StringBuffer result;
			if (args[0].charAt(0) != '-')
				result = new StringBuffer("P");
			else
			{
				result = new StringBuffer("-P");
				args[0] = args[0].substring(1);
			}

			boolean seenTime = false;
			int i = granularity._start;
			for (final String arg : args)
			{
				if (arg.length() > 0)
				{
					if (i > 2 && seenTime == false)
					{
						seenTime = true;
						result.append('T');
					}

					result.append(arg);
					result.append(SEP[i]);
				}
				i++;

			}

			return result.toString();
		}

		@Override
		public boolean isApplicable(final boolean[] boundPositions)
		{
			return applicability(4, 5, boundPositions);
		}

	}

	private static class Time implements GeneralFunction, StringToStringFunction
	{

		StringFunctionAdapter allBound = new StringFunctionAdapter(this, XSD + "time");

		@Override
		public boolean apply(final ABox abox, final Literal[] args)
		{
			if (restBound(args))
			{
				final Literal result = allBound.apply(abox, args[0], argList(args));
				if (result != null)
				{
					if (args[0] == null)
						args[0] = result;
					return true;
				}
				return false;
			}

			if (args[0].getValue() instanceof XMLGregorianCalendar)
			{
				final XMLGregorianCalendar value = (XMLGregorianCalendar) args[0].getValue();
				final Literal[] results = new Literal[5];
				results[0] = args[0];
				results[1] = createInteger(abox, value.getHour());
				results[2] = createInteger(abox, value.getMinute());
				final BigDecimal fractionalSeconds = value.getFractionalSecond();
				results[3] = createDecimal(abox, (fractionalSeconds == null) ? value.getSecond() : fractionalSeconds.add(BigDecimal.valueOf(value.getSecond())));
				if (value.getTimezone() != DatatypeConstants.FIELD_UNDEFINED)
					results[4] = abox.addLiteral(ATermUtils.makePlainLiteral(toTZ(value.getTimezone())));

				if (mergeResults(args, results))
					return true;
			}

			return false;

		}

		@Override
		public String apply(final String... args)
		{
			if (args.length < 3 || args.length > 4)
				return null;

			String tz = "";
			if (args.length == 4)
				tz = args[3];

			final String timeString = toTime(args[0], args[1], args[2]) + tz;

			if (!checkValue(timeString, XSDTime.getInstance()))
				return null;

			return timeString;
		}

		@Override
		public boolean isApplicable(final boolean[] boundPositions)
		{
			return applicability(4, 5, boundPositions);
		}

	}

	public final static GeneralFunction date = new Date();

	public final static GeneralFunction dateTime = new DateTime();

	public final static Function dayTimeDuration = new StringFunctionAdapter(new Duration(Duration.DURATIONTYPE.DAYTIME), XSD + "duration");

	public final static GeneralFunction time = new Time();

	public final static Function yearMonthDuration = new StringFunctionAdapter(new Duration(Duration.DURATIONTYPE.YEARMONTH), XSD + "duration");

	private static boolean applicability(final int minargs, final int maxargs, final boolean[] boundPositions)
	{
		if (boundPositions.length < minargs || boundPositions.length > maxargs)
			return false;

		if (boundPositions[0])
			return true;

		for (int i = 1; i < boundPositions.length; i++)
			if (!boundPositions[i])
				return false;
		return true;

	}

	private static Literal[] argList(final Literal[] literals)
	{
		final Literal[] args = new Literal[literals.length - 1];
		for (int i = 1; i < literals.length; i++)
			args[i - 1] = literals[i];
		return args;
	}

	private static boolean checkValue(final String val, final Datatype<?> dt)
	{
		try
		{
			dt.getValue(ATermUtils.makeTypedLiteral(val, dt.getName()));
			return true;
		}
		catch (final InvalidLiteralException e)
		{
			_logger.log(Level.FINE, "", e);
			return false;
		}
	}

	private static Literal createDecimal(final ABox abox, final Number val)
	{
		final ATermAppl term = ATermUtils.makeTypedLiteral(val.toString(), XSD + "decimal");
		return abox.addLiteral(term);
	}

	private static Literal createInteger(final ABox abox, final Number val)
	{
		final ATermAppl term = ATermUtils.makeTypedLiteral(val.toString(), XSD + "integer");
		return abox.addLiteral(term);
	}

	private static boolean mergeResults(final Literal[] args1, final Literal[] args2)
	{
		for (int i = 0; i < args1.length; i++)
			if (args1[i] == null)
			{
				if (args2[i] == null)
					return false;
				args1[i] = args2[i];
			}
			else
				if (args2[i] != null)
					if (!ComparisonTesters.equal.test(new Literal[] { args1[i], args2[i] }))
						return false;
		return true;
	}

	private static String pad(final int p, String s)
	{
		while (s.length() < p)
			s = "0" + s;
		return s;
	}

	private static boolean restBound(final Literal[] args)
	{

		for (int i = 1; i < args.length; i++)
			if (args[i] == null)
				return false;
		return true;
	}

	private static String toDate(final String year, final String month, final String day)
	{
		return pad(4, year) + "-" + pad(2, month) + "-" + pad(2, day);
	}

	private static String toTime(final String hour, final String minute, String second)
	{
		String millis = "";
		final int point = second.indexOf('.');
		if (point >= 0)
		{
			millis = second.substring(point);
			second = second.substring(0, point);
		}

		return pad(2, hour) + ":" + pad(2, minute) + ":" + pad(2, second) + millis;
	}

	private static String toTZ(final int tz)
	{
		if (tz == 0)
			return "Z";
		else
			return String.format("%+03d:02d", tz / 60, tz % 60);
	}

}
