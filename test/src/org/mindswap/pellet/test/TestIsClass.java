// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static com.clarkparsia.pellet.utils.TermFactory.term;

import java.net.URI;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.owlapi.Reasoner;
import org.mindswap.pellet.utils.ATermUtils;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDataRangeFacetRestriction;
import org.semanticweb.owl.model.OWLDataRangeRestriction;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.vocab.OWLRestrictedDataRangeFacetVocabulary;
import org.semanticweb.owl.vocab.XSDVocabulary;

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
	public void testIsClass5() throws OWLException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		OWLOntology ontology = manager.createOntology(URI
				.create("http://example.org"));
		
		OWLDataRange dataRange = factory.getOWLDataType(XSDVocabulary.INTEGER
				.getURI());
		OWLDataRangeFacetRestriction dataRangeFacetRestriction = factory
				.getOWLDataRangeFacetRestriction(
						OWLRestrictedDataRangeFacetVocabulary.MIN_EXCLUSIVE, 1);
		OWLDataRangeRestriction dataRangeRestriction = factory
				.getOWLDataRangeRestriction(dataRange,
						dataRangeFacetRestriction);

		OWLDataProperty p = factory.getOWLDataProperty(URI
				.create("http://example#p"));
		OWLDataSomeRestriction dataSomeRestriction = factory
				.getOWLDataSomeRestriction(p, dataRangeRestriction);

		OWLClass c = factory.getOWLClass(URI.create("http://example#c"));

		OWLSubClassAxiom sc = factory.getOWLSubClassAxiom(c,
				dataSomeRestriction);

		manager.addAxiom(ontology, sc);
		
		Reasoner reasoner = new Reasoner(manager);

		reasoner.loadOntology(ontology);
		assertTrue(reasoner.isConsistent());
		
		KnowledgeBase kb = reasoner.getKB();
		assertTrue(kb.isClass(term("http://example#c")));
		
		// check for complex class that refers to a user-defined datatype 
		ATermAppl term = reasoner.getLoader().term( dataSomeRestriction );
		term = ATermUtils.normalize( term );
		assertTrue( kb.isClass( term ) );		
	}
}
