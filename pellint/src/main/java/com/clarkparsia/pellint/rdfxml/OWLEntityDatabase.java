// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.rdfxml;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.mindswap.pellet.jena.vocabulary.SWRL;

import com.clarkparsia.pellint.util.CollectionUtil;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

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
public class OWLEntityDatabase {
	public final static Logger			log					= Logger
																.getLogger( OWLEntityDatabase.class.getName() );

	private DoubtfulSet<RDFNode>	m_Ontologies		= new DoubtfulSet<RDFNode>();
	private DoubtfulSet<RDFNode>	m_Classes			= new DoubtfulSet<RDFNode>();
	private DoubtfulSet<RDFNode>	m_Datatypes			= new DoubtfulSet<RDFNode>();
	private DoubtfulSet<RDFNode>	m_Individuals		= new DoubtfulSet<RDFNode>();

	private Set<RDFNode>			m_AllRoles			= CollectionUtil.makeSet();
	private DoubtfulSet<RDFNode>	m_AnnotationRoles	= new DoubtfulSet<RDFNode>();
	private DoubtfulSet<RDFNode>	m_OntologyRoles		= new DoubtfulSet<RDFNode>();
	private DoubtfulSet<RDFNode>	m_ObjectRoles		= new DoubtfulSet<RDFNode>();
	private DoubtfulSet<RDFNode>	m_DatatypeRoles		= new DoubtfulSet<RDFNode>();

	private DoubtfulSet<RDFNode>	m_SWRLVariables		= new DoubtfulSet<RDFNode>();

	private Set<RDFNode>			m_RDFClasses		= CollectionUtil.makeSet();
	// TODO: why is this Resource and everything else is RDFNode?  classes should be typed as Resource as well & Individuals, etc.
	private Set<Resource>			m_Restrictions		= CollectionUtil.makeSet();
	private Set<RDFNode>			m_Literals			= CollectionUtil.makeSet();
	private Set<Literal>			m_LiteralsAsClass  = CollectionUtil.makeSet();
	private Set<Literal>			m_LiteralsAsIndividuals = CollectionUtil.makeSet();
	private Set<Resource>			m_ResourcesAsLiterals = CollectionUtil.makeSet();

	public void addOntology(RDFNode s) {
		m_Ontologies.addDefinite( s );
	}

	public void assumeOntology(RDFNode s) {
		m_Ontologies.add( s );
	}

	public boolean containsOntology(RDFNode s) {
		return m_Ontologies.contains( s );
	}

	public Set<RDFNode> getDoubtfulOntologies() {
		return m_Ontologies.getDoubtfulElements();
	}

	public void addRDFSClass(RDFNode s) {
		m_RDFClasses.add( s );
	}

	public Set<RDFNode> getAllRDFClasses() {
		return m_RDFClasses;
	}

	public void addRestriction(Resource s) {
		m_Restrictions.add( s );
	}

	public Set<Resource> getAllRestrictions() {
		return m_Restrictions;
	}

	public void addLiteral(RDFNode s) {
		m_Literals.add( s );
	}

	public void addClass(RDFNode s) {
		m_Classes.addDefinite( s );
	}

	public void assumeClass(RDFNode s) {
		m_Classes.add( s );
		
		if (s instanceof Literal) {
			m_LiteralsAsClass.add( (Literal) s );
		}
	}

	public boolean containsClass(RDFNode s) {
		return m_Classes.contains( s ) || (s.isResource() && m_Restrictions.contains( s ));
	}

	public Set<RDFNode> getDoubtfulClasses() {
		Set<RDFNode> classes = m_Classes.getDoubtfulElements();
		classes.removeAll( m_Restrictions );

		return classes;
	}

	public void addDatatype(RDFNode s) {
		m_Datatypes.addDefinite( s );
	}

	public void assumeDatatype(RDFNode s) {
		m_Datatypes.add( s );
	}

	public boolean containsDatatype(RDFNode s) {
		return m_Datatypes.contains( s );
	}

	public Set<RDFNode> getDoubtfulDatatypes() {
		return m_Datatypes.getDoubtfulElements();
	}

	public void addIndividual(RDFNode s) {
		m_Individuals.addDefinite( s );
	}

	public void assumeIndividual(RDFNode s) {
		m_Individuals.add( s );
	}
	
	public boolean containsIndividual(RDFNode s) {
		return m_Individuals.contains( s );
	}
	
