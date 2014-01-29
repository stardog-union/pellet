// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.jena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.SortCondition;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.core.ResultBinding;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingComparator;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class SortedResultSet implements ResultSet {
	private List<Binding>		sortedRows;
	
	private Iterator<Binding>	iterator;

	private int					row;

	private List<String>		resultVars;

	@SuppressWarnings("unchecked")
	public SortedResultSet(ResultSet results, List<SortCondition> sortConditions) {
		resultVars = results.getResultVars();
		
		sortedRows = new ArrayList<Binding>();        
        while( results.hasNext() ) {
        	sortedRows.add( results.nextBinding() );
        }
        
		BindingComparator cmp = new BindingComparator( sortConditions );
        Collections.sort( sortedRows, cmp );
        
		iterator = sortedRows.iterator();
		row = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getResultVars() {
		return resultVars;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getRowNumber() {
		return row;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasNext() {
		return iterator.hasNext();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isOrdered() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public QuerySolution next() {
		return nextSolution();
	}

	/**
	 * {@inheritDoc}
	 */
	public Binding nextBinding() {
		return iterator.next();
	}

	/**
	 * {@inheritDoc}
	 */
	public QuerySolution nextSolution() {
		return new ResultBinding( null, nextBinding() );
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return sortedRows.toString();
	}
	
	public Model getResourceModel() {
		return null;
	}
}
