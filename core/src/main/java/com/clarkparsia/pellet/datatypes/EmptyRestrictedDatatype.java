package com.clarkparsia.pellet.datatypes;

import aterm.ATermAppl;
import java.util.Collection;

/**
 * <p>
 * Title: Empty Iterator
 * </p>
 * <p>
 * Description: Re-usable empty restricted _datatype implementation. Cannot be static so that parameterization is handled correctly.
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
public class EmptyRestrictedDatatype<T> extends EmptyDataRange<T> implements RestrictedDatatype<T>
{

	final private Datatype<? extends T> _datatype;

	public EmptyRestrictedDatatype(final Datatype<? extends T> datatype)
	{
		super();
		this._datatype = datatype;
	}

	@Override
	public RestrictedDatatype<T> applyConstrainingFacet(final ATermAppl facet, final Object value)
	{
		return this;
	}

	@Override
	public RestrictedDatatype<T> exclude(final Collection<?> values)
	{
		return this;
	}

	public void getConstrainingFacetValues(@SuppressWarnings("unused") final ATermAppl[] facets, @SuppressWarnings("unused") final Object[] values)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Datatype<? extends T> getDatatype()
	{
		return _datatype;
	}

	public boolean inFacetSpace(@SuppressWarnings("unused") final ATermAppl facet, @SuppressWarnings("unused") final Object value)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public RestrictedDatatype<T> intersect(final RestrictedDatatype<?> other, final boolean negated)
	{
		return this;
	}

	@Override
	public RestrictedDatatype<T> union(final RestrictedDatatype<?> other)
	{
		throw new UnsupportedOperationException();
	}

}
