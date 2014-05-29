// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test;

public interface WebOntTester {
	public void setInputOntology( String inputFileURI );
	
	public void setTimeout( long timeout );
	
	public boolean isConsistent();
	
	public void classify(); 
	
	public void testEntailment( String entailmentFileURI, boolean positiveEntailment );
	
	public void registerURIMapping(String fromURI, String toURI);
}
