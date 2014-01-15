package com.clarkparsia.pellet.datatypes;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Logger;

import aterm.ATermAppl;

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
	private final List<OWLRealInterval>			intervals;

	public RestrictedRealDatatype(Datatype<? extends Number> datatype, OWLRealInterval interval) {
		this.datatype = datatype;
		this.empty = new EmptyRestrictedDatatype<Number>( datatype );
		this.intervals = Collections.singletonList( interval );
		this.finite = interval.isFinite();
		this.enumerable = interval.isPoint()
				|| interval.getType().equals( OWLRealInterval.LineType.INTEGER_ONLY );
	}

	private RestrictedRealDatatype(RestrictedRealDatatype other, List<OWLRealInterval> intervals) {
		this.datatype = other.datatype;
		this.empty = other.empty;
		this.intervals = Collections.unmodifiableList( intervals );
		if( other.finite ) {
			this.finite = true;
		}
		else {
			boolean allFinite = true;
			for( OWLRealInterval i : intervals ) {
				if( !i.isFinite() ) {
					allFinite = false;
					break;
				}
			}
			this.finite = allFinite;
		}
		if( other.enumerable ) {
			this.enumerable = true;
		}
		else {
			boolean allEnumerable = true;
			for( OWLRealInterval i : intervals ) {
				if( !i.isPoint() && !i.getType().equals( OWLRealInterval.LineType.INTEGER_ONLY ) ) {
					allEnumerable = false;
					break;
				}
			}
			this.enumerable = allEnumerable;
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

		OWLRealInterval restriction = new OWLRealInterval( lower, upper, inclusiveLower,
				inclusiveUpper, OWLRealInterval.LineType.CONTINUOUS );

		List<OWLRealInterval> revisedIntervals = new ArrayList<OWLRealInterval>();
		boolean changes = false;

		for( OWLRealInterval i : intervals ) {
			final OWLRealInterval j = i.intersection( restriction );
			if( j != null ) {
				revisedIntervals.add( j );
				if( i != j )
					changes = true;
			}
			else
				changes = true;
		}

		if( changes ) {
			if( revisedIntervals.isEmpty() )
				return empty;
			else
				return new RestrictedRealDatatype( this, revisedIntervals );
		}
		else
			return this;
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
				for( OWLRealInterval i : intervals ) {
					if( i.contains( n ) )
						return true;
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
		for( OWLRealInterval i : intervals ) {
			sum = OWLRealUtils.integerSum( sum, i.size() );
			if( OWLRealUtils.compare( n, sum ) <= 0 )
				return true;
		}

		return false;
	}

	public RestrictedDatatype<Number> exclude(Collection<?> values) {
		boolean changes = false;
		List<OWLRealInterval> revisedIntervals = new ArrayList<OWLRealInterval>( intervals );

		for( Object o : values ) {
			if( o instanceof Number ) {
				final Number n = (Number) o;
				for( Iterator<OWLRealInterval> it = revisedIntervals.iterator(); it.hasNext(); ) {
					final OWLRealInterval i = it.next();
					if( i.contains( n ) ) {

						changes = true;
						it.remove();

						final OWLRealInterval less = i.less( n );
						if( less != null )
							revisedIntervals.add( less );

						final OWLRealInterval greater = i.greater( n );
						if( greater != null )
							revisedIntervals.add( greater );

						break;
					}
				}
			}
		}

		if( changes ) {
			if( revisedIntervals.isEmpty() )
				return empty;
			else
				return new RestrictedRealDatatype( this, revisedIntervals );
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

			List<OWLRealInterval> revisedIntervals = new ArrayList<OWLRealInterval>();

			List<OWLRealInterval> intersectWith;
			if( negated ) {
				intersectWith = new ArrayList<OWLRealInterval>( Arrays.asList( OWLRealInterval
						.allReals() ) );
				for( OWLRealInterval i : otherRRD.intervals ) {
					List<OWLRealInterval> tmp = new ArrayList<OWLRealInterval>();
					for( OWLRealInterval j : intersectWith ) {
						tmp.addAll( j.remove( i ) );
					}
					intersectWith = tmp;
				}
			}
			else
				intersectWith = otherRRD.intervals;

			for( OWLRealInterval i : this.intervals ) {
				for( OWLRealInterval j : intersectWith ) {
					OWLRealInterval k = i.intersection( j );
					if( k != null )
						revisedIntervals.add( k );
				}
			}

			if( revisedIntervals.equals( this.intervals ) )
				return this;
			else {
				if( revisedIntervals.isEmpty() )
					return empty;
				else
					return new RestrictedRealDatatype( this, revisedIntervals );
			}

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
		for( OWLRealInterval i : intervals ) {
			sum = OWLRealUtils.integerSum( sum, i.size() );
			if( OWLRealUtils.compare( Integer.MAX_VALUE, sum ) <= 0 )
				return Integer.MAX_VALUE;
		}
		return sum.intValue();
	}

	public RestrictedDatatype<Number> union(RestrictedDatatype<?> other) {
		if( other instanceof RestrictedRealDatatype ) {
			RestrictedRealDatatype otherRRD = (RestrictedRealDatatype) other;

			List<OWLRealInterval> revisedIntervals = new ArrayList<OWLRealInterval>( this.intervals );
			final EnumSet<IntervalRelations> connected = EnumSet.complementOf( EnumSet.of(
					IntervalRelations.PRECEDED_BY, IntervalRelations.PRECEDES ) );
			for( OWLRealInterval i : otherRRD.intervals ) {
				List<OWLRealInterval> unionWith = new ArrayList<OWLRealInterval>();
				for( Iterator<OWLRealInterval> jt = revisedIntervals.iterator(); jt.hasNext(); ) {
					OWLRealInterval j = jt.next();
					IntervalRelations rel = i.compare( j );
					if( connected.contains( rel ) ) {
						jt.remove();
						unionWith.add( j );
					}
				}
				if( unionWith.isEmpty() ) {
					revisedIntervals.add( i );
				}
				else {
					Set<OWLRealInterval> tmp = new HashSet<OWLRealInterval>();
					for( OWLRealInterval j : unionWith )
						tmp.addAll( i.union( j ) );
					revisedIntervals.addAll( tmp );
				}
			}

			return new RestrictedRealDatatype( this, revisedIntervals );
		}
		else
			throw new IllegalArgumentException();
	}

	public Iterator<Number> valueIterator() {
		if( !enumerable )
			throw new IllegalStateException();

		/*
		 * This implementation avoids allocating the value iterators for the
		 * intervals until (and only if) they are needed. This is a
		 * micro-optimization relative to using
		 * org.mindswap.pellet.utils.iterator.MultiIterator
		 */
		return new Iterator<Number>() {
			final Iterator<OWLRealInterval>	iit	= intervals.iterator();
			Iterator<Number>				nit	= null;

			public boolean hasNext() {

				while( nit == null || !nit.hasNext() ) {
					if( iit.hasNext() )
						nit = iit.next().valueIterator();
					else
						return false;
				}

				return true;
			}

			public Number next() {
				if( !hasNext() )
					throw new NoSuchElementException();

				return nit.next();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public String toString() {
		return format( "{%s,%s}", datatype, intervals );
	}

}
