// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package pellet;

import static pellet.PelletCmdOptionArg.NONE;

import aterm.ATermAppl;
import com.clarkparsia.modularity.IncrementalClassifier;
import com.clarkparsia.modularity.OntologyDiff;
import com.clarkparsia.modularity.io.IncrementalClassifierPersistence;
import com.clarkparsia.pellet.owlapi.OWLAPILoader;
import com.clarkparsia.pellet.owlapi.OWLClassTreePrinter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.taxonomy.printer.ClassTreePrinter;
import org.mindswap.pellet.taxonomy.printer.TaxonomyPrinter;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * <p>
 * Title: PelletClassify
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
public class PelletClassify extends PelletCmdApp
{

	/**
	 * Maximum radix for encoding of the MD5 of the root ontology URI
	 */
	private static final int ENCODING_RADIX = 36;

	/**
	 * The pattern for the names of the files containing the persisted _data of the incremental classifier. The parameter in the pattern should be replaced with
	 * the MD5 of the root ontology IRI (to prevent mixing up files that belong to different ontologies).
	 */
	private static final String FILE_NAME_PATTERN = "persisted-state-%s.zip";

	/**
	 * The directory where persisted state is saved (for now, this is the _current directory).
	 */
	private final File saveDirectory = new File(".");

	/**
	 * Boolean flag whether the state of the classifier saved on disk is up-to-date
	 */
	private boolean currentStateSaved = false;

	public PelletClassify()
	{
	}

	@Override
	public String getAppCmd()
	{
		return "pellet classify " + getMandatoryOptions() + "[_options] <file URI>...";
	}

	@Override
	public String getAppId()
	{
		return "PelletClassify: Classify the ontology and display the hierarchy";
	}

	@Override
	public PelletCmdOptions getOptions()
	{
		final PelletCmdOptions options = getGlobalOptions();

		final PelletCmdOption option = new PelletCmdOption("persist");
		option.setShortOption("p");
		option.setDescription("Enable persistence of classification results. The classifier will save its internal state in a file, and will reuse it the next time this ontology is loaded, therefore saving classification time. This option can only be used with OWLAPI _loader.");
		option.setIsMandatory(false);
		option.setArg(NONE);
		options.add(option);

		options.add(getLoaderOption());
		options.add(getIgnoreImportsOption());
		options.add(getInputFormatOption());

		return options;
	}

	@Override
	public void run()
	{
		if (_options.getOption("persist").getValueAsBoolean())
			runIncrementalClassify();
		else
			runClassicClassify();
	}

	/**
	 * Performs classification using the non-incremental (classic) classifier
	 */
	private void runClassicClassify()
	{
		final KnowledgeBase kb = getKB();

		startTask("consistency check");
		final boolean isConsistent = kb.isConsistent();
		finishTask("consistency check");

		if (!isConsistent)
			throw new PelletCmdException("Ontology is inconsistent, run \"pellet explain\" to get the reason");

		startTask("classification");
		kb.classify();
		finishTask("classification");

		final TaxonomyPrinter<ATermAppl> printer = new ClassTreePrinter();
		printer.print(kb.getTaxonomy());
	}

	/**
	 * Performs classification using the incremental classifier (and persisted _data)
	 */
	private void runIncrementalClassify()
	{
		final String loaderName = _options.getOption("loader").getValueAsString();

		if (!"OWLAPI".equals(loaderName))
			logger.log(Level.WARNING, "Ignoring -l " + loaderName + " option. When using --persist the only allowed _loader is OWLAPI");

		final OWLAPILoader loader = (OWLAPILoader) getLoader("OWLAPI");

		loader.parse(getInputFiles());
		final OWLOntology ontology = loader.getOntology();

		final IncrementalClassifier incrementalClassifier = createIncrementalClassifier(ontology);

		if (!incrementalClassifier.isClassified())
		{
			startTask("consistency check");
			final boolean isConsistent = incrementalClassifier.isConsistent();
			finishTask("consistency check");

			if (!isConsistent)
				throw new PelletCmdException("Ontology is inconsistent, run \"pellet explain\" to get the reason");

			startTask("classification");
			incrementalClassifier.classify();
			finishTask("classification");
		}

		final TaxonomyPrinter<OWLClass> printer = new OWLClassTreePrinter();
		printer.print(incrementalClassifier.getTaxonomy());

		if (!currentStateSaved)
			persistIncrementalClassifier(incrementalClassifier, ontology);
	}

