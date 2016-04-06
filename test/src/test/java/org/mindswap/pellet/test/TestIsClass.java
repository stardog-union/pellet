// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import static com.clarkparsia.pellet.utils.TermFactory.term;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.clarkparsia.pellet.owlapi.PelletReasoner;
import com.clarkparsia.pellet.owlapi.PelletReasonerFactory;

import junit.framework.JUnit4TestAdapter;
import org.junit.Test;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.utils.ATermUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;
import aterm.ATermAppl;

/**
 * <p>
 * Title: TestIsClass
 * </p>
 * <p>
 * Description: This test cases have been written to resolve issue #141 and #140
 * where certain terms were wrongly evaluated as classes, e.g.
 * value(literal(...))
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Markus Stocker
 */
public class TestIsClass {

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( TestIsClass.class );
	}

	@Test
	public void testIsClass1() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl p = term( "p" );
		ATermAppl l = ATermUtils.makeTermAppl( "l" );
		ATermAppl v = ATermUtils.makeValue( ATermUtils.makeLiteral( l ) );
		ATermAppl c = ATermUtils.makeSomeValues( p, v );

		kb.addProperty( p );

		assertTrue( kb.isClass( c ) );
	}

	@Test
	public void testIsClass2() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl l = ATermUtils.makeTermAppl( "l" );
		ATermAppl v = ATermUtils.makeValue( ATermUtils.makeLiteral( l ) );

		assertFalse( kb.isClass( v ) );
	}

	@Test
	public void testIsClass3() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl l1 = ATermUtils.makeTermAppl( "l1" );
		ATermAppl l2 = ATermUtils.makeTermAppl( "l2" );
		ATermAppl v1 = ATermUtils.makeValue( ATermUtils.makeLiteral( l1 ) );
		ATermAppl v2 = ATermUtils.makeValue( ATermUtils.makeLiteral( l2 ) );
		ATermAppl t = ATermUtils.makeOr( v1, v2 );

		assertFalse( kb.isClass( t ) );
	}

	@Test
	public void testIsClass4() {
		KnowledgeBase kb = new KnowledgeBase();

		ATermAppl l = ATermUtils.makeTermAppl( "l" );
		ATermAppl v = ATermUtils.makeValue( ATermUtils.makeLiteral( l ) );
		ATermAppl n = ATermUtils.makeNot( v );

		assertFalse( kb.isClass( n ) );
	}

	@Test
	public void testIsClass5() throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		OWLOntology ontology = manager.createOntology(IRI
				.create("http://example.org"));
		
		OWLDatatype dataRange = factory.getOWLDatatype(OWL2Datatype.XSD_INTEGER.getIRI());
		OWLFacetRestriction dataRangeFacetRestriction = factory
				.getOWLFacetRestriction(
						OWLFacet.MIN_EXCLUSIVE, 1);
		OWLDataRange dataRangeRestriction = factory
				.getOWLDatatypeRestriction(dataRange,
						dataRangeFacetRestriction);

		OWLDataProperty p = factory.getOWLDataProperty(IRI
				.create("http://example#p"));
		OWLDataSomeValuesFrom dataSomeRestriction = factory
				.getOWLDataSomeValuesFrom(p, dataRangeRestriction);

		OWLClass c = factory.getOWLClass(IRI.create("http://example#c"));

		OWLSubClassOfAxiom sc = factory.getOWLSubClassOfAxiom(c,
				dataSomeRestriction);

		manager.addAxiom(ontology, sc);
		
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);

		assertTrue(reasoner.isConsistent());
		
		KnowledgeBase kb = reasoner.getKB();
		assertTrue(kb.isClass(term("http://example#c")));
		
		// check for complex class that refers to a user-defined datatype 
		ATermAppl term = reasoner.term( dataSomeRestriction );
		term = ATermUtils.normalize( term );
		assertTrue( kb.isClass( term ) );		
	}
}
