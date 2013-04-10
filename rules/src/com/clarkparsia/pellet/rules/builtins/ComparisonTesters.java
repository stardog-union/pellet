// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.builtins;

import static java.lang.String.format;

import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.Literal;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.DatatypeReasoner;
import com.clarkparsia.pellet.datatypes.Facet;
import com.clarkparsia.pellet.datatypes.exceptions.DatatypeReasonerException;

/**
 * <p>
 * Title: Comparison Testers
 * </p>
 * <p>
 * Description: Implementations for each of the SWRL comparison tests.
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

public class ComparisonTesters {

	private static Logger	log;

	static {
		log = Logger.getLogger( ComparisonTesters.class.getCanonicalName() );
	}

	private static  class EqualityTester extends BinaryTester {
		
		
		private boolean flip;
		
		private EqualityTester( boolean flip ) {
			this.flip = flip;
		}
		
		protected boolean test( Literal a, Literal b) {
			Object aval = a.getValue();
			Object bval = b.getValue();
			
			// Numbers are a special case, since they can be promoted from Integers and Decimals to Floats and Doubles.
			if ( ( aval instanceof Number ) && ( bval instanceof Number ) ) {
				NumericPromotion promoter = new NumericPromotion();
				Number anum = (Number) aval;
				Number bnum = (Number) bval;
				
				promoter.promote( anum, bnum );
				NumericComparisonVisitor visitor = new NumericComparisonVisitor();
				promoter.accept( visitor );
				
				if ( visitor.getComparison() == 0 )
					return true ^ flip;
				return false ^ flip;
			}
			
			if ( a.getValue() != null && b.getValue() != null ) {
				return ( aval.getClass().equals( bval.getClass() ) && aval.equals( bval )) ^ flip;
			}
			return false;
		}
	}
	
	private static class OrderingTester extends BinaryTester {

		private boolean lt, inclusive;

		private OrderingTester(boolean flip, boolean inclusive) {
			this.lt = flip;
			this.inclusive = inclusive;
		}
		
		private boolean comparesWell( int comparison ) {
			if ( lt && comparison < 0 )
				return true;
			if ( !lt && comparison > 0 )
				return true;
			if ( inclusive && comparison == 0 )
				return true;
			
			return false;
		}

		public boolean test(Literal l1, Literal l2) {
			Object l1val = l1.getValue();
			Object l2val = l2.getValue();

			// String comparisons between ATerms
			if ( ( l1val instanceof ATermAppl ) && ( l2val instanceof ATermAppl) ) {
				ATermAppl l1term = (ATermAppl) l1val;
				ATermAppl l2term = (ATermAppl) l2val;
			
				String l1str = ATermUtils.getLiteralValue(l1term);
				String l2str = ATermUtils.getLiteralValue(l2term);
				String l1lang = ATermUtils.getLiteralLang( l1term );
				String l2lang = ATermUtils.getLiteralLang( l2term );
				String l1data = ATermUtils.getLiteralDatatype( l1term );
				String l2data = ATermUtils.getLiteralDatatype( l2term );
				
				
				if ( l1lang.equals( l2lang ) && l1data.equals( l2data ) ) {
					return comparesWell( l1str.compareTo( l2str ) );
				}
				return false;
			}
			
			// Numbers are a special case, since they can be promoted from
			// Integers and Decimals to Floats and Doubles.
			if ( ( l1val instanceof Number ) && ( l2val instanceof Number ) ) {
				NumericPromotion promoter = new NumericPromotion();
				Number l1num = (Number) l1val;
				Number l2num = (Number) l2val;

				promoter.promote(l1num, l2num);
				NumericComparisonVisitor visitor = new NumericComparisonVisitor();
				promoter.accept(visitor);

				return comparesWell( visitor.getComparison() );
			}

			final DatatypeReasoner dtr = l1.getABox().getDatatypeReasoner();
			final ATermAppl term1 = l1.getTerm();
			final ATermAppl type1 = (ATermAppl) term1.getArgument( ATermUtils.LIT_URI_INDEX );
			final ATermAppl type2 = (ATermAppl) l2.getTerm().getArgument( ATermUtils.LIT_URI_INDEX );
			try {
				/*
				 * First check if the literals' datatypes are comparable. If so,
				 * compile the comparison into a datatype reasoner
				 * satisfiability check.
				 */
				if( dtr.isSatisfiable( Arrays.asList( type1, type2 ) ) ) {
					Facet f = lt
						? inclusive
							? Facet.XSD.MIN_INCLUSIVE
							: Facet.XSD.MIN_EXCLUSIVE
						: inclusive
							? Facet.XSD.MAX_INCLUSIVE
							: Facet.XSD.MAX_EXCLUSIVE;
					final ATermAppl canon1 = dtr.getCanonicalRepresentation( term1 );
					final ATermAppl baseType = (ATermAppl) canon1
							.getArgument( ATermUtils.LIT_URI_INDEX );
					final ATermAppl dr = ATermUtils
							.makeRestrictedDatatype( baseType, new ATermAppl[] { ATermUtils
									.makeFacetRestriction( f.getName(), canon1 ) } );
					if( dtr.isSatisfiable( Collections.singleton( dr ), l2val ) )
						return true;
					else
						return false;
				}
				else
					return false;
			} catch( DatatypeReasonerException e ) {
				final String msg = format(
						"Unexpected datatype reasoner exception comparaing two literals ('%s','%s'). Treating as incomparable.",
						term1, l2.getTerm() );
				log.log( Level.WARNING, msg, e );
				return false;
			}
		}
	}

	public final static Tester equal = new EqualityTester(false),
			greaterThan = new OrderingTester(false, false),
			greaterThanOrEqual = new OrderingTester(false, true),
			lessThan = new OrderingTester(true, false),
			lessThanOrEqual = new OrderingTester(true, true),
			notEqual = new EqualityTester(true);
	
	/**
	 * If the first argument is null, return the second.
	 * Else, return the literal if its value equals the string.
	 * Otherwise return null.
	 */
	public static Literal expectedIfEquals( Literal expected, Literal result ) {
		if ( expected == null )
			return result;
		
		if ( ComparisonTesters.equal.test( new Literal[]{ expected, result } ) )
			return expected;
		return null;
	}
}
