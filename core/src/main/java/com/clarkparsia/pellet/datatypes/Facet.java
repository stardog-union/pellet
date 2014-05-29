package com.clarkparsia.pellet.datatypes;

import java.util.HashMap;
import java.util.Map;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Facet
 * </p>
 * <p>
 * Description: Interface to centralize enumeration and query of supported
 * constraining facets
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
public interface Facet {

	public abstract ATermAppl getName();

	public static class Registry {

		private static final Map<ATermAppl, Facet>	map;
		static {
			map = new HashMap<ATermAppl, Facet>();
			for( Facet f : XSD.values() ) {
				map.put( f.getName(), f );
			}
		}

		/**
		 * Get a Facet for a URI
		 * 
		 * @param name
		 *            the name of the facet, generally a URI
		 * @return A facet if the name is registered, <code>null</code> else
		 */
		public static Facet get(ATermAppl name) {
			return map.get( name );
		}

	}

	/**
	 * Facets in the XSD name space (and documented in the XML Schema
	 * specifications)
	 */
	public static enum XSD implements Facet {
		MAX_EXCLUSIVE("maxExclusive"), MAX_INCLUSIVE("maxInclusive"),
		MIN_EXCLUSIVE("minExclusive"), MIN_INCLUSIVE("minInclusive"),
		LENGTH("length"), MIN_LENGTH("minLength"), MAX_LENGTH("maxLength"),
		PATTERN("pattern");

		private final ATermAppl	name;

		private XSD(String localName) {
			name = ATermUtils.makeTermAppl( Namespaces.XSD + localName );
		}

		public ATermAppl getName() {
			return name;
		}

	}
}
