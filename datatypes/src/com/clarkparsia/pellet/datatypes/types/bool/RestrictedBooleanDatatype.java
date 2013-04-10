package com.clarkparsia.pellet.datatypes.types.bool;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.EmptyIterator;
import com.clarkparsia.pellet.datatypes.RestrictedDatatype;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidConstrainingFacetException;

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

public class RestrictedBooleanDatatype implements RestrictedDatatype<Boolean> {

	private final Datatype<Boolean>	dt;
	private final boolean			permitFalse;
	private final boolean			permitTrue;

	public RestrictedBooleanDatatype(Datatype<Boolean> dt) {
		this.dt = dt;
		permitTrue = true;
		permitFalse = true;
	}

	private RestrictedBooleanDatatype(RestrictedBooleanDatatype other, boolean permitTrue,
			boolean permitFalse) {
		this.dt = other.dt;
		this.permitFalse = permitFalse;
		this.permitTrue = permitTrue;
	}

	public RestrictedDatatype<Boolean> applyConstrainingFacet(ATermAppl facet, Object value)
			throws InvalidConstrainingFacetException {
		throw new InvalidConstrainingFacetException( facet, value );
	}

	public boolean contains(Object value) {
		if( value instanceof Boolean ) {
			final Boolean b = (Boolean) value;
			if( b.booleanValue() )
				return permitTrue;
			else
				return permitFalse;
		}
		return false;
	}

	public boolean containsAtLeast(int n) {
		if( n <= 0 )
			return true;
		if( n == 1 )
			return permitTrue || permitFalse;
		if( n == 2 )
			return permitTrue && permitFalse;
		return false;
	}

	public RestrictedDatatype<Boolean> exclude(Collection<?> values) {
		boolean permitTrue = this.permitTrue;
		boolean permitFalse = this.permitFalse;
		for( Object o : values ) {
			if( o instanceof Boolean ) {
				final Boolean b = (Boolean) o;
				if( b.booleanValue() )
					permitTrue = false;
				else
					permitFalse = false;
			}
		}
		if( (permitTrue == this.permitTrue) && (permitFalse == this.permitFalse) )
			return this;
		else
			return new RestrictedBooleanDatatype( this, permitTrue, permitFalse );
	}

	public Datatype<? extends Boolean> getDatatype() {
		return dt;
	}

	public Boolean getValue(int i) {
		throw new UnsupportedOperationException();
	}

	public RestrictedDatatype<Boolean> intersect(RestrictedDatatype<?> other, boolean negated) {
		if( other instanceof RestrictedBooleanDatatype ) {
			final RestrictedBooleanDatatype otherRBD = (RestrictedBooleanDatatype) other;
			final boolean permitTrue = this.permitTrue && otherRBD.permitTrue;
			final boolean permitFalse = this.permitFalse && otherRBD.permitFalse;

			if( (permitTrue == this.permitTrue) && (permitFalse == this.permitFalse) )
				return this;
			if( (permitTrue == otherRBD.permitTrue) && (permitFalse == otherRBD.permitFalse) )
				return otherRBD;
			return new RestrictedBooleanDatatype( this, permitTrue, permitFalse );
		}
		else
			throw new IllegalArgumentException();
	}

	public boolean isEmpty() {
		return !permitTrue && !permitFalse;
	}

	public boolean isEnumerable() {
		return true;
	}

	public boolean isFinite() {
		return true;
	}

	public int size() {
		return (permitTrue
			? 1
			: 0) + (permitFalse
			? 1
			: 0);
	}

	public RestrictedDatatype<Boolean> union(RestrictedDatatype<?> other) {
		if( other instanceof RestrictedBooleanDatatype ) {
			final RestrictedBooleanDatatype otherRBD = (RestrictedBooleanDatatype) other;
			final boolean permitTrue = this.permitTrue || otherRBD.permitTrue;
			final boolean permitFalse = this.permitFalse || otherRBD.permitFalse;

			if( (permitTrue == this.permitTrue) && (permitFalse == this.permitFalse) )
				return this;
			if( (permitTrue == otherRBD.permitTrue) && (permitFalse == otherRBD.permitFalse) )
				return otherRBD;
			return new RestrictedBooleanDatatype( this, permitTrue, permitFalse );
		}
		else
			throw new IllegalArgumentException();
	}

	public Iterator<Boolean> valueIterator() {
		if( permitTrue )
			if( permitFalse )
				return Arrays.asList( Boolean.TRUE, Boolean.FALSE ).iterator();
			else
				return Arrays.asList( Boolean.TRUE ).iterator();
		else if( permitFalse )
			return Arrays.asList( Boolean.FALSE ).iterator();
		else
			return new EmptyIterator<Boolean>();
	}

}
