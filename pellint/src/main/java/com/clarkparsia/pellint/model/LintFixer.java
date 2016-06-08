// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.model;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
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
 * Description: Contains a set of OWLAxiom's to remove and a set of OWLAxiom's to add in _order to fix a lint.
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
public class LintFixer
{
	private final Set<? extends OWLAxiom> _axiomsToRemove;
	private final Set<? extends OWLAxiom> _axiomsToAdd;

	public LintFixer(final Set<? extends OWLAxiom> axiomsToRemove, final Set<? extends OWLAxiom> axiomsToAdd)
	{
		_axiomsToRemove = axiomsToRemove;
		_axiomsToAdd = axiomsToAdd;
	}

	public LintFixer(final OWLAxiom axiomToRemove, final OWLAxiom axiomToAdd)
	{
		this(Collections.singleton(axiomToRemove), Collections.singleton(axiomToAdd));
	}

	public Set<? extends OWLAxiom> getAxiomsToRemove()
	{
		return _axiomsToRemove;
	}

	public Set<? extends OWLAxiom> getAxiomsToAdd()
	{
		return _axiomsToAdd;
	}

	/**
	 * Replace the detected OWLAxioms with a new set of OWLAxioms, and apply the change to the given OWLOntology using the given OWLOntologyManager.
	 *
	 * @param manager the owl ontology manager
	 * @param ontology the ontology to fix
	 * @return <code>true</code> if:<BR>
	 *         1. All the OWLAxioms that are about to be removed, do exist in the given OWLOntology; and<BR>
	 *         2. All the changes were successfully applied.<BR>
	 *         Otherwise returns <code>false</code>.
	 * @throws org.semanticweb.owlapi.model.OWLOntologyChangeException if there is an error applying a fix
	 */
	public boolean apply(final OWLOntologyManager manager, final OWLOntology ontology) throws OWLOntologyChangeException
	{
		final Set<OWLAxiom> axioms = ontology.axioms().collect(Collectors.toSet());

		if (!axioms.containsAll(_axiomsToRemove))
			return false;

		for (final OWLAxiom axiom : _axiomsToRemove)
		{
			final RemoveAxiom remove = new RemoveAxiom(ontology, axiom);
			manager.applyChange(remove);
		}

		for (final OWLAxiom axiom : _axiomsToAdd)
			if (!axioms.contains(axiom))
			{
				final AddAxiom add = new AddAxiom(ontology, axiom);
				manager.applyChange(add);
			}

		return true;
	}
}
