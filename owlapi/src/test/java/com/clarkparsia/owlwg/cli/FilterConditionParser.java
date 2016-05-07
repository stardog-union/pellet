package com.clarkparsia.owlwg.cli;


import static java.lang.String.format;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clarkparsia.owlwg.testcase.Semantics;
import com.clarkparsia.owlwg.testcase.SyntaxConstraint;
import com.clarkparsia.owlwg.testcase.filter.ConjunctionFilter;
import com.clarkparsia.owlwg.testcase.filter.DisjunctionFilter;
import com.clarkparsia.owlwg.testcase.filter.FilterCondition;
import com.clarkparsia.owlwg.testcase.filter.NegationFilter;
import com.clarkparsia.owlwg.testcase.filter.SatisfiedSyntaxConstraintFilter;
import com.clarkparsia.owlwg.testcase.filter.SemanticsFilter;
import com.clarkparsia.owlwg.testcase.filter.StatusFilter;
import com.clarkparsia.owlwg.testcase.filter.UnsatisfiedSyntaxConstraintFilter;

/**
 * <p>
 * Title: Filter Condition Parser
 * </p>
 * <p>
 * Description: Create a filter condition from a string
 * </p>
 * <p>
 * Copyright: Copyright &copy; 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <a
 * href="http://clarkparsia.com/"/>http://clarkparsia.com/</a>
 * </p>
 * 
 * @author Mike Smith &lt;msmith@clarkparsia.com&gt;
 */
public class FilterConditionParser {

	private static final Logger	log;

	static {
		log = Logger.getLogger( FilterConditionParser.class.getCanonicalName() );
	}

	public static FilterCondition parse(String filterString) {

		FilterCondition filter;
		LinkedList<FilterCondition> filterStack = new LinkedList<>();
		String[] splits = filterString.split( "\\s" );
		for( int i = 0; i < splits.length; i++ ) {
			if( splits[i].equalsIgnoreCase( "and" ) ) {
				ConjunctionFilter and = ConjunctionFilter.and( filterStack );
				filterStack.clear();
				filterStack.add( and );
			}
			else if( splits[i].equalsIgnoreCase( "approved" ) ) {
				filterStack.add( StatusFilter.APPROVED );
			}
			else if( splits[i].equalsIgnoreCase( "direct" ) ) {
				filterStack.add( new SemanticsFilter( Semantics.DIRECT ) );
			}
			else if( splits[i].equalsIgnoreCase( "dl" ) ) {
				filterStack.add( SatisfiedSyntaxConstraintFilter.DL );
			}
			else if( splits[i].equalsIgnoreCase( "!dl" ) ) {
				filterStack.add( new UnsatisfiedSyntaxConstraintFilter( SyntaxConstraint.DL ) );
			}
			else if( splits[i].equalsIgnoreCase( "el" ) ) {
				filterStack.add( SatisfiedSyntaxConstraintFilter.EL );
			}
			else if( splits[i].equalsIgnoreCase( "!el" ) ) {
				filterStack.add( new UnsatisfiedSyntaxConstraintFilter( SyntaxConstraint.EL ) );
			}
			else if( splits[i].equalsIgnoreCase( "extracredit" ) ) {
				filterStack.add( StatusFilter.EXTRACREDIT );
			}
			else if( splits[i].equalsIgnoreCase( "not" ) ) {
				FilterCondition a = filterStack.removeLast();
				filterStack.add( NegationFilter.not( a ) );
			}
			else if( splits[i].equalsIgnoreCase( "or" ) ) {
				DisjunctionFilter or = DisjunctionFilter.or( filterStack );
				filterStack.clear();
				filterStack.add( or );
			}
			else if( splits[i].equalsIgnoreCase( "proposed" ) ) {
				filterStack.add( StatusFilter.PROPOSED );
			}
			else if( splits[i].equalsIgnoreCase( "ql" ) ) {
				filterStack.add( SatisfiedSyntaxConstraintFilter.QL );
			}
			else if( splits[i].equalsIgnoreCase( "!ql" ) ) {
				filterStack.add( new UnsatisfiedSyntaxConstraintFilter( SyntaxConstraint.QL ) );
			}
			else if( splits[i].equalsIgnoreCase( "rdf" ) ) {
				filterStack.add( new SemanticsFilter( Semantics.RDF ) );
			}
			else if( splits[i].equalsIgnoreCase( "rejected" ) ) {
				filterStack.add( StatusFilter.REJECTED );
			}
			else if( splits[i].equalsIgnoreCase( "rl" ) ) {
				filterStack.add( SatisfiedSyntaxConstraintFilter.RL );
			}
			else if( splits[i].equalsIgnoreCase( "!rl" ) ) {
				filterStack.add( new UnsatisfiedSyntaxConstraintFilter( SyntaxConstraint.RL ) );
			}
			else {
				final String msg = format( "Unexpected filter condition argument: \"%s\"",
						splits[i] );
				log.severe( msg );
				throw new IllegalArgumentException( msg );
			}
		}
		if( filterStack.isEmpty() ) {
			final String msg = format(
					"Missing valid filter condition. Filter option argument: \"%s\"", filterString );
			log.severe( msg );
			throw new IllegalArgumentException( msg );
		}
		if( filterStack.size() > 1 ) {
			final String msg = format(
					"Filter conditions do not parse to a single condition. Final parse stack: \"%s\"",
					filterStack );
			log.severe( msg );
			throw new IllegalArgumentException( msg );
		}

		filter = filterStack.iterator().next();
		if( log.isLoggable( Level.FINE ) )
			log.fine( format( "Filter condition: \"%s\"", filter ) );
		return filter;
	}

}
