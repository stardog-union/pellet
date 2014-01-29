// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

import com.clarkparsia.pellet.rules.model.AtomDConstant;
import com.clarkparsia.pellet.rules.model.AtomDObject;
import com.clarkparsia.pellet.rules.model.AtomDVariable;
import com.clarkparsia.pellet.rules.model.AtomIConstant;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.AtomObject;
import com.clarkparsia.pellet.rules.model.AtomObjectVisitor;
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
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Clark & Parsia, LLC. <http://www.clarkparsia.com></p>
 *
 * @author Evren Sirin
 */
public class RulesToATermTranslator implements RuleAtomVisitor, AtomObjectVisitor {
	private ATermAppl term;
	
	public ATermAppl translate(Rule rule) {
		term = null;
		visit( rule );
		return term;
	}
	
	public ATermAppl translate(RuleAtom ruleAtom) {
		term = null;
		ruleAtom.accept( this );
		return term;
	}
	
	public ATermAppl translate(AtomObject obj) {
		term = null;
		obj.accept( this );
		return term;
	}
	
	public void visit(Rule rule) {
		ATermAppl[] head = new ATermAppl[rule.getHead().size()];
		ATermAppl[] body = new ATermAppl[rule.getBody().size()];
		
		int i = 0;
		for( RuleAtom atom : rule.getHead() ) {
			head[i++] = translate( atom );
		}	
		
		i = 0;
		for( RuleAtom atom : rule.getBody() ) {
			body[i++] = translate( atom );
		}
		
		term = ATermUtils.makeRule( rule.getName(), head, body );
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void visit(BuiltInAtom atom) {
		int arity = atom.getAllArguments().size();
		ATermAppl[] args = new ATermAppl[arity+1];
		args[0] = ATermUtils.makeTermAppl( atom.getPredicate() );
		int i = 1;
		for( AtomDObject arg : atom.getAllArguments() ) {
			args[i++] = translate( arg );
		}		
		
		term = ATermUtils.makeBuiltinAtom( args );
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ClassAtom atom) {
		ATermAppl c = atom.getPredicate();
		ATermAppl i = translate(  atom.getArgument() );
		
		term = ATermUtils.makeTypeAtom( i, c );
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DataRangeAtom atom) {
		ATermAppl d = atom.getPredicate();
		ATermAppl l = translate(  atom.getArgument() );
		
		term = ATermUtils.makeTypeAtom( l ,d );
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DatavaluedPropertyAtom atom) {
		ATermAppl p = atom.getPredicate();
		ATermAppl s = translate(  atom.getArgument1() );
		ATermAppl o = translate(  atom.getArgument2() );
		
		term = ATermUtils.makePropAtom( p, s, o );
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DifferentIndividualsAtom atom) {
		ATermAppl t1 = translate(  atom.getArgument1() );
		ATermAppl t2 = translate(  atom.getArgument2() );
		
		term = ATermUtils.makeDifferent( t1, t2 );
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(IndividualPropertyAtom atom) {
		ATermAppl p = atom.getPredicate();
		ATermAppl s = translate(  atom.getArgument1() );
		ATermAppl o = translate(  atom.getArgument2() );
		
		term = ATermUtils.makePropAtom( p, s, o );
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SameIndividualAtom atom) {
		ATermAppl t1 = translate(  atom.getArgument1() );
		ATermAppl t2 = translate(  atom.getArgument2() );
		
		term = ATermUtils.makeSameAs( t1, t2 );
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AtomDConstant constant) {
		term = constant.getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AtomDVariable variable) {
		term = ATermUtils.makeVar( variable.getName() );
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AtomIConstant constant) {
		term = constant.getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AtomIVariable variable) {
		term = ATermUtils.makeVar( variable.getName() );
	}

}
