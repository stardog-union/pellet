// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.classification;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.ReasonerVocabulary;

public class JenaClassificationTest extends AbstractClassificationTest {
	public void testClassification(String inputOnt, String classifiedOnt) {
		OntModel premise = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		premise.read( inputOnt );
		premise.prepare();

		Model conclusion = ModelFactory.createDefaultModel();
		conclusion.read( classifiedOnt );

		StmtIterator stmtIter = conclusion.listStatements();

		List<String> nonEntailments = new ArrayList<String>();
		while( stmtIter.hasNext() ) {
			Statement stmt = stmtIter.nextStatement();

			boolean entailed = true;
			if( stmt.getPredicate().equals( RDFS.subClassOf ) )
				entailed = premise.contains( stmt.getSubject(),
						ReasonerVocabulary.directSubClassOf, stmt.getObject() );
			else if( stmt.getPredicate().equals( OWL.equivalentClass ) )
				entailed = premise.contains( stmt );
			
			if( !entailed ) {				
				if( AbstractClassificationTest.FAIL_AT_FIRST_ERROR )
					fail( "Not entailed: " + format( stmt ) );
				else
					nonEntailments.add( format( stmt )  );
			}
		}
		
		assertTrue( nonEntailments.toString(), nonEntailments.isEmpty() );
	}
	
	private static String format(Statement stmt) {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append( '[' );
			sb.append( stmt.getSubject().getLocalName() );
			sb.append( ',' );
			sb.append( stmt.getPredicate().getLocalName() );
			sb.append( ',' );
			sb.append( stmt.getResource().getLocalName() );
			sb.append( ']' );
			
			return sb.toString();
		} catch( Exception e ) {
			e.printStackTrace();
			
			return stmt.toString();
		}
	}

}
