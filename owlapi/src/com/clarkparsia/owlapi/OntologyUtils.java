// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.owlapi;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDeclarationAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLOntologyStorageException;
import org.semanticweb.owl.model.OWLRuntimeException;
import org.semanticweb.owl.model.RemoveAxiom;
import org.semanticweb.owl.model.UnknownOWLOntologyException;
import org.semanticweb.owl.util.OWLEntityCollector;

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
	private static OWLEntityCollector	collector	= new OWLEntityCollector();
	static {
		collector.setCollectDataTypes( false );
	}

	public static void addAxioms(OWLOntology ontology, Collection<? extends OWLAxiom> axioms)
			throws OWLException {
		updateOntology( ontology, axioms, true );
	}

	public static void addAxioms(OWLOntology ontology, OWLAxiom... axioms) throws OWLException {
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
		collector.reset();

		axiom.accept( collector );

		return new HashSet<OWLEntity>( collector.getObjects() );
	}

	public static OWLOntology getOntologyFromAxioms(Collection<OWLAxiom> axioms) {
		URI uri = URI.create( "http://www.example.org/ontology" + new UID() );

		return getOntologyFromAxioms( axioms, uri );
	}

	public static OWLOntology getOntologyFromAxioms(Collection<OWLAxiom> axioms, URI uri) {
		OWLOntology module;
		try {
			module = manager.createOntology( uri );
			List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
			for( OWLAxiom axiom : axioms ) {
				changes.add( new AddAxiom( module, axiom ) );
			}
			manager.applyChanges( changes );
		} catch( OWLOntologyCreationException e ) {
			throw new RuntimeException( e );
		} catch( OWLOntologyChangeException e ) {
			throw new RuntimeException( e );
		}

		return module;
	}

	public static OWLOntology getOntologyFromAxioms(OWLAxiom... axioms) {
		return getOntologyFromAxioms( Arrays.asList( axioms ) );
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
			return manager.loadOntology( URI.create( uri ) );
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

	public static void removeAxioms(OWLOntology ontology, Collection<? extends OWLAxiom> axioms)
			throws OWLException {
		updateOntology( ontology, axioms, false );
	}

	public static void removeAxioms(OWLOntology ontology, OWLAxiom... axioms) throws OWLException {
		removeAxioms( ontology, Arrays.asList( axioms ) );
	}

	public static void save(OWLOntology ont, String path) throws UnknownOWLOntologyException,
			OWLOntologyStorageException {
		manager.saveOntology( ont, new File( path ).toURI() );
	}

	/**
	 * Update the ontology by adding or removing the given set of axioms
	 * 
	 * @param OWLObject
	 *            axiom - the axiom to add/remove
	 * @param boolean
	 *            add - true - add; false - delete
	 * @throws OWLException
	 */
	public static void updateOntology(OWLOntology ontology, Collection<? extends OWLAxiom> axioms, boolean add)
			throws OWLException {
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
	public static boolean containsUnreferencedEntity(OWLOntology ontology, OWLDescription desc) {
		OWLEntityCollector entityCollector = new OWLEntityCollector();
		desc.accept( entityCollector );
		for( OWLEntity entity : entityCollector.getObjects() ) {
			if( !ontology.containsEntityReference( entity ) ) {
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
			OWLOntologyManager manager) throws OWLOntologyChangeException {
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
	 * @throws OWLOntologyChangeException
	 */
	public static void addAxiom(OWLAxiom axiom, Set<OWLOntology> ontologies,
			OWLOntologyManager manager) throws OWLOntologyChangeException {
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
	 * @throws OWLOntologyChangeException
	 */
	public static void removeAllAnnotations(OWLOntology ontology, OWLOntologyManager manager) {
		try {
			Set<OWLEntity> referencedEntities = new HashSet<OWLEntity>();
			referencedEntities.addAll( ontology.getReferencedClasses() );
			referencedEntities.addAll( ontology.getReferencedObjectProperties() );
			referencedEntities.addAll( ontology.getReferencedDataProperties() );
			referencedEntities.addAll( ontology.getReferencedIndividuals() );

			List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
			for( OWLAxiom axiom : ontology.getAxioms() ) {
				if( !axiom.isLogicalAxiom() ) {
					changes.add( new RemoveAxiom( ontology, axiom ) );
				}
			}

			manager.applyChanges( changes ); 

			changes = new ArrayList<OWLOntologyChange>();
			for( OWLEntity entity : referencedEntities ) {
				if( !ontology.containsEntityReference( entity ) ) {
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
			URI uri = URI.create( name );

			if( uri == null )
			try {
				uri = new URI( name );
			} catch( URISyntaxException e ) {
				throw new RuntimeException( e );
			}

			if( !uri.isAbsolute() ) {
				URI baseURI = ontology.getURI();
				uri = baseURI.resolve( "#" + uri );
			}

			if( ontology.containsClassReference( uri ) )
				entity = OWL.Class( uri );
			else if( ontology.containsObjectPropertyReference( uri ) )
				entity = OWL.ObjectProperty( uri );
			else if( ontology.containsDataPropertyReference( uri ) )
				entity = OWL.DataProperty( uri );
			else if( ontology.containsIndividualReference( uri ) )
				entity = OWL.Individual( uri );
		}

		return entity;
	}
}
