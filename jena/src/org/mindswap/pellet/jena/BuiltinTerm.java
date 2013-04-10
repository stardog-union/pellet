// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.mindswap.pellet.jena.vocabulary.OWL2;
import org.mindswap.pellet.jena.vocabulary.SWRL;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * <p>
 * Title:
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
 * @author Evren Sirin
 */
public enum BuiltinTerm {
	RDF_Property(RDF.Property),

	RDFS_Class(RDFS.Class), RDFS_Datatype(RDFS.Datatype),

	OWL_Thing(OWL.Thing, false, true), OWL_Nothing(OWL.Nothing, false, true), OWL_Class(OWL.Class),
	OWL_ObjectProperty(OWL.ObjectProperty), OWL_DatatypeProperty(OWL.DatatypeProperty),
	OWL_FunctionalProperty(OWL.FunctionalProperty),
	OWL_InverseFunctionalProperty(OWL.InverseFunctionalProperty),
	OWL_TransitiveProperty(OWL.TransitiveProperty), OWL_SymmetricProperty(OWL.SymmetricProperty),
	OWL_AnnotationProperty(OWL.AnnotationProperty), OWL2_ReflexiveProperty(OWL2.ReflexiveProperty),
	OWL2_IrreflexiveProperty(OWL2.IrreflexiveProperty),
	OWL2_AsymmetricProperty(OWL2.AsymmetricProperty),

	OWL_DataRange(OWL.DataRange),

	OWL2_NamedIndividual(OWL2.NamedIndividual),

	OWL2_NegativePropertyAssertion(OWL2.NegativePropertyAssertion),

	OWL_AllDifferent(OWL.AllDifferent), OWL2_AllDisjointClasses(OWL2.AllDisjointClasses),
	OWL2_AllDisjointProperties(OWL2.AllDisjointProperties),

	SWRL_Imp(SWRL.Imp), SWRL_ClassAtom(SWRL.ClassAtom, true),
	SWRL_IndividualPropertyAtom(SWRL.IndividualPropertyAtom, true),
	SWRL_DatavaluedPropertyAtom(SWRL.DatavaluedPropertyAtom, true),
	SWRL_SameIndividualAtom(SWRL.SameIndividualAtom, true),
	SWRL_DifferentIndividualsAtom(SWRL.DifferentIndividualsAtom, true),
	SWRL_DataRangeAtom(SWRL.DataRangeAtom, true), SWRL_BuiltinAtom(SWRL.BuiltinAtom, true),
	SWRL_Builtin(SWRL.Builtin, true), SWRL_AtomList(SWRL.AtomList, true),
	SWRL_Variable(SWRL.Variable, true),

	RDFS_subClassOf(RDFS.subClassOf), RDFS_subPropertyOf(RDFS.subPropertyOf),
	RDFS_domain(RDFS.domain), RDFS_range(RDFS.range),

	OWL_unionOf(OWL.unionOf), OWL_intersectionOf(OWL.intersectionOf),
	OWL_complementOf(OWL.complementOf), OWL_oneOf(OWL.oneOf), OWL_inverseOf(OWL.inverseOf),
	OWL_sameAs(OWL.sameAs, false, true), OWL_equivalentProperty(OWL.equivalentProperty),
	OWL_equivalentClass(OWL.equivalentClass),
	OWL_distinctMembers(OWL.distinctMembers, false, true), OWL_disjointWith(OWL.disjointWith),
	OWL_differentFrom(OWL.differentFrom, false, true), OWL_members(OWL2.members),

	OWL2_disjointUnionOf(OWL2.disjointUnionOf),
	OWL2_propertyDisjointWith(OWL2.propertyDisjointWith),

	/** @deprecated Not in OWL 2 spec, only in earlier drafts */
	OWL2_propertyChain(OWL2.propertyChain, true),
	OWL2_propertyChainAxiom(OWL2.propertyChainAxiom),
	
	OWL2_Axiom(OWL2.Axiom, true), OWL2_Annotation(OWL2.Annotation, true), 
	OWL2_annotatedSource(OWL2.annotatedSource, true), OWL2_annotatedProperty(OWL2.annotatedProperty, true), 
	OWL2_annotatedTarget(OWL2.annotatedTarget, true), OWL2_object(OWL2.object, true), 
	OWL2_predicate(OWL2.predicate, true), OWL2_subject(OWL2.subject, true),
	
	
	RDF_type(RDF.type, false, true), RDF_first(RDF.first, true), RDF_rest(RDF.rest, true),
	RDF_subject(RDF.subject, true), RDF_predicate(RDF.predicate, true),
	RDF_object(RDF.object, true),

	OWL_imports(OWL.imports, true), OWL_onProperty(OWL.onProperty, true),
	OWL_hasValue(OWL.hasValue, true), OWL_allValuesFrom(OWL.allValuesFrom, true),
	OWL_someValuesFrom(OWL.someValuesFrom, true), OWL_minCardinality(OWL.minCardinality, true),
	OWL_maxCardinality(OWL.maxCardinality, true), OWL_cardinality(OWL.cardinality, true),
	OWL_versionInfo(OWL.versionInfo, true),
	OWL_backwardCompatibleWith(OWL.backwardCompatibleWith, true),
	OWL_incompatibleWith(OWL.incompatibleWith, true), OWL_priorVersion(OWL.priorVersion, true),

