// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package pellet;

import static pellet.PelletCmdOptionArg.REQUIRED;

import org.mindswap.pellet.dig.PelletDIGServer;

/**
 * <p>
 * Title: PelletDIG
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
 * @deprecated DIG functionality is deprecated and will be remove in the next major release
 */
@Deprecated
public class PelletDIG extends PelletCmdApp {

	public PelletDIG() {
	}

	@Override
    public String getAppId() {
		return "PelletDIG: DIG Server that is backed by Pellet reasoner *DEPRECATED*";
	}

	@Override
    public String getAppCmd() {
		return "pellet DIG " + getMandatoryOptions() + "[options]";
	}

	@Override
    public PelletCmdOptions getOptions() {
		PelletCmdOptions options = getGlobalOptions();

		PelletCmdOption option = new PelletCmdOption( "port" );
		option.setShortOption( "p" );
		option.setType( "positive integer" );
		option.setDescription( "The port number user by the server" );
		option.setIsMandatory( false );
		option.setDefaultValue( 8081 );
		option.setArg( REQUIRED );
		options.add( option );
		
		return options;
	}
	
	@Override
    public boolean requiresInputFiles() {
		return false;
	}
	
	@Override
    public void run() {
		output("*****************************************************************************");
		output("*                        DEPRECATION WARNGING                               *");
		output("*                                                                           *");
		output("*  DIG command is deprecated and will be removed in the next major release  *");
		output("*                                                                           *");
		output("*****************************************************************************");
		output("");
		
		int port = options.getOption( "port" ).getValueAsInteger( 1, 65535 );

		PelletDIGServer server = new PelletDIGServer(port);
		server.run();
	}

}
