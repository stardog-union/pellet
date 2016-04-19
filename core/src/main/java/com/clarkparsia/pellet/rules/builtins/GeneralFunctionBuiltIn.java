// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.builtins;

import com.clarkparsia.pellet.rules.BindingHelper;
import com.clarkparsia.pellet.rules.VariableBinding;
import com.clarkparsia.pellet.rules.VariableUtils;
import com.clarkparsia.pellet.rules.model.AtomDObject;
import com.clarkparsia.pellet.rules.model.AtomDVariable;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.utils.SetUtils;

/**
 * <p>
 * Title: General Function BuiltIn
 * </p>
 * <p>
 * Description: A wrapper for built-ins that have one binding for a given set of variables.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Ron Alford
 */
public class GeneralFunctionBuiltIn implements BuiltIn
{

	private class GeneralFunctionHelper implements BindingHelper
	{

		private final BuiltInAtom atom;
		private VariableBinding partial;
		private boolean used;

		public GeneralFunctionHelper(final BuiltInAtom atom)
		{
			this.atom = atom;
		}

		@Override
		public Collection<? extends AtomVariable> getBindableVars(final Collection<AtomVariable> bound)
		{
			if (!isApplicable(bound))
				return Collections.emptySet();

			return SetUtils.difference(VariableUtils.getVars(atom), bound);
		}

		@Override
		public Collection<? extends AtomVariable> getPrerequisiteVars(final Collection<AtomVariable> bound)
		{
			final Collection<AtomVariable> vars = VariableUtils.getVars(atom);
			vars.removeAll(getBindableVars(bound));
			return vars;
		}

		private boolean isApplicable(final Collection<AtomVariable> bound)
		{
			final boolean[] boundPositions = new boolean[atom.getAllArguments().size()];
			for (int i = 0; i < boundPositions.length; i++)
				if (bound.contains(atom.getAllArguments().get(i)))
					boundPositions[i] = true;
				else
					boundPositions[i] = false;
			return function.isApplicable(boundPositions);
		}

		@Override
		public void rebind(final VariableBinding newBinding)
		{

			final Literal[] arguments = new Literal[atom.getAllArguments().size()];

			for (int i = 0; i < arguments.length; i++)
				arguments[i] = newBinding.get(atom.getAllArguments().get(i));

			if (function.apply(newBinding.getABox(), arguments))
			{
				final VariableBinding newPartial = new VariableBinding(newBinding.getABox());
				for (int i = 0; i < arguments.length; i++)
				{
					final AtomDObject arg = atom.getAllArguments().get(i);
					final Literal result = arguments[i];
					final Literal current = newBinding.get(arg);

					if (current != null && !current.equals(result))
					{
						// Oops, we overwrote an argument.
						if (newBinding.get(arg) != null)
							throw new InternalReasonerException("General Function implementation overwrote one of its arguments!");
						ABox.log.info("Function results in multiple simultaneous values for variable");
						return;
					}
					if (current == null)
						newBinding.set(arg, result);
				}

				used = false;
				partial = newPartial;
			}
			else
			{
				System.out.println("Function failure: " + atom);
				System.out.println("Arguments: " + Arrays.toString(arguments));
			}

		}

		@Override
		public boolean selectNextBinding()
		{
			if (partial != null && used == false)
			{
				used = true;
				return true;
			}
			return false;
		}

		@Override
		public void setCurrentBinding(final VariableBinding currentBinding)
		{
			for (final Map.Entry<AtomDVariable, Literal> entry : partial.dataEntrySet())
				currentBinding.set(entry.getKey(), entry.getValue());
		}

	}

	private final GeneralFunction function;

	public GeneralFunctionBuiltIn(final GeneralFunction function)
	{
		this.function = function;
	}

	@Override
	public BindingHelper createHelper(final BuiltInAtom atom)
	{
		return new GeneralFunctionHelper(atom);
	}

	@Override
	public boolean apply(final ABox abox, final Literal[] args)
	{
		return function.apply(abox, args);
	}
}
