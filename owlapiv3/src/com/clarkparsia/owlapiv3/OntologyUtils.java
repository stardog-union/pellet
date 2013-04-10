// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.owlapiv3;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.util.OWLEntityCollector;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: Clark & Parsia, LLC. <http://www.clarkparsia.com></p>
 *
 * @author Evren Sirin
 */
public class OntologyUtils {
	private static OWLOntologyManager	manager		= OWL.manager;

	public static void addAxioms(OWLOntology ontology, Collection<? extends OWLAxiom> axioms) {
		updateOntology( ontology, axioms, true );
	}

	public static void addAxioms(OWLOntology ontology, OWLAxiom... axioms) {
		addAxioms( ontology, Arrays.asList( axioms ) );
	}

	public static boolean containsClass(Set<Set<OWLClass>> classes, OWLClass cls) {
		for( Set<OWLClass> set : classes ) {
			if( set.contains( cls ) )
				return true;
		}

		return false;
	}

	/**
	 * Given a set of OWL-API axiom, return its signature.
	 */
	public static Set<OWLEntity> getSignature(OWLAxiom axiom) {
		Set<OWLEntity> entities = new HashSet<OWLEntity>();
		OWLEntityCollector collector = new OWLEntityCollector(entities);
		collector.setCollectDatatypes( false );
		axiom.accept(collector);

		return entities;
	}
	
	/**
	 * @deprecated Use {@link #createOntology(Collection)} instead
	 */
	public static OWLOntology getOntologyFromAxioms(Collection<OWLAxiom> axioms) {
		return OWL.Ontology( axioms );
	}

	/**
	 * @deprecated Use {@link #createOntology(Collection, IRI)} instead
	 */
	public static OWLOntology getOntologyFromAxioms(Collection<OWLAxiom> axioms, IRI iri) {
		return OWL.Ontology( axioms, iri );
	}

	/**
	 * @deprecated Use {@link #createOntology(OWLAxiom...)} instead
	 */
	public static OWLOntology getOntologyFromAxioms(OWLAxiom... axioms) {
		return OWL.Ontology( Arrays.asList( axioms ) );
	}

	public static OWLOntologyManager getOWLOntologyManager() {
		return manager;
	}
	
	/**
	 * Loads the ontology with given URI.
	 * 
	 * @param uri the ontology uri
	 * @return the ontology
	 */
	public static OWLOntology loadOntology( String uri ) {
		try {
			return manager.loadOntology( IRI.create( uri ) );
		} catch( OWLOntologyCreationException e ) {
			throw new OWLRuntimeException( e );
		}
	}

	/**
	 * Loads the ontology with given URI and optionally removes all annotations
	 * leaving only logical axioms.
	 * 
	 * @see #removeAllAnnotations(OWLOntology, OWLOntologyManager)
	 * @param uri
	 *            the ontology uri
	 * @param withAnnotations
	 *            if <code>false</code> removes all annotation axioms from the
	 *            ontology, otherwise leaves the ontology intact
	 * @return the ontology
	 */
	public static OWLOntology loadOntology( String uri, boolean withAnnotations ) {
		OWLOntology ont = loadOntology( uri );
		
		if( !withAnnotations )
			removeAllAnnotations( ont, manager );
		
		return ont;
	}
	
	/**
	 * Prints a set of axioms to console
	 * 
	 * @param args
	 */
	public static void printAxioms(Collection<? extends OWLAxiom> axioms) {
		for( OWLAxiom axiom : axioms ) 
			System.out.println( axiom );		
	}

	/**
	 * Prints an ontology to console
	 * 
	 * @param args
	 */
	public static void printOntology(OWLOntology ont) {
		printAxioms( ont.getAxioms() );
	}

	public static void removeAxioms(OWLOntology ontology, Collection<? extends OWLAxiom> axioms) {
		updateOntology( ontology, axioms, false );
	}

	public static void removeAxioms(OWLOntology ontology, OWLAxiom... axioms) {
		removeAxioms( ontology, Arrays.asList( axioms ) );
	}

	public static void save(OWLOntology ont, String path) throws OWLOntologyStorageException {
		manager.saveOntology( ont, IRI.create( new File( path ).toURI() ) );
	}

	/**
	 * Update the ontology by adding or removing the given set of axioms
	 * 
	 * @param OWLObject
	 *            axiom - the axiom to add/remove
	 * @param boolean
	 *            add - true - add; false - delete
	 */
	public static void updateOntology(OWLOntology ontology, Collection<? extends OWLAxiom> axioms, boolean add) {
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		for( OWLAxiom axiom : axioms ) {
			OWLOntologyChange change = add
				? new AddAxiom( ontology, axiom )
				: new RemoveAxiom( ontology, axiom );
			changes.add( change );
		}
		manager.applyChanges( changes );
	}

