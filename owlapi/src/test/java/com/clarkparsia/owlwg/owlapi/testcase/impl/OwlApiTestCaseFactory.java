package com.clarkparsia.owlwg.owlapi.testcase.impl;

import com.clarkparsia.owlwg.testcase.TestCaseFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * <p>
 * Title: OWLAPI Test Case Factory
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright &copy; 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <a href="http://clarkparsia.com/"/>http://clarkparsia.com/</a>
 * </p>
 * 
 * @author Mike Smith &lt;msmith@clarkparsia.com&gt;
 */
public class OwlApiTestCaseFactory implements TestCaseFactory<OWLOntology>
{

	@Override
	public OwlApiConTstImpl getConsistencyTestCase(OWLOntology o, OWLNamedIndividual i)
	{
		return new OwlApiConTstImpl(o, i);
	}

	@Override
	public OwlApiIncTstImpl getInconsistencyTestCase(OWLOntology o, OWLNamedIndividual i)
	{
		return new OwlApiIncTstImpl(o, i);
	}

	@Override
	public OwlApiNegTstImpl getNegativeEntailmentTestCase(OWLOntology o, OWLNamedIndividual i)
	{
		return new OwlApiNegTstImpl(o, i);
	}

	@Override
	public OwlApiPosTstImpl getPositiveEntailmentTestCase(OWLOntology o, OWLNamedIndividual i)
	{
		return new OwlApiPosTstImpl(o, i);
	}

}
