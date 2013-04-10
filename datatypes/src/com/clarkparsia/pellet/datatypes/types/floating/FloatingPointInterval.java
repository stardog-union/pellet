package com.clarkparsia.pellet.datatypes.types.floating;

import com.clarkparsia.pellet.datatypes.DiscreteInterval;

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
public class FloatingPointInterval<T extends Number & Comparable<T>> extends
		DiscreteInterval<T, FloatingPointInterval<T>> {

	public static <U extends Number & Comparable<U>> FloatingPointInterval<U> unconstrained(
			FloatingPointType<U> type) {
		return new FloatingPointInterval<U>( type, type.getNegativeInfinity(), type
				.getPositiveInfinity() );
	}

	private final FloatingPointType<T>	type;

	public FloatingPointInterval(FloatingPointType<T> type, T point) {
		super( point );

		if( type == null )
			throw new NullPointerException();

		if( type.isNaN( point ) )
			throw new IllegalArgumentException();

		this.type = type;

		if( !valid( point ) )
			throw new IllegalArgumentException();
	}

	public FloatingPointInterval(FloatingPointType<T> type, T lower, T upper) {
		super( lower, upper );

		if( type == null )
			throw new NullPointerException();

		if( type.isNaN( lower ) )
			throw new IllegalArgumentException();
		if( type.isNaN( upper ) )
			throw new IllegalArgumentException();

		this.type = type;

		if( !valid( lower ) )
			throw new IllegalArgumentException();
		if( !valid( upper ) )
			throw new IllegalArgumentException();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected FloatingPointInterval<T> cast(DiscreteInterval<T, FloatingPointInterval<T>> i) {
		if( i instanceof FloatingPointInterval )
			return (FloatingPointInterval<T>) i;
		else
			throw new IllegalArgumentException();
	}

	@Override
	protected int compare(T a, NullSemantics na, T b, NullSemantics nb) {
		if( a == null )
			throw new NullPointerException();
		if( b == null )
			throw new NullPointerException();

		return a.compareTo( b );
	}

	@Override
	public boolean contains(T n) {
		if( type.isNaN( n ) )
			return false;

		return super.contains( n );
	}

	@Override
	protected FloatingPointInterval<T> create(T lower, T upper) {
		return new FloatingPointInterval<T>( type, lower, upper );
	}

	@Override
	protected T decrement(T t) {
		return type.decrement( t );
	}

	@Override
	protected boolean equal(T a, T b) {
		return a.equals( b );
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
		if( !getLower().equals( other.getLower() ) )
			return false;
		if( !getUpper().equals( other.getUpper() ) )
			return false;

		return true;
	}

	@Override
	public FloatingPointInterval<T> greater(T n) {
		if( type.isNaN( n ) )
			throw new IllegalArgumentException();
		return super.greater( n );
	}

	@Override
	protected T increment(T t) {
		return type.increment( t );
	}

	@Override
	public FloatingPointInterval<T> less(T n) {
		if( type.isNaN( n ) )
			throw new IllegalArgumentException();
		return super.less( n );
	}

	@Override
	public Number size() {
		return type.intervalSize( getLower(), getUpper() );
	}

	@Override
	protected boolean valid(T t) {
		/*
		 * The type == null check here is necessary because this method is
		 * called by the super's constructor before the type field is
		 * initialized
		 */
		return t != null && (type == null || type.isInstance( t ));
	}
}
