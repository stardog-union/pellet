// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package pellet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.mindswap.pellet.utils.VersionInfo;

/**
 * <p>
 * Title: PelletMain
 * </p>
 * <p>
 * Description: Pellet main command line entry point
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Markus Stocker
 * @author Evren Sirin
 */
public class Pellet {
	public static final Logger logger = Logger.getLogger( Pellet.class.getName() );	
	public static final PelletExceptionFormatter exceptionFormatter = new PelletExceptionFormatter();
	
	private static final Map<String, PelletCmdApp> COMMANDS = new TreeMap<String, PelletCmdApp>(); 
	static {
		COMMANDS.put( "classify", new PelletClassify() );
		COMMANDS.put( "consistency", new PelletConsistency() );
		COMMANDS.put( "realize", new PelletRealize() );
		COMMANDS.put( "unsat", new PelletUnsatisfiable() );
		COMMANDS.put( "explain", new PelletExplain() );
		COMMANDS.put( "query", new PelletQuery() );
		COMMANDS.put( "modularity", new PelletModularity() );
		COMMANDS.put( "trans-tree", new PelletTransTree() );
		COMMANDS.put( "extract", new PelletExtractInferences() );
		COMMANDS.put( "lint", new Pellint() );
		COMMANDS.put( "dig", new PelletDIG() );		
		COMMANDS.put("info", new PelletInfo());
		COMMANDS.put( "entail", new PelletEntailment() );	
	}

	public static void main(String[] args) {
		Pellet app = new Pellet();
		try {
			app.run( args );
		} catch( PelletCmdException e ) {
			printError( e );
			
			StringWriter sw = new StringWriter();
			e.printStackTrace( new PrintWriter( sw ) );
			logger.fine( sw.toString() );
			logger.throwing( null, null, e );
			System.exit( 1 );
		}
	}
	
	private static void printError(Throwable e) {
		System.err.println( exceptionFormatter.formatException( e ) );
	}
	
	public static PelletCmdApp getCommand(String name) {
		PelletCmdApp cmd = COMMANDS.get( name.toLowerCase() );
		if( cmd == null ) {
	        throw new PelletCmdException( "Unrecognized subcommand: " + name );
        }
		return cmd;
	}

	private void run(String[] args) {
		if( args.length == 0 ) {
	        throw new PelletCmdException( "Type 'pellet help' for usage." );
        }

		String arg = args[0];
				
		if( arg.equals( "h" ) || arg.equals( "-h" ) || arg.equals( "help" ) || arg.equals( "--help" ) ) {
			if( args.length == 1 ) {
				mainhelp();
			}
			else {
				PelletCmdApp cmd = getCommand( args[1] );
				cmd.help();
			}
		}
		else if( arg.equals( "--version" ) || arg.equals( "-V" ) ) {
			version();
		}
		else {
			PelletCmdApp cmd = getCommand( arg );
			cmd.parseArgs( args );
			cmd.run();
			cmd.finish();				
		}
	}

	private void mainhelp() {
		StringBuffer buf = new StringBuffer();
		String version = getVersionInfo().getVersionString();

		buf.append( "Usage: pellet <subcommand> [options] <file URI>...\n" );
		buf.append( "Pellet command-line client, version " + version + "." + "\n" );
		buf.append( "Type 'pellet help <subcommand>' for help on a specific subcommand.\n" );
		buf.append( "\n" );
		buf.append( "Available subcommands:\n" );
		
		for( String cmd : COMMANDS.keySet() ) {
			buf.append( "\t" );
			buf.append( cmd );
			buf.append( "\n" );	
		}
		
		buf.append( "\n" );
        buf.append( "Pellet is an OWL ontology reasoner.\n");
        buf.append( "For more information, see http://clarkparsia.com/pellet");

		System.out.println( buf );
		System.exit( 0 );
	}

	private VersionInfo getVersionInfo() {
		return VersionInfo.getInstance();
	}

	private void version() {
		System.out.println( getVersionInfo() );
	}
}
