package com.clarkparsia.pellet.datatypes.types.uri;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.RestrictedDatatype;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidConstrainingFacetException;

/**
 * <p>
 * Title: Restricted URI Datatype
 * </p>
 * <p>
 * Description: A subset of the value space of xsd:anyURI
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
public class RestrictedURIDatatype implements RestrictedDatatype<ATermAppl> {
	private final Datatype<ATermAppl>	dt;
	private final Set<Object> excludedValues;

	public RestrictedURIDatatype(Datatype<ATermAppl> dt) {
		this(dt, Collections.emptySet());
	}
	
	private RestrictedURIDatatype(Datatype<ATermAppl> dt, Set<Object> excludedValues) {
		this.dt = dt;
		this.excludedValues = excludedValues;
	}

	public RestrictedDatatype<ATermAppl> applyConstrainingFacet(ATermAppl facet, Object value)
			throws InvalidConstrainingFacetException {
		// TODO: support facets
		throw new UnsupportedOperationException();
	}

	public boolean contains(Object value) {
		if( value instanceof ATermAppl ) {
			final ATermAppl a = (ATermAppl) value;

			if (excludedValues.contains(a)) {
				return false;
			}

			if( ATermUtils.isLiteral( a )
					&& XSDAnyURI.NAME.equals( a.getArgument( ATermUtils.LIT_URI_INDEX ) ) ) {
				return true;
			}
		}
		return false;
	}

	public boolean containsAtLeast(int n) {
		return true;
	}

	public RestrictedDatatype<ATermAppl> exclude(Collection<?> values) {
		Set<Object> newExcludedValues = new HashSet<Object>(values);
		newExcludedValues.addAll(excludedValues);
		return new RestrictedURIDatatype(dt, newExcludedValues);
	}

	public Datatype<? extends ATermAppl> getDatatype() {
		return dt;
	}

	public ATermAppl getValue(int i) {
		throw new UnsupportedOperationException();
	}

	public RestrictedDatatype<ATermAppl> intersect(RestrictedDatatype<?> other, boolean negated) {
		if( other instanceof RestrictedURIDatatype ) {
			return this;
		}
        else {
	        throw new IllegalArgumentException();
        }
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

	public RestrictedDatatype<ATermAppl> union(RestrictedDatatype<?> other) {
		if( other instanceof RestrictedURIDatatype ) {
			return this;
		}
        else {
	        throw new IllegalArgumentException();
        }
	}

	public Iterator<ATermAppl> valueIterator() {
		throw new IllegalStateException();
	}

}
