// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.examples;

import com.clarkparsia.modularity.ModularityUtils;
import com.clarkparsia.owlapi.OWL;
import java.util.HashSet;
import java.util.Set;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;

/**
 * <p>
 * Title: ModularityExample
 * </p>
 * <p>
 * Description: This program shows the usage of Pellet's module extraction service
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Markus Stocker
 * @author Evren Sirin
 */
public class ModularityExample
{

	private static final String file = "file:examples/data/simple-galen.owl";
	private static final String NS = "http://www.co-ode.org/ontologies/galen#";

	private void run() throws Exception
	{
		// Create an OWLAPI manager that allows to load an ontology
		final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		// Load the ontology file into an OWL ontology object
		final OWLOntology ontology = manager.loadOntology(IRI.create(file));

		// Get some figures about the ontology and print them
		System.out.println("The ontology contains " + ontology.getLogicalAxiomCount() + //
				" axioms, " + ontology.classesInSignature().count() + //
				" classes, and " + ontology.objectPropertiesInSignature().count() + //
				" properties");

		// Create the signature of the module with are interested to extract
		final Set<OWLEntity> signature = new HashSet<>();
		signature.add(OWL.Class(NS + "Heart"));
		signature.add(OWL.Class(NS + "Liver"));
		signature.add(OWL.Class(NS + "BloodPressure"));

		// Select a module type. Modules contain axioms related to the signature
		// elements that describe how they relate to each other. There are four
		// module types supported with the following very rough explanations:
		// * lower (top) module
		//   contains subclasses of the signature elements
		// * upper (bot) module
		//   contains superclasses of the signature elements
		// * upper-of-lower (bot_of_top) module
		//   extract the upper module from the lower module
		// * lower-of-upper (top_of_bot) module -
		//   extract the lower module from the upper module
		//
		// The module types are closely related to the locality class used. Lower
		// module is extracted with top locality and thus also called top module.
		//
		// Upper-of-lower and lower-of-upper modules tend to be smaller (compared
		// to upper and lower modules) and we'll extract upper-of-lower module in
		// this example
		final ModuleType moduleType = ModuleType.STAR;

		// Extract the module axioms for the specified signature
		final Set<OWLAxiom> moduleAxioms = ModularityUtils.extractModule(ontology, signature, moduleType);
		// Create an ontology for the module axioms
		final OWLOntology moduleOnt = manager.createOntology(moduleAxioms);

		// Get some figures about the extracted module and print them
		System.out.println("The module contains " + moduleOnt.getLogicalAxiomCount() + //
				" axioms, " + moduleOnt.classesInSignature().count() + //
				" classes, and " + moduleOnt.objectPropertiesInSignature().count() + //
				" properties");
	}

	public static void main(final String[] args) throws Exception
	{
		final ModularityExample app = new ModularityExample();
		app.run();
	}

}