	public Set<RDFNode> getDoubtfulIndividuals() {
		return m_Individuals.getDoubtfulElements();
	}

	public void addUntypedRole(RDFNode s) {
		m_AllRoles.add( s );
	}

	public boolean containsRole(RDFNode s) {
		return m_AllRoles.contains( s );
	}

	public Set<RDFNode> getDoubtfulRoles() {
		Set<RDFNode> roles = CollectionUtil.copy( m_AllRoles );
		roles.removeAll( m_AnnotationRoles );
		roles.removeAll( m_OntologyRoles );
		roles.removeAll( m_ObjectRoles );
		roles.removeAll( m_DatatypeRoles );

		return roles;
	}

	public void addAnnotationRole(RDFNode s) {
		m_AllRoles.add( s );
		m_AnnotationRoles.addDefinite( s );
	}

	public void assumeAnnotationRole(RDFNode s) {
		m_AnnotationRoles.add( s );
	}

	public boolean containsAnnotaionRole(RDFNode s) {
		return m_AnnotationRoles.contains( s );
	}

	public Set<RDFNode> getDoubtfulAnnotaionRoles() {
		return m_AnnotationRoles.getDoubtfulElements();
	}

	public void addOntologyRole(RDFNode s) {
		m_AllRoles.add( s );
		m_OntologyRoles.addDefinite( s );
	}

	public boolean containsOntologyRole(RDFNode s) {
		return m_OntologyRoles.contains( s );
	}

	public void addObjectRole(RDFNode s) {
		m_AllRoles.add( s );
		m_ObjectRoles.addDefinite( s );
	}

	public void assumeObjectRole(RDFNode s) {
		m_ObjectRoles.add( s );
	}

	public boolean containsObjectRole(RDFNode s) {
		return m_ObjectRoles.contains( s );
	}

	public Set<RDFNode> getDoubtfulObjectRoles() {
		return m_ObjectRoles.getDoubtfulElements();
	}

	public void addInverseFunctionalRole(RDFNode s) {
		addObjectRole( s );
	}

	public void addTransitiveRole(RDFNode s) {
		addObjectRole( s );
	}

	public void addSymmetricRole(RDFNode s) {
		addObjectRole( s );
	}

	public void addAntiSymmetricRole(Resource s) {
		addObjectRole( s );
	}

	public void addReflexiveRole(Resource s) {
		addObjectRole( s );
	}

	public void addIrreflexiveRole(Resource s) {
		addObjectRole( s );
	}

	public void addDatatypeRole(RDFNode s) {
		m_AllRoles.add( s );
		m_DatatypeRoles.addDefinite( s );
	}

	public void assumeDatatypeRole(RDFNode s) {
		m_DatatypeRoles.add( s );
	}

	public boolean containsDatatypeRole(RDFNode s) {
		return m_DatatypeRoles.contains( s );
	}

	public Set<RDFNode> getDoubtfulDatatypeRoles() {
		return m_DatatypeRoles.getDoubtfulElements();
	}

	public void addSWRLVariable(RDFNode s) {
		m_SWRLVariables.addDefinite( s );
	}

	public void assumeSWRLVariable(RDFNode s) {
		m_SWRLVariables.add( s );
	}

	public boolean containsSWRLVariable(RDFNode s) {
		return m_SWRLVariables.contains( s );
	}

	public Set<RDFNode> getDoubtfulSWRLVariables() {
		return m_SWRLVariables.getDoubtfulElements();
	}
	
	public Set<Literal> getLiteralsAsClass() {
		return m_LiteralsAsClass;
	}
	
	public void addLiteralAsClass(Literal literal) {
		m_LiteralsAsClass.add( literal );
	}
	
	public Set<Literal> getLiteralsAsIndividuals() {
		return m_LiteralsAsIndividuals;
	}
	
	public void addLiteralAsIndividual(Literal literal) {
		m_LiteralsAsIndividuals.add( literal );
	}
	
	public Set<Resource> getResourcesAsLiterals() {
		return m_ResourcesAsLiterals;
	}
	
	public void addResourcesAsLiteral(Resource resource) {
		m_ResourcesAsLiterals.add( resource );
	}

	public boolean containsResource(RDFNode s) {
		return m_Ontologies.contains( s ) || m_Classes.contains( s ) || m_Datatypes.contains( s )
				|| m_Individuals.contains( s ) || m_AllRoles.contains( s )
				|| m_RDFClasses.contains( s ) || (s.isResource() && m_Restrictions.contains( s ))
				|| m_Literals.contains( s ) || m_SWRLVariables.contains( s );
	}

