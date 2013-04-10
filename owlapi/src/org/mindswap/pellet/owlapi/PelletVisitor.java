// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC.
// <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms
// of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.owlapi;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.exceptions.UnsupportedFeatureException;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Comparators;
import org.mindswap.pellet.utils.MultiValueMap;
import org.mindswap.pellet.utils.progress.ProgressMonitor;
import org.semanticweb.owl.model.OWLAntiSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomAnnotationAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLConstantAnnotation;
import org.semanticweb.owl.model.OWLDataAllRestriction;
import org.semanticweb.owl.model.OWLDataComplementOf;
import org.semanticweb.owl.model.OWLDataExactCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataOneOf;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLDataRangeFacetRestriction;
import org.semanticweb.owl.model.OWLDataRangeRestriction;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDataSubPropertyAxiom;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDataValueRestriction;
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
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLObjectAnnotation;
import org.semanticweb.owl.model.OWLObjectComplementOf;
import org.semanticweb.owl.model.OWLObjectExactCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectIntersectionOf;
import org.semanticweb.owl.model.OWLObjectMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectOneOf;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyChainSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLObjectPropertyInverse;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLObjectSelfRestriction;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLObjectSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectUnionOf;
import org.semanticweb.owl.model.OWLObjectValueRestriction;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyAnnotationAxiom;
import org.semanticweb.owl.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLSameIndividualsAxiom;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLTypedConstant;
import org.semanticweb.owl.model.OWLUntypedConstant;
import org.semanticweb.owl.model.SWRLAtom;
import org.semanticweb.owl.model.SWRLAtomConstantObject;
import org.semanticweb.owl.model.SWRLAtomDObject;
import org.semanticweb.owl.model.SWRLAtomDVariable;
import org.semanticweb.owl.model.SWRLAtomIObject;
import org.semanticweb.owl.model.SWRLAtomIVariable;
import org.semanticweb.owl.model.SWRLAtomIndividualObject;
import org.semanticweb.owl.model.SWRLBuiltInAtom;
import org.semanticweb.owl.model.SWRLClassAtom;
import org.semanticweb.owl.model.SWRLDataRangeAtom;
import org.semanticweb.owl.model.SWRLDataValuedPropertyAtom;
import org.semanticweb.owl.model.SWRLDifferentFromAtom;
import org.semanticweb.owl.model.SWRLObjectPropertyAtom;
import org.semanticweb.owl.model.SWRLRule;
import org.semanticweb.owl.model.SWRLSameAsAtom;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;

import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.pellet.datatypes.Facet;
import com.clarkparsia.pellet.rules.model.AtomDConstant;
import com.clarkparsia.pellet.rules.model.AtomDObject;
import com.clarkparsia.pellet.rules.model.AtomDVariable;
import com.clarkparsia.pellet.rules.model.AtomIConstant;
import com.clarkparsia.pellet.rules.model.AtomIObject;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;
import com.clarkparsia.pellet.rules.model.ClassAtom;
import com.clarkparsia.pellet.rules.model.DataRangeAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.DifferentIndividualsAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.model.SameIndividualAtom;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
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
public class PelletVisitor implements OWLObjectVisitor {
	private static final long											serialVersionUID	= 8211773146996997500L;

	public static Logger												log					= Logger
																									.getLogger( PelletVisitor.class
																											.getName() );

	private KnowledgeBase												kb;

	private ATermAppl													term;

	private AtomDObject													swrlDObject;

	private AtomIObject													swrlIObject;

	private RuleAtom													swrlAtom;

	private ProgressMonitor												monitor;

	private boolean														addAxioms;

	private boolean														reloadRequired;

	private Set<OWLAxiom>												unsupportedAxioms;

	/*
	 * Only simple properties can be used in cardinality restrictions,
	 * disjointness axioms, irreflexivity and antisymmetry axioms. The following
	 * constants will be used to identify why a certain property should be
	 * treated as simple
	 */
	private MultiValueMap<OWLObjectProperty, OWLObjectPropertyAxiom>	compositePropertyAxioms;
	private Set<OWLObjectProperty>										simpleProperties;

	public PelletVisitor(KnowledgeBase kb) {
		this.kb = kb;

		clear();
	}

	/**
	 * Clear the visitor cache about simple properties. Should be called before
	 * a reload.
	 */
	public void clear() {
		unsupportedAxioms = new HashSet<OWLAxiom>();
		compositePropertyAxioms = new MultiValueMap<OWLObjectProperty, OWLObjectPropertyAxiom>();
		simpleProperties = new HashSet<OWLObjectProperty>();
	}

