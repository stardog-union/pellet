package openllet.owlapi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Stream;
import openllet.shared.tools.Logging;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormatFactory;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.io.XMLUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyAlreadyExistsException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.NodeSet;

/**
 * Functions that help management of OWL related matters. NB: This interface should replace every occurrence of OWLTools every where it is possible.
 * 
 * @since 2.5.1
 */
public interface OWLHelper extends Logging, OWLManagementObject
{
	final static boolean _debug = false;

	public static final String _protocol = "http://";
	public static final String _secureProtocol = "https://";
	public static final String _localProtocol = "file:/";
	public static final String _webSeparator = "/";
	public static final String _prefixSeparator = ":";
	public static final String _entitySeparator = "#";
	public static final String _innerSeparator = "_";
	public static final String _caseSeparator = "-";
	public static final String _fileExtention = ".owl";
	public static final String _delta = "owl.delta";
	public static final OWLXMLDocumentFormatFactory _formatFactory = new OWLXMLDocumentFormatFactory();
	public static final OWLDocumentFormat _format = _formatFactory.get();

	/**
	 * Add the export format in the configuration of the provided ontology
	 * 
	 * @param manager of the ontology you consider.
	 * @param ontology you consider.
	 * @since 2.5.1
	 */
	public static void setFormat(final OWLOntologyManager manager, final OWLOntology ontology)
	{
		manager.setOntologyFormat(ontology, _format);
	}

	/**
	 * @param ontologyIRI is the id of the ontology without version. The ontology name.
	 * @param version is the short representation you want for this ontology.
	 * @return the complete representation of the version for the given identifier of ontology.
	 * @since 2.5.1
	 */
	public static IRI buildVersion(final IRI ontologyIRI, final double version)
	{
		return IRI.create(ontologyIRI + _innerSeparator + version);
	}

	/**
	 * @param iri that should be use to generate a filename
	 * @return a relative path filename that reflect the iri.
	 * @since 2.5.1
	 */
	public static String iri2filename(final IRI iri)
	{
		if (iri == null) { throw new OWLException("iri2filename(null)"); }

		return iri.toString().replaceAll(_protocol, "").replaceAll(_secureProtocol, "").replaceAll(_webSeparator, _innerSeparator).replaceAll("&", _innerSeparator);
	}

	/**
	 * @param directory where the ontology will be put
	 * @param ontId is the id the ontology to convert.
	 * @return a full path filename that reflect the name of this ontology.
	 * @since 2.5.1
	 */
	public static String ontology2filename(final File directory, final OWLOntologyID ontId)
	{
		final String id = iri2filename(ontId.getOntologyIRI().get());
		final IRI versionIRI = ontId.getVersionIRI().get();
		final String version = (versionIRI != null) ? (iri2filename(versionIRI)) : "0";
		return directory + _webSeparator + id + _caseSeparator + version + _fileExtention;
	}

	/**
	 * @param directory where the ontology will be put
	 * @param ontology is the ontology from witch we want a name
	 * @return a full path filename that reflect the name of this ontology.
	 * @since 2.5.1
	 */
	public static String ontology2filename(final File directory, final OWLOntology ontology)
	{
		return ontology2filename(directory, ontology.getOntologyID());
	}

	//////////////////////////////// defaults methods //////////////////////////////////////

	/**
	 * @return the namespace utils that can resolve prefix.
	 * @since 2.5.1
	 */
	public default Optional<PrefixDocumentFormat> getNamespaces()
	{
		final OWLDocumentFormat format = getManager().getOntologyFormat(getOntology());
		return (format.isPrefixOWLDocumentFormat()) ? Optional.of((PrefixDocumentFormat) format) : Optional.empty();
	}

	/**
	 * @return the manager that manage the current ontology.
	 * @since 2.5.1
	 */
	public abstract OWLManagerGroup getGroup();

