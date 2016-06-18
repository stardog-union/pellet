// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.test.rdfxml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.clarkparsia.pellint.rdfxml.OWLSyntaxChecker;
import com.clarkparsia.pellint.rdfxml.RDFLints;
import com.clarkparsia.pellint.rdfxml.RDFModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.junit.Before;
import org.junit.Test;

/**
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
public class OWLSyntaxCheckerTest
{
	private static final String NS = "tag:clarkparsia.com,2010:pellint:test#";

	private static final Resource _C = ResourceFactory.createResource(NS + "C");

	private static final Property _p = ResourceFactory.createProperty(NS + "P");

	private static final Resource _a = ResourceFactory.createResource(NS + "a");
	private static final Resource _b = ResourceFactory.createResource(NS + "b");

	private static final Resource _anon = ResourceFactory.createResource();

	private static final Literal _lit = ResourceFactory.createPlainLiteral("lit");

	private RDFModel _rdfModel;
	private OWLSyntaxChecker _checker;

	@Before
	public void before()
	{
		_rdfModel = new RDFModel();
		_checker = new OWLSyntaxChecker();
	}

	protected void addStatement(final Resource s, final Property p, final RDFNode o)
	{
		_rdfModel.addStatement(ResourceFactory.createStatement(s, p, o));
	}

	protected RDFLints validate()
	{
		final RDFLints lints = _checker.validate(_rdfModel);

		// System.out.println(lints);

		return lints;
	}

	protected void assertValid()
	{
		assertTrue(validate().isEmpty());
	}

	protected void assertInvalid()
	{
		assertFalse(validate().isEmpty());
	}

	@Test
	public void testLiteralSubClassOf()
	{
		addStatement(_C, RDF.type, OWL.Class);
		addStatement(_C, RDFS.subClassOf, _lit);

		assertInvalid();
	}

	@Test
	public void testLiteralSubPropertyOf()
	{
		addStatement(_p, RDF.type, OWL.ObjectProperty);
		addStatement(_p, RDFS.subPropertyOf, _lit);

		assertInvalid();
	}

	@Test
	public void testLiteralEquivalentClass()
	{
		addStatement(_C, RDF.type, OWL.Class);
		addStatement(_C, OWL.equivalentClass, _lit);

		assertInvalid();
	}

	/**
	 * Ticket #457 (https://clark-parsia.trac.cvsdude.com/pellet-devel/ticket/457)
	 */
	@Test
	public void testRDFPlainLiteral()
	{
		final Resource plainLiteral = ResourceFactory.createResource(RDF.getURI() + "PlainLiteral");

		addStatement(_p, RDF.type, OWL.DatatypeProperty);
		addStatement(_p, RDFS.range, plainLiteral);

		assertValid();
	}

	/**
	 * Ticket #502 (https://clark-parsia.trac.cvsdude.com/pellet-devel/ticket/502)
	 */
	@Test
	public void testUntypedObject()
	{
		addStatement(_p, RDF.type, OWL.ObjectProperty);
		addStatement(_a, RDF.type, OWL.Thing);
		addStatement(_a, _p, _b);

		assertInvalid();
	}

	@Test
	public void testUntypedSubject()
	{
		addStatement(_p, RDF.type, OWL.ObjectProperty);
		addStatement(_b, RDF.type, OWL.Thing);
		addStatement(_a, _p, _b);

		assertInvalid();
	}

	@Test
	public void testUntypedSubjectWithLiteralObject()
	{
		addStatement(_p, RDF.type, OWL.DatatypeProperty);
		addStatement(_a, _p, _lit);

		assertInvalid();
	}

	@Test
	public void testLiteralWithObjectProperty()
	{
		addStatement(_p, RDF.type, OWL.ObjectProperty);
		addStatement(_a, RDF.type, OWL.Thing);
		addStatement(_a, _p, _lit);

		assertInvalid();
	}

	@Test
	public void testIndividualsWithDataProperty()
	{
		addStatement(_p, RDF.type, OWL.DatatypeProperty);
		addStatement(_a, RDF.type, OWL.Thing);
		addStatement(_b, RDF.type, OWL.Thing);
		addStatement(_a, _p, _b);

		assertInvalid();
	}

	@Test
	public void testBnodeWithDataProperty()
	{
		addStatement(_p, RDF.type, OWL.DatatypeProperty);
		addStatement(_a, RDF.type, OWL.Thing);
		addStatement(_anon, RDF.type, OWL.Thing);
		addStatement(_a, _p, _anon);

		assertInvalid();
	}

	@Test
	public void testClassIndividualPunning()
	{
		addStatement(_a, RDF.type, OWL.Class);
		addStatement(_a, RDF.type, OWL.Thing);

		assertInvalid();
		_checker.setExcludeValidPunnings(true);
		assertValid();
	}

	@Test
	public void testClassPropertyPunning()
	{
		addStatement(_a, RDF.type, OWL.Class);
		addStatement(_a, RDF.type, OWL.ObjectProperty);

		assertInvalid();
		_checker.setExcludeValidPunnings(true);
		assertValid();
	}

	@Test
	public void testClassDatatypePunning()
	{
		addStatement(_a, RDF.type, OWL.Class);
		addStatement(_a, RDF.type, RDFS.Datatype);

		assertInvalid();
		_checker.setExcludeValidPunnings(true);
		assertInvalid();
	}

	@Test
	public void testObjectDataPropertyPunning()
	{
		addStatement(_p, RDF.type, OWL.ObjectProperty);
		addStatement(_p, RDF.type, OWL.DatatypeProperty);

		assertInvalid();

		_checker.setExcludeValidPunnings(true);

		assertInvalid();
	}

	@Test
	public void testObjectAnnotationPropertyPunning()
	{
		addStatement(_p, RDF.type, OWL.ObjectProperty);
		addStatement(_p, RDF.type, OWL.AnnotationProperty);

		assertInvalid();
		_checker.setExcludeValidPunnings(true);
		assertInvalid();
	}

	@Test
	public void testDataAnnotationPropertyPunning()
	{
		addStatement(_p, RDF.type, OWL.DatatypeProperty);
		addStatement(_p, RDF.type, OWL.AnnotationProperty);

		assertInvalid();
		_checker.setExcludeValidPunnings(true);
		assertInvalid();
	}
}