	OWL2_onClass(OWL2.onClass, true), OWL2_onDataRange(OWL2.onDataRange, true),
	OWL2_qualifiedCardinality(OWL2.qualifiedCardinality, true),
	OWL2_minQualifiedCardinality(OWL2.minQualifiedCardinality, true),
	OWL2_maxQualifiedCardinality(OWL2.maxQualifiedCardinality, true),

	OWL2_onDatatype(OWL2.onDatatype, true), OWL2_withRestrictions(OWL2.withRestrictions, true),
	OWL2_minInclusive(OWL2.minInclusive, true), OWL2_minExclusive(OWL2.minExclusive, true),
	OWL2_maxInclusive(OWL2.maxInclusive, true), OWL2_maxExclusive(OWL2.maxExclusive, true),
	OWL2_minLength(OWL2.minLength, true), OWL2_maxLength(OWL2.maxLength, true),
	OWL2_length(OWL2.length, true), OWL2_totalDigits(OWL2.length, true),
	OWL2_fractionDigits(OWL2.fractionDigits, true),
	OWL2_datatypeComplementOf(OWL2.datatypeComplementOf, true),

	OWL2_sourceIndividual(OWL2.sourceIndividual, true),
	OWL2_assertionProperty(OWL2.assertionProperty, true),
	OWL2_targetIndividual(OWL2.targetIndividual, true), OWL2_targetValue(OWL2.targetValue, true),

	OWL2_hasKey(OWL2.hasKey),

	OWL2_hasSelf(OWL2.hasSelf, true),
	
	OWL2_topDataProperty(OWL2.topDataProperty, false, true),
	OWL2_bottomDataProperty(OWL2.bottomDataProperty, false, true),
	OWL2_topObjectProperty(OWL2.topObjectProperty, false, true),
	OWL2_bottomObjectProperty(OWL2.bottomObjectProperty, false, true),

	SWRL_argument1(SWRL.argument1, true), SWRL_argument2(SWRL.argument2, true),
	SWRL_body(SWRL.body, true), SWRL_head(SWRL.head, true), SWRL_builtin(SWRL.builtin, true),
	SWRL_arguments(SWRL.arguments, true), SWRL_classPredicate(SWRL.classPredicate, true),
	SWRL_propertyPredicate(SWRL.propertyPredicate, true), SWRL_dataRange(SWRL.dataRange, true),

	RDF_List(RDF.List, true), RDF_Statement(RDF.Statement, true),
	OWL_Restriction(OWL.Restriction, true), OWL_Ontology(OWL.Ontology, true),

	/** @deprecated Not in OWL 2 spec, in OWL 1.1. Use {@link #OWL2_hasSelf} */
	OWL2_SelfRestriction(OWL2.SelfRestriction, true);

	private static final Map<Node, BuiltinTerm>	nodeMap;
	static {
		nodeMap = new HashMap<Node, BuiltinTerm>();
		for( BuiltinTerm builtinTerm : BuiltinTerm.values() ) {
			nodeMap.put( builtinTerm.getNode(), builtinTerm );
		}

		nodeMap.put( OWL.DeprecatedClass.asNode(), OWL_Class );
		nodeMap.put( OWL.DeprecatedProperty.asNode(), RDF_Property );
	}

	public static final EnumSet<BuiltinTerm>	EXPRESSION_PREDICATES	= EnumSet
																				.of(
																						BuiltinTerm.OWL_intersectionOf,
																						BuiltinTerm.OWL_unionOf,
																						BuiltinTerm.OWL_complementOf,
																						BuiltinTerm.OWL2_datatypeComplementOf,
																						BuiltinTerm.OWL2_disjointUnionOf,
																						BuiltinTerm.OWL_oneOf,
																						BuiltinTerm.OWL_inverseOf,
																						BuiltinTerm.OWL2_onDatatype,
																						BuiltinTerm.OWL2_onDataRange,
																						BuiltinTerm.OWL2_propertyChain );

	public static BuiltinTerm find(Node node) {
		return nodeMap.get( node );
	}

	public static boolean isExpression(Node node) {
		BuiltinTerm builtin = find( node );
		return builtin != null && EXPRESSION_PREDICATES.contains( builtin );
	}

	public static boolean isExpression(BuiltinTerm builtin) {
		return EXPRESSION_PREDICATES.contains( builtin );
	}

	public static boolean isBuiltin(Node node) {
		return find(node) != null;
	}
	
	public static boolean isSyntax(Node node) {
		BuiltinTerm builtin = find( node );
		return builtin != null && builtin.isSyntax();
	}

	private Node	node;
	private boolean	isABox;
	private boolean	isPredicate;
	private boolean	isSyntax;

	BuiltinTerm(Resource resource) {
		this( resource, false );
	}

	BuiltinTerm(Resource resource, boolean isSyntax) {
		this( resource, isSyntax, false );
	}

	BuiltinTerm(Resource resource, boolean isSyntax, boolean isABox) {
		this.node = resource.asNode();
		this.isSyntax = isSyntax;
		this.isPredicate = resource instanceof Property;
		this.isABox = isABox;
	}

	public boolean isABox() {
		return isABox;
	}

	public boolean isSyntax() {
		return isSyntax;
	}

	public boolean isPredicate() {
		return isPredicate;
	}

	public Node getNode() {
		return node;
	}

	public String getURI() {
		return node.getURI();
	}
}
