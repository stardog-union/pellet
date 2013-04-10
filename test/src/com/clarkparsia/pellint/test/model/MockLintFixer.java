package com.clarkparsia.pellint.test.model;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.clarkparsia.pellint.model.LintFixer;
import com.clarkparsia.pellint.util.CollectionUtil;

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
public class MockLintFixer extends LintFixer {
	public boolean applyCalled = false;
	
	public MockLintFixer() {
		super(CollectionUtil.<OWLAxiom>makeSet(), CollectionUtil.<OWLAxiom>makeSet());
	}

	public boolean apply(OWLOntologyManager manager, OWLOntology ontology) throws OWLOntologyChangeException {
		applyCalled = true;
		return true;
	}
}
