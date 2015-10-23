// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.clarkparsia.modularity.ModuleExtractor;
import org.mindswap.pellet.taxonomy.Taxonomy;
import org.semanticweb.owlapi.model.OWLClass;

/**
 * Provides persistence for IncrementalClassifier objects.
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Blazej Bulka
 */
public class IncrementalClassifierPersistence {
	/**
	 * The name for the zip entry that stores the taxonomy.
	 */
	private static final String TAXONOMY_FILE_NAME = "Taxonomy";
	
	private static final String PROPERTIES_FILE_NAME = "Properties";
	
	private static final String PROPERTIES_FILE_COMMENT = "Properties of the IncrementalClassifier";
	
	private static final String REALIZED_PROPERTY = "realized";

	/**
	 * Saves the internal state of an incremental classifier to an output stream.
	 */
	public static void save( OutputStream out, ModuleExtractor moduleExtractor, Taxonomy taxonomy ) throws IOException {
		ZipOutputStream zipOutputStream = new ZipOutputStream(out);

		try {
			// save the module extractor
			moduleExtractor.save(zipOutputStream);

			// save the taxonomy
			ZipEntry taxonomyEntry = new ZipEntry(TAXONOMY_FILE_NAME);
			zipOutputStream.putNextEntry(taxonomyEntry);

			TaxonomyPersistence.save(taxonomy, new UncloseableOutputStream(zipOutputStream));

			ZipEntry propertiesEntry = new ZipEntry(PROPERTIES_FILE_NAME);
			zipOutputStream.putNextEntry(propertiesEntry);

			Properties properties = new Properties();
			properties.setProperty(REALIZED_PROPERTY, String.valueOf(taxonomy.getTop().getDatum(REALIZED_PROPERTY)));
			properties.store(zipOutputStream, PROPERTIES_FILE_COMMENT);
		}
		finally {
			zipOutputStream.close();
		}
	}

	public static Taxonomy load(InputStream inputStream, ModuleExtractor extractor) throws IOException {
		ZipInputStream zipInputStream = new ZipInputStream( inputStream );
		try {
			extractor.load(zipInputStream);

			ZipEntry currentEntry = zipInputStream.getNextEntry();

			if (!(TAXONOMY_FILE_NAME.equals(currentEntry.getName()))) {
				throw new IOException(String.format("Unexpected entry (%s) in ZipInputStream. Expected %s", currentEntry.getName(), TAXONOMY_FILE_NAME));
			}

			Taxonomy<OWLClass> taxonomy = TaxonomyPersistence.load(zipInputStream);

			Properties properties = new Properties();
			currentEntry = zipInputStream.getNextEntry();
			if ((currentEntry != null) && (PROPERTIES_FILE_NAME.equals(currentEntry.getName()))) {
				properties.load(zipInputStream);
			}

			boolean realized = Boolean.valueOf(properties.getProperty(REALIZED_PROPERTY, "false"));

			taxonomy.getTop().putDatum(REALIZED_PROPERTY, realized);

			return taxonomy;
		}
		finally {
			zipInputStream.close();
		}
	}
}
