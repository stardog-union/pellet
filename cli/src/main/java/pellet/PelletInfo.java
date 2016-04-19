package pellet;

import static pellet.PelletCmdOptionArg.NONE;
import static pellet.PelletCmdOptionArg.REQUIRED;

import com.clarkparsia.pellet.owlapi.LimitedMapIRIMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.mindswap.pellet.utils.FileUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.profiles.OWL2DLProfile;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWL2Profile;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.profiles.OWL2RLProfile;
import org.semanticweb.owlapi.profiles.OWLProfile;
import org.semanticweb.owlapi.util.DLExpressivityChecker;
import org.semanticweb.owlapi.util.NonMappingOntologyIRIMapper;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class PelletInfo extends PelletCmdApp
{
	private final List<OWLProfile> profiles = Arrays.asList(new OWL2ELProfile(), new OWL2QLProfile(), new OWL2RLProfile(), new OWL2DLProfile(), new OWL2Profile());

	@Override
	public String getAppCmd()
	{
		return "pellet info " + getMandatoryOptions() + "[options] <file URI>...";
	}

	@Override
	public String getAppId()
	{
		return "PelletInfo: Display information and statistics about 1 or more ontologies";
	}

	@Override
	public PelletCmdOptions getOptions()
	{
		final PelletCmdOptions options = new PelletCmdOptions();

		//Don't call getGlobalOptions(), since we override the behaviour of verbose
		final PelletCmdOption helpOption = new PelletCmdOption("help");
		helpOption.setShortOption("h");
		helpOption.setDescription("Print this message");
		helpOption.setDefaultValue(false);
		helpOption.setIsMandatory(false);
		helpOption.setArg(NONE);
		options.add(helpOption);

		final PelletCmdOption verboseOption = new PelletCmdOption("verbose");
		verboseOption.setShortOption("v");
		verboseOption.setDescription("More verbose output");
		verboseOption.setDefaultValue(false);
		verboseOption.setIsMandatory(false);
		verboseOption.setArg(NONE);
		options.add(verboseOption);

		final PelletCmdOption configOption = new PelletCmdOption("config");
		configOption.setShortOption("C");
		configOption.setDescription("Use the selected configuration file");
		configOption.setIsMandatory(false);
		configOption.setType("configuration file");
		configOption.setArg(REQUIRED);
		options.add(configOption);

		final PelletCmdOption option = new PelletCmdOption("merge");
		option.setShortOption("m");
		option.setDescription("Merge the ontologies");
		option.setDefaultValue(false);
		option.setIsMandatory(false);
		option.setArg(PelletCmdOptionArg.NONE);
		options.add(option);

		options.add(getIgnoreImportsOption());

		return options;
	}

	@Override
	public void run()
	{

		try
		{
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			final Collection<String> inputFiles = FileUtils.getFileURIs(getInputFiles());

			final LimitedMapIRIMapper iriMapper = new LimitedMapIRIMapper();
			final OWLOntology baseOntology = manager.createOntology();
			manager.clearIRIMappers();

			if (options.getOption("ignore-imports").getValueAsBoolean())
			{
				manager.addIRIMapper(iriMapper);
				manager.setOntologyLoaderConfiguration(manager.getOntologyLoaderConfiguration().setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT));
			}
			else
			{
				manager.addIRIMapper(new NonMappingOntologyIRIMapper());
				manager.setOntologyLoaderConfiguration(manager.getOntologyLoaderConfiguration().setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT));
			}

			if (inputFiles.size() > 1)
				for (final String inputFile : inputFiles)
					addFile(inputFile, manager, iriMapper, baseOntology);
			else
				addSingleFile(inputFiles.iterator().next(), manager, iriMapper); //Prevent ugly OWLAPI messages

			manager.removeOntology(baseOntology);

			if (options.getOption("merge").getValueAsBoolean())
				manager = mergeOntologiesInNewManager(manager);

			printStats(manager);

		}
		catch (final Exception e)
		{
			throw new PelletCmdException(e);
		}
	}

	private void addFile(final String inputFile, final OWLOntologyManager manager, final LimitedMapIRIMapper iriMapper, final OWLOntology baseOntology)
	{
		try
		{
			final IRI iri = IRI.create(inputFile);
			iriMapper.addAllowedIRI(iri);

			final OWLImportsDeclaration declaration = manager.getOWLDataFactory().getOWLImportsDeclaration(iri);
			manager.applyChange(new AddImport(baseOntology, declaration));
			manager.makeLoadImportRequest(declaration);
		}
		catch (final Exception e)
		{
			if (verbose)
				System.err.println(e.getLocalizedMessage());
		}
	}

	private void addSingleFile(final String inputFile, final OWLOntologyManager manager, final LimitedMapIRIMapper iriMapper)
	{
		try
		{
			final IRI iri = IRI.create(inputFile);
			iriMapper.addAllowedIRI(iri);
			manager.loadOntologyFromOntologyDocument(iri);
		}
		catch (final Exception e)
		{
			if (verbose)
				System.err.println(e.getLocalizedMessage());
		}
	}

	private OWLOntologyManager mergeOntologiesInNewManager(final OWLOntologyManager manager) throws OWLOntologyCreationException
	{
		final OWLOntologyManager newManager = OWLManager.createOWLOntologyManager();
		final OWLOntology merged = newManager.createOntology();
		final List<OWLOntologyChange> changes = new ArrayList<>();

		for (final OWLOntology ontology : manager.getOntologies())
			for (final OWLAxiom ax : ontology.getAxioms())
				changes.add(new AddAxiom(merged, ax));
		newManager.applyChanges(changes);
		return newManager;
	}

	private void printStats(final OWLOntologyManager manager)
	{
		for (final OWLOntology ontology : manager.getOntologies())
		{
			final String ontologyLocation = manager.getOntologyDocumentIRI(ontology) != null ? manager.getOntologyDocumentIRI(ontology).toString() : "ontology";
			final String ontologyBaseURI = ontology.getOntologyID().getOntologyIRI().isPresent() ? ontology.getOntologyID().getOntologyIRI().get().toQuotedString() : "";
							output("Information about " + ontologyLocation + " (" + ontologyBaseURI + ")");
							if (verbose)
								printOntologyHeader(ontology);
							final DLExpressivityChecker expressivityChecker = new DLExpressivityChecker(Collections.singleton(ontology));
							output("OWL Profile = " + getProfile(ontology));
							output("DL Expressivity = " + expressivityChecker.getDescriptionLogicName());
							output("Axioms = " + ontology.getAxiomCount());
							output("Logical Axioms = " + ontology.getLogicalAxiomCount());
							output("GCI Axioms = " + ontology.getGeneralClassAxioms().size());
							output("Individuals = " + ontology.getIndividualsInSignature().size());
							output("Classes = " + ontology.getClassesInSignature().size());
							output("Object Properties = " + ontology.getObjectPropertiesInSignature().size());
							output("Data Properties = " + ontology.getDataPropertiesInSignature().size());
			output("Annotation Properties = " + ontology.getAnnotationPropertiesInSignature().size());

							final Set<OWLImportsDeclaration> imports = ontology.getImportsDeclarations();
							if (imports.size() > 0)
							{
								output("Direct Imports:");
								int count = 1;
								for (final OWLImportsDeclaration imp : imports)
									output(count + ": " + imp.getIRI().toString());
								count++;
							}
							output("");
		}
	}

	private String getProfile(final OWLOntology ontology)
	{
		for (final OWLProfile profile : profiles)
			if (profile.checkOntology(ontology).isInProfile())
				return profile.getName();
		return "Unknown Profile";
	}

	private void printOntologyHeader(final OWLOntology ontology)
	{
		for (final OWLAnnotation annotation : ontology.getAnnotations())
		{
			final IRI property = annotation.getProperty().getIRI();
			final OWLAnnotationValue value = annotation.getValue();

			if (property.equals(OWLRDFVocabulary.OWL_VERSION_INFO.getIRI()))
				verbose("Version Info = " + getString(value));
			else
				if (property.equals(OWLRDFVocabulary.OWL_PRIOR_VERSION.getIRI()))
					verbose("Prior Version Info = " + getString(value));
				else
					if (property.equals(OWLRDFVocabulary.OWL_BACKWARD_COMPATIBLE_WITH.getIRI()))
						verbose("Backward Compatible With = " + getString(value));
					else
						if (property.equals(OWLRDFVocabulary.OWL_INCOMPATIBLE_WITH.getIRI()))
							verbose("Incompatible With = " + getString(value));
		}
	}

	private String getString(final OWLAnnotationValue value)
	{
		if (value instanceof OWLLiteral)
			return ((OWLLiteral) value).getLiteral();
		else
			return value.toString();
	}
}
