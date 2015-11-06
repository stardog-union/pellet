// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;
import aterm.ATermList;

import com.clarkparsia.pellet.utils.TermFactory;

/**
 * <p>
 * Title: Default implementation of the {@link Query}
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
 * @author Petr Kremen
 */
public class QueryImpl implements Query {
	private static final ATermAppl				DEFAULT_NAME	= TermFactory.term( "query" );

	// COMMON PART
	private ATermAppl							name			= DEFAULT_NAME;

	private List<QueryAtom>						allAtoms;

	private KnowledgeBase						kb;

	private List<ATermAppl>						resultVars;

	private Set<ATermAppl>						allVars;

	private Set<ATermAppl>						individualsAndLiterals;

	private boolean								ground;

	private boolean								distinct;

	private Filter								filter;

	private QueryParameters						parameters;

	// VARIABLES
	private EnumMap<VarType, Set<ATermAppl>>	distVars;

	public QueryImpl(final KnowledgeBase kb, final boolean distinct) {
		this.kb = kb;

		this.ground = true;
		this.allAtoms = new ArrayList<QueryAtom>();
		this.resultVars = new ArrayList<ATermAppl>();
		this.allVars = new HashSet<ATermAppl>();
		this.individualsAndLiterals = new HashSet<ATermAppl>();
		this.distVars = new EnumMap<VarType, Set<ATermAppl>>( VarType.class );

		for( final VarType type : VarType.values() ) {
			distVars.put( type, new HashSet<ATermAppl>() );
		}

		this.distinct = distinct;
	}

	public QueryImpl(final Query query) {
		this( query.getKB(), query.isDistinct() );

		this.name = query.getName();
		this.parameters = query.getQueryParameters();
	}

