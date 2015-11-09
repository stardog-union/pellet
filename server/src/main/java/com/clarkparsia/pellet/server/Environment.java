package com.clarkparsia.pellet.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.TreeTraverser;
import org.mindswap.pellet.utils.FileUtils;

import static com.google.common.base.Strings.isNullOrEmpty;


/**
 * @author Edgar Rodriguez-Diaz
 */
public class Environment {
	public static final String PELLET_HOME_PROP = "pellet.home";

	private static final String PELLET_HOME_ENV = "PELLET_HOME";

	private static final String PELLET_INSTALL_PROP = "pellet.install.location";

	private static final String PELLET_INSTALL_ENV = "PELLET_HOME";

	private Environment() {
		throw new AssertionError();
	}

	public static void assertHome() {
		final String aHome = System.getProperty(PELLET_HOME_PROP);

		if (isNullOrEmpty(aHome)) {
			System.setProperty(PELLET_HOME_PROP, getHome());
		}

		checkHome(new File(getHome()));
	}

	public static void checkHome(final File theHome) {
		if (!theHome.exists()) {
			System.out.println(String.format("PELLET_HOME directory \'%s\' does not exist",
			                                 theHome.getAbsolutePath()));
			System.exit(1);
		}

		if (!theHome.isDirectory()) {
			System.out.println(String.format("PELLET_HOME directory \'%s\' is not a directory",
			                                 theHome.getAbsolutePath()));
			System.exit(1);
		}

		if (!theHome.canRead()) {
			System.out.println(String.format("PELLET_HOME directory \'%s\' is not readable by the current user",
			                                 theHome.getAbsolutePath()));
			System.exit(1);
		}

		if (!theHome.canWrite()) {
			System.out.println(String.format("PELLET_HOME directory \'%s\' is not writeable by the current user",
			                                 theHome.getAbsolutePath()));
			System.exit(1);
		}

		// Additional check (mostly for Windows, where the previous checks
		// like canRead(), canWrite() always return true, even
		// if we have no write permission there).
		try {
			File tempFile = File.createTempFile("pellet", ".tmp", theHome);
			tempFile.delete();
		}
		catch (IOException e) {
			// if we cannot create a temporary file in PELLET_HOME, then the directory
			// is effectively not writeable by us
			System.out.println(String.format("PELLET_HOME directory \'%s\' is not writeable by the current user",
			                                 theHome.getAbsolutePath()));
			System.exit(1);
		}
	}

	public static void setHome(final Path theHome) {
		System.setProperty(PELLET_HOME_PROP, theHome.toString());
	}

	public static String getHome() {
		final String aHome = System.getProperty(PELLET_HOME_PROP);

		if (!isNullOrEmpty(aHome)) {
			return aHome;
		}

		return !isNullOrEmpty(System.getenv(PELLET_HOME_ENV))
		       ? System.getenv(PELLET_HOME_ENV)
		       : !isNullOrEmpty(System.getProperty(PELLET_INSTALL_PROP))
		         ? System.getProperty(PELLET_INSTALL_PROP)
		         : !isNullOrEmpty(System.getProperty(PELLET_INSTALL_ENV))
		           ? System.getProperty(PELLET_INSTALL_ENV)
		           : System.getProperty("user.dir"); // if anything else fails, put it in the working dir
	}

	public static void cleanHome() {
		final Path aHome = Paths.get(getHome());
		final TreeTraverser<File> aTraverser = com.google.common.io.Files.fileTreeTraverser();

		for (File aFile : aTraverser.postOrderTraversal(aHome.toFile())) {
			aFile.delete();
		}
	}
}
