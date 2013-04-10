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


package org.mindswap.pellet;


import static com.clarkparsia.pellet.utils.TermFactory.BOTTOM_DATA_PROPERTY;
import static com.clarkparsia.pellet.utils.TermFactory.BOTTOM_OBJECT_PROPERTY;
import static com.clarkparsia.pellet.utils.TermFactory.TOP_DATA_PROPERTY;
import static com.clarkparsia.pellet.utils.TermFactory.TOP_OBJECT_PROPERTY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.taxonomy.TaxonomyNode;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

/**
 * @author Evren Sirin
 *
 */
public class RoleTaxonomyBuilder {
    protected static Logger log = Logger.getLogger( Taxonomy.class.getName() ); 
    
	public static final ATermAppl TOP_ANNOTATION_PROPERTY = ATermUtils.makeTermAppl("_TOP_ANNOTATION_PROPERTY_");
	public static final ATermAppl BOTTOM_ANNOTATION_PROPERTY = ATermUtils.makeTermAppl("_BOTTOM_ANNOTATION_PROPERTY_");
    
	private static enum Propagate { UP, DOWN, NONE }
    
	protected Collection<Role> properties;
	
	protected Taxonomy<ATermAppl> taxonomy; 
	protected RBox rbox;
	protected Role topRole;
	protected Role bottomRole;
	protected PropertyType propertyType;
	
	public RoleTaxonomyBuilder(RBox rbox, PropertyType type) {
		this.rbox = rbox;
		this.propertyType = type;
		properties =  rbox.getRoles();
		
		switch(this.propertyType){
		case OBJECT:
			taxonomy = new Taxonomy<ATermAppl>( null, TOP_OBJECT_PROPERTY, BOTTOM_OBJECT_PROPERTY );
			break;
		case DATATYPE:
			taxonomy = new Taxonomy<ATermAppl>( null, TOP_DATA_PROPERTY, BOTTOM_DATA_PROPERTY );
			break;
		case ANNOTATION:
			taxonomy = new Taxonomy<ATermAppl>( null, TOP_ANNOTATION_PROPERTY, BOTTOM_ANNOTATION_PROPERTY );
			//Hide the artificial roles TOP_ANNOTATION_PROPERTY and BOTTOM_ANNOTATION_PROPERTY
			taxonomy.getTop().setHidden(true);
			taxonomy.getBottom().setHidden(true);
			break;
		default:
			throw new AssertionError("Unknown property type: " + this.propertyType);
		}
		
		topRole = rbox.getRole( taxonomy.getTop().getName() );
		bottomRole = rbox.getRole( taxonomy.getBottom().getName() );
	}
	
	public RoleTaxonomyBuilder(RBox rbox, boolean objectRoles) {
		this.rbox = rbox;
		
		properties =  rbox.getRoles();
	    taxonomy = objectRoles
	    	? new Taxonomy<ATermAppl>( null, TOP_OBJECT_PROPERTY, BOTTOM_OBJECT_PROPERTY )
	    	: new Taxonomy<ATermAppl>( null, TOP_DATA_PROPERTY, BOTTOM_DATA_PROPERTY );
		topRole = rbox.getRole( taxonomy.getTop().getName() );
		bottomRole = rbox.getRole( taxonomy.getBottom().getName() );
	}

	public Taxonomy<ATermAppl> classify() {
		if( log.isLoggable( Level.FINE ) ) {
			log.fine( "Properties: " + properties.size() );
		}

		for( Role r : properties ) {
			if( propertyType != r.getType() )
				continue;

			classify( r );		
		}

		return taxonomy;
	}

	int count= 0;
	private void classify(Role c) {
		if( taxonomy.contains( c.getName() ) )
			return;

		if( log.isLoggable( Level.FINER ) )
			log.finer( "Property (" + (++count) + ") " + c + "..." );
		
		if( c.getSubRoles().contains( topRole ) ) {
			taxonomy.getTop().addEquivalent( c.getName() );
			return;
		}
		else if( c.getSuperRoles().contains( bottomRole ) ) {
			taxonomy.getBottom().addEquivalent( c.getName() );
			return;
		}

		Map<TaxonomyNode<ATermAppl>, Boolean> marked = new HashMap<TaxonomyNode<ATermAppl>, Boolean>();
		mark( taxonomy.getTop(), marked, Boolean.TRUE, Propagate.NONE );
		mark( taxonomy.getBottom(), marked, Boolean.FALSE, Propagate.NONE );

		Collection<TaxonomyNode<ATermAppl>> superNodes = search( true, c, taxonomy.getTop(),
				new HashSet<TaxonomyNode<ATermAppl>>(), new ArrayList<TaxonomyNode<ATermAppl>>(),
				marked );

		marked = new HashMap<TaxonomyNode<ATermAppl>, Boolean>();
		mark( taxonomy.getTop(), marked, Boolean.FALSE, Propagate.NONE );
		mark( taxonomy.getBottom(), marked, Boolean.TRUE, Propagate.NONE );

		if( superNodes.size() == 1 ) {
			TaxonomyNode<ATermAppl> sup = superNodes.iterator().next();

			// if i has only one super class j and j is a subclass
			// of i then it means i = j. There is no need to classify
			// i since we already know everything about j
			if( subsumed( sup, c, marked ) ) {
				if( log.isLoggable( Level.FINER ) )
					log.finer( ATermUtils.toString( c.getName() ) + " = " + ATermUtils.toString( sup.getName() ) );

				taxonomy.addEquivalentNode( c.getName(), sup );
				return;
			}
		}

		Collection<TaxonomyNode<ATermAppl>> subNodes = search( false, c, taxonomy.getBottom(),
				new HashSet<TaxonomyNode<ATermAppl>>(), new ArrayList<TaxonomyNode<ATermAppl>>(),
				marked );
		
		List<ATermAppl> supers = new ArrayList<ATermAppl>();
		for( TaxonomyNode<ATermAppl> n : superNodes ) {
			supers.add( n.getName() );
		}

		List<ATermAppl> subs = new ArrayList<ATermAppl>();
		for( TaxonomyNode<ATermAppl> n : subNodes ) {
			subs.add( n.getName() );
		}

		taxonomy.addNode( Collections.singleton( c.getName() ),
				supers, subs, /* hidden = */false );
	}	
	
