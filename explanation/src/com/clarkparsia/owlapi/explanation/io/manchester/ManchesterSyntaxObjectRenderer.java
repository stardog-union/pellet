// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.owlapi.explanation.io.manchester;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLQuantifiedRestriction;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLUnaryPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLArgument;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDataPropertyAtom;
import org.semanticweb.owlapi.model.SWRLDataRangeAtom;
import org.semanticweb.owlapi.model.SWRLDifferentIndividualsAtom;
import org.semanticweb.owlapi.model.SWRLIndividualArgument;
import org.semanticweb.owlapi.model.SWRLLiteralArgument;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLSameIndividualAtom;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

public class ManchesterSyntaxObjectRenderer implements OWLObjectVisitor {
	private boolean			wrapLines	= true;
	private boolean			smartIndent	= true;

	protected BlockWriter	writer;

	/**
	 * @param writer
	 */
	public ManchesterSyntaxObjectRenderer(BlockWriter writer) {
		this.writer = writer;
	}

	public boolean isSmartIndent() {
		return smartIndent;
	}

	public boolean isWrapLines() {
		return wrapLines;
	}

	public void setSmartIndent(boolean smartIndent) {
		this.smartIndent = smartIndent;
	}

	public void setWrapLines(boolean wrapLines) {
		this.wrapLines = wrapLines;
	}

	/**
	 * Return the short form (local name) for a URI identifier
	 * 
	 * @param theIRI
	 *            the URI
	 * @return the local name part of the URI identifier
	 */
	protected String shortForm(IRI theIRI) {
		String fragment = theIRI.getFragment();
		if( fragment != null ) {
			return fragment;
		}
		String str = theIRI.toString();
		int lastSlashIndex = str.lastIndexOf( '/' );
		if( lastSlashIndex != -1 ) {
			return str.substring( lastSlashIndex + 1, str.length() );
		}
		return str;
	}

	public void visit(OWLAsymmetricObjectPropertyAxiom theAxiom) {
		writeUnaryPropertyAxiom( theAxiom, Keyword.ASYMMETRIC_PROPERTY );
	}

	public void visit(OWLClass theOWLClass) {
		write( theOWLClass.getIRI() );
	}

	public void visit(OWLClassAssertionAxiom theAxiom) {
		writeKeywordInfix( Keyword.TYPE, theAxiom.getIndividual(), theAxiom.getClassExpression() );
	}

	public void visit(OWLAnnotation theAnnotation) {
		write( Keyword.ANNOTATION );
		writeSpace();
		write( "(" );
		write( theAnnotation.getProperty() );		
		write( " " );
		write( theAnnotation.getValue() );
		write( ")" );
	}

	public void visit(OWLDataAllValuesFrom theDescription) {
		writeQuantifiedRestriction( theDescription, Keyword.ONLY );
	}

	public void visit(OWLDataComplementOf theDescription) {
		writeKeywordPrefix( Keyword.NOT, theDescription.getDataRange() );
	}

	public void visit(OWLDataExactCardinality theDescription) {
		writeCardinalityRestriction( theDescription, Keyword.EXACTLY );
	}

	public void visit(OWLDataMaxCardinality theDescription) {
		writeCardinalityRestriction( theDescription, Keyword.MAX );
	}

	public void visit(OWLDataMinCardinality theDescription) {
		writeCardinalityRestriction( theDescription, Keyword.MIN );
	}

	public void visit(OWLDataOneOf theDescription) {
		writeEnumeration( theDescription.getValues() );
	}

	public void visit(OWLDataProperty theProperty) {
		write( theProperty.getIRI() );
	}

	public void visit(OWLDataPropertyAssertionAxiom theAxiom) {
		write( theAxiom.getSubject() );
		writeSpace();
		write( theAxiom.getProperty() );
		writeSpace();
		write( theAxiom.getObject() );
	}

	public void visit(OWLDataPropertyDomainAxiom theAxiom) {
		writeKeywordInfix( Keyword.DOMAIN, theAxiom.getProperty(), theAxiom.getDomain() );
	}

