// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.vocabulary;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Clark & Parsia, LLC. <http://www.clarkparsia.com></p>
 *
 * @author Evren Sirin
 */
public enum BuiltinNamespace {
	OWL( "http://www.w3.org/2002/07/owl#" ),
	RDF( "http://www.w3.org/1999/02/22-rdf-syntax-ns#" ),
	RDFS( "http://www.w3.org/2000/01/rdf-schema#" ),
	XSD( "http://www.w3.org/2001/XMLSchema#" ),
	SWRL( "http://www.w3.org/2003/11/swrl#" ),
	SWRLB( "http://www.w3.org/2003/11/swrlb#" );
	
	private String uri;
	
	BuiltinNamespace(String uri) {
		this.uri = uri;
	}
	
	public String getURI() {
		return uri;
	}		
	
	public static final Map<String, BuiltinNamespace>	uriMap;
	static {
		uriMap	= new HashMap<String, BuiltinNamespace>();
		for( BuiltinNamespace ns : BuiltinNamespace.values() ) {
			uriMap.put( ns.getURI(), ns );
		}
	}		
	
	public static BuiltinNamespace find(String uri) {
		return uriMap.get( uri );
	}
}