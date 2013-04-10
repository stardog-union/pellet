// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.incremental;

import java.util.logging.Level;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.tableau.branch.Branch;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.intset.IntSet;

import aterm.ATermAppl;

import com.clarkparsia.pellet.IncrementalChangeTracker;

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
public class IncrementalRestore {
	public static void restoreDependencies(KnowledgeBase kb) {
		IncrementalRestore restore = new IncrementalRestore( kb );
		restore.restoreDependencies();
	}
	
	private KnowledgeBase kb;	

	private IncrementalRestore(KnowledgeBase kb) {
		this.kb = kb;
	}

	/**
	 * Restore a branch add dependency
	 * 
	 * @param assertion
	 * @param branch
	 */
	private void restoreBranchAdd(ATermAppl assertion, BranchAddDependency branch) {
		if( DependencyIndex.log.isLoggable( Level.FINE ) )
			DependencyIndex.log.fine( "    Removing branch add? " + branch.getBranch() );
	
		// get merge dependency
		DependencySet ds = branch.getBranch().getTermDepends();
	
		// remove the dependency
		ds.removeExplain( assertion );
	
		// undo merge if empty
		if( ds.getExplain().isEmpty() ) {
			if( DependencyIndex.log.isLoggable( Level.FINE ) )
				DependencyIndex.log.fine( "           Actually removing branch!" );
	
			Collection<ATermAppl> allEffects = PelletOptions.TRACK_BRANCH_EFFECTS
				? kb.getABox().getBranchEffectTracker().getAll( branch.getBranch().getBranch() )
				: kb.getABox().getNodeNames();
	
			List<IntSet> updatedList = new ArrayList<IntSet>();
	
			for( ATermAppl a : allEffects ) {
	
				// get the actual node
				Node node = kb.getABox().getNode( a );
	
				// update type dependencies
				Map<ATermAppl,DependencySet> types = node.getDepends();
	
				for( Entry<ATermAppl,DependencySet> entry : types.entrySet() ) {
					// get ds for type
					DependencySet tDS = entry.getValue();
	
					// DependencySet.copy() does not create a new bitset object,
					// so we need to track which bitsets have been
					// updated, so we do not process the same bitset multiple
					// times
					boolean exit = false;
					for( int i = 0; i < updatedList.size(); i++ ) {
						if( updatedList.get( i ) == tDS.getDepends() )
							exit = true;
					}
	
					if( exit )
						continue;
	
					updatedList.add( tDS.getDepends() );
	
					// update branch if necessary
					if( tDS.getBranch() > branch.getBranch().getBranch() ) {
						tDS = tDS.copy( tDS.getBranch() - 1 );
					}
	
					for( int i = branch.getBranch().getBranch(); i <= kb.getABox().getBranches().size(); i++ ) {
						// update dependency set
						if( tDS.contains( i ) ) {
							tDS.remove( i );
							tDS.add( i - 1 );
						}
					}
					
					entry.setValue( tDS );
				}
	
				// update edge depdencies
				EdgeList edges = node.getInEdges();
				for( Edge edge: edges ) {
					DependencySet tDS = edge.getDepends();
	
					// DependencySet.copy() does not create a new bitset object,
					// so we need to track which bitsets have been
					// updated, so we do not process the same bitset multiple
					// times
					boolean exit = false;
					for( int i = 0; i < updatedList.size(); i++ ) {
						if( updatedList.get( i ) == tDS.getDepends() )
							exit = true;
					}
	
					if( exit )
						continue;
	
					updatedList.add( tDS.getDepends() );
	
					// update branch if necessary
					if( tDS.getBranch() > branch.getBranch().getBranch() )
						tDS = tDS.copy( edge.getDepends().getBranch() - 1 );
	
					for( int i = branch.getBranch().getBranch(); i <= kb.getABox().getBranches().size(); i++ ) {
						// update dependency set
						if( tDS.contains( i ) ) {
							tDS.remove( i );
							tDS.add( i - 1 );
						}
					}
					
					edge.setDepends( tDS );
				}
	
				// //TODO:The following code update outedges as well - after
				// testing is seems that this is un-necessary
				// if(node instanceof Individual){
				// Individual ind = (Individual)node;
				//					
				// //update edge depdencies
				// //update type dependencies
				// edges = ind.getInEdges();
				// for(Iterator eIt = edges.iterator(); eIt.hasNext();){
				// //get next type
				// Edge edge = (Edge)eIt.next();
				//						
				// //update branch if necessary
				// if(edge.getDepends().branch > branch.getBranch().branch)
				// edge.getDepends().branch--;
				//
				// for(int i = branch.getBranch().branch; i <=
				// kb.getABox().getBranches().size(); i++){
				// //update dependency set
				// if(edge.getDepends().contains(i)){
				// edge.getDepends().remove(i);
				// edge.getDepends().add(i-1);
				// }
				// }
				// }
				// }
			}
	
			if( PelletOptions.TRACK_BRANCH_EFFECTS )
				kb.getABox().getBranchEffectTracker().remove( branch.getBranch().getBranch() + 1 );
	
			// !!!!!!!!!!!!!!!! Next update kb.getABox() branches !!!!!!!!!!!!!!
			// remove the branch from branches
			List<Branch> branches = kb.getABox().getBranches();
	
			// decrease branch id for each branch after the branch we're
			// removing
			// also need to change the dependency set for each label
			for( int i = branch.getBranch().getBranch(); i < branches.size(); i++ ) {
				// cast for ease
				Branch br = branches.get( i );
				
				DependencySet tDS = br.getTermDepends();
				
				// update the term depends in the branch
				if( tDS.getBranch() > branch.getBranch().getBranch() )
					tDS = tDS.copy( tDS.getBranch() - 1 );
	
				for( int j = branch.getBranch().getBranch(); j < kb.getABox().getBranches().size(); j++ ) {
					if( tDS.contains( j ) ) {
						tDS.remove( j );
						tDS.add( j - 1 );
					}
				}
	
				// also need to decrement the branch number
				br.setBranch( br.getBranch() - 1 );
				br.setTermDepends( tDS );
			}
	
			// remove the actual branch
			branches.remove( branch.getBranch() );
	
			// set the branch counter
			kb.getABox().setBranch( kb.getABox().getBranch() - 1 );
		}
	}

