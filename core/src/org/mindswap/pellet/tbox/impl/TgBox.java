// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under the terms of the MIT License.
//
// The MIT License
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package org.mindswap.pellet.tbox.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;
import aterm.ATermInt;
import aterm.ATermList;

public class TgBox extends TBoxBase {
	public static final Logger	log	= Logger.getLogger( TgBox.class.getName() );

	private Set<ATermAppl>		explanation;

	// universal concept
	private List<Unfolding>		UC	= null;
	
	/*
	 * Constructors
	 */

	public TgBox(TBoxExpImpl tbox) {
		super( tbox );
	}

	/*
	 * Utility Functions
	 */

	public void internalize() {

		UC = new ArrayList<Unfolding>();

		for( TermDefinition termDef : termhash.values() ) {
			for( ATermAppl subClassAxiom : termDef.getSubClassAxioms() ) {
				ATermAppl c1 = (ATermAppl) subClassAxiom.getArgument( 0 );
				ATermAppl c2 = (ATermAppl) subClassAxiom.getArgument( 1 );
				ATermAppl notC1 = ATermUtils.makeNot( c1 );
				ATermAppl notC1orC2 = ATermUtils.makeOr( notC1, c2 );
				ATermAppl norm = ATermUtils.normalize( notC1orC2 );

				Set<ATermAppl> explanation;
				if( PelletOptions.USE_TRACING )
					explanation = tbox.getAxiomExplanation( subClassAxiom );
				else
					explanation = Collections.emptySet();

				UC.add( Unfolding.create( norm, explanation ) );
			}

			for( ATermAppl eqClassAxiom : termDef.getEqClassAxioms() ) {
				ATermAppl c1 = (ATermAppl) eqClassAxiom.getArgument( 0 );
				ATermAppl c2 = (ATermAppl) eqClassAxiom.getArgument( 1 );
				ATermAppl notC1 = ATermUtils.makeNot( c1 );
				ATermAppl notC2 = ATermUtils.makeNot( c2 );
				ATermAppl notC1orC2 = ATermUtils.makeOr( notC1, c2 );
				ATermAppl notC2orC1 = ATermUtils.makeOr( notC2, c1 );
				Set<ATermAppl> explanation;
				if( PelletOptions.USE_TRACING )
					explanation = tbox.getAxiomExplanation( eqClassAxiom );
				else
					explanation = Collections.emptySet();

				UC.add( Unfolding.create( ATermUtils.normalize( notC1orC2 ),
						explanation ) );
				UC.add( Unfolding.create( ATermUtils.normalize( notC2orC1 ),
						explanation ) );
			}
		}
	}

	public void absorb() {
		log.fine( "Absorption started" );

		if( log.isLoggable( Level.FINE ) ) {
			log.fine( "Tg.size was " + termhash.size() + " Tu.size was " + tbox.Tu.size() );
		}

		Collection<TermDefinition> terms = termhash.values();

		termhash = new HashMap<ATermAppl, TermDefinition>();

		for( TermDefinition def : terms ) {
			kb.timers.checkTimer( "preprocessing" );

			for( ATermAppl subClassAxiom : def.getSubClassAxioms() ) {
				ATermAppl c1 = (ATermAppl) subClassAxiom.getArgument( 0 );
				ATermAppl c2 = (ATermAppl) subClassAxiom.getArgument( 1 );

				absorbSubClass( c1, c2, tbox.getAxiomExplanation( subClassAxiom ) );
			}

			for( ATermAppl eqClassAxiom : def.getEqClassAxioms() ) {
				ATermAppl c1 = (ATermAppl) eqClassAxiom.getArgument( 0 );
				ATermAppl c2 = (ATermAppl) eqClassAxiom.getArgument( 1 );

				absorbSubClass( c1, c2, tbox.getAxiomExplanation( eqClassAxiom ) );
				absorbSubClass( c2, c1, tbox.getAxiomExplanation( eqClassAxiom ) );
			}
		}

		if( log.isLoggable( Level.FINE ) ) {
			log.fine( "Tg.size is " + termhash.size() + " Tu.size is " + tbox.Tu.size() );
		}

		log.fine( "Absorption finished" );
	}

