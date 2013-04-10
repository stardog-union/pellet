// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.model;

import java.util.Collection;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.clarkparsia.pellint.format.SimpleLintFormat;
import com.clarkparsia.pellint.lintpattern.LintPattern;
import com.clarkparsia.pellint.util.CollectionUtil;

/**
 * <p>
 * Title: Lint
 * </p>
 * <p>
 * Description: Encaptulates all the information related to a lint found. 
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
public class Lint {
	private LintPattern m_LintPattern;
	private LintFixer m_LintFixer;
	private Severity m_Severity;
	private Set<OWLClass> m_ParticipatingClasses;
	private Set<OWLClassAxiom> m_ParticipatingAxioms;
	private OWLOntology m_ParticipatingOntology;

	public Lint(LintPattern lintPattern, OWLOntology participatingOntology) {
		m_LintPattern = lintPattern;
		m_ParticipatingOntology = participatingOntology;
		m_ParticipatingClasses = CollectionUtil.makeSet();
		m_ParticipatingAxioms = CollectionUtil.makeSet();
	}

	/**
	 * @return The {@link com.clarkparsia.pellint.lintpattern.LintPattern} which generated this {@link com.clarkparsia.pellint.model.Lint}.
	 */
	public LintPattern getPattern() {
		return m_LintPattern;
	}

	public void setLintFixer(LintFixer fixer) {
		m_LintFixer = fixer;
	}

	public LintFixer getLintFixer() {
		return m_LintFixer;
	}

	public void setSeverity(Severity v) {
		m_Severity = v;
	}

	/**
	 * @return The {@link com.clarkparsia.pellint.model.Severity} of this {@link com.clarkparsia.pellint.model.Lint}
	 * relative to all the {@link com.clarkparsia.pellint.model.Lint} found for this {@link com.clarkparsia.pellint.lintpattern.LintPattern}.
	 */
	public Severity getSeverity() {
		return m_Severity;
	}
	
	public void addParticipatingClass(OWLClass c) {
		m_ParticipatingClasses.add(c);
	}
	
	public void addAllParticipatingClasses(Collection<? extends OWLClass> c) {
		m_ParticipatingClasses.addAll(c);
	}
	
	public Set<OWLClass> getParticipatingClasses() {
		return m_ParticipatingClasses;
	}

	public void addParticipatingAxiom(OWLClassAxiom a) {
		m_ParticipatingAxioms.add(a);
	}
	
	public void addAllParticipatingAxioms(Collection<? extends OWLClassAxiom> a) {
		m_ParticipatingAxioms.addAll(a);
	}
		
	public Set<OWLClassAxiom> getParticipatingAxioms() {
		return m_ParticipatingAxioms;
	}
	
	public OWLOntology getParticipatingOntology() {
		return m_ParticipatingOntology;
	}

	/**
	 * Apply {@link com.clarkparsia.pellint.model.LintFixer} to the source OWLOntology where
	 * this {@link com.clarkparsia.pellint.model.Lint} was found, using the given
	 * OWLOntologyManager.  The source OWLOntology must be part of the given OWLOntologyManager.
	 * 
	 * @param manager the owl ontology manager to use
	 * @return <code>true</code> if a {@link com.clarkparsia.pellint.model.LintFixer} was available and successfully applied, otherwise <code>false</code>.
	 * 
	 * @throws org.semanticweb.owlapi.model.OWLOntologyChangeException if a {@link com.clarkparsia.pellint.model.LintFixer} was available
	 * but org.semanticweb.owlapi.model.OWLOntologyChangeException was thrown when applying the change to the source OWLOntology.
	 * 
	 * @see com.clarkparsia.pellint.model.LintFixer#apply(OWLOntologyManager, OWLOntology)
	 */
	public boolean applyFix(OWLOntologyManager manager) throws OWLOntologyChangeException {
		return m_LintFixer != null && m_LintFixer.apply(manager, m_ParticipatingOntology);
	}

	/**
	 * It is recommended to use LintFormat.format() instead of calling toString() directly on a Lint.
	 */
	@Override
	public String toString() {
		return new SimpleLintFormat().format(this);
	}
}