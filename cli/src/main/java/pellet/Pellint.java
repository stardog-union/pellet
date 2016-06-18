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

import com.clarkparsia.pellint.lintpattern.LintPattern;
import com.clarkparsia.pellint.lintpattern.LintPatternLoader;
import com.clarkparsia.pellint.lintpattern.axiom.AxiomLintPattern;
import com.clarkparsia.pellint.lintpattern.ontology.OntologyLintPattern;
import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.model.OntologyLints;
import com.clarkparsia.pellint.model.Severity;
import com.clarkparsia.pellint.rdfxml.OWLSyntaxChecker;
import com.clarkparsia.pellint.rdfxml.RDFLints;
import com.clarkparsia.pellint.rdfxml.RDFModel;
import com.clarkparsia.pellint.rdfxml.RDFModelReader;
import com.clarkparsia.pellint.rdfxml.RDFModelWriter;
import com.clarkparsia.pellint.util.CollectionUtil;
import com.clarkparsia.pellint.util.IllegalPellintArgumentException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import openllet.shared.tools.Log;
import org.apache.jena.rdf.model.Statement;
import org.mindswap.pellet.utils.FileUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StreamDocumentTarget;
import org.semanticweb.owlapi.io.XMLUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;
import org.semanticweb.owlapi.profiles.OWL2DLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;
import org.semanticweb.owlapi.util.OWLOntologyImportsClosureSetProvider;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

/**
 * <p>
 * Title: Pellint Main class
 * </p>
 * <p>
 * Description: Provides CLI and API interfaces for the Pellint program
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Harris Lin
 */
public class Pellint extends PelletCmdApp
{
	private static final String CONFIGURATION_PROPERTY_NAME = "pellint.configuration";
	private static final String DEFAULT_CONFIGURATION_FILE_NAME = "pellint.properties";
	private static final IRI MERGED_ONTOLOGY_URI = IRI.create("tag:clarkparsia.com,2008:pellint:merged");

	private boolean _doRDF = true;
	private boolean _doOWL = true;
	private boolean _doRootOnly = false;
	private String _inputOntologyPath;
	private String _outputOntologyPath;

	public Pellint()
	{
	}

	@Override
	public void parseArgs(final String[] args)
	{
		super.parseArgs(args);

		final String only = _options.getOption("only").getValueAsString();
		if (only == null)
		{
			setDoRDF(true);
			setDoOWL(true);
		}
		else
			if (only.equalsIgnoreCase("RDF"))
			{
				setDoRDF(true);
				setDoOWL(false);
			}
			else
				if (only.equalsIgnoreCase("OWL"))
				{
					setDoRDF(false);
					setDoOWL(true);
				}
				else
					throw new PelletCmdException("Invalid argument to lint --only: " + only);

		setDoRootOnly(_options.getOption("root-only").getValueAsBoolean());

		if (getInputFiles().length > 1)
			throw new PelletCmdException("lint doesn't handle multiple input files");

		setInputOntologyPath(getInputFiles()[0]);

		setOutputOntologyPath(_options.getOption("fix").getValueAsString());
	}

	@Override
	public String getAppId()
	{
		return "Pellint: Lint tool for OWL ontologies";
	}

	@Override
	public String getAppCmd()
	{
		final String s1 = "pellet lint [_options] <file URI> ...\n";
		final String s2 = "Note: pellet lint <file URI> without arguments prints the lint report to STDOUT.";
		final String lb = System.getProperty("line.separator");
		final String s = s1 + lb + lb + s2;
		return s;
	}

	<T extends Serializable> void f(@SuppressWarnings("unused") final Map<T, T> arg)
	{
		//
	}

	@Override
	public PelletCmdOptions getOptions()
	{
		final PelletCmdOptions options = getGlobalOptions();

		PelletCmdOption option = new PelletCmdOption("fix");
		option.setShortOption("f");
		option.setIsMandatory(false);
		option.setType("File");
		option.setDescription("Apply any applicable fixes to ontology lints and save a new ontology to file in RDF/XML format.");
		option.setArg(REQUIRED);
		options.add(option);

		option = new PelletCmdOption("root-only");
		option.setIsMandatory(false);
		option.setDefaultValue(false);
		option.setType("boolean");
		option.setDescription("Lint the root ontology only; ignore its imports.");
		option.setArg(NONE);
		options.add(option);

		option = new PelletCmdOption("only");
		option.setShortOption("o");
		option.setIsMandatory(false);
		option.setType("RDF | OWL");
		option.setDescription("Analyze only RDF declarations or OWL axioms, not both.");
		option.setArg(REQUIRED);
		options.add(option);

		option = new PelletCmdOption("exclude-valid-punning");
		option.setIsMandatory(false);
		option.setDefaultValue(false);
		option.setDescription("Excludes valid punnings to be reported by lint. OWL 2 allows resources\n" + "to have certain multiple types (known as punning), e.g. a resource can\n" + "be both a class and an _individual. However, certain punnings are not\n" + "allowed under any _condition, e.g. a resource cannot be both a datatype\n" + "property and an object property. All punnings are reported by default\n" + "but if this option is used punnings valid for OWL 2 will be excluded\n" + "from the report.");
		option.setArg(NONE);
		options.add(option);

		return options;
	}

