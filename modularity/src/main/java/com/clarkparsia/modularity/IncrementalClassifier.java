// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity;

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
import org.semanticweb.owlapi.model.OWLException;
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

import aterm.ATermAppl;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.owlapiv3.OntologyUtils;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;


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
public class IncrementalClassifier implements OWLReasoner, OWLOntologyChangeListener {
	public static final Logger					log			= Logger
																	.getLogger( IncrementalClassifier.class
																			.getName() );
	
	/**
	 * Modularity results
	 */
	private MultiValueMap<OWLEntity, OWLEntity>	modules		= null;

	/**
	 * Standard Pellet reasoner
	 */
	private final PelletReasoner						reasoner;

	/**
	 * Module extractor
	 */
	private ModuleExtractor						extractor	= ModuleExtractorFactory.createModuleExtractor();

	private Taxonomy<OWLClass>					taxonomy	= null;

	/**
	 * Do the regular classification and module extraction in two separate
	 * threads concurrently. Doing so might reduce overall processing time but
	 * increases the memory requirements because both processes need additional
	 * memory during running which will be freed at the end of the process.
	 */
	private boolean								multiThreaded	= true;

	public Timers								timers		= extractor.getTimers();
	
	private final Random 								RND			= new Random();
	
	private boolean 								realized 	= false;

	public IncrementalClassifier(OWLOntology ontology) {
		this( PelletReasonerFactory.getInstance().createReasoner( ontology ), ModuleExtractorFactory.createModuleExtractor() );
	}
	
	public IncrementalClassifier(OWLOntology ontology, OWLReasonerConfiguration config) {
		this( PelletReasonerFactory.getInstance().createReasoner( ontology, config ), ModuleExtractorFactory.createModuleExtractor() );
	}
	
	public IncrementalClassifier(OWLOntology ontology, ModuleExtractor moduleExtractor) {
		this( PelletReasonerFactory.getInstance().createReasoner( ontology ), moduleExtractor );
	}
	
	public IncrementalClassifier(OWLOntology ontology, OWLReasonerConfiguration config, ModuleExtractor moduleExtractor) {
		this( PelletReasonerFactory.getInstance().createReasoner( ontology, config ), moduleExtractor );
	}
	
	public IncrementalClassifier(PelletReasoner reasoner) {
		this( reasoner, ModuleExtractorFactory.createModuleExtractor() );
	}
	
	public IncrementalClassifier(PelletReasoner reasoner, ModuleExtractor extractor) {
		this.reasoner = reasoner;
		this.extractor = extractor;
		
		OWLOntology ontology = reasoner.getRootOntology();
		for (OWLOntology ont : ontology.getImportsClosure()) { 
			extractor.addOntology( ont );
		}
		
		reasoner.getManager().addOntologyChangeListener( this );
	}
	
	public IncrementalClassifier(PersistedState persistedState) {
		extractor = persistedState.getModuleExtractor();
		taxonomy = persistedState.getTaxonomy();
		realized = persistedState.isRealized();
		
		modules = extractor.getModules();

		OWLOntology ontology = extractor.getAxiomOntology();
		
		reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
		
		reasoner.getManager().addOntologyChangeListener( this );
	}
	
	public IncrementalClassifier(PersistedState persistedState, OWLOntology ontology) {
		reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
		extractor = persistedState.getModuleExtractor();
		taxonomy = persistedState.getTaxonomy();
		realized = persistedState.isRealized();
		
		modules = extractor.getModules();
		
		OntologyDiff diff = OntologyDiff.diffAxiomsWithOntologies( extractor.getAxioms(), Collections.singleton( ontology ) );
		
		if( !diff.areSame() ) {
			for( OWLAxiom addition : diff.getAdditions() ) {
				extractor.addAxiom( addition );
			}
			
			for( OWLAxiom deletion : diff.getDeletions() ) {
				extractor.deleteAxiom( deletion );
			}
		}
		
		reasoner.getManager().addOntologyChangeListener( this );
	}
	
	public Collection<OWLAxiom> getAxioms() {
		return extractor.getAxioms();
	}
	
	/**
	 * Build the class hierarchy based on the results from the reasoner
	 */
	static public Taxonomy<OWLClass> buildClassHierarchy(final PelletReasoner reasoner) {
		
		Taxonomy<OWLClass> taxonomy = new Taxonomy<OWLClass>( null, OWL.Thing, OWL.Nothing );

		Set<OWLClass> things = reasoner.getEquivalentClasses( OWL.Thing ).getEntities();
		things.remove( OWL.Thing );
		if (!things.isEmpty()) {
	        taxonomy.addEquivalents( OWL.Thing, things );
        }
		
		Set<OWLClass> nothings = reasoner.getEquivalentClasses( OWL.Nothing ).getEntities();
		nothings.remove( OWL.Nothing );
		if (!nothings.isEmpty()) {
	        taxonomy.addEquivalents( OWL.Nothing, nothings );
        }

		for (Node<OWLClass> subEq : reasoner.getSubClasses(OWL.Thing, true) ) {
	        recursiveBuild( taxonomy, subEq, reasoner );
        }

		return taxonomy;
	}

