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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.mindswap.pellet.KBLoader;
import org.mindswap.pellet.KRSSLoader;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.jena.JenaLoader;
import org.mindswap.pellet.utils.Timer;
import org.mindswap.pellet.utils.Timers;

import com.clarkparsia.pellet.owlapiv3.OWLAPILoader;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReaderF;
import com.hp.hpl.jena.shared.NoReaderForLangException;

/**
 * <p>
 * Title: PelletCmdLine
 * </p>
 * <p>
 * Description: Provides some functionality for Pellet command line applications
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
public abstract class PelletCmdApp {
	public static final Logger logger = Logger.getLogger( PelletCmdApp.class.getName() );
        private final static String LINE_BREAK = System.getProperty("line.separator");
	private final static RDFReaderF READER_FACTORY = ModelFactory.createDefaultModel(); 
	
	protected String			appId;
	protected String			appCmd;
	protected String			help;
	protected PelletCmdOptions	options;
	private List<String>		inputFiles;
	protected KBLoader			loader;
	protected boolean			verbose;
	protected Timers			timers;
	protected List<String>		tasks;
	
	public PelletCmdApp() {
		this.options = getOptions();
		this.appId = getAppId();
		this.appCmd = getAppCmd();
		this.inputFiles = new ArrayList<String>();
		this.timers = new Timers();

		buildHelp();
	}
	
	public boolean requiresInputFiles() {
		return true;
	}
	
	protected void verbose( String msg ) {
		if (verbose)
			System.err.println( msg );
	}
	
	protected void output( String msg ) {
		System.out.println( msg );
	}
	
	protected void output( Model model ) {
		model.write( System.out );
	}
	
	public abstract String getAppId();

	public abstract String getAppCmd();

	public abstract PelletCmdOptions getOptions();

	public abstract void run();
	
	public void finish() {
		if( verbose ) {
			StringWriter sw = new StringWriter();
			timers.print( sw, true, null );
			
			verbose( "" );
			verbose( "Timer summary:" );
			verbose( sw.toString() );
		}
	}

	protected String getMandatoryOptions() {
		StringBuffer ret = new StringBuffer();
		Set<PelletCmdOption> mandatory = options.getMandatoryOptions();

		for( Iterator<PelletCmdOption> i = mandatory.iterator(); i.hasNext(); ) {
			PelletCmdOption option = i.next();
			ret.append( "-" + option.getShortOption() + " arg " );
		}

		return ret.toString();
	}

	public PelletCmdOption getIgnoreImportsOption() {
		PelletCmdOption option = new PelletCmdOption("ignore-imports");
		//option.setShortOption("I");
		option.setDescription("Ignore imported ontologies");
		option.setDefaultValue(false);
		option.setIsMandatory( false );
		option.setArg( NONE );
		
		return option;
	}

	public PelletCmdOption getLoaderOption() {
		PelletCmdOption option = new PelletCmdOption( "loader" );
		option.setShortOption( "l" );
		option.setDescription( "Use Jena, OWLAPI, OWLAPIv3 or KRSS to load the ontology" );
		option.setType( "Jena | OWLAPI | OWLAPIv3 | KRSS" );
		option.setDefaultValue( "OWLAPIv3" );
		option.setIsMandatory( false );
		option.setArg( REQUIRED );

		return option;
	}
	
	public PelletCmdOptions getGlobalOptions() {
		PelletCmdOptions options = new PelletCmdOptions();
	
		PelletCmdOption helpOption = new PelletCmdOption( "help" );
		helpOption.setShortOption( "h" );
		helpOption.setDescription( "Print this message" );
		helpOption.setDefaultValue( false );
		helpOption.setIsMandatory( false );
		helpOption.setArg( NONE );
		options.add( helpOption );
		
		PelletCmdOption verboseOption = new PelletCmdOption( "verbose" );
		verboseOption.setShortOption( "v" );
		verboseOption.setDescription( "Print full stack trace for errors." );
		verboseOption.setDefaultValue( false );
		verboseOption.setIsMandatory( false );
		verboseOption.setArg( NONE );
		options.add( verboseOption );
		
		PelletCmdOption configOption = new PelletCmdOption( "config" );
		configOption.setShortOption( "C" );
		configOption.setDescription( "Use the selected configuration file" );
		configOption.setIsMandatory( false );
		configOption.setType( "configuration file" );
		configOption.setArg( REQUIRED );
		options.add( configOption );
		
		return options;
	}
	
	public PelletCmdOption getInputFormatOption() {
		PelletCmdOption option = new PelletCmdOption( "input-format" );
		option.setDefaultValue( null );
		option.setDescription( "Format of the input file (valid only for the "
				+ "Jena loader). Default behaviour is to guess "
				+ "the input format based on the file extension." );
		option.setType( "RDF/XML | Turtle | N-Triples" );
		option.setIsMandatory( false );
		option.setArg( REQUIRED );

		return option;
	}

	protected KnowledgeBase getKB() {
		return getKB( getLoader() );
	}
	
	protected KnowledgeBase getKB(KBLoader loader) {
		try {
			String[] inputFiles = getInputFiles();			
			
			verbose( "There are " + inputFiles.length + " input files:" );
			for( String inputFile : inputFiles ) 
				verbose( inputFile );			
			
			startTask( "loading" );
			KnowledgeBase kb = loader.createKB( inputFiles );
			finishTask( "loading" );

			if( verbose ) {
				StringBuilder sb = new StringBuilder();
				sb.append( "Classes = " + kb.getAllClasses().size() + ", " );
				sb.append( "Properties = " + kb.getProperties().size() + ", " );
				sb.append( "Individuals = " + kb.getIndividuals().size() );
				verbose( "Input size: " + sb );
				
				verbose( "Expressivity: " + kb.getExpressivity() );
			}
			
			return kb;
		} catch( RuntimeException e ) {
			throw new PelletCmdException( e );
		}
	}
	
	protected KBLoader getLoader() {
		if( loader != null )
			return loader;

		String loaderName = options.getOption( "loader" ).getValueAsString();

		return getLoader( loaderName );
	}

	protected KBLoader getLoader(String loaderName) {
		if( loaderName.equalsIgnoreCase( "Jena" ) )
			loader = new JenaLoader();
		else if( loaderName.equalsIgnoreCase( "OWLAPIv3" ) )
			loader = new OWLAPILoader();
		else if( loaderName.equalsIgnoreCase( "OWLAPI" ) )
			loader = new org.mindswap.pellet.owlapi.OWLAPILoader();
		else if( loaderName.equalsIgnoreCase( "KRSS" ) )
			loader = new KRSSLoader();
		else
			throw new PelletCmdException( "Unknown loader: " + loaderName );

		loader.setIgnoreImports( options.getOption("ignore-imports").getValueAsBoolean() );
		PelletCmdOption option = options.getOption( "input-format" );		
		if( option != null && option.getValueAsString() != null ) {
			if( loader instanceof JenaLoader ) {
				String inputFormat = option.getValueAsString().toUpperCase();
				
				try {
					if( inputFormat != null ) {
						READER_FACTORY.getReader( inputFormat.toUpperCase() );
						
						((JenaLoader) loader).setInputFormat( inputFormat );
					}
				}
				catch( NoReaderForLangException e ) {
					throw new PelletCmdException( "Unrecognized input format: " + inputFormat );
				}				
			}
			else {
				// silently ignore
			}
		}

		return loader;
	}

	protected String[] getInputFiles() {
		return inputFiles.toArray( new String[] {} );
	}

	private void buildHelp() {
		StringBuffer u = new StringBuffer();

		HelpTable table = new HelpTable( options );

		u.append( appId + LINE_BREAK + LINE_BREAK );
		u.append( "Usage: " + appCmd + LINE_BREAK + LINE_BREAK );
		u.append( table.print() + LINE_BREAK );

		help = u.toString();
	}

	public void parseArgs(String[] args) {
		HashSet<String> seenOptions = new HashSet<String>();
		
		// skip first arg which is the name of the subcommand
		int i = 1;
		for( ; i < args.length; i++ ) {
			String arg = args[i];

			if( arg.equals( "--" ) )
				return;

			if( arg.charAt( 0 ) == '-' ) {
				if( arg.charAt( 1 ) == '-' )
					arg = arg.substring( 2 );
				else
					arg = arg.substring( 1 );
			}
			else
				// no more options to parse
				break;

			PelletCmdOption option = options.getOption( arg );

			if( option == null )
				throw new PelletCmdException( "Unrecognized option: " + arg );
			else if( option.getLongOption().equals( "help" ) )
				help();
			else if ( option.getLongOption().equals( "verbose" ) )
				Pellet.exceptionFormatter.setVerbose( true );
			
			if (seenOptions.contains(option.getLongOption())) {
				throw new PelletCmdException( "Repeated use of option: " + arg );
			}
			
			seenOptions.add( option.getLongOption() );

			PelletCmdOptionArg optionArg = option.getArg();
			boolean nextIsArg = (args.length > i + 1) && args[i + 1].charAt( 0 ) != '-';
			switch ( optionArg ) {
			case NONE:
				option.setValue( true );
				break;
			case REQUIRED:
				if( !nextIsArg )
					throw new PelletCmdException( "Option <" + option.getLongOption()
							+ "> requires an argument" );
				else
					option.setValue( args[++i] );
				break;
			case OPTIONAL:
				if( nextIsArg )
					option.setValue( args[++i] );
				else
					option.setExists( true );
				break;

			default:
				throw new PelletCmdException( "Unrecognized option argument: " + optionArg );
			}
		}

		// Check if all mandatory options are set
		for( PelletCmdOption option : options.getOptions() ) {
			if( option.isMandatory() ) {
				if( option.getValue() == null )
					throw new PelletCmdException( "Option <" + option.getLongOption()
							+ "> is mandatory" );
			}
		}

		loadConfig();
		
		// Input files are given as a list of file URIs at the end
		for( ; i < args.length; i++ ) {
			inputFiles.add( args[i] );
		}
		
		if ( options.getOption( "verbose" ).getValueAsBoolean() ) {
			verbose = true;
		}

		if( requiresInputFiles() ) {
			if( inputFiles.isEmpty() )
				throw new PelletCmdException( "No input file given" );
		}
		else {
			if( !inputFiles.isEmpty() )
				throw new PelletCmdException( "Unexpected argument(s): " + inputFiles );
		}
	}
	
	private void loadConfig() {
		String configFile = options.getOption( "config" ).getValueAsString();
		
		if( configFile != null ) {			
			try {
				URL url = new URL( "file:" + configFile );
				
				PelletOptions.load( url );
			} catch( MalformedURLException e ) {
				throw new PelletCmdException( "Invalid URL given for the config file: " + configFile );
			} catch( FileNotFoundException e ) {
				throw new PelletCmdException( "The specified configuration file cannot be found: " + configFile );
			} catch( IOException e ) {
				throw new PelletCmdException( "I/O error while reading the configuration file: " + e.toString() );
			}
		}		
	}

	public void help() {
		output( help );
		System.exit( 0 );
	}

	private static class HelpTable {
		private final String LINE_BREAK = System.getProperty("line.separator");
		private PelletCmdOptions options;
		private int					maxLineWidth	= 80;
		private int					indent			= 5;

		public HelpTable(PelletCmdOptions options) {
			this.options = options;
		}

		public String print() {
			StringBuffer ret = new StringBuffer();

			ret.append( "Argument description:" + LINE_BREAK + LINE_BREAK );

			int i = 0;
			boolean last = false;
			
			for( PelletCmdOption option : options.getOptions() ) {
				i++;
				
				if (i == options.getOptions().size()  )
					last = true;
				
				String longOption = option.getLongOption();
				String shortOption = option.getShortOption();
				String type = option.getType();
				PelletCmdOptionArg arg = option.getArg();
				String description = option.getDescription();

				String defaultValue = "";

				if( option.getDefaultValue() != null )
					defaultValue = option.getDefaultValue().toString();

				String firstLine = firstLine( shortOption, longOption, type, arg );
				String secondLine = secondLine( description, defaultValue );

				ret.append( firstLine );
				ret.append( LINE_BREAK );
				ret.append( secondLine );
				
				if (!last)
					ret.append( LINE_BREAK + LINE_BREAK );
			}

			return ret.toString();
		}

		private String fill(int n) {
			return draw( " ", n );
		}

		private String draw(String c, int n) {
			StringBuffer ret = new StringBuffer();

			for( int i = 0; i < n; i++ )
				ret.append( c );

			return ret.toString();
		}

		private String firstLine(String shortOption, String longOption, String type,
				PelletCmdOptionArg arg) {
			StringBuffer ret = new StringBuffer();

			ret.append( "--" + longOption );

			if( shortOption != null )
				ret.append( ", -" + shortOption );

			ret.append( " " );

			if( type != null ) {
				if( arg.equals( PelletCmdOptionArg.OPTIONAL )
						&& !(type.startsWith( "[" ) || type.startsWith( "(" )) )
					ret.append( "[" + type + "] " );
				else if( arg.equals( PelletCmdOptionArg.REQUIRED )
						&& !(type.startsWith( "[" ) || type.startsWith( "(" )) )
					ret.append( "(" + type + ") " );
			}

			return ret.toString();
		}

		private String secondLine(String description, String defaultValue) {
			int colStart = indent;
			int colLength = colStart;

			StringBuffer ret = new StringBuffer();

			if( description == null && defaultValue == null )
				return ret.toString();

			String tokens;

			if( defaultValue != null && defaultValue.length() != 0
					&& !(defaultValue.equals( "true" ) || defaultValue.equals( "false" )) )
				tokens = description + " (Default: " + defaultValue + ")";
			else
				tokens = description;

			ret.append( fill( colStart ) );

			StringTokenizer tokenizer = new StringTokenizer( tokens );

			while( tokenizer.hasMoreTokens() ) {
				String token = tokenizer.nextToken();

				colLength = colLength + token.length() + 1;
				
				if( colLength > maxLineWidth ) {
					ret.append( LINE_BREAK + fill( colStart ) );
					colLength = colStart + token.length() + 1;
				}

				ret.append( token + " " );
			}

			return ret.toString();
		}
	}
	
	protected void startTask(String task) {
		verbose( "Start " + task );		
		timers.startTimer( task );		
	}	
	
	protected void finishTask(String task) {
		Timer timer = timers.getTimer( task );
		timer.stop();
		verbose( "Finished " + task + " in " + timer.format() );
	}
}
