// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.reachability;

/**
 * @author Evren Sirin
 */
public class AndNode extends Node {

	//	private final int	activationLimit;
	private int activatedInputs;

	private int id;

	public AndNode(int id) {
		this.id = id;
//		this.activationLimit = activationLimit;
		this.activatedInputs = 0;

//		if( activationLimit < 2 )
//			throw new IllegalArgumentException();
	}

	@Override
	public boolean inputActivated() {
//		if( activatedInputs >= activationLimit )
//			throw new IllegalStateException();

		return (++activatedInputs == inputs.size());
	}

	@Override
	public boolean isActive() {
		return (activatedInputs == inputs.size());
	}

	@Override
	public boolean isRedundant() {
		return outputs.isEmpty() || inputs.size() == 1 || outputs.size() == 1
		                                                  && outputs.iterator().next() instanceof AndNode;
	}

	@Override
	public void reset() {
		activatedInputs = 0;
	}

	@Override
	public String toString() {
		return "And(" + id + ")[" + activatedInputs + "," + inputs.size() + "]";
	}
}
