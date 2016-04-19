// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.format;

import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.model.Severity;
import java.util.Iterator;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;

/**
 * <p>
 * Title: Simple Lint Format
 * </p>
 * <p>
 * Description: The default Lint Format that tries to print a short but informative content for a Lint.
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
public class SimpleLintFormat implements LintFormat
{

	private static final int CLASSES_LIMIT = 6;

	@Override
	public String format(final Lint lint)
	{
		final Set<OWLClass> participatingClasses = lint.getParticipatingClasses();
		final Set<OWLClassAxiom> participatingAxioms = lint.getParticipatingAxioms();
		if ((participatingClasses == null || participatingClasses.isEmpty()) && (participatingAxioms == null || participatingAxioms.isEmpty()))
			return "";

		final Severity severity = lint.getSeverity();
		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(" - ");

		if (severity != null)
			strBuilder.append(severity).append(' ');

		if (participatingClasses != null && !participatingClasses.isEmpty())
		{
			int i = 0;
			for (final Iterator<OWLClass> it = participatingClasses.iterator(); it.hasNext() && i < CLASSES_LIMIT; i++)
			{
				final OWLClass participatingClass = it.next();
				strBuilder.append(participatingClass).append(' ');
			}

			if (participatingClasses.size() > CLASSES_LIMIT)
			{
				strBuilder.append("... and ");
				strBuilder.append(participatingClasses.size() - CLASSES_LIMIT);
				strBuilder.append(" more.");
			}

			strBuilder.append('\n');
		}
		else
			if (participatingAxioms != null && !participatingAxioms.isEmpty())
				for (final OWLClassAxiom axiom : participatingAxioms)
					strBuilder.append(axiom).append('\n');

		return strBuilder.toString();
	}

}
