// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.incremental;

import aterm.ATermAppl;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.katk.tools.Log;
import org.mindswap.pellet.Clash;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.tableau.branch.Branch;
import org.mindswap.pellet.tableau.branch.DisjunctionBranch;

/**
 * This is the _index structure for maintaining the _dependencies between structures in an ABox and the syntactic assertions which caused them to be created.
 * This is used for incremental deletions.
 *
 * @author Christian Halaschek-Wiener
 */
public class DependencyIndex
{
	public final static Logger log = Log.getLogger(DependencyIndex.class);

	/**
	 * Map from assertions (ATermAppl) to Dependency entries
	 */
	private final Map<ATermAppl, DependencyEntry> _dependencies = new ConcurrentHashMap<>();

	/**
	 * Branch dependency _index
	 */
	private final Map<Branch, Set<BranchDependency>> _branchIndex = new ConcurrentHashMap<>();

	/**
	 * Clash dependency - used for cleanup
	 */
	private final Set<ClashDependency> _clashIndex = Collections.newSetFromMap(new ConcurrentHashMap<>());

	/**
	 * KB object
	 */
	private final KnowledgeBase _kb;

	/**
	 * Default constructor
	 */
	public DependencyIndex(final KnowledgeBase kb)
	{
		this._kb = kb;
	}

	/**
	 * Copy constructor
	 */
	public DependencyIndex(final KnowledgeBase kb, final DependencyIndex oldIndex)
	{
		this(kb);

		//iterate over old _dependencies and copy
		for (final ATermAppl next : oldIndex.getDependencies().keySet())
		{
			//duplication entry
			final DependencyEntry entry = oldIndex.getDependencies(next).copy();

			//add
			_dependencies.put(next, entry);
		}

	}

	/**
	 * @param assertion
	 * @return
	 */
	public DependencyEntry getDependencies(final ATermAppl assertion)
	{
		return _dependencies.get(assertion);
	}

	/**
	 * @return
	 */
	protected Map<ATermAppl, DependencyEntry> getDependencies()
	{
		return _dependencies;
	}

	/**
	 * Add a new type dependency
	 * 
	 * @param ind
	 * @param type
	 * @param ds
	 */
	public void addTypeDependency(final ATermAppl ind, final ATermAppl type, final DependencySet ds)
	{
		//		if(_log.isLoggable( Level.FINE ))
		//			_log.fine("DependencyIndex- Calling add type dependency");

		//loop over ds
		for (final ATermAppl nextAtom : ds.getExplain())
			//check if this assertion exists
			if (_kb.getSyntacticAssertions().contains(nextAtom))
			{
				//if this entry does not exist then create it
				if (!_dependencies.containsKey(nextAtom))
					_dependencies.put(nextAtom, new DependencyEntry());

				//					if(_log.isLoggable( Level.FINE ))
				//						_log.fine("DependencyIndex- Adding type dependency: Axiom [" +nextAtom + "]   ,  Ind [" + ind + "]   ,  Type["  + type + "]");

				//add the dependency
				_dependencies.get(nextAtom).addTypeDependency(ind, type);
			}
	}

	/**
	 * Add a new merge dependency
	 * 
	 * @param ind
	 * @param type
	 * @param ds
	 */
	public void addMergeDependency(final ATermAppl ind, final ATermAppl mergedTo, final DependencySet ds)
	{
		//		if(_log.isLoggable( Level.FINE ))
		//			_log.fine("DependencyIndex- Calling add merge dependency");

		//loop over ds
		for (final ATermAppl nextAtom : ds.getExplain())
			//check if this assertion exists
			if (_kb.getSyntacticAssertions().contains(nextAtom))
			{
				//if this entry does not exist then create it
				if (!_dependencies.containsKey(nextAtom))
					_dependencies.put(nextAtom, new DependencyEntry());

				//					if(_log.isLoggable( Level.FINE ))
				//						_log.fine("DependencyIndex- Adding merge dependency: Axiom [" +nextAtom + "]   ,  Ind [" + ind + "]   ,  mergedToInd["  + mergedTo + "]");

				//add the dependency
				_dependencies.get(nextAtom).addMergeDependency(ind, mergedTo);
			}
	}

	/**
	 * Add a new edge dependency
	 * 
	 * @param edge
	 * @param ds
	 */
	public void addEdgeDependency(final Edge edge, final DependencySet ds)
	{
		//		if(_log.isLoggable( Level.FINE ))
		//			_log.fine("DependencyIndex- Calling add edge dependency");

		//loop over ds
		for (final ATermAppl nextAtom : ds.getExplain())
			//check if this assertion exists
			if (_kb.getSyntacticAssertions().contains(nextAtom))
			{
				//if this entry does not exist then create it
				if (!_dependencies.containsKey(nextAtom))
					_dependencies.put(nextAtom, new DependencyEntry());

				//					if(_log.isLoggable( Level.FINE ))
				//						_log.fine("  DependencyIndex- Adding edge dependency: Axiom [" +nextAtom + "]   ,  Edge [" + edge + "]");

				//add the dependency
				_dependencies.get(nextAtom).addEdgeDependency(edge);
			}
	}

