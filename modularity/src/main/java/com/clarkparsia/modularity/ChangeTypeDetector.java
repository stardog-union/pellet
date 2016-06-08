package com.clarkparsia.modularity;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * Detects what kind of type of a change (i.e., TBox, RBox or ABox change) an addition or a deletion of an OWLAxiom introduces.
 *
 * @author Blazej Bulka <blazej@clarkparsia.com>
 */
public class ChangeTypeDetector
{
	/**
	 * Checks whether the given OWLAxiom is a TBox axiom
	 *
	 * @param axiom the axiom to be checked
	 * @return true if the axiom is a TBox axiom
	 */
	public static boolean isTBoxAxiom(final OWLAxiom axiom)
	{
		return axiom.isOfType(AxiomType.TBoxAxiomTypes);
	}

	/**
	 * Checks whether the given OWLAxiom is a RBox axiom
	 *
	 * @param axiom the axiom to be checked
	 * @return true if the axiom is a RBox axiom
	 */
	public static boolean isRBoxAxiom(final OWLAxiom axiom)
	{
		return axiom.isOfType(AxiomType.RBoxAxiomTypes);
	}

	/**
	 * Checks whether the given OWLAxiom is a ABox axiom
	 *
	 * @param axiom the axiom to be checked
	 * @return true if the axiom is a ABox axiom
	 */
	public static boolean isABoxAxiom(final OWLAxiom axiom)
	{
		return axiom.isOfType(AxiomType.ABoxAxiomTypes);
	}
}
