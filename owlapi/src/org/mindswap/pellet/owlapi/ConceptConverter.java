// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.owlapi;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.output.ATermBaseVisitor;
import org.mindswap.pellet.output.ATermVisitor;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDataRangeFacetRestriction;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLPropertyExpression;
import org.semanticweb.owl.model.OWLTypedConstant;
import org.semanticweb.owl.vocab.OWLRestrictedDataRangeFacetVocabulary;

import aterm.ATermAppl;
import aterm.ATermList;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: Converts concepts expressed as ATerms to OWL-API structures.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class ConceptConverter extends ATermBaseVisitor implements ATermVisitor {
	
	private static final URI OWL_TOP_OBJ_PROP = URI.create( Namespaces.OWL + "topObjectProperty" );
	private static final URI OWL_BOT_OBJ_PROP = URI.create( Namespaces.OWL + "bottomObjectProperty" );
	private static final URI OWL_TOP_DATA_PROP = URI.create( Namespaces.OWL + "topDataProperty" );
	private static final URI OWL_BOT_DATA_PROP = URI.create( Namespaces.OWL + "bottomDataProperty" );
	
	private KnowledgeBase	kb;
	private OWLDataFactory	factory;
	private OWLObject		obj;
	private Set				set;

	public ConceptConverter(KnowledgeBase kb, OWLDataFactory factory) {
		this.kb = kb;
		this.factory = factory;
	}

	public OWLObject convert(ATermAppl term) {
		obj = null;

		visit( term );

		return obj;
	}

	public OWLObject getResult() {
		return obj;
	}

	public void visitTerm(ATermAppl term) {
		obj = null;

		URI uri = null;

		if( ATermUtils.isBnode( term ) )
			uri = URI.create( ((ATermAppl) term.getArgument( 0 )).getName() );
		else
			uri = URI.create( term.getName() );

		if( uri == null )
			throw new NullPointerException( "Could not resolve URI for term: URI is null" );

		if( term.equals( OWL_THING ) )
			obj = factory.getOWLThing();
		else if( term.equals( OWL_NOTHING ) )
			obj = factory.getOWLNothing();
		if( kb.isClass( term ) )
			obj = factory.getOWLClass( uri );
		else if( kb.isObjectProperty( term ) ) {
			if( ATermUtils.TOP_OBJECT_PROPERTY.equals( term ) )
				obj = factory.getOWLObjectProperty( OWL_TOP_OBJ_PROP );
			else if( ATermUtils.BOTTOM_DATA_PROPERTY.equals( term ) )
				obj = factory.getOWLObjectProperty( OWL_BOT_OBJ_PROP );
			else
				obj = factory.getOWLObjectProperty( uri );
		}
		else if( kb.isDatatypeProperty( term ) ) {
			if( ATermUtils.TOP_DATA_PROPERTY.equals( term ) )
				obj = factory.getOWLDataProperty( OWL_TOP_DATA_PROP );
			else if( ATermUtils.BOTTOM_DATA_PROPERTY.equals( term ) )
				obj = factory.getOWLDataProperty( OWL_BOT_DATA_PROP );
			else
				obj = factory.getOWLDataProperty( uri );
		}
		else if( kb.isIndividual( term ) ) {
			if( ATermUtils.isBnode( term ) )
				obj = factory.getOWLAnonymousIndividual( uri );
			else
				obj = factory.getOWLIndividual( uri );
		}
		else if( kb.isDatatype( term ) )
			obj = factory.getOWLDataType( uri );

		if( obj == null )
			throw new InternalReasonerException( "Ontology does not contain: " + term );
	}

	public void visitAnd(ATermAppl term) {
		visitList( (ATermList) term.getArgument( 0 ) );

		obj = factory.getOWLObjectIntersectionOf( set );

	}

	public void visitOr(ATermAppl term) {
		visitList( (ATermList) term.getArgument( 0 ) );

		obj = factory.getOWLObjectUnionOf( set );

	}

	public void visitNot(ATermAppl term) {
		visit( (ATermAppl) term.getArgument( 0 ) );

		if( obj instanceof OWLDescription )
			obj = factory.getOWLObjectComplementOf( (OWLDescription) obj );
		else if( obj instanceof OWLDataRange )
			obj = factory.getOWLDataComplementOf( (OWLDataRange) obj );
	}

	public void visitSome(ATermAppl term) {
		visit( (ATermAppl) term.getArgument( 0 ) );
		OWLPropertyExpression<?,?> prop = (OWLPropertyExpression<?,?>) obj;

		visit( (ATermAppl) term.getArgument( 1 ) );

		if( prop instanceof OWLObjectPropertyExpression ) {
			OWLDescription desc = (OWLDescription) obj;

			obj = factory.getOWLObjectSomeRestriction( (OWLObjectPropertyExpression) prop, desc );
		}
		else {
			OWLDataRange datatype = (OWLDataRange) obj;

			obj = factory.getOWLDataSomeRestriction( (OWLDataProperty) prop, datatype );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mindswap.pellet.output.ATermVisitor#visitAll(aterm.ATermAppl)
	 */
	public void visitAll(ATermAppl term) {
		visit( (ATermAppl) term.getArgument( 0 ) );
		OWLPropertyExpression<?,?> prop = (OWLPropertyExpression<?,?>) obj;

		visit( (ATermAppl) term.getArgument( 1 ) );

		if( prop instanceof OWLObjectPropertyExpression ) {
			OWLDescription desc = (OWLDescription) obj;

			obj = factory.getOWLObjectAllRestriction( (OWLObjectPropertyExpression) prop, desc );
		}
		else {
			OWLDataRange datatype = (OWLDataRange) obj;

			obj = factory.getOWLDataAllRestriction( (OWLDataProperty) prop, datatype );
		}

	}

	public void visitMin(ATermAppl term) {
		visit( (ATermAppl) term.getArgument( 0 ) );
		OWLPropertyExpression<?,?> prop = (OWLPropertyExpression<?,?>) obj;

		int cardinality = Integer.parseInt( term.getArgument( 1 ).toString() );

		if( prop instanceof OWLObjectPropertyExpression ) {
			OWLDescription c = (OWLDescription) convert( (ATermAppl) term.getArgument( 2 ) );
			obj = factory.getOWLObjectMinCardinalityRestriction( (OWLObjectPropertyExpression) prop,
					cardinality, c );
		}
		else {
			OWLDataRange d = (OWLDataRange) convert( (ATermAppl) term.getArgument( 2 ) );
			obj = factory.getOWLDataMinCardinalityRestriction( (OWLDataProperty) prop, cardinality,
					d );
		}
	}

	public void visitCard(ATermAppl term) {
		visit( (ATermAppl) term.getArgument( 0 ) );
		OWLPropertyExpression<?,?> prop = (OWLPropertyExpression<?,?>) obj;

		int cardinality = Integer.parseInt( term.getArgument( 1 ).toString() );

		if( prop instanceof OWLObjectPropertyExpression ) {
			OWLDescription c = (OWLDescription) convert( (ATermAppl) term.getArgument( 2 ) );
			obj = factory.getOWLObjectExactCardinalityRestriction( (OWLObjectPropertyExpression) prop,
					cardinality, c );
		}
		else {
			OWLDataRange d = (OWLDataRange) convert( (ATermAppl) term.getArgument( 2 ) );
			obj = factory.getOWLDataExactCardinalityRestriction( (OWLDataProperty) prop,
					cardinality, d );
		}
	}

	public void visitMax(ATermAppl term) {
		visit( (ATermAppl) term.getArgument( 0 ) );
		OWLPropertyExpression<?,?> prop = (OWLPropertyExpression<?,?>) obj;

		int cardinality = Integer.parseInt( term.getArgument( 1 ).toString() );

		if( prop instanceof OWLObjectPropertyExpression ) {
			OWLDescription c = (OWLDescription) convert( (ATermAppl) term.getArgument( 2 ) );
			obj = factory.getOWLObjectMaxCardinalityRestriction( (OWLObjectPropertyExpression) prop,
					cardinality, c );
		}
		else {
			OWLDataRange d = (OWLDataRange) convert( (ATermAppl) term.getArgument( 2 ) );
			obj = factory.getOWLDataMaxCardinalityRestriction( (OWLDataProperty) prop, cardinality,
					d );
		}
	}

	public void visitHasValue(ATermAppl term) {
		visit( (ATermAppl) term.getArgument( 0 ) );
		OWLPropertyExpression<?,?> prop = (OWLPropertyExpression<?,?>) obj;

		visit( (ATermAppl) ((ATermAppl) term.getArgument( 1 )).getArgument( 0 ) );

		if( prop instanceof OWLObjectProperty ) {
			OWLIndividual ind = (OWLIndividual) obj;

			obj = factory.getOWLObjectValueRestriction( (OWLObjectPropertyExpression) prop, ind );
		}
		else {
			OWLConstant dataVal = (OWLConstant) obj;

			obj = factory.getOWLDataValueRestriction( (OWLDataProperty) prop, dataVal );
		}
	}

	public void visitValue(ATermAppl term) {
		visit( (ATermAppl) term.getArgument( 0 ) );
		
		if( obj instanceof OWLIndividual )
			obj = factory.getOWLObjectOneOf( (OWLIndividual) obj );
		else
			obj = factory.getOWLDataOneOf( (OWLConstant) obj );
	}

	public void visitSelf(ATermAppl term) {
		visit( (ATermAppl) term.getArgument( 0 ) );
		OWLObjectPropertyExpression prop = (OWLObjectPropertyExpression) obj;

		obj = factory.getOWLObjectSelfRestriction( prop );

	}

	public void visitOneOf(ATermAppl term) {
		set = new HashSet();

		ATermList list = (ATermList) term.getArgument( 0 );	
		for( ; !list.isEmpty(); list = list.getNext() ) {		
			ATermAppl first = (ATermAppl) list.getFirst();
			visit( (ATermAppl) first.getArgument( 0 ) );
			if( obj == null )
				return;
			set.add( obj );
		}

		if( set.isEmpty() || set.iterator().next() instanceof OWLIndividual )
			obj = factory.getOWLObjectOneOf( set );
		else
			obj = factory.getOWLDataOneOf( set );
	}

	public void visitLiteral(ATermAppl term) {
		// literal(lexicalValue, language, datatypeURI)

		String lexValue = ((ATermAppl) term.getArgument( 0 )).toString();
		ATermAppl lang = (ATermAppl) term.getArgument( 1 );
		ATermAppl dtype = (ATermAppl) term.getArgument( 2 );
		if( dtype.equals( ATermUtils.PLAIN_LITERAL_DATATYPE ) ) {
			if( lang.equals( ATermUtils.EMPTY ) )
				obj = factory.getOWLUntypedConstant( lexValue );
			else
				obj = factory.getOWLUntypedConstant( lexValue, lang.toString() );
		}
		else {
			URI dtypeURI = URI.create( dtype.toString() );
			OWLDataType datatype = factory.getOWLDataType( dtypeURI );
			obj = factory.getOWLTypedConstant( lexValue, datatype );
		}
	}

	public void visitList(ATermList list) {
		this.set = null;
		Set elements = new HashSet();
		while( !list.isEmpty() ) {
			ATermAppl term = (ATermAppl) list.getFirst();
			visit( term );
			if( obj == null )
				return;
			elements.add( obj );
			list = list.getNext();
		}
		this.set = elements;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visitInverse(ATermAppl p) {
		OWLObjectPropertyExpression prop = (OWLObjectPropertyExpression) convert( (ATermAppl) p
				.getArgument( 0 ) );

		obj = factory.getOWLObjectPropertyInverse( prop );
	}
	
	
	public void visitRestrictedDatatype(ATermAppl dt) {
        OWLDataType baseDatatype = factory.getOWLDataType( URI.create( ((ATermAppl) dt
				.getArgument( 0 )).getName() ) );

		Set<OWLDataRangeFacetRestriction> restrictions = new HashSet<OWLDataRangeFacetRestriction>();		
		for( ATermList list = (ATermList) dt.getArgument( 1 ); !list.isEmpty() ; list = list.getNext()) {
			ATermAppl facet = (ATermAppl) list.getFirst();
			String facetName = ((ATermAppl) facet.getArgument( 0 )).getName();
			ATermAppl facetValue = (ATermAppl) facet.getArgument( 1 );
			visitLiteral( facetValue );
			restrictions.add( factory.getOWLDataRangeFacetRestriction( OWLRestrictedDataRangeFacetVocabulary
					.getFacet( URI.create( facetName ) ), (OWLTypedConstant) obj ) );			
		}
		obj = factory.getOWLDataRangeRestriction( baseDatatype, restrictions );
	}
}
