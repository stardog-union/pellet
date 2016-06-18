// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tbox.impl;

import static com.clarkparsia.pellet.utils.TermFactory.not;

import aterm.ATermAppl;
import com.clarkparsia.pellet.utils.CollectionUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import openllet.shared.tools.Log;
import org.mindswap.pellet.utils.ATermUtils;
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
public class PrimitiveTBox
{
	public static final Logger _logger = Log.getLogger(PrimitiveTBox.class);

	private final Map<ATermAppl, Unfolding> _definitions;
	private final Map<ATermAppl, Set<ATermAppl>> _dependencies;

	public PrimitiveTBox()
	{
		_definitions = CollectionUtils.makeIdentityMap();
		_dependencies = CollectionUtils.makeIdentityMap();
	}

	public boolean contains(final ATermAppl concept)
	{
		return _definitions.containsKey(concept);
	}

	public Unfolding getDefinition(final ATermAppl concept)
	{
		return _definitions.get(concept);
	}

	//	public boolean add(ATermAppl axiom, Set<ATermAppl> clashExplanation) {
	//		boolean added = false;
	//		
	//		if( axiom.getAFun().equals( ATermUtils.EQCLASSFUN ) ) {
	//			ATermAppl c1 = (ATermAppl) axiom.getArgument( 0 );
	//			ATermAppl c2 = (ATermAppl) axiom.getArgument( 1 );
	//		
	//			added = addDefinition( c1, c2, clashExplanation );
	//			if( !added ) {
	//				added = addDefinition( c2, c1, clashExplanation );
	//			}			
	//		}
	//		
	//		return added;
	//	}

	public boolean add(final ATermAppl concept, final ATermAppl definition, final Set<ATermAppl> explanation)
	{
		if (!ATermUtils.isPrimitive(concept) || contains(concept))
			return false;

		final Set<ATermAppl> deps = ATermUtils.findPrimitives(definition);
		final Set<ATermAppl> seen = new HashSet<>();

		for (final ATermAppl current : deps)
		{
			final boolean result = findTarget(current, concept, seen);
			if (result)
				return false;
		}

		addDefinition(concept, definition, explanation);
		addDefinition(not(concept), not(definition), explanation);
		_dependencies.put(concept, deps);

		return true;
	}

	protected void addDefinition(final ATermAppl concept, ATermAppl definition, final Set<ATermAppl> explanation)
	{
		definition = ATermUtils.normalize(definition);

		if (_logger.isLoggable(Level.FINE))
			_logger.fine("Def: " + ATermUtils.toString(concept) + " = " + ATermUtils.toString(definition));

		_definitions.put(concept, Unfolding.create(definition, explanation));

	}

	protected boolean findTarget(final ATermAppl term, final ATermAppl target, final Set<ATermAppl> seen)
	{
		final List<ATermAppl> queue = new ArrayList<>();
		queue.add(term);

		while (!queue.isEmpty())
		{
			final ATermAppl current = queue.remove(queue.size() - 1);

			if (!seen.add(current))
				continue;

			if (current.equals(target))
				return true;

			final Set<ATermAppl> deps = _dependencies.get(current);
			if (deps != null)
			{
				// Shortcut
				if (deps.contains(target))
					return true;

				queue.addAll(deps);
			}
		}

		return false;
	}

	public boolean remove(@SuppressWarnings("unused") final ATermAppl axiom)
	{
		return false;
	}

	public Iterator<Unfolding> unfold(final ATermAppl concept)
	{
		final Unfolding unfolding = _definitions.get(concept);

		return unfolding == null ? IteratorUtils.<Unfolding> emptyIterator() : IteratorUtils.singletonIterator(unfolding);
	}

	public void print(final Appendable out) throws IOException
	{
		for (final Entry<ATermAppl, Unfolding> e : _definitions.entrySet())
		{
			out.append(ATermUtils.toString(e.getKey()));
			out.append(" = ");
			out.append(e.getValue().toString());
			out.append("\n");
		}
	}
}
