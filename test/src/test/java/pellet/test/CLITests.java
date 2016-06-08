package pellet.test;

import static org.junit.Assert.assertTrue;

import com.clarkparsia.owlapi.OWL;
import java.io.File;
import java.io.FilenameFilter;
import junit.framework.JUnit4TestAdapter;
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

public class CLITests
{

	private abstract class CLIMaker
	{

		protected abstract PelletCmdApp create();

		public void run(final String... args)
		{
			OWL._manager.ontologies().forEach(OWL._manager::removeOntology);
			final PelletCmdApp app = create();
			app.parseArgs(prepend(args, app.getAppCmd()));
			app.run();
		}
	}

	// Not in Arrays as of 1.5
	private static String[] copyOf(final String[] arr, final int len)
	{
		final String[] ret = new String[len];
		for (int i = 0; i < len && i < arr.length; i++)
			ret[i] = arr[i];
		return ret;
	}

	private static String[] prepend(final String[] strs, final String... prefix)
	{
		final String[] value = copyOf(prefix, strs.length + prefix.length);
		for (int i = prefix.length; i < value.length; i++)
			value[i] = strs[i - prefix.length];
		return value;
	}

	private static void runAppSimple(final CLIMaker app, final String... args)
	{
		app.run(args);
	}

	private static void runAppVerbose(final CLIMaker app, final String... args)
	{
		app.run(args);
		app.run(prepend(args, "-v"));
		app.run(prepend(args, "--verbose"));
	}

	private static void runWithLoaders(final CLIMaker app, final String... args)
	{
		runAppVerbose(app, args);
		app.run(prepend(args, "-l", "OWLAPI"));
		app.run(prepend(args, "-l", "Jena"));
	}

	private static void runWithIgnore(final CLIMaker app, final String... args)
	{
		runWithLoaders(app, args);
		runWithLoaders(app, prepend(args, "--ignore-imports"));
	}

	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(CLITests.class);
	}

	@Test
	public void classify()
	{
		runWithIgnore(new CLIMaker()
		{

			@Override
			protected PelletCmdApp create()
			{
				return new PelletClassify();
			}
		}, fileIRI("test/data/misc/family.owl"));
	}

	private String fileIRI(final String s)
	{
		return getClass().getResource("/" + s).toString();
	}

	@Test
	public void classifyWithPersist()
	{
		runWithIgnore(new CLIMaker()
		{

			@Override
			protected PelletCmdApp create()
			{
				return new PelletClassify();
			}
		}, "--persist", fileIRI("test/data/misc/family.owl"));
		final File folder = new File(System.getProperty("user.dir"));
		final File[] persistenceFiles = folder.listFiles((FilenameFilter) (dir, name) -> name.startsWith("persisted-state-"));
		// check that persistence generated the proper file
		// the file name contains a hash code of the ontology's IRI
		assertTrue(persistenceFiles.length > 0);
		// run again (to test operation from a persisted state)
		runWithIgnore(new CLIMaker()
		{

			@Override
			protected PelletCmdApp create()
			{
				return new PelletClassify();
			}
		}, "--persist", fileIRI("test/data/misc/family.owl"));
		for (final File file : persistenceFiles)
			file.delete();
	}

	@Test
	public void consistency()
	{
		runWithIgnore(new CLIMaker()
		{

			@Override
			protected PelletCmdApp create()
			{
				return new PelletConsistency();
			}
		}, fileIRI("test/data/misc/family.owl"));
	}

	// //DIG doesn't terminate - hard to test.
	// @Test
	// public void dig() {
	//
	// }
	@Test
	public void entailment()
	{
		runAppVerbose(new CLIMaker()
		{

			@Override
			protected PelletCmdApp create()
			{
				return new PelletEntailment();
			}
		}, "-e", fileIRI("test/data/modularity/koala-conclusions.owl"), fileIRI("test/data/modularity/koala.owl"));
	}

	@Test
	public void explain()
	{
		runAppVerbose(new CLIMaker()
		{

			@Override
			protected PelletCmdApp create()
			{
				return new PelletExplain();
			}
		}, fileIRI("test/data/modularity/koala.owl"));
	}

	@Test
	public void extract()
	{
		runWithIgnore(new CLIMaker()
		{

			@Override
			protected PelletCmdApp create()
			{
				return new PelletExtractInferences();
			}
		}, fileIRI("test/data/misc/family.owl"));
	}

	@Test
	public void info()
	{
		runAppSimple(new CLIMaker()
		{

			@Override
			protected PelletCmdApp create()
			{
				return new PelletInfo();
			}
		},//
				fileIRI("test/data/modularity/koala.owl"),//
				fileIRI("test/data/modularity/galen.owl"),//
				fileIRI("test/data/modularity/miniTambis.owl"),//
				fileIRI("test/data/modularity/SUMO.owl"),//
				fileIRI("test/data/modularity/SWEET.owl"),//
				fileIRI("test/data/modularity/wine.owl")//
				);
	}

	@Test
	public void modularity()
	{
		runAppVerbose(new CLIMaker()
		{

			@Override
			protected PelletCmdApp create()
			{
				return new PelletModularity();
			}
		}, "-s", "Koala", fileIRI("test/data/modularity/koala.owl"));
	}

	@Test
	public void pellint()
	{
		runAppVerbose(new CLIMaker()
		{

			@Override
			protected PelletCmdApp create()
			{
				return new Pellint();
			}
		}, fileIRI("test/data/misc/family.owl"));
	}

	@Test
	public void query()
	{
		runAppVerbose(new CLIMaker()
		{

			@Override
			protected PelletCmdApp create()
			{
				return new PelletQuery();
			}
		}, "-q", fileIRI("test/data/query/sameAs/sameAs-01.rq"), fileIRI("test/data/query/sameAs/data-01.ttl"));
	}

	@Test
	public void realize()
	{
		runWithIgnore(new CLIMaker()
		{

			@Override
			protected PelletCmdApp create()
			{
				return new PelletRealize();
			}
		}, fileIRI("test/data/misc/family.owl"));
	}

	@Test
	public void transTree()
	{
		runAppVerbose(new CLIMaker()
		{

			@Override
			protected PelletCmdApp create()
			{
				return new PelletTransTree();
			}
		}, "-p", "http://www.co-ode.org/ontologies/test/pellet/transitive.owl#p", fileIRI("test/data/misc/transitiveSub.owl"));
	}

	@Test
	public void transTree2()
	{
		runAppVerbose(new CLIMaker()
		{

			@Override
			protected PelletCmdApp create()
			{
				return new PelletTransTree();
			}
		}, "-p", "http://www.co-ode.org/ontologies/test/pellet/transitive.owl#p", "-f", "http://www.co-ode.org/ontologies/test/pellet/transitive.owl#A", "--individuals", fileIRI("test/data/misc/transitiveSub.owl"));
	}

	@Test
	public void unsatisfiable()
	{
		runWithLoaders(new CLIMaker()
		{

			@Override
			protected PelletCmdApp create()
			{
				return new PelletUnsatisfiable();
			}
		}, fileIRI("test/data/modularity/koala.owl"));
	}
	// @Test
	// public void validate() {
	// runWithIgnore( new CLIMaker(){
	// @Override
	// protected PelletCmdApp create(String[] args) {
	// return new PelletValidate();
	// }
	// } );
	// }
}
