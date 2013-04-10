// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under the terms of the MIT License.
//
// The MIT License
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package org.mindswap.pellet;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.tableau.branch.Branch;
import org.mindswap.pellet.tableau.cache.CachedNode;
import org.mindswap.pellet.tableau.cache.CachedNodeFactory;
import org.mindswap.pellet.tableau.cache.ConceptCache;
import org.mindswap.pellet.tableau.cache.ConceptCacheLRU;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;
import org.mindswap.pellet.tableau.completion.SROIQIncStrategy;
import org.mindswap.pellet.tableau.completion.queue.BasicCompletionQueue;
import org.mindswap.pellet.tableau.completion.queue.CompletionQueue;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;
import org.mindswap.pellet.tableau.completion.queue.OptimizedBasicCompletionQueue;
import org.mindswap.pellet.tableau.completion.queue.QueueElement;
import org.mindswap.pellet.tbox.TBox;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Bool;
import org.mindswap.pellet.utils.CandidateSet;
import org.mindswap.pellet.utils.SetUtils;
import org.mindswap.pellet.utils.Timer;
import org.mindswap.pellet.utils.fsm.State;
import org.mindswap.pellet.utils.fsm.Transition;
import org.mindswap.pellet.utils.fsm.TransitionGraph;
import org.mindswap.pellet.utils.iterator.MultiListIterator;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;

import com.clarkparsia.pellet.BranchEffectTracker;
import com.clarkparsia.pellet.IncrementalChangeTracker;
import com.clarkparsia.pellet.datatypes.DatatypeReasoner;
import com.clarkparsia.pellet.datatypes.DatatypeReasonerImpl;
import com.clarkparsia.pellet.datatypes.exceptions.DatatypeReasonerException;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import com.clarkparsia.pellet.datatypes.exceptions.UnrecognizedDatatypeException;
import com.clarkparsia.pellet.expressivity.Expressivity;
import com.clarkparsia.pellet.impl.SimpleBranchEffectTracker;
import com.clarkparsia.pellet.impl.SimpleIncrementalChangeTracker;
import com.clarkparsia.pellet.utils.MultiMapUtils;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Clark & Parsia, LLC. <http://www.clarkparsia.com></p>
 *
 * @author Evren Sirin
 */
public class ABox {
	public final static Logger				log					= Logger.getLogger( ABox.class.getName() );


	// following two variables are used to generate names
	// for newly generated individuals. so during rules are
	// applied anon1, anon2, etc. will be generated. This prefix
	// will also make sure that any node whose name starts with
	// this prefix is not a root node
	private int								anonCount			= 0;

	public  ABoxStats						stats				= new ABoxStats();
	
	/**
	 * datatype reasoner used for checking the satisfiability of datatypes
	 */
	protected final DatatypeReasoner		dtReasoner;

	/**
	 * This is a list of nodes. Each node has a name expressed as an ATerm which
	 * is used as the key in the Hashtable. The value is the actual node object
	 */
	protected Map<ATermAppl, Node>			nodes;

	/**
	 * This is a list of node names. This list stores the individuals in the
	 * order they are created
	 */
	protected List<ATermAppl>				nodeList;

	/**
	 * Indicates if any of the completion rules has been applied to modify ABox
	 */
	private boolean							changed				= false;

	private boolean							doExplanation;

	// cached satisfiability results
	// the table maps every atomic concept A (and also its negation not(A))
	// to the root node of its completed tree. If a concept is mapped to
	// null value it means it is not satisfiable
	protected ConceptCache					cache;

	// pseudo model for this Abox. This is the ABox that results from
	// completing to the original Abox
	// private ABox pseudoModel;

	// cache of the last completion. it may be different from the pseudo
	// model, e.g. type checking for individual adds one extra assertion
	// last completion is stored for caching the root nodes that was
	// the result of
	private ABox							lastCompletion;
	private boolean							keepLastCompletion;
	private Clash							lastClash;

	// complete ABox means no more tableau rules are applicable
	private boolean							isComplete			= false;

	// the last clash recorded
	private Clash							clash;
	
	private Set<Clash>						assertedClashes;

	// the current branch number
	private int								branch;
	private List<Branch>					branches;

	private List<NodeMerge>					toBeMerged;

	private Map<ATermAppl, int[]>			disjBranchStats;

	// if we are using copy on write, this is where to copy from
	private ABox							sourceABox;

	// return true if init() function is called. This indicates parsing
	// is completed and ABox is ready for completion
	private boolean							initialized			= false;

	// The KB to which this ABox belongs
	private KnowledgeBase					kb;

	public boolean							rulesNotApplied;

	public boolean							ranRete				= false;
	public boolean							useRete				= false;

	private BranchEffectTracker				branchEffects;
	private CompletionQueue					completionQueue;
	private IncrementalChangeTracker		incChangeTracker;

	// flag set when incrementally updating the abox with explicit assertions
	private boolean							syntacticUpdate		= false;

	public ABox(KnowledgeBase kb) {
		this.kb = kb;
		nodes = new HashMap<ATermAppl, Node>();
		nodeList = new ArrayList<ATermAppl>();
		clash = null;
		assertedClashes = new HashSet<Clash>();
		doExplanation = false;
		dtReasoner = new DatatypeReasonerImpl();
		keepLastCompletion = false;

		setBranch( DependencySet.NO_BRANCH );
		branches = new ArrayList<Branch>();
		setDisjBranchStats( new HashMap<ATermAppl, int[]>() );

		toBeMerged = new ArrayList<NodeMerge>();
		rulesNotApplied = true;
		
		if( PelletOptions.TRACK_BRANCH_EFFECTS ) {
	        branchEffects = new SimpleBranchEffectTracker();
        }
        else {
	        branchEffects = null;
        }
		
		if( PelletOptions.USE_COMPLETION_QUEUE ) {
			if( PelletOptions.USE_OPTIMIZED_BASIC_COMPLETION_QUEUE ) {
	            completionQueue = new OptimizedBasicCompletionQueue( this );
            }
            else {
	            completionQueue = new BasicCompletionQueue( this );
            }
		}
        else {
	        completionQueue = null;
        }
		
		if( PelletOptions.USE_INCREMENTAL_CONSISTENCY ) {
	        incChangeTracker = new SimpleIncrementalChangeTracker();
        }
        else {
	        incChangeTracker = null;
        }
	}

	public ABox(KnowledgeBase kb, ABox abox, ATermAppl extraIndividual, boolean copyIndividuals) {
		this.kb = kb;
		Timer timer = kb.timers.startTimer( "cloneABox" );


		this.rulesNotApplied = true;
		initialized = abox.initialized;
		setChanged( abox.isChanged() );
		setAnonCount( abox.getAnonCount() );
		cache = abox.cache;
		clash = abox.clash;		
		dtReasoner = abox.dtReasoner;
		doExplanation = abox.doExplanation;
		setDisjBranchStats( abox.getDisjBranchStats() );

		int extra = (extraIndividual == null)
			? 0
			: 1;
		int nodeCount = extra + (copyIndividuals
			? abox.nodes.size()
			: 0);

		nodes = new HashMap<ATermAppl, Node>( nodeCount );
		nodeList = new ArrayList<ATermAppl>( nodeCount );

		if( PelletOptions.TRACK_BRANCH_EFFECTS ) {
			if( copyIndividuals ) {
	            branchEffects = abox.branchEffects.copy();
            }
            else {
	            branchEffects = new SimpleBranchEffectTracker();
            }
		}
        else {
	        branchEffects = null;
        }
		
		// copy the queue - this must be done early so that the effects of
		// adding the extra individual do not get removed
		if( PelletOptions.USE_COMPLETION_QUEUE ) {
			if( copyIndividuals ) {
				completionQueue = abox.completionQueue.copy();
				completionQueue.setABox( this );
			}
			else if( PelletOptions.USE_OPTIMIZED_BASIC_COMPLETION_QUEUE ) {
	            completionQueue = new OptimizedBasicCompletionQueue( this );
            }
            else {
	            completionQueue = new BasicCompletionQueue( this );
            }
		}
        else {
	        completionQueue = null;
        }

		if( extraIndividual != null ) {
			Individual n = new Individual( extraIndividual, this, null );
			n.setNominalLevel( Node.BLOCKABLE );
			n.setConceptRoot( true );
			n.addType( ATermUtils.TOP, DependencySet.INDEPENDENT );
			nodes.put( extraIndividual, n );
			nodeList.add( extraIndividual );

			if( PelletOptions.COPY_ON_WRITE ) {
	            sourceABox = abox;
            }
		}

		if( copyIndividuals ) {
			toBeMerged = abox.getToBeMerged();
			if( sourceABox == null ) {
				for( int i = 0; i < nodeCount - extra; i++ ) {
					ATermAppl x = abox.nodeList.get( i );
					Node node = abox.getNode( x );
					Node copy = node.copyTo( this );

					nodes.put( x, copy );
					nodeList.add( x );
				}

				for( Node node : nodes.values() ) {
					node.updateNodeReferences();
				}
			}
		}
		else {
			toBeMerged = Collections.emptyList();
			sourceABox = null;
			initialized = false;
		}

		// Copy of the incChangeTracker looks up nodes in the new ABox, so this
		// copy must follow node copying
		if( PelletOptions.USE_INCREMENTAL_CONSISTENCY ) {
			if( copyIndividuals ) {
	            incChangeTracker = abox.incChangeTracker.copy( this );
            }
            else {
	            incChangeTracker = new SimpleIncrementalChangeTracker();
            }
		}
        else {
	        incChangeTracker = null;
        }

		assertedClashes = new HashSet<Clash>();
		for( Clash clash : abox.assertedClashes ) {
			assertedClashes.add( clash.copyTo( this ) );
		}
		
		if( extraIndividual == null || copyIndividuals ) {
			setBranch( abox.branch );
			branches = new ArrayList<Branch>( abox.branches.size() );
			for( int i = 0, n = abox.branches.size(); i < n; i++ ) {
				Branch branch = abox.branches.get( i );
				Branch copy;

				if( sourceABox == null ) {
					copy = branch.copyTo( this );
					copy.setNodeCount( branch.getNodeCount() + extra );
				}
				else {
					copy = branch;
				}
				branches.add( copy );
			}
		}
		else {
			setBranch( DependencySet.NO_BRANCH );
			branches = new ArrayList<Branch>();
		}

		timer.stop();

	}

