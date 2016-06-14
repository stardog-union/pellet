// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.test;

import junit.framework.JUnit4TestAdapter;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * <p>
 * Title: ModularityTestSuite
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

@RunWith(Suite.class)
@SuiteClasses({ //
		SyntacticBottomLocalityTests.class, //
		SyntacticTopLocalityTests.class, //
		SyntacticTopTopLocalityTests.class, //
		AxiomBasedModularityTestSuite.class, //
		GraphBasedModularityTestSuite.class, //
		PersistenceModularityTest.class, //
		PersistenceClassificationTest.class, // FIXME #4
		PersistenceRealizationTest.class, // FIXME #4
		PersistenceUpdatesTest.class //
})
public class ModularityTestSuite
{
	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(ModularityTestSuite.class);
	}
}
