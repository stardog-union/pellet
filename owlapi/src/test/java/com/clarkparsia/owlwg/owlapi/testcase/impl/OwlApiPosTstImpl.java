package com.clarkparsia.owlwg.owlapi.testcase.impl;

import com.clarkparsia.owlwg.testcase.PositiveEntailmentTest;
import com.clarkparsia.owlwg.testcase.TestCaseVisitor;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * <p>
 * Title: OWLAPI Positive Entailment Test Case
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
public class OwlApiPosTstImpl extends OwlApiETImpl implements PositiveEntailmentTest<OWLOntology>
{

	public OwlApiPosTstImpl(OWLOntology ontology, OWLNamedIndividual i)
	{
		super(ontology, i, true);
	}

	@Override
	public void accept(TestCaseVisitor<OWLOntology> visitor)
	{
		visitor.visit(this);
	}

	@Override
	public String toString()
	{
		return String.format("PositiveEntailmentTest(%s)", getIdentifier());
	}
}