	private void addUnsupportedAxiom(OWLAxiom axiom) {
		if( !PelletOptions.IGNORE_UNSUPPORTED_AXIOMS )
			throw new UnsupportedFeatureException( "Axiom: " + axiom );

		if( unsupportedAxioms.add( axiom ) )
			log.warning( "Ignoring unsupported axiom: " + axiom );
	}
	
	public Set<OWLAxiom> getUnsupportedAxioms() {
		return Collections.unmodifiableSet( unsupportedAxioms );
	}

	private OWLObjectProperty getNamedProperty(OWLObjectPropertyExpression ope) {
		if( ope.isAnonymous() )
			return getNamedProperty( ((OWLObjectPropertyInverse) ope).getInverse() );
		else
			return ope.asOWLObjectProperty();
	}

	private void addSimpleProperty(OWLObjectPropertyExpression ope) {
		if( !addAxioms ) {
			// no need to mark simple properties during removal 
			return;
		}
		
		OWLObjectProperty prop = getNamedProperty( ope );
		simpleProperties.add( prop );

		prop.accept( this );
		Role role = kb.getRBox().getRole( term );
		role.setForceSimple( true );
	}

	void verify() {
		for( Map.Entry<OWLObjectProperty, Set<OWLObjectPropertyAxiom>> entry : compositePropertyAxioms
				.entrySet() ) {
			OWLObjectProperty nonSimpleProperty = entry.getKey();

			if( !simpleProperties.contains( nonSimpleProperty ) )
				continue;

			Set<OWLObjectPropertyAxiom> axioms = entry.getValue();
			for( OWLObjectPropertyAxiom axiom : axioms )
				addUnsupportedAxiom( axiom );

			ATermAppl name = ATermUtils.makeTermAppl( nonSimpleProperty.getURI().toString() );
			Role role = kb.getRBox().getRole( name );
			role.removeSubRoleChains();
		}
	}

	public void setAddAxiom(boolean addAxioms) {
		this.addAxioms = addAxioms;
	}

	public boolean isReloadRequired() {
		return reloadRequired;
	}

	public ATermAppl result() {
		return term;
	}

	/**
	 * Reset the visitor state about created terms. Should be called before
	 * every visit so terms created earlier will not affect the future results.
	 */
	public void reset() {
		term = null;
		reloadRequired = false;
	}

	public void visit(OWLClass c) {
		if( c.isOWLThing() )
			term = ATermUtils.TOP;
		else if( c.isOWLNothing() )
			term = ATermUtils.BOTTOM;
		else
			term = ATermUtils.makeTermAppl( c.getURI().toString() );

		if( addAxioms )
			kb.addClass( term );
	}

	public void visit(OWLIndividual ind) {
		if( ind.isAnonymous() )
			term = ATermUtils.makeBnode( ind.getURI().toString() );
		else
			term = ATermUtils.makeTermAppl( ind.getURI().toString() );

		if( addAxioms )
			kb.addIndividual( term );
	}

	public void visit(OWLObjectProperty prop) {
		if ( prop.equals( OWL.topObjectProperty ) ) {
			term = ATermUtils.TOP_OBJECT_PROPERTY;
		}
		else if (prop.equals( OWL.bottomObjectProperty ) ) {
			term = ATermUtils.BOTTOM_OBJECT_PROPERTY;
		}
		else {			
			term = ATermUtils.makeTermAppl( prop.getURI().toString() );
	
			if( addAxioms )
				kb.addObjectProperty( term );
		}
	}

	public void visit(OWLObjectPropertyInverse propInv) {
		propInv.getInverse().accept( this );
		ATermAppl p = term;

		term = ATermUtils.makeInv( p );
	}

	public void visit(OWLDataProperty prop) {
		if ( prop.equals( OWL.topDataProperty ) ) {
			term = ATermUtils.TOP_DATA_PROPERTY;
		}
		else if (prop.equals( OWL.bottomDataProperty ) ) {
			term = ATermUtils.BOTTOM_DATA_PROPERTY;
		}
		else {	
			term = ATermUtils.makeTermAppl( prop.getURI().toString() );
	
			if( addAxioms )
				kb.addDatatypeProperty( term );
		}
	}

