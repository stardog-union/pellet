// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tbox.impl;

import aterm.ATermAppl;
import com.clarkparsia.pellet.utils.CollectionUtils;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.katk.tools.Log;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.BinarySet;
import org.mindswap.pellet.utils.iterator.IteratorUtils;

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
public class BinaryTBox
{
	public static final Logger log = Log.getLogger(UnaryTBox.class);

	private final Map<BinarySet<ATermAppl>, Unfolding> _unfoldings;
	private final Map<ATermAppl, List<Unfolding>> _conditionalUnfoldings;

	public BinaryTBox()
	{
		_unfoldings = CollectionUtils.makeMap();
		_conditionalUnfoldings = CollectionUtils.makeIdentityMap();
	}

	public void add(final BinarySet<ATermAppl> set, ATermAppl result, final Set<ATermAppl> explanation)
	{
		if (log.isLoggable(Level.FINE))
			log.fine("Add sub: (" + ATermUtils.toString(set.first()) + ", " + ATermUtils.toString(set.second()) + ") < " + ATermUtils.toString(result));

		result = ATermUtils.normalize(result);

		_unfoldings.put(set, Unfolding.create(result, explanation));

		addUnfolding(set.first(), set.second(), result, explanation);
		addUnfolding(set.second(), set.first(), result, explanation);
	}

	private void addUnfolding(final ATermAppl c, final ATermAppl condition, final ATermAppl result, final Set<ATermAppl> explanation)
	{
		List<Unfolding> list = _conditionalUnfoldings.get(c);
		if (list == null)
		{
			list = CollectionUtils.makeList();
			_conditionalUnfoldings.put(c, list);
		}
		list.add(Unfolding.create(result, condition, explanation));
	}

	public Unfolding unfold(final BinarySet<ATermAppl> set)
	{
		return _unfoldings.get(set);
	}

	public Iterator<Unfolding> unfold(final ATermAppl concept)
	{
		final List<Unfolding> unfoldingList = _conditionalUnfoldings.get(concept);
		return unfoldingList == null ? IteratorUtils.<Unfolding> emptyIterator() : unfoldingList.iterator();
	}

	public boolean contains(final ATermAppl concept)
	{
		return _conditionalUnfoldings.containsKey(concept);
	}

	public void print(final Appendable out) throws IOException
	{
		for (final Entry<BinarySet<ATermAppl>, Unfolding> e : _unfoldings.entrySet())
		{
			final BinarySet<ATermAppl> set = e.getKey();
			out.append("(");
			out.append(ATermUtils.toString(set.first()));
			out.append(",");
			out.append(ATermUtils.toString(set.second()));
			out.append(") < ");
			out.append(e.getValue().toString());
			out.append("\n");
		}
	}
}
