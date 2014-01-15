// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.query;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.utils.PermutationGenerator;
import org.mindswap.pellet.utils.Timer;

import aterm.ATermAppl;

import com.clarkparsia.pellet.sparqldl.engine.QueryEngine;
import com.clarkparsia.pellet.sparqldl.jena.JenaIOUtils;
import com.clarkparsia.pellet.sparqldl.jena.SparqlDLResultSet;
import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.model.QueryResult;
import com.clarkparsia.pellet.sparqldl.model.ResultBinding;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingBase;

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
public class SparqlDLDawgTester implements SparqlDawgTester {

	private static final Logger	log				= Logger.getLogger( SparqlDLDawgTester.class
														.getName() );

	private String				queryURI		= "";

	private Set<String>			graphURIs		= new HashSet<String>();

	private Set<String>			namedGraphURIs	= new HashSet<String>();

	private OntModel 			model			= null;
	
	private Query				query			= null;

	private String				resultURI		= null;

	private boolean				allOrderings;

	private boolean				writeResults	= true;

	private boolean				noCheck;

	public SparqlDLDawgTester(final boolean allOrderings, final boolean noCheck) {
		this.allOrderings = allOrderings;
		this.noCheck = noCheck;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDatasetURIs(Set<String> graphURIs, Set<String> namedGraphURIs) {
		if( this.graphURIs.equals( graphURIs ) && this.namedGraphURIs.equals( namedGraphURIs ) ) {
			return;
		}

		this.graphURIs = graphURIs;
		this.namedGraphURIs = namedGraphURIs;

		model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );

		for( String dataURI : graphURIs ) {
			model.read( dataURI, null, JenaIOUtils.fileType( dataURI ).jenaName() );
		}

		model.prepare();

//		((PelletInfGraph) model.getGraph()).classify();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setQueryURI(String queryURI) {
		if( this.queryURI.equals( queryURI ) ) {
			return;
		}

		this.queryURI = queryURI;
		final com.hp.hpl.jena.query.Query query = QueryFactory.read( queryURI );

		this.query = QueryEngine.getParser().parse( query.toString( Syntax.syntaxSPARQL ),
				((PelletInfGraph) model.getGraph()).getKB() );

	}