	/**
	 * Restore a clash dependency
	 * 
	 * @param assertion
	 * @param clash
	 */
	private void restoreClash(ATermAppl assertion, ClashDependency clash) {
	
		if( DependencyIndex.log.isLoggable( Level.FINE ) )
			DependencyIndex.log.fine( "    Restoring clash dependency clash: " + clash.getClash() );
	
		// remove the dependency
		clash.getClash().getDepends().removeExplain( assertion );
	
		// undo clash if empty and is independent
		if( clash.getClash().getDepends().getExplain().isEmpty() && clash.getClash().getDepends().isIndependent() ) {
			if( DependencyIndex.log.isLoggable( Level.FINE ) )
				DependencyIndex.log.fine( "           Actually removing clash!" );
	
			kb.getABox().setClash( null );
		}
	}

	/**
	 * Restore a disjunct, merge pairs, etc. of a branch that has been closed
	 * due to a clash whose dependency set contains an assertion that has been
	 * deleted
	 * 
	 * @param assertion
	 * @param branch
	 */
	private void restoreCloseBranch(ATermAppl assertion, CloseBranchDependency branch) {
		// only proceed if tryNext is larger than 1!
		if( branch.getTheBranch().getTryNext() > -1 ) {
			if( DependencyIndex.log.isLoggable( Level.FINE ) )
				DependencyIndex.log.fine( "    Undoing branch remove - branch "
						+ branch.getBranch() + "  -  " + branch.getInd() + "   tryNext: "
						+ branch.getTryNext() );
	
			// shift try next for branch
			branch.getTheBranch().shiftTryNext( branch.getTryNext() );
		}
	}

	/**
	 * Method to remove all stuctures dependent on an kb.getABox() assertion from the
	 * kb.getABox(). This is used for incremental reasoning under kb.getABox() deletions.
	 * 
	 * @param ATermAppl
	 *            assertion The deleted assertion
	 */
	private void restoreDependencies() {
	
		// iterate over all removed assertions
		for( ATermAppl next : kb.getDeletedAssertions() ) {
			// get the dependency entry
			DependencyEntry entry = kb.getDependencyIndex().getDependencies( next );
	
			if( entry != null ) {
				if( DependencyIndex.log.isLoggable( Level.FINE ) )
					DependencyIndex.log.fine( "Restoring dependencies for " + next );
	
				// restore the entry
				restoreDependency( next, entry );
			}
	
			// remove the entry in the index for this assertion
			kb.getDependencyIndex().removeDependencies( next );
		}
	
	}

