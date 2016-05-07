// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.rdfxml;

import aterm.ATermAppl;
import com.clarkparsia.pellet.datatypes.DatatypeReasonerImpl;
import com.clarkparsia.pellet.datatypes.Facet;
import com.clarkparsia.pellint.util.CollectionUtil;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.mindswap.pellet.jena.vocabulary.OWL2;
import org.mindswap.pellet.jena.vocabulary.SWRL;
import org.mindswap.pellet.utils.Namespaces;
import org.semanticweb.owlapi.vocab.SWRLBuiltInsVocabulary;

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
 * @author Evren Sirin, Harris Lin
 */
public class OWLSyntaxChecker
{
	/*
	 * predicates related to restrictions (owl:onProperty, owl:allValuesFrom,
	 * etc.) are preprocessed before all the triples are processed. these
	 * predicates are stored in the following list so processTriples function
	 * can ignore the triples with these predicates
	 */
	final static Collection<Property> RESTRICTION_PROPS;
	final static Collection<Resource> DATA_RANGE_FACETS;
	final static Collection<Resource> SWRL_BUILT_INS;

	static
	{
		RESTRICTION_PROPS = Arrays.asList(OWL.onProperty, OWL.hasValue, OWL.allValuesFrom, OWL.someValuesFrom, OWL.minCardinality, OWL2.minQualifiedCardinality, OWL.maxCardinality, OWL2.maxQualifiedCardinality, OWL.cardinality, OWL2.qualifiedCardinality, OWL2.hasSelf);

		DATA_RANGE_FACETS = CollectionUtil.makeSet();
		for (final Facet v : Facet.XSD.values())
			DATA_RANGE_FACETS.add(ResourceFactory.createResource(v.getName().getName()));

		SWRL_BUILT_INS = CollectionUtil.makeSet();
		for (final SWRLBuiltInsVocabulary v : SWRLBuiltInsVocabulary.values())
			SWRL_BUILT_INS.add(ResourceFactory.createResource(v.getIRI().toString()));
	}

	private Map<Resource, List<RDFNode>> m_Lists;
	private OWLEntityDatabase m_OWLEntities;
	private RDFModel m_Model = null;

	private boolean excludeValidPunnings = false;

	/**
	 * Sets if valid punninga will be excluded from lint report. OWL 2 allows resources to have certain multiple types (known as punning), e.g. a resource can
	 * be both a class and an individual. However, certain punnings are not allowed under any condition, e.g. a resource cannot be both a datatype property and
	 * an object property. Invalid punnings are always returned. If this option is set to <code>true</code>, punnings valid for OWL 2 will be excluded from the
	 * report. By default, these punnings are reported.
	 *
	 * @param excludeValidPunning If <code>true</code> OWL 2 valid punnings will not be inluded in the result
	 */
	public void setExcludeValidPunnings(final boolean excludeValidPunning)
	{
		excludeValidPunnings = excludeValidPunning;
	}

	/**
	 * Returns if valid punninga will be excluded from lint report.
	 */
	public boolean isExcludeValidPunnings()
	{
		return excludeValidPunnings;
	}

	public RDFLints validate(final RDFModel model)
	{
		m_Model = model;

		m_OWLEntities = new OWLEntityDatabase();
		m_OWLEntities.addAnnotationRole(RDFS.label);
		m_OWLEntities.addAnnotationRole(RDFS.comment);
		m_OWLEntities.addAnnotationRole(RDFS.seeAlso);
		m_OWLEntities.addAnnotationRole(RDFS.isDefinedBy);
		m_OWLEntities.addAnnotationRole(OWL.versionInfo);
		m_OWLEntities.addOntologyRole(OWL.backwardCompatibleWith);
		m_OWLEntities.addOntologyRole(OWL.priorVersion);
		m_OWLEntities.addOntologyRole(OWL.incompatibleWith);
		m_OWLEntities.addClass(OWL.Thing);
		m_OWLEntities.addClass(OWL.Nothing);

		// Fixes #194
		m_OWLEntities.addDatatype(RDFS.Literal);

		// Fixes #457
		m_OWLEntities.addDatatype(ResourceFactory.createResource("http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral"));

		for (final ATermAppl uri : new DatatypeReasonerImpl().listDataRanges())
			m_OWLEntities.addDatatype(ResourceFactory.createResource(uri.getName()));

		m_Lists = CollectionUtil.makeMap();
		m_Lists.put(RDF.nil, CollectionUtil.<RDFNode> makeList());

		processTypes();
		processTriples();
		processRestrictions();

		return reportLints();
	}

