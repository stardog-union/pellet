
package org.mindswap.pellet.datatypes;

import java.util.Set;

/**
 * @author Evren Sirin
 */
public interface UnionDatatype extends Datatype {
	public Set<Datatype> getMembers();
}
