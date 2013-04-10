// Copyright (c) 2006 - 2010, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.taxonomy;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATerm;
import aterm.ATermAppl;

import com.clarkparsia.pellet.utils.CollectionUtils;

/**
 * 
 * @author Evren Sirin
 */
public class TaxonomyBasedDefinitionOrder extends AbstractDefinitionOrder {
	private Taxonomy<ATermAppl> definitionOrderTaxonomy;

	public TaxonomyBasedDefinitionOrder(KnowledgeBase kb, Comparator<ATerm> comparator) {
		super( kb, comparator );
	}
	
	protected void initialize() {
		definitionOrderTaxonomy = new Taxonomy<ATermAppl>( kb.getClasses(),
				ATermUtils.TOP, ATermUtils.BOTTOM );
	}

	@Override
	protected void addUses(ATermAppl c, ATermAppl d) {		
		if( definitionOrderTaxonomy.isEquivalent( c, d ).isTrue() )
			return;
	
		TaxonomyNode<ATermAppl> cNode = definitionOrderTaxonomy.getNode( c );
		TaxonomyNode<ATermAppl> dNode = definitionOrderTaxonomy.getNode( d );
		if( cNode == null )
			throw new InternalReasonerException( c + " is not in the definition order" );
		else if( cNode.equals( definitionOrderTaxonomy.getTop() ) )
			definitionOrderTaxonomy.merge( cNode, dNode );
		else {
			definitionOrderTaxonomy.addSuper( c, d );
			definitionOrderTaxonomy.removeCycles( cNode );
		}
	}

	protected Set<ATermAppl> computeCycles() {
		Set<ATermAppl> cyclicConcepts = CollectionUtils.makeIdentitySet();
		for( TaxonomyNode<ATermAppl> node : definitionOrderTaxonomy.getNodes() ) {
			Set<ATermAppl> names = node.getEquivalents();
			if( names.size() > 1 )
				cyclicConcepts.addAll( names );
		}
		
		return cyclicConcepts;
	}
	
	protected List<ATermAppl> computeDefinitionOrder() {
		definitionOrderTaxonomy.assertValid();

		return definitionOrderTaxonomy.topologocialSort( true, comparator );
	}
}
