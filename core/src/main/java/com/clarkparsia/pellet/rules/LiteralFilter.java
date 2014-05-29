// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.mindswap.pellet.Literal;
import org.mindswap.pellet.Node;

/**
 * <p>
 * Title: Literal Filter
 * </p>
 * <p>
 * Description: Filters an iterator of nodes for literals.
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
public class LiteralFilter implements Iterator<Literal> {

	private Iterator<Node> iterator;
	private Literal next;
	
	public LiteralFilter( Iterator<Node> iterator ) {
		this.iterator = iterator;
	}
	
	public boolean hasNext() {
		while ( ( next == null ) && iterator.hasNext() ) {
			Node node = iterator.next();
			if ( node.isLiteral() && node.isRootNominal() ) 
				next = ( Literal ) node;
		}
		return next != null;
	}

	public Literal next() {
		if ( !hasNext() ) {
			throw new NoSuchElementException();
		}
		
		Literal result = next;
		next = null;
		
		return result;
	}

	public void remove() {
		iterator.remove();
	}

}
