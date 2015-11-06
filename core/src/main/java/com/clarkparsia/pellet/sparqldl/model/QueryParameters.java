// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import aterm.ATermAppl;

/**
 * <p>
 * Title: QueryParameter
 * </p>
 * <p>
 * Description: Class for query parameterization
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Markus Stocker
 */
public class QueryParameters {

	private Map<ATermAppl, ATermAppl> parameters ;
	
	public QueryParameters() {
		parameters = new HashMap<ATermAppl, ATermAppl>();
	}
	
	public void add(ATermAppl key, ATermAppl value) {
		parameters.put( key, value );
	}
	
	public Set<Map.Entry<ATermAppl, ATermAppl>> entrySet() {
		return parameters.entrySet();
	}
	
	public boolean cointains(ATermAppl key) {
		return parameters.containsKey( key );
	}
	
	public ATermAppl get(ATermAppl key) {
		return parameters.get( key );
	}
	
	public String toString() {
		return parameters.toString();
	}
}
