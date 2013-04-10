// Copyright (c) 2006 - 2010, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.taxonomy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.tbox.TBox;
import org.mindswap.pellet.tbox.impl.Unfolding;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATerm;
import aterm.ATermAppl;

import com.clarkparsia.pellet.utils.CollectionUtils;

/**
 * 
 * @author Evren Sirin
 */
public abstract class AbstractDefinitionOrder implements DefinitionOrder {
	protected KnowledgeBase kb;
	protected Comparator<ATerm>	comparator;

	private Set<ATermAppl> cyclicConcepts;
	private List<ATermAppl> definitionOrder;
	
	public AbstractDefinitionOrder(KnowledgeBase kb, Comparator<ATerm>	comparator) {
		this.kb = kb;
		this.comparator = comparator;
		
		cyclicConcepts = CollectionUtils.makeIdentitySet();		
		definitionOrder = new ArrayList<ATermAppl>( kb.getClasses().size() + 2 );				

		initialize();
		
		processDefinitions();
		
		cyclicConcepts = computeCycles();
		
		definitionOrder = computeDefinitionOrder();
	}

	protected abstract void initialize();
	
	protected abstract Set<ATermAppl> computeCycles();
	
	protected abstract List<ATermAppl> computeDefinitionOrder();
	
	protected void processDefinitions() {
		boolean hasInverses = kb.getExpressivity().hasInverse();
		TBox tbox = kb.getTBox();
		for( ATermAppl c : kb.getClasses() ) {
			Iterator<Unfolding> unfoldingList = tbox.unfold( c );		
			while( unfoldingList.hasNext() ) {
				Unfolding unf = unfoldingList.next();
				Set<ATermAppl> usedByC = ATermUtils.findPrimitives( unf.getResult(), !hasInverses, true );
				for( ATermAppl used : usedByC ) {
					if( !kb.getClasses().contains( used ) )
						continue;
								
					addUses( c, used );
				}
			}
		}
	}
	
	protected abstract void addUses(ATermAppl c, ATermAppl usedByC);
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isCyclic(ATermAppl concept) {
		return cyclicConcepts.contains( concept );
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<ATermAppl> iterator() {
		return definitionOrder.iterator();
	}
}