	/**
	 * Create a copy of this ABox with all the nodes and edges.
	 * 
	 * @return
	 */
	public ABox copy() {
		return copy(kb);
	}

	/**
	 * Create a copy of this ABox with all the nodes and edges and the given KB.
	 */
	public ABox copy(KnowledgeBase kb) {
		return new ABox( kb, this, null, true );
	}

	/**
	 * Create a copy of this ABox with one more additional individual. This is
	 * <b>NOT</b> equivalent to create a copy and then add the individual. The
	 * order of individuals in the ABox is important to figure out which
	 * individuals exist in the original ontology and which ones are created by
	 * the tableau algorithm. This function creates a new ABox such that the
	 * individual is supposed to exist in the original ontology. This is very
	 * important when satisfiability of a concept starts with a pesudo model
	 * rather than the initial ABox.
	 * 
	 * @param extraIndividual
	 *            Extra individual to be added to the copy ABox
	 * @return
	 */
	public ABox copy(ATermAppl extraIndividual, boolean copyIndividuals) {
		return new ABox( kb, this, extraIndividual, copyIndividuals );
	}

	public void copyOnWrite() {
		if( sourceABox == null ) {
	        return;
        }

		Timer t = kb.timers.startTimer( "copyOnWrite" );

		List<ATermAppl> currentNodeList = new ArrayList<ATermAppl>( nodeList );
		int currentSize = currentNodeList.size();
		int nodeCount = sourceABox.nodes.size();

		nodeList = new ArrayList<ATermAppl>( nodeCount + 1 );
		nodeList.add( currentNodeList.get( 0 ) );

		for( int i = 0; i < nodeCount; i++ ) {
			ATermAppl x = sourceABox.nodeList.get( i );
			Node node = sourceABox.getNode( x );
			Node copyNode = node.copyTo( this );
			nodes.put( x, copyNode );
			nodeList.add( x );
		}

		if( currentSize > 1 ) {
	        nodeList.addAll( currentNodeList.subList( 1, currentSize ) );
        }

		for( Iterator<Node> i = nodes.values().iterator(); i.hasNext(); ) {
			Node node = i.next();

			if( sourceABox.nodes.containsKey( node.getName() ) ) {
	            node.updateNodeReferences();
            }
		}
		
		

		for( int i = 0, n = branches.size(); i < n; i++ ) {
			Branch branch = branches.get( i );
			Branch copy = branch.copyTo( this );
			branches.set( i, copy );

			if( i >= sourceABox.getBranches().size() ) {
	            copy.setNodeCount( copy.getNodeCount() + nodeCount );
            }
            else {
	            copy.setNodeCount( copy.getNodeCount() + 1 );
            }
		}

		t.stop();

		sourceABox = null;
	}

	/**
	 * Clear the pseudo model created for the ABox and concept satisfiability.
	 * 
	 * @param clearSatCache
	 *            If true clear concept satisfiability cache, if false only
	 *            clear pseudo model.
	 */
	public void clearCaches(boolean clearSatCache) {
		lastCompletion = null;

		if( clearSatCache ) {
			cache = new ConceptCacheLRU( kb );
		}
	}

	public Bool getCachedSat(ATermAppl c) {
		return cache.getSat( c );
	}

	public ConceptCache getCache() {
		return cache;
	}

	public CachedNode getCached(ATermAppl c) {
		if (ATermUtils.isNominal(c)) {
	        return getIndividual(c.getArgument(0)).getSame();
        }
        else {
	        return cache.get( c );
        }
	}

	private void cache(Individual rootNode, ATermAppl c, boolean isConsistent) {

		if( !isConsistent ) {
			if( log.isLoggable( Level.FINE ) ) {
				log.fine( "Unsatisfiable: " + ATermUtils.toString( c ) );
				log.fine( "Equivalent to TOP: " + ATermUtils.toString( ATermUtils.negate( c ) ) );
			}

			cache.putSat( c, false );
		}
		else {

			if( log.isLoggable( Level.FINE ) ) {
	            log.fine( "Cache " + rootNode.debugString() );
            }
			
			cache.put( c, CachedNodeFactory.createNode( c, rootNode ) );
			
//			System.err.println( c + " " + rootNode.debugString() );
		}
	}

	public Bool isKnownSubClassOf(ATermAppl c1, ATermAppl c2) {
		Bool isSubClassOf = Bool.UNKNOWN;
		CachedNode cached = getCached( c1 );
		if( cached != null ) {
			isSubClassOf = isType( cached, c2 );
		}

		return isSubClassOf;
	}

	public boolean isSubClassOf(ATermAppl c1, ATermAppl c2) {
		if( !doExplanation ) {
			Bool isKnownSubClass = isKnownSubClassOf( c1, c2 );
			if( isKnownSubClass.isKnown() ) {
	            return isKnownSubClass.isTrue();
            }
		}
		
		if( log.isLoggable( Level.FINE ) ) {
			long count = kb.timers.getTimer( "subClassSat" ) == null
				? 0
				: kb.timers.getTimer( "subClassSat" ).getCount();
			log.fine( count + ") Checking subclass [" + ATermUtils.toString( c1 ) + " " + ATermUtils.toString( c2 ) + "]" );
		}

		ATermAppl notC2 = ATermUtils.negate( c2 );
		ATermAppl c = ATermUtils.makeAnd( c1, notC2 );
		Timer t = kb.timers.startTimer( "subClassSat" );
		boolean sub = !isSatisfiable( c, false );
		t.stop();

		if( log.isLoggable( Level.FINE ) ) {
	        log.fine( " Result: " + sub + " (" + t.getLast() + "ms)" );
        }

		return sub;
	}

	public boolean isSatisfiable(ATermAppl c) {
		boolean cacheModel = PelletOptions.USE_CACHING
				&& (ATermUtils.isPrimitiveOrNegated( c ) || PelletOptions.USE_ADVANCED_CACHING);
		return isSatisfiable( c, cacheModel );
	}

	public boolean isSatisfiable(ATermAppl c, boolean cacheModel) {
		c = ATermUtils.normalize( c );

		// if normalization revealed an obvious unsatisfiability, return
		// immediately
		if( c.equals( ATermUtils.BOTTOM ) ) {
			lastClash = Clash.unexplained(null, DependencySet.INDEPENDENT,
			                "Obvious contradiction in class expression: " + ATermUtils.toString(c));
			return false;
		}

		if( log.isLoggable( Level.FINE ) ) {
	        log.fine( "Satisfiability for " + ATermUtils.toString( c )  );
        }

		if( cacheModel ) {
			CachedNode cached = getCached( c );
			if( cached != null ) {
				boolean satisfiable = !cached.isBottom();
				boolean needToCacheModel = cacheModel && !cached.isComplete();
				if( log.isLoggable( Level.FINE ) ) {
	                log.fine( "Cached sat for " + ATermUtils.toString( c ) + " is " + satisfiable );
                }
				// if explanation is enabled we should actually build the
				// tableau again to generate the clash. we don't cache the
				// explanation up front because generating explanation is costly
				// and we only want to do it when explicitly asked note that
				// when the concepts is satisfiable there is no explanation to
				// be generated so we return the result immediately
				if( !needToCacheModel && (satisfiable || !doExplanation) ) {
	                return satisfiable;
                }
			}
		}

		stats.satisfiabilityCount++;

		Timer t = kb.timers.startTimer( "satisfiability" );
		boolean isSat = isConsistent( SetUtils.<ATermAppl>emptySet(), c, cacheModel );
		t.stop();

		return isSat;
	}

	public CandidateSet<ATermAppl> getObviousInstances(ATermAppl c) {
		return getObviousInstances( c, kb.getIndividuals() );
	}

	public CandidateSet<ATermAppl> getObviousInstances(ATermAppl c, Collection<ATermAppl> individuals) {
		c = ATermUtils.normalize( c );
		Set<ATermAppl> subs = (kb.isClassified() && kb.getTaxonomy().contains( c ))
			? kb.getTaxonomy().getFlattenedSubs( c, false )
			: Collections.<ATermAppl>emptySet();
		subs.remove( ATermUtils.BOTTOM );

		CandidateSet<ATermAppl> cs = new CandidateSet<ATermAppl>();
		for( ATermAppl x : individuals ) {
			Bool isType = isKnownType( x, c, subs );
			cs.add( x, isType );
		}

		return cs;
	}

	public void getObviousTypes(ATermAppl x, List<ATermAppl> types, List<ATermAppl> nonTypes) {
		assert isComplete() : "Initial consistency check has not been performed!";

		Individual pNode = getIndividual( x );
		if( !pNode.getMergeDependency( true ).isIndependent() ) {
	        pNode = getIndividual( x );
        }
        else {
	        pNode = pNode.getSame();
        }

		pNode.getObviousTypes( types, nonTypes );
	}

	public CandidateSet<ATermAppl> getObviousSubjects(ATermAppl p, ATermAppl o) {
		CandidateSet<ATermAppl> candidates = new CandidateSet<ATermAppl>( kb.getIndividuals() );
		getObviousSubjects( p, o, candidates );

		return candidates;
	}

