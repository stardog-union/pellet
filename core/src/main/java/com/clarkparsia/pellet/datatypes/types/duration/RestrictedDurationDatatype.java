package com.clarkparsia.pellet.datatypes.types.duration;

import aterm.ATermAppl;
import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.RestrictedDatatype;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidConstrainingFacetException;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.datatype.Duration;

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
public class RestrictedDurationDatatype implements RestrictedDatatype<Duration>
{
	private final Datatype<Duration> _dt;

	public RestrictedDurationDatatype(final Datatype<Duration> dt)
	{
		this._dt = dt;
	}

	@Override
	public RestrictedDatatype<Duration> applyConstrainingFacet(final ATermAppl facet, final Object value) throws InvalidConstrainingFacetException
	{
		// TODO: support facets
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(final Object value)
	{
		if (value instanceof Duration)
			return true;
		return false;
	}

	@Override
	public boolean containsAtLeast(final int n)
	{
		return true;
	}

	@Override
	public RestrictedDatatype<Duration> exclude(final Collection<?> values)
	{
		// TODO:
		throw new UnsupportedOperationException();
	}

	@Override
	public Datatype<? extends Duration> getDatatype()
	{
		return _dt;
	}

	@Override
	public RestrictedDatatype<Duration> intersect(final RestrictedDatatype<?> other, final boolean negated)
	{
		if (other instanceof RestrictedDurationDatatype)
			return this;
		else
			throw new IllegalArgumentException();
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}

	@Override
	public boolean isEnumerable()
	{
		return false;
	}

	@Override
	public boolean isFinite()
	{
		return false;
	}

	@Override
	public RestrictedDatatype<Duration> union(final RestrictedDatatype<?> other)
	{
		if (other instanceof RestrictedDurationDatatype)
			return this;
		else
			throw new IllegalArgumentException();
	}

	@Override
	public Iterator<Duration> valueIterator()
	{
		throw new IllegalStateException();
	}

}
