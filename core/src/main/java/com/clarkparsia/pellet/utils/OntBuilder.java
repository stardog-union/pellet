package com.clarkparsia.pellet.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.output.ATermBaseVisitor;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.AFun;
import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;

import com.clarkparsia.pellet.rules.model.AtomDConstant;
import com.clarkparsia.pellet.rules.model.AtomDObject;
import com.clarkparsia.pellet.rules.model.AtomDVariable;
import com.clarkparsia.pellet.rules.model.AtomIConstant;
import com.clarkparsia.pellet.rules.model.AtomIObject;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;
import com.clarkparsia.pellet.rules.model.ClassAtom;
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
 * Description: Creates a KnowledgeBase from ATerm axioms.
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
public class OntBuilder {
	private class DefinitionVisitor extends ATermBaseVisitor {
		public void visitAll(ATermAppl term) {
			visitQR( term );
		}

		public void visitAnd(ATermAppl term) {
			visitList( (ATermList) term.getArgument( 0 ) );
		}
		
		public void visitCard(ATermAppl term) {
			visitQCR( term );
		}

		public void visitHasValue(ATermAppl term) {
			visitQR( term );
		}

		public void visitLiteral(ATermAppl term) {
			return;
		}

		public void visitMax(ATermAppl term) {
			visitQCR( term );
		}

		public void visitMin(ATermAppl term) {
			visitQCR( term );
		}

		public void visitNot(ATermAppl term) {
			this.visit( (ATermAppl) term.getArgument( 0 ) );
		}

		public void visitOneOf(ATermAppl term) {
			visitList( (ATermList) term.getArgument( 0 ) );
		}

		public void visitOr(ATermAppl term) {
			visitList( (ATermList) term.getArgument( 0 ) );
		}

		private void visitQCR(ATermAppl term) {
			ATermAppl p = (ATermAppl) term.getArgument( 0 );
			ATermAppl q = (ATermAppl) term.getArgument( 2 );
			
			visitRestr(p ,q );			
		}

		private void visitQR(ATermAppl term) {
			ATermAppl p = (ATermAppl) term.getArgument( 0 );
			ATermAppl q = (ATermAppl) term.getArgument( 1 );
			
			visitRestr(p ,q );			
		}

		private void visitRestr(ATermAppl p, ATermAppl q) {
			if( originalKB.isObjectProperty( p ) ) {
				kb.addObjectProperty( p );
				visit( q );
			}
			else {
				kb.addDatatypeProperty( p );
			}			
		}

		public void visitSelf(ATermAppl term) {
			kb.addObjectProperty( term.getArgument( 0 ) );
		}

		public void visitSome(ATermAppl term) {
			visitQR( term );
		}

		public void visitTerm(ATermAppl term) {
			kb.addClass( term );
		}

		public void visitValue(ATermAppl term) {
			ATermAppl nominal = (ATermAppl) term.getArgument( 0 );
			if( !ATermUtils.isLiteral( nominal ) )
				kb.addIndividual( nominal );
		}
		
		public void visitInverse(ATermAppl term) {
			ATermAppl p = (ATermAppl) term.getArgument( 0 );
			if( ATermUtils.isPrimitive( p ) )
				kb.addObjectProperty( p );
			else
				visitInverse( p );
		}

		public void visitRestrictedDatatype(ATermAppl dt) {
			// do nothing
		}

	}
	private KnowledgeBase	kb;
	private KnowledgeBase	originalKB;
	
	private DefinitionVisitor defVisitor = new DefinitionVisitor();

	public OntBuilder(KnowledgeBase originalKB) {
		this.originalKB = originalKB;
	}

