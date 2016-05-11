// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import com.clarkparsia.pellet.rules.model.AtomDVariable;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.AtomObject;
import com.clarkparsia.pellet.rules.model.AtomObjectVisitor;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import java.util.Collection;
import java.util.HashSet;

/**
 * <p>
 * Title: Variable Utilities
 * </p>
 * <p>
 * Description: Collection of utilities for dealing with variables
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
public class VariableUtils
{

	/**
	 * Collects all variables that it visits
	 */
	private static class VisitingCollector implements AtomObjectVisitor
	{
		private final Collection<AtomVariable> variables;

		public VisitingCollector()
		{
			variables = new HashSet<>();
		}

		public Collection<AtomVariable> getVariables()
		{
			return variables;
		}

		@Override
		public void visit(final AtomDVariable var)
		{
			variables.add(var);
		}

		@Override
		public void visit(final AtomIVariable var)
		{
			variables.add(var);
		}
	}

	/**
	 * Collects all _data variables that it visits
	 */
	private static class VisitingDCollector implements AtomObjectVisitor
	{
		private final Collection<AtomDVariable> variables;

		public VisitingDCollector()
		{
			variables = new HashSet<>();
		}

		public Collection<AtomDVariable> getVariables()
		{
			return variables;
		}

		@Override
		public void visit(final AtomDVariable var)
		{
			variables.add(var);
		}
	}

	/**
	 * Collects all instance variables that it visits
	 */
	private static class VisitingICollector implements AtomObjectVisitor
	{
		private final Collection<AtomIVariable> variables;

		public VisitingICollector()
		{
			variables = new HashSet<>();
		}

		public Collection<AtomIVariable> getVariables()
		{
			return variables;
		}

		@Override
		public void visit(final AtomIVariable var)
		{
			variables.add(var);
		}
	}

	/**
	 * Static convenience function to return the instance variables used in the given atom.
	 */
	public static Collection<AtomDVariable> getDVars(final RuleAtom atom)
	{
		final VisitingDCollector collector = new VisitingDCollector();
		for (final AtomObject obj : atom.getAllArguments())
			obj.accept(collector);
		return collector.getVariables();
	}

	/**
	 * Static convenience function to return the instance variables used in the given atom.
	 */
	public static Collection<AtomIVariable> getIVars(final RuleAtom atom)
	{
		final VisitingICollector collector = new VisitingICollector();
		for (final AtomObject obj : atom.getAllArguments())
			obj.accept(collector);
		return collector.getVariables();
	}

	/**
	 * Static convenience function to return the variables used in the given atom.
	 */
	public static Collection<AtomVariable> getVars(final RuleAtom atom)
	{
		final VisitingCollector collector = new VisitingCollector();
		for (final AtomObject obj : atom.getAllArguments())
			obj.accept(collector);
		return collector.getVariables();
	}

	/**
	 * Returns true if atom object is a variable
	 */
	public static boolean isVariable(final AtomObject obj)
	{
		final VisitingCollector collector = new VisitingCollector();
		obj.accept(collector);

		return collector.getVariables().size() == 1;
	}

}
