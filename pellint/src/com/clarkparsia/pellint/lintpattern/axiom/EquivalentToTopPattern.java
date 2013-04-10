// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.lintpattern.axiom;

import java.util.Collections;
import java.util.HashSet;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellint.format.CompactClassLintFormat;
import com.clarkparsia.pellint.format.LintFormat;
import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.model.LintFixer;

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
public class EquivalentToTopPattern extends AxiomLintPattern {
	private static final LintFormat DEFAULT_LINT_FORMAT = new CompactClassLintFormat();
	
	public String getName() {
		return getClass().getSimpleName();
	}
	
	public String getDescription() {
		return "Top is equivalent to some concept or is part of an equivalent classes axiom";
	}

	public boolean isFixable() {
		return true;
	}
	
	public LintFormat getDefaultLintFormat() {
		return DEFAULT_LINT_FORMAT;
	}

	public void visit(OWLEquivalentClassesAxiom axiom) {
		if (axiom.getClassExpressions().contains(OWL.Thing)) {
			Lint lint = makeLint();
			lint.addParticipatingAxiom(axiom);
			LintFixer fixer = new LintFixer(Collections.singleton(axiom), new HashSet<OWLAxiom>());
			lint.setLintFixer(fixer);
			setLint(lint);
		}
	}
}