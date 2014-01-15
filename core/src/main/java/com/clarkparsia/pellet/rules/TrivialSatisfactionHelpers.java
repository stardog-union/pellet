// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

import com.clarkparsia.pellet.rules.model.AtomDObject;
import com.clarkparsia.pellet.rules.model.AtomIObject;
import com.clarkparsia.pellet.rules.model.AtomObject;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;
import com.clarkparsia.pellet.rules.model.ClassAtom;
import com.clarkparsia.pellet.rules.model.DataRangeAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.DifferentIndividualsAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.model.RuleAtomVisitor;
import com.clarkparsia.pellet.rules.model.SameIndividualAtom;

/**
 * <p>
 * Title: Trivial Satisfaction helper
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
public class TrivialSatisfactionHelpers {
	private static final BindingTester ALWAYS_TRUE = new BindingTester() {
		public DependencySet check(VariableBinding binding) {
			return DependencySet.INDEPENDENT;
		}

		public String toString() {
			return "test(TRUE)";
		}
	};

	private abstract class BinaryBindingTester<R extends AtomObject, S extends AtomObject>
			implements BindingTester {
		R	arg1;
		S	arg2;

		public BinaryBindingTester(R arg1, S arg2) {
			this.arg1 = arg1;
			this.arg2 = arg2;
		}

		public R getArg1() {
			return arg1;
		}

		public S getArg2() {
			return arg2;
		}
	}

	private interface BindingTester {
		/**
		 * Returns true if binding supports testing condition
		 */
		public DependencySet check(VariableBinding binding);
	}

	/**
	 * Creates a helper for each body atom it can. If none can be made,
	 * getHelper() returns null
	 */
	private class BodyAtomVisitor implements RuleAtomVisitor {

		private BindingTester	tester	= null;

		public BindingTester getTester() {
			return tester;
		}

		public void visit(BuiltInAtom atom) {
			tester = null;
		}

		public void visit(ClassAtom atom) {
			tester = new TestClass(
					ATermUtils.normalize( ATermUtils.negate( atom.getPredicate() ) ), atom
							.getArgument() );
		}

		public void visit(DataRangeAtom atom) {
			tester = null;
		}

		public void visit(DatavaluedPropertyAtom atom) {
			tester = null;
		}

		public void visit(DifferentIndividualsAtom atom) {
			tester = new TestSame( atom.getArgument1(), atom.getArgument2() );
		}

		public void visit(IndividualPropertyAtom atom) {
			tester = null;
		}

		public void visit(SameIndividualAtom atom) {
			tester = new TestDifferent( atom.getArgument1(), atom.getArgument2() );
		}

	}

	/**
	 * Takes a filter and a list of vars to implement Binding helper.
	 */
	private static class FilterHelper implements BindingHelper {

		private boolean								result	= false;
		private BindingTester						tester;
		private Collection<? extends AtomVariable>	vars;

		public FilterHelper(BindingTester tester, Collection<? extends AtomVariable> vars) {
			this.tester = tester;
			this.vars = vars;
		}

		public Collection<? extends AtomVariable> getBindableVars(Collection<AtomVariable> bound) {
			return Collections.emptySet();
		}

		public Collection<? extends AtomVariable> getPrerequisiteVars(Collection<AtomVariable> bound) {
			return vars;
		}

		public void rebind(VariableBinding newBinding) {
			result = (tester.check( newBinding ) == null);
		}

		public boolean selectNextBinding() {
			boolean result = this.result;
			this.result = false;
			return result;
		}

		public void setCurrentBinding(VariableBinding currentBinding) {
			// nothing to do.
		}

		public String toString() {
			return "Filter(" + tester + ")";
		}

	}

	/**
	 * Creates a helper for each head atom it can. If none can be made,
	 * getHelper() returns null
	 */
	private class HeadAtomVisitor implements RuleAtomVisitor {

		private BindingTester	tester	= null;

		public BindingTester getTester() {
			return tester;
		}

		public void visit(BuiltInAtom atom) {
			tester = ALWAYS_TRUE;
		}

		public void visit(ClassAtom atom) {
			tester = new TestClass( ATermUtils.normalize( atom.getPredicate() ), atom.getArgument() );
		}

		public void visit(DataRangeAtom atom) {
			tester = ALWAYS_TRUE;
		}

		public void visit(DatavaluedPropertyAtom atom) {
			tester = new TestDataProperty( atom.getPredicate(), atom.getArgument1(), atom
					.getArgument2() );
		}

		public void visit(DifferentIndividualsAtom atom) {
			tester = new TestDifferent( atom.getArgument1(), atom.getArgument2() );
		}

		public void visit(IndividualPropertyAtom atom) {
			tester = new TestIndividualProperty( atom.getPredicate(), atom.getArgument1(), atom
					.getArgument2() );
		}

		public void visit(SameIndividualAtom atom) {
			tester = new TestSame( atom.getArgument1(), atom.getArgument2() );
		}

	}

	/**
	 * Returns the dependency set if the given class assertion holds,
	 * <code>null</code> otherwise
	 */
	private class TestClass extends UnaryBindingTester<AtomIObject> {
		private ATermAppl	c;

		public TestClass(ATermAppl c, AtomIObject arg) {
			super( arg );
			this.c = c;
		}

		public DependencySet check(VariableBinding binding) {
			Individual ind = binding.get( getArg() );
			return ind.getDepends( c );
		}

		public String toString() {
			return "notClass(" + getArg() + ":" + c + ")";
		}

	}
	
	/**
	 * Returns the dependency set if the given property assertion holds,
	 * <code>null</code> otherwise
	 */
	private class TestDataProperty extends TestProperty<AtomDObject> {

		public TestDataProperty(ATermAppl p, AtomIObject arg1, AtomDObject arg2) {
			super( p, arg1, arg2 );
		}

		public DependencySet check(VariableBinding binding) {
			return check( binding.get( getArg1() ), binding.get( getArg2() ) );
		}

	}

	/**
	 * Returns the dependency set if the given individuals are different,
	 * <code>null</code> otherwise
	 */
	private class TestDifferent extends BinaryBindingTester<AtomIObject, AtomIObject> {

		public TestDifferent(AtomIObject arg1, AtomIObject arg2) {
			super( arg1, arg2 );
		}

		public DependencySet check(VariableBinding binding) {
			return binding.get( arg1 ).getDifferenceDependency( binding.get( arg2 ) );
		}

		public String toString() {
			return "notDifferent(" + getArg1() + "," + getArg2() + ")";
		}

	}

	/**
	 * Returns the dependency set if the given property assertion holds,
	 * <code>null</code> otherwise
	 */
	private class TestIndividualProperty extends TestProperty<AtomIObject> {

		public TestIndividualProperty(ATermAppl p, AtomIObject arg1, AtomIObject arg2) {
			super( p, arg1, arg2 );
		}

		public DependencySet check(VariableBinding binding) {
			return check( binding.get( getArg1() ), binding.get( getArg2() ) );
		}

	}

	/**
	 * Returns the dependency set if the given property assertion holds,
	 * <code>null</code> otherwise
	 */
	private abstract class TestProperty<S extends AtomObject> extends
			BinaryBindingTester<AtomIObject, S> {
		Role	r;

		public TestProperty(ATermAppl p, AtomIObject arg1, S arg2) {
			super( arg1, arg2 );
			r = abox.getRole( p );
			if( r == null ) {
				throw new InternalReasonerException( "Cannot retreive role!: " + p );
			}
		}

		public DependencySet check(Individual node1, Node node2) {
			EdgeList list = node1.getRNeighborEdges( r );
			for( int i = 0, n = list.size(); i < n; i++ ) {
				Edge edge = list.edgeAt( i );
				if( edge.getNeighbor( node1 ).equals( node2 ) ) {
					return edge.getDepends();
				}
			}

			return null;
		}

	}

	/**
	 * Returns the dependency set if the given individuals ar same,
	 * <code>null</code> otherwise
	 */
	private class TestSame extends BinaryBindingTester<AtomIObject, AtomIObject> {

		public TestSame(AtomIObject arg1, AtomIObject arg2) {
			super( arg1, arg2 );
		}

		public DependencySet check(VariableBinding binding) {
			Individual ind1 = binding.get( arg1 );
			Individual ind2 = binding.get( arg2 );
			if( ind1.isSame( ind2 ) ) {
				// we might be returning a super set of the actual dependency
				// set for the sameness since it is not straight-forward to come
				// up with the exact dependency set if there are other
				// individuals involved in this sameAs inference
				DependencySet ds1 = ind1.getMergeDependency( true );
				DependencySet ds2 = ind2.getMergeDependency( true );

				return ds1 == null
					? ds2
					: ds2 == null
						? ds1
						: ds1.union( ds2, true );
			}
			else {
				return null;
			}
		}

	}

	private abstract class UnaryBindingTester<R extends AtomObject> implements BindingTester {
		R	arg;

		public UnaryBindingTester(R arg) {
			this.arg = arg;
		}

		public R getArg() {
			return arg;
		}
	}

	private ABox	abox;

	public TrivialSatisfactionHelpers(ABox abox) {
		this.abox = abox;
	}

	public Collection<BindingHelper> getHelpers(Rule rule) {
		Collection<BindingHelper> helpers = new ArrayList<BindingHelper>();

		BodyAtomVisitor bodyVisitor = new BodyAtomVisitor();
		for( RuleAtom atom : rule.getBody() ) {
			atom.accept( bodyVisitor );
			if( bodyVisitor.getTester() != null )
				helpers.add( new FilterHelper( bodyVisitor.getTester(), VariableUtils
						.getVars( atom ) ) );
		}

		return helpers;
	}

	public DependencySet isAtomTrue(RuleAtom atom, VariableBinding binding) {
		HeadAtomVisitor visitor = new HeadAtomVisitor();
		atom.accept( visitor );
		BindingTester tester = visitor.getTester();
		return tester == null
			? null
			: tester.check( binding );
	}
}
