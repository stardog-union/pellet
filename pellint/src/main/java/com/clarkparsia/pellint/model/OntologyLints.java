// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.clarkparsia.pellint.format.LintFormat;
import com.clarkparsia.pellint.lintpattern.LintPattern;
import com.clarkparsia.pellint.util.CollectionUtil;

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
public class OntologyLints {
	private OWLOntology m_Ontology;
	private OWLOntology m_RootOntology;
	private Map<LintPattern, List<Lint>> m_Lints;
	
	public OntologyLints(OWLOntology ontology) {
		m_Ontology = ontology;
		m_Lints = new TreeMap<LintPattern, List<Lint>>(new Comparator<LintPattern>() {
			public int compare(LintPattern p0, LintPattern p1) {
				return p0.getName().compareTo(p1.getName());
			}
		});
	}
	
	public OWLOntology getOntology() {
		return m_Ontology;
	}
	
	public void setRootOntology(OWLOntology rootOntology) {
		m_RootOntology = rootOntology;
	}
	
	public OWLOntology getRootOntology() {
		return m_RootOntology;
	}
	
	public void addLint(LintPattern pattern, Lint newLint) {
		List<Lint> lints = m_Lints.get(pattern);
		if (lints == null) {
			lints = CollectionUtil.makeList();
			m_Lints.put(pattern, lints);
		}
		lints.add(newLint);
	}
	
	public void addLints(LintPattern pattern, List<Lint> newLints) {
		List<Lint> lints = m_Lints.get(pattern);
		if (lints == null) {
			lints = CollectionUtil.makeList();
			m_Lints.put(pattern, lints);
		}
		lints.addAll(newLints);
	}
	
	public void sort(Comparator<? super Lint> comparator) {
		for (List<Lint> lints : m_Lints.values()) {
			Collections.sort(lints, comparator);
		}
	}
	
	public boolean isEmpty() {
		return m_Lints.isEmpty();
	}
	
	public int size() {
		int total = 0;
		for (List<Lint> lints : m_Lints.values()) {
			total += lints.size();
		}
		return total;
	}

	/**
	 * Fix all reparable lints.
	 * @param manager
	 * @return Returns the set of unfixable lints.
	 * @throws OWLOntologyChangeException
	 */
	public Set<Lint> applyFix(OWLOntologyManager manager) throws OWLOntologyChangeException {
		Set<Lint> unfixable = new HashSet<Lint>();
		for (Entry<LintPattern, List<Lint>> entry : m_Lints.entrySet()) {
			if (entry.getKey().isFixable()) {
				for (Lint lint : entry.getValue()) {
					if (!lint.applyFix(manager)) {
						unfixable.add( lint );
					}
				}
			} else {
				unfixable.addAll( entry.getValue() );
			}
		}
		return unfixable;
	}
	
	public String toString() {
		final String ONTOLOGY_NAME;
		if (m_RootOntology == null) {
			ONTOLOGY_NAME = m_Ontology.getOntologyID().toString();
		} else {
			ONTOLOGY_NAME = m_RootOntology.getOntologyID().toString() + " and its import closure";
		}
		
		if (m_Lints.isEmpty()) {
			return "\nNo OWL lints found for ontology " + ONTOLOGY_NAME + ".";
		}
		
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("\n==================================================================\n");
		strBuilder.append("OWL Lints found for ontology ").append(ONTOLOGY_NAME).append(":");
		for (LintPattern pattern : m_Lints.keySet()) {
			strBuilder.append("\n[")
				.append(pattern.getName())
				.append(": ")
				.append(pattern.getDescription())
				.append("]\n");
			
			LintFormat lintFormat = pattern.getDefaultLintFormat();
			for (Lint lint : m_Lints.get(pattern)) {
				strBuilder.append(lintFormat.format(lint));
			}
			
			strBuilder.append('\n');
		}
		
		return strBuilder.toString();
	}
}
