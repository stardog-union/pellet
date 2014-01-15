// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.rdfxml;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.coode.owlapi.rdfxml.parser.AnonymousNodeChecker;
import org.coode.owlapi.rdfxml.parser.OWLRDFConsumer;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

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
		if( v.isLiteral() )
			return ((Literal) v).getLexicalForm();
		else if( v.isAnon() )
			return ANON_URI + v.asNode().getBlankNodeLabel();
		else
			return ((Resource) v).getURI();
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
