// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.taxonomy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.Individual;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.tbox.TBox;
import org.mindswap.pellet.tbox.impl.Unfolding;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.MemUtils;
import org.mindswap.pellet.utils.SetUtils;
import org.mindswap.pellet.utils.TaxonomyUtils;
import org.mindswap.pellet.utils.Timer;
import org.mindswap.pellet.utils.iterator.IteratorUtils;
import org.mindswap.pellet.utils.progress.ProgressMonitor;
import org.mindswap.pellet.utils.progress.SilentProgressMonitor;

import aterm.ATermAppl;
import aterm.ATermList;

import com.clarkparsia.pellet.utils.CollectionUtils;

/**
 * <p>
 * Title: CD Optimized Taxonomy Builder
 * </p>
 * <p>
 * Description: Taxonomy Builder implementation optimized for completely defined
 * concepts
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Mike Smith
 */
public class CDOptimizedTaxonomyBuilder implements TaxonomyBuilder {
	protected static Logger					log				= Logger.getLogger( Taxonomy.class.getName() );

	protected ProgressMonitor				monitor			= PelletOptions.USE_CLASSIFICATION_MONITOR
																	.create();
	
	private static enum Propogate { 
		UP, DOWN, NONE 
	}

	private static enum ConceptFlag {
		COMPLETELY_DEFINED, PRIMITIVE, NONPRIMITIVE, NONPRIMITIVE_TA, OTHER
	}
	
	private static final Set<ConceptFlag>	PHASE1_FLAGS	= EnumSet.of(
																	ConceptFlag.COMPLETELY_DEFINED,
																	ConceptFlag.PRIMITIVE,
																	ConceptFlag.OTHER );	
	
	protected Collection<ATermAppl>			classes;

	private Map<ATermAppl, Set<ATermAppl>>	toldDisjoints;

	private Map<ATermAppl, ATermList>		unionClasses;

	private DefinitionOrder					definitionOrder;

	protected Taxonomy<ATermAppl>			toldTaxonomy;

	protected Taxonomy<ATermAppl>			taxonomy;
	protected KnowledgeBase					kb;

	private boolean							useCD;

	private List<TaxonomyNode<ATermAppl>>	markedNodes;

	private Map<ATermAppl, ConceptFlag>	conceptFlags;

	public CDOptimizedTaxonomyBuilder() {

	}

	public void setKB(KnowledgeBase kb) {
		this.kb = kb;
	}

	public void setProgressMonitor(ProgressMonitor monitor) {
		if( monitor == null ) {
	        this.monitor = new SilentProgressMonitor();
        }
        else {
	        this.monitor = monitor;
        }
	}

	private boolean	prepared	= false;

	public Taxonomy<ATermAppl> getTaxonomy() {
		return taxonomy;
	}

	// TODO optimize();
	public Taxonomy<ATermAppl> getToldTaxonomy() {
		if( !prepared ) {
			reset();
			computeToldInformation();
		}

		return toldTaxonomy;
	}

	// TODO optimize();
	public Map<ATermAppl, Set<ATermAppl>> getToldDisjoints() {
		if( !prepared ) {
			reset();
			computeToldInformation();
		}

		return toldDisjoints;
	}

	/**
	 * Classify the KB.
	 */
	public boolean classify() {		
		classes = kb.getClasses();
		
		int classCount = classes.size();
		if( !classes.contains( ATermUtils.TOP ) ) {
	        classCount++;
        }
		if( !classes.contains( ATermUtils.BOTTOM ) ) {
	        classCount++;
        }
		
		monitor.setProgressTitle( "Classifying" );
		monitor.setProgressLength( classCount );
		monitor.taskStarted();

		if( classes.isEmpty() ) {
			taxonomy = new Taxonomy<ATermAppl>( null, ATermUtils.TOP, ATermUtils.BOTTOM );
			return true;
		}

		if( log.isLoggable( Level.FINE ) ) {
			kb.timers.createTimer( "classifySub" );
			log.fine( "Classes: " + classCount + " Individuals: "
					+ kb.getIndividuals().size() );
		}

		if( !prepared ) {
			Timer t = kb.timers.startTimer( "taxBuilder.prepare" );
			prepare();
			t.stop();
		}

		if( log.isLoggable( Level.FINE ) ) {
			log.fine( "Starting classification..." );
		}
		
		Iterator<ATermAppl> phase1, phase2;

		if( useCD ) {
			List<ATermAppl> phase1List = new ArrayList<ATermAppl>();
			List<ATermAppl> phase2List = new ArrayList<ATermAppl>();
			for( ATermAppl c : definitionOrder ) {
				if( PHASE1_FLAGS.contains( conceptFlags.get( c ) ) ) {				
					phase1List.add( c );
				}
				else {
					phase2List.add( c );
				}
			}

			if( log.isLoggable( Level.FINE ) ) {
				log.fine( "Using CD classification with phase1: " + phase1List.size()
						+ " phase2: " + phase2List.size() );
				logList( Level.FINER, "Phase 1", phase1List );
				logList( Level.FINER, "Phase 2", phase2List );
			}
			
			phase1 = phase1List.iterator();
			phase2 = phase2List.iterator();
		}
		else {
			phase1 = IteratorUtils.emptyIterator();
			phase2 = definitionOrder.iterator();

			if( log.isLoggable( Level.FINE ) ) {
	            log.fine( "CD classification disabled" );
            }
		}

		boolean completed = true;
		
		completed = completed && classify( phase1, /* requireTopSearch = */false );
		completed = completed && classify( phase2, /* requireTopSearch = */true );

		monitor.taskFinished();

		if( log.isLoggable( Level.FINE ) ) {
			log.fine( "Satisfiability Count: "
					+ (kb.getABox().stats.satisfiabilityCount - (2 * kb.getClasses().size())) );
		}

		// Reset the definition order, so the sorted copy can be gc'd
		definitionOrder = null;

		taxonomy.assertValid();
		
		if( log.isLoggable( Level.FINER ) ) {
			log.finer( "Tax size : " + taxonomy.getNodes().size() );
			log.finer( "Tax depth: " + taxonomy.depth );
			log.finer( "Branching: " + ((double)taxonomy.totalBranching)/taxonomy.getNodes().size() );
			log.finer( "Leaf size: " + taxonomy.getBottom().getSupers().size() );
		}

		return completed;
	}
	
