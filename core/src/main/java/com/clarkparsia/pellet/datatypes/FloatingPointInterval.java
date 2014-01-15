package com.clarkparsia.pellet.datatypes;

import static java.lang.String.format;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>
 * Title: Floating Point Interval
 * </p>
 * <p>
 * Description: An immutable interval representation supporting the value space
 * of floating point numbers.
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
public class FloatingPointInterval<T extends Number & Comparable<T>> {

	public class ValueIterator implements Iterator<T> {
		private final T	last;
		private T		next;

		public ValueIterator(T lower, T upper) {
			if( lower == null )
				throw new NullPointerException();
			if( upper == null )
				throw new NullPointerException();

			this.next = lower;
			this.last = upper;
		}

		public boolean hasNext() {
			return next != null;
		}

		public T next() {
			final T ret = next;
			if( next.equals( last ) )
				next = null;
			else
				next = type.increment( next );
			return ret;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private static final Logger	log;

	static {
		log = Logger.getLogger( FloatingPointInterval.class.getCanonicalName() );
	}

	public static <U extends Number & Comparable<U>> FloatingPointInterval<U> unconstrained(
			FloatingPointType<U> type) {
		return new FloatingPointInterval<U>( type, type.getNegativeInfinity(), type
				.getPositiveInfinity() );
	}

	private final T						lower;
	private final FloatingPointType<T>	type;
	private final T						upper;

	/**
	 * Create a point interval. This is equivalent to
	 * {@link #IEEEFloatInterval(Float, Float)} with arguments
	 * <code>point,point,true,true</code>
	 * 
	 * @param point
	 *            Value of point interval
	 */
	public FloatingPointInterval(FloatingPointType<T> type, T point) {
		if( type == null )
			throw new NullPointerException();

		if( point == null )
			throw new NullPointerException();
		if( type.isNaN( point ) )
			throw new IllegalArgumentException();

		this.type = type;
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
	public FloatingPointInterval(FloatingPointType<T> type, T lower, T upper) {
		if( type == null )
			throw new NullPointerException();

		if( lower == null )
			throw new NullPointerException();
		if( upper == null )
			throw new NullPointerException();
		if( type.isNaN( lower ) )
			throw new IllegalArgumentException();
		if( type.isNaN( upper ) )
			throw new IllegalArgumentException();

		final int cmp = lower.compareTo( upper );
		if( cmp > 0 ) {
			final String msg = format(
					"Lower bound of interval (%s) should not be greater than upper bound of interval (%s)",
					lower, upper );
			log.severe( msg );
			throw new IllegalArgumentException( msg );
		}

		this.type = type;
		this.lower = lower;
		this.upper = upper;
	}

	public boolean canUnionWith(FloatingPointInterval<T> other) {
		final int ll = this.lower.compareTo( other.lower );
		final int uu = this.upper.compareTo( other.upper );
		if( ll <= 0 ) {
			if( uu < 0 ) {
				if( this.upper.compareTo( other.lower ) < 0 ) {
					if( type.increment( this.upper ).equals( other.lower ) )
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
				if( this.lower.compareTo( other.upper ) > 0 ) {
					if( type.increment( other.upper ).equals( this.lower ) )
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

	public boolean contains(T n) {
		if( type.isNaN( n ) )
			return false;

		final int lcmp = getLower().compareTo( n );
		if( lcmp > 0 )
			return false;
		if( lcmp == 0 )
			return true;

		final int ucmp = getUpper().compareTo( n );
		if( ucmp < 0 )
			return false;

		return true;
	}

	private FloatingPointInterval<T> create(T lower, T upper) {
		return new FloatingPointInterval<T>( type, lower, upper );
	}

	@Override
	public boolean equals(Object obj) {
		if( this == obj )
			return true;
		if( obj == null )
			return false;
		if( getClass() != obj.getClass() )
			return false;
		FloatingPointInterval<?> other = (FloatingPointInterval<?>) obj;
		if( !lower.equals( other.lower ) )
			return false;
		if( !upper.equals( other.upper ) )
			return false;

		return true;
	}

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
	public FloatingPointInterval<T> greater(T n) {
		if( n == null )
			throw new NullPointerException();

		if( type.isNaN( n ) )
			throw new IllegalArgumentException();

		if( getLower().compareTo( n ) >= 0 )
			return this;
		else if( getUpper().compareTo( n ) <= 0 )
			return null;
		else
			return create( type.increment( n ), getUpper() );
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null)
			? 0
			: type.hashCode());
		result = prime * result + ((lower == null)
			? 0
			: lower.hashCode());
		result = prime * result + ((upper == null)
			? 0
			: upper.hashCode());
		return result;
	}

	public FloatingPointInterval<T> intersection(FloatingPointInterval<T> that) {

		final int ll = this.lower.compareTo( that.lower );
		final int uu = this.upper.compareTo( that.upper );
		if( ll <= 0 ) {
			if( uu < 0 ) {
				if( this.upper.compareTo( that.lower ) < 0 )
					return null;
				else
					return create( that.lower, this.upper );
			}
			else
				return that;
		}
		else {
			if( uu > 0 ) {
				if( this.lower.compareTo( that.upper ) > 0 )
					return null;
				else
					return create( this.lower, that.upper );
			}
			else
				return this;
		}
	}

	/**
	 * Get the subinterval less than n
	 * 
	 * @param n
	 * @return a new interval, formed by intersecting this interval with
	 *         (-inf,n) or <code>null</code> if that intersection is empty
	 */
	public FloatingPointInterval<T> less(T n) {
		if( n == null )
			throw new NullPointerException();

		if( type.isNaN( n ) )
			throw new IllegalArgumentException();

		if( getUpper().compareTo( n ) <= 0 )
			return this;
		else if( getLower().compareTo( n ) >= 0 )
			return null;
		else
			return create( getLower(), type.decrement( n ) );
	}

	public List<FloatingPointInterval<T>> remove(FloatingPointInterval<T> other) {

		FloatingPointInterval<T> before, after;

		final int ll = this.lower.compareTo( other.lower );
		final int lu = this.lower.compareTo( other.upper );
		final int ul = this.upper.compareTo( other.lower );
		final int uu = this.upper.compareTo( other.upper );

		if( ll < 0 ) {
			if( ul < 0 ) {
				before = this;
				after = null;
			}
			else {
				{
					final T f = type.decrement( other.lower );
					if( f.equals( type.getNegativeInfinity() ) )
						before = null;
					else
						before = create( this.lower, f );
				}
				if( uu <= 0 )
					after = null;
				else {
					final T f = type.increment( other.upper );
					if( f.equals( type.getPositiveInfinity() ) )
						after = null;
					else
						after = create( type.increment( other.upper ), this.upper );
				}
			}
		}
		else {
			if( lu > 0 ) {
				before = null;
				after = this;
			}
			else {
				if( uu <= 0 ) {
					before = null;
					after = null;
				}
				else {
					before = null;
					final T f = type.increment( other.upper );
					if( f.equals( type.getPositiveInfinity() ) )
						after = create( type.increment( other.upper ), this.upper );
					else
						after = null;
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
		else
			return Arrays.asList( before, after );
	}

	public Number size() {
		return type.intervalSize( lower, upper );
	}

	public List<FloatingPointInterval<T>> union(FloatingPointInterval<T> other) {
		FloatingPointInterval<T> first, second;

		final int ll = this.lower.compareTo( other.lower );
		final int lu = this.lower.compareTo( other.upper );
		final int ul = this.upper.compareTo( other.lower );
		final int uu = this.upper.compareTo( other.upper );

		if( ll < 0 ) {
			if( ul < 0 ) {
				first = this;
				second = other;
			}
			else {
				second = null;
				if( uu < 0 )
					first = create( this.lower, other.upper );
				else
					first = this;
			}
		}
		else {
			if( lu > 0 ) {
				first = other;
				second = this;
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
		else
			return Arrays.asList( first, second );
	}

	public Iterator<T> valueIterator() {
		return new ValueIterator( lower, upper );
	}
}
