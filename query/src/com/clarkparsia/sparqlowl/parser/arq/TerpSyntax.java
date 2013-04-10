// Copyright (c) 2010, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
package com.clarkparsia.sparqlowl.parser.arq;

import com.hp.hpl.jena.query.Syntax;

/**
 * <p>
 * Title: ARQ Terp Syntax
 * </p>
 * <p>
 * Description: Terp Syntax class for use with ARQ parsing infrastructure
 * </p>
 * <p>
 * Copyright: Copyright (c) 2010
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Mike Smith <a
 *         href="mailto:msmith@clarkparsia.com">msmith@clarkparsia.com</a>
 */
public class TerpSyntax extends Syntax {

	private static final TerpSyntax	INSTANCE;
	private static final String		NAME;
	private static final String		URI;

	static {
		URI = "tag:clarkparsia.com,2010:terp/syntax";
		NAME = "terp";
		INSTANCE = new TerpSyntax();
		Syntax.querySyntaxNames.put( NAME, INSTANCE );
	}

	private TerpSyntax() {
		super( URI );
	}

	/**
	 * Get the singleton instance of the class
	 * 
	 * @return the singleton instance
	 */
	public static TerpSyntax getInstance() {
		return INSTANCE;
	}
}
