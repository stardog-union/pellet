// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity;

import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.owlapi.OntologyUtils;
import com.clarkparsia.pellet.owlapi.PelletReasoner;
import com.clarkparsia.pellet.owlapi.PelletReasonerFactory;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import openllet.aterm.ATermAppl;
import openllet.shared.tools.Log;
import org.mindswap.pellet.exceptions.PelletRuntimeException;
import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.taxonomy.TaxonomyNode;
import org.mindswap.pellet.taxonomy.printer.ClassTreePrinter;
import org.mindswap.pellet.taxonomy.printer.TreeTaxonomyPrinter;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Bool;
import org.mindswap.pellet.utils.MultiValueMap;
import org.mindswap.pellet.utils.Namespaces;
import org.mindswap.pellet.utils.PartialOrderBuilder;
import org.mindswap.pellet.utils.PartialOrderComparator;
import org.mindswap.pellet.utils.PartialOrderRelation;
import org.mindswap.pellet.utils.SetUtils;
import org.mindswap.pellet.utils.TaxonomyUtils;
import org.mindswap.pellet.utils.Timer;
import org.mindswap.pellet.utils.Timers;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.AxiomNotInProfileException;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.ClassExpressionNotInProfileException;
import org.semanticweb.owlapi.reasoner.FreshEntitiesException;
import org.semanticweb.owlapi.reasoner.FreshEntityPolicy;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.IndividualNodeSetPolicy;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.owlapi.reasoner.TimeOutException;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;
import org.semanticweb.owlapi.reasoner.impl.NodeFactory;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNode;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNode;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNodeSet;
import org.semanticweb.owlapi.util.Version;

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
public class IncrementalClassifier implements OWLReasoner, OWLOntologyChangeListener
{
	public static final Logger _logger = Log.getLogger(IncrementalClassifier.class);

	/**
	 * Modularity results
	 */
	private MultiValueMap<OWLEntity, OWLEntity> _modules = null;

	/**
	 * Standard Pellet reasoner
	 */
	private final PelletReasoner _reasoner;

	/**
	 * Module _extractor
	 */
	private ModuleExtractor _extractor = ModuleExtractorFactory.createModuleExtractor();

	private Taxonomy<OWLClass> _taxonomy = null;

	/**
	 * Do the regular classification and module extraction in two separate threads concurrently. Doing so might reduce overall processing time but increases the
	 * memory requirements because both processes need additional memory during running which will be freed at the _end of the process.
	 */
	private boolean _multiThreaded = true;

	public Timers _timers = _extractor.getTimers();

	private final Random RND = new Random();

	private boolean _realized = false;

	public IncrementalClassifier(final OWLOntology ontology)
	{
		this(PelletReasonerFactory.getInstance().createReasoner(ontology), ModuleExtractorFactory.createModuleExtractor());
	}

	public IncrementalClassifier(final OWLOntology ontology, final OWLReasonerConfiguration config)
	{
		this(PelletReasonerFactory.getInstance().createReasoner(ontology, config), ModuleExtractorFactory.createModuleExtractor());
	}

	public IncrementalClassifier(final OWLOntology ontology, final ModuleExtractor moduleExtractor)
	{
		this(PelletReasonerFactory.getInstance().createReasoner(ontology), moduleExtractor);
	}

	public IncrementalClassifier(final OWLOntology ontology, final OWLReasonerConfiguration config, final ModuleExtractor moduleExtractor)
	{
		this(PelletReasonerFactory.getInstance().createReasoner(ontology, config), moduleExtractor);
	}

	public IncrementalClassifier(final PelletReasoner reasoner)
	{
		this(reasoner, ModuleExtractorFactory.createModuleExtractor());
	}

	public IncrementalClassifier(final PelletReasoner reasoner, final ModuleExtractor extractor)
	{
		_reasoner = reasoner;
		_extractor = extractor;

		final OWLOntology ontology = reasoner.getRootOntology();
		ontology.importsClosure().map(OWLOntology::axioms).forEach(extractor::addAxioms);
		reasoner.getManager().addOntologyChangeListener(this);
	}

	public IncrementalClassifier(final PersistedState persistedState)
	{
		_extractor = persistedState.getModuleExtractor();
		_taxonomy = persistedState.getTaxonomy();
		_realized = persistedState.isRealized();

		_modules = _extractor.getModules();

		final OWLOntology ontology = OWL.Ontology(_extractor.axioms());

		_reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);

