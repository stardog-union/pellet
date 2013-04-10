// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.datatypes;

import java.util.HashMap;
import java.util.Map;

import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

import com.clarkparsia.pellet.vocabulary.BuiltinNamespace;


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
public enum DatatypeFacet {
	minInclusive,
	minExclusive,
	maxInclusive,
	maxExclusive,
	pattern,
	length,
	minLength,
	maxLength;
	
	private static final Map<String, DatatypeFacet>	uriMap = new HashMap<String, DatatypeFacet>();
	static {
		for( DatatypeFacet facet : DatatypeFacet.values() ) {
			uriMap.put( facet.getURI(), facet );
		}
	}
	
	private String uri;
	private ATermAppl name;
	
	DatatypeFacet() {
		this.uri = BuiltinNamespace.XSD.getURI() + toString();
		this.name = ATermUtils.makeTermAppl( uri );
	}
	
	public String getURI() {
		return uri;
	}
	
	public ATermAppl getName() {
		return name;
	}
	
	public static DatatypeFacet find(String uri) {
		return uriMap.get( uri );
	}
}
