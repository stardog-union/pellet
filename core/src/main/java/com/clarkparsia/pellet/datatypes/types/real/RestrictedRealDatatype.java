package com.clarkparsia.pellet.datatypes.types.real;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Logger;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.EmptyRestrictedDatatype;
import com.clarkparsia.pellet.datatypes.Facet;
import com.clarkparsia.pellet.datatypes.OWLRealUtils;
import com.clarkparsia.pellet.datatypes.RestrictedDatatype;
import com.clarkparsia.pellet.datatypes.Facet.XSD;

/**
 * <p>
 * Title: Restricted Real Datatype
 * </p>
 * <p>
 * Description: A subset of the value space of owl:real.
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
public class RestrictedRealDatatype implements RestrictedDatatype<Number> {

	private final static Logger					log;

	static {
		log = Logger.getLogger( RestrictedRealDatatype.class.getCanonicalName() );
	}

	/*
	 * TODO: Evaluate storing intervals in a tree to improve the efficiency of
	 * #contains calls
	 */

	private final Datatype<? extends Number>	datatype;
	private final RestrictedDatatype<Number>	empty;
	private final boolean						enumerable;
	private final boolean						finite;
	private final List<IntegerInterval>			intIntervals;
	private final List<ContinuousRealInterval>	decimalIntervals;
	private final List<ContinuousRealInterval>	rationalIntervals;

	public RestrictedRealDatatype(Datatype<? extends Number> datatype, IntegerInterval ints,
			ContinuousRealInterval decimals, ContinuousRealInterval rationals) {
		this.datatype = datatype;
		this.empty = new EmptyRestrictedDatatype<Number>( datatype );
		this.intIntervals = ints == null
			? Collections.<IntegerInterval> emptyList()
			: Collections.singletonList( ints );
		this.decimalIntervals = decimals == null
			? Collections.<ContinuousRealInterval> emptyList()
			: Collections.singletonList( decimals );
		this.rationalIntervals = rationals == null
			? Collections.<ContinuousRealInterval> emptyList()
			: Collections.singletonList( rationals );

		this.finite = ((ints == null)
			? true
			: ints.isFinite()) && ((decimals == null)
			? true
			: decimals.isPoint()) && ((rationals == null)
			? true
			: rationals.isPoint());
		this.enumerable = this.finite || (decimals == null && rationals == null);
	}

	private RestrictedRealDatatype(RestrictedRealDatatype other,
			List<IntegerInterval> intIntervals, List<ContinuousRealInterval> decimalIntervals,
			List<ContinuousRealInterval> rationalIntervals) {
		this.datatype = other.datatype;
		this.empty = other.empty;
		this.intIntervals = intIntervals;
		this.decimalIntervals = decimalIntervals;
		this.rationalIntervals = rationalIntervals;

		if( other.enumerable ) {
			this.enumerable = true;
		}
		else {
			boolean allEnumerable = true;
			for( List<ContinuousRealInterval> l : new List[] { decimalIntervals, rationalIntervals } ) {
				if( allEnumerable ) {
					for( ContinuousRealInterval i : l ) {
						if( !i.isPoint() ) {
							allEnumerable = false;
							break;
						}
					}
				}
			}
			this.enumerable = allEnumerable;
		}

		if( other.finite ) {
			this.finite = true;
		}
		else {
			if( this.enumerable ) {
				boolean allFinite = true;
				for( IntegerInterval i : intIntervals ) {
					if( !i.isFinite() ) {
						allFinite = false;
						break;
					}
				}
				this.finite = allFinite;
			}
			else {
				this.finite = false;
			}
		}
	}

	public RestrictedDatatype<Number> applyConstrainingFacet(ATermAppl facet, Object value) {

		/*
		 * FIXME throw correct exception type here
		 */

		/*
		 * Check the facet
		 */
		Facet f = Facet.Registry.get( facet );
		if( f == null ) {
			final String msg = format(
					"Attempt to constrain datatype (%s) with unsupported constraining facet ('%s' , '%s')",
					getDatatype(), facet, value );
			log.severe( msg );
			throw new IllegalArgumentException( msg );
		}

		/*
		 * Check the value
		 */
		Number n = null;
		if( value instanceof Number ) {
			n = (Number) value;
			if( !OWLRealUtils.acceptable( n.getClass() ) )
				n = null;
		}
		if( n == null ) {
			final String msg = format(
					"Attempt to constrain datatype (%s) using constraining facet ('%s') with an unsupported value ('%s')",
					getDatatype(), f, value );
			log.severe( msg );
			throw new IllegalArgumentException( msg );
		}

		Number lower, upper;
		boolean inclusiveLower, inclusiveUpper;
		if( XSD.MAX_EXCLUSIVE.equals( f ) ) {
			lower = null;
			inclusiveLower = false;
			upper = n;
			inclusiveUpper = false;
		}
		else if( XSD.MAX_INCLUSIVE.equals( f ) ) {
			lower = null;
			inclusiveLower = false;
			upper = n;
			inclusiveUpper = true;
		}
		else if( XSD.MIN_EXCLUSIVE.equals( f ) ) {
			lower = n;
			inclusiveLower = false;
			upper = null;
			inclusiveUpper = false;
		}
		else if( XSD.MIN_INCLUSIVE.equals( f ) ) {
			lower = n;
			inclusiveLower = true;
			upper = null;
			inclusiveUpper = false;
		}
		else
			throw new IllegalStateException();

		ContinuousRealInterval continuousRestriction = new ContinuousRealInterval( lower, upper,
				inclusiveLower, inclusiveUpper );
		IntegerInterval integerRestriction = asIntegerInterval( continuousRestriction );

		boolean changes = false;

		List<IntegerInterval> revisedInts = new ArrayList<IntegerInterval>();
		for( IntegerInterval i : intIntervals ) {
			final IntegerInterval j = i.intersection( integerRestriction );
			if( j != null ) {
				revisedInts.add( j );
				if( !i.equals( j ) )
					changes = true;
			}
			else
				changes = true;

		}

		List<ContinuousRealInterval> revisedDecimals = new ArrayList<ContinuousRealInterval>();
		for( ContinuousRealInterval i : decimalIntervals ) {
			final ContinuousRealInterval j = i.intersection( continuousRestriction );
			if( j != null ) {
				revisedDecimals.add( j );
				if( !i.equals( j ) )
					changes = true;
			}
			else
				changes = true;
		}

		List<ContinuousRealInterval> revisedRationals = new ArrayList<ContinuousRealInterval>();
		for( ContinuousRealInterval i : rationalIntervals ) {
			final ContinuousRealInterval j = i.intersection( continuousRestriction );
			if( j != null ) {
				revisedRationals.add( j );
				if( !i.equals( j ) )
					changes = true;
			}
			else
				changes = true;
		}

		if( changes ) {
			if( revisedInts.isEmpty() && revisedDecimals.isEmpty() && revisedRationals.isEmpty() )
				return empty;
			else
				return new RestrictedRealDatatype( this, revisedInts, revisedDecimals,
						revisedRationals );
		}
		else
			return this;
	}

	private static IntegerInterval asIntegerInterval(ContinuousRealInterval continuousRestriction) {
		Number lower, upper;

		if( continuousRestriction.boundLower() ) {
			final Number cl = continuousRestriction.getLower();
			if( OWLRealUtils.isInteger( cl ) ) {
				if( continuousRestriction.inclusiveLower() )
					lower = cl;
				else
					lower = OWLRealUtils.integerIncrement( cl );
			}
			else
				lower = OWLRealUtils.roundCeiling( cl );
		}
		else
			lower = null;

		if( continuousRestriction.boundUpper() ) {
			final Number cu = continuousRestriction.getUpper();
			if( OWLRealUtils.isInteger( cu ) ) {
				if( continuousRestriction.inclusiveUpper() )
					upper = cu;
				else
					upper = OWLRealUtils.integerDecrement( cu );
			}
			else
				upper = OWLRealUtils.roundFloor( cu );
		}
		else
			upper = null;

		if( lower != null && upper != null && OWLRealUtils.compare( lower, upper ) > 0 )
			return null;
		else
			return new IntegerInterval( lower, upper );
	}

	public boolean contains(Object value) {
		if( value instanceof Number ) {
			final Number n = (Number) value;
			if( OWLRealUtils.acceptable( n.getClass() ) ) {
				/*
				 * TODO: This could be made more efficient by looking at how
				 * each contained check fails (e.g., if intervals is sorted by
				 * boundaries and n is not contained, but less than upper, there
				 * is no need to look further).
				 */
				if( OWLRealUtils.isInteger( n ) ) {
					for( IntegerInterval i : intIntervals ) {
						if( i.contains( n ) )
							return true;
					}
				}
				else if( OWLRealUtils.isDecimal( n ) ) {
					for( ContinuousRealInterval i : decimalIntervals ) {
						if( i.contains( n ) )
							return true;
					}
				}
				else if( OWLRealUtils.isRational( n ) ) {
					for( ContinuousRealInterval i : rationalIntervals ) {
						if( i.contains( n ) )
							return true;
					}
				}
				return false;
			}
			else
				return false;
		}
		else
			return false;
	}

	public boolean containsAtLeast(int n) {
		if( !finite || n <= 0 )
			return true;

		Number sum = 0;
		for( IntegerInterval i : intIntervals ) {
			sum = OWLRealUtils.integerSum( sum, i.size() );
			if( OWLRealUtils.compare( n, sum ) <= 0 )
				return true;
		}
		for( ContinuousRealInterval i : decimalIntervals ) {
			if( !OWLRealUtils.isInteger( i.getLower() ) ) {
				sum = OWLRealUtils.integerIncrement( sum );
				if( OWLRealUtils.compare( n, sum ) <= 0 )
					return true;
			}
		}
		for( ContinuousRealInterval i : rationalIntervals ) {
			if( !OWLRealUtils.isInteger( i.getLower() ) && !OWLRealUtils.isRational( i.getLower() ) ) {
				sum = OWLRealUtils.integerIncrement( sum );
				if( OWLRealUtils.compare( n, sum ) <= 0 )
					return true;
			}
		}

		return false;
	}

	public RestrictedDatatype<Number> exclude(Collection<?> values) {

		boolean changes = false;

		List<IntegerInterval> revisedInts = new ArrayList<IntegerInterval>( intIntervals );
		List<ContinuousRealInterval> revisedDecimals = new ArrayList<ContinuousRealInterval>(
				decimalIntervals );
		List<ContinuousRealInterval> revisedRationals = new ArrayList<ContinuousRealInterval>(
				rationalIntervals );

		for( Object o : values ) {
			if( o instanceof Number ) {
				final Number n = (Number) o;
				if( OWLRealUtils.acceptable( n.getClass() ) ) {
					if( OWLRealUtils.isInteger( n ) ) {
						for( Iterator<IntegerInterval> it = revisedInts.iterator(); it.hasNext(); ) {
							final IntegerInterval i = it.next();
							if( i.contains( n ) ) {
								changes = true;
								it.remove();
								final IntegerInterval less = i.less( n );
								if( less != null )
									revisedInts.add( less );
								final IntegerInterval greater = i.greater( n );
								if( greater != null )
									revisedInts.add( greater );

								break;
							}
						}
					}
					else {
						List<ContinuousRealInterval> revised;
						if( OWLRealUtils.isDecimal( n ) )
							revised = revisedDecimals;
						else if( OWLRealUtils.isRational( n ) )
							revised = revisedRationals;
						else
							throw new IllegalStateException();

						for( Iterator<ContinuousRealInterval> it = revised.iterator(); it.hasNext(); ) {
							final ContinuousRealInterval i = it.next();
							if( i.contains( n ) ) {
								changes = true;
								it.remove();
								final ContinuousRealInterval less = i.less( n );
								if( less != null )
									revised.add( less );
								final ContinuousRealInterval greater = i.greater( n );
								if( greater != null )
									revised.add( greater );

								break;
							}
						}
					}
				}
			}
		}

		if( changes ) {
			if( revisedInts.isEmpty() && revisedDecimals.isEmpty() && revisedRationals.isEmpty() )
				return empty;
			else
				return new RestrictedRealDatatype( this, revisedInts, revisedDecimals,
						revisedRationals );
		}
		else
			return this;
	}

	public Datatype<? extends Number> getDatatype() {
		return datatype;
	}

	public Number getValue(int i) {
		throw new UnsupportedOperationException();
	}

	public RestrictedDatatype<Number> intersect(RestrictedDatatype<?> other, boolean negated) {

		if( other instanceof RestrictedRealDatatype ) {
			RestrictedRealDatatype otherRRD = (RestrictedRealDatatype) other;

			List<IntegerInterval> revisedInts = new ArrayList<IntegerInterval>();
			List<ContinuousRealInterval> revisedDecimals = new ArrayList<ContinuousRealInterval>();
			List<ContinuousRealInterval> revisedRationals = new ArrayList<ContinuousRealInterval>();

			/*
			 * Intersect the integer ranges
			 */
			List<IntegerInterval> intIntersectWith;
			if( negated ) {
				intIntersectWith = Collections.singletonList( IntegerInterval.allIntegers() );
				for( IntegerInterval i : otherRRD.intIntervals ) {
					List<IntegerInterval> tmp = new ArrayList<IntegerInterval>(
							2 * intIntersectWith.size() );
					for( IntegerInterval j : intIntersectWith )
						tmp.addAll( j.remove( i ) );
					intIntersectWith = tmp;
				}
			}
			else
				intIntersectWith = otherRRD.intIntervals;

			for( IntegerInterval i : this.intIntervals ) {
				for( IntegerInterval j : intIntersectWith ) {
					IntegerInterval k = i.intersection( j );
					if( k != null )
						revisedInts.add( k );
				}
			}

			/*
			 * Intersect the decimal ranges
			 */
			List<ContinuousRealInterval> decimalIntersectWith;
			if( negated ) {
				decimalIntersectWith = Collections
						.singletonList( ContinuousRealInterval.allReals() );
				for( ContinuousRealInterval i : otherRRD.decimalIntervals ) {
					List<ContinuousRealInterval> tmp = new ArrayList<ContinuousRealInterval>(
							2 * decimalIntersectWith.size() );
					for( ContinuousRealInterval j : decimalIntersectWith )
						tmp.addAll( j.remove( i ) );
					decimalIntersectWith = tmp;
				}
			}
			else
				decimalIntersectWith = otherRRD.decimalIntervals;

			for( ContinuousRealInterval i : this.decimalIntervals ) {
				for( ContinuousRealInterval j : decimalIntersectWith ) {
					ContinuousRealInterval k = i.intersection( j );
					if( k != null )
						revisedDecimals.add( k );
				}
			}

			/*
			 * Intersect the rational ranges
			 */
			List<ContinuousRealInterval> rationalIntersectWith;
			if( negated ) {
				rationalIntersectWith = Collections.singletonList( ContinuousRealInterval
						.allReals() );
				for( ContinuousRealInterval i : otherRRD.rationalIntervals ) {
					List<ContinuousRealInterval> tmp = new ArrayList<ContinuousRealInterval>(
							2 * rationalIntersectWith.size() );
					for( ContinuousRealInterval j : rationalIntersectWith )
						tmp.addAll( j.remove( i ) );
					rationalIntersectWith = tmp;
				}
			}
			else
				rationalIntersectWith = otherRRD.rationalIntervals;

			for( ContinuousRealInterval i : this.rationalIntervals ) {
				for( ContinuousRealInterval j : rationalIntersectWith ) {
					ContinuousRealInterval k = i.intersection( j );
					if( k != null )
						revisedRationals.add( k );
				}
			}

			if( revisedInts.equals( this.intIntervals )
					&& revisedDecimals.equals( this.decimalIntervals )
					&& revisedRationals.equals( this.rationalIntervals ) )
				return this;
			else if( revisedInts.isEmpty() && revisedDecimals.isEmpty()
					&& revisedRationals.isEmpty() )
				return empty;
			else
				return new RestrictedRealDatatype( this, revisedInts, revisedDecimals,
						revisedRationals );
		}
		else
			throw new IllegalArgumentException();
	}

	public boolean isEmpty() {
		return false;
	}

	public boolean isEnumerable() {
		return enumerable;
	}

	public boolean isFinite() {
		return finite;
	}

	public int size() {
		if( !finite )
			throw new IllegalStateException();

		Number sum = 0;
		for( IntegerInterval i : intIntervals ) {
			sum = OWLRealUtils.integerSum( sum, i.size() );
			if( OWLRealUtils.compare( Integer.MAX_VALUE, sum ) <= 0 )
				return Integer.MAX_VALUE;
		}
		for( ContinuousRealInterval i : decimalIntervals ) {
			if( !OWLRealUtils.isInteger( i.getLower() ) ) {
				sum = OWLRealUtils.integerIncrement( sum );
				if( OWLRealUtils.compare( Integer.MAX_VALUE, sum ) <= 0 )
					return Integer.MAX_VALUE;
			}
		}
		for( ContinuousRealInterval i : rationalIntervals ) {
			if( !OWLRealUtils.isInteger( i.getLower() ) && !OWLRealUtils.isRational( i.getLower() ) ) {
				sum = OWLRealUtils.integerIncrement( sum );
				if( OWLRealUtils.compare( Integer.MAX_VALUE, sum ) <= 0 )
					return Integer.MAX_VALUE;
			}
		}
		return sum.intValue();
	}

	public RestrictedDatatype<Number> union(RestrictedDatatype<?> other) {
		if( other instanceof RestrictedRealDatatype ) {
			RestrictedRealDatatype otherRRD = (RestrictedRealDatatype) other;

			List<IntegerInterval> revisedInts = new ArrayList<IntegerInterval>( this.intIntervals );
			List<ContinuousRealInterval> revisedDecimals = new ArrayList<ContinuousRealInterval>(
					this.decimalIntervals );
			List<ContinuousRealInterval> revisedRationals = new ArrayList<ContinuousRealInterval>(
					this.rationalIntervals );

			/*
			 * Union the integer intervals
			 */
			for( IntegerInterval i : otherRRD.intIntervals ) {
				List<IntegerInterval> unionWith = new ArrayList<IntegerInterval>();
				for( Iterator<IntegerInterval> jt = revisedInts.iterator(); jt.hasNext(); ) {
					final IntegerInterval j = jt.next();
					if( i.canUnionWith( j ) ) {
						jt.remove();
						unionWith.add( j );
					}
				}
				if( unionWith.isEmpty() )
					revisedInts.add( i );
				else {
					Set<IntegerInterval> tmp = new HashSet<IntegerInterval>();
					for( IntegerInterval j : unionWith )
						tmp.addAll( i.union( j ) );
					revisedInts.addAll( tmp );
				}
			}

			/*
			 * Union the decimal intervals
			 */
			for( ContinuousRealInterval i : otherRRD.decimalIntervals ) {
				List<ContinuousRealInterval> unionWith = new ArrayList<ContinuousRealInterval>();
				for( Iterator<ContinuousRealInterval> jt = revisedDecimals.iterator(); jt.hasNext(); ) {
					final ContinuousRealInterval j = jt.next();
					if( i.canUnionWith( j ) ) {
						jt.remove();
						unionWith.add( j );
					}
				}
				if( unionWith.isEmpty() )
					revisedDecimals.add( i );
				else {
					Set<ContinuousRealInterval> tmp = new HashSet<ContinuousRealInterval>();
					for( ContinuousRealInterval j : unionWith )
						tmp.addAll( i.union( j ) );
					revisedDecimals.addAll( tmp );
				}
			}

			/*
			 * Union the rational intervals
			 */
			for( ContinuousRealInterval i : otherRRD.rationalIntervals ) {
				List<ContinuousRealInterval> unionWith = new ArrayList<ContinuousRealInterval>();
				for( Iterator<ContinuousRealInterval> jt = revisedRationals.iterator(); jt
						.hasNext(); ) {
					final ContinuousRealInterval j = jt.next();
					if( i.canUnionWith( j ) ) {
						jt.remove();
						unionWith.add( j );
					}
				}
				if( unionWith.isEmpty() )
					revisedRationals.add( i );
				else {
					Set<ContinuousRealInterval> tmp = new HashSet<ContinuousRealInterval>();
					for( ContinuousRealInterval j : unionWith )
						tmp.addAll( i.union( j ) );
					revisedRationals.addAll( tmp );
				}
			}

			if( revisedInts.equals( this.intIntervals )
					&& revisedDecimals.equals( this.decimalIntervals )
					&& revisedRationals.equals( this.rationalIntervals ) )
				return this;
			else
				return new RestrictedRealDatatype( this, revisedInts, revisedDecimals,
						revisedRationals );
		}
		else
			throw new IllegalArgumentException();
	}

	public Iterator<Number> valueIterator() {
		if( !enumerable )
			throw new IllegalStateException();

		return new Iterator<Number>() {
			final Iterator<IntegerInterval>			intit	= intIntervals.iterator();
			final Iterator<ContinuousRealInterval>	decit	= decimalIntervals.iterator();
			final Iterator<ContinuousRealInterval>	ratit	= rationalIntervals.iterator();

			private Iterator<Number>				nit		= null;
			private boolean							intOk	= true;
			private boolean							decOk	= true;
			private Number							next	= null;

			public boolean hasNext() {

				while( next == null ) {
					if( nit != null && nit.hasNext() ) {
						next = nit.next();
						if( !intOk && OWLRealUtils.isInteger( next ) )
							next = null;
						if( !decOk && OWLRealUtils.isDecimal( next ) )
							next = null;
					}
					else {
						if( intit.hasNext() )
							nit = intit.next().valueIterator();
						else {
							intOk = false;
							if( decit.hasNext() )
								nit = decit.next().valueIterator();
							else {
								decOk = false;
								if( ratit.hasNext() )
									nit = ratit.next().valueIterator();
								else
									return false;
							}
						}
					}
				}
				return true;
			}

			public Number next() {
				if( !hasNext() )
					throw new NoSuchElementException();

				Number ret = next;
				next = null;
				return ret;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public String toString() {
		return format( "{%s,%s,%s,%s}", datatype, intIntervals, decimalIntervals, rationalIntervals );
	}

}