	private void logList(Level level, String header, List<ATermAppl> list) {
		if( !log.isLoggable( Level.FINER ) ) {
			return;
		}
		log.log( level, header );
		int i = 0;
		for(ATermAppl c : list ) {
			log.log( level, i + ") " + c );
			i++;
		}
	}
	
	protected boolean classify(Iterator<ATermAppl> phase, boolean requireTopSearch) {
//		int lastPercent = -1;
		
		while( phase.hasNext() ) {

			ATermAppl c = phase.next();
			
			if( log.isLoggable( Level.FINE ) ) {
	            log.fine( "Classify (" + taxonomy.getNodes().size() + ") " + format( c ) + "..." );
            }
				
			classify( c, requireTopSearch );
			monitor.incrementProgress();

//			int percent = monitor.getProgressPercent();
//			if( percent != lastPercent ) {
//				lastPercent = percent;
//				printStats();
//				if( percent % 20 == 0 )
//					printMemory();
//			}
			
			kb.timers.getTimer( "classify" ).check();
			
			if( monitor.isCanceled() ) {
				return false;
			}
		}
		
		return true;
	}

	private void prepare() {
		reset();

		computeToldInformation();

		createDefinitionOrder();

		computeConceptFlags();

		prepared = true;
	}

	protected void reset() {
		kb.prepare();
		
		classes = new ArrayList<ATermAppl>( kb.getClasses() );

		useCD = PelletOptions.USE_CD_CLASSIFICATION 
				&& !kb.getTBox().unfold( ATermUtils.TOP ).hasNext()
				&& !kb.getExpressivity().hasNominal();

		toldDisjoints = CollectionUtils.makeIdentityMap();
		unionClasses = CollectionUtils.makeIdentityMap();
		markedNodes = CollectionUtils.makeList();

		taxonomy = new Taxonomy<ATermAppl>( null, ATermUtils.TOP, ATermUtils.BOTTOM );

		toldTaxonomy = new Taxonomy<ATermAppl>();

		definitionOrder = null;

		conceptFlags = CollectionUtils.makeIdentityMap();
	}

	private void computeToldInformation() {
		Timer t = kb.timers.startTimer( "computeToldInformation" );

		toldTaxonomy = new Taxonomy<ATermAppl>( classes, ATermUtils.TOP, ATermUtils.BOTTOM );

		// compute told subsumers for each concept
		TBox tbox = kb.getTBox();
		Collection<ATermAppl> axioms = tbox.getAxioms();
		for( ATermAppl axiom : axioms ) {
			ATermAppl c1 = (ATermAppl) axiom.getArgument( 0 );
			ATermAppl c2 = (ATermAppl) axiom.getArgument( 1 );

			boolean equivalent = axiom.getAFun().equals( ATermUtils.EQCLASSFUN );
			Set<ATermAppl> explanation = tbox.getAxiomExplanation( axiom );

			boolean reverseArgs = !ATermUtils.isPrimitive( c1 ) && ATermUtils.isPrimitive( c2 );
			if( equivalent && reverseArgs ) {
	            addToldRelation( c2, c1, equivalent, explanation );
            }
            else {
	            addToldRelation( c1, c2, equivalent, explanation );
            }
		}

		// additional step for union classes. for example, if we have
		// C = or(A, B)
		// and both A and B subclass of D then we can conclude C is also
		// subclass of D
		for( ATermAppl c : unionClasses.keySet() ) {

			List<ATermAppl> list = new ArrayList<ATermAppl>();
			for( ATermList disj = unionClasses.get( c ); !disj.isEmpty(); disj = disj.getNext() ) {
				list.add( (ATermAppl) disj.getFirst() );
			}
			List<ATermAppl> lca = toldTaxonomy.computeLCA( list );

			for( ATermAppl d : lca ) {
				if( log.isLoggable( Level.FINER ) ) {
	                log.finer( "Union subsumption " + format( c ) + " " + format( d ) );
                }

				addToldSubsumer( c, d );
			}
		}

		// we don't need this any more so set it null and let GC claim it
		unionClasses = null;

		toldTaxonomy.assertValid();

		t.stop();
	}

	private void createDefinitionOrder() {
		definitionOrder = DefinitionOrderFactory.createDefinitionOrder( kb );
	}

