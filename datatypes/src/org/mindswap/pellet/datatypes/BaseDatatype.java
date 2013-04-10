
package org.mindswap.pellet.datatypes;

import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;


/**
 * @author Evren Sirin
 */
public abstract class BaseDatatype implements Datatype {
	protected ATermAppl name;
	
	public BaseDatatype(ATermAppl name) {
		this.name = name;
	}
	
	public ATermAppl getName() {
		return name;
	}
	
	final public String getURI() {
	    return name == null ? null : name.getName();
	}

	public boolean isEmpty() {
		return size() == 0;
	}
	
	public boolean contains( Object value, AtomicDatatype datatype ) {
		return this.contains(value);					
	}
    
    public ATermAppl getValue( int n ) {
//        System.out.println("BaseDatatype " + this);
         return ATermUtils.makeTypedLiteral( n + "", name.getName());    
    }
}
