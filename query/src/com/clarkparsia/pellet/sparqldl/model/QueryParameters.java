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

import org.mindswap.pellet.jena.JenaUtils;
import org.mindswap.pellet.utils.ATermUtils;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;

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
	
	public QueryParameters(QuerySolution initialBinding) {
		this();
		
		if (initialBinding == null)
			initialBinding = new QuerySolutionMap();
		
		for (Iterator iter = initialBinding.varNames(); iter.hasNext(); ) {
			String varName = (String)iter.next();
			ATermAppl key = ATermUtils.makeVar(varName);
			ATermAppl value = JenaUtils.makeATerm(initialBinding.get( varName ));
			parameters.put( key, value );
		}
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
