// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.parser;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * <p>
 * Title: Vocabulary for nonOWL SPARQL-DL constructs.
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

public class SparqldlExtensionsVocabulary {

	public static final String sdlExtensionsBase = "http://pellet.owldl.com/ns/sdle#";

	public static final String sdlExtensionsNs = "sdle";

	// SPARQL-DL extensions
	public static final Property strictSubClassOf = ResourceFactory
			.createProperty(sdlExtensionsBase + "strictSubClassOf");

	public static final Property directSubClassOf = ResourceFactory
			.createProperty(sdlExtensionsBase + "directSubClassOf");

	public static final Property directSubPropertyOf = ResourceFactory
			.createProperty(sdlExtensionsBase + "directSubPropertyOf");

	public static final Property strictSubPropertyOf = ResourceFactory
			.createProperty(sdlExtensionsBase + "strictSubPropertyOf");

	public static final Property directType = ResourceFactory
			.createProperty(sdlExtensionsBase + "directType");
}
