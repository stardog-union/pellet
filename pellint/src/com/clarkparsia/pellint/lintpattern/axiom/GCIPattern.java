// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.lintpattern.axiom;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import com.clarkparsia.pellint.format.LintFormat;
import com.clarkparsia.pellint.format.SimpleLintFormat;
import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.util.OWLUtil;

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
public class GCIPattern extends AxiomLintPattern {
	private static final LintFormat DEFAULT_LINT_FORMAT = new SimpleLintFormat();
	
	public String getName() {
		return getClass().getSimpleName();
	}
	
	public String getDescription() {
		return "GCI axiom, or equivalence classes axiom with two or more complex concepts";
	}

	public boolean isFixable() {
		return false;
	}
	
	public LintFormat getDefaultLintFormat() {
		return DEFAULT_LINT_FORMAT;
	}

	public void visit(OWLEquivalentClassesAxiom axiom) {
		int complexCount = 0;
		for (OWLClassExpression desc : axiom.getClassExpressions()) {
			if (OWLUtil.isComplex(desc)) {
				complexCount++;
			}
		}
		
		if (complexCount > 1) {
			Lint lint = makeLint();
			lint.addParticipatingAxiom(axiom);
			setLint(lint);
		}
	}
	
	public void visit(OWLSubClassOfAxiom axiom) {
		if (OWLUtil.isComplex(axiom.getSubClass())) {
			Lint lint = makeLint();
			lint.addParticipatingAxiom(axiom);
			setLint(lint);			
		}		
	}
}