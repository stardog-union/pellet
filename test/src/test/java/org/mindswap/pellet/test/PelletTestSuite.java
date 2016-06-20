// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

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
import com.clarkparsia.pellet.test.owlapi.OWLPrimerTests;
import com.clarkparsia.pellet.test.query.QueryTestSuite;
import com.clarkparsia.pellet.test.rbox.RBoxTestSuite;
import com.clarkparsia.pellet.test.tbox.TBoxTests;
import com.clarkparsia.pellet.test.transtree.TransTreeTestSuite;
import com.clarkparsia.pellint.test.PellintTestSuite;
import java.io.File;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.mindswap.pellet.test.inctest.IncConsistencyTests;
import org.mindswap.pellet.test.inctest.IncJenaConsistencyTests;
import org.mindswap.pellet.test.rules.RulesTestSuite;

@RunWith(Suite.class)
@SuiteClasses({ ATermTests.class, //
		PellintTestSuite.class, //
		TracingTests.class, //
		MiscTests.class, //
		MergeTests.class, //
		RBoxTestSuite.class, //
		BlockingTests.class, //
		CacheSafetyTests.class, //
		JenaTests.class, //
		OWLAPITests.class, //
		OWLPrimerTests.class, //
		OWLAPIObjectConversionTests.class, //
		OWLAPIAxiomConversionTests.class, //
		IncConsistencyTests.class, //
		IncJenaConsistencyTests.class, //
		RulesTestSuite.class, //
		TBoxTests.class, //
		DatatypesSuite.class, //
		ELTests.class, //
		ExplanationTestSuite.class, //
		TestIsClass.class, //
		TestKnowledgeBase.class, //
		TestATermManchesterSyntaxRenderer.class, //
		AnnotationsTestSuite.class, //
		TransTreeTestSuite.class, //
		LiebigTestSuite.class, //
		QueryTestSuite.class, //
		WebOntTestSuite.class, //
		DLTestSuite.class, //
		ClassificationTestSuite.class, //
		ModularityTestSuite.class, //
		// CLI Tests must go last, since some of them muck with PelletOptions!
		pellet.test.CLITests.class })
public class PelletTestSuite
{
	public static String base = ((new File("test/data/")).exists()) ? "test/data/" : "src/test/resources/test/data/";

	static
	{
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "WARN");
	}
}
