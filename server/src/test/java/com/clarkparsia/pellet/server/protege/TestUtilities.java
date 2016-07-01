package com.clarkparsia.pellet.server.protege;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class TestUtilities {
	private static Logger logger = Logger.getLogger(TestUtilities.class.getCanonicalName());

	public static final String BASE = "file:test/data/";

	public static final File SERVER_ROOT = new File("target/server-distribution/server");
	public static final File ROOT_DIRECTORY = new File(SERVER_ROOT, "root");
	public static final File CONFIGURATION_DIRECTORY = new File(SERVER_ROOT, "configuration");
	public static final File PELLET_DIRECTORY = new File(SERVER_ROOT, "pellet");

	public static final String PROTEGE_HOST = "localhost";
	public static final String PROTEGE_PORT = "8080";
	public static final String PROTEGE_USERNAME = "root";
	public static final String PROTEGE_PASSWORD = "rootpwd";

//	public static final String PREFIX;
//	static {
//		StringBuffer sb = new StringBuffer();
//		sb.append("src");
//		sb.append(File.separator);
//		sb.append("test");
//		sb.append(File.separator);
//		sb.append("resources");
//		sb.append(File.separator);
//		PREFIX = sb.toString();
//	}
//
//	TestUtilities() {
//	}
//
//	public static File initializeServerRoot() {
//		delete(ROOT_DIRECTORY);
//		delete(CONFIGURATION_DIRECTORY);
//		delete(PELLET_DIRECTORY);
//		ROOT_DIRECTORY.mkdirs();
//		CONFIGURATION_DIRECTORY.mkdirs();
//		PELLET_DIRECTORY.mkdirs();
//
//		return ROOT_DIRECTORY;
//	}

	protected static void delete(File f) {
		if (f.isDirectory()) {
			for (File child : f.listFiles()) {
				delete(child);
			}
		}
		f.delete();
	}

	/**
	 * This routine creates a temporary directory and then creates a file inside that directory.
	 * <p/>
	 * This routine is sometimes needed when running a test that needs a temporary file but then also needs to
	 * be able to write to the containing directory.  When we created the temporary file with File.createTempFile(),
	 * it was noticed that when certain tests (e.g. the tests that serialize server side ontologies) were run by different
	 * users the second run would sometimes fail because the second user would fail to have write access to all the contents of
	 * the containing directory (e.g. the .owlserver directory).
	 *
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public static File createFileInTempDirectory(String name) throws IOException {
		File tmpDirectory = File.createTempFile("Save", "test");
		tmpDirectory.delete();
		if (!tmpDirectory.mkdir()) {
			throw new IOException("Coud not create temporary directory " + tmpDirectory);
		}
		logger.info("Created temporary directory " + tmpDirectory + " for the file " + name);
		return new File(tmpDirectory, name);
	}
}