	public void add(ATermAppl axiom) {
		AFun afun = axiom.getAFun();
		if( afun.equals( ATermUtils.EQCLASSFUN ) ) {				
			ATermAppl c1 = (ATermAppl) axiom.getArgument( 0 );
			ATermAppl c2 = (ATermAppl) axiom.getArgument( 1 );

			defineClass( c1 );
			defineClass( c2 );
			kb.addEquivalentClass( c1, c2 );
		}
		else if( afun.equals( ATermUtils.SUBFUN ) ) {
			ATermAppl c1 = (ATermAppl) axiom.getArgument( 0 );
			ATermAppl c2 = (ATermAppl) axiom.getArgument( 1 );

			defineClass( c1 );
			defineClass( c2 );
			kb.addSubClass( c1, c2 );
		}
		else if( afun.equals( ATermUtils.DISJOINTSFUN ) ) {
			ATermList concepts = (ATermList) axiom.getArgument( 0 );
			
			for( ATermList l = concepts; !l.isEmpty(); l = l.getNext() ) 
				defineClass( (ATermAppl) l.getFirst() );						
			kb.addDisjointClasses( concepts );
		}
		else if( afun.equals( ATermUtils.DISJOINTFUN ) ) {
			ATermAppl c1 = (ATermAppl) axiom.getArgument( 0 );
			ATermAppl c2 = (ATermAppl) axiom.getArgument( 1 );

			defineClass( c1 );
			defineClass( c2 );
			kb.addDisjointClass( c1, c2 );
		}
		else if( afun.equals( ATermUtils.DISJOINTPROPSFUN ) ) {
			ATermList props = (ATermList) axiom.getArgument( 0 );
			
			for( ATermList l = props; !l.isEmpty(); l = l.getNext() ) 
				defineProperty( l.getFirst() );
			kb.addDisjointProperties( props );
		}
		else if( afun.equals( ATermUtils.DISJOINTPROPFUN ) ) {
			ATermAppl p1 = (ATermAppl) axiom.getArgument( 0 );
			ATermAppl p2 = (ATermAppl) axiom.getArgument( 1 );

			defineProperty( p1 );
			defineProperty( p2 );
			kb.addDisjointProperty( p1, p2 );
		}
		else if( afun.equals( ATermUtils.SUBPROPFUN ) ) {
			ATerm p1 = axiom.getArgument( 0 );
			ATermAppl p2 = (ATermAppl) axiom.getArgument( 1 );

			defineProperty( p1 );
			defineProperty( p2 );
			kb.addSubProperty( p1, p2 );
		}
		else if( afun.equals( ATermUtils.EQPROPFUN ) ) {
			ATermAppl p1 = (ATermAppl) axiom.getArgument( 0 );
			ATermAppl p2 = (ATermAppl) axiom.getArgument( 1 );

			defineProperty( p1 );
			defineProperty( p2 );
			kb.addEquivalentProperty( p1, p2 );
		}
		else if( afun.equals( ATermUtils.DOMAINFUN ) ) {
			ATermAppl p = (ATermAppl) axiom.getArgument( 0 );
			ATermAppl c = (ATermAppl) axiom.getArgument( 1 );

			defineProperty( p );
			defineClass( c );
			kb.addDomain( p, c );
		}
		else if( afun.equals( ATermUtils.RANGEFUN ) ) {
			ATermAppl p = (ATermAppl) axiom.getArgument( 0 );
			ATermAppl c = (ATermAppl) axiom.getArgument( 1 );

			defineProperty( p );
			defineClass( c );
			kb.addRange( p, c );
		}
		else if( afun.equals( ATermUtils.INVPROPFUN ) ) {
			ATermAppl p1 = (ATermAppl) axiom.getArgument( 0 );
			ATermAppl p2 = (ATermAppl) axiom.getArgument( 1 );

			kb.addObjectProperty( p1 );
			kb.addObjectProperty( p2 );
			kb.addInverseProperty( p1, p2 );
		}
		else if( afun.equals( ATermUtils.TRANSITIVEFUN ) ) {
			ATermAppl p = (ATermAppl) axiom.getArgument( 0 );

			kb.addObjectProperty( p );
			kb.addTransitiveProperty( p );
		}
		else if( afun.equals( ATermUtils.FUNCTIONALFUN ) ) {
			ATermAppl p = (ATermAppl) axiom.getArgument( 0 );

			defineProperty( p );
			kb.addFunctionalProperty( p );
		}
		else if( afun.equals( ATermUtils.INVFUNCTIONALFUN ) ) {
			ATermAppl p = (ATermAppl) axiom.getArgument( 0 );

			kb.addObjectProperty( p );
			kb.addInverseFunctionalProperty( p );
		}
		else if( afun.equals( ATermUtils.SYMMETRICFUN ) ) {
			ATermAppl p = (ATermAppl) axiom.getArgument( 0 );

			kb.addObjectProperty( p );
			kb.addSymmetricProperty( p );
		}
		else if( afun.equals( ATermUtils.ASYMMETRICFUN ) ) {
			ATermAppl p = (ATermAppl) axiom.getArgument( 0 );

			kb.addObjectProperty( p );
			kb.addAsymmetricProperty( p );
		}
		else if( afun.equals( ATermUtils.REFLEXIVEFUN ) ) {
			ATermAppl p = (ATermAppl) axiom.getArgument( 0 );

			kb.addObjectProperty( p );
			kb.addReflexiveProperty( p );
		}
		else if( afun.equals( ATermUtils.IRREFLEXIVEFUN ) ) {
			ATermAppl p = (ATermAppl) axiom.getArgument( 0 );

			kb.addObjectProperty( p );
			kb.addIrreflexiveProperty( p );	
		}
		else if( afun.equals( ATermUtils.TYPEFUN ) ) {
			ATermAppl ind = (ATermAppl) axiom.getArgument( 0 );
			ATermAppl cls = (ATermAppl) axiom.getArgument( 1 );

			kb.addIndividual( ind );
			defineClass( cls );
			kb.addType( ind, cls );
		}
		else if( afun.equals( ATermUtils.PROPFUN ) ) {
			ATermAppl p = (ATermAppl) axiom.getArgument( 0 );
			ATermAppl s = (ATermAppl) axiom.getArgument( 1 );
			ATermAppl o = (ATermAppl) axiom.getArgument( 2 );

			kb.addIndividual( s );
			if( ATermUtils.isLiteral( o ) ) {
				kb.addDatatypeProperty( p );
			}
			else {
				kb.addObjectProperty( p );
				kb.addIndividual( o );
			}
			kb.addPropertyValue( p, s, o );
		}
		else if( afun.equals( ATermUtils.NOTFUN )
				&& ((ATermAppl) axiom.getArgument( 0 )).getAFun().equals( ATermUtils.PROPFUN ) ) {
			axiom = (ATermAppl) axiom.getArgument( 0 );

			ATermAppl p = (ATermAppl) axiom.getArgument( 0 );
			ATermAppl s = (ATermAppl) axiom.getArgument( 1 );
			ATermAppl o = (ATermAppl) axiom.getArgument( 2 );
			
			kb.addIndividual( s );
			if( ATermUtils.isLiteral( o ) ) {
				kb.addDatatypeProperty( p );
			}
			else {
				kb.addObjectProperty( p );
				kb.addIndividual( o );
			}
			kb.addNegatedPropertyValue( p, s, o );			
		}
		else if( afun.equals( ATermUtils.SAMEASFUN ) ) {
			ATermAppl ind1 = (ATermAppl) axiom.getArgument( 0 );
			ATermAppl ind2 = (ATermAppl) axiom.getArgument( 1 );

			kb.addIndividual( ind1 );
			kb.addIndividual( ind2 );
			kb.addSame( ind1, ind2 );
		}
		else if( afun.equals( ATermUtils.DIFFERENTFUN ) ) {
			ATermAppl ind1 = (ATermAppl) axiom.getArgument( 0 );
			ATermAppl ind2 = (ATermAppl) axiom.getArgument( 1 );

			kb.addIndividual( ind1 );
			kb.addIndividual( ind2 );
			kb.addDifferent( ind1, ind2 );
		}
		else if( afun.equals( ATermUtils.ALLDIFFERENTFUN ) ) {
			ATermList inds = (ATermList) axiom.getArgument( 0 );
			
			for( ATermList l = inds; !l.isEmpty(); l = l.getNext() ) 
				kb.addIndividual( (ATermAppl) l.getFirst() );
			kb.addAllDifferent( inds );
		}
		else if( afun.equals( ATermUtils.RULEFUN ) ) {
			Set<RuleAtom> antecedent = new HashSet<RuleAtom>(); // Body
			Set<RuleAtom> consequent = new HashSet<RuleAtom>(); // Head

			ATermList head = (ATermList) axiom.getArgument( 1 );
			ATermList body = (ATermList) axiom.getArgument( 2 );

			for( ; !body.isEmpty(); body = body.getNext() )
				antecedent.add( convertRuleAtom( (ATermAppl) body.getFirst() ) );

			for( ; !head.isEmpty(); head = head.getNext() )
				consequent.add( convertRuleAtom( (ATermAppl) head.getFirst() ) );

			if( !antecedent.contains( null ) && !consequent.contains( null ) ) {
				ATermAppl name = (ATermAppl) axiom.getArgument( 0 );
				Rule rule = new Rule( name, consequent, antecedent );
				kb.addRule( rule );
			}
		}
		else {
			throw new InternalReasonerException( "Unknown axiom " + axiom );
		}
	}
	