	private void computeConceptFlags() {
		if( !useCD ) {
	        return;
        }

		/*
		 * Use RBox domain axioms to mark some concepts as complex
		 */
		for( Role r : kb.getRBox().getRoles() ) {			
			for( ATermAppl c : r.getDomains() ) {
				if( ATermUtils.isPrimitive( c ) ) {
					conceptFlags.put( c, ConceptFlag.OTHER );
				}
				else if( ATermUtils.isAnd( c ) ) {
					ATermList list = (ATermList) c.getArgument( 0 );
					for( ; !list.isEmpty(); list = list.getNext() ) {
						ATermAppl d = (ATermAppl) list.getFirst();
						if( ATermUtils.isPrimitive( d ) ) {
							conceptFlags.put( d, ConceptFlag.OTHER );
						}
					}
				}
				else if( ATermUtils.isNot( c ) && ATermUtils.isAnd( (ATermAppl) c.getArgument( 0 ) ) ) {
					ATermList list = (ATermList) ((ATermAppl) c.getArgument( 0 )).getArgument( 0 );
					for( ; !list.isEmpty(); list = list.getNext() ) {
						ATermAppl d = (ATermAppl) list.getFirst();
						if( ATermUtils.isNegatedPrimitive( d ) ) {
							conceptFlags.put( (ATermAppl) d.getArgument( 0 ), ConceptFlag.OTHER );
						}
					}
				}
			}
		}

		/*
		 * Iterate over the post-absorption unfolded class descriptions to set
		 * concept flags The iteration needs to be over classes to include
		 * orphans
		 */
		TBox tbox = kb.getTBox();
		for( ATermAppl c : definitionOrder ) {

			Iterator<Unfolding> unfoldingList = kb.getTBox().unfold( c );

			if( !tbox.isPrimitive( c ) 
				|| definitionOrder.isCyclic( c )
				|| toldTaxonomy.getAllEquivalents( c ).size() > 1 ) {
				conceptFlags.put( c, ConceptFlag.NONPRIMITIVE );
				while( unfoldingList.hasNext() ) {
					Unfolding unf = unfoldingList.next();
					for( ATermAppl d : ATermUtils.findPrimitives( unf.getResult() ) ) {
						ConceptFlag current = conceptFlags.get( d );
						if( current == null || current == ConceptFlag.COMPLETELY_DEFINED ) {
	                        conceptFlags.put( d, ConceptFlag.PRIMITIVE );
                        }
					}
				}
				continue;
			}

			boolean flagged = false;
			for( ATermAppl sup : toldTaxonomy.getFlattenedSupers( c, /* direct = */true ) ) {
				ConceptFlag supFlag = conceptFlags.get( sup );
				if( (supFlag == ConceptFlag.NONPRIMITIVE)
						|| (supFlag == ConceptFlag.NONPRIMITIVE_TA) ) {
					conceptFlags.put( c, ConceptFlag.NONPRIMITIVE_TA );
					flagged = true;
					break;
				}
			}
			if( flagged ) {
	            continue;
            }

			/*
			 * The concept may have appeared in the definition of a
			 * non-primitive or, it may already have an 'OTHER' flag.
			 */
			if( conceptFlags.get( c ) != null ) {
	            continue;
            }

			conceptFlags.put( c, isCDDesc( unfoldingList )
				? ConceptFlag.COMPLETELY_DEFINED
				: ConceptFlag.PRIMITIVE );
		}

		if( log.isLoggable( Level.FINE ) ) {
			int[] counts = new int[ConceptFlag.values().length];
			Arrays.fill( counts, 0 );

			for( ATermAppl c : classes ) {
				counts[conceptFlags.get( c ).ordinal()]++;
			}
			
			log.fine( "Concept flags:" );
			for( ConceptFlag flag : ConceptFlag.values() ) {
				log.fine( "\t" + flag + " = " + counts[flag.ordinal()] );
			}
		}
	}

	private void clearMarks() {
		for (TaxonomyNode<ATermAppl> n : markedNodes) {
			n.mark = null;
		}

		markedNodes.clear();
	}

	private boolean isCDDesc(Iterator<Unfolding> unfoldingList) {		
		while( unfoldingList.hasNext() ) {
			Unfolding unf = unfoldingList.next();
			if( !isCDDesc( unf.getResult() ) ) {
	            return false;
            }			
		}

		return true;
	}

	private boolean isCDDesc(ATermAppl desc) {
		if( desc == null ) {
	        return true;
        }

		if( ATermUtils.isPrimitive( desc ) ) {
	        return true;
        }

		if( ATermUtils.isAllValues( desc ) ) {
	        return true;
        }

		if( ATermUtils.isAnd( desc ) ) {
			boolean allCDConj = true;
			ATermList conj = (ATermList) desc.getArgument( 0 );
			for( ATermList subConj = conj; allCDConj && !subConj.isEmpty(); subConj = subConj
					.getNext() ) {
				ATermAppl ci = (ATermAppl) subConj.getFirst();
				allCDConj = isCDDesc( ci );
			}
			return allCDConj;
		}

		if( ATermUtils.isNot( desc ) ) {
			ATermAppl negd = (ATermAppl) desc.getArgument( 0 );

			if( ATermUtils.isPrimitive( negd ) ) {
	            return true;
            }

		}

		return false;
	}

	private void addToldRelation(ATermAppl c, ATermAppl d, boolean equivalent,
			Set<ATermAppl> explanation) {

		if( !equivalent && ((c == ATermUtils.BOTTOM) || (d == ATermUtils.TOP)) ) {
	        return;
        }

		if( !ATermUtils.isPrimitive( c ) ) {
			if( c.getAFun().equals( ATermUtils.ORFUN ) ) {
				ATermList list = (ATermList) c.getArgument( 0 );
				for( ATermList disj = list; !disj.isEmpty(); disj = disj.getNext() ) {
					ATermAppl e = (ATermAppl) disj.getFirst();
					addToldRelation( e, d, false, explanation );
				}
			}
		}
		else if( ATermUtils.isPrimitive( d ) ) {
			if( ATermUtils.isBnode( d ) ) {
	            return;
            }

			if( !equivalent ) {
				if( log.isLoggable( Level.FINER ) ) {
	                log.finer( "Preclassify (1) " + format( c ) + " " + format( d ) );
                }

				addToldSubsumer( c, d, explanation );
			}
			else {
				if( log.isLoggable( Level.FINER ) ) {
	                log.finer( "Preclassify (2) " + format( c ) + " " + format( d ) );
                }

				addToldEquivalent( c, d );
			}
		}
		else if( d.getAFun().equals( ATermUtils.ANDFUN ) ) {
			for( ATermList conj = (ATermList) d.getArgument( 0 ); !conj.isEmpty(); conj = conj
					.getNext() ) {
				ATermAppl e = (ATermAppl) conj.getFirst();
				addToldRelation( c, e, false, explanation );
			}
		}
		else if( d.getAFun().equals( ATermUtils.ORFUN ) ) {
			boolean allPrimitive = true;

			ATermList list = (ATermList) d.getArgument( 0 );
			for( ATermList disj = list; !disj.isEmpty(); disj = disj.getNext() ) {
				ATermAppl e = (ATermAppl) disj.getFirst();
				if( ATermUtils.isPrimitive( e ) ) {
					if( equivalent ) {
						if( log.isLoggable( Level.FINER ) ) {
	                        log.finer( "Preclassify (3) " + format( c ) + " " + format( e ) );
                        }

						addToldSubsumer( e, c );
					}
				}
                else {
	                allPrimitive = false;
                }
			}

			if( allPrimitive ) {
	            unionClasses.put( c, list );
            }
		}
		else if( d.equals( ATermUtils.BOTTOM ) ) {
			if( log.isLoggable( Level.FINER ) ) {
	            log.finer( "Preclassify (4) " + format( c ) + " BOTTOM" );
            }
			addToldEquivalent( c, ATermUtils.BOTTOM );
		}
		else if( d.getAFun().equals( ATermUtils.NOTFUN ) ) {
			// handle case sub(a, not(b)) which implies sub[a][b] is false
			ATermAppl negation = (ATermAppl) d.getArgument( 0 );
			if( ATermUtils.isPrimitive( negation ) ) {
				if( log.isLoggable( Level.FINER ) ) {
	                log.finer( "Preclassify (5) " + format( c ) + " not " + format( negation ) );
                }

				addToldDisjoint( c, negation );
				addToldDisjoint( negation, c );
			}
		}
	}

