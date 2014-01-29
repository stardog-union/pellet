// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.owlapi.explanation.io.manchester;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObject;

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
public class DescriptionSorter {

	/**
	 * Sorts a set of OWLObjects alphabetically based on toString values.
	 * Named objects always come before unnamed objects.
	 * 
	 * @param set the set to sort
	 * @return the sorted version of the set
	 */
	public static <N extends OWLObject> Set<N> toSortedSet(Collection<N> set) {
	    Set<N> sorted = new TreeSet<N>(new Comparator<N>() {
	        public int compare(N o1, N o2) {
	        	boolean named1 = o1 instanceof OWLNamedObject;
	        	boolean named2 = o2 instanceof OWLNamedObject;
	        	int cmp;
	        	if (named1 && !named2)
	        		cmp = -1;
	        	else if (!named1 && named2)
	        		cmp = 1;
	        	else 
	        		cmp = o1.toString().compareTo(o2.toString());
	        	
	        	if( cmp == 0 )
	        		cmp = ((OWLNamedObject) o1).getIRI().compareTo( ((OWLNamedObject) o2).getIRI() );
	        	
	        	return cmp;
	        }
	    });
	    sorted.addAll(set);
	    return sorted;
	}

}
