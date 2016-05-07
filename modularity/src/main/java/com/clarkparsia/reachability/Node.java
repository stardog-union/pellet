// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.reachability;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Evren Sirin
 */
public abstract class Node
{

	protected Set<Node> outputs;

	protected Set<Node> inputs;

	public Node()
	{
		this.outputs = new HashSet<>();
		this.inputs = new HashSet<>();
	}

	public void addOutput(final Node output)
	{
		if (output.equals(this))
			return;

		//		outputs.add( output );
		if (outputs.add(output))
			output.inputs.add(this);
	}

	public boolean hasOutput(final Node node)
	{
		return outputs.contains(node);
	}

	public Set<Node> getInputs()
	{
		return Collections.unmodifiableSet(inputs);
	}

	public Set<Node> getOutputs()
	{
		return Collections.unmodifiableSet(outputs);
	}

	public abstract boolean inputActivated();

	public abstract boolean isActive();

	public boolean isRedundant()
	{
		return false;
	}

	public void removeOutput(final Node output)
	{
		if (outputs.remove(output))
			output.inputs.remove(output);
	}

	public void removeInOuts()
	{
		for (final Node input : inputs)
			input.outputs.remove(this);
		inputs = null;

		for (final Node output : outputs)
			output.inputs.remove(this);
		outputs = null;
	}

	public void remove()
	{
		for (final Node input : inputs)
		{
			input.outputs.remove(this);
			for (final Node output : outputs)
				input.addOutput(output);
		}
		for (final Node output : outputs)
			output.inputs.remove(this);
		inputs = null;
		outputs = null;
	}

	public abstract void reset();
}
