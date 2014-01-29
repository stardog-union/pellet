// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package pellet;

import static pellet.PelletCmdOptionArg.REQUIRED;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.mindswap.pellet.jena.ModelExtractor;
import org.mindswap.pellet.jena.ModelExtractor.StatementType;
import org.mindswap.pellet.utils.SetUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * <p>
 * Title: PelletExtractInferences
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
public class PelletExtractInferences extends PelletCmdApp {

	private EnumSet<StatementType>	selector;

	public PelletExtractInferences() {
	}

	public String getAppId() {
		return "PelletExtractInferences: Extract a set of inferences from an ontology";
	}

	public String getAppCmd() {
		return "pellet extract " + getMandatoryOptions() + "[options] <file URI>...";
	}

	public PelletCmdOptions getOptions() {
		PelletCmdOptions options = getGlobalOptions();

		PelletCmdOption option = new PelletCmdOption( "statements" );
		option.setShortOption( "s" );
		option
				.setDescription( "Statements to extract. The option accepts all axioms of the OWL functional syntax plus some additional ones. Valid arguments are: "
						+ validStatementArguments()
						+ ". Example: \"DirectSubClassOf DirectSubPropertyOf\"" );
		option.setType( "Space separated list surrounded by quotes" );
		option.setDefaultValue( "DefaultStatements" );
		option.setIsMandatory( false );
		option.setArg( REQUIRED );
		options.add( option );

		options.add( getLoaderOption() );
		options.add( getIgnoreImportsOption() );
		options.add( getInputFormatOption() );

		return options;
	}

	public void run() {
		mapStatementTypes();
		extractInferences();
	}

	private void extractInferences() {
		if( selector.size() == 0 )
			throw new PelletCmdException( "Selector is empty, provide types to extract" );

		ModelExtractor extractor = new ModelExtractor( getKB() );
		extractor.setSelector( selector );

		Model extracted = ModelFactory.createDefaultModel();
		
		if( SetUtils.intersects( selector, ModelExtractor.StatementType.ALL_CLASS_STATEMENTS ) ) {
			startTask( "Extracting class statements" );
			extractor.extractClassModel( extracted );
			finishTask( "Extracting class statements" );
		}

		if( SetUtils.intersects( selector, ModelExtractor.StatementType.ALL_PROPERTY_STATEMENTS ) ) {
			startTask( "Extracting property statements" );
			extractor.extractPropertyModel( extracted );
			finishTask( "Extracting property statements" );
		}

		if( SetUtils.intersects( selector, ModelExtractor.StatementType.ALL_INDIVIDUAL_STATEMENTS ) ) {
			startTask( "Extracting individual statements" );
			extractor.extractIndividualModel( extracted );
			finishTask( "Extracting individual statements" );
		}

		output( extracted );
	}

	private String validStatementArguments() {
		List<String> sa = new ArrayList<String>();

		sa.add( "DefaultStatements" );
		sa.add( "AllClass" );
		sa.add( "AllIndividual" );
		sa.add( "AllProperty" );
		sa.add( "AllStatements" );
		sa.add( "AllStatementsIncludingJena" );
		sa.add( "ClassAssertion" );
		sa.add( "ComplementOf" );
		sa.add( "DataPropertyAssertion" );
		sa.add( "DifferentIndividuals" );
		sa.add( "DirectClassAssertion" );
		sa.add( "DirectSubClassOf" );
		sa.add( "DirectSubPropertyOf" );
		sa.add( "DisjointClasses" );
		sa.add( "DisjointProperties" );
		sa.add( "EquivalentClasses" );
		sa.add( "EquivalentProperties" );
		sa.add( "InverseProperties" );
		sa.add( "ObjectPropertyAssertion" );
		sa.add( "PropertyAssertion" );
		sa.add( "SameIndividual" );
		sa.add( "SubClassOf" );
		sa.add( "SubPropertyOf" );

		return sa.toString();
	}

