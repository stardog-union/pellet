// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.expressivity;

import aterm.ATermAppl;
import com.clarkparsia.pellet.el.ELExpressivityChecker;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.utils.ATermUtils;

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
public class ExpressivityChecker
{
	private final KnowledgeBase _KB;
	private final ELExpressivityChecker _ELChecker;
	private final DLExpressivityChecker _DLChecker;
	private Expressivity _expressivity;

	public ExpressivityChecker(final KnowledgeBase kb)
	{
		this(kb, new Expressivity());
	}

	public ExpressivityChecker(final KnowledgeBase kb, final Expressivity expr)
	{
		_KB = kb;
		_ELChecker = new ELExpressivityChecker(_KB);
		_DLChecker = new DLExpressivityChecker(_KB);
		_expressivity = expr;
	}

	public void prepare()
	{
		_expressivity = new Expressivity();
		if (_ELChecker.compute(_expressivity))
			return;

		_expressivity = new Expressivity();
		// force expressivity to be non-EL
		_expressivity.setHasAllValues(true);
		_DLChecker.compute(_expressivity);
	}

	public Expressivity getExpressivity()
	{
		return _expressivity;
	}

	public Expressivity getExpressivityWith(final ATermAppl c)
	{
		if (c == null)
			return _expressivity;

		final Expressivity newExp = new Expressivity(_expressivity);
		_DLChecker.updateWith(newExp, c);

		return newExp;
	}

	/**
	 * Added for incremental reasoning. Given an aterm corresponding to an _individual and concept, the expressivity is updated accordingly.
	 */
	public void updateWithIndividual(final ATermAppl i, final ATermAppl concept)
	{
		final ATermAppl nominal = ATermUtils.makeValue(i);

		if (concept.equals(nominal))
			return;

		_DLChecker.updateWith(_expressivity, concept);
	}
}
