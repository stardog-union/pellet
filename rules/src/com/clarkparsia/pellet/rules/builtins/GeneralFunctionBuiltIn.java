// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.builtins;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.utils.SetUtils;

import com.clarkparsia.pellet.rules.BindingHelper;
import com.clarkparsia.pellet.rules.VariableBinding;
import com.clarkparsia.pellet.rules.VariableUtils;
import com.clarkparsia.pellet.rules.model.AtomDObject;
import com.clarkparsia.pellet.rules.model.AtomDVariable;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;

/**
 * <p>
 * Title: General Function BuiltIn
 * </p>
 * <p>
 * Description: A wrapper for built-ins that have one binding for a given set of variables.
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
public class GeneralFunctionBuiltIn implements BuiltIn {

	private class GeneralFunctionHelper implements BindingHelper {
		
		private BuiltInAtom atom;
		private VariableBinding partial;
		private boolean used;
		
		public GeneralFunctionHelper( BuiltInAtom atom ) {
			this.atom = atom;
		}
		
		public Collection<? extends AtomVariable> getBindableVars( Collection<AtomVariable> bound ) {
			if ( !isApplicable( bound ) )
				return Collections.emptySet();
			
			return SetUtils.difference( VariableUtils.getVars( atom ), bound );
		}

		public Collection<? extends AtomVariable> getPrerequisiteVars( Collection<AtomVariable> bound ) {
			Collection<AtomVariable> vars = VariableUtils.getVars( atom );
			vars.removeAll( getBindableVars( bound ) );
			return vars;
		}

		private boolean isApplicable( Collection<AtomVariable> bound ) {
			boolean[] boundPositions = new boolean[ atom.getAllArguments().size() ];
			for ( int i = 0; i < boundPositions.length; i++ ) {
				if ( bound.contains( atom.getAllArguments().get( i ) ) )
					boundPositions[i] = true;
				else
					boundPositions[i] = false;
			}
			return function.isApplicable( boundPositions );
		}
		
		public void rebind(VariableBinding newBinding) {
			
			Literal[] arguments = new Literal[ atom.getAllArguments().size() ];
			
			for ( int i = 0; i < arguments.length; i++ ) {
				arguments[i] = newBinding.get( atom.getAllArguments().get( i ) );
			}
			
			if ( function.apply( newBinding.getABox(), arguments ) ) {
				VariableBinding newPartial = new VariableBinding( newBinding.getABox() );
				for ( int i = 0; i < arguments.length; i++ ) {
					AtomDObject arg = atom.getAllArguments().get( i );
					Literal result = arguments[ i ];
					Literal current = newBinding.get( arg );
					
					if ( current != null && !current.equals( result ) ) {
						// Oops, we overwrote an argument.
						if ( newBinding.get( arg ) != null )
							throw new InternalReasonerException( "General Function implementation overwrote one of its arguments!" );
						ABox.log.info( "Function results in multiple simultaneous values for variable" );
						return;
					}
					if ( current == null ) {
						newBinding.set( arg, result );
					}
				}
				
				used = false;
				partial = newPartial;
			} else {
				System.out.println( "Function failure: " + atom  );
				System.out.println( "Arguments: " + Arrays.toString( arguments ) );
			}
			
		}

		public boolean selectNextBinding() {
			if ( partial != null && used == false ) {
				used = true;
				return true;
			}
			return false;
		}

		public void setCurrentBinding(VariableBinding currentBinding) {
			for ( Map.Entry<AtomDVariable, Literal> entry : partial.dataEntrySet() ) {
				currentBinding.set( entry.getKey(), entry.getValue() );
			}
		}
		
	}
	
	private GeneralFunction function;
	
	public GeneralFunctionBuiltIn( GeneralFunction function) {
		this.function = function;
	}
	
	public BindingHelper createHelper(BuiltInAtom atom) {
		return new GeneralFunctionHelper( atom );
	}
	
}
