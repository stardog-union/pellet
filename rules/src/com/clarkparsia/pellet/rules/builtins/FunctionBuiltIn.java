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
 * Title: Function Built-In
 * </p>
 * <p>
 * Description: A wrapper for built-ins that bind
 * the first argument.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Ron Alford
 */ 
public class FunctionBuiltIn implements BuiltIn {

	private class FunctionHelper implements BindingHelper {
		
		private BuiltInAtom atom;
		private AtomDObject head;
		private Literal value;
		private boolean used;
		
		public FunctionHelper( BuiltInAtom atom ) {
			this.atom = atom;
		}
		
		public Collection<? extends AtomVariable> getBindableVars( Collection<AtomVariable> bound ) {
			AtomDObject head = null;
			for ( AtomDObject obj : atom.getAllArguments() ) {
				if (head == null) {
					head = obj;
					// Can only bind first argument to a function
					if ( !VariableUtils.isVariable( head ) )
						return Collections.emptySet();
				} else {
					// Cannot bind a variable that occurs in multiple places.
					if ( head.equals( obj ) )
						return Collections.emptySet();
				}
			}
			if ( head == null )
				return Collections.emptySet();
			return Collections.singleton( (AtomVariable) head );
		}

		public Collection<? extends AtomVariable> getPrerequisiteVars( Collection<AtomVariable> bound ) {
			Collection<AtomVariable> vars = VariableUtils.getVars( atom );
			vars.removeAll( getBindableVars( bound ) );
			return vars;
		}

		public void rebind(VariableBinding newBinding) {
			used = false;
			head = null;
			value = null;
			Literal resultLit = null;
			
			// Can't bind the first arg if it doesn't exist!
			if ( atom.getAllArguments().size() == 0 )
				return;
			
			// The arguments to a numeric function number one less than the arguments
			// to the SWRL atom.  The first argument to the atom is either set
			// or tested against the result of the function.
			Literal[] arguments = new Literal[ atom.getAllArguments().size() - 1 ];
			
			int i = 0;
			for ( AtomDObject obj : atom.getAllArguments() ) {
				Literal lit = newBinding.get( obj );
				
				if ( i == 0 ) {
					if (lit != null) {
						resultLit = lit;
					}
					
					head = obj;
					i++;
					continue;
				}
				
				arguments[i-1] = lit;
				i++;
			}
			
			value = function.apply( newBinding.getABox(), resultLit, arguments );
			
		}

		public boolean selectNextBinding() {
			if ( value != null && used == false ) {
				used = true;
				return true;
			}
			return false;
		}

		public void setCurrentBinding(VariableBinding currentBinding) {
			currentBinding.set( head, value );
		}
		
	}
	
	private Function function;
	
	public FunctionBuiltIn( Function function ) {
		this.function = function;
	}
	
	public BindingHelper createHelper(BuiltInAtom atom) {
		return new FunctionHelper( atom );
	}
	
}
