// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.clarkparsia.modularity.io.ModuleExtractorPersistence;
import com.clarkparsia.modularity.io.UncloseableOutputStream;
import com.clarkparsia.owlapi.modularity.locality.LocalityClass;
import com.clarkparsia.owlapi.modularity.locality.SyntacticLocalityEvaluator;
import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.owlapiv3.OntologyUtils;
import com.clarkparsia.pellet.expressivity.Expressivity;
import com.clarkparsia.pellet.utils.DeltaMap;
import com.clarkparsia.pellet.utils.DeltaSet;
import com.clarkparsia.pellet.utils.MultiMapUtils;
import org.mindswap.pellet.KnowledgeBase.ChangeType;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.taxonomy.TaxonomyNode;
import org.mindswap.pellet.utils.MultiValueMap;
import org.mindswap.pellet.utils.Timer;
import org.mindswap.pellet.utils.Timers;
import org.mindswap.pellet.utils.progress.ConsoleProgressMonitor;
import org.mindswap.pellet.utils.progress.ProgressMonitor;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public abstract class AbstractModuleExtractor implements ModuleExtractor {
	public static final Logger log = Logger.getLogger(AbstractModuleExtractor.class.getName());

	private final Set<OWLAxiom> additions = new HashSet<OWLAxiom>();

	private final Set<OWLClass> newClasses = new HashSet<OWLClass>();

	/**
	 * Map to find entities referenced in an axiom
	 */
	private final Set<OWLAxiom> axioms;

	/**
	 * Set of axioms that will be deleted
	 */
	private final Set<OWLAxiom> deletions = new HashSet<OWLAxiom>();

	/**
	 * The types of changes that are pending in additions and deletions
	 */
	protected final EnumSet<ChangeType> changes = EnumSet.noneOf(ChangeType.class);

	/**
	 * Map to find axioms that references an axiom
	 */
	protected final Map<OWLEntity, Set<OWLAxiom>> entityAxioms;

	private final LocalityClass localityClass;

	private final SyntacticLocalityEvaluator localityEvaluator;

	protected Map<OWLEntity, Set<OWLEntity>> modules;

	/**
	 * Flag to check if a non-local axiom has been updated
	 */
	private boolean nonLocalAxioms = false;

	private final Timers timers = new Timers();

	public AbstractModuleExtractor() {
		this(LocalityClass.BOTTOM_BOTTOM);
	}

	public AbstractModuleExtractor(LocalityClass localityClass) {
		this.localityClass = localityClass;
		localityEvaluator = new SyntacticLocalityEvaluator(localityClass);
		axioms = new HashSet<OWLAxiom>();
		entityAxioms = new HashMap<OWLEntity, Set<OWLAxiom>>();
		modules = null;
	}

	protected AbstractModuleExtractor(AbstractModuleExtractor extractor) {
		this.localityClass = extractor.localityClass;
		localityEvaluator = new SyntacticLocalityEvaluator(localityClass);
		axioms = new DeltaSet<OWLAxiom>(extractor.axioms);
		entityAxioms = new DeltaMap<OWLEntity, Set<OWLAxiom>>(extractor.entityAxioms);
		modules = extractor.modules == null ? null : new DeltaMap<OWLEntity, Set<OWLEntity>>(extractor.modules);
	}

	public void addAxiom(OWLAxiom axiom) {
		if( !axiom.isLogicalAxiom() )
			return;

		axiom = axiom.getAxiomWithoutAnnotations();

		if( axioms.contains( axiom ) )
			return;

		checkNonLocalAxiom( axiom );

		if( log.isLoggable( Level.FINE ) )
			log.fine( "Adding " + axiom );

		deletions.remove( axiom );
		additions.add( axiom );
		categorizeAddedAxiom( axiom );
	}

	/**
	 * Returns if the extracted modules can be updated. We can update the
	 * modules if we have computed modules and no non-local axiom has been added
	 * or deleted.
	 *
	 * @return
	 */
	public boolean canUpdate() {
		return modules != null && !nonLocalAxioms;
	}

	/**
	 * Checks if the given axiom is non-local w.r.t. empty signature and updates
	 * the {@link #nonLocalAxioms} field.
	 *
	 * @param axiom -
	 *            Axiom to be checked
	 */
	private void checkNonLocalAxiom(OWLAxiom axiom) {
		// no need to check for non-locals if we already know that we cannot
		// update modules
		if( canUpdate() ) {
			if( !isLocal( axiom, Collections.<OWLEntity>emptySet() ) ) {
				log.warning( "*** Non-local axiom: " + axiom );
				nonLocalAxioms = true;
			}
		}
	}

	public void deleteAxiom(OWLAxiom axiom) {
		if( !axiom.isLogicalAxiom() )
			return;

		axiom = axiom.getAxiomWithoutAnnotations();

		checkNonLocalAxiom( axiom );

		if( !axioms.contains(axiom) ) {
			if( additions.remove( axiom ) ) {
				if( log.isLoggable( Level.FINE ) )
					log.fine( "Deleted axiom from add queue before processing " + axiom );
			}
			return;
		}

		if( log.isLoggable( Level.FINE ) )
			log.fine( "Deleting " + axiom );

		additions.remove( axiom );
		deletions.add( axiom );
		categorizeRemovedAxiom( axiom );
	}

	/**
	 * Extract modules from scratch
	 *
	 * @return
	 */
	public void extractModules() {
		Timer timer = timers.startTimer( "extractModules" );

		// cache the axiom signatures
		processAdditions();
		additions.clear();

		// no need to consider deletions for initial module extraction
		deletions.clear();
		changes.clear();

		nonLocalAxioms = false;

		modules = new HashMap<OWLEntity, Set<OWLEntity>>();

		extractModuleSignatures(entityAxioms.keySet());

		timer.stop();
	}

	/**
	 * This is a main method to extract the signature for a set of classes Note
	 * that this method updates the modules for the classes which are maintained
	 * (either newly created or already existing) in a module partial order
	 *
	 * @param entities -
	 *            the set of entities whose modules should be extracted
	 */
	private void extractModuleSignatures(Set<? extends OWLEntity> entities) {
		if (log.isLoggable(Level.FINE)) {
			log.fine("Extracting module for each of " + entities);
		}

		if (entities.isEmpty()) {
			return;
		}

		ProgressMonitor monitor = new ConsoleProgressMonitor();
		monitor.setProgressTitle( "Extracting" );
		monitor.setProgressLength( entities.size() );
		monitor.taskStarted();

		extractModuleSignatures(entities, monitor);

		monitor.taskFinished();

		if (log.isLoggable(Level.FINER)) {
			log.finer("Modules: " + modules);
		}
	}

	protected abstract void extractModuleSignatures(Set<? extends OWLEntity> entities, ProgressMonitor monitor);


		/**
		 * Given an axiom, this function locates all root nodes in the partial order
		 * that are affected by the update
		 *
		 * @param axiom -
		 *            the update
		 * @param add -
		 *            Flag for additions/deletions
		 */
	private Set<OWLEntity> getAffectedRoots(OWLAxiom axiom, Taxonomy<OWLClass> taxonomy, boolean add) {
		Set<OWLEntity> roots = new HashSet<OWLEntity>();
		Set<TaxonomyNode<OWLClass>> visited = new HashSet<TaxonomyNode<OWLClass>>();
		visited.add( taxonomy.getBottom() );
		getAffectedRoots( axiom, taxonomy.getTop(), roots, add, visited );

		/*
		 * Special case when the only node affected by a deletion is
		 * unsatisfiable
		 */
		if( !add && roots.isEmpty() ) {
			for( OWLClass unsat : taxonomy.getEquivalents(OWL.Nothing) ) {
				Set<OWLEntity> signature = modules.get(unsat);

				if( (signature != null) && signature.containsAll(axiom.getSignature()) )
					roots.add( unsat );
			}
		}

		return roots;
	}

	/**
	 * Given an axiom, this function locates all root nodes in the partial order
	 * that are affected by the update
	 *
	 * @param axiom -
	 *            the update
	 * @param node -
	 *            the next node
	 * @param effects -
	 *            the actual set of affected nodes collected
	 * @param add -
	 *            Flag for additions/deletions
	 * @param visited -
	 *            nodes visited so far
	 */
	private void getAffectedRoots(OWLAxiom axiom, TaxonomyNode<OWLClass> node,
			Set<OWLEntity> effects, boolean add, Set<TaxonomyNode<OWLClass>> visited) {

		// only proceed if not seen this node
		if( visited.contains( node ) )
			return;
		else
			visited.add( node );

		OWLEntity entity = node.getName();

		// get the sig for this module
		Set<OWLEntity> signature = modules.get(entity);

		boolean outdated = false;

		// check if the entity has been removed due to a deletion
		if( signature == null ) {
			if( log.isLoggable( Level.FINE ) )
				log.fine( "Removed entity " + entity );
		}
		else if( add ) {
			// only affected if axiom is non-local w.r.t. the sig of the
			// module
			outdated = !isLocal( axiom, signature );
		}
		else {
			// only affected if sig of axiom is contained in sig of module
			outdated = signature.containsAll(axiom.getSignature());
		}

		// if outdated add to effected set
		if( outdated ) {
			effects.addAll( node.getEquivalents() );
		}
		else {
			// recursive call to children
			for( TaxonomyNode<OWLClass> next : node.getSubs() ) {
				getAffectedRoots( axiom, next, effects, add, visited );
			}
		}
	}

	/**
	 * Return the axioms which references this entity
	 *
	 * @param entity
	 * @return
	 */
	public Set<OWLAxiom> getAxioms(OWLEntity entity) {
		return MultiMapUtils.get(entityAxioms, entity);
	}

	@Override
	public Set<OWLEntity> getModuleEntities(OWLEntity entity) {
		return modules.get(entity);
	}

	protected Set<OWLAxiom> getModuleAxioms(Set<OWLEntity> signature) {
		Set<OWLEntity> referenced = new HashSet<OWLEntity>();

		Set<OWLEntity> augmentedSig = new HashSet<OWLEntity>( signature );
		augmentedSig.add( OWL.Thing );
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

		Set<OWLAxiom> candidates = new HashSet<OWLAxiom>();
		for( OWLEntity e : signature ) {
			candidates.addAll( getAxioms( e ) );
		}

		for( OWLAxiom axiom : candidates ) {
			Set<OWLEntity> sigAxiom = axiom.getSignature();

			/*
			 * An axiom is in the module of the signature if the augmented
			 * signature contains all the entities referenced in the axiom.
			 * However, there are cases this necessary condition is not
			 * sufficient. For example, an axiom may be a tautology, e.g.
			 * PropertyDomain(p owl:Thing), and if this axiom is included the
			 * module would not be minimal anymore. The locality check we
			 * perform for the axiom w.r.t. the given signature filters these
			 * axioms.
			 */
			if( augmentedSig.containsAll( sigAxiom ) && !isLocal( axiom, signature ) ) {
				axioms.add( axiom );
				referenced.addAll( sigAxiom );
			}
		}

		/*
		 * Special handling is required if an entity is in the input signature,
		 * and is referenced by some axioms, but all those axioms are local to
		 * the signature and contain entities not in the signature. A
		 * declaration axiom is used to keep the entity in the module.
		 */
		Set<OWLEntity> notReferenced = new HashSet<OWLEntity>( signature );
		notReferenced.removeAll( referenced );
		for( OWLEntity e : notReferenced ) {
			if( entityAxioms.get( e ) != null )
				axioms.add( OWL.declaration( e ) );
		}

		return axioms;
	}

	/**
	 * Returns a new ontology that contains the axioms that are in the module
	 * for given set of entities
	 *
	 * @param signature
	 * @return
	 * @throws OWLException
	 */
	public OWLOntology getModuleFromSignature(Set<OWLEntity> signature) {
		Set<OWLAxiom> moduleAxioms = getModuleAxioms( signature );
		return OWL.Ontology(moduleAxioms);
	}

	/**
	 * Checks if axioms have been added/removed and modules need to be updated
	 *
	 * @return <code>true</code> if axioms have been added/removed
	 */
	public boolean isChanged() {
		return !additions.isEmpty() || !deletions.isEmpty() || nonLocalAxioms;
	}

	protected boolean isLocal(OWLAxiom axiom, Set<OWLEntity> signature) {
		return localityEvaluator.isLocal( axiom, signature );
	}

	private void processAdditions() {
		for( OWLAxiom axiom : additions ) {
			axioms.add( axiom );

			for( OWLEntity entity : axiom.getSignature() ) {
				MultiMapUtils.add(entityAxioms, entity, axiom);

				if( entity instanceof OWLClass ) {
					OWLClass cls = (OWLClass) entity;
					if (modules != null && !modules.containsKey(cls)) {
						newClasses.add(cls);
					}
				}
			}
		}
	}

	private void processDeletions() {
		for( OWLAxiom axiom : deletions ) {
			axioms.remove( axiom );

			for( OWLEntity entity : axiom.getSignature() ) {
				MultiMapUtils.remove(entityAxioms, entity, axiom);

				if( !entityAxioms.containsKey( entity ) ) {
					if( log.isLoggable( Level.FINE ) )
						log.fine( "Remove " + entity + " which is not mentioned anymore" );
					modules.remove( entity );
				}
			}
		}
	}

	/**
	 * Method to 1) find affected modules and 2) update them - that is extract
	 * their new signatures
	 *
	 * @param effects affected entities
	 * @param taxonomy classification hierarchy
	 * @param add Flag for additions/deletions
	 */
	private void updateEffectedModules(Set<OWLEntity> effects, Taxonomy<OWLClass> taxonomy, boolean add) {
		// affected root nodes in order
		Set<OWLEntity> affectedRoots = new HashSet<OWLEntity>();
		// Set of all nodes affected
		Set<OWLEntity> affected = new HashSet<OWLEntity>();

		if( log.isLoggable( Level.FINE ) )
			log.fine( "Update modules for " + (add
				? "additions"
				: "deletions") );

		// any new classes are affected
		affectedRoots.addAll(newClasses);

		// iterate over all axioms and get find the set of root nodes
		// affected by the update
		Set<OWLAxiom> axioms = (add ? additions : deletions);
		for( OWLAxiom axiom : axioms ) {
			// find affected roots - recursive function
			affectedRoots.addAll( getAffectedRoots( axiom, taxonomy, add ) );
		}

		if( log.isLoggable( Level.FINE ) )
			log.fine( "Affected roots " + affectedRoots );

		// given root, get all affected objects
		for( OWLEntity nextRoot : affectedRoots ) {
			// add root to affected
			affected.add( nextRoot );

			if( nextRoot instanceof OWLClass ) {
				// collect all the descendants of this class
				if( taxonomy.contains( (OWLClass) nextRoot ) )
					affected.addAll( taxonomy.getFlattenedSubs( (OWLClass) nextRoot, false ) );
			}
		}

		if( log.isLoggable( Level.FINE ) )
			log.fine( "Affected entities " + affected );

		for( OWLEntity entity : affected )
			modules.remove( entity );

		// Next update mods of all affected nodes
		extractModuleSignatures( affected );

		for( OWLEntity entity : affected ) {
			Set<OWLEntity> module = modules.get(entity);
			if( module == null ) {
				String msg = "No module for " + entity;
				log.log( Level.SEVERE,  msg, new RuntimeException( msg ) );
			}

			effects.addAll( module );
		}
	}

	@Override
	public Set<OWLEntity> applyChanges(final Taxonomy<OWLClass> taxonomy) throws UnsupportedOperationException {
		Timer timer = timers.startTimer( "updateModules" );

		if( !canUpdate() )
			throw new UnsupportedOperationException( "Modules cannot be updated!" );

		// Set of all entities in the module of affected entities
		Set<OWLEntity> effects = new HashSet<OWLEntity>();

		// cash the signatures for axioms as they are used in the next step
		processAdditions();
		// compute effects
		updateEffectedModules( effects, taxonomy, true );

		updateEffectedModules( effects, taxonomy, false );
		// remove signatures for deleted axioms now that they are not needed
		processDeletions();

		// clear processed axioms
		additions.clear();
		deletions.clear();

		// clear the pending change types
		changes.clear();

		// clear new classes as well
		newClasses.clear();

		timer.stop();

		return effects;
	}

	public Timers getTimers() {
		return timers;
	}

	public Set<OWLAxiom> getAxioms() {
		return Collections.unmodifiableSet( axioms );
	}

	public Set<OWLEntity> getEntities() {
		return Collections.unmodifiableSet( entityAxioms.keySet() );
	}

	public void resetModules() {
		// cache the axiom signatures
		processAdditions();
		additions.clear();

		// no need to consider deletions for initial module extraction
		deletions.clear();
		changes.clear();

		nonLocalAxioms = false;

		modules = new MultiValueMap<OWLEntity, OWLEntity>();
	}

	/**
	 * @inheritDoc
	 */
	public boolean isClassificationNeeded(Expressivity expressivity) {
		return
			isTBoxChanged()
			// RBox did not change since classification
			|| isRBoxChanged()
			// there are no nominals
			|| (expressivity.hasNominal() && !PelletOptions.USE_PSEUDO_NOMINALS);

	}

	/**
	 * Checks whether there are unapplied changes to the TBox
	 *
	 * @return true if there are unapplied changes to TBox
	 */
	public boolean isTBoxChanged() {
		return changes.contains( ChangeType.TBOX_ADD ) || changes.contains( ChangeType.TBOX_DEL );
	}

	/**
	 * Checks whether there are unapplied changes to the RBox
	 *
	 * @return true if there are unapplied changes to RBox
	 */
	public boolean isRBoxChanged() {
		return changes.contains( ChangeType.RBOX_ADD ) || changes.contains( ChangeType.RBOX_DEL );
	}

	/**
	 * Checks whether there are unapplied changes to the ABox
	 *
	 * @return true if there are unapplied changes to ABox
	 */
	public boolean isABoxChanged() {
		return changes.contains( ChangeType.ABOX_ADD ) || changes.contains( ChangeType.ABOX_DEL );
	}

	/**
	 * Checks the category of the change the addition of the axiom introduces
	 * (i.e., change to a TBox, RBox, or ABox), and updates the changes set appropriately.
	 *
	 * @param axiom the axiom being added
	 */
	private void categorizeAddedAxiom(OWLAxiom axiom) {
		if (ChangeTypeDetector.isTBoxAxiom(axiom)) {
			changes.add(ChangeType.TBOX_ADD);
		}
		else if (ChangeTypeDetector.isRBoxAxiom(axiom)) {
			changes.add(ChangeType.RBOX_ADD);
		}
		else if (ChangeTypeDetector.isABoxAxiom(axiom)) {
			changes.add(ChangeType.ABOX_ADD);
		}
	}

	/**
	 * Checks the category of the change the deletion of the axiom introduces
	 * (i.e., change to a TBox, RBox, or ABox), and updates the changes set appropriately.
	 *
	 * @param axiom the axiom being removed
	 */
	private void categorizeRemovedAxiom(OWLAxiom axiom) {
		if (ChangeTypeDetector.isTBoxAxiom(axiom)) {
			changes.add(ChangeType.TBOX_DEL);
		}
		else if (ChangeTypeDetector.isRBoxAxiom(axiom)) {
			changes.add(ChangeType.RBOX_DEL);
		}
		else if (ChangeTypeDetector.isABoxAxiom(axiom)) {
			changes.add(ChangeType.ABOX_DEL);
		}
	}

	// I/O code to persist the state of the AbstractModuleExtractor

	/**
	 * The name of the entry in the zip file that stores axioms
	 */
	private static final String MODULE_EXTRACTOR_AXIOMS_FILE_NAME = "ModuleExtractorAxioms";

	/**
	 * The name of the entry in the zip file that stores the module information
	 */
	private static final String MODULE_EXTRACTOR_MODULES_FILE_NAME = "ModuleExtractorModules";

	/**
	 * @inheritDoc
	 */
	public void save(ZipOutputStream outputStream) throws IOException, IllegalStateException {
		if ( !additions.isEmpty() || !deletions.isEmpty() ) {
			throw new IllegalStateException( "The module extractor contains unapplied changes to the modules, and therefore cannot be saved." );
		}

		// first save the axioms
		ZipEntry axiomsEntry = new ZipEntry( MODULE_EXTRACTOR_AXIOMS_FILE_NAME );
		outputStream.putNextEntry( axiomsEntry );

		ModuleExtractorPersistence.saveAxioms( axioms, new UncloseableOutputStream( outputStream ) );

		// next save the modules
		ZipEntry modulesEntry = new ZipEntry( MODULE_EXTRACTOR_MODULES_FILE_NAME );
		outputStream.putNextEntry( modulesEntry );

		ModuleExtractorPersistence.saveModules( modules, new UncloseableOutputStream( outputStream ) );

		outputStream.flush();
	}

	/**
	 * @inheritDoc
	 */
	public void load(ZipInputStream inputStream) throws IOException, IllegalArgumentException {
		resetModules();

		ZipEntry zipEntry = inputStream.getNextEntry();

		if( !( MODULE_EXTRACTOR_AXIOMS_FILE_NAME.equals( zipEntry.getName() ) ) ) {
			throw new IllegalArgumentException( String.format( "Unexpected entry (%s) in ZipInputStream. Expected %s", zipEntry.getName(), MODULE_EXTRACTOR_AXIOMS_FILE_NAME ) );
		}

		Collection<OWLAxiom> axioms = OntologyUtils.loadAxioms(inputStream);

		modules = null;
		additions.addAll(axioms);
		processAdditions();
		additions.clear();

		zipEntry = inputStream.getNextEntry();

		if( !( MODULE_EXTRACTOR_MODULES_FILE_NAME.equals( zipEntry.getName() ) ) ) {
			throw new IllegalArgumentException( String.format( "Unexpected entry (%s) in ZipInputStream. Expected %s", zipEntry.getName(), MODULE_EXTRACTOR_MODULES_FILE_NAME ) );
		}

		modules = ModuleExtractorPersistence.loadModules( inputStream );
	}
}