	/**
	 * Perform the actual rollback of a depenedency entry
	 * 
	 * @param assertion
	 * @param entry
	 */
	private void restoreDependency(ATermAppl assertion, DependencyEntry entry) {
	
		if( DependencyIndex.log.isLoggable( Level.FINE ) )
			DependencyIndex.log.fine( "  Restoring Edge Dependencies:" );
		for( Edge next : entry.getEdges() ) {
			restoreEdge( assertion, next );
		}
	
		if( DependencyIndex.log.isLoggable( Level.FINE ) )
			DependencyIndex.log.fine( "  Restoring Type Dependencies:" );
		for( TypeDependency next : entry.getTypes() ) {
			restoreType( assertion, next );
		}
	
		if( DependencyIndex.log.isLoggable( Level.FINE ) )
			DependencyIndex.log.fine( "  Restoring Merge Dependencies: " + entry.getMerges() );
		for( MergeDependency next : entry.getMerges() ) {
			restoreMerge( assertion, next );
		}
	
		if( DependencyIndex.log.isLoggable( Level.FINE ) )
			DependencyIndex.log.fine( "  Restoring Branch Add Dependencies: "
					+ entry.getBranchAdds() );
		for( BranchAddDependency next : entry.getBranchAdds() ) {
			restoreBranchAdd( assertion, next );
		}
	
		if( DependencyIndex.log.isLoggable( Level.FINE ) )
			DependencyIndex.log.fine( "  Restoring Branch Remove DS Dependencies: "
					+ entry.getBranchAdds() );
		for( CloseBranchDependency next : entry.getCloseBranches() ) {
			restoreCloseBranch( assertion, next );
		}
	
		if( DependencyIndex.log.isLoggable( Level.FINE ) )
			DependencyIndex.log.fine( "  Restoring clash dependency: " + entry.getClash() );
		if( entry.getClash() != null ) {
			restoreClash( assertion, entry.getClash() );
		}
	
	}

	/**
	 * Restore an edge - i.e., remove it
	 * 
	 * @param assertion
	 * @param edge
	 */
	private void restoreEdge(ATermAppl assertion, Edge theEdge) {
		if( DependencyIndex.log.isLoggable( Level.FINE ) )
			DependencyIndex.log.fine( "    Removing edge? " + theEdge );
	
		// the edge could have previously been removed so return
		if( theEdge == null )
			return;
	
		// get the object
		Individual subj = kb.getABox().getIndividual( theEdge.getFrom().getName() );
		Node obj = kb.getABox().getNode( theEdge.getTo().getName() );
		Role role = kb.getRole( theEdge.getRole().getName() );
	
		// loop over all edges for the subject
		EdgeList edges = subj.getEdgesTo( obj, role );
		for( int i = 0; i < edges.size(); i++ ) {
			Edge edge = edges.edgeAt( i );
			if( edge.getRole().equals( role ) ) {
				// get dependency set for the edge
				DependencySet ds = edge.getDepends();
	
				// clean it
				ds.removeExplain( assertion );
	
				// remove if the dependency set is empty
				if( ds.getExplain().isEmpty() ) {
					final IncrementalChangeTracker tracker = kb.getABox().getIncrementalChangeTracker();
					// need to check if the
	
					subj.removeEdge( edge );
					obj.removeInEdge( edge );
	
					// update the removed set of edges
					tracker.addDeletedEdge( edge );
	
					// add to updated individuals
					tracker.addUpdatedIndividual( subj );
	
					// TODO: Do we need to add literals?
					if( obj instanceof Individual )
						tracker.addUpdatedIndividual( (Individual) obj );
	
					if( DependencyIndex.log.isLoggable( Level.FINE ) )
						DependencyIndex.log.fine( "           Actually removed edge!" );
				}
				break;
			}
		}
	}