	private void addToldEquivalent(ATermAppl c, ATermAppl d) {
		if( c.equals( d ) ) {
	        return;
        }

		TaxonomyNode<ATermAppl> cNode = toldTaxonomy.getNode( c );
		TaxonomyNode<ATermAppl> dNode = toldTaxonomy.getNode( d );

		toldTaxonomy.merge( cNode, dNode );
		
		TaxonomyUtils.clearSuperExplanation( toldTaxonomy, c );
	}

	private void addToldSubsumer(ATermAppl c, ATermAppl d) {
		addToldSubsumer( c, d, null );
	}

	private void addToldSubsumer(ATermAppl c, ATermAppl d, Set<ATermAppl> explanation) {
		TaxonomyNode<ATermAppl> cNode = toldTaxonomy.getNode( c );
		TaxonomyNode<ATermAppl> dNode = toldTaxonomy.getNode( d );

		if( cNode == null ) {
	        throw new InternalReasonerException( c + " is not in the definition order" );
        }

		if( dNode == null ) {
	        throw new InternalReasonerException( d + " is not in the definition order" );
        }

		if( cNode.equals( dNode ) ) {
	        return;
        }

		if( cNode.equals( toldTaxonomy.getTop() ) ) {
			toldTaxonomy.merge( cNode, dNode );
			
			TaxonomyUtils.clearSuperExplanation( toldTaxonomy, c );
		}
		else {
			toldTaxonomy.addSuper( c, d );

			toldTaxonomy.removeCycles( cNode );
			
			if( cNode.getEquivalents().size() > 1 ) {
	            TaxonomyUtils.clearSuperExplanation( toldTaxonomy, c );
            }
            else if( explanation != null && !explanation.isEmpty() ) {
	            TaxonomyUtils.addSuperExplanation( toldTaxonomy, c, d, explanation );
            }
		}
	}

	private void addToldDisjoint(ATermAppl c, ATermAppl d) {
		Set<ATermAppl> disjoints = toldDisjoints.get( c );
		if( disjoints == null ) {
			disjoints = new HashSet<ATermAppl>();
			toldDisjoints.put( c, disjoints );
		}
		disjoints.add( d );
	}

	private void markToldSubsumers(ATermAppl c) {
		TaxonomyNode<ATermAppl> node = taxonomy.getNode( c );
		if( node != null ) {
			boolean newMark = mark( node, Boolean.TRUE, Propogate.UP );
			if( !newMark ) {
	            return;
            }
		}
		else if( log.isLoggable( Level.FINE ) && markedNodes.size() > 2 ) {
	        log.warning( "Told subsumer " + c + " is not classified yet" );
        }

		if( toldTaxonomy.contains( c ) ) {
			// TODO just getting direct supers and letting recursion handle rest
			// might be more efficient
			for( ATermAppl sup : toldTaxonomy.getFlattenedSupers( c, /* direct = */true ) ) {
				markToldSubsumers( sup );
			}
		}
	}

	private void markToldSubsumeds(ATermAppl c, Boolean b) {
		TaxonomyNode<ATermAppl> node = taxonomy.getNode( c );
		if( node != null ) {
			boolean newMark = mark( node, b, Propogate.DOWN );
			if( !newMark ) {
	            return;
            }
		}

		if( toldTaxonomy.contains( c ) ) {
			for( ATermAppl sub : toldTaxonomy.getFlattenedSubs( c, /* direct = */true ) ) {
				markToldSubsumeds( sub, b );
			}
		}
	}

	private void markToldDisjoints(Collection<ATermAppl> inputc, boolean topSearch) {

		Set<ATermAppl> cset = new HashSet<ATermAppl>();
		cset.addAll( inputc );

		for( ATermAppl c : inputc ) {
			if( taxonomy.contains( c ) ) {
	            cset.addAll( taxonomy.getFlattenedSupers( c, /* direct = */false ) );
            }

			if( toldTaxonomy.contains( c ) ) {
	            cset.addAll( toldTaxonomy.getFlattenedSupers( c, /* direct = */false ) );
            }
		}

		Set<ATermAppl> disjoints = new HashSet<ATermAppl>();
		for( ATermAppl a : cset ) {
			Set<ATermAppl> disj = toldDisjoints.get( a );
			if( disj != null ) {
	            disjoints.addAll( disj );
            }
		}

		if( topSearch ) {
			for( ATermAppl d : disjoints ) {
				TaxonomyNode<ATermAppl> node = taxonomy.getNode( d );
				if( node != null ) {
	                mark( node, Boolean.FALSE, Propogate.NONE );
                }
			}
		}
		else {
			for( ATermAppl d : disjoints ) {
				markToldSubsumeds( d, Boolean.FALSE );
			}
		}
	}

