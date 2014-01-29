package pellet.test;

import static org.junit.Assert.assertTrue;

import java.io.File;

import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;
import org.mindswap.pellet.test.utils.TestUtils;

import com.clarkparsia.StableTests;

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

@Category(StableTests.class)
public class CLITests {

    /**
     * Timeout individual tests after 60 seconds.
     */
    @Rule
    public Timeout timeout = new Timeout(60000);
    
    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();
    
    protected File testDir;
    
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
	
   @Before
    public void setUp() throws Exception {
        testDir = tempDir.newFolder("clitests");
   }
	
	@Test
	public void classify() throws Exception {
		runWithIgnore( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new PelletClassify();
			}
		}, TestUtils.copyResourceToFile(testDir, "/data/misc/family.owl") );
	}

	@Test
	public void classifyWithPersist() throws Exception {
		runWithIgnore( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new PelletClassify();
			}
		}, "--persist", TestUtils.copyResourceToFile(testDir, "/data/misc/family.owl") );
		
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
		}, "--persist", TestUtils.copyResourceToFile(testDir, "/data/misc/family.owl") );
		
		file.delete();
	}

	
	@Test
	public void consistency() throws Exception {
		runWithIgnore( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new PelletConsistency();
			}
		}, TestUtils.copyResourceToFile(testDir, "/data/misc/family.owl") );
	}
	
////DIG doesn't terminate - hard to test.
//	@Test
//	public void dig() {
//		
//	}
	
	@Test
	public void entailment() throws Exception {
		runAppVerbose( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new PelletEntailment();
			}
		}, "-e", TestUtils.copyResourceToFile(testDir, "/data/modularity/koala-conclusions.owl"), TestUtils.copyResourceToFile(testDir, "/data/modularity/koala.owl") );
	}
	
	@Test
	public void explain() throws Exception {
		runAppVerbose( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new PelletExplain();
			}
		}, TestUtils.copyResourceToFile(testDir, "/data/modularity/koala.owl") );
	}
	
	@Test
	public void extract() throws Exception {
		runWithIgnore( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new PelletExtractInferences();
			}
		}, TestUtils.copyResourceToFile(testDir, "/data/misc/family.owl") );
	}
	
	@Test
	public void info() throws Exception {
		runAppSimple( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new PelletInfo();
			}
		}, TestUtils.copyResourceToFile(testDir, "/data/modularity/koala.owl"), 
		   TestUtils.copyResourceToFile(testDir, "/data/modularity/galen.owl") , 
		   TestUtils.copyResourceToFile(testDir, "/data/modularity/miniTambis.owl"), 
		   TestUtils.copyResourceToFile(testDir, "/data/modularity/SUMO.owl"), 
		   TestUtils.copyResourceToFile(testDir, "/data/modularity/SWEET.owl"), 
		   TestUtils.copyResourceToFile(testDir, "/data/modularity/wine.owl"));
	}
	
	@Test
	public void modularity() throws Exception {
		runAppVerbose( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new PelletModularity();
			}
		}, "-s", "Koala", TestUtils.copyResourceToFile(testDir, "/data/modularity/koala.owl"));
	}

	@Test
	public void pellint() throws Exception {
		runAppVerbose( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new Pellint();
			}
		}, TestUtils.copyResourceToFile(testDir, "/data/misc/family.owl") );
	}
	
	@Test
	public void query() throws Exception {
		runAppVerbose( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new PelletQuery();
			}
		}, "-q", TestUtils.copyResourceToFile(testDir, "/data/query/sameAs/sameAs-01.rq"), 
		         TestUtils.copyResourceToFile(testDir, "/data/query/sameAs/data-01.ttl") );
	}
	
	@Test
	public void realize() throws Exception {
		runWithIgnore( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new PelletRealize();
			}
		}, TestUtils.copyResourceToFile(testDir, "/data/misc/family.owl") );
	}
	
	@Test
	public void transTree() throws Exception {
		runAppVerbose( new CLIMaker() {
			@Override
			protected PelletCmdApp create() {
				return new PelletTransTree();
			}
		}, "-p", "http://www.co-ode.org/ontologies/test/pellet/transitive.owl#p",TestUtils.copyResourceToFile(testDir, "/data/misc/transitiveSub.owl"));
	}
	
	@Test
	public void transTree2() throws Exception {
		runAppVerbose( new CLIMaker() {
			@Override
			protected PelletCmdApp create() {
				return new PelletTransTree();
			}
		}, "-p", "http://www.co-ode.org/ontologies/test/pellet/transitive.owl#p","-f","http://www.co-ode.org/ontologies/test/pellet/transitive.owl#A","--individuals",TestUtils.copyResourceToFile(testDir, "/data/misc/transitiveSub.owl"));
	}
	
	@Test
	public void unsatisfiable() throws Exception {
		runWithLoaders( new CLIMaker(){
			@Override
			protected PelletCmdApp create() {
				return new PelletUnsatisfiable();
			}
		}, TestUtils.copyResourceToFile(testDir, "/data/modularity/koala.owl") );
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
