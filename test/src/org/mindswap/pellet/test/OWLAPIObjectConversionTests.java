// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import java.net.URI;

import junit.framework.JUnit4TestAdapter;

import org.junit.Ignore;
import org.junit.Test;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.owlapi.ConceptConverter;
import org.mindswap.pellet.owlapi.PelletVisitor;
import org.mindswap.pellet.utils.SetUtils;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.vocab.XSDVocabulary;

import aterm.ATermAppl;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class OWLAPIObjectConversionTests {
	public static String				base			= "file:" + PelletTestSuite.base + "misc/";

	private static String				ns				= "urn:test:";

	private static OWLOntologyManager	manager			= OWLManager.createOWLOntologyManager();

	private static OWLDataFactory		factory			= manager.getOWLDataFactory();

	private static OWLClass				c1				= factory.getOWLClass( URI.create( ns
																+ "c1" ) );

	private static OWLClass				c2				= factory.getOWLClass( URI.create( ns
																+ "c2" ) );

	private static OWLObjectProperty	op1				= factory.getOWLObjectProperty( URI
																.create( ns + "op1" ) );

	private static OWLDataProperty		dp1				= factory.getOWLDataProperty( URI
																.create( ns + "dp1" ) );

	private static OWLIndividual		ind1			= factory.getOWLIndividual( URI.create( ns
																+ "ind1" ) );

	private static OWLIndividual		ind2			= factory.getOWLIndividual( URI.create( ns
																+ "ind2" ) );

	private static OWLIndividual		ind3			= factory.getOWLAnonymousIndividual( URI
																.create( ns + "ind3" ) );

	private static OWLConstant			uc1				= factory.getOWLUntypedConstant( "lit1" );

	private static OWLConstant			uc2				= factory.getOWLUntypedConstant( "lit2",
																"en" );

	private static OWLConstant			tc1				= factory
																.getOWLTypedConstant(
																		"lit3",
																		factory
																				.getOWLDataType( XSDVocabulary.STRING
																						.getURI() ) );

	private static OWLConstant			tc2				= factory.getOWLTypedConstant( "1", factory
																.getOWLDataType( XSDVocabulary.INT
																		.getURI() ) );

	private static OWLDataType			d1				= factory.getOWLDataType( URI.create( ns
																+ "d1" ) );

	private KnowledgeBase				kb				= new KnowledgeBase();

	private PelletVisitor				atermConverter	= new PelletVisitor( kb );

	private ConceptConverter			owlapiConverter	= new ConceptConverter( kb, factory );

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( OWLAPIObjectConversionTests.class );
	}

	private void testConversion(OWLObject object) {
		try {
			kb.clear();
			atermConverter.clear();
			atermConverter.setAddAxiom( true );

			object.accept( atermConverter );

			ATermAppl aterm = atermConverter.result();

			// for the ATerm->OWLObject translation to work we need
			// all the entities referred in the ATerm to be defined
			// entities. therefore, we add a dummy axiom to the KB
			// that will add the definitions for each entity.
			OWLAxiom axiom = null;
			if( object instanceof OWLDescription ) {
				OWLDescription c = (OWLDescription) object;
				axiom = factory.getOWLSubClassAxiom( c, c );
			}
			else if( object instanceof OWLObjectProperty ) {
				OWLObjectProperty p = (OWLObjectProperty) object;
				axiom = factory.getOWLSubObjectPropertyAxiom( p, p );
			}
			else if( object instanceof OWLDataProperty ) {
				OWLDataProperty p = (OWLDataProperty) object;
				axiom = factory.getOWLSubDataPropertyAxiom( p, p );
			}
			else if( object instanceof OWLIndividual ) {
				OWLIndividual ind = (OWLIndividual) object;
				axiom = factory.getOWLClassAssertionAxiom( ind, factory.getOWLThing() );
			}
			if( axiom != null )
				axiom.accept( atermConverter );

			OWLObject converted = owlapiConverter.convert( aterm );

			assertEquals( object, converted );
		} catch( Exception e ) {
			e.printStackTrace();

			fail( "Explanation failed" );
		}
	}

	@Test
	public void testClass() {
		testConversion( c1 );
	}

	@Test
	public void testIntersectionOf() {
		testConversion( factory.getOWLObjectIntersectionOf( SetUtils.create( c1, c2 ) ) );
	}

	@Test
	public void testUnionOf() {
		testConversion( factory.getOWLObjectUnionOf( SetUtils.create( c1, c2 ) ) );
	}

	@Test
	public void testComplementOf() {
		testConversion( factory.getOWLObjectComplementOf( c1 ) );
	}

	@Test
	public void testObjectSomeRestriction() {
		testConversion( factory.getOWLObjectSomeRestriction( op1, c1 ) );
		testConversion( factory.getOWLObjectSomeRestriction( factory.getOWLObjectPropertyInverse( op1 ), c1 ) );
	}

	@Test
	public void testObjectAllRestriction() {
		testConversion( factory.getOWLObjectAllRestriction( op1, c1 ) );
		testConversion( factory.getOWLObjectAllRestriction( factory.getOWLObjectPropertyInverse( op1 ), c1 ) );
	}

	/**
	 * {@inheritDoc}
	 */
	public void testObjectValueRestriction() {
		testConversion( factory.getOWLObjectValueRestriction( op1, ind1 ) );
		testConversion( factory.getOWLObjectValueRestriction( factory.getOWLObjectPropertyInverse( op1 ), ind1 ) );
	}

	@Test
	public void testObjectMinCardinalityRestriction() {
		testConversion( factory.getOWLObjectMinCardinalityRestriction( op1, 1 ) );
		testConversion( factory.getOWLObjectMinCardinalityRestriction( op1, 1, c1 ) );
		testConversion( factory.getOWLObjectMinCardinalityRestriction( factory.getOWLObjectPropertyInverse( op1 ), 1, c1 ) );
	}

	@Test
	public void testObjectExactCardinalityRestriction() {
		testConversion( factory.getOWLObjectExactCardinalityRestriction( op1, 1 ) );
		testConversion( factory.getOWLObjectExactCardinalityRestriction( op1, 1, c1 ) );
		testConversion( factory.getOWLObjectExactCardinalityRestriction( factory.getOWLObjectPropertyInverse( op1 ), 1, c1 ) );
	}

	@Test
	public void testObjectMaxCardinalityRestriction() {
		testConversion( factory.getOWLObjectMaxCardinalityRestriction( op1, 1 ) );
		testConversion( factory.getOWLObjectMaxCardinalityRestriction( op1, 1, c1 ) );
		testConversion( factory.getOWLObjectMaxCardinalityRestriction( factory.getOWLObjectPropertyInverse( op1 ), 1, c1 ) );
	}

	@Test
	public void testSelfRestriction() {
		testConversion( factory.getOWLObjectSelfRestriction( op1 ) );
		testConversion( factory.getOWLObjectSelfRestriction( factory.getOWLObjectPropertyInverse( op1 ) ) );
	}

	@Test
	public void testObjectOneOf() {
		testConversion( factory.getOWLObjectOneOf( SetUtils.create( ind1, ind2 ) ) );
	}

	@Test
	public void testDataSomeRestriction() {
		testConversion( factory.getOWLDataSomeRestriction( dp1, d1 ) );
	}

	@Test
	public void testDataAllRestriction() {
		testConversion( factory.getOWLDataAllRestriction( dp1, d1 ) );
	}

	@Test
	public void testDataValueRestriction() {
		testConversion( factory.getOWLDataValueRestriction( dp1, uc1 ) );
		testConversion( factory.getOWLDataValueRestriction( dp1, uc2 ) );
		testConversion( factory.getOWLDataValueRestriction( dp1, tc1 ) );
		testConversion( factory.getOWLDataValueRestriction( dp1, tc2 ) );
	}

	@Test
	public void testDataMinCardinalityRestriction() {
		testConversion( factory.getOWLDataMinCardinalityRestriction( dp1, 1, d1 ) );
	}

	@Test
	public void testDataExactCardinalityRestriction() {
		testConversion( factory.getOWLDataExactCardinalityRestriction( dp1, 1, d1 ) );
	}

	@Test
	public void testDataMaxCardinalityRestriction() {
		testConversion( factory.getOWLDataMaxCardinalityRestriction( dp1, 1, d1 ) );
	}

	@Test
	public void testDataType() {
		testConversion( d1 );
	}

	@Test
	public void testDataComplementOf() {
		testConversion( factory.getOWLDataComplementOf( d1 ) );
	}

	@Test
	public void testDataOneOf() {
		testConversion( factory.getOWLDataOneOf( SetUtils.create( uc1, uc2, tc1, tc2 ) ) );
	}

	@Ignore
	@Test
	public void testDataRangeRestriction() {
	}

	@Test
	public void testTypedConstant() {
		testConversion( tc1 );
		testConversion( tc2 );
	}

	@Test
	public void testUntypedConstant() {
		testConversion( uc1 );
		testConversion( uc2 );
	}

	@Test
	public void testObjectProperty() {
		testConversion( op1 );
	}

	@Test
	public void testObjectPropertyInverse() {
		testConversion( factory.getOWLObjectPropertyInverse( op1 ) );
	}

	@Test
	public void testDataProperty() {
		testConversion( dp1 );
	}

	@Test
	public void testIndividual() {
		testConversion( ind1 );
	}

	@Test
	public void testAnonymousIndividual() {
		testConversion( ind3 );
	}
}
