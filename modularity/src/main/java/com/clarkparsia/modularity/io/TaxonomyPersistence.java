// Copyright (c) 2006 - 2009, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.io;

import com.clarkparsia.owlapi.OWL;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import openllet.shared.tools.Log;
import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.taxonomy.TaxonomyNode;
import org.mindswap.pellet.utils.TaxonomyUtils;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.io.StreamDocumentTarget;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

/**
 * <p>
 * Title: Stores and restores a taxonomy to a stream.
 * </p>
 * <p>
 * Description: Enables storing and reading back a taxonomy from a stream. The taxonomy is first converted into an ontology, and then saved using the standard
 * OWLRenderers.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Blazej Bulka
 */
public class TaxonomyPersistence
{

	public static final Logger _logger = Log.getLogger(TaxonomyPersistence.class);

	/**
	 * The URI of the ontology created to represent the Taxonomy
	 */
	private static IRI TAXONOMY_ONTOLOGY_IRI = IRI.create("http://clarkparsia.com/pellet/modularity/taxonomy");

	/**
	 * Saves a taxonomy into a stream.
	 *
	 * @param ontologyManager the ontology manager
	 * @param taxonomy the taxonomy to be saved
	 * @param outputStream the output stream where the ontology should be saved
	 * @throws IOException if an I/O error should occur
	 */
	public static void save(final Taxonomy<OWLClass> taxonomy, final OutputStream outputStream) throws IOException
	{
		try
		{
			final OWLOntology ontology = createTaxonomyOntology(taxonomy);

			OWL._manager.saveOntology(ontology, new OWLXMLDocumentFormat(), new StreamDocumentTarget(outputStream));

			outputStream.flush();

			OWL._manager.removeOntology(ontology);
		}
		catch (final OWLException e)
		{
			_logger.log(Level.SEVERE, "An error occured while creating an ontology for taxonomy", e);
			throw new IOException("An error occured while creating an ontology for taxonomy");
		}
	}

	/**
	 * Converts a taxonomy into an ontology.
	 *
	 * @param ontologyManager the ontology manager
	 * @param taxonomy the taxonomy to be converted
	 * @return the ontology based on the taxonomy
	 * @throws OWLOntologyCreationException if OWLAPI reports an exception during the creation of the ontology
	 * @throws OWLOntologyChangeException if OWLAPI report an exception during the population of the ontology
	 */
	private static OWLOntology createTaxonomyOntology(final Taxonomy<OWLClass> taxonomy) throws OWLOntologyChangeException
	{
		final OWLOntology ontology = OWL.Ontology(Collections.<OWLAxiom> emptyList(), TAXONOMY_ONTOLOGY_IRI);

		// populate the ontology
		final LinkedList<OWLOntologyChange> changes = new LinkedList<>();
		final HashSet<OWLClass> processedEquivalentClasses = new HashSet<>();

		for (final TaxonomyNode<OWLClass> taxonomyNode : taxonomy.getNodes())
		{
			if (processedEquivalentClasses.contains(taxonomyNode.getName()))
				continue;

			processedEquivalentClasses.addAll(taxonomyNode.getEquivalents());

			for (final OWLClass owlClass : taxonomyNode.getEquivalents())
			{
				// add the class axiom
				final AddAxiom classAxiom = new AddAxiom(ontology, OWL.declaration(owlClass));
				changes.add(classAxiom);

				// add the super/subclass axiom between the classes
				for (final TaxonomyNode<OWLClass> superNode : taxonomyNode.getSupers())
				{
					//if( superNode == taxonomy.getTop() )
					//	continue;
					final AddAxiom subClassOfAxiom = new AddAxiom(ontology, OWL.subClassOf(owlClass, superNode.getName()));
					changes.add(subClassOfAxiom);
				}
			}

			// add the equivalent classes axiom
			if (taxonomyNode.getEquivalents().size() > 1)
			{
				final AddAxiom equivalentAxiom = new AddAxiom(ontology, OWL.equivalentClasses(taxonomyNode.getEquivalents()));
				changes.add(equivalentAxiom);
			}

			// save the individuals
			final Collection<OWLNamedIndividual> individuals = getDatumInstanceAsCollectorOfOWLNamedIndividual(taxonomyNode);

			if (individuals != null && !individuals.isEmpty())
				for (final OWLNamedIndividual ind : individuals)
				{
					final AddAxiom classAssertionAxiom = new AddAxiom(ontology, OWL.classAssertion(ind, taxonomyNode.getName()));
					changes.add(classAssertionAxiom);
				}
		}

		OWL._manager.applyChanges(changes);

		return ontology;
	}

	@SuppressWarnings("unchecked")
	private static Collection<OWLNamedIndividual> getDatumInstanceAsCollectorOfOWLNamedIndividual(final TaxonomyNode<OWLClass> taxonomyNode)
	{
		return (Collection<OWLNamedIndividual>) taxonomyNode.getDatum(TaxonomyUtils.INSTANCES_KEY);
	}