	/**
	 * Add a new _branch dependency
	 * 
	 * @param ind
	 * @param type
	 * @param ds
	 */
	public void addBranchAddDependency(final Branch branch)
	{
		//loop over ds
		for (final ATermAppl nextAtom : branch.getTermDepends().getExplain())
			//check if this assertion exists
			if (_kb.getSyntacticAssertions().contains(nextAtom))
			{
				//if this entry does not exist then create it
				if (!_dependencies.containsKey(nextAtom))
					_dependencies.put(nextAtom, new DependencyEntry());

				if (log.isLoggable(Level.FINE))
					log.fine("DependencyIndex- Adding _branch add dependency for assertion: " + nextAtom + " -  Branch id [" + branch.getBranch() + "]   ,  Branch [" + branch + "]");

				//add the dependency
				final BranchDependency newDep = _dependencies.get(nextAtom).addBranchAddDependency(nextAtom, branch.getBranch(), branch);

				//add dependency to _index so that backjumping can be supported (ie, we need a fast way to remove the _branch _dependencies
				if (!_branchIndex.containsKey(branch))
				{
					final Set<BranchDependency> newS = new HashSet<>();
					newS.add(newDep);
					_branchIndex.put(branch, newS);
				}
				else
					_branchIndex.get(branch).add(newDep);
			}
	}

	/**
	 * Add a new _branch ds removal dependency
	 * 
	 * @param ind
	 * @param type
	 * @param ds
	 */
	public void addCloseBranchDependency(final Branch branch, final DependencySet ds)
	{
		//loop over ds
		for (final ATermAppl nextAtom : ds.getExplain())
			//check if this assertion exists
			if (_kb.getSyntacticAssertions().contains(nextAtom))
			{
				//if this entry does not exist then create it
				if (!_dependencies.containsKey(nextAtom))
					_dependencies.put(nextAtom, new DependencyEntry());

				ATermAppl label = null;
				if (branch instanceof DisjunctionBranch)
					label = ((DisjunctionBranch) branch).getDisjunct(branch.getTryNext());

				if (log.isLoggable(Level.FINE))
					log.fine("DependencyIndex- Adding _branch remove ds dependency for assertion: " + nextAtom + " -  Branch id [" + branch.getBranch() + "]   ,  Branch [" + branch + "]   on label [" + label + "]  ,    _tryNext [" + branch.getTryNext() + "]");

				//add the dependency
				final BranchDependency newDep = _dependencies.get(nextAtom).addCloseBranchDependency(nextAtom, branch);

				//add depedency to _index so that backjumping can be supported (ie, we need a fast way to remove the _branch _dependencies
				if (!_branchIndex.containsKey(branch))
				{
					final Set<BranchDependency> newS = new HashSet<>();
					newS.add(newDep);
					_branchIndex.put(branch, newS);
				}
				else
					_branchIndex.get(branch).add(newDep);
			}
	}

	/**
	 * Remove the _dependencies for a given assertion
	 * 
	 * @param assertion
	 */
	public void removeDependencies(final ATermAppl assertion)
	{
		_dependencies.remove(assertion);
	}

	/**
	 * Remove _branch _dependencies - this is needed due to backjumping!
	 * 
	 * @param b
	 */
	public void removeBranchDependencies(final Branch b)
	{
		final Set<BranchDependency> deps = _branchIndex.get(b);

		//TODO: why is this null? is this because of duplicate entries in the _index set?
		//This seems to creep up in WebOntTest-I5.8-Manifest004 and 5 among others...
		if (deps == null)
			return;

		//loop over depencies and remove them
		for (final BranchDependency next : deps)
		{
			if (log.isLoggable(Level.FINE))
				log.fine("DependencyIndex: RESTORE causing remove of _branch _index for assertion: " + next.getAssertion() + " _branch dep.: " + next);
			if (next instanceof BranchAddDependency)
				//remove the dependency
				_dependencies.get(next.getAssertion()).getBranchAdds().remove(next);
			else
			{
				//remove the dependency
				//((DependencyEntry)_dependencies.get(next.getAssertion())).getBranchRemoveDSs().remove(next);
			}

		}
	}

	/**
	 * Set clash _dependencies
	 */
	public void setClashDependencies(final Clash clash)
	{

		//first remove old entry using clashindex
		for (final ClashDependency next : _clashIndex)
			//remove the dependency
			if (_dependencies.containsKey(next.getAssertion()))
				_dependencies.get(next.getAssertion()).setClash(null);

		//clear the old _index
		_clashIndex.clear();

		if (clash == null)
			return;

		//loop over ds
		for (final ATermAppl nextAtom : clash.getDepends().getExplain())
			//check if this assertion exists
			if (_kb.getSyntacticAssertions().contains(nextAtom))
			{
				//if this entry does not exist then create it
				if (!_dependencies.containsKey(nextAtom))
					_dependencies.put(nextAtom, new DependencyEntry());

				if (log.isLoggable(Level.FINE))
					log.fine("  DependencyIndex- Adding clash dependency: Axiom [" + nextAtom + "]   ,  Clash [" + clash + "]");

				final ClashDependency newDep = new ClashDependency(nextAtom, clash);

				//set the dependency
				_dependencies.get(nextAtom).setClash(newDep);

				//update _index
				_clashIndex.add(newDep);
			}
	}
}
