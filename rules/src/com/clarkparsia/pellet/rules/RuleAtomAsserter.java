// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import static com.clarkparsia.pellet.utils.TermFactory.all;
import static com.clarkparsia.pellet.utils.TermFactory.not;
import static com.clarkparsia.pellet.utils.TermFactory.value;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

import com.clarkparsia.pellet.rules.model.BuiltInAtom;
import com.clarkparsia.pellet.rules.model.ClassAtom;
import com.clarkparsia.pellet.rules.model.DataRangeAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.DifferentIndividualsAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.model.RuleAtomVisitor;
import com.clarkparsia.pellet.rules.model.SameIndividualAtom;

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
public class RuleAtomAsserter implements RuleAtomVisitor {
	private ABox				abox;
	private CompletionStrategy	strategy;

	private VariableBinding		binding;
	private DependencySet		ds;
	private boolean				negated;

	private boolean				asserted;

	public RuleAtomAsserter() {
	}

	public boolean assertAtom(RuleAtom atom, VariableBinding binding, DependencySet ds,
			boolean negated,ABox abox, CompletionStrategy strategy) {
		asserted = true;

		this.binding = binding;
		this.ds = ds;
		this.negated = negated;
		this.strategy = strategy;
		this.abox = abox;

		atom.accept( this );

		return asserted;
	}

	public void visit(BuiltInAtom atom) {
		asserted = false;
	}

	public void visit(ClassAtom atom) {
		ATermAppl cls = atom.getPredicate();
		ATermAppl ind = binding.get( atom.getArgument() ).getName();

		addType( ind, cls );
	}

	private void addType(ATermAppl ind, ATermAppl cls) {
		Individual node = abox.getIndividual( ind ).getSame();
		if( negated )
			cls = ATermUtils.negate( cls );

		strategy.addType( node, cls, ds );
	}

	private void addEdge(ATermAppl p, ATermAppl s, ATermAppl o) {
		Individual node1 = abox.getIndividual( s ).getSame();

		if( negated ) {
			ATermAppl cls = all( p, not( value( o ) ) );
			strategy.addType( node1, cls, ds );
		}
		else {
			Node node2 = abox.getNode( o ).getSame();
			strategy.addEdge( node1, abox.getRole( p ), node2, ds );
		}
	}

	public void visit(DataRangeAtom atom) {
		asserted = false;
	}

	public void visit(DatavaluedPropertyAtom atom) {
		ATermAppl p = atom.getPredicate();
		ATermAppl s = binding.get( atom.getArgument1() ).getName();
		ATermAppl o = binding.get( atom.getArgument2() ).getName();

		addEdge( p, s, o );
	}

	public void visit(DifferentIndividualsAtom atom) {
		ATermAppl ind1 = binding.get( atom.getArgument1() ).getName();
		ATermAppl ind2 = binding.get( atom.getArgument2() ).getName();
		ATermAppl cls = not( value( ind2 ) );

		addType( ind1, cls );
	}

	public void visit(IndividualPropertyAtom atom) {
		ATermAppl p = atom.getPredicate();
		ATermAppl s = binding.get( atom.getArgument1() ).getName();
		ATermAppl o = binding.get( atom.getArgument2() ).getName();

		addEdge( p, s, o );
	}

	public void visit(SameIndividualAtom atom) {
		ATermAppl ind1 = binding.get( atom.getArgument1() ).getName();
		ATermAppl ind2 = binding.get( atom.getArgument2() ).getName();
		ATermAppl cls = value( ind2 );

		addType( ind1, cls );
	}

}
