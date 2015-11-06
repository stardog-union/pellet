// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.exceptions.UnsupportedFeatureException;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.KBOperation;
import org.mindswap.pellet.utils.SizeEstimate;

import aterm.ATermAppl;

import com.clarkparsia.pellet.sparqldl.model.Core;
import com.clarkparsia.pellet.sparqldl.model.NotKnownQueryAtom;
import com.clarkparsia.pellet.sparqldl.model.QueryAtom;
import com.clarkparsia.pellet.sparqldl.model.UnionQueryAtom;

/**
 * <p>
 * Title: AtomCostImpl
 * </p>
 * <p>
 * Description: Computes the cost estimate for given atom.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Petr Kremen
 */
public class QueryCost {
	private double			staticCost;

	private double			branchCount;
	
	private KnowledgeBase	kb;

	private SizeEstimate	estimate;

	public QueryCost(KnowledgeBase kb) {
		this.kb = kb;
		this.estimate = kb.getSizeEstimate();
	}


	public double estimate(List<QueryAtom> atoms) {
		return estimate( atoms, new HashSet<ATermAppl>() );
	}

	public double estimate(List<QueryAtom> atoms, Collection<ATermAppl> bound) {
		double totalStaticCount = 1.0;
		double totalBranchCount = 1.0;

		branchCount = 1;
		staticCost = 1.0;

		int n = atoms.size();
		
		Set<ATermAppl> lastBound = new HashSet<ATermAppl>( bound );
		List<Set<ATermAppl>> boundList = new ArrayList<Set<ATermAppl>>(n);
		for( int i = 0; i < n; i++ ) {
			QueryAtom atom = atoms.get( i );
			
			boundList.add( lastBound );
			
			lastBound = new HashSet<ATermAppl>( lastBound );
			lastBound.addAll( atom.getArguments() );	
		}
		
		
		for( int i = n - 1; i >= 0; i-- ) {
			QueryAtom atom = atoms.get( i );

			estimate( atom, boundList.get( i ) );

			totalBranchCount *= branchCount;
			totalStaticCount = staticCost + branchCount * totalStaticCount;
		}

		staticCost = totalStaticCount;
		branchCount = totalBranchCount;
		
		return staticCost;
	}
	
	public double estimate(final QueryAtom atom) {
		return estimate( atom, new HashSet<ATermAppl>() );
	}
	
