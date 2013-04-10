// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import java.util.Collection;
import java.util.HashSet;

import com.clarkparsia.pellet.rules.model.AtomDVariable;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.AtomObject;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.DefaultAtomObjectVisitor;
import com.clarkparsia.pellet.rules.model.RuleAtom;


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
public class VariableUtils {

	/** 
	 * Collects all variables that it visits
	 *
	 */
	private static class VisitingCollector extends DefaultAtomObjectVisitor {
		private Collection<AtomVariable>	variables;

		public VisitingCollector() {
			variables = new HashSet<AtomVariable>();
		}

		public Collection<AtomVariable> getVariables() {
			return variables;
		}

		public void visit(AtomDVariable var) {
			variables.add( var );
		}

		public void visit(AtomIVariable var) {
			variables.add( var );
		}
	}
	
	/** 
	 * Collects all data variables that it visits
	 *
	 */
	private static class VisitingDCollector extends DefaultAtomObjectVisitor {
		private Collection<AtomDVariable>	variables;

		public VisitingDCollector() {
			variables = new HashSet<AtomDVariable>();
		}

		public Collection<AtomDVariable> getVariables() {
			return variables;
		}

		public void visit(AtomDVariable var) {
			variables.add( var );
		}
	}
	
	/** 
	 * Collects all instance variables that it visits
	 *
	 */
	private static class VisitingICollector extends DefaultAtomObjectVisitor {
		private Collection<AtomIVariable>	variables;

		public VisitingICollector() {
			variables = new HashSet<AtomIVariable>();
		}

		public Collection<AtomIVariable> getVariables() {
			return variables;
		}

		public void visit(AtomIVariable var) {
			variables.add( var );
		}
	}
	
	/**
	 * Static convenience function to return the instance variables used
	 * in the given atom.
	 */
	public static Collection<AtomDVariable> getDVars(RuleAtom atom) {
		VisitingDCollector collector = new VisitingDCollector();
		for( AtomObject obj : atom.getAllArguments() ) {
			obj.accept( collector );
		}
		return collector.getVariables();
	}
	
	/**
	 * Static convenience function to return the instance variables used
	 * in the given atom.
	 */
	public static Collection<AtomIVariable> getIVars(RuleAtom atom) {
		VisitingICollector collector = new VisitingICollector();
		for( AtomObject obj : atom.getAllArguments() ) {
			obj.accept( collector );
		}
		return collector.getVariables();
	}
	
	/**
	 * Static convenience function to return the variables used
	 * in the given atom.
	 */
	public static Collection<AtomVariable> getVars(RuleAtom atom) {
		VisitingCollector collector = new VisitingCollector();
		for( AtomObject obj : atom.getAllArguments() ) {
			obj.accept( collector );
		}
		return collector.getVariables();
	}

	/**
	 * Returns true if atom object is a variable
	 */
	public static boolean isVariable(AtomObject obj) {
		VisitingCollector collector = new VisitingCollector();
		obj.accept( collector );

		return collector.getVariables().size() == 1;
	}
	
}
