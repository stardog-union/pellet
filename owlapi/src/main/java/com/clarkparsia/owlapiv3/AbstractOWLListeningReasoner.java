// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.owlapiv3;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * @author Evren Sirin
 */
public abstract class AbstractOWLListeningReasoner implements OWLListeningReasoner {
	private boolean listenChanges = false;

	public abstract OWLOntologyManager getManager();

	public void setListenChanges(boolean listen) {
		if (listenChanges != listen) {
			listenChanges = listen;
			if (listenChanges) {
				getManager().addOntologyChangeListener(this);
			}
			else {
				getManager().removeOntologyChangeListener(this);
			}
		}
	}

	@Override
	public boolean isListenChanges() {
		return listenChanges;
	}
}