	private TaxonomyNode<ATermAppl> checkSatisfiability(ATermAppl c) {
		if( log.isLoggable( Level.FINER ) ) {
	        log.finer( "Satisfiable " );
        }

		Timer t = kb.timers.startTimer( "classifySat" );
		boolean isSatisfiable = kb.getABox().isSatisfiable( c, true );
		t.stop();

		if( log.isLoggable( Level.FINER ) ) {
	        log.finer( (isSatisfiable
				? "true"
				: "*****FALSE*****") + " (" + t.getLast() + "ms)" );
        }

		if( !isSatisfiable ) {
	        taxonomy.addEquivalentNode( c, taxonomy.getBottom() );
        }

		if( PelletOptions.USE_CACHING ) {
			if( log.isLoggable( Level.FINER ) ) {
	            log.finer( "...negation " );
            }

			t = kb.timers.startTimer( "classifySatNot" );
			ATermAppl notC = ATermUtils.makeNot( c );
			isSatisfiable = kb.getABox().isSatisfiable( notC, true );
			t.stop();

			if( !isSatisfiable ) {
	            taxonomy.addEquivalentNode( c, taxonomy.getTop() );
            }

			if( log.isLoggable( Level.FINER ) ) {
	            log.finer( isSatisfiable + " (" + t.getLast() + "ms)" );
            }
		}

		return taxonomy.getNode( c );
	}

	/**
	 * Add a new concept to the already classified taxonomy
	 */
	public void classify(ATermAppl c) {
		classify( c, /* requireTopSearch = */true );
	}

	private TaxonomyNode<ATermAppl> classify(ATermAppl c, boolean requireTopSearch) {
		boolean skipTopSearch;
		boolean skipBottomSearch;

		TaxonomyNode<ATermAppl> node = taxonomy.getNode( c );
		if( node != null ) {
	        return node;
        }

		node = checkSatisfiability( c );
		if( node != null ) {
	        return node;
        }

		clearMarks();

		List<TaxonomyNode<ATermAppl>> superNodes;
		List<TaxonomyNode<ATermAppl>> subNodes;
		List<ATermAppl> subs;
		List<ATermAppl> supers;

		ConceptFlag flag = conceptFlags.get( c );

		// FIXME: There may be a better thing to do here...
		if( flag == null ) {
	        flag = ConceptFlag.OTHER;
        }

		skipTopSearch = !requireTopSearch && useCD && (flag == ConceptFlag.COMPLETELY_DEFINED);

		if( skipTopSearch ) {

			superNodes = getCDSupers( c );
			skipBottomSearch = true;

		}
		else {

			superNodes = doTopSearch( c );
			skipBottomSearch = useCD
					&& ((flag == ConceptFlag.PRIMITIVE) || (flag == ConceptFlag.COMPLETELY_DEFINED));
		}
		supers = new ArrayList<ATermAppl>();
		for( TaxonomyNode<ATermAppl> n : superNodes ) {
			supers.add( n.getName() );
		}

		if( skipBottomSearch ) {
			subs = Collections.singletonList( ATermUtils.BOTTOM );
		}
		else {
			if( superNodes.size() == 1 ) {
				TaxonomyNode<ATermAppl> supNode = superNodes.iterator().next();

				/*
				 * if i has only one super class j and j is a subclass of i then
				 * it means i = j. There is no need to classify i since we
				 * already know everything about j
				 */
				ATermAppl sup = supNode.getName();
				Timer t = kb.timers.startTimer( "eqCheck" );
				boolean isEq = subsumes( c, sup );
				t.stop();
				if( isEq ) {
					if( log.isLoggable( Level.FINER ) ) {
	                    log.finer( format( c ) + " = " + format( sup ) );
                    }

					taxonomy.addEquivalentNode( c, supNode );
					return supNode;
				}
			}

			subNodes = doBottomSearch( c, superNodes );
			subs = new ArrayList<ATermAppl>();
			for( TaxonomyNode<ATermAppl> n : subNodes ) {
				subs.add( n.getName() );
			}
		}

		node = taxonomy.addNode( Collections.singleton( c ), supers, subs, /* hidden = */
		!ATermUtils.isPrimitive( c ) );

		/*
		 * For told relations maintain explanations.
		 */
		TaxonomyNode<ATermAppl> toldNode = toldTaxonomy.getNode( c );
		if( toldNode != null ) {
			// Add the told equivalents to the taxonomy
			TaxonomyNode<ATermAppl> defOrder = toldTaxonomy.getNode( c );
			for( ATermAppl eq : defOrder.getEquivalents() ) {
				taxonomy.addEquivalentNode( eq, node );
			}

			for( TaxonomyNode<ATermAppl> n : superNodes ) {
				Set<Set<ATermAppl>> exps = TaxonomyUtils.getSuperExplanations( toldTaxonomy, c, n
						.getName() );
				if( exps != null ) {
					for( Set<ATermAppl> exp : exps ) {
						if( !exp.isEmpty() ) {
							TaxonomyUtils.addSuperExplanation( taxonomy, c, n.getName(), exp );
						}
					}
				}
			}
		}

		if( log.isLoggable( Level.FINER ) ) {
	        log.finer( "Subsumption Count: " + kb.getABox().stats.satisfiabilityCount );
        }
		
		return node;
	}

	private List<TaxonomyNode<ATermAppl>> doBottomSearch(ATermAppl c,
			List<TaxonomyNode<ATermAppl>> supers) {
		Set<TaxonomyNode<ATermAppl>> searchFrom = new HashSet<TaxonomyNode<ATermAppl>>();
		for( TaxonomyNode<ATermAppl> sup : supers ) {
	        collectLeafs( sup, searchFrom );
        }

		if( searchFrom.isEmpty() ) {
			return Collections.singletonList( taxonomy.getBottom() );
		}

		clearMarks();

		mark( taxonomy.getTop(), Boolean.FALSE, Propogate.NONE );
		taxonomy.getBottom().mark = Boolean.TRUE;
		markToldSubsumeds( c, Boolean.TRUE );
		for( TaxonomyNode<ATermAppl> sup : supers ) {
	        mark( sup, Boolean.FALSE, Propogate.NONE );
        }

		log.finer( "Bottom search..." );

		List<TaxonomyNode<ATermAppl>> subs = new ArrayList<TaxonomyNode<ATermAppl>>();
		Set<TaxonomyNode<ATermAppl>> visited = new HashSet<TaxonomyNode<ATermAppl>>();
		for( TaxonomyNode<ATermAppl> n : searchFrom ) {
	        if( subsumed( n, c ) ) {
	            search( /* topSearch = */false, c, n, visited, subs );
            }
        }
		
		if( subs.isEmpty() ) {
	        return Collections.singletonList( taxonomy.getBottom() );
        }

		return subs;
	}

