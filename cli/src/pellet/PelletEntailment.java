// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package pellet;

import static pellet.PelletCmdOptionArg.NONE;
import static pellet.PelletCmdOptionArg.REQUIRED;

import java.io.PrintWriter;
import java.util.Set;

import org.mindswap.pellet.utils.FileUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import com.clarkparsia.owlapi.explanation.io.manchester.ManchesterSyntaxObjectRenderer;
import com.clarkparsia.owlapi.explanation.io.manchester.TextBlockWriter;
import com.clarkparsia.pellet.owlapiv3.EntailmentChecker;
import com.clarkparsia.pellet.owlapiv3.OWLAPILoader;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;

/**
 * <p>
 * Title: PelletEntailment
 * </p>
 * <p>
 * Description: Given an input ontology check if the axioms in the output
 * ontology are all entailed. If not, report either the first non-entailment or
 * all non-entailments.
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
public class PelletEntailment extends PelletCmdApp {

	private String			entailmentFile;
	private boolean			findAll;

	public PelletEntailment() {
	}

	@Override
    public String getAppId() {
		return "PelletEntailment: Check if all axioms are entailed by the ontology";
	}

	@Override
    public String getAppCmd() {
		return "pellet entail " + getMandatoryOptions() + "[options] <file URI>...";
	}

	@Override
    public PelletCmdOptions getOptions() {
		PelletCmdOptions options = getGlobalOptions();

		options.add( getIgnoreImportsOption() );
		
		PelletCmdOption option = new PelletCmdOption( "entailment-file" );
		option.setShortOption( "e" );
		option.setType( "<file URI>" );
		option.setDescription( "Entailment ontology URI" );
		option.setIsMandatory( true );
		option.setArg( REQUIRED );
		options.add( option );

		option = new PelletCmdOption( "all" );
		option.setShortOption( "a" );
		option.setDefaultValue( false );
		option.setDescription( "Show all non-entailments" );
		option.setDefaultValue( findAll );
		option.setIsMandatory( false );
		option.setArg( NONE );
		options.add( option );

		return options;
	}

	@Override
    public void run() {
		entailmentFile = options.getOption( "entailment-file" ).getValueAsString();
		findAll = options.getOption( "all" ).getValueAsBoolean();
		
		OWLAPILoader loader = (OWLAPILoader) getLoader( "OWLAPIv3" );
		
		getKB();
		
		PelletReasoner reasoner = loader.getReasoner();
		
		OWLOntology entailmentOntology = null;
		try {
			verbose( "Loading entailment file: " );
			verbose( entailmentFile );
			IRI entailmentFileURI = IRI.create( FileUtils.toURI( entailmentFile ) );
			entailmentOntology = loader.getManager().loadOntology( entailmentFileURI );
		} catch( Exception e ) {
			throw new PelletCmdException( e );
		}
		
		EntailmentChecker checker = new EntailmentChecker(reasoner);		
		Set<OWLLogicalAxiom> axioms = entailmentOntology.getLogicalAxioms();
		
		verbose( "Check entailments for (" + axioms.size() + ") axioms" );
		startTask( "Checking" );
		Set<OWLAxiom> nonEntailments = checker.findNonEntailments(axioms, findAll);		
		finishTask( "Checking" );
		
		if( nonEntailments.isEmpty() ) {
			output( "All axioms are entailed." );
		}
		else {
			output( "Non-entailments (" + nonEntailments.size() + "): " );
			
			int index = 1;
			TextBlockWriter writer = new TextBlockWriter( new PrintWriter( System.out ) );
			ManchesterSyntaxObjectRenderer renderer = new ManchesterSyntaxObjectRenderer( writer );
			writer.println();
			for( OWLAxiom axiom : nonEntailments ) {
				writer.print(index++);
				writer.print(")");
				writer.printSpace();

				writer.startBlock();
				axiom.accept( renderer );
				writer.endBlock();
				writer.println();
            }
			writer.flush();
		}
	}

}
