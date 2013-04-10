// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.test.rdfxml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.clarkparsia.pellint.rdfxml.OWLSyntaxChecker;
import com.clarkparsia.pellint.rdfxml.RDFLints;
import com.clarkparsia.pellint.rdfxml.RDFModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Blazej Bulka
 * @author Evren Sirin
 */
public class OWLSyntaxCheckerTest {
	private static final String NS = "tag:clarkparsia.com,2010:pellint:test#";

	private static final Resource C = ResourceFactory.createResource(NS + "C");

	private static final Property p = ResourceFactory.createProperty(NS + "P");

	private static final Resource a = ResourceFactory.createResource(NS + "a");
	private static final Resource b = ResourceFactory.createResource(NS + "b");
	
	private static final Resource anon = ResourceFactory.createResource();

	private static final Literal lit = ResourceFactory.createPlainLiteral("lit");

	private RDFModel rdfModel;
	private OWLSyntaxChecker checker;

	@Before
	public void before() {
		rdfModel = new RDFModel();
		checker = new OWLSyntaxChecker();
	}

	protected void addStatement(Resource s, Property p, RDFNode o) {
		rdfModel.addStatement(ResourceFactory.createStatement(s, p, o));
	}

	protected RDFLints validate() {
		RDFLints lints = checker.validate(rdfModel);
		
		// System.out.println(lints);
		
		return lints;
	}

	protected void assertValid() {
		assertTrue(validate().isEmpty());
	}

	protected void assertInvalid() {
		assertFalse(validate().isEmpty());
	}

	@Test
	public void testLiteralSubClassOf() {
		addStatement(C, RDF.type, OWL.Class);
		addStatement(C, RDFS.subClassOf, lit);

		assertInvalid();
	}
	
	@Test
	public void testLiteralSubPropertyOf() {
		addStatement(p, RDF.type, OWL.ObjectProperty);
		addStatement(p, RDFS.subPropertyOf, lit);

		assertInvalid();
	}

	@Test
	public void testLiteralEquivalentClass() {
		addStatement(C, RDF.type, OWL.Class);
		addStatement(C, OWL.equivalentClass, lit);

		assertInvalid();
	}

	/**
	 * Ticket #457 (https://clark-parsia.trac.cvsdude.com/pellet-devel/ticket/457)
	 */
	@Test
	public void testRDFPlainLiteral() {
		Resource plainLiteral = ResourceFactory.createResource(RDF.getURI() + "PlainLiteral");

		addStatement(p, RDF.type, OWL.DatatypeProperty);
		addStatement(p, RDFS.range, plainLiteral);

		assertValid();
	}

	/**
	 * Ticket #502 (https://clark-parsia.trac.cvsdude.com/pellet-devel/ticket/502)
	 */
	@Test
	public void testUntypedObject() {
		addStatement(p, RDF.type, OWL.ObjectProperty);
		addStatement(a, RDF.type, OWL.Thing);
		addStatement(a, p, b);

		assertInvalid();
	}

	@Test
	public void testUntypedSubject() {
		addStatement(p, RDF.type, OWL.ObjectProperty);
		addStatement(b, RDF.type, OWL.Thing);
		addStatement(a, p, b);

		assertInvalid();
	}

	@Test
	public void testUntypedSubjectWithLiteralObject() {
		addStatement(p, RDF.type, OWL.DatatypeProperty);
		addStatement(a, p, lit);

		assertInvalid();
	}

	@Test
	public void testLiteralWithObjectProperty() {
		addStatement(p, RDF.type, OWL.ObjectProperty);
		addStatement(a, RDF.type, OWL.Thing);
		addStatement(a, p, lit);

		assertInvalid();
	}

	@Test
	public void testIndividualsWithDataProperty() {
		addStatement(p, RDF.type, OWL.DatatypeProperty);
		addStatement(a, RDF.type, OWL.Thing);
		addStatement(b, RDF.type, OWL.Thing);
		addStatement(a, p, b);

		assertInvalid();
	}

	@Test
	public void testBnodeWithDataProperty() {
		addStatement(p, RDF.type, OWL.DatatypeProperty);
		addStatement(a, RDF.type, OWL.Thing);
		addStatement(anon, RDF.type, OWL.Thing);
		addStatement(a, p, anon);

		assertInvalid();
	}

	@Test
	public void testClassIndividualPunning() {
		addStatement(a, RDF.type, OWL.Class);
		addStatement(a, RDF.type, OWL.Thing);

		assertInvalid();
		checker.setExcludeValidPunnings(true);
		assertValid();
	}

	@Test
	public void testClassPropertyPunning() {
		addStatement(a, RDF.type, OWL.Class);
		addStatement(a, RDF.type, OWL.ObjectProperty);

		assertInvalid();
		checker.setExcludeValidPunnings(true);
		assertValid();
	}


	@Test
	public void testClassDatatypePunning() {
		addStatement(a, RDF.type, OWL.Class);
		addStatement(a, RDF.type, RDFS.Datatype);

		assertInvalid();
		checker.setExcludeValidPunnings(true);
		assertInvalid();
	}
	
	@Test
	public void testObjectDataPropertyPunning() {
		addStatement(p, RDF.type, OWL.ObjectProperty);
		addStatement(p, RDF.type, OWL.DatatypeProperty);

		assertInvalid();

		checker.setExcludeValidPunnings(true);
		
		assertInvalid();		
	}
	
	@Test
	public void testObjectAnnotationPropertyPunning() {
		addStatement(p, RDF.type, OWL.ObjectProperty);
		addStatement(p, RDF.type, OWL.AnnotationProperty);

		assertInvalid();
		checker.setExcludeValidPunnings(true);
		assertInvalid();		
	}
	
	@Test
	public void testDataAnnotationPropertyPunning() {
		addStatement(p, RDF.type, OWL.DatatypeProperty);
		addStatement(p, RDF.type, OWL.AnnotationProperty);

		assertInvalid();
		checker.setExcludeValidPunnings(true);
		assertInvalid();		
	}
}
