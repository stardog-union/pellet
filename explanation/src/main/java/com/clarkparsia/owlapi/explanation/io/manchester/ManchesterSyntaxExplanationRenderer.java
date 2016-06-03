// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.owlapi.explanation.io.manchester;

import com.clarkparsia.owlapi.explanation.io.ExplanationRenderer;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLException;

/**
 * An explanation renderer implementation that prints the axioms in the explanation using Manchester syntax.
 *
 * @author Evren Sirin
 */
public class ManchesterSyntaxExplanationRenderer implements ExplanationRenderer
{
	protected ManchesterSyntaxObjectRenderer _renderer;

	protected BlockWriter _writer;

	protected OWLAxiom _currentAxiom;

	private boolean _wrapLines = true;

	private boolean _smartIndent = true;

	private int _index;

	public ManchesterSyntaxExplanationRenderer()
	{
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endRendering()
	{
		_writer.flush();
	}

	/**
	 * Returns the _current axioms being whose explanation is being rendered or <code>null</code> if no axiom has been provided.
	 *
	 * @return the _current axioms being whose explanation is being rendered or <code>null</code> if no axiom has been provided
	 */
	protected OWLAxiom getCurrentAxiom()
	{
		return _currentAxiom;
	}

	/**
	 * Returns the _current smart indent value.
	 *
	 * @return the _current smart indent value
	 */
	public boolean isSmartIndent()
	{
		return _smartIndent;
	}

	/**
	 * Returns the _current line wrapping value.
	 *
	 * @return the _current line wrapping value
	 */
	public boolean isWrapLines()
	{
		return _wrapLines;
	}

	/**
	 * Render an explanation without the axiom header. This function is not guaranteed to be supported by the subclasses since an explanation renderer may rely
	 * on the axiom being explained to reorder the axioms or find irrelevant bits.
	 *
	 * @param explanations Set of explanations we are rendering
	 * @throws OWLException
	 * @throws IOException
	 * @throws UnsupportedOperationException
	 */
	public void render(final Set<Set<OWLAxiom>> explanations) throws OWLException, IOException, UnsupportedOperationException
	{
		render((OWLAxiom) null, explanations);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void render(final OWLAxiom axiom, final Set<Set<OWLAxiom>> explanations) throws OWLException, IOException
	{
		setCurrentAxiom(axiom);

		if (_index == 1)
		{
			if (axiom != null)
			{
				_writer.print("Axiom: ");
				axiom.accept(_renderer);
				_writer.println();
				_writer.println();
			}
			if (explanations.isEmpty())
			{
				_writer.println("Explanation: AXIOM IS NOT ENTAILED!");
				return;
			}
			_writer.println("Explanation(s): ");
		}

		final String header = _index++ + ")";
		_writer.print(header);
		renderSingleExplanation(explanations.iterator().next());
		_writer.println();
	}

	protected void renderSingleExplanation(final Set<OWLAxiom> explanation)
	{
		_writer.printSpace();
		_writer.printSpace();
		_writer.printSpace();

		_writer.startBlock();

		for (final OWLAxiom a : explanation)
		{
			a.accept(_renderer);
			_writer.println();
		}

		_writer.endBlock();
		_writer.println();
	}

	protected void setCurrentAxiom(final OWLAxiom currentAxiom)
	{
		this._currentAxiom = currentAxiom;
	}

	/**
	 * Sets the smart indent option which will align the elements of intersections and unions in columns when line wrapping is turned on.
	 *
	 * @param _smartIndent the smart indent value
	 * @see #setWrapLines(boolean)
	 */
	public void setSmartIndent(final boolean smartIndent)
	{
		this._smartIndent = smartIndent;
	}

	/**
	 * Sets the line wrapping option which will print the elements of intersections and unions into multiple lines.
	 *
	 * @param _wrapLines the line wrapping value
	 */
	public void setWrapLines(final boolean wrapLines)
	{
		this._wrapLines = wrapLines;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startRendering(final Writer w)
	{
		_writer = new TextBlockWriter(w);
		_renderer = new ManchesterSyntaxObjectRenderer(this._writer);
		_renderer.setWrapLines(isWrapLines());
		_renderer.setSmartIndent(isSmartIndent());
		_index = 1;
	}
}
