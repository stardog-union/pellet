// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import junit.framework.TestSuite;

import org.mindswap.pellet.test.inctest.IncConsistencyTests;
import org.mindswap.pellet.test.inctest.IncJenaConsistencyTests;
import org.mindswap.pellet.test.rules.RulesTestSuite;

import com.clarkparsia.TestATermManchesterSyntaxRenderer;
import com.clarkparsia.explanation.test.ExplanationTestSuite;
import com.clarkparsia.modularity.test.ModularityTestSuite;
import com.clarkparsia.pellet.datatypes.test.DatatypesSuite;
import com.clarkparsia.pellet.test.BlockingTests;
import com.clarkparsia.pellet.test.CacheSafetyTests;
import com.clarkparsia.pellet.test.TestKnowledgeBase;
import com.clarkparsia.pellet.test.annotations.AnnotationsTestSuite;
import com.clarkparsia.pellet.test.classification.ClassificationTestSuite;
import com.clarkparsia.pellet.test.el.ELTests;
import com.clarkparsia.pellet.test.owlapi.OWLAPITests;
import com.clarkparsia.pellet.test.owlapi.OWLAPIv3Tests;
import com.clarkparsia.pellet.test.owlapi.OWLPrimerTests;
import com.clarkparsia.pellet.test.query.QueryTestSuite;
import com.clarkparsia.pellet.test.rbox.RBoxTestSuite;
import com.clarkparsia.pellet.test.tbox.TBoxTests;
import com.clarkparsia.pellet.test.transtree.TransTreeTestSuite;
import com.clarkparsia.pellint.test.PellintTestSuite;

public class PelletTestSuite extends TestSuite {
	public static String	base	= "test/data/";

	public static TestSuite suite() {
		TestSuite suite = new TestSuite( PelletTestSuite.class.getName() );

		suite.addTest( ATermTests.suite() );
		suite.addTest( PellintTestSuite.suite() );
		suite.addTest( DIGTestSuite.suite() );
		suite.addTest( TracingTests.suite() );
		suite.addTest( MiscTests.suite() );
		suite.addTest( MergeTests.suite() );
		suite.addTest( RBoxTestSuite.suite() );
		suite.addTest( BlockingTests.suite() );
		suite.addTest( CacheSafetyTests.suite() );
		suite.addTest( JenaTests.suite() );
		suite.addTest( OWLAPITests.suite() );
		suite.addTest( OWLAPIv3Tests.suite() );
		suite.addTest( OWLPrimerTests.suite() );
		suite.addTest( OWLAPIObjectConversionTests.suite() );
		suite.addTest( OWLAPIAxiomConversionTests.suite() );
		suite.addTest( IncConsistencyTests.suite() );
		suite.addTest( IncJenaConsistencyTests.suite() );
		suite.addTest( RulesTestSuite.suite() );
		suite.addTest( TBoxTests.suite() );
		suite.addTest( DatatypesSuite.suite() );
		suite.addTest( ELTests.suite() );
		suite.addTest( ExplanationTestSuite.suite() );
		suite.addTest( TestIsClass.suite() );
		suite.addTest( TestKnowledgeBase.suite() );
		suite.addTest( TestATermManchesterSyntaxRenderer.suite() );
		suite.addTest( AnnotationsTestSuite.suite() );
		suite.addTest( TransTreeTestSuite.suite() );
		suite.addTest( LiebigTestSuite.suite() );
		suite.addTest( QueryTestSuite.suite() );
		suite.addTest( WebOntTestSuite.suite() );
		suite.addTest( DLTestSuite.suite() );
		suite.addTest( ClassificationTestSuite.suite() );
		suite.addTest( ModularityTestSuite.suite() );
		
		// CLI Tests must go last, since some of them muck with PelletOptions!
		suite.addTest( pellet.test.CLITests.suite() );

		return suite;
	}

	public static void main(String args[]) {
		junit.textui.TestRunner.run( suite() );
	}
}
