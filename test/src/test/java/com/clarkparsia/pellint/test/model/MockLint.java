package com.clarkparsia.pellint.test.model;

import com.clarkparsia.pellint.model.Lint;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

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
public class MockLint extends Lint
{
	public boolean _applyFixCalled = false;

	public MockLint()
	{
		super(null, null);
	}

	@Override
	public boolean applyFix(final OWLOntologyManager manager) throws OWLOntologyChangeException
	{
		_applyFixCalled = true;
		return true;
	}

}