	public double estimate(final QueryAtom atom, final Collection<ATermAppl> bound) {
		boolean direct = false;
		boolean strict = false;

		List<ATermAppl> arguments = atom.getArguments();
		for( ATermAppl a : arguments ) {
			if( isConstant( a ) ) {
				bound.add( a );
			}
		}

		switch ( atom.getPredicate() ) {
		case DirectType:
			direct = true;
		case Type:
			ATermAppl instance = arguments.get( 0 );
			ATermAppl clazz = arguments.get( 1 );

			if( bound.containsAll( arguments ) ) {
				staticCost = direct
					? estimate.getCost( KBOperation.IS_DIRECT_TYPE )
					: estimate.getCost( KBOperation.IS_TYPE );
				branchCount = 1;
			}
			else if( bound.contains( clazz ) ) {
				staticCost = direct
					? estimate.getCost( KBOperation.GET_DIRECT_INSTANCES )
					: estimate.getCost( KBOperation.GET_INSTANCES );
				branchCount = isConstant( clazz )
					? estimate.size( clazz )
					: estimate.avgInstancesPerClass( direct );
			}
			else if( bound.contains( instance ) ) {
				staticCost = estimate.getCost( KBOperation.GET_TYPES );
				branchCount = isConstant( instance )
					? estimate.classesPerInstance( instance, direct )
					: estimate.avgClassesPerInstance( direct );
			}
			else {
				staticCost = estimate.getClassCount() * (direct
					? estimate.getCost( KBOperation.GET_DIRECT_INSTANCES )
					: estimate.getCost( KBOperation.GET_INSTANCES ));
				branchCount = estimate.getClassCount() * estimate.avgInstancesPerClass( direct );
			}
			break;

		case Annotation: // TODO
		case PropertyValue:
			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.HAS_PROPERTY_VALUE );
				branchCount = 1;
			}
			else {
				ATermAppl subject = arguments.get( 0 );
				ATermAppl predicate = arguments.get( 1 );
				ATermAppl object = arguments.get( 2 );

				if( bound.contains( predicate ) ) {
					if( bound.contains( subject ) ) {
						staticCost = estimate.getCost( KBOperation.GET_PROPERTY_VALUE );
						branchCount = isConstant( predicate )
							? estimate.avg( predicate )
							: estimate.avgSubjectsPerProperty();
					}
					else if( bound.contains( object ) ) {
						staticCost = estimate.getCost( KBOperation.GET_PROPERTY_VALUE );
						if( isConstant( predicate ) ) {
							if( kb.isObjectProperty( predicate ) ) {
								branchCount = estimate.avg( inv( predicate ) );
							}
							else {
								branchCount = estimate.avgSubjectsPerProperty();
							}
						}
						else {
							branchCount = estimate.avgSubjectsPerProperty();
						}
					}
					else {
						staticCost = estimate.getCost( KBOperation.GET_PROPERTY_VALUE )
						/*
						 * TODO should be st. like
						 * GET_INSTANCES_OF_ROLLED_CONCEPT that reflects the
						 * complexity of the concept.
						 */
						+ (isConstant( predicate )
							? estimate.avg( predicate )
							: estimate.avgSubjectsPerProperty())
								* estimate.getCost( KBOperation.GET_PROPERTY_VALUE );
						branchCount = (isConstant( predicate )
							? estimate.size( predicate )
							: estimate.avgPairsPerProperty());
					}
				}
				else if( bound.contains( subject ) || bound.contains( object ) ) {
					staticCost = estimate.getPropertyCount()
							* estimate.getCost( KBOperation.GET_PROPERTY_VALUE );
					branchCount = estimate.getPropertyCount() * estimate.avgSubjectsPerProperty();
				}
				else {
					staticCost = estimate.getPropertyCount()
							* (estimate.getCost( KBOperation.GET_PROPERTY_VALUE )
							/*
							 * TODO should be st. like
							 * GET_INSTANCES_OF_ROLLED_CONCEPT that reflects the
							 * complexity of the concept.
							 */+ estimate.avgSubjectsPerProperty()
									* estimate.getCost( KBOperation.GET_PROPERTY_VALUE ));
					branchCount = estimate.avgPairsPerProperty() * estimate.getPropertyCount();
				}
			}
			break;

