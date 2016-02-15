// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.rdfxml;

import java.io.OutputStream;
import java.net.URI;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Harris Lin
 */
public class RDFModelWriter {

	private static final String	ANON_URI	= "#anon";
	private static final URI	TMP_URI		= URI.create( "tag:clarkparsia.com,2008:pellint:tmp" );

	private static String toString(RDFNode v) {
		if( v.isLiteral() ) {
            return ((Literal) v).getLexicalForm();
        } else if( v.isAnon() ) {
            return ANON_URI + v.asNode().getBlankNodeLabel();
        } else {
            return ((Resource) v).getURI();
        }
	}

	public void write(OutputStream out, RDFModel m) {
		Model model = ModelFactory.createDefaultModel();

//		for( Map.Entry<String, String> entry : m.getNamespaces().entrySet() ) {
//			writer.handleNamespace( entry.getKey(), entry.getValue() );
//		}
//
//		for( String comment : m.getComments() ) {
//			writer.handleComment( comment );
//		}

		for( Statement stmt : m.getStatements() ) {
			model.add( stmt );
		}
		
		model.write( out );
	}
}
