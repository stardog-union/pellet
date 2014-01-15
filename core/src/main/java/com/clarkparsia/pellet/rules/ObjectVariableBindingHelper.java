// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Individual;

import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.AtomVariable;

/**
* <p>
* Title: Object Variable Binding Helper
* </p>
* <p>
* Description: A binding helper that will iterate
* over all named individuals in the abox.
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

public class ObjectVariableBindingHelper implements BindingHelper {
	
	private ABox abox;
	private Individual currentIndividual;
	private Iterator< Individual > individualIterator;
	private AtomIVariable var;
	
	public ObjectVariableBindingHelper( ABox abox, AtomIVariable var ) {
		this.abox = abox;
		this.var = var;
	}
	
	public Collection<AtomIVariable> getBindableVars( Collection<AtomVariable> bound ) {
		return Collections.singleton( var );
	}

	public Collection<AtomIVariable> getPrerequisiteVars( Collection<AtomVariable> bound ) {
		return Collections.emptyList();
	}

	public void rebind(VariableBinding newBinding) {
		if ( newBinding.containsKey( var ) ) {
			individualIterator = Collections.singleton( newBinding.get( var ) ).iterator();
		} else {
			individualIterator = new AllNamedIndividualsIterator( abox );
		} 
	}

	public boolean selectNextBinding() {
		if ( ( individualIterator == null ) || ! individualIterator.hasNext() )
			return false;
		
		currentIndividual = individualIterator.next();
		
		return true;
	}

	public void setCurrentBinding(VariableBinding currentBinding) {
		currentBinding.set( var, currentIndividual );
	}
	
	public String toString() {
		return "individuals("+var+")";
	}

}