	public void setDoRDF(final boolean v)
	{
		_doRDF = v;
	}

	public void setDoOWL(final boolean v)
	{
		_doOWL = v;
	}

	public void setDoRootOnly(final boolean v)
	{
		_doRootOnly = v;
	}

	public void setInputOntologyPath(final String v)
	{
		_inputOntologyPath = v;
	}

	public void setOutputOntologyPath(final String v)
	{
		_outputOntologyPath = v;
	}

	public static OntologyLints lint(final List<AxiomLintPattern> axiomLintPatterns, final List<OntologyLintPattern> ontologyLintPatterns, final OWLOntology ontology)
	{
		final OntologyLints ontologyLints = new OntologyLints(ontology);
		ontology.axioms().forEach(axiom ->
		{
			for (final AxiomLintPattern pattern : axiomLintPatterns)
			{
				final Lint lint = pattern.match(ontology, axiom);
				if (lint != null)
					ontologyLints.addLint(pattern, lint);
			}
		});

		for (final OntologyLintPattern pattern : ontologyLintPatterns)
		{
			final List<Lint> lints = pattern.match(ontology);
			if (!lints.isEmpty())
				ontologyLints.addLints(pattern, lints);
		}

		ontologyLints.sort((lint0, lint1) ->
		{
			final Severity severity0 = lint0.getSeverity();
			final Severity severity1 = lint1.getSeverity();
			if (severity0 != null && severity1 != null)
				return -severity0.compareTo(severity1);

			final Set<OWLClass> classes0 = lint0.getParticipatingClasses();
			final Set<OWLClass> classes1 = lint1.getParticipatingClasses();
			if (classes0 == null || classes1 == null)
				return 0;
			if (classes0.size() != 1 || classes1.size() != 1)
				return 0;

			final IRI uri0 = classes0.iterator().next().getIRI();
			final IRI uri1 = classes1.iterator().next().getIRI();
			if (uri0 == null || uri1 == null)
				return 0;

			final String fragment0 = XMLUtils.getNCNameSuffix(uri0);
			final String fragment1 = XMLUtils.getNCNameSuffix(uri1);
			if (fragment0 == null || fragment1 == null)
				return 0;

			return fragment0.compareTo(fragment1);
		});

		return ontologyLints;
	}

