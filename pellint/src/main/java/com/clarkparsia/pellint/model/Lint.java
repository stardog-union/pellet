// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.model;

import com.clarkparsia.pellint.format.SimpleLintFormat;
import com.clarkparsia.pellint.lintpattern.LintPattern;
import com.clarkparsia.pellint.util.CollectionUtil;
import java.util.Collection;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

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
public class Lint
{
	private final LintPattern _lintPattern;
	private LintFixer _lintFixer;
	private Severity _severity;
	private final Set<OWLClass> _participatingClasses;
	private final Set<OWLClassAxiom> _participatingAxioms;
	private final OWLOntology _participatingOntology;

	public Lint(final LintPattern lintPattern, final OWLOntology participatingOntology)
	{
		_lintPattern = lintPattern;
		_participatingOntology = participatingOntology;
		_participatingClasses = CollectionUtil.makeSet();
		_participatingAxioms = CollectionUtil.makeSet();
	}

	/**
	 * @return The {@link com.clarkparsia.pellint.lintpattern.LintPattern} which generated this {@link com.clarkparsia.pellint.model.Lint}.
	 */
	public LintPattern getPattern()
	{
		return _lintPattern;
	}

	public void setLintFixer(final LintFixer fixer)
	{
		_lintFixer = fixer;
	}

	public LintFixer getLintFixer()
	{
		return _lintFixer;
	}

	public void setSeverity(final Severity v)
	{
		_severity = v;
	}

	/**
	 * @return The {@link com.clarkparsia.pellint.model.Severity} of this {@link com.clarkparsia.pellint.model.Lint} relative to all the
	 *         {@link com.clarkparsia.pellint.model.Lint} found for this {@link com.clarkparsia.pellint.lintpattern.LintPattern}.
	 */
	public Severity getSeverity()
	{
		return _severity;
	}

	public void addParticipatingClass(final OWLClass c)
	{
		_participatingClasses.add(c);
	}

	public void addAllParticipatingClasses(final Collection<? extends OWLClass> c)
	{
		_participatingClasses.addAll(c);
	}

	public Set<OWLClass> getParticipatingClasses()
	{
		return _participatingClasses;
	}

	public void addParticipatingAxiom(final OWLClassAxiom a)
	{
		_participatingAxioms.add(a);
	}

	public void addAllParticipatingAxioms(final Collection<? extends OWLClassAxiom> a)
	{
		_participatingAxioms.addAll(a);
	}

	public Set<OWLClassAxiom> getParticipatingAxioms()
	{
		return _participatingAxioms;
	}

	public OWLOntology getParticipatingOntology()
	{
		return _participatingOntology;
	}

	/**
	 * Apply {@link com.clarkparsia.pellint.model.LintFixer} to the source OWLOntology where this {@link com.clarkparsia.pellint.model.Lint} was found, using
	 * the given OWLOntologyManager. The source OWLOntology must be part of the given OWLOntologyManager.
	 *
	 * @param manager the owl ontology manager to use
	 * @return <code>true</code> if a {@link com.clarkparsia.pellint.model.LintFixer} was available and successfully applied, otherwise <code>false</code>.
	 * @throws org.semanticweb.owlapi.model.OWLOntologyChangeException if a {@link com.clarkparsia.pellint.model.LintFixer} was available but
	 *         org.semanticweb.owlapi.model.OWLOntologyChangeException was thrown when applying the change to the source OWLOntology.
	 * @see com.clarkparsia.pellint.model.LintFixer#apply(OWLOntologyManager, OWLOntology)
	 */
	public boolean applyFix(final OWLOntologyManager manager) throws OWLOntologyChangeException
	{
		return _lintFixer != null && _lintFixer.apply(manager, _participatingOntology);
	}

	/**
	 * It is recommended to use LintFormat.format() instead of calling toString() directly on a Lint.
	 */
	@Override
	public String toString()
	{
		return new SimpleLintFormat().format(this);
	}
}
