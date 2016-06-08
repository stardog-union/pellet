// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.rdfxml;

import com.clarkparsia.pellint.util.CollectionUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import net.katk.tools.Log;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.mindswap.pellet.jena.vocabulary.SWRL;

/**
 * <p>
 * Title:
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
 * @author Harris Lin
 */
public class OWLEntityDatabase
{
	public final static Logger _logger = Log.getLogger(OWLEntityDatabase.class);

	private final DoubtfulSet<RDFNode> _ontologies = new DoubtfulSet<>();
	private final DoubtfulSet<RDFNode> _classes = new DoubtfulSet<>();
	private final DoubtfulSet<RDFNode> _datatypes = new DoubtfulSet<>();
	private final DoubtfulSet<RDFNode> _individuals = new DoubtfulSet<>();

	private final Set<RDFNode> _allRoles = CollectionUtil.makeSet();
	private final DoubtfulSet<RDFNode> _annotationRoles = new DoubtfulSet<>();
	private final DoubtfulSet<RDFNode> _ontologyRoles = new DoubtfulSet<>();
	private final DoubtfulSet<RDFNode> _objectRoles = new DoubtfulSet<>();
	private final DoubtfulSet<RDFNode> _datatypeRoles = new DoubtfulSet<>();

	private final DoubtfulSet<RDFNode> _SWRLVariables = new DoubtfulSet<>();

	private final Set<RDFNode> _RDFClasses = CollectionUtil.makeSet();
	// TODO: why is this Resource and everything else is RDFNode?  classes should be typed as Resource as well & Individuals, etc.
	private final Set<Resource> _restrictions = CollectionUtil.makeSet();
	private final Set<RDFNode> _literals = CollectionUtil.makeSet();
	private final Set<Literal> _literalsAsClass = CollectionUtil.makeSet();
	private final Set<Literal> _literalsAsIndividuals = CollectionUtil.makeSet();
	private final Set<Resource> _resourcesAsLiterals = CollectionUtil.makeSet();

	public void addOntology(final RDFNode s)
	{
		_ontologies.addDefinite(s);
	}

	public void assumeOntology(final RDFNode s)
	{
		_ontologies.add(s);
	}

	public boolean containsOntology(final RDFNode s)
	{
		return _ontologies.contains(s);
	}

	public Set<RDFNode> getDoubtfulOntologies()
	{
		return _ontologies.getDoubtfulElements();
	}

	public void addRDFSClass(final RDFNode s)
	{
		_RDFClasses.add(s);
	}

	public Set<RDFNode> getAllRDFClasses()
	{
		return _RDFClasses;
	}

	public void addRestriction(final Resource s)
	{
		_restrictions.add(s);
	}

	public Set<Resource> getAllRestrictions()
	{
		return _restrictions;
	}

	public void addLiteral(final RDFNode s)
	{
		_literals.add(s);
	}

	public void addClass(final RDFNode s)
	{
		_classes.addDefinite(s);
	}

	public void assumeClass(final RDFNode s)
	{
		_classes.add(s);

		if (s instanceof Literal)
			_literalsAsClass.add((Literal) s);
	}

	public boolean containsClass(final RDFNode s)
	{
		return _classes.contains(s) || (s.isResource() && _restrictions.contains(s));
	}

	public Set<RDFNode> getDoubtfulClasses()
	{
		final Set<RDFNode> classes = _classes.getDoubtfulElements();
		classes.removeAll(_restrictions);

		return classes;
	}

	public void addDatatype(final RDFNode s)
	{
		_datatypes.addDefinite(s);
	}

	public void assumeDatatype(final RDFNode s)
	{
		_datatypes.add(s);
	}

	public boolean containsDatatype(final RDFNode s)
	{
		return _datatypes.contains(s);
	}

	public Set<RDFNode> getDoubtfulDatatypes()
	{
		return _datatypes.getDoubtfulElements();
	}

	public void addIndividual(final RDFNode s)
	{
		_individuals.addDefinite(s);
	}

	public void assumeIndividual(final RDFNode s)
	{
		_individuals.add(s);
	}

	public boolean containsIndividual(final RDFNode s)
	{
		return _individuals.contains(s);
	}

	public Set<RDFNode> getDoubtfulIndividuals()
	{
		return _individuals.getDoubtfulElements();
	}

