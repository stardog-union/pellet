package com.clarkparsia.pellet.datatypes.types.text;

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

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.RestrictedDatatype;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidConstrainingFacetException;

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
public class RestrictedTextDatatype implements RestrictedDatatype<ATermAppl> {

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

	private static final Set<ATermAppl>	permittedDts;
	
	private final Set<Object> excludedValues;
	private final Set<Pattern> patterns;

	static {
		permittedDts = new HashSet<ATermAppl>( Arrays.asList( ATermUtils.EMPTY ) );
	}

	/*
	 * TODO: This is awkward.
	 */
	public static boolean addPermittedDatatype(ATermAppl dt) {
		return permittedDts.add( dt );
	}

	private final boolean				allowLang;
	private final Datatype<ATermAppl>	dt;

	public RestrictedTextDatatype(Datatype<ATermAppl> dt, boolean allowLang) {
		this(dt, Collections.<Pattern>emptySet(), allowLang, Collections.emptySet());
	}

	public RestrictedTextDatatype(Datatype<ATermAppl> dt, String pattern) {
		this(dt, Collections.singleton(Pattern.compile(pattern)), false, Collections.emptySet());
	}
	
	private RestrictedTextDatatype(Datatype<ATermAppl> dt, Set<Pattern> patterns, boolean allowLang, Set<Object> excludedValues) {
		this.dt = dt;
		this.allowLang = allowLang;
		this.excludedValues = excludedValues;
		this.patterns = patterns;
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
					&& permittedDts.contains( a.getArgument( ATermUtils.LIT_URI_INDEX ) ) ) {
				if( !allowLang
						&& !ATermUtils.EMPTY.equals( a.getArgument( ATermUtils.LIT_LANG_INDEX ) ) ) {
	                return false;
                }
				
				if (!patterns.isEmpty()) {
					String litValue = ((ATermAppl) a.getArgument(ATermUtils.LIT_VAL_INDEX)).getName();
					for (Pattern pattern : patterns) {
		                if( !pattern.matcher(litValue).matches() )
		                	return false;
	                }
				}
				
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
		return new RestrictedTextDatatype(dt, patterns, allowLang, newExcludedValues);
	}

	public Datatype<? extends ATermAppl> getDatatype() {
		return dt;
	}

	public ATermAppl getValue(int i) {
		throw new UnsupportedOperationException();
	}

	protected <T> List<T> concatLists(List<T> l1, List<T> l2) {
		if( l1.isEmpty() )
			return l2;
		if( l2.isEmpty() )
			return l1;
		
		List<T> newList = new ArrayList<T>(l1.size() + l2.size());
		newList.addAll(l1);
		newList.addAll(l2);
		
		return newList;
	}
	
	public RestrictedDatatype<ATermAppl> intersect(RestrictedDatatype<?> other, boolean negated) {
		if( other instanceof RestrictedTextDatatype ) {
			RestrictedTextDatatype that = (RestrictedTextDatatype) other;
			
			return new RestrictedTextDatatype(dt, SetUtils.union(this.patterns, that.patterns), this.allowLang
			                                                                                    && that.allowLang,
			                SetUtils.union(this.excludedValues, that.excludedValues));			
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
		if( other instanceof RestrictedTextDatatype ) {
			if (!patterns.isEmpty() || !((RestrictedTextDatatype) other).patterns.isEmpty()) {
				throw new UnsupportedOperationException();
			}

			if( this.allowLang ) {
	            return this;
            }

			return (RestrictedTextDatatype) other;
		}
        else {
	        throw new IllegalArgumentException();
        }
	}

	public Iterator<ATermAppl> valueIterator() {
		throw new IllegalStateException();
	}

}
