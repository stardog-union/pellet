// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.owlapi;


import org.semanticweb.owl.inference.OWLReasonerFactory;
import org.semanticweb.owl.model.OWLOntologyManager;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: Clark & Parsia, LLC. <http://www.clarkparsia.com></p>
 *
 * @author Evren Sirin
 */
public class PelletReasonerFactory implements OWLReasonerFactory {
	public Reasoner createReasoner(OWLOntologyManager manager) {         
        try {
        	Reasoner reasoner = new Reasoner( manager );
			
			return reasoner;
		} catch( Exception e ) {
			throw new RuntimeException( e );
		}        
	}

	/**
	 * {@inheritDoc}
	 */
	public String getReasonerName() {
		return "Pellet";
	}

	public String toString() {
		return getReasonerName();
	}
}