	static private void recursiveBuild(Taxonomy<OWLClass> taxonomy, Node<OWLClass> eqClasses, PelletReasoner reasoner) {
		
		assert !eqClasses.getEntities().isEmpty() : "Equivalents empty as passed";

		final OWLClass cls = eqClasses.iterator().next();
		if (taxonomy.contains( cls )) {
	        return;
        }

		final Set<OWLClass> emptySet = Collections.emptySet();
		taxonomy.addNode( eqClasses.getEntities(), emptySet, emptySet, /* hidden =*/false );

		for (Node<OWLClass> subEq : reasoner.getSubClasses(cls, true) ) {
			recursiveBuild( taxonomy, subEq, reasoner );
			taxonomy.addSuper( subEq.iterator().next(), cls );
		}
		
	}
	
	public void classify() {
		if( isClassified() ) {			
			if (extractor.isChanged()) {
				// this means that there are some changes to the modules, which do not affect				
				// the current taxonomy (i.e., that is why isClassified() returns true)
				// let's update the modules here 
				// TODO: maybe we should move these calls somewhere else but in general
				// the users expect that all changes are applied after classify()
				// and unapplied changes will prevent the classifie
				extractor.updateModules( taxonomy, true );
				extractor.updateModules( taxonomy, false );
			}
			return;
		}

		if( extractor.canUpdate() ) {
	        incrementalClassify();
        }
        else {
	        regularClassify();
        }
				
		resetRealization();
	}

	public void dispose() {
		reasoner.dispose();
		
		reasoner.getManager().removeOntologyChangeListener( this );
	}

	public Node<OWLClass> getEquivalentClasses(OWLClassExpression clsC) {
		if( clsC.isAnonymous() ) {
	        throw new IllegalArgumentException( "This reasoner only supports named classes" );
        }

		classify();

		return NodeFactory.getOWLClassNode( taxonomy.getAllEquivalents( (OWLClass) clsC ) );
	}

	/**
	 * Get the modules
	 */
	public MultiValueMap<OWLEntity, OWLEntity> getModules() {
		return modules;
	}

	/**
	 * Get the underlying reasoner
	 */
	public PelletReasoner getReasoner() {
		return reasoner;
	}

	public NodeSet<OWLClass> getSubClasses(OWLClassExpression clsC, boolean direct) {
		if( clsC.isAnonymous() ) {
	        throw new UnsupportedOperationException( "This reasoner only supports named classes" );
        }
		
		classify();

		Set<Node<OWLClass>> values = new HashSet<Node<OWLClass>>();
		for( Set<OWLClass> val : taxonomy.getSubs( (OWLClass) clsC, direct ) ) {
			values.add( NodeFactory.getOWLClassNode( val ) );
		}

		return new OWLClassNodeSet( values );
	}

	/**
	 * This incremental classification strategy does the following: for all
	 * modules that are affected, collect all of their axioms and classify them
	 * all once in Pellet. This allows the exploitation current classification
	 * optimizations
	 * 
	 * @param args
	 */
	private void incClassifyAllModStrategy() {
		// Get the entities whose modules are affected
		Set<OWLEntity> effects = new HashSet<OWLEntity>();

		// collect entities affected by additions
		effects.addAll( extractor.updateModules( taxonomy, true ) );
		// collect entities affected by deletions
		effects.addAll( extractor.updateModules( taxonomy, false ) );
		
		if( log.isLoggable( Level.FINE ) ) {
	        log.fine( "Module entities " + effects );
        }

		// create ontology for all the axioms of all effected modules
		OWLOntology owlModule = extractor.getModuleFromSignature( effects );
		
		if( log.isLoggable( Level.FINE ) ) {
	        log.fine( "Module axioms " + owlModule.getLogicalAxioms() );
        }

		// load the extracted module to a new reasoner
		PelletReasoner moduleReasoner = PelletReasonerFactory.getInstance().createReasoner( owlModule );

		// classify the module
		moduleReasoner.getKB().classify();

		if( log.isLoggable( Level.FINE ) ) {
			log.fine( "Classified module:" );

			new ClassTreePrinter().print( moduleReasoner.getKB().getTaxonomy(), new PrintWriter( System.err ) );
		}

		Taxonomy<OWLClass> moduleTaxonomy = buildClassHierarchy( moduleReasoner );

		Set<OWLClass> affectedCls = new HashSet<OWLClass>();
		for (OWLEntity entity : effects) {
			if (entity instanceof OWLClass) {
				affectedCls.add( (OWLClass) entity );
			}
		}
		taxonomy = updateClassHierarchy( taxonomy, moduleTaxonomy, affectedCls );

		if( log.isLoggable( Level.FINE ) ) {
			log.fine( "Updated taxonomy:" );
			new TreeTaxonomyPrinter<OWLClass>().print( taxonomy, new PrintWriter( System.err ) );
//			new FunctionalTaxonomyPrinter().print( taxonomy, new OutputFormatter( System.err, false ) );
		}

		OntologyUtils.getOWLOntologyManager().removeOntology( owlModule );
	}

	private void incrementalClassify() {
		if( log.isLoggable( Level.FINE ) ) {
	        log.fine( "Incremental classification starting" );
        }

		Timer timer = timers.startTimer( "incrementalClassify" );

		incClassifyAllModStrategy();
		
		timer.stop();

		if( log.isLoggable( Level.FINE ) ) {
	        log.fine( "Incremental classification done" );
        }
	}

