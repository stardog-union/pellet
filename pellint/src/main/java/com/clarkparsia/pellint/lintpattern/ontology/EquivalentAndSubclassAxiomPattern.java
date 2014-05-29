// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.lintpattern.ontology;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellint.format.CompactClassLintFormat;
import com.clarkparsia.pellint.format.LintFormat;
import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.model.LintFixer;
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
public class EquivalentAndSubclassAxiomPattern implements OntologyLintPattern {
	private static final LintFormat DEFAULT_LINT_FORMAT = new CompactClassLintFormat();
	
	public String getName() {
		return getClass().getSimpleName();
	}
	
	public String getDescription() {
		return "A named concept appears in equivalent axiom(s) and on the left-hand side of a subclass axiom";
	}

	public boolean isFixable() {
		return true;
	}
	
	public LintFormat getDefaultLintFormat() {
		return DEFAULT_LINT_FORMAT;
	}

	public List<Lint> match(OWLOntology ontology) {
		List<Lint> allLints = new ArrayList<Lint>();
		for (OWLClass owlClass : ontology.getClassesInSignature()) {
			Set<OWLEquivalentClassesAxiom> equivalents = ontology.getEquivalentClassesAxioms(owlClass);
			Set<OWLSubClassOfAxiom> subclasses = ontology.getSubClassAxiomsForSubClass(owlClass);
			
			Set<OWLEquivalentClassesAxiom> badEquivalents = new HashSet<OWLEquivalentClassesAxiom>();
			for (OWLEquivalentClassesAxiom equivalent : equivalents) {
				for (OWLClassExpression desc : equivalent.getClassExpressions()) {
					if (OWLUtil.isComplex(desc)) {
						badEquivalents.add(equivalent);
						break;
					}
				}
			}
			
			if (badEquivalents.isEmpty()) continue;
			if (badEquivalents.size() == 1 && subclasses.isEmpty()) continue;
			
			Lint lint = new Lint(this, ontology);
			lint.addParticipatingClass(owlClass);
			lint.addAllParticipatingAxioms(badEquivalents);
			lint.addAllParticipatingAxioms(subclasses);
			Set<OWLClassAxiom> fixedAxioms = fixEquivalentAxioms(owlClass, badEquivalents);
			LintFixer fixer = new LintFixer(badEquivalents, fixedAxioms);
			lint.setLintFixer(fixer);
			allLints.add(lint);
		}
		
		return allLints;
	}
	
	private static Set<OWLClassAxiom> fixEquivalentAxioms(OWLClass classToFix, Set<OWLEquivalentClassesAxiom> axioms) {
		Set<OWLClassAxiom> fixes = new HashSet<OWLClassAxiom>();
		for (OWLEquivalentClassesAxiom axiom : axioms) {
			Set<OWLClassExpression> descs = new HashSet<OWLClassExpression>(axiom.getClassExpressions());
			descs.remove(classToFix);
			
			if (descs.size() == 1) {
				fixes.add(OWL.subClassOf(classToFix, descs.iterator().next()));
			} else {
				fixes.add(OWL.equivalentClasses(descs));
				fixes.add(OWL.subClassOf(classToFix, OWL.and(descs)));
			}
		}
		return fixes;
	}
}