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
		protected final Individual subject;
		private final DependencySet depends;

		public AbstractWME(final Individual subject, final DependencySet depends)
		{
			this.subject = subject;
			this.depends = depends;
		}

		@Override
		public DependencySet getDepends()
		{
			return depends;
		}
	}

	private abstract static class BinaryWME extends AbstractWME
	{
		private final Individual object;

		public BinaryWME(final Individual subject, final Individual object, final DependencySet depends)
		{
			super(subject, depends);

			this.object = object;
		}

		@Override
		public Node getArg(final int index)
		{
			assert index == 0 || index == 1;
			return (index == 0) ? subject : object;
		}

		@Override
		public String toString()
		{
			return getKind() + "(" + subject + ", " + object + ")";
		}
	}

	public static class TypeWME extends AbstractWME
	{
		private final ATermAppl type;

		public TypeWME(final Individual subject, final ATermAppl type, final DependencySet depends)
		{
			super(subject, depends);

			this.type = type;
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
			return subject;
		}

		@Override
		public String toString()
		{
			return ATermUtils.toString(type) + "(" + subject + ")";
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
		private final Edge edge;
		private final EdgeDirection dir;

		public EdgeWME(final Edge edge, final EdgeDirection dir)
		{
			if (dir == null || dir == EdgeDirection.BOTH)
				throw new IllegalArgumentException();
			this.edge = edge;
			this.dir = dir;
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
			return (index == (dir == EdgeDirection.FORWARD ? 0 : 1)) ? edge.getFrom() : edge.getTo();
		}

		@Override
		public DependencySet getDepends()
		{
			return edge.getDepends();
		}

		@Override
		public String toString()
		{
			final boolean isFwd = (dir == EdgeDirection.FORWARD);
			return String.format("%s%s-%s-%s%s %s", edge.getFrom(), isFwd ? "" : "<", edge.getRole(), isFwd ? ">" : "", edge.getTo(), edge.getDepends());
		}
	}

	public static class BuiltinWME extends WME
	{
		private final Literal[] literals;
		private final DependencySet depends;

		public BuiltinWME(final Literal[] literals, final DependencySet depends)
		{
			this.literals = literals;
			this.depends = depends;
		}

		@Override
		public Kind getKind()
		{
			return Kind.BUILT_IN;
		}

		@Override
		public Node getArg(final int index)
		{
			return literals[index];
		}

		@Override
		public DependencySet getDepends()
		{
			return depends;
		}

		@Override
		public String toString()
		{
			return getKind() + Arrays.toString(literals);
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
