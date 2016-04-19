// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena.graph.loader;

import org.apache.jena.graph.Node;
import org.mindswap.pellet.utils.Bool;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: A simple structure to cache information related to restrictions.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public class RestrictionInfo
{
	private Node type;
	private Node predicate;
	private Node filler;
	private Node detail;
	private Bool isObjectRestriction = Bool.UNKNOWN;

	public RestrictionInfo(final Node predicate)
	{
		this.predicate = predicate;
	}

	public Node getType()
	{
		return type;
	}

	public void setType(final Node type)
	{
		this.type = type;
	}

	public Node getPredicate()
	{
		return predicate;
	}

	public void setPredicate(final Node predicate)
	{
		this.predicate = predicate;
	}

	public Node getFiller()
	{
		return filler;
	}

	public void setFiller(final Node filler)
	{
		this.filler = filler;
	}

	public Node getDetail()
	{
		return detail;
	}

	public void setDetail(final Node detail)
	{
		this.detail = detail;
	}

	public Bool isObjectRestriction()
	{
		return isObjectRestriction;
	}

	public void setObjectRestriction(final boolean isObjectRestriction)
	{
		this.isObjectRestriction = Bool.create(isObjectRestriction);
	}
}