	private Collection<TaxonomyNode<ATermAppl>> search(boolean topSearch, Role c,
			TaxonomyNode<ATermAppl> x, Set<TaxonomyNode<ATermAppl>> visited,
			List<TaxonomyNode<ATermAppl>> result, Map<TaxonomyNode<ATermAppl>, Boolean> marked) {
		List<TaxonomyNode<ATermAppl>> posSucc = new ArrayList<TaxonomyNode<ATermAppl>>();
		visited.add( x );

		Collection<TaxonomyNode<ATermAppl>> list = topSearch
			? x.getSubs()
			: x.getSupers();
		for( TaxonomyNode<ATermAppl> next : list ) {
			if( topSearch ) {
				if( subsumes( next, c, marked ) )
					posSucc.add( next );
			}
			else {
				if( subsumed( next, c, marked ) )
					posSucc.add( next );
			}
		}

		if( posSucc.isEmpty() ) {
			result.add( x );
		}
		else {
			for( TaxonomyNode<ATermAppl> y : posSucc ) {
				if( !visited.contains( y ) )
					search( topSearch, c, y, visited, result, marked );
			}
		}

		return result;
	}


	private boolean subsumes(TaxonomyNode<ATermAppl> node, Role c,
			Map<TaxonomyNode<ATermAppl>, Boolean> marked) {	
	    Boolean cached = marked.get( node ); 
	    if( cached != null )
	        return cached.booleanValue();
		
		// check subsumption
		boolean subsumes = subsumes( rbox.getRole( node.getName() ), c );
		// create an object based on result
		Boolean value = subsumes ? Boolean.TRUE : Boolean.FALSE;
		// during top search only negative information is propagated down
		Propagate propagate = subsumes ? Propagate.NONE : Propagate.DOWN;
		// mark the node appropriately
		mark( node, marked, value, propagate);
		
		return subsumes;
	}

	private boolean subsumed(TaxonomyNode<ATermAppl> node, Role c,
			Map<TaxonomyNode<ATermAppl>, Boolean> marked) {		
	    Boolean cached = marked.get( node ); 
	    if( cached != null )
	        return cached.booleanValue();

	    // check subsumption
		boolean subsumed = subsumes( c, rbox.getRole( node.getName() ) );
		// create an object based on result
		Boolean value = subsumed ? Boolean.TRUE : Boolean.FALSE;
		// during bottom search only negative information is propagated down
		Propagate propagate = subsumed ? Propagate.NONE : Propagate.UP;
		// mark the node appropriately
		mark( node, marked, value, propagate);
		
		return subsumed;
	}
	
	private void mark(TaxonomyNode<ATermAppl> node, Map<TaxonomyNode<ATermAppl>, Boolean> marked,
			Boolean value, Propagate propagate) {
	    Boolean exists = marked.get( node );
	    if( exists != null ) {
	        if( !exists.equals( value ) )
	            throw new RuntimeException("Inconsistent classification result " + 
	                node.getName() + " " + exists + " " + value);
	        else 
	            return;
	    }
	    marked.put( node, value );
	    
	    if( propagate != Propagate.NONE ) {
	    	Collection<TaxonomyNode<ATermAppl>> others = (propagate == Propagate.UP)
				? node.getSupers()
				: node.getSubs();
			for( TaxonomyNode<ATermAppl> next : others ) {
				mark( next, marked, value, propagate );
			}
		}
	}
	
	private boolean subsumes(Role sup, Role sub) {
	    boolean result = sup.isSuperRoleOf( sub );
		ATermUtils.assertTrue(sub.isSubRoleOf( sup ) == result);
		return result;
	} 
	
}