	public void visit(OWLDataPropertyRangeAxiom theAxiom) {
		writeKeywordInfix( Keyword.RANGE, theAxiom.getProperty(), theAxiom.getRange() );
	}

	public void visit(OWLFacetRestriction theRestriction) {
		write( theRestriction.getFacet().getSymbolicForm() );
		writeSpace();
		write( theRestriction.getFacetValue() );
	}

	public void visit(OWLDatatypeRestriction theRestriction) {

		write( theRestriction.getDatatype() );
		write( "[" );
		boolean first = true;
		for( OWLFacetRestriction restriction : theRestriction.getFacetRestrictions() ) {
			if( first ) {
				first = false;
			}
			else {
				write( "," );
				writeSpace();
			}
			write( restriction );
		}
		write( "]" );

	}

	public void visit(OWLDataSomeValuesFrom theDescription) {
		writeQuantifiedRestriction( theDescription, Keyword.SOME );
	}

	public void visit(OWLSubDataPropertyOfAxiom theAxiom) {
		writeKeywordInfix( Keyword.SUB_PROPERTY_OF, theAxiom.getSubProperty(), theAxiom
				.getSuperProperty() );
	}

	public void visit(OWLDatatype node) {
		write( node.getIRI() );
	}

	public void visit(OWLDataHasValue theDescription) {
		writeRestriction( theDescription.getProperty(), Keyword.VALUE, theDescription.getValue() );
	}

	public void visit(OWLDeclarationAxiom theAxiom) {
		writeKeywordPrefix( Keyword.DECLARATION, theAxiom.getEntity() );
	}

	public void visit(OWLDifferentIndividualsAxiom theAxiom) {
		writeNaryAxiom( theAxiom.getIndividuals(), Keyword.DIFFERENT_INDIVIDUAL,
				Keyword.DIFFERENT_INDIVIDUALS );
	}

	public void visit(OWLDisjointClassesAxiom theAxiom) {
		writeNaryAxiom( theAxiom.getClassExpressions(), Keyword.DISJOINT_CLASS,
				Keyword.DISJOINT_CLASSES );
	}

	public void visit(OWLDisjointDataPropertiesAxiom theAxiom) {
		writeNaryAxiom( theAxiom.getProperties(), Keyword.DISJOINT_PROPERTY,
				Keyword.DISJOINT_PROPERTIES );
	}

	public void visit(OWLDisjointObjectPropertiesAxiom theAxiom) {
		writeNaryAxiom( theAxiom.getProperties(), Keyword.DISJOINT_PROPERTY,
				Keyword.DISJOINT_PROPERTIES );
	}

	public void visit(OWLDisjointUnionAxiom theAxiom) {
		write( theAxiom.getOWLClass() );
		writeSpace();
		write( Keyword.DISJOINT_UNION );
		writeSpace();
		writeNaryKeyword( Keyword.OR, theAxiom.getClassExpressions() );
	}

	public void visit(OWLEquivalentClassesAxiom theAxiom) {
		writeNaryAxiom( theAxiom.getClassExpressions(), Keyword.EQUIVALENT_TO,
				Keyword.EQUIVALENT_CLASSES );
	}

	public void visit(OWLEquivalentDataPropertiesAxiom theAxiom) {
		writeNaryAxiom( theAxiom.getProperties(), Keyword.EQUIVALENT_TO,
				Keyword.EQUIVALENT_PROPERTIES );
	}

	public void visit(OWLEquivalentObjectPropertiesAxiom theAxiom) {
		writeNaryAxiom( theAxiom.getProperties(), Keyword.EQUIVALENT_TO,
				Keyword.EQUIVALENT_PROPERTIES );
	}

	public void visit(OWLFunctionalDataPropertyAxiom theAxiom) {
		writeUnaryPropertyAxiom( theAxiom, Keyword.FUNCTIONAL );
	}

