// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.model;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.mindswap.pellet.KnowledgeBase;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Query Interface
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
 * @author Petr Kremen
 */
public interface Query {

	public enum VarType {
		CLASS, PROPERTY, INDIVIDUAL, LITERAL
	}

	/**
	 * Sets the filter for this query.
	 * 
	 * @return
	 */
	public void setFilter(final Filter filter);

	/**
	 * Sets the filter for this query.
	 * 
	 * @return
	 */
	public Filter getFilter();

	/**
	 * Returns true if distinct results are required.
	 * 
	 * @return
	 */
	public boolean isDistinct();

	/**
	 * Returns variables that occur in the subquery specified by the given type.
	 * 
	 * @return
	 */
	public Set<ATermAppl> getDistVarsForType(final VarType queryType);

	/**
	 * Adds an query atom to the query.
	 * 
	 * @param atom
	 */
	public void add(final QueryAtom atom);

	/**
	 * Adds a distinguished variable to the query with its type - there can be
	 * more variable types to support punning.
	 * 
	 * @param atom
	 */
	public void addDistVar(final ATermAppl a, final VarType type);

	/**
	 * Adds a distinguished variable that appears in the result projection to
	 * the query;
	 * 
	 * @param atom
	 */
	public void addResultVar(final ATermAppl a);

	/**
	 * Return all the variables used in this query.
	 * 
	 * @return Set of variables
	 */
	public Set<ATermAppl> getVars();

	/**
	 * Return all undistinguished variables used in this query.
	 * 
	 * @return Set of variables
	 */
	public Set<ATermAppl> getUndistVars();

	/**
	 * Return individuals and literals used in this query.
	 * 
	 * @return
	 */
	public Set<ATermAppl> getConstants();

	/**
	 * Return all the variables that will be in the results. For SPARQL, these
	 * are the variables in the SELECT clause.
	 * 
	 * @return Set of variables
	 */
	public List<ATermAppl> getResultVars();

	/**
	 * Return all the distinguished variables. These are variables that will be
	 * bound to individuals (or data values) existing in the KB.
	 * 
	 * @return Set of variables
	 */
	public Set<ATermAppl> getDistVars();

	/**
	 * Get all the atoms in the query.
	 * 
	 * @return
	 */
	public List<QueryAtom> getAtoms();

	/**
	 * The KB that will be used to answer this query.
	 * 
	 * @return
	 */
	public KnowledgeBase getKB();

	/**
	 * Sets the KB that will be used to answer this query.
	 * 
	 * @param kb KB that will be used to answer this query
	 */
	public void setKB(KnowledgeBase kb);
	
	/**
	 * Checks whether the query is ground.
	 * 
	 * @return true iff the query is ground
	 */
	public boolean isGround();

	/**
	 * Replace the variables in the query with the values specified in the
	 * binding and return a new query instance (without modifying this query).
	 * 
	 * @param binding
	 * @return
	 */
	public Query apply(ResultBinding binding);

	/**
	 * Rolls up the query to the given variable.
	 * 
	 * @param distVar
	 * @return
	 */
	public ATermAppl rollUpTo(final ATermAppl distVar,
			final Collection<ATermAppl> avoidList, final boolean stopOnConstants);

	/**
	 * Creates a subquery from the given query. Atoms are listed according to
	 * the 'atoms' parameter.
	 * 
	 * @param atoms
	 *            selected atom indices
	 * @return subquery
	 */
	public Query reorder(int[] atoms);

	public void remove(final QueryAtom atom);

	/**
	 * Searches for given atom pattern. This also might be used for different
	 * types of rolling-up, involving various sets of allowed atom types.
	 * 
	 * @return query atoms in the order as they appear in the query
	 */
	public List<QueryAtom> findAtoms(final QueryPredicate predicate,
			final ATermAppl... arguments);
	
	/**
	 * Set the query parameterization
	 * 
	 * @params parameters
	 */
	public void setQueryParameters(QueryParameters parameters);
	
	/**
	 * Get the query parameterization values
	 * 
	 * @return QueryParameters
	 */
	public QueryParameters getQueryParameters();

	/**
	 * Return the name of this query
	 * 
	 * @return name of the query
	 */
	public ATermAppl getName();
	
	/**
	 * Sets the name of this query
	 * @param name name of the query
	 */
	public void setName(ATermAppl name);
}
