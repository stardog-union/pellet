// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.mindswap.pellet.ABox;

/**
 * <p>
 * Title: Binding Generator Implementation
 * </p>
 * <p>
 * Description: Takes a list of _binding _helpers
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

public class BindingGeneratorImpl implements BindingGenerator
{

	private final Collection<BindingHelper> _helpers;
	private VariableBinding _initialBinding;

	private class BindingIterator implements Iterator<VariableBinding>
	{

		private VariableBinding _binding;
		private BindingHelper[] _helperChain;

		public BindingIterator()
		{
			_helperChain = new BindingHelper[_helpers.size()];
			_helperChain = _helpers.toArray(_helperChain);

			if (_helperChain.length > 0)
				_helperChain[0].rebind(_initialBinding);

		}

		/**
		 * Return the _current _binding up through and including the <code>max</code> element of the pattern chain.
		 * 
		 * @param max
		 * @return
		 */
		private VariableBinding getBinding(final int max)
		{
			final VariableBinding newBinding = new VariableBinding(_initialBinding);
			for (int i = 0; i <= max; i++)
				_helperChain[i].setCurrentBinding(newBinding);
			return newBinding;
		}

		@Override
		public boolean hasNext()
		{
			if (_binding != null)
				return true;

			// Search loop to find new _binding.
			VariableBinding newBinding = null;
			int position = _helperChain.length - 1;
			while (position >= 0)
				if (_helperChain[position].selectNextBinding())
				{
					if (newBinding == null)
						newBinding = getBinding(position);
					else
						_helperChain[position].setCurrentBinding(newBinding);
					if (position < _helperChain.length - 1)
					{
						// Not at last helper, need to move forward.
						_helperChain[position + 1].rebind(newBinding);
						position++;
					}
					else
					{
						// Found new _binding at last helper in the chain.
						// We can exit now.
						_binding = newBinding;
						return true;
					}
				}
				else
				{
					// Continue going backwards.
					newBinding = null;
					position--;
				}

			return false;
		}

		@Override
		public VariableBinding next()
		{
			if (!hasNext())
				throw new NoSuchElementException();
			final VariableBinding result = _binding;
			_binding = null;
			return result;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}

	}

	/**
	 * Empty Binding Generator
	 */
	public BindingGeneratorImpl()
	{
		_helpers = Collections.emptySet();
	}

	/**
	 * Constructs a _binding generator with the given list of _helpers. The _helpers must be in such an _order that prerequisite variables of any helper are
	 * bound by a helper before it.
	 */
	public BindingGeneratorImpl(@SuppressWarnings("unused") final ABox abox, final VariableBinding initialBinding, final Collection<BindingHelper> helpers)
	{
		this._helpers = helpers;
		this._initialBinding = initialBinding;
	}

	@Override
	public Iterator<VariableBinding> iterator()
	{
		return new BindingIterator();
	}

}