	public void visit(OWLFunctionalObjectPropertyAxiom theAxiom) {
		writeUnaryPropertyAxiom( theAxiom, Keyword.FUNCTIONAL );
	}

	public void visit(OWLAnonymousIndividual theIndividual) {
		write( theIndividual.getID().getID() );
	}
	
	public void visit(OWLNamedIndividual theIndividual) {
		write( theIndividual.getIRI() );
	}

	public void visit(OWLInverseFunctionalObjectPropertyAxiom theAxiom) {
		writeUnaryPropertyAxiom( theAxiom, Keyword.INVERSE_FUNCTIONAL );
	}

	public void visit(OWLInverseObjectPropertiesAxiom theAxiom) {
		writeKeywordInfix( Keyword.INVERSE_OF, theAxiom.getFirstProperty(), theAxiom
				.getSecondProperty() );
	}

	public void visit(OWLIrreflexiveObjectPropertyAxiom theAxiom) {
		writeUnaryPropertyAxiom( theAxiom, Keyword.IRREFLEXIVE );
	}

	public void visit(OWLNegativeDataPropertyAssertionAxiom theAxiom) {
		write( Keyword.NOT_RELATIONSHIP );
		writeSpace();
		write( "(" );
		write( theAxiom.getSubject() );
		writeSpace();
		write( theAxiom.getProperty() );
		writeSpace();
		write( theAxiom.getObject() );
		write( ")" );
	}

	public void visit(OWLNegativeObjectPropertyAssertionAxiom theAxiom) {
		write( Keyword.NOT_RELATIONSHIP );
		writeSpace();
		write( "(" );
		write( theAxiom.getSubject() );
		writeSpace();
		write( theAxiom.getProperty() );
		writeSpace();
		write( theAxiom.getObject() );
		write( ")" );
	}

	public void visit(OWLObjectAllValuesFrom theDescription) {
		writeQuantifiedRestriction( theDescription, Keyword.ONLY );
	}

	public void visit(OWLObjectComplementOf theDescription) {
		writeKeywordPrefix( Keyword.NOT, theDescription.getOperand() );
	}

	public void visit(OWLObjectExactCardinality theDescription) {
		writeCardinalityRestriction( theDescription, Keyword.EXACTLY );
	}

	public void visit(OWLObjectIntersectionOf theDescription) {
		writeNaryKeyword( Keyword.AND, theDescription.getOperands() );
	}

	public void visit(OWLObjectMaxCardinality theDescription) {
		writeCardinalityRestriction( theDescription, Keyword.MAX );
	}

	public void visit(OWLObjectMinCardinality theDescription) {
		writeCardinalityRestriction( theDescription, Keyword.MIN );
	}

	public void visit(OWLObjectOneOf theDescription) {
		writeEnumeration( theDescription.getIndividuals() );
	}

	public void visit(OWLObjectProperty theProperty) {
		write( theProperty.getIRI() );
	}

	public void visit(OWLObjectPropertyAssertionAxiom theAxiom) {
		write( theAxiom.getSubject() );
		writeSpace();
		write( theAxiom.getProperty() );
		writeSpace();
		write( theAxiom.getObject() );
	}

	public void visit(OWLSubPropertyChainOfAxiom theAxiom) {
		writeCollection( theAxiom.getPropertyChain(), " o", false );
		writeSpace();
		write( Keyword.SUB_PROPERTY_OF );
		writeSpace();
		write( theAxiom.getSuperProperty() );
	}

	public void visit(OWLObjectPropertyDomainAxiom theAxiom) {
		writeKeywordInfix( Keyword.DOMAIN, theAxiom.getProperty(), theAxiom.getDomain() );
	}

	public void visit(OWLObjectInverseOf theInverse) {
		writeKeywordPrefix( Keyword.INVERSE, theInverse.getInverse() );
	}

	public void visit(OWLObjectPropertyRangeAxiom theAxiom) {
		writeKeywordInfix( Keyword.RANGE, theAxiom.getProperty(), theAxiom.getRange() );
	}

