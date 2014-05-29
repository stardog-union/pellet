package com.clarkparsia.pellet.datatypes.types.real;

import com.clarkparsia.pellet.datatypes.DiscreteInterval;
import com.clarkparsia.pellet.datatypes.OWLRealUtils;

public class IntegerInterval extends DiscreteInterval<Number, IntegerInterval> {

	private static final IntegerInterval	unconstrained;

	static {
		unconstrained = new IntegerInterval( null, null );
	}

	public static IntegerInterval allIntegers() {
		return unconstrained;
	}

	public IntegerInterval(Number point) {
		super( point );
	}

	public IntegerInterval(Number lower, Number upper) {
		super( lower, upper );
	}

	@Override
	protected IntegerInterval cast(DiscreteInterval<Number, IntegerInterval> i) {
		if( IntegerInterval.class.isInstance( i ) )
			return IntegerInterval.class.cast( i );
		else
			throw new IllegalArgumentException();
	}

	@Override
	protected int compare(Number a, NullSemantics na, Number b, NullSemantics nb) {

		if( a == null && NA.equals( na ) )
			throw new NullPointerException();

		if( b == null && NA.equals( nb ) )
			throw new NullPointerException();

		if( a == null ) {
			if( b == null ) {
				if( na.equals( nb ) )
					return 0;
				else if( LEAST.equals( na ) )
					return -1;
				else
					return 1;
			}
			else {
				if( LEAST.equals( na ) )
					return -1;
				else
					return 1;
			}
		}
		else {
			if( b == null ) {
				if( GREATEST.equals( nb ) )
					return -1;
				else
					return 1;
			}
			else
				return OWLRealUtils.compare( a, b );
		}
	}

	@Override
	protected IntegerInterval create(Number lower, Number upper) {
		return new IntegerInterval( lower, upper );
	}

	@Override
	protected Number decrement(Number t) {
		return OWLRealUtils.integerDecrement( t );
	}

	@Override
	protected boolean equal(Number a, Number b) {
		if( a == null )
			throw new NullPointerException();
		if( b == null )
			throw new NullPointerException();
		return OWLRealUtils.compare( a, b ) == 0;
	}

	@Override
	public boolean equals(Object obj) {
		if( this == obj )
			return true;
		if( obj == null )
			return false;
		if( getClass() != obj.getClass() )
			return false;
		IntegerInterval other = (IntegerInterval) obj;
		if( getLower() == null ) {
			if( other.getLower() != null )
				return false;
		}
		else {
			if( other.getLower() == null )
				return false;
			if( OWLRealUtils.compare( getLower(), other.getLower() ) != 0 )
				return false;
		}
		if( getUpper() == null ) {
			if( other.getUpper() != null )
				return false;
		}
		else {
			if( other.getUpper() == null )
				return false;
			if( OWLRealUtils.compare( getUpper(), other.getUpper() ) != 0 )
				return false;
		}

		return true;
	}

	@Override
	protected Number increment(Number n) {
		return OWLRealUtils.integerIncrement( n );
	}

	@Override
	public Number size() {
		if( getLower() == null || getUpper() == null )
			throw new IllegalStateException();
		return OWLRealUtils.integerIncrement( OWLRealUtils.integerDifference( getUpper(),
				getLower() ) );
	}

	@Override
	protected boolean valid(Number n) {
		if( n == null )
			return true;

		return OWLRealUtils.acceptable( n.getClass() ) && OWLRealUtils.isInteger( n );
	}

}
