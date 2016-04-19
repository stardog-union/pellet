package com.clarkparsia.pellet.datatypes;

import aterm.ATermAppl;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
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
class NamedDatatype<T> implements Datatype<T>
{
	private final ATermAppl name;
	private final RestrictedDatatype<T> range;

	NamedDatatype(final ATermAppl name, final RestrictedDatatype<T> range)
	{
		if (name == null)
			throw new NullPointerException();
		if (name.getArity() != 0)
			throw new IllegalArgumentException();

		this.name = name;
		this.range = range;
	}

	@Override
	public RestrictedDatatype<T> asDataRange()
	{
		return range;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final NamedDatatype other = (NamedDatatype) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else
			if (!name.equals(other.name))
				return false;
		return true;
	}

	@Override
	public ATermAppl getCanonicalRepresentation(final ATermAppl input) throws InvalidLiteralException
	{
		return range.getDatatype().getCanonicalRepresentation(input);
	}

	@Override
	public ATermAppl getLiteral(final Object value)
	{
		if (value instanceof ATermAppl)
		{
			final ATermAppl a = (ATermAppl) value;
			if (ATermUtils.isLiteral(a))
				if (name.equals(a.getArgument(ATermUtils.LIT_URI_INDEX)))
					return a;
		}
		throw new IllegalArgumentException();
	}

	@Override
	public ATermAppl getName()
	{
		return name;
	}

	@Override
	public Datatype<?> getPrimitiveDatatype()
	{
		return range.getDatatype().getPrimitiveDatatype();
	}

	@Override
	public T getValue(final ATermAppl literal) throws InvalidLiteralException
	{
		final T value = range.getDatatype().getValue(literal);

		if (!range.contains(value))
			throw new InvalidLiteralException(name, literal.getArgument(ATermUtils.LIT_VAL_INDEX).toString());

		return value;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean isPrimitive()
	{
		return false;
	}

	@Override
	public String toString()
	{
		return name.getName();
	}

}
