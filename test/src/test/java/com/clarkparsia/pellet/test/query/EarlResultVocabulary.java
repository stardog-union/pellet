// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.query;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * <p>
 * Title: Engine for processing DAWG test manifests
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Petr Kremen
 */
public class EarlResultVocabulary {

	public static final String doapBase = "http://usefulinc.com/ns/doap#";

	public static final String doapBaseNs = "doap";

	// DOAP
	// general classes
	public static final Resource Project = ResourceFactory
			.createResource(doapBase + "Project");

	public static final Resource Version = ResourceFactory
			.createResource(doapBase + "Version");

	// general properties
	public static final Property doapName = ResourceFactory
			.createProperty(doapBase + "name");

	public static final Property created = ResourceFactory
			.createProperty(doapBase + "created");

	public static final Property release = ResourceFactory
			.createProperty(doapBase + "release");

	public static final Property revision = ResourceFactory
			.createProperty(doapBase + "release");
}
