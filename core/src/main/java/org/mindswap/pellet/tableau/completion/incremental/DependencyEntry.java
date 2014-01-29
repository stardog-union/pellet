// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.incremental;

import java.util.HashSet;
import java.util.Set;

import org.mindswap.pellet.DefaultEdge;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.tableau.branch.Branch;

import aterm.ATermAppl;


/**
 * Structure for containing all dependencies for a given assertion. This is the object stored
 * in the dependency index 
 * 
 * @author Christian Halaschek-Wiener
 *
 */
public class DependencyEntry {

	/**
	 * The set of node lables which are dependent
	 */
	private Set<TypeDependency> types;

	
	/**
	 * The set of merges which are dependent
	 */
	private Set<MergeDependency> merges;
	
	/**
	 * The set of edge which are dependent
	 */
	private Set<Edge> edges;


	/**
	 * The set of branches which are dependent
	 */
	private Set<BranchAddDependency> branchAdds;
	
	
	/**
	 * The set of branch remove ds' which are dependent
	 */
	private Set<CloseBranchDependency> branchCloses;
	
	
	/**
	 * Clash dependency
	 */
	private ClashDependency clash;

	
	/**
	 * Default constructor
	 *
	 */
	public DependencyEntry(){
		types = new HashSet<TypeDependency>();
		edges = new HashSet<Edge>();
		merges = new HashSet<MergeDependency>();
		branchAdds = new HashSet<BranchAddDependency>();
		branchCloses = new HashSet<CloseBranchDependency>();
		clash = null;
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	public DependencyEntry copy(){
		DependencyEntry newEntry = new DependencyEntry();
		
		
		//TODO:may need to perform a deep copy here
		newEntry.types = new HashSet<TypeDependency>(this.types);

		//TODO:may need to perform a deep copy here
		newEntry.merges = new HashSet<MergeDependency>(this.merges);
		
		
		//copy edge depenedencies
		for (Edge next : edges) {
			
			//create new edge
			Edge newEdge = new DefaultEdge(next.getRole(), next.getFrom(), next.getTo(), next.getDepends()); 
			
			//add to edge list
			newEntry.edges.add(newEdge);
		}
		
		//TODO:may need to perform a deep copy here
		newEntry.branchAdds = new HashSet<BranchAddDependency>(this.branchAdds);

		//TODO:may need to perform a deep copy here
		newEntry.branchCloses = new HashSet<CloseBranchDependency>(this.branchCloses);
		
		
		//TODO:may need to perform a deep copy here
		newEntry.clash = this.clash;

		
		return newEntry;
	}
	
	
	/**
	 * Add a type dependency
	 * 
	 * @param ind
	 * @param type
	 */
	protected void addTypeDependency(ATermAppl ind, ATermAppl type){
		types.add(new TypeDependency(ind, type));
	}
	
	/**
	 * Add a edge dependency
	 * 
	 * @param edge
	 */
	protected void addEdgeDependency(Edge edge){
		edges.add(edge);
	}
	
	
	/**
	 * Add a edge dependency
	 * 
	 * @param ind
	 * @param mergedTo
	 */
	protected void addMergeDependency(ATermAppl ind, ATermAppl mergedTo){
		merges.add(new MergeDependency(ind, mergedTo));
	}
	
	/**
	 * Add a branch add dependency
	 * 
	 * @param branchId
	 * @param branch
	 */
	protected BranchDependency addBranchAddDependency(ATermAppl assertion, int branchId, Branch branch){
		BranchAddDependency b = new BranchAddDependency(assertion, branchId, branch); 
		
		branchAdds.add( b );
		return b;
	}
	
	
	/**
	 * Add a branch remove ds dependency
	 * 
	 * @param branchId
	 * @param branch
	 */
	protected BranchDependency addCloseBranchDependency(ATermAppl assertion, Branch theBranch){
		CloseBranchDependency b = new CloseBranchDependency(assertion, theBranch.getTryNext(), theBranch); 
		
		if(branchCloses.contains(b))
			branchCloses.remove(b);
		
		branchCloses.add( b );
		return b;
	}

	
	
	/**
	 * Helper method to print all dependencies
	 * TODO: this print is not complete
	 */
	public void print(){
		System.out.println( "  Edge Dependencies:" );
		for( Edge e : edges ) {
			System.out.println( "    " + e.toString() );
		}

		System.out.println( "  Type Dependencies:" );
		for( TypeDependency t : types ) {
			System.out.println( "    " + t.toString() );
		}

		System.out.println( "  Merge Dependencies:" );
		for( MergeDependency m : merges ) {
			System.out.println( "    " + m.toString() );
		}
	}


	/**
	 * Get edges
	 * @return
	 */
	public Set<Edge> getEdges() {
		return edges;
	}


	/**
	 * Get merges
	 * @return
	 */
	public Set<MergeDependency> getMerges() {
		return merges;
	}


	/**
	 * Get types
	 * @return
	 */
	public Set<TypeDependency> getTypes() {
		return types;
	}


	/**
	 * Get branches
	 * @return
	 */
	public Set<BranchAddDependency> getBranchAdds() {
		return branchAdds;
	}



	/**
	 * Get the close branches for this entry
	 * 
	 * @return
	 */
	public Set<CloseBranchDependency> getCloseBranches() {
		return branchCloses;
	}


	/**
	 * Get clash dependency
	 * @return
	 */
	public ClashDependency getClash() {
		return clash;
	}



	/**
	 * Set clash dependency
	 * @param clash
	 */
	protected void setClash(ClashDependency clash) {
		this.clash = clash;
	}
}
