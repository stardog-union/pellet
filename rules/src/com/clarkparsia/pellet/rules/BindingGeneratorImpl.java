// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.mindswap.pellet.ABox;

/**
* <p>
* Title: Binding Generator Implementation
* </p>
* <p>
* Description: Takes a list of binding helpers
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

public class BindingGeneratorImpl implements BindingGenerator {
	
	private Collection<BindingHelper> helpers;
	private VariableBinding initialBinding;
	
	private class BindingIterator implements Iterator<VariableBinding> {
		
		private VariableBinding binding;
		private BindingHelper[] helperChain;
		
		public BindingIterator( ) {
			helperChain = new BindingHelper[ helpers.size() ];
			helperChain = helpers.toArray( helperChain );
			
			if ( helperChain.length > 0 ) {
				helperChain[0].rebind( initialBinding );
			}
			
		}

		/**
		 * Return the current binding up through and including the <code>max</code> element of the pattern chain.
		 * @param max
		 * @return
		 */
		private VariableBinding getBinding( int max ) {
			VariableBinding newBinding = new VariableBinding( initialBinding );
			for ( int i = 0; i <= max; i++ ) {
				helperChain[i].setCurrentBinding( newBinding );
			}
			return newBinding;
		}
		
		public boolean hasNext() {
			if ( binding != null )
				return true;
			
			// Search loop to find new binding.
			VariableBinding newBinding = null;
			int position = helperChain.length - 1;
			while ( position >= 0 ) {
				
				if ( helperChain[position].selectNextBinding() ) {
					if ( newBinding == null )
						newBinding = getBinding( position );
					else 
						helperChain[position].setCurrentBinding( newBinding );
					if ( position < helperChain.length - 1 ) {
						// Not at last helper, need to move forward.
						helperChain[position + 1].rebind( newBinding );
						position++;
					} else {
						// Found new binding at last helper in the chain.
						// We can exit now.
						binding = newBinding;
						return true;
					}
				} else {
					// Continue going backwards.
					newBinding = null;
					position--;
				}
				
			}
			
			return false;
		}

		public VariableBinding next() {
			if ( !hasNext() ) {
				throw new NoSuchElementException();
			}
			VariableBinding result = binding;
			binding = null;
			return result;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
	/**
	 * Empty Binding Generator
	 */
	public BindingGeneratorImpl() { 
		helpers = Collections.emptySet();
	}
	
	/**
	 * 
	 * Constructs a binding generator with the given list of helpers.
	 * The helpers must be in such an order that prerequisite variables
	 * of any helper are bound by a helper before it.
	 */
	public BindingGeneratorImpl( ABox abox, VariableBinding initialBinding, Collection< BindingHelper > helpers ) {
		this.helpers = helpers;
		this.initialBinding = initialBinding;
	}
	
	
	
	public Iterator<VariableBinding> iterator() {
		return new BindingIterator();
	}

}
