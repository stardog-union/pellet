// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.el;

import com.clarkparsia.pellet.utils.CollectionUtils;
import java.util.Collections;
import java.util.Map.Entry;
import openllet.aterm.ATermAppl;
import java.util.Set;
import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.taxonomy.TaxonomyNode;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.MultiValueMap;

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
public class GenericTaxonomyBuilder
{
	private Taxonomy<ATermAppl> _taxonomy;

	private MultiValueMap<ATermAppl, ATermAppl> _subsumers;

	public Taxonomy<ATermAppl> build(final MultiValueMap<ATermAppl, ATermAppl> subsumers)
	{
		this._subsumers = subsumers;
		_taxonomy = new Taxonomy<>(null, ATermUtils.TOP, ATermUtils.BOTTOM);

		for (final ATermAppl subsumer : subsumers.get(ATermUtils.TOP))
			if (ATermUtils.isPrimitive(subsumer))
				_taxonomy.addEquivalentNode(subsumer, _taxonomy.getTop());

		for (final Entry<ATermAppl, Set<ATermAppl>> entry : subsumers.entrySet())
		{
			final ATermAppl c = entry.getKey();
			if (ATermUtils.isPrimitive(c))
				if (entry.getValue().contains(ATermUtils.BOTTOM))
					_taxonomy.addEquivalentNode(c, _taxonomy.getBottom());
				else
					add(c);
		}

		return _taxonomy;
	}

	private TaxonomyNode<ATermAppl> add(final ATermAppl c)
	{
		TaxonomyNode<ATermAppl> node = _taxonomy.getNode(c);

		if (node == null)
		{
			final Set<ATermAppl> equivalents = CollectionUtils.makeSet();
			final Set<TaxonomyNode<ATermAppl>> subsumerNodes = CollectionUtils.makeSet();

			for (final ATermAppl subsumer : _subsumers.get(c))
			{
				if (c.equals(subsumer) || !ATermUtils.isPrimitive(subsumer))
					continue;

				if (_subsumers.get(subsumer).contains(c))
					equivalents.add(subsumer);
				else
				{
					final TaxonomyNode<ATermAppl> supNode = add(subsumer);
					subsumerNodes.add(supNode);
				}
			}

			node = add(c, subsumerNodes);

			for (final ATermAppl eq : equivalents)
				_taxonomy.addEquivalentNode(eq, node);
		}

		return node;
	}

	private TaxonomyNode<ATermAppl> add(final ATermAppl c, final Set<TaxonomyNode<ATermAppl>> subsumers)
	{
		final Set<TaxonomyNode<ATermAppl>> parents = CollectionUtils.makeSet(subsumers);
		final Set<ATermAppl> supers = CollectionUtils.makeSet();
		final Set<ATermAppl> subs = Collections.singleton(ATermUtils.BOTTOM);

		for (final TaxonomyNode<ATermAppl> subsumer : subsumers)
			parents.removeAll(subsumer.getSupers());

		for (final TaxonomyNode<ATermAppl> parent : parents)
		{
			supers.add(parent.getName());
			parent.removeSub(_taxonomy.getBottom());
		}

		return _taxonomy.addNode(Collections.singleton(c), supers, subs, false);
	}
}
