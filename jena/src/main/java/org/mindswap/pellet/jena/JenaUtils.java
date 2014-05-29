// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under the terms of the MIT License.
//
// The MIT License
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package org.mindswap.pellet.jena;

import java.util.Iterator;
import java.util.Map;

import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.jena.vocabulary.OWL2;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.QNameProvider;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.Datatypes;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * Utility functions related to Jena structures. The functions here may have
 * similar functionality to the ones in ATermUtils but they are provided here
 * because ATermUtils is supposed to be library-independent (it should NOT
 * import Jena packages otherwise applications based on OWL-API would require
 * Jena packages)
 * 
 * @author Evren Sirin
 */
public class JenaUtils {
	final public static Literal	XSD_BOOLEAN_TRUE	= ResourceFactory.createTypedLiteral(
															Boolean.TRUE.toString(),
															XSDDatatype.XSDboolean );

	static public ATermAppl makeLiteral(LiteralLabel jenaLiteral) {
		String lexicalValue = jenaLiteral.getLexicalForm();
		String datatypeURI = jenaLiteral.getDatatypeURI();
		ATermAppl literalValue = null;

		if( datatypeURI != null )
			literalValue = ATermUtils.makeTypedLiteral( lexicalValue, datatypeURI );
		else if( jenaLiteral.language() != null )
			literalValue = ATermUtils.makePlainLiteral( lexicalValue, jenaLiteral.language() );
		else
			literalValue = ATermUtils.makePlainLiteral( lexicalValue );

		return literalValue;
	}

	static public ATermAppl makeATerm(RDFNode node) {
		return makeATerm( node.asNode() );
	}
	
	static public ATermAppl makeATerm(Node node) {
		if( node.isLiteral() ) {
			return makeLiteral( node.getLiteral() );
		}
		else if( node.isBlank() ) {
			return ATermUtils.makeBnode( node.getBlankNodeLabel() );
		}
		else if( node.isURI() ) {
			if( node.equals( OWL.Thing.asNode() ) ) {
				return ATermUtils.TOP;
			}
			else if( node.equals( OWL.Nothing.asNode() ) ) {
				return ATermUtils.BOTTOM;
			}
			else if ( node.equals( OWL2.topDataProperty.asNode() ) ) {
				return ATermUtils.TOP_DATA_PROPERTY;
			}
			else if ( node.equals( OWL2.bottomDataProperty.asNode() ) ) {
				return ATermUtils.BOTTOM_DATA_PROPERTY;
			}
			else if ( node.equals( OWL2.topObjectProperty.asNode() ) ) {
				return ATermUtils.TOP_OBJECT_PROPERTY;
			}
			else if ( node.equals( OWL2.bottomObjectProperty.asNode() ) ) {
				return ATermUtils.BOTTOM_OBJECT_PROPERTY;
			}
			else {
				return ATermUtils.makeTermAppl( node.getURI() );
			}
		}
		else if( node.isVariable() ) {
			return ATermUtils.makeVar( node.getName() );
		}

		return null;
	}

	static public Node makeGraphLiteral(ATermAppl literal) {
		Node node;

		String lexicalValue = ((ATermAppl) literal.getArgument( 0 )).getName();
		ATermAppl lang = (ATermAppl) literal.getArgument( 1 );
		ATermAppl datatype = (ATermAppl) literal.getArgument( 2 );

		if( datatype.equals( ATermUtils.PLAIN_LITERAL_DATATYPE ) ) {
			if( lang.equals( ATermUtils.EMPTY ) )
				node = Node.createLiteral( lexicalValue );
			else
				node = Node.createLiteral( lexicalValue, lang.getName(), false );
		}
		else if( datatype.equals( Datatypes.XML_LITERAL ) ) {
			node = Node.createLiteral( lexicalValue, "", true );
		}
		else {			
			RDFDatatype type = TypeMapper.getInstance().getTypeByName( datatype.getName() );
			node = Node.createLiteral( lexicalValue, "", type );
		}

		return node;
	}

	static public Node makeGraphResource(ATermAppl term) {
		if( ATermUtils.isBnode( term ) ) {
			return Node.createAnon( new AnonId( ((ATermAppl) term.getArgument( 0 )).getName() ) );
		}
		else if( term.equals( ATermUtils.TOP ) ) {
			return OWL.Thing.asNode();
		}
		else if( term.equals( ATermUtils.BOTTOM ) ) {
			return OWL.Nothing.asNode();
		}
		else if ( term.equals( ATermUtils.TOP_DATA_PROPERTY ) ) {
			return OWL2.topDataProperty.asNode();
		}
		else if ( term.equals( ATermUtils.BOTTOM_DATA_PROPERTY ) ) {
			return OWL2.bottomDataProperty.asNode();
		}
		else if ( term.equals( ATermUtils.TOP_OBJECT_PROPERTY ) ) {
			return OWL2.topObjectProperty.asNode();
		}
		else if ( term.equals( ATermUtils.BOTTOM_OBJECT_PROPERTY ) ) {
			return OWL2.bottomObjectProperty.asNode();
		}
		else if( term.getArity() == 0 ) {
			return Node.createURI( term.getName() );
		}
		else {
			throw new InternalReasonerException( "Invalid term found " + term );
		}
	}

	static public Node makeGraphNode(ATermAppl value) {
		if( ATermUtils.isLiteral( value ) )
			return makeGraphLiteral( value );
		else
			return makeGraphResource( value );
	}

	static public Literal makeLiteral(ATermAppl literal, Model model) {
		return (Literal) model.asRDFNode( makeGraphLiteral( literal ) );
	}

	static public Resource makeResource(ATermAppl term, Model model) {
		return (Resource) model.asRDFNode( makeGraphResource( term ) );
	}

	static public RDFNode makeRDFNode(ATermAppl term, Model model) {
		return model.asRDFNode( makeGraphNode( term ) );
	}

	static public QNameProvider makeQNameProvider(PrefixMapping mapping) {
		QNameProvider qnames = new QNameProvider();

		Iterator<Map.Entry<String, String>> entries = mapping.getNsPrefixMap().entrySet().iterator();
		while( entries.hasNext() ) {
			Map.Entry<String, String> entry = entries.next();
			String prefix = entry.getKey();
			String uri = entry.getValue();

			qnames.setMapping( prefix, uri );
		}

		return qnames;
	}
}
