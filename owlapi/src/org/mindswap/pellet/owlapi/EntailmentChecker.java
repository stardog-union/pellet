// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.owlapi;

import java.util.ArrayList;
import java.util.Iterator;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owl.model.OWLAntiSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomAnnotationAxiom;
import org.semanticweb.owl.model.OWLAxiomVisitor;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLDataSubPropertyAxiom;
import org.semanticweb.owl.model.OWLDeclarationAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointUnionAxiom;
import org.semanticweb.owl.model.OWLEntityAnnotationAxiom;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owl.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLImportsDeclaration;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyChainSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLObjectPropertyInverse;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLObjectSubPropertyAxiom;
import org.semanticweb.owl.model.OWLOntologyAnnotationAxiom;
import org.semanticweb.owl.model.OWLPropertyExpression;
import org.semanticweb.owl.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLSameIndividualsAxiom;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owl.model.SWRLRule;

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
public class EntailmentChecker implements OWLAxiomVisitor {
	public static Logger	log			= Logger.getLogger( EntailmentChecker.class.getName() );

	private Reasoner	reasoner;
	private boolean		isEntailed	= false;

	public EntailmentChecker(Reasoner reasoner) {
		this.reasoner = reasoner;
	}

	public boolean isEntailed(OWLAxiom axiom) {
		isEntailed = false;

		axiom.accept( this );

		return isEntailed;
	}

	private OWLObjectProperty _getProperty(OWLObjectPropertyExpression pe) {
		while( pe.isAnonymous() )
			pe = ((OWLObjectPropertyInverse) pe).getInverse();

		return (OWLObjectProperty) pe;
	}

	private OWLPropertyExpression _normalize(OWLPropertyExpression pe) {
		OWLPropertyExpression inverse = null;
		boolean returnInv = false;

		while( pe.isAnonymous() ) {
			inverse = pe;
			pe = ((OWLObjectPropertyInverse) pe).getInverse();
			returnInv = !returnInv;
		}

		return returnInv
			? inverse
			: pe;
	}

	public void visit(OWLSubClassAxiom axiom) {
		isEntailed = reasoner.isSubClassOf( axiom.getSubClass(), axiom.getSuperClass() );
	}