		case SameAs:
			ATermAppl saLHS = arguments.get( 0 );
			ATermAppl saRHS = arguments.get( 1 );

			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_SAME_AS );
				branchCount = 1;
			}
			else if( bound.contains( saLHS ) || bound.contains( saRHS ) ) {
				staticCost = estimate.getCost( KBOperation.GET_SAMES );

				if( bound.contains( saLHS ) ) {
					branchCount = isConstant( saLHS )
						? estimate.sames( saLHS )
						: estimate.avgSamesPerInstance();
				}
				else {
					branchCount = isConstant( saRHS )
						? estimate.sames( saRHS )
						: estimate.avgSamesPerInstance();
				}
			}
			else {
				staticCost = estimate.getInstanceCount() * estimate.getCost( KBOperation.GET_SAMES );
				branchCount = estimate.getInstanceCount() * estimate.avgSamesPerInstance();
			}
			break;
		case DifferentFrom:
			ATermAppl dfLHS = arguments.get( 0 );
			ATermAppl dfRHS = arguments.get( 1 );

			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_DIFFERENT_FROM );
				branchCount = 1;
			}
			else if( bound.contains( dfLHS ) || bound.contains( dfRHS ) ) {
				staticCost = estimate.getCost( KBOperation.GET_DIFFERENTS );

				if( bound.contains( dfLHS ) ) {
					branchCount = isConstant( dfLHS )
						? estimate.differents( dfLHS )
						: estimate.avgDifferentsPerInstance();
				}
				else {
					branchCount = isConstant( dfRHS )
						? estimate.differents( dfRHS )
						: estimate.avgDifferentsPerInstance();
				}
			}
			else {
				staticCost = estimate.getInstanceCount()
						* estimate.getCost( KBOperation.GET_DIFFERENTS );
				branchCount = estimate.getInstanceCount() * estimate.avgDifferentsPerInstance();
			}
			break;

		case DirectSubClassOf:
			direct = true;
		case StrictSubClassOf:
			strict = true;
		case SubClassOf:
			ATermAppl clazzLHS = arguments.get( 0 );
			ATermAppl clazzRHS = arguments.get( 1 );

			if( bound.containsAll( arguments ) ) {
				if( strict ) {
					if( direct ) {
						staticCost = estimate.getCost( KBOperation.GET_DIRECT_SUB_OR_SUPERCLASSES );
					}
					else {
						staticCost = estimate.getCost( KBOperation.IS_SUBCLASS_OF )
								+ estimate.getCost( KBOperation.GET_EQUIVALENT_CLASSES );
					}
				}
				else {
					staticCost = estimate.getCost( KBOperation.IS_SUBCLASS_OF );
				}

				branchCount = 1;
			}
			else if( bound.contains( clazzLHS ) || bound.contains( clazzRHS ) ) {
				if( strict && !direct ) {
					staticCost = estimate.getCost( KBOperation.GET_SUB_OR_SUPERCLASSES )
							+ estimate.getCost( KBOperation.GET_EQUIVALENT_CLASSES );
				}
				else {
					staticCost = direct
						? estimate.getCost( KBOperation.GET_DIRECT_SUB_OR_SUPERCLASSES )
						: estimate.getCost( KBOperation.GET_SUB_OR_SUPERCLASSES );
				}
				if( bound.contains( clazzLHS ) ) {
					branchCount = isConstant( clazzLHS )
						? estimate.superClasses( clazzLHS, direct )
						: estimate.avgSuperClasses( direct );

					if( strict ) {
						branchCount -= isConstant( clazzLHS )
							? estimate.equivClasses( clazzLHS )
							: estimate.avgEquivClasses();
						branchCount = Math.max( branchCount, 0 );
					}
				}
				else {
					branchCount = isConstant( clazzRHS )
						? estimate.superClasses( clazzRHS, direct )
						: estimate.avgSuperClasses( direct );

					if( strict ) {
						branchCount -= isConstant( clazzRHS )
							? estimate.equivClasses( clazzRHS )
							: estimate.avgEquivClasses();
						branchCount = Math.max( branchCount, 0 );
					}
				}
			}
			else {
				if( strict && !direct ) {
					staticCost = estimate.getCost( KBOperation.GET_SUB_OR_SUPERCLASSES )
							+ estimate.getCost( KBOperation.GET_EQUIVALENT_CLASSES );
				}
				else {
					staticCost = direct
						? estimate.getCost( KBOperation.GET_DIRECT_SUB_OR_SUPERCLASSES )
						: estimate.getCost( KBOperation.GET_SUB_OR_SUPERCLASSES );
				}

				staticCost *= estimate.getClassCount();

				branchCount = estimate.getClassCount() * estimate.avgSubClasses( direct );

				if( strict ) {
					branchCount -= estimate.avgEquivClasses();
					branchCount = Math.max( branchCount, 0 );
				}
			}
			break;
		case EquivalentClass:
			ATermAppl eqcLHS = arguments.get( 0 );
			ATermAppl eqcRHS = arguments.get( 1 );

			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_EQUIVALENT_CLASS );
				branchCount = 1;
			}
			else if( bound.contains( eqcLHS ) || bound.contains( eqcRHS ) ) {
				staticCost = estimate.getCost( KBOperation.GET_EQUIVALENT_CLASSES );

				if( bound.contains( eqcLHS ) ) {
					branchCount = isConstant( eqcLHS )
						? estimate.equivClasses( eqcLHS )
						: estimate.avgEquivClasses();
				}
				else {
					branchCount = isConstant( eqcRHS )
						? estimate.equivClasses( eqcRHS )
						: estimate.avgEquivClasses();
				}
			}
			else {
				staticCost = estimate.getClassCount()
						* estimate.getCost( KBOperation.GET_EQUIVALENT_CLASSES );
				branchCount = estimate.getClassCount() * estimate.avgEquivClasses();
			}
			break;
		case DisjointWith:
			ATermAppl dwLHS = arguments.get( 0 );
			ATermAppl dwRHS = arguments.get( 1 );

			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_DISJOINT_WITH );
				branchCount = 1;
			}
			else if( bound.contains( dwLHS ) || bound.contains( dwRHS ) ) {
				staticCost = estimate.getCost( KBOperation.GET_DISJOINT_CLASSES );

				if( bound.contains( dwLHS ) ) {
					branchCount = isConstant( dwLHS )
						? estimate.disjoints( dwLHS )
						: estimate.avgDisjointClasses();
				}
				else {
					branchCount = isConstant( dwRHS )
						? estimate.disjoints( dwRHS )
						: estimate.avgDisjointClasses();
				}
			}
			else {
				staticCost = estimate.getClassCount()
						* estimate.getCost( KBOperation.GET_DISJOINT_CLASSES );
				branchCount = estimate.getClassCount() * estimate.avgDisjointClasses();
			}
			break;
		case ComplementOf:
			ATermAppl coLHS = arguments.get( 0 );
			ATermAppl coRHS = arguments.get( 1 );

			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_COMPLEMENT_OF );
				branchCount = 1;
			}
			else if( bound.contains( coLHS ) || bound.contains( coRHS ) ) {
				staticCost = estimate.getCost( KBOperation.GET_COMPLEMENT_CLASSES );

				if( bound.contains( coLHS ) ) {
					branchCount = isConstant( coLHS )
						? estimate.complements( coLHS )
						: estimate.avgComplementClasses();
				}
				else {
					branchCount = isConstant( coRHS )
						? estimate.complements( coRHS )
						: estimate.avgComplementClasses();
				}
			}
			else {
				staticCost = estimate.getClassCount()
						* estimate.getCost( KBOperation.GET_COMPLEMENT_CLASSES );
				branchCount = estimate.getClassCount() * estimate.avgComplementClasses();
			}
			break;

		case DirectSubPropertyOf:
			direct = true;
		case StrictSubPropertyOf:
			strict = true;
		case SubPropertyOf:
			ATermAppl spLHS = arguments.get( 0 );
			ATermAppl spRHS = arguments.get( 1 );

			if( bound.containsAll( arguments ) ) {
				if( strict ) {
					if( direct ) {
						staticCost = estimate
								.getCost( KBOperation.GET_DIRECT_SUB_OR_SUPERPROPERTIES );
					}
					else {
						staticCost = estimate.getCost( KBOperation.IS_SUBPROPERTY_OF )
								+ estimate.getCost( KBOperation.GET_EQUIVALENT_PROPERTIES );
					}
				}
				else {
					staticCost = estimate.getCost( KBOperation.IS_SUBPROPERTY_OF );
				}

				branchCount = 1;
			}
			else if( bound.contains( spLHS ) || bound.contains( spRHS ) ) {
				if( strict && !direct ) {
					staticCost = estimate.getCost( KBOperation.GET_SUB_OR_SUPERPROPERTIES )
							+ estimate.getCost( KBOperation.GET_EQUIVALENT_PROPERTIES );
				}
				else {
					staticCost = direct
						? estimate.getCost( KBOperation.GET_DIRECT_SUB_OR_SUPERPROPERTIES )
						: estimate.getCost( KBOperation.GET_SUB_OR_SUPERPROPERTIES );
				}
				if( bound.contains( spLHS ) ) {
					branchCount = isConstant( spLHS )
						? estimate.superProperties( spLHS, direct )
						: estimate.avgSuperProperties( direct );

					if( strict ) {
						branchCount -= isConstant( spLHS )
							? estimate.equivProperties( spLHS )
							: estimate.avgEquivProperties();
						branchCount = Math.max( branchCount, 0 );

					}
				}
				else {
					branchCount = isConstant( spRHS )
						? estimate.superProperties( spRHS, direct )
						: estimate.avgSuperProperties( direct );

					if( strict ) {
						branchCount -= isConstant( spRHS )
							? estimate.equivProperties( spRHS )
							: estimate.avgEquivProperties();
						branchCount = Math.max( branchCount, 0 );

					}
				}
			}
			else {
				if( strict && !direct ) {
					staticCost = estimate.getCost( KBOperation.GET_SUB_OR_SUPERPROPERTIES )
							+ estimate.getCost( KBOperation.GET_EQUIVALENT_PROPERTIES );
				}
				else {
					staticCost = direct
						? estimate.getCost( KBOperation.GET_DIRECT_SUB_OR_SUPERPROPERTIES )
						: estimate.getCost( KBOperation.GET_SUB_OR_SUPERPROPERTIES );
				}

				staticCost *= estimate.getPropertyCount();

				branchCount = estimate.getPropertyCount() * estimate.avgSubProperties( direct );

				if( strict ) {
					branchCount -= estimate.avgEquivProperties();
					branchCount = Math.max( branchCount, 0 );

				}
			}
			break;

		case EquivalentProperty:
			ATermAppl eqpLHS = arguments.get( 0 );
			ATermAppl eqpRHS = arguments.get( 1 );

			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_EQUIVALENT_PROPERTY );
				branchCount = 1;
			}
			else if( bound.contains( eqpLHS ) || bound.contains( eqpRHS ) ) {
				staticCost = estimate.getCost( KBOperation.GET_EQUIVALENT_PROPERTIES );

				if( bound.contains( eqpLHS ) ) {
					branchCount = isConstant( eqpLHS )
						? estimate.equivProperties( eqpLHS )
						: estimate.avgEquivProperties();
				}
				else {
					branchCount = isConstant( eqpRHS )
						? estimate.equivProperties( eqpRHS )
						: estimate.avgEquivProperties();
				}
			}
			else {
				staticCost = estimate.getPropertyCount()
						* estimate.getCost( KBOperation.GET_EQUIVALENT_PROPERTIES );
				branchCount = estimate.getPropertyCount() * estimate.avgEquivProperties();
			}
			break;
		case Domain:
			ATermAppl domLHS = arguments.get( 0 );
			ATermAppl domRHS = arguments.get( 1 );
			
			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_DOMAIN );
				branchCount = 1;
			}
			else if( bound.contains( domLHS ) || bound.contains( domRHS ) ) {
				staticCost = estimate.getCost( KBOperation.GET_DOMAINS );
				
				if( bound.contains( domLHS ) ) {
					branchCount = isConstant( domLHS )
						? estimate.equivProperties( domLHS )
						: estimate.avgEquivProperties();
				}
				else {
					branchCount = isConstant( domRHS )
						? estimate.equivClasses( domRHS )
						: estimate.avgEquivClasses();
				}
			}
			else {
				staticCost = estimate.getPropertyCount()
					* estimate.getCost( KBOperation.GET_DOMAINS );
				branchCount = estimate.getPropertyCount() * estimate.avgEquivProperties();
			}
			break;
		case Range:
			ATermAppl rangeLHS = arguments.get( 0 );
			ATermAppl rangeRHS = arguments.get( 1 );
			
			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_RANGE );
				branchCount = 1;
			}
			else if( bound.contains( rangeLHS ) || bound.contains( rangeRHS ) ) {
				staticCost = estimate.getCost( KBOperation.GET_RANGES );
				
				if( bound.contains( rangeLHS ) ) {
					branchCount = isConstant( rangeLHS )
						? estimate.equivProperties( rangeLHS )
						: estimate.avgEquivProperties();
				}
				else {
					branchCount = isConstant( rangeRHS )
						? estimate.equivClasses( rangeRHS )
						: estimate.avgEquivClasses();
				}
			}
			else {
				staticCost = estimate.getPropertyCount()
					* estimate.getCost( KBOperation.GET_RANGES );
				branchCount = estimate.getPropertyCount() * estimate.avgEquivProperties();
			}
			
			break;
		case InverseOf:
			ATermAppl ioLHS = arguments.get( 0 );
			ATermAppl ioRHS = arguments.get( 1 );

			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_INVERSE_OF );
				branchCount = 1;
			}
			else if( bound.contains( ioLHS ) || bound.contains( ioRHS ) ) {
				staticCost = estimate.getCost( KBOperation.GET_INVERSES );

				if( bound.contains( ioLHS ) ) {
					branchCount = isConstant( ioLHS )
						? estimate.inverses( ioLHS )
						: estimate.avgInverseProperties();
				}
				else {
					branchCount = isConstant( ioRHS )
						? estimate.inverses( ioRHS )
						: estimate.avgInverseProperties();
				}
			}
			else {
				staticCost = estimate.getPropertyCount()
						* estimate.getCost( KBOperation.GET_INVERSES );
				branchCount = estimate.getPropertyCount() * estimate.avgInverseProperties();
			}
			break;
		case ObjectProperty:
			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_OBJECT_PROPERTY );
				branchCount = 1;
			}
			else {
				staticCost = estimate.getCost( KBOperation.GET_OBJECT_PROPERTIES );
				branchCount = estimate.getObjectPropertyCount();
			}
			break;
		case DatatypeProperty:
			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_DATATYPE_PROPERTY );
				branchCount = 1;
			}
			else {
				staticCost = estimate.getCost( KBOperation.GET_DATATYPE_PROPERTIES );
				branchCount = estimate.getDataPropertyCount();
			}
			break;
		case Functional:
			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_FUNCTIONAL_PROPERTY );
				branchCount = 1;
			}
			else {
				staticCost = estimate.getCost( KBOperation.GET_FUNCTIONAL_PROPERTIES );
				branchCount = estimate.getFunctionalPropertyCount();
			}
			break;
		case InverseFunctional:
			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_INVERSE_FUNCTIONAL_PROPERTY );
				branchCount = 1;
			}
			else {
				staticCost = estimate.getCost( KBOperation.GET_INVERSE_FUNCTIONAL_PROPERTIES );
				branchCount = estimate.getInverseFunctionalPropertyCount();
			}
			break;
		case Transitive:
			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_TRANSITIVE_PROPERTY );
				branchCount = 1;
			}
			else {
				staticCost = estimate.getCost( KBOperation.GET_TRANSITIVE_PROPERTIES );
				branchCount = estimate.getTransitivePropertyCount();
			}
			break;
		case Symmetric:
			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_SYMMETRIC_PROPERTY );
				branchCount = 1;
			}
			else {
				staticCost = estimate.getCost( KBOperation.GET_SYMMETRIC_PROPERTIES );
				branchCount = estimate.getSymmetricPropertyCount();
			}
			break;
		case Asymmetric:
			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_ASYMMETRIC_PROPERTY );
				branchCount = 1;
			}
			else {
				staticCost = estimate.getCost( KBOperation.GET_ASYMMETRIC_PROPERTIES);
				branchCount = estimate.getSymmetricPropertyCount();
			}
			break;
		case Reflexive:
			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_REFLEXIVE_PROPERTY );
				branchCount = 1;
			}
			else {
				staticCost = estimate.getCost( KBOperation.GET_REFLEXIVE_PROPERTIES );
				branchCount = estimate.getSymmetricPropertyCount();
			}
			break;
		case Irreflexive:
			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_IRREFLEXIVE_PROPERTY );
				branchCount = 1;
			}
			else {
				staticCost = estimate.getCost( KBOperation.GET_IRREFLEXIVE_PROPERTIES );
				branchCount = estimate.getSymmetricPropertyCount();
			}
			break;
		case NotKnown:
			estimate( ((NotKnownQueryAtom) atom).getAtoms(), bound );
			break;
			
		case Union: {
			double totalStaticCount = 1.0;
			double totalBranchCount = 1.0;

			for( List<QueryAtom> atoms : ((UnionQueryAtom) atom).getUnion() ) {
				estimate( atoms, bound );

				totalBranchCount += branchCount;
				totalStaticCount += staticCost;
			}		 

			staticCost = totalStaticCount;
			branchCount = totalBranchCount;
			
			break;
		}

		case UndistVarCore:
			// if (!bound.containsAll(query.getDistVarsForType(VarType.CLASS))
			// || !bound.containsAll(query
			// .getDistVarsForType(VarType.PROPERTY))) {
			// // meanwhile not supporting query orderings that allow evaluate
			// // schema atoms after a core.
			// return Double.MAX_VALUE;
			// }

			// neglecting rolling-ups
			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_TYPE );
				branchCount = 1;
			}
			else {
				Core core = (Core) atom;
				int n = core.getDistVars().size();

				double b = Math.pow( estimate.avgInstancesPerClass( false ), n );
				branchCount = b;

				switch ( QueryEngine.getStrategy( atom ) ) {
				case ALLFAST: // TODO
				case SIMPLE:
					staticCost = n * estimate.getCost( KBOperation.GET_INSTANCES ) + b
							* estimate.getCost( KBOperation.IS_TYPE );
					break;
				default:
					throw new IllegalArgumentException( "Not yet implemented." );
				}
			}
			break;
			
		case Datatype:
			if( bound.containsAll( arguments ) ) {
				staticCost = 1;
			}
			else {
				staticCost = Integer.MAX_VALUE;
			}
			branchCount = 1;
			break;
			
		default:
			throw new UnsupportedFeatureException( "Unknown atom type " + atom.getPredicate() + "." );
		}
		
		return staticCost;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getBranchCount() {
		return branchCount;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getStaticCost() {
		return staticCost;
	}

	private ATermAppl inv(ATermAppl pred) {
		return kb.getRBox().getRole( pred ).getInverse().getName();
	}

	private boolean isConstant(ATermAppl a) {
		return !ATermUtils.isVar( a );
	}

}