	public void addUntypedRole(final RDFNode s)
	{
		_allRoles.add(s);
	}

	public boolean containsRole(final RDFNode s)
	{
		return _allRoles.contains(s);
	}

	public Set<RDFNode> getDoubtfulRoles()
	{
		final Set<RDFNode> roles = CollectionUtil.copy(_allRoles);
		roles.removeAll(_annotationRoles);
		roles.removeAll(_ontologyRoles);
		roles.removeAll(_objectRoles);
		roles.removeAll(_datatypeRoles);

		return roles;
	}

	public void addAnnotationRole(final RDFNode s)
	{
		_allRoles.add(s);
		_annotationRoles.addDefinite(s);
	}

	public void assumeAnnotationRole(final RDFNode s)
	{
		_annotationRoles.add(s);
	}

	public boolean containsAnnotaionRole(final RDFNode s)
	{
		return _annotationRoles.contains(s);
	}

	public Set<RDFNode> getDoubtfulAnnotaionRoles()
	{
		return _annotationRoles.getDoubtfulElements();
	}

	public void addOntologyRole(final RDFNode s)
	{
		_allRoles.add(s);
		_ontologyRoles.addDefinite(s);
	}

	public boolean containsOntologyRole(final RDFNode s)
	{
		return _ontologyRoles.contains(s);
	}

	public void addObjectRole(final RDFNode s)
	{
		_allRoles.add(s);
		_objectRoles.addDefinite(s);
	}

	public void assumeObjectRole(final RDFNode s)
	{
		_objectRoles.add(s);
	}

	public boolean containsObjectRole(final RDFNode s)
	{
		return _objectRoles.contains(s);
	}

	public Set<RDFNode> getDoubtfulObjectRoles()
	{
		return _objectRoles.getDoubtfulElements();
	}

	public void addInverseFunctionalRole(final RDFNode s)
	{
		addObjectRole(s);
	}

	public void addTransitiveRole(final RDFNode s)
	{
		addObjectRole(s);
	}

	public void addSymmetricRole(final RDFNode s)
	{
		addObjectRole(s);
	}

	public void addAntiSymmetricRole(final Resource s)
	{
		addObjectRole(s);
	}

	public void addReflexiveRole(final Resource s)
	{
		addObjectRole(s);
	}

	public void addIrreflexiveRole(final Resource s)
	{
		addObjectRole(s);
	}

	public void addDatatypeRole(final RDFNode s)
	{
		_allRoles.add(s);
		_datatypeRoles.addDefinite(s);
	}

	public void assumeDatatypeRole(final RDFNode s)
	{
		_datatypeRoles.add(s);
	}

	public boolean containsDatatypeRole(final RDFNode s)
	{
		return _datatypeRoles.contains(s);
	}

	public Set<RDFNode> getDoubtfulDatatypeRoles()
	{
		return _datatypeRoles.getDoubtfulElements();
	}

	public void addSWRLVariable(final RDFNode s)
	{
		_SWRLVariables.addDefinite(s);
	}

	public void assumeSWRLVariable(final RDFNode s)
	{
		_SWRLVariables.add(s);
	}

	public boolean containsSWRLVariable(final RDFNode s)
	{
		return _SWRLVariables.contains(s);
	}

	public Set<RDFNode> getDoubtfulSWRLVariables()
	{
		return _SWRLVariables.getDoubtfulElements();
	}

	public Set<Literal> getLiteralsAsClass()
	{
		return _literalsAsClass;
	}

	public void addLiteralAsClass(final Literal literal)
	{
		_literalsAsClass.add(literal);
	}

	public Set<Literal> getLiteralsAsIndividuals()
	{
		return _literalsAsIndividuals;
	}

	public void addLiteralAsIndividual(final Literal literal)
	{
		_literalsAsIndividuals.add(literal);
	}

	public Set<Resource> getResourcesAsLiterals()
	{
		return _resourcesAsLiterals;
	}

	public void addResourcesAsLiteral(final Resource resource)
	{
		_resourcesAsLiterals.add(resource);
	}

