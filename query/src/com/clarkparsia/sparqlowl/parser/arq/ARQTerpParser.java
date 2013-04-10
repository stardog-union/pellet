// Copyright (c) 2010, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
package com.clarkparsia.sparqlowl.parser.arq;

import static java.lang.String.format;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;

import com.clarkparsia.sparqlowl.parser.antlr.SparqlOwlLexer;
import com.clarkparsia.sparqlowl.parser.antlr.SparqlOwlParser;
import com.clarkparsia.sparqlowl.parser.antlr.SparqlOwlTreeARQ;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.sparql.lang.Parser;
import com.hp.hpl.jena.sparql.lang.ParserFactory;
import com.hp.hpl.jena.sparql.lang.ParserRegistry;

/**
 * <p>
 * Title: ARQ Terp parser
 * </p>
 * <p>
 * Description: ARQ parser (and static parser factory) to integrate the
 * Terp parser with the ARQ & Jena query tools
 * </p>
 * <p>
 * Copyright: Copyright (c) 2010
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Mike Smith <a
 *         href="mailto:msmith@clarkparsia.com">msmith@clarkparsia.com</a>
 */
public class ARQTerpParser extends Parser {

	private final static ParserFactory	FACTORY;
	static {
		FACTORY = new ParserFactory() {

			public boolean accept(Syntax syntax) {
				return (TerpSyntax.getInstance() == syntax);
			}

			public Parser create(Syntax syntax) {
				return new ARQTerpParser();
			}
		};
	}

	/**
	 * Get the singleton ParserFactory
	 * 
	 * @return a singleton <code>ParserFactory</code>
	 */
	public static ParserFactory getFactory() {
		return FACTORY;
	}

	/**
	 * Register a {@link ParserFactory} for the {@link TerpSyntax}
	 */
	public static void registerFactory() {
		ParserRegistry.addFactory( TerpSyntax.getInstance(), FACTORY );
	}
	
	/**
	 * Unregister a {@link ParserFactory} for the {@link TerpSyntax}
	 */
	public static void unregisterFactory() {
		ParserRegistry.removeFactory( TerpSyntax.getInstance() );
	}

	@Override
	public Query parse(Query query, String queryString) throws QueryParseException {

		if( query == null )
			throw new NullPointerException();

		query.setSyntax( TerpSyntax.getInstance() );

		final SparqlOwlLexer lexer = new SparqlOwlLexer( new ANTLRStringStream( queryString ) );
		final CommonTokenStream tokenStream = new CommonTokenStream( lexer );
		final SparqlOwlParser parser = new SparqlOwlParser( tokenStream );
		SparqlOwlParser.query_return result;
		try {
			result = parser.query();
		} catch( RecognitionException e ) {
			throw new QueryParseException( format( "%s %s", parser.getErrorHeader( e ), parser
					.getErrorMessage( e, parser.getTokenNames() ) ), e.line, e.charPositionInLine );
		}
		final CommonTree t = (CommonTree) result.getTree();

		final CommonTreeNodeStream nodes = new CommonTreeNodeStream( t );
		nodes.setTokenStream( tokenStream );

		final SparqlOwlTreeARQ treeWalker = new SparqlOwlTreeARQ( nodes );

		try {
			treeWalker.query( query );
		} catch( RecognitionException e ) {
			throw new QueryParseException( format( "%s %s", treeWalker.getErrorHeader( e ),
					treeWalker.getErrorMessage( e, parser.getTokenNames() ) ), e.line,
					e.charPositionInLine );
		}

		return query;
	}

}
