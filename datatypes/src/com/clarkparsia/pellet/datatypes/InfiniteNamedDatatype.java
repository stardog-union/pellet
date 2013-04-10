package com.clarkparsia.pellet.datatypes;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.exceptions.InvalidConstrainingFacetException;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;

/**
 * <p>
 * Title: Infinite Named Datatype
 * </p>
 * <p>
 * Description: Infinite named datatype, which permits all strings as lexical
 * forms and for which identity and equality of the value space match equality
 * of the lexical space.
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
public class InfiniteNamedDatatype implements Datatype<ATermAppl> {

	private static final Map<ATermAppl, WeakReference<InfiniteNamedDatatype>>	cache;

	static {
		cache = new WeakHashMap<ATermAppl, WeakReference<InfiniteNamedDatatype>>();
	}

	/**
	 * Get an instance with a specific name.
	 * 
	 * @param dtName
	 *            the name of the datatype
	 * @return an instance
	 */
	public static InfiniteNamedDatatype get(ATermAppl dtName) {
		WeakReference<InfiniteNamedDatatype> dtRef = cache.get( dtName );
		InfiniteNamedDatatype dt = (dtRef == null)
			? null
			: dtRef.get();
		if( dt == null ) {
			dt = new InfiniteNamedDatatype( dtName );
			cache.put( dtName, new WeakReference<InfiniteNamedDatatype>( dt ) );
		}

		return dt;
	}

	private final ATermAppl						name;
	private final RestrictedDatatype<ATermAppl>	range;

	private InfiniteNamedDatatype(ATermAppl name) {
		if( name == null )
			throw new NullPointerException();
		if( name.getArity() != 0 )
			throw new IllegalArgumentException();

		this.name = name;
		range = new RestrictedDatatype<ATermAppl>() {

			public RestrictedDatatype<ATermAppl> applyConstrainingFacet(ATermAppl facet,
					Object value) throws InvalidConstrainingFacetException {
				throw new UnsupportedOperationException();
			}

			public boolean contains(Object value) {
				if( value instanceof ATermAppl ) {
					final ATermAppl a = (ATermAppl) value;
					if( ATermUtils.isLiteral( a ) ) {
						final ATermAppl dt = (ATermAppl) a.getArgument( ATermUtils.LIT_URI_INDEX );
						return InfiniteNamedDatatype.this.name.equals( dt );
					}
				}
				return false;
			}

			public boolean containsAtLeast(int n) {
				return true;
			}

			public RestrictedDatatype<ATermAppl> exclude(Collection<?> values) {
				/*
				 * TODO: Supporting everything exception exclusion will cause
				 * problems
				 */
				throw new UnsupportedOperationException();
			}

			public Datatype<? extends ATermAppl> getDatatype() {
				return InfiniteNamedDatatype.this;
			}

			public ATermAppl getValue(int i) {
				throw new UnsupportedOperationException();
			}

			public RestrictedDatatype<ATermAppl> intersect(RestrictedDatatype<?> other,
					boolean negated) {
				if( other == this )
					return this;
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
				throw new UnsupportedOperationException();
			}

			public RestrictedDatatype<ATermAppl> union(RestrictedDatatype<?> other) {
				if( other == this )
					return this;
				throw new IllegalArgumentException();
			}

			public Iterator<ATermAppl> valueIterator() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public RestrictedDatatype<ATermAppl> asDataRange() {
		return range;
	}

	@Override
	public boolean equals(Object obj) {
		if( this == obj )
			return true;
		if( obj == null )
			return false;
		if( getClass() != obj.getClass() )
			return false;
		InfiniteNamedDatatype other = (InfiniteNamedDatatype) obj;
		if( name == null ) {
			if( other.name != null )
				return false;
		}
		else if( !name.equals( other.name ) )
			return false;
		return true;
	}

	public ATermAppl getCanonicalRepresentation(ATermAppl input) throws InvalidLiteralException {
		if( !ATermUtils.isLiteral( input ) )
			throw new IllegalArgumentException();
		if( !name.equals( input.getArgument( ATermUtils.LIT_URI_INDEX ) ) )
			throw new IllegalArgumentException();

		return input;
	}

	public ATermAppl getLiteral(Object value) {
		if( value instanceof ATermAppl ) {
			final ATermAppl a = (ATermAppl) value;
			if( ATermUtils.isLiteral( a ) )
				if( name.equals( a.getArgument( ATermUtils.LIT_URI_INDEX ) ) )
					return a;
		}
		throw new IllegalArgumentException();
	}

	public ATermAppl getName() {
		return name;
	}

	public Datatype<?> getPrimitiveDatatype() {
		return this;
	}

	public ATermAppl getValue(ATermAppl literal) throws InvalidLiteralException {
		if( !ATermUtils.isLiteral( literal ) )
			throw new IllegalArgumentException();
		if( !name.equals( literal.getArgument( ATermUtils.LIT_URI_INDEX ) ) )
			throw new IllegalArgumentException();

		return literal;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null)
			? 0
			: name.hashCode());
		return result;
	}

	public boolean isPrimitive() {
		return true;
	}

	@Override
	public String toString() {
		return name.getName();
	}

}
