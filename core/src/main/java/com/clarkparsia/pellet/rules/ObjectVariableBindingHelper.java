// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Individual;

/**
 * <p>
 * Title: Object Variable Binding Helper
 * </p>
 * <p>
 * Description: A binding helper that will iterate over all named individuals in the _abox.
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

public class ObjectVariableBindingHelper implements BindingHelper
{

	private final ABox abox;
	private Individual currentIndividual;
	private Iterator<Individual> individualIterator;
	private final AtomIVariable var;

	public ObjectVariableBindingHelper(final ABox abox, final AtomIVariable var)
	{
		this.abox = abox;
		this.var = var;
	}

	@Override
	public Collection<AtomIVariable> getBindableVars(final Collection<AtomVariable> bound)
	{
		return Collections.singleton(var);
	}

	@Override
	public Collection<AtomIVariable> getPrerequisiteVars(final Collection<AtomVariable> bound)
	{
		return Collections.emptyList();
	}

	@Override
	public void rebind(final VariableBinding newBinding)
	{
		if (newBinding.containsKey(var))
			individualIterator = Collections.singleton(newBinding.get(var)).iterator();
		else
			individualIterator = new AllNamedIndividualsIterator(abox);
	}

	@Override
	public boolean selectNextBinding()
	{
		if ((individualIterator == null) || !individualIterator.hasNext())
			return false;

		currentIndividual = individualIterator.next();

		return true;
	}

	@Override
	public void setCurrentBinding(final VariableBinding currentBinding)
	{
		currentBinding.set(var, currentIndividual);
	}

	@Override
	public String toString()
	{
		return "individuals(" + var + ")";
	}

}
