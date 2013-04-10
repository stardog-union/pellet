// Copyright (c) 2006 - 2009, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.io;

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

import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.taxonomy.TaxonomyNode;
import org.mindswap.pellet.utils.TaxonomyUtils;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.io.StreamDocumentTarget;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import com.clarkparsia.owlapiv3.OWL;

/**
 * <p>
 * Title: Stores and restores a taxonomy to a stream.
 * </p>
 * <p>
 * Description: Enables storing and reading back a taxonomy from a stream. The taxonomy is first converted into an ontology, and then saved using 
 * the standard OWLRenderers.
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
public class TaxonomyPersistence {

	public static final Logger					log			= Logger.getLogger( TaxonomyPersistence.class.getName() );
	
	/**
	 * The URI of the ontology created to represent the Taxonomy
	 */
	private static IRI	TAXONOMY_ONTOLOGY_IRI	= IRI.create( "http://clarkparsia.com/pellet/modularity/taxonomy" );

	/**
	 * Saves a taxonomy into a stream.
	 * 
	 * @param ontologyManager the ontology manager
	 * @param taxonomy the taxonomy to be saved
	 * @param outputStream the output stream where the ontology should be saved
	 * @throws IOException if an I/O error should occur
	 */
	public static void save( Taxonomy<OWLClass> taxonomy,
			OutputStream outputStream ) throws IOException {
		try {
			OWLOntology ontology = createTaxonomyOntology( taxonomy );

			OWL.manager.saveOntology( ontology, new OWLXMLOntologyFormat(), new StreamDocumentTarget( outputStream) );
			
			outputStream.flush();
			
			OWL.manager.removeOntology( ontology );
		} catch( OWLException e ) {
			log.log( Level.SEVERE, "An error occured while creating an ontology for taxonomy", e );
			throw new IOException( "An error occured while creating an ontology for taxonomy" );
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
	private static OWLOntology createTaxonomyOntology( Taxonomy<OWLClass> taxonomy ) throws OWLOntologyCreationException,
			OWLOntologyChangeException {
		OWLOntology ontology = OWL.Ontology( Collections.<OWLAxiom>emptyList(), TAXONOMY_ONTOLOGY_IRI );

		// populate the ontology
		LinkedList<OWLOntologyChange> changes = new LinkedList<OWLOntologyChange>();
		HashSet<OWLClass> processedEquivalentClasses = new HashSet<OWLClass>();

		for( TaxonomyNode<OWLClass> taxonomyNode : taxonomy.getNodes() ) {
			if ( processedEquivalentClasses.contains( taxonomyNode.getName() ) ) {
				continue;
			}
			
			processedEquivalentClasses.addAll( taxonomyNode.getEquivalents() );			
			
			for( OWLClass owlClass : taxonomyNode.getEquivalents() ) {
				// add the class axiom
				AddAxiom classAxiom = new AddAxiom( ontology, OWL.declaration( owlClass ) );
				changes.add( classAxiom );

				// add the super/subclass axiom between the classes
				for( TaxonomyNode<OWLClass> superNode : taxonomyNode.getSupers() ) {
					//if( superNode == taxonomy.getTop() ) 
					//	continue;
					AddAxiom subClassOfAxiom = new AddAxiom( ontology, 
							OWL.subClassOf( owlClass, superNode.getName() ) );
					changes.add( subClassOfAxiom );
				}
			}

			// add the equivalent classes axiom
			if( taxonomyNode.getEquivalents().size() > 1 ) { 
				AddAxiom equivalentAxiom = new AddAxiom( ontology, 
						OWL.equivalentClasses( taxonomyNode.getEquivalents() ) );
				changes.add( equivalentAxiom );
			}
			
			// save the individuals
			Collection<OWLNamedIndividual> individuals = (Collection<OWLNamedIndividual>) taxonomyNode.getDatum( TaxonomyUtils.INSTANCES_KEY );
			
			if( ( individuals != null ) && ( !individuals.isEmpty() ) ) {
				for( OWLNamedIndividual ind : individuals ) {
					AddAxiom classAssertionAxiom = new AddAxiom( ontology, OWL.classAssertion( ind, taxonomyNode.getName() ) );
					changes.add( classAssertionAxiom );
				}
			}
		}

		OWL.manager.applyChanges( changes );

		return ontology;
	}
	
	/**
	 * Gets all the super classes of the given class in the ontology
	 * @param ontology ontology to be queried
	 * @param owlClass the class whose super classes are to be retrieved
	 * @return a set of super classes
	 */
	private static Set<OWLClass> getSuperClasses( OWLOntology ontology, OWLClass owlClass ) {
		HashSet<OWLClass> superClasses = new HashSet<OWLClass>();
		
		for( OWLSubClassOfAxiom superClassAxiom : ontology.getSubClassAxiomsForSubClass( owlClass ) ) {
			OWLClassExpression owlSuperDescription = superClassAxiom.getSuperClass();
			
			if (owlSuperDescription instanceof OWLClass) {
				superClasses.add( (OWLClass) owlSuperDescription );
			}
		}
		
		return superClasses;
	}
	
	/**
	 * Creates a taxonomy from the ontology.
	 * @param ontology the ontology containing the data which is the source for the taxonomy
	 * @return the created taxonomy
	 */
	private static Taxonomy<OWLClass> createTaxonomy( OWLOntology ontology ) {
		Taxonomy<OWLClass> taxonomy = new Taxonomy<OWLClass>(null, OWL.Thing, OWL.Nothing);
		
		HashSet<OWLClass> processedEquivalentClasses = new HashSet<OWLClass>();
		processedEquivalentClasses.add( OWL.Thing );
		processedEquivalentClasses.add( OWL.Nothing );

		// first create all the nodes in the taxonomy based on classes in the ontology and the equivalence relationships among them
		// (only one node in taxonomy for all the equivalent classes in the group)
		for( OWLClass owlClass : ontology.getClassesInSignature() ) {
			if (processedEquivalentClasses.contains( owlClass )) {
				continue;
			}
			
			HashSet<OWLClass> equivalentClasses = new HashSet<OWLClass>();
			boolean equivalentToThing = false;
			boolean equivalentToNothing = false;
			
			for ( OWLEquivalentClassesAxiom equivalentAxiom : ontology.getEquivalentClassesAxioms( owlClass ) ) {
				equivalentClasses.addAll( equivalentAxiom.getNamedClasses() );
				
				if ( equivalentAxiom.containsOWLNothing() ) {
					equivalentToNothing = true; 
				}
				
				if ( equivalentAxiom.containsOWLThing() ) {
					equivalentToThing = true;
				}
			}
			
			equivalentClasses.removeAll( processedEquivalentClasses );
			
			if ( equivalentToThing ) {
				taxonomy.addEquivalents( OWL.Thing, equivalentClasses );
			} else if ( equivalentToNothing ) {
				taxonomy.addEquivalents( OWL.Nothing, equivalentClasses );
			} else {
			
				if ( equivalentClasses.contains( owlClass ) ) {
					equivalentClasses.remove( owlClass );
				}
			
				taxonomy.addNode( owlClass, false );				
				taxonomy.addEquivalents( owlClass, equivalentClasses );
			}
			
			processedEquivalentClasses.add( owlClass );
			processedEquivalentClasses.addAll( equivalentClasses );
		}
		
		// post process the top and bottom nodes
		for( TaxonomyNode<OWLClass> taxonomyNode : taxonomy.getNodes() ) {
			if ( OWL.Nothing.equals(taxonomyNode.getName() ) && ( taxonomyNode.getSupers().size() > 1 ) && ( taxonomyNode.getSupers().contains( taxonomy.getTop() ) ) ) {
				taxonomy.getTop().removeSub( taxonomyNode );
			}
		}
		
		// after all the nodes are in the taxonomy, create subclass and superclass relationships among them
		for ( TaxonomyNode<OWLClass> taxonomyNode : taxonomy.getNodes() ) {
			OWLClass owlClass = taxonomyNode.getName();
			
			if( owlClass == null || owlClass.equals( OWL.Nothing ) ) 
				continue;			
			
			taxonomy.addSupers( owlClass, getSuperClasses( ontology, owlClass ) );	
		}
		
		// read the instance data (if available)
		for( TaxonomyNode<OWLClass> taxonomyNode : taxonomy.getNodes() ) {
			Set<OWLNamedIndividual> individuals = null;
			
			for( OWLClassAssertionAxiom classAssertionAxiom : ontology.getClassAssertionAxioms( taxonomyNode.getName() ) ) {
				OWLIndividual individual = classAssertionAxiom.getIndividual();
				
				if( individual.isNamed() && ( individual instanceof OWLNamedIndividual ) ) {
					if( individuals == null ) {
						individuals = new HashSet<OWLNamedIndividual>();
					}
						
					individuals.add( (OWLNamedIndividual) individual );
				}
			}
			
			if( individuals != null ) {
				taxonomyNode.putDatum( TaxonomyUtils.INSTANCES_KEY, individuals );
			}
		}

		return taxonomy;
	}

	/**
	 * Loads the taxonomy from a stream
	 * @param ontologyManager the ontology manager
	 * @param is the stream containing the taxonomy in the form of an ontology
	 * @return the read taxonomy
	 * @throws IOException if an I/O error should occur while reading the taxonomy
	 */
	public static Taxonomy<OWLClass> load( InputStream is )
			throws IOException {
		try {
			OWLOntology ontology = OWL.manager.loadOntologyFromOntologyDocument( is );
			
			Taxonomy<OWLClass> result = createTaxonomy( ontology );
			
			OWL.manager.removeOntology( ontology );
			
			return result; 
		} catch( OWLOntologyCreationException e ) {
			log.log( Level.SEVERE, "Unable to create the ontology", e );
			throw new IOException( "Unable to create the ontology" );
		}
	}
}
