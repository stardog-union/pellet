package com.clarkparsia.pellint.test.model;

import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.clarkparsia.pellint.model.Lint;

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
public class MockLint extends Lint {
	public boolean applyFixCalled = false;
	
	public MockLint() {
		super(null, null);
	}

	public boolean applyFix(OWLOntologyManager manager) throws OWLOntologyChangeException {
		applyFixCalled = true;
		return true;
	}

}
