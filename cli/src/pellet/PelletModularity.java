// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package pellet;

import static pellet.PelletCmdOptionArg.REQUIRED;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.io.SystemOutDocumentTarget;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.manchester.cs.owlapi.modularity.ModuleType;

import com.clarkparsia.modularity.ModularityUtils;
import com.clarkparsia.owlapiv3.OntologyUtils;
import com.clarkparsia.pellet.owlapiv3.OWLAPILoader;


/**
 * <p>
 * Title: PelletModularity
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
public class PelletModularity extends PelletCmdApp {

	private OWLAPILoader	loader;
	private ModuleType		moduleType;
	private String[] 		entityNames;

	public PelletModularity() {
	}

	@Override
	public String getAppCmd() {
		return "pellet modularity " + getMandatoryOptions() + "[options] <file URI>...";
	}

	@Override
	public String getAppId() {
		return "PelletModularity: Extract ontology modules for classes and write it to the STDOUT";
	}

	@Override
	public PelletCmdOptions getOptions() {
		PelletCmdOptions options = getGlobalOptions();

		options.add( getIgnoreImportsOption() );
		
		PelletCmdOption option = new PelletCmdOption( "signature" );
		option.setShortOption( "s" );
		option.setType( "Space separated list" );
		option.setDescription( "One or more entity URI(s) or local name(s) to be extracted as a module. Example: \"Animal Wildlife Rainforest\"" );
		option.setIsMandatory( true );
		option.setArg( REQUIRED );
		options.add( option );

		option = new PelletCmdOption( "type" );
		option.setShortOption( "t" );
		option.setType( "lower| upper | upper-of-lower | lower-of-upper" );
		option.setDefaultValue( "lower" );
		option.setDescription( "The type of the module that will be extracted. See http://bit.ly/ontology-module-types for an explanation of the module types." );
		option.setIsMandatory( false );
		option.setArg( REQUIRED );
		options.add( option );
		
		return options;
	}

	@Override
	public void run() {
		loadEntityNames();
		loadModuleType();
		loadOntology();
		extractModule();
	}

	private void loadOntology() {
		loader = (OWLAPILoader) getLoader( "OWLAPIv3" );
		getKB();
	}
	
	private void loadEntityNames() {
		String signature = options.getOption( "signature" ).getValueAsString();

		if( signature == null )
			throw new PelletCmdException( "No signature provided" );

		entityNames = signature.split( " " );

		if( entityNames.length == 0 )
			throw new PelletCmdException( "No signature provided" );
	}
	
	private void loadModuleType() {
		String type = options.getOption( "type" ).getValueAsString();
		
		if( type.equalsIgnoreCase( "lower" ) ) {
			moduleType = ModuleType.TOP;
		}
		else if( type.equalsIgnoreCase( "upper" ) ) {
			moduleType = ModuleType.BOT;
		}
		else if( type.equalsIgnoreCase( "upper-of-lower" ) ) {
			moduleType = ModuleType.BOT_OF_TOP;
		}
		else if( type.equalsIgnoreCase( "lower-of-upper" ) ) {
			moduleType = ModuleType.TOP_OF_BOT;
		}
		else {
			throw new PelletCmdException( "Unknown module type: " + type );
		}
	}

	private void extractModule() {
		Set<OWLEntity> entities = new HashSet<OWLEntity>();
		for( String entityName : entityNames ) {
			OWLEntity entity = OntologyUtils.findEntity( entityName, loader.getAllOntologies() );
			
			if( entity == null )
				throw new PelletCmdException( "Entity not found in ontology: " + entityName );

			entities.add( entity );
		}

		Set<OWLAxiom> module = ModularityUtils.extractModule( loader.getOntology(), entities, moduleType );

		try {
			OWLOntology moduleOnt = loader.getManager().createOntology( module );
			loader.getManager().saveOntology( moduleOnt, new RDFXMLOntologyFormat(),
					new SystemOutDocumentTarget() );
		} catch( OWLException e ) {
			throw new RuntimeException( e );
		}
	}
}
