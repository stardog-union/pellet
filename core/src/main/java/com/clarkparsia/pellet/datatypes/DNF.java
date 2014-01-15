package com.clarkparsia.pellet.datatypes;

import static org.mindswap.pellet.utils.ATermUtils.ANDFUN;
import static org.mindswap.pellet.utils.ATermUtils.ORFUN;
import static org.mindswap.pellet.utils.ATermUtils.isAnd;
import static org.mindswap.pellet.utils.ATermUtils.isOr;
import static org.mindswap.pellet.utils.ATermUtils.makeAnd;
import static org.mindswap.pellet.utils.ATermUtils.makeOr;
import static org.mindswap.pellet.utils.ATermUtils.nnf;
import static org.mindswap.pellet.utils.ATermUtils.toSet;

import java.util.ArrayList;
import java.util.List;

import org.mindswap.pellet.utils.iterator.MultiListIterator;

import aterm.AFun;
import aterm.ATermAppl;
import aterm.ATermList;

/**
 * <p>
 * Title: Disjunction Normal Form
 * </p>
 * <p>
 * Description: Static implementation to translate ATermAppl descriptions of
 * complex data ranges to disjunction normal form
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Mike Smith
 */
public class DNF {

	/**
	 * Get disjunctive normal form for an expression
	 * 
	 * @param term
	 *            The expression
	 * @return <code>term</code> in DNF
	 */
	public static ATermAppl dnf(ATermAppl term) {
		return dnfFromNnf( nnf( term ) );
	}

	/**
	 * Internal method that assumes input is NNF
	 * 
	 * @param term
	 *            A NNF expression
	 * @return <code>term</code> in DNF
	 */
	private static ATermAppl dnfFromNnf(ATermAppl term) {
		/*
		 * TODO: Avoid processing DataOneOf when forcing into NNF
		 */

		ATermAppl dnf;

		final AFun fun = term.getAFun();

		/*
		 * If the term is a conjunction, each conjunct must be converted to dnf
		 * and then element-wise distributed.
		 */
		if( ANDFUN.equals( fun ) ) {

			/*
			 * Step 1: the input conjunction may have conjunctions as arguments.
			 * After this step, <code>conjuncts</code> is the flattened list of
			 * conjuncts, each in DNF
			 */
			ATermList rootConjuncts = (ATermList) term.getArgument( 0 );
			List<ATermAppl> conjuncts = new ArrayList<ATermAppl>();
			MultiListIterator i = new MultiListIterator( rootConjuncts );
			while( i.hasNext() ) {
				ATermAppl a = i.next();
				if( isAnd( a ) )
					i.append( (ATermList) a.getArgument( 0 ) );
				else {
					ATermAppl dnfA = dnfFromNnf( a ); 
					conjuncts.add( dnfA );
				}
			}

			/*
			 * Step 2: element-wise distribute any disjunction among the
			 * conjuncts.
			 */
			List<ATermAppl> disjuncts = new ArrayList<ATermAppl>();
			for( ATermAppl a : conjuncts ) {
				if( disjuncts.isEmpty() ) {
					addToList( a, isOr( a ), disjuncts );
				}
				else {
					List<ATermAppl> thisArgs = new ArrayList<ATermAppl>();
					List<ATermAppl> newDisjuncts = new ArrayList<ATermAppl>();
					addToList( a, isOr( a ), thisArgs );

					for( ATermAppl a1 : thisArgs ) {
						for( ATermAppl b : disjuncts ) {
							List<ATermAppl> list = new ArrayList<ATermAppl>();
							addToList( a1, isAnd( a1 ), list );
							addToList( b, isAnd( b ), list );							
							newDisjuncts.add( makeAnd( toSet( list ) ) );
						}
					}
					disjuncts = newDisjuncts;
				}
			}

			dnf = makeOr( toSet( disjuncts ) );

		}
		/*
		 * If the term is a disjunction merge each element into DNF
		 */
		else if( ORFUN.equals( fun ) ) {
			ATermList disjuncts = (ATermList) term.getArgument( 0 );
			MultiListIterator i = new MultiListIterator( disjuncts );
			List<ATermAppl> args = new ArrayList<ATermAppl>();
			while( i.hasNext() ) {
				ATermAppl a = i.next();
				if( isOr( a ) )
					i.append( (ATermList) a.getArgument( 0 ) );
				else
					args.add( dnfFromNnf( a ) );
			}
			dnf = makeOr( toSet( args ) );
		}
		/*
		 * If the term is not a conjunction or disjunction (and its in NNF), it
		 * is already in DNF
		 */
		else
			dnf = term;

		return dnf;
	}

	private static void addToList(ATermAppl term, boolean flatten, List<ATermAppl> result) {
		if( flatten ) {
			for( ATermList l = (ATermList) term.getArgument( 0 ); !l.isEmpty(); l = l.getNext() )
				result.add( (ATermAppl) l.getFirst() );
		}
		else {
			result.add( term );
		}
	}
	
}