	private RuleAtom convertRuleAtom(ATermAppl term) {
		RuleAtom atom = null;

		if( term.getAFun().equals( ATermUtils.TYPEFUN ) ) {
			ATermAppl i = (ATermAppl) term.getArgument( 0 );
			AtomIObject io = convertAtomIObject( i );
			ATermAppl c = (ATermAppl) term.getArgument( 1 );
			
			defineClass( c );
			
			atom = new ClassAtom( c, io );
		}
		else if( term.getAFun().equals( ATermUtils.PROPFUN ) ) {
			ATermAppl p = (ATermAppl) term.getArgument( 0 );
			ATermAppl i1 = (ATermAppl) term.getArgument( 1 );
			ATermAppl i2 = (ATermAppl) term.getArgument( 2 );
			AtomIObject io1 = convertAtomIObject( i1 );
			
			defineProperty( p );

			if( originalKB.isObjectProperty( p ) ) {
				kb.addObjectProperty( p );
				AtomIObject io2 = convertAtomIObject( i2 );
				atom = new IndividualPropertyAtom( p, io1, io2 );
			}
			else if( originalKB.isDatatypeProperty( p ) ) {
				kb.addDatatypeProperty( p );
				AtomDObject do2 = convertAtomDObject( i2 );
				atom = new DatavaluedPropertyAtom( p, io1, do2 );
			}
			else {
				throw new InternalReasonerException( "Unknown property " + p );
			}
		}
		else if( term.getAFun().equals( ATermUtils.SAMEASFUN ) ) {
			ATermAppl i1 = (ATermAppl) term.getArgument( 0 );
			ATermAppl i2 = (ATermAppl) term.getArgument( 1 );
			AtomIObject io1 = convertAtomIObject( i1 );
			AtomIObject io2 = convertAtomIObject( i2 );
			
			atom = new SameIndividualAtom( io1, io2 );			
		}
		else if( term.getAFun().equals( ATermUtils.DIFFERENTFUN ) ) {
			ATermAppl i1 = (ATermAppl) term.getArgument( 0 );
			ATermAppl i2 = (ATermAppl) term.getArgument( 1 );
			AtomIObject io1 = convertAtomIObject( i1 );
			AtomIObject io2 = convertAtomIObject( i2 );
			
			atom = new DifferentIndividualsAtom( io1, io2 );			
		}
		else if( term.getAFun().equals( ATermUtils.BUILTINFUN ) ) {
			ATermList args = (ATermList) term.getArgument( 0 );
			ATermAppl builtin = (ATermAppl) args.getFirst();
			List<AtomDObject> list = new ArrayList<AtomDObject>();
			for( args = args.getNext(); !args.isEmpty(); args = args.getNext() ) {
				ATermAppl arg = (ATermAppl) args.getFirst();
				list.add( convertAtomDObject( arg ) );
			}
			
			atom = new BuiltInAtom( builtin.toString(), list );			
		}
		else {
			throw new InternalReasonerException( "Unknown rule atom " + term );
		}

		return atom;
	}

