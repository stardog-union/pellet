// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.jena;

import aterm.ATermAppl;
import com.clarkparsia.pellet.sparqldl.model.QueryParameters;
import com.clarkparsia.pellet.sparqldl.model.QueryResult;
import com.clarkparsia.pellet.sparqldl.model.ResultBinding;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.apache.jena.graph.Node;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.binding.BindingHashMap;
import org.apache.jena.sparql.engine.binding.BindingMap;
import org.mindswap.pellet.jena.JenaUtils;

/**
 * <p>
 * Title: ResultSet wrapper for SPARQL-DL results.
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
 * @author Evren Sirin
 */
public class SparqlDLResultSet implements ResultSetRewindable
{
	private final Model model;

	private final List<ATermAppl> resultVars;

	private final List<String> resultVarsString;

	private final QueryResult queryResult;

	private int index;

	private Iterator<ResultBinding> bindings;

	private final Binding parent;

	private QueryParameters parameters;

	public SparqlDLResultSet(final QueryResult answers, final Model model)
	{
		this(answers, model, null, null);
	}

	public SparqlDLResultSet(final QueryResult answers, final Model model, final Binding parent)
	{
		this(answers, model, parent, null);
	}

	public SparqlDLResultSet(final QueryResult answers, final Model model, final QueryParameters parameters)
	{
		this(answers, model, null, parameters);
	}

	public SparqlDLResultSet(final QueryResult answers, final Model model, final Binding parent, final QueryParameters parameters)
	{
		this.parent = parent;
		this.queryResult = answers;
		this.model = model;
		this.parameters = parameters;
		this.index = 0;
		this.bindings = answers.iterator();

		resultVars = new ArrayList<>();
		resultVarsString = new ArrayList<>();

		for (final ATermAppl var : queryResult.getResultVars())
		{
			resultVars.add(var);
			resultVarsString.add(getVarName(var));
		}

		// Ensure initial bindings is not a null pointer
		if (parameters == null)
			this.parameters = new QueryParameters();
	}

	protected String getVarName(final ATermAppl term)
	{
		return ((ATermAppl) term.getArgument(0)).getName();
	}

	/**
	 * Return the underlying QueryResults object
	 *
	 * @return
	 */
	public QueryResult getQueryResult()
	{
		return queryResult;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext()
	{
		return bindings.hasNext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Binding nextBinding()
	{
		index++;
		final ResultBinding binding = bindings.next();

		final BindingMap result = parent == null ? new BindingHashMap() : new BindingHashMap(parent);

		for (final ATermAppl var : resultVars)
			if (binding.isBound(var))
			{
				final String varName = getVarName(var);

				final ATermAppl value = binding.getValue(var);

				if (value == null)
					continue;

				final Node node = JenaUtils.makeGraphNode(value);
				result.add(Var.alloc(varName), node);
			}

		if (resultVars.size() == 0)
			for (final Entry<ATermAppl, ATermAppl> entry : parameters.entrySet())
			{
				final ATermAppl term = entry.getKey();

				final String varName = getVarName(term);
				final Var var = Var.alloc(varName);

				if (!result.contains(var))
					result.add(var, JenaUtils.makeGraphNode(entry.getValue()));
			}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QuerySolution nextSolution()
	{
		return new org.apache.jena.sparql.core.ResultBinding(model, nextBinding());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QuerySolution next()
	{
		return nextSolution();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDistinct()
	{
		return queryResult.isDistinct();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isOrdered()
	{
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getRowNumber()
	{
		return index;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getResultVars()
	{
		return Collections.unmodifiableList(resultVarsString);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove() throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("Remove not supported");
	}

	@Override
	public String toString()
	{
		return queryResult.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset()
	{
		index = 0;
		bindings = queryResult.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size()
	{
		return queryResult.size();
	}

	@Override
	public Model getResourceModel()
	{
		return model;
	}
}
