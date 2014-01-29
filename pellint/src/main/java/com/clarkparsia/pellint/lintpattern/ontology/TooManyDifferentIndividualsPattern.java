// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.lintpattern.ontology;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import com.clarkparsia.pellint.format.CompactClassLintFormat;
import com.clarkparsia.pellint.format.LintFormat;
import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.model.Severity;

/**
 * <p>
 * Title: 
 * </p>
 * <p>
 * Description: 
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
public class TooManyDifferentIndividualsPattern implements OntologyLintPattern {
	private static final LintFormat DEFAULT_LINT_FORMAT = new CompactClassLintFormat();
	
	private int m_MaxAllowed = 50;
	
	public String getName() {
		return getClass().getSimpleName() + " (MaxAllowed = " + m_MaxAllowed + ")";
	}
	
	public String getDescription() {
		return "Too many individuals involved in DifferentIndividuals axioms - maximum recommended is " + m_MaxAllowed;
	}

	public boolean isFixable() {
		return false;
	}
	
	public LintFormat getDefaultLintFormat() {
		return DEFAULT_LINT_FORMAT;
	}

	public void setMaxAllowed(int value) {
		m_MaxAllowed = value;
	}

	public List<Lint> match(OWLOntology ontology) {
		int totalEstimatedMemory = 0;
		for (OWLDifferentIndividualsAxiom axiom : ontology.getAxioms(AxiomType.DIFFERENT_INDIVIDUALS)) {
			totalEstimatedMemory += estimateMemoryConcumption(axiom.getIndividuals().size());
		}
		
		List<Lint> allLints = new ArrayList<Lint>();
		if (totalEstimatedMemory > estimateMemoryConcumption(m_MaxAllowed)) {
			Lint lint = new Lint(this, ontology);
			lint.setSeverity(new Severity(totalEstimatedMemory));
			allLints.add(lint);	
		}
		return allLints;
	}
	
	private static int estimateMemoryConcumption(int individualCount) {
		return individualCount * (individualCount - 1);
	}
}