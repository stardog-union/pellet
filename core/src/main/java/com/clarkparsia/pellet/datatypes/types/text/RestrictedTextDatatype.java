package com.clarkparsia.pellet.datatypes.types.text;

import aterm.ATermAppl;
import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.RestrictedDatatype;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidConstrainingFacetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.SetUtils;

/**
 * <p>
 * Title: Restricted Text Datatype
 * </p>
 * <p>
 * Description: A subset of the value space of rdf:plainLiteral
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
public class RestrictedTextDatatype implements RestrictedDatatype<ATermAppl>
{

	private static final String NCNAMESTARTCHAR = "[A-Z]|_|[a-z]|[\u00C0-\u00D6]|[\u00D8-\u00F6]|[\u00F8-\u02FF]|[\u0370-\u037D]|[\u037F-\u1FFF]|[\u200C-\u200D]|[\u2070-\u218F]|[\u2C00-\u2FEF]|[\u3001-\uD7FF]|[\uF900-\uFDCF]|[\uFDF0-\uFFFD]";
	private static final String NCNAMECHAR = NCNAMESTARTCHAR + "|-|\\.|[0-9]|\u00B7|[\u0300-\u036F]|[\u203F-\u2040]";
	protected static final String NCNAME = "(" + NCNAMESTARTCHAR + ")(" + NCNAMECHAR + ")*";

	private static final String NAMESTARTCHAR = ":|" + NCNAMESTARTCHAR;
	private static final String NAMECHAR = NAMESTARTCHAR + "|-|\\.|[0-9]|\u00B7|[\u0300-\u036F]|[\u203F-\u2040]";
	protected static final String NAME = "(" + NAMESTARTCHAR + ")(" + NAMECHAR + ")*";

	protected static final String NMTOKEN = "(" + NAMECHAR + ")+";

	protected static final String TOKEN = "([^\\s])(\\s([^\\s])|([^\\s]))*";

	protected static final String LANGUAGE = "[a-zA-Z]{1,8}(-[a-zA-Z0-9]{1,8})*";

	protected static final String NORMALIZED_STRING = "([^\\r\\n\\t])*";

	private static final Set<ATermAppl> permittedDts = new HashSet<>(Arrays.asList(ATermUtils.EMPTY));

	/*
	 * TODO: This is awkward.
	 */
	public static boolean addPermittedDatatype(final ATermAppl dt)
	{
		return permittedDts.add(dt);
	}

	private final Set<Object> _excludedValues;
	private final Set<Pattern> _patterns;
	private final boolean _allowLang;
	private final Datatype<ATermAppl> _dt;

	public RestrictedTextDatatype(final Datatype<ATermAppl> dt, final boolean allowLang)
	{
		this(dt, Collections.<Pattern> emptySet(), allowLang, Collections.emptySet());
	}

	public RestrictedTextDatatype(final Datatype<ATermAppl> dt, final String pattern)
	{
		this(dt, Collections.singleton(Pattern.compile(pattern)), false, Collections.emptySet());
	}

	private RestrictedTextDatatype(final Datatype<ATermAppl> dt, final Set<Pattern> patterns, final boolean allowLang, final Set<Object> excludedValues)
	{
		this._dt = dt;
		this._allowLang = allowLang;
		this._excludedValues = excludedValues;
		this._patterns = patterns;
	}

	@Override
	public RestrictedDatatype<ATermAppl> applyConstrainingFacet(final ATermAppl facet, final Object value) throws InvalidConstrainingFacetException
	{
		// TODO: support facets
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(final Object value)
	{
		if (value instanceof ATermAppl)
		{
			final ATermAppl a = (ATermAppl) value;

			if (_excludedValues.contains(a))
				return false;

			if (ATermUtils.isLiteral(a) && permittedDts.contains(a.getArgument(ATermUtils.LIT_URI_INDEX)))
			{
				if (!_allowLang && !ATermUtils.EMPTY.equals(a.getArgument(ATermUtils.LIT_LANG_INDEX)))
					return false;

				if (!_patterns.isEmpty())
				{
					final String litValue = ((ATermAppl) a.getArgument(ATermUtils.LIT_VAL_INDEX)).getName();
					for (final Pattern pattern : _patterns)
						if (!pattern.matcher(litValue).matches())
							return false;
				}

				return true;
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
		final Set<Object> newExcludedValues = new HashSet<>(values);
		newExcludedValues.addAll(_excludedValues);
		return new RestrictedTextDatatype(_dt, _patterns, _allowLang, newExcludedValues);
	}

	@Override
	public Datatype<? extends ATermAppl> getDatatype()
	{
		return _dt;
	}

	protected <T> List<T> concatLists(final List<T> l1, final List<T> l2)
	{
		if (l1.isEmpty())
			return l2;
		if (l2.isEmpty())
			return l1;

		final List<T> newList = new ArrayList<>(l1.size() + l2.size());
		newList.addAll(l1);
		newList.addAll(l2);

		return newList;
	}

	@Override
	public RestrictedDatatype<ATermAppl> intersect(final RestrictedDatatype<?> other, final boolean negated)
	{
		if (other instanceof RestrictedTextDatatype)
		{
			final RestrictedTextDatatype that = (RestrictedTextDatatype) other;

			return new RestrictedTextDatatype(_dt, SetUtils.union(this._patterns, that._patterns), this._allowLang && that._allowLang, SetUtils.union(this._excludedValues, that._excludedValues));
		}
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
	public RestrictedDatatype<ATermAppl> union(final RestrictedDatatype<?> other)
	{
		if (other instanceof RestrictedTextDatatype)
		{
			if (!_patterns.isEmpty() || !((RestrictedTextDatatype) other)._patterns.isEmpty())
				throw new UnsupportedOperationException();

			if (this._allowLang)
				return this;

			return (RestrictedTextDatatype) other;
		}
		else
			throw new IllegalArgumentException();
	}

	@Override
	public Iterator<ATermAppl> valueIterator()
	{
		throw new IllegalStateException();
	}

}
