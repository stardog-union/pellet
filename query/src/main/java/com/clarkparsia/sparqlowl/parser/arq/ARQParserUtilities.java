// Copyright (c) 2006 - 2010, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
package com.clarkparsia.sparqlowl.parser.arq;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mindswap.pellet.jena.vocabulary.OWL2;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.XSD;

/**
 * <p>
 * Title: ARQ Parser Utilities
 * </p>
 * <p>
 * Description: Static utility methods and fields used by the ANTLR generated
 * ARQ Tree Walker source. This code is in a separate Java file rather than in
 * the ANTLR sources to make it easier to maintain with comfortable Java tools
 * (e.g., Eclipse).
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
public class ARQParserUtilities {

	/**
	 * Set containing all OWL 2 datatypes
	 */
	private static final Set<Node>	OWL2_DATATYPES;
	/**
	 * Jena node for "false"^^xsd:boolean
	 */
	public static final Node		XSD_BOOLEAN_FALSE;
	/**
	 * Jena node for "true"^^xsd:boolean
	 */
	public static final Node		XSD_BOOLEAN_TRUE;

	static {
		XSD_BOOLEAN_FALSE = Node.createLiteral( Boolean.FALSE.toString(), null,
				XSDDatatype.XSDboolean );
		XSD_BOOLEAN_TRUE = Node.createLiteral( Boolean.TRUE.toString(), null,
				XSDDatatype.XSDboolean );

		/*
		 * After merging the newer datatype reasoner, the Collection below
		 * should be pulled from that code.
		 */
		OWL2_DATATYPES = Collections.unmodifiableSet( new HashSet<Node>( Arrays.<Node> asList( Node
				.createURI( OWL2.getURI() + "real" ), Node.createURI( OWL2.getURI() + "rational" ),
				XSD.decimal.asNode(), XSD.integer.asNode(), XSD.nonNegativeInteger.asNode(),
				XSD.nonPositiveInteger.asNode(), XSD.negativeInteger.asNode(), XSD.positiveInteger
						.asNode(), XSD.xlong.asNode(), XSD.xint.asNode(), XSD.xshort.asNode(),
				XSD.xbyte.asNode(), XSD.unsignedLong.asNode(), XSD.unsignedInt.asNode(),
				XSD.unsignedShort.asNode(), XSD.unsignedByte.asNode(), XSD.xdouble.asNode(),
				XSD.xfloat.asNode(), XSD.xstring.asNode(), XSD.normalizedString.asNode(), XSD.token
						.asNode(), XSD.language.asNode(), XSD.Name.asNode(), XSD.NCName.asNode(),
				XSD.xboolean.asNode(), XSD.hexBinary.asNode(), XSD.base64Binary.asNode(),
				XSD.anyURI.asNode(), XSD.dateTime.asNode(), Node.createURI( XSD.getURI()
						+ "dateTimeStamp" ), Node.createURI( RDF.getURI() + "XMLLiteral" ) ) ) );
	}

	/**
	 * Create an xsd:nonNegativeInteger literal from a string. Useful when
	 * parsing number restrictions.
	 * 
	 * @param s
	 *            A <code>String</code> of the number to be parsed
	 * @return A literal <code>Node</code>
	 */
	public static Node createNonNegativeInteger(String s) {
		return Node.createLiteral( s, null, XSDDatatype.XSDnonNegativeInteger );
	}

	/**
	 * Test if a <code>Node</code> is an OWL 2 datatype.
	 * 
	 * @param n
	 *            The <code>Node</code> to test
	 * @return <code>true</code> if <code>n</code> matches the URI of an OWL 2
	 *         datatype, else <code>false</code>
	 */
	public static boolean isOWL2Datatype(Node n) {
		return OWL2_DATATYPES.contains( n );
	}

	/**
	 * Construct an RDF container from a <code>List</code> of nodes, preserving
	 * the ordering
	 * 
	 * @param nodes
	 *            The list of <code>Node</code>s
	 * @param triples
	 *            A mutable container to which the <code>Triple</code>s
	 *            representing the container will be added.
	 * @return The blank node used as the start of the container
	 */
	public static Node listToTriples(List<Node> nodes, Collection<Triple> triples) {
		Node list = RDF.Nodes.nil;
		for( int i = nodes.size() - 1; i >= 0; i-- ) {
			final Node oldList = list;
			final Node first = nodes.get( i );
			list = Node.createAnon();
			triples.add( new Triple( list, RDF.Nodes.first, first ) );
			triples.add( new Triple( list, RDF.Nodes.rest, oldList ) );
		}
		return list;
	}

}
