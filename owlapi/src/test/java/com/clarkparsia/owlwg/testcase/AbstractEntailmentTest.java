package com.clarkparsia.owlwg.testcase;

import static java.lang.String.format;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import openllet.shared.tools.Log;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

/**
 * <p>
 * Title: Abstract Entailment Test Case
 * </p>
 * <p>
 * Description: Common base implementation shared by positive and negative entailment tests
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
public abstract class AbstractEntailmentTest<O> extends AbstractPremisedTest<O> implements EntailmentTest<O>
{
	private static final Logger _logger = Log.getLogger(AbstractEntailmentTest.class);

	private final EnumSet<SerializationFormat> conclusionFormats;
	private final EnumMap<SerializationFormat, String> conclusionOntologyLiteral;

	public AbstractEntailmentTest(final OWLOntology ontology, final OWLNamedIndividual i, final boolean positive)
	{
		super(ontology, i);

		conclusionFormats = EnumSet.noneOf(SerializationFormat.class);
		conclusionOntologyLiteral = new EnumMap<>(SerializationFormat.class);

		final Map<OWLDataPropertyExpression, Collection<OWLLiteral>> values = EntitySearcher.getDataPropertyValues(i, ontology).asMap();

		for (final SerializationFormat f : SerializationFormat.values())
		{
			final Collection<OWLLiteral> conclusions = values.get(positive ? f.getConclusionOWLDataProperty() : f.getNonConclusionOWLDataProperty());
			if (conclusions != null)
			{
				if (conclusions.size() > 1)
				{
					_logger.warning(format("Multiple conclusion ontologies found for testcase (%s) with serialization format (%s).  Choosing arbitrarily.", getIdentifier(), f));
				}
				conclusionOntologyLiteral.put(f, conclusions.iterator().next().getLiteral());
				conclusionFormats.add(f);
			}
		}
	}

	@Override
	public Set<SerializationFormat> getConclusionFormats()
	{
		return Collections.unmodifiableSet(conclusionFormats);
	}

	@Override
	public String getConclusionOntology(SerializationFormat format)
	{
		return conclusionOntologyLiteral.get(format);
	}
}
