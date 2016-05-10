// Copyright (c) 2006 - 2010, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.taxonomy;

import aterm.ATerm;
import aterm.ATermAppl;
import com.clarkparsia.pellet.utils.CollectionUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.tbox.TBox;
import org.mindswap.pellet.tbox.impl.Unfolding;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * @author Evren Sirin
 */
public abstract class AbstractDefinitionOrder implements DefinitionOrder
{
	protected KnowledgeBase _kb;
	protected Comparator<ATerm> _comparator;

	private Set<ATermAppl> _cyclicConcepts;
	private List<ATermAppl> _definitionOrder;

	public AbstractDefinitionOrder(final KnowledgeBase kb, final Comparator<ATerm> comparator)
	{
		this._kb = kb;
		this._comparator = comparator;

		_cyclicConcepts = CollectionUtils.makeIdentitySet();
		_definitionOrder = new ArrayList<>(kb.getClasses().size() + 2);

		initialize();

		processDefinitions();

		_cyclicConcepts = computeCycles();

		_definitionOrder = computeDefinitionOrder();
	}

	protected abstract void initialize();

	protected abstract Set<ATermAppl> computeCycles();

	protected abstract List<ATermAppl> computeDefinitionOrder();

	protected void processDefinitions()
	{
		final boolean hasInverses = _kb.getExpressivity().hasInverse();
		final TBox tbox = _kb.getTBox();
		for (final ATermAppl c : _kb.getClasses())
		{
			final Iterator<Unfolding> unfoldingList = tbox.unfold(c);
			while (unfoldingList.hasNext())
			{
				final Unfolding unf = unfoldingList.next();
				final Set<ATermAppl> usedByC = ATermUtils.findPrimitives(unf.getResult(), !hasInverses, true);
				for (final ATermAppl used : usedByC)
				{
					if (!_kb.getClasses().contains(used))
						continue;

					addUses(c, used);
				}
			}
		}
	}

	protected abstract void addUses(ATermAppl c, ATermAppl usedByC);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCyclic(final ATermAppl concept)
	{
		return _cyclicConcepts.contains(concept);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<ATermAppl> iterator()
	{
		return _definitionOrder.iterator();
	}
}
