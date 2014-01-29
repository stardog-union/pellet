// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC.
// <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms
// of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under
// the terms of the MIT License.
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;

import com.clarkparsia.pellet.utils.CollectionUtils;

public class TuBox extends TBoxBase {

	private Map<ATermAppl, List<Unfolding>>	unfoldingMap;

	private Collection<ATermAppl>			termsToNormalize	= null;

	/*
	 * Constructors
	 */

	public TuBox(TBoxExpImpl tbox) {
		super( tbox );
	}

	public boolean addDef(ATermAppl axiom) {
		boolean added = false;

		ATermAppl name = (ATermAppl) axiom.getArgument( 0 );
		TermDefinition td = getTD( name );
		if( td == null ) {
			td = new TermDefinition();
			termhash.put( name, td );
		}

		added = td.addDef( axiom );

		if( added && termsToNormalize != null )
			termsToNormalize.add( name );

		return added;
	}

	public boolean removeDef(ATermAppl axiom) {
		boolean removed = super.removeDef( axiom );

		if( removed && termsToNormalize != null )
			termsToNormalize.add( (ATermAppl) axiom.getArgument( 0 ) );

		return removed;
	}

	public void updateDef(ATermAppl axiom) {
		ATermAppl c = (ATermAppl) axiom.getArgument( 0 );
		if( ATermUtils.isPrimitive( c ) )
			termsToNormalize.add( c );
	}

	public List<Unfolding> unfold(ATermAppl c) {
		List<Unfolding> list = unfoldingMap.get( c );
		return list != null ? list : Collections.<Unfolding>emptyList();
	}

	/**
	 * Normalize all the definitions in the Tu
	 */
	public void normalize() {
		if( termsToNormalize == null ) {
			termsToNormalize = termhash.keySet();
			unfoldingMap = CollectionUtils.makeIdentityMap();
		}
		else if( log.isLoggable( Level.FINE ) ) {
			log.fine( "Normalizing " + termsToNormalize );
		}

		for( ATermAppl c : termsToNormalize ) {
			TermDefinition td = termhash.get( c );
			td.clearDependencies();

			ATermAppl notC = ATermUtils.makeNot( c );

			List<Unfolding> unfoldC = new ArrayList<Unfolding>();

			if( !td.getEqClassAxioms().isEmpty() ) {
				List<Unfolding> unfoldNotC = new ArrayList<Unfolding>();

				for( ATermAppl eqClassAxiom : td.getEqClassAxioms() ) {
					ATermAppl unfolded = (ATermAppl) eqClassAxiom.getArgument( 1 );
					Set<ATermAppl> ds = tbox.getAxiomExplanation( eqClassAxiom );

					ATermAppl normalized = ATermUtils.normalize( unfolded );
					ATermAppl normalizedNot = ATermUtils.negate( normalized );

					unfoldC.add( Unfolding.create( normalized, ds ) );
					unfoldNotC.add( Unfolding.create( normalizedNot, ds ) );
				}

				unfoldingMap.put( notC, unfoldNotC );
			}
			else
				unfoldingMap.remove( notC );

			for( ATermAppl subClassAxiom : td.getSubClassAxioms() ) {
				ATermAppl unfolded = (ATermAppl) subClassAxiom.getArgument( 1 );
				Set<ATermAppl> ds = tbox.getAxiomExplanation( subClassAxiom );

				ATermAppl normalized = ATermUtils.normalize( unfolded );
				unfoldC.add( Unfolding.create( normalized, ds ) );
			}

			if( !unfoldC.isEmpty() )
				unfoldingMap.put( c, unfoldC );
			else
				unfoldingMap.remove( c );
		}

		termsToNormalize = new HashSet<ATermAppl>();
		// termsToNormalize = null;

		if( PelletOptions.USE_ROLE_ABSORPTION )
			absorbRanges( tbox );
	}

