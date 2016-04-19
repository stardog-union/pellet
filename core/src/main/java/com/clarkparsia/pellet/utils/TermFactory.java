// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.utils;

import aterm.ATermAppl;
import aterm.ATermList;
import com.clarkparsia.pellet.datatypes.Datatypes;
import com.clarkparsia.pellet.datatypes.Facet;
import java.net.URI;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * @author Evren Sirin
 */
public class TermFactory
{
	public static final ATermAppl TOP = ATermUtils.TOP;
	public static final ATermAppl BOTTOM = ATermUtils.BOTTOM;
	public static final ATermAppl TOP_LIT = ATermUtils.TOP_LIT;
	public static final ATermAppl BOTTOM_LIT = ATermUtils.BOTTOM_LIT;
	public static final ATermAppl TOP_DATA_PROPERTY = ATermUtils.TOP_DATA_PROPERTY;
	public static final ATermAppl BOTTOM_DATA_PROPERTY = ATermUtils.BOTTOM_DATA_PROPERTY;
	public static final ATermAppl TOP_OBJECT_PROPERTY = ATermUtils.TOP_OBJECT_PROPERTY;
	public static final ATermAppl BOTTOM_OBJECT_PROPERTY = ATermUtils.BOTTOM_OBJECT_PROPERTY;

	/**
	 * Create a named term.
	 *
	 * @param name name of the term
	 * @return an ATermAppl with the given name and no arguments
	 */
	public static ATermAppl term(final String name)
	{
		return ATermUtils.makeTermAppl(name);
	}

	/**
	 * Create a term that corresponds to a bnode (anonymous term).
	 *
	 * @param anonID is of the anonymous terms
	 * @return an ATermAppl with a single argument for anonID
	 */
	public static ATermAppl bnode(final String anonID)
	{
		return ATermUtils.makeBnode(anonID);
	}

	/**
	 * Create a variable temr with the given name.
	 *
	 * @param name name of the variable
	 * @return an ATermAppl with a single argument for variable name
	 */
	public static ATermAppl var(final String name)
	{
		return ATermUtils.makeVar(name);
	}

	/**
	 * Create a list of terms.
	 *
	 * @param args elements of thre list
	 * @return an ATermList with given elements in the given order
	 */
	public static ATermList list(final ATermAppl... args)
	{
		return ATermUtils.makeList(args);
	}

	/**
	 * Create a negated term.
	 *
	 * @param c Term to be negated
	 * @return an ATermAppl in the form not(c)
	 */
	public static ATermAppl not(final ATermAppl c)
	{
		return ATermUtils.makeNot(c);
	}

	/**
	 * Create an all values restriction.
	 *
	 * @param r property term
	 * @param c class or datatype term
	 * @return an ATermAppl in the form all(r,c)
	 */
	public static ATermAppl all(final ATermAppl r, final ATermAppl c)
	{
		return ATermUtils.makeAllValues(r, c);
	}

	public static ATermAppl some(final ATermAppl r, final ATermAppl c)
	{
		return ATermUtils.makeSomeValues(r, c);
	}

	public static ATermAppl min(final ATermAppl r, final int n, final ATermAppl c)
	{
		return ATermUtils.makeMin(r, n, c);
	}

	public static ATermAppl max(final ATermAppl r, final int n, final ATermAppl c)
	{
		return ATermUtils.makeMax(r, n, c);
	}

	public static ATermAppl card(final ATermAppl r, final int n, final ATermAppl c)
	{
		return ATermUtils.makeCard(r, n, c);
	}

	public static ATermAppl inv(final ATermAppl r)
	{
		return ATermUtils.makeInv(r);
	}

	/**
	 * Create a typed literal with xsd:boolean datatype.
	 * 
	 * @param value boolean value
	 * @return an ATermAppl representing the boolean literal
	 */
	public static ATermAppl literal(final boolean value)
	{
		return ATermUtils.makeTypedLiteral(value ? "true" : "false", Datatypes.BOOLEAN);
	}

	/**
	 * Create a typed literal with xsd:byte datatype.
	 * 
	 * @param value byte value
	 * @return an ATermAppl representing the byte literal
	 */
	public static ATermAppl literal(final byte value)
	{
		return ATermUtils.makeTypedLiteral(String.valueOf(value), Datatypes.BYTE);
	}

	/**
	 * Create a typed literal with xsd:short datatype.
	 * 
	 * @param value short value
	 * @return an ATermAppl representing the short literal
	 */
	public static ATermAppl literal(final short value)
	{
		return ATermUtils.makeTypedLiteral(String.valueOf(value), Datatypes.SHORT);
	}

	/**
	 * Create a typed literal with xsd:long datatype.
	 * 
	 * @param value long value
	 * @return an ATermAppl representing the long literal
	 */
	public static ATermAppl literal(final long value)
	{
		return ATermUtils.makeTypedLiteral(String.valueOf(value), Datatypes.LONG);
	}

	/**
	 * Create a typed literal with xsd:integer datatype.
	 * 
	 * @param value integer value
	 * @return an ATermAppl representing the integer literal
	 */
	public static ATermAppl literal(final int value)
	{
		return ATermUtils.makeTypedLiteral(String.valueOf(value), Datatypes.INTEGER);
	}

	/**
	 * Create a typed literal with xsd:float datatype.
	 * 
	 * @param value float value
	 * @return an ATermAppl representing the float literal
	 */
	public static ATermAppl literal(final float value)
	{
		return ATermUtils.makeTypedLiteral(String.valueOf(value), Datatypes.FLOAT);
	}