	/**
	 * @return true if this ontology isn't persistent.
	 * @since 2.5.1
	 */
	public abstract boolean isVolatile();

	/**
	 * @return the root of the default object insert in the ontology without namespace.
	 * @since 2.5.1
	 */
	default public IRI getRootIri()
	{
		return IRI.create(getOntology().getOntologyID().getOntologyIRI().get().getNamespace());
	}

	/**
	 * This function exist because the one in IRI is deprecated and will be remove : We want the memory of calling it 'fragment' preserved.
	 * 
	 * @return the NCNameSuffix
	 * @since 2.5.1
	 */
	default public String getFragment(IRI iri)
	{
		return XMLUtils.getNCNameSuffix(iri);
	}

	/**
	 * The standard 'getOntology' from the OWLManager don't really take care of versionning. This function is here to enforce the notion of version
	 * 
	 * @param ontologyID with version information
	 * @return the ontology if already load into the given manager.
	 * @since 2.5.1
	 */
	public default Optional<OWLOntology> getOntology(final OWLOntologyID ontologyID)
	{
		return OWLManagerGroup.getOntology(getManager(), ontologyID);
	}

	/**
	 * @return the shortest representation of the version of an ontology. Defaulting on 'zero' if no-version information.
	 * @since 2.5.1
	 */
	default public double getVersion()
	{
		final IRI version = getOntology().getOntologyID().getVersionIRI().get();
		if (version == null) { return 0; }
		try
		{
			final String fragment = getFragment(version);
			final int index = fragment.lastIndexOf(_innerSeparator.charAt(0));
			return Double.parseDouble(fragment.substring(index + 1));
		}
		catch (final Exception e)
		{
			getLogger().log(Level.SEVERE, version.toString() + " isn't a standard version format", e);
			throw new OWLException("Plz use OWLTools to manage your versions.", e);
		}
	}

	/**
	 * Clone into another ontology with the same axioms and same manager. NB : In a future version this function may return an ontology that share axiom with
	 * previous for memory saving.
	 * 
	 * @param version that will have the new ontology.
	 * @return a new ontology with the axioms of the given one.
	 * @throws OWLOntologyCreationException if we can't create the ontology.
	 * @since 2.5.1
	 */
	default public OWLHelper derivate(final double version) throws OWLOntologyCreationException
	{
		final OWLHelper result = new OWLGenericTools(this.getGroup(), this.getOntology().getOntologyID().getOntologyIRI().get(), version, isVolatile());
		if (result.getOntology().getAxiomCount() != 0)
			getLogger().warning(() -> "The ontology you try to derivate from " + getVersion() + " to version " + version + " already exist.");

		result.addAxioms(getOntology().axioms());
		return result;
	}

	/**
	 * Same as derivate but with a version number based on EPOCH time.
	 * 
	 * @return a new ontology with the axioms of the given one.
	 * @throws OWLOntologyCreationException if we can't create the ontology.
	 * @since 2.5.1
	 * @Deprecated because we want this function to return an OWLHelper
	 */
	default public OWLHelper derivate() throws OWLOntologyCreationException
	{
		return derivate(System.currentTimeMillis());
	}

	/**
	 * @param quoteExpression an expression with " at begin and end.
	 * @return the same expression without the first and last char.
	 * @since 1.1
	 */
	default public String removeFirstLast(final String quoteExpression)
	{
		String expression = quoteExpression;
		if (quoteExpression != null)
			expression = quoteExpression.substring(1, quoteExpression.length() - 1); // Remove the " at begin and end of literal.

		return expression;
	}

	/**
	 * @param parts of an uri
	 * @return a join of all parts separated by an entitySeparator.
	 * @since 2.5.1
	 */
	default String path(final String[] parts)
	{
		final StringBuffer buff = new StringBuffer();
		buff.append(parts[1]);

		for (int i = 2; i < parts.length; i++)
		{
			buff.append(_entitySeparator).append(parts[i]);
		}

		return buff.toString();
	}

