// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity;

import com.clarkparsia.modularity.io.ModuleExtractorPersistence;
import com.clarkparsia.modularity.io.UncloseableOutputStream;
import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.owlapi.modularity.locality.LocalityClass;
import com.clarkparsia.owlapi.modularity.locality.LocalityEvaluator;
import com.clarkparsia.owlapi.modularity.locality.SyntacticLocalityEvaluator;
import com.clarkparsia.pellet.expressivity.Expressivity;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import net.katk.tools.Log;
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
public abstract class AbstractModuleExtractor implements ModuleExtractor
{
	public static final Logger _logger = Log.getLogger(AbstractModuleExtractor.class);

	private final Set<OWLAxiom> _additions = new HashSet<>();

	private final Set<OWLClass> _newClasses = new HashSet<>();

	/**
	 * Map to find entities referenced in an axiom
	 */
	private final Set<OWLAxiom> _axioms = new HashSet<>();

	/**
	 * Set of _axioms that will be deleted
	 */
	private final Set<OWLAxiom> _deletions = new HashSet<>();

	/**
	 * The types of changes that are pending in additions and deletions
	 */
	protected EnumSet<ChangeType> _changes = EnumSet.noneOf(ChangeType.class);

	/**
	 * Map to find _axioms that references an axiom
	 */
	protected MultiValueMap<OWLEntity, OWLAxiom> _entityAxioms = new MultiValueMap<>();

	private LocalityEvaluator _localityEvaluator = null;

	protected MultiValueMap<OWLEntity, OWLEntity> _modules = null;

	/**
	 * Flag to check if a non-local axiom has been updated
	 */
	private boolean _nonLocalAxioms = false;

	private final Timers _timers = new Timers();

	public AbstractModuleExtractor()
	{
		this(new SyntacticLocalityEvaluator(LocalityClass.BOTTOM_BOTTOM));
	}

	public AbstractModuleExtractor(final LocalityEvaluator localityEvaluator)
	{
		_localityEvaluator = localityEvaluator;
	}

	@Override
	public void addAxiom(final OWLAxiom axiom)
	{
		checkNonLocalAxiom(axiom);

		if (_axioms.contains(axiom))
			return;

		_logger.fine(() -> "Adding " + axiom);

		_deletions.remove(axiom);
		_additions.add(axiom);
		categorizeAddedAxiom(axiom);
	}

	/**
	 * Returns if the extracted modules can be updated. We can update the modules if we have computed modules and no non-local axiom has been added or deleted.
	 *
	 * @return
	 */
	@Override
	public boolean canUpdate()
	{
		return _modules != null && !_nonLocalAxioms;
	}

	/**
	 * Checks if the given axiom is non-local w.r.t. empty signature and updates the {@link #_nonLocalAxioms} field.
	 *
	 * @param axiom - Axiom to be checked
	 */
	private void checkNonLocalAxiom(final OWLAxiom axiom)
	{

		if (!axiom.isLogicalAxiom())
			return;

		// no need to check for non-locals if we already know that we cannot
		// update modules
		if (canUpdate())
			if (!isLocal(axiom, Collections.<OWLEntity> emptySet()))
			{
				_logger.warning("*** Non-local axiom: " + axiom);
				_nonLocalAxioms = true;
			}
	}

	@Override
	public void deleteAxiom(final OWLAxiom axiom)
	{
		checkNonLocalAxiom(axiom);

		if (!_axioms.contains(axiom))
		{
			if (_additions.remove(axiom))
				_logger.fine(() -> "Deleted axiom from add _queue before processing " + axiom);
			return;
		}

		_logger.fine(() -> "Deleting " + axiom);

		_additions.remove(axiom);
		_deletions.add(axiom);
		categorizeRemovedAxiom(axiom);
	}

	@Override
	public MultiValueMap<OWLEntity, OWLEntity> getModules()
	{
		return _modules;
	}

	/**
	 * Extract modules from scratch
	 *
	 * @return
	 */
	@Override
	public MultiValueMap<OWLEntity, OWLEntity> extractModules()
	{
		final Timer timer = _timers.startTimer("extractModules");

		// _cache the axiom signatures
		processAdditions();
		_additions.clear();

		// no need to consider _deletions for initial module extraction
		_deletions.clear();
		_changes.clear();

		_nonLocalAxioms = false;

		_modules = new MultiValueMap<>();

		extractModuleSignatures(_entityAxioms.keySet());

		timer.stop();

		return _modules;
	}

