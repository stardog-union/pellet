// Copyright (c) 2006 - 2010, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import com.clarkparsia.pellet.rules.rete.NodeProvider.TokenNodeProvider;
import com.clarkparsia.pellet.rules.rete.NodeProvider.WMENodeProvider;

public class JoinCondition implements FilterCondition
{
	private final WMENodeProvider wmeProvider;
	private final TokenNodeProvider tokenProvider;

	public JoinCondition(final WMENodeProvider wme, final TokenNodeProvider token)
	{
		this.wmeProvider = wme;
		this.tokenProvider = token;
	}

	@Override
	public boolean test(final WME wme, final Token token)
	{
		return wmeProvider.getNode(wme, token).getTerm().equals(tokenProvider.getNode(wme, token).getTerm());
	}

	public WMENodeProvider getWME()
	{
		return wmeProvider;
	}

	public TokenNodeProvider getToken()
	{
		return tokenProvider;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + wmeProvider.hashCode();
		result = prime * result + tokenProvider.hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		if (!(obj instanceof JoinCondition))
			return false;
		final JoinCondition other = (JoinCondition) obj;
		return wmeProvider.equals(other.wmeProvider) && tokenProvider.equals(other.tokenProvider);
	}

	@Override
	public String toString()
	{
		return wmeProvider + "=" + tokenProvider;
	}
}
