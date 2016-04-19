// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.rule;

import java.util.logging.Logger;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.IndividualIterator;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;
import org.mindswap.pellet.tableau.completion.queue.QueueElement;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public abstract class AbstractTableauRule implements TableauRule
{
	public final static Logger log = Logger.getLogger(AbstractTableauRule.class.getName());

	protected enum BlockingType
	{
		NONE, DIRECT, INDIRECT, COMPLETE
	}

	protected CompletionStrategy strategy;
	protected NodeSelector nodeSelector;
	protected BlockingType blockingType;

	public AbstractTableauRule(final CompletionStrategy strategy, final NodeSelector nodeSelector, final BlockingType blockingType)
	{
		this.strategy = strategy;
		this.nodeSelector = nodeSelector;
		this.blockingType = blockingType;
	}

	public boolean isDisabled()
	{
		return false;
	}

	@Override
	public void apply(final IndividualIterator i)
	{
		i.reset(nodeSelector);
		while (i.hasNext())
		{
			final Individual node = i.next();

			if (strategy.getBlocking().isBlocked(node))
			{
				if (PelletOptions.USE_COMPLETION_QUEUE)
					addQueueElement(node);
			}
			else
			{
				apply(node);

				if (strategy.getABox().isClosed())
					return;
			}
		}
	}

	protected boolean isBlocked(final Individual node)
	{
		switch (blockingType)
		{
			case NONE:
				return false;
			case DIRECT:
				return strategy.getBlocking().isDirectlyBlocked(node);
			case INDIRECT:
				return strategy.getBlocking().isIndirectlyBlocked(node);
			case COMPLETE:
				return strategy.getBlocking().isBlocked(node);
			default:
				throw new AssertionError();
		}
	}

	protected void addQueueElement(final Node node)
	{
		strategy.getABox().getCompletionQueue().add(new QueueElement(node), nodeSelector);
	}
}
