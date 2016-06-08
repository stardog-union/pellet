// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.el;

import aterm.ATermAppl;
import com.clarkparsia.pellet.utils.CollectionUtils;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.mindswap.pellet.RBox;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.iterator.IteratorUtils;

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
public class RoleRestrictionCache
{
	private final Map<ATermAppl, ATermAppl> _domains;
	private final Map<ATermAppl, ATermAppl> _ranges;

	public RoleRestrictionCache(final RBox rbox)
	{
		_domains = CollectionUtils.makeMap();
		_ranges = CollectionUtils.makeMap();

		prepareDomainsRanges(rbox);
	}

	private void prepareDomainsRanges(final RBox rbox)
	{
		for (final Role role : rbox.getRoles())
		{
			final Iterator<ATermAppl> assertedDomains = rbox.getAssertedDomains(role);
			if (assertedDomains.hasNext())
				addTo(_domains, role.getName(), IteratorUtils.toSet(assertedDomains));

			final Iterator<ATermAppl> assertedRanges = rbox.getAssertedRanges(role);
			if (assertedRanges.hasNext())
				addTo(_ranges, role.getName(), IteratorUtils.toSet(assertedRanges));
		}
	}

	private static void addTo(final Map<ATermAppl, ATermAppl> map, final ATermAppl roleName, final Set<ATermAppl> asserted)
	{
		if (asserted.isEmpty())
			return;

		ATermAppl range = null;
		if (asserted.size() == 1)
			range = asserted.iterator().next();
		else
			range = ATermUtils.makeAnd(ATermUtils.toSet(asserted));

		range = ELSyntaxUtils.simplify(ATermUtils.nnf(range));

		map.put(roleName, range);
	}

	public Map<ATermAppl, ATermAppl> getDomains()
	{
		return _domains;
	}

	public Map<ATermAppl, ATermAppl> getRanges()
	{
		return _ranges;
	}

	public ATermAppl getDomain(final ATermAppl prop)
	{
		return _domains.get(prop);
	}

	public ATermAppl getRange(final ATermAppl prop)
	{
		return _ranges.get(prop);
	}
}
