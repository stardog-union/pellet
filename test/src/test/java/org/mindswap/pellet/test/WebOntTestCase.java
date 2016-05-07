// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

public class WebOntTestCase
{
	File _manifest;
	WebOntTest _test;

	public WebOntTestCase(final WebOntTest test, final File manifest, @SuppressWarnings("unused") final String name)
	{
		//        super( "OWLTestCase-" + name );
		this._test = test;
		this._manifest = manifest;
	}

	public void runTest() throws IOException
	{
		assertTrue(_test.doSingleTest(_manifest.toURI().toURL().toString()) != WebOntTest.TEST_FAIL);
	}

	@Override
	public String toString()
	{
		return _manifest.getParentFile().getName() + "/" + _manifest.getName();
	}
}
