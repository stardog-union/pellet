package com.clarkparsia.owlwg.testcase;

import static com.clarkparsia.owlwg.testcase.TestVocabulary.ObjectProperty.IMPORTED_ONTOLOGY_IRI;
import static java.lang.String.format;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import net.katk.tools.Log;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

/**
 * <p>
 * Title: Imported Ontology Implementation
 * </p>
 * <p>
 * Description: Default implementation of {@link ImportedOntology}, which queries an {@link OWLOntology} for details of the imported ontology, such as
 * serializations and normative formats.
 * </p>
 * <p>
 * Copyright: Copyright &copy; 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <a href="http://clarkparsia.com/"/>http://clarkparsia.com/</a>
 * </p>
 * 
 * @author Mike Smith &lt;msmith@clarkparsia.com&gt;
 */
public class ImportedOntologyImpl implements ImportedOntology
{
	private static final Logger log = Log.getLogger(ImportedOntologyImpl.class);

	private final EnumSet<SerializationFormat> formats;
	private final EnumMap<SerializationFormat, String> ontologyLiteral;
	private final IRI iri;

	public ImportedOntologyImpl(OWLOntology ontology, OWLNamedIndividual i)
	{
		final Map<OWLObjectPropertyExpression, Collection<OWLIndividual>> opValues = EntitySearcher.getObjectPropertyValues(i, ontology).asMap();

		final Collection<OWLIndividual> iris = opValues.get(IMPORTED_ONTOLOGY_IRI.getOWLObjectProperty());
		if (iris == null)
		{
			final String msg = format("Value for property %s missing for imported ontology %s", IMPORTED_ONTOLOGY_IRI.getOWLObjectProperty().getIRI(), i.getIRI());
			log.warning(msg);
			throw new NullPointerException(msg);
		}
		else
			if (iris.size() != 1)
			{
				final String msg = format("Property %s should have a single value for imported ontology %s, but has %d", IMPORTED_ONTOLOGY_IRI.getOWLObjectProperty().getIRI(), i.getIRI(), iris.size());
				log.warning(msg);
				throw new IllegalArgumentException();
			}
			else
			{
				iri = iris.iterator().next().asOWLNamedIndividual().getIRI();
			}

		final Map<OWLDataPropertyExpression, Collection<OWLLiteral>> values = EntitySearcher.getDataPropertyValues(i, ontology).asMap();

		formats = EnumSet.noneOf(SerializationFormat.class);
		ontologyLiteral = new EnumMap<>(SerializationFormat.class);
		for (final SerializationFormat f : SerializationFormat.values())
		{
			final Collection<OWLLiteral> literals = values.get(f.getInputOWLDataProperty());
			if (literals != null)
			{
				if (literals.size() > 1)
				{
					log.warning(format("Multiple ontologies found for imported ontology (%s) with serialization format (%s).  Choosing arbitrarily.", i.getIRI(), f));
				}
				ontologyLiteral.put(f, literals.iterator().next().getLiteral());
				formats.add(f);
			}
		}

	}

	@Override
	public Set<SerializationFormat> getFormats()
	{
		return Collections.unmodifiableSet(formats);
	}

	@Override
	public String getOntology(SerializationFormat format)
	{
		return ontologyLiteral.get(format);
	}

	@Override
	public IRI getIRI()
	{
		return iri;
	}

}
