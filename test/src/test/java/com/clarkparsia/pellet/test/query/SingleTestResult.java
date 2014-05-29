// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.query;

import java.net.URI;

/**
 * <p>
 * Title: Engine for processing DAWG test manifests
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
public class SingleTestResult {

	private URI uri;

	private ResultEnum result;

	private long time;

	public SingleTestResult(URI uri, ResultEnum result, long time) {
		super();
		this.uri = uri;
		this.result = result;
		this.time = time;
	}

	public URI getUri() {
		return uri;
	}

	public ResultEnum getResult() {
		return result;
	}

	public long getTime() {
		return time;
	}
}