	public void visit(OWLObjectHasSelf theRestriction) {
		writeRestriction( theRestriction.getProperty(), Keyword.SELF );
	}

	public void visit(OWLObjectSomeValuesFrom theDescription) {
		writeQuantifiedRestriction( theDescription, Keyword.SOME );
	}

	public void visit(OWLSubObjectPropertyOfAxiom theAxiom) {
		writeKeywordInfix( Keyword.SUB_PROPERTY_OF, theAxiom.getSubProperty(), theAxiom
				.getSuperProperty() );
	}

	public void visit(OWLObjectUnionOf theDescription) {
		writeNaryKeyword( Keyword.OR, theDescription.getOperands() );
	}

	public void visit(OWLObjectHasValue theDescription) {
		writeRestriction( theDescription.getProperty(), Keyword.VALUE, theDescription.getValue() );
	}

	public void visit(OWLOntology ontology) {
		write( ontology.getOntologyID().getOntologyIRI() );
	}

	public void visit(OWLReflexiveObjectPropertyAxiom theAxiom) {
		writeUnaryPropertyAxiom( theAxiom, Keyword.REFLEXIVE_PROPERTY );
	}

	public void visit(OWLSameIndividualAxiom theAxiom) {
		writeNaryAxiom( theAxiom.getIndividuals(), Keyword.SAME_INDIVIDUAL,
				Keyword.SAME_INDIVIDUALS );
	}

	public void visit(OWLSubClassOfAxiom theAxiom) {
		writeKeywordInfix( Keyword.SUB_CLASS_OF, theAxiom.getSubClass(), theAxiom.getSuperClass() );
	}

	public void visit(OWLSymmetricObjectPropertyAxiom theAxiom) {
		writeUnaryPropertyAxiom( theAxiom, Keyword.SYMMETRIC );
	}

	public void visit(OWLTransitiveObjectPropertyAxiom theAxiom) {
		writeUnaryPropertyAxiom( theAxiom, Keyword.TRANSITIVE );
	}

	public void visit(OWLLiteral node) {
		if( node.isRDFPlainLiteral() ) {
			write( "\"" );
			write( node.getLiteral() );
			write( "\"" );
			if( node.getLang() != null && !node.getLang().equals("")) {
				write( "@" );
				write( node.getLang() );
			}
		}
		else if( node.getDatatype().getIRI().equals( XSDVocabulary.INTEGER.getIRI() ) 
			|| node.getDatatype().getIRI().equals( XSDVocabulary.DECIMAL.getIRI() ) ) {
			write( node.getLiteral() );	
		}
		else if( node.getDatatype().getIRI().equals( XSDVocabulary.FLOAT.getIRI() )  ) {
			write( node.getLiteral() );	
			write( "f" );	
		}
		else {
			write( "\"" );
			write( node.getLiteral() );
			write( "\"" );
			write( "^^" );	
			write( node.getDatatype() );
		}
	}

	public void visit(SWRLLiteralArgument node) {
		write( node.getLiteral() );
	}

	public void visit(SWRLIndividualArgument node) {
		write( node.getIndividual() );
	}

	public void visit(SWRLVariable node) {
		write( "?" );
		write( node.getIRI() );
	}

	public void visit(SWRLBuiltInAtom node) {
		write( node.getPredicate() );
		write( "(" );
		for( SWRLArgument arg : node.getArguments() ) {
			write( arg );
			write( " " );
		}
		write( ")" );
	}

	public void visit(SWRLClassAtom node) {
		write( node.getPredicate() );
		write( "(" );
		write( node.getArgument() );
		write( ")" );
	}

	/*
	 * this is all the SWRL rendering stuff that we'll provide some defaults for
	 * using evren's concise format stuff
	 */

	public void visit(SWRLDataRangeAtom node) {
		write( node.getPredicate() );
		write( "(" );
		write( node.getArgument() );
		write( ")" );
	}

