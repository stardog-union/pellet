// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.query;

import aterm.ATermAppl;
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

	private static final Logger log = Logger.getLogger(SparqlDLDawgTester.class.getName());

	private String queryURI = "";

	private Set<String> graphURIs = new HashSet<>();

	private Set<String> namedGraphURIs = new HashSet<>();

	private OntModel model = null;

	private Query query = null;

	private String resultURI = null;

	private final boolean allOrderings;

	private final boolean writeResults = true;

	private boolean noCheck;

	public SparqlDLDawgTester(final boolean allOrderings, final boolean noCheck)
	{
		this.allOrderings = allOrderings;
		this.noCheck = noCheck;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDatasetURIs(final Set<String> graphURIs, final Set<String> namedGraphURIs)
	{
		if (this.graphURIs.equals(graphURIs) && this.namedGraphURIs.equals(namedGraphURIs))
			return;

		this.graphURIs = graphURIs;
		this.namedGraphURIs = namedGraphURIs;

		model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

		for (final String dataURI : graphURIs)
			model.read(dataURI, null, JenaIOUtils.fileType(dataURI).jenaName());

		model.prepare();

		//		((PelletInfGraph) model.getGraph()).classify();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setQueryURI(final String queryURI)
	{
		if (this.queryURI.equals(queryURI))
			return;

		this.queryURI = queryURI;
		final org.apache.jena.query.Query query = QueryFactory.read(queryURI);

		this.query = QueryEngine.getParser().parse(query.toString(Syntax.syntaxSPARQL), ((PelletInfGraph) model.getGraph()).getKB());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setResult(final String resultURI)
	{
		this.resultURI = resultURI;
		if (resultURI == null)
			noCheck = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isParsable()
	{
		try
		{
			QueryEngine.getParser().parse(new FileInputStream(queryURI.substring(5)), new KnowledgeBase());

			return true;
		}
		catch (final Exception e)
		{
			log.log(Level.INFO, e.getMessage(), e);
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

			if (query.getDistVars().isEmpty())
			{
				Boolean expected = null;
				if (!noCheck)
				{
					expected = JenaIOUtils.parseAskResult(resultURI);

					if (log.isLoggable(Level.INFO))
						log.info("Expected=" + expected);
				}

				if (allOrderings)
				{
					final PermutationGenerator g = new PermutationGenerator(query.getAtoms().size());

					while (g.hasMore())
						ok &= runSingleAskTest(query.reorder(g.getNext()), expected);
				}
				else
					ok = runSingleAskTest(query, expected);

				return ok;
			}
			else
			{
				ResultSetRewindable expected = null;
				if (!noCheck)
				{
					expected = ResultSetFactory.makeRewindable(JenaIOUtils.parseResultSet(resultURI));

					final List<?> expectedList = ResultSetFormatter.toList(expected);
					if (expected.size() > 10)
					{
						if (log.isLoggable(Level.INFO))
							log.log(Level.INFO, "Expected=" + expectedList.subList(0, 9) + " ... " + expectedList.size());
					}
					else
						if (log.isLoggable(Level.INFO))
							log.info("Expected=" + expectedList);
				}

				if (allOrderings)
				{
					final PermutationGenerator g = new PermutationGenerator(query.getAtoms().size());

					while (g.hasMore())
						ok &= runSingleSelectTest(query.reorder(g.getNext()), expected);
				}
				else
					ok = runSingleSelectTest(query, expected);

				return ok;
			}
		}
		catch (final IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);
			return false;
		}
	}

	private QueryResult runSingleTest(final Query query)
	{
		final Timer t = new Timer("Single query execution");

		t.start();
		final QueryResult bindings = QueryEngine.exec(query);
		log.info("Execution time=" + t.getElapsed());
		t.stop();
		log.info("Result size = " + bindings.size());

		return bindings;
	}

	private final boolean runSingleAskTest(final Query query, final Boolean expected)
	{
		final QueryResult bindings = runSingleTest(query);

		boolean ok = true;

		if (!noCheck)
		{
			final Boolean real = !bindings.isEmpty();

			log.log(Level.INFO, "real=" + real + ", exp=" + expected);
			ok = (real == null && expected == null) || (real != null && real.equals(expected));
		}

		return ok;
	}

	private final boolean runSingleSelectTest(final Query query, final ResultSetRewindable expected)
	{
		final QueryResult bindings = runSingleTest(query);

		boolean ok = true;

		if (!noCheck)
		{
			final ResultSetRewindable real = realResultsHandler(bindings);

			real.reset();
			expected.reset();
			ok &= ResultSetUtils.assertEquals(real, expected);

			if (writeResults)
			{
				real.reset();
				expected.reset();
				// final ResultSetRewindable rMinusE = ResultSetFactory
				// .makeRewindable(ResultSetFactory.copyResults(real));
				// final ResultSetRewindable eMinusR = ResultSetFactory
				// .makeRewindable(ResultSetFactory.copyResults(expected));

				// real.reset();
				// final Model realModel = ResultSetFormatter.toModel(real);
				// expected.reset();
				// final Model expectedModel = ResultSetFormatter
				// .toModel(expected);

				try
				{
					real.reset();
					ResultSetFormatter.out(new FileOutputStream("real"), real);

					ResultSetFormatter.out(new FileOutputStream("real-expected"), new DifferenceResultSet(real, expected));
					ResultSetFormatter.out(new FileOutputStream("expected-real"), new DifferenceResultSet(expected, real));

					// final Set<ResultBinding> rMinusE = SetUtils.difference(
					// new HashSet<ResultBinding>(realList),
					// new HashSet<ResultBinding>(expectedList));
					//
					// final FileWriter fwre = new FileWriter("real-expected");
					// writeResults(resultVars,
					// (Collection<ResultBinding>) rMinusE, fwre);
					//
					// final FileWriter fwer = new FileWriter("expected-real");
					// final Set<ResultBinding> eMinusR = SetUtils.difference(
					// new HashSet<ResultBinding>(expectedList),
					// new HashSet<ResultBinding>(realList));
					//
					// writeResults(resultVars,
					// (Collection<ResultBinding>) eMinusR, fwer);

				}
				catch (final FileNotFoundException e)
				{
					log.log(Level.SEVERE, e.getMessage(), e);
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
		final ResultSetRewindable real = ResultSetFactory.makeRewindable(new SparqlDLResultSet(bindings, model.getRawModel()));

		final List<?> realList = ResultSetFormatter.toList(real);
		if (realList.size() > 10)
		{
			if (log.isLoggable(Level.INFO))
				log.log(Level.INFO, "Real=" + realList.subList(0, 9) + " ... " + realList.size());
		}
		else
			if (log.isLoggable(Level.INFO))
				log.info("Real=" + realList);
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

		@SuppressWarnings("unchecked")
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