	private RDFLints reportLints()
	{
		final RDFLints lints = new RDFLints();

		lints.add("Untyped ontologies", toString(m_OWLEntities.getDoubtfulOntologies()));
		lints.add("Untyped classes", toString(m_OWLEntities.getDoubtfulClasses()));
		lints.add("Untyped datatypes", toString(m_OWLEntities.getDoubtfulDatatypes()));
		lints.add("Untyped object properties", toString(m_OWLEntities.getDoubtfulObjectRoles()));
		lints.add("Untyped datatype properties", toString(m_OWLEntities.getDoubtfulDatatypeRoles()));
		lints.add("Untyped annotation properties", toString(m_OWLEntities.getDoubtfulAnnotaionRoles()));
		lints.add("Untyped properties", toString(m_OWLEntities.getDoubtfulRoles()));
		lints.add("Untyped individuals", toString(m_OWLEntities.getDoubtfulIndividuals()));

		lints.add("Using rdfs:Class instead of owl:Class", toString(m_OWLEntities.getAllRDFClasses()));
		lints.add("Multiple typed resources", toString(m_OWLEntities.getMultiTypedResources(excludeValidPunnings)));

		lints.add("Literals used where a class is expected", toStringLiterals(m_OWLEntities.getLiteralsAsClass()));
		lints.add("Literals used where an individual is expected", toStringLiterals(m_OWLEntities.getLiteralsAsIndividuals()));
		lints.add("Resource used where a literal is expected", toString(m_OWLEntities.getResourcesAsLiterals()));

		lints.addMissingStatements(m_OWLEntities.getAllTypingStatements());

		return lints;
	}

	private static List<String> toStringLiterals(final Set<Literal> values)
	{
		final List<String> strings = CollectionUtil.makeList();

		for (final Literal value : values)
			strings.add("\"" + value.toString() + "\"");

		return strings;
	}

	private static List<String> toString(final Set<? extends RDFNode> values)
	{
		final List<String> strings = CollectionUtil.makeList();
		int bNodeCount = 0;
		for (final RDFNode value : values)
			if (value.isAnon())
				bNodeCount++;
			else
				strings.add(value.toString());

		if (bNodeCount > 0)
			strings.add(bNodeCount + " BNode(s)");

		return strings;
	}

	private static <K, V> List<String> toString(final Map<K, List<V>> map)
	{
		final List<String> strings = CollectionUtil.makeList();
		for (final Map.Entry<K, List<V>> entry : map.entrySet())
		{
			final StringBuilder builder = new StringBuilder();
			builder.append(entry.getKey()).append(": ").append(entry.getValue());
			strings.add(builder.toString());
		}
		return strings;
	}

	private void createList(final Resource head, final List<RDFNode> outList)
	{
		if (head.equals(RDF.nil))
			return;

		final RDFNode first = m_Model.getUniqueObject(head, RDF.first);
		final RDFNode rest = m_Model.getUniqueObject(head, RDF.rest);
		if (first == null)
			// report.addMessage(WARNING, "Invalid List", "The list " + r + "
			// does not have a rdf:first property");
			return;
		if (rest == null)
			// report.addMessage(WARNING, "Invalid List", "The list " + r + "
			// does not have a rdf:rest property");
			return;

		outList.add(first);

		if (!rest.isResource())
			return;

		createList((Resource) rest, outList);
	}

	private void createList(final Resource head)
	{
		if (m_Lists.containsKey(head))
			return;

		final List<RDFNode> list = CollectionUtil.makeList();
		m_Lists.put(head, list);
		createList(head, list);
	}

