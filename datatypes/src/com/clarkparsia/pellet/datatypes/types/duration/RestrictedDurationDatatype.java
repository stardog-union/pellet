package com.clarkparsia.pellet.datatypes.types.duration;

import java.util.Collection;
import java.util.Iterator;

import javax.xml.datatype.Duration;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.RestrictedDatatype;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidConstrainingFacetException;

/**
 * <p>
 * Title: Restricted Duration Datatype
 * </p>
 * <p>
 * Description: A subset of the value space of xsd:duration
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class RestrictedDurationDatatype implements RestrictedDatatype<Duration> {
	private final Datatype<Duration>	dt;

	public RestrictedDurationDatatype(Datatype<Duration> dt) {
		this.dt = dt;
	}

	public RestrictedDatatype<Duration> applyConstrainingFacet(ATermAppl facet, Object value)
			throws InvalidConstrainingFacetException {
		// TODO: support facets
		throw new UnsupportedOperationException();
	}

	public boolean contains(Object value) {
		if( value instanceof Duration ) {
			return true;
		}
		return false;
	}

	public boolean containsAtLeast(int n) {
		return true;
	}

	public RestrictedDatatype<Duration> exclude(Collection<?> values) {
		// TODO:
		throw new UnsupportedOperationException();
	}

	public Datatype<? extends Duration> getDatatype() {
		return dt;
	}

	public Duration getValue(int i) {
		throw new UnsupportedOperationException();
	}

	public RestrictedDatatype<Duration> intersect(RestrictedDatatype<?> other, boolean negated) {
		if( other instanceof RestrictedDurationDatatype ) {
			return this;
		}
		else
			throw new IllegalArgumentException();
	}

	public boolean isEmpty() {
		return false;
	}

	public boolean isEnumerable() {
		return false;
	}

	public boolean isFinite() {
		return false;
	}

	public int size() {
		throw new IllegalStateException();
	}

	public RestrictedDatatype<Duration> union(RestrictedDatatype<?> other) {
		if( other instanceof RestrictedDurationDatatype ) {
			return this;
		}
		else
			throw new IllegalArgumentException();
	}

	public Iterator<Duration> valueIterator() {
		throw new IllegalStateException();
	}

}
