// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import aterm.ATermAppl;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.utils.ATermUtils;

/**
 */
public abstract class AlphaNode extends ReteNode
{
	protected static final Iterator<WME> NO_MATCH = Collections.<WME> emptyList().iterator();

	protected boolean doExplanation;

	protected final ABox abox;

	public AlphaNode(final ABox abox)
	{
		this.abox = abox;
	}

	public abstract Iterator<WME> getMatches(int argIndex, Node arg);

	public abstract Iterator<WME> getMatches();

	public abstract boolean matches(RuleAtom atom);

	protected Node initNode(final ATermAppl name)
	{
		if (ATermUtils.isLiteral(name))
			return abox.addLiteral(name);
		else
		{
			abox.copyOnWrite();
			return abox.getIndividual(name);
		}
	}

	protected void activate(final WME wme)
	{
		if (log.isLoggable(Level.FINE))
			log.fine("Activate alpha " + wme);
		for (final BetaNode beta : getBetas())
			beta.activate(wme);
	}

	public void setDoExplanation(final boolean doExplanation)
	{
		this.doExplanation = doExplanation;
	}

	@Override
	public void print(final String indent)
	{
		for (final BetaNode node : getBetas())
			if (node.isTop())
				node.print(indent);
	}
}
