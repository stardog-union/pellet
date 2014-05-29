// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tbox.impl;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.BinarySet;
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
public class BinaryTBox {
	public static final Logger log = Logger.getLogger( UnaryTBox.class.getName() );
	
	private Map<BinarySet<ATermAppl>,Unfolding> unfoldings;
	private Map<ATermAppl,List<Unfolding>> conditionalUnfoldings;
		
	public BinaryTBox() {
		unfoldings = CollectionUtils.makeMap();
		conditionalUnfoldings = CollectionUtils.makeIdentityMap();
	}
	
	public void add(BinarySet<ATermAppl> set, ATermAppl result, Set<ATermAppl> explanation ) {		
		if( log.isLoggable( Level.FINE ) )
			log.fine("Add sub: (" + ATermUtils.toString(set.first()) + ", "
					+ ATermUtils.toString(set.second()) + ") < "
					+ ATermUtils.toString(result));

		result = ATermUtils.normalize( result );
		
		unfoldings.put( set, Unfolding.create( result, explanation ) );
		
		addUnfolding( set.first(), set.second(), result, explanation );
		addUnfolding( set.second(), set.first(), result, explanation );
	}
	
	private void addUnfolding(ATermAppl c, ATermAppl condition, ATermAppl result, Set<ATermAppl> explanation ) {
		List<Unfolding> list = conditionalUnfoldings.get( c );
		if( list == null ) {
			list = CollectionUtils.makeList();
			conditionalUnfoldings.put( c, list );
		}
		list.add( Unfolding.create( result, condition, explanation ) );
	}
	
	public Unfolding unfold(BinarySet<ATermAppl> set) {
		return unfoldings.get( set );
	}
	
	public Iterator<Unfolding> unfold(ATermAppl concept) {
		List<Unfolding> unfoldingList = conditionalUnfoldings.get( concept );
		return  unfoldingList == null
			? IteratorUtils.<Unfolding>emptyIterator()
			: unfoldingList.iterator();
	}
	
	public boolean contains(ATermAppl concept) {
		return conditionalUnfoldings.containsKey( concept );
	}
	
	public void print(Appendable out) throws IOException {
		for( Entry<BinarySet<ATermAppl>, Unfolding> e : unfoldings.entrySet() ) {
			BinarySet<ATermAppl> set = e.getKey();
			out.append( "(" );
			out.append( ATermUtils.toString( set.first() ) );
			out.append( "," );
			out.append( ATermUtils.toString( set.second() ) );
			out.append( ") < " );
			out.append( e.getValue().toString() );
			out.append( "\n" );
		}		
	}
}
