// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tbox.impl;

import static com.clarkparsia.pellet.utils.TermFactory.not;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.iterator.IteratorUtils;

import aterm.ATermAppl;

import com.clarkparsia.pellet.utils.CollectionUtils;

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
public class PrimitiveTBox {
	public static final Logger log	= Logger.getLogger( PrimitiveTBox.class.getName() );

	private Map<ATermAppl,Unfolding> definitions;
	private Map<ATermAppl,Set<ATermAppl>> dependencies;
	
	public PrimitiveTBox() {
		definitions = CollectionUtils.makeIdentityMap();
		dependencies = CollectionUtils.makeIdentityMap();
	}
	
	public boolean contains(ATermAppl concept) {
		return definitions.containsKey( concept );
	}	
	
	public Unfolding getDefinition(ATermAppl concept) {
		return definitions.get( concept );
	}	
	
//	public boolean add(ATermAppl axiom, Set<ATermAppl> explanation) {
//		boolean added = false;
//		
//		if( axiom.getAFun().equals( ATermUtils.EQCLASSFUN ) ) {
//			ATermAppl c1 = (ATermAppl) axiom.getArgument( 0 );
//			ATermAppl c2 = (ATermAppl) axiom.getArgument( 1 );
//		
//			added = addDefinition( c1, c2, explanation );
//			if( !added ) {
//				added = addDefinition( c2, c1, explanation );
//			}			
//		}
//		
//		return added;
//	}
	
	public boolean add(ATermAppl concept, ATermAppl definition, Set<ATermAppl> explanation) {
		if( !ATermUtils.isPrimitive( concept ) || contains( concept ) ) {
			return false;
		}
		
		Set<ATermAppl> deps = ATermUtils.findPrimitives( definition );
		Set<ATermAppl> seen = new HashSet<ATermAppl>();
		
		for( ATermAppl current : deps ) {
			boolean result = findTarget( current, concept, seen );
			if( result ) {
				return false;
			}
		}

		addDefinition( concept, definition, explanation );
		addDefinition( not(concept), not(definition), explanation );
		dependencies.put( concept, deps );
		
		return true;
	}
	
	protected void addDefinition(ATermAppl concept, ATermAppl definition, Set<ATermAppl> explanation) {
		definition = ATermUtils.normalize( definition );

		if( log.isLoggable( Level.FINE ) )
			log.fine( "Def: " + ATermUtils.toString( concept ) + " = " + ATermUtils.toString( definition )  );
		
		definitions.put( concept, Unfolding.create( definition, explanation ) );

	}

	protected boolean findTarget(ATermAppl term, ATermAppl target, Set<ATermAppl> seen) {
		List<ATermAppl> queue = new ArrayList<ATermAppl>();
		queue.add( term );

		while( !queue.isEmpty() ) {
			ATermAppl current = queue.remove( queue.size() - 1 );

			if( !seen.add( current ) ) {
				continue;
			}

			if( current.equals( target ) ) {
				return true;
			}

			Set<ATermAppl> deps = dependencies.get( current );
			if( deps != null ) {
				// Shortcut
				if( deps.contains( target ) ) {
					return true;
				}

				queue.addAll( deps );
			}
		}

		return false;
	}
	
	public boolean remove(ATermAppl axiom) {
		return false;
	}
	
	public Iterator<Unfolding> unfold(ATermAppl concept) {
		Unfolding unfolding = definitions.get( concept );
				
		return unfolding == null
			? IteratorUtils.<Unfolding>emptyIterator()
			: IteratorUtils.singletonIterator( unfolding );
	}
	
	public void print(Appendable out) throws IOException {
		for( Entry<ATermAppl, Unfolding> e : definitions.entrySet() ) {
			out.append( ATermUtils.toString( e.getKey() ) );
			out.append( " = " );
			out.append( e.getValue().toString() );
			out.append( "\n" );
		}		
	}	
}
