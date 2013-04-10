// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.el;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.mindswap.pellet.RBox;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.utils.ATermUtils;
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
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Harris Lin
 */
public class RoleRestrictionCache {
	private Map<ATermAppl, ATermAppl> m_Domains;
	private Map<ATermAppl, ATermAppl> m_Ranges;
	
	public RoleRestrictionCache(RBox rbox) {
		m_Domains = CollectionUtils.makeMap();
		m_Ranges = CollectionUtils.makeMap();

		prepareDomainsRanges( rbox );
	}

	private void prepareDomainsRanges(RBox rbox) {
		for (Role role : rbox.getRoles()) {
			Iterator<ATermAppl> assertedDomains = rbox.getAssertedDomains(role);
			if (assertedDomains.hasNext()) {
				addTo(m_Domains, role.getName(), IteratorUtils.toSet(assertedDomains));
			}
			
			Iterator<ATermAppl> assertedRanges = rbox.getAssertedRanges(role);
			if (assertedRanges.hasNext()) {
				addTo(m_Ranges, role.getName(), IteratorUtils.toSet(assertedRanges));
			}
		}
	}

	private static void addTo(Map<ATermAppl, ATermAppl> map, ATermAppl roleName, Set<ATermAppl> asserted) {
		if (asserted.isEmpty()) return;
		
		ATermAppl range = null;
		if (asserted.size() == 1) {
			range = asserted.iterator().next();
		} else {
			range = ATermUtils.makeAnd(ATermUtils.toSet(asserted));
		}

		range = ELSyntaxUtils.simplify( ATermUtils.nnf( range ) );

		map.put(roleName, range);
	}

	public Map<ATermAppl, ATermAppl> getDomains() {
		return m_Domains;
	}
	
	public Map<ATermAppl, ATermAppl> getRanges() {
		return m_Ranges;
	}

	public ATermAppl getDomain(ATermAppl prop) {
		return m_Domains.get(prop);
	}
	
	public ATermAppl getRange(ATermAppl prop) {
		return m_Ranges.get(prop);
	}
}
