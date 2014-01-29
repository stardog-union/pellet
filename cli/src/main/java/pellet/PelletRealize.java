// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package pellet;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.taxonomy.printer.ClassTreePrinter;
import org.mindswap.pellet.taxonomy.printer.TaxonomyPrinter;

import aterm.ATermAppl;

/**
 * <p>
 * Title: PelletRealize
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
public class PelletRealize extends PelletCmdApp {

	public PelletRealize() {
		super( );
	}

	@Override
	public String getAppCmd() {
		return "pellet realize " + getMandatoryOptions() + "[options] <file URI>...";
	}

	@Override
	public String getAppId() {
		return "PelletRealize: Compute and display the most specific instances for each class";
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

		startTask( "classification" );
		kb.classify();
		finishTask( "classification" );
		
		startTask( "realization" );
		kb.realize();
		finishTask( "realization" );

		TaxonomyPrinter<ATermAppl> printer = new ClassTreePrinter();
		printer.print( kb.getTaxonomy() );
	}

}
