// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.model;

import java.util.Collections;
import java.util.Set;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;

/**
 * <p>
 * Title: Lint Fixer
 * </p>
 * <p>
 * Description: Contains a set of OWLAxiom's to remove and a set of OWLAxiom's to add
 * in order to fix a lint.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Harris Lin
 */
public class LintFixer {
	private Set<? extends OWLAxiom> m_AxiomsToRemove;
	private Set<? extends OWLAxiom> m_AxiomsToAdd;
	
	public LintFixer(Set<? extends OWLAxiom> axiomsToRemove, Set<? extends OWLAxiom> axiomsToAdd) {
		m_AxiomsToRemove = axiomsToRemove;
		m_AxiomsToAdd = axiomsToAdd;
	}
	
	public LintFixer(OWLAxiom axiomToRemove, OWLAxiom axiomToAdd) {
		this(Collections.singleton(axiomToRemove), Collections.singleton(axiomToAdd));
	}
	
	public Set<? extends OWLAxiom> getAxiomsToRemove() {
		return m_AxiomsToRemove;
	}
	
	public Set<? extends OWLAxiom> getAxiomsToAdd() {
		return m_AxiomsToAdd;
	}

	/**
	 * Replace the detected OWLAxioms with a new set of OWLAxioms,
	 * and apply the change to the given OWLOntology using the given OWLOntologyManager. 
	 * 
	 * @param manager the owl ontology manager
	 * @param ontology the ontology to fix
	 * @return <code>true</code> if:<BR>
	 * 1. All the OWLAxioms that are about to be removed, do exist in the given OWLOntology; and<BR>
	 * 2. All the changes were successfully applied.<BR>
	 * Otherwise returns <code>false</code>.
	 * @throws org.semanticweb.owlapi.model.OWLOntologyChangeException if there is an error applying a fix
	 */
	public boolean apply(OWLOntologyManager manager, OWLOntology ontology) throws OWLOntologyChangeException {
		final Set<OWLAxiom> ALL_AXIOMS = ontology.getAxioms();

		if (!ALL_AXIOMS.containsAll(m_AxiomsToRemove))
			return false;
		
		for (OWLAxiom axiom : m_AxiomsToRemove) {
			RemoveAxiom remove = new RemoveAxiom(ontology, axiom);
			manager.applyChange(remove);
		}
		
		for (OWLAxiom axiom : m_AxiomsToAdd) {
			if (!ALL_AXIOMS.contains(axiom)) {
				AddAxiom add = new AddAxiom(ontology, axiom);
				manager.applyChange(add);
			}
		}
		
		return true;
	}
}
