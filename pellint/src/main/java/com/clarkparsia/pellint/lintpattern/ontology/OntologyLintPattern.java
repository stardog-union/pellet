// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.lintpattern.ontology;

import java.util.List;

import org.semanticweb.owlapi.model.OWLOntology;

import com.clarkparsia.pellint.lintpattern.LintPattern;
import com.clarkparsia.pellint.model.Lint;

/**
 * <p>
 * Title: Ontology-based Lint Pattern Interface
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
public interface OntologyLintPattern extends LintPattern {

	/**
	 * Match an OWLOntology and returns a list of {@link com.clarkparsia.pellint.model.Lint}
	 * for the OWLOntology.
	 * 
	 * @return A possibly empty list of {@link com.clarkparsia.pellint.model.Lint} for the OWLOntology. Never returns <code>null</code>.
	 * 
	 * @see com.clarkparsia.pellint.model.Lint
	 */
	List<Lint> match(OWLOntology ontology);
}