		_reasoner.getManager().addOntologyChangeListener(this);
	}

	public IncrementalClassifier(final PersistedState persistedState, final OWLOntology ontology)
	{
		_reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
		_extractor = persistedState.getModuleExtractor();
		_taxonomy = persistedState.getTaxonomy();
		_realized = persistedState.isRealized();

		_modules = _extractor.getModules();

		final OntologyDiff diff = OntologyDiff.diffAxiomsWithOntologies(_extractor.axioms(), Collections.singleton(ontology));

		if (!diff.areSame())
		{
			for (final OWLAxiom addition : diff.getAdditions())
				_extractor.addAxiom(addition);

			for (final OWLAxiom deletion : diff.getDeletions())
				_extractor.deleteAxiom(deletion);
		}

		_reasoner.getManager().addOntologyChangeListener(this);
	}

	@Deprecated
	public Collection<OWLAxiom> getAxioms()
	{
		return _extractor.getAxioms();
	}

	public Stream<OWLAxiom> axioms()
	{
		return _extractor.axioms();
	}

	/**
	 * Build the class hierarchy based on the results from the _reasoner
	 */
	static public Taxonomy<OWLClass> buildClassHierarchy(final PelletReasoner reasoner)
	{

		final Taxonomy<OWLClass> taxonomy = new Taxonomy<>(null, OWL.Thing, OWL.Nothing);

		final Set<OWLClass> things = reasoner.getEquivalentClasses(OWL.Thing).entities().collect(Collectors.toSet());
		things.remove(OWL.Thing);
		if (!things.isEmpty())
			taxonomy.addEquivalents(OWL.Thing, things);

		final Set<OWLClass> nothings = reasoner.getEquivalentClasses(OWL.Nothing).entities().collect(Collectors.toSet());
		nothings.remove(OWL.Nothing);
		if (!nothings.isEmpty())
			taxonomy.addEquivalents(OWL.Nothing, nothings);

		for (final Node<OWLClass> subEq : reasoner.getSubClasses(OWL.Thing, true))
			recursiveBuild(taxonomy, subEq, reasoner);

		return taxonomy;
	}

	static private void recursiveBuild(final Taxonomy<OWLClass> taxonomy, final Node<OWLClass> eqClasses, final PelletReasoner reasoner)
	{
		assert eqClasses.entities().findAny().isPresent() : "Equivalents empty as passed";

		final OWLClass cls = eqClasses.iterator().next();
		if (taxonomy.contains(cls))
			return;

		final Set<OWLClass> emptySet = Collections.emptySet();
		taxonomy.addNode(eqClasses.entities().collect(Collectors.toList()), emptySet, emptySet, /* hidden =*/false);

		for (final Node<OWLClass> subEq : reasoner.getSubClasses(cls, true))
		{
			recursiveBuild(taxonomy, subEq, reasoner);
			taxonomy.addSuper(subEq.iterator().next(), cls);
		}

	}

	public void classify()
	{
		if (isClassified())
		{
			if (_extractor.isChanged())
				// this means that there are some changes to the modules, which do not affect
				// the current taxonomy (i.e., that is why isClassified() returns true)
				// let's update the modules here
				// TODO: maybe we should move these calls somewhere else but in general
				// the users expect that all changes are applied after classify()
				// and unapplied changes will prevent the classifie
				_extractor.applyChanges(_taxonomy);
			return;
		}

		if (_extractor.canUpdate())
			incrementalClassify();
		else
			regularClassify();

		resetRealization();
	}

	@Override
	public void dispose()
	{
		_reasoner.dispose();

		_reasoner.getManager().removeOntologyChangeListener(this);
	}

	@Override
	public Node<OWLClass> getEquivalentClasses(final OWLClassExpression clsC)
	{
		if (clsC.isAnonymous())
			throw new IllegalArgumentException("This _reasoner only supports named classes");

		classify();

		return NodeFactory.getOWLClassNode(_taxonomy.getAllEquivalents((OWLClass) clsC));
	}

	/**
	 * Get the _modules
	 */
	public MultiValueMap<OWLEntity, OWLEntity> getModules()
	{
		return _modules;
	}

	/**
	 * Get the underlying _reasoner
	 */
	public PelletReasoner getReasoner()
	{
		return _reasoner;
	}

	@Override
	public NodeSet<OWLClass> getSubClasses(final OWLClassExpression clsC, final boolean direct)
	{
		if (clsC.isAnonymous())
			throw new UnsupportedOperationException("This _reasoner only supports named classes");

		classify();

		final Set<Node<OWLClass>> values = new HashSet<>();
		for (final Set<OWLClass> val : _taxonomy.getSubs((OWLClass) clsC, direct))
			values.add(NodeFactory.getOWLClassNode(val));

		return new OWLClassNodeSet(values);
	}

	/**
	 * This incremental classification _strategy does the following: for all _modules that are affected, collect all of their axioms and classify them all once
	 * in Openllet. This allows the exploitation _current classification optimizations
	 */
	private void incClassifyAllModStrategy()
	{
		// Get the entities whose _modules are affected
		final Set<OWLEntity> effects = _extractor.applyChanges(_taxonomy);

		_logger.fine(() -> "Module entities " + effects);

		// create ontology for all the axioms of all effected _modules
		final OWLOntology owlModule = _extractor.getModuleFromSignature(effects);

		_logger.fine(() -> "Module axioms " + owlModule.logicalAxioms().map(OWLAxiom::toString).collect(Collectors.joining(" ")));

		// load the extracted module to a new _reasoner
		final PelletReasoner moduleReasoner = PelletReasonerFactory.getInstance().createReasoner(owlModule);

		// classify the module
		moduleReasoner.getKB().classify();

		if (_logger.isLoggable(Level.FINE))
		{
			_logger.fine("Classified module:");

			new ClassTreePrinter().print(moduleReasoner.getKB().getTaxonomy(), new PrintWriter(System.err));
		}

		final Taxonomy<OWLClass> moduleTaxonomy = buildClassHierarchy(moduleReasoner);

		final Set<OWLClass> affectedCls = new HashSet<>();
		for (final OWLEntity entity : effects)
			if (entity instanceof OWLClass)
				affectedCls.add((OWLClass) entity);
		_taxonomy = updateClassHierarchy(_taxonomy, moduleTaxonomy, affectedCls);

		if (_logger.isLoggable(Level.FINE))
		{
			_logger.fine("Updated _taxonomy:");
			new TreeTaxonomyPrinter<OWLClass>().print(_taxonomy, new PrintWriter(System.err));
			//			new FunctionalTaxonomyPrinter().print( _taxonomy, new OutputFormatter( System.err, false ) );
		}

		OntologyUtils.getOWLOntologyManager().removeOntology(owlModule);
	}

	private void incrementalClassify()
	{
		if (_logger.isLoggable(Level.FINE))
			_logger.fine("Incremental classification starting");

		final Timer timer = _timers.startTimer("incrementalClassify");

		incClassifyAllModStrategy();

		timer.stop();

		if (_logger.isLoggable(Level.FINE))
			_logger.fine("Incremental classification done");
	}

	public boolean isClassified()
	{
		// what if expressivity should change because of the yet unapplied changes?
		return _modules != null && (!_extractor.isChanged() || !_extractor.isClassificationNeeded(_reasoner.getKB().getExpressivity()));
	}

	public boolean isRealized()
	{
		return isClassified() && _realized;
	}

	public boolean isDefined(final OWLClass cls)
	{
		return _extractor.axioms(cls).findAny().isPresent();
	}

	public boolean isDefined(final OWLDataProperty prop)
	{
		return _extractor.axioms(prop).findAny().isPresent();
	}

	public boolean isDefined(final OWLNamedIndividual ind)
	{
		return _extractor.axioms(ind).findAny().isPresent();
	}

	public boolean isDefined(final OWLObjectProperty prop)
	{
		return _extractor.axioms(prop).findAny().isPresent();
	}

	public boolean isEquivalentClass(final OWLClassExpression clsC, final OWLClassExpression clsD)
	{
		if (clsC.isAnonymous() || clsD.isAnonymous())
			throw new UnsupportedOperationException("This reasoner only supports named classes");

		classify();

		return _taxonomy.isEquivalent((OWLClass) clsC, (OWLClass) clsD) == Bool.TRUE;
	}

	@Override
	public boolean isSatisfiable(final OWLClassExpression description)
	{
		if (description.isAnonymous() || !isClassified())
			return _reasoner.isSatisfiable(description);

		return !getUnsatisfiableClasses().contains((OWLClass) description);
	}

	@Override
	public void ontologiesChanged(final List<? extends OWLOntologyChange> changes)
	{
		final OWLOntology ontology = getRootOntology();
		if (ontology == null || ontology.getOWLOntologyManager() == null || ontology.getOWLOntologyManager().ontologies() == null)
			return;

		if (!ontology//
				.getOWLOntologyManager()//
				.ontologies()//
				.filter(o -> o != null)//
				.anyMatch(o -> o.getOntologyID()//
						.equals(ontology.getOntologyID())//
		)) // TODO : need a comment.
			return;

		final Set<OWLOntology> ontologies = getRootOntology().importsClosure().collect(Collectors.toSet());
		for (final OWLOntologyChange change : changes)
		{
			if (!change.isAxiomChange() || !ontologies.contains(change.getOntology()))
				continue;

			resetRealization();

			final OWLAxiom axiom = change.getAxiom();

			if (change instanceof AddAxiom)
				_extractor.addAxiom(axiom);
			else
				if (change instanceof RemoveAxiom)
					_extractor.deleteAxiom(axiom);
				else
					throw new UnsupportedOperationException("Unrecognized axiom change: " + change);
		}
	}

	private void regularClassify()
	{
		_logger.fine("Regular classification starting");

		final Thread classification = new Thread("classification")
		{
			@Override
			public void run()
			{
				// classify ontology
				Timer timer = _timers.startTimer("reasonerClassify");
				_reasoner.flush();
				_reasoner.getKB().classify();
				timer.stop();

				if (_logger.isLoggable(Level.FINE))
				{
					_logger.fine("Regular _taxonomy:");

					new TreeTaxonomyPrinter<ATermAppl>().print(_reasoner.getKB().getTaxonomy(), new PrintWriter(System.err));
				}

				timer = _timers.startTimer("buildClassHierarchy");
				_taxonomy = buildClassHierarchy(_reasoner);
				timer.stop();

				if (_logger.isLoggable(Level.FINE))
				{
					_logger.fine("Copied _taxonomy:");

					new TreeTaxonomyPrinter<OWLClass>().print(_taxonomy, new PrintWriter(System.err));
				}
			}
		};

		final Thread partitioning = new Thread("partitioning")
		{
			@Override
			public void run()
			{
				// get _modules for each concept
				_modules = _extractor.extractModules();
			}
		};

		try
		{
			final Timer timer = _timers.startTimer("regularClassify");

			if (_multiThreaded)
			{
				classification.start();
				partitioning.start();

				classification.join();
				partitioning.join();
			}
			else
			{
				classification.run();
				partitioning.run();
			}

			timer.stop();
		}
		catch (final InterruptedException e)
		{
			throw new RuntimeException(e);
		}

		if (_logger.isLoggable(Level.FINE))
			_logger.fine("Regular classification done");
	}

	/**
	 * @param _taxonomy Previous _taxonomy state
	 * @param moduleTaxonomy Change in _taxonomy state
	 * @param affected Set of classes affected by changes
	 */
	private Taxonomy<OWLClass> updateClassHierarchy(final Taxonomy<OWLClass> taxonomy, final Taxonomy<OWLClass> moduleTaxonomy, final Set<OWLClass> affected)
	{
		final Set<OWLClass> inTaxonomy = new HashSet<>(moduleTaxonomy.getClasses());
		inTaxonomy.remove(OWL.Thing);
		inTaxonomy.remove(OWL.Nothing);
		assert affected.containsAll(inTaxonomy) : "Unaffected _nodes in changed _taxonomy";

		final Set<OWLClass> removed = new HashSet<>(affected);
		removed.removeAll(moduleTaxonomy.getClasses());

		final List<OWLClass> sorted = taxonomy.topologocialSort( /* includeEquivalents = */false);

		// TODO: Top equivalents could change?!
		final Set<OWLClass> emptySet = Collections.emptySet();
		for (final OWLClass cls : sorted)
		{

			// TODO: assert assumption that if any classes equivalent in
			// _taxonomy exist in moduleTaxonomy, all are
			if (removed.contains(cls) || moduleTaxonomy.contains(cls))
				continue;

			moduleTaxonomy.addNode(taxonomy.getAllEquivalents(cls), emptySet, emptySet, /* hidden= */
					false);
			final Set<OWLClass> supers = taxonomy.getFlattenedSupers(cls, /* direct = */true);
			supers.removeAll(removed);
			moduleTaxonomy.addSupers(cls, supers);
		}

		final List<OWLClass> nothings = new ArrayList<>();
		for (final OWLClass cls : taxonomy.getEquivalents(OWL.Nothing))
			if (!removed.contains(cls) && !moduleTaxonomy.contains(cls))
				nothings.add(cls);
		if (!nothings.isEmpty())
			moduleTaxonomy.addEquivalents(OWL.Nothing, nothings);

		return moduleTaxonomy;
	}

	//	/**
	//	 * FIXME: This function is incredibly broken.
	//	 * Main method to update the partial _order and find new subsumptions.
	//	 *
	//	 * @param add
	//	 *            Flag for additions/deletions
	//	 * @param _node
	//	 *            the _node which is being updated
	//	 * @param newR
	//	 *            The _reasoner which has been loaded and processed with the new
	//	 *            signature of this module
	//	 */
	//	private void updatePartialOrder(boolean add, TaxonomyNode<OWLClass> _node, PelletReasoner newR) {
	//		if( _logger.isLoggable( Level.FINER ) )
	//			_logger.finer( "Update _node " + _node );
	//
	//		OWLClass clazz = _node.getName();
	//
	//		// collect all of its valid super classes from the _reasoner
	//		Set<Set<OWLClass>> validSupers = newR.getAncestorClasses( clazz );
	//
	//		// get all of the old super classes from the subclass _index
	//		Set<OWLClass> oldSupers = null;// superClasses.get( clazz );
	//
	//		// case for add
	//		if( add ) {
	//
	//			// for each subclass check if its new...if it is, then add it to
	//			// the child of this _node
	//			for( Set<OWLClass> nextCls : validSupers ) {
	//				// if this is thing, then continue
	//				if( nextCls.contains( OWL.Thing ) )
	//					continue;
	//
	//				// iterate over new super classes - again this is a set
	//				// because pellet returns a set of sets
	//				for( OWLClass next : nextCls ) {
	//					// check if this subsumption already exists
	//					if( !oldSupers.contains( next ) ) {
	//
	//						// add it to the subclass _index
	//						oldSupers.add( next );
	//
	//						if( _logger.isLoggable( Level.FINER ) ) {
	//							_logger.finer( "  Found new subsumption " + clazz + " subClassOf " + next );
	//						}
	//
	//						// update the partial _order
	//						TaxonomyNode<OWLClass> newSubNode = _taxonomy.getNode( next );
	//						if( !newSubNode.getSubs().contains( _node ) ) {
	//							newSubNode.addSub( _node );
	//						}
	//					}
	//				}
	//			}
	//		}
	//		else {
	//
	//			// case for deletions
	//
	//			// if there were no previous supers then by monotonicity, there
	//			// still will not be, so return
	//			if( oldSupers.isEmpty() )
	//				return;
	//
	//			// used to track invalidated supers
	//			Set<OWLClass> removeSet = new HashSet<OWLClass>();
	//
	//			// for each old subclass check if its still exists, if not
	//			// remove it
	//			for( OWLClass nextOldSuper : oldSupers ) {
	//				// if thing, then continue
	//				if( nextOldSuper.equals( OWL.Thing ) )
	//					continue;
	//
	//				TaxonomyNode<OWLClass> oldSubNode = _taxonomy.getNode( nextOldSuper );
	//
	//				// check if this subsumption still exists
	//				if( !OntologyUtils.containsClass( validSupers, nextOldSuper ) ) {
	//					// update remove set
	//					removeSet.add( nextOldSuper );
	//
	//					if( _logger.isLoggable( Level.FINER ) ) {
	//						_logger.finer( "  Found violated subsumption " + clazz + " subClassOf "
	//								+ nextOldSuper );
	//					}
	//
	//					// update partial _order
	//					oldSubNode.removeSub( _node );
	//				}
	//			}
	//
	//			// update subclass _index
	//			oldSupers.removeAll( removeSet );
	//		}
	//	}

	/**
	 * Returns the value of multi-threaded option.
	 *
	 * @see IncrementalClassifier#setMultiThreaded(boolean)
	 * @return the value of multi-threaded option
	 */
	public boolean isMultiThreaded()
	{
		return _multiThreaded;
	}

	/**
	 * Sets the multi-threading option. In multi-threaded mode, during the initial setup, the regular classification and module extraction are performed in two
	 * separate threads concurrently. Doing so might reduce overall processing time but it also increases the memory requirements because both processes need
	 * additional memory during running which will be freed at the _end of the process.
	 *
	 * @param _multiThreaded value to set the multi-threaded option
	 */
	public void setMultiThreaded(final boolean multiThreaded)
	{
		_multiThreaded = multiThreaded;
	}

	/**
	 * A class that has access to all the internal parts of the IncrementalClassifier that has to be persisted when saving the state to the stream. This class
	 * enables the separation between the I/O code and the reasoning code. This class should not be used any other parts of code than the I/O code. This class
	 * is a variation of Memento design pattern (as it encapsulates the state) with the important difference that actually no state is being copied; instead,
	 * this class only contains the references to the _data structures to be saved. (The I/O code is thought as actually performing the copy, and therefore
	 * completing the Memento pattern.)
	 * <p>
	 * Copyright: Copyright (c) 2009
	 * </p>
	 * <p>
	 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
	 * </p>
	 *
	 * @author Blazej Bulka
	 */
	public static class PersistedState
	{
		private final ModuleExtractor _persistedExtractor;
		private final Taxonomy<OWLClass> _persistedTaxonomy;
		private final boolean _persistedRealized;

		public PersistedState(final IncrementalClassifier incrementalClassifier)
		{
			_persistedExtractor = incrementalClassifier._extractor;
			_persistedTaxonomy = incrementalClassifier._taxonomy;
			_persistedRealized = incrementalClassifier._realized;
		}

		public PersistedState(final ModuleExtractor extractor, final Taxonomy<OWLClass> taxonomy, final boolean realized)
		{
			_persistedExtractor = extractor;
			_persistedTaxonomy = taxonomy;
			_persistedRealized = realized;
		}

		public ModuleExtractor getModuleExtractor()
		{
			return _persistedExtractor;
		}

		public Taxonomy<OWLClass> getTaxonomy()
		{
			return _persistedTaxonomy;
		}

		public boolean isRealized()
		{
			return _persistedRealized;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void flush()
	{
		_reasoner.flush();
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node<OWLClass> getBottomClassNode()
	{
		return getEquivalentClasses(OWL.Nothing);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node<OWLDataProperty> getBottomDataPropertyNode()
	{
		return getEquivalentDataProperties(OWL.bottomDataProperty);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode()
	{
		return getEquivalentObjectProperties(OWL.bottomObjectProperty);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BufferingMode getBufferingMode()
	{
		return BufferingMode.NON_BUFFERING;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeSet<OWLClass> getDataPropertyDomains(final OWLDataProperty pe, final boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		_reasoner.flush();
		return _reasoner.getDataPropertyDomains(pe, direct);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<OWLLiteral> getDataPropertyValues(final OWLNamedIndividual ind, final OWLDataProperty pe) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		_reasoner.flush();
		return _reasoner.getDataPropertyValues(ind, pe);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeSet<OWLNamedIndividual> getDifferentIndividuals(final OWLNamedIndividual ind) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		_reasoner.flush();
		return _reasoner.getDifferentIndividuals(ind);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeSet<OWLClass> getDisjointClasses(final OWLClassExpression ce)
	{
		final DisjointClassComparator disjointClassComparator = new DisjointClassComparator(_taxonomy, ce);

		if (!_taxonomy.contains(disjointClassComparator.getComplementRepresentation()))
		{
			_reasoner.flush();
			final PartialOrderBuilder<OWLClass> orderBuilder = new PartialOrderBuilder<>(_taxonomy, disjointClassComparator);

			orderBuilder.add(disjointClassComparator.getComplementRepresentation(), true);
		}

		final OWLClassNodeSet result = new OWLClassNodeSet();

		for (final Set<OWLClass> equivSet : _taxonomy.getSubs(disjointClassComparator.getComplementRepresentation(), false))
			result.addSameEntities(equivSet);

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeSet<OWLDataProperty> getDisjointDataProperties(final OWLDataPropertyExpression pe) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		_reasoner.flush();
		return _reasoner.getDisjointDataProperties(pe);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(final OWLObjectPropertyExpression pe) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		_reasoner.flush();
		return _reasoner.getDisjointObjectProperties(pe);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node<OWLDataProperty> getEquivalentDataProperties(final OWLDataProperty pe) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		_reasoner.flush();
		return _reasoner.getEquivalentDataProperties(pe);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(final OWLObjectPropertyExpression pe) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		_reasoner.flush();
		return _reasoner.getEquivalentObjectProperties(pe);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FreshEntityPolicy getFreshEntityPolicy()
	{
		return _reasoner.getFreshEntityPolicy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IndividualNodeSetPolicy getIndividualNodeSetPolicy()
	{
		return _reasoner.getIndividualNodeSetPolicy();
	}

	private NodeSet<OWLNamedIndividual> getIndividualNodeSetBySameAs(final Collection<OWLNamedIndividual> individuals)
	{
		final Set<Node<OWLNamedIndividual>> instances = new HashSet<>();
		final Set<OWLNamedIndividual> seen = new HashSet<>();
		for (final OWLNamedIndividual ind : individuals)
			if (!seen.contains(ind))
			{
				final Node<OWLNamedIndividual> equiv = _reasoner.getSameIndividuals(ind);
				instances.add(equiv);
				seen.addAll(equiv.entities().collect(Collectors.toList()));
			}

		return new OWLNamedIndividualNodeSet(instances);
	}

	private NodeSet<OWLNamedIndividual> getIndividualNodeSetByName(final Collection<OWLNamedIndividual> individuals)
	{
		final Set<Node<OWLNamedIndividual>> instances = new HashSet<>();

		for (final OWLNamedIndividual ind : individuals)
			for (final OWLNamedIndividual equiv : _reasoner.getSameIndividuals(ind))
				instances.add(new OWLNamedIndividualNode(equiv));

		return new OWLNamedIndividualNodeSet(instances);
	}

	private NodeSet<OWLNamedIndividual> getIndividualNodeSet(final Collection<OWLNamedIndividual> individuals)
	{
		if (IndividualNodeSetPolicy.BY_NAME.equals(getIndividualNodeSetPolicy()))
			return getIndividualNodeSetByName(individuals);
		else
			if (IndividualNodeSetPolicy.BY_SAME_AS.equals(getIndividualNodeSetPolicy()))
				return getIndividualNodeSetBySameAs(individuals);
			else
				throw new AssertionError("Unsupported IndividualNodeSetPolicy : " + getIndividualNodeSetPolicy());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeSet<OWLNamedIndividual> getInstances(final OWLClassExpression ce, final boolean direct) throws InconsistentOntologyException, ClassExpressionNotInProfileException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		if (ce.isAnonymous() && direct)
			throw new UnsupportedOperationException("This _reasoner only supports named classes");

		_reasoner.flush();

		if (!isRealized() && !direct)
			return _reasoner.getInstances(ce, direct);

		realize();

		final Set<OWLNamedIndividual> individuals = direct ? TaxonomyUtils.<OWLClass, OWLNamedIndividual> getDirectInstances(_taxonomy, (OWLClass) ce) : TaxonomyUtils.<OWLClass, OWLNamedIndividual> getAllInstances(_taxonomy, (OWLClass) ce);

		return getIndividualNodeSet(individuals);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node<OWLObjectPropertyExpression> getInverseObjectProperties(final OWLObjectPropertyExpression pe) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		_reasoner.flush();
		return _reasoner.getInverseObjectProperties(pe);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeSet<OWLClass> getObjectPropertyDomains(final OWLObjectPropertyExpression pe, final boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		_reasoner.flush();
		return _reasoner.getObjectPropertyDomains(pe, direct);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeSet<OWLClass> getObjectPropertyRanges(final OWLObjectPropertyExpression pe, final boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		_reasoner.flush();
		return _reasoner.getObjectPropertyRanges(pe, direct);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeSet<OWLNamedIndividual> getObjectPropertyValues(final OWLNamedIndividual ind, final OWLObjectPropertyExpression pe) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		_reasoner.flush();
		return _reasoner.getObjectPropertyValues(ind, pe);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<OWLAxiom> getPendingAxiomAdditions()
	{
		return Collections.emptySet();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<OWLAxiom> getPendingAxiomRemovals()
	{
		return Collections.emptySet();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<OWLOntologyChange> getPendingChanges()
	{
		return Collections.emptyList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getReasonerName()
	{
		return "Openllet (Incremental)";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Version getReasonerVersion()
	{
		return _reasoner.getReasonerVersion();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OWLOntology getRootOntology()
	{
		return _reasoner.getRootOntology();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node<OWLNamedIndividual> getSameIndividuals(final OWLNamedIndividual ind) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		_reasoner.flush();
		return _reasoner.getSameIndividuals(ind);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeSet<OWLDataProperty> getSubDataProperties(final OWLDataProperty pe, final boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		_reasoner.flush();
		return _reasoner.getSubDataProperties(pe, direct);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(final OWLObjectPropertyExpression pe, final boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		_reasoner.flush();
		return _reasoner.getSubObjectProperties(pe, direct);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeSet<OWLClass> getSuperClasses(final OWLClassExpression ce, final boolean direct) throws InconsistentOntologyException, ClassExpressionNotInProfileException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		if (ce.isAnonymous())
			throw new UnsupportedOperationException("This reasoner only supports named classes");
		final OWLClass namedClass = (OWLClass) ce;
		classify();

		return new OWLClassNodeSet(//
				_taxonomy.supers(namedClass, direct)//
						.map(NodeFactory::getOWLClassNode)//
						.collect(Collectors.toSet())//
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeSet<OWLDataProperty> getSuperDataProperties(final OWLDataProperty pe, final boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		_reasoner.flush();
		return _reasoner.getSuperDataProperties(pe, direct);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(final OWLObjectPropertyExpression pe, final boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		_reasoner.flush();
		return _reasoner.getSuperObjectProperties(pe, direct);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getTimeOut()
	{
		return _reasoner.getTimeOut();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node<OWLClass> getTopClassNode()
	{
		return getEquivalentClasses(OWL.Thing);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node<OWLDataProperty> getTopDataPropertyNode()
	{
		return getEquivalentDataProperties(OWL.topDataProperty);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode()
	{
		return getEquivalentObjectProperties(OWL.topObjectProperty);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeSet<OWLClass> getTypes(final OWLNamedIndividual ind, final boolean direct) throws InconsistentOntologyException, FreshEntitiesException, ReasonerInterruptedException, TimeOutException
	{
		_reasoner.flush();
		realize();

		final OWLClassNodeSet types = new OWLClassNodeSet();
		for (final Set<OWLClass> t : TaxonomyUtils.getTypes(_taxonomy, ind, direct))
			//Set<OWLClass> eqSet = ATermUtils.primitiveOrBottom( t );
			//if( !eqSet.isEmpty() )
			types.addNode(new OWLClassNode(t));

		return types;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node<OWLClass> getUnsatisfiableClasses() throws ReasonerInterruptedException, TimeOutException
	{
		classify();
		return getBottomClassNode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void interrupt()
	{
		// TODO : compute the multithreaded case.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConsistent() throws ReasonerInterruptedException, TimeOutException
	{
		_reasoner.flush();
		return _reasoner.isConsistent();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEntailed(final OWLAxiom axiom) throws ReasonerInterruptedException, UnsupportedEntailmentTypeException, TimeOutException, AxiomNotInProfileException, FreshEntitiesException
	{
		try
		{
			final EntailmentChecker entailmentChecker = new EntailmentChecker(this);
			return entailmentChecker.isEntailed(axiom);
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEntailed(final Set<? extends OWLAxiom> axioms) throws ReasonerInterruptedException, UnsupportedEntailmentTypeException, TimeOutException, AxiomNotInProfileException, FreshEntitiesException
	{
		try
		{
			final EntailmentChecker entailmentChecker = new EntailmentChecker(this);
			return entailmentChecker.isEntailed(axioms);
		}
		catch (final PelletRuntimeException e)
		{
			throw convert(e);
		}
	}

	private PelletRuntimeException convert(final PelletRuntimeException e) throws InconsistentOntologyException, ReasonerInterruptedException, TimeOutException, FreshEntitiesException
	{

		if (e instanceof org.mindswap.pellet.exceptions.TimeoutException)
			throw new TimeOutException();

		if (e instanceof org.mindswap.pellet.exceptions.TimerInterruptedException)
			throw new ReasonerInterruptedException(e);

		if (e instanceof org.mindswap.pellet.exceptions.InconsistentOntologyException)
			throw new InconsistentOntologyException();

		if (e instanceof org.mindswap.pellet.exceptions.UndefinedEntityException)
		{
			final Set<OWLEntity> unknown = Collections.emptySet();
			throw new FreshEntitiesException(unknown);
		}

		return e;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEntailmentCheckingSupported(final AxiomType<?> axiomType)
	{
		// the _current EntailmentChecker supports the same set of axioms as
		// the underlying _reasoner (if it cannot handle any element directly,
		// it forwards the entailment check to the underlying _reasoner)
		return getReasoner().isEntailmentCheckingSupported(axiomType);
	}

	/**
	 * {@inheritDoc}
	 */
	public void prepareReasoner() throws ReasonerInterruptedException, TimeOutException
	{
		classify();
	}

	public Taxonomy<OWLClass> getTaxonomy()
	{
		return _taxonomy;
	}

	private void resetRealization()
	{
		if (_taxonomy != null)
			for (final TaxonomyNode<OWLClass> node : _taxonomy.getNodes())
				node.removeDatum(TaxonomyUtils.INSTANCES_KEY);

		_realized = false;
	}

	private void realize()
	{
		if (isRealized())
			return;

		final Set<ATermAppl> allIndividuals = _reasoner.getKB().getIndividuals();

		final Set<OWLClass> visitedClasses = new HashSet<>();

		if (!allIndividuals.isEmpty())
			realizeByConcept(ATermUtils.TOP, allIndividuals, _reasoner.getManager().getOWLDataFactory(), visitedClasses);

		_realized = true;
	}

	private Set<ATermAppl> realizeByConcept(final ATermAppl c, final Collection<ATermAppl> individuals, final OWLDataFactory factory, final Set<OWLClass> visitedClasses)
	{
		if (c.equals(ATermUtils.BOTTOM))
			return SetUtils.emptySet();

		if (_logger.isLoggable(Level.FINER))
			_logger.finer("Realizing concept " + c);

		final OWLClass owlClass = termToOWLClass(c, factory);

		if (visitedClasses.contains(owlClass))
			return TaxonomyUtils.getAllInstances(_taxonomy, owlClass);

		final Set<ATermAppl> instances = new HashSet<>(_reasoner.getKB().retrieve(c, individuals));
		final Set<ATermAppl> mostSpecificInstances = new HashSet<>(instances);

		if (!instances.isEmpty())
		{
			final TaxonomyNode<OWLClass> node = _taxonomy.getNode(owlClass);

			if (node == null)
			{
				_logger.warning(" no _node for " + c);
				return instances;
			}

			for (final TaxonomyNode<OWLClass> sub : node.getSubs())
			{
				final OWLClass d = sub.getName();
				final Set<ATermAppl> subInstances = realizeByConcept(owlClassToTerm(d), instances, factory, visitedClasses);

				if (subInstances == null)
					return null;

				mostSpecificInstances.removeAll(subInstances);
			}

			if (!mostSpecificInstances.isEmpty())
				node.putDatum(TaxonomyUtils.INSTANCES_KEY, toOWLNamedIndividuals(mostSpecificInstances, factory));
		}

		return instances;
	}

	private Set<OWLNamedIndividual> toOWLNamedIndividuals(final Set<ATermAppl> terms, final OWLDataFactory factory)
	{
		final HashSet<OWLNamedIndividual> result = new HashSet<>();

		for (final ATermAppl ind : terms)
		{
			final OWLNamedIndividual owlInd = termToOWLNamedIndividual(ind, factory);
			if (owlInd != null)
				result.add(owlInd);
		}

		return result;
	}

	private static final ATermAppl OWL_THING = ATermUtils.makeTermAppl(Namespaces.OWL + "Thing");
	private static final ATermAppl OWL_NOTHING = ATermUtils.makeTermAppl(Namespaces.OWL + "Nothing");

	private OWLClass termToOWLClass(final ATermAppl c, final OWLDataFactory factory)
	{
		if (c.equals(ATermUtils.TOP))
			return factory.getOWLThing();
		else
			if (c.equals(OWL_THING))
				return factory.getOWLThing();
			else
				if (c.equals(OWL_NOTHING))
					return factory.getOWLNothing();

		if (!ATermUtils.isBnode(c))
			return factory.getOWLClass(IRI.create(c.getName()));

		return null;
	}

	private OWLNamedIndividual termToOWLNamedIndividual(final ATermAppl c, final OWLDataFactory factory)
	{
		if (!ATermUtils.isBnode(c))
			return factory.getOWLNamedIndividual(IRI.create(c.getName()));

		return null;
	}

	private ATermAppl owlClassToTerm(final OWLClass c)
	{
		if (c.isOWLThing())
			return ATermUtils.TOP;
		else
			if (c.isOWLNothing())
				return ATermUtils.BOTTOM;
			else
				return ATermUtils.makeTermAppl(c.getIRI().toString());
	}

	public class DisjointClassComparator implements PartialOrderComparator<OWLClass>
	{
		private static final String ANONYMOUS_COMPLEMENT_REPRESENTATION_BASE = "http://clarkparsia.com/pellet/complement/";
		private static final String COMPLEMENT_POSTFIX = "-complement";

		private final OWLClassExpression _complementClass;
		private final OWLClass _complementRepresentation;

		public DisjointClassComparator(final Taxonomy<OWLClass> taxonomy, final OWLClassExpression originalClass)
		{
			_complementClass = OWL._factory.getOWLObjectComplementOf(originalClass);
			_complementRepresentation = generateComplementRepresentation(taxonomy, originalClass);
		}

		private OWLClass generateComplementRepresentation(final Taxonomy<OWLClass> taxonomy, final OWLClassExpression originalClass)
		{
			OWLClass complementClass = null;

			if (!originalClass.isAnonymous() && (originalClass instanceof OWLClass))
				return OWL._factory.getOWLClass(IRI.create(((OWLClass) originalClass).getIRI() + COMPLEMENT_POSTFIX));

			do
				complementClass = OWL._factory.getOWLClass(IRI.create(ANONYMOUS_COMPLEMENT_REPRESENTATION_BASE + RND.nextLong()));
			while (taxonomy.contains(complementClass));

			return complementClass;
		}

		public OWLClass getComplementRepresentation()
		{
			return _complementRepresentation;
		}

		@Override
		public PartialOrderRelation compare(final OWLClass a, final OWLClass b)
		{
			OWLClassExpression aExpression = a;
			OWLClassExpression bExpression = b;

			if (a.equals(_complementRepresentation))
				aExpression = _complementClass;

			if (b.equals(_complementRepresentation))
				bExpression = _complementClass;

			final OWLAxiom aSubClassBAxiom = OWL._factory.getOWLSubClassOfAxiom(aExpression, bExpression);
			final OWLAxiom bSubClassAAxiom = OWL._factory.getOWLSubClassOfAxiom(bExpression, aExpression);

			final boolean aLessB = _reasoner.isEntailed(aSubClassBAxiom);
			final boolean bLessA = _reasoner.isEntailed(bSubClassAAxiom);

			if (aLessB && bLessA)
				return PartialOrderRelation.EQUAL;
			else
				if (aLessB)
					return PartialOrderRelation.LESS;
				else
					if (bLessA)
						return PartialOrderRelation.GREATER;
					else
						return PartialOrderRelation.INCOMPARABLE;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<InferenceType> getPrecomputableInferenceTypes()
	{
		return _reasoner.getPrecomputableInferenceTypes();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPrecomputed(final InferenceType inferenceType)
	{
		switch (inferenceType)
		{
			case CLASS_HIERARCHY:
				return isClassified();
			case CLASS_ASSERTIONS:
				return isRealized();
			default:
				return _reasoner.isPrecomputed(inferenceType);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void precomputeInferences(final InferenceType... inferenceTypes) throws ReasonerInterruptedException, TimeOutException, InconsistentOntologyException
	{
		for (final InferenceType inferenceType : inferenceTypes)
			switch (inferenceType)
			{
				case CLASS_HIERARCHY:
					classify();
					//$FALL-THROUGH$
				case CLASS_ASSERTIONS:
					realize();
					//$FALL-THROUGH$
				default:
					_reasoner.precomputeInferences(inferenceTypes);
			}
	}
}
