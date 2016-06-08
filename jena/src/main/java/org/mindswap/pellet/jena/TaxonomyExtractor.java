// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena;

import aterm.ATermAppl;
import java.util.Collection;
import java.util.HashSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.taxonomy.TaxonomyNode;
import org.mindswap.pellet.utils.TaxonomyUtils;

/**
 * Extracts a Jena Model from a Taxonomy (i.e., creates a Model that contains only the classes in the _taxonomy and the subclass relationships among them).
 *
 * @author Blazej Bulka
 */
public class TaxonomyExtractor
{
	private final Taxonomy<ATermAppl> _taxonomy;
	private Model _model;
	private boolean _includeIndividuals;

	public TaxonomyExtractor(final Taxonomy<ATermAppl> taxonomy)
	{
		this._taxonomy = taxonomy;
		this._includeIndividuals = false;
	}

	public void setIncludeIndividuals(final boolean includeIndividuals)
	{
		this._includeIndividuals = includeIndividuals;
	}

	public Model extractModel()
	{
		if (_model == null)
			_model = createExtractedModel();

		return _model;
	}

	private Model createExtractedModel()
	{
		final Model model = ModelFactory.createDefaultModel();

		final HashSet<ATermAppl> processedEquivalentClasses = new HashSet<>();

		for (final TaxonomyNode<ATermAppl> taxonomyNode : _taxonomy.getNodes())
		{
			if (processedEquivalentClasses.contains(taxonomyNode.getName()))
				continue;

			processedEquivalentClasses.addAll(taxonomyNode.getEquivalents());

			for (final ATermAppl aClass : taxonomyNode.getEquivalents())
			{
				model.add(classAssertion(model, aClass));

				for (final TaxonomyNode<ATermAppl> superNode : taxonomyNode.getSupers())
					model.add(subClassOfAssertion(model, aClass, superNode.getName()));

				if (taxonomyNode.getEquivalents().size() > 1)
					for (final ATermAppl equivalentClass : taxonomyNode.getEquivalents())
						if (!equivalentClass.equals(aClass))
							model.add(equivalentClassAssertion(model, aClass, equivalentClass));

				if (_includeIndividuals)
				{
					final Collection<ATermAppl> individuals = getDatumInstanceAsCollectorOfATermAppl(taxonomyNode);

					if ((individuals != null) && !individuals.isEmpty())
						for (final ATermAppl individual : individuals)
							model.add(typeAssertion(model, individual, aClass));
				}
			}
		}

		return model;
	}

	// FIXME me : add strong typing on taxonmy.
	@SuppressWarnings("unchecked")
	protected static Collection<ATermAppl> getDatumInstanceAsCollectorOfATermAppl(final TaxonomyNode<ATermAppl> taxonomyNode)
	{
		return (Collection<ATermAppl>) taxonomyNode.getDatum(TaxonomyUtils.INSTANCES_KEY);
	}

	private static Statement typeAssertion(final Model model, final ATermAppl individual, final ATermAppl type)
	{
		final Resource individualResource = createResource(model, individual);
		final Property typeProperty = RDF.type;
		final Resource typeResource = createResource(model, type);

		return model.createStatement(individualResource, typeProperty, typeResource);
	}

	private static Statement classAssertion(final Model model, final ATermAppl aClass)
	{
		final Resource classResource = createResource(model, aClass);
		final Property typeProperty = RDF.type;
		final Resource owlClassResource = OWL.Class;

		return model.createStatement(classResource, typeProperty, owlClassResource);
	}

	private static Statement subClassOfAssertion(final Model model, final ATermAppl subClass, final ATermAppl superClass)
	{
		final Resource subClassResource = createResource(model, subClass);
		final Property subClassOfProperty = RDFS.subClassOf;
		final Resource superClassResource = createResource(model, superClass);

		return model.createStatement(subClassResource, subClassOfProperty, superClassResource);
	}

	private static Statement equivalentClassAssertion(final Model model, final ATermAppl firstClass, final ATermAppl secondClass)
	{
		final Resource firstClassResource = createResource(model, firstClass);
		final Property equivalentClassProperty = OWL.equivalentClass;
		final Resource secondClassResource = createResource(model, secondClass);

		return model.createStatement(firstClassResource, equivalentClassProperty, secondClassResource);
	}

	private static Resource createResource(final Model model, final ATermAppl term)
	{
		return JenaUtils.makeResource(term, model);
	}
}
