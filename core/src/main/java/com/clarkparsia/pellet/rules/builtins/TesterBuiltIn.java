// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.builtins;

import java.util.Collection;
import java.util.Collections;

import org.mindswap.pellet.Literal;

import com.clarkparsia.pellet.rules.BindingHelper;
import com.clarkparsia.pellet.rules.VariableBinding;
import com.clarkparsia.pellet.rules.VariableUtils;
import com.clarkparsia.pellet.rules.model.AtomDObject;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;

/**
 * <p>
 * Title: Test Built-In
 * </p>
 * <p>
 * Description: An implementation of BuiltInFunction for
 * Tests.
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
public class TesterBuiltIn implements BuiltIn {
	
	private class TestHelper implements BindingHelper {
		
		private BuiltInAtom atom;
		private boolean result;
		
		
		public TestHelper( BuiltInAtom atom ) {
			this.atom = atom;
			result = false;
		}
		
		public Collection<? extends AtomVariable> getBindableVars( Collection<AtomVariable> bound ) {
			return Collections.emptySet();
		}

		public Collection<? extends AtomVariable> getPrerequisiteVars( Collection<AtomVariable> bound ) {
			return VariableUtils.getVars( atom );
		}

		public void rebind(VariableBinding newBinding) {
			Literal[] arguments = new Literal[ atom.getAllArguments().size() ];
			int i = 0;
			for ( AtomDObject obj : atom.getAllArguments() ) {
				arguments[i++] = newBinding.get( obj );
			}
			result = test.test( arguments );
		}

		public boolean selectNextBinding() {
			if ( result ) {
				result = false;
				return true;
			}
			return false;
		}

		public void setCurrentBinding(VariableBinding currentBinding) {
			// Nothing to do.
		}
		
	}
	
	private Tester test;
	
	public TesterBuiltIn( Tester test ) {
		this.test = test;
	}
	
	public BindingHelper createHelper( BuiltInAtom atom ) {
		return new TestHelper( atom );
	}

}
