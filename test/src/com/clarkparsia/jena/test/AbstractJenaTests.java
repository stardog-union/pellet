// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.jena.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * @author Evren Sirin
 */

public abstract class AbstractJenaTests {
	protected static final Resource A = ResourceFactory.createResource( "A" );
	protected static final Resource B = ResourceFactory.createResource( "B" );
	protected static final Resource C = ResourceFactory.createResource( "C" );
	protected static final Resource D = ResourceFactory.createResource( "D" );
	protected static final Resource E = ResourceFactory.createResource( "E" );
	
	protected static final Resource a = ResourceFactory.createResource( "a" );
	protected static final Resource b = ResourceFactory.createResource( "b" );
	protected static final Resource c = ResourceFactory.createResource( "c" );
	protected static final Resource d = ResourceFactory.createResource( "d" );
	protected static final Resource e = ResourceFactory.createResource( "e" );
	
	protected OntModel model;
	protected OntModel reasoner;

	@Before
	public void before() {
		model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM);
		reasoner = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC, model ); 
		reasoner.setStrictMode( false );
	}

	@After
	public void after() throws Exception {
		model.close();
	}

	protected void classes(Resource... classes) {
		for( Resource cls : classes )
			model.add( cls, RDF.type, OWL.Class );
	}
	
	protected void objectProperties(Resource... props) {
		for( Resource p : props )
			model.add( p, RDF.type, OWL.ObjectProperty );
	}
	
	protected void dataProperties(Resource... props) {
		for( Resource p : props )
			model.add( p, RDF.type, OWL.DatatypeProperty );
	}
	
	protected void annotationProperties(Resource... props) {
		for( Resource p : props )
			model.add( p, RDF.type, OWL.AnnotationProperty );
	}
	
	protected void individuals(Resource... inds) {
		for( Resource ind : inds )
			model.add( ind, RDF.type, OWL.Thing );
	}
	
	public Resource oneOf(Resource... terms) {
		return model.createEnumeratedClass( null, model.createList( terms ) );
	}
	
	public Resource not(Resource cls) {
		return model.createComplementClass( null, cls );
	}
	
	public void assertConsistent() {
		assertTrue( ((PelletInfGraph) reasoner.getGraph()).isConsistent() );
	}
	
	
	public void assertInconsistent() {
		assertFalse( ((PelletInfGraph) reasoner.getGraph()).isConsistent() );
	}
}
