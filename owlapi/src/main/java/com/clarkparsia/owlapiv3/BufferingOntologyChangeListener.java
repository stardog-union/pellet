// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.owlapiv3;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyID;

/**
 * @author Evren Sirin
 */
public class BufferingOntologyChangeListener implements OWLOntologyChangeListener {
	private final Set<OWLAxiom> additions = Sets.newHashSet();
	private final Set<OWLAxiom> removals = Sets.newHashSet();
	private final Set<OWLOntologyID> ontologies;

	public BufferingOntologyChangeListener(final Iterable<OWLOntologyID> onts) {
		ontologies = Sets.newHashSet(onts);
	}

	@Override
	public void ontologiesChanged(final List<? extends OWLOntologyChange> changeList) throws OWLException {
		for (OWLOntologyChange change : changeList) {
			if (ontologies.contains(change.getOntology().getOntologyID())) {
				if (change.isAxiomChange()) {
					OWLAxiom axiom = change.getAxiom();

					if (!axiom.isAnnotationAxiom()) {
						if (change.isAddAxiom()) {
							additions.add(axiom);
							removals.remove(axiom);
						}
						else {
							additions.remove(axiom);
							removals.add(axiom);
						}
					}
				}
			}
		}
	}

	public boolean isChanged() {
		return !getAdditions().isEmpty() || !getRemovals().isEmpty();
	}

	public Set<OWLAxiom> getAdditions() {
		return additions;
	}

	public Set<OWLAxiom> getRemovals() {
		return removals;
	}

	public void reset() {
		additions.clear();
		removals.clear();
	}
}