	public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
		OWLDataFactory factory = reasoner.getManager().getOWLDataFactory();
		OWLDescription hasValue = factory.getOWLObjectValueRestriction( axiom.getProperty(), axiom.getObject() );
		OWLDescription doesNotHaveValue = factory.getOWLObjectComplementOf( hasValue );
		isEntailed = reasoner.hasType( axiom.getSubject(), doesNotHaveValue );
	}

	public void visit(OWLAntiSymmetricObjectPropertyAxiom axiom) {
		isEntailed = reasoner.isAntiSymmetric( (OWLObjectProperty) axiom.getProperty() );
	}

	public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
		isEntailed = reasoner.isReflexive( (OWLObjectProperty) axiom.getProperty() );
	}

	public void visit(OWLDisjointClassesAxiom axiom) {
		isEntailed = true;

		int n = axiom.getDescriptions().size();
		OWLDescription[] classes = axiom.getDescriptions().toArray( new OWLDescription[n] );
		for( int i = 0; i < n - 1; i++ ) {
			for( int j = i + 1; j < n; j++ ) {
				if( !reasoner.isDisjointWith( classes[i], classes[j] ) ) {
					isEntailed = false;
					return;
				}
			}
		}
	}

	public void visit(OWLDataPropertyDomainAxiom axiom) {
		isEntailed = reasoner.hasDomain( (OWLDataProperty) axiom.getProperty(), axiom.getDomain() );
	}

	public void visit(OWLImportsDeclaration axiom) {
		isEntailed = true;
		if( log.isLoggable( Level.FINE ) )
			log.fine( "Ignoring imports declaration " + axiom );
	}

	public void visit(OWLAxiomAnnotationAxiom axiom) {
		isEntailed = true;
		if( log.isLoggable( Level.FINE ) )
			log.fine( "Ignoring axiom annotation " + axiom );
	}

	public void visit(OWLObjectPropertyDomainAxiom axiom) {
		isEntailed = reasoner
				.hasDomain( (OWLObjectProperty) axiom.getProperty(), axiom.getDomain() );
	}

	public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
		isEntailed = true;

		Iterator<OWLObjectPropertyExpression> i = axiom.getProperties().iterator();
		if( i.hasNext() ) {
			OWLObjectProperty head = (OWLObjectProperty) i.next();

			while( i.hasNext() && isEntailed ) {
				OWLObjectProperty next = (OWLObjectProperty) i.next();

				isEntailed = reasoner.isEquivalentProperty( head, next );
			}
		}
	}

	public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		OWLDataFactory factory = reasoner.getManager().getOWLDataFactory();
		OWLDescription hasValue = factory.getOWLDataValueRestriction( axiom.getProperty(), axiom.getObject() );
		OWLDescription doesNotHaveValue = factory.getOWLObjectComplementOf( hasValue );
		isEntailed = reasoner.hasType( axiom.getSubject(), doesNotHaveValue );
	}

	public void visit(OWLDifferentIndividualsAxiom axiom) {
		isEntailed = true;

		ArrayList<OWLIndividual> list = new ArrayList<OWLIndividual>( axiom.getIndividuals() );
		for( int i = 0; i < list.size() - 1; i++ ) {
			OWLIndividual head = list.get( i );
			for( int j = i + 1; j < list.size(); j++ ) {
				OWLIndividual next = list.get( j );

				if( !reasoner.isDifferentFrom( head, next ) ) {
					isEntailed = false;
					return;
				}
			}
		}
	}

	public void visit(OWLDisjointDataPropertiesAxiom axiom) {
		isEntailed = true;

		int n = axiom.getProperties().size();
		OWLDataProperty[] properties = axiom.getProperties().toArray( new OWLDataProperty[n] );
		for( int i = 0; i < n - 1; i++ ) {
			for( int j = i + 1; j < n; j++ ) {
				if( !reasoner.isDisjointWith( properties[i], properties[j] ) ) {
					isEntailed = false;
					return;
				}
			}
		}
	}

	public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
		isEntailed = true;

		int n = axiom.getProperties().size();
		OWLObjectProperty[] properties = axiom.getProperties().toArray( new OWLObjectProperty[n] );
		for( int i = 0; i < n - 1; i++ ) {
			for( int j = i + 1; j < n; j++ ) {
				if( !reasoner.isDisjointWith( properties[i], properties[j] ) ) {
					isEntailed = false;
					return;
				}
			}
		}
	}

	public void visit(OWLObjectPropertyRangeAxiom axiom) {
		isEntailed = reasoner.hasRange( (OWLObjectProperty) axiom.getProperty(), axiom.getRange() );
	}

	public void visit(OWLObjectPropertyAssertionAxiom axiom) {
		isEntailed = reasoner.hasObjectPropertyRelationship( axiom.getSubject(), axiom
				.getProperty(), axiom.getObject() );
	}

	public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
		isEntailed = reasoner.isFunctional( (OWLObjectProperty) axiom.getProperty() );
	}

	public void visit(OWLObjectSubPropertyAxiom axiom) {
		isEntailed = reasoner.isSubPropertyOf( (OWLObjectProperty) axiom.getSubProperty(),
				(OWLObjectProperty) axiom.getSuperProperty() );
	}

	public void visit(OWLDisjointUnionAxiom axiom) {
		throw new UnsupportedOperationException();
	}

	public void visit(OWLDeclarationAxiom axiom) {
		isEntailed = true;
		if( log.isLoggable( Level.FINE ) )
			log.fine( "Ignoring declaration " + axiom );
	}

	public void visit(OWLEntityAnnotationAxiom axiom) {
		isEntailed = true;
		if( log.isLoggable( Level.FINE ) )
			log.fine( "Ignoring entity annotation " + axiom );
	}

	public void visit(OWLOntologyAnnotationAxiom axiom) {
		isEntailed = true;
		if( log.isLoggable( Level.FINE ) )
			log.fine( "Ignoring ontology annotation " + axiom );
	}

	public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
		isEntailed = reasoner.isSymmetric( (OWLObjectProperty) axiom.getProperty() );
	}

	public void visit(OWLDataPropertyRangeAxiom axiom) {
		isEntailed = reasoner.hasRange( (OWLDataProperty) axiom.getProperty(), axiom.getRange() );
	}

	public void visit(OWLFunctionalDataPropertyAxiom axiom) {
		isEntailed = reasoner.isFunctional( (OWLDataProperty) axiom.getProperty() );
	}

	public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
		isEntailed = true;
	
		Iterator<OWLDataPropertyExpression> i = axiom.getProperties().iterator();
		if( i.hasNext() ) {
			OWLDataProperty first = (OWLDataProperty) i.next();

			while( i.hasNext() && isEntailed ) {
				OWLDataProperty next = (OWLDataProperty) i.next();

				isEntailed = reasoner.isEquivalentProperty( first, next );
			}
		}
	}

	public void visit(OWLClassAssertionAxiom axiom) {
		OWLIndividual ind = axiom.getIndividual();
		OWLDescription c = axiom.getDescription();

		if( ind.isAnonymous() )
			isEntailed = reasoner.isSatisfiable( c );
		else
			isEntailed = reasoner.hasType( ind, c );
	}

	public void visit(OWLEquivalentClassesAxiom axiom) {
		isEntailed = true;

		Iterator<OWLDescription> i = axiom.getDescriptions().iterator();
		if( i.hasNext() ) {
			OWLDescription first = i.next();

			while( i.hasNext() && isEntailed ) {
				OWLDescription next = i.next();

				isEntailed = reasoner.isEquivalentClass( first, next );
			}
		}
	}

	public void visit(OWLDataPropertyAssertionAxiom axiom) {
		isEntailed = reasoner.hasDataPropertyRelationship( axiom.getSubject(), axiom.getProperty(),
				axiom.getObject() );
	}

	public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
		isEntailed = reasoner.isTransitive( (OWLObjectProperty) axiom.getProperty() );
	}

	public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		isEntailed = reasoner.isIrreflexive( (OWLObjectProperty) axiom.getProperty() );
	}

	public void visit(OWLDataSubPropertyAxiom axiom) {
		isEntailed = reasoner.isSubPropertyOf( (OWLDataProperty) axiom.getSubProperty(),
				(OWLDataProperty) axiom.getSuperProperty() );
	}

	public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		isEntailed = reasoner.isInverseFunctional( (OWLObjectProperty) axiom.getProperty() );
	}

	public void visit(OWLSameIndividualsAxiom axiom) {
		isEntailed = true;

		Iterator<OWLIndividual> i = axiom.getIndividuals().iterator();
		if( i.hasNext() ) {
			OWLIndividual first = i.next();

			while( i.hasNext() ) {
				OWLIndividual next = i.next();

				if( !reasoner.isSameAs( first, next ) ) {
					isEntailed = false;
					return;
				}
			}
		}
	}

	public void visit(OWLObjectPropertyChainSubPropertyAxiom axiom) {
		throw new UnsupportedOperationException();
	}

	public void visit(OWLInverseObjectPropertiesAxiom axiom) {
		isEntailed = reasoner.isInverseOf( (OWLObjectProperty) axiom.getFirstProperty(),
				(OWLObjectProperty) axiom.getSecondProperty() );
	}

	public void visit(SWRLRule rule) {
		throw new UnsupportedOperationException();
	}

}
