// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.rule;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Edge;
import org.mindswap.pellet.EdgeList;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import com.clarkparsia.pellet.datatypes.exceptions.UnrecognizedDatatypeException;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class SomeValuesRule extends AbstractTableauRule {
	public SomeValuesRule(CompletionStrategy strategy) {
		super( strategy, NodeSelector.EXISTENTIAL, BlockingType.COMPLETE );
	}
	
    public void apply( Individual x ) {
        if( !x.canApply( Individual.SOME ) )
        	return;

        List<ATermAppl> types = x.getTypes( Node.SOME );
        int size = types.size();
        for( int j = x.applyNext[Node.SOME]; j < size; j++ ) {
            ATermAppl sv = types.get( j );

            applySomeValuesRule( x, sv );
            
            if( strategy.getABox().isClosed() || x.isPruned() )
                return;
        }
        x.applyNext[Individual.SOME] = size;
    }

    
    protected void applySomeValuesRule( Individual x, ATermAppl sv ) {
        // someValuesFrom is now in the form not(all(p. not(c)))
        ATermAppl a = (ATermAppl) sv.getArgument( 0 );
        ATermAppl s = (ATermAppl) a.getArgument( 0 );
        ATermAppl c = (ATermAppl) a.getArgument( 1 );

        DependencySet ds = x.getDepends( sv );

        
        if(!PelletOptions.MAINTAIN_COMPLETION_QUEUE && ds == null)
			return;
        
        c = ATermUtils.negate( c );
        
        // Special rule to optimize topObjectProperty
        if ( s.equals( ATermUtils.TOP_OBJECT_PROPERTY ) ) {
        	if ( ATermUtils.isNominal( c ) )
        		return;
        	
        	for ( Node node : strategy.getABox().getNodes() ) {
        		if ( node.isIndividual() && !node.isPruned() && node.hasType( c ) ) {
        			return;
        		}
        	}
        	
        	Individual y = strategy.createFreshIndividual( x, ds );
        	strategy.addType( y, c, ds );
        	return;
        }
        
        Role role = strategy.getABox().getRole( s );


        // Is there a r-neighbor that satisfies the someValuesFrom restriction
        boolean neighborFound = false;
        // Safety condition as defined in the SHOIQ algorithm.
        // An R-neighbor y of a node x is safe if
        // (i) x is blockable or if
        // (ii) x is a nominal node and y is not blocked.
        boolean neighborSafe = x.isBlockable();
        // y is going to be the node we create, and edge its connection to the
        // current node
        Node y = null;
        Edge edge = null;

        // edges contains all the edges going into of coming out from the node
        // And labeled with the role R
        EdgeList edges = x.getRNeighborEdges( role );
        // We examine all those edges one by one and check if the neighbor has
        // type C, in which case we set neighborFound to true
        for( Iterator<Edge> i = edges.iterator(); i.hasNext(); ) {
            edge = i.next();

            y = edge.getNeighbor( x );            
            
            if( PelletOptions.USE_COMPLETION_QUEUE && y.isPruned() ){
            		y = null;
            		continue;
            }            	 
            
            if( y.hasType( c ) ) {
            	neighborFound = neighborSafe || y.isLiteral() || !strategy.getBlocking().isBlocked( (Individual) y );
                if( neighborFound ) {
                    break;
                }
            }
        }

        // If we have found a R-neighbor with type C, continue, do nothing
        if( neighborFound )
            return;

        // If not, we have to create it
        // If the role is a datatype property...
        if( role.isDatatypeRole() ) {
            Literal literal = (Literal) y;
			if( ATermUtils.isNominal( c ) && !PelletOptions.USE_PSEUDO_NOMINALS ) {
				strategy.getABox().copyOnWrite();

				final ATermAppl input = (ATermAppl) c.getArgument( 0 );
				ATermAppl canonical;
				if( input.getArgument( ATermUtils.LIT_URI_INDEX ).equals( ATermUtils.NO_DATATYPE ) ) {
					canonical = input;
				}
				else {
					try {
						canonical = strategy.getABox().getDatatypeReasoner().getCanonicalRepresentation( input );
					} catch( InvalidLiteralException e ) {
						final String msg = "Invalid literal encountered in nominal when attempting to apply some values rule: "
								+ e.getMessage();
						throw new InternalReasonerException( msg, e );
					} catch( UnrecognizedDatatypeException e ) {
						final String msg = "Unrecognized datatype for literal encountered in nominal when attempting to apply some values rule: "
								+ e.getMessage();
						throw new InternalReasonerException( msg, e );
					}
				}
				literal = strategy.getABox().addLiteral( canonical );
			}
            else {
                if( !role.isFunctional() || literal == null ) {
                    literal = strategy.getABox().addLiteral( ds );
                }
                else {
                	ds = ds.union( role.getExplainFunctional(), strategy.getABox().doExplanation()  );
                	ds = ds.union( edge.getDepends(), strategy.getABox().doExplanation()  );
                }
                strategy.addType( literal, c, ds );
            }
            
            if( log.isLoggable( Level.FINE ) )
                log.fine( "SOME: " + x + " -> " + s + " -> " + literal + " : " + ATermUtils.toString( c ) + " - " + ds );
            
            strategy. addEdge( x, role, literal, ds );
        }
        // If it is an object property
        else {
            if( ATermUtils.isNominal( c ) && !PelletOptions.USE_PSEUDO_NOMINALS ) {
                strategy.getABox().copyOnWrite();

                ATermAppl value = (ATermAppl) c.getArgument( 0 );
                y = strategy.getABox().getIndividual( value );

                if( log.isLoggable( Level.FINE ) )
                    log.fine( "VAL : " + x + " -> " + ATermUtils.toString( s ) + " -> " + y + " - " + ds );

                if( y == null ) {
                    if( ATermUtils.isAnonNominal( value ) ) {
                        y = strategy.getABox().addIndividual( value, ds );
                    }
                    else if( ATermUtils.isLiteral( value ) )
                        throw new InternalReasonerException( "Object Property " + role
                            + " is used with a hasValue restriction "
                            + "where the value is a literal: " + ATermUtils.toString( value ) );
                    else
                        throw new InternalReasonerException( "Nominal " + c
                            + " is not found in the KB!" );
                }

                if( y.isMerged() ) {
                    ds = ds.union( y.getMergeDependency( true ), strategy.getABox().doExplanation() );

                    y = y.getSame();
                }

                strategy.addEdge( x, role, y, ds );
            }
            else {
                boolean useExistingNode = false;
                boolean useExistingRole = false;
                DependencySet maxCardDS = role.isFunctional()
					? role.getExplainFunctional()
					: x.hasMax1( role );
                if( maxCardDS != null ) {
                    ds = ds.union( maxCardDS, strategy.getABox().doExplanation() );

                    // if there is an r-neighbor and we can have at most one r then
                    // we should reuse that node and edge. there is no way that neighbor
                    // is not safe (a node is unsafe only if it is blockable and has
                    // a nominal successor which is not possible if there is a cardinality
                    // restriction on the property)
                    if( edge != null ) {
                        useExistingRole = useExistingNode = true;                       
                    }
                    else {
                        // this is the tricky part. we need some merges to happen
                        // under following conditions:
                        // 1) if r is functional and there is a p-neighbor where
                        // p is superproperty of r then we need to reuse that
                        // p neighbor for the some values restriction (no
                        // need to check subproperties because functionality of r
                        // precents having two or more successors for subproperties)
                        // 2) if r is not functional, i.e. max(r, 1) is in the types,
                        // then having a p neighbor (where p is subproperty of r)
                        // means we need to reuse that p-neighbor
                        // In either case if there are more than one such value we also
                        // need to merge them together
                        Set<Role> fs = role.isFunctional() ? role.getFunctionalSupers() : role
                            .getSubRoles();
                        
                        for( Iterator<Role> it = fs.iterator(); it.hasNext(); ) {
                            Role f = it.next();
                            edges = x.getRNeighborEdges( f );
                            if( !edges.isEmpty() ) {
                                if( useExistingNode ) {
                                	DependencySet fds = DependencySet.INDEPENDENT;
                                	if (PelletOptions.USE_TRACING) {
                                		if (role.isFunctional()) {
                                			fds = role.getExplainSuper(f.getName());
                                		} else {
                                			fds = role.getExplainSub(f.getName());
                                		}
                                	}
                                    Edge otherEdge = edges.edgeAt( 0 );
                                    Node otherNode = otherEdge.getNeighbor( x );
                                    DependencySet d = ds.union( edge.getDepends(), strategy.getABox().doExplanation() ).union(
                                        otherEdge.getDepends(), strategy.getABox().doExplanation() ).union(fds, strategy.getABox().doExplanation());
                                    strategy.mergeTo( y, otherNode, d );
                                }
                                else {
                                    useExistingNode = true;
                                    edge = edges.edgeAt( 0 );
                                    y = edge.getNeighbor( x );
                                }
                            }
                        }
                        if( y != null )
                            y = y.getSame();
                    }
                }

                if( useExistingNode ) {
                    ds = ds.union( edge.getDepends(), strategy.getABox().doExplanation() );
                }
                else {
                    y = strategy.createFreshIndividual( x, ds );
                }

                if( log.isLoggable( Level.FINE ) )
                    log.fine( "SOME: " + x + " -> " + role + " -> " + y + " : " + ATermUtils.toString( c ) 
                        + (useExistingNode ? "" : " (*)") + " - " + ds );

                strategy.addType( y, c, ds );

                if( !useExistingRole ) {
                	if (x.isBlockable() && y.isConceptRoot())
                		strategy.addEdge( (Individual) y, role.getInverse(), x, ds );
                	else
                		strategy.addEdge( x, role, y, ds );
                }
            }
        }   
    }
    
    
}