	/**
	 * This is a main method to extract the signature for a set of classes Note that this method updates the modules for the classes which are maintained
	 * (either newly created or already existing) in a module partial _order
	 *
	 * @param entities - the set of entities whose modules should be extracted
	 */
	private void extractModuleSignatures(final Set<? extends OWLEntity> entities)
	{
		_logger.fine(() -> "Extracting module for each of " + entities);

		if (entities.isEmpty())
			return;

		final ProgressMonitor monitor = new ConsoleProgressMonitor();
		monitor.setProgressTitle("Extracting");
		monitor.setProgressLength(entities.size());
		//monitor.setProgressLength(10);
		monitor.taskStarted();

		extractModuleSignatures(entities, monitor);

		monitor.taskFinished();

		_logger.finer(() -> "Modules: " + _modules);
	}

	protected abstract void extractModuleSignatures(Set<? extends OWLEntity> entities, ProgressMonitor monitor);

	/**
	 * Given an axiom, this function locates all root _nodes in the partial _order that are affected by the update
	 *
	 * @param axiom - the update
	 * @param add - Flag for _additions/_deletions
	 */
	private Set<OWLEntity> getAffectedRoots(final OWLAxiom axiom, final Taxonomy<OWLClass> taxonomy, final boolean add)
	{
		final Set<OWLEntity> roots = new HashSet<>();
		final Set<TaxonomyNode<OWLClass>> visited = new HashSet<>();
		visited.add(taxonomy.getBottom());
		getAffectedRoots(axiom, taxonomy.getTop(), roots, add, visited);

		/*
		 * Special case when the only _node affected by a deletion is
		 * unsatisfiable
		 */
		if (!add && roots.isEmpty())
			for (final OWLClass unsat : taxonomy.getEquivalents(OWL.Nothing))
			{
				final Set<OWLEntity> signature = _modules.get(unsat);

				if ((signature != null) && signature.containsAll(getSignature(axiom)))
					roots.add(unsat);
			}

		return roots;
	}

	/**
	 * Given an axiom, this function locates all root _nodes in the partial _order that are affected by the update
	 *
	 * @param axiom - the update
	 * @param _node - the next _node
	 * @param effects - the actual set of affected _nodes collected
	 * @param add - Flag for _additions/_deletions
	 * @param visited - _nodes visited so far
	 */
	private void getAffectedRoots(final OWLAxiom axiom, final TaxonomyNode<OWLClass> node, final Set<OWLEntity> effects, final boolean add, final Set<TaxonomyNode<OWLClass>> visited)
	{

		// only proceed if not seen this _node
		if (visited.contains(node))
			return;
		else
			visited.add(node);

		final OWLEntity entity = node.getName();

		// get the sig for this module
		final Set<OWLEntity> signature = _modules.get(entity);

		boolean outdated = false;

		// check if the entity has been removed due to a deletion
		if (signature == null)
		{
			if (_logger.isLoggable(Level.FINE))
				_logger.fine("Removed entity " + entity);
		}
		else
			if (add)
				// only affected if axiom is non-local w.r.t. the sig of the
				// module
				outdated = !isLocal(axiom, signature);
			else
				// only affected if sig of axiom is contained in sig of module
				outdated = signature.containsAll(getSignature(axiom));

		// if outdated add to effected set
		if (outdated)
			effects.addAll(node.getEquivalents());
		else
			// recursive call to children
			for (final TaxonomyNode<OWLClass> next : node.getSubs())
			getAffectedRoots(axiom, next, effects, add, visited);
	}

	/**
	 * Return the _axioms which references this entity
	 *
	 * @param entity
	 * @return
	 */
	@Override
	public Stream<OWLAxiom> axioms(final OWLEntity entity)
	{
		final Set<OWLAxiom> axioms = _entityAxioms.get(entity);

		if (axioms == null)
			return Stream.empty();

		return axioms.stream();
	}

	@Deprecated
	@Override
	public Set<OWLAxiom> getAxioms(final OWLEntity entity)
	{
		Set<OWLAxiom> axioms = _entityAxioms.get(entity);

		if (axioms == null)
			axioms = Collections.emptySet();

		return axioms;
	}

	@Override
	public OWLOntology getModule(final OWLEntity entity)
	{
		return getModuleFromSignature(_modules.get(entity));
	}