	/**
	 * @param identifier to resolve
	 * @return parts of the identifiers using optionnaly prefix resolution.
	 * @since 2.5.1
	 */
	default public String[] resolvPrefix(final String identifier)
	{
		final String[] parts = identifier.split(":");

		if (parts.length == 0)
			return new String[] { getRootIri().toString(), "" };

		final Optional<PrefixDocumentFormat> space = getNamespaces();
		if (space.isPresent())
		{
			if (parts.length == 1)
				return new String[] { space.get().getPrefix(""), parts[0] };
			else
				return new String[] { space.get().getPrefix(parts[0]), path(parts) };
		}
		else
		{
			return new String[] { identifier };
		}
	}

	/**
	 * @param identifier of the data.
	 * @return an array of size 2, with the first element that contain the namespace and the second that contain the fragment.
	 */
	default String[] getNameSpace(final String identifier)
	{
		if (identifier.startsWith(_protocol) || identifier.startsWith(_secureProtocol))
		{
			final String[] parts = identifier.split(_entitySeparator); // XXX see also this : XMLUtils.getNCNamePrefix(identifier);
			switch (parts.length)
			{
				case 0:
					throw new RuntimeException("Error processing : " + identifier);
				case 1:
					return new String[] { identifier, "" };
				default:
				{
					return new String[] { parts[0], path(parts) };
				}
			}
		}
		else
		{
			return resolvPrefix(identifier);
		}
	}

	/**
	 * @param name to resolve
	 * @return an IRI using the name or information of name + namespace declaration of this ontology.
	 * @since 2.5.1
	 */
	default public IRI resolveToIRI(final String name)
	{
		if (IRIUtils.isIRI(name))
		{
			return IRI.create(name);
		}
		else
		{
			final String[] parts = getNameSpace(name);
			return IRI.create(parts[0] + _entitySeparator + parts[1]);
		}
	}

	/**
	 * @param name of the individual to declare, if name is IRI then it is ok else if name is not it is also ok.
	 * @return an individual
	 * @since 2.5.1
	 */
	default public OWLNamedIndividual declareIndividual(final String name)
	{
		return declareIndividual(resolveToIRI(name));
	}

	/**
	 * @param name of the property to declare, if name is IRI then it is ok else if name is not it is also ok.
	 * @return an property
	 * @since 2.5.1
	 */
	default public OWLObjectProperty declareObjectProperty(final String name)
	{
		return declareObjectProperty(resolveToIRI(name));
	}

	/**
	 * @param name of the property to declare, if name is IRI then it is ok else if name is not it is also ok.
	 * @return an property
	 * @since 2.5.1
	 */
	default public OWLDataProperty declareDataProperty(final String name)
	{
		return declareDataProperty(resolveToIRI(name));
	}

	/**
	 * @param name of the class to declare, if name is IRI then it is ok else if name is not it is also ok.
	 * @return a class
	 * @since 2.5.1
	 */
	default public OWLClass declareClass(final String name)
	{
		return declareClass(resolveToIRI(name));
	}

	/**
	 * Axiom are parsed from the stream then add into the ontology.
	 * 
	 * @param input is a stream of axioms
	 * @throws OWLOntologyCreationException if we can't load the ontology.
	 * @throws IOException if there is an problem when reading.
	 * @since 2.5.1
	 */
	default public void deserializeAxiomsInto(final String input) throws OWLOntologyCreationException, IOException
	{
		try (final InputStream stream = new ByteArrayInputStream(input.getBytes()))
		{
			addAxioms(getManager().loadOntologyFromOntologyDocument(stream).axioms());
		}
		catch (final OWLOntologyAlreadyExistsException e)
		{
			if (e.getOntologyID().equals(getOntology().getOntologyID()))
			{
				getLogger().severe("The ontology already exists with the name of the Tools : " + e.getOntologyID());
				throw e;
			}
			else
			{
				getManager().removeOntology(e.getOntologyID());
				deserializeAxiomsInto(input); // WARN : if the file define 2 ontologies and one is already define, then an infinite loop here can occur.
			}
		}
	}

