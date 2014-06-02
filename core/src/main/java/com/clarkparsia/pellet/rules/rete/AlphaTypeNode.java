// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.Iterator;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.iterator.IteratorUtils;
import org.mindswap.pellet.utils.iterator.NestedIterator;

import aterm.ATermAppl;

import com.clarkparsia.pellet.rules.model.AtomIConstant;
import com.clarkparsia.pellet.rules.model.AtomObject;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.ClassAtom;
import com.clarkparsia.pellet.rules.model.RuleAtom;

/**
 */
public class AlphaTypeNode extends AlphaNode {
	private final ATermAppl predicate;
	private final ATermAppl name;
	private Individual node;

	public AlphaTypeNode(ABox abox, ATermAppl predicate) {
		this(abox, predicate, null);
	}
	
	public AlphaTypeNode(ABox abox, ATermAppl predicate, ATermAppl name) {
		super(abox);
	    this.predicate = predicate;
	    this.name = name;
    }
	
	protected Individual initNode() {
		if (node == null) {			
			node = (Individual) initNode(name);
		}
		assert node != null;
		return node;
	}
	
	public boolean activate(Individual ind, ATermAppl type, DependencySet ds) {
		assert predicate.equals(type);
		if (name != null) {
			Individual node = initNode();
			if (!ind.isSame(node)) {
				return false;
			}
		}
		
		activate(WME.createType(ind, type, ds));
		return true;
	}

	public Iterator<WME> getMatches(int argIndex, Node arg) {
		if (name != null || argIndex != 0) {
			throw new IndexOutOfBoundsException();
		}
		
		if (!(arg instanceof Individual)) {
			throw new IllegalArgumentException();
		}
		
		DependencySet depends = arg.getDepends(predicate);
		
	    return (depends == null) 
	    	? IteratorUtils.<WME>emptyIterator() 
	    	: IteratorUtils.<WME>singletonIterator(WME.createType((Individual) arg, predicate, depends));

    }

	public Iterator<WME> getMatches() {
		Iterator<Individual> inds = (name == null) ? abox.getIndIterator() : IteratorUtils.singletonIterator(initNode());
		return new NestedIterator<Individual, WME>(inds) {
			@Override
            public Iterator<WME> getInnerIterator(Individual ind) {
				DependencySet depends = ind.getDepends(predicate);
				
			    return (depends == null) 
			    	? IteratorUtils.<WME>emptyIterator() 
			    	: IteratorUtils.<WME>singletonIterator(WME.createType(ind, predicate, depends));
            }
		};
	}
	
	public boolean matches(RuleAtom atom) {
		return (atom instanceof ClassAtom) && atom.getPredicate().equals(predicate) && argMatches((ClassAtom) atom);
	}
	
	private boolean argMatches(ClassAtom atom) {
		AtomObject arg = atom.getArgument();
		return name == null 
			? arg instanceof AtomVariable 
			: (arg instanceof AtomIConstant && ((AtomIConstant) arg).getValue().equals(name));
	}
	
	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + predicate.hashCode();
	    return result;
    }

	@Override
    public boolean equals(Object obj) {
	    if (this == obj) {
		    return true;
	    }
	    if (!(obj instanceof AlphaTypeNode)) {
		    return false;
	    }
	    AlphaTypeNode other = (AlphaTypeNode) obj;
	    return predicate.equals(other.predicate) && (name == null ? other.name == null : name.equals(other.name));
    }

	public String toString() {
		return ATermUtils.toString(predicate) + "(" + (name == null ? "0" : name) + ")";
	}
}