	private void collectLeafs(TaxonomyNode<ATermAppl> node,
			Collection<TaxonomyNode<ATermAppl>> leafs) {
		for( TaxonomyNode<ATermAppl> sub : node.getSubs() ) {
			if( sub.isLeaf() ) {
	            leafs.add( sub );
            }
            else {
	            collectLeafs( sub, leafs );
            }
		}
	}

	private List<TaxonomyNode<ATermAppl>> doTopSearch(ATermAppl c) {
		List<TaxonomyNode<ATermAppl>> supers = new ArrayList<TaxonomyNode<ATermAppl>>();

		mark( taxonomy.getTop(), Boolean.TRUE, Propogate.NONE );
		taxonomy.getBottom().mark = Boolean.FALSE;
		markToldSubsumers( c );
		markToldDisjoints( Collections.singleton( c ), true );

		log.finer( "Top search..." );

		search( true, c, taxonomy.getTop(), new HashSet<TaxonomyNode<ATermAppl>>(), supers );
		
		return supers;
	}

	private List<TaxonomyNode<ATermAppl>> getCDSupers(ATermAppl c) {

		/*
		 * Find all of told subsumers already classified and not redundant
		 */
		List<TaxonomyNode<ATermAppl>> supers = new ArrayList<TaxonomyNode<ATermAppl>>();

		TaxonomyNode<ATermAppl> toldTaxNode = toldTaxonomy.getNode( c );
		
		// every class is added to told taxonomy so we cannot have null values here
		assert toldTaxNode != null;
		
		Collection<TaxonomyNode<ATermAppl>> cDefs = toldTaxNode.getSupers();

		int nTS = cDefs.size();
		if( nTS == 1 ) {
			for( TaxonomyNode<ATermAppl> def : cDefs ) {
				if( def == toldTaxonomy.getTop() ) {
	                continue;
                }
				TaxonomyNode<ATermAppl> parent = taxonomy.getNode( def.getName() );
				if (parent == null) {
					log.warning("Possible tautological definition, assuming "
					            + format(def.getName())
					            + " is equivalent to "
					            + format(ATermUtils.TOP));
					if (log.isLoggable(Level.FINE)) {
						log.fine("Told subsumer of " + format(c) + "  is not classified: " + format(def.getName()));
					}
				}
				else {
					supers.add( parent );
				}
				break;
			}
		}
		else {
			for( TaxonomyNode<ATermAppl> def : cDefs ) {
				if( def == toldTaxonomy.getTop() ) {
	                continue;
                }
				TaxonomyNode<ATermAppl> candidate = taxonomy.getNode( def.getName() );
				if( candidate == null ) {
					log.warning("Possible tautological definition, assuming "
					            + format(def.getName())
					            + " is equivalent to "
					            + format(ATermUtils.TOP));
					if (log.isLoggable(Level.FINE)) {
						log.fine("Told subsumer of " + format(c) + "  is not classified: " + format(def.getName()));
					}
                }
				else {
					for( TaxonomyNode<ATermAppl> ancestor : candidate.getSupers() ) {
						mark( ancestor, Boolean.TRUE, Propogate.UP );
					}
				}
			}
			for( TaxonomyNode<ATermAppl> def : cDefs ) {
				if( def == toldTaxonomy.getTop() ) {
	                continue;
                }
				TaxonomyNode<ATermAppl> candidate = taxonomy.getNode( def.getName() );
				if( candidate.mark == null ) {
					supers.add( candidate );
					if( log.isLoggable( Level.FINER ) ) {
	                    log.finer( "...completely defined by " + candidate.getName().getName() );
                    }
				}
			}
		}

		if( supers.isEmpty() ) {
	        supers.add( taxonomy.getTop() );
        }

		return supers;
	}

	private Collection<TaxonomyNode<ATermAppl>> search(boolean topSearch, ATermAppl c,
			TaxonomyNode<ATermAppl> x, Set<TaxonomyNode<ATermAppl>> visited,
			List<TaxonomyNode<ATermAppl>> result) {
		Timer t = kb.timers.startTimer( "search" + (topSearch ? "Top" : "Bottom") );
		
		List<TaxonomyNode<ATermAppl>> posSucc = new ArrayList<TaxonomyNode<ATermAppl>>();
		visited.add( x );

		Collection<TaxonomyNode<ATermAppl>> list = topSearch
			? x.getSubs()
			: x.getSupers();

		for( TaxonomyNode<ATermAppl> next : list ) {

			if( topSearch ) {
				if( subsumes( next, c ) ) {
	                posSucc.add( next );
                }
			}
			else {
				if( subsumed( next, c ) ) {
	                posSucc.add( next );
                }
			}
		}

		if( posSucc.isEmpty() ) {
			result.add( x );
		}
		else {
			for( TaxonomyNode<ATermAppl> y : posSucc ) {
				if( !visited.contains( y ) ) {
	                search( topSearch, c, y, visited, result );
                }
			}
		}

		t.stop();
		
		return result;
	}

