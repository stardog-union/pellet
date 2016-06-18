// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity.io;

import com.clarkparsia.owlapi.OWL;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import openllet.shared.tools.Log;
import org.mindswap.pellet.utils.MultiValueMap;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * Provides methods that serialize and deserialize AbstractModuleExtractor
 *
 * @author Blazej Bulka
 */
public class ModuleExtractorPersistence
{

	public static final Logger _logger = Log.getLogger(ModuleExtractorPersistence.class);

	/**
	 * Saves the axioms from the ModuleExtractor in the form of an ontology containing these axioms.
	 *
	 * @param axioms the axioms to be saved
	 * @param outputStream the output stream where the axioms should be saved
	 * @throws IOException if an error occurs during the save process
	 */
	public static void saveAxioms(final Collection<OWLAxiom> axioms, final OutputStream outputStream) throws IOException
	{
		try
		{
			final OWLOntology ontology = OWL.Ontology(axioms);

			OWL._manager.saveOntology(ontology, new OWLXMLDocumentFormat(), outputStream);

			outputStream.flush();

			OWL._manager.removeOntology(ontology);
		}
		catch (final OWLException e)
		{
			_logger.log(Level.SEVERE, "A problem occurred during creation or saving the ontology with axioms for the ModuleExtractor", e);
			throw new IOException("A problem occurred during creation or saving the ontology with axioms for the ModuleExtractor");
		}
	}

	private static final String MODULE_BEGIN_KEYWORD = "MODULE";

	private static final String MODULE_END_KEYWORD = "END";

	private static final String CLASS_KEYWORD = "class";

	private static final String DATA_TYPE_KEYWORD = "datatype";

	private static final String INDIVIDUAL_KEYWORD = "individual";

	private static final String DATA_PROPERTY_KEYWORD = "dataproperty";

	private static final String OBJECT_PROPERTY_KEYWORD = "objectproperty";

	private static String getTypeKeywordForEntity(final OWLEntity owlEntity) throws IllegalArgumentException
	{
		if (owlEntity.isOWLClass())
			return CLASS_KEYWORD;
		else
			if (owlEntity.isOWLDatatype())
				return DATA_TYPE_KEYWORD;
			else
				if (owlEntity.isOWLNamedIndividual())
					return INDIVIDUAL_KEYWORD;
				else
					if (owlEntity.isOWLDataProperty())
						return DATA_PROPERTY_KEYWORD;
					else
						if (owlEntity.isOWLObjectProperty())
							return OBJECT_PROPERTY_KEYWORD;

		throw new IllegalArgumentException("Unrecognized type of OWLEntity: " + owlEntity.getClass());
	}

	private static String getModuleBegin(final OWLEntity module)
	{
		final StringBuffer result = new StringBuffer(MODULE_BEGIN_KEYWORD);

		result.append(" ");
		result.append(getTypeKeywordForEntity(module));
		result.append(" ");
		result.append(module.getIRI());

		return result.toString();
	}

	private static String getModuleMember(final OWLEntity member)
	{
		return getTypeKeywordForEntity(member) + " " + member.getIRI();
	}

	private static String getModuleEnd()
	{
		return MODULE_END_KEYWORD;
	}

	/**
	 * Saves the information about modules from ModuleExtractor to an output stream as an ontology of modules annotated with URIs of the OWL entities that
	 * belong to the respective modules.
	 *
	 * @param modules the modules to be saved
	 * @param outputStream the output stream where the _data should be saved
	 * @throws IOException if an error should occur during the save process
	 */
	public static void saveModules(final MultiValueMap<OWLEntity, OWLEntity> modules, final OutputStream outputStream)
	{
		final PrintWriter pw = new PrintWriter(outputStream);

		for (final Entry<OWLEntity, Set<OWLEntity>> entry : modules.entrySet())
		{
			final OWLEntity entity = entry.getKey();
			final Set<OWLEntity> module = entry.getValue();

			pw.println(getModuleBegin(entity));

			for (final OWLEntity member : module)
				pw.println(getModuleMember(member));

			pw.println(getModuleEnd());
		}

		pw.flush();
	}

	public static OWLOntology loadAxiomOntology(final InputStream inputStream) throws IOException
	{
		try
		{
			return OWL._manager.loadOntologyFromOntologyDocument(inputStream);
		}
		catch (final OWLOntologyCreationException e)
		{
			_logger.log(Level.SEVERE, "Unable to create an ontology", e);
			throw new IOException("Unable to create an ontology");
		}
	}