	private void absorbSubClass(ATermAppl sub, ATermAppl sup, Set<ATermAppl> axiomExplanation) {
		if( log.isLoggable( Level.FINE ) ) 
			log.fine( "Absorb: subClassOf(" + ATermUtils.toString(sub) + ", " + ATermUtils.toString(sup) + ")");
			
		HashSet<ATermAppl> set = new HashSet<ATermAppl>();

		set.add( ATermUtils.nnf( sub ) );
		set.add( ATermUtils.nnf( ATermUtils.makeNot( sup ) ) );

		// ***********************************
		// Explanation-related axiom tracking:
		// This is used in absorbII() where actual absorption takes place
		// with primitive definition
		explanation = new HashSet<ATermAppl>();
		explanation.addAll( axiomExplanation );
		// ***********************************

		absorbTerm( set );
	}

	private boolean absorbTerm(Set<ATermAppl> set) {
		RuleAbsorber ruleAbsorber = new RuleAbsorber( tbox );
		if( log.isLoggable( Level.FINER ) )
			log.finer( "Absorbing term " + set );
		while( true ) {
			log.finer( "Absorb rule" );
			if( PelletOptions.USE_RULE_ABSORPTION && ruleAbsorber.absorbRule( set, explanation ) ) {
				return true;
			}
			log.finer( "Absorb nominal" );
			if( !PelletOptions.USE_PSEUDO_NOMINALS
					&& (PelletOptions.USE_NOMINAL_ABSORPTION || PelletOptions.USE_HASVALUE_ABSORPTION)
					&& absorbNominal( set ) ) {
				return true;
			}
			log.finer( "Absorb II" );
			if( absorbII( set ) ) {
				log.finer( "Absorbed" );
				return true;
			}
			log.finer( "Absorb III" );
			if( absorbIII( set ) ) {
				log.finer( "Absorb III" );
				continue;
			}
			// log.finer("Absorb IV");
			// if (absorbIV(set)) {
			// log.finer("Absorb IV");
			// continue;
			// }
			log.finer( "Absorb V" );
			if( absorbV( set ) ) {
				log.finer( "Absorb V" );
				continue;
			}
			log.finer( "Absorb VI" );
			if( absorbVI( set ) ) {
				log.finer( "Recursed on OR" );
				return true;
			}
			log.finer( "Absorb role" );
			if( PelletOptions.USE_ROLE_ABSORPTION && absorbRole( set ) ) {
				log.finer( "Absorbed w/ Role" );
				return true;
			}
			log.finer( "Absorb VII" );
			absorbVII( set );
			log.finer( "Finished absorbTerm" );
			return false;
		}
	}

	private boolean absorbNominal(Set<ATermAppl> set) {
		for( Iterator<ATermAppl> i = set.iterator(); i.hasNext(); ) {
			ATermAppl name = i.next();
			if( PelletOptions.USE_NOMINAL_ABSORPTION
					&& (ATermUtils.isOneOf( name ) || ATermUtils.isNominal( name )) ) {
				i.remove();

				ATermList list = null;
				if( ATermUtils.isNominal( name ) )
					list = ATermUtils.makeList( name );
				else
					list = (ATermList) name.getArgument( 0 );

				ATermAppl c = ATermUtils.makeNot( ATermUtils.makeAnd( ATermUtils.makeList( set ) ) );

				absorbOneOf( list, c, explanation );

				return true;
			}
			else if( PelletOptions.USE_HASVALUE_ABSORPTION && ATermUtils.isHasValue( name ) ) {
				ATermAppl p = (ATermAppl) name.getArgument( 0 );
				if( !kb.isObjectProperty( p ) )
					continue;

				i.remove();
				ATermAppl c = ATermUtils.makeNot( ATermUtils.makeAnd( ATermUtils.makeList( set ) ) );

				ATermAppl nominal = (ATermAppl) name.getArgument( 1 );
				ATermAppl ind = (ATermAppl) nominal.getArgument( 0 );

				ATermAppl invP = kb.getProperty( p ).getInverse().getName();
				ATermAppl allInvPC = ATermUtils.makeAllValues( invP, c );

				if( log.isLoggable( Level.FINER ) )
					log.finer( "Absorb into " + ind + " with inverse of " + p + " for " + c );
				
				tbox.getAbsorbedAxioms().addAll( explanation );

				kb.addIndividual( ind );
				kb.addType( ind, allInvPC, new DependencySet( explanation ) );

				return true;
			}
		}

		return false;
	}

