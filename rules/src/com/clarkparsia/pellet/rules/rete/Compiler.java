// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.Iterator;
import java.util.Set;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Compiler
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
 */
public class Compiler {
	/**
	 * Predicate used to state two individuals are different from each other.
	 */
	public static final ATermAppl		DIFF_FROM;

	/**
	 * Empty fact to fire empty bodied rules
	 */
	public static final TermTuple		EMPTY_TUPLE	= new TermTuple( DependencySet.INDEPENDENT );
	public static final Fact			EMPTY_FACT	= new Fact( DependencySet.INDEPENDENT );

	/**
	 * standard object position
	 */
	public static final int				OBJ			= 2;
	/**
	 * standard predicate position
	 */
	public static final int				PRED		= 0;
	/**
	 * Predicate used to state two individuals are the same.
	 */
	public static final ATermAppl		SAME_AS;
	/**
	 * standard subject position
	 */
	public static final int				SUBJ		= 1;
	/**
	 * Predicate used to type an individual.
	 */
	public static final ATermAppl		TYPE;

	static {
		String PREFIX = "tag:clarkparsia.info,2007:pellet:dl-safe-rules:predicate:";
		DIFF_FROM = ATermUtils.makeTermAppl( PREFIX + "differentFrom" );
		SAME_AS = ATermUtils.makeTermAppl( PREFIX + "sameAs" );
		TYPE = ATermUtils.makeTermAppl( PREFIX + "type" );
	}

	ABox								abox;
	AlphaIndex							alphaIndex;

	Interpreter							interpreter;
	// the following field was not used at all and has been commented out in r1933
	// HashSet<ATermAppl>					typesMentioned;

	public Compiler(Interpreter interp, ABox abox) {
		this.abox = abox;
		this.interpreter = interp;
		alphaIndex = new AlphaIndex();
//		typesMentioned = new HashSet<ATermAppl>();
	}

	/**
	 * Add different assertions as facts to rete.
	 */
	public boolean addDifferents(Individual individual) {
		boolean changed = false;
		for( org.mindswap.pellet.Node different : individual.getDifferents() ) {
			if( different.isNamedIndividual() && !different.isPruned() )
				changed |= interpreter.addFact( createDifferent( individual, different, individual
						.getDifferenceDependency( different ) ) );
		}
		return changed;
	}

	/**
	 * Add property assertion as a fact to rete (if relevant). This will
	 * consider the role taxonomy and inverse roles.
	 * 
	 * @param edge
	 *            the {@code Edge}
	 * @return boolean {@code true} if added, {@code false} else
	 */
	public boolean addFact(Edge edge) {
		final Individual from = edge.getFrom();
		final org.mindswap.pellet.Node to = edge.getTo().getSame();
		if( !to.isRootNominal() || to.isPruned() )
			return false;
		final DependencySet ds = edge.getDepends();
		Role role = edge.getRole();
		
		boolean added = addFact( role, from, to, ds );

		if( role.isObjectRole() ) {
			added |= addFact( role.getInverse(), (Individual) to, from, ds );
		}

		return added;
	}
	
	private boolean addFact(Role r, Individual from, org.mindswap.pellet.Node to, DependencySet ds) {
		boolean added = false;
		for( Role sup : r.getSuperRoles() ) {
				added |= interpreter.addFact( createFact( sup.getName(), from, to, 
						ds.union( r.getExplainSuper( sup.getName() ), abox.doExplanation() ) ) );
		}
		return added;
	}

	/**
	 * Add concept assertion as a fact to rete (if relevant).
	 * 
	 * @param individual
	 *            the {@code Individual}
	 * @param type
	 *            the concept
	 * @return boolean {@code true} if added, {@code false} else
	 */
	public boolean addFact(Individual individual, ATermAppl type, DependencySet ds) {
		boolean changed = false;

		changed |= interpreter.addFact( createFact( individual, type, ds ) );
		return changed;
	}