	private void processTypes()
	{
		// list pre-processing
		for (final Statement stmt : m_Model.getStatementsByPredicate(RDF.first))
		{
			final Resource s = stmt.getSubject();
			for (final Statement aStmt : m_Model.getStatementsByObject(s))
			{
				final Property predicate = aStmt.getPredicate();
				if (!predicate.equals(RDF.first) && !predicate.equals(RDF.rest))
				{
					createList(s);
					break;
				}
			}
		}

		final List<Statement> processLater = CollectionUtil.makeList();
		for (final Statement stmt : m_Model.getStatementsByPredicate(RDF.type))
		{
			final Resource s = stmt.getSubject();
			final RDFNode o = stmt.getObject();

			if (o.equals(OWL.Class) || o.equals(OWL.DeprecatedClass))
				m_OWLEntities.addClass(s);
			else
				if (o.equals(RDFS.Class))
					processLater.add(stmt);
				else
					if (o.equals(RDFS.Datatype))
						m_OWLEntities.addDatatype(s);
					else
						if (o.equals(OWL.Thing))
							m_OWLEntities.addIndividual(s);
						else
							if (o.equals(OWL.Restriction))
								m_OWLEntities.addRestriction(s);
							else
								if (o.equals(OWL2.SelfRestriction))
									m_OWLEntities.addRestriction(s);
								else
									if (o.equals(OWL.AllDifferent))
									{
										// Ignore
									}
									else
										if (o.equals(OWL.ObjectProperty))
											m_OWLEntities.addObjectRole(s);
										else
											if (o.equals(OWL.DatatypeProperty))
												m_OWLEntities.addDatatypeRole(s);
											else
												if (o.equals(OWL.AnnotationProperty))
													m_OWLEntities.addAnnotationRole(s);
												else
													if (o.equals(OWL.DeprecatedProperty))
														m_OWLEntities.addUntypedRole(s);
													else
														if (o.equals(RDF.Property))
															processLater.add(stmt);
														else
															if (o.equals(OWL.TransitiveProperty))
																m_OWLEntities.addTransitiveRole(s);
															else
																if (o.equals(OWL.SymmetricProperty))
																	m_OWLEntities.addSymmetricRole(s);
																else
																	if (o.equals(OWL2.AsymmetricProperty))
																		m_OWLEntities.addAntiSymmetricRole(s);
																	else
																		if (o.equals(OWL2.ReflexiveProperty))
																			m_OWLEntities.addReflexiveRole(s);
																		else
																			if (o.equals(OWL2.IrreflexiveProperty))
																				m_OWLEntities.addIrreflexiveRole(s);
																			else
																				if (o.equals(OWL.FunctionalProperty))
																					processLater.add(stmt);
																				else
																					if (o.equals(OWL.InverseFunctionalProperty))
																						m_OWLEntities.addInverseFunctionalRole(s);
																					else
																						if (o.equals(OWL.Ontology))
																							m_OWLEntities.addOntology(s);
																						else
																							if (o.equals(OWL.DataRange))
																								m_OWLEntities.addDatatype(s);
																							else
																								if (o.equals(OWL2.NamedIndividual))
																									m_OWLEntities.addIndividual(s);
																								else
																									if (o.equals(OWL2.NegativePropertyAssertion))
																									{
																										final RDFNode assertedSub = m_Model.getUniqueObject(s, OWL2.sourceIndividual);
																										final RDFNode assertedPred = m_Model.getUniqueObject(s, OWL2.assertionProperty);
																										final RDFNode assertedObjTV = m_Model.getUniqueObject(s, OWL2.targetValue);
																										final RDFNode assertedObjTI = m_Model.getUniqueObject(s, OWL2.targetIndividual);

																										if (assertedSub != null)
																											m_OWLEntities.addIndividual(assertedSub);
																										if (assertedPred != null)
																											if (assertedObjTV != null)
																												m_OWLEntities.assumeDatatypeRole(assertedPred);
																											else
																												m_OWLEntities.assumeObjectRole(assertedPred);
																										if (assertedObjTV != null)
																										{
																											if (assertedObjTV.isLiteral())
																												m_OWLEntities.addLiteral(assertedObjTV);
																											else
																												m_OWLEntities.addIndividual(assertedObjTV);
																										}
																										else
																											if (assertedObjTI != null)
																												m_OWLEntities.addIndividual(assertedObjTI);
																									}
																									else
																										if (o.equals(SWRL.Imp))
																										{
																											// Ignore
																										}
																										else
																											if (o.equals(SWRL.AtomList))
																											{
																												// Ignore
																											}
																											else
																												if (o.equals(SWRL.Variable))
																													m_OWLEntities.addSWRLVariable(s);
																												else
																													if (o.equals(SWRL.ClassAtom) || o.equals(SWRL.DataRangeAtom) || o.equals(SWRL.IndividualPropertyAtom) || o.equals(SWRL.DatavaluedPropertyAtom) || o.equals(SWRL.SameIndividualAtom) || o.equals(SWRL.DifferentIndividualsAtom))
																														processLater.add(stmt);
																													else
																														if (o.equals(SWRL.BuiltinAtom))
																														{
																															// Ignore
																														}
																														else
																														{
																															m_OWLEntities.addIndividual(s);

																															// to check if o is a class
																															processLater.add(stmt);
																														}
		}

		for (final Statement stmt : processLater)
		{
			final Resource s = stmt.getSubject();
			final RDFNode o = stmt.getObject();

			if (o.equals(RDFS.Class))
			{
				if (!m_Model.containsStatement(s, RDF.type, OWL.Restriction) && !m_Model.containsStatement(s, RDF.type, OWL.Class))
					m_OWLEntities.addRDFSClass(s);
			}
			else
				if (o.equals(OWL.FunctionalProperty))
				{
					if (!m_OWLEntities.containsRole(s))
						m_OWLEntities.assumeObjectRole(s);
				}
				else
					if (o.equals(RDF.Property))
					{
						if (!m_OWLEntities.containsRole(s))
							m_OWLEntities.assumeObjectRole(s);
					}
					else
						if (o.equals(SWRL.ClassAtom))
						{
							final RDFNode assertedClass = m_Model.getUniqueObject(s, SWRL.classPredicate);
							final RDFNode assertedObject = m_Model.getUniqueObject(s, SWRL.argument1);

							m_OWLEntities.assumeClass(assertedClass);
							if (!m_OWLEntities.containsIndividual(assertedObject))
								m_OWLEntities.assumeSWRLVariable(assertedObject);
						}
						else
							if (o.equals(SWRL.DataRangeAtom))
							{
								final RDFNode assertedDataRange = m_Model.getUniqueObject(s, SWRL.dataRange);
								final RDFNode assertedObject = m_Model.getUniqueObject(s, SWRL.argument1);

								m_OWLEntities.assumeDatatype(assertedDataRange);

								if (!assertedObject.isLiteral())
									m_OWLEntities.assumeSWRLVariable(assertedObject);
							}
							else
								if (o.equals(SWRL.IndividualPropertyAtom))
								{
									final RDFNode assertedProperty = m_Model.getUniqueObject(s, SWRL.propertyPredicate);
									final RDFNode assertedSubject = m_Model.getUniqueObject(s, SWRL.argument1);
									final RDFNode assertedObject = m_Model.getUniqueObject(s, SWRL.argument2);

									m_OWLEntities.assumeObjectRole(assertedProperty);

									if (!m_OWLEntities.containsIndividual(assertedSubject))
										m_OWLEntities.assumeSWRLVariable(assertedSubject);
									if (!m_OWLEntities.containsIndividual(assertedObject))
										m_OWLEntities.assumeSWRLVariable(assertedObject);
								}
								else
									if (o.equals(SWRL.DatavaluedPropertyAtom))
									{
										final RDFNode assertedProperty = m_Model.getUniqueObject(s, SWRL.propertyPredicate);
										final RDFNode assertedSubject = m_Model.getUniqueObject(s, SWRL.argument1);
										final RDFNode assertedObject = m_Model.getUniqueObject(s, SWRL.argument2);

										m_OWLEntities.assumeDatatypeRole(assertedProperty);

										if (!m_OWLEntities.containsIndividual(assertedSubject))
											m_OWLEntities.assumeSWRLVariable(assertedSubject);
										if (!assertedObject.isLiteral())
											m_OWLEntities.assumeSWRLVariable(assertedObject);
									}
									else
										if (o.equals(SWRL.SameIndividualAtom) || o.equals(SWRL.DifferentIndividualsAtom))
										{
											final RDFNode assertedObject1 = m_Model.getUniqueObject(s, SWRL.argument1);
											final RDFNode assertedObject2 = m_Model.getUniqueObject(s, SWRL.argument2);

											if (!m_OWLEntities.containsIndividual(assertedObject1))
												m_OWLEntities.assumeSWRLVariable(assertedObject1);
											if (!m_OWLEntities.containsIndividual(assertedObject2))
												m_OWLEntities.assumeSWRLVariable(assertedObject2);
										}
										else
											if (o.equals(OWL2.AllDisjointProperties) || o.equals(OWL2.AllDisjointClasses))
											{
												// Ignore, i don't think we want these things flagged.
											}
											else
												m_OWLEntities.assumeClass(o);
		}
	}

