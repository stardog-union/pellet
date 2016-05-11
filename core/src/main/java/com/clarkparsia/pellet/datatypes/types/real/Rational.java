package com.clarkparsia.pellet.datatypes.types.real;

import com.clarkparsia.pellet.datatypes.OWLRealUtils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.DatatypeConverter;

/**
 * <p>
 * Title: Rational
 * </p>
 * <p>
 * Description: Object representation of rational numbers.
 * </p>
 * <p>
 * Note: care should be exercised if <code>Rational</code> objects are used as keys in a <code>SortedMap</code> or elements in a SortedSet since
 * <code>Rational</code>'s <i>natural ordering is inconsistent with equals</i>. See <code>Comparable</code>, <code>SortedMap</code> or <code>SortedSet</code>
 * for more information.
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
public class Rational extends Number implements Comparable<Rational>
{
	private static final long serialVersionUID = 1L;
	private static final Pattern _lexicalPattern = Pattern.compile("\\s*([+-])?\\s*(\\d+)\\s*/\\s*(\\d+)\\s*");

	public static int compare(final Rational a, final Rational b)
	{
		final int as = a.signum();
		final int bs = b.signum();
		if (as == bs)
		{
			if (OWLRealUtils.compare(a.getDenominator(), b.getDenominator()) == 0)
				return OWLRealUtils.compare(a.getNumerator(), b.getNumerator());
			else
			{
				final Number qa = a.getQuotient();
				final Number qb = b.getQuotient();
				final int qCmp = OWLRealUtils.compare(qa, qb);
				if (qCmp == 0)
				{
					if (a.isQuotientExact() && b.isQuotientExact())
						return 0;
					else
					{
						/*
						 * TODO: Find a more efficient implementation
						 */
						final BigInteger aprod = OWLRealUtils.bigInteger(a.getNumerator()).multiply(OWLRealUtils.bigInteger(b.getDenominator()));
						final BigInteger bprod = OWLRealUtils.bigInteger(b.getNumerator()).multiply(OWLRealUtils.bigInteger(a.getDenominator()));
						return aprod.compareTo(bprod);
					}
				}
				else
					return qCmp;
			}
		}
		else
			return as > bs ? 1 : -1;
	}

	public static Rational valueOf(final String s) throws NumberFormatException
	{
		if (s == null)
			throw new NullPointerException();

		final Matcher m = _lexicalPattern.matcher(s);
		if (!m.matches())
			throw new NumberFormatException(s);

		final String sLex = m.group(1);
		final String nLex = m.group(2);
		final String dLex = m.group(3);

		final Number n = OWLRealUtils.getCanonicalObject(DatatypeConverter.parseInteger((sLex == null) ? nLex : sLex + nLex));
		final Number d = OWLRealUtils.getCanonicalObject(DatatypeConverter.parseInteger(dLex));
		if (OWLRealUtils.signum(d) != 1)
			throw new NumberFormatException(s);

		return new Rational(n, d);
	}

	public static Rational simplify(final Rational r)
	{
		BigInteger n = OWLRealUtils.bigInteger(r.getNumerator());
		BigInteger d = OWLRealUtils.bigInteger(r.getDenominator());

		BigInteger gcd = n.gcd(d);
		while (!gcd.equals(BigInteger.ONE))
		{
			n = n.divide(gcd);
			d = d.divide(gcd);
			gcd = n.gcd(d);
		}

		final Number canonicalNum = OWLRealUtils.getCanonicalObject(n);
		final Number canonicalDenom = OWLRealUtils.getCanonicalObject(d);
		if (canonicalNum.equals(r.getNumerator()) && canonicalDenom.equals(r.getDenominator()))
			return r;
		else
			return new Rational(canonicalNum, canonicalDenom);
	}

	private final Number _denominator;
	private final boolean exact;
	private final Number _numerator;
	private final Number quotient;

	@Override
	public String toString()
	{
		final StringBuffer buf = new StringBuffer();
		buf.append(DatatypeConverter.printInteger(OWLRealUtils.bigInteger(_numerator)));
		buf.append(" / ");
		buf.append(DatatypeConverter.printInteger(OWLRealUtils.bigInteger(_denominator)));
		return buf.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_denominator == null) ? 0 : _denominator.hashCode());
		result = prime * result + ((_numerator == null) ? 0 : _numerator.hashCode());
		return result;
	}

	/**
	 * Compares this <code>Rational</code> with the specified <code>Object</code> for equality. Unlike compareTo, this method considers two
	 * <code>Rational</code> objects equal only if they are equal in _numerator and _denominator.
	 *
	 * @param obj <code>Object</code> to which this <code>Rational</code> is to be compared
	 * @return <code>true> if and only if the specified <code>Object</code> is a <code>Rational</code> whose _numerator and _denominator are equal to this
	 *         <code>Rational</code>'s.
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Rational other = (Rational) obj;
		if (_denominator == null)
		{
			if (other._denominator != null)
				return false;
		}
		else
			if (!_denominator.equals(other._denominator))
				return false;
		if (_numerator == null)
		{
			if (other._numerator != null)
				return false;
		}
		else
			if (!_numerator.equals(other._numerator))
				return false;
		return true;
	}

	/**
	 * Construct a rational number from
	 *
	 * @param _numerator An integer value
	 * @param _denominator A positive integer value
	 * @throws IllegalArgumentException if this condition does not hold for the input parameters
	 *         <code>OWLRealUtils.acceptable(_numerator) && OWLRealUtils.isInteger(_numerator) && OWLRealUtils.acceptable(_denominator) && OWLRealUtils.isInteger(_denominator) && _denominator > 0</code>
	 */
	public Rational(final Number numerator, final Number denominator)
	{
		if (numerator == null)
			throw new NullPointerException();
		if (!OWLRealUtils.isInteger(numerator))
			throw new IllegalArgumentException();

		if (denominator == null)
			throw new NullPointerException();
		if (OWLRealUtils.signum(denominator) <= 0)
			throw new IllegalArgumentException();
		if (!OWLRealUtils.isInteger(denominator))
			throw new IllegalArgumentException();

		this._numerator = numerator;
		this._denominator = denominator;

		final BigDecimal n = OWLRealUtils.bigDecimal(numerator);
		final BigDecimal d = OWLRealUtils.bigDecimal(denominator);

		Number q;
		boolean ex;
		try
		{
			q = n.divide(d);
			ex = true;
		}
		catch (final ArithmeticException e)
		{
			/*
			 * TODO: Consider if this MathContext is appropriate
			 */
			q = n.divide(d, MathContext.DECIMAL32);
			ex = false;
		}
		this.quotient = q;
		this.exact = ex;
	}

	@Override
	public int compareTo(final Rational that)
	{
		return compare(this, that);
	}

	@Override
	public double doubleValue()
	{
		return quotient.doubleValue();
	}

	@Override
	public float floatValue()
	{
		return quotient.floatValue();
	}

	public Number getDenominator()
	{
		return _denominator;
	}

	public Number getNumerator()
	{
		return _numerator;
	}

	public Number getQuotient()
	{
		return quotient;
	}

	@Override
	public int intValue()
	{
		return quotient.intValue();
	}

	public boolean isQuotientExact()
	{
		return exact;
	}

	@Override
	public long longValue()
	{
		return quotient.longValue();
	}

	public int signum()
	{
		return OWLRealUtils.signum(_numerator);
	}

}