	public void visit(SWRLDataPropertyAtom node) {
		write( node.getPredicate() );
		write( "(" );
		write( node.getFirstArgument() );
		write( ", " );
		write( node.getSecondArgument() );
		write( ")" );
	}

	public void visit(SWRLDifferentIndividualsAtom node) {
		write( "differentFrom" );
		write( "(" );
		write( node.getFirstArgument() );
		write( ", " );
		write( node.getSecondArgument() );
		write( ")" );
	}

	public void visit(SWRLObjectPropertyAtom node) {
		write( node.getPredicate() );
		write( "(" );
		write( node.getFirstArgument() );
		write( ", " );
		write( node.getSecondArgument() );
		write( ")" );
	}

	/**
	 * @inheritDoc
	 */
	public void visit(SWRLRule rule) {
		write( "Rule" );
		write( "(" );

//		if( !rule.isAnonymous() ) {
//			write( rule.getIRI() );
//			writeSpace();
//		}

		boolean first = true;
		for( SWRLAtom at : rule.getBody() ) {
			if( first )
				first = false;
			else
				write( ", " );
			write( at );
			
		}
		write( " -> " );

		first = true;
		for( SWRLAtom at : rule.getHead() ) {
			if( first )
				first = false;
			else
				write( ", " );
			write( at );
		}

		write( ")" );
	}

	public void visit(SWRLSameIndividualAtom node) {
		write( "sameAs" );
		write( "(" );
		write( node.getFirstArgument() );
		write( ", " );
		write( node.getSecondArgument() );
		write( ")" );
	}

	protected void writeNaryKeyword(Keyword theKeyword, Set<? extends OWLObject> theObjects) {

		theObjects = DescriptionSorter.toSortedSet( theObjects );
		
		Iterator<? extends OWLObject> aIter = theObjects.iterator();
		
		// write( "(" );

		if( smartIndent )
			writer.startBlock();

		write( aIter.next() );
		while( aIter.hasNext() ) {
			if( wrapLines ) {
				writeNewLine();
			}
			else {
				writeSpace();
			}

			if( theKeyword != null ) {
				write( theKeyword );
				writeSpace();
			}

			write( aIter.next() );
		}

		if( smartIndent )
			writer.endBlock();

		// write( ")" );
	}

	protected void writeCardinalityRestriction(OWLCardinalityRestriction<?, ?, ?> theRestriction,
			Keyword theKeyword) {
		if( theRestriction.isQualified() )
			writeRestriction( theRestriction.getProperty(), theKeyword, theRestriction
					.getCardinality(), theRestriction.getFiller() );
		else
			writeRestriction( theRestriction.getProperty(), theKeyword, theRestriction
					.getCardinality() );
	}

	/**
	 * Render an n-ary axiom with special handling for the binary case.
	 * 
	 * @param set
	 *            objects to be rendered
	 * @param binary
	 *            keyword used for binary case
	 * @param nary
	 *            keyword used for n-ary case
	 */
	protected void writeNaryAxiom(Set<? extends OWLObject> set, Keyword binary, Keyword nary) {
		Set<? extends OWLObject> sortedSet = DescriptionSorter.toSortedSet( set );
		Iterator<? extends OWLObject> aIter = sortedSet.iterator();

		if( set.size() == 2 ) {
			writeKeywordInfix( binary, aIter.next(), aIter.next() );
		}
		else {
			write( nary );
			write( "(" );

			writeNaryKeyword( null, set );

			write( ")" );
		}
	}

	protected void writeQuantifiedRestriction(OWLQuantifiedRestriction<?, ?, ?> theRestriction,
			Keyword theKeyword) {
		writeRestriction( theRestriction.getProperty(), theKeyword, theRestriction.getFiller() );
	}

