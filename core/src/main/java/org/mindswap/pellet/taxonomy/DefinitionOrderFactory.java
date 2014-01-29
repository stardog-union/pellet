// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.taxonomy;

import java.util.Comparator;
import static org.mindswap.pellet.PelletOptions.OrderedClassification.*;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.utils.Comparators;

import aterm.ATerm;

/**
 * Creates a definition order based on the configuration options defined in {@link PelletOptions}.
 * 
 * @author Evren Sirin
 */
public class DefinitionOrderFactory {
	public static DefinitionOrder createDefinitionOrder(KnowledgeBase kb) {
		Comparator<ATerm> comparator = PelletOptions.ORDERED_CLASSIFICATION != DISABLED
			? Comparators.termComparator
			: null;
		
		return PelletOptions.ORDERED_CLASSIFICATION == ENABLED_LEGACY_ORDERING
			? new TaxonomyBasedDefinitionOrder( kb, comparator )
			: new JGraphBasedDefinitionOrder( kb, comparator );
	}
}
