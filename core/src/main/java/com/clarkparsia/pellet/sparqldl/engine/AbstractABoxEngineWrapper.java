// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.utils.ATermUtils;

import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.model.QueryAtom;
import com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory;
import com.clarkparsia.pellet.sparqldl.model.QueryImpl;
import com.clarkparsia.pellet.sparqldl.model.QueryPredicate;
import com.clarkparsia.pellet.sparqldl.model.QueryResult;
import com.clarkparsia.pellet.sparqldl.model.QueryResultImpl;
import com.clarkparsia.pellet.sparqldl.model.ResultBinding;
import com.clarkparsia.pellet.sparqldl.model.ResultBindingImpl;
import com.clarkparsia.pellet.sparqldl.model.Query.VarType;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Abstract class for all purely ABox engines.
 * </p>
 * <p>
 * Description: All variable name spaces are disjoint.
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
public abstract class AbstractABoxEngineWrapper implements QueryExec {
	public static final Logger log = Logger.getLogger(QueryEngine.class.getName());

	public static final QueryExec distCombinedQueryExec = new CombinedQueryEngine();

	protected Query schemaQuery;

	protected Query aboxQuery;

	/**
	 * {@inheritDoc}
	 */
	public QueryResult exec(Query query) {
		if (log.isLoggable( Level.FINE )) {
			log.fine("Executing query " + query.getAtoms());
		}

		partitionQuery(query);

		QueryResult newResult;

		boolean shouldHaveBinding;
		final QueryResult result;

		if (schemaQuery.getAtoms().isEmpty()) {
			shouldHaveBinding = false;
			result = new QueryResultImpl(query);
			result.add(new ResultBindingImpl());
		} else {
			if (log.isLoggable( Level.FINE )) {
				log.fine("Executing TBox query: " + schemaQuery);
			}
			result = distCombinedQueryExec.exec(schemaQuery);

			shouldHaveBinding = org.mindswap.pellet.utils.SetUtils.intersects(
					query.getDistVarsForType(VarType.CLASS), query
							.getResultVars())
					|| org.mindswap.pellet.utils.SetUtils.intersects(query
							.getDistVarsForType(VarType.PROPERTY), query
							.getResultVars());
		}
		if (shouldHaveBinding && result.isEmpty()) {
			return result;
		}

		if (log.isLoggable( Level.FINE )) {
			log.fine("Partial binding after schema query : " + result);
		}

		if (aboxQuery.getAtoms().size() > 0) {
			newResult = new QueryResultImpl(query);
			for (ResultBinding binding : result) {
				final Query query2 = aboxQuery.apply(binding);

				if (log.isLoggable( Level.FINE )) {
					log.fine("Executing ABox query: " + query2);
				}
				final QueryResult aboxResult = execABoxQuery(query2);

				for (ResultBinding newBinding : aboxResult) {
					for (final ATermAppl var : binding.getAllVariables()) {
						newBinding.setValue(var, binding.getValue(var));
					}

					newResult.add(newBinding);
				}
			}
		} else {
			newResult = result;
			if (log.isLoggable( Level.FINER )) {
				log.finer("ABox query empty ... returning.");
			}
		}
		return newResult;
	}

	private final void partitionQuery(final Query query) {

		schemaQuery = new QueryImpl(query);
		aboxQuery = new QueryImpl(query);

		for (final QueryAtom atom : query.getAtoms()) {
			switch (atom.getPredicate()) {
			case Type:
			case PropertyValue:
//			case SameAs:
//			case DifferentFrom:
				aboxQuery.add(atom);
				break;
			default:
				;
			}
		}

		final List<QueryAtom> atoms = new ArrayList<QueryAtom>(query.getAtoms());
		atoms.removeAll(aboxQuery.getAtoms());

		for (final QueryAtom atom : atoms) {
			schemaQuery.add(atom);
		}

		for (final VarType t : VarType.values()) {
			for (final ATermAppl a : query.getDistVarsForType(t)) {
				if (aboxQuery.getVars().contains(a)) {
					aboxQuery.addDistVar(a, t);
				}
				if (schemaQuery.getVars().contains(a)) {
					schemaQuery.addDistVar(a, t);
				}
			}
		}

		for (final ATermAppl a : query.getResultVars()) {
			if (aboxQuery.getVars().contains(a)) {
				aboxQuery.addResultVar(a);
			}
			if (schemaQuery.getVars().contains(a)) {
				schemaQuery.addResultVar(a);
			}
		}

		for (final ATermAppl v : aboxQuery.getDistVarsForType(VarType.CLASS)) {
			if (!schemaQuery.getVars().contains(v)) {
				schemaQuery.add(QueryAtomFactory.SubClassOfAtom(v,
						ATermUtils.TOP));
			}
		}

		for (final ATermAppl v : aboxQuery.getDistVarsForType(VarType.PROPERTY)) {
			if (!schemaQuery.getVars().contains(v)) {
				schemaQuery.add(QueryAtomFactory.SubPropertyOfAtom(v, v));
			}
		}

	}

