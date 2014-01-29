// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package pellet;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.utils.Comparators;
import org.mindswap.pellet.utils.QNameProvider;
import org.mindswap.pellet.utils.progress.ProgressMonitor;

import aterm.ATermAppl;

/**
 * <p>
 * Title: PelletClassify
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Markus Stocker
 */
public class PelletUnsatisfiable extends PelletCmdApp {

	public PelletUnsatisfiable() {
		super( );
	}

	@Override
	public String getAppCmd() {
		return "pellet unsatisfiable " + getMandatoryOptions() + "[options] <file URI>...";
	}

	@Override
	public String getAppId() {
		return "PelletUnsatisfiable: Find the unsatisfiable classes in the ontology";
	}

	@Override
	public PelletCmdOptions getOptions() {
		PelletCmdOptions options = getGlobalOptions();
		
		options.add( getLoaderOption() );
		options.add( getIgnoreImportsOption() );
		options.add( getInputFormatOption() );

		return options;
	}

	@Override
	public void run() {	
		KnowledgeBase kb = getKB();

		startTask( "consistency check" );
		boolean isConsistent = kb.isConsistent();		
		finishTask( "consistency check" );

		if( !isConsistent )
			throw new PelletCmdException( "Ontology is inconsistent, run \"pellet explain\" to get the reason" );

		QNameProvider qnames = new QNameProvider();
		Set<String> unsatisfiableClasses = new TreeSet<String>( Comparators.stringComparator );
		
		ProgressMonitor monitor = PelletOptions.USE_CLASSIFICATION_MONITOR.create();
		monitor.setProgressTitle( "Finding unsatisfiable" );
		monitor.setProgressLength( kb.getClasses().size() );
		
		startTask( "find unsatisfiable" );
		monitor.taskStarted();
		
		Iterator<ATermAppl> i = kb.getClasses().iterator();
		while( i.hasNext() ) {
			monitor.incrementProgress();
			ATermAppl c = i.next();
			if( !kb.isSatisfiable( c ) ) {
				unsatisfiableClasses.add( qnames.shortForm( c.getName() ) );
			}
		}
		
		monitor.taskFinished();
		finishTask( "find unsatisfiable" );
		
		output("");
		if( unsatisfiableClasses.isEmpty() ) {
			output( "Found no unsatisfiable concepts." );			
		}
		else {
			output( "Found " + unsatisfiableClasses.size() + " unsatisfiable concept(s):" );
			
			for( String c : unsatisfiableClasses ) {
				output( c );
			}
		}
	}

}
