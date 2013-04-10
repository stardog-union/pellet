// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.taxonomy;

import java.util.Map;
import java.util.Set;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.PartialOrderBuilder;
import org.mindswap.pellet.utils.PartialOrderComparator;
import org.mindswap.pellet.utils.progress.ProgressMonitor;

import aterm.ATermAppl;

public class POTaxonomyBuilder implements TaxonomyBuilder {

	private PartialOrderBuilder<ATermAppl>	builder;
	private KnowledgeBase					kb;
	private Taxonomy<ATermAppl>				tax;

	public POTaxonomyBuilder(KnowledgeBase kb) {
		this( kb, new SubsumptionComparator( kb ) );
	}

	public POTaxonomyBuilder(KnowledgeBase kb, PartialOrderComparator<ATermAppl> comparator) {
		this.kb = kb;
		this.tax = new Taxonomy<ATermAppl>( null, ATermUtils.TOP, ATermUtils.BOTTOM );
		this.builder = new PartialOrderBuilder<ATermAppl>( tax, comparator );
	}

	public boolean classify() {
		builder.addAll( kb.getClasses() );
		
		return true;
	}

	public void classify(ATermAppl c) {
		builder.add( c );
	}

	public boolean realize() {
		throw new UnsupportedOperationException();
		/*
		 * CDOptimizedTaxonomyBuilder b = new CDOptimizedTaxonomyBuilder();
		 * b.setKB( kb ); b.classify(); return b.realize();
		 */
	}

	public void setKB(KnowledgeBase kb) {
		this.kb = kb;
	}
	
	public PartialOrderComparator<ATermAppl> getComparator() {
		return builder.getComparator();
	}

	public void setComparator(PartialOrderComparator<ATermAppl> comparator) {
		builder.setComparator( comparator );
	}

	public void setProgressMonitor(ProgressMonitor monitor) {
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<ATermAppl, Set<ATermAppl>> getToldDisjoints() {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public Taxonomy<ATermAppl> getToldTaxonomy() {
		throw new UnsupportedOperationException();
	}
	
	public Taxonomy<ATermAppl> getTaxonomy() {
		return tax;
	}
} 