	/**
	 * Create a typed literal with xsd:double datatype.
	 * 
	 * @param value double value
	 * @return an ATermAppl representing the double literal
	 */
	public static ATermAppl literal(final double value)
	{
		return ATermUtils.makeTypedLiteral(String.valueOf(value), Datatypes.DOUBLE);
	}

	/**
	 * Create a typed literal with xsd:anyURI datatype.
	 * 
	 * @param value URI value
	 * @return an ATermAppl representing the URI literal
	 */
	public static ATermAppl literal(final URI value)
	{
		return ATermUtils.makeTypedLiteral(String.valueOf(value), Datatypes.ANY_URI);
	}

	/**
	 * Create an untyped plain literal with no language tag.
	 * 
	 * @param value string value
	 * @return an ATermAppl representing the plain literal
	 */
	public static ATermAppl literal(final String value)
	{
		return ATermUtils.makePlainLiteral(value);
	}

	/**
	 * Create an untyped plain literal with the given language tag.
	 * 
	 * @param value byte value
	 * @param lang language identifier
	 * @return an ATermAppl representing the plain literal with language tag
	 */
	public static ATermAppl literal(final String value, final String lang)
	{
		return ATermUtils.makePlainLiteral(value, lang);
	}

	/**
	 * Create a typed literal with given lexical form and datatypes.
	 * 
	 * @param value lexical value of the literal
	 * @param datatype type of the literal
	 * @return an ATermAppl representing the typed literal
	 */
	public static ATermAppl literal(final String value, final ATermAppl datatype)
	{
		return ATermUtils.makeTypedLiteral(value, datatype);
	}

	/**
	 * @deprecated Use {@link #literal(String, String)} instead
	 */
	@Deprecated
	public static ATermAppl plainLiteral(final String value, final String lang)
	{
		return ATermUtils.makePlainLiteral(value, lang);
	}

	/**
	 * @deprecated Use {@link #literal(String, ATermAppl)} instead
	 */
	@Deprecated
	public static ATermAppl typedLiteral(final String value, final ATermAppl dt)
	{
		return ATermUtils.makeTypedLiteral(value, dt);
	}

	/**
	 * @deprecated Use {@link #literal(String, ATermAppl)} instead
	 */
	@Deprecated
	public static ATermAppl typedLiteral(final String value, final String dt)
	{
		return ATermUtils.makeTypedLiteral(value, dt);
	}

	public static ATermAppl value(final ATermAppl r)
	{
		return ATermUtils.makeValue(r);
	}

	public static ATermAppl and(final ATermAppl c1, final ATermAppl c2)
	{
		return ATermUtils.makeAnd(c1, c2);
	}

	public static ATermAppl and(final ATermAppl... c)
	{
		if (c.length == 1)
			return c[0];

		return ATermUtils.makeAnd(ATermUtils.makeList(c));
	}

	public static ATermAppl or(final ATermAppl c1, final ATermAppl c2)
	{
		return ATermUtils.makeOr(c1, c2);
	}

	public static ATermAppl or(final ATermAppl... c)
	{
		if (c.length == 1)
			return c[0];

		return ATermUtils.makeOr(ATermUtils.makeList(c));
	}

	public static ATermAppl hasValue(final ATermAppl r, final ATermAppl ind)
	{
		return ATermUtils.makeHasValue(r, ind);
	}

	public static ATermAppl oneOf(final ATermAppl... terms)
	{
		ATermList list = ATermUtils.EMPTY_LIST;
		for (final ATermAppl term : terms)
			list = list.insert(value(term));
		return ATermUtils.makeOr(list);
	}

	public static ATermAppl self(final ATermAppl p)
	{
		return ATermUtils.makeSelf(p);
	}

	public static ATermAppl minInclusive(final ATermAppl facetValue)
	{
		return ATermUtils.makeFacetRestriction(Facet.XSD.MIN_INCLUSIVE.getName(), facetValue);
	}

	public static ATermAppl minExclusive(final ATermAppl facetValue)
	{
		return ATermUtils.makeFacetRestriction(Facet.XSD.MIN_EXCLUSIVE.getName(), facetValue);
	}

	public static ATermAppl maxInclusive(final ATermAppl facetValue)
	{
		return ATermUtils.makeFacetRestriction(Facet.XSD.MAX_INCLUSIVE.getName(), facetValue);
	}

	public static ATermAppl maxExclusive(final ATermAppl facetValue)
	{
		return ATermUtils.makeFacetRestriction(Facet.XSD.MAX_EXCLUSIVE.getName(), facetValue);
	}

	public static ATermAppl minLength(final ATermAppl facetValue)
	{
		return ATermUtils.makeFacetRestriction(Facet.XSD.MIN_LENGTH.getName(), facetValue);
	}

	public static ATermAppl maxLength(final ATermAppl facetValue)
	{
		return ATermUtils.makeFacetRestriction(Facet.XSD.MAX_LENGTH.getName(), facetValue);
	}

	public static ATermAppl length(final ATermAppl facetValue)
	{
		return ATermUtils.makeFacetRestriction(Facet.XSD.LENGTH.getName(), facetValue);
	}

	public static ATermAppl pattern(final ATermAppl facetValue)
	{
		return ATermUtils.makeFacetRestriction(Facet.XSD.PATTERN.getName(), facetValue);
	}

	public static ATermAppl restrict(final ATermAppl baseDatatype, final ATermAppl... restrictions)
	{
		return ATermUtils.makeRestrictedDatatype(baseDatatype, restrictions);
	}
}