	public void visit(OWLTypedConstant constant) {
		String lexicalValue = constant.getLiteral();
		constant.getDataType().accept( this );
		ATerm datatype = term;

		term = ATermUtils.makeTypedLiteral( lexicalValue, datatype.toString() );
	}

	public void visit(OWLUntypedConstant constant) {
		String lexicalValue = constant.getLiteral();
		String lang = constant.getLang();

		if( lang != null )
			term = ATermUtils.makePlainLiteral( lexicalValue, lang );
		else
			term = ATermUtils.makePlainLiteral( lexicalValue );
	}

	public void visit(OWLDataType ocdt) {
		term = ATermUtils.makeTermAppl( ocdt.getURI().toString() );

		kb.addDatatype( term );
	}

	public void visit(OWLObjectIntersectionOf and) {
		Set<OWLDescription> operands = and.getOperands();
		ATerm[] terms = new ATerm[operands.size()];
		int size = 0;
		for( OWLDescription desc : operands ) {
			desc.accept( this );
			terms[size++] = term;
		}
		// create a sorted set of terms so we will have a stable
		// concept creation and removal using this concept will work
		ATermList setOfTerms = size > 0
			? ATermUtils.toSet( terms, size )
			: ATermUtils.EMPTY_LIST;
		term = ATermUtils.makeAnd( setOfTerms );
	}

	public void visit(OWLObjectUnionOf or) {
		Set<OWLDescription> operands = or.getOperands();
		ATerm[] terms = new ATerm[operands.size()];
		int size = 0;
		for( OWLDescription desc : operands ) {
			desc.accept( this );
			terms[size++] = term;
		}
		// create a sorted set of terms so we will have a stable
		// concept creation and removal using this concept will work
		ATermList setOfTerms = size > 0
			? ATermUtils.toSet( terms, size )
			: ATermUtils.EMPTY_LIST;
		term = ATermUtils.makeOr( setOfTerms );
	}

	public void visit(OWLObjectComplementOf not) {
		OWLDescription desc = not.getOperand();
		desc.accept( this );

		term = ATermUtils.makeNot( term );
	}

	public void visit(OWLObjectOneOf enumeration) {
		Set<OWLIndividual> operands = enumeration.getIndividuals();
		ATerm[] terms = new ATerm[operands.size()];
		int size = 0;
		for( OWLIndividual ind : operands ) {
			ind.accept( this );
			terms[size++] = ATermUtils.makeValue( term );
		}
		// create a sorted set of terms so we will have a stable
		// concept creation and removal using this concept will work
		ATermList setOfTerms = size > 0
			? ATermUtils.toSet( terms, size )
			: ATermUtils.EMPTY_LIST;
		term = ATermUtils.makeOr( setOfTerms );
	}

	public void visit(OWLObjectSomeRestriction restriction) {
		restriction.getProperty().accept( this );
		ATerm p = term;
		restriction.getFiller().accept( this );
		ATerm c = term;

		term = ATermUtils.makeSomeValues( p, c );
	}

	public void visit(OWLObjectAllRestriction restriction) {
		restriction.getProperty().accept( this );
		ATerm p = term;
		restriction.getFiller().accept( this );
		ATerm c = term;

		term = ATermUtils.makeAllValues( p, c );
	}

	public void visit(OWLObjectValueRestriction restriction) {
		restriction.getProperty().accept( this );
		ATerm p = term;
		restriction.getValue().accept( this );
		ATermAppl ind = term;

		term = ATermUtils.makeHasValue( p, ind );
	}

	public void visit(OWLObjectExactCardinalityRestriction restriction) {
		addSimpleProperty( restriction.getProperty() );

		restriction.getProperty().accept( this );
		ATerm p = term;
		int n = restriction.getCardinality();
		restriction.getFiller().accept( this );
		ATermAppl desc = term;

		term = ATermUtils.makeCard( p, n, desc );
	}

	public void visit(OWLObjectMaxCardinalityRestriction restriction) {
		addSimpleProperty( restriction.getProperty() );

		restriction.getProperty().accept( this );
		ATerm p = term;
		int n = restriction.getCardinality();
		restriction.getFiller().accept( this );
		ATermAppl desc = term;

		term = ATermUtils.makeMax( p, n, desc );

	}

	public void visit(OWLObjectMinCardinalityRestriction restriction) {
		addSimpleProperty( restriction.getProperty() );

		restriction.getProperty().accept( this );
		ATerm p = term;
		int n = restriction.getCardinality();
		restriction.getFiller().accept( this );
		ATermAppl desc = term;

		term = ATermUtils.makeMin( p, n, desc );
	}

