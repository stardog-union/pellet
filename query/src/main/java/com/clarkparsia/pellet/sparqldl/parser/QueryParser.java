// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.parser;

import java.io.InputStream;

import org.mindswap.pellet.KnowledgeBase;

import com.clarkparsia.pellet.sparqldl.model.Query;

/**
 * <p>
 * Title: SPARQL-DL Query Parser Interface
 * </p>
 * <p>
 * Description: 
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Petr Kremen
 */
public interface QueryParser {

	public Query parse(final String queryString, KnowledgeBase kb);

	public Query parse(final InputStream stream, KnowledgeBase kb);

	public Query parse(final com.hp.hpl.jena.query.Query stream, KnowledgeBase kb);
}
