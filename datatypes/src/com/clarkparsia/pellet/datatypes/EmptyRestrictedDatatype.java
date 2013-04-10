package com.clarkparsia.pellet.datatypes;

import java.util.Collection;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Empty Iterator
 * </p>
 * <p>
 * Description: Re-usable empty restricted datatype implementation. Cannot be
 * static so that parameterization is handled correctly.
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
public class EmptyRestrictedDatatype<T> extends EmptyDataRange<T> implements RestrictedDatatype<T> {

	final private Datatype<? extends T>	datatype;

	public EmptyRestrictedDatatype(Datatype<? extends T> datatype) {
		super();
		this.datatype = datatype;
	}

	public RestrictedDatatype<T> applyConstrainingFacet(ATermAppl facet, Object value) {
		return this;
	}

	public RestrictedDatatype<T> exclude(Collection<?> values) {
		return this;
	}

	public void getConstrainingFacetValues(ATermAppl[] facets, Object[] values) {
		throw new UnsupportedOperationException();
	}

	public Datatype<? extends T> getDatatype() {
		return datatype;
	}

	public boolean inFacetSpace(ATermAppl facet, Object value) {
		throw new UnsupportedOperationException();
	}

	public RestrictedDatatype<T> intersect(RestrictedDatatype<?> other, boolean negated) {
		return this;
	}

	public RestrictedDatatype<T> union(RestrictedDatatype<?> other) {
		throw new UnsupportedOperationException();
	}

}