	protected Set<OWLAxiom> getModuleAxioms(final Set<OWLEntity> signature)
	{
		final Set<OWLEntity> referenced = new HashSet<>();

		final Set<OWLEntity> augmentedSig = new HashSet<>(signature);
		augmentedSig.add(OWL.Thing);
		final Set<OWLAxiom> axioms = new HashSet<>();

		final Set<OWLAxiom> candidates = new HashSet<>();
		for (final OWLEntity e : signature)
			candidates.addAll(getAxioms(e));

		for (final OWLAxiom axiom : candidates)
		{
			final Set<OWLEntity> sigAxiom = axiom.signature().collect(Collectors.toSet());

			/*
			 * An axiom is in the module of the signature if the augmented
			 * signature contains all the entities referenced in the axiom.
			 * However, there are cases this necessary _condition is not
			 * sufficient. For example, an axiom may be a tautology, e.g.
			 * PropertyDomain(p owl:Thing), and if this axiom is included the
			 * module would not be minimal anymore. The locality check we
			 * perform for the axiom w.r.t. the given signature filters these
			 * _axioms.
			 */
			if (augmentedSig.containsAll(sigAxiom) && !isLocal(axiom, signature))
			{
				axioms.add(axiom);
				referenced.addAll(sigAxiom);
			}
		}

		/*
		 * Special handling is required if an entity is in the input signature,
		 * and is referenced by some _axioms, but all those _axioms are local to
		 * the signature and contain entities not in the signature. A
		 * declaration axiom is used to keep the entity in the module.
		 */
		final Set<OWLEntity> notReferenced = new HashSet<>(signature);
		notReferenced.removeAll(referenced);
		for (final OWLEntity e : notReferenced)
			if (_entityAxioms.get(e) != null)
				axioms.add(OWL.declaration(e));

		return axioms;
	}

	/**
	 * Returns a new ontology that contains the _axioms that are in the module for given set of entities
	 *
	 * @param signature
	 * @return
	 * @throws OWLException
	 */
	@Override
	public OWLOntology getModuleFromSignature(final Set<OWLEntity> signature)
	{
		final Set<OWLAxiom> moduleAxioms = getModuleAxioms(signature);
		return OWL.Ontology(moduleAxioms);
	}

	/**
	 * Get the entities referenced in this axiom
	 *
	 * @param axiom
	 * @return
	 */
	@Deprecated
	protected Set<OWLEntity> getSignature(final OWLAxiom axiom)
	{
		return axiom.getSignature();
	}

	protected Stream<OWLEntity> signature(final OWLAxiom axiom)
	{
		return axiom.signature();
	}

	/**
	 * Checks if _axioms have been added/removed and modules need to be updated
	 *
	 * @return <code>true</code> if _axioms have been added/removed
	 */
	@Override
	public boolean isChanged()
	{
		return !_additions.isEmpty() || !_deletions.isEmpty() || _nonLocalAxioms;
	}

	protected boolean isLocal(final OWLAxiom axiom, final Set<OWLEntity> signature)
	{
		return _localityEvaluator.isLocal(axiom, signature);
	}

	@Deprecated
	@Override
	public void addAxioms(final Iterable<OWLAxiom> axioms)
	{
		for (final OWLAxiom axiom : axioms)
			addAxiom(axiom);
	}

	@Override
	public void addAxioms(final Stream<OWLAxiom> axioms)
	{
		axioms.forEach(this::addAxiom);
	}

	private void processAdditions()
	{
		for (final OWLAxiom axiom : _additions)
		{
			_axioms.add(axiom);
			axiom.signature().forEach(entity ->
			{
				_entityAxioms.add(entity, axiom);

				if (entity instanceof OWLClass)
				{
					final OWLClass cls = (OWLClass) entity;
					if (_modules != null && !_modules.containsKey(cls))
						_newClasses.add(cls);
				}
			});
		}
	}

	private void processDeletions()
	{
		for (final OWLAxiom axiom : _deletions)
		{
			_axioms.remove(axiom);
			axiom.signature().forEach(entity ->
			{
				_entityAxioms.remove(entity, axiom);

				if (!_entityAxioms.containsKey(entity))
				{
					_logger.fine(() -> "Remove " + entity + " which is not mentioned anymore");
					_modules.remove(entity);
				}
			});
		}
	}