	private AtomIObject convertAtomIObject(ATermAppl t) {
		if( ATermUtils.isVar( t ) )
			return new AtomIVariable( ((ATermAppl) t.getArgument( 0 )).getName() );
		else if( kb.isIndividual( t ) )
			return new AtomIConstant( t );
		else if( ATermUtils.isAnon( t ) )
			return new AtomIConstant( t );

		throw new InternalReasonerException( "Unrecognized term: " + t );
	}

	private AtomDObject convertAtomDObject(ATermAppl t) {
		if( ATermUtils.isVar( t ) )
			return new AtomDVariable( ((ATermAppl) t.getArgument( 0 )).getName() );
		else if( ATermUtils.isLiteral( t ) )
			return new AtomDConstant( t );		

		throw new InternalReasonerException( "Unrecognized term: " + t );
	}	

	public KnowledgeBase build(Set<ATermAppl> axioms) {
		reset();

		for( ATermAppl axiom : axioms ) {
			add( axiom );
		}

		return kb;
	}
	
	private void defineClass(ATermAppl cls) {
		defVisitor.visit( cls );
	}
	
	private void defineProperty(ATerm p) {
		if( p instanceof ATermList ) {						
			for( ATermList l = (ATermList) p; !l.isEmpty(); l = l.getNext() ) { 
				ATermAppl r = (ATermAppl) l.getFirst();
				defineProperty( r );
			}
		}
		else if( ATermUtils.isInv( (ATermAppl) p ) ) {
			kb.addObjectProperty( ((ATermAppl) p).getArgument( 0 ) );
		}
		else if( originalKB.isDatatypeProperty( p ) ) {
			kb.addDatatypeProperty( p );
		}
		else {
			kb.addObjectProperty( p );
		}
	}
	
	public void reset() {
		kb = new KnowledgeBase();
	}
}
