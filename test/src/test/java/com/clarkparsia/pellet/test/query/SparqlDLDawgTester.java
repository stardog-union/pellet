// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.query;

import com.clarkparsia.pellet.sparqldl.engine.QueryEngine;
import com.clarkparsia.pellet.sparqldl.jena.JenaIOUtils;
import com.clarkparsia.pellet.sparqldl.jena.SparqlDLResultSet;
import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.model.QueryResult;
import com.clarkparsia.pellet.sparqldl.model.ResultBinding;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import openllet.aterm.ATermAppl;
import openllet.shared.tools.Log;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.binding.BindingBase;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.utils.PermutationGenerator;
import org.mindswap.pellet.utils.Timer;

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
 * @author Petr Kremen
 */
public class SparqlDLDawgTester implements SparqlDawgTester
{

	private static final Logger _logger = Log.getLogger(SparqlDLDawgTester.class);

	private String _queryURI = "";

	private Set<String> _graphURIs = new HashSet<>();

	private Set<String> _namedGraphURIs = new HashSet<>();

	private OntModel _model = null;

	private Query _query = null;

	private String _resultURI = null;

	private final boolean _allOrderings;

	private final boolean _writeResults = true;

	private boolean _noCheck;

	public SparqlDLDawgTester(final boolean allOrderings, final boolean noCheck)
	{
		this._allOrderings = allOrderings;
		this._noCheck = noCheck;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDatasetURIs(final Set<String> graphURIs, final Set<String> namedGraphURIs)
	{
		if (this._graphURIs.equals(graphURIs) && this._namedGraphURIs.equals(namedGraphURIs))
			return;

		this._graphURIs = graphURIs;
		this._namedGraphURIs = namedGraphURIs;

		_model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

		for (final String dataURI : graphURIs)
			_model.read(dataURI, null, JenaIOUtils.fileType(dataURI).jenaName());

		_model.prepare();

		//		((PelletInfGraph) _model.getGraph()).classify();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setQueryURI(final String queryURI)
	{
		if (this._queryURI.equals(queryURI))
			return;

		this._queryURI = queryURI;
		final org.apache.jena.query.Query query = QueryFactory.read(queryURI);

		this._query = QueryEngine.getParser().parse(query.toString(Syntax.syntaxSPARQL), ((PelletInfGraph) _model.getGraph()).getKB());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setResult(final String resultURI)
	{
		this._resultURI = resultURI;
		if (resultURI == null)
			_noCheck = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isParsable()
	{
		try
		{
			QueryEngine.getParser().parse(new FileInputStream(_queryURI.substring(5)), new KnowledgeBase());

			return true;
		}
		catch (final Exception e)
		{
			_logger.log(Level.INFO, e.getMessage(), e);
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCorrectlyEvaluated()
	{
		try
		{
			boolean ok = true;

			if (_query.getDistVars().isEmpty())
			{
				Boolean expected = null;
				if (!_noCheck)
				{
					expected = JenaIOUtils.parseAskResult(_resultURI);

					if (_logger.isLoggable(Level.INFO))
						_logger.info("Expected=" + expected);
				}

				if (_allOrderings)
				{
					final PermutationGenerator g = new PermutationGenerator(_query.getAtoms().size());

					while (g.hasMore())
						ok &= runSingleAskTest(_query.reorder(g.getNext()), expected);
				}
				else
					ok = runSingleAskTest(_query, expected);

				return ok;
			}
			else
			{
				ResultSetRewindable expected = null;
				if (!_noCheck)
				{
					expected = ResultSetFactory.makeRewindable(JenaIOUtils.parseResultSet(_resultURI));

					final List<?> expectedList = ResultSetFormatter.toList(expected);
					if (expected.size() > 10)
					{
						if (_logger.isLoggable(Level.INFO))
							_logger.log(Level.INFO, "Expected=" + expectedList.subList(0, 9) + " ... " + expectedList.size());
					}
					else
						if (_logger.isLoggable(Level.INFO))
							_logger.info("Expected=" + expectedList);
				}

				if (_allOrderings)
				{
					final PermutationGenerator g = new PermutationGenerator(_query.getAtoms().size());

					while (g.hasMore())
						ok &= runSingleSelectTest(_query.reorder(g.getNext()), expected);
				}
				else
					ok = runSingleSelectTest(_query, expected);

				return ok;
			}
		}
		catch (final IOException e)
		{
			_logger.log(Level.SEVERE, e.getMessage(), e);
			return false;
		}
	}

	private QueryResult runSingleTest(final Query query)
	{
		final Timer t = new Timer("Single _query execution");

		t.start();
		final QueryResult bindings = QueryEngine.exec(query);
		_logger.info("Execution time=" + t.getElapsed());
		t.stop();
		_logger.info("Result size = " + bindings.size());

		return bindings;
	}

	private final boolean runSingleAskTest(final Query query, final Boolean expected)
	{
		final QueryResult bindings = runSingleTest(query);

		boolean ok = true;

		if (!_noCheck)
		{
			final Boolean real = !bindings.isEmpty();

			_logger.log(Level.INFO, "real=" + real + ", exp=" + expected);
			ok = real.equals(expected);
		}

		return ok;
	}

	private final boolean runSingleSelectTest(final Query query, final ResultSetRewindable expected)
	{
		final QueryResult bindings = runSingleTest(query);

		boolean ok = true;

		if (!_noCheck)
		{
			final ResultSetRewindable real = realResultsHandler(bindings);

			real.reset();
			expected.reset();
			ok &= ResultSetUtils.assertEquals(real, expected);

			if (_writeResults)
			{
				real.reset();
				expected.reset();
				// final ResultSetRewindable rMinusE = ResultSetFactory
				// .makeRewindable(ResultSetFactory.copyResults(real));
				// final ResultSetRewindable eMinusR = ResultSetFactory
				// .makeRewindable(ResultSetFactory.copyResults(_expected));

				// real.reset();
				// final Model realModel = ResultSetFormatter.toModel(real);
				// _expected.reset();
				// final Model expectedModel = ResultSetFormatter
				// .toModel(_expected);

				try
				{
					real.reset();
					ResultSetFormatter.out(new FileOutputStream("real"), real);

					ResultSetFormatter.out(new FileOutputStream("real-_expected"), new DifferenceResultSet(real, expected));
					ResultSetFormatter.out(new FileOutputStream("_expected-real"), new DifferenceResultSet(expected, real));

					// final Set<ResultBinding> rMinusE = SetUtils.difference(
					// new HashSet<ResultBinding>(realList),
					// new HashSet<ResultBinding>(expectedList));
					//
					// final FileWriter fwre = new FileWriter("real-_expected");
					// _writeResults(resultVars,
					// (Collection<ResultBinding>) rMinusE, fwre);
					//
					// final FileWriter fwer = new FileWriter("_expected-real");
					// final Set<ResultBinding> eMinusR = SetUtils.difference(
					// new HashSet<ResultBinding>(expectedList),
					// new HashSet<ResultBinding>(realList));
					//
					// _writeResults(resultVars,
					// (Collection<ResultBinding>) eMinusR, fwer);

				}
				catch (final FileNotFoundException e)
				{
					_logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}

		return ok;
	}

	@SuppressWarnings("unused")
	private void writeResults(final List<ATermAppl> resultVars, final Collection<ResultBinding> bindingCollection, final FileWriter fwre) throws IOException
	{
		for (final ATermAppl var : resultVars)
			fwre.write(var.getName() + "\t");
		for (final ResultBinding b : bindingCollection)
		{
			for (final ATermAppl var : resultVars)
				fwre.write(b.getValue(var) + "\t");
			fwre.write("\n");
		}
	}

	private final ResultSetRewindable realResultsHandler(final QueryResult bindings)
	{
		final ResultSetRewindable real = ResultSetFactory.makeRewindable(new SparqlDLResultSet(bindings, _model.getRawModel()));

		final List<?> realList = ResultSetFormatter.toList(real);
		if (realList.size() > 10)
		{
			if (_logger.isLoggable(Level.INFO))
				_logger.log(Level.INFO, "Real=" + realList.subList(0, 9) + " ... " + realList.size());
		}
		else
			if (_logger.isLoggable(Level.INFO))
				_logger.info("Real=" + realList);
		real.reset();

		return real;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isApplicable(final String uri)
	{
		return !uri.startsWith("http://www.w3.org/2001/sw/DataAccess/tests/data-r2/syntax-sparql1/manifest#") && !uri.startsWith("http://www.w3.org/2001/sw/DataAccess/tests/data-r2/syntax-sparql2/manifest#") && !uri.startsWith("http://www.w3.org/2001/sw/DataAccess/tests/data-r2/syntax-sparql3/manifest#") && !uri.startsWith("http://www.w3.org/2001/sw/DataAccess/tests/data-r2/syntax-sparql4/manifest#");
	}

	private static class DifferenceResultSet implements ResultSet
	{

		private final List<Binding> solutions = new ArrayList<>();

		private final List<String> vars;

		private int index;

		public DifferenceResultSet(final ResultSet rs1, final ResultSet rs2)
		{
			vars = rs1.getResultVars();

			index = 0;

			final ResultSetRewindable real = ResultSetFactory.makeRewindable(rs1);
			final ResultSetRewindable expected = ResultSetFactory.makeRewindable(rs2);

			real.reset();
			while (real.hasNext())
			{
				final Binding b1 = real.nextBinding();
				expected.reset();
				boolean toAdd = true;
				while (expected.hasNext())
				{
					final Binding b2 = expected.nextBinding();
					if (BindingBase.equals(b1, b2))
					{
						toAdd = false;
						break;
					}
				}

				if (toAdd)
					solutions.add(b1);
			}
		}

		@Override
		public List<String> getResultVars()
		{
			return vars;
		}

		@Override
		public int getRowNumber()
		{
			return index;
		}

		@Override
		public boolean hasNext()
		{
			return index < solutions.size();
		}

		@Override
		public QuerySolution next()
		{
			throw new UnsupportedOperationException("Next is not supported.");
		}

		@Override
		public Binding nextBinding()
		{
			return solutions.get(index++);
		}

		@Override
		public QuerySolution nextSolution()
		{
			throw new UnsupportedOperationException("Next solution is not supported.");
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException("Removal is not supported.");
		}

		@Override
		public Model getResourceModel()
		{
			return null;
		}
	}

	@Override
	public String getName()
	{
		return "SparqlDLDawgTester";
	}
}
