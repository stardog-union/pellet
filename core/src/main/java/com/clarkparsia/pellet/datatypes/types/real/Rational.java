package com.clarkparsia.pellet.datatypes.types.real;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import com.clarkparsia.pellet.datatypes.OWLRealUtils;

/**
 * <p>
 * Title: Rational
 * </p>
 * <p>
 * Description: Object representation of rational numbers.
 * </p>
 * <p>
 * Note: care should be exercised if <code>Rational</code> objects are used as
 * keys in a <code>SortedMap</code> or elements in a SortedSet since
 * <code>Rational</code>'s <i>natural ordering is inconsistent with equals</i>.
 * See <code>Comparable</code>, <code>SortedMap</code> or <code>SortedSet</code>
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
public class Rational extends Number implements Comparable<Rational> {

	private static final long		serialVersionUID;
	private static final Pattern	lexicalPattern;

	static {
		serialVersionUID = 1L;

		String regex = "\\s*([+-])?\\s*(\\d+)\\s*/\\s*(\\d+)\\s*";
		lexicalPattern = Pattern.compile( regex );
	}

	public static int compare(Rational a, Rational b) {
		final int as = a.signum();
		final int bs = b.signum();
		if( as == bs ) {
			if( OWLRealUtils.compare( a.getDenominator(), b.getDenominator() ) == 0 ) {
				return OWLRealUtils.compare( a.getNumerator(), b.getNumerator() );
			}
			else {
				final Number qa = a.getQuotient();
				final Number qb = b.getQuotient();
				final int qCmp = OWLRealUtils.compare( qa, qb );
				if( qCmp == 0 ) {
					if( a.isQuotientExact() && b.isQuotientExact() )
						return 0;
					else {
						/*
						 * TODO: Find a more efficient implementation
						 */
						final BigInteger aprod = OWLRealUtils.bigInteger( a.getNumerator() )
								.multiply( OWLRealUtils.bigInteger( b.getDenominator() ) );
						final BigInteger bprod = OWLRealUtils.bigInteger( b.getNumerator() )
								.multiply( OWLRealUtils.bigInteger( a.getDenominator() ) );
						return aprod.compareTo( bprod );
					}
				}
				else
					return qCmp;
			}
		}
		else {
			return as > bs
				? 1
				: -1;
		}
	}

	public static Rational valueOf(String s) throws NumberFormatException {
		if( s == null )
			throw new NullPointerException();

		final Matcher m = lexicalPattern.matcher( s );
		if( !m.matches() )
			throw new NumberFormatException( s );

		final String sLex = m.group( 1 );
		final String nLex = m.group( 2 );
		final String dLex = m.group( 3 );

		final Number n = OWLRealUtils.getCanonicalObject( DatatypeConverter
				.parseInteger( (sLex == null)
					? nLex
					: sLex + nLex ) );
		final Number d = OWLRealUtils.getCanonicalObject( DatatypeConverter.parseInteger( dLex ) );
		if( OWLRealUtils.signum( d ) != 1 )
			throw new NumberFormatException( s );

		return new Rational( n, d );
	}

	public static Rational simplify(Rational r) {
		BigInteger n = OWLRealUtils.bigInteger( r.getNumerator() );
		BigInteger d = OWLRealUtils.bigInteger( r.getDenominator() );

		BigInteger gcd = n.gcd( d );
		while( !gcd.equals( BigInteger.ONE ) ) {
			n = n.divide( gcd );
			d = d.divide( gcd );
			gcd = n.gcd( d );
		}

		final Number canonicalNum = OWLRealUtils.getCanonicalObject( n );
		final Number canonicalDenom = OWLRealUtils.getCanonicalObject( d );
		if( canonicalNum.equals( r.getNumerator() ) && canonicalDenom.equals( r.getDenominator() ) )
			return r;
		else
			return new Rational( canonicalNum, canonicalDenom );
	}

	private final Number	denominator;
	private final boolean	exact;
	private final Number	numerator;
	private final Number	quotient;

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append( DatatypeConverter.printInteger( OWLRealUtils.bigInteger( numerator ) ) );
		buf.append( " / " );
		buf.append( DatatypeConverter.printInteger( OWLRealUtils.bigInteger( denominator ) ) );
		return buf.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((denominator == null)
			? 0
			: denominator.hashCode());
		result = prime * result + ((numerator == null)
			? 0
			: numerator.hashCode());
		return result;
	}

	/**
	 * Compares this <code>Rational</code> with the specified
	 * <code>Object</code> for equality. Unlike compareTo, this method considers
	 * two <code>Rational</code> objects equal only if they are equal in
	 * numerator and denominator.
	 * 
	 * @param obj
	 *            <code>Object</code> to which this <code>Rational</code> is to
	 *            be compared
	 * @return <code>true> if and only if the specified <code>Object</code> is a
	 *         <code>Rational</code> whose numerator and denominator are equal
	 *         to this <code>Rational</code>'s.
	 */
	@Override
	public boolean equals(Object obj) {
		if( this == obj )
			return true;
		if( obj == null )
			return false;
		if( getClass() != obj.getClass() )
			return false;
		Rational other = (Rational) obj;
		if( denominator == null ) {
			if( other.denominator != null )
				return false;
		}
		else if( !denominator.equals( other.denominator ) )
			return false;
		if( numerator == null ) {
			if( other.numerator != null )
				return false;
		}
		else if( !numerator.equals( other.numerator ) )
			return false;
		return true;
	}

	/**
	 * Construct a rational number from
	 * 
	 * @param numerator
	 *            An integer value
	 * @param denominator
	 *            A positive integer value
	 * @throws IllegalArgumentException
	 *             if this condition does not hold for the input parameters
	 * 
	 * 
	 *             <code>OWLRealUtils.acceptable(numerator) && OWLRealUtils.isInteger(numerator) && OWLRealUtils.acceptable(denominator) && OWLRealUtils.isInteger(denominator) && denominator > 0</code>
	 */
	public Rational(Number numerator, Number denominator) {
		if( numerator == null )
			throw new NullPointerException();
		if( !OWLRealUtils.isInteger( numerator ) )
			throw new IllegalArgumentException();

		if( denominator == null )
			throw new NullPointerException();
		if( OWLRealUtils.signum( denominator ) <= 0 )
			throw new IllegalArgumentException();
		if( !OWLRealUtils.isInteger( denominator ) )
			throw new IllegalArgumentException();

		this.numerator = numerator;
		this.denominator = denominator;

		final BigDecimal n = OWLRealUtils.bigDecimal( numerator );
		final BigDecimal d = OWLRealUtils.bigDecimal( denominator );

		Number q;
		boolean ex;
		try {
			q = n.divide( d );
			ex = true;
		} catch( ArithmeticException e ) {
			/*
			 * TODO: Consider if this MathContext is appropriate
			 */
			q = n.divide( d, MathContext.DECIMAL32 );
			ex = false;
		}
		this.quotient = q;
		this.exact = ex;
	}

	public int compareTo(Rational that) {
		return compare( this, that );
	}

	@Override
	public double doubleValue() {
		return quotient.doubleValue();
	}

	@Override
	public float floatValue() {
		return quotient.floatValue();
	}

	public Number getDenominator() {
		return denominator;
	}

	public Number getNumerator() {
		return numerator;
	}

	public Number getQuotient() {
		return quotient;
	}

	@Override
	public int intValue() {
		return quotient.intValue();
	}

	public boolean isQuotientExact() {
		return exact;
	}

	@Override
	public long longValue() {
		return quotient.longValue();
	}

	public int signum() {
		return OWLRealUtils.signum( numerator );
	}

}