	public void getSubjects(ATermAppl p, ATermAppl o, CandidateSet<ATermAppl> candidates) {
		Iterator<ATermAppl> i = candidates.iterator();
		while( i.hasNext() ) {
			ATermAppl s = i.next();

			Bool hasObviousValue = hasObviousPropertyValue( s, p, o );
			candidates.update( s, hasObviousValue );
		}
	}

	public void getObviousSubjects(ATermAppl p, ATermAppl o, CandidateSet<ATermAppl> candidates) {
		Iterator<ATermAppl> i = candidates.iterator();
		while( i.hasNext() ) {
			ATermAppl s = i.next();

			Bool hasObviousValue = hasObviousPropertyValue( s, p, o );
			if( hasObviousValue.isFalse() ) {
	            i.remove();
            }
            else {
	            candidates.update( s, hasObviousValue );
            }
		}
	}

	public void getObviousObjects(ATermAppl p, CandidateSet<ATermAppl> candidates) {
		p = getRole( p ).getInverse().getName();
		Iterator<ATermAppl> i = candidates.iterator();
		while( i.hasNext() ) {
			ATermAppl s = i.next();

			Bool hasObviousValue = hasObviousObjectPropertyValue( s, p, null );
			candidates.update( s, hasObviousValue );
		}
	}

	public Bool isKnownType(ATermAppl x, ATermAppl c) {
		return isKnownType( x, c, SetUtils.<ATermAppl>emptySet() );
	}

	public Bool isKnownType(ATermAppl x, ATermAppl c, Collection<ATermAppl> subs) {
		assert isComplete() : "Initial consistency check has not been performed!";

		Individual pNode = getIndividual( x );

		boolean isIndependent = true;
		if( pNode.isMerged() ) {
			isIndependent = pNode.getMergeDependency( true ).isIndependent();
			pNode = pNode.getSame();
		}

		Bool isType = isKnownType( pNode, c, subs );

		if( isIndependent ) {
	        return isType;
        }
        else if( isType.isTrue() ) {
	        return Bool.UNKNOWN;
        }
        else {
	        return isType;
        }
	}

	public Bool isKnownType(Individual pNode, ATermAppl concept, Collection<ATermAppl> subs) {
		// Timer t = kb.timers.startTimer( "isKnownType" );
		Bool isType = isType( pNode, concept );
		if( isType.isUnknown() ) {
			Set<ATermAppl> concepts = ATermUtils.isAnd( concept )
				? ATermUtils.listToSet( (ATermList) concept.getArgument( 0 ) )
				: SetUtils.singleton( concept );

			isType = Bool.TRUE;
			for( ATermAppl c : concepts ) {
				Bool type = pNode.hasObviousType( c );

				if( type.isUnknown() && pNode.hasObviousType( subs ) ) {
					type = Bool.TRUE;
				}
				
				if( type.isKnown() ) {
					isType = isType.and( type );
				}
				else {
					isType = Bool.UNKNOWN;
					
//					boolean justSC = true;

					Collection<ATermAppl> axioms = kb.getTBox().getAxioms( c );
					LOOP: for( ATermAppl axiom : axioms ) {
						ATermAppl term = (ATermAppl) axiom.getArgument( 1 );

//						final AFun afun = axiom.getAFun();
//
//						if( !afun.equals( ATermUtils.SUBFUN ) ) {
//							justSC = false;
//						}

						boolean equivalent = axiom.getAFun().equals( ATermUtils.EQCLASSFUN );
						if( equivalent ) {
							Iterator<ATermAppl> i = ATermUtils.isAnd( term )
								? new MultiListIterator( (ATermList) term.getArgument( 0 ) )
								: Collections.singleton( term ).iterator();
							Bool knownType = Bool.TRUE;
							while( i.hasNext() && knownType.isTrue() ) {
								term = i.next();
								knownType = isKnownType( pNode, term, SetUtils.<ATermAppl>emptySet() );
							}
							if( knownType.isTrue() ) {
								isType = Bool.TRUE;
								break LOOP;
							}
						}
					}

					// TODO following short-cut might be implemented correctly
					// the main problem here is that concept might be in the
					// types of the individual with a dependency. In this case,
					// Node.hasObviousType returns unknown and changing it to
					// false here is wrong.
//					 if( justSC && ATermUtils.isPrimitive( c ) ) {
//						return Bool.FALSE;
//					}

					if( isType.isUnknown() ) {
						return Bool.UNKNOWN;
					}
				}
			}
		}
		// t.stop();

		return isType;
	}

	private Bool isType(CachedNode pNode, ATermAppl c) {
		Bool isType = Bool.UNKNOWN;
		
		boolean isPrimitive = kb.getTBox().isPrimitive( c );
		
		if( isPrimitive && !pNode.isTop() && !pNode.isBottom() && pNode.isComplete() ) {
			DependencySet ds = pNode.getDepends().get( c );
			if( ds == null ) {
				return Bool.FALSE;
			}
			else if( ds.isIndependent() && pNode.isIndependent() ) {
				return Bool.TRUE;
			}
		}
		
		ATermAppl notC = ATermUtils.negate( c );
		CachedNode cached = getCached( notC );
		if( cached != null && cached.isComplete() ) {
			isType = cache.isMergable( kb, pNode, cached ).not();
		}

		if( PelletOptions.CHECK_NOMINAL_EDGES && isType.isUnknown() ) {
			CachedNode cNode = getCached( c );
			if( cNode != null ) {
				isType = cache.checkNominalEdges( kb, pNode, cNode );	
			}
		}

		return isType;
	}

	public boolean isSameAs(ATermAppl ind1, ATermAppl ind2) {
		ATermAppl c = ATermUtils.makeValue( ind2 );

		return isType( ind1, c );
	}

	/**
	 * Returns true if individual x belongs to type c. This is a logical
	 * consequence of the KB if in all possible models x belongs to C. This is
	 * checked by trying to construct a model where x belongs to not(c).
	 * 
	 * @param x
	 * @param c
	 * @return
	 */
	public boolean isType(ATermAppl x, ATermAppl c) {
		c = ATermUtils.normalize( c );

		if( !doExplanation() ) {
			Set<ATermAppl> subs;
			if( kb.isClassified() && kb.getTaxonomy().contains( c ) ) {
				subs = kb.getTaxonomy().getFlattenedSubs( c, false );
				subs.remove( ATermUtils.BOTTOM );
			}
            else {
	            subs = SetUtils.emptySet();
            }
	
			Bool type = isKnownType( x, c, subs );
			if( type.isKnown() ) {
	            return type.isTrue();
            }
		}
		// List list = (List) kb.instances.get( c );
		// if( list != null )
		// return list.contains( x );

		if( log.isLoggable( Level.FINE ) ) {
	        log.fine( "Checking type " + ATermUtils.toString( c ) + " for individual " + ATermUtils.toString( x ) );
        }

		ATermAppl notC = ATermUtils.negate( c );

		Timer t = kb.timers.startTimer( "isType" );
		boolean isType = !isConsistent( SetUtils.singleton( x ), notC, false );
		t.stop();

		if( log.isLoggable( Level.FINE ) ) {
	        log.fine( "Type " + isType + " " + ATermUtils.toString( c ) + " for individual " + ATermUtils.toString( x ) );
        }

		return isType;
	}

	/**
	 * Returns true if any of the individuals in the given list belongs to type
	 * c.
	 * 
	 * @param c
	 * @param inds
	 * @return
	 */
	public boolean isType(List<ATermAppl> inds, ATermAppl c) {
		c = ATermUtils.normalize( c );

		if( log.isLoggable( Level.FINE ) ) {
	        log.fine( "Checking type " + ATermUtils.toString( c ) + " for individuals " + inds.size() );
        }

		ATermAppl notC = ATermUtils.negate( c );

		boolean isType = !isConsistent( inds, notC, false );

		if( log.isLoggable( Level.FINE ) ) {
	        log.fine( "Type " + isType + " " + ATermUtils.toString( c ) + " for individuals " + inds.size() );
        }

		return isType;
	}

	public Bool hasObviousPropertyValue(ATermAppl s, ATermAppl p, ATermAppl o) {
		Role prop = getRole( p );

		if( prop.isDatatypeRole() ) {
			try {
				Object value = (o == null)
					? null
					: dtReasoner.getValue( o );
				return hasObviousDataPropertyValue( s, p, value );
			} catch( UnrecognizedDatatypeException e ) {
				log
						.warning( format(
								"Returning false for property value check (%s,%s,%s) due to datatype problem with input literal: %s",
								s, p, o, e.getMessage() ) );
				return Bool.FALSE;
			} catch( InvalidLiteralException e ) {
				log
						.warning( format(
								"Returning false for property value check (%s,%s,%s) due to problem with input literal: %s",
								s, p, o, e.getMessage() ) );
				return Bool.FALSE;
			}
		}
        else {
	        return hasObviousObjectPropertyValue( s, p, o );
        }
	}

	public Bool hasObviousDataPropertyValue(ATermAppl s, ATermAppl p, Object value) {
		assert isComplete() : "Initial consistency check has not been performed!";
		
		Individual subj = getIndividual( s );
		Role prop = getRole( p );

		if( prop.isTop() ) {
	        return Bool.TRUE;
        }
        else if( prop.isBottom() ) {
	        return Bool.FALSE;
        }
		
		// if onlyPositive is set then the answer returned is sound but not
		// complete so we cannot return negative answers
		boolean onlyPositive = false;

		if( !subj.getMergeDependency( true ).isIndependent() ) {
			onlyPositive = true;
			subj = getIndividual( s );
		}
        else {
	        subj = subj.getSame();
        }

		Bool hasValue = subj.hasDataPropertyValue( prop, value );
		if( onlyPositive && hasValue.isFalse() ) {
	        return Bool.UNKNOWN;
        }

		return hasValue;
	}