	/**
	 * Loads the ontology with axioms (for ModuleExtractor) from an input stream.
	 * 
	 * @param inputStream the input stream from which to load the ontology.
	 * @return the collection of axioms read from the ontology
	 * @throws IOException if an error should occur during the read operation
	 */
	@Deprecated
	public static Collection<OWLAxiom> loadAxioms(final InputStream inputStream) throws IOException
	{
		return loadAxiomOntology(inputStream).getAxioms();
	}

	public static Stream<OWLAxiom> load_axioms(final InputStream inputStream) throws IOException
	{
		return loadAxiomOntology(inputStream).axioms();
	}

	private static OWLEntity createEntity(final String type, final String entityIRI)
	{
		if (CLASS_KEYWORD.equals(type))
			return OWL.Class(entityIRI);
		else
			if (DATA_TYPE_KEYWORD.equals(type))
				return OWL.Datatype(entityIRI);
			else
				if (INDIVIDUAL_KEYWORD.equals(type))
					return OWL.Individual(entityIRI);
				else
					if (DATA_PROPERTY_KEYWORD.equals(type))
						return OWL.DataProperty(entityIRI);
					else
						if (OBJECT_PROPERTY_KEYWORD.equals(type))
							return OWL.ObjectProperty(entityIRI);

		throw new IllegalArgumentException("Unrecognized type of OWLEntity in module " + type);
	}

	private static OWLEntity readModuleInformation(final String moduleBeginLine) throws IOException
	{
		final StringTokenizer tokenizer = new StringTokenizer(moduleBeginLine);

		if (!tokenizer.hasMoreElements())
			throw new IOException("Premature _end of line; module " + MODULE_BEGIN_KEYWORD + " _expected: " + moduleBeginLine);

		tokenizer.nextToken(); // discard the MODULE_BEGIN_KEYWORD

		if (!tokenizer.hasMoreElements())
			throw new IOException("Premature _end of line; entity type information _expected: " + moduleBeginLine);

		final String entityType = tokenizer.nextToken();

		if (!tokenizer.hasMoreElements())
			throw new IOException("Premature _end of line; module URI _expected: " + moduleBeginLine);

		final String iriString = tokenizer.nextToken();

		if (tokenizer.hasMoreElements())
			throw new IOException("Trailing tokens on the line: " + moduleBeginLine);

		return createEntity(entityType, iriString);
	}

	private static OWLEntity readModuleMember(final String memberLine) throws IOException
	{
		final StringTokenizer tokenizer = new StringTokenizer(memberLine);

		if (!tokenizer.hasMoreElements())
			throw new IOException("Premature _end of line; entity type information _expected: " + memberLine);

		final String entityType = tokenizer.nextToken();

		if (!tokenizer.hasMoreElements())
			throw new IOException("Premature _end of line; module member URI _expected: " + memberLine);

		final String iriString = tokenizer.nextToken();

		if (tokenizer.hasMoreElements())
			throw new IOException("Trailing tokens on the line: " + memberLine);

		return createEntity(entityType, iriString);
	}

	private static boolean readModule(final LineNumberReader lnr, final MultiValueMap<OWLEntity, OWLEntity> modules) throws IOException
	{
		String line = null;

		line = lnr.readLine();

		if (line == null)
			return false;

		if (!line.startsWith(MODULE_BEGIN_KEYWORD))
			throw new IOException("Invalid information in the module file (line " + lnr.getLineNumber() + "). " + MODULE_BEGIN_KEYWORD + " _expected.");

		final OWLEntity module = readModuleInformation(line.trim());

		boolean endOfModuleReached = false;
		final Set<OWLEntity> members = new HashSet<>();

		while ((line = lnr.readLine()) != null)
		{
			if (line.startsWith(MODULE_END_KEYWORD))
			{
				endOfModuleReached = true;
				break;
			}

			members.add(readModuleMember(line.trim()));
		}

		if (!endOfModuleReached)
			throw new IOException("Premature _end of file; module information not terminated.");

		modules.put(module, members);

		return true;
	}

	/**
	 * Reads information about the modules from the input stream that has the information stored in a form of ontology.
	 * 
	 * @param inputStream the input stream from which the ontology should be read
	 * @return the read information about the modules
	 * @throws IOException if an error occurs during the read process.
	 */
	public static MultiValueMap<OWLEntity, OWLEntity> loadModules(final InputStream is) throws IOException
	{
		final MultiValueMap<OWLEntity, OWLEntity> modules = new MultiValueMap<>();
		final LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is));

		while (readModule(lnr, modules))
		{
			// nothing to do
		}

		return modules;
	}
}