	private void processRestrictions()
	{
		for (final Resource res : m_OWLEntities.getAllRestrictions())
		{
			final RDFNode prop = m_Model.getUniqueObject(res, OWL.onProperty);

			if (prop == null)
				continue;

			RDFNode val = null;

			val = m_Model.getUniqueObject(res, OWL2.onClass);
			if (val != null && val.isResource())
			{
				m_OWLEntities.assumeObjectRole(prop);
				m_OWLEntities.assumeClass(val);
			}

			val = m_Model.getUniqueObject(res, OWL2.onDataRange);
			if (val != null && val.isResource())
			{
				m_OWLEntities.assumeDatatypeRole(prop);
				m_OWLEntities.assumeDatatype(val);
			}

			val = m_Model.getUniqueObject(res, OWL.hasValue);
			if (val != null)
				if (val.isResource())
					m_OWLEntities.addIndividual(val);
				else
					m_OWLEntities.assumeDatatypeRole(prop);

			if (!m_OWLEntities.containsRole(prop))
				m_OWLEntities.assumeObjectRole(prop);

			val = m_Model.getUniqueObject(res, OWL.someValuesFrom);
			if (val == null)
				val = m_Model.getUniqueObject(res, OWL.allValuesFrom);
			if (val != null && val.isResource())
				if (m_OWLEntities.containsObjectRole(prop))
					m_OWLEntities.assumeClass(val);
				else
					if (m_OWLEntities.containsDatatypeRole(prop))
						m_OWLEntities.assumeDatatype(val);

		}
	}

