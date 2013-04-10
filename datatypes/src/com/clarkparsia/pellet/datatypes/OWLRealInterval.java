package com.clarkparsia.pellet.datatypes;

import static com.clarkparsia.pellet.datatypes.OWLRealUtils.integerDecrement;
import static com.clarkparsia.pellet.datatypes.OWLRealUtils.integerDifference;
import static com.clarkparsia.pellet.datatypes.OWLRealUtils.integerIncrement;
import static com.clarkparsia.pellet.datatypes.OWLRealUtils.isInteger;
import static com.clarkparsia.pellet.datatypes.OWLRealUtils.roundDown;
import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

/**
 * <p>
 * Title: <code>owl:real</code> Interval
 * </p>
 * <p>
 * Description: An immutable interval representation supporting the
 * <code>owl:real</code> value space. Supports continuous (real) number lines,
 * discontinuous (real - integer) number lines, and discrete (integer) number
 * lines.
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
public class OWLRealInterval {

	public static class IntegerIterator implements Iterator<Number> {

		private final boolean	increment;
		private final Number	last;
		private Number			next;

		public IntegerIterator(Number first, Number last, boolean increment) {
			this.last = last;
			this.increment = increment;
			this.next = first;
		}

		public boolean hasNext() {
			return next != null;
		}

		public Number next() {
			if( next == null )
				throw new NoSuchElementException();

			Number n = next;

			if( (last != null) && (OWLRealUtils.compare( next, last ) == 0) ) {
				next = null;
			}
			else {
				next = increment
					? OWLRealUtils.integerIncrement( next )
					: OWLRealUtils.integerDecrement( next );
			}

			return n;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	public static enum LineType {
		CONTINUOUS, INTEGER_EXCLUDED, INTEGER_ONLY;

		public LineType intersect(LineType other) {
			if( other == null )
				throw new NullPointerException();

			switch ( this ) {
			case CONTINUOUS:
				return other;
			case INTEGER_ONLY:
				if( other.equals( INTEGER_EXCLUDED ) )
					return null;
				else
					return INTEGER_ONLY;
			case INTEGER_EXCLUDED:
				if( other.equals( INTEGER_ONLY ) )
					return null;
				else
					return INTEGER_EXCLUDED;
			default:
				throw new IllegalArgumentException();
			}
		}
	}

	private static final Logger		log;

	private static OWLRealInterval	unconstrainedInteger;

	private static OWLRealInterval	unconstrainedReal;

	static {
		log = Logger.getLogger( OWLRealInterval.class.getCanonicalName() );
	}

	static {
		unconstrainedReal = new OWLRealInterval( null, null, true, true, LineType.CONTINUOUS );
		unconstrainedInteger = new OWLRealInterval( null, null, true, true, LineType.INTEGER_ONLY );
	}

	public static OWLRealInterval allIntegers() {
		return unconstrainedInteger;
	}

	public static OWLRealInterval allReals() {
		return unconstrainedReal;
	}

	private static IntervalRelations compare(OWLRealInterval a, OWLRealInterval b) {
		int ll = compareLowerLower( a, b );

		if( ll < 0 ) {
			int ul = compareUpperLower( a, b );
			if( ul < 0 )
				return IntervalRelations.PRECEDES;
			else if( ul == 0 ) {
				if( a.inclusiveUpper() ) {
					if( b.inclusiveLower() )
						return IntervalRelations.OVERLAPS;
					else
						return IntervalRelations.MEETS;
				}
				else if( b.inclusiveLower() )
					return IntervalRelations.MEETS;
				else
					return IntervalRelations.PRECEDES;
			}
			else {
				int uu = compareUpperUpper( a, b );
				if( uu < 0 )
					return IntervalRelations.OVERLAPS;
				else if( uu == 0 )
					return IntervalRelations.FINISHED_BY;
				else
					return IntervalRelations.CONTAINS;
			}
		}
		else if( ll == 0 ) {
			int uu = compareUpperUpper( a, b );
			if( uu < 0 )
				return IntervalRelations.STARTS;
			else if( uu == 0 )
				return IntervalRelations.EQUALS;
			else
				return IntervalRelations.STARTED_BY;
		}
		else {
			int lu = -compareUpperLower( b, a );
			if( lu < 0 ) {
				int uu = compareUpperUpper( a, b );
				if( uu < 0 )
					return IntervalRelations.DURING;
				else if( uu == 0 )
					return IntervalRelations.FINISHES;
				else
					return IntervalRelations.OVERLAPPED_BY;
			}
			else if( lu == 0 ) {
				if( b.inclusiveUpper() ) {
					if( a.inclusiveLower() )
						return IntervalRelations.OVERLAPPED_BY;
					else
						return IntervalRelations.MET_BY;
				}
				else if( a.inclusiveLower() )
					return IntervalRelations.MET_BY;
				else
					return IntervalRelations.PRECEDED_BY;
			}
			else
				return IntervalRelations.PRECEDED_BY;
		}
	}

	private static int compareLowerLower(OWLRealInterval a, OWLRealInterval other) {
		int ll;
		if( !a.boundLower() ) {
			if( !other.boundLower() )
				ll = 0;
			else
				ll = -1;
		}
		else {
			if( !other.boundLower() )
				ll = 1;
			else {
				ll = OWLRealUtils.compare( a.getLower(), other.getLower() );
				if( ll == 0 ) {
					if( a.inclusiveLower() ) {
						if( !other.inclusiveLower() )
							ll = -1;
					}
					else if( other.inclusiveLower() )
						ll = 1;
				}
			}
		}
		return ll;
	}

	private static int compareUpperLower(OWLRealInterval a, OWLRealInterval b) {
		int ul;
		if( !a.boundUpper() )
			ul = 1;
		else if( !b.boundLower() )
			ul = 1;
		else
			ul = OWLRealUtils.compare( a.getUpper(), b.getLower() );
		return ul;
	}

	private static int compareUpperUpper(OWLRealInterval a, OWLRealInterval b) {
		int uu;
		if( !a.boundUpper() ) {
			if( !b.boundUpper() )
				uu = 0;
			else
				uu = 1;
		}
		else if( !b.boundUpper() )
			uu = -1;
		else {
			uu = OWLRealUtils.compare( a.getUpper(), b.getUpper() );
			if( uu == 0 ) {
				if( a.inclusiveUpper() ) {
					if( !b.inclusiveUpper() )
						uu = 1;
				}
				else if( b.inclusiveUpper() )
					uu = -1;
			}
		}
		return uu;
	}

	private final boolean	finite;
	private final boolean	inclusiveLower;
	private final boolean	inclusiveUpper;
	private final Number	lower;
	private final boolean	point;
	private final LineType	type;

	private final Number	upper;

	/**
	 * Create a point interval. This is equivalent to
	 * {@link #OWLRealInterval(Number, Number, boolean, boolean)} with arguments
	 * <code>point,point,true,true</code>
	 * 
	 * @param point
	 *            Value of point interval
	 */
	public OWLRealInterval(Number point) {
		this.lower = point;
		this.upper = point;
		this.point = true;
		this.inclusiveLower = true;
		this.inclusiveUpper = true;
		this.type = LineType.CONTINUOUS;
		this.finite = true;
	}

	/**
	 * Create an interval. <code>null</code> should be used to indicate unbound
	 * (i.e., infinite intervals).
	 * 
	 * @param lower
	 *            Interval lower bound
	 * @param upper
	 *            Interval upper bound
	 * @param inclusiveLower
	 *            <code>true</code> if lower bound is inclusive,
	 *            <code>false</code> for exclusive. Ignored if
	 *            <code>lower == null</code>.
	 * @param inclusiveUpper
	 *            <code>true</code> if upper bound is inclusive,
	 *            <code>false</code> for exclusive. Ignored if
	 *            <code>upper == null</code>.
	 */
	public OWLRealInterval(Number lower, Number upper, boolean inclusiveLower,
			boolean inclusiveUpper, LineType type) {
		if( lower != null && upper != null ) {
			final int cmp = OWLRealUtils.compare( lower, upper );
			if( cmp > 0 ) {
				final String msg = format(
						"Lower bound of interval (%s) should not be greater than upper bound of interval (%s)",
						lower, upper );
				log.severe( msg );
				throw new IllegalArgumentException( msg );
			}
			else if( cmp == 0 ) {
				if( (!inclusiveLower || !inclusiveUpper) ) {
					final String msg = "Point intervals must be inclusive";
					log.severe( msg );
					throw new IllegalArgumentException( msg );
				}
				type = LineType.CONTINUOUS;
			}
		}

		this.type = type;
		if( LineType.INTEGER_ONLY.equals( type ) ) {
			if( lower == null ) {
				this.lower = null;
				this.inclusiveLower = false;
			}
			else {
				if( inclusiveLower ) {
					if( isInteger( lower ) )
						this.lower = lower;
					else
						this.lower = roundDown( lower );
				}
				else {
					if( isInteger( lower ) )
						this.lower = integerIncrement( lower );
					else
						this.lower = roundDown( lower );
				}
				this.inclusiveLower = true;
			}

			if( upper == null ) {
				this.upper = null;
				this.inclusiveUpper = false;
			}
			else {
				if( inclusiveUpper ) {
					if( isInteger( upper ) )
						this.upper = upper;
					else
						this.upper = roundDown( upper );
				}
				else {
					if( isInteger( upper ) )
						this.upper = integerDecrement( upper );
					else
						this.upper = roundDown( upper );
				}
				this.inclusiveUpper = true;
			}

		}
		else if( LineType.INTEGER_EXCLUDED.equals( type ) ) {
			if( lower == null ) {
				this.lower = null;
				this.inclusiveLower = false;
			}
			else {
				this.lower = lower;
				if( inclusiveLower ) {
					if( isInteger( lower ) )
						this.inclusiveLower = false;
					else
						this.inclusiveLower = true;
				}
				else
					this.inclusiveLower = false;
			}

			if( upper == null ) {
				this.upper = null;
				this.inclusiveUpper = false;
			}
			else {
				this.upper = upper;
				if( inclusiveUpper ) {
					if( isInteger( upper ) )
						this.inclusiveUpper = false;
					else
						this.inclusiveUpper = true;
				}
				else
					this.inclusiveUpper = false;
			}
		}
		else {
			this.lower = lower;
			this.upper = upper;
			this.inclusiveLower = (lower == null)
				? false
				: inclusiveLower;
			this.inclusiveUpper = (upper == null)
				? false
				: inclusiveUpper;
		}

		this.point = (lower != null && upper != null && lower.equals( upper ));

		this.finite = this.point
				|| ((LineType.INTEGER_ONLY.equals( type ) && (lower != null) && (upper != null)));
	}

	public boolean boundLower() {
		return (lower != null);
	}

	public boolean boundUpper() {
		return (upper != null);
	}

	public IntervalRelations compare(OWLRealInterval other) {
		return compare( this, other );
	}

	public boolean contains(Number n) {

		if( type.equals( LineType.INTEGER_ONLY ) ) {
			if( !isInteger( n ) )
				return false;
		}
		else if( type.equals( LineType.INTEGER_EXCLUDED ) ) {
			if( isInteger( n ) )
				return false;
		}

		int comp;
		if( boundLower() ) {
			comp = OWLRealUtils.compare( getLower(), n );
			if( comp > 0 )
				return false;
			if( (comp == 0) && !inclusiveLower() )
				return false;
		}

		if( boundUpper() ) {
			comp = OWLRealUtils.compare( getUpper(), n );
			if( comp < 0 )
				return false;
			if( (comp == 0) && !inclusiveUpper() )
				return false;
		}

		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if( this == obj )
			return true;
		if( obj == null )
			return false;
		if( getClass() != obj.getClass() )
			return false;
		OWLRealInterval other = (OWLRealInterval) obj;
		if( inclusiveLower != other.inclusiveLower )
			return false;
		if( inclusiveUpper != other.inclusiveUpper )
			return false;
		if( lower == null ) {
			if( other.lower != null )
				return false;
		}
		else if( OWLRealUtils.compare( lower, other.lower ) != 0 )
			return false;
		if( type == null ) {
			if( other.type != null )
				return false;
		}
		else if( !type.equals( other.type ) )
			return false;
		if( upper == null ) {
			if( other.upper != null )
				return false;
		}
		else if( OWLRealUtils.compare( upper, other.upper ) != 0 )
			return false;
		return true;
	}

	public Number getLower() {
		return lower;
	}

	public LineType getType() {
		return type;
	}

	public Number getUpper() {
		return upper;
	}

	/**
	 * Get the subinterval greater than n
	 * 
	 * @param n
	 * @return a new interval, formed by intersecting this interval with
	 *         (n,+inf) or <code>null</code> if that intersection is empty
	 */
	public OWLRealInterval greater(Number n) {
		if( boundLower() && OWLRealUtils.compare( n, getLower() ) < 0 )
			return this;
		else if( boundUpper() && OWLRealUtils.compare( n, getUpper() ) >= 0 )
			return null;
		return new OWLRealInterval( n, getUpper(), false, inclusiveUpper(), getType() );
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (inclusiveLower
			? 1231
			: 1237);
		result = prime * result + (inclusiveUpper
			? 1231
			: 1237);
		result = prime * result + ((lower == null)
			? 0
			: lower.hashCode());
		result = prime * result + ((type == null)
			? 0
			: type.hashCode());
		result = prime * result + ((upper == null)
			? 0
			: upper.hashCode());
		return result;
	}

	public boolean inclusiveLower() {
		return inclusiveLower;
	}

	public boolean inclusiveUpper() {
		return inclusiveUpper;
	}

	public OWLRealInterval intersection(OWLRealInterval that) {
		Number lower, upper;
		boolean inclusiveUpper, inclusiveLower;

		LineType intersectionType = this.type.intersect( that.type );
		if( intersectionType == null )
			return null;

		switch ( compare( that ) ) {

		case CONTAINS:
		case STARTED_BY:

			if( intersectionType.equals( that.type ) )
				return that;

			lower = that.getLower();
			inclusiveLower = that.inclusiveLower();
			upper = that.getUpper();
			inclusiveUpper = that.inclusiveUpper();
			break;

		case EQUALS:
			if( intersectionType.equals( this.type ) )
				return this;
			if( intersectionType.equals( this.type ) )
				return that;

			lower = this.getLower();
			inclusiveLower = this.inclusiveLower();
			upper = this.getUpper();
			inclusiveUpper = this.inclusiveUpper();
			break;

		case DURING:
		case STARTS:

			if( intersectionType.equals( this.type ) )
				return this;

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

		/*
		 * If intersection is integer only verify that it is non-empty after
		 * adjusting endpoints to appropriate (inclusive) integer values
		 */
		if( LineType.INTEGER_ONLY.equals( intersectionType ) ) {
			boolean change = false;

			if( lower != null ) {
				if( OWLRealUtils.isInteger( lower ) ) {
					if( !inclusiveLower ) {
						lower = OWLRealUtils.integerIncrement( lower );
						inclusiveLower = true;
						change = true;
					}
				}
				else {
					lower = OWLRealUtils.roundDown( lower );
					inclusiveLower = true;
					change = true;
				}
			}

			if( upper != null ) {
				if( OWLRealUtils.isInteger( upper ) ) {
					if( !inclusiveUpper ) {
						upper = OWLRealUtils.integerDecrement( upper );
						inclusiveUpper = true;
						change = true;
					}
				}
				else {
					upper = OWLRealUtils.roundDown( upper );
					inclusiveUpper = true;
					change = true;
				}
			}

			if( change && lower != null && upper != null
					&& OWLRealUtils.compare( lower, upper ) > 0 )
				return null;

		}
		/*
		 * If intersection is integer excluded verify that it is not an integer
		 * point
		 */
		else if( LineType.INTEGER_EXCLUDED.equals( intersectionType ) ) {
			if( lower != null && upper != null && lower.equals( upper )
					&& OWLRealUtils.isInteger( lower ) )
				return null;
		}

		return new OWLRealInterval( lower, upper, inclusiveLower, inclusiveUpper, intersectionType );
	}

	public boolean isFinite() {
		return finite;
	}

	public boolean isPoint() {
		return point;
	}

	/**
	 * Get the subinterval less than n
	 * 
	 * @param n
	 * @return a new interval, formed by intersecting this interval with
	 *         (-inf,n) or <code>null</code> if that intersection is empty
	 */
	public OWLRealInterval less(Number n) {
		if( boundUpper() && OWLRealUtils.compare( n, getUpper() ) > 0 )
			return this;
		else if( boundLower() && OWLRealUtils.compare( n, getLower() ) <= 0 )
			return null;
		return new OWLRealInterval( getLower(), n, inclusiveLower(), false, getType() );
	}

	public List<OWLRealInterval> remove(OWLRealInterval other) {

		final LineType t1 = this.getType();
		final LineType t2 = other.getType();

		if( LineType.INTEGER_ONLY.equals( t1 ) ) {
			if( LineType.INTEGER_EXCLUDED.equals( t2 ) )
				return Collections.singletonList( this );
		}
		else if( LineType.INTEGER_EXCLUDED.equals( t1 ) ) {
			if( LineType.INTEGER_ONLY.equals( t2 ) )
				return Collections.singletonList( this );
		}

		OWLRealInterval before, during, after;
		switch ( compare( other ) ) {

		case CONTAINS:
			before = new OWLRealInterval( this.getLower(), other.getLower(), inclusiveLower(),
					!other.inclusiveLower(), t1 );
			if( t1.equals( t2 ) || LineType.CONTINUOUS.equals( t2 ) ) {
				during = null;
			}
			else {
				during = new OWLRealInterval( other.getLower(), other.getUpper(), false, false,
						LineType.INTEGER_EXCLUDED.equals( t2 )
							? LineType.INTEGER_ONLY
							: LineType.INTEGER_EXCLUDED );
			}
			after = new OWLRealInterval( other.getUpper(), this.getUpper(),
					!other.inclusiveUpper(), this.inclusiveUpper(), t1 );
			break;

		case DURING:
		case EQUALS:
		case FINISHES:
		case STARTS:
			before = null;
			if( t1.equals( t2 ) || LineType.CONTINUOUS.equals( t2 ) ) {
				during = null;
			}
			else {
				during = new OWLRealInterval( this.getLower(), this.getUpper(), false, false,
						LineType.INTEGER_EXCLUDED.equals( t2 )
							? LineType.INTEGER_ONLY
							: LineType.INTEGER_EXCLUDED );
			}
			after = null;
			break;

		case FINISHED_BY:
			before = new OWLRealInterval( this.getLower(), other.getLower(), this.inclusiveLower(),
					!other.inclusiveLower(), t1 );
			if( t1.equals( t2 ) || LineType.CONTINUOUS.equals( t2 ) ) {
				during = null;
			}
			else {
				during = new OWLRealInterval( other.getLower(), this.getUpper(), false, false,
						LineType.INTEGER_EXCLUDED.equals( t2 )
							? LineType.INTEGER_ONLY
							: LineType.INTEGER_EXCLUDED );
			}
			after = null;
			break;

		case MEETS:
			before = new OWLRealInterval( this.getLower(), this.getUpper(), this.inclusiveLower(),
					false, t1 );
			during = null;
			after = null;
			break;

		case MET_BY:
			before = null;
			during = null;
			after = new OWLRealInterval( this.getLower(), this.getUpper(), false, this
					.inclusiveUpper(), t1 );
			break;

		case OVERLAPPED_BY:
		case STARTED_BY:
			before = null;
			if( t1.equals( t2 ) || LineType.CONTINUOUS.equals( t2 ) ) {
				during = null;
			}
			else {
				during = new OWLRealInterval( this.getLower(), other.getUpper(), false, false,
						LineType.INTEGER_EXCLUDED.equals( t2 )
							? LineType.INTEGER_ONLY
							: LineType.INTEGER_EXCLUDED );
			}
			after = new OWLRealInterval( other.getUpper(), this.getUpper(),
					!other.inclusiveUpper(), this.inclusiveUpper(), t1 );
			break;

		case OVERLAPS:
			before = new OWLRealInterval( this.getLower(), other.getLower(), this.inclusiveLower(),
					!other.inclusiveLower(), t1 );
			if( t1.equals( t2 ) || LineType.CONTINUOUS.equals( t2 ) ) {
				during = null;
			}
			else {
				during = new OWLRealInterval( other.getLower(), this.getUpper(), false, false,
						LineType.INTEGER_EXCLUDED.equals( t2 )
							? LineType.INTEGER_ONLY
							: LineType.INTEGER_EXCLUDED );
			}
			after = null;
			break;

		case PRECEDED_BY:
		case PRECEDES:
			return Collections.singletonList( this );

		default:
			throw new IllegalStateException();
		}

		List<OWLRealInterval> ret = new ArrayList<OWLRealInterval>();
		if( before != null )
			ret.add( before );
		if( during != null )
			ret.add( during );
		if( after != null )
			ret.add( after );

		return ret;
	}

	public Number size() {
		if( !finite )
			throw new IllegalStateException();
		else {
			if( point )
				return 1;
			else
				return integerIncrement( integerDifference( upper, lower ) );
		}
	}

	@Override
	public String toString() {
		return format( "%s%s,%s%s%s", inclusiveLower()
			? "["
			: "(", boundLower()
			? getLower()
			: "-Inf", boundUpper()
			? getUpper()
			: "+Inf", inclusiveUpper()
			? "]"
			: ")", type.equals( LineType.CONTINUOUS )
			? ""
			: type.equals( LineType.INTEGER_ONLY )
				? "{int}"
				: "{noint}" );
	}

	public List<OWLRealInterval> union(OWLRealInterval other) {
		final LineType t1 = this.getType();
		final LineType t2 = other.getType();

		OWLRealInterval before, during, after;
		switch ( compare( other ) ) {
		case CONTAINS:
			if( LineType.CONTINUOUS.equals( t1 ) || t1.equals( t2 ) )
				return Collections.singletonList( this );

			before = new OWLRealInterval( getLower(), other.getLower(), inclusiveLower(), !other
					.inclusiveLower(), t1 );
			if( LineType.CONTINUOUS.equals( t2 ) )
				during = other;
			else
				during = new OWLRealInterval( other.getLower(), other.getUpper(), other
						.inclusiveLower(), other.inclusiveUpper(), LineType.CONTINUOUS );
			after = new OWLRealInterval( other.getUpper(), getUpper(), !other.inclusiveUpper(),
					inclusiveUpper(), t1 );
			break;

		case DURING:
			if( LineType.CONTINUOUS.equals( t2 ) || t1.equals( t2 ) )
				return Collections.singletonList( other );

			before = new OWLRealInterval( other.getLower(), getLower(), other.inclusiveLower(),
					!inclusiveLower(), t2 );
			if( LineType.CONTINUOUS.equals( t1 ) )
				during = this;
			else
				during = new OWLRealInterval( getLower(), getUpper(), inclusiveLower(),
						inclusiveUpper(), LineType.CONTINUOUS );
			after = new OWLRealInterval( getUpper(), other.getUpper(), !inclusiveUpper(), other
					.inclusiveUpper(), t2 );
			break;

		case EQUALS:
			if( LineType.CONTINUOUS.equals( t1 ) || t1.equals( t2 ) )
				return Collections.singletonList( this );
			if( LineType.CONTINUOUS.equals( t2 ) )
				return Collections.singletonList( other );

			before = null;
			during = new OWLRealInterval( getLower(), getUpper(), inclusiveLower(),
					inclusiveUpper(), LineType.CONTINUOUS );
			after = null;
			break;

		case FINISHED_BY:
			if( LineType.CONTINUOUS.equals( t1 ) || t1.equals( t2 ) )
				return Collections.singletonList( this );

			before = new OWLRealInterval( getLower(), other.getLower(), inclusiveLower(), !other
					.inclusiveLower(), t1 );
			if( LineType.CONTINUOUS.equals( t2 ) )
				during = other;
			else
				during = new OWLRealInterval( other.getLower(), getUpper(), other.inclusiveLower(),
						inclusiveUpper(), LineType.CONTINUOUS );
			after = null;
			break;

		case FINISHES:
			if( LineType.CONTINUOUS.equals( t2 ) || t1.equals( t2 ) )
				return Collections.singletonList( other );

			before = new OWLRealInterval( other.getLower(), getLower(), other.inclusiveLower(),
					!inclusiveLower(), t2 );
			if( LineType.CONTINUOUS.equals( t1 ) )
				during = this;
			else
				during = new OWLRealInterval( getLower(), getUpper(), inclusiveLower(),
						inclusiveUpper(), LineType.CONTINUOUS );
			after = null;
			break;

		case MEETS:
			if( t1.equals( t2 ) )
				return Collections.singletonList( new OWLRealInterval( getLower(),
						other.getUpper(), inclusiveLower(), other.inclusiveUpper(), t1 ) );
			return Arrays.asList( this, other );

		case MET_BY:
			if( t1.equals( t2 ) )
				return Collections.singletonList( new OWLRealInterval( other.getLower(),
						getUpper(), other.inclusiveLower(), inclusiveUpper(), t1 ) );
			return Arrays.asList( other, this );

		case OVERLAPPED_BY:
			if( t1.equals( t2 ) )
				return Collections.singletonList( new OWLRealInterval( other.getLower(),
						getUpper(), other.inclusiveLower(), inclusiveUpper(), t1 ) );

			if( LineType.CONTINUOUS.equals( t2 ) ) {
				before = other;
				during = null;
				after = new OWLRealInterval( other.getUpper(), getUpper(), !other.inclusiveUpper(),
						inclusiveUpper(), t1 );
			}
			else if( LineType.CONTINUOUS.equals( t1 ) ) {
				before = new OWLRealInterval( other.getLower(), getLower(), other.inclusiveLower(),
						!inclusiveLower(), t2 );
				during = null;
				after = this;
			}
			else {
				before = new OWLRealInterval( other.getLower(), getLower(), other.inclusiveLower(),
						!inclusiveLower(), t2 );
				during = new OWLRealInterval( getLower(), other.getUpper(), inclusiveLower(), other
						.inclusiveUpper(), LineType.CONTINUOUS );
				after = new OWLRealInterval( other.getUpper(), getUpper(), !other.inclusiveUpper(),
						inclusiveUpper(), t1 );
			}
			break;

		case OVERLAPS:
			if( t1.equals( t2 ) )
				return Collections.singletonList( new OWLRealInterval( getLower(),
						other.getUpper(), inclusiveLower(), other.inclusiveUpper(), t1 ) );

			if( LineType.CONTINUOUS.equals( t1 ) ) {
				before = this;
				during = null;
				after = new OWLRealInterval( getUpper(), other.getUpper(), !inclusiveUpper(), other
						.inclusiveUpper(), t2 );
			}
			else if( LineType.CONTINUOUS.equals( t2 ) ) {
				before = new OWLRealInterval( getLower(), other.getLower(), inclusiveLower(),
						!other.inclusiveLower(), t1 );
				during = null;
				after = other;
			}
			else {
				before = new OWLRealInterval( getLower(), other.getLower(), inclusiveLower(),
						!other.inclusiveLower(), t1 );
				during = new OWLRealInterval( other.getLower(), getUpper(), other.inclusiveLower(),
						inclusiveUpper(), LineType.CONTINUOUS );
				after = new OWLRealInterval( getUpper(), other.getUpper(), !inclusiveUpper(), other
						.inclusiveUpper(), t2 );
			}
			break;

		case PRECEDED_BY:
		case PRECEDES:
			return Arrays.asList( this, other );

		case STARTED_BY:
		case STARTS:
		default:
			throw new IllegalStateException();
		}

		List<OWLRealInterval> ret = new ArrayList<OWLRealInterval>();
		if( before != null )
			ret.add( before );
		if( during != null )
			ret.add( during );
		if( after != null )
			ret.add( after );

		return ret;
	}

	public Iterator<Number> valueIterator() {
		if( isPoint() ) {
			return Collections.singletonList( getUpper() ).iterator();
		}
		else if( LineType.INTEGER_ONLY.equals( getType() ) ) {
			Number start, finish;
			boolean increment;
			if( boundLower() ) {
				start = getLower();
				increment = true;
				finish = boundUpper()
					? getUpper()
					: null;
			}
			else if( boundUpper() ) {
				start = getUpper();
				increment = false;
				finish = null;
			}
			else {
				start = Byte.valueOf( (byte) 0 );
				increment = true;
				finish = null;
			}
			return new IntegerIterator( start, finish, increment );
		}
		else
			throw new IllegalStateException();

	}
}
