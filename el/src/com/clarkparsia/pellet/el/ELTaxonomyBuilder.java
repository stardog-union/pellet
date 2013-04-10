// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.el;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.taxonomy.TaxonomyNode;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: Builds a fully classified taxonomy from existing classification
 * results.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
class ELTaxonomyBuilder {
	private Taxonomy<ATermAppl>	taxonomy;

	public Taxonomy<ATermAppl> build(Map<ATermAppl, ConceptInfo> concepts) {
		taxonomy = new Taxonomy<ATermAppl>( null, ATermUtils.TOP, ATermUtils.BOTTOM );

		for( ConceptInfo ci : concepts.get( ATermUtils.TOP ).getSuperClasses() ) {
			ATermAppl eq = ci.getConcept();
			if( ATermUtils.isPrimitive( eq ) ) {
				taxonomy.addEquivalentNode( eq, taxonomy.getTop() );
			}
		}

		final ConceptInfo BOTTOM = concepts.get( ATermUtils.BOTTOM );
		for( ConceptInfo ci : concepts.values() ) {
			ATermAppl c = ci.getConcept();
			if( !ATermUtils.isPrimitive( c ) ) continue; 
			
			if( ci.getSuperClasses().contains(BOTTOM) ) {
				taxonomy.addEquivalentNode( c, taxonomy.getBottom() );
			} else {
				classify( ci );
			}
		}

		return taxonomy;
	}

	private TaxonomyNode<ATermAppl> classify(ConceptInfo ci) {
		ATermAppl c = ci.getConcept();
		TaxonomyNode<ATermAppl> node = taxonomy.getNode( c );

		if( node == null ) {
			Set<ConceptInfo> equivalents = new HashSet<ConceptInfo>();
			Set<TaxonomyNode<ATermAppl>> subsumers = new HashSet<TaxonomyNode<ATermAppl>>();

			for( ConceptInfo subsumer : ci.getSuperClasses() ) {
				if( !ATermUtils.isPrimitive( subsumer.getConcept() ) ) {
					continue;
				}

				if( ci.equals( subsumer ) ) {
					continue;
				}
				else if( subsumer.hasSuperClass( ci ) ) {
					equivalents.add( subsumer );
				}
				else {
					TaxonomyNode<ATermAppl> supNode = classify( subsumer );
					if( supNode != null )
						subsumers.add( supNode );
				}
			}

			node = add( ci, subsumers );

			for( ConceptInfo eqInfo : equivalents ) {
				ATermAppl eq = eqInfo.getConcept();
				taxonomy.addEquivalentNode( eq, node );
			}
		}

		return node;
	}

	private TaxonomyNode<ATermAppl> add(ConceptInfo ci, Set<TaxonomyNode<ATermAppl>> subsumers) {
		ATermAppl c = ci.getConcept();

		Set<TaxonomyNode<ATermAppl>> parents = new HashSet<TaxonomyNode<ATermAppl>>( subsumers );
		Set<ATermAppl> supers = new HashSet<ATermAppl>();
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
