package com.clarkparsia.pellet.test.annotations;

import junit.framework.TestSuite;

import org.mindswap.pellet.test.TestAnnotations;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2010
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Hector Perez-Urbina
 */
public class AnnotationsTestSuite extends TestSuite {

	public static TestSuite suite() {
		TestSuite suite = new TestSuite( AnnotationsTestSuite.class.getName() );
		
		suite.addTest( TestAnnotations.suite());
		suite.addTest( TestReasoningWithAnnotationAxioms.suite() );
		
		return suite;
	}

	public static void main(String args[]) {
		junit.textui.TestRunner.run( suite() );
	}
}