	public Bool hasObviousObjectPropertyValue(ATermAppl s, ATermAppl p, ATermAppl o) {
		Role prop = getRole( p );
		
		if( prop.isTop() ) {
	        return Bool.TRUE;
        }
        else if( prop.isBottom() ) {
	        return Bool.FALSE;
        }

		Set<ATermAppl> knowns = new HashSet<ATermAppl>();
		Set<ATermAppl> unknowns = new HashSet<ATermAppl>();

		getObjectPropertyValues( s, prop, knowns, unknowns, true );

		if( o == null ) {
			if( !knowns.isEmpty() ) {
	            return Bool.TRUE;
            }
            else if( !unknowns.isEmpty() ) {
	            return Bool.UNKNOWN;
            }
            else {
	            return Bool.FALSE;
            }
		}
		else {
			if( knowns.contains( o ) ) {
	            return Bool.TRUE;
            }
            else if( unknowns.contains( o ) ) {
	            return Bool.UNKNOWN;
            }
            else {
	            return Bool.FALSE;
            }
		}
	}

	public boolean hasPropertyValue(ATermAppl s, ATermAppl p, ATermAppl o) {		
		Bool hasObviousValue = hasObviousPropertyValue( s, p, o );
		if( hasObviousValue.isKnown() ) {
			if( hasObviousValue.isFalse() || !doExplanation() ) {
	            return hasObviousValue.isTrue();
            }
		}

		ATermAppl c = null;
		if( o == null ) {
			if( kb.isDatatypeProperty( p ) ) {
	            c = ATermUtils.makeMin( p, 1, ATermUtils.TOP_LIT );
            }
            else {
	            c = ATermUtils.makeMin( p, 1, ATermUtils.TOP );
            }
		}
		else {
			c = ATermUtils.makeHasValue( p, o );
		}

		boolean isType = isType( s, c );

		return isType;
	}

	public List<ATermAppl> getDataPropertyValues(ATermAppl s, Role role, ATermAppl datatype) {
		return getDataPropertyValues( s, role, datatype, false );
	}

	public List<ATermAppl> getDataPropertyValues(ATermAppl s, Role role, ATermAppl datatype,
			boolean onlyObvious) {
		assert isComplete() : "Initial consistency check has not been performed!";
		
		Individual subj = getIndividual( s );

		List<ATermAppl> values = new ArrayList<ATermAppl>();

		boolean isIndependent = true;
		if( subj.isMerged() ) {
			isIndependent = subj.getMergeDependency( true ).isIndependent();
			subj = subj.getSame();
		}

		EdgeList edges = subj.getRSuccessorEdges( role );
		for( int i = 0; i < edges.size(); i++ ) {
			Edge edge = edges.edgeAt( i );
			DependencySet ds = edge.getDepends();
			final Literal literal = (Literal) edge.getTo();
			final ATermAppl literalValue = literal.getTerm();
			if( literalValue != null ) {
				if( datatype != null ) {
					if( !literal.hasType( datatype ) ) {
						try {
							if( !dtReasoner.isSatisfiable( Collections.singleton( datatype ),
									literal.getValue() ) ) {
	                            continue;
                            }
						} catch( DatatypeReasonerException e ) {
							final String msg = format(
									"Unexpected datatype reasoner exception while fetching property values (%s,%s,%s): %s",
									s, role, datatype, e.getMessage() );
							log.severe( msg );
							throw new InternalReasonerException( msg );
						}
					}
				}

				if( isIndependent && ds.isIndependent() ) {
	                values.add( literalValue );
                }
                else if( !onlyObvious ) {
					ATermAppl hasValue = ATermUtils.makeHasValue( role.getName(), literalValue );
					if( isType( s, hasValue ) ) {
	                    values.add( literalValue );
                    }
				}
			}
		}

		return values;
	}

	public List<ATermAppl> getObviousDataPropertyValues(ATermAppl s, Role prop, ATermAppl datatype) {
		return getDataPropertyValues( s, prop, datatype, true );
	}

	public void getObjectPropertyValues(ATermAppl s, Role role, Set<ATermAppl> knowns,
			Set<ATermAppl> unknowns, boolean getSames) {
		assert isComplete() : "Initial consistency check has not been performed!";

		Individual subj = getIndividual( s );

		boolean isIndependent = true;
		if( subj.isMerged() ) {
			isIndependent = subj.getMergeDependency( true ).isIndependent();
			subj = subj.getSame();
		}

		if( role.isSimple() ) {
	        getSimpleObjectPropertyValues( subj, role, knowns, unknowns, getSames );
        }
        else if( !role.hasComplexSubRole() ) {
	        getTransitivePropertyValues( subj, role, knowns, unknowns, getSames,
					new HashMap<Individual,Set<Role>>(), true );
        }
        else {
			TransitionGraph<Role> tg = role.getFSM();
			getComplexObjectPropertyValues( subj, tg.getInitialState(), tg, knowns, unknowns,
					getSames, new HashMap<Individual,Set<State<Role>>>(), true );
		}

		if( !isIndependent ) {
			unknowns.addAll( knowns );
			knowns.clear();
		}
	}

	void getSimpleObjectPropertyValues(Individual subj, Role role, Set<ATermAppl> knowns,
			Set<ATermAppl> unknowns, boolean getSames) {
		EdgeList edges = subj.getRNeighborEdges( role );
		for( int i = 0; i < edges.size(); i++ ) {
			Edge edge = edges.edgeAt( i );
			DependencySet ds = edge.getDepends();
			Individual value = (Individual) edge.getNeighbor( subj );

			if( value.isRootNominal() ) {
				if( ds.isIndependent() ) {
					if( getSames ) {
	                    getSames( value, knowns, unknowns );
                    }
                    else {
	                    knowns.add( value.getName() );
                    }
				}
				else {
					if( getSames ) {
	                    getSames( value, unknowns, unknowns );
                    }
                    else {
	                    unknowns.add( value.getName() );
                    }
				}
			}
		}
	}

	void getTransitivePropertyValues(Individual subj, Role prop, Set<ATermAppl> knowns,
			Set<ATermAppl> unknowns, boolean getSames, Map<Individual,Set<Role>> visited, boolean isIndependent) {		
		if( !MultiMapUtils.addAll( visited, subj, prop.getSubRoles() ) ) {
	        return;
        }

		EdgeList edges = subj.getRNeighborEdges( prop );
		for( int i = 0; i < edges.size(); i++ ) {
			Edge edge = edges.edgeAt( i );
			DependencySet ds = edge.getDepends();
			Individual value = (Individual) edge.getNeighbor( subj );
			Role edgeRole = edge.getFrom().equals( subj )
				? edge.getRole()
				: edge.getRole().getInverse();
			if( value.isRootNominal() ) {
				if( isIndependent && ds.isIndependent() ) {
					if( getSames ) {
	                    getSames( value, knowns, unknowns );
                    }
                    else {
	                    knowns.add( value.getName() );
                    }
				}
				else {
					if( getSames ) {
	                    getSames( value, unknowns, unknowns );
                    }
                    else {
	                    unknowns.add( value.getName() );
                    }
				}
			}

			if( !prop.isSimple() ) {
				// all the following roles might cause this property to
				// propagate
				Set<Role> transRoles = SetUtils.intersection( edgeRole.getSuperRoles(), prop
						.getTransitiveSubRoles() );
				for( Iterator<Role> j = transRoles.iterator(); j.hasNext(); ) {
					Role transRole = j.next();
					getTransitivePropertyValues( value, transRole, knowns, unknowns, getSames,
							visited, isIndependent && ds.isIndependent() );
				}
			}
		}
	}

	void getComplexObjectPropertyValues(Individual subj, State<Role> st, TransitionGraph<Role> tg,
			Set<ATermAppl> knowns, Set<ATermAppl> unknowns, boolean getSames,
			HashMap<Individual,Set<State<Role>>> visited, boolean isIndependent) {
		if( !MultiMapUtils.add( visited, subj, st ) ) {
	        return;
        }

		if( tg.isFinal( st ) && subj.isRootNominal() ) {
			log.fine( "add " + subj );
			if( isIndependent ) {
				if( getSames ) {
	                getSames( subj, knowns, unknowns );
                }
                else {
	                knowns.add( subj.getName() );
                }
			}
			else {
				if( getSames ) {
	                getSames( subj, unknowns, unknowns );
                }
                else {
	                unknowns.add( subj.getName() );
                }
			}
		}

		log.fine( subj.toString() );

		for( Transition<Role> t : st.getTransitions() ) {
			Role r = t.getName();
			EdgeList edges = subj.getRNeighborEdges( r );
			for( int i = 0; i < edges.size(); i++ ) {
				Edge edge = edges.edgeAt( i );
				DependencySet ds = edge.getDepends();
				Individual value = (Individual) edge.getNeighbor( subj );

				getComplexObjectPropertyValues( value, t.getTo(), tg, knowns, unknowns, getSames,
						visited, isIndependent && ds.isIndependent() );
			}
		}
	}

	public void getSames(Individual ind, Set<ATermAppl> knowns, Set<ATermAppl> unknowns) {
		knowns.add( ind.getName() );

		boolean thisMerged = ind.isMerged() && !ind.getMergeDependency( true ).isIndependent();

		for( Node other : ind.getMerged() ) {
			if( !other.isRootNominal() ) {
	            continue;
            }

			boolean otherMerged = other.isMerged()
					&& !other.getMergeDependency( true ).isIndependent();
			if( thisMerged || otherMerged ) {
				unknowns.add( other.getName() );
				getSames( (Individual) other, unknowns, unknowns );
			}
			else {
				knowns.add( other.getName() );
				getSames( (Individual) other, knowns, unknowns );
			}
		}
	}

