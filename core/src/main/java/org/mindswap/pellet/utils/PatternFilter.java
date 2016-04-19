// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.utils;

import java.io.File;
import java.io.FileFilter;

/**
 * File filter implementation that filters by pattern matching on the file name using regular expressions. Two patterns are specified. A file is accepted if its
 * name matches the first pattern and does NOT match the second pattern.
 *
 * @author Evren Sirin
 */
public class PatternFilter implements FileFilter
{
	private final String match;
	private final String noMatch;

	public PatternFilter(final String match)
	{
		this.match = match;
		this.noMatch = "";
	}

	public PatternFilter(final String match, final String noMatch)
	{
		this.match = match;
		this.noMatch = noMatch;
	}

	@Override
	public boolean accept(final File file)
	{
		return file.getName().matches(match) && !file.getName().matches(noMatch);
	}
}
