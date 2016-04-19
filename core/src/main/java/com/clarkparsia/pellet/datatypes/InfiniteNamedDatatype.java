package com.clarkparsia.pellet.datatypes;

import aterm.ATermAppl;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidConstrainingFacetException;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * <p>
 * Title: Infinite Named Datatype
 * </p>
 * <p>
 * Description: Infinite named datatype, which permits all strings as lexical forms and for which identity and equality of the value space match equality of the
 * lexical space.
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
public class InfiniteNamedDatatype implements Datatype<ATermAppl>
{

	private static final Map<ATermAppl, WeakReference<InfiniteNamedDatatype>> cache;

	static
	{
		cache = new WeakHashMap<>();
	}

	/**
	 * Get an instance with a specific name.
	 *
	 * @param dtName the name of the datatype
	 * @return an instance
	 */
	public static InfiniteNamedDatatype get(final ATermAppl dtName)
	{
		final WeakReference<InfiniteNamedDatatype> dtRef = cache.get(dtName);
		InfiniteNamedDatatype dt = (dtRef == null) ? null : dtRef.get();
		if (dt == null)
		{
			dt = new InfiniteNamedDatatype(dtName);
			cache.put(dtName, new WeakReference<>(dt));
		}

		return dt;
	}

	private final ATermAppl name;
	private final RestrictedDatatype<ATermAppl> range;

	private InfiniteNamedDatatype(final ATermAppl name)
	{
		if (name == null)
			throw new NullPointerException();
		if (name.getArity() != 0)
			throw new IllegalArgumentException();

		this.name = name;
		range = new RestrictedDatatype<ATermAppl>()
		{

			@Override
			public RestrictedDatatype<ATermAppl> applyConstrainingFacet(final ATermAppl facet, final Object value) throws InvalidConstrainingFacetException
			{
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean contains(final Object value)
			{
				if (value instanceof ATermAppl)
				{
					final ATermAppl a = (ATermAppl) value;
					if (ATermUtils.isLiteral(a))
					{
						final ATermAppl dt = (ATermAppl) a.getArgument(ATermUtils.LIT_URI_INDEX);
						return InfiniteNamedDatatype.this.name.equals(dt);
					}
				}
				return false;
			}

			@Override
			public boolean containsAtLeast(final int n)
			{
				return true;
			}

			@Override
			public RestrictedDatatype<ATermAppl> exclude(final Collection<?> values)
			{
				/*
				 * TODO: Supporting everything exception exclusion will cause
				 * problems
				 */
				throw new UnsupportedOperationException();
			}

			@Override
			public Datatype<? extends ATermAppl> getDatatype()
			{
				return InfiniteNamedDatatype.this;
			}

			@Override
			public ATermAppl getValue(final int i)
			{
				throw new UnsupportedOperationException();
			}

			@Override
			public RestrictedDatatype<ATermAppl> intersect(final RestrictedDatatype<?> other, final boolean negated)
			{
				if (other == this)
					return this;
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
			public int size()
			{
				throw new UnsupportedOperationException();
			}

			@Override
			public RestrictedDatatype<ATermAppl> union(final RestrictedDatatype<?> other)
			{
				if (other == this)
					return this;
				throw new IllegalArgumentException();
			}

			@Override
			public Iterator<ATermAppl> valueIterator()
			{
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public RestrictedDatatype<ATermAppl> asDataRange()
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
		final InfiniteNamedDatatype other = (InfiniteNamedDatatype) obj;
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
		if (!ATermUtils.isLiteral(input))
			throw new IllegalArgumentException();
		if (!name.equals(input.getArgument(ATermUtils.LIT_URI_INDEX)))
			throw new IllegalArgumentException();

		return input;
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
		return this;
	}

	@Override
	public ATermAppl getValue(final ATermAppl literal) throws InvalidLiteralException
	{
		if (!ATermUtils.isLiteral(literal))
			throw new IllegalArgumentException();
		if (!name.equals(literal.getArgument(ATermUtils.LIT_URI_INDEX)))
			throw new IllegalArgumentException();

		return literal;
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
		return true;
	}

	@Override
	public String toString()
	{
		return name.getName();
	}

}