	public boolean containsResource(final RDFNode s)
	{
		return _ontologies.contains(s) || _classes.contains(s) || _datatypes.contains(s) || _individuals.contains(s) || _allRoles.contains(s) || _RDFClasses.contains(s) || (s.isResource() && _restrictions.contains(s)) || _literals.contains(s) || _SWRLVariables.contains(s);
	}

	public Map<RDFNode, List<String>> getAllMultiTypedResources()
	{
		return getMultiTypedResources(false);
	}

	/**
	 * Returns resources that have multiple types. OWL 2 allows resources to have certain multiple types (known as punning), e.g. a resource can be both a class
	 * and an _individual. However, certain punnings are not allowed under any _condition, e.g. a resource cannot be both a datatype property and an object
	 * property. Invalid punnings are always returned. Punnings valid for OWL 2 will be excluded based on the given parameter value.
	 *
	 * @param excludeValidPunning If <code>true</code> OWL 2 valid punnings will not be inluded in the result
	 * @return resources that have multiple types
	 */
	public Map<RDFNode, List<String>> getMultiTypedResources(final boolean excludeValidPunning)
	{
		final Map<String, Set<RDFNode>> definiteResourcesByType = CollectionUtil.makeMap();
		if (!excludeValidPunning)
		{
			definiteResourcesByType.put("Ontology", _ontologies.getDefiniteElements());
			definiteResourcesByType.put("Class", _classes.getDefiniteElements());
			definiteResourcesByType.put("Datatype", _datatypes.getDefiniteElements());
			definiteResourcesByType.put("Individual", _individuals);
			definiteResourcesByType.put("Literal", _literals);
		}

		definiteResourcesByType.put("Annotation Property", _annotationRoles.getDefiniteElements());
		definiteResourcesByType.put("Ontology Property", _ontologyRoles.getDefiniteElements());
		definiteResourcesByType.put("Datatype Property", _datatypeRoles.getDefiniteElements());
		definiteResourcesByType.put("Object Property", _objectRoles.getDefiniteElements());

		definiteResourcesByType.put("SWRL Variable", _SWRLVariables.getDefiniteElements());

		final Set<RDFNode> allDefiniteResources = CollectionUtil.makeSet();
		for (final Set<RDFNode> definiteResources : definiteResourcesByType.values())
			allDefiniteResources.addAll(definiteResources);

		final Map<RDFNode, List<String>> multiTypedResources = CollectionUtil.makeMap();
		for (final RDFNode node : allDefiniteResources)
		{
			final List<String> types = CollectionUtil.makeList();
			for (final Map.Entry<String, Set<RDFNode>> definiteResources : definiteResourcesByType.entrySet())
				if (definiteResources.getValue().contains(node))
					types.add(definiteResources.getKey());

			if (types.size() > 1)
				multiTypedResources.put(node, types);
		}

		if (excludeValidPunning)
		{
			final List<String> classDatatypePunning = Arrays.asList("Class", "Datatype");
			for (final RDFNode node : _datatypes.getDefiniteElements())
				if (_classes.getDefiniteElements().contains(node))
					multiTypedResources.put(node, classDatatypePunning);
		}

		return multiTypedResources;
	}

	public List<Statement> getAllTypingStatements()
	{
		final List<Statement> statements = CollectionUtil.makeList();
		addTypingStatements(statements, getDoubtfulOntologies(), OWL.Ontology);
		addTypingStatements(statements, getDoubtfulClasses(), OWL.Class);
		addTypingStatements(statements, getDoubtfulDatatypes(), RDFS.Datatype);
		addTypingStatements(statements, getDoubtfulRoles(), OWL.ObjectProperty);
		addTypingStatements(statements, getDoubtfulAnnotaionRoles(), OWL.AnnotationProperty);
		addTypingStatements(statements, getDoubtfulObjectRoles(), OWL.ObjectProperty);
		addTypingStatements(statements, getDoubtfulDatatypeRoles(), OWL.DatatypeProperty);
		addTypingStatements(statements, getDoubtfulSWRLVariables(), SWRL.Variable);

		return statements;
	}

	private static void addTypingStatements(final List<Statement> statements, final Set<RDFNode> subjects, final Resource rdfType)
	{
		final Model model = ModelFactory.createDefaultModel();
		for (final RDFNode s : subjects)
			if (s instanceof Resource)
				statements.add(model.createStatement((Resource) s, RDF.type, rdfType));
	}
}
