// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class ABoxStats {

	/**
	 * Total number of ABox consistency checks
	 */
	public long		consistencyCount	= 0;

	/**
	 * Total number of satisfiability tests performed
	 */
	public long		satisfiabilityCount	= 0;

	/**
	 * size of the completion graph
	 */
	public int		size				= 0;
	public short	treeDepth			= 0;

	public int		backjumps			= 0;
	public int		backtracks			= 0;
	public int		globalRestores		= 0;
	public int		localRestores		= 0;

	public int		branch				= 0;

	public void add(ABoxStats other) {
		backjumps += other.backjumps;
		backtracks += other.backtracks;
		globalRestores += other.globalRestores;
		localRestores += other.localRestores;
		branch += other.branch;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( " Branches " + branch );
		sb.append( " Tree depth: " + treeDepth );
		sb.append( " Graph size: " + size );
		sb.append( " Restores " + globalRestores + " global " + localRestores + " local" );
		sb.append( " Backtracks " + backtracks );
		sb.append( " Avg backjump " + (backjumps / (double) backtracks) );
		return sb.toString();
	}
}