	@Override
	public void run()
	{
		try
		{
			if (_inputOntologyPath == null)
				throw new IllegalPellintArgumentException("Input ontology is not specified");

			if (_doRDF)
				runLintForRDFXML();

			if (_doOWL)
				runLintForOWL();
		}
		catch (final IllegalPellintArgumentException e)
		{
			e.printStackTrace();
		}
		catch (final MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
		catch (final OWLOntologyCreationException e)
		{
			e.printStackTrace();
		}
		catch (final OWLOntologyStorageException e)
		{
			e.printStackTrace();
		}
		catch (final OWLOntologyChangeException e)
		{
			e.printStackTrace();
		}
	}

	private void runLintForRDFXML() throws IOException
	{
		final RDFModelReader reader = new RDFModelReader();
		RDFModel rootModel = null;
		try
		{
			rootModel = reader.read(_inputOntologyPath, !_doRootOnly);
		}
		catch (final Exception e)
		{
			throw new PelletCmdException(e);
		}
		final OWLSyntaxChecker checker = new OWLSyntaxChecker();

		checker.setExcludeValidPunnings(_options.getOption("exclude-valid-punning").getValueAsBoolean());

		final RDFLints lints = checker.validate(rootModel);

		output(lints.toString());

		if (_outputOntologyPath != null && !_doOWL)
		{
			final List<Statement> missingStmts = lints.getMissingStatements();

			rootModel.addAllStatementsWithExistingBNodesOnly(missingStmts);

			final RDFModelWriter writer = new RDFModelWriter();
			writer.write(new FileOutputStream(new File(_outputOntologyPath)), rootModel);
			output("Saved to " + _outputOntologyPath);
		}
	}

	private void runLintForOWL() throws OWLOntologyCreationException, OWLOntologyChangeException, UnknownOWLOntologyException, OWLOntologyStorageException, FileNotFoundException
	{
		final LintPatternLoader patternLoader = new LintPatternLoader(loadProperties());
		final List<AxiomLintPattern> axiomLintPatterns = patternLoader.getAxiomLintPatterns();
		final List<OntologyLintPattern> ontologyLintPatterns = patternLoader.getOntologyLintPatterns();
		logLoadedPatterns(axiomLintPatterns, ontologyLintPatterns);

		final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		OWLOntology rootOntology = null;
		try
		{
			final String inputOntologyURI = FileUtils.toURI(_inputOntologyPath);
			rootOntology = manager.loadOntology(IRI.create(inputOntologyURI));
		}
		catch (final Exception e)
		{
			throw new PelletCmdException(e);
		}

		output(getOWL2DLProfileViolations(rootOntology));

		final OntologyLints rootOntologyLints = lint(axiomLintPatterns, ontologyLintPatterns, rootOntology);
		output(rootOntologyLints.toString());

		if (!_doRootOnly)
		{
			final Set<OWLOntology> importClosures = manager.importsClosure(rootOntology).collect(Collectors.toSet());
			importClosures.remove(rootOntology);

			if (importClosures.isEmpty())
				output("\n" + rootOntology.getOntologyID() + " does not import other ontologies.");
			else
			{
				for (final OWLOntology importedOntology : importClosures)
				{
					output(getOWL2DLProfileViolations(importedOntology));

					final OntologyLints importedOntologyLints = lint(axiomLintPatterns, ontologyLintPatterns, importedOntology);
					output(importedOntologyLints.toString());
				}

				final OWLOntology mergedImportClosure = buildMergedImportClosure(manager, rootOntology);
				final OntologyLints mergedOntologyLints = lint(axiomLintPatterns, ontologyLintPatterns, mergedImportClosure);
				mergedOntologyLints.setRootOntology(rootOntology);
				output(mergedOntologyLints.toString());
			}
		}

		if (_outputOntologyPath != null)
		{
			final Set<Lint> unreparableLints = rootOntologyLints.applyFix(manager);
			if (!unreparableLints.isEmpty())
			{
				output("Unreparable lints:");
				for (final Lint lint : unreparableLints)
					output(lint.toString());
			}
			manager.saveOntology(rootOntologyLints.getOntology(), new StreamDocumentTarget(new FileOutputStream(_outputOntologyPath)));
			output("Saved to " + _outputOntologyPath);

		}
	}

	private String getOWL2DLProfileViolations(final OWLOntology ontology)
	{
		final OWL2DLProfile owl2Profile = new OWL2DLProfile();
		final OWLProfileReport profileReport = owl2Profile.checkOntology(ontology);

		if (profileReport.isInProfile())
			return "No OWL 2 DL violations found for ontology " + ontology.getOntologyID().toString();

		final StringBuffer result = new StringBuffer();
		result.append("\n=========================================================\n");
		result.append("OWL 2 DL violations found for ontology ").append(ontology.getOntologyID().toString()).append(":\n");

		for (final OWLProfileViolation violation : profileReport.getViolations())
		{
			result.append(violation.toString());
			result.append("\n");
		}

		return result.toString();
	}

	private void logLoadedPatterns(final List<AxiomLintPattern> axiomLintPatterns, final List<OntologyLintPattern> ontologyLintPatterns)
	{
		if (!getLogger().isLoggable(Level.FINE))
			return;

		final List<LintPattern> allPatterns = CollectionUtil.<LintPattern> copy(axiomLintPatterns);
		allPatterns.addAll(ontologyLintPatterns);
		Collections.sort(allPatterns, (p0, p1) -> p0.getName().compareTo(p1.getName()));

		getLogger().fine("Loaded lint patterns:");
		for (final LintPattern pattern : allPatterns)
		{
			final StringBuilder builder = new StringBuilder();
			builder.append("  ");
			if (pattern.isFixable())
				builder.append("[fixable] ");
			else
				builder.append("          ");
			builder.append(pattern.getName());
			getLogger().fine(builder.toString());
		}
	}

	private static OWLOntology buildMergedImportClosure(final OWLOntologyManager manager, final OWLOntology rootOntology) throws OWLOntologyCreationException, OWLOntologyChangeException
	{

		final OWLOntologyImportsClosureSetProvider importClosureSetProvider = new OWLOntologyImportsClosureSetProvider(manager, rootOntology);
		final OWLOntologyMerger merger = new OWLOntologyMerger(importClosureSetProvider);
		return merger.createMergedOntology(manager, MERGED_ONTOLOGY_URI);
	}

	private static Properties loadProperties()
	{
		final Properties properties = new Properties();

		final String configFile = System.getProperty(CONFIGURATION_PROPERTY_NAME);
		URL configURL = null;

		// if the user has not specified the pellint.configuration property,
		// we search for the file "pellint.properties"
		if (configFile == null)
		{
			configURL = Pellint.class.getClassLoader().getResource(DEFAULT_CONFIGURATION_FILE_NAME);

			if (configURL == null)
				_logger.severe("Cannot find Pellint configuration file " + DEFAULT_CONFIGURATION_FILE_NAME);
		}
		else
		{
			try
			{
				configURL = new URL(configFile);
			}
			catch (final MalformedURLException e)
			{
				e.printStackTrace();

				// so, resource is not a URL - attempt to get the resource from
				// the class path
				configURL = Pellint.class.getClassLoader().getResource(configFile);
			}

			if (configURL == null)
				_logger.severe("Cannot find Pellint configuration file " + configFile);
		}

		if (configURL != null)
			try
			{
				properties.load(configURL.openStream());
			}
			catch (final FileNotFoundException e)
			{
				Log.error(_logger, "Pellint configuration file cannot be found", e);
			}
			catch (final IOException e)
			{
				Log.error(_logger, "I/O error while reading Pellet configuration file", e);
			}

		return properties;
	}
}
