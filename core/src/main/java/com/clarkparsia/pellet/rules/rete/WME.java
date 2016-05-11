// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import aterm.ATermAppl;
import java.util.Arrays;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.utils.ATermUtils;

/**
 */
public abstract class WME
{
	public enum Kind
	{
		TYPE, EDGE, SAME_AS, DIFF_FROM, BUILT_IN
	}

	public abstract Node getArg(int index);

	public abstract DependencySet getDepends();

	public boolean dependsOn(final int branch)
	{
		return getDepends().max() > branch;
	}

	public abstract Kind getKind();

	private abstract static class AbstractWME extends WME
	{
		protected final Individual _subject;
		private final DependencySet _depends;

		public AbstractWME(final Individual subject, final DependencySet depends)
		{
			this._subject = subject;
			this._depends = depends;
		}

		@Override
		public DependencySet getDepends()
		{
			return _depends;
		}
	}

	private abstract static class BinaryWME extends AbstractWME
	{
		private final Individual _object;

		public BinaryWME(final Individual subject, final Individual object, final DependencySet depends)
		{
			super(subject, depends);

			this._object = object;
		}

		@Override
		public Node getArg(final int index)
		{
			assert index == 0 || index == 1;
			return (index == 0) ? _subject : _object;
		}

		@Override
		public String toString()
		{
			return getKind() + "(" + _subject + ", " + _object + ")";
		}
	}

	public static class TypeWME extends AbstractWME
	{
		private final ATermAppl _type;

		public TypeWME(final Individual subject, final ATermAppl type, final DependencySet depends)
		{
			super(subject, depends);

			this._type = type;
		}

		@Override
		public Kind getKind()
		{
			return Kind.TYPE;
		}

		@Override
		public Node getArg(final int index)
		{
			assert index == 0;
			return _subject;
		}

		@Override
		public String toString()
		{
			return ATermUtils.toString(_type) + "(" + _subject + ")";
		}
	}

	public static class SameAs extends BinaryWME
	{
		public SameAs(final Individual subject, final Individual object, final DependencySet depends)
		{
			super(subject, object, depends);
		}

		@Override
		public Kind getKind()
		{
			return Kind.SAME_AS;
		}
	}

	public static class DiffFrom extends BinaryWME
	{
		public DiffFrom(final Individual subject, final Individual object, final DependencySet depends)
		{
			super(subject, object, depends);
		}

		@Override
		public Kind getKind()
		{
			return Kind.DIFF_FROM;
		}
	}

	public static class EdgeWME extends WME
	{
		private final Edge _edge;
		private final EdgeDirection _dir;

		public EdgeWME(final Edge edge, final EdgeDirection dir)
		{
			if (dir == null || dir == EdgeDirection.BOTH)
				throw new IllegalArgumentException();
			this._edge = edge;
			this._dir = dir;
		}

		@Override
		public Kind getKind()
		{
			return Kind.EDGE;
		}

		@Override
		public Node getArg(final int index)
		{
			assert index == 0 || index == 1;
			return (index == (_dir == EdgeDirection.FORWARD ? 0 : 1)) ? _edge.getFrom() : _edge.getTo();
		}

		@Override
		public DependencySet getDepends()
		{
			return _edge.getDepends();
		}

		@Override
		public String toString()
		{
			final boolean isFwd = (_dir == EdgeDirection.FORWARD);
			return String.format("%s%s-%s-%s%s %s", _edge.getFrom(), isFwd ? "" : "<", _edge.getRole(), isFwd ? ">" : "", _edge.getTo(), _edge.getDepends());
		}
	}

	public static class BuiltinWME extends WME
	{
		private final Literal[] _literals;
		private final DependencySet _depends;

		public BuiltinWME(final Literal[] literals, final DependencySet depends)
		{
			this._literals = literals;
			this._depends = depends;
		}

		@Override
		public Kind getKind()
		{
			return Kind.BUILT_IN;
		}

		@Override
		public Node getArg(final int index)
		{
			return _literals[index];
		}

		@Override
		public DependencySet getDepends()
		{
			return _depends;
		}

		@Override
		public String toString()
		{
			return getKind() + Arrays.toString(_literals);
		}
	}

	public enum EdgeDirection
	{
		FORWARD, BACKWARD, BOTH
	}

	public static TypeWME createType(final Individual arg, final ATermAppl type, final DependencySet depends)
	{
		return new TypeWME(arg, type, depends);
	}

	public static DiffFrom createDiffFrom(final Individual subject, final Individual object, final DependencySet depends)
	{
		return new DiffFrom(subject, object, depends);
	}

	public static WME createEdge(final Edge edge)
	{
		return new EdgeWME(edge, EdgeDirection.FORWARD);
	}

	public static WME createEdge(final Edge edge, final EdgeDirection dir)
	{
		return new EdgeWME(edge, dir);
	}

	public static WME createBuiltin(final Literal[] literals, final DependencySet ds)
	{
		return new BuiltinWME(literals, ds);
	}
}