	public Map<RDFNode, List<String>> getAllMultiTypedResources() {
		return getMultiTypedResources(false);
	}

	/**
	 * Returns resources that have multiple types. OWL 2 allows resources to have certain multiple types (known as
	 * punning), e.g. a resource can be both a class and an individual. However, certain punnings are not allowed under
	 * any condition, e.g. a resource cannot be both a datatype property and an object property. Invalid punnings are
	 * always returned. Punnings valid for OWL 2 will be excluded based on the given parameter value.
	 * 
	 * 
	 * @param excludeValidPunning
	 *            If <code>true</code> OWL 2 valid punnings will not be inluded in the result
	 * @return resources that have multiple types
	 */
	public Map<RDFNode, List<String>> getMultiTypedResources(boolean excludeValidPunning) {
		Map<String, Set<RDFNode>> definiteResourcesByType = CollectionUtil.makeMap();
		if (!excludeValidPunning) {
			definiteResourcesByType.put( "Ontology", m_Ontologies.getDefiniteElements() );
			definiteResourcesByType.put( "Class", m_Classes.getDefiniteElements() );
			definiteResourcesByType.put( "Datatype", m_Datatypes.getDefiniteElements() );
			definiteResourcesByType.put( "Individual", m_Individuals );
			definiteResourcesByType.put( "Literal", m_Literals );
		}

		definiteResourcesByType.put( "Annotation Property", m_AnnotationRoles.getDefiniteElements() );
		definiteResourcesByType.put( "Ontology Property", m_OntologyRoles.getDefiniteElements() );
		definiteResourcesByType.put( "Datatype Property", m_DatatypeRoles.getDefiniteElements() );
		definiteResourcesByType.put( "Object Property", m_ObjectRoles.getDefiniteElements() );

		definiteResourcesByType.put( "SWRL Variable", m_SWRLVariables.getDefiniteElements() );

		Set<RDFNode> allDefiniteResources = CollectionUtil.makeSet();
		for( Set<RDFNode> definiteResources : definiteResourcesByType.values() ) {
			allDefiniteResources.addAll( definiteResources );
		}

		Map<RDFNode, List<String>> multiTypedResources = CollectionUtil.makeMap();
		for( RDFNode node : allDefiniteResources ) {
			List<String> types = CollectionUtil.makeList();
			for( Map.Entry<String, Set<RDFNode>> definiteResources : definiteResourcesByType
					.entrySet() ) {
				if( definiteResources.getValue().contains( node ) ) {
					types.add( definiteResources.getKey() );
				}
			}

			if( types.size() > 1 ) {
				multiTypedResources.put( node, types );
			}
		}
		
		if (excludeValidPunning) {
			List<String> classDatatypePunning = Arrays.asList("Class", "Datatype");
			for( RDFNode node : m_Datatypes.getDefiniteElements() ) {				
				if( m_Classes.getDefiniteElements().contains( node ) ) {					
					multiTypedResources.put( node, classDatatypePunning );
				}
			}
		}

		return multiTypedResources;
	}

	public List<Statement> getAllTypingStatements() {
		List<Statement> statements = CollectionUtil.makeList();
		addTypingStatements( statements, getDoubtfulOntologies(), OWL.Ontology );
		addTypingStatements( statements, getDoubtfulClasses(), OWL.Class );
		addTypingStatements( statements, getDoubtfulDatatypes(), RDFS.Datatype );
		addTypingStatements( statements, getDoubtfulRoles(), OWL.ObjectProperty );
		addTypingStatements( statements, getDoubtfulAnnotaionRoles(), OWL.AnnotationProperty );
		addTypingStatements( statements, getDoubtfulObjectRoles(), OWL.ObjectProperty );
		addTypingStatements( statements, getDoubtfulDatatypeRoles(), OWL.DatatypeProperty );
		addTypingStatements( statements, getDoubtfulSWRLVariables(), SWRL.Variable );

		return statements;
	}

	private static void addTypingStatements(List<Statement> statements, Set<RDFNode> subjects,
			Resource rdfType) {
		Model model = ModelFactory.createDefaultModel();
		for( RDFNode s : subjects ) {
			if (s instanceof Resource) {
				statements.add( model.createStatement( (Resource) s, RDF.type, rdfType ) );
			}
		}
	}
}