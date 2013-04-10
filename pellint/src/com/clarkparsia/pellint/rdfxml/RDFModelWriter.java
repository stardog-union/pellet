// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.rdfxml;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.coode.owl.rdfxml.parser.AnonymousNodeChecker;
import org.coode.owl.rdfxml.parser.OWLRDFConsumer;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.io.StreamOutputTarget;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLOntologyStorageException;
import org.semanticweb.owl.model.UnknownOWLOntologyException;
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

	private static OWLOntology convert(RDFModel model, OWLOntologyManager manager)
			throws OWLOntologyCreationException, SAXException {
		OWLOntology ontology = manager.createOntology( TMP_URI );

		OWLRDFConsumer consumer = new OWLRDFConsumer( manager, ontology,
				new AnonymousNodeChecker() {
					public boolean isAnonymousNode(URI uri) {
						return isAnonymousNode( uri.toString() );
					}

					public boolean isAnonymousNode(String uri) {
						return uri.startsWith( ANON_URI );
					}
				} );

		consumer.startModel( "" );

		for( Statement stmt : model.getStatements() ) {
			String subj = toString( stmt.getSubject() );
			String pred = toString( stmt.getPredicate() );
			RDFNode vObj = stmt.getObject();
			String obj = toString( vObj );

			if( vObj instanceof Literal ) {
				Literal literal = (Literal) vObj;

				String datatypeURI = literal.getDatatypeURI();
				String lang = literal.getLanguage();
				
				if( lang != null && lang.length() == 0 )
					lang = null;

				consumer.statementWithLiteralValue( subj, pred, obj, lang, datatypeURI );
			}
			else {
				consumer.statementWithResourceValue( subj, pred, obj );
			}
		}

		consumer.endModel();

		return ontology;
	}

	public void writePretty(OutputStream out, RDFModel model) throws IOException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = null;
		try {
			ontology = convert( model, manager );
		} catch( OWLOntologyCreationException e ) {
			throw new RuntimeException( e );
		} catch( SAXException e ) {
			throw new RuntimeException( e );
		}

		try {
			manager.saveOntology( ontology, new StreamOutputTarget( out ) );
		} catch( UnknownOWLOntologyException e ) {
			throw new RuntimeException( e );
		} catch( OWLOntologyStorageException e ) {
			throw new IOException( e.getMessage() );
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