	public void visit(OWLDataExactCardinalityRestriction restriction) {
		restriction.getProperty().accept( this );
		ATerm p = term;
		int n = restriction.getCardinality();
		restriction.getFiller().accept( this );
		ATermAppl desc = term;

		term = ATermUtils.makeCard( p, n, desc );
	}

	public void visit(OWLDataMaxCardinalityRestriction restriction) {
		restriction.getProperty().accept( this );
		ATerm p = term;
		int n = restriction.getCardinality();
		restriction.getFiller().accept( this );
		ATermAppl desc = term;

		term = ATermUtils.makeMax( p, n, desc );
	}

	public void visit(OWLDataMinCardinalityRestriction restriction) {
		restriction.getProperty().accept( this );
		ATerm p = term;
		int n = restriction.getCardinality();
		restriction.getFiller().accept( this );
		ATermAppl desc = term;

		term = ATermUtils.makeMin( p, n, desc );
	}

	public void visit(OWLEquivalentClassesAxiom axiom) {
		Set<OWLDescription> descriptions = axiom.getDescriptions();
		int size = descriptions.size();
		if( size > 1 ) {
			ATermAppl[] terms = new ATermAppl[size];
			int index = 0;
			for( OWLDescription desc : descriptions ) {
				desc.accept( this );
				terms[index++] = term;
			}
			Arrays.sort( terms, 0, size, Comparators.termComparator );

			ATermAppl c1 = terms[0];

			for( int i = 1; i < terms.length; i++ ) {
				ATermAppl c2 = terms[i];

				if( addAxioms ) {
					kb.addEquivalentClass( c1, c2 );
				}
				else {
					// create the equivalence axiom
					ATermAppl sameAxiom = ATermUtils.makeEqClasses( c1, c2 );

					// if removal fails we need to reload
					reloadRequired = !kb.removeAxiom( sameAxiom );
					// if removal is required there is no point to continue
					if( reloadRequired )
						return;
				}
			}
		}
	}

	public void visit(OWLDisjointClassesAxiom axiom) {
		Set<OWLDescription> descriptions = axiom.getDescriptions();
		int size = descriptions.size();
		if( size > 1 ) {
			ATermAppl[] terms = new ATermAppl[size];
			int index = 0;
			for( OWLDescription desc : descriptions ) {
				desc.accept( this );
				terms[index++] = term;
			}

			ATermList list = ATermUtils.toSet( terms, size );
			if( addAxioms ) {
				kb.addDisjointClasses( list );
			}
			else {
				reloadRequired = !kb.removeAxiom( ATermUtils.makeDisjoints( list ) );
			}
		}
	}

	public void visit(OWLSubClassAxiom axiom) {
		axiom.getSubClass().accept( this );
		ATermAppl c1 = term;
		axiom.getSuperClass().accept( this );
		ATermAppl c2 = term;

		if( addAxioms ) {
			kb.addSubClass( c1, c2 );
		}
		else {
			// create the TBox axiom to remove
			ATermAppl subAxiom = ATermUtils.makeSub( c1, c2 );
			// reload is required if remove fails
			reloadRequired = !kb.removeAxiom( subAxiom );
		}
	}

