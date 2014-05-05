package pellet.test;

import static org.junit.Assert.assertTrue;

import java.io.File;

import junit.framework.JUnit4TestAdapter;

import org.junit.Ignore;
import org.junit.Test;

import pellet.PelletClassify;
import pellet.PelletCmdApp;
import pellet.PelletConsistency;
import pellet.PelletEntailment;
import pellet.PelletExplain;
import pellet.PelletExtractInferences;
import pellet.PelletInfo;
import pellet.PelletModularity;
import pellet.PelletQuery;
import pellet.PelletRealize;
import pellet.PelletTransTree;
import pellet.PelletUnsatisfiable;
import pellet.Pellint;


public class CLITests {

	private abstract class CLIMaker {
		
		protected abstract PelletCmdApp create( );
		
		public void run( String... args ) {
			
			PelletCmdApp app = create( );
			app.parseArgs( prepend( args, app.getAppCmd() ) );
			app.run();
		}
		
		
	}
	
	// Not in Arrays as of 1.5
	private static String[] copyOf( String[] arr, int len ) {
		String[] ret = new String[ len ];
		for( int i = 0; i < len && i < arr.length; i++ ) {
			ret[i] = arr[i];
		}
		return ret;
	}
	
	private static String[] prepend( String[] strs, String... prefix ) {
		String[] value = copyOf( prefix, strs.length + prefix.length );
		for( int i = prefix.length; i < value.length; i++ ) {
			value[i] = strs[ i - prefix.length ];
		}
		return value;
	}
	
	private static void runAppSimple( CLIMaker app, String... args ) {
		app.run( args );
	}
	
	private static void runAppVerbose( CLIMaker app, String... args ) {
		app.run( args );
		app.run( prepend( args, "-v" ) );
		app.run( prepend( args, "--verbose" ) );
	}
	
	private static void runWithLoaders( CLIMaker app, String... args ) {
		runAppVerbose( app, args );
		app.run( prepend( args, "-l", "OWLAPI" ) );
		app.run( prepend( args, "-l", "OWLAPIv3" ) );
		app.run( prepend( args, "-l", "Jena" ) );
	}
	
	
	private static void runWithIgnore( CLIMaker app, String... args ) {
		runWithLoaders( app, args );
		runWithLoaders( app, prepend( args, "--ignore-imports" ) );
	}
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter( CLITests.class );
	}
	
	@Test
	public void classify() {
		runWithIgnore( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new PelletClassify();
			}
		}, "test/data/misc/family.owl" );
	}

	@Test
	public void classifyWithPersist() {
		runWithIgnore( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new PelletClassify();
			}
		}, "--persist", "test/data/misc/family.owl" );
		
		File file = new File("persisted-state-aga5g99yq71la6a89exp75qfu.zip");
		
		// check that persistence generated the proper file
		// the file name contains a hash code of the ontology's IRI
		assertTrue(file.exists());
		
		// run again (to test operation from a persisted state)
		runWithIgnore( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new PelletClassify();
			}
		}, "--persist", "test/data/misc/family.owl" );
		
		file.delete();
	}

	
	@Test
	public void consistency() {
		runWithIgnore( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new PelletConsistency();
			}
		}, "test/data/misc/family.owl" );
	}
	
////DIG doesn't terminate - hard to test.
//	@Test
//	public void dig() {
//		
//	}
	
	@Test
	public void entailment() {
		runAppVerbose( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new PelletEntailment();
			}
		}, "-e", "test/data/modularity/koala-conclusions.owl", "test/data/modularity/koala.owl" );
	}
	
	@Test
	public void explain() {
		runAppVerbose( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new PelletExplain();
			}
		}, "test/data/modularity/koala.owl" );
	}
	
	@Test
	public void extract() {
		runWithIgnore( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new PelletExtractInferences();
			}
		}, "test/data/misc/family.owl" );
	}
	
	@Test
	public void info() {
		runAppSimple( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new PelletInfo();
			}
		}, "test/data/modularity/koala.owl", "test/data/modularity/galen.owl" , 
		"test/data/modularity/miniTambis.owl", "test/data/modularity/SUMO.owl", 
		"test/data/modularity/SWEET.owl", "test/data/modularity/wine.owl");
	}
	
	@Test
	public void modularity() {
		runAppVerbose( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new PelletModularity();
			}
		}, "-s", "Koala", "test/data/modularity/koala.owl");
	}

	@Test
	public void pellint() {
		runAppVerbose( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new Pellint();
			}
		}, "test/data/misc/family.owl" );
	}
	
	@Test
	public void query() {
		runAppVerbose( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new PelletQuery();
			}
		}, "-q", "test/data/query/sameAs/sameAs-01.rq", "test/data/query/sameAs/data-01.ttl" );
	}
	
	@Test
	public void realize() {
		runWithIgnore( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new PelletRealize();
			}
		}, "test/data/misc/family.owl"  );
	}
	
	@Test
	public void transTree() {
		runAppVerbose( new CLIMaker() {
			@Override
			protected PelletCmdApp create() {
				return new PelletTransTree();
			}
		}, "-p", "http://www.co-ode.org/ontologies/test/pellet/transitive.owl#p","test/data/misc/transitiveSub.owl");
	}
	
	@Test
	public void transTree2() {
		runAppVerbose( new CLIMaker() {
			@Override
			protected PelletCmdApp create() {
				return new PelletTransTree();
			}
		}, "-p", "http://www.co-ode.org/ontologies/test/pellet/transitive.owl#p","-f","http://www.co-ode.org/ontologies/test/pellet/transitive.owl#A","--individuals","test/data/misc/transitiveSub.owl");
	}
	
	@Test
	public void unsatisfiable() {
		runWithLoaders( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new PelletUnsatisfiable();
			}
		}, "test/data/modularity/koala.owl" );
	}
	
//	@Test
//	public void validate() {
//		runWithIgnore( new CLIMaker(){
//			@Override
//			protected PelletCmdApp create(String[] args) {
//				return new PelletValidate();
//			}
//		} );
//	}
}