	/**
	 * @param input is a stream of axioms
	 * @return the set of axiom contains in the input
	 * @throws OWLOntologyCreationException if we can't load the ontology.
	 * @throws IOException if there is an problem when reading.
	 * @since 2.5.1
	 */
	default public Stream<OWLAxiom> deserializeAxioms(final String input) throws OWLOntologyCreationException, IOException
	{
		try (final InputStream stream = new ByteArrayInputStream(input.getBytes()))
		{
			final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			final OWLOntology ontology = manager.loadOntologyFromOntologyDocument(stream);
			return ontology.axioms();
		}
	}

	/**
	 * @return the axioms as a single blob string.
	 * @throws OWLOntologyStorageException if we can't store the ontology.
	 * @throws IOException if there is an problem when reading. * @since 1.2
	 * @since 2.5.1
	 */
	default public String serializeAxioms() throws OWLOntologyStorageException, IOException
	{
		try (final ByteArrayOutputStream stream = new ByteArrayOutputStream())
		{
			getManager().saveOntology(getOntology(), stream);
			return stream.toString();
		}
		catch (final OWLOntologyStorageException | IOException e)
		{
			getLogger().log(Level.SEVERE, "Problem at serialisation of axioms : " + getOntology().getOntologyID(), e);
			throw e;
		}
	}

	/**
	 * Compute the types of an individual. Use this function only if you mix Named and Anonymous individuals.
	 * 
	 * @param param the individual named _or_ anonymous
	 * @return the classes of the individual.
	 * @since 2.5.1
	 */
	default public NodeSet<OWLClass> getTypes(final OWLIndividual param)
	{
		if (param instanceof OWLAnonymousIndividual)
		{
			// We create a temporary named Individual to allow the reasoner to work.
			final OWLNamedIndividual individual = getFactory().getOWLNamedIndividual(IRI.create(_protocol + OWLHelper.class.getPackage().getName() + _webSeparator + OWLHelper.class.getSimpleName() + _entitySeparator + IRIUtils.randId(OWLHelper.class.getSimpleName())));
			final Stream<OWLAxiom> axioms = Stream.of( //
					getFactory().getOWLDeclarationAxiom(individual), //
					getFactory().getOWLSameIndividualAxiom(individual, param) // The temporary named is the same as the anonymous one.
			);
			getManager().addAxioms(getOntology(), axioms);
			final NodeSet<OWLClass> result = getReasoner().getTypes(individual, false);
			getManager().removeAxioms(getOntology(), axioms);
			return result;
		}
		else
		{
			return getReasoner().getTypes((OWLNamedIndividual) param, false);
		}
	}

	/**
	 * @param buff is the target for the axioms rendered in DL syntax
	 * @param msg is insert before and after the axioms to detach it from its background (use "" if you don't know what to do with that).
	 * @return the given buffer
	 * @since 2.5.1
	 */
	default public StringBuffer ontologyToString(final StringBuffer buff, final String msg)
	{
		final DLSyntaxObjectRenderer syntax = new DLSyntaxObjectRenderer();
		buff.append("====\\/==" + msg + "===\\/====\n");
		getOntology().axioms().map(syntax::render).filter(x -> x != null && x.length() > 0).sorted().map(x -> x + "\n").forEach(buff::append);
		buff.append("====/\\==" + msg + "===/\\====\n");
		return buff;
	}

	/**
	 * @return the axioms rendered in DL syntax
	 * @param msg is insert before and after the axioms to detach it from its background (use "" if you don't know what to do with that).
	 * @since 2.5.1
	 */
	default public String ontologyToString(final String msg)
	{
		final StringBuffer buff = new StringBuffer();
		ontologyToString(buff, msg);
		return buff.toString();
	}

}