	public boolean isClassified() {
		// what if expressivity should change because of the yet unapplied changes?
		return modules != null 
			&& (!extractor.isChanged() || !extractor.isClassificationNeeded(reasoner.getKB().getExpressivity()));
	}
	
	public boolean isRealized() {
		return isClassified() && realized;
	}

	public boolean isDefined(OWLClass cls) {
		return !extractor.getAxioms( cls ).isEmpty();
	}

	public boolean isDefined(OWLDataProperty prop) {
		return !extractor.getAxioms( prop ).isEmpty();
	}

	public boolean isDefined(OWLNamedIndividual ind) {
		return !extractor.getAxioms( ind ).isEmpty();
	}

	public boolean isDefined(OWLObjectProperty prop) {
		return !extractor.getAxioms( prop ).isEmpty();
	}

	public boolean isEquivalentClass(OWLClassExpression clsC, OWLClassExpression clsD) {
		if( clsC.isAnonymous() || clsD.isAnonymous() ) {
	        throw new UnsupportedOperationException( "This reasoner only supports named classes" );
        }

		classify();

		return taxonomy.isEquivalent( (OWLClass) clsC, (OWLClass) clsD ) == Bool.TRUE;
	}

	public boolean isSatisfiable(OWLClassExpression description) {
		if( description.isAnonymous() || !isClassified() ) {
	        return reasoner.isSatisfiable( description );
        }

		return !getUnsatisfiableClasses().contains( (OWLClass) description );
	}

	public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
		if( !getRootOntology().getOWLOntologyManager().contains( getRootOntology().getOntologyID() ) ) {
			return;
		}
		
		Set<OWLOntology> ontologies = getRootOntology().getImportsClosure();
		for( OWLOntologyChange change : changes ) {
			if( !change.isAxiomChange() || !ontologies.contains( change.getOntology() ) ) {
	            continue;
            }

			resetRealization();
			
			OWLAxiom axiom = change.getAxiom();

			if( change instanceof AddAxiom ) {
	            extractor.addAxiom( axiom );
            }
            else if( change instanceof RemoveAxiom ) {
	            extractor.deleteAxiom( axiom );
            }
            else {
	            throw new UnsupportedOperationException( "Unrecognized axiom change: " + change );
            }
		}		
	}

	private void regularClassify() {
		if( log.isLoggable( Level.FINE ) ) {
	        log.fine( "Regular classification starting" );
        }

		Thread classification = new Thread( "classification" ) {
			@Override
            public void run() {
				// classify ontology
				Timer timer = timers.startTimer( "reasonerClassify" );
				reasoner.flush();
				reasoner.getKB().classify();
				timer.stop();

				if( log.isLoggable( Level.FINE ) ) {
					log.fine( "Regular taxonomy:" );

					new TreeTaxonomyPrinter<ATermAppl>().print( reasoner.getKB().getTaxonomy(), new PrintWriter( System.err ) );
				}

				timer = timers.startTimer( "buildClassHierarchy" );
				taxonomy = buildClassHierarchy( reasoner );
				timer.stop();

				if( log.isLoggable( Level.FINE ) ) {
					log.fine( "Copied taxonomy:" );

					new TreeTaxonomyPrinter<OWLClass>().print( taxonomy, new PrintWriter( System.err ) );
				}
			}
		};

		Thread partitioning = new Thread( "partitioning" ) {
			@Override
            public void run() {
				// get modules for each concept
				modules = extractor.extractModules();
			}
		};

		try {
			Timer timer = timers.startTimer( "regularClassify" );

			
			if( multiThreaded ) {
				classification.start();
				partitioning.start();
				
				classification.join();			
				partitioning.join();
			}
			else {
				classification.run();
				partitioning.run();				
			}

			timer.stop();
		} catch( InterruptedException e ) {
			throw new RuntimeException( e );
		}		

		if( log.isLoggable( Level.FINE ) ) {
	        log.fine( "Regular classification done" );
        }
	}
	
	/**
	 * @param taxonomy Previous taxonomy state
	 * @param moduleTaxonomy Change in taxonomy state
	 * @param affected Set of classes affected by changes
	 */
	private Taxonomy<OWLClass> updateClassHierarchy(Taxonomy<OWLClass> taxonomy,
			Taxonomy<OWLClass> moduleTaxonomy, Set<OWLClass> affected) {

		Set<OWLClass> inTaxonomy = new HashSet<OWLClass>( moduleTaxonomy.getClasses() );
		inTaxonomy.remove( OWL.Thing );
		inTaxonomy.remove( OWL.Nothing );
		assert affected.containsAll( inTaxonomy ) : "Unaffected nodes in changed taxonomy";

		Set<OWLClass> removed = new HashSet<OWLClass>( affected );
		removed.removeAll( moduleTaxonomy.getClasses() );

		List<OWLClass> sorted = taxonomy.topologocialSort( /* includeEquivalents = */false );

		// TODO: Top equivalents could change?!
		final Set<OWLClass> emptySet = Collections.emptySet();
		for( OWLClass cls : sorted ) {

			// TODO: assert assumption that if any classes equivalent in
			// taxonomy exist in moduleTaxonomy, all are
			if( removed.contains( cls ) || moduleTaxonomy.contains( cls ) ) {
	            continue;
            }

			moduleTaxonomy.addNode( taxonomy.getAllEquivalents( cls ), emptySet, emptySet, /* hidden= */
			false );
			Set<OWLClass> supers = taxonomy.getFlattenedSupers( cls, /* direct = */true );
			supers.removeAll( removed );
			moduleTaxonomy.addSupers( cls, supers );
		}

		List<OWLClass> nothings = new ArrayList<OWLClass>();
		for( OWLClass cls : taxonomy.getEquivalents( OWL.Nothing ) ) {
			if( !removed.contains( cls ) && !moduleTaxonomy.contains( cls ) ) {
	            nothings.add( cls );
            }
		}
		if( !nothings.isEmpty() ) {
	        moduleTaxonomy.addEquivalents( OWL.Nothing, nothings );
        }

		return moduleTaxonomy;
	}	

