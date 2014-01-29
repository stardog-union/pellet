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
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.Role;

import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;

/**
 * <p>
 * Title: Datavalue Property Binding Helper
 * </p>
 * <p>
 * Description: Generates bindings based off the given pattern.
 * The predicate must be a datatype property.
 * TODO: Rename to DatavaluedPropertyBindingHelper
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
public class DatavaluePropertyBindingHelper implements BindingHelper {
	private ABox abox;
	private VariableBinding binding;
	private Literal object;
	private Iterator<Literal> objectIterator;
	private DatavaluedPropertyAtom pattern;
	private Role role;
	private Individual subject;
	private Iterator<Individual> subjectIterator;
	
	public DatavaluePropertyBindingHelper( ABox abox, DatavaluedPropertyAtom pattern) {
		this.abox = abox;
		this.pattern = pattern;
	}
	

	public Collection<AtomVariable> getBindableVars( Collection<AtomVariable> bound ) {
		return VariableUtils.getVars( pattern );
	}

	private Literal getObject() {
		return binding.get( pattern.getArgument2() );
	}
	
	public Collection<AtomVariable> getPrerequisiteVars( Collection<AtomVariable> bound ) {
		return Collections.emptySet();
	}

	private Role getRole() {
		if ( role == null ) 
			role = abox.getRole( pattern.getPredicate() );
		return role;
	}
	
	private Individual getSubject() {
		return binding.get( pattern.getArgument1() );
	}
	
	/**
	 * Checks to see if an object is set (either bound, or a constant)
	 * @return
	 */
	private boolean isObjectSet() {
		return binding.get( pattern.getArgument2() ) != null;
	}
	
	/**
	 * Set the incoming binding for this helper.  This fixes
	 * any variables that are already bound by a preceding
	 * Binding Helper.
	 * 
	 * @param newBinding Binding map.  Copied on input.
	 */
	public void rebind( VariableBinding newBinding ) {
		binding = new VariableBinding( newBinding );
		
		if ( getSubject() != null ) {
			subjectIterator = Collections.singleton( getSubject() ).iterator();
		} else {
			subjectIterator = new AllNamedIndividualsIterator( abox );
		}
		
	}
	
	/**
	 * Selects the next binding. 
	 * 
	 * @return True if a binding was available for this pattern  given
	 * the initial binding.  False otherwise.  Will return if the binding
	 * is not set.
	 */
	public boolean selectNextBinding() {
		if ( binding == null )
			return false;
		
		while ( true ) {
			if ( subject == null || isObjectSet() ) {
				// Check to see if there are any more subjects to try
				if ( !subjectIterator.hasNext() )
					return false;
				subject = subjectIterator.next();
				
				if ( !isObjectSet() ) {
					objectIterator = new LiteralFilter( subject.getRNeighbors( getRole() ).iterator() );
				}
			}
			
			if ( isObjectSet() ) {
				// Object of pattern is already set; just test the pattern
				boolean result = subject.getRNeighbors( getRole() ).contains( getObject() );
				if (result) {
					return true;
				}
			} else {
				// Cycle through possible object bindings
				if ( objectIterator.hasNext() ) {
					object = objectIterator.next();
					return true;
				} else {
					// no more bindings - need a new subject
					subject = null;
				}
				
			}
		}
	}
	
	/**
	 * Set the variables this pattern uses in the given map.
	 * @param currentBinding
	 */
	public void setCurrentBinding( VariableBinding currentBinding ) {
		currentBinding.set( pattern.getArgument1(), subject );
		currentBinding.set( pattern.getArgument2(), object );
	}
	
	public String toString() {
		return "edges("+pattern+")";
	}

}
