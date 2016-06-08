// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.el;

import java.util.Arrays;

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
public class Trigger
{
	private final ConceptInfo[] _premises;
	private final ConceptInfo _consequence;

	public Trigger(final ConceptInfo[] premises, final ConceptInfo consequence)
	{
		_premises = premises;
		_consequence = consequence;
	}

	public Trigger(final ConceptInfo consequence)
	{
		this(new ConceptInfo[0], consequence);
	}

	public ConceptInfo[] getPremises()
	{
		return _premises;
	}

	public ConceptInfo getConsequence()
	{
		return _consequence;
	}

	public boolean isTriggered(final ConceptInfo ci)
	{
		for (final ConceptInfo premise : _premises)
			if (!ci.hasSuperClass(premise))
				return false;
		return true;
	}

	@Override
	public String toString()
	{
		return Arrays.toString(_premises) + " -> " + _consequence;
	}
}
