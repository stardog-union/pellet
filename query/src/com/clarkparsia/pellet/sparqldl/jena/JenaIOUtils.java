// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.jena;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.sparql.resultset.XMLInput;
import com.hp.hpl.jena.util.FileManager;

/**
 * <p>
 * Title: Query Utilities
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
public class JenaIOUtils {

	public enum RDFFormatType {
		N3("N3"), RDFXML("RDF/XML"), TURTLE("TURTLE"), NTRIPLE("N-TRIPLE");

		private String	jenaName;

		RDFFormatType(final String s) {
			this.jenaName = s;
		}

		public String jenaName() {
			return jenaName;
		}

	}

	public static ResultSet parseResultSet(final String resultURI) throws IOException {
		if( resultURI.endsWith( "srx" ) ) {
			return ResultSetFactory.fromXML( URI.create( resultURI ).toURL().openStream() );
		}
		else if( resultURI.endsWith( "ttl" ) ) {
			return ResultSetFactory.fromRDF( FileManager.get().loadModel( resultURI ) );
		}
		else if( resultURI.endsWith( "rdf" ) ) {
			return ResultSetFactory.fromRDF( FileManager.get().loadModel( resultURI ) );
		}
		else {
			throw new RuntimeException( "Unknown format." );
		}
	}

	// TODO meanwhile just for files
	public static boolean parseAskResult(final String resultURI) throws FileNotFoundException {

		if( resultURI.endsWith( "srx" ) ) {
			return XMLInput.booleanFromXML( new FileInputStream( resultURI.substring( 5 ) ) );
		}
		else if( resultURI.endsWith( "ttl" ) || resultURI.endsWith( "rdf" ) ) {
			return FileManager
					.get()
					.loadModel( resultURI.substring( 5 ) )
					.getProperty(
							null,
							ResourceFactory
									.createProperty( "http://www.w3.org/2001/sw/DataAccess/tests/result-set#boolean" ) )
					.getBoolean();
		}
		else {
			throw new RuntimeException( "Unknown format." );
		}
	}

	public static RDFFormatType fileType(String fileURI) {
		if( fileURI.endsWith( ".n3" ) ) {
			return RDFFormatType.N3;
		}
		else if( fileURI.endsWith( ".ttl" ) ) {
			return RDFFormatType.TURTLE;
		}
		else {
			return RDFFormatType.RDFXML;
		}
	}
}
