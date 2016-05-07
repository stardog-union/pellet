// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;

/**
 * <p>
 * Title: All Named Individuals Iterator
 * </p>
 * <p>
 * Description: Iterates over all named individuals in the _abox,
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Ron Alford
 */
public class AllNamedIndividualsIterator implements Iterator<Individual>
{

	private Individual nextIndividual;
	private final Iterator<Individual> nodeIterator;

	public AllNamedIndividualsIterator(final ABox abox)
	{
		nodeIterator = abox.getIndIterator();
	}

	@Override
	public boolean hasNext()
	{
		if (nextIndividual != null)
			return true;

		while (nodeIterator.hasNext())
		{
			final Node candidate = nodeIterator.next();
			if ((candidate instanceof Individual) && candidate.isRootNominal())
			{
				nextIndividual = (Individual) candidate;
				return true;
			}
		}

		return false;
	}

	@Override
	public Individual next()
	{
		if (!hasNext())
			throw new NoSuchElementException();
		final Individual result = nextIndividual;
		nextIndividual = null;
		return result;
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}

}
