package com.clarkparsia.pellet.datatypes;

import static java.lang.String.format;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>
 * Title: Discrete Interval
 * </p>
 * <p>
 * Description: An abstract base class interval representation of discrete value
 * spaces. Instances are immutable.
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
public abstract class DiscreteInterval<T extends Number, U extends DiscreteInterval<T, U>> {

	protected static enum NullSemantics {
		GREATEST, LEAST, NA
	}

	public class ValueIterator implements Iterator<T> {
		private final T			last;
		private T				next;
		private final boolean	increment;

		public ValueIterator(T lower, T upper, boolean increment) {
			if( lower == null )
				throw new NullPointerException();

			this.next = lower;
			this.last = upper;
			this.increment = increment;
		}

		public boolean hasNext() {
			return next != null;
		}

		public T next() {
			final T ret = next;
			if( upper != null && equal( next, last ) )
				next = null;
			else
				next = increment
					? increment( next )
					: decrement( next );
			return ret;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	/*
	 * These constants are setup so that reference to the NullSemantics enum
	 * does not need qualification within this class.
	 */
	protected final static NullSemantics	GREATEST, LEAST, NA;
	private static final Logger				log;

	static {
		log = Logger.getLogger( DiscreteInterval.class.getCanonicalName() );

		GREATEST = NullSemantics.GREATEST;
		LEAST = NullSemantics.LEAST;
		NA = NullSemantics.NA;
	}

	private final T							lower;
	private final T							upper;

	/**
	 * Create a point interval. This is equivalent to
	 * {@link #DiscreteInterval(T, T)} with arguments <code>point,point</code>
	 * 
	 * @param point
	 *            Value of point interval
	 */
	public DiscreteInterval(T point) {
		if( point == null )
			throw new NullPointerException();

		this.lower = point;
		this.upper = point;
	}

	/**
	 * Create an interval.
	 * 
	 * @param lower
	 *            Interval lower bound
	 * @param upper
	 *            Interval upper bound
	 */
	public DiscreteInterval(T lower, T upper) {
		if( !valid( lower ) )
			throw new IllegalArgumentException();
		if( !valid( upper ) )
			throw new IllegalArgumentException();

		final int cmp = compare( lower, LEAST, upper, GREATEST );
		if( cmp > 0 ) {
			final String msg = format(
					"Lower bound of interval (%s) should not be greater than upper bound of interval (%s)",
					lower, upper );
			log.severe( msg );
			throw new IllegalArgumentException( msg );
		}

		this.lower = lower;
		this.upper = upper;
	}

	public boolean canUnionWith(U other) {
		final int ll = compare( this.lower, LEAST, other.lower, LEAST );
		final int uu = compare( this.upper, GREATEST, other.upper, GREATEST );
		if( ll <= 0 ) {
			if( uu < 0 ) {
				if( compare( this.upper, GREATEST, other.lower, LEAST ) < 0 ) {
					if( equal( increment( this.upper ), other.lower ) )
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
		else {
			if( uu > 0 ) {
				if( compare( this.lower, LEAST, other.upper, GREATEST ) > 0 ) {
					if( equal( increment( other.upper ), this.lower ) )
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
	}

	protected abstract U cast(DiscreteInterval<T, U> i);

	protected abstract int compare(T a, NullSemantics na, T b, NullSemantics nb);

	public boolean contains(T n) {
		if( !valid( n ) )
			throw new IllegalArgumentException();

		final int lcmp = compare( getLower(), LEAST, n, NA );
		if( lcmp > 0 )
			return false;
		if( lcmp == 0 )
			return true;

		final int ucmp = compare( getUpper(), GREATEST, n, NA );
		if( ucmp < 0 )
			return false;

		return true;
	}

	protected abstract U create(T lower, T upper);

	protected abstract T decrement(T t);

	protected abstract boolean equal(T a, T b);

	@Override
	public abstract boolean equals(Object obj);

	public T getLower() {
		return lower;
	}

	public T getUpper() {
		return upper;
	}

	/**
	 * Get the subinterval greater than n
	 * 
	 * @param n
	 * @return a new interval, formed by intersecting this interval with
	 *         (n,+inf) or <code>null</code> if that intersection is empty
	 */
	public U greater(T n) {
		if( n == null )
			throw new NullPointerException();
		if( !valid( n ) )
			throw new IllegalArgumentException();

		if( compare( getLower(), LEAST, n, NA ) > 0 )
			return cast( this );
		else if( compare( getUpper(), GREATEST, n, NA ) <= 0 )
			return null;
		else
			return create( increment( n ), getUpper() );
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lower == null)
			? 0
			: lower.hashCode());
		result = prime * result + ((upper == null)
			? 0
			: upper.hashCode());
		return result;
	}

	protected abstract T increment(T t);

	public U intersection(U that) {

		final int ll = compare( this.lower, LEAST, that.lower, LEAST );
		final int uu = compare( this.upper, GREATEST, that.upper, GREATEST );
		if( ll <= 0 ) {
			if( uu < 0 ) {
				if( compare( this.upper, GREATEST, that.lower, LEAST ) < 0 )
					return null;
				else
					return create( that.lower, this.upper );
			}
			else
				return that;
		}
		else {
			if( uu > 0 ) {
				if( compare( this.lower, LEAST, that.upper, GREATEST ) > 0 )
					return null;
				else
					return create( this.lower, that.upper );
			}
			else
				return cast( this );
		}
	}

	public boolean isFinite() {
		return lower != null && upper != null;
	}

	/**
	 * Get the subinterval less than n
	 * 
	 * @param n
	 * @return a new interval, formed by intersecting this interval with
	 *         (-inf,n) or <code>null</code> if that intersection is empty
	 */
	public U less(T n) {
		if( n == null )
			throw new NullPointerException();
		if( !valid( n ) )
			throw new IllegalArgumentException();

		if( compare( getUpper(), GREATEST, n, NA ) < 0 )
			return cast( this );
		else if( compare( getLower(), LEAST, n, NA ) >= 0 )
			return null;
		else
			return create( getLower(), decrement( n ) );
	}

	public List<U> remove(U other) {

		U before, after;

		final int ll = compare( this.lower, LEAST, other.lower, LEAST );
		final int lu = compare( this.lower, LEAST, other.upper, GREATEST );
		final int ul = compare( this.upper, GREATEST, other.lower, LEAST );
		final int uu = compare( this.upper, GREATEST, other.upper, GREATEST );

		if( ll < 0 ) {
			if( ul < 0 ) {
				before = cast( this );
				after = null;
			}
			else {
				before = create( this.lower, decrement( other.lower ) );
				if( uu <= 0 )
					after = null;
				else
					after = create( increment( other.upper ), this.upper );
			}
		}
		else {
			if( lu > 0 ) {
				before = null;
				after = cast( this );
			}
			else {
				if( uu <= 0 ) {
					before = null;
					after = null;
				}
				else {
					before = null;
					after = create( increment( other.upper ), this.upper );
				}
			}
		}

		if( before == null )
			if( after == null )
				return Collections.emptyList();
			else
				return Collections.singletonList( after );
		else if( after == null )
			return Collections.singletonList( before );
		else {
			@SuppressWarnings("unchecked")
			final List<U> ret = Arrays.asList( before, after );
			return ret;
		}
	}

	public abstract Number size();

	public List<U> union(U other) {
		U first, second;

		final int ll = compare( this.lower, LEAST, other.lower, LEAST );
		final int lu = compare( this.lower, LEAST, other.upper, GREATEST );
		final int ul = compare( this.upper, GREATEST, other.lower, LEAST );
		final int uu = compare( this.upper, GREATEST, other.upper, GREATEST );

		if( ll < 0 ) {
			if( ul < 0 ) {
				first = cast( this );
				second = other;
			}
			else {
				second = null;
				if( uu < 0 )
					first = create( this.lower, other.upper );
				else
					first = cast( this );
			}
		}
		else {
			if( lu > 0 ) {
				first = other;
				second = cast( this );
			}
			else {
				second = null;
				if( uu <= 0 )
					first = other;
				else
					first = create( other.lower, this.upper );
			}
		}

		if( first == null )
			if( second == null )
				return Collections.emptyList();
			else
				return Collections.singletonList( second );
		else if( second == null )
			return Collections.singletonList( first );
		else {
			@SuppressWarnings("unchecked")
			final List<U> ret = Arrays.asList( first, second );
			return ret;
		}
	}

	protected abstract boolean valid(T t);

	public Iterator<T> valueIterator() {
		if( lower == null ) {
			if( upper == null )
				throw new IllegalStateException();
			else
				return new ValueIterator( upper, lower, false );
		}
		else
			return new ValueIterator( lower, upper, true );
	}

	@Override
	public String toString() {
		return format( "[%s,%s]", getLower() != null
			? getLower()
			: "-Inf", getUpper() != null
			? getUpper()
			: "+Inf" );
	}
}
