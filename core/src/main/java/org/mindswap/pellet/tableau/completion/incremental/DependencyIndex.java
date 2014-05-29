// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.incremental;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindswap.pellet.Clash;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.tableau.branch.Branch;
import org.mindswap.pellet.tableau.branch.DisjunctionBranch;

import aterm.ATermAppl;


/**
 * 
 * This is the index structure for maintaining the dependencies between structures in an ABox and the syntactic assertions which caused them to be created. This is used
 * for incremental deletions.
 * 
 * @author Christian Halaschek-Wiener
 */
public class DependencyIndex {
	public final static Logger log = Logger.getLogger( DependencyIndex.class.getName() );
	
	
	/**
	 * Map from assertions (ATermAppl) to Dependency entries
	 */
	private Map<ATermAppl, DependencyEntry> dependencies;
	
	
	/**
	 * Branch dependency index
	 */
	private Map<Branch, Set<BranchDependency>> branchIndex;
	
	
	/**
	 * Clash dependency - used for cleanup
	 */
	private Set<ClashDependency> clashIndex;
	
	
	/**
	 * KB object
	 */
	private KnowledgeBase kb;
	
	
	/**
	 * Default constructor
	 *
	 */
	public DependencyIndex(KnowledgeBase kb){
		dependencies = new HashMap<ATermAppl, DependencyEntry>();
		branchIndex = new HashMap<Branch, Set<BranchDependency>>();
		clashIndex = new HashSet<ClashDependency>();
		this.kb = kb; 
	}
	
	
	/**
	 * Copy constructor
	 *
	 */
	public DependencyIndex(KnowledgeBase kb, DependencyIndex oldIndex){
		this(kb);
		
		//iterate over old dependencies and copy
		for(ATermAppl next : oldIndex.getDependencies().keySet()) {
			//duplication entry
			DependencyEntry entry = oldIndex.getDependencies(next).copy();
			
			//add
			dependencies.put(next, entry);
		}
		
	}
	
	
	/**
	 * 
	 * @param assertion
	 * @return
	 */
	public DependencyEntry getDependencies(ATermAppl assertion){
		return dependencies.get(assertion);
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	protected Map<ATermAppl, DependencyEntry> getDependencies(){
		return dependencies;
	}
	
	
	/**
	 * Add a new type dependency
	 * @param ind
	 * @param type
	 * @param ds
	 */
	public void addTypeDependency(ATermAppl ind, ATermAppl type, DependencySet ds){
//		if(log.isLoggable( Level.FINE ))
//			log.fine("DependencyIndex- Calling add type dependency");
		
		//loop over ds
		for(ATermAppl nextAtom : ds.getExplain()){
			
			//check if this assertion exists
			if(kb.getSyntacticAssertions().contains(nextAtom)){
				//if this entry does not exist then create it
				if(!dependencies.containsKey(nextAtom))
					dependencies.put(nextAtom, new DependencyEntry());
					
//					if(log.isLoggable( Level.FINE ))
//						log.fine("DependencyIndex- Adding type dependency: Axiom [" +nextAtom + "]   ,  Ind [" + ind + "]   ,  Type["  + type + "]");
				
				//add the dependency
				dependencies.get(nextAtom).addTypeDependency(ind, type);
			}
		}
	}
	
	
	
	
	/**
	 * Add a new merge dependency
	 * @param ind
	 * @param type
	 * @param ds
	 */
	public void addMergeDependency(ATermAppl ind, ATermAppl mergedTo, DependencySet ds){
//		if(log.isLoggable( Level.FINE ))
//			log.fine("DependencyIndex- Calling add merge dependency");

		//loop over ds
		for(ATermAppl nextAtom : ds.getExplain()){
			
			//check if this assertion exists
			if(kb.getSyntacticAssertions().contains(nextAtom)){
				//if this entry does not exist then create it
				if(!dependencies.containsKey(nextAtom))
					dependencies.put(nextAtom, new DependencyEntry());
					
//					if(log.isLoggable( Level.FINE ))
//						log.fine("DependencyIndex- Adding merge dependency: Axiom [" +nextAtom + "]   ,  Ind [" + ind + "]   ,  mergedToInd["  + mergedTo + "]");
				
				//add the dependency
				dependencies.get(nextAtom).addMergeDependency(ind, mergedTo);
			}			
		}
	}
	
	
	
	/**
	 * Add a new edge dependency
	 * @param edge
	 * @param ds
	 */
	public void addEdgeDependency(Edge edge, DependencySet ds){
//		if(log.isLoggable( Level.FINE ))
//			log.fine("DependencyIndex- Calling add edge dependency");

		//loop over ds
		for(ATermAppl nextAtom : ds.getExplain()){
			
			//check if this assertion exists
			if(kb.getSyntacticAssertions().contains(nextAtom)){
				//if this entry does not exist then create it
				if(!dependencies.containsKey(nextAtom))
					dependencies.put(nextAtom, new DependencyEntry());
					
//					if(log.isLoggable( Level.FINE ))
//						log.fine("  DependencyIndex- Adding edge dependency: Axiom [" +nextAtom + "]   ,  Edge [" + edge + "]");

				//add the dependency
				dependencies.get(nextAtom).addEdgeDependency(edge);
			}			
		}
	}
	
	
	
	/**
	 * Add a new branch dependency
	 * @param ind
	 * @param type
	 * @param ds
	 */
	public void addBranchAddDependency(Branch branch){
		//loop over ds
		for(ATermAppl nextAtom : branch.getTermDepends().getExplain()){
			
			//check if this assertion exists
			if(kb.getSyntacticAssertions().contains(nextAtom)){
				//if this entry does not exist then create it
				if(!dependencies.containsKey(nextAtom))
					dependencies.put(nextAtom, new DependencyEntry());
					
				if(log.isLoggable( Level.FINE ))
					log.fine("DependencyIndex- Adding branch add dependency for assertion: " + nextAtom + " -  Branch id [" +branch.getBranch() + "]   ,  Branch [" + branch + "]");
				
				
				//add the dependency
				BranchDependency newDep = dependencies.get(nextAtom).addBranchAddDependency(nextAtom, branch.getBranch(), branch);
				
				//add dependency to index so that backjumping can be supported (ie, we need a fast way to remove the branch dependencies
				if(!branchIndex.containsKey(branch)){
					Set<BranchDependency> newS = new HashSet<BranchDependency>();
					newS.add(newDep);
					branchIndex.put(branch, newS);
				}else{
					branchIndex.get(branch).add(newDep);
				}
			}			
		}
	}
	
	
	
	/**
	 * Add a new branch ds removal dependency
	 * @param ind
	 * @param type
	 * @param ds
	 */
	public void addCloseBranchDependency(Branch branch, DependencySet ds){
		//loop over ds
		for(ATermAppl nextAtom : ds.getExplain()){
			
			//check if this assertion exists
			if(kb.getSyntacticAssertions().contains(nextAtom)){
				//if this entry does not exist then create it
				if(!dependencies.containsKey(nextAtom))
					dependencies.put(nextAtom, new DependencyEntry());
				
				ATermAppl label = null;
				if(branch instanceof DisjunctionBranch)
					label = ((DisjunctionBranch)branch).getDisjunct(branch.getTryNext()); 
				
				if(log.isLoggable( Level.FINE ))
					log.fine("DependencyIndex- Adding branch remove ds dependency for assertion: " + nextAtom + " -  Branch id [" +branch.getBranch() + "]   ,  Branch [" + branch + "]   on label [" + label + "]  ,    tryNext [" + branch.getTryNext() +"]");

				//add the dependency
				BranchDependency newDep = dependencies.get(nextAtom).addCloseBranchDependency(nextAtom, branch);
				
				//add depedency to index so that backjumping can be supported (ie, we need a fast way to remove the branch dependencies
				if(!branchIndex.containsKey(branch)){
					Set<BranchDependency> newS = new HashSet<BranchDependency>();
					newS.add(newDep);
					branchIndex.put(branch, newS);
				}else{
					branchIndex.get(branch).add(newDep);
				}
			}
		}
	}
	
	
	/**
	 * Remove the dependencies for a given assertion
	 * @param assertion
	 */
	public void removeDependencies(ATermAppl assertion){
		dependencies.remove(assertion);
	}
	
	
	/**
	 * Remove branch dependencies - this is needed due to backjumping!
	 * @param b
	 */
	public void removeBranchDependencies(Branch b){
		Set<BranchDependency>deps = branchIndex.get(b);
		
		//TODO: why is this null? is this because of duplicate entries in the index set?
		//This seems to creep up in WebOntTest-I5.8-Manifest004 and 5 among others...
		if(deps == null)
			return;
		
		//loop over depencies and remove them
		for(BranchDependency next : deps){
			if(log.isLoggable( Level.FINE ))
				log.fine("DependencyIndex: RESTORE causing remove of branch index for assertion: " + next.getAssertion() + " branch dep.: " +next);
			if(next instanceof BranchAddDependency){
				//remove the dependency
				dependencies.get(next.getAssertion()).getBranchAdds().remove(next);
			}else{
				//remove the dependency
				//((DependencyEntry)dependencies.get(next.getAssertion())).getBranchRemoveDSs().remove(next);
			}
			
		}		
	}
	
	
	
	
	
	
	/**
	 * Set clash dependencies
	 */
	public void setClashDependencies(Clash clash){

		//first remove old entry using clashindex
		for(ClashDependency next : clashIndex){			
			//remove the dependency
			if(dependencies.containsKey(next.getAssertion()))
				dependencies.get(next.getAssertion()).setClash(null);			
		}
		
		//clear the old index
		clashIndex.clear();
		
		if(clash==null)
			return;
		
		//loop over ds
		for(ATermAppl nextAtom : clash.getDepends().getExplain()){
			
			//check if this assertion exists
			if(kb.getSyntacticAssertions().contains(nextAtom)){
				//if this entry does not exist then create it
				if(!dependencies.containsKey(nextAtom))
					dependencies.put(nextAtom, new DependencyEntry());
					
				if(log.isLoggable( Level.FINE ))
					log.fine("  DependencyIndex- Adding clash dependency: Axiom [" +nextAtom + "]   ,  Clash [" + clash + "]");

				ClashDependency newDep = new ClashDependency(nextAtom, clash);

				//set the dependency
				dependencies.get(nextAtom).setClash(newDep);
				
				//update index
				clashIndex.add(newDep);
			}		
		}
	}
}