	private boolean subCheckWithCache(TaxonomyNode<ATermAppl> node, ATermAppl c, boolean topDown) {

		Boolean cached = node.mark;
		if( cached != null ) {
	        return cached.booleanValue();
        }

		/*
		 * Search ancestors for marks to propogate
		 */
		Collection<TaxonomyNode<ATermAppl>> others = topDown
			? node.getSupers()
			: node.getSubs();

		if( others.size() > 1 ) {
			Map<TaxonomyNode<ATermAppl>, TaxonomyNode<ATermAppl>> visited = new LinkedHashMap<TaxonomyNode<ATermAppl>, TaxonomyNode<ATermAppl>>();
			visited.put( node, null );

			Map<TaxonomyNode<ATermAppl>, TaxonomyNode<ATermAppl>> toBeVisited = new LinkedHashMap<TaxonomyNode<ATermAppl>, TaxonomyNode<ATermAppl>>();
			for( TaxonomyNode<ATermAppl> n : others ) {
				toBeVisited.put( n, node );
			}

			while( !toBeVisited.isEmpty() ) {
				TaxonomyNode<ATermAppl> relative = toBeVisited.keySet().iterator().next();
				TaxonomyNode<ATermAppl> reachedFrom = toBeVisited.get( relative );

				Boolean ancestorMark = relative.mark;
				if( Boolean.FALSE.equals( ancestorMark ) ) {
					for( TaxonomyNode<ATermAppl> n = reachedFrom; n != null; n = visited.get( n ) ) {
						mark( n, Boolean.FALSE, Propogate.NONE );
					}
					return false;
				}

				if( ancestorMark == null ) {
					Collection<TaxonomyNode<ATermAppl>> moreRelatives = topDown
						? relative.getSupers()
						: relative.getSubs();
					for( TaxonomyNode<ATermAppl> n : moreRelatives ) {
						if( !visited.keySet().contains( n ) && !toBeVisited.keySet().contains( n ) ) {
							toBeVisited.put( n, relative );
						}
					}
				}
				toBeVisited.remove( relative );
				visited.put( relative, reachedFrom );
			}
		}

		// check subsumption
		boolean calcdMark = topDown
			? subsumes( node.getName(), c )
			: subsumes( c, node.getName() );
		// mark the node appropriately
		mark( node, Boolean.valueOf( calcdMark ), Propogate.NONE );

		return calcdMark;
	}

	private boolean subsumes(TaxonomyNode<ATermAppl> node, ATermAppl c) {
		return subCheckWithCache( node, c, true );
	}

	private boolean subsumed(TaxonomyNode<ATermAppl> node, ATermAppl c) {
		return subCheckWithCache( node, c, false );
	}

	private boolean mark(TaxonomyNode<ATermAppl> node, Boolean value, Propogate propogate) {
		if( node.getEquivalents().contains( ATermUtils.BOTTOM ) ) {
	        return true;
        }

		if( node.mark != null ) {
			if( !node.mark.equals( value ) ) {
	            throw new RuntimeException( "Inconsistent classification result " + node.getName()
						+ " " + node.mark + " " + value );
            }
            else {
	            return false;
            }
		}
		node.mark = value;
		markedNodes.add( node );

		if( propogate != Propogate.NONE ) {
			Collection<TaxonomyNode<ATermAppl>> others = (propogate == Propogate.UP)
				? node.getSupers()
				: node.getSubs();
			for( TaxonomyNode<ATermAppl> n : others ) {
	            mark( n, value, propogate );
            }
		}

		return true;
	}

	private boolean subsumes(ATermAppl sup, ATermAppl sub) {
		long time = 0, count = 0;
		if( log.isLoggable( Level.FINER ) ) {
			time = System.currentTimeMillis();
			count = kb.getABox().stats.satisfiabilityCount;
			log
					.finer( "Subsumption testing for [" + format( sub ) + "," + format( sup )
							+ "]..." );
		}

		boolean result = kb.getABox().isSubClassOf( sub, sup );

		if( log.isLoggable( Level.FINER ) ) {
			String sign = (kb.getABox().stats.satisfiabilityCount > count)
				? "+"
				: "-";
			time = System.currentTimeMillis() - time;
			log.finer( " done (" + (result
				? "+"
				: "-") + ") (" + sign + time + "ms)" );
		}

		return result;
	}

	private void mark(Set<ATermAppl> set, Map<ATermAppl, Boolean> marked, Boolean value) {
		for( ATermAppl c : set ) {
			marked.put( c, value );
		}
	}

	/**
	 * Realize the KB by finding the instances of each class.
	 * 
	 * @return boolean False if the progress monitor is canceled
	 */
	public boolean realize() {
		monitor.setProgressTitle( "Realizing" );

		return PelletOptions.REALIZE_INDIVIDUAL_AT_A_TIME
			? realizeByIndividuals()
			: realizeByConcepts();
	}

	private boolean realizeByIndividuals() {
		monitor.setProgressLength( kb.getIndividuals().size() );
		monitor.taskStarted();

		Iterator<Individual> i = kb.getABox().getIndIterator();
		for( int count = 0; i.hasNext(); count++ ) {
			Individual x = i.next();

			monitor.incrementProgress();
			
			kb.timers.getTimer( "realize" ).check();
			
			if( monitor.isCanceled() ) {
	            return false;
            }

			if( log.isLoggable( Level.FINER ) ) {
	            log.finer( count + ") Realizing " + format( x.getName() ) + " " );
            }

			Map<ATermAppl, Boolean> marked = new HashMap<ATermAppl, Boolean>();

			List<ATermAppl> obviousTypes = new ArrayList<ATermAppl>();
			List<ATermAppl> obviousNonTypes = new ArrayList<ATermAppl>();

			kb.getABox().getObviousTypes( x.getName(), obviousTypes, obviousNonTypes );

			for( ATermAppl c : obviousTypes ) {
				// since nominals can be returned by getObviousTypes
				// we need the following check
				if( !taxonomy.contains( c ) ) {
	                continue;
                }

				mark( taxonomy.getAllEquivalents( c ), marked, Boolean.TRUE );
				mark( taxonomy.getFlattenedSupers( c, /* direct = */true ), marked, Boolean.TRUE );

				// FIXME: markToldDisjoints operates on a map key'd with
				// TaxonomyNodes, not ATermAppls
				// markToldDisjoints( c, false );
			}

			for( ATermAppl c : obviousNonTypes ) {
				mark( taxonomy.getAllEquivalents( c ), marked, Boolean.FALSE );
				mark( taxonomy.getFlattenedSubs( c, /* direct = */true ), marked, Boolean.FALSE );
			}

			realizeByIndividual( x.getName(), ATermUtils.TOP, marked );
		}

		monitor.taskFinished();

		return true;
	}

