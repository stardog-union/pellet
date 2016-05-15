package com.clarkparsia.owlwg.owlapi.testcase.impl;

import com.clarkparsia.owlwg.testcase.TestCaseVisitor;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * <p>
 * Title: OWLAPI Negative Entailment Test Case
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
public class OwlApiNegTstImpl extends OwlApiETImpl implements com.clarkparsia.owlwg.testcase.NegativeEntailmentTest<OWLOntology>
{

	public OwlApiNegTstImpl(OWLOntology ontology, OWLNamedIndividual i)
	{
		super(ontology, i, false);
	}

	@Override
	public void accept(TestCaseVisitor<OWLOntology> visitor)
	{
		visitor.visit(this);
	}

	@Override
	public String toString()
	{
		return String.format("NegativeEntailmentTest(%s)", getIdentifier());
	}
}
