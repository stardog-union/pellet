// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package profiler;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

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
public class ResultList<T> {
	private int colCount;

	private int	colWidth			= 8;
	
	private Map<String, Collection<Result<T>>>	results	= new LinkedHashMap<String, Collection<Result<T>>>();
		
	public ResultList(int colCount, int colWidth) {
		this.colCount = colCount;
		this.colWidth = colWidth;
	}

	public void addResult(String name, Collection<Result<T>> currResults) {
		name = ProfileUtils.formatFileName( name, 2 * colWidth );
		Collection<Result<T>> prevResults = results.get( name );
		if( prevResults == null ) {
			results.put( name, currResults );
		}
		else {
			Iterator<Result<T>> prev = prevResults.iterator();
			Iterator<Result<T>> curr = currResults.iterator();
			
			while( prev.hasNext() ) {
				prev.next().addIteration( curr.next() );
			}
		}
	}

	public void print() {
		printHeader( results.values().iterator().next() );

		for( Map.Entry<String, Collection<Result<T>>> entry : results.entrySet() ) {
			String name = entry.getKey();
			Collection<Result<T>> result = entry.getValue();

			printDataset( name, result );
		}

		System.out.println();
	}

	private void printDataset(String name, Collection<Result<T>> results) {
		System.out.format( "%-" + 2 * colWidth + "s|", name );
		for( Result<T> result : results ) {
			System.out.format( "%" + colWidth + ".2f |", result.getAvgTime() );
			if( colCount > 1 )
				System.out.format( "%" + colWidth + ".2f |", result.getAvgMemory() );
		}

		System.out.println();
	}

	private void printHeader(Collection<Result<T>> results) {
		System.out.println();
		System.out.println();
		System.out.format( "%-" + (2 * colWidth) + "s|", " " );

		int headerWidth = (colCount * colWidth);

		for( Result<T> result : results ) {
			String colHeader = result.getTask().toString();
			if( colHeader.length() > headerWidth )
				colHeader = colHeader.substring( 0, headerWidth - 1 ) + '.';
			System.out.format( " %-" + headerWidth + "s", colHeader );
			for( int i = 0; i < 2 * (colCount - 1); i++ ) {
				System.out.print( " " );
			}
			System.out.print( "|" );
		}
		System.out.println();
		System.out.format( "%-" + (2 * colWidth) + "s|", " " );
		for( int i = 0; i < results.size(); i++ ) {
			System.out.format( " %-" + colWidth + "s|", "Time" );
			if( colCount > 1 )
				System.out.format( " %-" + colWidth + "s|", "Mem" );
		}
		System.out.println();
	}
}