	/**
	 * {@inheritDoc}
	 */
	public void add(final QueryAtom atom) {
		if( allAtoms.contains( atom ) ) {
			return;
		}
		allAtoms.add( atom );

		for( final ATermAppl a : atom.getArguments() ) {
			if( ATermUtils.isVar( a ) ) {
				if( !allVars.contains( a ) ) {
					allVars.add( a );
				}
			}
			else if( ATermUtils.isLiteral( a ) || kb.isIndividual( a ) ) {
				if( !individualsAndLiterals.contains( a ) ) {
					individualsAndLiterals.add( a );
				}
			}
		}

		ground = ground && atom.isGround();
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ATermAppl> getDistVarsForType(final VarType type) {
		return distVars.get( type );
	}

	/**
	 * {@inheritDoc}
	 */
	public void addDistVar(ATermAppl a, final VarType type) {
		Set<ATermAppl> set = distVars.get( type );

		if( !set.contains( a ) ) {
			set.add( a );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addResultVar(ATermAppl a) {
		resultVars.add( a );
	}

	/**
	 * {@inheritDoc}
	 */
	public List<QueryAtom> getAtoms() {
		return Collections.unmodifiableList( allAtoms );
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ATermAppl> getConstants() {
		return Collections.unmodifiableSet( individualsAndLiterals );
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ATermAppl> getDistVars() {
		final Set<ATermAppl> result = new HashSet<ATermAppl>();

		for( final VarType t : VarType.values() ) {
			result.addAll( distVars.get( t ) );
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ATermAppl> getUndistVars() {
		final Set<ATermAppl> result = new HashSet<ATermAppl>( allVars );

		result.removeAll( getDistVars() );

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ATermAppl> getResultVars() {
		return Collections.unmodifiableList( resultVars );
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ATermAppl> getVars() {
		return Collections.unmodifiableSet( allVars );
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isGround() {
		return ground;
	}

	/**
	 * {@inheritDoc}
	 */
	public KnowledgeBase getKB() {
		return kb;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setKB(KnowledgeBase kb) {
		this.kb = kb;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Query apply(final ResultBinding binding) {
		final List<QueryAtom> atoms = new ArrayList<QueryAtom>();

		for( final QueryAtom atom : getAtoms() ) {
			atoms.add( atom.apply( binding ) );
		}

		final QueryImpl query = new QueryImpl( this );

		query.resultVars.addAll( this.resultVars );
		query.resultVars.removeAll( binding.getAllVariables() );

		for( final VarType type : VarType.values() ) {
			for( final ATermAppl atom : getDistVarsForType( type ) ) {
				if( !binding.isBound( atom ) ) {
					query.addDistVar( atom, type );
				}
			}
		}

		for( final QueryAtom atom : atoms ) {
			query.add( atom );
		}

		return query;
	}

	/**
	 * {@inheritDoc} TODO
	 */
	public ATermAppl rollUpTo(final ATermAppl var, final Collection<ATermAppl> stopList,
			final boolean stopOnConstants) {
		if( getDistVarsForType( VarType.LITERAL ).contains( var )
				&& !getDistVarsForType( VarType.INDIVIDUAL ).contains( var )
				&& !individualsAndLiterals.contains( var ) ) {
			throw new InternalReasonerException( "Trying to roll up to the variable '" + var
					+ "' which is not distinguished and individual." );
		}

		ATermList classParts = ATermUtils.EMPTY_LIST;

		final Set<ATermAppl> visited = new HashSet<ATermAppl>();

		if( stopOnConstants ) {
			visited.addAll( getConstants() );
		}

		final Collection<QueryAtom> inEdges = findAtoms( QueryPredicate.PropertyValue, null, null,
				var );
		for( final QueryAtom a : inEdges ) {
			classParts = classParts.append( rollEdgeIn( QueryPredicate.PropertyValue, a, visited,
					stopList ) );
		}

		final Collection<QueryAtom> outEdges = findAtoms( QueryPredicate.PropertyValue, var, null,
				null );
		for( final QueryAtom a : outEdges ) {
			classParts = classParts.append( rollEdgeOut( QueryPredicate.PropertyValue, a, visited,
					stopList ) );
		}

		classParts = classParts.concat( getClasses( var ) );

		return ATermUtils.makeAnd( classParts );
	}

	// TODO optimize - cache
	private ATermList getClasses(final ATermAppl a) {
		final List<ATermAppl> aterms = new ArrayList<ATermAppl>();

		for( final QueryAtom atom : findAtoms( QueryPredicate.Type, a, null ) ) {
			final ATermAppl arg = atom.getArguments().get( 1 );
			if( ATermUtils.isVar( arg ) ) {
				throw new InternalReasonerException(
						"Variables as predicates are not supported yet" );
			}
			aterms.add( arg );
		}

		if( !ATermUtils.isVar( a ) ) {
			aterms.add( ATermUtils.makeValue( a ) );
		}

		return ATermUtils.makeList( aterms );
	}

	/**
	 * TODO
	 */
	private ATermAppl rollEdgeOut(final QueryPredicate allowed, final QueryAtom atom,
			final Set<ATermAppl> visited, final Collection<ATermAppl> stopList) {
		switch ( atom.getPredicate() ) {
		case PropertyValue:
			final ATermAppl subj = atom.getArguments().get( 0 );
			final ATermAppl pred = atom.getArguments().get( 1 );
			final ATermAppl obj = atom.getArguments().get( 2 );

			if( ATermUtils.isVar( pred ) ) {
				// variables as predicates are not supported yet.
				return ATermUtils.TOP;
			}

			visited.add( subj );

			if( visited.contains( obj ) ) {
				ATermList temp = getClasses( obj );
				if( temp.getLength() == 0 ) {
					if( kb.isDatatypeProperty( pred ) )
						// return ATermUtils.makeSoMin(pred, 1,
						// ATermUtils.TOP_LIT);
						return ATermUtils.makeSomeValues( pred, ATermUtils.TOP_LIT );
					else
						return ATermUtils.makeSomeValues( pred, ATermUtils.TOP );
				}
				else {
					return ATermUtils.makeSomeValues( pred, ATermUtils.makeAnd( temp ) );
				}

			}

			if( ATermUtils.isLiteral( obj ) ) {
				ATermAppl type = ATermUtils.makeValue( obj );
				return ATermUtils.makeSomeValues( pred, type );
			}

			// TODO
			// else if (litVars.contains(obj)) {
			// Datatype dtype = getDatatype(obj);
			//
			// return ATermUtils.makeSomeValues(pred, dtype.getName());
			// }

			ATermList targetClasses = getClasses( obj );

			for( final QueryAtom in : _findAtoms( stopList, allowed, null, null, obj ) ) {
				if( !in.equals( atom ) ) {
					targetClasses = targetClasses.append( rollEdgeIn( allowed, in, visited,
							stopList ) );
				}
			}

			final List<QueryAtom> targetOuts = _findAtoms( stopList, allowed, obj, null, null );

			if( targetClasses.isEmpty() ) {
				if( targetOuts.size() == 0 ) {
					// this is a simple leaf node
					if( kb.isDatatypeProperty( pred ) )
						return ATermUtils.makeSomeValues( pred, ATermUtils.TOP_LIT );
					else
						return ATermUtils.makeSomeValues( pred, ATermUtils.TOP );
				}
				else {
					// not a leaf node, recurse over all outgoing edges
					ATermList outs = ATermUtils.EMPTY_LIST;

					for( final QueryAtom currEdge : targetOuts ) {
						outs = outs.append( rollEdgeOut( allowed, currEdge, visited, stopList ) );
					}

					return ATermUtils.makeSomeValues( pred, ATermUtils.makeAnd( outs ) );
				}
			}
			else {
				if( targetOuts.size() == 0 ) {
					// this is a simple leaf node, but with classes specified
					return ATermUtils.makeSomeValues( pred, ATermUtils.makeAnd( targetClasses ) );
				}
				else {
					// not a leaf node, recurse over all outgoing edges
					ATermList outs = ATermUtils.EMPTY_LIST;

					for( final QueryAtom currEdge : targetOuts ) {
						outs = outs.append( rollEdgeOut( allowed, currEdge, visited, stopList ) );
					}

					for( int i = 0; i < targetClasses.getLength(); i++ ) {
						outs = outs.append( targetClasses.elementAt( i ) );
					}

					return ATermUtils.makeSomeValues( pred, ATermUtils.makeAnd( outs ) );

				}
			}
		default:
			throw new RuntimeException( "This atom cannot be included to rolling-up : " + atom );
		}
	}

	// TODO this should die if called on a literal node
	private ATermAppl rollEdgeIn(final QueryPredicate allowed, final QueryAtom atom,
			final Set<ATermAppl> visited, final Collection<ATermAppl> stopList) {
		switch ( atom.getPredicate() ) {
		case PropertyValue:
			final ATermAppl subj = atom.getArguments().get( 0 );
			final ATermAppl pred = atom.getArguments().get( 1 );
			final ATermAppl obj = atom.getArguments().get( 2 );
			ATermAppl invPred = kb.getRBox().getRole( pred ).getInverse().getName();

			if( ATermUtils.isVar( pred ) ) {
				throw new InternalReasonerException(
						"Variables as predicates are not supported yet" );
				// // TODO variables as predicates are not supported yet.
				// return ATermUtils.TOP;
			}

			visited.add( obj );

			if( visited.contains( subj ) ) {
				ATermList temp = getClasses( subj );
				if( temp.getLength() == 0 ) {
					if( kb.isDatatypeProperty( invPred ) )
						return ATermUtils.makeSomeValues( invPred, ATermUtils.TOP_LIT );
					else
						return ATermUtils.makeSomeValues( invPred, ATermUtils.TOP );
				}
				else {
					return ATermUtils.makeSomeValues( invPred, ATermUtils.makeAnd( temp ) );
				}
			}

			ATermList targetClasses = getClasses( subj );

			final List<QueryAtom> targetIns = _findAtoms( stopList, allowed, null, null, subj );

			for( final QueryAtom o : _findAtoms( stopList, allowed, subj, null, null ) ) {
				if( !o.equals( atom ) ) {
					targetClasses = targetClasses.append( rollEdgeOut( allowed, o, visited,
							stopList ) );
				}
			}

			if( targetClasses.isEmpty() ) {
				if( targetIns.isEmpty() ) {
					// this is a simple leaf node
					if( kb.isDatatypeProperty( pred ) )
						return ATermUtils.makeSomeValues( invPred, ATermUtils.TOP_LIT );
					else
						return ATermUtils.makeSomeValues( invPred, ATermUtils.TOP );
				}
				else {
					// not a leaf node, recurse over all incoming edges
					ATermList ins = ATermUtils.EMPTY_LIST;

					for( QueryAtom currEdge : targetIns ) {
						ins = ins.append( rollEdgeIn( allowed, currEdge, visited, stopList ) );
					}

					return ATermUtils.makeSomeValues( invPred, ATermUtils.makeAnd( ins ) );
				}
			}
			else {
				if( targetIns.isEmpty() ) {
					// this is a simple leaf node, but with classes specified

					return ATermUtils.makeSomeValues( invPred, ATermUtils.makeAnd( targetClasses ) );
				}
				else {
					// not a leaf node, recurse over all outgoing edges
					ATermList ins = ATermUtils.EMPTY_LIST;

					for( QueryAtom currEdge : targetIns ) {
						ins = ins.append( rollEdgeIn( allowed, currEdge, visited, stopList ) );
					}

					for( int i = 0; i < targetClasses.getLength(); i++ ) {
						ins = ins.append( targetClasses.elementAt( i ) );
					}

					return ATermUtils.makeSomeValues( invPred, ATermUtils.makeAnd( ins ) );

				}
			}
		default:
			throw new RuntimeException( "This atom cannot be included to rolling-up : " + atom );

		}
	}

	private List<QueryAtom> _findAtoms(final Collection<ATermAppl> stopList,
			final QueryPredicate predicate, final ATermAppl... args) {
		final List<QueryAtom> list = new ArrayList<QueryAtom>();
		for( final QueryAtom atom : allAtoms ) {
			if( predicate.equals( atom.getPredicate() ) ) {
				int i = 0;
				boolean add = true;
				for( final ATermAppl arg : atom.getArguments() ) {
					final ATermAppl argValue = args[i++];
					if( (argValue != null && argValue != arg) || stopList.contains( arg ) ) {
						add = false;
						break;
					}
				}

				if( add ) {
					list.add( atom );
				}
			}
		}
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<QueryAtom> findAtoms(final QueryPredicate predicate, final ATermAppl... args) {
		return _findAtoms( Collections.<ATermAppl>emptySet(), predicate, args );
	}

	/**
	 * {@inheritDoc}
	 */
	public Query reorder(int[] ordering) {
		if( ordering.length != allAtoms.size() ) {
			throw new InternalReasonerException(
					"Ordering permutation must be of the same size as the query : "
							+ ordering.length );
		}
		final QueryImpl newQuery = new QueryImpl( this );

		// shallow copies for faster processing
		for( int j = 0; j < ordering.length; j++ ) {
			newQuery.allAtoms.add( allAtoms.get( ordering[j] ) );
		}

		newQuery.allVars = allVars;
		newQuery.distVars = distVars;
		newQuery.individualsAndLiterals = individualsAndLiterals;
		newQuery.resultVars = resultVars;
		newQuery.ground = ground;

		return newQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove(QueryAtom atom) {
		if( !allAtoms.contains( atom ) ) {
			return;
		}

		allAtoms.remove( atom );

		final Set<ATermAppl> rest = new HashSet<ATermAppl>();

		boolean ground = true;

		for( final QueryAtom atom2 : allAtoms ) {
			ground &= atom2.isGround();
			rest.addAll( atom2.getArguments() );
		}

		this.ground = ground;

		final Set<ATermAppl> toRemove = new HashSet<ATermAppl>( atom.getArguments() );
		toRemove.removeAll( rest );

		for( final ATermAppl a : toRemove ) {
			allVars.remove( a );
			for (Entry<VarType, Set<ATermAppl>> entry : distVars.entrySet() ) {
				entry.getValue().remove( a );
			}
			resultVars.remove( a );
			individualsAndLiterals.remove( a );
		}
	}

	@Override
	public String toString() {
		return toString( false );
	}

	public String toString(boolean multiLine) {
		final String indent = multiLine
			? "     "
			: " ";
		final StringBuffer sb = new StringBuffer();

		sb.append( ATermUtils.toString( name ) + "(" );
		for( int i = 0; i < resultVars.size(); i++ ) {
			ATermAppl var = resultVars.get( i );
			if( i > 0 )
				sb.append( ", " );
			sb.append( ATermUtils.toString( var ) );
		}
		sb.append( ")" );

		if( allAtoms.size() > 0 ) {
			sb.append( " :-" );
			if( multiLine )
				sb.append( "\n" );
			for( int i = 0; i < allAtoms.size(); i++ ) {
				final QueryAtom a = allAtoms.get( i );
				if( i > 0 ) {
					sb.append( "," );
					if( multiLine )
						sb.append( "\n" );
				}

				sb.append( indent );
				sb.append( a.toString() ); // TODO qNameProvider
			}
		}

		sb.append( "." );
		if( multiLine )
			sb.append( "\n" );
		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDistinct() {
		return distinct;
	}

	/**
	 * {@inheritDoc}
	 */
	public Filter getFilter() {
		return filter;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public void setQueryParameters(QueryParameters parameters) {
		this.parameters = parameters;
	}

	public QueryParameters getQueryParameters() {
		return parameters;
	}

	public ATermAppl getName() {
		return name;
	}

	public void setName(ATermAppl name) {
		this.name = name;
	}
}
