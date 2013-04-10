// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.datatypes;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.QNameProvider;
import org.mindswap.pellet.utils.SetUtils;

import aterm.ATermAppl;

/**
 * @author Evren Sirin
 */
public abstract class BaseAtomicDatatype extends BaseDatatype implements AtomicDatatype {
	private boolean derived = false;
	protected boolean negated = false;
	protected Set<Object> values = null;
	
	public BaseAtomicDatatype(ATermAppl name) {
		super(name);
	}
	
	protected AtomicDatatype derive(Set<Object> values, boolean negated) {
		BaseAtomicDatatype newType = null;
		try {
			newType = (BaseAtomicDatatype) getClass().newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		newType.name = null;
		newType.values = values;
		newType.negated = negated;
		newType.derived = true;
		return newType;
	}

	public AtomicDatatype not() {		
		return derive(values, !negated);
	}

	public AtomicDatatype intersection(AtomicDatatype dt) {
		if(getPrimitiveType().equals(dt.getPrimitiveType()) && dt instanceof BaseAtomicDatatype) {
			BaseAtomicDatatype other = (BaseAtomicDatatype) dt;
			if(values == null) {
				if(negated)
					return this;
				else
					return other;
			}
			else if(other.values == null) {
				if(other.negated)
					return other;
				else
					return this;			
			}
			else if(negated) {
				if(other.negated)
					return derive(SetUtils.union(values, other.values), true);
				else
					return derive(SetUtils.difference(other.values, values), false);
			}
			else if(other.negated)
				return derive(SetUtils.difference(values, other.values), false);
			else
				return derive(SetUtils.intersection(values, other.values), false);
		}
		
		return null;
	}

	public AtomicDatatype union(AtomicDatatype dt) {
		if(getPrimitiveType().equals(dt.getPrimitiveType())) 
			return this.not().intersection(dt.not()).not();
		
		return null;	
	}

	public AtomicDatatype difference(AtomicDatatype dt) {
		if(getPrimitiveType().equals(dt.getPrimitiveType()))
			return this.intersection(dt.not());
		
		return null;	
	}

	public int size() {
		if(values == null)
			return negated ? 0 : Integer.MAX_VALUE;
			
		return negated ? Integer.MAX_VALUE : values.size();
	}

	public boolean contains(Object value) {
		return (values == null) || (values.contains(value) == !negated);
	}

	public AtomicDatatype enumeration(Set<Object> values) {
		return derive(values, false);
	}

	public Datatype singleton(Object value) {
		return enumeration(Collections.singleton(value));
	}

    public boolean isDerived() {
		return derived;
	}
    
    public ATermAppl getValue( int n ) {
        String uri = getPrimitiveType().getURI();
        if( values == null ) {
            if( uri == null )
                return ATermUtils.makePlainLiteral( n + "" );
            else
                return ATermUtils.makeTypedLiteral( n + "", name.getName());
        }
        else {
            // FIXME this is very inefficient
            Iterator<Object> it = values.iterator();
            for( int i = 0; i < n; i++ )
                it.next();
            Object value = it.next();
            
            if( value instanceof ATermAppl )
            	return (ATermAppl) value;
            else if( uri == null )
                return ATermUtils.makePlainLiteral( n + "" );
            else
                return ATermUtils.makeTypedLiteral( value.toString(), uri );
        }
  }
    
    
    public String toString() {
        QNameProvider qnames = new QNameProvider();
        String str;
        if( isDerived() ) 
            str = qnames.shortForm( getPrimitiveType().getName().toString() ) + " " + (negated?"not":"") + values;        
        else
            str = qnames.shortForm( name.toString() );
            
        
        return str;
    }
}
