package com.clarkparsia.pellet.datatypes.types.bool;

import aterm.ATermAppl;
import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.EmptyIterator;
import com.clarkparsia.pellet.datatypes.RestrictedDatatype;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidConstrainingFacetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * <p>
 * Title: Restricted Boolean Datatype
 * </p>
 * <p>
 * Description: A subset of the value space of xsd:boolean.
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

public class RestrictedBooleanDatatype implements RestrictedDatatype<Boolean>
{

	private final Datatype<Boolean> dt;
	private final boolean permitFalse;
	private final boolean permitTrue;

	public RestrictedBooleanDatatype(final Datatype<Boolean> dt)
	{
		this.dt = dt;
		permitTrue = true;
		permitFalse = true;
	}

	private RestrictedBooleanDatatype(final RestrictedBooleanDatatype other, final boolean permitTrue, final boolean permitFalse)
	{
		this.dt = other.dt;
		this.permitFalse = permitFalse;
		this.permitTrue = permitTrue;
	}

	@Override
	public RestrictedDatatype<Boolean> applyConstrainingFacet(final ATermAppl facet, final Object value) throws InvalidConstrainingFacetException
	{
		throw new InvalidConstrainingFacetException(facet, value);
	}

	@Override
	public boolean contains(final Object value)
	{
		if (value instanceof Boolean)
		{
			final Boolean b = (Boolean) value;
			if (b.booleanValue())
				return permitTrue;
			else
				return permitFalse;
		}
		return false;
	}

	@Override
	public boolean containsAtLeast(final int n)
	{
		if (n <= 0)
			return true;
		if (n == 1)
			return permitTrue || permitFalse;
		if (n == 2)
			return permitTrue && permitFalse;
		return false;
	}

	@Override
	public RestrictedDatatype<Boolean> exclude(final Collection<?> values)
	{
		boolean permitTrue = this.permitTrue;
		boolean permitFalse = this.permitFalse;
		for (final Object o : values)
			if (o instanceof Boolean)
			{
				final Boolean b = (Boolean) o;
				if (b.booleanValue())
					permitTrue = false;
				else
					permitFalse = false;
			}
		if ((permitTrue == this.permitTrue) && (permitFalse == this.permitFalse))
			return this;
		else
			return new RestrictedBooleanDatatype(this, permitTrue, permitFalse);
	}

	@Override
	public Datatype<? extends Boolean> getDatatype()
	{
		return dt;
	}

	@Override
	public Boolean getValue(final int i)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public RestrictedDatatype<Boolean> intersect(final RestrictedDatatype<?> other, final boolean negated)
	{
		if (other instanceof RestrictedBooleanDatatype)
		{
			final RestrictedBooleanDatatype otherRBD = (RestrictedBooleanDatatype) other;
			final boolean permitTrue = this.permitTrue && otherRBD.permitTrue;
			final boolean permitFalse = this.permitFalse && otherRBD.permitFalse;

			if ((permitTrue == this.permitTrue) && (permitFalse == this.permitFalse))
				return this;
			if ((permitTrue == otherRBD.permitTrue) && (permitFalse == otherRBD.permitFalse))
				return otherRBD;
			return new RestrictedBooleanDatatype(this, permitTrue, permitFalse);
		}
		else
			throw new IllegalArgumentException();
	}

	@Override
	public boolean isEmpty()
	{
		return !permitTrue && !permitFalse;
	}

	@Override
	public boolean isEnumerable()
	{
		return true;
	}

	@Override
	public boolean isFinite()
	{
		return true;
	}

	@Override
	public int size()
	{
		return (permitTrue ? 1 : 0) + (permitFalse ? 1 : 0);
	}

	@Override
	public RestrictedDatatype<Boolean> union(final RestrictedDatatype<?> other)
	{
		if (other instanceof RestrictedBooleanDatatype)
		{
			final RestrictedBooleanDatatype otherRBD = (RestrictedBooleanDatatype) other;
			final boolean permitTrue = this.permitTrue || otherRBD.permitTrue;
			final boolean permitFalse = this.permitFalse || otherRBD.permitFalse;

			if ((permitTrue == this.permitTrue) && (permitFalse == this.permitFalse))
				return this;
			if ((permitTrue == otherRBD.permitTrue) && (permitFalse == otherRBD.permitFalse))
				return otherRBD;
			return new RestrictedBooleanDatatype(this, permitTrue, permitFalse);
		}
		else
			throw new IllegalArgumentException();
	}

	@Override
	public Iterator<Boolean> valueIterator()
	{
		if (permitTrue)
			if (permitFalse)
				return Arrays.asList(Boolean.TRUE, Boolean.FALSE).iterator();
			else
				return Arrays.asList(Boolean.TRUE).iterator();
		else
			if (permitFalse)
				return Arrays.asList(Boolean.FALSE).iterator();
			else
				return new EmptyIterator<>();
	}

}
