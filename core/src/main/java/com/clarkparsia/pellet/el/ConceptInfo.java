// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.el;

import aterm.ATermAppl;
import com.clarkparsia.pellet.utils.CollectionUtils;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import net.katk.tools.Log;
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
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
class ConceptInfo
{
	public final static Logger logger = Log.getLogger(ConceptInfo.class);

	private final ATermAppl concept;
	private final Set<Trigger> triggers;

	private final MultiValueMap<ATermAppl, ConceptInfo> successors;
	private final MultiValueMap<ATermAppl, ConceptInfo> predecessors;

	private final Set<ConceptInfo> superClasses;

	public ConceptInfo(final ATermAppl c, final boolean storeSuccessors, final boolean noTriggers)
	{
		concept = c;

		superClasses = CollectionUtils.makeSet();

		successors = storeSuccessors ? new MultiValueMap<>() : null;
		predecessors = new MultiValueMap<>();

		triggers = noTriggers ? null : new HashSet<>();
	}

	public boolean addSuccessor(final ATermAppl p, final ConceptInfo ci)
	{
		if (ci.predecessors.add(p, this))
		{
			if (successors != null)
				successors.add(p, ci);

			return true;
		}

		return false;
	}

	public boolean addSuperClass(final ConceptInfo sup)
	{
		return superClasses.add(sup);
	}

	public boolean addTrigger(final Trigger trigger)
	{
		return triggers.add(trigger);
	}

	@Override
	public boolean equals(final Object obj)
	{
		return (obj instanceof ConceptInfo) && ((ConceptInfo) obj).concept == concept;
	}

	public ATermAppl getConcept()
	{
		return concept;
	}

	public MultiValueMap<ATermAppl, ConceptInfo> getSuccessors()
	{
		return successors;
	}

	public MultiValueMap<ATermAppl, ConceptInfo> getPredecessors()
	{
		return predecessors;
	}

	public Set<ConceptInfo> getSuperClasses()
	{
		return superClasses;
	}

	public Set<Trigger> getTriggers()
	{
		return triggers;
	}

	public boolean hasSuccessor(final ATermAppl p, final ConceptInfo ci)
	{
		return ci.predecessors.contains(p, this);
	}

	@Override
	public int hashCode()
	{
		return concept.hashCode();
	}

	public boolean hasSuperClass(final ConceptInfo ci)
	{
		return superClasses.contains(ci);
	}

	@Override
	public String toString()
	{
		return ATermUtils.toString(concept);
	}
}
