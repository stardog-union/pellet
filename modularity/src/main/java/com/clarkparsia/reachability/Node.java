// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.reachability;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * FIXME TODO : We need to add a strong type system for the class Node This is related to the kind of information carry by the EntityNode.
 */
public abstract class Node
{

	protected volatile Set<Node> _outputs = new HashSet<>();

	protected volatile Set<Node> _inputs = new HashSet<>();

	public void addOutput(final Node output)
	{
		if (output.equals(this))
			return;

		//		outputs.add( output );
		if (_outputs.add(output))
			output._inputs.add(this);
	}

	public boolean hasOutput(final Node node)
	{
		return _outputs.contains(node);
	}

	public Set<Node> getInputs()
	{
		return Collections.unmodifiableSet(_inputs);
	}

	public Stream<Node> inputs()
	{
		return _inputs.stream();
	}

	public Set<Node> getOutputs()
	{
		return Collections.unmodifiableSet(_outputs);
	}

	public Stream<Node> outputs()
	{
		return _outputs.stream();
	}

	public abstract boolean inputActivated();

	public abstract boolean isActive();

	public boolean isRedundant()
	{
		return false;
	}

	public void removeOutput(final Node output)
	{
		if (_outputs.remove(output))
			output._inputs.remove(output);
	}

	public void removeInOuts()
	{
		_inputs.forEach(input -> input._outputs.remove(Node.this));
		_inputs = null;

		_outputs.forEach(input -> input._inputs.remove(Node.this));
		_outputs = null;
	}

	public void remove()
	{
		_inputs.forEach(input ->
		{
			input._outputs.remove(Node.this);
			_outputs.forEach(input::addOutput);
		});
		_outputs.forEach(output -> output._inputs.remove(Node.this));
		_inputs = null;
		_outputs = null;
	}

	public abstract void reset();

	public boolean isEntityNode()
	{
		return false;
	}

	@SuppressWarnings("unchecked")
	public <X> EntityNode<X> asEntityNode()
	{
		return (EntityNode<X>) this; // TODO add strong typing on every kind of node.
	}
}
