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

package org.mindswap.pellet.jena.vocabulary;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class SWRL {
	private static final String		URI							= "http://www.w3.org/2003/11/swrl#";
	
	public static String getURI() { return URI; }

	public static final Resource	Imp							= ResourceFactory
																		.createResource( URI
																				+ "Imp" );
	/**
	 * @deprecated Correct term is {@link #Imp}
	 */
	public static final Resource	Impl						= ResourceFactory
																		.createResource( URI
																				+ "Impl" );

	public static final Resource	AtomList					= ResourceFactory
																		.createResource( URI
																				+ "AtomList" );
	public static final Resource	Variable					= ResourceFactory
																		.createResource( URI
																				+ "Variable" );
	public static final Resource	IndividualPropertyAtom		= ResourceFactory
																		.createResource( URI
																				+ "IndividualPropertyAtom" );
	public static final Resource	BuiltinAtom					= ResourceFactory
																		.createResource( URI
																				+ "BuiltinAtom" );
	public static final Resource	DatavaluedPropertyAtom		= ResourceFactory
																		.createResource( URI
																				+ "DatavaluedPropertyAtom" );
	public static final Resource	DataRangeAtom				= ResourceFactory
																		.createResource( URI
																				+ "DataRangeAtom" );
	public static final Resource	ClassAtom					= ResourceFactory
																		.createResource( URI
																				+ "ClassAtom" );
	public static final Resource	SameIndividualAtom			= ResourceFactory
																		.createResource( URI
																				+ "SameIndividualAtom" );
	public static final Resource	DifferentIndividualsAtom	= ResourceFactory
																		.createResource( URI
																				+ "DifferentIndividualsAtom" );
	public static final Resource	Builtin						= ResourceFactory
																		.createResource( URI
																				+ "builtin" );

	public static final Property	head						= ResourceFactory
																		.createProperty( URI
																				+ "head" );
	public static final Property	body						= ResourceFactory
																		.createProperty( URI
																				+ "body" );
	public static final Property	dataRange					= ResourceFactory
																		.createProperty( URI 
																				+ "dataRange" );
	public static final Property	propertyPredicate			= ResourceFactory
																		.createProperty( URI
																				+ "propertyPredicate" );
	public static final Property	arguments					= ResourceFactory
																		.createProperty( URI
																				+ "arguments" );
	public static final Property	argument1					= ResourceFactory
																		.createProperty( URI
																				+ "argument1" );
	public static final Property	argument2					= ResourceFactory
																		.createProperty( URI
																				+ "argument2" );
	public static final Property	classPredicate				= ResourceFactory
																		.createProperty( URI
																				+ "classPredicate" );
	public static final Property	builtin						= ResourceFactory
																		.createProperty( URI
																				+ "builtin" );
}
