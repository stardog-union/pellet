package com.clarkparsia.sparqlowl.parser.test;

import java.io.IOException;
import java.io.InputStreamReader;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;

import com.clarkparsia.sparqlowl.parser.antlr.SparqlOwlLexer;
import com.clarkparsia.sparqlowl.parser.antlr.SparqlOwlParser;

/**
 * <p>
 * Title: Parser Driver
 * </p>
 * <p>
 * Description: Stub driver that reads Terp query on stdin and writes the
 * AST on stdout. Useful to exercise {@link SparqlOwlParser}.
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
public class ParserDriver {

	public static void main(String[] args) throws IOException, RecognitionException {
		SparqlOwlLexer lexer = new SparqlOwlLexer( new ANTLRReaderStream( new InputStreamReader(
				System.in ) ) );
		CommonTokenStream tokenStream = new CommonTokenStream( lexer );
		SparqlOwlParser parser = new SparqlOwlParser( tokenStream );
		SparqlOwlParser.query_return result = parser.query();
		Tree t = (Tree) result.getTree();
		System.out.println( t.toStringTree() );
	}
}
