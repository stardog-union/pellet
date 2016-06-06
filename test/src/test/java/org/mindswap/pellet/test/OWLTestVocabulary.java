// The MIT License
//
// Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
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

package org.mindswap.pellet.test;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.OWL;

public class OWLTestVocabulary
{
	private static Model _model = ModelFactory.createDefaultModel();
	private static String _otest = "http://www.w3.org/2002/03owlt/testOntology#";
	private static String _rtest = "http://www.w3.org/2000/10/rdf-tests/rdfcore/testSchema#";

	public static final Resource NotOwlFeatureTest = ResourceFactory.createResource(_otest + "NotOwlFeatureTest");
	public static final Resource PositiveEntailmentTest = ResourceFactory.createResource(_otest + "PositiveEntailmentTest");
	public static final Resource NegativeEntailmentTest = ResourceFactory.createResource(_otest + "NegativeEntailmentTest");
	public static final Resource TrueTest = ResourceFactory.createResource(_otest + "TrueTest");
	public static final Resource OWLforOWLTest = ResourceFactory.createResource(_otest + "OWLforOWLTest");
	public static final Resource ConsistencyTest = ResourceFactory.createResource(_otest + "ConsistencyTest");
	public static final Resource InconsistencyTest = ResourceFactory.createResource(_otest + "InconsistencyTest");
	public static final Resource ImportEntailmentTest = ResourceFactory.createResource(_otest + "ImportEntailmentTest");
	public static final Resource ImportLevelTest = ResourceFactory.createResource(_otest + "ImportLevelTest");
	public static final Resource DL = ResourceFactory.createResource(_otest + "DL");
	public static final Resource Lite = ResourceFactory.createResource(_otest + "Lite");
	public static final Resource Full = ResourceFactory.createResource(_otest + "Full");
	public static final Resource ClassificationTest = ResourceFactory.createResource(_otest + "ClassificationTest");
	public static final Literal Approved = _model.createLiteral("APPROVED", null);
	public static final Literal Proposed = _model.createLiteral("PROPOSED", null);
	public static final Literal Obsoleted = _model.createLiteral("OBSOLETED", null);
	public static final Literal ExtraCredit = _model.createLiteral("EXTRACREDIT", null);

	public static final Property level = ResourceFactory.createProperty(_otest, "level");
	public static final Property status = ResourceFactory.createProperty(_rtest, "status");
	public static final Property premiseDocument = ResourceFactory.createProperty(_rtest, "premiseDocument");
	public static final Property conclusionDocument = ResourceFactory.createProperty(_rtest, "conclusionDocument");
	public static final Property inputDocument = ResourceFactory.createProperty(_rtest, "inputDocument");
	public static final Property supportedDatatype = ResourceFactory.createProperty(_otest, "supportedDatatype");
	public static final Property notSupportedDatatype = ResourceFactory.createProperty(_otest, "notSupportedDatatype");

	public static final Resource OWL_DataRange = ResourceFactory.createResource(OWL.getURI() + "DataRange");

}
