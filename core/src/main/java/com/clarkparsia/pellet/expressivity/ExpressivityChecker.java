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
	private final KnowledgeBase m_KB;
	private final ELExpressivityChecker m_ELChecker;
	private final DLExpressivityChecker m_DLChecker;
	private Expressivity m_Expressivity;

	public ExpressivityChecker(final KnowledgeBase kb)
	{
		this(kb, new Expressivity());
	}

	public ExpressivityChecker(final KnowledgeBase kb, final Expressivity expr)
	{
		m_KB = kb;
		m_ELChecker = new ELExpressivityChecker(m_KB);
		m_DLChecker = new DLExpressivityChecker(m_KB);
		m_Expressivity = expr;
	}

	public void prepare()
	{
		m_Expressivity = new Expressivity();
		if (m_ELChecker.compute(m_Expressivity))
			return;

		m_Expressivity = new Expressivity();
		// force expressivity to be non-EL
		m_Expressivity.setHasAllValues(true);
		m_DLChecker.compute(m_Expressivity);
	}

	public Expressivity getExpressivity()
	{
		return m_Expressivity;
	}

	public Expressivity getExpressivityWith(final ATermAppl c)
	{
		if (c == null)
			return m_Expressivity;

		final Expressivity newExp = new Expressivity(m_Expressivity);
		m_DLChecker.updateWith(newExp, c);

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

		m_DLChecker.updateWith(m_Expressivity, concept);
	}
}