//	/**
//	 * FIXME: This function is incredibly broken.
//	 * Main method to update the partial order and find new subsumptions.
//	 * 
//	 * @param add
//	 *            Flag for additions/deletions
//	 * @param node
//	 *            the node which is being updated
//	 * @param newR
//	 *            The reasoner which has been loaded and processed with the new
//	 *            signature of this module
//	 */
//	private void updatePartialOrder(boolean add, TaxonomyNode<OWLClass> node, PelletReasoner newR) {
//		if( log.isLoggable( Level.FINER ) )
//			log.finer( "Update node " + node );
//
//		OWLClass clazz = node.getName();
//
//		// collect all of its valid super classes from the reasoner
//		Set<Set<OWLClass>> validSupers = newR.getAncestorClasses( clazz );
//
//		// get all of the old super classes from the subclass index
//		Set<OWLClass> oldSupers = null;// superClasses.get( clazz );
//
//		// case for add
//		if( add ) {
//
//			// for each subclass check if its new...if it is, then add it to
//			// the child of this node
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
//						// add it to the subclass index
//						oldSupers.add( next );
//
//						if( log.isLoggable( Level.FINER ) ) {
//							log.finer( "  Found new subsumption " + clazz + " subClassOf " + next );
//						}
//
//						// update the partial order
//						TaxonomyNode<OWLClass> newSubNode = taxonomy.getNode( next );
//						if( !newSubNode.getSubs().contains( node ) ) {
//							newSubNode.addSub( node );
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
//				TaxonomyNode<OWLClass> oldSubNode = taxonomy.getNode( nextOldSuper );
//
//				// check if this subsumption still exists
//				if( !OntologyUtils.containsClass( validSupers, nextOldSuper ) ) {
//					// update remove set
//					removeSet.add( nextOldSuper );
//
//					if( log.isLoggable( Level.FINER ) ) {
//						log.finer( "  Found violated subsumption " + clazz + " subClassOf "
//								+ nextOldSuper );
//					}
//
//					// update partial order
//					oldSubNode.removeSub( node );
//				}
//			}
//
//			// update subclass index
//			oldSupers.removeAll( removeSet );
//		}
//	}

	/**
	 * Returns the value of multi-threaded option.
	 * 
	 * @see IncrementalClassifier#setMultiThreaded(boolean)
	 * @return the value of multi-threaded option
	 */
	public boolean isMultiThreaded() {
		return multiThreaded;
	}

	/**
	 * Sets the multi-threading option. In multi-threaded mode, during the
	 * initial setup, the regular classification and module extraction are
	 * performed in two separate threads concurrently. Doing so might reduce
	 * overall processing time but it also increases the memory requirements
	 * because both processes need additional memory during running which will
	 * be freed at the end of the process.
	 * 
	 * @param multiThreaded value to set the multi-threaded option
	 */
	public void setMultiThreaded(boolean multiThreaded) {
		this.multiThreaded = multiThreaded;
	}

	/**
	 * A class that has access to all the internal parts of the IncrementalClassifier that has to be persisted when saving
	 * the state to the stream. This class enables the separation between the I/O code and the reasoning code. This class should 
	 * not be used any other parts of code than the I/O code.
	 * 
	 * This class is a variation of Memento design pattern (as it encapsulates the state) with the 
	 * important difference that actually no state is being copied; instead, this class only contains the references to the
	 * data structures to be saved. (The I/O code is thought as actually performing the copy, and therefore completing the Memento pattern.)
	 *
     * <p>
     * Copyright: Copyright (c) 2009
     * </p>
     * <p>
     * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
     * </p>
 	 * 
	 * @author Blazej Bulka
	 */
	public static class PersistedState {
		private final ModuleExtractor extractor;
		private final Taxonomy<OWLClass> taxonomy;
		private final boolean realized;
		
		public PersistedState( IncrementalClassifier incrementalClassifier ) {
			this.extractor = incrementalClassifier.extractor;
			this.taxonomy = incrementalClassifier.taxonomy;
			this.realized = incrementalClassifier.realized;
		}
		
		public PersistedState( ModuleExtractor extractor, Taxonomy<OWLClass> taxonomy, boolean realized ) {
			this.extractor = extractor;
			this.taxonomy = taxonomy;
			this.realized = realized;
		}
		
		public ModuleExtractor getModuleExtractor() {
			return extractor;
		}

		public Taxonomy<OWLClass> getTaxonomy() {
			return taxonomy;
		}
		
		public boolean isRealized() {
			return realized;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void flush() {
		reasoner.flush();
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	public Node<OWLClass> getBottomClassNode() {
		return getEquivalentClasses( OWL.Nothing );
	}

	/**
	 * {@inheritDoc}
	 */
	public Node<OWLDataProperty> getBottomDataPropertyNode() {
		return getEquivalentDataProperties( OWL.bottomDataProperty );
	}

	/**
	 * {@inheritDoc}
	 */
	public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
		return getEquivalentObjectProperties( OWL.bottomObjectProperty );
	}

	/**
	 * {@inheritDoc}
	 */
	public BufferingMode getBufferingMode() {
		return BufferingMode.NON_BUFFERING;
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty pe, boolean direct)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		reasoner.flush();
		
		return reasoner.getDataPropertyDomains( pe, direct );
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<OWLLiteral> getDataPropertyValues(OWLNamedIndividual ind, OWLDataProperty pe)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		reasoner.flush();
		
		return reasoner.getDataPropertyValues( ind, pe );
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeSet<OWLNamedIndividual> getDifferentIndividuals(OWLNamedIndividual ind)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		reasoner.flush();
		
		return reasoner.getDifferentIndividuals( ind );
	}
	
	/**
	 * {@inheritDoc}
	 */
	public NodeSet<OWLClass> getDisjointClasses(OWLClassExpression ce) {		
		DisjointClassComparator disjointClassComparator = new DisjointClassComparator( taxonomy, ce );
		
		if( !taxonomy.contains( disjointClassComparator.getComplementRepresentation() ) ) {
			reasoner.flush();	
			PartialOrderBuilder<OWLClass> orderBuilder = new PartialOrderBuilder<OWLClass>(taxonomy, disjointClassComparator);
		
			orderBuilder.add( disjointClassComparator.getComplementRepresentation(), true ); 		
		}
		
		OWLClassNodeSet result = new OWLClassNodeSet();
		
		for (Set<OWLClass> equivSet : taxonomy.getSubs( disjointClassComparator.getComplementRepresentation(), false ) ) {
			result.addSameEntities( equivSet );
		}
		
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeSet<OWLDataProperty> getDisjointDataProperties(OWLDataPropertyExpression pe)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		reasoner.flush();
		
		return reasoner.getDisjointDataProperties( pe );

	}

	/**
	 * {@inheritDoc}
	 */
	public NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(OWLObjectPropertyExpression pe)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		reasoner.flush();
		
		return reasoner.getDisjointObjectProperties( pe );
	}

	/**
	 * {@inheritDoc}
	 */
	public Node<OWLDataProperty> getEquivalentDataProperties(OWLDataProperty pe)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		reasoner.flush();
		
		return reasoner.getEquivalentDataProperties( pe );
	}

	/**
	 * {@inheritDoc}
	 */
	public Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(OWLObjectPropertyExpression pe)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		reasoner.flush();
		
		return reasoner.getEquivalentObjectProperties( pe );
	}

	/**
	 * {@inheritDoc}
	 */
	public FreshEntityPolicy getFreshEntityPolicy() {
		return reasoner.getFreshEntityPolicy();
	}

	/**
	 * {@inheritDoc}
	 */
	public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
		return reasoner.getIndividualNodeSetPolicy();
	}

	private NodeSet<OWLNamedIndividual> getIndividualNodeSetBySameAs( Collection<OWLNamedIndividual> individuals ) {
		Set<Node<OWLNamedIndividual>> instances = new HashSet<Node<OWLNamedIndividual>>();
		Set<OWLNamedIndividual> seen = new HashSet<OWLNamedIndividual>();
		for( OWLNamedIndividual ind : individuals ) {
			if( !seen.contains( ind ) ) {
				Node<OWLNamedIndividual> equiv = reasoner.getSameIndividuals( ind );
				instances.add( equiv );
				seen.addAll( equiv.getEntities() );
			}
		}

		return new OWLNamedIndividualNodeSet( instances );
	}
	
	private NodeSet<OWLNamedIndividual> getIndividualNodeSetByName( Collection<OWLNamedIndividual> individuals ) {
		Set<Node<OWLNamedIndividual>> instances = new HashSet<Node<OWLNamedIndividual>>();
		
		for( OWLNamedIndividual ind : individuals ) {			
			for ( OWLNamedIndividual equiv : reasoner.getSameIndividuals( ind ) ) {				 
				instances.add( new OWLNamedIndividualNode( equiv ) );			
			}
		}
		
		return new OWLNamedIndividualNodeSet( instances );
	}
	
	private NodeSet<OWLNamedIndividual> getIndividualNodeSet( Collection<OWLNamedIndividual> individuals ) {
		if ( IndividualNodeSetPolicy.BY_NAME.equals( getIndividualNodeSetPolicy() ) ) {
			return getIndividualNodeSetByName( individuals );
		} else if ( IndividualNodeSetPolicy.BY_SAME_AS.equals( getIndividualNodeSetPolicy() ) ) {
			return getIndividualNodeSetBySameAs( individuals );
		} else {
			throw new AssertionError( "Unsupported IndividualNodeSetPolicy : " + getIndividualNodeSetPolicy() );
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression ce, boolean direct)
			throws InconsistentOntologyException, ClassExpressionNotInProfileException,
			FreshEntitiesException, ReasonerInterruptedException, TimeOutException {		
		if( ce.isAnonymous() && direct) {
	        throw new UnsupportedOperationException( "This reasoner only supports named classes" );
        }
		
		reasoner.flush();		
	
		if( !isRealized() && !direct ) {
			return reasoner.getInstances( ce, direct );
		}
	
		realize();
		
		Set<OWLNamedIndividual> individuals = direct
			? TaxonomyUtils.<OWLClass, OWLNamedIndividual>getDirectInstances(taxonomy, (OWLClass) ce)
			: TaxonomyUtils.<OWLClass, OWLNamedIndividual>getAllInstances(taxonomy, (OWLClass) ce);				
					
		return getIndividualNodeSet( individuals );				
	}

	/**
	 * {@inheritDoc}
	 */
	public Node<OWLObjectPropertyExpression> getInverseObjectProperties(OWLObjectPropertyExpression pe)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		reasoner.flush();
		
		return reasoner.getInverseObjectProperties( pe );
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeSet<OWLClass> getObjectPropertyDomains(OWLObjectPropertyExpression pe, boolean direct)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		reasoner.flush();
		
		return reasoner.getObjectPropertyDomains( pe, direct );
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeSet<OWLClass> getObjectPropertyRanges(OWLObjectPropertyExpression pe, boolean direct)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		reasoner.flush();
		
		return reasoner.getObjectPropertyRanges( pe, direct );
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeSet<OWLNamedIndividual> getObjectPropertyValues(OWLNamedIndividual ind,
			OWLObjectPropertyExpression pe) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
		reasoner.flush();
		
		return reasoner.getObjectPropertyValues( ind, pe );

	}

	/**
	 * {@inheritDoc}
	 */
	public Set<OWLAxiom> getPendingAxiomAdditions() {
		return Collections.emptySet();
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<OWLAxiom> getPendingAxiomRemovals() {
		return Collections.emptySet();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<OWLOntologyChange> getPendingChanges() {
		return Collections.emptyList();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getReasonerName() {
		return "Pellet (Incremental)";
	}

	/**
	 * {@inheritDoc}
	 */
	public Version getReasonerVersion() {
		return reasoner.getReasonerVersion();
	}

	/**
	 * {@inheritDoc}
	 */
	public OWLOntology getRootOntology() {
		return reasoner.getRootOntology();
	}

	/**
	 * {@inheritDoc}
	 */
	public Node<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual ind)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		reasoner.flush();
		
		return reasoner.getSameIndividuals( ind );
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeSet<OWLDataProperty> getSubDataProperties(OWLDataProperty pe, boolean direct)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		reasoner.flush();
		
		return reasoner.getSubDataProperties( pe, direct );
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(OWLObjectPropertyExpression pe,
			boolean direct) throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		reasoner.flush();
		
		return reasoner.getSubObjectProperties( pe, direct );
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeSet<OWLClass> getSuperClasses(OWLClassExpression ce, boolean direct)
			throws InconsistentOntologyException, ClassExpressionNotInProfileException,
			FreshEntitiesException, ReasonerInterruptedException, TimeOutException {
		if( ce.isAnonymous() ) {
	        throw new UnsupportedOperationException( "This reasoner only supports named classes" );
        }
		
		classify();

		Set<Node<OWLClass>> values = new HashSet<Node<OWLClass>>();
		for( Set<OWLClass> val : taxonomy.getSupers( (OWLClass) ce, direct ) ) {
			values.add( NodeFactory.getOWLClassNode( val ) );
		}

		return new OWLClassNodeSet( values );
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeSet<OWLDataProperty> getSuperDataProperties(OWLDataProperty pe, boolean direct)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		reasoner.flush();

		return reasoner.getSuperDataProperties( pe, direct );
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(OWLObjectPropertyExpression pe,
			boolean direct) throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		reasoner.flush();

		return reasoner.getSuperObjectProperties( pe, direct );
	}

	/**
	 * {@inheritDoc}
	 */
	public long getTimeOut() {
		return reasoner.getTimeOut();
	}

	/**
	 * {@inheritDoc}
	 */
	public Node<OWLClass> getTopClassNode() {
		return getEquivalentClasses( OWL.Thing );
	}

	/**
	 * {@inheritDoc}
	 */
	public Node<OWLDataProperty> getTopDataPropertyNode() {
		return getEquivalentDataProperties( OWL.topDataProperty );
	}

	/**
	 * {@inheritDoc}
	 */
	public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
		return getEquivalentObjectProperties( OWL.topObjectProperty );
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeSet<OWLClass> getTypes(OWLNamedIndividual ind, boolean direct)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		reasoner.flush();
		realize();
		
		OWLClassNodeSet types = new OWLClassNodeSet();
		for( Set<OWLClass> t : TaxonomyUtils.getTypes( taxonomy, ind, direct ) ) {		
			//Set<OWLClass> eqSet = ATermUtils.primitiveOrBottom( t );
			//if( !eqSet.isEmpty() )
				types.addNode( new OWLClassNode( t ) );
		}

		return types;

	}

	/**
	 * {@inheritDoc}
	 */
	public Node<OWLClass> getUnsatisfiableClasses() throws ReasonerInterruptedException,
			TimeOutException {
		classify();
		
		return getBottomClassNode();
	}

	/**
	 * {@inheritDoc}
	 */
	public void interrupt() {
		
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isConsistent() throws ReasonerInterruptedException, TimeOutException {
		reasoner.flush();
		
		return reasoner.isConsistent();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEntailed(OWLAxiom axiom) throws ReasonerInterruptedException,
			UnsupportedEntailmentTypeException, TimeOutException, AxiomNotInProfileException,
			FreshEntitiesException {
		try {
			EntailmentChecker entailmentChecker = new EntailmentChecker( this );
			return entailmentChecker.isEntailed( axiom );
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEntailed(Set<? extends OWLAxiom> axioms) throws ReasonerInterruptedException,
			UnsupportedEntailmentTypeException, TimeOutException, AxiomNotInProfileException,
			FreshEntitiesException {
		try {
			EntailmentChecker entailmentChecker = new EntailmentChecker( this );
			return entailmentChecker.isEntailed( axioms );
		} catch( PelletRuntimeException e ) {
			throw convert( e );
		}
	}
	
	private PelletRuntimeException convert(PelletRuntimeException e) throws InconsistentOntologyException,
			ReasonerInterruptedException, TimeOutException, FreshEntitiesException {
		
		if( e instanceof org.mindswap.pellet.exceptions.TimeoutException ) {
			throw new TimeOutException();
		}
		
		if( e instanceof org.mindswap.pellet.exceptions.TimerInterruptedException ) {
			throw new ReasonerInterruptedException( e );
		}
		
		if( e instanceof org.mindswap.pellet.exceptions.InconsistentOntologyException ) {
			throw new InconsistentOntologyException();
		}
		
		if( e instanceof org.mindswap.pellet.exceptions.UndefinedEntityException ) {
			Set<OWLEntity> unknown = Collections.emptySet();
			throw new FreshEntitiesException( unknown );
		}
		
		return e;
		}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEntailmentCheckingSupported(AxiomType<?> axiomType) {
		// the current EntailmentChecker supports the same set of axioms as 
		// the underlying reasoner (if it cannot handle any element directly,
		// it forwards the entailment check to the underlying reasoner)
		return this.getReasoner().isEntailmentCheckingSupported( axiomType );
	}

	/**
	 * {@inheritDoc}
	 */
	public void prepareReasoner() throws ReasonerInterruptedException, TimeOutException {
		classify();
	}
	
	public Taxonomy<OWLClass> getTaxonomy() {
		return taxonomy;
	}
	
	private void resetRealization() {
		if ( taxonomy != null ) {
			for( TaxonomyNode<OWLClass> node : taxonomy.getNodes() ) {
				node.removeDatum( TaxonomyUtils.INSTANCES_KEY );
			}
		}
		
		realized = false;
	}
	
	private void realize() {
		if (isRealized()) {
	        return;
        }
		
		Set<ATermAppl> allIndividuals = reasoner.getKB().getIndividuals();
		
		Set<OWLClass> visitedClasses = new HashSet<OWLClass>();
		
		if( !allIndividuals.isEmpty() ) {
			realizeByConcept( ATermUtils.TOP, allIndividuals, reasoner.getManager().getOWLDataFactory(), visitedClasses );
		}
		
		realized = true;
	}
	
	private Set<ATermAppl> realizeByConcept( ATermAppl c, Collection<ATermAppl> individuals, OWLDataFactory factory, Set<OWLClass> visitedClasses ) {
		if( c.equals( ATermUtils.BOTTOM ) ) {
	        return SetUtils.emptySet();
        }
		
		if( log.isLoggable( Level.FINER ) ) {
	        log.finer( "Realizing concept " + c );
        }
		
		OWLClass owlClass = termToOWLClass( c, factory );
		
		if( visitedClasses.contains( owlClass ) ) {
			return TaxonomyUtils.getAllInstances(taxonomy, owlClass);
		}		

		Set<ATermAppl> instances = new HashSet<ATermAppl>( reasoner.getKB().retrieve( c, individuals ) );
		Set<ATermAppl> mostSpecificInstances = new HashSet<ATermAppl>( instances );
		
		if( !instances.isEmpty() ) {
			TaxonomyNode<OWLClass> node = taxonomy.getNode( owlClass );
			
			if (node == null) {
				System.out.println(" no node for " + c );
			}

			for( TaxonomyNode<OWLClass> sub : node.getSubs() ) {
				OWLClass d = sub.getName();
				Set<ATermAppl> subInstances = realizeByConcept( owlClassToTerm( d ), instances, factory, visitedClasses );

				if( subInstances == null ) {
					return null;
				}

				mostSpecificInstances.removeAll( subInstances );
			}

			if( !mostSpecificInstances.isEmpty() ) {
				node.putDatum( TaxonomyUtils.INSTANCES_KEY, toOWLNamedIndividuals( mostSpecificInstances, factory ) );
			}
		}

		return instances;
	}
	
	private Set<OWLNamedIndividual> toOWLNamedIndividuals( Set<ATermAppl> terms, OWLDataFactory factory ) {
		HashSet<OWLNamedIndividual> result = new HashSet<OWLNamedIndividual>();
		
		for( ATermAppl ind : terms ) {
			OWLNamedIndividual owlInd = termToOWLNamedIndividual( ind, factory );			
			if( owlInd != null ) {
				result.add( owlInd );
			} 
		}
		
		return result;
	}
	
	private static final ATermAppl OWL_THING   = ATermUtils.makeTermAppl(Namespaces.OWL + "Thing");
    private static final ATermAppl OWL_NOTHING = ATermUtils.makeTermAppl(Namespaces.OWL + "Nothing");
	
	private OWLClass termToOWLClass( ATermAppl c, OWLDataFactory factory ) {
		if ( c.equals( ATermUtils.TOP) ) {
	        return factory.getOWLThing();
        }
        else if( c.equals( OWL_THING ) ) {
	        return factory.getOWLThing();
        }
        else if( c.equals( OWL_NOTHING ) ) {
	        return factory.getOWLNothing();
        }
		
		if ( !ATermUtils.isBnode( c ) ) {
	        return factory.getOWLClass( IRI.create( c.getName() ) );
        }
		
		return null;
	}
	
	private OWLNamedIndividual termToOWLNamedIndividual( ATermAppl c, OWLDataFactory factory ) {
		if ( !ATermUtils.isBnode( c ) ) {
	        return factory.getOWLNamedIndividual( IRI.create( c.getName() ) );
        }
		
		return null;
	}
	
	private ATermAppl owlClassToTerm( OWLClass c ) {
		if( c.isOWLThing() ) {
	        return ATermUtils.TOP;
        }
        else if( c.isOWLNothing() ) {
	        return ATermUtils.BOTTOM;
        }
        else {
	        return ATermUtils.makeTermAppl( c.getIRI().toString() );
        }	
	}	
	
	public class DisjointClassComparator implements PartialOrderComparator<OWLClass> {
		private static final String ANONYMOUS_COMPLEMENT_REPRESENTATION_BASE = "http://clarkparsia.com/pellet/complement/";
		private static final String COMPLEMENT_POSTFIX = "-complement";
		
		private final OWLClassExpression complementClass;
		private final OWLClass complementRepresentation;
		
		public DisjointClassComparator(Taxonomy<OWLClass> taxonomy, OWLClassExpression originalClass) {
			this.complementClass = OWL.factory.getOWLObjectComplementOf( originalClass );
			this.complementRepresentation = generateComplementRepresentation(taxonomy, originalClass);		
		}
		
		private OWLClass generateComplementRepresentation(Taxonomy<OWLClass> taxonomy, OWLClassExpression originalClass) {
			OWLClass complementClass = null;
			
			if( !originalClass.isAnonymous() && ( originalClass instanceof OWLClass ) ) {
				return OWL.factory.getOWLClass( IRI.create( ( (OWLClass) originalClass ).getIRI() + COMPLEMENT_POSTFIX ) );
			}
			
			do {
				complementClass = OWL.factory.getOWLClass( IRI.create( ANONYMOUS_COMPLEMENT_REPRESENTATION_BASE + RND.nextLong()) );
			} while ( taxonomy.contains( complementClass ) );
			
			return complementClass;			
		}
		
		public OWLClass getComplementRepresentation() {
			return complementRepresentation;
		}
		
		public PartialOrderRelation compare( OWLClass a, OWLClass b ) {
			OWLClassExpression aExpression = a;
			OWLClassExpression bExpression = b;
			
			if( a.equals( complementRepresentation ) ) {
				aExpression = complementClass;
			}
			
			if( b.equals( complementRepresentation ) ) {
				bExpression = complementClass;
			}

			OWLAxiom aSubClassBAxiom = OWL.factory.getOWLSubClassOfAxiom( aExpression, bExpression );
			OWLAxiom bSubClassAAxiom = OWL.factory.getOWLSubClassOfAxiom( bExpression, aExpression );
			
 			boolean aLessB = reasoner.isEntailed( aSubClassBAxiom );
 			boolean bLessA = reasoner.isEntailed( bSubClassAAxiom );
			
 			if( aLessB && bLessA ) {
 				return PartialOrderRelation.EQUAL;
 			} else if( aLessB ) {
 				return PartialOrderRelation.LESS;
 			} else if( bLessA ) {
 				return PartialOrderRelation.GREATER;
 			} else {
 				return PartialOrderRelation.INCOMPARABLE;
 			}
		}		
	}
	

	/**
     * {@inheritDoc}
     */
    public Set<InferenceType> getPrecomputableInferenceTypes() {
	    return reasoner.getPrecomputableInferenceTypes();
    }

	/**
     * {@inheritDoc}
     */
    public boolean isPrecomputed(InferenceType inferenceType) {
		switch (inferenceType) {
			case CLASS_HIERARCHY:
				return isClassified();
			case CLASS_ASSERTIONS:
				return isRealized();
			default:
				return reasoner.isPrecomputed(inferenceType);
		}
    }

	/**
     * {@inheritDoc}
     */
    public void precomputeInferences(InferenceType... inferenceTypes) throws ReasonerInterruptedException,
                    TimeOutException, InconsistentOntologyException {
    	for (InferenceType inferenceType : inferenceTypes) {
    		switch (inferenceType) {
    			case CLASS_HIERARCHY:
    				classify();
    			case CLASS_ASSERTIONS:
    				realize();
    			default:
    				reasoner.precomputeInferences(inferenceTypes);
    		}
        }
    }
}