	public void compile(Rule rule, Set<ATermAppl> explain) {
		AlphaStore alphaNodesOfRule = new AlphaStore();

		// If head empty create empty TermTuple

		// iterate over the left hand side (rule body)
		// turn each triple into an alpha node, and add to AlphaStore
		for( TermTuple anodePattern : rule.getBody() ) {
			AlphaNode anode = makeAlphaNode( anodePattern );
			alphaNodesOfRule.addNode( anode );
//			if( anodePattern.getElements().get( 0 ).equals( TYPE ) ) {
//				typesMentioned.add( (anodePattern.getElements().get( 2 )) );
//			}
		}

		// sort them (body triples)
		alphaNodesOfRule.sort();

		AlphaNode alpha1, alpha2;
		BetaNode beta;
		switch ( alphaNodesOfRule.nodes.size() ) {
		// no body/ascendant
		case 0:
			alpha1 = makeAlphaNode( EMPTY_TUPLE );
			alphaNodesOfRule.addNode( alpha1 );
			// Fall through to case 1
			// 1 body/ascendant
		case 1:
			alpha1 = alphaNodesOfRule.nodes.get( 0 );
			beta = makeBetaNode( alpha1, alpha1, false );
			alpha1.add( beta );
			beta.rule = new RuleNode( rule, explain );
			beta.rule.betaNode = beta;
			break;
			// more than 2 body/ascendants
		default:
			Iterator<AlphaNode> nodes = alphaNodesOfRule.nodes.iterator();
		
			alpha1 = nodes.next();
			alpha2 = nodes.next();

			beta = makeBetaNode( alpha1, alpha2, true );

			alpha1.add( beta );
			alpha2.add( beta );
			
			while( nodes.hasNext() ) {
				AlphaNode alpha = nodes.next();
				beta = makeBetaNode(beta, alpha, true);
				alpha.add(beta);
			}
			
			beta.rule = new RuleNode(rule, explain);
			beta.rule.betaNode = beta;				
		}		

		// result of method is that the AlphaStore has all the assendants of the
		// bodies of the rules in it
	}

	public void compileFacts(ABox abox) {
		// compile facts

		// get all the individuals
		interpreter.addFact( EMPTY_FACT );
		for( Iterator<Individual> i = abox.getIndIterator(); i.hasNext(); ) {

			Individual ind = i.next();
			processIndividual( ind );

		}
		return;
	}

	private Fact createDifferent(Individual ind1, org.mindswap.pellet.Node ind2, DependencySet ds) {
		ATermAppl subj = ind1.getName();
		ATermAppl obj = ind2.getName();

		return new Fact( ds, DIFF_FROM, subj, obj );
	}

	private Fact createFact(ATermAppl r, Individual from, org.mindswap.pellet.Node to,
			DependencySet ds) {
		ATermAppl pred = r, subj = from.getName(), obj = to.getName();

		return new Fact( ds, pred, subj, obj );
	}

	private Fact createFact(Individual ind, ATermAppl c, DependencySet ds) {
		ATermAppl predType = TYPE;
		ATermAppl subj = ind.getName();
		ATermAppl obj = c;

		return new Fact( ds, predType, subj, obj );
	}

	private AlphaNode makeAlphaNode(TermTuple pattern) {
		return alphaIndex.add( pattern );
	}

	private BetaNode makeBetaNode(Node node1, Node node2, @SuppressWarnings("unused")
	boolean futureJoins) {
		BetaNode b = new BetaNode( node1, node2, abox.doExplanation() );

		return b;
	}

	public boolean processIndividual(Individual ind) {
		// only named non-pruned individuals
		boolean changed = false;
		if( !ind.isRootNominal() || ind.isPruned() )
			return false;

		for( ATermAppl indType : ind.getTypes() )
			changed |= addFact( ind, indType, ind.getDepends( indType ) );

		changed |= addDifferents( ind );

		for( Edge edge : ind.getOutEdges() )
			changed |= addFact( edge );

		return changed;
	}

	public String toString() {
		return alphaIndex.toString();
	}
}
