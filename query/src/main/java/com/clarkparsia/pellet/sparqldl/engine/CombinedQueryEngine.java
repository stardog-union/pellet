// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.engine;

import static com.clarkparsia.pellet.utils.TermFactory.hasValue;
import static com.clarkparsia.pellet.utils.TermFactory.inv;
import static com.clarkparsia.pellet.utils.TermFactory.not;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.exceptions.UnsupportedQueryException;
import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.taxonomy.TaxonomyNode;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.CandidateSet;
import org.mindswap.pellet.utils.DisjointSet;
import org.mindswap.pellet.utils.Timer;

import aterm.ATermAppl;

import com.clarkparsia.pellet.sparqldl.model.CoreNewImpl;
import com.clarkparsia.pellet.sparqldl.model.NotKnownQueryAtom;
import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.model.QueryAtom;
import com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory;
import com.clarkparsia.pellet.sparqldl.model.QueryImpl;
import com.clarkparsia.pellet.sparqldl.model.QueryPredicate;
import com.clarkparsia.pellet.sparqldl.model.QueryResult;
import com.clarkparsia.pellet.sparqldl.model.QueryResultImpl;
import com.clarkparsia.pellet.sparqldl.model.ResultBinding;
import com.clarkparsia.pellet.sparqldl.model.ResultBindingImpl;
import com.clarkparsia.pellet.sparqldl.model.UnionQueryAtom;
import com.clarkparsia.pellet.sparqldl.model.Query.VarType;

