// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import static java.lang.String.format;

import aterm.ATermAppl;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import com.clarkparsia.pellet.datatypes.exceptions.UnrecognizedDatatypeException;
import com.clarkparsia.pellet.rules.model.AtomDConstant;
import com.clarkparsia.pellet.rules.model.AtomDObject;
import com.clarkparsia.pellet.rules.model.AtomDVariable;
import com.clarkparsia.pellet.rules.model.AtomIConstant;
import com.clarkparsia.pellet.rules.model.AtomIObject;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.AtomObjectVisitor;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.DefaultAtomObjectVisitor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.exceptions.InternalReasonerException;

/**
 * <p>
 * Title: Variable Binding
 * </p>
 * <p>
 * Description: Keeps variable bindings. Data and Individual variables are kept in seperate name spaces.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Ron Alford
 */
public class VariableBinding
{

	private static final Logger log;

	static
	{
		log = Logger.getLogger(VariableBinding.class.getCanonicalName());
	}

	/**
	 * Collects data values of a objects it visits
	 */
	private class DataValueCollector extends DefaultAtomObjectVisitor
	{
		Literal value = null;

		public Literal getValue()
		{
			return value;
		}

		@Override
		public void visit(final AtomDConstant constant)
		{
			ATermAppl canonical;
			final ATermAppl literal = constant.getValue();
			try
			{
				canonical = abox.getKB().getDatatypeReasoner().getCanonicalRepresentation(literal);
			}
			catch (final InvalidLiteralException e)
			{
				final String msg = format("Invalid literal (%s) in SWRL data constant: %s", literal, e.getMessage());
				if (PelletOptions.INVALID_LITERAL_AS_INCONSISTENCY)
					canonical = literal;
				else
				{
					log.severe(msg);
					throw new InternalReasonerException(msg, e);
				}
			}
			catch (final UnrecognizedDatatypeException e)
			{
				final String msg = format("Unrecognized datatype in literal appearing (%s) in SWRL data constant: %s", literal, e.getMessage());
				log.severe(msg);
				throw new InternalReasonerException(msg, e);
			}

			abox.copyOnWrite();
			value = abox.getLiteral(canonical);
			if (value == null)
				value = abox.addLiteral(canonical);
		}

		@Override
		public void visit(final AtomDVariable variable)
		{
			value = dataVars.get(variable);
		}

	}

	/**
	 * Collects individual values of a objects it visits
	 */
	private class IndividualValueCollector extends DefaultAtomObjectVisitor
	{

		Individual value = null;

		public Individual getValue()
		{
			return value;
		}

		@Override
		public void visit(final AtomIConstant constant)
		{
			abox.copyOnWrite();
			value = abox.getIndividual(constant.getValue());
		}

		@Override
		public void visit(final AtomIVariable variable)
		{
			value = instanceVars.get(variable);
		}

	}

	/**
	 * Sets the value of a variable to the individual or node as appropriate.
	 */
	private class ValueSettingVisitor implements AtomObjectVisitor
	{

		Literal data;
		Individual individual;

		public ValueSettingVisitor(final Individual individual, final Literal data)
		{
			this.data = data;
			this.individual = individual;
		}

		public Literal getData()
		{
			return data;
		}

		public Individual getIndividual()
		{
			return individual;
		}

		@Override
		public void visit(final AtomDConstant constant)
		{
			data = null;
		}

		@Override
		public void visit(final AtomDVariable var)
		{
			if (data != null)
				data = dataVars.put(var, data);
		}

		@Override
		public void visit(final AtomIConstant constant)
		{
			individual = null;
		}

		@Override
		public void visit(final AtomIVariable var)
		{
			if (individual != null)
				individual = instanceVars.put(var, individual);
		}

	}

	private final ABox abox;

	private final Map<AtomDVariable, Literal> dataVars;

	private final Map<AtomIVariable, Individual> instanceVars;

	public VariableBinding(final ABox abox)
	{
		this.abox = abox;
		dataVars = new HashMap<>();
		instanceVars = new HashMap<>();
	}

	/**
	 * Shallow copies the binding maps.
	 */
	public VariableBinding(final VariableBinding binding)
	{
		abox = binding.abox;
		dataVars = new HashMap<>(binding.dataVars);
		instanceVars = new HashMap<>(binding.instanceVars);
	}

	public boolean containsKey(final AtomDVariable key)
	{
		return dataVars.containsKey(key);
	}

	public boolean containsKey(final AtomIVariable key)
	{
		return instanceVars.containsKey(key);
	}

	public Set<Map.Entry<AtomDVariable, Literal>> dataEntrySet()
	{
		return dataVars.entrySet();
	}

	public Set<Map.Entry<? extends AtomVariable, ? extends Node>> entrySet()
	{
		final Set<Map.Entry<? extends AtomVariable, ? extends Node>> entries = new HashSet<>();
		entries.addAll(dataVars.entrySet());
		entries.addAll(instanceVars.entrySet());
		return entries;
	}

	@Override
	public boolean equals(final Object other)
	{
		if (other instanceof VariableBinding)
		{
			final VariableBinding otherBinding = (VariableBinding) other;
			if (dataVars.equals(otherBinding.dataVars) && instanceVars.equals(otherBinding.instanceVars))
				return true;
		}
		return false;
	}

	/**
	 * If the key is a variable, return the node associated with it in the map. If the key is a constant, return the corresponding node from the abox.
	 */
	public Literal get(final AtomDObject key)
	{
		final DataValueCollector collector = new DataValueCollector();
		key.accept(collector);
		return collector.getValue();
	}

	/**
	 * If the key is a variable, return the individual associated with it in the map. If the key is a constant, return the corresponding individual from the
	 * abox.
	 */
	public Individual get(final AtomIObject key)
	{
		final IndividualValueCollector collector = new IndividualValueCollector();
		key.accept(collector);
		return collector.getValue();
	}

	public ABox getABox()
	{
		return abox;
	}

	@Override
	public int hashCode()
	{
		return dataVars.hashCode() + instanceVars.hashCode();
	}

	/**
	 * If the key is a data variable, set the value. Otherwise, ignore it.
	 */
	public Literal set(final AtomDObject key, final Literal value)
	{
		final ValueSettingVisitor visitor = new ValueSettingVisitor(null, value);
		key.accept(visitor);
		return visitor.getData();
	}

	public Literal set(final AtomDVariable key, final ATermAppl value)
	{
		final AtomDConstant constant = new AtomDConstant(value);
		final DataValueCollector collector = new DataValueCollector();
		constant.accept(collector);
		return set(key, collector.getValue());
	}

	/**
	 * If the key is an instance variable, set the value. Otherwise, ignore it.
	 */
	public Individual set(final AtomIObject key, final Individual value)
	{
		final ValueSettingVisitor visitor = new ValueSettingVisitor(value, null);
		key.accept(visitor);
		return visitor.getIndividual();
	}

	public Individual set(final AtomIVariable key, final ATermAppl value)
	{
		final AtomIConstant constant = new AtomIConstant(value);
		final IndividualValueCollector collector = new IndividualValueCollector();
		constant.accept(collector);
		return set(key, collector.getValue());
	}

	@Override
	public String toString()
	{
		final StringBuffer buffer = new StringBuffer("{");
		buffer.append(instanceVars);
		buffer.append(", ");
		buffer.append(dataVars);
		buffer.append("}");
		return buffer.toString();
	}

}
