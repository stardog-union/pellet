// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

/**
 * @author Evren Sirin
 */
public abstract class BetaNode extends ReteNode
{
	public abstract void activate(WME wme);

	public abstract void activate(Token token);

	public boolean isTop()
	{
		return false;
	}

	protected void activateChildren(final WME wme, final Token token)
	{
		final Token newToken = Token.create(wme, token);
		for (final BetaNode beta : getBetas())
			beta.activate(newToken);
	}
}
