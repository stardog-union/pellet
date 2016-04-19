// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.format;

import com.clarkparsia.pellint.model.Lint;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;

/**
 * <p>
 * Title: Compact Class Lint Format
 * </p>
 * <p>
 * Description: A compact formatter which only prints one participating OWLClass for a Lint without line breaks. If there are multiple participating OWLClasses,
 * it arbitrary chooses one.
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
public class CompactClassLintFormat implements LintFormat
{

	@Override
	public String format(final Lint lint)
	{
		final Set<OWLClass> participatingClasses = lint.getParticipatingClasses();
		if (participatingClasses == null || participatingClasses.isEmpty())
			return "";

		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(participatingClasses.iterator().next());
		strBuilder.append(' ');
		return strBuilder.toString();
	}

}
