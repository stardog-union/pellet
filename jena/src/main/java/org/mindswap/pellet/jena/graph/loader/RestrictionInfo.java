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
 * Description: A simple structure to _cache information related to restrictions.
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
	private Node _type;
	private Node _predicate;
	private Node _filler;
	private Node _detail;
	private Bool _isObjectRestriction = Bool.UNKNOWN;

	public RestrictionInfo(final Node predicate)
	{
		this._predicate = predicate;
	}

	public Node getType()
	{
		return _type;
	}

	public void setType(final Node type)
	{
		this._type = type;
	}

	public Node getPredicate()
	{
		return _predicate;
	}

	public void setPredicate(final Node predicate)
	{
		this._predicate = predicate;
	}

	public Node getFiller()
	{
		return _filler;
	}

	public void setFiller(final Node filler)
	{
		this._filler = filler;
	}

	public Node getDetail()
	{
		return _detail;
	}

	public void setDetail(final Node detail)
	{
		this._detail = detail;
	}

	public Bool isObjectRestriction()
	{
		return _isObjectRestriction;
	}

	public void setObjectRestriction(final boolean isObjectRestriction)
	{
		this._isObjectRestriction = Bool.create(isObjectRestriction);
	}
}
