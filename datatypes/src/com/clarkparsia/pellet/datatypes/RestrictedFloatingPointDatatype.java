package com.clarkparsia.pellet.datatypes;

import static java.lang.String.format;

import java.util.ArrayList;
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
 * Title: Restricted Float Datatype
 * </p>
 * <p>
 * Description: A subset of the value space of xsd:float.
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
public class RestrictedFloatingPointDatatype<T extends Number & Comparable<T>> implements
		RestrictedDatatype<T> {

	private final static Logger						log;

	static {
		log = Logger.getLogger( RestrictedFloatingPointDatatype.class.getCanonicalName() );
	}

	/*
	 * TODO: Evaluate storing intervals in a tree to improve the efficiency of
	 * #contains calls
	 */

	private final boolean							containsNaN;
	private final Datatype<? extends T>				datatype;
	private final RestrictedDatatype<T>				empty;
	private final List<FloatingPointInterval<T>>	intervals;
	private final FloatingPointType<T>				type;

	public RestrictedFloatingPointDatatype(Datatype<? extends T> datatype, FloatingPointType<T> type) {
		this.datatype = datatype;
		this.type = type;
		this.empty = new EmptyRestrictedDatatype<T>( datatype );
		this.intervals = Collections.singletonList( FloatingPointInterval.unconstrained( type ) );
		this.containsNaN = true;
	}

	private RestrictedFloatingPointDatatype(RestrictedFloatingPointDatatype<T> other,
			List<FloatingPointInterval<T>> intervals, boolean containsNaN) {
		this.datatype = other.datatype;
		this.type = other.type;
		this.empty = other.empty;
		this.intervals = Collections.unmodifiableList( intervals );
		this.containsNaN = containsNaN;
	}

	public RestrictedDatatype<T> applyConstrainingFacet(ATermAppl facet, Object value) {

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
		T n;
		if( type.isInstance( value ) )
			n = type.cast( value );
		else {
			final String msg = format(
					"Attempt to constrain datatype (%s) using constraining facet ('%s') with an unsupported value ('%s')",
					getDatatype(), f, value );
			log.severe( msg );
			throw new IllegalArgumentException( msg );
		}

		T lower, upper;
		if( EnumSet.of( XSD.MAX_EXCLUSIVE, XSD.MAX_INCLUSIVE, XSD.MIN_EXCLUSIVE, XSD.MIN_INCLUSIVE )
				.contains( f ) ) {

			if( type.isNaN( n ) )
				return empty;

			if( XSD.MAX_EXCLUSIVE.equals( f ) ) {
				lower = type.getNegativeInfinity();
				if( n.equals( type.getNegativeInfinity() ) )
					return empty;
				else
					upper = type.decrement( n );
			}
			else if( XSD.MAX_INCLUSIVE.equals( f ) ) {
				lower = type.getNegativeInfinity();
				upper = n;
			}
			else if( XSD.MIN_EXCLUSIVE.equals( f ) ) {
				if( n.equals( type.getPositiveInfinity() ) )
					return empty;
				else
					lower = n;
				upper = type.getPositiveInfinity();
			}
			else if( XSD.MIN_INCLUSIVE.equals( f ) ) {
				lower = n;
				upper = type.getPositiveInfinity();
			}
			else
				throw new IllegalStateException();
		}
		else
			throw new IllegalStateException();

		FloatingPointInterval<T> restriction = new FloatingPointInterval<T>( type, lower, upper );

		List<FloatingPointInterval<T>> revisedIntervals = new ArrayList<FloatingPointInterval<T>>();

		/*
		 * As described in XSD 1.1 part 2, NaN is incomparable with all float
		 * values, so if any facet is applied it is excluded from the value
		 * space.
		 */
		boolean changes = containsNaN;

		for( FloatingPointInterval<T> i : intervals ) {
			final FloatingPointInterval<T> j = i.intersection( restriction );
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
				return new RestrictedFloatingPointDatatype<T>( this, revisedIntervals, false );
		}
		else
			return this;
	}

	public boolean contains(Object value) {
		if( type.isInstance( value ) ) {
			final T n = type.cast( value );
			if( type.isNaN( n ) )
				return containsNaN;
			else {
				for( FloatingPointInterval<T> i : intervals ) {
					if( i.contains( n ) )
						return true;
				}
				return false;
			}
		}
		else
			return false;
	}

	public boolean containsAtLeast(int n) {
		if( n <= 0 )
			return true;

		Number sum = containsNaN
			? 1
			: 0;
		for( FloatingPointInterval<T> i : intervals ) {
			sum = OWLRealUtils.integerSum( sum, i.size() );
			if( OWLRealUtils.compare( sum, n ) >= 0 )
				return true;
		}

		return false;
	}

	public RestrictedDatatype<T> exclude(Collection<?> values) {
		boolean changes = false;
		List<FloatingPointInterval<T>> revisedIntervals = new ArrayList<FloatingPointInterval<T>>(
				intervals );

		for( Object o : values ) {
			if( type.isInstance( o ) ) {
				final T n = type.cast( o );
				for( Iterator<FloatingPointInterval<T>> it = revisedIntervals.iterator(); it
						.hasNext(); ) {
					final FloatingPointInterval<T> i = it.next();
					if( i.contains( n ) ) {

						changes = true;
						it.remove();

						final FloatingPointInterval<T> less = i.less( n );
						if( less != null )
							revisedIntervals.add( less );

						final FloatingPointInterval<T> greater = i.greater( n );
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
				return new RestrictedFloatingPointDatatype<T>( this, revisedIntervals, containsNaN );
		}
		else
			return this;
	}

	public Datatype<? extends T> getDatatype() {
		return datatype;
	}

	public T getValue(int i) {
		throw new UnsupportedOperationException();
	}

	public RestrictedDatatype<T> intersect(RestrictedDatatype<?> other, boolean negated) {

		if( other instanceof RestrictedFloatingPointDatatype<?> ) {
			if( !type.equals( ((RestrictedFloatingPointDatatype<?>) other).type ) )
				throw new IllegalArgumentException();
			@SuppressWarnings("unchecked")
			final RestrictedFloatingPointDatatype<T> otherRRD = (RestrictedFloatingPointDatatype) other;

			boolean changes = false;
			List<FloatingPointInterval<T>> revisedIntervals = new ArrayList<FloatingPointInterval<T>>();

			List<FloatingPointInterval<T>> intersectWith;
			if( negated ) {
				intersectWith = Collections.singletonList( FloatingPointInterval
						.unconstrained( type ) );
				for( FloatingPointInterval<T> i : otherRRD.intervals ) {
					List<FloatingPointInterval<T>> tmp = new ArrayList<FloatingPointInterval<T>>();
					for( FloatingPointInterval<T> j : intersectWith ) {
						tmp.addAll( j.remove( i ) );
					}
					intersectWith = tmp;
				}
			}
			else
				intersectWith = otherRRD.intervals;

			for( FloatingPointInterval<T> i : this.intervals ) {
				for( FloatingPointInterval<T> j : intersectWith ) {
					FloatingPointInterval<T> k = i.intersection( j );
					if( k != i ) {
						changes = true;
						if( k != null )
							revisedIntervals.add( k );
					}
				}
			}

			boolean toContainNaN;
			if( this.containsNaN ) {
				if( otherRRD.containsNaN ) {
					if( negated ) {
						changes = true;
						toContainNaN = false;
					}
					else {
						toContainNaN = true;
					}
				}
				else {
					if( negated ) {
						toContainNaN = true;
					}
					else {
						changes = true;
						toContainNaN = false;
					}
				}
			}
			else
				toContainNaN = false;

			if( changes ) {
				if( revisedIntervals.isEmpty() )
					return empty;
				else
					return new RestrictedFloatingPointDatatype<T>( this, revisedIntervals,
							toContainNaN );
			}
			else
				return this;

		}
		else
			throw new IllegalArgumentException();
	}

	public boolean isEmpty() {
		return false;
	}

	public boolean isEnumerable() {
		return true;
	}

	public boolean isFinite() {
		return true;
	}

	public int size() {
		long sum = containsNaN
			? 1
			: 0;
		for( FloatingPointInterval<T> i : intervals ) {
			sum += i.size().longValue();
			if( sum >= Integer.MAX_VALUE )
				return Integer.MAX_VALUE;
		}
		return (int) sum;
	}

	@Override
	public String toString() {
		return format( "{%s,%s}", datatype, intervals );
	}

	public RestrictedDatatype<T> union(RestrictedDatatype<?> other) {
		if( other instanceof RestrictedFloatingPointDatatype<?> ) {
			if( !type.equals( ((RestrictedFloatingPointDatatype<?>) other).type ) )
				throw new IllegalArgumentException();
			@SuppressWarnings("unchecked")
			final RestrictedFloatingPointDatatype<T> otherRRD = (RestrictedFloatingPointDatatype) other;

			List<FloatingPointInterval<T>> revisedIntervals = new ArrayList<FloatingPointInterval<T>>(
					this.intervals );
			for( FloatingPointInterval<T> i : otherRRD.intervals ) {
				List<FloatingPointInterval<T>> unionWith = new ArrayList<FloatingPointInterval<T>>();
				for( Iterator<FloatingPointInterval<T>> jt = revisedIntervals.iterator(); jt
						.hasNext(); ) {
					FloatingPointInterval<T> j = jt.next();
					if( i.canUnionWith( j ) ) {
						jt.remove();
						unionWith.add( j );
					}
				}
				if( unionWith.isEmpty() ) {
					revisedIntervals.add( i );
				}
				else {
					Set<FloatingPointInterval<T>> tmp = new HashSet<FloatingPointInterval<T>>();
					for( FloatingPointInterval<T> j : unionWith )
						tmp.addAll( i.union( j ) );
					revisedIntervals.addAll( tmp );
				}
			}
			final boolean toContainNaN = this.containsNaN || otherRRD.containsNaN;

			return new RestrictedFloatingPointDatatype<T>( this, revisedIntervals, toContainNaN );
		}
		else
			throw new IllegalArgumentException();
	}

	public Iterator<T> valueIterator() {
		/*
		 * This implementation avoids allocating the value iterators for the
		 * intervals until (and only if) they are needed. This is a
		 * micro-optimization relative to using
		 * org.mindswap.pellet.utils.iterator.MultiIterator
		 */
		return new Iterator<T>() {
			final Iterator<FloatingPointInterval<T>>	iit	= intervals.iterator();
			Iterator<T>									nit	= null;

			public boolean hasNext() {

				/*
				 * TODO: This implementation will never return NaN but should if
				 * containsNaN is true
				 */

				while( nit == null || !nit.hasNext() ) {
					if( iit.hasNext() )
						nit = iit.next().valueIterator();
					else
						return false;
				}

				return true;
			}

			public T next() {
				if( !hasNext() )
					throw new NoSuchElementException();

				return nit.next();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

}