	/**
	 * Method to 1) find affected modules and 2) update them - that is extract their new signatures
	 *
	 * @param effects affected entities
	 * @param taxonomy classification hierarchy
	 * @param add Flag for _additions/_deletions
	 */
	private void updateEffectedModules(final Set<OWLEntity> effects, final Taxonomy<OWLClass> taxonomy, final boolean add)
	{
		// affected root _nodes in _order
		final Set<OWLEntity> affectedRoots = new HashSet<>();
		// Set of all _nodes affected
		final Set<OWLEntity> affected = new HashSet<>();

		_logger.fine(() -> "Update modules for " + (add ? "_additions" : "_deletions"));

		// any new classes are affected
		affectedRoots.addAll(_newClasses);

		// iterate over all _axioms and get find the set of root _nodes
		// affected by the update
		final Set<OWLAxiom> axioms = (add ? _additions : _deletions);
		for (final OWLAxiom axiom : axioms)
			// find affected roots - recursive function
			affectedRoots.addAll(getAffectedRoots(axiom, taxonomy, add));

		_logger.fine(() -> "Affected roots " + affectedRoots);

		// given root, get all affected objects
		for (final OWLEntity nextRoot : affectedRoots)
		{
			// add root to affected
			affected.add(nextRoot);

			if (nextRoot instanceof OWLClass)
				// collect all the descendants of this class
				if (taxonomy.contains((OWLClass) nextRoot))
				affected.addAll(taxonomy.getFlattenedSubs((OWLClass) nextRoot, false));
		}

		_logger.fine(() -> "Affected entities " + affected);

		for (final OWLEntity entity : affected)
			_modules.remove(entity);

		// Next update mods of all affected _nodes
		extractModuleSignatures(affected);

		for (final OWLEntity entity : affected)
		{
			final Set<OWLEntity> module = _modules.get(entity);
			if (module == null)
			{
				final String msg = "No module for " + entity;
				_logger.log(Level.SEVERE, msg, new RuntimeException(msg));
			}

			effects.addAll(module);
		}
	}

	@Override
	public Set<OWLEntity> applyChanges(final Taxonomy<OWLClass> taxonomy) throws UnsupportedOperationException
	{
		final Timer timer = _timers.startTimer("updateModules");

		if (!canUpdate())
			throw new UnsupportedOperationException("Modules cannot be updated!");

		// Set of all entities in the module of affected entities
		final Set<OWLEntity> effects = new HashSet<>();

		// cash the signatures for _axioms as they are used in the next step
		processAdditions();
		// compute effects
		updateEffectedModules(effects, taxonomy, true);

		updateEffectedModules(effects, taxonomy, false);
		// remove signatures for deleted _axioms now that they are not needed
		processDeletions();

		// clear processed _axioms
		_additions.clear();
		_deletions.clear();

		// clear the pending change types
		_changes.clear();

		// clear new classes as well
		_newClasses.clear();

		timer.stop();

		return effects;
	}

	@Override
	public Timers getTimers()
	{
		return _timers;
	}

	@Override
	public Stream<OWLAxiom> axioms()
	{
		return _axioms.stream();
	}

	@Deprecated
	@Override
	public Set<OWLAxiom> getAxioms()
	{
		return Collections.unmodifiableSet(_axioms);
	}

	@Override
	public Set<OWLEntity> getEntities()
	{
		return Collections.unmodifiableSet(_entityAxioms.keySet());
	}

