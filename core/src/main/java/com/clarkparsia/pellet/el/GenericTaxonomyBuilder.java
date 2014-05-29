// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.el;

import java.util.Collections;
import java.util.Set;
import java.util.Map.Entry;

import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.taxonomy.TaxonomyNode;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.MultiValueMap;

import aterm.ATermAppl;

import com.clarkparsia.pellet.utils.CollectionUtils;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
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
public class GenericTaxonomyBuilder {
	private Taxonomy<ATermAppl>					taxonomy;

	private MultiValueMap<ATermAppl, ATermAppl>	subsumers;

	public Taxonomy<ATermAppl> build(MultiValueMap<ATermAppl, ATermAppl> subsumers) {
		this.subsumers = subsumers;
		taxonomy = new Taxonomy<ATermAppl>( null, ATermUtils.TOP, ATermUtils.BOTTOM );

		for( ATermAppl subsumer : subsumers.get( ATermUtils.TOP ) ) {
			if( ATermUtils.isPrimitive( subsumer ) )
				taxonomy.addEquivalentNode( subsumer, taxonomy.getTop() );
		}

		for( Entry<ATermAppl, Set<ATermAppl>> entry : subsumers.entrySet() ) {
			ATermAppl c = entry.getKey();
			if( ATermUtils.isPrimitive( c ) ) {
				if( entry.getValue().contains( ATermUtils.BOTTOM ) ) {
					taxonomy.addEquivalentNode( c, taxonomy.getBottom() );
				}
				else {
					add( c );
				}
			}
		}

		return taxonomy;
	}

	private TaxonomyNode<ATermAppl> add(ATermAppl c) {
		TaxonomyNode<ATermAppl> node = taxonomy.getNode( c );

		if( node == null ) {
			Set<ATermAppl> equivalents = CollectionUtils.makeSet();
			Set<TaxonomyNode<ATermAppl>> subsumerNodes = CollectionUtils.makeSet();

			for( ATermAppl subsumer : subsumers.get( c ) ) {
				if( c.equals( subsumer ) || !ATermUtils.isPrimitive( subsumer )  )
					continue;

				if( subsumers.get( subsumer ).contains( c ) ) {
					equivalents.add( subsumer );
				}
				else {
					TaxonomyNode<ATermAppl> supNode = add( subsumer );
					subsumerNodes.add( supNode );					
				}
			}

			node = add( c, subsumerNodes );

			for( ATermAppl eq : equivalents ) {
				taxonomy.addEquivalentNode( eq, node );
			}
		}

		return node;
	}

	private TaxonomyNode<ATermAppl> add(ATermAppl c, Set<TaxonomyNode<ATermAppl>> subsumers) {
		Set<TaxonomyNode<ATermAppl>> parents = CollectionUtils.makeSet( subsumers );
		Set<ATermAppl> supers = CollectionUtils.makeSet();
		Set<ATermAppl> subs = Collections.singleton( ATermUtils.BOTTOM );

		for( TaxonomyNode<ATermAppl> subsumer : subsumers ) {
			parents.removeAll( subsumer.getSupers() );
		}

		for( TaxonomyNode<ATermAppl> parent : parents ) {
			supers.add( parent.getName() );
			parent.removeSub( taxonomy.getBottom() );
		}

		return taxonomy.addNode( Collections.singleton( c ), supers, subs, false );
	}
}
