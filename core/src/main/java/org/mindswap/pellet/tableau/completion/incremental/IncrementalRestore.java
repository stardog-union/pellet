// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.incremental;

import aterm.ATermAppl;
import com.clarkparsia.pellet.IncrementalChangeTracker;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
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

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public class IncrementalRestore
{
	public static void restoreDependencies(final KnowledgeBase kb)
	{
		final IncrementalRestore restore = new IncrementalRestore(kb);
		restore.restoreDependencies();
	}

	private final KnowledgeBase _kb;

	private IncrementalRestore(final KnowledgeBase kb)
	{
		this._kb = kb;
	}

	/**
	 * Restore a _branch add dependency
	 *
	 * @param assertion
	 * @param _branch
	 */
	private void restoreBranchAdd(final ATermAppl assertion, final BranchAddDependency branch)
	{
		if (DependencyIndex.log.isLoggable(Level.FINE))
			DependencyIndex.log.fine("    Removing _branch add? " + branch.getBranch());

		// get merge dependency
		final DependencySet ds = branch.getBranch().getTermDepends();

		// remove the dependency
		ds.removeExplain(assertion);

		// undo merge if empty
		if (ds.getExplain().isEmpty())
		{
			if (DependencyIndex.log.isLoggable(Level.FINE))
				DependencyIndex.log.fine("           Actually removing _branch!");

			final Collection<ATermAppl> allEffects = PelletOptions.TRACK_BRANCH_EFFECTS ? _kb.getABox().getBranchEffectTracker().getAll(branch.getBranch().getBranch()) : _kb.getABox().getNodeNames();

			final List<IntSet> updatedList = new ArrayList<>();

			for (final ATermAppl a : allEffects)
			{

				// get the actual _node
				final Node node = _kb.getABox().getNode(a);

				// update type dependencies
				final Map<ATermAppl, DependencySet> types = node.getDepends();

				for (final Entry<ATermAppl, DependencySet> entry : types.entrySet())
				{
					// get ds for type
					DependencySet tDS = entry.getValue();

					// DependencySet.copy() does not create a new bitset object,
					// so we need to track which bitsets have been
					// updated, so we do not process the same bitset multiple
					// times
					boolean exit = false;
					for (int i = 0; i < updatedList.size(); i++)
						if (updatedList.get(i) == tDS.getDepends())
							exit = true;

					if (exit)
						continue;

					updatedList.add(tDS.getDepends());

					// update _branch if necessary
					if (tDS.getBranch() > branch.getBranch().getBranch())
						tDS = tDS.copy(tDS.getBranch() - 1);

					for (int i = branch.getBranch().getBranch(); i <= _kb.getABox().getBranches().size(); i++)
						// update dependency set
						if (tDS.contains(i))
						{
							tDS.remove(i);
							tDS.add(i - 1);
						}

					entry.setValue(tDS);
				}

				// update edge depdencies
				final EdgeList edges = node.getInEdges();
				for (final Edge edge : edges)
				{
					DependencySet tDS = edge.getDepends();

					// DependencySet.copy() does not create a new bitset object,
					// so we need to track which bitsets have been
					// updated, so we do not process the same bitset multiple
					// times
					boolean exit = false;
					for (int i = 0; i < updatedList.size(); i++)
						if (updatedList.get(i) == tDS.getDepends())
							exit = true;

					if (exit)
						continue;

					updatedList.add(tDS.getDepends());

					// update _branch if necessary
					if (tDS.getBranch() > branch.getBranch().getBranch())
						tDS = tDS.copy(edge.getDepends().getBranch() - 1);

					for (int i = branch.getBranch().getBranch(); i <= _kb.getABox().getBranches().size(); i++)
						// update dependency set
						if (tDS.contains(i))
						{
							tDS.remove(i);
							tDS.add(i - 1);
						}

					edge.setDepends(tDS);
				}

				// //TODO:The following code update outedges as well - after
				// testing is seems that this is un-necessary
				// if(_node instanceof Individual){
				// Individual ind = (Individual)_node;
				//
				// //update edge depdencies
				// //update type dependencies
				// edges = ind.getInEdges();
				// for(Iterator eIt = edges.iterator(); eIt.hasNext();){
				// //get next type
				// Edge edge = (Edge)eIt.next();
				//
				// //update _branch if necessary
				// if(edge.getDepends().branch > _branch.getBranch().branch)
				// edge.getDepends().branch--;
				//
				// for(int i = _branch.getBranch().branch; i <=
				// _kb.getABox().getBranches().size(); i++){
				// //update dependency set
				// if(edge.getDepends().contains(i)){
				// edge.getDepends().remove(i);
				// edge.getDepends().add(i-1);
				// }
				// }
				// }
				// }
			}

			if (PelletOptions.TRACK_BRANCH_EFFECTS)
				_kb.getABox().getBranchEffectTracker().remove(branch.getBranch().getBranch() + 1);

			// !!!!!!!!!!!!!!!! Next update _kb.getABox() branches !!!!!!!!!!!!!!
			// remove the _branch from branches
			final List<Branch> branches = _kb.getABox().getBranches();

			// decrease _branch id for each _branch after the _branch we're
			// removing
			// also need to change the dependency set for each label
			for (int i = branch.getBranch().getBranch(); i < branches.size(); i++)
			{
				// cast for ease
				final Branch br = branches.get(i);

				DependencySet tDS = br.getTermDepends();

				// update the term depends in the _branch
				if (tDS.getBranch() > branch.getBranch().getBranch())
					tDS = tDS.copy(tDS.getBranch() - 1);

				for (int j = branch.getBranch().getBranch(); j < _kb.getABox().getBranches().size(); j++)
					if (tDS.contains(j))
					{
						tDS.remove(j);
						tDS.add(j - 1);
					}

				// also need to decrement the _branch number
				br.setBranch(br.getBranch() - 1);
				br.setTermDepends(tDS);
			}

			// remove the actual _branch
			branches.remove(branch.getBranch());

			// set the _branch counter
			_kb.getABox().setBranch(_kb.getABox().getBranch() - 1);
		}
	}

	/**
	 * Restore a clash dependency
	 *
	 * @param assertion
	 * @param clash
	 */
	private void restoreClash(final ATermAppl assertion, final ClashDependency clash)
	{

		if (DependencyIndex.log.isLoggable(Level.FINE))
			DependencyIndex.log.fine("    Restoring clash dependency clash: " + clash.getClash());

		// remove the dependency
		clash.getClash().getDepends().removeExplain(assertion);

		// undo clash if empty and is independent
		if (clash.getClash().getDepends().getExplain().isEmpty() && clash.getClash().getDepends().isIndependent())
		{
			if (DependencyIndex.log.isLoggable(Level.FINE))
				DependencyIndex.log.fine("           Actually removing clash!");

			_kb.getABox().setClash(null);
		}
	}

	/**
	 * Restore a disjunct, merge pairs, etc. of a _branch that has been closed due to a clash whose dependency set contains an assertion that has been deleted
	 *
	 * @param assertion
	 * @param _branch
	 */
	private void restoreCloseBranch(@SuppressWarnings("unused") final ATermAppl assertion, final CloseBranchDependency branch)
	{
		// only proceed if _tryNext is larger than 1!
		if (branch.getTheBranch().getTryNext() > -1)
		{
			if (DependencyIndex.log.isLoggable(Level.FINE))
				DependencyIndex.log.fine("    Undoing _branch remove - _branch " + branch.getBranch() + "  -  " + branch.getInd() + "   _tryNext: " + branch.getTryNext());

			// shift try next for _branch
			branch.getTheBranch().shiftTryNext(branch.getTryNext());
		}
	}

	/**
	 * Method to remove all stuctures dependent on an _kb.getABox() assertion from the _kb.getABox(). This is used for incremental reasoning under _kb.getABox()
	 * deletions.
	 *
	 * @param ATermAppl assertion The deleted assertion
	 */
	private void restoreDependencies()
	{

		// iterate over all removed assertions
		for (final ATermAppl next : _kb.getDeletedAssertions())
		{
			// get the dependency entry
			final DependencyEntry entry = _kb.getDependencyIndex().getDependencies(next);

			if (entry != null)
			{
				if (DependencyIndex.log.isLoggable(Level.FINE))
					DependencyIndex.log.fine("Restoring dependencies for " + next);

				// restore the entry
				restoreDependency(next, entry);
			}

			// remove the entry in the _index for this assertion
			_kb.getDependencyIndex().removeDependencies(next);
		}

	}

	/**
	 * Perform the actual rollback of a depenedency entry
	 *
	 * @param assertion
	 * @param entry
	 */
	private void restoreDependency(final ATermAppl assertion, final DependencyEntry entry)
	{

		if (DependencyIndex.log.isLoggable(Level.FINE))
			DependencyIndex.log.fine("  Restoring Edge Dependencies:");
		for (final Edge next : entry.getEdges())
			restoreEdge(assertion, next);

		if (DependencyIndex.log.isLoggable(Level.FINE))
			DependencyIndex.log.fine("  Restoring Type Dependencies:");
		for (final TypeDependency next : entry.getTypes())
			restoreType(assertion, next);

		if (DependencyIndex.log.isLoggable(Level.FINE))
			DependencyIndex.log.fine("  Restoring Merge Dependencies: " + entry.getMerges());
		for (final MergeDependency next : entry.getMerges())
			restoreMerge(assertion, next);

		if (DependencyIndex.log.isLoggable(Level.FINE))
			DependencyIndex.log.fine("  Restoring Branch Add Dependencies: " + entry.getBranchAdds());
		for (final BranchAddDependency next : entry.getBranchAdds())
			restoreBranchAdd(assertion, next);

		if (DependencyIndex.log.isLoggable(Level.FINE))
			DependencyIndex.log.fine("  Restoring Branch Remove DS Dependencies: " + entry.getBranchAdds());
		for (final CloseBranchDependency next : entry.getCloseBranches())
			restoreCloseBranch(assertion, next);

		if (DependencyIndex.log.isLoggable(Level.FINE))
			DependencyIndex.log.fine("  Restoring clash dependency: " + entry.getClash());
		if (entry.getClash() != null)
			restoreClash(assertion, entry.getClash());

	}

	/**
	 * Restore an edge - i.e., remove it
	 *
	 * @param assertion
	 * @param edge
	 */
	private void restoreEdge(final ATermAppl assertion, final Edge theEdge)
	{
		if (DependencyIndex.log.isLoggable(Level.FINE))
			DependencyIndex.log.fine("    Removing edge? " + theEdge);

		// the edge could have previously been removed so return
		if (theEdge == null)
			return;

		// get the object
		final Individual subj = _kb.getABox().getIndividual(theEdge.getFrom().getName());
		final Node obj = _kb.getABox().getNode(theEdge.getTo().getName());
		final Role role = _kb.getRole(theEdge.getRole().getName());

		// loop over all edges for the subject
		final EdgeList edges = subj.getEdgesTo(obj, role);
		for (int i = 0; i < edges.size(); i++)
		{
			final Edge edge = edges.edgeAt(i);
			if (edge.getRole().equals(role))
			{
				// get dependency set for the edge
				final DependencySet ds = edge.getDepends();

				// clean it
				ds.removeExplain(assertion);

				// remove if the dependency set is empty
				if (ds.getExplain().isEmpty())
				{
					final IncrementalChangeTracker tracker = _kb.getABox().getIncrementalChangeTracker();
					// need to check if the

					subj.removeEdge(edge);
					obj.removeInEdge(edge);

					// update the removed set of edges
					tracker.addDeletedEdge(edge);

					// add to updated individuals
					tracker.addUpdatedIndividual(subj);

					// TODO: Do we need to add literals?
					if (obj instanceof Individual)
						tracker.addUpdatedIndividual((Individual) obj);

					if (DependencyIndex.log.isLoggable(Level.FINE))
						DependencyIndex.log.fine("           Actually removed edge!");
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
	private void restoreMerge(final ATermAppl assertion, final MergeDependency merge)
	{

		if (DependencyIndex.log.isLoggable(Level.FINE))
			DependencyIndex.log.fine("    Removing merge? " + merge.getInd() + " merged to " + merge.getmergedIntoInd());

		// get merge dependency
		final DependencySet ds = _kb.getABox().getNode(merge.getInd()).getMergeDependency(false);

		// remove the dependency
		ds.removeExplain(assertion);

		// undo merge if empty
		if (ds.getExplain().isEmpty())
		{
			if (DependencyIndex.log.isLoggable(Level.FINE))
				DependencyIndex.log.fine("           Actually removing merge!");

			// get _nodes
			final Node ind = _kb.getABox().getNode(merge.getInd());
			final Node mergedToInd = _kb.getABox().getNode(merge.getmergedIntoInd());

			// check that they are actually the same - else throw error
			if (!ind.isSame(mergedToInd))
				throw new InternalReasonerException(" Restore merge error: " + ind + " not same as " + mergedToInd);

			if (!ind.isPruned())
				throw new InternalReasonerException(" Restore merge error: " + ind + " not pruned");

			// unprune to prune _branch
			ind.unprune(ind.getPruned().getBranch());

			// undo set same
			ind.undoSetSame();

			// add to updated
			// Note that ind.unprune may add edges, however we do not need to
			// add them to the updated individuals as
			// they will be added when the edge is removed from the _node which
			// this individual was merged to
			// add to updated
			final IncrementalChangeTracker tracker = _kb.getABox().getIncrementalChangeTracker();

			// because this _node was pruned, we must guarantee that all of
			// its lables have been fired
			tracker.addUnprunedNode(ind);

			if (ind instanceof Individual)
				tracker.addUpdatedIndividual((Individual) ind);

			if (mergedToInd instanceof Individual)
				tracker.addUpdatedIndividual((Individual) mergedToInd);
		}
	}

	/**
	 * Restore a type dependency
	 *
	 * @param assertion
	 * @param type
	 */
	private void restoreType(final ATermAppl assertion, final TypeDependency type)
	{

		final Node node = _kb.getABox().getNode(type.getInd());
		final ATermAppl desc = type.getType();

		if (DependencyIndex.log.isLoggable(Level.FINE))
			if (node instanceof Individual)
				DependencyIndex.log.fine("    Removing type? " + desc + " from " + ((Individual) node).debugString());
			else
				DependencyIndex.log.fine("    Removing type? " + desc + " from " + node);

		// get the dependency set - Note: we must normalize the concept
		final DependencySet ds = node.getDepends(ATermUtils.normalize(desc));

		// return if null - this can happen as currently I have dupilicates in
		// the _index
		if (ds == null || desc == ATermUtils.TOP)
			return;

		// clean it
		ds.removeExplain(assertion);

		// remove if the explanation set is empty
		if (ds.getExplain().isEmpty())
		{
			final IncrementalChangeTracker tracker = _kb.getABox().getIncrementalChangeTracker();

			_kb.getABox().removeType(node.getName(), desc);

			// update the set of removed types
			tracker.addDeletedType(node, type.getType());

			// add to updated individuals
			if (node instanceof Individual)
			{
				final Individual ind = (Individual) node;
				tracker.addUpdatedIndividual(ind);

				// also need to add all edge object to updated individuals -
				// this is needed to fire allValues/domain/range rules etc.
				for (final Edge e : ind.getInEdges())
					tracker.addUpdatedIndividual(e.getFrom());
				for (final Edge e : ind.getOutEdges())
					if (e.getTo() instanceof Individual)
						tracker.addUpdatedIndividual((Individual) e.getTo());
			}

			if (DependencyIndex.log.isLoggable(Level.FINE))
				DependencyIndex.log.fine("           Actually removed type!");
		}
	}

}