	private boolean realizeByIndividual(ATermAppl n, ATermAppl c, Map<ATermAppl, Boolean> marked) {
		boolean realized = false;

		if( c.equals( ATermUtils.BOTTOM ) ) {
	        return false;
        }

		boolean isType;
		if( marked.containsKey( c ) ) {
			isType = marked.get( c ).booleanValue();
		}
		else {
			long time = 0, count = 0;
			if( log.isLoggable( Level.FINER ) ) {
				time = System.currentTimeMillis();
				count = kb.getABox().stats.consistencyCount;
				log.finer( "Type checking for [" + format( n ) + ", " + format( c ) + "]..." );
			}

			Timer t = kb.timers.startTimer( "classifyType" );
			isType = kb.isType( n, c );
			t.stop();
			marked.put( c, isType
				? Boolean.TRUE
				: Boolean.FALSE );

			if( log.isLoggable( Level.FINER ) ) {
				String sign = (kb.getABox().stats.consistencyCount > count)
					? "+"
					: "-";
				time = System.currentTimeMillis() - time;
				log.finer( "done (" + (isType
					? "+"
					: "-") + ") (" + sign + time + "ms)" );
			}
		}

		if( isType ) {
			TaxonomyNode<ATermAppl> node = taxonomy.getNode( c );

			for( TaxonomyNode<ATermAppl> sub : node.getSubs() ) {
				ATermAppl d = sub.getName();
				realized = realizeByIndividual( n, d, marked ) || realized;
			}

			// this concept is the most specific concept x belongs to
			// so add it here and return true
			if( !realized ) {
				@SuppressWarnings("unchecked")
				Set<ATermAppl> instances = (Set<ATermAppl>) node
						.getDatum( TaxonomyUtils.INSTANCES_KEY );
				if( instances == null ) {
					instances = new HashSet<ATermAppl>();
					node.putDatum( TaxonomyUtils.INSTANCES_KEY, instances );
				}
				instances.add( n );
				realized = true;
			}
		}

		return realized;
	}

	private boolean realizeByConcepts() {
		monitor.setProgressLength( classes.size() + 2 );
		monitor.taskStarted();


		if (markedNodes == null) {
	        markedNodes = CollectionUtils.makeList();
        }
		else {
			clearMarks();
		}
		
		Collection<ATermAppl> individuals = kb.getIndividuals();
		if( !individuals.isEmpty() ) {
	        realizeByConcept( ATermUtils.TOP, individuals );
        }

		kb.timers.getTimer( "realize" ).check();
		
		if( monitor.isCanceled() ) {
	        return false;
        }

		monitor.taskFinished();

		return true;
	}


    private Set<ATermAppl> realizeByConcept(ATermAppl c, Collection<ATermAppl> individuals) {
		if( c.equals( ATermUtils.BOTTOM ) ) {
	        return SetUtils.emptySet();
        }

		kb.timers.getTimer( "realize" ).check();
		
		if( monitor.isCanceled() ) {
	        return null;
        }
		
		TaxonomyNode<ATermAppl> node = taxonomy.getNode( c );
		
		if( node.mark == Boolean.TRUE ) {
			return TaxonomyUtils.getAllInstances(taxonomy, c);
		}
		
		monitor.incrementProgress();
		mark(node, Boolean.TRUE, Propogate.NONE);
		
		if( log.isLoggable( Level.FINE ) ) {
	        log.fine( "Realizing concept " + c );
        }
		
		Set<ATermAppl> instances = new HashSet<ATermAppl>( kb.retrieve( c, individuals ) );
		Set<ATermAppl> mostSpecificInstances = new HashSet<ATermAppl>( instances );

		if( !instances.isEmpty() ) {
			for( TaxonomyNode<ATermAppl> sub : node.getSubs() ) {
				ATermAppl d = sub.getName();
				Set<ATermAppl> subInstances = realizeByConcept( d, instances );

				// Returned value can be null if the monitor is canceled
				if( subInstances == null ) {
	                return null;
                }

				mostSpecificInstances.removeAll( subInstances );
			}

			if( !mostSpecificInstances.isEmpty() ) {
	            node.putDatum( TaxonomyUtils.INSTANCES_KEY, mostSpecificInstances );
            }
		}

		return instances;
	}

	public void printStats() {
		Timer t1 = kb.timers.getTimer( "satisfiability" );
		Timer t2 = kb.timers.getTimer( "subClassSat" );
		StringBuilder sb = new StringBuilder( kb.getABox().getCache().toString() );
		sb.append( "sat: " );
		if( t1 != null ) {
			sb.append( t1.getCount() ).append( " " ).append( t1.getTotal() );
		}
		else {
			sb.append( "0" );
		}

		sb.append( " sub: " );
		if( t2 != null ) {
			sb.append( t2.getCount() ).append( " " ).append( t2.getTotal() );
		}
		else {
			sb.append( "0" );
		}

		int totalExps = 0;
		int totalAxioms = 0;
		Iterator<?> i = taxonomy.depthFirstDatumOnly( ATermUtils.TOP,
				TaxonomyUtils.SUPER_EXPLANATION_KEY );
		while( i.hasNext() ) {
			@SuppressWarnings("unchecked")
			Map<ATermAppl, Set<Set<ATermAppl>>> allExps = (Map<ATermAppl, Set<Set<ATermAppl>>>) i
					.next();
			if( allExps != null ) {
				totalExps++;
				for( Set<Set<ATermAppl>> exps : allExps.values() ) {
					for( Set<ATermAppl> exp : exps ) {
	                    totalAxioms += exp.size();
                    }
				}
			}
		}
		System.out.println( sb );
	}

	public void printMemory() {
		try {
			MemUtils.printMemory( "Total: ", MemUtils.totalMemory() );
			MemUtils.printMemory( "Free : ", MemUtils.freeMemory() );
			MemUtils.printMemory( "Used*: ", MemUtils.totalMemory() - MemUtils.freeMemory() );
			MemUtils.runGC();
			MemUtils.printMemory( "Used : ", MemUtils.usedMemory() );
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}

	private String format(ATermAppl c) {
		 return ATermUtils.toString(c);
	}

}
