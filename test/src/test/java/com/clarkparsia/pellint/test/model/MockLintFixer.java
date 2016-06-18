package com.clarkparsia.pellint.test.model;

import com.clarkparsia.pellint.model.LintFixer;
import com.clarkparsia.pellint.util.CollectionUtil;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Harris Lin
 */
public class MockLintFixer extends LintFixer
{
	public boolean _applyCalled = false;

	public MockLintFixer()
	{
		super(CollectionUtil.<OWLAxiom> makeSet(), CollectionUtil.<OWLAxiom> makeSet());
	}

	@Override
	public boolean apply(final OWLOntologyManager manager, final OWLOntology ontology) throws OWLOntologyChangeException
	{
		_applyCalled = true;
		return true;
	}
}
