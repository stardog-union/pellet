// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.model;

import com.clarkparsia.pellint.format.LintFormat;
import com.clarkparsia.pellint.lintpattern.LintPattern;
import com.clarkparsia.pellint.util.CollectionUtil;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * <p>
 * Title: Ontology Lints
 * </p>
 * <p>
 * Description: Collects all lints found against some ontology, categorized by lint patterns.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Harris Lin
 */
public class OntologyLints
{
	private final OWLOntology _ontology;
	private OWLOntology _rootOntology;
	private final Map<LintPattern, List<Lint>> _lints;

	public OntologyLints(final OWLOntology ontology)
	{
		_ontology = ontology;
		_lints = new TreeMap<LintPattern, List<Lint>>((p0, p1) -> p0.getName().compareTo(p1.getName()));
	}

	public OWLOntology getOntology()
	{
		return _ontology;
	}

	public void setRootOntology(final OWLOntology rootOntology)
	{
		_rootOntology = rootOntology;
	}

	public OWLOntology getRootOntology()
	{
		return _rootOntology;
	}

	public void addLint(final LintPattern pattern, final Lint newLint)
	{
		List<Lint> lints = _lints.get(pattern);
		if (lints == null)
		{
			lints = CollectionUtil.makeList();
			_lints.put(pattern, lints);
		}
		lints.add(newLint);
	}

	public void addLints(final LintPattern pattern, final List<Lint> newLints)
	{
		List<Lint> lints = _lints.get(pattern);
		if (lints == null)
		{
			lints = CollectionUtil.makeList();
			_lints.put(pattern, lints);
		}
		lints.addAll(newLints);
	}

	public void sort(final Comparator<? super Lint> comparator)
	{
		for (final List<Lint> lints : _lints.values())
			Collections.sort(lints, comparator);
	}

	public boolean isEmpty()
	{
		return _lints.isEmpty();
	}

	public int size()
	{
		int total = 0;
		for (final List<Lint> lints : _lints.values())
			total += lints.size();
		return total;
	}

	/**
	 * Fix all reparable lints.
	 * 
	 * @param manager
	 * @return Returns the set of unfixable lints.
	 * @throws OWLOntologyChangeException
	 */
	public Set<Lint> applyFix(final OWLOntologyManager manager) throws OWLOntologyChangeException
	{
		final Set<Lint> unfixable = new HashSet<>();
		for (final Entry<LintPattern, List<Lint>> entry : _lints.entrySet())
			if (entry.getKey().isFixable())
			{
				for (final Lint lint : entry.getValue())
					if (!lint.applyFix(manager))
						unfixable.add(lint);
			}
			else
				unfixable.addAll(entry.getValue());
		return unfixable;
	}

	@Override
	public String toString()
	{
		final String ONTOLOGY_NAME;
		if (_rootOntology == null)
			ONTOLOGY_NAME = _ontology.getOntologyID().toString();
		else
			ONTOLOGY_NAME = _rootOntology.getOntologyID().toString() + " and its import closure";

		if (_lints.isEmpty())
			return "\nNo OWL lints found for ontology " + ONTOLOGY_NAME + ".";

		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("\n==================================================================\n");
		strBuilder.append("OWL Lints found for ontology ").append(ONTOLOGY_NAME).append(":");
		for (final LintPattern pattern : _lints.keySet())
		{
			strBuilder.append("\n[").append(pattern.getName()).append(": ").append(pattern.getDescription()).append("]\n");

			final LintFormat lintFormat = pattern.getDefaultLintFormat();
			for (final Lint lint : _lints.get(pattern))
				strBuilder.append(lintFormat.format(lint));

			strBuilder.append('\n');
		}

		return strBuilder.toString();
	}
}
