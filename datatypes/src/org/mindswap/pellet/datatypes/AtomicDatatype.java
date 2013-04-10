
package org.mindswap.pellet.datatypes;

import java.util.Set;

/**
 * @author Evren Sirin
 */
public interface AtomicDatatype extends Datatype {	
	public AtomicDatatype not();
	
	public AtomicDatatype intersection(AtomicDatatype dt);
	
	public AtomicDatatype union(AtomicDatatype dt); 
	
	public AtomicDatatype difference(AtomicDatatype dt); 
	
	public AtomicDatatype getPrimitiveType();
	
	public AtomicDatatype enumeration(Set<Object> values);
}
