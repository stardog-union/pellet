// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.owlapi.explanation.io.manchester;

public enum Keyword {
	AND("and", "purple", 3), OR("or", "purple", 3), ONLY("only", "purple", 3),
	SOME("some", "purple", 3), EXACTLY("exactly", "purple", 3), NOT("not", "purple", 3),
	OPEN_BRACE("{", "orange", 3), CLOSE_BRACE("}", "orange", 3), VALUE("value", "purple", 3),
	MIN("min", "purple", 3), MAX("max", "purple", 3), SUB_CLASS_OF("subClassOf", "green", 3),
	SUB_PROPERTY_OF("subPropertyOf", "green", 3),
	EQUIVALENT_TO("equivalentTo", "green", 3),
	@Deprecated
	EQUIVALENT_PROPERTY("equivalentTo", "green", 3),
	@Deprecated
	EQUIVALENT_CLASS("equivalentTo", "green", 3),
	EQUIVALENT_PROPERTIES("EquivalentProperties", "green", 3),
	EQUIVALENT_CLASSES("EquivalentClasses", "green", 3), ANNOTATION("annotation", "green", 3),
	LABEL("label", "green", 3), COMMENT("comment", "green", 3),
	INVERSE_OF("inverseOf", "green", 3), INVERSE("inverse", "green", 3),
	NOT_RELATIONSHIP("NOT", "green", 3), DISJOINT_PROPERTY("propertyDisjointWith", "green", 3),
	DISJOINT_CLASS("disjointWith", "green", 3),
	DISJOINT_PROPERTIES("DisjointProperties", "green", 3),
	DISJOINT_CLASSES("DisjointClasses", "green", 3),
	DISJOINT_UNION("disjointUnionOf", "green", 3), SYMMETRIC("symmetric", "green", 3),
	DECLARATION("declaration", "green", 3),
	@Deprecated
	ONTOLOGY_ANNOTATION("ontologyAnnotation", "green", 3),
	@Deprecated
	ENTITY_ANNOTATION_AXIOM("entityAnnotationAxiom", "green", 3),
	ASYMMETRIC_PROPERTY("Asymmetric", "green", 3),
	REFLEXIVE_PROPERTY("Reflexive", "green", 3),
	OBJECT_RELATIONSHIP("ObjectRelationship", "green", 3), RANGE("range", "green", 3),
	DOMAIN("domain", "green", 3), TYPE("type", "green", 3),
	IRREFLEXIVE("Irreflexive", "green", 3), TRANSITIVE("Transitive", "green", 3),
	DIFFERENT_INDIVIDUAL("differentFrom", "green", 3),
	DIFFERENT_INDIVIDUALS("DifferentIndividuals", "green", 3),
	DATA_RELATIONSHIP("DataRelationship", "green", 3),
	INVERSE_FUNCTIONAL("InverseFunctional", "green", 3), SAME_INDIVIDUAL("sameAs", "green", 3),
	SAME_INDIVIDUALS("SameIndividual", "green", 3),
	@Deprecated
	IMPORTS("imports", "green", 3),
	SELF("self", "green", 3),
	FUNCTIONAL("Functional", "green", 3),
	HAS_KEY("hasKey", "green", 3);

	private int		mSize;
	private String	mColor;
	private String	mLabel;
	private String	mFace;
	private String	mStyleClass;

	Keyword(String theLabel, String theStyleClass) {
		mLabel = theLabel;
		mStyleClass = theStyleClass;
	}

	Keyword(String theLabel, String theColor, int theSize) {
		this( theLabel, theColor, theSize, null );
	}

	Keyword(String theLabel, String theColor, int theSize, String theFace) {
		mColor = theColor;
		mSize = theSize;
		mLabel = theLabel;
		mFace = theFace != null
			? theFace
			: "monospaced";
	}

	public String getColor() {
		return mColor;
	}

	public String getFace() {
		return mFace;
	}

	public String getLabel() {
		return mLabel;
	}

	public int getSize() {
		return mSize;
	}

	public String getStyleClass() {
		return mStyleClass;
	}
}