	private void processTriples()
	{
		for (final Statement stmt : m_Model.getStatements())
		{
			final Resource s = stmt.getSubject();
			final Property p = stmt.getPredicate();
			final RDFNode o = stmt.getObject();

			if (o.isLiteral())
				m_OWLEntities.addLiteral(o);

			if (p.equals(RDF.type))
			{
				// these triples have been processed before so don't do anything
			}
			else
				if (p.equals(RDF.subject) || p.equals(RDF.predicate) || p.equals(RDF.object))
				{
					// processed before
				}
				else
					if (RESTRICTION_PROPS.contains(p))
					{
						// Ignore
					}
					else
						if (DATA_RANGE_FACETS.contains(p))
						{
							// Ignore
						}
						else
							if (p.equals(OWL2.members))
							{
								if (m_Model.containsStatement(s, RDF.type, OWL.AllDifferent))
								{
									if (m_Lists.containsKey(o))
										for (final RDFNode r : m_Lists.get(o))
											m_OWLEntities.addIndividual(r);
									else
									{
										// TODO we probably want to warn about this case but it is not clear under which category
									}
								}
								else
									if (m_Model.containsStatement(s, RDF.type, OWL2.AllDisjointClasses))
									{
										if (m_Lists.containsKey(o))
											for (final RDFNode r : m_Lists.get(o))
												m_OWLEntities.assumeClass(r);
										else
										{
											// TODO we probably want to warn about this case but it is not clear under which category
										}
									}
									else
										if (m_Model.containsStatement(s, RDF.type, OWL2.AllDisjointProperties))
										{
											if (m_Lists.containsKey(o))
												for (final RDFNode r : m_Lists.get(o))
													m_OWLEntities.addUntypedRole(r);
											else
											{
												// TODO we probably want to warn about this case but it is not clear under which category
											}
										}
										else
										{
											// TODO we probably want to warn about this case but it is not clear under which category
										}
							}
							else
								if (p.equals(OWL2.assertionProperty) || p.equals(OWL2.targetValue) || p.equals(OWL2.sourceIndividual) || p.equals(OWL2.targetIndividual))
								{
									// processed before
								}
								else
									if (p.equals(OWL.intersectionOf) || p.equals(OWL.unionOf) || p.equals(OWL2.disjointUnionOf))
									{

										if (o.isResource())
											for (final RDFNode node : m_Lists.get(o))
												m_OWLEntities.assumeClass(node);
										else
										{
											// TODO: _log this
										}
									}
									else
										if (p.equals(OWL.complementOf))
										{
											if (m_OWLEntities.containsDatatype(s))
												m_OWLEntities.assumeDatatype(o);
											else
											{
												m_OWLEntities.assumeClass(s);
												m_OWLEntities.assumeClass(o);
											}
										}
										else
											if (p.equals(OWL.oneOf))
											{
												if (!m_OWLEntities.containsDatatype(s))
												{
													m_OWLEntities.assumeClass(s);

													if (o.isResource())
														for (final RDFNode node : m_Lists.get(o))
															m_OWLEntities.addIndividual(node);
													else
													{
														// TODO: _log this
													}
												}
											}
											else
												if (p.equals(OWL2.hasKey))
												{
													m_OWLEntities.assumeClass(s);

													if (o.isResource())
														if (m_Lists.containsKey(o))
															for (final RDFNode aProp : m_Lists.get(o))
																m_OWLEntities.addUntypedRole(aProp);
														else
														{
															// what is this case?  this is always supposed to be a list, maybe this never happens cause the parser
															// will catch it.
														}
												}
												else
													if (p.equals(RDFS.subClassOf))
													{
														m_OWLEntities.assumeClass(s);
														m_OWLEntities.assumeClass(o);
													}
													else
														if (p.equals(OWL.equivalentClass))
														{
															// fix for #438: do not assume that both arguments to owl:equivalentClass must automatically be classes
															// owl:equivalentClass can also be used to relate equivalent datatypes. Make such an assumption only
															// if both arguments are not datatypes

															if (!m_OWLEntities.containsDatatype(s) && !m_OWLEntities.containsDatatype(o))
															{
																m_OWLEntities.assumeClass(s);
																m_OWLEntities.assumeClass(o);
															}
														}
														else
															if (p.equals(OWL.disjointWith))
															{
																m_OWLEntities.assumeClass(s);
																m_OWLEntities.assumeClass(o);
															}
															else
																if (p.equals(OWL.equivalentProperty))
																{
																	// TODO: i dont think these should be assume object role
																	if (!m_OWLEntities.containsRole(s))
																		m_OWLEntities.assumeObjectRole(s);

																	if (!m_OWLEntities.containsRole(o))
																		m_OWLEntities.assumeObjectRole(o);
																}
																else
																	if (p.equals(RDFS.subPropertyOf))
																	{
																		// TODO: i dont think these should be assume object role either
																		if (!m_OWLEntities.containsRole(s))
																			m_OWLEntities.assumeObjectRole(s);

																		if (!m_OWLEntities.containsRole(o))
																			m_OWLEntities.assumeObjectRole(o);
																	}
																	else
																		if (p.equals(OWL2.propertyDisjointWith))
																		{
																			m_OWLEntities.addUntypedRole(s);
																			m_OWLEntities.addUntypedRole(o);
																		}
																		else
																			if (p.equals(OWL2.propertyChainAxiom))
																			{
																				m_OWLEntities.assumeObjectRole(s);
																				if (o.isResource())
																					for (final RDFNode node : m_Lists.get(o))
																						m_OWLEntities.assumeObjectRole(node);
																				else
																				{
																					// TODO: _log this
																				}
																			}
																			else
																				if (p.equals(OWL2.onDatatype))
																				{
																					if (!m_Model.containsStatement(s, RDF.type, RDFS.Datatype))
																						m_OWLEntities.assumeDatatype(s);
																					else
																						m_OWLEntities.addDatatype(s);
																				}
																				else
																					if (p.equals(OWL2.withRestrictions))
																					{
																						if (!m_Model.containsStatement(s, RDF.type, RDFS.Datatype))
																							m_OWLEntities.assumeDatatype(s);
																						else
																							m_OWLEntities.addDatatype(s);

																						if (o.isResource() && m_Lists.containsKey(o))
																							for (final RDFNode aType : m_Lists.get(o))
																								processWithRestrictionNode(aType);
																						else
																							if (o.isResource())
																								// it's a resource, but not a list, maybe then we'll just assume this is a facet and we'll validate it
																								processWithRestrictionNode(o);
																							else
																							{
																								// TODO: _log this? or would this be a parse error.  probably not.  this is probably a lint?
																							}
																					}
																					else
																						if (p.equals(OWL.inverseOf))
																						{
																							if (!m_OWLEntities.containsRole(s))
																								if (s.isAnon())
																									m_OWLEntities.addObjectRole(o);
																								else
																									m_OWLEntities.assumeObjectRole(s);

																							if (!m_OWLEntities.containsRole(o))
																								m_OWLEntities.assumeObjectRole(o);
																						}
																						else
																							if (p.equals(OWL.sameAs))
																							{
																								m_OWLEntities.addIndividual(s);
																								m_OWLEntities.addIndividual(o);
																							}
																							else
																								if (p.equals(OWL2.onClass))
																									m_OWLEntities.assumeClass(o);
																								else
																									if (p.equals(OWL.differentFrom))
																									{

																									}
																									else
																										if (p.equals(RDFS.domain))
																										{
																											if (!s.isAnon())
																												if (s.getURI().equals(Namespaces.RDF.toString()) || s.getURI().equals(Namespaces.OWL.toString()))
																													// report.addMessage(FULL, "Invalid Domain Restriction",
																													// "rdfs:domain is used on built-in property %1%", st);
																													continue;

																											if (!m_OWLEntities.containsRole(s))
																												m_OWLEntities.assumeObjectRole(s);

																											m_OWLEntities.assumeClass(o);
																										}
																										else
																											if (p.equals(RDFS.range))
																											{
																												if (s.isAnon())
																													// report.addMessage(FULL, "Invalid Range Restriction",
																													// "rdfs:range is used on an anonymous property");
																													continue;
																												if (!s.isAnon())
																													if (s.getURI().equals(Namespaces.RDF.toString()) || s.getURI().equals(Namespaces.OWL.toString()))
																														// report.addMessage(FULL, "Invalid Domain Restriction",
																														// "rdfs:domain is used on built-in property %1%", st);
																														continue;

																												// we have s rdfs:range o
																												// there are couple of different possibilities
																												// s is DP & o is undefined -> o is Datatype
																												// s is OP & o is undefined -> o is class
																												// s is undefined & o is Class -> s is OP
																												// s is undefined & o is Datatype -> s is DP
																												// s is undefined & o is undefined -> s is OP, o is class
																												// any other case error!

																												if (!m_OWLEntities.containsResource(s))
																												{
																													if (m_OWLEntities.containsDatatype(o))
																													{
																														if (!m_OWLEntities.containsRole(s))
																															m_OWLEntities.assumeDatatypeRole(s);
																													}
																													else
																														if (m_OWLEntities.containsClass(o))
																														{
																															if (!m_OWLEntities.containsRole(s))
																																m_OWLEntities.assumeObjectRole(s);
																														}
																														else
																															if (m_OWLEntities.containsIndividual(o) || m_OWLEntities.containsRole(o))
																															{
																																// report.addMessage(FULL, "Untyped Resource", "%1% is
																																// used in an rdfs:range restriction", st, ot);
																															}
																															else
																															{
																																if (!m_OWLEntities.containsRole(s))
																																	m_OWLEntities.assumeObjectRole(s);
																																m_OWLEntities.assumeClass(o);
																															}
																												}
																												else
																													if (!m_OWLEntities.containsResource(o))
																														if (m_OWLEntities.containsObjectRole(s))
																															m_OWLEntities.assumeClass(o);
																														else
																															if (m_OWLEntities.containsDatatypeRole(s))
																																m_OWLEntities.assumeDatatype(o);
																											}
																											else
																												if (p.equals(OWL2.onDataRange))
																													m_OWLEntities.assumeDatatype(s);
																												else
																													if (p.equals(OWL.distinctMembers))
																													{
																														if (o.isResource())
																															for (final RDFNode node : m_Lists.get(o))
																																m_OWLEntities.addIndividual(node);
																														else
																														{
																															// TODO: _log this
																														}
																													}
																													else
																														if (p.equals(OWL.imports))
																														{
																															m_OWLEntities.assumeOntology(o);
																															m_OWLEntities.assumeOntology(s);
																														}
																														else
																															if (p.equals(RDF.first))
																															{
																																// Ignore
																															}
																															else
																																if (p.equals(RDF.rest))
																																{
																																	// Ignore
																																}
																																else
																																	if (m_OWLEntities.containsOntologyRole(p))
																																	{
																																		m_OWLEntities.assumeOntology(o);
																																		m_OWLEntities.assumeOntology(s);

																																	}
																																	else
																																		if (p.equals(SWRL.Imp) || p.equals(SWRL.head) || p.equals(SWRL.body) || p.equals(SWRL.builtin))
																																		{
																																			// Ignore
																																		}
																																		else
																																			if (p.equals(SWRL.classPredicate) || p.equals(SWRL.propertyPredicate) || p.equals(SWRL.argument1) || p.equals(SWRL.argument2) || p.equals(SWRL.arguments))
																																			{
																																				// Processed before
																																			}
																																			else
																																			{
																																				if (m_OWLEntities.containsAnnotaionRole(p))
																																					continue;
																																				else
																																					if (!m_OWLEntities.containsRole(p))
																																						if (o.isLiteral())
																																							m_OWLEntities.assumeDatatypeRole(p);
																																						else
																																							if (!m_OWLEntities.containsIndividual(s))
																																								m_OWLEntities.assumeAnnotationRole(p);
																																							else
																																								m_OWLEntities.assumeObjectRole(p);

																																				if (m_OWLEntities.containsAnnotaionRole(p))
																																					continue;

																																				if (m_OWLEntities.containsDatatypeRole(p))
																																				{
																																					if (o.isLiteral())
																																					{
																																						final Literal literal = o.asLiteral();
																																						final String datatypeURI = literal.getDatatypeURI();

																																						if (datatypeURI != null && !datatypeURI.equals(""))
																																						{
																																							final Resource datatype = ResourceFactory.createResource(datatypeURI);
																																							if (!m_OWLEntities.containsDatatype(datatype))
																																								m_OWLEntities.assumeDatatype(datatype);
																																						}
																																					}
																																					else
																																						m_OWLEntities.addResourcesAsLiteral(o.asResource());

																																					m_OWLEntities.assumeIndividual(s);
																																				}
																																				else
																																				{
																																					m_OWLEntities.assumeIndividual(s);
																																					if (o.isLiteral())
																																						m_OWLEntities.addLiteralAsIndividual(o.asLiteral());
																																					else
																																						m_OWLEntities.assumeIndividual(o);
																																				}
																																			}
		}
	}

	private void processWithRestrictionNode(final RDFNode theNode)
	{
		// TODO: implement me
		// for now, this will do nothing.  but the intent here is that theNode is an item from the withRestriction
		// collection for an owl datatype restriction.  so we want this to validate that the facet described is
		// valid.
	}
}