	void absorbOneOf(ATermAppl oneOf, ATermAppl c, Set<ATermAppl> explain) {
		absorbOneOf( (ATermList) oneOf.getArgument( 0 ), c, explain );
	}

	private void absorbOneOf(ATermList list, ATermAppl c, Set<ATermAppl> explain) {
		if( PelletOptions.USE_PSEUDO_NOMINALS ) {
			if( log.isLoggable( Level.WARNING ) )
				log.warning( "Ignoring axiom involving nominals: " + explain );
			return;
		}

		if( log.isLoggable( Level.FINE ) )
			log.fine( "Absorb nominals: " + ATermUtils.toString( c ) + " " +  list );

		tbox.getAbsorbedAxioms().addAll( explain );

		DependencySet ds = new DependencySet( explain );
		while( !list.isEmpty() ) {
			ATermAppl nominal = (ATermAppl) list.getFirst();
			ATermAppl ind = (ATermAppl) nominal.getArgument( 0 );
			kb.addIndividual( ind );
			kb.addType( ind, c, ds );
			list = list.getNext();
		}
	}

	private boolean absorbRole(Set<ATermAppl> set) {
		for( Iterator<ATermAppl> i = set.iterator(); i.hasNext(); ) {
			ATermAppl name = i.next();

			if( ATermUtils.isSomeValues( name ) ) {
				ATermAppl r = (ATermAppl) name.getArgument( 0 );
				if( kb.getRole( r ).hasComplexSubRole() )
					continue;
				
				ATermAppl domain = ATermUtils.makeNot( ATermUtils.makeAnd( ATermUtils
						.makeList( set ) ) );
				kb.addDomain( r, domain, explanation );

				if( log.isLoggable( Level.FINE ) )
					log.fine( "Absorb domain: " + ATermUtils.toString( r ) + " " + ATermUtils.toString( domain ) );
				
				tbox.getAbsorbedAxioms().addAll( explanation );
				return true;
			}
			else if( ATermUtils.isMin( name ) ) {
				ATermAppl r = (ATermAppl) name.getArgument( 0 );
				ATermAppl q = (ATermAppl) name.getArgument( 2 );
				if( kb.getRole( r ).hasComplexSubRole() || !ATermUtils.isTop( q ) )
					continue;
				
				int n = ((ATermInt) name.getArgument( 1 )).getInt();

				// if we have min(r,1) sub ... this is also equal to a domain
				// restriction
				if( n == 1 ) {
					i.remove();
					ATermAppl domain = ATermUtils.makeNot( ATermUtils.makeAnd( ATermUtils
							.makeList( set ) ) );
					kb.addDomain( r, domain, explanation );
					if( log.isLoggable( Level.FINE ) )
						log.fine( "Absorb domain: " + ATermUtils.toString( r ) + " " + ATermUtils.toString( domain ) );					
					tbox.getAbsorbedAxioms().addAll( explanation );
					return true;
				}
			}
		}

		return false;
	}

	private boolean absorbII(Set<ATermAppl> set) {
		for( ATermAppl term : set ) {
			TermDefinition td = tbox.Tu.getTD( term );
			boolean canAbsorb;
			if( td != null )
				canAbsorb = td.getEqClassAxioms().isEmpty();
			else
				canAbsorb = term.getArity() == 0 && set.size() > 1;

			if( canAbsorb ) {
				set.remove( term );

				ATermList setlist = ATermUtils.makeList( set );
				ATermAppl conjunct = ATermUtils.makeAnd( setlist );
				conjunct = ATermUtils.makeNot( conjunct );
				ATermAppl sub = ATermUtils.makeSub( term, ATermUtils.nnf( conjunct ) );
				tbox.Tu.addDef( sub );

				if( log.isLoggable( Level.FINE ) )
					log.fine( "Absorb named: " + ATermUtils.toString( sub ) );
				
				tbox.addAxiomExplanation( sub, explanation );

				return true;
			}
		}

		return false;
	}

