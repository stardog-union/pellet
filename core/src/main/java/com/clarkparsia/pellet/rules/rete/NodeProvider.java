// Copyright (c) 2006 - 2010, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import org.mindswap.pellet.Node;
import org.mindswap.pellet.utils.ATermUtils;

public abstract class NodeProvider
{
	public abstract Node getNode(WME wme, Token token);

	public static class ConstantNodeProvider extends NodeProvider
	{
		private final Node _node;

		public ConstantNodeProvider(final Node node)
		{
			this._node = node;
		}

		@Override
		public Node getNode(final WME wme, final Token token)
		{
			return _node;
		}

		@Override
		public int hashCode()
		{
			return _node.getName().hashCode();
		}

		@Override
		public boolean equals(final Object obj)
		{
			if (this == obj)
				return true;
			if (!(obj instanceof ConstantNodeProvider))
				return false;
			final ConstantNodeProvider other = (ConstantNodeProvider) obj;
			return _node.getName().equals(other._node.getName());
		}

		@Override
		public String toString()
		{
			return ATermUtils.toString(_node.getName());
		}
	}

	public static class TokenNodeProvider extends NodeProvider
	{
		private final int _indexWME;
		private final int _indexArg;

		public TokenNodeProvider(final int indexWME, final int indexArg)
		{
			this._indexWME = indexWME;
			this._indexArg = indexArg;
		}

		@Override
		public Node getNode(final WME wme, final Token token)
		{
			return token.get(_indexWME).getArg(_indexArg);
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + _indexArg;
			result = prime * result + _indexWME;
			return result;
		}

		@Override
		public boolean equals(final Object obj)
		{
			if (this == obj)
				return true;
			if (!(obj instanceof TokenNodeProvider))
				return false;
			final TokenNodeProvider other = (TokenNodeProvider) obj;
			return (_indexArg != other._indexArg) && (_indexWME != other._indexWME);
		}

		@Override
		public String toString()
		{
			return "token[" + _indexWME + "]." + _indexArg;
		}
	}

	public static class WMENodeProvider extends NodeProvider
	{
		private final int _indexArg;

		public WMENodeProvider(final int indexArg)
		{
			this._indexArg = indexArg;
		}

		@Override
		public Node getNode(final WME wme, final Token token)
		{
			return wme.getArg(_indexArg);
		}

		public int getIndexArg()
		{
			return _indexArg;
		}

		@Override
		public int hashCode()
		{
			return _indexArg;
		}

		@Override
		public boolean equals(final Object obj)
		{
			if (this == obj)
				return true;
			if (!(obj instanceof WMENodeProvider))
				return false;
			final WMENodeProvider other = (WMENodeProvider) obj;
			return _indexArg == other._indexArg;
		}

		@Override
		public String toString()
		{
			return "wme." + _indexArg;
		}
	}
}
