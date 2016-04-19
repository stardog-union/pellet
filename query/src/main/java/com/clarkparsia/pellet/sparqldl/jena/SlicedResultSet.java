// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.jena;

import java.util.List;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.engine.binding.Binding;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: Clark & Parsia, LLC. <http://www.clarkparsia.com></p>
 *
 * @author Evren Sirin
 */
public class SlicedResultSet implements ResultSet {
    private ResultSet results;
    private int row;
    private long limit;
    
    public SlicedResultSet( ResultSet results, long offset, long limit ) {
        this.results = results;
        this.row = 0;
        this.limit = limit;
                        
        for( int i = 0; i < offset && results.hasNext(); i++ ) {
        	results.next();
		}       
    }    

	/**
	 * {@inheritDoc}
	 */
    public boolean hasNext() {
        return row < limit && results.hasNext();
    }

	/**
	 * {@inheritDoc}
	 */
	public Binding nextBinding() {
		row++;
		
        return results.nextBinding(); 
	}

	/**
	 * {@inheritDoc}
	 */
    public QuerySolution nextSolution() {        
		row++;
		
        return results.nextSolution();
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
	public List<String> getResultVars() {
		return results.getResultVars();
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
    public void remove() throws UnsupportedOperationException {
        results.remove();
    }

	@Override
    public String toString() {
        return results.toString();
    }
    
    public Model getResourceModel() {
    	return null;
    }
}