	private boolean absorbIII(Set<ATermAppl> set) {
		for( ATermAppl term : set ) {
			ATermAppl negatedTerm = null;

			TermDefinition td = tbox.Tu.getTD( term );

			if( td == null && ATermUtils.isNegatedPrimitive( term ) ) {
				negatedTerm = (ATermAppl) term.getArgument( 0 );
				td = tbox.Tu.getTD( negatedTerm );
			}

			if( td == null || ATermUtils.isTop( td.getName() ) )
				continue;

			List<ATermAppl> eqClassAxioms = td.getEqClassAxioms();
			if( !eqClassAxioms.isEmpty() ) {
				ATermAppl eqClassAxiom = eqClassAxioms.get( 0 );
				ATermAppl eqClass = (ATermAppl) eqClassAxiom.getArgument( 1 );

				set.remove( term );

				if( negatedTerm == null )
					set.add( eqClass );
				else
					set.add( ATermUtils.negate( eqClass ) );
				// *******************************
				// Explanation-related tracking of axioms
				explanation.addAll( tbox.getAxiomExplanation( eqClassAxiom ) );
				// *******************************

				return true;
			}
		}

		return false;
	}

	private boolean absorbV(Set<ATermAppl> set) {
		for( ATermAppl term : set ) {
			ATermAppl nnfterm = ATermUtils.nnf( term );
			// System.out.println(term);
			if( nnfterm.getAFun().equals( ATermUtils.ANDFUN ) ) {
				set.remove( term );
				ATermList andlist = (ATermList) nnfterm.getArgument( 0 );
				while( !andlist.isEmpty() ) {
					set.add( (ATermAppl) andlist.getFirst() );
					andlist = andlist.getNext();
				}
				return true;
			}
		}
		return false;
	}

	private boolean absorbVI(Set<ATermAppl> set) {
		for( ATermAppl term : set ) {
			ATermAppl nnfterm = ATermUtils.nnf( term );
			if( nnfterm.getAFun().equals( ATermUtils.ORFUN ) ) {
				set.remove( term );
				for( ATermList orlist = (ATermList) nnfterm.getArgument( 0 ); !orlist.isEmpty(); orlist = orlist
						.getNext() ) {
					Set<ATermAppl> cloned = new HashSet<ATermAppl>( set );
					cloned.add( (ATermAppl) orlist.getFirst() );
					// System.out.println("Term: "+term);
					// System.out.println("Recursing on "+cloned);
					// System.out.println("--");
					absorbTerm( cloned );
				}
				return true;
			}
		}

		return false;
	}

	private boolean absorbVII(Set<ATermAppl> set) {
		ATermList list = ATermUtils.makeList( set );
		ATermAppl sub = ATermUtils.nnf( (ATermAppl) list.getFirst() );
		list = list.getNext();

		ATermAppl sup = list.isEmpty()
			? ATermUtils.makeNot( sub )
			: ATermUtils.makeNot( ATermUtils.makeAnd( list ) );

		sup = ATermUtils.nnf( sup );

		ATermAppl subClassAxiom = ATermUtils.makeSub( sub, sup );

		if( log.isLoggable( Level.FINE ) )
			log.fine( "GCI: " + subClassAxiom + "\nexplanation: " + explanation );

		addDef( subClassAxiom );

		tbox.addAxiomExplanation( subClassAxiom, explanation );

		return true;
	}

	/**
	 * @return Returns the UC.
	 */
	public List<Unfolding> getUC() {
		return UC;
	}

	public int size() {
		return UC == null
			? 0
			: UC.size();
	}
	
	public void print(Appendable out) {
		try {			
			out.append( "Tg: [\n" );
			if( UC != null ) {
				for( Unfolding unf : UC ) {
					out.append( ATermUtils.toString( unf.getResult() ) );
					out.append( ", " );
				}
				out.append( "\n" );
			}
			
			out.append( "]" );
		} catch( IOException e ) {
			e.printStackTrace();
		}
	}	

	public void print() {
		print( System.out );
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		print( sb );
		return sb.toString();
	}
}
