// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.blocking;

import java.util.HashSet;
import java.util.Set;

import org.mindswap.pellet.Edge;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.Role;

/**
 * A class to keep track of the current individual being tested for blocking
 * conditions. Current context stores the blocker candidate and caches the
 * incoming edges to the (possibly) blocked individual since multiple blocking
 * conditions need to access that information.
 * 
 * @author Evren Sirin
 */
public class BlockingContext {
	Individual blocked;
	Individual blocker;
	Set<Role> rolesToBlocked;
	
	public BlockingContext(Individual blocked) {
		this.blocked = blocked;
		this.blocker = blocked;
	}

	/**
	 * Sets the blocker to the parent of current blocker and checks if if the
	 * new blocker candidate is allowed to block. Root nodes are not allowed to
	 * block.
	 * 
	 * @return <code>true</code> if the new blocker candidate is allowed to
	 *         block
	 */
	public boolean moveBlockerUp() {
		this.blocker = blocker.getParent();
		this.rolesToBlocked = null;
		
		return !blocker.isRoot();
	}

	/**
	 * Sets the blocker to the specified child of the current blocker and
	 * returns if the new blocker candidate is allowed to block. The child 
	 * is not allowed to block if it is a literal, or a root, or pruned/merged,
	 * or is blocked itself.
	 * 
	 * @param child child of the current blocker
	 * @return <code>true</code> if the new blocker candidate is allowed to
	 *         block
	 */
	public boolean moveBlockerDown(Node child) {
		if( child.isLiteral() || child.isRoot() || child.isPruned() || child.isMerged()
				|| ((Individual) child).isBlocked() || child.equals( blocker ) )
			return false;
		
		this.blocker = (Individual) child;
		this.rolesToBlocked = null;

		return true;
	}
	
	/**
	 * Returns if the blocked node is an r-successor of its parent.
	 * 
	 * @param r the property to check for r-successor relation
	 * @return <true> if the blocked node is an r-successor of its parent.
	 */
	public boolean isRSuccessor(Role r) {
		return getIncomingRoles().contains( r );
	}
		
	/**
	 * Returns if the role from the parent of blocked candidate has any inverse
	 * super properties.
	 * 
	 * @return if the role from the parent of blocked candidate has any inverse
	 *         super properties
	 */
	public boolean isInvSuccessor() {
		for( Role role : getIncomingRoles() ) {
			if( role.isAnon() ) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Returns the roles that points to the blocked candidate from its parent and
	 * cache the result for future use.
	 * 
	 * @return the roles that points to the blocked candidate from its parent
	 */
	protected Set<Role> getIncomingRoles() {
		if( rolesToBlocked == null ) {
			rolesToBlocked = getIncomingRoles( blocked );
			
			assert rolesToBlocked != null;
		}
		
		return rolesToBlocked;
	}
	
	/**
	 * Returns the roles that points to the given individual from its parent.
	 * 
	 * @param ind individual to check
	 * @return the roles that points to the given individual from its parent
	 */
	protected static Set<Role> getIncomingRoles(Individual ind) {
		Set<Role> rolesToBlocked = null;
		for( Edge e : ind.getInEdges() ) {
			if( e.getFrom().equals( ind.getParent() ) ) {
				if( rolesToBlocked == null )
					rolesToBlocked = e.getRole().getSuperRoles();
				else if( !rolesToBlocked.contains( e.getRole() ) ) {
					rolesToBlocked = new HashSet<Role>( rolesToBlocked );
					rolesToBlocked.addAll( e.getRole().getSuperRoles() );
				}
			}
		}
		return rolesToBlocked;
	}
	
	public String toString() {
		return blocked + " blocked by " + blocker;
	}
}
