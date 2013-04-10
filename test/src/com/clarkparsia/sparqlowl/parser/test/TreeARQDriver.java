// Copyright (c) 2006 - 2009, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.sparqlowl.parser.test;

import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStreamReader;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;

import com.clarkparsia.sparqlowl.parser.antlr.SparqlOwlLexer;
import com.clarkparsia.sparqlowl.parser.antlr.SparqlOwlParser;
import com.clarkparsia.sparqlowl.parser.antlr.SparqlOwlTreeARQ;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

/**
 * <p>
 * Title: TreeARQ Driver
 * </p>
 * <p>
 * Description: Stub driver that reads Terp query on stdin and writes the
 * ARQ friendly version on stdout. Useful to exercise {@link SparqlOwlTreeARQ}.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Mike Smith <a
 *         href="mailto:msmith@clarkparsia.com">msmith@clarkparsia.com</a>
 */
public class TreeARQDriver {

	public static void main(String[] args) throws IOException, RecognitionException {
		SparqlOwlLexer lexer = new SparqlOwlLexer( new ANTLRReaderStream( new InputStreamReader(
				System.in ) ) );
		CommonTokenStream tokenStream = new CommonTokenStream( lexer );
		SparqlOwlParser parser = new SparqlOwlParser( tokenStream );
		SparqlOwlParser.query_return result;
		try {
			result = parser.query();
		} catch( RecognitionException e ) {
			throw new QueryParseException( format( "%s %s", parser.getErrorHeader( e ), parser
					.getErrorMessage( e, parser.getTokenNames() ) ), e.line, e.charPositionInLine );
		}
		CommonTree t = (CommonTree) result.getTree();

		CommonTreeNodeStream nodes = new CommonTreeNodeStream( t );
		nodes.setTokenStream( tokenStream );

		SparqlOwlTreeARQ treeWalker = new SparqlOwlTreeARQ( nodes );
		Query q = treeWalker.query( null );

		if( q.getPrefix( "rdf" ) == null )
			q.setPrefix( "rdf", RDF.getURI() );
		if( q.getPrefix( "rdfs" ) == null )
			q.setPrefix( "rdfs", RDFS.getURI() );
		if( q.getPrefix( "owl" ) == null )
			q.setPrefix( "owl", OWL.getURI() );
		if( q.getPrefix( "xsd" ) == null )
			q.setPrefix( "xsd", XSD.getURI() );

		System.out.print( "\nARQ Query\n---------\n" );
		q.serialize( System.out );
	}
}
