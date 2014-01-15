package com.clarkparsia.pellet.datatypes.types.real;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.clarkparsia.pellet.datatypes.IntervalRelations;
import com.clarkparsia.pellet.datatypes.OWLRealUtils;

/**
 * <p>
 * Title: <code>owl:real</code> Interval
 * </p>
 * <p>
 * Description: An immutable interval representation supporting continuous
 * (decimal and rational) number lines in <code>owl:real</code> value space.
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
public class ContinuousRealInterval {

	private static final Logger				log;

	private static ContinuousRealInterval	unconstrained;

	static {
		log = Logger.getLogger( ContinuousRealInterval.class.getCanonicalName() );
	}

	static {
		unconstrained = new ContinuousRealInterval( null, null, true, true );
	}

	public static ContinuousRealInterval allReals() {
		return unconstrained;
	}

	private static IntervalRelations compare(ContinuousRealInterval a, ContinuousRealInterval b) {
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

	private static int compareLowerLower(ContinuousRealInterval a, ContinuousRealInterval other) {
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

	private static int compareUpperLower(ContinuousRealInterval a, ContinuousRealInterval b) {
		int ul;
		if( !a.boundUpper() )
			ul = 1;
		else if( !b.boundLower() )
			ul = 1;
		else
			ul = OWLRealUtils.compare( a.getUpper(), b.getLower() );
		return ul;
	}

	private static int compareUpperUpper(ContinuousRealInterval a, ContinuousRealInterval b) {
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

	private final boolean	inclusiveLower;
	private final boolean	inclusiveUpper;
	private final Number	lower;
	private final boolean	point;
	private final Number	upper;

	/**
	 * Create a point interval. This is equivalent to
	 * {@link #OWLRealInterval(Number, Number, boolean, boolean)} with arguments
	 * <code>point,point,true,true</code>
	 * 
	 * @param point
	 *            Value of point interval
	 */
	public ContinuousRealInterval(Number point) {
		this.lower = point;
		this.upper = point;
		this.point = true;
		this.inclusiveLower = true;
		this.inclusiveUpper = true;
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
	public ContinuousRealInterval(Number lower, Number upper, boolean inclusiveLower,
			boolean inclusiveUpper) {
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
			}
		}

		this.lower = lower;
		this.upper = upper;
		this.inclusiveLower = (lower == null)
			? false
			: inclusiveLower;
		this.inclusiveUpper = (upper == null)
			? false
			: inclusiveUpper;

		this.point = (lower != null && upper != null && lower.equals( upper ));
	}

	public boolean boundLower() {
		return (lower != null);
	}

	public boolean boundUpper() {
		return (upper != null);
	}

	public boolean canUnionWith(ContinuousRealInterval other) {
		return EnumSet.complementOf(
				EnumSet.of( IntervalRelations.PRECEDES, IntervalRelations.PRECEDED_BY ) ).contains(
				compare( other ) );
	}

	public IntervalRelations compare(ContinuousRealInterval other) {
		return compare( this, other );
	}

	public boolean contains(Number n) {

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
		ContinuousRealInterval other = (ContinuousRealInterval) obj;
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
	public ContinuousRealInterval greater(Number n) {
		if( boundLower() && OWLRealUtils.compare( n, getLower() ) < 0 )
			return this;
		else if( boundUpper() && OWLRealUtils.compare( n, getUpper() ) >= 0 )
			return null;
		return new ContinuousRealInterval( n, getUpper(), false, inclusiveUpper() );
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

	public ContinuousRealInterval intersection(ContinuousRealInterval that) {
		Number lower, upper;
		boolean inclusiveUpper, inclusiveLower;

		switch ( compare( that ) ) {

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

		return new ContinuousRealInterval( lower, upper, inclusiveLower, inclusiveUpper );
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
	public ContinuousRealInterval less(Number n) {
		if( boundUpper() && OWLRealUtils.compare( n, getUpper() ) > 0 )
			return this;
		else if( boundLower() && OWLRealUtils.compare( n, getLower() ) <= 0 )
			return null;
		return new ContinuousRealInterval( getLower(), n, inclusiveLower(), false );
	}

	public List<ContinuousRealInterval> remove(ContinuousRealInterval other) {

		ContinuousRealInterval before, after;
		switch ( compare( other ) ) {

		case CONTAINS:
			before = new ContinuousRealInterval( this.getLower(), other.getLower(),
					inclusiveLower(), !other.inclusiveLower() );
			after = new ContinuousRealInterval( other.getUpper(), this.getUpper(), !other
					.inclusiveUpper(), this.inclusiveUpper() );
			break;

		case DURING:
		case EQUALS:
		case FINISHES:
		case STARTS:
			return Collections.emptyList();

		case MEETS:
			before = new ContinuousRealInterval( this.getLower(), this.getUpper(), this
					.inclusiveLower(), false );
			after = null;
			break;

		case MET_BY:
			before = null;
			after = new ContinuousRealInterval( this.getLower(), this.getUpper(), false, this
					.inclusiveUpper() );
			break;

		case OVERLAPPED_BY:
		case STARTED_BY:
			before = null;
			after = new ContinuousRealInterval( other.getUpper(), this.getUpper(), !other
					.inclusiveUpper(), this.inclusiveUpper() );
			break;

		case OVERLAPS:
		case FINISHED_BY:
			before = new ContinuousRealInterval( this.getLower(), other.getLower(), this
					.inclusiveLower(), !other.inclusiveLower() );
			after = null;
			break;

		case PRECEDED_BY:
		case PRECEDES:
			return Collections.singletonList( this );

		default:
			throw new IllegalStateException();
		}

		List<ContinuousRealInterval> ret = new ArrayList<ContinuousRealInterval>();
		if( before != null )
			ret.add( before );
		if( after != null )
			ret.add( after );

		return ret;
	}

	public Number size() {
		if( !point )
			throw new IllegalStateException();
		else
			return 1;
	}

	@Override
	public String toString() {
		return format( "%s%s,%s%s", inclusiveLower()
			? "["
			: "(", boundLower()
			? getLower()
			: "-Inf", boundUpper()
			? getUpper()
			: "+Inf", inclusiveUpper()
			? "]"
			: ")" );
	}

	public List<ContinuousRealInterval> union(ContinuousRealInterval other) {

		switch ( compare( other ) ) {
		case CONTAINS:
		case EQUALS:
		case FINISHED_BY:
		case STARTED_BY:
			return Collections.singletonList( this );

		case DURING:
		case FINISHES:
		case STARTS:
			return Collections.singletonList( other );

		case MEETS:
			return Collections.singletonList( new ContinuousRealInterval( getLower(), other
					.getUpper(), inclusiveLower(), other.inclusiveUpper() ) );

		case MET_BY:
			return Collections.singletonList( new ContinuousRealInterval( other.getLower(),
					getUpper(), other.inclusiveLower(), inclusiveUpper() ) );

		case OVERLAPPED_BY:
			return Collections.singletonList( new ContinuousRealInterval( other.getLower(),
					getUpper(), other.inclusiveLower(), inclusiveUpper() ) );

		case OVERLAPS:
			return Collections.singletonList( new ContinuousRealInterval( getLower(), other
					.getUpper(), inclusiveLower(), other.inclusiveUpper() ) );

		case PRECEDED_BY:
		case PRECEDES:
			return Arrays.asList( this, other );

		default:
			throw new IllegalStateException();
		}
	}

	public Iterator<Number> valueIterator() {
		if( isPoint() )
			return Collections.singletonList( getUpper() ).iterator();
		else
			throw new IllegalStateException();

	}
}
