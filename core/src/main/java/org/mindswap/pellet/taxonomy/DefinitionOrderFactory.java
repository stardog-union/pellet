// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.taxonomy;

import static org.mindswap.pellet.PelletOptions.OrderedClassification.DISABLED;
import static org.mindswap.pellet.PelletOptions.OrderedClassification.ENABLED_LEGACY_ORDERING;

import java.util.Comparator;
import openllet.aterm.ATerm;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.utils.Comparators;

/**
 * Creates a definition _order based on the configuration options defined in {@link PelletOptions}.
 *
 * @author Evren Sirin
 */
public class DefinitionOrderFactory
{
	public static DefinitionOrder createDefinitionOrder(final KnowledgeBase kb)
	{
		final Comparator<ATerm> comparator = PelletOptions.ORDERED_CLASSIFICATION != DISABLED ? Comparators.termComparator : null;

		return PelletOptions.ORDERED_CLASSIFICATION == ENABLED_LEGACY_ORDERING ? new TaxonomyBasedDefinitionOrder(kb, comparator) : new JGraphBasedDefinitionOrder(kb, comparator);
	}
}