	private void absorbRanges(TBoxExpImpl tbox) {
		List<Unfolding> unfoldTop = unfoldingMap.get( ATermUtils.TOP );
		if( unfoldTop == null )
			return;

		List<Unfolding> newUnfoldTop = new ArrayList<Unfolding>();
		for( Unfolding unfolding : unfoldTop ) {
			ATermAppl unfolded = unfolding.getResult();
			Set<ATermAppl> explain = unfolding.getExplanation();

			if( ATermUtils.isAllValues( unfolded ) ) {
				ATerm r = unfolded.getArgument( 0 );
				ATermAppl range = (ATermAppl) unfolded.getArgument( 1 );

				kb.addRange( r, range, explain );

				tbox.getAbsorbedAxioms().addAll( explain );
			}
			else if( ATermUtils.isAnd( unfolded ) ) {
				ATermList l = (ATermList) unfolded.getArgument( 0 );
				ATermList newList = ATermUtils.EMPTY_LIST;
				for( ; !l.isEmpty(); l = l.getNext() ) {
					ATermAppl term = (ATermAppl) l.getFirst();
					if( term.getAFun().equals( ATermUtils.ALLFUN ) ) {
						ATerm r = term.getArgument( 0 );
						ATermAppl range = (ATermAppl) term.getArgument( 1 );

						kb.addRange( r, range, explain );

						tbox.getAbsorbedAxioms().addAll( explain );
					}
					else {
						newList = newList.insert( term );
					}
				}

				if( !newList.isEmpty() ) {
					newUnfoldTop.add( Unfolding.create( ATermUtils.makeAnd( newList ), explain ) );
				}
			}
			else {
				newUnfoldTop.add( unfolding );
			}
		}

		if( newUnfoldTop.isEmpty() )
			unfoldingMap.remove( ATermUtils.TOP );

	}

	/*
	 * Accessor Methods
	 */

	public boolean addIfUnfoldable(ATermAppl term) {
		ATermAppl name = (ATermAppl) term.getArgument( 0 );
		ATermAppl body = (ATermAppl) term.getArgument( 1 );
		TermDefinition td = getTD( name );

		if( !ATermUtils.isPrimitive( name ) )
			return false;

		if( td == null )
			td = new TermDefinition();

		// Basic Check
		if( !td.isUnique( term ) )
			return false;

		// Loop Checks
		Set<ATermAppl> dependencies = ATermUtils.findPrimitives( body );
		Set<ATermAppl> seen = new HashSet<ATermAppl>();
		if( !td.getDependencies().containsAll( dependencies ) ) {
			// Fast check failed
			for( Iterator<ATermAppl> iter = dependencies.iterator(); iter.hasNext(); ) {
				ATermAppl current = iter.next();

				boolean result = findTarget( current, name, seen );
				if( result ) {
					return false;
				}
			}
		}

		boolean added = addDef( term );

		return added;
	}

	protected boolean findTarget(ATermAppl term, ATermAppl target, Set<ATermAppl> seen) {
		List<ATermAppl> queue = new ArrayList<ATermAppl>();
		queue.add( term );

		while( !queue.isEmpty() ) {
			kb.timers.checkTimer( "preprocessing" );
			ATermAppl current = queue.remove( queue.size() - 1 );

			if( !seen.add( current ) ) {
				continue;
			}

			if( current.equals( target ) ) {
				return true;
			}

			TermDefinition td = this.getTD( current );
			if( td != null ) {
				// Shortcut
				if( td.getDependencies().contains( target ) ) {
					return true;
				}

				queue.addAll( td.getDependencies() );
			}
		}

		return false;
	}

	public void print(Appendable out) {
		try {
			out.append( "Tu: [\n" );
			for( ATermAppl c : unfoldingMap.keySet() ) {
				List<Unfolding> unfoldedList = unfold( c );
				if( !unfoldedList.isEmpty() ) {
					out.append( ATermUtils.toString( c ) ).append( " -> " );
					for( Unfolding unf : unfoldedList )
						out.append( ATermUtils.toString( unf.getResult() ) ).append( ", " );
					out.append( "\n" );
				}
			}
			out.append( "]\n" );
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