	private void mapStatementTypes() {
		String statements = options.getOption( "statements" ).getValueAsString();

		String[] list = statements.split( " " );

		if( list.length == 0 )
			throw new PelletCmdException( "No values for statements argument given" );

		for( int i = 0; i < list.length; i++ ) {
			String l = list[i];
			if( l.equalsIgnoreCase( "DefaultStatements" ) )
				selectorAddAll( StatementType.DEFAULT_STATEMENTS );
			else if( l.equalsIgnoreCase( "AllStatements" ) )
				selectorAddAll( StatementType.ALL_STATEMENTS );
			else if( l.equalsIgnoreCase( "AllStatementsIncludingJena" ) )
				selectorAddAll( StatementType.ALL_STATEMENTS_INCLUDING_JENA );
			else if( l.equalsIgnoreCase( "AllClass" ) )
				selectorAddAll( StatementType.ALL_CLASS_STATEMENTS );
			else if( l.equalsIgnoreCase( "AllIndividual" ) )
				selectorAddAll( StatementType.ALL_INDIVIDUAL_STATEMENTS );
			else if( l.equalsIgnoreCase( "AllProperty" ) )
				selectorAddAll( StatementType.ALL_PROPERTY_STATEMENTS );
			else if( l.equalsIgnoreCase( "ClassAssertion" ) )
				selectorAdd( StatementType.ALL_INSTANCE );
			else if( l.equalsIgnoreCase( "ComplementOf" ) )
				selectorAdd( StatementType.COMPLEMENT_CLASS );
			else if( l.equalsIgnoreCase( "DataPropertyAssertion" ) )
				selectorAdd( StatementType.DATA_PROPERTY_VALUE );
			else if( l.equalsIgnoreCase( "DifferentIndividuals" ) )
				selectorAdd( StatementType.DIFFERENT_FROM );
			else if( l.equalsIgnoreCase( "DirectClassAssertion" ) )
				selectorAdd( StatementType.DIRECT_INSTANCE );
			else if( l.equalsIgnoreCase( "DirectSubClassOf" ) )
				selectorAdd( StatementType.DIRECT_SUBCLASS );
			else if( l.equalsIgnoreCase( "DirectSubPropertyOf" ) )
				selectorAdd( StatementType.DIRECT_SUBPROPERTY );
			else if( l.equalsIgnoreCase( "DisjointClasses" ) )
				selectorAdd( StatementType.DISJOINT_CLASS );
			else if( l.equalsIgnoreCase( "DisjointProperties" ) )
				selectorAdd( StatementType.DISJOINT_PROPERTY );
			else if( l.equalsIgnoreCase( "EquivalentClasses" ) )
				selectorAdd( StatementType.EQUIVALENT_CLASS );
			else if( l.equalsIgnoreCase( "EquivalentProperties" ) )
				selectorAdd( StatementType.EQUIVALENT_PROPERTY );
			else if( l.equalsIgnoreCase( "InverseProperties" ) )
				selectorAdd( StatementType.INVERSE_PROPERTY );
			else if( l.equalsIgnoreCase( "ObjectPropertyAssertion" ) )
				selectorAdd( StatementType.OBJECT_PROPERTY_VALUE );
			else if( l.equalsIgnoreCase( "PropertyAssertion" ) )
				selectorAddAll( StatementType.PROPERTY_VALUE );
			else if( l.equalsIgnoreCase( "SameIndividual" ) )
				selectorAdd( StatementType.SAME_AS );
			else if( l.equalsIgnoreCase( "SubClassOf" ) )
				selectorAdd( StatementType.ALL_SUBCLASS );
			else if( l.equalsIgnoreCase( "SubPropertyOf" ) )
				selectorAdd( StatementType.ALL_SUBPROPERTY );
			else
				throw new PelletCmdException( "Unknown statement type: " + l );
		}

		if( selector == null )
			selector = StatementType.DEFAULT_STATEMENTS;
	}

	private void selectorAddAll(EnumSet<StatementType> types) {
		if( selector == null )
			selector = types;
		else
			selector.addAll( types );
	}

	private void selectorAdd(StatementType type) {
		if( selector == null )
			selector = EnumSet.of( type );
		else
			selector.add( type );
	}
}
