// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tbox.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

import aterm.AFun;
import aterm.ATermAppl;
import aterm.ATermList;

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
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;

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
public class RuleAbsorber {
	public static final Logger						log	= TgBox.log;
	
	public static final Map<ATermAppl,String> FACETS;
	static {
		FACETS = new HashMap<ATermAppl,String>();
		FACETS.put( Facet.XSD.MIN_INCLUSIVE.getName(), Namespaces.SWRLB + "greaterThanOrEqual" );
		FACETS.put( Facet.XSD.MIN_EXCLUSIVE.getName(), Namespaces.SWRLB + "greaterThan" );
		FACETS.put( Facet.XSD.MAX_INCLUSIVE.getName(), Namespaces.SWRLB + "lessThanOrEqual" );
		FACETS.put( Facet.XSD.MAX_EXCLUSIVE.getName(), Namespaces.SWRLB + "lessThan" );
	}
	
	private KnowledgeBase kb;
	private TuBox Tu;
	
	public RuleAbsorber(TBoxExpImpl tbox) {
		this.kb = tbox.getKB();
		this.Tu = tbox.Tu;
	}

	public boolean absorbRule(Set<ATermAppl> set, Set<ATermAppl> explanation) {
		int propertyAtoms = 0;
		int primitiveClassAtoms = 0;
		ATermAppl head = null;
		for( ATermAppl term : set ) {
			if( ATermUtils.isPrimitive( term ) ) {
				TermDefinition td = Tu.getTD( term );
				if( td == null || td.getEqClassAxioms().isEmpty() )
					primitiveClassAtoms++;
			}
			else if( ATermUtils.isSomeValues( term ) ) {
				propertyAtoms++;
			}
			else if( ATermUtils.isNot( term ) ) {
				head = term;
			}
		}
		
		if( head  == null || (propertyAtoms == 0 && primitiveClassAtoms < 2) ) {
			return false;
		}
		
		set.remove( head );
		
		AtomIObject var = new AtomIVariable( "var");
		int varCount = 0;
		List<RuleAtom> bodyAtoms  = new ArrayList<RuleAtom>();
		for( ATermAppl term : set ) {
			varCount = processClass( var, term, bodyAtoms, varCount );
		}
		
		List<RuleAtom> headAtoms  = new ArrayList<RuleAtom>();
		processClass( var, ATermUtils.negate( head ), headAtoms, 1 );
		
		Rule rule = new Rule(headAtoms, bodyAtoms, explanation);
		kb.addRule( rule );
		

		if( log.isLoggable( Level.FINE ) )
			log.fine( "Absorbed rule: " + rule );
		
		return true;
	}
	
	protected int processClass(AtomIObject var, ATermAppl c, List<RuleAtom> atoms, int varCount) {
		AFun afun = c.getAFun();
		if( afun.equals( ATermUtils.ANDFUN ) ) {
			for( ATermList list = (ATermList) c.getArgument( 0 ); !list.isEmpty(); list = list.getNext() ) {
				ATermAppl conjunct = (ATermAppl) list.getFirst();
				varCount = processClass( var, conjunct, atoms, varCount );
			}
		}
		else if( afun.equals( ATermUtils.SOMEFUN ) ) {
			ATermAppl p = (ATermAppl) c.getArgument( 0 );
			ATermAppl filler = (ATermAppl) c.getArgument( 1 );
			
			if( filler.getAFun().equals( ATermUtils.VALUEFUN ) ) {
				ATermAppl nominal = (ATermAppl) filler.getArgument( 0 );
				if( kb.isDatatypeProperty( p ) ) {
					AtomDConstant arg = new AtomDConstant( nominal );
					RuleAtom atom = new DatavaluedPropertyAtom( p, var, arg );
					atoms.add(atom);
				}
				else {
					AtomIConstant arg = new AtomIConstant( nominal );
					RuleAtom atom = new IndividualPropertyAtom( p, var, arg );
					atoms.add(atom);
				}
			}
			else { 
				varCount++;
				if( kb.isDatatypeProperty( p ) ) {
					AtomDObject newVar = new AtomDVariable( "var" + varCount );
					RuleAtom atom = new DatavaluedPropertyAtom( p, var, newVar );
					atoms.add(atom);
					processDatatype( newVar, filler, atoms );
				}
				else{
					AtomIObject newVar = new AtomIVariable( "var" + varCount );
					RuleAtom atom = new IndividualPropertyAtom( p, var, newVar );
					atoms.add(atom);
					varCount = processClass( newVar, filler, atoms, varCount );
				}
			}
		}
		else if( !c.equals( ATermUtils.TOP ) ) {
			atoms.add( new ClassAtom( c, var ) );
		}
		
		return varCount;
	}
	
	protected void processDatatype(AtomDObject var, ATermAppl c, List<RuleAtom> atoms) {
		AFun afun = c.getAFun();
		if( afun.equals( ATermUtils.ANDFUN ) ) {
			for( ATermList list = (ATermList) c.getArgument( 0 ); !list.isEmpty(); list = list.getNext() ) {
				ATermAppl conjunct = (ATermAppl) list.getFirst();
				processDatatype( var, conjunct, atoms );
			}
		}
		else if( afun.equals( ATermUtils.RESTRDATATYPEFUN ) ) {
			ATermAppl baseDatatype = (ATermAppl) c.getArgument( 0 );
			
			atoms.add( new DataRangeAtom( baseDatatype, var ) );
			
			for( ATermList list = (ATermList) c.getArgument( 1 ); !list.isEmpty(); list = list.getNext() ) {
				ATermAppl facetRestriction = (ATermAppl) list.getFirst();				
				ATermAppl facet = (ATermAppl) facetRestriction.getArgument( 0 );				
				String builtin = FACETS.get( facet );
				if( builtin != null ) {					
					ATermAppl value = (ATermAppl) facetRestriction.getArgument( 1 );
					atoms.add( new BuiltInAtom( builtin, var, new AtomDConstant( value ) ) );
				}
				else {
					atoms.add( new DataRangeAtom( c, var ) );
					return;
				}
			}			
		}
		else {
			atoms.add( new DataRangeAtom( c, var ) );
		}
	}
	
}
