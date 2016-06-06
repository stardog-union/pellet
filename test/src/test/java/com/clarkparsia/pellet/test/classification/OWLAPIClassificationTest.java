// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.classification;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.pellet.owlapi.PelletReasoner;
import com.clarkparsia.pellet.owlapi.PelletReasonerFactory;
import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

/**
 * @author Evren Sirin
 */
public class OWLAPIClassificationTest extends AbstractClassificationTest
{
	@Override
	public void testClassification(final String inputOnt, final String classifiedOnt) throws OWLOntologyCreationException
	{
		final OWLOntology premise = OWL.manager.loadOntology(IRI.create(inputOnt));
		final OWLOntology conclusion = OWL.manager.loadOntology(IRI.create(classifiedOnt));

		try
		{
			final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(premise);
			reasoner.getKB().classify();

			final List<OWLAxiom> nonEntailments = new ArrayList<>();

			{
				final Iterable<OWLSubClassOfAxiom> it = conclusion.axioms(AxiomType.SUBCLASS_OF)::iterator;
				for (final OWLSubClassOfAxiom axiom : it)
				{
					final boolean entailed = reasoner.getSubClasses(axiom.getSuperClass(), true).containsEntity((OWLClass) axiom.getSubClass());

					if (!entailed)
						if (AbstractClassificationTest.FAIL_AT_FIRST_ERROR)
							fail("Not entailed: " + axiom);
						else
							nonEntailments.add(axiom);
				}
			}

			{
				final Iterable<OWLEquivalentClassesAxiom> it = conclusion.axioms(AxiomType.EQUIVALENT_CLASSES)::iterator;
				for (final OWLEquivalentClassesAxiom axiom : it)
				{
					final boolean entailed = reasoner.isEntailed(axiom);

					if (!entailed)
						if (AbstractClassificationTest.FAIL_AT_FIRST_ERROR)
							fail("Not entailed: " + axiom);
						else
							nonEntailments.add(axiom);
				}
			}

			{
				final Iterable<OWLClassAssertionAxiom> it = conclusion.axioms(AxiomType.CLASS_ASSERTION)::iterator;
				for (final OWLClassAssertionAxiom axiom : it)
				{
					final boolean entailed = reasoner.getInstances(axiom.getClassExpression(), true).containsEntity((OWLNamedIndividual) axiom.getIndividual());

					if (!entailed)
						if (AbstractClassificationTest.FAIL_AT_FIRST_ERROR)
							fail("Not entailed: " + axiom);
						else
							nonEntailments.add(axiom);
				}
			}

			assertTrue(nonEntailments.size() + " " + nonEntailments.toString(), nonEntailments.isEmpty());
		}
		finally
		{
			OWL.manager.removeOntology(premise);
			OWL.manager.removeOntology(conclusion);
		}
	}

}