	public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		int size = axiom.getProperties().size();
		OWLObjectPropertyExpression[] props = new OWLObjectPropertyExpression[size];
		axiom.getProperties().toArray( props );
		for( int i = 0; i < size; i++ ) {
			for( int j = i + 1; j < size; j++ ) {
				props[i].accept( this );
				ATermAppl p1 = term;
				props[j].accept( this );
				ATermAppl p2 = term;

				kb.addEquivalentProperty( p1, p2 );
			}
		}
	}

	public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		int size = axiom.getProperties().size();
		OWLDataPropertyExpression[] props = new OWLDataPropertyExpression[size];
		axiom.getProperties().toArray( props );
		for( int i = 0; i < size; i++ ) {
			for( int j = i + 1; j < size; j++ ) {
				props[i].accept( this );
				ATermAppl p1 = term;
				props[j].accept( this );
				ATermAppl p2 = term;

				kb.addEquivalentProperty( p1, p2 );
			}
		}
	}

	public void visit(OWLDifferentIndividualsAxiom axiom) {
		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		if( axiom.getIndividuals().size() == 2 ) {
			Iterator<OWLIndividual> iter = axiom.getIndividuals().iterator();
			iter.next().accept( this );
			ATermAppl i1 = term;
			iter.next().accept( this );
			ATermAppl i2 = term;
			kb.addDifferent( i1, i2 );
		}
		else {
			ATermAppl[] terms = new ATermAppl[axiom.getIndividuals().size()];
			int i = 0;
			for( OWLIndividual ind : axiom.getIndividuals() ) {
				ind.accept( this );
				terms[i++] = term;
			}
			kb.addAllDifferent( ATermUtils.makeList( terms ) );
		}
	}

	public void visit(OWLSameIndividualsAxiom axiom) {
		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		Iterator<OWLIndividual> eqs = axiom.getIndividuals().iterator();
		if( eqs.hasNext() ) {
			eqs.next().accept( this );
			ATermAppl i1 = term;

			while( eqs.hasNext() ) {
				eqs.next().accept( this );
				ATermAppl i2 = term;

				kb.addSame( i1, i2 );
			}
		}
	}

	public void visit(OWLDataOneOf enumeration) {
		ATermList ops = ATermUtils.EMPTY_LIST;
		for( OWLConstant value : enumeration.getValues() ) {
			value.accept( this );
			ops = ops.insert( ATermUtils.makeValue( result() ) );
		}
		term = ATermUtils.makeOr( ops );
	}

	public void visit(OWLDataAllRestriction restriction) {
		restriction.getProperty().accept( this );
		ATerm p = term;
		restriction.getFiller().accept( this );
		ATerm c = term;

		term = ATermUtils.makeAllValues( p, c );
	}

	public void visit(OWLDataSomeRestriction restriction) {
		restriction.getProperty().accept( this );
		ATerm p = term;
		restriction.getFiller().accept( this );
		ATerm c = term;

		term = ATermUtils.makeSomeValues( p, c );
	}

	public void visit(OWLDataValueRestriction restriction) {
		restriction.getProperty().accept( this );
		ATermAppl p = term;
		restriction.getValue().accept( this );
		ATermAppl dv = term;

		term = ATermUtils.makeHasValue( p, dv );
	}

	public void visit(OWLOntology ont) {
		for( OWLAxiom axiom : ont.getAxioms() ) {
			monitor.incrementProgress();

			if( log.isLoggable( Level.FINE ) )
				log.fine( "Load " + axiom );

			axiom.accept( this );
		}
	}

	public void visit(OWLObjectSelfRestriction restriction) {
		addSimpleProperty( restriction.getProperty() );

		restriction.getProperty().accept( this );
		ATermAppl p = term;

		term = ATermUtils.makeSelf( p );
	}

	public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		Object[] disjs = axiom.getProperties().toArray();
		for( int i = 0; i < disjs.length - 1; i++ ) {
			OWLObjectProperty prop1 = (OWLObjectProperty) disjs[i];
			addSimpleProperty( prop1 );
			for( int j = i + 1; j < disjs.length; j++ ) {
				OWLObjectProperty prop2 = (OWLObjectProperty) disjs[j];
				addSimpleProperty( prop2 );
				prop1.accept( this );
				ATermAppl p1 = term;
				prop2.accept( this );
				ATermAppl p2 = term;

				kb.addDisjointProperty( p1, p2 );
			}
		}
	}

	public void visit(OWLDisjointDataPropertiesAxiom axiom) {
		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		Object[] disjs = axiom.getProperties().toArray();
		for( int i = 0; i < disjs.length; i++ ) {
			for( int j = i + 1; j < disjs.length; j++ ) {
				OWLDataProperty desc1 = (OWLDataProperty) disjs[i];
				OWLDataProperty desc2 = (OWLDataProperty) disjs[j];
				desc1.accept( this );
				ATermAppl p1 = term;
				desc2.accept( this );
				ATermAppl p2 = term;

				kb.addDisjointProperty( p1, p2 );
			}
		}
	}

	public void visit(OWLObjectPropertyChainSubPropertyAxiom axiom) {
		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		compositePropertyAxioms.add( getNamedProperty( axiom.getSuperProperty() ), axiom );

		axiom.getSuperProperty().accept( this );
		ATermAppl prop = result();

		List<OWLObjectPropertyExpression> propChain = axiom.getPropertyChain();
		ATermList chain = ATermUtils.EMPTY_LIST;
		for( int i = propChain.size() - 1; i >= 0; i-- ) {
			propChain.get( i ).accept( this );
			chain = chain.insert( result() );
		}

		kb.addSubProperty( chain, prop );
	}

	public void visit(OWLDisjointUnionAxiom axiom) {
		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		axiom.getOWLClass().accept( this );
		ATermAppl c = term;

		ATermList classes = ATermUtils.EMPTY_LIST;
		for( OWLDescription desc : axiom.getDescriptions() ) {
			desc.accept( this );
			classes = classes.insert( result() );
		}

		kb.addDisjointClasses( classes );
		kb.addEquivalentClass( c, ATermUtils.makeOr( classes ) );
	}

	public void visit(OWLDataComplementOf node) {
		node.getDataRange().accept( this );
		term = ATermUtils.makeNot( term );
	}

	public void visit(OWLDataRangeRestriction node) {
		node.getDataRange().accept( this );
		ATermAppl baseDatatype = term;
		
		List<ATermAppl> restrictions = new ArrayList<ATermAppl>();
		for( OWLDataRangeFacetRestriction restr : node.getFacetRestrictions() ) {
			restr.accept( this );

			if( term != null ) {
				restrictions.add( term );
			}
			else {				
				log.warning( "Unrecognized facet " + restr.getFacet() );
				
				return;				
			}
		}
		
		if( restrictions.isEmpty() ) {
			log.warning( "A data range is defined without facet restrictions "
					+ node );
		}
		else {
			term = ATermUtils.makeRestrictedDatatype( baseDatatype, restrictions
					.toArray( new ATermAppl[restrictions.size()] ) );
		}
	}

	public void visit(OWLAntiSymmetricObjectPropertyAxiom axiom) {
		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		addSimpleProperty( axiom.getProperty() );

		axiom.getProperty().accept( this );
		ATermAppl p = term;

		kb.addAsymmetricProperty( p );
	}

	public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		axiom.getProperty().accept( this );
		ATermAppl p = term;

		kb.addReflexiveProperty( p );
	}

	public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		addSimpleProperty( axiom.getProperty() );

		axiom.getProperty().accept( this );
		ATermAppl p = term;

		kb.addFunctionalProperty( p );
	}

	public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		axiom.getSubject().accept( this );
		ATermAppl s = term;
		axiom.getProperty().accept( this );
		ATermAppl p = term;
		axiom.getObject().accept( this );
		ATermAppl o = term;

		kb.addNegatedPropertyValue( p, s, o );
	}

	public void visit(OWLDataPropertyDomainAxiom axiom) {
		axiom.getProperty().accept( this );
		ATermAppl p = term;
		axiom.getDomain().accept( this );
		ATermAppl c = term;

		if( addAxioms )
			kb.addDomain( p, c );
		else
			reloadRequired = !kb.removeDomain( p, c );
	}

	public void visit(OWLImportsDeclaration axiom) {
		if( log.isLoggable( Level.FINE ) )
			log.fine( "Ignoring imports declaration: " + axiom );
	}

	public void visit(OWLAxiomAnnotationAxiom axiom) {
		if( log.isLoggable( Level.FINE ) )
			log.fine( "Ignoring axiom annotation: " + axiom );
	}

	public void visit(OWLObjectPropertyDomainAxiom axiom) {
		axiom.getProperty().accept( this );
		ATermAppl p = term;
		axiom.getDomain().accept( this );
		ATermAppl c = term;

		if( addAxioms )
			kb.addDomain( p, c );
		else
			reloadRequired = !kb.removeDomain( p, c );
	}

	public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		axiom.getSubject().accept( this );
		ATermAppl s = term;
		axiom.getProperty().accept( this );
		ATermAppl p = term;
		axiom.getObject().accept( this );
		ATermAppl o = term;

		kb.addNegatedPropertyValue( p, s, o );
	}

	public void visit(OWLObjectPropertyRangeAxiom axiom) {
		axiom.getProperty().accept( this );
		ATermAppl p = term;
		axiom.getRange().accept( this );
		ATermAppl c = term;

		if( addAxioms )
			kb.addRange( p, c );
		else
			reloadRequired = !kb.removeRange( p, c );
	}

	public void visit(OWLObjectPropertyAssertionAxiom axiom) {
		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		axiom.getSubject().accept( this );
		ATermAppl subj = term;
		axiom.getProperty().accept( this );
		ATermAppl pred = term;
		axiom.getObject().accept( this );
		ATermAppl obj = term;

		kb.addPropertyValue( pred, subj, obj );
	}

	public void visit(OWLObjectSubPropertyAxiom axiom) {
		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		axiom.getSubProperty().accept( this );
		ATermAppl sub = term;
		axiom.getSuperProperty().accept( this );
		ATermAppl sup = term;

		kb.addSubProperty( sub, sup );
	}

	public void visit(OWLDeclarationAxiom axiom) {
		axiom.getEntity().accept( this );
	}

	public void visit(OWLEntityAnnotationAxiom axiom) {
		axiom.getSubject().accept( this );
		ATermAppl s = term;
		
		URI uri = axiom.getAnnotation().getAnnotationURI();
		ATermAppl p = ATermUtils.makeTermAppl( uri.toASCIIString() );
		kb.addAnnotationProperty( p );

		axiom.getAnnotation().accept( this );
		ATermAppl o = term;

		kb.addAnnotation( s, p, o );
	}

	public void visit(OWLOntologyAnnotationAxiom axiom) {
		if( log.isLoggable( Level.FINE ) )
			log.fine( "Ignoring ontology annotation: " + axiom );
	}

	public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		axiom.getProperty().accept( this );
		ATermAppl p = term;

		kb.addSymmetricProperty( p );
	}

	public void visit(OWLDataPropertyRangeAxiom axiom) {
		axiom.getProperty().accept( this );
		ATermAppl p = term;
		axiom.getRange().accept( this );
		ATermAppl c = term;

		if( addAxioms )
			kb.addRange( p, c );
		else
			reloadRequired = !kb.removeRange( p, c );
	}

	public void visit(OWLFunctionalDataPropertyAxiom axiom) {
		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		axiom.getProperty().accept( this );
		ATermAppl p = term;

		kb.addFunctionalProperty( p );
	}

	public void visit(OWLClassAssertionAxiom axiom) {
		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		axiom.getIndividual().accept( this );
		ATermAppl ind = term;
		axiom.getDescription().accept( this );
		ATermAppl c = term;

		kb.addType( ind, c );
	}

	public void visit(OWLDataPropertyAssertionAxiom axiom) {
		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		axiom.getSubject().accept( this );
		ATermAppl subj = term;
		axiom.getProperty().accept( this );
		ATermAppl pred = term;
		axiom.getObject().accept( this );
		ATermAppl obj = term;

		kb.addPropertyValue( pred, subj, obj );
	}

	public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		compositePropertyAxioms.add( getNamedProperty( axiom.getProperty() ), axiom );

		axiom.getProperty().accept( this );
		ATermAppl p = term;

		kb.addTransitiveProperty( p );
	}

	public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		addSimpleProperty( axiom.getProperty() );

		axiom.getProperty().accept( this );
		ATermAppl p = term;

		kb.addIrreflexiveProperty( p );
	}

	public void visit(OWLDataSubPropertyAxiom axiom) {
		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		axiom.getSubProperty().accept( this );
		ATermAppl p1 = term;
		axiom.getSuperProperty().accept( this );
		ATermAppl p2 = term;

		kb.addSubProperty( p1, p2 );
	}

	public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		addSimpleProperty( axiom.getProperty() );

		axiom.getProperty().accept( this );
		ATermAppl p = term;

		kb.addInverseFunctionalProperty( p );
	}

	public void visit(OWLInverseObjectPropertiesAxiom axiom) {
		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		axiom.getFirstProperty().accept( this );
		ATermAppl p1 = term;
		axiom.getSecondProperty().accept( this );
		ATermAppl p2 = term;

		kb.addInverseProperty( p1, p2 );
	}

	public void visit(OWLDataRangeFacetRestriction node) {
		Facet facet = Facet.Registry.get( ATermUtils.makeTermAppl( node.getFacet().getURI().toString() ) );	

		if( facet != null ) {
			OWLConstant facetValue = node.getFacetValue();
			facetValue.accept( this );			

			term = ATermUtils.makeFacetRestriction( facet.getName(), term );
		}
	}

	public void visit(OWLObjectAnnotation a) {
		a.getAnnotationValue().accept( this );
		// if( log.isLoggable( Level.FINE ) )
		// log.fine( "Ignoring object annotation: " + annotation );
	}

	public void visit(OWLConstantAnnotation a) {
		a.getAnnotationValue().accept( this );
		// if( log.isLoggable( Level.FINE ) )
		// log.fine( "Ignoring constant annotation: " + annotation );
	}

	public void visit(SWRLRule rule) {
		if( !PelletOptions.DL_SAFE_RULES )
			return;

		if( !addAxioms ) {
			reloadRequired = true;
			return;
		}

		List<RuleAtom> head = parseAtomList( rule.getHead() );
		List<RuleAtom> body = parseAtomList( rule.getBody() );

		if( head == null || body == null ) {
			addUnsupportedAxiom( rule );

			return;
		}
		
		URI uri = rule.getURI();
		ATermAppl name = uri == null
			? null
			: rule.isAnonymous() 
				? ATermUtils.makeBnode( uri.toString() )
				: ATermUtils.makeTermAppl( uri.toString() );

		Rule pelletRule = new Rule( name, head, body );
		kb.addRule( pelletRule );
	}

	@SuppressWarnings("unchecked")
	private List<RuleAtom> parseAtomList(Set<SWRLAtom> atomList) {
		List<RuleAtom> atoms = new ArrayList<RuleAtom>();

		for( SWRLAtom atom : atomList ) {
			atom.accept( this );

			if( swrlAtom == null )
				return null;

			atoms.add( swrlAtom );
		}

		return atoms;
	}

	public void visit(SWRLClassAtom atom) {
		OWLDescription c = atom.getPredicate();

		SWRLAtomIObject v = atom.getArgument();
		v.accept( this );

		AtomIObject subj = swrlIObject;

		c.accept( this );
		swrlAtom = new ClassAtom( term, subj );
	}

	public void visit(SWRLDataRangeAtom atom) {
		// set the term field
		atom.getPredicate().accept( this );

		atom.getArgument().accept( this );
		swrlAtom = new DataRangeAtom( term, swrlDObject );
	}

	public void visit(SWRLObjectPropertyAtom atom) {
		if( atom.getPredicate().isAnonymous() ) {
			swrlAtom = null;
			return;
		}

		atom.getFirstArgument().accept( this );
		AtomIObject subj = swrlIObject;

		atom.getSecondArgument().accept( this );
		AtomIObject obj = swrlIObject;

		atom.getPredicate().accept( this );
		swrlAtom = new IndividualPropertyAtom( term, subj, obj );

	}

	public void visit(SWRLDataValuedPropertyAtom atom) {
		if( atom.getPredicate().isAnonymous() ) {
			swrlAtom = null;
			return;
		}

		atom.getFirstArgument().accept( this );
		AtomIObject subj = swrlIObject;

		atom.getSecondArgument().accept( this );
		AtomDObject obj = swrlDObject;

		atom.getPredicate().accept( this );
		swrlAtom = new DatavaluedPropertyAtom( term, subj, obj );
	}

	public void visit(SWRLSameAsAtom atom) {
		atom.getFirstArgument().accept( this );
		AtomIObject subj = swrlIObject;

		atom.getSecondArgument().accept( this );
		AtomIObject obj = swrlIObject;

		swrlAtom = new SameIndividualAtom( subj, obj );
	}

	public void visit(SWRLDifferentFromAtom atom) {
		atom.getFirstArgument().accept( this );
		AtomIObject subj = swrlIObject;

		atom.getSecondArgument().accept( this );
		AtomIObject obj = swrlIObject;

		swrlAtom = new DifferentIndividualsAtom( subj, obj );
	}

	public void visit(SWRLBuiltInAtom atom) {
		List<AtomDObject> arguments = new ArrayList<AtomDObject>( atom.getAllArguments().size() );
		for( SWRLAtomDObject swrlArg : atom.getArguments() ) {
			swrlArg.accept( this );
			arguments.add( swrlDObject );
		}
		swrlAtom = new BuiltInAtom( atom.getPredicate().getURI().toString(), arguments );
	}

	public void visit(SWRLAtomDVariable dvar) {
		swrlDObject = new AtomDVariable( dvar.getURI().toString() );
	}

	public void visit(SWRLAtomIVariable ivar) {
		swrlIObject = new AtomIVariable( ivar.getURI().toString() );
	}

	public void visit(SWRLAtomIndividualObject iobj) {
		iobj.getIndividual().accept( this );
		swrlIObject = new AtomIConstant( term );
	}

	public void visit(SWRLAtomConstantObject cons) {
		cons.getConstant().accept( this );
		swrlDObject = new AtomDConstant( term );
	}

	public ProgressMonitor getMonitor() {
		return monitor;
	}

	public void setMonitor(ProgressMonitor monitor) {
		this.monitor = monitor;
	}

}
