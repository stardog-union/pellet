// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.model;

import org.semanticweb.owlapi.model.OWLOntology;

import com.clarkparsia.pellint.lintpattern.LintPattern;

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
public class LintFactory {
	private LintPattern m_LintPattern;
	private OWLOntology m_ParticipatingOntology;
	
	public LintFactory(LintPattern lintPattern, OWLOntology participatingOntology) {
		m_LintPattern = lintPattern;
		m_ParticipatingOntology = participatingOntology;
	}
	
	public Lint make() {
		return new Lint(m_LintPattern, m_ParticipatingOntology);
	}
}
