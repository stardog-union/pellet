// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tbox.impl;

import aterm.ATermAppl;
import java.util.Set;
import org.mindswap.pellet.utils.ATermUtils;

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
public abstract class Unfolding
{
	public abstract ATermAppl getCondition();

	public abstract Set<ATermAppl> getExplanation();

	public abstract ATermAppl getResult();

	public static Unfolding create(final ATermAppl result, final Set<ATermAppl> explanation)
	{
		return new Unconditional(result, explanation);
	}

	public static Unfolding create(final ATermAppl result, final ATermAppl condition, final Set<ATermAppl> explanation)
	{
		return new Conditional(result, condition, explanation);
	}

	private static class Unconditional extends Unfolding
	{
		private final ATermAppl result;

		private final Set<ATermAppl> explanation;

		private Unconditional(final ATermAppl result, final Set<ATermAppl> explanation)
		{
			this.result = result;
			this.explanation = explanation;
		}

		@Override
		public boolean equals(final Object obj)
		{
			if (this == obj)
				return true;
			if (!(obj instanceof Unfolding))
				return false;
			final Unconditional other = (Unconditional) obj;

			return getCondition().equals(other.getCondition()) && explanation.equals(other.explanation) && result.equals(other.result);
		}

		@Override
		public ATermAppl getCondition()
		{
			return ATermUtils.TOP;
		}

		@Override
		public Set<ATermAppl> getExplanation()
		{
			return explanation;
		}

		@Override
		public ATermAppl getResult()
		{
			return result;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int hashCode = 1;
			hashCode = prime * hashCode + getCondition().hashCode();
			hashCode = prime * hashCode + explanation.hashCode();
			hashCode = prime * hashCode + result.hashCode();
			return hashCode;
		}

		@Override
		public String toString()
		{
			return ATermUtils.toString(result);
		}
	}

	private static class Conditional extends Unconditional
	{
		private final ATermAppl condition;

		private Conditional(final ATermAppl result, final ATermAppl condition, final Set<ATermAppl> explanation)
		{
			super(result, explanation);

			this.condition = condition;
		}

		@Override
		public ATermAppl getCondition()
		{
			return condition;
		}

		@Override
		public String toString()
		{
			return ATermUtils.toString(condition) + " ? " + ATermUtils.toString(getResult());
		}
	}
}
