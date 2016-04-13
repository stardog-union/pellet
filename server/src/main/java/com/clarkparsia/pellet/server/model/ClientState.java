// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.server.model;

import com.clarkparsia.pellet.service.reasoner.SchemaReasoner;

/**
 * @author Evren Sirin
 */
public interface ClientState extends AutoCloseable {
	SchemaReasoner getReasoner();

	/**
	 * Gets the version of the data used by this reasoner.
	 *
	 * @return  an ID of the data version
	 */
	int version();

	void close();
}