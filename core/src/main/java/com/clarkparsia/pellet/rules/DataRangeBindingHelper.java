// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.exceptions.InternalReasonerException;

import com.clarkparsia.pellet.datatypes.DatatypeReasoner;
import com.clarkparsia.pellet.datatypes.exceptions.DatatypeReasonerException;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.DataRangeAtom;


/**
* <p>
* Title: Data Range Binding Helper
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
public class DataRangeBindingHelper implements BindingHelper {

	private static final Logger log;

	static {
		log = Logger.getLogger( DataRangeBindingHelper.class.getCanonicalName() );
	}

	private DatatypeReasoner dtReasoner;
	private DataRangeAtom atom;
	private boolean hasNext;
	
	public DataRangeBindingHelper( ABox abox, DataRangeAtom atom ) {
		this.dtReasoner = abox.getDatatypeReasoner();
		this.atom = atom;
		hasNext = false;
	}
	
	public Collection<AtomVariable> getBindableVars( Collection<AtomVariable> bound ) {
		return Collections.emptySet();
	}

	public Collection<AtomVariable> getPrerequisiteVars( Collection<AtomVariable> bound ) {
		return VariableUtils.getVars( atom );
	}

	public void rebind(VariableBinding newBinding) {
		Literal dValue = newBinding.get( atom.getArgument() );

		if( dValue == null ) {
			throw new InternalReasonerException(
					"DataRangeBindingHelper cannot generate bindings for " + atom );
		}

		try {
			hasNext = dtReasoner.isSatisfiable( Collections.singleton( atom.getPredicate() ),
					dValue.getValue() );
		} catch( DatatypeReasonerException e ) {
			final String msg = "Unexpected datatype reasoner exception: " + e.getMessage();
			log.severe( msg );
			throw new InternalReasonerException( e );
		}
	}

	public boolean selectNextBinding() {
		if ( hasNext ) {
			hasNext = false;
			return true;
		}
		return false;
	}

	public void setCurrentBinding(VariableBinding currentBinding) {
		// This space left intentionally blank.
	}
	
	public String toString() { return atom.toString(); }

}
