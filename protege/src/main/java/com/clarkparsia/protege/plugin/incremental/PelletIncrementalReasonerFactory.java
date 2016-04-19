// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.protege.plugin.incremental;

import com.clarkparsia.modularity.PelletIncremantalReasonerFactory;
import org.protege.editor.owl.model.inference.AbstractProtegeOWLReasonerInfo;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * @author Evren Sirin
 */
public class PelletIncrementalReasonerFactory extends AbstractProtegeOWLReasonerInfo
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public OWLReasonerFactory getReasonerFactory()
	{
		return PelletIncremantalReasonerFactory.getInstance();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BufferingMode getRecommendedBuffering()
	{
		return BufferingMode.BUFFERING;
	}
}
