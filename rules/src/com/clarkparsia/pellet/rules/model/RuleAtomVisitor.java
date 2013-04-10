// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.model;

/**
 * <p>
 * Title: Rule Atom Visitor
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
 * @author Ron Alford
 */ 
public interface RuleAtomVisitor {

	public void visit( BuiltInAtom atom );
	
	public void visit( ClassAtom atom );
	
	public void visit( DataRangeAtom atom );
	
	public void visit( DatavaluedPropertyAtom atom );
	
	public void visit( DifferentIndividualsAtom atom );
	
	public void visit( IndividualPropertyAtom atom );
	
	public void visit( SameIndividualAtom atom );
}