	/**
	 * Creates incremental classifier by either creating it from scratch or by reading its state from file (if there exists such a state)
	 *
	 * @param ontology the ontology (the _current state of it)
	 * @return the incremental classifier
	 */
	private IncrementalClassifier createIncrementalClassifier(final OWLOntology ontology)
	{
		final File saveFile = determineSaveFile(ontology);
		IncrementalClassifier result = null;

		// first try to restore the classifier from the file (if one exists)
		if (saveFile.exists())
			result = loadIncrementalClassifier(ontology, saveFile);

		// if it was not possible to restore the classifier, create one from scratch
		if (result == null)
			result = new IncrementalClassifier(ontology);

		result.getReasoner().getKB().setTaxonomyBuilderProgressMonitor(PelletOptions.USE_CLASSIFICATION_MONITOR.create());

		return result;
	}

	/**
	 * Stores the _current state of the incremental classifier to a file (the file name is determined automatically based on ontology's IRI).
	 *
	 * @param incrementalClassifier the incremental classifier to be stored
	 * @param ontology the ontology
	 */
	private void persistIncrementalClassifier(final IncrementalClassifier incrementalClassifier, final OWLOntology ontology)
	{
		final File saveFile = determineSaveFile(ontology);

		try
		{
			verbose("Saving the state of the classifier to " + saveFile);
			final FileOutputStream outputStream = new FileOutputStream(saveFile);
			IncrementalClassifierPersistence.save(incrementalClassifier, outputStream);
		}
		catch (final IOException e)
		{
			logger.log(Level.WARNING, "Unable to persist the _current classifier state: " + e.toString());
		}
	}

	/**
	 * Loads the incremental classifier from a file. If the ontology changed since the state of the classifier was persisted, the classifier will be
	 * incrementally updated with the changes.
	 *
	 * @param ontology the ontology (its _current state, since class
	 * @param file the file from which the persisted state will be read
	 * @return the read classifier or null, if it was not possible to read the classifier
	 */
	private IncrementalClassifier loadIncrementalClassifier(final OWLOntology ontology, final File file)
	{
		try
		{
			final FileInputStream inputStream = new FileInputStream(file);

			verbose("Reading persisted classifier state from " + file);
			final IncrementalClassifier result = IncrementalClassifierPersistence.load(inputStream, ontology);

			// check whether anything changed in the ontology in the time between the incremental classifier
			// was persisted and the _current time
			final OntologyDiff ontologyDiff = OntologyDiff.diffAxioms(result.getAxioms(), ontology.axioms().collect(Collectors.toSet()));

			if (ontologyDiff.getDiffCount() > 0)
			{
				verbose("There were changes to the underlying ontology since the classifier was persisted. Incrementally updating the classifier");
				result.ontologiesChanged(new LinkedList<>(ontologyDiff.getChanges(ontology)));
			}
			else
				currentStateSaved = true;

			return result;
		}
		catch (final IOException e)
		{
			logger.log(Level.WARNING, "Unable to read the persisted information from a file. Pellet will perform full classification: " + e);

			return null;
		}
	}

	/**
	 * Computes the name of the file to which the state of the incremental classifier will be persisted/read from.
	 *
	 * @return the file name
	 */
	private File determineSaveFile(final OWLOntology ontology)
	{
		final String fileName = String.format(FILE_NAME_PATTERN, hashOntologyIRI(ontology));

		return new File(saveDirectory, fileName);
	}

	/**
	 * Computes the hash code of the ontology IRI and returns the string representation of the hash code. The hash code is used to identify which files contain
	 * information about the particular ontology (and we can't use directly IRIs since they can contain special characters that are not allowed in file names,
	 * not to mention that this would make the file names too long).
	 *
	 * @return the string representation of the hash code of the ontology IRI
	 */
	private String hashOntologyIRI(final OWLOntology ontology)
	{
		final byte[] uriBytes = ontology.getOntologyID().getOntologyIRI().toString().getBytes();

		MessageDigest MD5 = null;

		try
		{
			MD5 = MessageDigest.getInstance("MD5");
		}
		catch (final NoSuchAlgorithmException e)
		{
			throw new PelletCmdException("MD5 digest algorithm is not available.");
		}

		final byte[] hashBytes = MD5.digest(uriBytes);

		final BigInteger bi = new BigInteger(1, hashBytes);

		return bi.toString(ENCODING_RADIX);
	}
}
