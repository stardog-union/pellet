// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.explanation.test;

import static org.junit.Assert.assertNotNull;

import com.clarkparsia.owlapi.OntologyUtils;
import com.clarkparsia.owlapi.explanation.ExplanationGenerator;
import com.clarkparsia.owlapi.explanation.SatisfiabilityConverter;
import com.clarkparsia.owlapi.explanation.io.ConciseExplanationRenderer;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.katk.tools.Log;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public class ExplanationTester
{
	private static final Logger _logger = Log.getLogger(ExplanationTester.class);

	private final SatisfiabilityConverter _converter;
	private final ConciseExplanationRenderer _renderer;
	private final ExplanationGenerator _expGen;

	private int _axiomCount = 0;

	public ExplanationTester(final ExplanationGenerator expGen)
	{
		this._expGen = expGen;

		_converter = new SatisfiabilityConverter(OntologyUtils.getOWLOntologyManager().getOWLDataFactory());
		_renderer = new ConciseExplanationRenderer();
	}

	public void testExplanations(final OWLAxiom axiom, final int max, final Set<Set<OWLAxiom>> expectedExplanations)
	{
		final OWLClassExpression unsatClass = _converter.convert(axiom);

		if (_logger.isLoggable(Level.FINE))
			_logger.fine("Axiom " + (++_axiomCount) + ": " + axiom + " Expecting " + expectedExplanations.size() + " explanations");

		final Set<Set<OWLAxiom>> generatedExplanations = _expGen.getExplanations(unsatClass, max);
		final Set<Set<OWLAxiom>> notFoundExplanations = new HashSet<>(expectedExplanations);

		if (_logger.isLoggable(Level.FINER))
		{
			final StringWriter sw = new StringWriter();
			_renderer.startRendering(sw);
			_renderer.render(axiom, expectedExplanations);
			_renderer.endRendering();
			_logger.finer("Expected:\n" + sw);
		}

		assertNotNull("Axiom " + axiom + " not entailed", generatedExplanations);

		final Set<Set<OWLAxiom>> unexpectedExplanations = new HashSet<>();
		for (final Set<OWLAxiom> explanation : generatedExplanations)
			if (!notFoundExplanations.remove(explanation))
				unexpectedExplanations.add(explanation);

		if (!notFoundExplanations.isEmpty() || !unexpectedExplanations.isEmpty())
		{
			final StringWriter sw = new StringWriter();
			final ConciseExplanationRenderer renderer = new ConciseExplanationRenderer();
			renderer.startRendering(sw);
			sw.getBuffer().append("\nExpected:\n");
			renderer.render(axiom, expectedExplanations);
			if (!notFoundExplanations.isEmpty())
			{
				sw.getBuffer().append("Not Found:\n");
				renderer.render(axiom, notFoundExplanations);
			}
			if (!unexpectedExplanations.isEmpty())
			{
				sw.getBuffer().append("Unexpected:\n");
				renderer.render(axiom, unexpectedExplanations);
			}
			renderer.endRendering();

			_logger.severe("Error in explanation: " + sw);

			org.junit.Assert.fail("Error in explanation, see the _logger file for details");
		}
	}
}
