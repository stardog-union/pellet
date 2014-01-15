// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package pellet;

import org.mindswap.pellet.KnowledgeBase;

/**
 * <p>
 * Title: PelletConsistency
 * </p>
 * <p>
 * Description: Check the consistency of an ontology
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
public class PelletConsistency extends PelletCmdApp {

	public PelletConsistency() {
	}

	@Override
	public String getAppCmd() {
		return "pellet consistency " + getMandatoryOptions() + "[options] <file URI>...";
	}

	@Override
	public String getAppId() {
		return "PelletConsistency: Check the consistency of an ontology";
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

		if( isConsistent )
			output( "Consistent: Yes" );
		else {
			output( "Consistent: No" );
			output( "Reason: " + kb.getExplanation() );
		}
	}

}