	/**
	 * Gets all the super classes of the given class in the ontology
	 *
	 * @param ontology ontology to be queried
	 * @param owlClass the class whose super classes are to be retrieved
	 * @return a set of super classes
	 */
	private static Stream<OWLClass> superClasses(final OWLOntology ontology, final OWLClass owlClass)
	{
		return ontology.subClassAxiomsForSubClass(owlClass) //
				.map(OWLSubClassOfAxiom::getSuperClass) //
				.filter(owlSuperDescription -> owlSuperDescription instanceof OWLClass) //
				.map(x -> (OWLClass) x); //
	}

	private static Set<OWLClass> getSuperClasses(final OWLOntology ontology, final OWLClass owlClass)
	{
		return superClasses(ontology, owlClass).collect(Collectors.toSet());
	}

	/**
	 * Creates a taxonomy from the ontology.
	 *
	 * @param ontology the ontology containing the _data which is the source for the taxonomy
	 * @return the created taxonomy
	 */
	private static Taxonomy<OWLClass> createTaxonomy(final OWLOntology ontology)
	{
		final Taxonomy<OWLClass> taxonomy = new Taxonomy<>(null, OWL.Thing, OWL.Nothing);

		final HashSet<OWLClass> processedEquivalentClasses = new HashSet<>();
		processedEquivalentClasses.add(OWL.Thing);
		processedEquivalentClasses.add(OWL.Nothing);

		// first create all the _nodes in the taxonomy based on classes in the ontology and the equivalence relationships among them
		// (only one _node in taxonomy for all the equivalent classes in the group)
		ontology.classesInSignature().filter(owlClass -> !processedEquivalentClasses.contains(owlClass)).forEach(owlClass ->
		{
			final HashSet<OWLClass> equivalentClasses = new HashSet<>();
			final boolean[] thing_Nothing = { false, false };

			ontology.equivalentClassesAxioms(owlClass).forEach(equivalentAxiom ->
			{
				equivalentAxiom.namedClasses().forEach(equivalentClasses::add);

				if (equivalentAxiom.containsOWLNothing())
					thing_Nothing[1] = true; //equivalentToNothing

				if (equivalentAxiom.containsOWLThing())
					thing_Nothing[0] = true; //equivalentToThing
			});

			equivalentClasses.removeAll(processedEquivalentClasses);

			if (thing_Nothing[0])
				taxonomy.addEquivalents(OWL.Thing, equivalentClasses);
			else
				if (thing_Nothing[1])
					taxonomy.addEquivalents(OWL.Nothing, equivalentClasses);
				else
				{
					if (equivalentClasses.contains(owlClass))
						equivalentClasses.remove(owlClass);

					taxonomy.addNode(owlClass, false);
					taxonomy.addEquivalents(owlClass, equivalentClasses);
				}

			processedEquivalentClasses.add(owlClass);
			processedEquivalentClasses.addAll(equivalentClasses);
		});

		// post process the top and bottom _nodes
		for (final TaxonomyNode<OWLClass> taxonomyNode : taxonomy.getNodes())
			if (OWL.Nothing.equals(taxonomyNode.getName()) && taxonomyNode.getSupers().size() > 1 && taxonomyNode.getSupers().contains(taxonomy.getTop()))
				taxonomy.getTop().removeSub(taxonomyNode);

		// after all the _nodes are in the taxonomy, create subclass and superclass relationships among them
		for (final TaxonomyNode<OWLClass> taxonomyNode : taxonomy.getNodes())
		{
			final OWLClass owlClass = taxonomyNode.getName();

			if (owlClass == null || owlClass.equals(OWL.Nothing))
				continue;

			taxonomy.addSupers(owlClass, getSuperClasses(ontology, owlClass));
		}

		// read the instance _data (if available)
		for (final TaxonomyNode<OWLClass> taxonomyNode : taxonomy.getNodes())
		{
			final Set<OWLNamedIndividual> individuals = //
			ontology.classAssertionAxioms(taxonomyNode.getName())//
					.map(classAssertionAxiom -> classAssertionAxiom.getIndividual())//
					.filter(individual -> individual.isNamed() && individual instanceof OWLNamedIndividual) //
					.map(x -> (OWLNamedIndividual) x)//
					.collect(Collectors.toSet());//

			if (!individuals.isEmpty())
				taxonomyNode.putDatum(TaxonomyUtils.INSTANCES_KEY, individuals);
		}

		return taxonomy;
	}

	/**
	 * Loads the taxonomy from a stream
	 *
	 * @param ontologyManager the ontology manager
	 * @param is the stream containing the taxonomy in the form of an ontology
	 * @return the read taxonomy
	 * @throws IOException if an I/O error should occur while reading the taxonomy
	 */
	public static Taxonomy<OWLClass> load(final InputStream is) throws IOException
	{
		try
		{
			final OWLOntology ontology = OWL._manager.loadOntologyFromOntologyDocument(is);

			final Taxonomy<OWLClass> result = createTaxonomy(ontology);

			OWL._manager.removeOntology(ontology);

			return result;
		}
		catch (final OWLOntologyCreationException e)
		{
			_logger.log(Level.SEVERE, "Unable to create the ontology", e);
			throw new IOException("Unable to create the ontology");
		}
	}
}