	/**
	 * Determines if a class description contains any unreferenced entities with
	 * respect to the ontology that contains the entailments which are being
	 * explained.
	 * 
	 * @param desc
	 *            The description to be searched
	 * @return <code>true</code> if the description references entities that
	 *         the ontology that contains entailments which are being explained,
	 *         otherwise <code>false</code>
	 */
	public static boolean containsUnreferencedEntity(OWLOntology ontology, OWLClassExpression desc) {
		for( OWLEntity entity : desc.getSignature() ) {
			if( !ontology.containsEntityInSignature( entity ) ) {
				if( entity instanceof OWLClass
						&& (((OWLClass) entity).isOWLThing() || ((OWLClass) entity).isOWLNothing()) ) {
					continue;
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes an axiom from all the given ontologies that contains the axiom
	 * and returns those ontologies.
	 * 
	 * @param axiom
	 *            axiom being removed
	 * @param ontologies
	 *            ontologies from which axiom is being removed
	 * @param manager
	 *            manager to apply the actual change
	 * @return set of ontologies that have been affected
	 */
	public static Set<OWLOntology> removeAxiom(OWLAxiom axiom, Set<OWLOntology> ontologies,
			OWLOntologyManager manager) {
		Set<OWLOntology> modifiedOnts = new HashSet<OWLOntology>();

		for( OWLOntology ont : ontologies ) {
			if( ont.getAxioms().contains( axiom ) ) {
				modifiedOnts.add( ont );

				manager.applyChange( new RemoveAxiom( ont, axiom ) );
			}
		}

		return modifiedOnts;
	}

	/**
	 * Add the axiom to all the given ontologies.
	 * 
	 * @param axiom
	 * @param ontologies
	 * @param manager
	 */
	public static void addAxiom(OWLAxiom axiom, Set<OWLOntology> ontologies,
			OWLOntologyManager manager) {
		for( OWLOntology ont : ontologies ) {
			manager.applyChange( new AddAxiom( ont, axiom ) );
		}
	}
	
	/**
	 * Removes all annotations (non-logical axioms) from the ontology causing
	 * the ontology to be changed in an unreversible way. For any entity that is
	 * only referenced in an annotation but no logical axiom a declaration is
	 * added so that the referenced entities by the ontology remain same.
	 * Annotations have no semantic importance and can be ignored for reasoning
	 * purposes including generating explanations and computing modules.
	 * Removing them from the ontology completely reduces the memory
	 * requirements which is very high for large-scale annotation-heavy
	 * ontologies.
	 * 
	 * @param ontology
	 *            the ontology being changed
	 */
	public static void removeAllAnnotations(OWLOntology ontology, OWLOntologyManager manager) {
		try {
			Set<OWLEntity> referencedEntities = new HashSet<OWLEntity>();
			referencedEntities.addAll( ontology.getClassesInSignature() );
			referencedEntities.addAll( ontology.getObjectPropertiesInSignature() );
			referencedEntities.addAll( ontology.getDataPropertiesInSignature() );
			referencedEntities.addAll( ontology.getIndividualsInSignature() );

			List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
			for( OWLAxiom axiom : ontology.getAxioms() ) {
				if( !axiom.isLogicalAxiom() ) {
					changes.add( new RemoveAxiom( ontology, axiom ) );
				}
			}

			manager.applyChanges( changes ); 

			changes = new ArrayList<OWLOntologyChange>();
			for( OWLEntity entity : referencedEntities ) {
				if( !ontology.containsEntityInSignature( entity ) ) {
					OWLDeclarationAxiom declaration = manager.getOWLDataFactory()
							.getOWLDeclarationAxiom( entity );
					changes.add( new AddAxiom( ontology, declaration ) );
				}
			}
			
			manager.applyChanges( changes );
		} catch( OWLOntologyChangeException e ) {
			throw new OWLRuntimeException( e );			
		} 
	}

	/**
	 * Finds an entity (class, individual, object or data property) in the given
	 * set of ontologies that has the given local name or URI.
	 * 
	 * @see #findEntity(String, OWLOntology)
	 * @param name
	 *            URI or local name for an entity
	 * @param ontologies
	 *            ontologies we are searching
	 * @return an entity referenced in the given ontology that has the given URI
	 *         or local name	 */
	public static OWLEntity findEntity(String name, Set<OWLOntology> ontologies) {
		OWLEntity entity = null;
		for( OWLOntology ontology : ontologies ) {
			if( (entity = findEntity( name, ontology )) != null )
				break;
		}
		return entity;
	}

	/**
	 * Finds an entity (class, individual, object or data property) in the given
	 * ontology that has the given local name or URI. If the given name is not
	 * an absolute URI we use the logical URI of the ontology as the namespace
	 * and search for an entity with that URI. If the URI is punned in the
	 * ontology , e.g. used both as a class and as an individual, any one of the
	 * punned entities may be returned.
	 * 
	 * @param name
	 *            URI or local name for an entity
	 * @param ontology
	 *            ontology we are searching
	 * @return an entity referenced in the given ontology that has the given URI
	 *         or local name
	 */
	public static OWLEntity findEntity(String name, OWLOntology ontology) {
		OWLEntity entity = null;

		if( name.equals( "owl:Thing" ) )
			entity = OWL.Thing;
		else if( name.equals( "owl:Nothing" ) )
			entity = OWL.Nothing;
		else {
			IRI iri = IRI.create( name );

			if( iri == null ) {
				throw new RuntimeException( "Invalid IRI: " + iri );
			}

			if( !iri.isAbsolute() ) {
				IRI baseIRI = ontology.getOntologyID().getOntologyIRI();
				if( baseIRI != null )
					iri = baseIRI.resolve( "#" + iri );
			}

			if( ontology.containsClassInSignature( iri ) )
				entity = OWL.Class( iri );
			else if( ontology.containsObjectPropertyInSignature( iri ) )
				entity = OWL.ObjectProperty( iri );
			else if( ontology.containsDataPropertyInSignature( iri ) )
				entity = OWL.DataProperty( iri );
			else if( ontology.containsIndividualInSignature( iri ) )
				entity = OWL.Individual( iri ).asOWLNamedIndividual();
		}

		return entity;
	}
}
