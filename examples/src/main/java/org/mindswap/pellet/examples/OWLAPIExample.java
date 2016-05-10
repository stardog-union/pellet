// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.examples;

import com.clarkparsia.pellet.owlapi.PelletReasoner;
import com.clarkparsia.pellet.owlapi.PelletReasonerFactory;
import java.util.Set;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

/*
 * Created on Oct 10, 2004
 */

/**
 * @author Evren Sirin
 */
public class OWLAPIExample
{
	public final static void main(final String[] args) throws Exception
	{
		final String file = "http://www.mindswap.org/2004/owl/mindswappers#";

		System.out.print("Reading file " + file + "...");
		final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		final OWLOntology ontology = manager.loadOntology(IRI.create(file));

		final PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
		System.out.println("done.");

		reasoner.getKB().realize();
		reasoner.getKB().printClassTree();

		// create property and resources to query the reasoner
		final OWLClass Person = manager.getOWLDataFactory().getOWLClass(IRI.create("http://xmlns.com/foaf/0.1/Person"));
		final OWLObjectProperty workHomepage = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create("http://xmlns.com/foaf/0.1/workInfoHomepage"));
		final OWLDataProperty foafName = manager.getOWLDataFactory().getOWLDataProperty(IRI.create("http://xmlns.com/foaf/0.1/name"));

		// get all instances of Person class
		final NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(Person, false);
		for (final Node<OWLNamedIndividual> sameInd : individuals)
		{
			// sameInd contains information about the _individual (and all other individuals that were inferred to be the same)
			final OWLNamedIndividual ind = sameInd.getRepresentativeElement();

			// get the info about this specific _individual
			final Set<OWLLiteral> names = reasoner.getDataPropertyValues(ind, foafName);
			final NodeSet<OWLClass> types = reasoner.getTypes(ind, true);
			final NodeSet<OWLNamedIndividual> homepages = reasoner.getObjectPropertyValues(ind, workHomepage);

			// we know there is a single name for each person so we can get that value directly
			final String name = names.iterator().next().getLiteral();
			System.out.println("Name: " + name);

			// at least one direct type is guaranteed to exist for each _individual 
			final OWLClass type = types.iterator().next().getRepresentativeElement();
			System.out.println("Type:" + type);

			// there may be zero or more homepages so check first if there are any found
			if (homepages.isEmpty())
				System.out.print("Homepage: Unknown");
			else
			{
				System.out.print("Homepage:");
				for (final Node<OWLNamedIndividual> homepage : homepages)
					System.out.print(" " + homepage.getRepresentativeElement().getIRI());
			}
			System.out.println();
			System.out.println();
		}
	}
}
