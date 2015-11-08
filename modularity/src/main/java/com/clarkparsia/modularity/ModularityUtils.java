// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity;

import java.util.Iterator;
import java.util.Set;

import org.mindswap.pellet.utils.iterator.NestedIterator;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owlapi.modularity.ModuleType;

import com.clarkparsia.owlapi.modularity.locality.LocalityClass;

/**
 * @author Evren Sirin
 */
public class ModularityUtils {
	/**
	 * Extract the module from the imports closure of the given ontology for the
	 * given signature. Modules contain axioms related to the signature elements
	 * that describe how they relate to each other. There are four module types
	 * supported with the following very rough explanations: <ul <li>lower (top)
	 * module contains subclasses of the signature elements</li> <li>upper (bot)
	 * module contains superclasses of the signature elements</li> <li>
	 * upper-of-lower (bot_of_top) module extract the upper module from the
	 * lower module</li> <li>lower-of-upper (top_of_bot) module - extract the
	 * lower module from the upper module</li> </ul> The module types are
	 * closely related to the locality class used. Lower module is extracted
	 * with top locality and thus also called top module.
	 * 
	 * @param ontology
	 *            ontolgoy from which the module is extracted
	 * @param signature
	 *            set of entities used to extract the module
	 * @param moduleType
	 *            type of the module
	 * @return a set of axioms representing the relevant axioms for the
	 *         signature elements
	 */
	public static Set<OWLAxiom> extractModule(OWLOntology ontology, Set<OWLEntity> signature,
			ModuleType moduleType) {
		return extractModule( ontology.getImportsClosure(), signature, moduleType );
	}

	/**
	 * Extract the module from a given set of ontologies (but not their imports)
	 * for the given signature. Only the axioms in the given set of ontologies
	 * is considered. Only the axioms from the ontologies that explicitly exists
	 * in the given set will be included in the module.
	 * 
	 * @see #extractModule(OWLOntology, Set, ModuleType)
	 * @param ontologies
	 *            ontologies from which the module is extracted
	 * @param signature
	 *            set of entities used to extract the module
	 * @param moduleType
	 *            type of the module
	 * @return a set of axioms representing the relevant axioms for the
	 *         signature elements
	 */
	public static Set<OWLAxiom> extractModule(Set<OWLOntology> ontologies,
			Set<OWLEntity> signature, ModuleType moduleType) {
		switch ( moduleType ) {
		case TOP:
			return extractTopModule( axiomIterator( ontologies ), signature );

		case BOT:
			return extractBottomModule( axiomIterator( ontologies ), signature );

		case TOP_OF_BOT:
			Set<OWLAxiom> bottomModule = extractBottomModule( axiomIterator( ontologies ),
					signature );

			return extractTopModule( bottomModule.iterator(), signature );

		case BOT_OF_TOP:
			Set<OWLAxiom> topModule = extractTopModule( axiomIterator( ontologies ), signature );

			return extractBottomModule( topModule.iterator(), signature );

		default:
			throw new UnsupportedOperationException( "Unrecognized module type: " + moduleType );
		}
	}

	private static Iterator<OWLAxiom> axiomIterator(Set<OWLOntology> ontologies) {
		return new NestedIterator<OWLOntology, OWLAxiom>( ontologies ) {
			@Override
			public Iterator<? extends OWLAxiom> getInnerIterator(OWLOntology ont) {
				return ont.getAxioms().iterator();
			}
		};
	}

	private static Set<OWLAxiom> extractTopModule(Iterator<OWLAxiom> axioms,
			Set<OWLEntity> signature) {
		return extractModule( axioms, signature, LocalityClass.TOP_TOP );
	}

	private static Set<OWLAxiom> extractBottomModule(Iterator<OWLAxiom> axioms,
			Set<OWLEntity> signature) {
		return extractModule( axioms, signature, LocalityClass.BOTTOM_BOTTOM );
	}

	private static Set<OWLAxiom> extractModule(Iterator<OWLAxiom> axioms, Set<OWLEntity> signature,
			LocalityClass localityClass) {
		ModuleExtractor extractor = new AxiomBasedModuleExtractor( localityClass );
		while( axioms.hasNext() ) {
			extractor.addAxiom( axioms.next() );
		}
		return extractor.extractModule( signature );
	}

}
