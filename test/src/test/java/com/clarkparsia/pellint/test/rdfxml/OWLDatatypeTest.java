package com.clarkparsia.pellint.test.rdfxml;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mindswap.pellet.test.PelletTestSuite;

import com.clarkparsia.pellint.rdfxml.OWLSyntaxChecker;
import com.clarkparsia.pellint.rdfxml.RDFLints;
import com.clarkparsia.pellint.rdfxml.RDFModel;
import com.clarkparsia.pellint.rdfxml.RDFModelReader;

/**
 * Tests for datatypes in lint
 * 
 * @author Blazej Bulka <blazej@clarkparsia.com>
 */
public class OWLDatatypeTest {
	private static final String TEST_438_DATA = PelletTestSuite.base + "/misc/ticket-438.ttl";
	
	/**
	 * Test for ticket 438. (Lint reported user-defined datatypes as "untyped classes"
	 * because they used owl:equivalentClass to connect a named datatype with an anonymous datatype;
	 * in the implementation of lint at that time, it was expected (incorrectly) that both arguments
	 * to equivalentClasses are types).
	 */
	@Test
	public void testDatatypeEquivalentClass() {
		RDFModelReader modelReader = new RDFModelReader();
		RDFModel rdfModel = modelReader.read( TEST_438_DATA, false /* loadImports */ );
		
		OWLSyntaxChecker checker = new OWLSyntaxChecker();
		RDFLints lints = checker.validate( rdfModel );
		
		assertTrue( lints.isEmpty() );	
	}
}