	/**
	 * {@inheritDoc}
	 */
	public void setResult(String resultURI) {
		this.resultURI = resultURI;
		if( resultURI == null ) {
			noCheck = true;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isParsable() {
		try {
			QueryEngine.getParser().parse( new FileInputStream( queryURI.substring( 5 ) ),
					new KnowledgeBase() );

			return true;
		} catch( Exception e ) {
			log.log( Level.INFO, e.getMessage(), e );
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isCorrectlyEvaluated() {
		try {
			boolean ok = true;

			if( query.getDistVars().isEmpty() ) {
				Boolean expected = null;
				if( !noCheck ) {
					expected = JenaIOUtils.parseAskResult( resultURI );

					if( log.isLoggable( Level.INFO ) ) {
						log.info( "Expected=" + expected );
					}
				}

				if( allOrderings ) {
					final PermutationGenerator g = new PermutationGenerator( query.getAtoms()
							.size() );

					while( g.hasMore() ) {
						ok &= runSingleAskTest( query.reorder( g.getNext() ), expected );
					}
				}
				else {
					ok = runSingleAskTest( query, expected );
				}

				return ok;
			}
			else {
				ResultSetRewindable expected = null;
				if( !noCheck ) {
					expected = ResultSetFactory.makeRewindable( JenaIOUtils
							.parseResultSet( resultURI ) );

					final List<?> expectedList = ResultSetFormatter.toList( expected );
					if( expected.size() > 10 ) {
						if( log.isLoggable( Level.INFO ) ) {
							log.log( Level.INFO, "Expected=" + expectedList.subList( 0, 9 )
									+ " ... " + expectedList.size() );
						}
					}
					else {
						if( log.isLoggable( Level.INFO ) ) {
							log.info( "Expected=" + expectedList );
						}
					}
				}

				if( allOrderings ) {
					final PermutationGenerator g = new PermutationGenerator( query.getAtoms()
							.size() );

					while( g.hasMore() ) {
						ok &= runSingleSelectTest( query.reorder( g.getNext() ), expected );
					}
				}
				else {
					ok = runSingleSelectTest( query, expected );
				}

				return ok;
			}
		} catch( IOException e ) {
			log.log( Level.SEVERE, e.getMessage(), e );
			return false;
		}
	}

	private QueryResult runSingleTest(final Query query) {
		final Timer t = new Timer( "Single query execution" );

		t.start();
		final QueryResult bindings = QueryEngine.exec( query );
		log.info( "Execution time=" + t.getElapsed() );
		t.stop();
		log.info( "Result size = " + bindings.size() );

		return bindings;
	}

	private final boolean runSingleAskTest(final Query query, final Boolean expected) {
		final QueryResult bindings = runSingleTest( query );

		boolean ok = true;

		if( !noCheck ) {
			final Boolean real = !bindings.isEmpty();

			log.log( Level.INFO, "real=" + real + ", exp=" + expected );
			ok = ( real == null && expected == null) 
				|| (real != null && real.equals( expected ) );
		}

		return ok;
	}

	private final boolean runSingleSelectTest(final Query query, final ResultSetRewindable expected) {
		final QueryResult bindings = runSingleTest( query );

		boolean ok = true;

		if( !noCheck ) {
			final ResultSetRewindable real = realResultsHandler( bindings );

			real.reset();
			expected.reset();
			ok &= ResultSetUtils.assertEquals( real, expected );

			if( writeResults ) {
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

				try {
					real.reset();
					ResultSetFormatter.out( new FileOutputStream( "real" ), real );

					ResultSetFormatter.out( new FileOutputStream( "real-expected" ),
							new DifferenceResultSet( real, expected ) );
					ResultSetFormatter.out( new FileOutputStream( "expected-real" ),
							new DifferenceResultSet( expected, real ) );

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

				} catch( FileNotFoundException e ) {
					log.log( Level.SEVERE, e.getMessage(), e );
				}
			}
		}

		return ok;
	}

	@SuppressWarnings("unused")
	private void writeResults(final List<ATermAppl> resultVars,
			final Collection<ResultBinding> bindingCollection, final FileWriter fwre)
			throws IOException {
		for( final ATermAppl var : resultVars ) {
			fwre.write( var.getName() + "\t" );
		}
		for( final Iterator<ResultBinding> i = bindingCollection.iterator(); i.hasNext(); ) {
			final ResultBinding b = i.next();
			for( final ATermAppl var : resultVars ) {
				fwre.write( b.getValue( var ) + "\t" );
			}
			fwre.write( "\n" );
		}
	}

	private final ResultSetRewindable realResultsHandler(QueryResult bindings) {
		final ResultSetRewindable real = ResultSetFactory.makeRewindable( new SparqlDLResultSet(
				bindings, model.getRawModel() ) );

		final List<?> realList = ResultSetFormatter.toList( real );
		if( realList.size() > 10 ) {
			if( log.isLoggable( Level.INFO ) ) {
				log
						.log( Level.INFO, "Real=" + realList.subList( 0, 9 ) + " ... "
								+ realList.size() );
			}
		}
		else {
			if( log.isLoggable( Level.INFO ) ) {
				log.info( "Real=" + realList );
			}
		}
		real.reset();

		return real;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isApplicable(String uri) {
		return !uri
				.startsWith( "http://www.w3.org/2001/sw/DataAccess/tests/data-r2/syntax-sparql1/manifest#" )
				&& !uri
						.startsWith( "http://www.w3.org/2001/sw/DataAccess/tests/data-r2/syntax-sparql2/manifest#" )
				&& !uri
						.startsWith( "http://www.w3.org/2001/sw/DataAccess/tests/data-r2/syntax-sparql3/manifest#" )
				&& !uri
						.startsWith( "http://www.w3.org/2001/sw/DataAccess/tests/data-r2/syntax-sparql4/manifest#" );
	}

	private static class DifferenceResultSet implements ResultSet {

		private final List<Binding>	solutions	= new ArrayList<Binding>();

		private final List<String>	vars;

		private int					index;

		@SuppressWarnings("unchecked")
		public DifferenceResultSet(final ResultSet rs1, final ResultSet rs2) {
			vars = rs1.getResultVars();

			index = 0;

			final ResultSetRewindable real = ResultSetFactory.makeRewindable( rs1 );
			final ResultSetRewindable expected = ResultSetFactory.makeRewindable( rs2 );

			real.reset();
			while( real.hasNext() ) {
				final Binding b1 = real.nextBinding();
				expected.reset();
				boolean toAdd = true;
				while( expected.hasNext() ) {
					final Binding b2 = expected.nextBinding();
					if( BindingBase.equals( b1, b2 ) ) {
						toAdd = false;
						break;
					}
				}

				if( toAdd ) {
					solutions.add( b1 );
				}
			}
		}

		public List<String> getResultVars() {
			return vars;
		}

		public int getRowNumber() {
			return index;
		}

		public boolean hasNext() {
			return index < solutions.size();
		}

		public QuerySolution next() {
			throw new UnsupportedOperationException( "Next is not supported." );
		}

		public Binding nextBinding() {
			return solutions.get( index++ );
		}

		public QuerySolution nextSolution() {
			throw new UnsupportedOperationException( "Next solution is not supported." );
		}

		public void remove() {
			throw new UnsupportedOperationException( "Removal is not supported." );
		}
		
		public Model getResourceModel() {
			return null;
		}
	}

	public String getName() {
		return "SparqlDLDawgTester";
	}
}
