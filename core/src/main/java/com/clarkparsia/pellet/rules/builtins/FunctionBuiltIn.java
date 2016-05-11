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
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Literal;

/**
 * <p>
 * Title: Function Built-In
 * </p>
 * <p>
 * Description: A wrapper for built-ins that bind the first argument.
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
public class FunctionBuiltIn implements BuiltIn
{

	private class FunctionHelper implements BindingHelper
	{

		private final BuiltInAtom _atom;
		private AtomDObject _head;
		private Literal _value;
		private boolean _used;

		public FunctionHelper(final BuiltInAtom atom)
		{
			this._atom = atom;
		}

		@Override
		public Collection<? extends AtomVariable> getBindableVars(final Collection<AtomVariable> bound)
		{
			AtomDObject head = null;
			for (final AtomDObject obj : _atom.getAllArguments())
				if (head == null)
				{
					head = obj;
					// Can only bind first argument to a _function
					if (!VariableUtils.isVariable(head))
						return Collections.emptySet();
				}
				else
					// Cannot bind a variable that occurs in multiple places.
					if (head.equals(obj))
						return Collections.emptySet();
			if (head == null)
				return Collections.emptySet();
			return Collections.singleton((AtomVariable) head);
		}

		@Override
		public Collection<? extends AtomVariable> getPrerequisiteVars(final Collection<AtomVariable> bound)
		{
			final Collection<AtomVariable> vars = VariableUtils.getVars(_atom);
			vars.removeAll(getBindableVars(bound));
			return vars;
		}

		@Override
		public void rebind(final VariableBinding newBinding)
		{
			_used = false;
			_head = null;
			_value = null;
			Literal resultLit = null;

			// Can't bind the first _arg if it doesn't exist!
			if (_atom.getAllArguments().size() == 0)
				return;

			// The arguments to a numeric _function number one less than the arguments
			// to the SWRL _atom.  The first argument to the _atom is either set
			// or tested against the result of the _function.
			final Literal[] arguments = new Literal[_atom.getAllArguments().size() - 1];

			int i = 0;
			for (final AtomDObject obj : _atom.getAllArguments())
			{
				final Literal lit = newBinding.get(obj);

				if (i == 0)
				{
					if (lit != null)
						resultLit = lit;

					_head = obj;
					i++;
					continue;
				}

				arguments[i - 1] = lit;
				i++;
			}

			_value = _function.apply(newBinding.getABox(), resultLit, arguments);

		}

		@Override
		public boolean selectNextBinding()
		{
			if (_value != null && _used == false)
			{
				_used = true;
				return true;
			}
			return false;
		}

		@Override
		public void setCurrentBinding(final VariableBinding currentBinding)
		{
			currentBinding.set(_head, _value);
		}

	}

	private final Function _function;

	public FunctionBuiltIn(final Function function)
	{
		this._function = function;
	}

	@Override
	public BindingHelper createHelper(final BuiltInAtom atom)
	{
		return new FunctionHelper(atom);
	}

	@Override
	public boolean apply(final ABox abox, final Literal[] args)
	{
		final Literal result = _function.apply(abox, args[0], Arrays.copyOfRange(args, 1, args.length));
		args[0] = result;
		return result != null;
	}
}
