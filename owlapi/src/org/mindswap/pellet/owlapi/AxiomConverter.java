// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.owlapi;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.SetUtils;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLPropertyRange;
import org.semanticweb.owl.model.SWRLAtom;
import org.semanticweb.owl.model.SWRLAtomDObject;
import org.semanticweb.owl.model.SWRLAtomIObject;
import org.semanticweb.owl.vocab.SWRLBuiltInsVocabulary;

import aterm.ATermAppl;
import aterm.ATermList;

/**
 * <p>
 * Title: AxiomConverter
 * </p>
 * <p>
 * Description: Converts axioms expressed as ATerms to OWL-API structures.
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
public class AxiomConverter {
	public static Logger		log	= Logger.getLogger( AxiomConverter.class.getName() );

	private ConceptConverter	conceptConverter;
	private OWLDataFactory		factory;
	private KnowledgeBase		kb;

	public AxiomConverter(KnowledgeBase kb, OWLDataFactory factory) {
		if( kb == null )
			throw new NullPointerException( "KnowledgeBase is null" );
		if( factory == null )
			throw new NullPointerException( "OWLDataFactory is null" );

		this.kb = kb;
		this.factory = factory;
		this.conceptConverter = new ConceptConverter( kb, factory );
	}

	public OWLAxiom convert(ATermAppl term) {
		OWLAxiom axiom = null;

		if( term.getAFun().equals( ATermUtils.EQCLASSFUN ) ) {
			OWLDescription c1 = (OWLDescription) conceptConverter.convert( (ATermAppl) term
					.getArgument( 0 ) );
			OWLDescription c2 = (OWLDescription) conceptConverter.convert( (ATermAppl) term
					.getArgument( 1 ) );

			Set<OWLDescription> descriptions = new HashSet<OWLDescription>();
			descriptions.add( c1 );
			descriptions.add( c2 );

			if( c1 != null && c2 != null )
				axiom = factory.getOWLEquivalentClassesAxiom( descriptions );
		}
		else if( term.getAFun().equals( ATermUtils.SUBFUN ) ) {
			OWLDescription c1 = (OWLDescription) conceptConverter.convert( (ATermAppl) term
					.getArgument( 0 ) );
			OWLDescription c2 = (OWLDescription) conceptConverter.convert( (ATermAppl) term
					.getArgument( 1 ) );

			if( c1 != null && c2 != null )
				axiom = factory.getOWLSubClassAxiom( c1, c2 );
		}
		else if( term.getAFun().equals( ATermUtils.DISJOINTSFUN ) ) {
			Set<OWLDescription> descriptions = new HashSet<OWLDescription>();

			ATermList concepts = (ATermList) term.getArgument( 0 );
			for( ; !concepts.isEmpty(); concepts = concepts.getNext() ) {
				ATermAppl concept = (ATermAppl) concepts.getFirst();
				OWLDescription c = (OWLDescription) conceptConverter.convert( concept );
				if( c == null )
					break;

				descriptions.add( c );
			}

			// if no error occurred list will be empty
			if( concepts.isEmpty() )
				axiom = factory.getOWLDisjointClassesAxiom( descriptions );
		}
		else if( term.getAFun().equals( ATermUtils.DISJOINTFUN ) ) {
			OWLDescription c1 = (OWLDescription) conceptConverter.convert( (ATermAppl) term
					.getArgument( 0 ) );
			OWLDescription c2 = (OWLDescription) conceptConverter.convert( (ATermAppl) term
					.getArgument( 1 ) );

			Set<OWLDescription> descriptions = new HashSet<OWLDescription>();
			descriptions.add( c1 );
			descriptions.add( c2 );

			if( c1 != null && c2 != null )
				axiom = factory.getOWLDisjointClassesAxiom( descriptions );
		}
		else if( term.getAFun().equals( ATermUtils.DISJOINTPROPSFUN ) ) {
			// Set<OWLProperty<?,?>> properties = new
			// HashSet<OWLProperty<?,?>>();
			Set properties = new HashSet();

			Boolean isObjectProps = null;
			ATermList props = (ATermList) term.getArgument( 0 );
			for( ; !props.isEmpty(); props = props.getNext() ) {
				OWLObject p = conceptConverter.convert( (ATermAppl) term.getArgument( 0 ) );
				if( p == null )
					break;
				boolean isObjectProp = p instanceof OWLObjectProperty;
				if( !isObjectProp && !(p instanceof OWLDataProperty) )
					break;
				if( isObjectProps == null )
					isObjectProps = isObjectProp;
				else if( isObjectProps != isObjectProp )
					break;

				properties.add( (OWLProperty<?, ?>) p );
			}

			// if no error occurred list will be empty
			if( props.isEmpty() ) {
				if( isObjectProps )
					axiom = factory
							.getOWLDisjointObjectPropertiesAxiom( (Set<? extends OWLObjectPropertyExpression>) properties );
				else
					axiom = factory
							.getOWLDisjointDataPropertiesAxiom( (Set<? extends OWLDataPropertyExpression>) properties );
			}
		}
		else if( term.getAFun().equals( ATermUtils.DISJOINTPROPFUN ) ) {
			OWLObject p1 = conceptConverter.convert( (ATermAppl) term.getArgument( 0 ) );
			OWLObject p2 = conceptConverter.convert( (ATermAppl) term.getArgument( 1 ) );

			if( p1 != null && p2 != null ) {
				if( p1 instanceof OWLObjectProperty && p2 instanceof OWLObjectProperty )
					axiom = factory.getOWLDisjointObjectPropertiesAxiom( SetUtils.create(
							(OWLObjectProperty) p1, (OWLObjectProperty) p2 ) );
				else if( p1 instanceof OWLDataProperty && p2 instanceof OWLDataProperty )
					axiom = factory.getOWLDisjointDataPropertiesAxiom( SetUtils.create(
							(OWLDataProperty) p1, (OWLDataProperty) p2 ) );
			}
		}
		else if( term.getAFun().equals( ATermUtils.SUBPROPFUN ) ) {
			if( term.getArgument( 0 ) instanceof ATermList ) {
				List<OWLObjectPropertyExpression> subs = new ArrayList<OWLObjectPropertyExpression>();
				for( ATermList list = (ATermList) term.getArgument( 0 ); !list.isEmpty(); list = list
						.getNext() ) {
					OWLObjectPropertyExpression p = (OWLObjectPropertyExpression) conceptConverter
							.convert( (ATermAppl) list.getFirst() );
					if( p == null ) {
						subs = null;
						break;
					}
					subs.add( p );
				}
				OWLObjectProperty sup = (OWLObjectProperty) conceptConverter
						.convert( (ATermAppl) term.getArgument( 1 ) );

				if( subs != null && sup != null ) {
					axiom = factory.getOWLObjectPropertyChainSubPropertyAxiom( subs, sup );
				}
			}
			else {
				OWLObject p1 = conceptConverter.convert( (ATermAppl) term.getArgument( 0 ) );
				OWLObject p2 = conceptConverter.convert( (ATermAppl) term.getArgument( 1 ) );

				if( p1 != null && p2 != null ) {
					if( p1 instanceof OWLObjectPropertyExpression && p2 instanceof OWLObjectPropertyExpression )
						axiom = factory.getOWLSubObjectPropertyAxiom( (OWLObjectPropertyExpression) p1,
								(OWLObjectPropertyExpression) p2 );
					else if( p1 instanceof OWLDataProperty && p2 instanceof OWLDataProperty )
						axiom = factory.getOWLSubDataPropertyAxiom( (OWLDataProperty) p1,
								(OWLDataProperty) p2 );
				}
			}
		}
		else if( term.getAFun().equals( ATermUtils.EQPROPFUN ) ) {
			OWLObject p1 = conceptConverter.convert( (ATermAppl) term.getArgument( 0 ) );
			OWLObject p2 = conceptConverter.convert( (ATermAppl) term.getArgument( 1 ) );

			if( p1 != null && p2 != null ) {
				if( p1 instanceof OWLObjectProperty && p2 instanceof OWLObjectProperty )
					axiom = factory.getOWLEquivalentObjectPropertiesAxiom( SetUtils.create(
							(OWLObjectProperty) p1, (OWLObjectProperty) p2 ) );
				else if( p1 instanceof OWLDataProperty && p2 instanceof OWLDataProperty )
					axiom = factory.getOWLEquivalentDataPropertiesAxiom( SetUtils.create(
							(OWLDataProperty) p1, (OWLDataProperty) p2 ) );
			}
		}
		else if( term.getAFun().equals( ATermUtils.DOMAINFUN ) ) {
			OWLObject p = conceptConverter.convert( (ATermAppl) term.getArgument( 0 ) );
			OWLDescription c = (OWLDescription) conceptConverter.convert( (ATermAppl) term
					.getArgument( 1 ) );

			if( c != null && p != null ) {
				if( p instanceof OWLObjectProperty )
					axiom = factory.getOWLObjectPropertyDomainAxiom(
							(OWLObjectPropertyExpression) p, c );
				else
					axiom = factory
							.getOWLDataPropertyDomainAxiom( (OWLDataPropertyExpression) p, c );
			}
		}
		else if( term.getAFun().equals( ATermUtils.RANGEFUN ) ) {
			OWLPropertyRange e = (OWLPropertyRange) conceptConverter.convert( (ATermAppl) term
					.getArgument( 1 ) );
			if( e != null ) {
				if( e instanceof OWLDescription ) {
					OWLObjectProperty p = (OWLObjectProperty) conceptConverter
							.convert( (ATermAppl) term.getArgument( 0 ) );
					if( p != null )
						axiom = factory.getOWLObjectPropertyRangeAxiom( p, (OWLDescription) e );
				}
				else {
					OWLDataProperty p = (OWLDataProperty) conceptConverter
							.convert( (ATermAppl) term.getArgument( 0 ) );
					if( p != null )
						axiom = factory.getOWLDataPropertyRangeAxiom( p, (OWLDataRange) e );
				}
			}
		}
		else if( term.getAFun().equals( ATermUtils.INVPROPFUN ) ) {
			OWLObjectProperty p1 = (OWLObjectProperty) conceptConverter.convert( (ATermAppl) term
					.getArgument( 0 ) );
			OWLObjectProperty p2 = (OWLObjectProperty) conceptConverter.convert( (ATermAppl) term
					.getArgument( 1 ) );

			if( p1 != null && p2 != null )
				axiom = factory.getOWLInverseObjectPropertiesAxiom( p1, p2 );
		}
		else if( term.getAFun().equals( ATermUtils.TRANSITIVEFUN ) ) {
			OWLObjectProperty p = (OWLObjectProperty) conceptConverter.convert( (ATermAppl) term
					.getArgument( 0 ) );

			if( p != null )
				axiom = factory.getOWLTransitiveObjectPropertyAxiom( p );
		}
		else if( term.getAFun().equals( ATermUtils.FUNCTIONALFUN ) ) {
			OWLObject p = conceptConverter.convert( (ATermAppl) term.getArgument( 0 ) );

			if( p != null ) {
				if( p instanceof OWLObjectProperty )
					axiom = factory
							.getOWLFunctionalObjectPropertyAxiom( (OWLObjectPropertyExpression) p );
				else if( p instanceof OWLDataProperty )
					axiom = factory
							.getOWLFunctionalDataPropertyAxiom( (OWLDataPropertyExpression) p );
			}
		}
		else if( term.getAFun().equals( ATermUtils.INVFUNCTIONALFUN ) ) {
			OWLObjectProperty p = (OWLObjectProperty) conceptConverter.convert( (ATermAppl) term
					.getArgument( 0 ) );

			if( p != null )
				axiom = factory.getOWLInverseFunctionalObjectPropertyAxiom( p );
		}
		else if( term.getAFun().equals( ATermUtils.SYMMETRICFUN ) ) {
			OWLObject p = conceptConverter.convert( (ATermAppl) term.getArgument( 0 ) );

			if( p != null && p instanceof OWLObjectPropertyExpression )
				axiom = factory
						.getOWLSymmetricObjectPropertyAxiom( (OWLObjectPropertyExpression) p );
		}
		else if( term.getAFun().equals( ATermUtils.ASYMMETRICFUN ) ) {
			OWLObject p = conceptConverter.convert( (ATermAppl) term.getArgument( 0 ) );

			if( p != null && p instanceof OWLObjectPropertyExpression )
				axiom = factory
						.getOWLAntiSymmetricObjectPropertyAxiom( (OWLObjectPropertyExpression) p );
		}
		else if( term.getAFun().equals( ATermUtils.REFLEXIVEFUN ) ) {
			OWLObject p = conceptConverter.convert( (ATermAppl) term.getArgument( 0 ) );

			if( p != null && p instanceof OWLObjectPropertyExpression )
				axiom = factory
						.getOWLReflexiveObjectPropertyAxiom( (OWLObjectPropertyExpression) p );
		}
		else if( term.getAFun().equals( ATermUtils.IRREFLEXIVEFUN ) ) {
			OWLObject p = conceptConverter.convert( (ATermAppl) term.getArgument( 0 ) );

			if( p != null && p instanceof OWLObjectPropertyExpression )
				axiom = factory
						.getOWLIrreflexiveObjectPropertyAxiom( (OWLObjectPropertyExpression) p );
		}
		else if( term.getAFun().equals( ATermUtils.TYPEFUN ) ) {
			OWLIndividual i = (OWLIndividual) conceptConverter.convert( (ATermAppl) term
					.getArgument( 0 ) );
			OWLDescription c = (OWLDescription) conceptConverter.convert( (ATermAppl) term
					.getArgument( 1 ) );

			if( i != null && c != null )
				axiom = factory.getOWLClassAssertionAxiom( i, c );
		}
		else if( term.getAFun().equals( ATermUtils.PROPFUN ) ) {
			OWLIndividual subj = (OWLIndividual) conceptConverter.convert( (ATermAppl) term.getArgument( 1 ) );

			if( subj == null )
				axiom = null;
			else if( ATermUtils.isLiteral( (ATermAppl) term.getArgument( 2 ) ) ) {
				OWLDataProperty pred = (OWLDataProperty) conceptConverter.convert( (ATermAppl) term
						.getArgument( 0 ) );
				OWLConstant obj = (OWLConstant) conceptConverter.convert( (ATermAppl) term
						.getArgument( 2 ) );
				if( pred != null && obj != null )
					axiom = factory.getOWLDataPropertyAssertionAxiom( subj, pred, obj );
			}
			else {
				OWLObjectProperty pred = (OWLObjectProperty) conceptConverter
						.convert( (ATermAppl) term.getArgument( 0 ) );
				OWLIndividual obj = (OWLIndividual) conceptConverter.convert( (ATermAppl) term.getArgument( 2 ) );
				if( pred != null && obj != null )
					axiom = factory.getOWLObjectPropertyAssertionAxiom( subj, pred, obj );
			}
		}
		else if( term.getAFun().equals( ATermUtils.NOTFUN )
				&& ((ATermAppl) term.getArgument( 0 )).getAFun().equals( ATermUtils.PROPFUN ) ) {
			term = (ATermAppl) term.getArgument( 0 );
			OWLIndividual subj = (OWLIndividual) conceptConverter.convert( (ATermAppl) term
					.getArgument( 1 ) );

			if( subj == null )
				axiom = null;
			else if( ATermUtils.isLiteral( (ATermAppl) term.getArgument( 2 ) ) ) {
				OWLDataProperty pred = (OWLDataProperty) conceptConverter.convert( (ATermAppl) term
						.getArgument( 0 ) );
				OWLConstant obj = (OWLConstant) conceptConverter.convert( (ATermAppl) term
						.getArgument( 2 ) );
				if( pred != null && obj != null )
					axiom = factory.getOWLNegativeDataPropertyAssertionAxiom( subj, pred, obj );
			}
			else {
				OWLObjectProperty pred = (OWLObjectProperty) conceptConverter
						.convert( (ATermAppl) term.getArgument( 0 ) );
				OWLIndividual obj = (OWLIndividual) conceptConverter.convert( (ATermAppl) term
						.getArgument( 2 ) );;
				if( pred != null && obj != null )
					axiom = factory.getOWLNegativeObjectPropertyAssertionAxiom( subj, pred, obj );
			}
		}
		else if( term.getAFun().equals( ATermUtils.SAMEASFUN ) ) {
			OWLIndividual ind1 = (OWLIndividual) conceptConverter.convert( (ATermAppl) term
					.getArgument( 0 ) );;
			OWLIndividual ind2 = (OWLIndividual) conceptConverter.convert( (ATermAppl) term
					.getArgument( 1 ) );;

			Set<OWLIndividual> inds = new HashSet<OWLIndividual>();
			inds.add( ind1 );
			inds.add( ind2 );

			if( ind1 != null && ind2 != null )
				axiom = factory.getOWLSameIndividualsAxiom( inds );
		}
		else if( term.getAFun().equals( ATermUtils.DIFFERENTFUN ) ) {
			OWLIndividual ind1 = (OWLIndividual) conceptConverter.convert( (ATermAppl) term
					.getArgument( 0 ) );;
			OWLIndividual ind2 = (OWLIndividual) conceptConverter.convert( (ATermAppl) term
					.getArgument( 1 ) );;

			Set<OWLIndividual> inds = new HashSet<OWLIndividual>();
			inds.add( ind1 );
			inds.add( ind2 );

			if( ind1 != null && ind2 != null )
				axiom = factory.getOWLDifferentIndividualsAxiom( inds );
		}
		else if( term.getAFun().equals( ATermUtils.ALLDIFFERENTFUN ) ) {
			Set<OWLIndividual> individuals = new HashSet<OWLIndividual>();

			ATermList list = (ATermList) term.getArgument( 0 );
			for( ; !list.isEmpty(); list = list.getNext() ) {
				ATermAppl ind = (ATermAppl) list.getFirst();
				OWLIndividual i = (OWLIndividual) conceptConverter.convert( ind );;
				if( i == null )
					break;

				individuals.add( i );
			}

			// if no error occurred list will be empty
			if( list.isEmpty() )
				axiom = factory.getOWLDifferentIndividualsAxiom( individuals );
		}
		else if( term.getAFun().equals( ATermUtils.RULEFUN ) ) {
			Set<SWRLAtom> antecedent = new HashSet<SWRLAtom>(); // Body
			Set<SWRLAtom> consequent = new HashSet<SWRLAtom>(); // Head

			ATermList head = (ATermList) term.getArgument( 1 );
			ATermList body = (ATermList) term.getArgument( 2 );

			for( ; !body.isEmpty(); body = body.getNext() )
				antecedent.add( parseToSWRLAtom( (ATermAppl) body.getFirst() ) );

			for( ; !head.isEmpty(); head = head.getNext() )
				consequent.add( parseToSWRLAtom( (ATermAppl) head.getFirst() ) );

			if( !antecedent.contains( null ) && !consequent.contains( null ) ) {
				ATermAppl name = (ATermAppl) term.getArgument( 0 );
				if( name == ATermUtils.EMPTY ) {
					axiom = factory.getSWRLRule( antecedent, consequent );
				}
				else if( ATermUtils.isBnode( name ) ) {
					URI uri = URI.create( ((ATermAppl) name.getArgument( 0 )).getName() );
					axiom = factory.getSWRLRule( uri, true, antecedent, consequent );
				}
				else {
					axiom = factory.getSWRLRule( URI.create( name.getName() ), antecedent, consequent );
				}
			}
		}

		if( axiom == null )
			log.warning( "Cannot convert to OWLAPI: " + term );

		return axiom;
	}

	private SWRLAtom parseToSWRLAtom(ATermAppl term) {
		SWRLAtom atom = null;

		if( term.getAFun().equals( ATermUtils.TYPEFUN ) ) {
			ATermAppl i = (ATermAppl) term.getArgument( 0 );
			SWRLAtomIObject io = parseToAtomIObject( i );

			OWLDescription c = (OWLDescription) conceptConverter.convert( (ATermAppl) term
					.getArgument( 1 ) );

			atom = factory.getSWRLClassAtom( c, io );
		}
		else if( term.getAFun().equals( ATermUtils.PROPFUN ) ) {
			ATermAppl p = (ATermAppl) term.getArgument( 0 );
			ATermAppl i1 = (ATermAppl) term.getArgument( 1 );
			ATermAppl i2 = (ATermAppl) term.getArgument( 2 );
			SWRLAtomIObject io1 = parseToAtomIObject( i1 );

			if( kb.isObjectProperty( p ) ) {
				SWRLAtomIObject io2 = parseToAtomIObject( i2 );
				OWLObjectProperty op = factory.getOWLObjectProperty( URI.create( p.getName() ) );
				atom = factory.getSWRLObjectPropertyAtom( op, io1, io2 );
			}
			else if( kb.isDatatypeProperty( p ) ) {
				SWRLAtomDObject do2 = parseToAtomDObject( i2 );
				OWLDataProperty dp = factory.getOWLDataProperty( URI.create( p.getName() ) );
				atom = factory.getSWRLDataValuedPropertyAtom( dp, io1, do2 );
			}
		}
		else if( term.getAFun().equals( ATermUtils.SAMEASFUN ) ) {
			ATermAppl i1 = (ATermAppl) term.getArgument( 0 );
			ATermAppl i2 = (ATermAppl) term.getArgument( 1 );
			SWRLAtomIObject io1 = parseToAtomIObject( i1 );
			SWRLAtomIObject io2 = parseToAtomIObject( i2 );
			
			atom = factory.getSWRLSameAsAtom( io1, io2 );			
		}
		else if( term.getAFun().equals( ATermUtils.DIFFERENTFUN ) ) {
			ATermAppl i1 = (ATermAppl) term.getArgument( 0 );
			ATermAppl i2 = (ATermAppl) term.getArgument( 1 );
			SWRLAtomIObject io1 = parseToAtomIObject( i1 );
			SWRLAtomIObject io2 = parseToAtomIObject( i2 );
			
			atom = factory.getSWRLDifferentFromAtom( io1, io2 );			
		}
		else if( term.getAFun().equals( ATermUtils.BUILTINFUN ) ) {
			ATermList args = (ATermList) term.getArgument( 0 );
			ATermAppl builtin = (ATermAppl) args.getFirst();
			List<SWRLAtomDObject> list = new ArrayList<SWRLAtomDObject>();
			for( args = args.getNext(); !args.isEmpty(); args = args.getNext() ) {
				ATermAppl arg = (ATermAppl) args.getFirst();
				list.add( parseToAtomDObject( arg ) );
			}
			
			atom = factory.getSWRLBuiltInAtom( SWRLBuiltInsVocabulary.getBuiltIn( 
				URI.create( builtin.toString() ) ), list );			
		}

		if( atom == null )
			log.warning( "Cannot convert to SWRLAtom: " + term );

		return atom;
	}

	private SWRLAtomIObject parseToAtomIObject(ATermAppl t) {
		if( ATermUtils.isVar( t ) )
			return factory.getSWRLAtomIVariable( URI.create( ((ATermAppl) t.getArgument( 0 ))
					.getName() ) );
		if( kb.isIndividual( t ) )
			return factory.getSWRLAtomIndividualObject( (OWLIndividual) conceptConverter
					.convert( t ) );

		throw new InternalReasonerException( "Unrecognized term: " + t );
	}

	private SWRLAtomDObject parseToAtomDObject(ATermAppl t) {
		if( ATermUtils.isVar( t ) )
			return factory.getSWRLAtomDVariable( URI.create( ((ATermAppl) t.getArgument( 0 ))
					.getName() ) );
		else if( ATermUtils.isLiteral( t ) ) {
			return factory.getSWRLAtomConstantObject( (OWLConstant) conceptConverter.convert( t ) );
		}

		throw new InternalReasonerException( "Unrecognized term: " + t );
	}
}
