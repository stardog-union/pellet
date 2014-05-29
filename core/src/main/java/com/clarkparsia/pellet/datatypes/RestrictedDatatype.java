package com.clarkparsia.pellet.datatypes;

import java.util.Collection;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.exceptions.InvalidConstrainingFacetException;

/**
 * <p>
 * Title: Restricted Datatype
 * </p>
 * <p>
 * Description: Interface representing the value space of a datatype, optionally
 * constrained by application of constraing facets (see
 * {@link #applyConstrainingFacet(ATermAppl, Object)}), and optionally excluding
 * specific values (see {@link #exclude(Collection)}).<i>Implementations should
 * be immutable, with all mutators returning either the object unchanged, or a
 * new object.</i>
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
public interface RestrictedDatatype<T> extends DataRange<T> {

	/**
	 * Query for the base datatype of a restricted datatype. This is not
	 * necessarily a primitive datatype.
	 * 
	 * @return the base datatype
	 */
	public Datatype<? extends T> getDatatype();

	/**
	 * Apply a constraining facet to further restrict the value space
	 * 
	 * @param facet
	 *            the constraining facet name (typically a URI)
	 * @param value
	 *            the value for the constraining facet. This is not necessarily
	 *            in the value space of the base datatype (e.g., length facet
	 *            values on string types are not)
	 * @return the further restricted value space
	 * @throws InvalidConstrainingFacetException
	 */
	public RestrictedDatatype<T> applyConstrainingFacet(ATermAppl facet, Object value)
			throws InvalidConstrainingFacetException;

	/**
	 * Intersect this range with another range. It must be the case that
	 * 
	 * <code>getDatatype().getPrimitiveDatatype().equals( other.getDatatype.getPrimiveDatatype()</code>
	 * 
	 * @param other
	 *            restricted datatype to merge into this
	 * @param negated
	 *            <code>true</code> if <code>other</code> is negated,
	 *            <code>false</code> else
	 * @return a new {@link RestrictedDatatype}, representing the intersection
	 *         of <code>this</code> and <code>other</code>
	 * @throws {@link IllegalArgumentException} if the primitive datatypes for
	 *         the ranges are not equal
	 */
	public RestrictedDatatype<T> intersect(RestrictedDatatype<?> other, boolean negated);

	/**
	 * Exclude specific elements from the data range. Used when a restricted
	 * data range is in a conjunction with a negative enumeration. Elements in
	 * <code>values</code> that are not contained in the restriction are
	 * ignored.
	 * 
	 * @param values
	 *            The values to exclude
	 * @return A new {@link RestrictedDatatype}
	 */
	public RestrictedDatatype<T> exclude(Collection<?> values);

	/**
	 * Union this range with another range. It must be the case that
	 * 
	 * <code>getDatatype().getPrimitiveDatatype().equals( other.getDatatype.getPrimiveDatatype()</code>
	 * 
	 * @param other
	 *            restricted datatype to merge with this
	 * @return a new {@link RestrictedDatatype}, representing the intersection
	 *         of <code>this</code> and <code>other</code>
	 * @throws {@link IllegalArgumentException} if the primitive datatypes for
	 *         the ranges are not equal
	 */
	public RestrictedDatatype<T> union(RestrictedDatatype<?> other);
}