	protected abstract QueryResult execABoxQuery(final Query q);
}

class BindingIterator implements Iterator<ResultBinding> {
	private final List<List<ATermAppl>> varB = new ArrayList<List<ATermAppl>>();

	private final List<ATermAppl> vars = new ArrayList<ATermAppl>();

	private int[] indices;

	private boolean more = true;

	public BindingIterator(final Map<ATermAppl, Set<ATermAppl>> bindings) {
		vars.addAll(bindings.keySet());

		for (final ATermAppl var : vars) {
			final Set<ATermAppl> values = bindings.get(var);
			if (values.isEmpty()) {
				more = false;
				break;
			} else {
				varB.add(new ArrayList<ATermAppl>(values));
			}
		}

		indices = new int[vars.size()];
	}

	private boolean incIndex(int index) {
		if (indices[index] + 1 < varB.get(index).size()) {
			indices[index]++;
		} else {
			if (index == indices.length - 1) {
				return false;
			} else {
				indices[index] = 0;
				return incIndex(index + 1);
			}
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasNext() {
		return more;
	}

	/**
	 * {@inheritDoc}
	 */
	public ResultBinding next() {
		if (!more)
			return null;

		final ResultBinding next = new ResultBindingImpl();

		for (int i = 0; i < indices.length; i++) {
			next.setValue(vars.get(i), varB.get(i).get(indices[i]));
		}

		more = incIndex(0);

		return next;
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove() {
		throw new UnsupportedOperationException(
				"Removal from this iterator is not supported.");
	}
}

class LiteralIterator implements Iterator<ResultBinding> {
	private int[] indices;

	private ResultBinding binding;

	private Set<ATermAppl> litVars;

	private List<List<ATermAppl>> litVarBindings = new ArrayList<List<ATermAppl>>();

	private boolean more = true;

	public LiteralIterator(final Query q, final ResultBinding binding) {
		final KnowledgeBase kb = q.getKB();
		this.binding = binding;
		this.litVars = q.getDistVarsForType(VarType.LITERAL);

		indices = new int[litVars.size()];
		int index = 0;
		for (final ATermAppl litVar : litVars) {
			// final Datatype dtype = ;// q.getDatatype(litVar); TODO after
			// recognizing Datatypes and adjusting Query model supply the
			// corresponding literal.

			final List<ATermAppl> foundLiterals = new ArrayList<ATermAppl>();
			boolean first = true;

			for (final QueryAtom atom : q.findAtoms(
					QueryPredicate.PropertyValue, null, null, litVar)) {

				ATermAppl subject = atom.getArguments().get(0);
				final ATermAppl predicate = atom.getArguments().get(1);

				if (ATermUtils.isVar(subject))
					subject = binding.getValue(subject);

				litVarBindings.add(index, new ArrayList<ATermAppl>());

				final List<ATermAppl> act = kb.getDataPropertyValues(predicate,
						subject); // dtype);

				if (first) {
					foundLiterals.addAll(act);
				} else {
					foundLiterals.retainAll(act);
					first = false;
				}
			}

			if (foundLiterals.size() > 0) {
				litVarBindings.get(index++).addAll(foundLiterals);
			} else {
				more = false;
			}
		}
	}

	private boolean incIndex(int index) {
		if (indices[index] + 1 < litVarBindings.get(index).size()) {
			indices[index]++;
		} else {
			if (index == indices.length - 1) {
				return false;
			} else {
				indices[index] = 0;
				return incIndex(index + 1);
			}
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove() {
		throw new UnsupportedOperationException(
				"Removal from this iterator is not supported.");
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasNext() {
		return more;
	}

	/**
	 * {@inheritDoc}
	 */
	public ResultBinding next() {
		if (!more)
			return null;

		final ResultBinding next = binding.duplicate();

		int index = 0;
		for (final ATermAppl o1 : litVars) {
			ATermAppl o2 = litVarBindings.get(index).get(indices[index++]);
			next.setValue(o1, o2);
		}

		more = incIndex(0);

		return next;
	}
}