	/**
	 * Restore a merge dependency
	 * 
	 * @param assertion
	 * @param merge
	 */
	private void restoreMerge(ATermAppl assertion, MergeDependency merge) {
	
		if( DependencyIndex.log.isLoggable( Level.FINE ) )
			DependencyIndex.log.fine( "    Removing merge? " + merge.getInd() + " merged to "
					+ merge.getmergedIntoInd() );
	
		// get merge dependency
		DependencySet ds = kb.getABox().getNode( merge.getInd() ).getMergeDependency( false );
	
		// remove the dependency
		ds.removeExplain( assertion );
	
		// undo merge if empty
		if( ds.getExplain().isEmpty() ) {
			if( DependencyIndex.log.isLoggable( Level.FINE ) )
				DependencyIndex.log.fine( "           Actually removing merge!" );
	
			// get nodes
			Node ind = kb.getABox().getNode( merge.getInd() );
			Node mergedToInd = kb.getABox().getNode( merge.getmergedIntoInd() );
	
			// check that they are actually the same - else throw error
			if( !ind.isSame( mergedToInd ) )
				throw new InternalReasonerException( " Restore merge error: " + ind
						+ " not same as " + mergedToInd );
	
			if( !ind.isPruned() )
				throw new InternalReasonerException( " Restore merge error: " + ind + " not pruned" );
	
			// unprune to prune branch
			ind.unprune( ind.getPruned().getBranch() );
	
			// undo set same
			ind.undoSetSame();
	
			// add to updated
			// Note that ind.unprune may add edges, however we do not need to
			// add them to the updated individuals as
			// they will be added when the edge is removed from the node which
			// this individual was merged to
			// add to updated
			final IncrementalChangeTracker tracker = kb.getABox().getIncrementalChangeTracker();
	
			// because this node was pruned, we must guarantee that all of
			// its lables have been fired
			tracker.addUnprunedNode( ind );
	
			if( ind instanceof Individual ) {
				tracker.addUpdatedIndividual( (Individual) ind );
			}
	
			if( mergedToInd instanceof Individual ) {
				tracker.addUpdatedIndividual( (Individual) mergedToInd );
			}
		}
	}

	/**
	 * Restore a type dependency
	 * 
	 * @param assertion
	 * @param type
	 */
	private void restoreType(ATermAppl assertion, TypeDependency type) {
	
		final Node node = kb.getABox().getNode( type.getInd() );
		final ATermAppl desc = type.getType();
	
		if( DependencyIndex.log.isLoggable( Level.FINE ) ) {
			if( node instanceof Individual )
				DependencyIndex.log.fine( "    Removing type? " + desc + " from "
						+ ((Individual) node).debugString() );
			else
				DependencyIndex.log.fine( "    Removing type? " + desc + " from " + node );
		}
	
		// get the dependency set - Note: we must normalize the concept
		DependencySet ds = node.getDepends( ATermUtils.normalize( desc ) );
	
		// return if null - this can happen as currently I have dupilicates in
		// the index
		if( ds == null || desc == ATermUtils.TOP )
			return;
	
		// clean it
		ds.removeExplain( assertion );
	
		// remove if the explanation set is empty
		if( ds.getExplain().isEmpty() ) {
			final IncrementalChangeTracker tracker = kb.getABox().getIncrementalChangeTracker();
	
			kb.getABox().removeType( node.getName(), desc );
	
			// update the set of removed types
			tracker.addDeletedType( node, type.getType() );
	
			// add to updated individuals
			if( node instanceof Individual ) {
				Individual ind = (Individual) node;
				tracker.addUpdatedIndividual( ind );
	
				// also need to add all edge object to updated individuals -
				// this is needed to fire allValues/domain/range rules etc.
				for( Edge e : ind.getInEdges() ) {
					tracker.addUpdatedIndividual( e.getFrom() );
				}
				for( Edge e : ind.getOutEdges() ) {
					if( e.getTo() instanceof Individual )
						tracker.addUpdatedIndividual( (Individual) e.getTo() );
				}
			}
	
			if( DependencyIndex.log.isLoggable( Level.FINE ) )
				DependencyIndex.log.fine( "           Actually removed type!" );
		}
	}

}
