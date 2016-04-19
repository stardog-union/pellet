// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import com.clarkparsia.pellet.datatypes.DatatypeReasoner;
import com.clarkparsia.pellet.datatypes.exceptions.DatatypeReasonerException;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.DataRangeAtom;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.exceptions.InternalReasonerException;

/**
 * <p>
 * Title: Data Range Binding Helper
 * </p>
 * <p>
 * Description:
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
public class DataRangeBindingHelper implements BindingHelper
{

	private static final Logger log;

	static
	{
		log = Logger.getLogger(DataRangeBindingHelper.class.getCanonicalName());
	}

	private final DatatypeReasoner dtReasoner;
	private final DataRangeAtom atom;
	private boolean hasNext;

	public DataRangeBindingHelper(final ABox abox, final DataRangeAtom atom)
	{
		this.dtReasoner = abox.getDatatypeReasoner();
		this.atom = atom;
		hasNext = false;
	}

	@Override
	public Collection<AtomVariable> getBindableVars(final Collection<AtomVariable> bound)
	{
		return Collections.emptySet();
	}

	@Override
	public Collection<AtomVariable> getPrerequisiteVars(final Collection<AtomVariable> bound)
	{
		return VariableUtils.getVars(atom);
	}

	@Override
	public void rebind(final VariableBinding newBinding)
	{
		final Literal dValue = newBinding.get(atom.getArgument());

		if (dValue == null)
			throw new InternalReasonerException("DataRangeBindingHelper cannot generate bindings for " + atom);

		try
		{
			hasNext = dtReasoner.isSatisfiable(Collections.singleton(atom.getPredicate()), dValue.getValue());
		}
		catch (final DatatypeReasonerException e)
		{
			final String msg = "Unexpected datatype reasoner exception: " + e.getMessage();
			log.severe(msg);
			throw new InternalReasonerException(e);
		}
	}

	@Override
	public boolean selectNextBinding()
	{
		if (hasNext)
		{
			hasNext = false;
			return true;
		}
		return false;
	}

	@Override
	public void setCurrentBinding(final VariableBinding currentBinding)
	{
		// This space left intentionally blank.
	}

	@Override
	public String toString()
	{
		return atom.toString();
	}

}