/**
 * <p>
 * Title: Engine for queries with only distinguished variables.
 * </p>
 * <p>
 * Description: All variable name spaces are disjoint.
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
public class CombinedQueryEngine implements QueryExec {
	public static final Logger			log			= Logger.getLogger( CombinedQueryEngine.class
															.getName() );

	public static final QueryOptimizer	optimizer	= new QueryOptimizer();

	private KnowledgeBase				kb;

	protected QueryPlan					plan;

	protected Query						oldQuery;

	protected Query						query;

	private QueryResult					result;

	private Set<ATermAppl>				downMonotonic;

	private void prepare(Query query) {
		if( log.isLoggable( Level.FINE ) ) {
			log.fine( "Preparing plan ..." );
		}

		this.kb = query.getKB();
		if( kb == null ) {
			throw new RuntimeException( "No input data set is given for query!" );
		}

		this.result = new QueryResultImpl( query );

		this.oldQuery = query;
		this.query = setupCores( query );

		if( log.isLoggable( Level.FINE ) ) {
			log.fine( "After setting-up cores : " + this.query );
		}

		this.plan = optimizer.getExecutionPlan( this.query );
		this.plan.reset();

		// warm up the reasoner by computing the satisfiability of classes
		// used in the query so that cached models can be used for instance
		// checking - TODO also non-named classes
		if( (PelletOptions.USE_CACHING) && !kb.isClassified() ) {
			for( final QueryAtom a : oldQuery.getAtoms() ) {
				for( final ATermAppl arg : a.getArguments() ) {
					if( kb.isClass( arg ) ) {
						kb.isSatisfiable( arg );
						kb.isSatisfiable( ATermUtils.makeNot( arg ) );
					}
				}
			}
		}

		if( PelletOptions.OPTIMIZE_DOWN_MONOTONIC ) {
			// TODO use down monotonic variables for implementation of
			// DirectType atom
			downMonotonic = new HashSet<ATermAppl>();
			setupDownMonotonicVariables( this.query );
			if( log.isLoggable( Level.FINE ) ) {
				log.fine( "Variables to be optimized : " + downMonotonic );
			}
		}
	}

	// computes cores of undistinguished variables
	private Query setupCores(final Query query) {
		final Iterator<ATermAppl> undistVarIterator = query.getUndistVars().iterator();
		if( !undistVarIterator.hasNext() ) {
			return query;
		}
		final DisjointSet<Object> coreVertices = new DisjointSet<Object>();

		final List<QueryAtom> toRemove = new ArrayList<QueryAtom>();

		while( undistVarIterator.hasNext() ) {
			final ATermAppl a = undistVarIterator.next();

			coreVertices.add( a );

			for( final QueryAtom atom : query.findAtoms( QueryPredicate.PropertyValue, a, null,
					null ) ) {
				coreVertices.add( atom );
				coreVertices.union( a, atom );

				final ATermAppl a2 = atom.getArguments().get( 2 );
				if( query.getUndistVars().contains( a2 ) ) {
					coreVertices.add( a2 );
					coreVertices.union( a, a2 );
				}
				toRemove.add( atom );
			}
			for( final QueryAtom atom : query.findAtoms( QueryPredicate.PropertyValue, null, null,
					a ) ) {
				coreVertices.add( atom );
				coreVertices.union( a, atom );

				final ATermAppl a2 = atom.getArguments().get( 0 );
				if( query.getUndistVars().contains( a2 ) ) {
					coreVertices.add( a2 );
					coreVertices.union( a, a2 );
				}
				toRemove.add( atom );
			}

			for( final QueryAtom atom : query.findAtoms( QueryPredicate.Type, a, null ) ) {
				coreVertices.add( atom );
				coreVertices.union( a, atom );
				toRemove.add( atom );
			}
		}

		final Query transformedQuery = query.apply( new ResultBindingImpl() );

		for( final Set<Object> set : coreVertices.getEquivalanceSets() ) {
			final Collection<QueryAtom> atoms = new ArrayList<QueryAtom>();

			for( final Object a : set ) {
				if( a instanceof QueryAtom ) {
					atoms.add( (QueryAtom) a );
				}
			}

			final CoreNewImpl c = (CoreNewImpl) QueryAtomFactory.Core( atoms,
					query.getUndistVars(), kb );

			transformedQuery.add( c );

			if( log.isLoggable( Level.FINE ) ) {
				log.fine( c.getUndistVars() + " : " + c.getDistVars() + " : " + c.getQuery().getAtoms() );
			}
		}

		for( final QueryAtom atom : toRemove ) {
			transformedQuery.remove( atom );
		}

		return transformedQuery;
	}

	// down-monotonic variables = Class variables in Type atoms and Property
	// variables in PropertyValue atoms
	private void setupDownMonotonicVariables(final Query query) {
		for( final QueryAtom atom : query.getAtoms() ) {
			ATermAppl arg;

			switch ( atom.getPredicate() ) {
			case PropertyValue:
			case Type:
				arg = atom.getArguments().get( 1 );
				if( ATermUtils.isVar( arg ) ) {
					downMonotonic.add( arg );
				}
				break;
			default:
				arg = null;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean supports(Query q) {
		// TODO cycles in undist vars and fully undist.vars queries are not
		// supported !!!
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public QueryResult exec(Query query) {
		if( log.isLoggable( Level.FINE ) ) {
			log.fine( "Executing query " + query );
		}

		Timer timer = new Timer( "CombinedQueryEngine" );
		timer.start();
		prepare( query );
		branches = 0;
		exec( new ResultBindingImpl() );
		timer.stop();

		if( log.isLoggable( Level.FINE ) ) {
			log.log( Level.FINE, "#B=" + branches + ", time=" + timer.getLast() + " ms." );
		}

		return result;
	}

	private long	branches;

	private void exec(ResultBinding binding) {
		if( log.isLoggable( Level.FINE ) ) {
			branches++;
		}

		if( !plan.hasNext() ) {
			// TODO if result vars are not same as dist vars.
			if( !binding.isEmpty() || result.isEmpty() ) {
				if( log.isLoggable( Level.FINE ) ) {
					log.fine( "Found binding: " + binding );
				}
				
//				Filter filter = query.getFilter();

				if( !result.getResultVars().containsAll( binding.getAllVariables() ) ) {
					ResultBinding newBinding = new ResultBindingImpl();
					for( ATermAppl var : result.getResultVars() ) {
						ATermAppl value = binding.getValue( var );

						newBinding.setValue( var, value );
					}
					binding = newBinding;
				}

				result.add( binding );
			}
			
			if( log.isLoggable( Level.FINER ) ) {
				log.finer( "Returning ... binding=" + binding );
			}
			return;
		}

		final QueryAtom current = plan.next( binding );
		
		if( log.isLoggable( Level.FINER ) ) {
			log.finer( "Evaluating " + current );
		}
		
		if( current.isGround() && !current.getPredicate().equals( QueryPredicate.UndistVarCore ) ) {
			if( QueryEngine.checkGround( current, kb ) ) {
				exec( binding );
			}
		}
		else {
			exec( current, binding );
		}
		
		if( log.isLoggable( Level.FINER ) ) {
			log.finer( "Returning ... " + binding );
		}

		plan.back();
	}

	private void exec(QueryAtom current, ResultBinding binding) {
		final List<ATermAppl> arguments = current.getArguments();

		boolean direct = false;
		boolean strict = false;

		switch ( current.getPredicate() ) {

		case DirectType:
			direct = true;
		case Type: // TODO implementation of downMonotonic vars
			final ATermAppl tI = arguments.get( 0 );
			final ATermAppl tC = arguments.get( 1 );

			Set<ATermAppl> instanceCandidates = null;
			if( tI.equals( tC ) ) {
				instanceCandidates = kb.getIndividuals().size() < kb.getClasses().size()
					? kb.getIndividuals()
					: kb.getClasses();
				for( final ATermAppl ic : instanceCandidates ) {
					if( direct
						? kb.getInstances( ic, direct ).contains( ic )
						: kb.isType( ic, ic ) ) {
						final ResultBinding candidateBinding = binding.duplicate();

						if( ATermUtils.isVar( tI ) ) {
							candidateBinding.setValue( tI, ic );
						}

						exec( candidateBinding );
					}
				}
			}
			else {
				final Set<ATermAppl> classCandidates;

				if( !ATermUtils.isVar( tC ) ) {
					classCandidates = Collections.singleton( tC );
					instanceCandidates = kb.getInstances( tC, direct );
				}
				else if( !ATermUtils.isVar( tI ) ) {
					// classCandidates = flatten(TaxonomyUtils.getTypes(kb
					// .getTaxonomy(), tI, direct)); // TODO
					classCandidates = flatten( kb.getTypes( tI, direct ) ); // TODO
					instanceCandidates = Collections.singleton( tI );
				}
				else {
					classCandidates = kb.getAllClasses();
				}

				// explore all possible bindings
				boolean loadInstances = (instanceCandidates == null);
				for( final ATermAppl cls : classCandidates ) {
					if( loadInstances ) {
						instanceCandidates = kb.getInstances( cls, direct );
					}
					for( final ATermAppl inst : instanceCandidates ) {
						runNext( binding, arguments, inst, cls );
					}
				} // finish explore bindings
			}
			break;

		case PropertyValue: // TODO implementation of downMonotonic vars
			final ATermAppl pvI = arguments.get( 0 );
			final ATermAppl pvP = arguments.get( 1 );
			final ATermAppl pvIL = arguments.get( 2 );

			Collection<ATermAppl> propertyCandidates = null;
			Collection<ATermAppl> subjectCandidates = null;
			Collection<ATermAppl> objectCandidates = null;

			boolean loadProperty = false;
			boolean loadSubjects = false;
			boolean loadObjects = false;

			if( !ATermUtils.isVar( pvP ) ) {
				propertyCandidates = Collections.singleton( pvP );
				if( !ATermUtils.isVar( pvI ) ) {
					subjectCandidates = Collections.singleton( pvI );
					objectCandidates = kb.getPropertyValues( pvP, pvI );
				}
				else if( !ATermUtils.isVar( pvIL ) ) {
					objectCandidates = Collections.singleton( pvIL );
					subjectCandidates = kb.getIndividualsWithProperty( pvP, pvIL );
				}
				loadProperty = false;
			}
			else {
				if( !ATermUtils.isVar( pvI ) ) {
					subjectCandidates = Collections.singleton( pvI );
				}

				if( !ATermUtils.isVar( pvIL ) ) {
					objectCandidates = Collections.singleton( pvIL );
				}
				else if( !plan.getQuery().getDistVarsForType( VarType.LITERAL ).contains( pvIL ) ) {
					propertyCandidates = kb.getObjectProperties();
				}

				if( propertyCandidates == null ) {
					propertyCandidates = kb.getProperties();
				}
				loadProperty = true;
			}

			loadSubjects = (subjectCandidates == null);
			loadObjects = (objectCandidates == null);

			for( final ATermAppl property : propertyCandidates ) {
				// TODO replace this nasty if-cascade with some map for
				// var
				// bindings.
				if( loadObjects && loadSubjects ) {
					if( pvI.equals( pvIL ) ) {
						if( pvI.equals( pvP ) ) {
							if( !kb.hasPropertyValue( property, property, property ) ) {
								continue;
							}
							runNext( binding, arguments, property, property, property );
						}
						else {
							for( final ATermAppl i : kb.getIndividuals() ) {
								if( !kb.hasPropertyValue( i, property, i ) ) {
									continue;
								}
								runNext( binding, arguments, i, property, i );
							}
						}
					}
					else {
						if( pvI.equals( pvP ) ) {
							for( final ATermAppl i : kb.getIndividuals() ) {
								if( !kb.hasPropertyValue( property, property, i ) ) {
									continue;
								}
								runNext( binding, arguments, property, property, i );
							}
						}
						else if( pvIL.equals( pvP ) ) {
							for( final ATermAppl i : kb.getIndividuals() ) {
								if( !kb.hasPropertyValue( i, property, property ) ) {
									continue;
								}
								runNext( binding, arguments, i, property, property );
							}
						}
						else {
							for( final ATermAppl subject : kb.getIndividuals() ) {
								for( final ATermAppl object : kb.getPropertyValues( property,
										subject ) ) {
									runNext( binding, arguments, subject, property, object );
								}
							}
						}
					}
				}
				else if( loadObjects ) {
					// subject is known.
					if( pvP.equals( pvIL ) ) {
						if( !kb.hasPropertyValue( subjectCandidates.iterator().next(),
								property, property ) ) {
							// terminate
							subjectCandidates = Collections.emptySet();
						}
					}

					for( final ATermAppl subject : subjectCandidates ) {
						for( final ATermAppl object : kb.getPropertyValues( property, subject ) ) {
							runNext( binding, arguments, subject, property, object );
						}
					}
				}
				else {
					// object is known.
					for( final ATermAppl object : objectCandidates ) {
						if( loadSubjects ) {
							if( pvI.equals( pvP ) ) {
								if( kb.hasPropertyValue( property, property, object ) ) {
									subjectCandidates = Collections.singleton( property );
								}
								else {
									// terminate
									subjectCandidates = Collections.emptySet();
								}
							}
							else {
								subjectCandidates = new HashSet<ATermAppl>( kb
										.getIndividualsWithProperty( property, object ) );
							}
						}

						for( final ATermAppl subject : subjectCandidates ) {
							if( loadProperty
									&& !kb.hasPropertyValue( subject, property, object ) ) {
								continue;
							}

							runNext( binding, arguments, subject, property, object );
						}
					}
				}
			} // finish visiting non-ground triple.
			break;
			

		case SameAs:
			// optimize - merge nodes
			final ATermAppl saI1 = arguments.get( 0 );
			final ATermAppl saI2 = arguments.get( 1 );

			for( final ATermAppl known : getSymmetricCandidates( VarType.INDIVIDUAL, saI1, saI2 ) ) {

				final Set<ATermAppl> dependents;

				if( saI1.equals( saI2 ) ) {
					dependents = Collections.singleton( known );
				}
				else {
					dependents = kb.getAllSames( known );
				}

				for( final ATermAppl dependent : dependents ) {
					runSymetricCheck( current, saI1, known, saI2, dependent, binding );
				}
			}
			break;

		case DifferentFrom:
			// optimize - different from map
			final ATermAppl dfI1 = arguments.get( 0 );
			final ATermAppl dfI2 = arguments.get( 1 );

			if( !dfI1.equals( dfI2 ) ) {
				for( final ATermAppl known : getSymmetricCandidates( VarType.INDIVIDUAL, dfI1,
						dfI2 ) ) {
					for( final ATermAppl dependent : kb.getDifferents( known ) ) {
						runSymetricCheck( current, dfI1, known, dfI2, dependent, binding );
					}
				}
			}
			else {
				if( log.isLoggable( Level.FINER ) ) {
					log.finer( "Atom " + current
							+ "cannot be satisfied in any consistent ontology." );
				}
			}
			// TODO What about undist vars ?
			// Query : PropertyValue(?x,p,_:x), Type(_:x, C),
			// DifferentFrom( _:x, x) .
			// Data : p(a,x) . p(b,y) . C(x) . C(y) .
			// Result: {b}
			//
			// Data : p(a,x) . (exists p (C and {y}))(b) . C(x) .
			// Result: {y}
			//
			// rolling-up to ?x : (exists p (C and not {x}))(?x) .
			//
			// More complex problems :
			// Query : PropertyValue(?x,p,_:x), Type(_:x, C),
			// DifferentFrom( _:x, _:y) . Type(_:y, T) .
			// Data : p(a,x) . C(x) .
			// Result: {a}
			//
			// Query : PropertyValue(?x,p,_:x), Type(_:x, C),
			// DifferentFrom( _:x, _:y) . Type(_:y, T) .
			// Data : p(x,x) . C(x) .
			// Result: {}
			//
			// Query : PropertyValue(?x,p,_:x), Type(_:x, C),
			// DifferentFrom( _:x, _:y) . Type(_:y, D) .
			// Data : p(a,x) . C(x) . D(a) .
			// Result: {a}
			//
			// rolling-up to ?x : (exists p (C and (not D)))(?x) .
			//
			// rolling-up to _:x of DifferentFrom(_:x,_:y) :
			// roll-up(_:x) and (not roll-up(_:y)).
			// but it is not complete if the rolling-up to _:y is not
			// complete, but just a preprocessing (for example _:y is in
			// a cycle).
			break;

		case Annotation:
			final ATermAppl aI = arguments.get( 0 );
			final ATermAppl aP = arguments.get( 1 );
			final ATermAppl aIL = arguments.get( 2 );

			subjectCandidates = null;
			objectCandidates = null;
			propertyCandidates = null;

			//if aI is a variable, get all the annotation subjects
			if( ATermUtils.isVar( aI ) ) {
				subjectCandidates = kb.getAnnotationSubjects();
			} 
			//else, we only have one subject candidate
			else {
				subjectCandidates = Collections.singleton( aI );
			}
			
			//if aP is a variable, get all the annotation properties
			if( ATermUtils.isVar( aP ) ) {
				propertyCandidates = kb.getAnnotationProperties();
			}
			//else, we only have one property candidate
			else {
				propertyCandidates = Collections.singleton( aP );
			}
			
			//if aIL is a variable, get all the annotation objects for the subject and the property candidates
			if( ATermUtils.isVar( aIL ) ) {
				for( final ATermAppl subject : subjectCandidates ) {
					for( final ATermAppl property : propertyCandidates ) {
						for( final ATermAppl object : kb.getAnnotations( subject, property ) ) {
							runNext( binding, arguments, subject, property, object );
						}
					}
				}
			}
			//else, we only have one object candidate
			else {
				for( final ATermAppl subject : subjectCandidates ) {
					for( final ATermAppl property : propertyCandidates ) {
						if( kb.isAnnotation(subject, property, aIL) ) {
							runNext( binding, arguments, subject, property, aIL );
						}
					}
				}
			}
			
			break;
		// throw new IllegalArgumentException("The annotation atom "
		// + current + " should be ground, but is not.");

		// TBOX ATOMS
		case DirectSubClassOf:
			direct = true;
		case StrictSubClassOf:
			strict = true;
		case SubClassOf:
			final ATermAppl scLHS = arguments.get( 0 );
			final ATermAppl scRHS = arguments.get( 1 );

			if( scLHS.equals( scRHS ) ) {
				// TODO optimization for downMonotonic variables
				for( final ATermAppl ic : kb.getClasses() ) {
					runNext( binding, arguments, ic, ic );
				}
			}
			else {
				final boolean lhsDM = isDownMonotonic( scLHS );
				final boolean rhsDM = isDownMonotonic( scRHS );

				if( lhsDM || rhsDM ) {
					downMonotonic( kb.getTaxonomy(), kb.getClasses(), lhsDM, scLHS, scRHS,
							binding, direct, strict );
				}
				else {
					final Set<ATermAppl> lhsCandidates;
					Set<ATermAppl> rhsCandidates = null;

					if( !ATermUtils.isVar( scLHS ) ) {
						lhsCandidates = Collections.singleton( scLHS );
						rhsCandidates = flatten( kb.getSuperClasses( scLHS, direct ) );

						rhsCandidates.addAll( kb.getEquivalentClasses( scLHS ) );

						if( strict ) {
							rhsCandidates.removeAll( kb.getEquivalentClasses( scLHS ) );
						}
						else if( !ATermUtils.isComplexClass( scLHS ) ) {
							rhsCandidates.add( scLHS );
						}
					}
					else if( !ATermUtils.isVar( scRHS ) ) {
						rhsCandidates = Collections.singleton( scRHS );
						if( scRHS.equals( ATermUtils.TOP ) ) {
							lhsCandidates = new HashSet<ATermAppl>( kb.getAllClasses() );
						}
						else {
							lhsCandidates = flatten( kb.getSubClasses( scRHS, direct ) );

							lhsCandidates.addAll( kb.getAllEquivalentClasses( scRHS ) );
						}

						if( strict ) {
							lhsCandidates.removeAll( kb.getAllEquivalentClasses( scRHS ) );
						}					
					}
					else {
						lhsCandidates = kb.getClasses();
					}

					boolean reload = (rhsCandidates == null);
					for( final ATermAppl subject : lhsCandidates ) {
						if( reload ) {
							rhsCandidates = flatten( kb.getSuperClasses( subject, direct ) );
							if( strict ) {
								rhsCandidates.removeAll( kb.getEquivalentClasses( subject ) );
							}
							else if( !ATermUtils.isComplexClass( subject ) ) {
								rhsCandidates.add( subject );
							}
						}
						for( final ATermAppl object : rhsCandidates ) {
							runNext( binding, arguments, subject, object );
						}
					}
				}
			}
			break;

		case EquivalentClass: // TODO implementation of downMonotonic vars
			final ATermAppl eqcLHS = arguments.get( 0 );
			final ATermAppl eqcRHS = arguments.get( 1 );

			for( final ATermAppl known : getSymmetricCandidates( VarType.CLASS, eqcLHS, eqcRHS ) ) {
				// TODO optimize - try just one - if success then take
				// all
				// found bindings and extend them for other equivalent
				// classes as well.
				// meanwhile just a simple check below

				final Set<ATermAppl> dependents;

				if( eqcLHS.equals( eqcRHS ) ) {
					dependents = Collections.singleton( known );
				}
				else {
					dependents = kb.getEquivalentClasses( known );
				}

				for( final ATermAppl dependent : dependents ) {
					int size = result.size();

					runSymetricCheck( current, eqcLHS, known, eqcRHS, dependent, binding );

					if( result.size() == size ) {
						// no binding found, so that there is no need to
						// explore other equivalent classes - they fail
						// as
						// well.
						break;
					}
				}
			}
			break;

		case DisjointWith: // TODO implementation of downMonotonic vars
			final ATermAppl dwLHS = arguments.get( 0 );
			final ATermAppl dwRHS = arguments.get( 1 );

			if( !dwLHS.equals( dwRHS ) ) {
				// TODO optimizeTBox
				for( final ATermAppl known : getSymmetricCandidates( VarType.CLASS, dwLHS,
						dwRHS ) ) {
					for( final Set<ATermAppl> dependents : kb.getDisjointClasses( known ) ) {
						for( final ATermAppl dependent : dependents ) {
							runSymetricCheck( current, dwLHS, known, dwRHS, dependent, binding );
						}
					}
				}
			}
			else {
				log.finer( "Atom " + current
						+ "cannot be satisfied in any consistent ontology." );
			}
			break;

		case ComplementOf: // TODO implementation of downMonotonic vars
			final ATermAppl coLHS = arguments.get( 0 );
			final ATermAppl coRHS = arguments.get( 1 );

			if( !coLHS.equals( coRHS ) ) {
				// TODO optimizeTBox
				for( final ATermAppl known : getSymmetricCandidates( VarType.CLASS, coLHS,
						coRHS ) ) {
					for( final ATermAppl dependent : kb.getComplements( known ) ) {
						runSymetricCheck( current, coLHS, known, coRHS, dependent, binding );
					}
				}
			}
			else {
				log.finer( "Atom " + current
						+ "cannot be satisfied in any consistent ontology." );
			}
			break;

		// RBOX ATOMS
		case DirectSubPropertyOf:
			direct = true;
		case StrictSubPropertyOf:
			strict = true;
		case SubPropertyOf:
			final ATermAppl spLHS = arguments.get( 0 );
			final ATermAppl spRHS = arguments.get( 1 );

			if( spLHS.equals( spRHS ) ) {
				// TODO optimization for downMonotonic variables
				for( final ATermAppl ic : kb.getProperties() ) {
					runNext( binding, arguments, ic, ic );
				}
			}
			else {
				final boolean lhsDM = isDownMonotonic( spLHS );
				final boolean rhsDM = isDownMonotonic( spRHS );

				if( lhsDM || rhsDM ) {
					downMonotonic( kb.getRoleTaxonomy(true), kb.getProperties(), lhsDM, spLHS,
							spRHS, binding, direct, strict );
				}
				else {
					final Set<ATermAppl> spLhsCandidates;
					Set<ATermAppl> spRhsCandidates = null;

					if( !ATermUtils.isVar( spLHS ) ) {
						spLhsCandidates = Collections.singleton( spLHS );
						spRhsCandidates = flatten( kb.getSuperProperties( spLHS, direct ) );
						if( strict ) {
							spRhsCandidates.removeAll( kb.getEquivalentProperties( spLHS ) );
						}
						else {
							spRhsCandidates.add( spLHS );
						}
					}
					else if( !ATermUtils.isVar( spRHS ) ) {
						spRhsCandidates = Collections.singleton( spRHS );
						spLhsCandidates = flatten( kb.getSubProperties( spRHS, direct ) );
						if( strict ) {
							spLhsCandidates.removeAll( kb.getEquivalentProperties( spRHS ) );
						}
						else {
							spLhsCandidates.add( spRHS );
						}
					}
					else {
						spLhsCandidates = kb.getProperties();
					}
					boolean reload = (spRhsCandidates == null);
					for( final ATermAppl subject : spLhsCandidates ) {
						if( reload ) {
							spRhsCandidates = flatten( kb.getSuperProperties( subject, direct ) );
							if( strict ) {
								spRhsCandidates
										.removeAll( kb.getEquivalentProperties( subject ) );
							}
							else {
								spRhsCandidates.add( subject );
							}
						}
						for( final ATermAppl object : spRhsCandidates ) {
							runNext( binding, arguments, subject, object );
						}
					}
				}
			}
			break;

		case EquivalentProperty: // TODO implementation of downMonotonic
			// vars
			final ATermAppl eqpLHS = arguments.get( 0 );
			final ATermAppl eqpRHS = arguments.get( 1 );

			// TODO optimize - try just one - if success then take all
			// found
			// bindings and extend them for other equivalent classes as
			// well.
			// meanwhile just a simple check below
			for( final ATermAppl known : getSymmetricCandidates( VarType.PROPERTY, eqpLHS,
					eqpRHS ) ) {
				final Set<ATermAppl> dependents;

				if( eqpLHS.equals( eqpRHS ) ) {
					dependents = Collections.singleton( known );
				}
				else {
					dependents = kb.getEquivalentProperties( known );
				}

				for( final ATermAppl dependent : dependents ) {
					int size = result.size();
					runSymetricCheck( current, eqpLHS, known, eqpRHS, dependent, binding );
					if( result.size() == size ) {
						// no binding found, so that there is no need to
						// explore other equivalent classes - they fail
						// as
						// well.
						break;
					}

				}
			}
			break;

		case Domain:
			final ATermAppl domLHS = arguments.get( 0 );
			final ATermAppl domRHS = arguments.get( 1 );
			
			Collection<ATermAppl> domLhsCandidates;
			Collection<ATermAppl> domRhsCandidates;
			
			if ( !ATermUtils.isVar( domLHS ) ) {
				domLhsCandidates = Collections.singleton( domLHS );
			} else {
				domLhsCandidates = kb.getProperties();
			}
			
			if ( !ATermUtils.isVar( domRHS ) ) {
				domRhsCandidates = Collections.singleton( domRHS );
			} else {
				domRhsCandidates = kb.getAllClasses();
			}
			
			for ( ATermAppl prop : domLhsCandidates) {
				for ( ATermAppl cls : domRhsCandidates ) {
					//System.out.println("Checking dom(" + prop + ", " + cls + ")");
					if ( (kb.isDatatypeProperty( prop ) || kb.isObjectProperty( prop ))
							&& kb.hasDomain( prop, cls ) ) {
						runNext( binding, arguments, prop, cls );
					}
				}
			}
			
			break;
			
		case Range:
			final ATermAppl rangeLHS = arguments.get( 0 );
			final ATermAppl rangeRHS = arguments.get( 1 );
			
			Collection<ATermAppl> rangeLhsCandidates;
			Collection<ATermAppl> rangeRhsClassCandidates;
			Collection<ATermAppl> rangeRhsDTypeCandidates;
			
			if ( !ATermUtils.isVar( rangeLHS ) ) {
				rangeLhsCandidates = Collections.singleton( rangeLHS );
			} else {
				rangeLhsCandidates = kb.getProperties();
			}
			
			if ( !ATermUtils.isVar( rangeRHS ) ) {

				//System.out.println( "Bound range: " + rangeRHS );
				if ( kb.isDatatype( rangeRHS ) ) {
					rangeRhsClassCandidates = Collections.emptySet();
					rangeRhsDTypeCandidates = Collections.singleton( rangeRHS );
				} else {
					rangeRhsClassCandidates = Collections.singleton( rangeRHS );
					rangeRhsDTypeCandidates = Collections.emptySet();
				}
				
			} else {
				rangeRhsClassCandidates = kb.getAllClasses();
				// TODO : change the datatype reasoner to keep track of associated aterms.
				rangeRhsDTypeCandidates = new HashSet<ATermAppl>();
				for (ATermAppl dtype : kb.getDatatypeReasoner().listDataRanges() ) {
					rangeRhsDTypeCandidates.add( dtype );
				}
			}
			
			for ( ATermAppl prop : rangeLhsCandidates) {
				if( kb.isObjectProperty( prop ) ) {
					for( ATermAppl cls : rangeRhsClassCandidates ) {
						//System.out.println("Checking range(" + prop + ", " + cls + ")");
						if( kb.hasRange( prop, cls ) ) {
							runNext( binding, arguments, prop, cls );
						}
					}
				}
				else if ( kb.isDatatypeProperty( prop ) ) {
					for( ATermAppl dtype : rangeRhsDTypeCandidates ) {
						//System.out.println("Checking range(" + prop + ", " + dtype + ")");
						if ( kb.hasRange( prop, dtype ) ) {
							runNext( binding, arguments, prop, dtype );
						}
					}
				}
			}
			
			break;
			
		case InverseOf: // TODO implementation of downMonotonic vars
			final ATermAppl ioLHS = arguments.get( 0 );
			final ATermAppl ioRHS = arguments.get( 1 );

			if( ioLHS.equals( ioRHS ) ) {
				runAllPropertyChecks( current, arguments.get( 0 ), kb.getSymmetricProperties(),
						binding );
			}
			else {
				for( final ATermAppl known : getSymmetricCandidates( VarType.PROPERTY, ioLHS,
						ioRHS ) ) {
					// meanwhile workaround
					for( final ATermAppl dependent : kb.getInverses( known ) ) {
						runSymetricCheck( current, ioLHS, known, ioRHS, dependent, binding );
					}
				}
			}
			break;
			
		case Symmetric:
			runAllPropertyChecks( current, arguments.get( 0 ), kb.getSymmetricProperties(),
					binding );
			break;

		case Asymmetric:
			runAllPropertyChecks( current, arguments.get( 0 ), kb.getAsymmetricProperties(),
					binding );
			break;
		
		case Reflexive:
			runAllPropertyChecks( current, arguments.get( 0 ), kb.getReflexiveProperties(),
					binding );
			break;
			
		case Irreflexive:
			runAllPropertyChecks( current, arguments.get( 0 ), kb.getIrreflexiveProperties(),
					binding );
			break;
			
		case ObjectProperty:
			runAllPropertyChecks( current, arguments.get( 0 ), kb.getObjectProperties(),
					binding );
			break;

		case DatatypeProperty:
			runAllPropertyChecks( current, arguments.get( 0 ), kb.getDataProperties(), binding );
			break;

		case Functional:
			runAllPropertyChecks( current, arguments.get( 0 ), kb.getFunctionalProperties(),
					binding );
			break;

		case InverseFunctional:
			runAllPropertyChecks( current, arguments.get( 0 ), kb
					.getInverseFunctionalProperties(), binding );
			break;

		case Transitive:
			runAllPropertyChecks( current, arguments.get( 0 ), kb.getTransitiveProperties(),
					binding );
			break;

		case UndistVarCore:
			// TODO Core IF
			final CoreNewImpl core = (CoreNewImpl) current.apply( binding );

			final Collection<ATermAppl> distVars = core.getDistVars();

			if( distVars.isEmpty() ) {
				final Collection<ATermAppl> constants = core.getConstants();
				if( constants.isEmpty() ) {
					if( QueryEngine.execBooleanABoxQuery( core.getQuery() ) ) {
	                    result.add( binding );
					// throw new RuntimeException(
					// "The query contains neither dist vars, nor constants,
					// yet evaluated by the CombinedQueryEngine !!! ");
                    }
				}
				else {
					final ATermAppl c = constants.iterator().next();
					final ATermAppl clazz = core.getQuery().rollUpTo( c,
							Collections.<ATermAppl>emptySet(), STOP_ROLLING_ON_CONSTANTS );

					if( kb.isType( c, clazz ) ) {
						exec( binding );
					}
				}
			}
			else if( distVars.size() == 1 ) {
				final ATermAppl var = distVars.iterator().next();
				final ATermAppl c = core.getQuery().rollUpTo( var, Collections.<ATermAppl>emptySet(),
						STOP_ROLLING_ON_CONSTANTS );
				final Collection<ATermAppl> instances = kb.getInstances( c );

				for( final ATermAppl a : instances ) {
					final ResultBinding candidateBinding = binding.duplicate();
					candidateBinding.setValue( var, a );
					exec( candidateBinding );
				}
			}
			else {
				// TODO
				// if (distVars.size() == 2
				// && core.getUndistVars().size() == 1
				// && !kb.getExpressivity().hasNominal()
				// && !kb.getExpressivity().hasTransitivity()) {
				// // TODO 1. undist. var. in distinguished manner
				// // TODO 2. identify both DV's
				// }

				final CoreStrategy s = QueryEngine.getStrategy( current );

				switch ( s ) {
				case SIMPLE:
					execSimpleCore( oldQuery, binding, distVars );
					break;
				case ALLFAST:
					execAllFastCore( oldQuery, binding, distVars, core.getUndistVars() );
					break;
				default:
					throw new InternalReasonerException( "Unknown core strategy." );
				}
			}

			break;
						
		case NegativePropertyValue: {
			final ATermAppl s = arguments.get( 0 );
			final ATermAppl p = arguments.get( 1 );
			final ATermAppl o = arguments.get( 2 );
			
			if( ATermUtils.isVar( p ) ) {
				throw new UnsupportedQueryException(
						"NegativePropertyValue atom with a variable property not supported" );
			}
			if( ATermUtils.isVar( o ) && kb.isDatatypeProperty( p ) ) {
				throw new UnsupportedQueryException(
						"NegativePropertyValue atom with a datatype property and variable object not supported" );
			}
			
			if( ATermUtils.isVar( s ) ) {
				Set<ATermAppl> oValues = ATermUtils.isVar( o )
					? kb.getIndividuals()
					: Collections.singleton( o );
					
				for( ATermAppl oValue : oValues ) {					
					Set<ATermAppl> sValues = kb.getInstances( not( hasValue( p, oValue ) ) );
					for( ATermAppl sValue : sValues ) {
						runNext( binding, arguments, sValue, p, oValue );	
					}		
				}
			}
			else if( ATermUtils.isVar( o ) ) {
				Set<ATermAppl> oValues = kb.getInstances( not( hasValue( inv( p ), o ) ) );
				for( ATermAppl oValue : oValues ) {
					runNext( binding, arguments, s, p, oValue );	
				}									
			}
			else {
				if( kb.isType( s, hasValue( p, o ) ) ) {
	                exec( binding );
                }
			}
			
			break;	
		}
			
		case NotKnown: {
			Query newQuery = new QueryImpl( kb, true );
			for( QueryAtom atom : ((NotKnownQueryAtom) current).getAtoms() ) {
				newQuery.add( atom.apply( binding ) );
			}
			
			for( ATermAppl var : newQuery.getUndistVars() ) {
				newQuery.addDistVar( var, VarType.INDIVIDUAL );
			}
			
			QueryExec newEngine = new CombinedQueryEngine();
			
			boolean isNegationTrue = newEngine.exec( newQuery ).isEmpty();
			
			if( isNegationTrue ) {
	            exec( binding );
            }

			break;
		}
		
		case Union: {
			for( List<QueryAtom> atoms : ((UnionQueryAtom) current).getUnion() ) {
				Query newQuery = new QueryImpl( kb, true );			
				for( QueryAtom atom : atoms ) {
					newQuery.add( atom.apply( binding ) );
				}			
				for( ATermAppl var : newQuery.getUndistVars() ) {
					newQuery.addDistVar( var, VarType.INDIVIDUAL );
					newQuery.addResultVar( var );
				}
				
				QueryExec newEngine = new CombinedQueryEngine();
				
				QueryResult newResult = newEngine.exec( newQuery );	
				for( ResultBinding newBinding : newResult ) {
					newBinding.setValues( binding );
					exec( newBinding );
				}
			}
			break;
		}
		
		case Datatype:
			throw new UnsupportedQueryException( "Datatype atom not ground: "
					+ current );
		
		case propertyDisjointWith:
			final ATermAppl dwLHSp = arguments.get( 0 );
			final ATermAppl dwRHSp = arguments.get( 1 );

			if( !dwLHSp.equals( dwRHSp ) ) {
				// TODO optimizeTBox
				for( final ATermAppl known : getSymmetricCandidates( VarType.PROPERTY, dwLHSp,
						dwRHSp ) ) {
					for( final Set<ATermAppl> dependents : kb.getDisjointProperties( known ) ) {
						for( final ATermAppl dependent : dependents ) {
							runSymetricCheck( current, dwLHSp, known, dwRHSp, dependent, binding );
						}
					}
				}
			}
			else {
				log.finer( "Atom " + current
						+ "cannot be satisfied in any consistent ontology." );
			}
			break;
		default:
			throw new UnsupportedQueryException( "Unknown atom type '"
					+ current.getPredicate() + "'." );

		}		
	}

	private boolean	STOP_ROLLING_ON_CONSTANTS	= false;

	private void execSimpleCore(final Query q, final ResultBinding binding,
			final Collection<ATermAppl> distVars) {
		final Map<ATermAppl, Set<ATermAppl>> varBindings = new HashMap<ATermAppl, Set<ATermAppl>>();

		final KnowledgeBase kb = q.getKB();

		for( final ATermAppl currVar : distVars ) {
			ATermAppl rolledUpClass = q.rollUpTo( currVar, Collections.<ATermAppl>emptySet(),
					STOP_ROLLING_ON_CONSTANTS );

			if( log.isLoggable( Level.FINER ) ) {
				log.finer( currVar + " rolled to " + rolledUpClass );
			}

			Set<ATermAppl> inst = kb.getInstances( rolledUpClass );
			varBindings.put( currVar, inst );
		}

		if( log.isLoggable( Level.FINER ) ) {
			log.finer( "Var bindings: " + varBindings );
		}

		final Set<ATermAppl> literalVars = q.getDistVarsForType( VarType.LITERAL );
		final Set<ATermAppl> individualVars = q.getDistVarsForType( VarType.INDIVIDUAL );

		boolean hasLiterals = !individualVars.containsAll( literalVars );

		for( final Iterator<ResultBinding> i = new BindingIterator( varBindings ); i.hasNext(); ) {
			final ResultBinding candidate = i.next().duplicate();
			candidate.setValues( binding );
			if( hasLiterals ) {
				for( final Iterator<ResultBinding> l = new LiteralIterator( q, candidate ); l
						.hasNext(); ) {
					final ResultBinding mappy = binding.duplicate();
					mappy.setValues( l.next() );
					if( QueryEngine.execBooleanABoxQuery( q.apply( mappy ) ) ) {
						exec( mappy );
					}
				}
			}
			else {
				if( QueryEngine.execBooleanABoxQuery( q.apply( candidate ) ) ) {
					exec( candidate );
				}
			}
		}
	}

	private Map<ATermAppl, Boolean> fastPrune(final Query q, final ATermAppl var) {
		// final Collection<ATermAppl> instances = new HashSet<ATermAppl>(kb
		// .getIndividuals());
		//
		// final KnowledgeBase kb = q.getKB();
		//
		//		
		//		
		// for (final QueryAtom atom : q.findAtoms(QueryPredicate.PropertyValue,
		// node, null, null)) {
		// instances.retainAll(kb.retrieveIndividualsWithProperty(atom
		// .getArguments().get(1)));
		// }
		// for (final QueryAtom atom : q.findAtoms(QueryPredicate.PropertyValue,
		// null, null, node)) {
		// instances.retainAll(kb.retrieveIndividualsWithProperty(ATermUtils
		// .makeInv(atom.getArguments().get(1))));
		// }
		// return instances;

		// final ATermAppl c = q.rollUpTo(var, Collections.EMPTY_SET, false);
		//
		// CandidateSet set = kb.getABox().getObviousInstances(c);
		// log.fine(c + " : " + set.getKnowns().size() + " : "
		// + set.getUnknowns().size());
		//
		// if (set.getUnknowns().isEmpty()) {
		// return set.getKnowns();
		// } else {
		// return kb.getInstances(q
		// .rollUpTo(var, Collections.EMPTY_SET, false));
		// }

		// return kb.getIndividuals();

		final ATermAppl c = q.rollUpTo( var, Collections.<ATermAppl>emptySet(), STOP_ROLLING_ON_CONSTANTS );
		if( log.isLoggable( Level.FINER ) ) {
			log.finer( var + " rolled to " + c );
		}

		CandidateSet<ATermAppl> set = kb.getABox().getObviousInstances( c );

		final Map<ATermAppl, Boolean> map = new HashMap<ATermAppl, Boolean>();

		for( final Object o : set.getKnowns() ) {
			map.put( (ATermAppl) o, true );
		}

		for( final Object o : set.getUnknowns() ) {
			map.put( (ATermAppl) o, false );
		}

		return map;
	}

	private void execAllFastCore(final Query q, final ResultBinding binding,
			final Collection<ATermAppl> distVars, final Collection<ATermAppl> undistVars) {
		if( distVars.isEmpty() ) {
			exec( binding );
		}
		else {
			final ATermAppl var = distVars.iterator().next();
			distVars.remove( var );

			final Map<ATermAppl, Boolean> instances = fastPrune( q, var );

			for( final Entry<ATermAppl, Boolean> entry : instances.entrySet() ) {
				ATermAppl b = entry.getKey();
				final ResultBinding newBinding = binding.duplicate();

				newBinding.setValue( var, b );
				final Query q2 = q.apply( newBinding );

				if( entry.getValue() || QueryEngine.execBooleanABoxQuery( q2 ) ) {
					execAllFastCore( q2, newBinding, distVars, undistVars );
				}
			}

			distVars.add( var );
		}
	}

	// private void execIteratedCore(final Query q, final ResultBinding binding,
	// final Collection<ATermAppl> distVars, CoreStrategy strategy) {
	// if (distVars.isEmpty()) {
	// exec(binding);
	// } else {
	// final ATermAppl var = distVars.iterator().next();
	// distVars.remove(var);
	//
	// boolean loadAll = (distVars.isEmpty() && !strategy
	// .equals(CoreStrategy.ALLFAST))
	// || strategy.equals(CoreStrategy.OPTIMIZED);
	//
	// final Collection<ATermAppl> instances;
	//
	// final KnowledgeBase kb = q.getKB();
	//
	// if (loadAll) {
	// final ATermAppl clazz = q.rollUpTo(var, Collections.EMPTY_SET,
	// false);
	//
	// if (log.isLoggable( Level.FINE )) {
	// log
	// .debug("Rolling up " + var + " to " + clazz
	// + " in " + q);
	// }
	//
	// instances = kb.getInstances(clazz);
	// } else {
	// instances = new HashSet<ATermAppl>(kb.getIndividuals());
	// for (final QueryAtom atom : q.findAtoms(
	// QueryPredicate.PropertyValue, var, null, null)) {
	// instances.retainAll(kb.retrieveIndividualsWithProperty(atom
	// .getArguments().get(1)));
	// }
	// for (final QueryAtom atom : q.findAtoms(
	// QueryPredicate.PropertyValue, null, null, var)) {
	// instances.retainAll(kb
	// .retrieveIndividualsWithProperty(ATermUtils
	// .makeInv(atom.getArguments().get(1))));
	// }
	//
	// }
	//
	// if (strategy.equals(CoreStrategy.FIRSTFAST)) {
	// strategy = CoreStrategy.OPTIMIZED;
	// }
	//
	// for (final ATermAppl b : instances) {
	// if (log.isLoggable( Level.FINE )) {
	// log.fine("trying " + var + " --> " + b);
	// }
	// final ResultBinding newBinding = binding.clone();
	//
	// newBinding.setValue(var, b);
	// final Query q2 = q.apply(newBinding);
	//
	// if (!loadAll || QueryEngine.execBooleanABoxQuery(q2)) {
	// execIteratedCore(q2, newBinding, distVars, strategy);
	// }
	// }
	//
	// distVars.add(var);
	// }
	// }

	private void downMonotonic(final Taxonomy<ATermAppl> taxonomy, final Collection<ATermAppl> all,
			final boolean lhsDM, final ATermAppl lhs, final ATermAppl rhs,
			final ResultBinding binding, boolean direct, boolean strict) {
		final ATermAppl downMonotonic = lhsDM
			? lhs
			: rhs;
		final ATermAppl theOther = lhsDM
			? rhs
			: lhs;
		Collection<ATermAppl> candidates;

		if( ATermUtils.isVar( theOther ) ) {
			candidates = all;
			// TODO more refined evaluation in case that both
			// variables are down-monotonic
		}
		else {
			final ATermAppl top = lhsDM
				? rhs
				: taxonomy.getTop().getName();

			if( ATermUtils.isComplexClass( top ) ) {
				candidates = kb.getEquivalentClasses( top );

				if( !strict && candidates.isEmpty() ) {
					candidates = flatten( kb.getSubClasses( top, true ) );
				}
			}
			else {
				candidates = Collections.singleton( top );
			}
		}

		for( final ATermAppl candidate : candidates ) {
			final ResultBinding newBinding = binding.duplicate();

			if( ATermUtils.isVar( theOther ) ) {
				newBinding.setValue( theOther, candidate );
			}

			// final Set<ATermAppl> toDo = lhsDM ? taxonomy.getFlattenedSubs(
			// ATermUtils.normalize(candidate), direct) :
			// taxonomy.getFlattenedSupers(ATermUtils.normalize(candidate),
			// direct);

			final Set<ATermAppl> toDo = lhsDM
				? flatten( taxonomy.getSubs( candidate, direct ) )
				: flatten( taxonomy.getSupers( candidate, direct ) );

			if( strict ) {
				toDo.removeAll( taxonomy.getEquivalents( candidate ) );
			}
			else {
				toDo.add( candidate );
			}

			runRecursively( taxonomy, downMonotonic, candidate, newBinding, new HashSet<ATermAppl>(
					toDo ), direct, strict );
		}
	}

	private boolean isDownMonotonic(final ATermAppl scLHS) {
		// TODO more refined condition to allow optimization for other atoms as
		// well - Type and
		// PropertyValue as well.

		return PelletOptions.OPTIMIZE_DOWN_MONOTONIC && downMonotonic.contains( scLHS );
	}

	private void runNext(final ResultBinding binding, final List<ATermAppl> arguments,
			final ATermAppl... values) {

		final ResultBinding candidateBinding = binding.duplicate();

		for( int i = 0; i < arguments.size(); i++ ) {
			if( ATermUtils.isVar( arguments.get( i ) ) ) {
				candidateBinding.setValue( arguments.get( i ), values[i] );
			}
		}

		exec( candidateBinding );
	}

	private Set<ATermAppl> getSymmetricCandidates(VarType forType, ATermAppl cA, ATermAppl cB) {
		final Set<ATermAppl> candidates;

		if( !ATermUtils.isVar( cA ) ) {
			candidates = Collections.singleton( cA );
		}
		else if( !ATermUtils.isVar( cB ) ) {
			candidates = Collections.singleton( cB );
		}
		else {
			switch ( forType ) {
			case CLASS:
				candidates = kb.getClasses();
				break;
			case PROPERTY:
				candidates = kb.getProperties();
				break;
			case INDIVIDUAL:
				candidates = kb.getIndividuals();
				break;
			default:
				throw new RuntimeException( "Uknown variable type : " + forType );
			}
		}

		return candidates;
	}

	private void runRecursively(final Taxonomy<ATermAppl> t, final ATermAppl downMonotonic,
			final ATermAppl rootCandidate, final ResultBinding binding, final Set<ATermAppl> toDo,
			final boolean direct, final boolean strict) {
		int size = result.size();

		if( log.isLoggable( Level.FINE ) ) {
			log.fine( "Trying : " + rootCandidate + ", done=" + toDo );
		}

		if( !strict ) {
			toDo.remove( rootCandidate );
			runNext( binding, Collections.singletonList( downMonotonic ), rootCandidate );
		}

		if( strict || result.size() > size ) {
			// final Set<ATermAppl> subs = t.getSFlattenedSubs(rootCandidate,
			// direct);
			final Set<ATermAppl> subs = flatten( t.getSubs( rootCandidate, direct ) );

			for( final ATermAppl subject : subs ) {
				if( !toDo.contains( subject ) ) {
					continue;
				}
				runRecursively( t, downMonotonic, subject, binding, toDo, false, false );
			}
		}
		else {
			if( log.isLoggable( Level.FINE ) ) {
				log.fine( "Skipping subs of " + rootCandidate );
			}
			// toDo.removeAll(t.getFlattenedSubs(rootCandidate, false));
			toDo.removeAll( flatten( t.getSubs( rootCandidate, false ) ) );
		}
	}

	private void runSymetricCheck(@SuppressWarnings("unused") QueryAtom current, ATermAppl cA, ATermAppl known, ATermAppl cB,
			ATermAppl dependent, ResultBinding binding) {
		final ResultBinding candidateBinding = binding.duplicate();

		if( !ATermUtils.isVar( cA ) ) {
			candidateBinding.setValue( cB, dependent );
		}
		else if( !ATermUtils.isVar( cB ) ) {
			candidateBinding.setValue( cA, dependent );
		}
		else {
			candidateBinding.setValue( cA, known );
			candidateBinding.setValue( cB, dependent );
		}

		exec( candidateBinding );
	}

	private void runAllPropertyChecks(@SuppressWarnings("unused") QueryAtom current, final ATermAppl var,
			final Set<ATermAppl> candidates, ResultBinding binding) {
		if( isDownMonotonic( var ) ) {
			for( final TaxonomyNode<ATermAppl> topNode : kb.getRoleTaxonomy(true).getTop().getSubs() ) {

				final ATermAppl top = topNode.getName();

				if( candidates.contains( top ) ) {
					runRecursively( kb.getRoleTaxonomy(true), var, topNode.getName(), binding,
							new HashSet<ATermAppl>( candidates ), false, false );
				}
			}
		}
		else {
			for( final ATermAppl candidate : candidates ) {
				final ResultBinding candidateBinding = binding.duplicate();

				candidateBinding.setValue( var, candidate );

				exec( candidateBinding );
			}
		}
	}

	private Set<ATermAppl> flatten(final Set<Set<ATermAppl>> set) {
		final Set<ATermAppl> result = new HashSet<ATermAppl>();

		for( final Set<ATermAppl> set2 : set ) {
			for( final ATermAppl a : set2 ) {
				result.add( a );
			}
		}

		return result;
	}
}
