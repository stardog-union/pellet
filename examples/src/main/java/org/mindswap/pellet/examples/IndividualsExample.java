// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.examples;

import com.clarkparsia.pellet.owlapi.PelletReasoner;
import java.util.Iterator;
import java.util.Set;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

/**
 * Example to demonstrate how to use the reasoner for queries related to individuals. Exact same functionality is shown for both Jena and OWL-API interfaces.
 *
 * @author Evren Sirin
 */
public class IndividualsExample
{
	public static void main(final String[] args) throws Exception
	{
		System.out.println("Results using Jena interface");
		System.out.println("----------------------------");
		runWithJena();

		System.out.println("Results using OWL-API interface");
		System.out.println("-------------------------------");
		runWithOWLAPI();
	}

	public static void runWithJena()
	{
		// ontology that will be used
		final String ont = "http://www.mindswap.org/2004/owl/mindswappers#";

		// load the ontology with its imports and no reasoning
		final OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		model.read(ont);

		// load the model to the reasoner
		model.prepare();

		// create property and resources to query the reasoner
		final OntClass Person = model.getOntClass("http://xmlns.com/foaf/0.1/Person");
		final Property workHomepage = model.getProperty("http://xmlns.com/foaf/0.1/workInfoHomepage");
		final Property foafName = model.getProperty("http://xmlns.com/foaf/0.1/name");

		// get all instances of Person class
		final Iterator<?> i = Person.listInstances();
		while (i.hasNext())
		{
			final Individual ind = (Individual) i.next();

			// get the info about this specific individual
			final String name = ((Literal) ind.getPropertyValue(foafName)).getString();
			final Resource type = ind.getRDFType();
			final Resource homepage = (Resource) ind.getPropertyValue(workHomepage);

			// print the results
			System.out.println("Name: " + name);
			System.out.println("Type: " + type.getLocalName());
			if (homepage == null)
				System.out.println("Homepage: Unknown");
			else
				System.out.println("Homepage: " + homepage);
			System.out.println();
		}
	}

	public static void runWithOWLAPI() throws OWLException
	{
		final String ont = "http://www.mindswap.org/2004/owl/mindswappers#";

		// create an ontology manager
		final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		final OWLDataFactory factory = manager.getOWLDataFactory();

		// read the ontology
		final OWLOntology ontology = manager.loadOntology(IRI.create(ont));

		// load the ontology to the reasoner
		final PelletReasoner reasoner = com.clarkparsia.pellet.owlapi.PelletReasonerFactory.getInstance().createReasoner(ontology);

		// create property and resources to query the reasoner
		final OWLClass Person = factory.getOWLClass(IRI.create("http://xmlns.com/foaf/0.1/Person"));
		final OWLObjectProperty workHomepage = factory.getOWLObjectProperty(IRI.create("http://xmlns.com/foaf/0.1/workInfoHomepage"));
		final OWLDataProperty foafName = factory.getOWLDataProperty(IRI.create("http://xmlns.com/foaf/0.1/name"));

		// get all instances of Person class
		final Set<OWLNamedIndividual> individuals = reasoner.getInstances(Person, false).getFlattened();
		for (final OWLNamedIndividual ind : individuals)
		{
			// get the info about this specific individual
			final Set<OWLLiteral> names = reasoner.getDataPropertyValues(ind, foafName);
			final NodeSet<OWLClass> types = reasoner.getTypes(ind, true);
			final NodeSet<OWLNamedIndividual> homepages = reasoner.getObjectPropertyValues(ind, workHomepage);

			// we know there is a single name for each person so we can get that value directly
			final String name = names.iterator().next().getLiteral();
			System.out.println("Name: " + name);

			// at least one direct type is guaranteed to exist for each individual 
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