	/**
	 * Return true if this ABox is consistent. Consistent ABox means after
	 * applying all the tableau completion rules at least one branch with no
	 * clashes was found
	 * 
	 * @return
	 */
	public boolean isConsistent() {
		boolean isConsistent = false;
		
		checkAssertedClashes();
			
		isConsistent = isConsistent( SetUtils.<ATermAppl>emptySet(), null, false );

		if( isConsistent ) {
			// put the BOTTOM concept into the cache which will
			// also put TOP in there
			cache.putSat( ATermUtils.BOTTOM, false );

			assert isComplete() : "ABox not marked complete!";
		}		

		return isConsistent;
	}
	
	/**
	 * Checks if all the previous asserted clashes are resolved. If there is an
	 * unresolved clash, the clash will be set to the first such clash found
	 * (selection is arbitrary). The clash remains unchanged if all clashes are
	 * resolved. That is, the clash might be non-null after this function even
	 * if all asserted clashes are This function is used when incremental
	 * deletion is disabled.
	 */
	private boolean checkAssertedClashes() {
		Iterator<Clash> i = assertedClashes.iterator();
		while( i.hasNext() ) {
			Clash clash = i.next();				
			Node node = clash.getNode();
			ATermAppl term = clash.args != null 
				? (ATermAppl) clash.args[0]
				: null;
			
			// check if clash is resolved through deletions
			boolean resolved = true;
			switch( clash.getClashType() ) {
			case ATOMIC:
				ATermAppl negation = ATermUtils.negate( term );
				resolved = !node.hasType( term ) || !node.hasType( negation );
				break;
			case NOMINAL:
				resolved = !node.isSame( getNode( term ) );
				break;	
			case INVALID_LITERAL:
				resolved = false;
				break;
			default:
				log.warning( "Unexpected asserted clash type: " + clash );
				break;
			}
			
			if( resolved ) {
				// discard resolved clash
				i.remove();
			}
			else {
				// this clash is not resolved, no point in continuing
				setClash( clash );
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Check the consistency of this ABox possibly after adding some type
	 * assertions. If <code>c</code> is null then nothing is added to ABox
	 * (pure consistency test) and the individuals should be an empty
	 * collection. If <code>c</code> is not null but <code>individuals</code>
	 * is empty, this is a satisfiability check for concept <code>c</code> so
	 * a new individual will be added with type <code>c</code>. If
	 * individuals is not empty, this means we will add type <code>c</code> to
	 * each of the individuals in the collection and check the consistency.
	 * <p>
	 * The consistency checks will be done either on a copy of the ABox or its
	 * pseudo model depending on the situation. In either case this ABox will
	 * not be modified at all. After the consistency check lastCompletion points
	 * to the modified ABox.
	 * 
	 * @param individuals
	 * @param c
	 * @return
	 */
	private boolean isConsistent(Collection<ATermAppl> individuals, ATermAppl c, boolean cacheModel) {
		Timer t = kb.timers.startTimer( "isConsistent" );

		if( log.isLoggable( Level.FINE ) ) {
			if( c == null ) {
	            log.fine( "ABox consistency for " + individuals.size() + " individuals" );
            }
            else {
				StringBuilder sb = new StringBuilder();
				sb.append("[");				
				Iterator<ATermAppl> it = individuals.iterator();
				for( int i = 0; i < 100 && it.hasNext(); i++ ) {
					if( i > 0 ) {
	                    sb.append( ", " );
                    }
					sb.append( ATermUtils.toString( it.next() ) );
				}
				if( it.hasNext() ) {
	                sb.append( ", ..." );
                }
				sb.append("]");
				log.fine( "Consistency " + ATermUtils.toString( c ) + " for " + individuals.size() + " individuals "
						+ sb );
			}
		}

		Expressivity expr = kb.getExpressivityChecker().getExpressivityWith( c );

		// if c is null we are checking the consistency of this ABox as
		// it is and we will not add anything extra
		boolean initialConsistencyCheck = (c == null);

		boolean emptyConsistencyCheck = initialConsistencyCheck && isEmpty();

		// if individuals is empty and we are not building the pseudo
		// model then this is concept satisfiability
		boolean conceptSatisfiability = individuals.isEmpty()
				&& (!initialConsistencyCheck || emptyConsistencyCheck);

		// Check if there are any nominals in the KB or nominal
		// reasoning is disabled
		boolean hasNominal = expr.hasNominal() && !PelletOptions.USE_PSEUDO_NOMINALS;

		// Use empty model only if this is concept satisfiability for a KB
		// where there are no nominals
		boolean canUseEmptyABox = conceptSatisfiability && !hasNominal;

		ATermAppl x = null;
		if( conceptSatisfiability ) {
			x = ATermUtils.CONCEPT_SAT_IND;
			individuals = SetUtils.singleton( x );
		}

		if( emptyConsistencyCheck ) {
	        c = ATermUtils.TOP;
        }

		ABox abox = canUseEmptyABox
			? this.copy( x, false )
			: initialConsistencyCheck
				? this
				: this.copy( x, true );

		for( ATermAppl ind : individuals ) {
			abox.setSyntacticUpdate( true );
			abox.addType( ind, c );
			abox.setSyntacticUpdate( false );
		}

		if( log.isLoggable( Level.FINE ) ) {
	        log.fine( "Consistency check starts" );
        }

		CompletionStrategy strategy = kb.chooseStrategy( abox, expr );

		if( log.isLoggable( Level.FINE ) ) {
	        log.fine( "Strategy: " + strategy.getClass().getName() );
        }

		Timer completionTimer = kb.timers.getTimer( "complete" );
		completionTimer.start();
		try {
			strategy.complete( expr );
		}
		finally {
			completionTimer.stop();
		}

		boolean consistent = !abox.isClosed();

		if( x != null && c != null && cacheModel ) {
			cache( abox.getIndividual( x ), c, consistent );
		}
		
		if( log.isLoggable( Level.FINE ) ) {
			log.fine( "Consistent: " + consistent + " Time: " + t.getElapsed()
					+" Branches " + abox.branches.size()
					+" Tree depth: " + abox.stats.treeDepth + " Tree size: " + abox.getNodes().size()
					+" Restores " + abox.stats.globalRestores + " global " + abox.stats.localRestores + " local"
					+" Backtracks " + abox.stats.backtracks + " avg backjump " + (abox.stats.backjumps/(double)abox.stats.backtracks));
		}

		if( consistent ) {
			if( initialConsistencyCheck && isEmpty() ) {
	            setComplete( true );
            }
		}
		else {
			lastClash = abox.getClash();
			if( log.isLoggable( Level.FINE ) ) {
	            log.fine( "Clash: " + abox.getClash().detailedString() );
            }
			if( doExplanation && PelletOptions.USE_TRACING ) {
				if( individuals.size() == 1 ) {				
					ATermAppl ind = individuals.iterator().next();

					ATermAppl tempAxiom = ATermUtils.makeTypeAtom( ind, c );
					Set<ATermAppl> explanationSet = getExplanationSet();
					boolean removed = explanationSet.remove( tempAxiom );
					if( !removed ) {
						if( log.isLoggable( Level.FINE ) ) {
	                        log.fine( "Explanation set is missing an axiom.\n\tAxiom: " + tempAxiom
									+ "\n\tExplantionSet: " + explanationSet );
                        }
					}
				}
				if( log.isLoggable( Level.FINE ) ) {
					StringBuilder sb = new StringBuilder();
					for( ATermAppl axiom : getExplanationSet() ) {
						sb.append( "\n\t" );
						sb.append( ATermUtils.toString( axiom ) );
					}
					log.fine( "Explanation: " + sb );
				}
			}
		}

		stats.consistencyCount++;

		if( keepLastCompletion ) {
	        lastCompletion = abox;
        }
        else {
	        lastCompletion = null;
        }

		t.stop();

		return consistent;
	}

	/**
	 * Check the consistency of this ABox using the incremental consistency
	 * checking approach
	 */
	boolean isIncConsistent() {
		assert isComplete() : "Initial consistency check has not been performed!";

		Timer incT = kb.timers.startTimer( "isIncConsistent" );
		Timer t = kb.timers.startTimer( "isConsistent" );

		// throw away old information to let gc do its work
		lastCompletion = null;
		
		if( log.isLoggable( Level.FINE ) ) {
	        log.fine( "Consistency check starts" );
        }
		
		// currently there is only one incremental consistency strategy
		CompletionStrategy incStrategy = new SROIQIncStrategy( this );

		if( log.isLoggable( Level.FINE ) ) {
	        log.fine( "Strategy: " + incStrategy.getClass().getName() );
        }

		// set abox to not being complete
		setComplete( false );
		Timer completionTimer = kb.timers.getTimer( "complete" );
		completionTimer.start();
		try {
			incStrategy.complete(kb.getExpressivityChecker().getExpressivity());
		}
		finally {
			completionTimer.stop();
		}
		
		boolean consistent = !isClosed();

		if( log.isLoggable( Level.FINE ) ) {
	        log.fine( "Consistent: " + consistent + " Tree depth: " + stats.treeDepth
					+ " Tree size: " + getNodes().size() );
        }

		if( !consistent ) {
			lastClash = getClash();
			if( log.isLoggable( Level.FINE ) ) {
	            log.fine( getClash().detailedString() );
            }
		}

		stats.consistencyCount++;

		lastCompletion = this;

		t.stop();
		incT.stop();
		
		// do not clear the clash information 

		// ((Log4JLogger)ABox.log).getLogger().setLevel(Level.OFF);
		// ((Log4JLogger)DependencyIndex.log).getLogger().setLevel(Level.OFF);

		return consistent;
	}

	public EdgeList getInEdges(ATerm x) {
		return getNode( x ).getInEdges();
	}

	public EdgeList getOutEdges(ATerm x) {
		Node node = getNode( x );
		if( node instanceof Literal ) {
	        return new EdgeList();
        }
		return ((Individual) node).getOutEdges();
	}

	public Individual getIndividual(ATerm x) {
		Object o = nodes.get( x );
		if(o instanceof Individual){
			return (Individual) o;
		}
		return null;
	}

	public Literal getLiteral(ATerm x) {
		Object o = nodes.get( x );
		if(o instanceof Literal){
			return (Literal) o;
		}
		return null;
	}

	public Node getNode(ATerm x) {
		return nodes.get( x );
	}

	public void addType(ATermAppl x, ATermAppl c) {
		DependencySet ds = PelletOptions.USE_TRACING
			? new DependencySet( ATermUtils.makeTypeAtom( x, c ) )
			: DependencySet.INDEPENDENT;

		addType( x, c, ds );
	}

	public void addType(ATermAppl x, ATermAppl c, DependencySet ds) {
		c = ATermUtils.normalize( c );

		// when a type is being added to 
		// an ABox that has already been completed, the branch
		// of the dependency set will automatically be set to
		// the current branch. We need to set it to the initial
		// branch number to make sure that this type assertion
		// will not be removed during backtracking
		int remember = branch;
		setBranch( DependencySet.NO_BRANCH );

		Individual node = getIndividual( x );
		node.addType( c, ds, false );
		
        while( node.isMerged() ) {
			ds = ds.union( node.getMergeDependency( false ), doExplanation );
			node = (Individual) node.getMergedTo();
			node.addType( c, ds, !node.isMerged() );
		}	

        setBranch( remember );
	}
	
	public Edge addEdge(ATermAppl p, ATermAppl s, ATermAppl o, DependencySet ds) {	
		Role role = getRole( p );
		Individual subj = getIndividual( s );
		Node obj = getNode( o );
		
		if( subj.isMerged() && obj.isMerged() ) {
	        return null;
        }		

		if( obj.isMerged() ) {
			obj.addInEdge( new DefaultEdge(role, subj, obj, ds) );
			ds = ds.union( obj.getMergeDependency( true ), true );
			ds = ds.copy( ds.max() + 1 );
			obj = obj.getSame();
		}
				
		Edge edge = new DefaultEdge(role, subj, obj, ds);
		Edge existingEdge = subj.getOutEdges().getExactEdge( subj, role, obj ); 
		if( existingEdge == null ) {
			subj.addOutEdge( edge );
		}
		else if( !existingEdge.getDepends().isIndependent() ) {
			subj.removeEdge( existingEdge );
			subj.addOutEdge( edge );
		}
		
		if( subj.isMerged() ) {
			ds = ds.union( subj.getMergeDependency( true ), true );
			ds = ds.copy( ds.max() + 1 );
			subj = subj.getSame();
			edge = new DefaultEdge(role, subj, obj, ds);
			
			if( subj.getOutEdges().hasEdge( edge ) ) {
	            return null;
            }
			
			subj.addOutEdge( edge );
			obj.addInEdge( edge );			
		}
		else if( existingEdge == null ) {
			obj.addInEdge( edge );
		}
		else if( !existingEdge.getDepends().isIndependent() ) {
			obj.removeInEdge( existingEdge );
			obj.addInEdge( edge );
		}
		
		return edge;
	}

	/**
	 * Remove the given node from the node map which maps names to nodes. Does not remove the node from the node list 
	 * or other nodes' edge lists.
	 * 
	 * @param x
	 * @return
	 */
	public boolean removeNode(ATermAppl x) {
		return (nodes.remove( x ) != null);
	}
	
	public void removeType(ATermAppl x, ATermAppl c) {
		c = ATermUtils.normalize( c );

		Node node = getNode( x );
		node.removeType( c );
	}

	/**
	 * Add a new literal to the ABox. This function is used only when the
	 * literal value does not have a known value, e.g. applyMinRule would create
	 * such a literal.
	 * 
	 * @return
	 */
	public Literal addLiteral(DependencySet ds) {
		return createLiteral( ATermUtils.makeLiteral( createUniqueName( false ) ), ds );
	}

	/**
	 * Add a new literal to the ABox. Literal will be assigned a fresh unique
	 * name.
	 * 
	 * @param dataValue
	 *            A literal ATerm which should be constructed with one of
	 *            ATermUtils.makeXXXLiteral functions
	 * @return Literal object that has been created
	 */
	public Literal addLiteral(ATermAppl dataValue) {
		int remember = getBranch();
		setBranch( DependencySet.NO_BRANCH );
		
		Literal lit = addLiteral( dataValue, DependencySet.INDEPENDENT );
		
		setBranch( remember );
		
		return lit;
	}

	public Literal addLiteral(ATermAppl dataValue, DependencySet ds) {
		if( dataValue == null || !ATermUtils.isLiteral( dataValue ) ) {
	        throw new InternalReasonerException( "Invalid value to create a literal. Value: "
					+ dataValue );
        }

		return createLiteral( dataValue, ds );
	}

	/**
	 * Helper function to add a literal.
	 * 
	 * @param value
	 *            The java object that represents the value of this literal
	 * @return
	 */
	private Literal createLiteral(ATermAppl dataValue, DependencySet ds) {
		ATermAppl name;
		/*
		 * No datatype means the literal is an anonymous variable created for a
		 * min cardinality or some values from restriction.
		 */
		if( ATermUtils.NO_DATATYPE.equals( dataValue.getArgument( ATermUtils.LIT_URI_INDEX ) ) ) {
	        name = dataValue;
        }
        else {
			try {
				name = getDatatypeReasoner().getCanonicalRepresentation( dataValue );
			} catch( InvalidLiteralException e ) {
				final String msg = format( "Attempt to create an invalid literal (%s): %s",
						dataValue, e.getMessage() );
				if (PelletOptions.INVALID_LITERAL_AS_INCONSISTENCY) {
					log.fine( msg );
					name = dataValue;
				} else {
					log.severe( msg );
					throw new InternalReasonerException( msg, e );
				}
			} catch( UnrecognizedDatatypeException e ) {
				final String msg = format(
						"Attempt to create a literal with an unrecognized datatype (%s): %s",
						dataValue, e.getMessage() );
				log.severe( msg );
				throw new InternalReasonerException( msg, e );
			}
		}

		Node node = getNode( name );
		if( node != null ) {
			if( node instanceof Literal ) {

				if( ((Literal) node).getValue() == null && PelletOptions.USE_COMPLETION_QUEUE ) {
					// added for completion queue
					QueueElement newElement = new QueueElement( node );
					this.completionQueue.add( newElement, NodeSelector.LITERAL );
				}

				if( getBranch() >= 0 && PelletOptions.TRACK_BRANCH_EFFECTS ) {
	                branchEffects.add( getBranch(), node.getName() );
                }

				return (Literal) node;
			}
            else {
	            throw new InternalReasonerException(
						"Same term refers to both a literal and an individual: " + name );
            }
		}

		int remember = branch;
		setBranch( DependencySet.NO_BRANCH );
		
		/*
		 * TODO Investigate the effects of storing asserted value 
		 * The input version of the literal is not discarded, only the canonical
		 * versions are stored in the literal. This may cause problems in cases 
		 * where the same value space object is presented in the data in multiple 
		 * forms.
		 */
		Literal lit = new Literal( name, dataValue, this, ds );
		lit.addType( ATermUtils.TOP_LIT, ds );
		
		setBranch( remember );
		
		nodes.put( name, lit );
		nodeList.add( name );

		if( lit.getValue() == null && PelletOptions.USE_COMPLETION_QUEUE ) {
			// added for completion queue
			QueueElement newElement = new QueueElement( lit );
			this.completionQueue.add( newElement, NodeSelector.LITERAL );
		}

		if( getBranch() >= 0 && PelletOptions.TRACK_BRANCH_EFFECTS ) {
	        branchEffects.add( getBranch(), lit.getName() );
        }

		return lit;
	}

	public Individual addIndividual(ATermAppl x, DependencySet ds) {
		Individual ind = addIndividual( x, null, ds );

		// update affected inds for this branch
		if( getBranch() >= 0 && PelletOptions.TRACK_BRANCH_EFFECTS ) {
	        branchEffects.add( getBranch(), ind.getName() );
        }
		
		return ind;
	}

	public Individual addFreshIndividual(Individual parent, DependencySet ds) {
		boolean isNominal = parent == null;
		ATermAppl name = createUniqueName( isNominal );
		Individual ind = addIndividual( name, parent, ds );

		if( isNominal ) {
	        ind.setNominalLevel( 1 );
        }

		return ind;
	}

	private Individual addIndividual(ATermAppl x, Individual parent, DependencySet ds) {
		if( nodes.containsKey( x ) ) {
	        throw new InternalReasonerException( "adding a node twice " + x );
        }

		setChanged( true );

		Individual n = new Individual( x, this, parent );

		nodes.put( x, n );
		nodeList.add( x );
		
		if( n.getDepth() > stats.treeDepth ) {
			stats.treeDepth = n.getDepth();
			if( log.isLoggable( Level.FINER )) {
	            log.finer( "Depth: " + stats.treeDepth + " Size: " + size() );
            }
		}
		
		//this must be performed after the nodeList is updated as this call will update the completion queues
		n.addType( ATermUtils.TOP, ds );

		if( getBranch() > 0 && PelletOptions.TRACK_BRANCH_EFFECTS ) {
	        branchEffects.add( getBranch(), n.getName() );
        }

		return n;
	}

	public void addSame(ATermAppl x, ATermAppl y) {
		Individual ind1 = getIndividual( x );
		Individual ind2 = getIndividual( y );

		// ind1.setSame(ind2, new DependencySet(explanationTable.getCurrent()));

		// ind1.setSame(ind2, DependencySet.INDEPENDENT);
		ATermAppl sameAxiom = ATermUtils.makeSameAs( x, y );

		// update syntactic assertions - currently i do not add this to the
		// dependency index
		// now, as it will be added during the actual merge when the completion
		// is performed
		if( PelletOptions.USE_INCREMENTAL_DELETION ) {
	        kb.getSyntacticAssertions().add( sameAxiom );
        }

		DependencySet ds = PelletOptions.USE_TRACING
			? new DependencySet( sameAxiom )
			: DependencySet.INDEPENDENT;
		getToBeMerged().add( new NodeMerge( ind1, ind2, ds ) );
	}

	public void addDifferent(ATermAppl x, ATermAppl y) {
		Individual ind1 = getIndividual( x );
		Individual ind2 = getIndividual( y );

		ATermAppl diffAxiom = ATermUtils.makeDifferent( x, y );

		// update syntactic assertions - currently i do not add this to the
		// dependency index
		// now, as it will simply be used during the completion strategy
		if( PelletOptions.USE_INCREMENTAL_DELETION ) {
	        kb.getSyntacticAssertions().add( diffAxiom );
        }

		// ind1.setDifferent(ind2, new
		// DependencySet(explanationTable.getCurrent()));
		DependencySet ds = PelletOptions.USE_TRACING
			? new DependencySet( diffAxiom )
			: DependencySet.INDEPENDENT;

		// Temporarily reset the branch so that this assertion survives resets
		final int remember = branch;
		setBranch( DependencySet.NO_BRANCH );
		
		ind1.setDifferent( ind2, ds );
		
		setBranch( remember );
	}

	public void addAllDifferent(ATermList list) {
		ATermAppl allDifferent = ATermUtils.makeAllDifferent( list );
		ATermList outer = list;
		while( !outer.isEmpty() ) {
			ATermList inner = outer.getNext();
			while( !inner.isEmpty() ) {
				Individual ind1 = getIndividual( outer.getFirst() );
				Individual ind2 = getIndividual( inner.getFirst() );

				// update syntactic assertions - currently i do not add this to
				// the dependency index
				// now, as it will be added during the actual merge when the
				// completion is performed
				if( PelletOptions.USE_INCREMENTAL_DELETION ) {
	                kb.getSyntacticAssertions().add( allDifferent );
                }

				DependencySet ds = PelletOptions.USE_TRACING
					? new DependencySet( allDifferent )
					: DependencySet.INDEPENDENT;
					
				final int remember = branch;
				setBranch( DependencySet.NO_BRANCH );
				
				ind1.setDifferent( ind2, ds );
				
				setBranch( remember );

				inner = inner.getNext();
			}
			outer = outer.getNext();
		}
	}

	public boolean isNode(ATerm x) {
		return getNode( x ) != null;
	}

	final public ATermAppl createUniqueName(boolean isNominal) {
		anonCount++;

		ATermAppl name = isNominal
			? ATermUtils.makeAnonNominal( anonCount )
			: ATermUtils.makeAnon( anonCount );

		return name;
	}

	final public Collection<Node> getNodes() {
		return nodes.values();
	}

	final public List<ATermAppl> getNodeNames() {
		return nodeList;
	}

	@Override
    public String toString() {
		return "[size: " + nodes.size() + " freeMemory: "
				+ (Runtime.getRuntime().freeMemory() / 1000000.0) + "mb]";
	}

	/**
	 * @return Returns the datatype reasoner.
	 */
	public DatatypeReasoner getDatatypeReasoner() {
		return dtReasoner;
	}

	/**
	 * @return Returns the isComplete.
	 */
	public boolean isComplete() {
		return isComplete;
	}

	/**
	 * @param isComplete
	 *            The isComplete to set.
	 */
	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	/**
	 * Returns true if Abox has a clash.
	 * 
	 * @return
	 */
	public boolean isClosed() {
		return !PelletOptions.SATURATE_TABLEAU && clash != null;
	}

	public Clash getClash() {
		return clash;
	}

	public void setClash(Clash clash) {
		if( clash != null ) {
			if( log.isLoggable( Level.FINER ) ) {
				log.finer( "CLSH: " + clash );
				if( clash.getDepends().max() > branch && branch != -1 ) {
	                log.severe( "Invalid clash dependency " + clash + " > " + branch );
                }
			}
			
			if( branch == DependencySet.NO_BRANCH
					&& clash.getDepends().getBranch() == DependencySet.NO_BRANCH ) {
				assertedClashes.add( clash );
			}
			
			if( this.clash != null ) {
				if( log.isLoggable( Level.FINER ) ) {
	                log.finer( "Clash was already set \nExisting: " + this.clash + "\nNew     : "
							+ clash );
                }

				if( this.clash.getDepends().max() < clash.getDepends().max() ) {
	                return;
                }
			}
		}

		this.clash = clash;
		// CHW - added for incremental deletions
		if( PelletOptions.USE_INCREMENTAL_DELETION ) {
	        kb.getDependencyIndex().setClashDependencies( this.clash );
        }

	}

	/**
	 * @return Returns the kb.
	 */
	public KnowledgeBase getKB() {
		return kb;
	}

	/**
	 * Convenience function to get the named role.
	 */
	public Role getRole(ATerm r) {
		return kb.getRole( r );
	}

	/**
	 * Return the RBox
	 */
	public RBox getRBox() {
		return kb.getRBox();
	}

	/**
	 * Return the TBox
	 */
	public TBox getTBox() {
		return kb.getTBox();
	}

	/**
	 * Return the current branch number. Branches are created when a
	 * non-deterministic rule, e.g. disjunction or max rule, is being applied.
	 * 
	 * @return Returns the branch.
	 */
	public int getBranch() {
		return branch;
	}

	/**
	 * Set the branch number (should only be called when backjumping is in
	 * progress)
	 * 
	 * @param branch
	 */
	public void setBranch(int branch) {
		this.branch = branch;
	}

	/**
	 * Increment the branch number (should only be called when a
	 * non-deterministic rule, e.g. disjunction or max rule, is being applied)
	 * 
	 * @param branch
	 */
	public void incrementBranch() {

		if( PelletOptions.USE_COMPLETION_QUEUE ) {
			completionQueue.incrementBranch( this.branch );
		}

		this.branch++;
	}

	/**
	 * Check if the ABox is ready to be completed.
	 * 
	 * @return Returns the initialized.
	 */
	public boolean isInitialized() {
		return initialized && !kb.isChanged();
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	/**
	 * Checks if the explanation is turned on.
	 * 
	 * @return Returns the doExplanation.
	 */
	final public boolean doExplanation() {
		return doExplanation;
	}

	/**
	 * Enable/disable explanation generation
	 * 
	 * @param doExplanation
	 *            The doExplanation to set.
	 */
	public void setDoExplanation(boolean doExplanation) {
		this.doExplanation = doExplanation;
	}
	
	public void setExplanation(DependencySet ds) {
		lastClash = Clash.unexplained( null, ds );
	}
	
	public String getExplanation() {
		// Clash lastClash = (lastCompletion != null) ?
		// lastCompletion.getClash() : null;
		if( lastClash == null ) {
	        return "No inconsistency was found! There is no explanation generated.";
        }
        else {
	        return lastClash.detailedString();
        }
	}

	public Set<ATermAppl> getExplanationSet() {
		if( lastClash == null ) {
	        throw new RuntimeException( "No explanation was generated!" );
        }

		return lastClash.getDepends().getExplain();
	}
	
	public BranchEffectTracker getBranchEffectTracker() {
		if( branchEffects == null ) {
	        throw new NullPointerException();
        }

		return branchEffects;
	}

	/**
	 * Returns the branches.
	 */
	public List<Branch> getBranches() {
		return branches;
	}

	public IncrementalChangeTracker getIncrementalChangeTracker() {
		if( incChangeTracker == null ) {
	        throw new NullPointerException();
        }

		return incChangeTracker;
	}
	
	/**
	 * Return individuals to which we need to apply the tableau rules
	 * 
	 * @return
	 */
	public IndividualIterator getIndIterator() {
		return new IndividualIterator( this );
	}

	/**
	 * Validate all the edges in the ABox nodes. Used to find bugs in the copy
	 * and detach/attach functions.
	 */
	public void validate() {
		if( !PelletOptions.VALIDATE_ABOX ) {
	        return;
        }
		System.out.print( "VALIDATING..." );
		Iterator<Individual> n = getIndIterator();
		while( n.hasNext() ) {
			Individual node = n.next();
			if( node.isPruned() ) {
	            continue;
            }
			validate( node );
		}
	}
	
	void validateTypes(Individual node, List<ATermAppl> negatedTypes) {
		for( int i = 0, n = negatedTypes.size(); i < n; i++ ) {
			ATermAppl a = negatedTypes.get( i );
			if( a.getArity() == 0 ) {
	            continue;
            }
			ATermAppl notA = (ATermAppl) a.getArgument( 0 );

			if( node.hasType( notA ) ) {
				if( !node.hasType( a ) ) {
	                throw new InternalReasonerException( "Invalid type found: " + node + " "
							+ " " + a + " " + node.debugString() + " " + node.depends );
                }
				throw new InternalReasonerException( "Clash found: " + node + " " + a + " "
						+ node.debugString() + " " + node.depends );
			}
		}
	}

	void validate(Individual node) {
		validateTypes( node, node.getTypes( Node.ATOM ) );
		validateTypes( node, node.getTypes( Node.SOME ) );
		validateTypes( node, node.getTypes( Node.OR ) );
		validateTypes( node, node.getTypes( Node.MAX ) );
		
		if( !node.isRoot() ) {
			EdgeList preds = node.getInEdges();
			boolean validPred = preds.size() == 1 || (preds.size() == 2 && preds.hasEdgeFrom( node ));
			if( !validPred ) {
	            throw new InternalReasonerException( "Invalid blockable node: " + node + " "
						+ node.getInEdges() );
            }

		}
		else if( node.isNominal() ) {
			ATermAppl nominal = ATermUtils.makeValue( node.getName() );
			if( !ATermUtils.isAnonNominal( node.getName() ) && !node.hasType( nominal ) ) {
	            throw new InternalReasonerException( "Invalid nominal node: " + node + " "
						+ node.getTypes() );
            }
		}

		for( Iterator<ATermAppl> i = node.getDepends().keySet().iterator(); i.hasNext(); ) {
			ATermAppl c = i.next();
			DependencySet ds = node.getDepends( c );
			if( ds.max() > branch || (!PelletOptions.USE_SMART_RESTORE && ds.getBranch() > branch) ) {
	            throw new InternalReasonerException( "Invalid ds found: " + node + " " + c + " "
						+ ds + " " + branch );
			// if( c.getAFun().equals( ATermUtils.VALUEFUN ) ) {
			// if( !PelletOptions.USE_PSEUDO_NOMINALS ) {
			// Individual z = getIndividual(c.getArgument(0));
			// if(z == null)
			// throw new InternalReasonerException("Nominal to non-existing
			// node: " + node + " " + c + " " + ds + " " + branch);
			// }
			// }
            }
		}
		for( Iterator<Node> i = node.getDifferents().iterator(); i.hasNext(); ) {
			Node ind = i.next();
			DependencySet ds = node.getDifferenceDependency( ind );
			if( ds.max() > branch || ds.getBranch() > branch ) {
	            throw new InternalReasonerException( "Invalid ds: " + node + " != " + ind + " "
						+ ds );
            }
			if( ind.getDifferenceDependency( node ) == null ) {
	            throw new InternalReasonerException( "Invalid difference: " + node + " != " + ind
						+ " " + ds );
            }
		}
		EdgeList edges = node.getOutEdges();
		for( int e = 0; e < edges.size(); e++ ) {
			Edge edge = edges.edgeAt( e );
			Node succ = edge.getTo();
			if( nodes.get( succ.getName() ) != succ ) {
	            throw new InternalReasonerException( "Invalid edge to a non-existing node: " + edge
						+ " " + nodes.get( succ.getName() ) + "("
						+ nodes.get( succ.getName() ).hashCode() + ")" + succ + "("
						+ succ.hashCode() + ")" );
            }
			if( !succ.getInEdges().hasEdge( edge ) ) {
	            throw new InternalReasonerException( "Invalid edge: " + edge );
            }
			if( succ.isMerged() ) {
	            throw new InternalReasonerException( "Invalid edge to a removed node: " + edge
						+ " " + succ.isMerged() );
            }
			DependencySet ds = edge.getDepends();
			if( ds.max() > branch || ds.getBranch() > branch ) {
	            throw new InternalReasonerException( "Invalid ds: " + edge + " " + ds );
            }
			EdgeList allEdges = node.getEdgesTo( succ );
			if( allEdges.getRoles().size() != allEdges.size() ) {
	            throw new InternalReasonerException( "Duplicate edges: " + allEdges );
            }
		}
		edges = node.getInEdges();
		for( int e = 0; e < edges.size(); e++ ) {
			Edge edge = edges.edgeAt( e );
			DependencySet ds = edge.getDepends();
			if( ds.max() > branch || ds.getBranch() > branch ) {
	            throw new InternalReasonerException( "Invalid ds: " + edge + " " + ds );
            }
		}
	}

	/**
	 * Print the ABox as a completion tree (child nodes are indented).
	 */
	public void printTree() {
		if( !PelletOptions.PRINT_ABOX ) {
	        return;
        }
		System.err.println( "PRINTING... " + DependencySet.INDEPENDENT );
		Iterator<Node> n = nodes.values().iterator();
		while( n.hasNext() ) {
			Node node = n.next();
			if( !node.isRoot() || node instanceof Literal ) {
	            continue;
            }
			printNode( (Individual) node, new HashSet<Individual>(), "   " );
		}
	}

	/**
	 * Print the node in the completion tree.
	 * 
	 * @param node
	 * @param printed
	 * @param indent
	 */
	private void printNode(Individual node, Set<Individual> printed, String indent) {
		boolean printOnlyName = (node.isNominal() && !printed.isEmpty());

		System.err.print( node );
		if( !printed.add( node ) ) {
			System.err.println();
			return;
		}
		if( node.isMerged() ) {
			System.err.println( " -> " + node.getMergedTo() + " "
					+ node.getMergeDependency( false ) );
			return;
		}
		else if( node.isPruned() ) {
	        throw new InternalReasonerException( "Pruned node: " + node );
        }


		System.err.print( " = " );
		for(int i = 0; i < Node.TYPES; i++) {
			for( ATermAppl c : node.getTypes( i ) ) {
				System.err.print( ATermUtils.toString( c ) );
				System.err.print( ", ");
			}
		}
		System.err.println( node.getDifferents() );

		if( printOnlyName ) {
	        return;
        }

		indent += "  ";
		Iterator<Edge> i = node.getOutEdges().iterator();
		while( i.hasNext() ) {
			Edge edge = i.next();
			Node succ = edge.getTo();
			EdgeList edges = node.getEdgesTo( succ );

			System.err.print( indent + "[" );
			for( int e = 0; e < edges.size(); e++ ) {
				if( e > 0 ) {
	                System.err.print( ", " );
                }
				System.err.print( edges.edgeAt( e ).getRole() );
			}
			System.err.print( "] " );
			if( succ instanceof Individual ) {
	            printNode( (Individual) succ, printed, indent );
            }
            else {
	            System.err.println( " (Literal) " + ATermUtils.toString(succ.getName()) + " " + ATermUtils.toString(succ.getTypes()) );
            }
		}
	}

	public Clash getLastClash() {
		return lastClash;
	}

	public ABox getLastCompletion() {
		return lastCompletion;
	}

	public boolean isKeepLastCompletion() {
		return keepLastCompletion;
	}

	public void setKeepLastCompletion(boolean keepLastCompletion) {
		this.keepLastCompletion = keepLastCompletion;
	}

	/**
	 * Return the number of nodes in the ABox. This number includes both the
	 * individuals and the literals.
	 * 
	 * @return
	 */
	public int size() {
		return nodes.size();
	}

	/**
	 * Returns true if there are no individuals in the ABox.
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return nodes.isEmpty();
	}

	public void setLastCompletion(ABox comp) {
		lastCompletion = comp;
	}

	/**
	 * Set whether changes to the update should be treated as syntactic updates,
	 * i.e., if the changes are made on explicit source axioms. This is used for
	 * the completion queue for incremental consistency checking purposes.
	 * 
	 * @param boolean
	 *            val The value
	 */
	protected void setSyntacticUpdate(boolean val) {
		syntacticUpdate = val;
	}

	/**
	 * Set whether changes to the update should be treated as syntactic updates,
	 * i.e., if the changes are made on explicit source axioms. This is used for
	 * the completion queue for incremental consistency checking purposes.
	 * 
	 * @param boolean
	 *            val The value
	 */
	protected boolean isSyntacticUpdate() {
		return syntacticUpdate;
	}

	public CompletionQueue getCompletionQueue() {
		return completionQueue;
	}

	/**
	 * Reset the ABox to contain only asserted information. Any ABox assertion
	 * added by tableau rules will be removed.
	 */
	public void reset() {
		if( !isComplete() ) {
	        return;
        }
		
		setComplete( false );
		
		Iterator<ATermAppl> i = nodeList.iterator();
		while( i.hasNext() ) {
			ATermAppl nodeName = i.next();
			Node node = nodes.get( nodeName );
			if( !node.isRootNominal() ) {
				i.remove();
				nodes.remove( nodeName );
			}
			else {
				node.reset( false );
			}
		}
		
		setComplete( false );
		setInitialized( false );
		// clear the clash. we can safely clear the clash because
		// either this was an asserted clash and already stored in the
		// assertedClashes (and will be verified before consistency change)
		// or this was a clash that occurred during completion and will
		// reoccur (if no already resolved) since we will run the tableau
		// completion again
		setClash( null );
		
		setBranch( DependencySet.NO_BRANCH );
		branches = new ArrayList<Branch>();
		setDisjBranchStats( new HashMap<ATermAppl, int[]>() );
		rulesNotApplied = true;
	}
	
	public void resetQueue() {
		for( Node node : nodes.values() ) {			
			node.reset(true);
		}
	}

	/**
	 * @param anonCount the anonCount to set
	 */
	public int setAnonCount(int anonCount) {
		return this.anonCount = anonCount;
	}

	/**
	 * @return the anonCount
	 */
	public int getAnonCount() {
		return anonCount;
	}

	/**
	 * @param disjBranchStats the disjBranchStats to set
	 */
	public void setDisjBranchStats(Map<ATermAppl, int[]> disjBranchStats) {
		this.disjBranchStats = disjBranchStats;
	}

	/**
	 * @return the disjBranchStats
	 */
	public Map<ATermAppl, int[]> getDisjBranchStats() {
		return disjBranchStats;
	}

	/**
	 * @param changed the changed to set
	 */
	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	/**
	 * @return the changed
	 */
	public boolean isChanged() {
		return changed;
	}

	/**
	 * @return the toBeMerged
	 */
	public List<NodeMerge> getToBeMerged() {
		return toBeMerged;
	}
}
