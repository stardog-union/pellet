package com.clarkparsia.modularity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;


/**
 * Detects what kind of type of a change (i.e., TBox, RBox or ABox change) an addition or a deletion of 
 * an OWLAxiom introduces.
 * 
 * @author Blazej Bulka <blazej@clarkparsia.com>
 */
public class ChangeTypeDetector {
	/**
	 * The AxiomTypes of all axioms that are considered TBox axioms
	 */
	private static final Set<AxiomType> TBOX_AXIOM_TYPES = new HashSet<AxiomType>(Arrays.asList(new AxiomType[] { 
		AxiomType.DISJOINT_CLASSES,
		AxiomType.DISJOINT_UNION, 
		AxiomType.EQUIVALENT_CLASSES,
		AxiomType.SUBCLASS_OF
	}));

	/**
	 * The AxiomTypes of all axioms that are considered RBox axioms
	 */
	private static final Set<AxiomType> RBOX_AXIOM_TYPES = new HashSet<AxiomType>(Arrays.asList(new AxiomType[] { 
		AxiomType.ASYMMETRIC_OBJECT_PROPERTY,
		AxiomType.DATA_PROPERTY_DOMAIN,
		AxiomType.DATA_PROPERTY_RANGE,
		AxiomType.DISJOINT_DATA_PROPERTIES,
		AxiomType.DISJOINT_OBJECT_PROPERTIES,
		AxiomType.EQUIVALENT_DATA_PROPERTIES,
		AxiomType.EQUIVALENT_OBJECT_PROPERTIES,		
		AxiomType.FUNCTIONAL_DATA_PROPERTY,
		AxiomType.FUNCTIONAL_OBJECT_PROPERTY,
		AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY,
		AxiomType.INVERSE_OBJECT_PROPERTIES,
		AxiomType.IRREFLEXIVE_OBJECT_PROPERTY,
		AxiomType.OBJECT_PROPERTY_DOMAIN,
		AxiomType.OBJECT_PROPERTY_RANGE,
		AxiomType.REFLEXIVE_OBJECT_PROPERTY,
		AxiomType.SUB_DATA_PROPERTY,
		AxiomType.SUB_OBJECT_PROPERTY,
		AxiomType.SUB_PROPERTY_CHAIN_OF,
		AxiomType.SYMMETRIC_OBJECT_PROPERTY,
		AxiomType.TRANSITIVE_OBJECT_PROPERTY,
	}));

	/**
	 * The AxiomTypes that are considered ABox axioms
	 */
	private static final Set<AxiomType> ABOX_AXIOM_TYPES = new HashSet<AxiomType>(Arrays.asList(new AxiomType[] {
		AxiomType.CLASS_ASSERTION,
		AxiomType.DATA_PROPERTY_ASSERTION, 
		AxiomType.DIFFERENT_INDIVIDUALS,
		AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION,
		AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION,
		AxiomType.OBJECT_PROPERTY_ASSERTION,
		AxiomType.SAME_INDIVIDUAL
	}));
	
	/**
	 * Checks whether the given OWLAxiom is a TBox axiom
	 * 
	 * @param axiom the axiom to be checked
	 * @return true if the axiom is a TBox axiom
	 */
	public static boolean isTBoxAxiom(OWLAxiom axiom) {
		return (TBOX_AXIOM_TYPES.contains(axiom.getAxiomType()));
	}
	
	/**
	 * Checks whether the given OWLAxiom is a RBox axiom
	 * 
	 * @param axiom the axiom to be checked
	 * @return true if the axiom is a RBox axiom
	 */
	public static boolean isRBoxAxiom(OWLAxiom axiom) {
		return (RBOX_AXIOM_TYPES.contains(axiom.getAxiomType()));
	}
	
	/**
	 * Checks whether the given OWLAxiom is a ABox axiom
	 * 
	 * @param axiom the axiom to be checked
	 * @return true if the axiom is a ABox axiom
	 */
	public static boolean isABoxAxiom(OWLAxiom axiom) {
		return (ABOX_AXIOM_TYPES.contains(axiom.getAxiomType()));
	}
}
