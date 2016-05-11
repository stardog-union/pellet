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
import java.util.Collection;
import java.util.Collections;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Literal;

/**
 * <p>
 * Title: Test Built-In
 * </p>
 * <p>
 * Description: An implementation of BuiltInFunction for Tests.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Ron Alford
 */
public class TesterBuiltIn implements BuiltIn
{

	private class TestHelper implements BindingHelper
	{

		private final BuiltInAtom _atom;
		private boolean _result;

		public TestHelper(final BuiltInAtom atom)
		{
			this._atom = atom;
			_result = false;
		}

		@Override
		public Collection<? extends AtomVariable> getBindableVars(final Collection<AtomVariable> bound)
		{
			return Collections.emptySet();
		}

		@Override
		public Collection<? extends AtomVariable> getPrerequisiteVars(final Collection<AtomVariable> bound)
		{
			return VariableUtils.getVars(_atom);
		}

		@Override
		public void rebind(final VariableBinding newBinding)
		{
			final Literal[] arguments = new Literal[_atom.getAllArguments().size()];
			int i = 0;
			for (final AtomDObject obj : _atom.getAllArguments())
				arguments[i++] = newBinding.get(obj);
			_result = _test.test(arguments);
		}

		@Override
		public boolean selectNextBinding()
		{
			if (_result)
			{
				_result = false;
				return true;
			}
			return false;
		}

		@Override
		public void setCurrentBinding(final VariableBinding currentBinding)
		{
			// Nothing to do.
		}

	}

	private final Tester _test;

	public TesterBuiltIn(final Tester test)
	{
		this._test = test;
	}

	@Override
	public BindingHelper createHelper(final BuiltInAtom atom)
	{
		return new TestHelper(atom);
	}

	@Override
	public boolean apply(final ABox abox, final Literal[] args)
	{
		return _test.test(args);
	}
}