	protected void writeRestriction(OWLPropertyExpression<?, ?> theProperty, Keyword theKeyword,
			Object... theArgs) {
		// write( "(" );

		write( theProperty );
		writeSpace();
		write( theKeyword );		
		for( Object aObject : theArgs ) {
			writeSpace();
			if( aObject instanceof OWLObject ) {
				write( (OWLObject) aObject );
			}
			else {
				write( aObject.toString() );
			}
		}

		// write( ")" );

	}

	protected void writeUnaryPropertyAxiom(OWLUnaryPropertyAxiom<?> theAxiom, Keyword keyword) {
		writeKeywordPrefix( keyword, theAxiom.getProperty() );
	}

	protected void writeKeywordPrefix(Keyword keyword, OWLObject arg) {
		write( keyword );
		writeSpace();
//		write( "(" );
		write( arg );
//		write( ")" );
	}

	protected void writeKeywordPrefix(Keyword keyword, OWLObject arg1, OWLObject arg2) {
		write( keyword );
		writeSpace();
		write( "(" );
		write( arg1 );
		writeSpace();
		write( arg2 );
		write( ")" );
	}

	protected void writeKeywordInfix(Keyword keyword, OWLObject arg1, OWLObject arg2) {
		write( arg1 );
		writeSpace();
		write( keyword );
		writeSpace();
		write( arg2 );
	}

	protected void writeEnumeration(Set<? extends OWLObject> objects) {
		write( Keyword.OPEN_BRACE );
		writeCollection( objects, ",", true );
		write( Keyword.CLOSE_BRACE );
	}
	
	protected void writeCollection(Collection<? extends OWLObject> objects, String separator, boolean sort) {
		if( sort )
			objects = DescriptionSorter.toSortedSet( objects );
		boolean first = true;
		for( OWLObject ind : objects ) {
			if( first ) {
				first = false;
			}
			else {
				write( separator );
				writeSpace();
			}
			write( ind );
		}
	}

	protected void write(OWLObject object) {
		object.accept( this );
	}

	protected void write(Keyword keyword) {
		write( keyword.getLabel() );
	}

	protected void write(String s) {
		writer.print( s );
	}

	protected void write(IRI iri) {
		write( shortForm( iri ) );
	}

	protected void writeNewLine() {
		writer.println();
	}

	protected void writeSpace() {
		write( " " );
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OWLHasKeyAxiom theAxiom) {
		write( theAxiom.getClassExpression() );
		writeSpace();
		write( Keyword.HAS_KEY );
		if( !theAxiom.getObjectPropertyExpressions().isEmpty() ) {
			writeCollection( theAxiom.getObjectPropertyExpressions(), "", true );
			writeSpace();
		}
		writeCollection( theAxiom.getDataPropertyExpressions(), "", true );
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OWLDatatypeDefinitionAxiom theAxiom) {
		writeKeywordInfix( Keyword.EQUIVALENT_TO, theAxiom.getDatatype(), theAxiom.getDataRange() );
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OWLAnnotationAssertionAxiom theAxiom) {
		write( theAxiom.getSubject() );
		writeSpace();
		write( theAxiom.getProperty() );
		writeSpace();
		write( theAxiom.getValue() );
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OWLSubAnnotationPropertyOfAxiom theAxiom) {
		writeKeywordInfix( Keyword.SUB_PROPERTY_OF, theAxiom.getSubProperty(), theAxiom.getSuperProperty() );
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OWLAnnotationPropertyDomainAxiom theAxiom) {
		writeKeywordInfix( Keyword.DOMAIN, theAxiom.getProperty(), theAxiom.getDomain() );
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OWLAnnotationPropertyRangeAxiom theAxiom) {
		writeKeywordInfix( Keyword.RANGE, theAxiom.getProperty(), theAxiom.getRange() );
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OWLDataIntersectionOf node) {
		writeNaryKeyword( Keyword.AND, node.getOperands() );
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OWLDataUnionOf node) {
		writeNaryKeyword( Keyword.OR, node.getOperands() );
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OWLAnnotationProperty property) {
		write( property.getIRI() );
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(IRI iri) {
		write( iri );		
	}
}
