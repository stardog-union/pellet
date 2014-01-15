// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.builtins;

import java.util.Collection;
import java.util.Collections;

import org.mindswap.pellet.ABox;

import com.clarkparsia.pellet.rules.BindingHelper;
import com.clarkparsia.pellet.rules.VariableBinding;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;

/**
 * <p>
 * Title: No-Such-Built-In Built-In
 * </p>
 * <p>
 * Description: Place holder for any unimplemented built-in.
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
public class NoSuchBuiltIn implements BuiltIn {
	
	private static class EmptyHelper implements BindingHelper {

		public Collection<? extends AtomVariable> getBindableVars( Collection<AtomVariable> bound ) {
			return Collections.emptySet();
		}

		public Collection<? extends AtomVariable> getPrerequisiteVars( Collection<AtomVariable> bound ) {
			return Collections.emptySet();
		}

		public void rebind(VariableBinding newBinding) {
			// Nothing to do
		}

		public boolean selectNextBinding() {
			return false;
		}

		public void setCurrentBinding(VariableBinding currentBinding) {
			// Nothing to do
		}
		
	}
	
	private BindingHelper empty = new EmptyHelper();
	
	public static final BuiltIn instance = new NoSuchBuiltIn();
	
	private NoSuchBuiltIn(){}
	
	public BindingHelper createHelper(BuiltInAtom atom) {
		ABox.log.warning( "Returning an empty binding helper for unimplemented built-in "+atom );
		return empty;
	}

}
