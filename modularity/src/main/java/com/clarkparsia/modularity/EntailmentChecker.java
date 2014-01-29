// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.utils.TaxonomyUtils;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

import com.clarkparsia.owlapiv3.OWL;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class EntailmentChecker extends OWLAxiomVisitorAdapter implements OWLAxiomVisitor {
	public static Logger	log			= Logger.getLogger( EntailmentChecker.class.getName() );
	

	private IncrementalClassifier reasoner;
	private Boolean isEntailed;

	public EntailmentChecker(IncrementalClassifier reasoner) {
		this.reasoner = reasoner;
	}
	
	public boolean isEntailed(Set<? extends OWLAxiom> axioms) {
		for( OWLAxiom axiom : axioms ) {
			if( !isEntailed( axiom ) )
				return false;
		}
		
		return true;
	}
	
	public boolean isEntailed(OWLAxiom axiom) {
		isEntailed = null;

		axiom.accept( this );

		if( isEntailed == null )
			throw new UnsupportedEntailmentTypeException( axiom );
			
		return isEntailed;
	}
	
	public void visit(OWLSubClassOfAxiom axiom) {
		OWLClassExpression subClass = axiom.getSubClass();
		OWLClassExpression superClass = axiom.getSuperClass();
		
		if( !reasoner.isClassified() || subClass.isAnonymous() || superClass.isAnonymous() )
			isEntailed = reasoner.getReasoner().isEntailed( axiom );
		else 
			isEntailed = reasoner.getTaxonomy().isSubNodeOf( (OWLClass) subClass, (OWLClass) superClass ).isTrue();
	}
	
	
	public void visit(OWLEquivalentClassesAxiom axiom) {
		isEntailed = true;

		Iterator<OWLClassExpression> i = axiom.getClassExpressions().iterator();
		if( i.hasNext() ) {
			OWLClassExpression first = i.next();

			while( i.hasNext() && isEntailed ) {
				OWLClassExpression next = i.next();

				if( !reasoner.isClassified() || first.isAnonymous() || next.isAnonymous() )
					isEntailed = reasoner.getReasoner().isEntailed( OWL.equivalentClasses( first, next ) );
				else 
					isEntailed = reasoner.getTaxonomy().isEquivalent( (OWLClass) first, (OWLClass) next ).isTrue();
			}
		}		
	}

    public void visit(OWLSameIndividualAxiom axiom) {
    	if ( reasoner.isRealized() ) {
    		// the code uses the assumption that if any of the individuals listed have differing direct types
    		// then they cannot be the same; however, if they have the same types, they still have to
    		// be checked by the underlying reasoner
    		boolean sameTypes = true;
    		Taxonomy<OWLClass> taxonomy = reasoner.getTaxonomy();
    		
    		Iterator<OWLIndividual> i = axiom.getIndividuals().iterator();
    		
    		if( i.hasNext() ) {
    			OWLIndividual first = i.next();
    			Set<OWLClass> firstTypes = flatten( TaxonomyUtils.getTypes( taxonomy, first, true ) );
    			
    			while( i.hasNext() && sameTypes ) {
    				OWLIndividual next = i.next();
    				Set<OWLClass> nextTypes = flatten( TaxonomyUtils.getTypes( taxonomy, next, true ) );
    				
    				sameTypes = firstTypes.equals(nextTypes);
    			}
    			
    			if( sameTypes ) {
    				isEntailed = reasoner.getReasoner().isEntailed( axiom );
    			}
    			else {
    				isEntailed = false;
    			}
    		}
    	}
    	else {
    		isEntailed = reasoner.getReasoner().isEntailed( axiom );
    	}
    }
	
    public void visit(OWLDisjointClassesAxiom axiom) {
    	if ( reasoner.isClassified() && !containsAnonymousClasses( axiom.getClassExpressions() ) ) {
    		
    		OWLClass[] classes = new OWLClass[ axiom.getClassExpressions().size() ];
    		Iterator<OWLClassExpression> iter = axiom.getClassExpressions().iterator();
    		
    		for( int i = 0; i < classes.length; i++ ) {
    			classes[i] = iter.next().asOWLClass();
    		}
    		
    		if( possiblyDisjoint( classes ) ) {
    			// no data detected that would disqualify the axiom -- it has to be checked by the 
    			// underlying reasoner
    			isEntailed = reasoner.getReasoner().isEntailed( axiom );
    		} 
    		else {
    			isEntailed = false;
    		}
    	}
    	else {
    		isEntailed = reasoner.getReasoner().isEntailed( axiom );
    	}
    }
    
    /**
     * Performs checks for the given array whether the classes 
     * can be pair-wise disjoint. (In other words, it tries to find whether
     * there is information that proves that there is a pair that cannot be disjoint.)
     * 
     * @param classes an array of classes to be checked
     * @return true if the classes may be disjoint, false if information was found that 
     * prevents the disjointness
     */

    private boolean possiblyDisjoint(OWLClass[] classes) {
    	for (int i = 0; i < classes.length - 1; i++) {
    		for( int j = i + 1; j < classes.length; j++ ) {
    			if( !possiblyDisjoint( classes[i], classes[j] ) ) {
    				return false;
    			}
    		}
    	}
    	
    	return true;
    }
    
    /**
     * Tests whether two classes can be possibly disjoint; i.e., there are no disqualifying conditions
     * for them to be disjoint. The disqualifying conditions are: the classes are listed as equivalent to each other, or
     * one class is listed as a superclass of the other.
     * 
     * @param first the first class in the pair
     * @param next the next class in the pair
     * @return if the classes may be disjoint, false if the classes cannot be disjoint
     */
    private boolean possiblyDisjoint(OWLClass first, OWLClass next) {
    	Taxonomy<OWLClass> taxonomy = reasoner.getTaxonomy();
    	
    	if( taxonomy.getAllEquivalents( first ).contains( next ) ) {
    		return false;
    	}
    	
    	// getting supers should be typically faster than getting subs
    	if( taxonomy.getFlattenedSupers( first, false ).contains( next ) ) {
    		return false;
    	}
    	
    	if( taxonomy.getFlattenedSupers( next, false ).contains( first ) ) {
    		return false;
    	}
    	
    	return true;
    }
    
    /**
     * Checks whether the collection contains any anonymous classes (i.e., elements that cannot
     * be converted to OWLClass).
     * 
     * @param classExpressions the list of class expressions to be checked
     * @return true if the collection contains at least one anonymous class
     */
    private boolean containsAnonymousClasses(Collection<OWLClassExpression> classExpressions) {
    	for ( OWLClassExpression classExpression : classExpressions ) {
    		if( classExpression.isAnonymous() ) {
    			return true;
    		}
    	}
    	
    	return false;
    }

    public void visit(OWLClassAssertionAxiom axiom) {
    	if( reasoner.isRealized() && !axiom.getClassExpression().isAnonymous() ) {
    		isEntailed = contains(TaxonomyUtils.getTypes( reasoner.getTaxonomy(), axiom.getIndividual(), false ), 
    						axiom.getClassExpression().asOWLClass());
    	}
    	else {
    		isEntailed = reasoner.getReasoner().isEntailed( axiom );
    	}
    }
	
	public void visit(OWLDeclarationAxiom axiom) {
		isEntailed = reasoner.getReasoner().isEntailed( axiom );
	}

    public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }


    public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLDataPropertyDomainAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLObjectPropertyDomainAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLDifferentIndividualsAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLDisjointDataPropertiesAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLObjectPropertyRangeAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLObjectPropertyAssertionAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLSubObjectPropertyOfAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLDisjointUnionAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLDataPropertyRangeAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLFunctionalDataPropertyAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLDataPropertyAssertionAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }
    
    public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLSubDataPropertyOfAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLSubPropertyChainOfAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLInverseObjectPropertiesAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLHasKeyAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }

    public void visit(OWLDatatypeDefinitionAxiom axiom) {
    	isEntailed = reasoner.getReasoner().isEntailed( axiom );
    }
    
    public void visit(SWRLRule rule) {
    	isEntailed = reasoner.getReasoner().isEntailed( rule );
    }

    /**
     * Checks whether an element is contained in the sets of set 
     * 
     * @param <T>
     * @param setOfSets the set of sets
     * @param element the element
     * @return true if the element was found in the set of sets
     */
    private static <T> boolean contains(Set<Set<T>> setOfSets, T element) {
    	for (Set<T> set : setOfSets) {
    		if (set.contains(element)) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    /**
     * Flattens a set of sets to a single set.
     * 
     * @param <T>
     * @param setOfSets the set to be flattened
     * @return the flattened set
     */
    private static <T> Set<T> flatten(Set<Set<T>> setOfSets) {
    	Set<T> result = new HashSet<T>();
    	
    	for (Set<T> set : setOfSets) {
    		result.addAll(set);
    	}
    	
    	return result;
    }
}