	public void resetModules()
	{
		// _cache the axiom signatures
		processAdditions();
		_additions.clear();

		// no need to consider _deletions for initial module extraction
		_deletions.clear();
		_changes.clear();

		_nonLocalAxioms = false;

		_modules = new MultiValueMap<>();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean isClassificationNeeded(final Expressivity expressivity)
	{
		return isTBoxChanged()
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
	public boolean isTBoxChanged()
	{
		return _changes.contains(ChangeType.TBOX_ADD) || _changes.contains(ChangeType.TBOX_DEL);
	}

	/**
	 * Checks whether there are unapplied changes to the RBox
	 *
	 * @return true if there are unapplied changes to RBox
	 */
	public boolean isRBoxChanged()
	{
		return _changes.contains(ChangeType.RBOX_ADD) || _changes.contains(ChangeType.RBOX_DEL);
	}

	/**
	 * Checks whether there are unapplied changes to the ABox
	 *
	 * @return true if there are unapplied changes to ABox
	 */
	public boolean isABoxChanged()
	{
		return _changes.contains(ChangeType.ABOX_ADD) || _changes.contains(ChangeType.ABOX_DEL);
	}

	/**
	 * Checks the category of the change the addition of the axiom introduces (i.e., change to a TBox, RBox, or ABox), and updates the changes set
	 * appropriately.
	 *
	 * @param axiom the axiom being added
	 */
	private void categorizeAddedAxiom(final OWLAxiom axiom)
	{
		if (ChangeTypeDetector.isTBoxAxiom(axiom))
			_changes.add(ChangeType.TBOX_ADD);
		else
			if (ChangeTypeDetector.isRBoxAxiom(axiom))
				_changes.add(ChangeType.RBOX_ADD);
			else
				if (ChangeTypeDetector.isABoxAxiom(axiom))
					_changes.add(ChangeType.ABOX_ADD);
	}

	/**
	 * Checks the category of the change the deletion of the axiom introduces (i.e., change to a TBox, RBox, or ABox), and updates the changes set
	 * appropriately.
	 *
	 * @param axiom the axiom being removed
	 */
	private void categorizeRemovedAxiom(final OWLAxiom axiom)
	{
		if (ChangeTypeDetector.isTBoxAxiom(axiom))
			_changes.add(ChangeType.TBOX_DEL);
		else
			if (ChangeTypeDetector.isRBoxAxiom(axiom))
				_changes.add(ChangeType.RBOX_DEL);
			else
				if (ChangeTypeDetector.isABoxAxiom(axiom))
					_changes.add(ChangeType.ABOX_DEL);
	}

	// I/O code to persist the state of the AbstractModuleExtractor

	/**
	 * The name of the entry in the zip file that stores _axioms
	 */
	private static final String MODULE_EXTRACTOR_AXIOMS_FILE_NAME = "ModuleExtractorAxioms";

	/**
	 * The name of the entry in the zip file that stores the module information
	 */
	private static final String MODULE_EXTRACTOR_MODULES_FILE_NAME = "ModuleExtractorModules";

	/**
	 * @inheritDoc
	 */
	@Override
	public void save(final ZipOutputStream outputStream) throws IOException, IllegalStateException
	{
		if (!_additions.isEmpty() || !_deletions.isEmpty())
			throw new IllegalStateException("The module extractor contains unapplied changes to the modules, and therefore cannot be saved.");

		// first save the _axioms
		final ZipEntry axiomsEntry = new ZipEntry(MODULE_EXTRACTOR_AXIOMS_FILE_NAME);
		outputStream.putNextEntry(axiomsEntry);

		ModuleExtractorPersistence.saveAxioms(_axioms, new UncloseableOutputStream(outputStream));

		// next save the modules
		final ZipEntry modulesEntry = new ZipEntry(MODULE_EXTRACTOR_MODULES_FILE_NAME);
		outputStream.putNextEntry(modulesEntry);

		ModuleExtractorPersistence.saveModules(_modules, new UncloseableOutputStream(outputStream));

		outputStream.flush();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void load(final ZipInputStream inputStream) throws IOException, IllegalArgumentException
	{
		resetModules();

		ZipEntry zipEntry = inputStream.getNextEntry();

		if (!(MODULE_EXTRACTOR_AXIOMS_FILE_NAME.equals(zipEntry.getName())))
			throw new IllegalArgumentException(String.format("Unexpected entry (%s) in ZipInputStream. Expected %s", zipEntry.getName(), MODULE_EXTRACTOR_AXIOMS_FILE_NAME));

		final OWLOntology axiomOntology = ModuleExtractorPersistence.loadAxiomOntology(inputStream);

		// I am not sure that this is the right way to recompute this ...
		_additions.addAll(axiomOntology.axioms().collect(Collectors.toList()));
		processAdditions();
		_additions.clear();

		zipEntry = inputStream.getNextEntry();

		if (!(MODULE_EXTRACTOR_MODULES_FILE_NAME.equals(zipEntry.getName())))
			throw new IllegalArgumentException(String.format("Unexpected entry (%s) in ZipInputStream. Expected %s", zipEntry.getName(), MODULE_EXTRACTOR_MODULES_FILE_NAME));

		_modules = ModuleExtractorPersistence.loadModules(inputStream);
	}
}
