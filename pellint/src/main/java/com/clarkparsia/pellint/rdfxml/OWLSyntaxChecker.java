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

	private Map<Resource, List<RDFNode>> _lists;
	private OWLEntityDatabase _OWLEntities;
	private RDFModel _model = null;

	private boolean _excludeValidPunnings = false;

	/**
	 * Sets if valid punninga will be excluded from lint report. OWL 2 allows resources to have certain multiple types (known as punning), e.g. a resource can
	 * be both a class and an _individual. However, certain punnings are not allowed under any _condition, e.g. a resource cannot be both a datatype property
	 * and an object property. Invalid punnings are always returned. If this option is set to <code>true</code>, punnings valid for OWL 2 will be excluded from
	 * the report. By default, these punnings are reported.
	 *
	 * @param excludeValidPunning If <code>true</code> OWL 2 valid punnings will not be inluded in the result
	 */
	public void setExcludeValidPunnings(final boolean excludeValidPunning)
	{
		_excludeValidPunnings = excludeValidPunning;
	}

	/**
	 * Returns if valid punninga will be excluded from lint report.
	 */
	public boolean isExcludeValidPunnings()
	{
		return _excludeValidPunnings;
	}

	public RDFLints validate(final RDFModel model)
	{
		_model = model;

		_OWLEntities = new OWLEntityDatabase();
		_OWLEntities.addAnnotationRole(RDFS.label);
		_OWLEntities.addAnnotationRole(RDFS.comment);
		_OWLEntities.addAnnotationRole(RDFS.seeAlso);
		_OWLEntities.addAnnotationRole(RDFS.isDefinedBy);
		_OWLEntities.addAnnotationRole(OWL.versionInfo);
		_OWLEntities.addOntologyRole(OWL.backwardCompatibleWith);
		_OWLEntities.addOntologyRole(OWL.priorVersion);
		_OWLEntities.addOntologyRole(OWL.incompatibleWith);
		_OWLEntities.addClass(OWL.Thing);
		_OWLEntities.addClass(OWL.Nothing);

		// Fixes #194
		_OWLEntities.addDatatype(RDFS.Literal);

		// Fixes #457
		_OWLEntities.addDatatype(ResourceFactory.createResource("http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral"));

		for (final ATermAppl uri : new DatatypeReasonerImpl().listDataRanges())
			_OWLEntities.addDatatype(ResourceFactory.createResource(uri.getName()));

		_lists = CollectionUtil.makeMap();
		_lists.put(RDF.nil, CollectionUtil.<RDFNode> makeList());

		processTypes();
		processTriples();
		processRestrictions();

		return reportLints();
	}

	private RDFLints reportLints()
	{
		final RDFLints lints = new RDFLints();

		lints.add("Untyped ontologies", toString(_OWLEntities.getDoubtfulOntologies()));
		lints.add("Untyped classes", toString(_OWLEntities.getDoubtfulClasses()));
		lints.add("Untyped datatypes", toString(_OWLEntities.getDoubtfulDatatypes()));
		lints.add("Untyped object properties", toString(_OWLEntities.getDoubtfulObjectRoles()));
		lints.add("Untyped datatype properties", toString(_OWLEntities.getDoubtfulDatatypeRoles()));
		lints.add("Untyped annotation properties", toString(_OWLEntities.getDoubtfulAnnotaionRoles()));
		lints.add("Untyped properties", toString(_OWLEntities.getDoubtfulRoles()));
		lints.add("Untyped individuals", toString(_OWLEntities.getDoubtfulIndividuals()));

		lints.add("Using rdfs:Class instead of owl:Class", toString(_OWLEntities.getAllRDFClasses()));
		lints.add("Multiple typed resources", toString(_OWLEntities.getMultiTypedResources(_excludeValidPunnings)));

		lints.add("Literals used where a class is _expected", toStringLiterals(_OWLEntities.getLiteralsAsClass()));
		lints.add("Literals used where an _individual is _expected", toStringLiterals(_OWLEntities.getLiteralsAsIndividuals()));
		lints.add("Resource used where a literal is _expected", toString(_OWLEntities.getResourcesAsLiterals()));

		lints.addMissingStatements(_OWLEntities.getAllTypingStatements());

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

		final RDFNode first = _model.getUniqueObject(head, RDF.first);
		final RDFNode rest = _model.getUniqueObject(head, RDF.rest);
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
		if (_lists.containsKey(head))
			return;

		final List<RDFNode> list = CollectionUtil.makeList();
		_lists.put(head, list);
		createList(head, list);
	}

	// We manage the org.mindswap.pellet.jena.vocabulary.OWL2 so we can manage the code when removing will occure.
	@SuppressWarnings("deprecation")
	private static Resource _selfRestriction = OWL2.SelfRestriction;

	private void processTypes()
	{
		// list pre-processing
		for (final Statement stmt : _model.getStatementsByPredicate(RDF.first))
		{
			final Resource s = stmt.getSubject();
			for (final Statement aStmt : _model.getStatementsByObject(s))
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
		for (final Statement stmt : _model.getStatementsByPredicate(RDF.type))
		{
			final Resource s = stmt.getSubject();
			final RDFNode o = stmt.getObject();

			if (o.equals(OWL.Class) || o.equals(OWL.DeprecatedClass))
				_OWLEntities.addClass(s);
			else
				if (o.equals(RDFS.Class))
					processLater.add(stmt);
				else
					if (o.equals(RDFS.Datatype))
						_OWLEntities.addDatatype(s);
					else
						if (o.equals(OWL.Thing))
							_OWLEntities.addIndividual(s);
						else
							if (o.equals(OWL.Restriction))
								_OWLEntities.addRestriction(s);
							else
								if (o.equals(_selfRestriction))
									_OWLEntities.addRestriction(s);
								else
									if (o.equals(OWL.AllDifferent))
									{
										// Ignore
									}
									else
										if (o.equals(OWL.ObjectProperty))
											_OWLEntities.addObjectRole(s);
										else
											if (o.equals(OWL.DatatypeProperty))
												_OWLEntities.addDatatypeRole(s);
											else
												if (o.equals(OWL.AnnotationProperty))
													_OWLEntities.addAnnotationRole(s);
												else
													if (o.equals(OWL.DeprecatedProperty))
														_OWLEntities.addUntypedRole(s);
													else
														if (o.equals(RDF.Property))
															processLater.add(stmt);
														else
															if (o.equals(OWL.TransitiveProperty))
																_OWLEntities.addTransitiveRole(s);
															else
																if (o.equals(OWL.SymmetricProperty))
																	_OWLEntities.addSymmetricRole(s);
																else
																	if (o.equals(OWL2.AsymmetricProperty))
																		_OWLEntities.addAntiSymmetricRole(s);
																	else
																		if (o.equals(OWL2.ReflexiveProperty))
																			_OWLEntities.addReflexiveRole(s);
																		else
																			if (o.equals(OWL2.IrreflexiveProperty))
																				_OWLEntities.addIrreflexiveRole(s);
																			else
																				if (o.equals(OWL.FunctionalProperty))
																					processLater.add(stmt);
																				else
																					if (o.equals(OWL.InverseFunctionalProperty))
																						_OWLEntities.addInverseFunctionalRole(s);
																					else
																						if (o.equals(OWL.Ontology))
																							_OWLEntities.addOntology(s);
																						else
																							if (o.equals(OWL.DataRange))
																								_OWLEntities.addDatatype(s);
																							else
																								if (o.equals(OWL2.NamedIndividual))
																									_OWLEntities.addIndividual(s);
																								else
																									if (o.equals(OWL2.NegativePropertyAssertion))
																									{
																										final RDFNode assertedSub = _model.getUniqueObject(s, OWL2.sourceIndividual);
																										final RDFNode assertedPred = _model.getUniqueObject(s, OWL2.assertionProperty);
																										final RDFNode assertedObjTV = _model.getUniqueObject(s, OWL2.targetValue);
																										final RDFNode assertedObjTI = _model.getUniqueObject(s, OWL2.targetIndividual);

																										if (assertedSub != null)
																											_OWLEntities.addIndividual(assertedSub);
																										if (assertedPred != null)
																											if (assertedObjTV != null)
																												_OWLEntities.assumeDatatypeRole(assertedPred);
																											else
																												_OWLEntities.assumeObjectRole(assertedPred);
																										if (assertedObjTV != null)
																										{
																											if (assertedObjTV.isLiteral())
																												_OWLEntities.addLiteral(assertedObjTV);
																											else
																												_OWLEntities.addIndividual(assertedObjTV);
																										}
																										else
																											if (assertedObjTI != null)
																												_OWLEntities.addIndividual(assertedObjTI);
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
																													_OWLEntities.addSWRLVariable(s);
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
																															_OWLEntities.addIndividual(s);

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
				if (!_model.containsStatement(s, RDF.type, OWL.Restriction) && !_model.containsStatement(s, RDF.type, OWL.Class))
					_OWLEntities.addRDFSClass(s);
			}
			else
				if (o.equals(OWL.FunctionalProperty))
				{
					if (!_OWLEntities.containsRole(s))
						_OWLEntities.assumeObjectRole(s);
				}
				else
					if (o.equals(RDF.Property))
					{
						if (!_OWLEntities.containsRole(s))
							_OWLEntities.assumeObjectRole(s);
					}
					else
						if (o.equals(SWRL.ClassAtom))
						{
							final RDFNode assertedClass = _model.getUniqueObject(s, SWRL.classPredicate);
							final RDFNode assertedObject = _model.getUniqueObject(s, SWRL.argument1);

							_OWLEntities.assumeClass(assertedClass);
							if (!_OWLEntities.containsIndividual(assertedObject))
								_OWLEntities.assumeSWRLVariable(assertedObject);
						}
						else
							if (o.equals(SWRL.DataRangeAtom))
							{
								final RDFNode assertedDataRange = _model.getUniqueObject(s, SWRL.dataRange);
								final RDFNode assertedObject = _model.getUniqueObject(s, SWRL.argument1);

								_OWLEntities.assumeDatatype(assertedDataRange);

								if (!assertedObject.isLiteral())
									_OWLEntities.assumeSWRLVariable(assertedObject);
							}
							else
								if (o.equals(SWRL.IndividualPropertyAtom))
								{
									final RDFNode assertedProperty = _model.getUniqueObject(s, SWRL.propertyPredicate);
									final RDFNode assertedSubject = _model.getUniqueObject(s, SWRL.argument1);
									final RDFNode assertedObject = _model.getUniqueObject(s, SWRL.argument2);

									_OWLEntities.assumeObjectRole(assertedProperty);

									if (!_OWLEntities.containsIndividual(assertedSubject))
										_OWLEntities.assumeSWRLVariable(assertedSubject);
									if (!_OWLEntities.containsIndividual(assertedObject))
										_OWLEntities.assumeSWRLVariable(assertedObject);
								}
								else
									if (o.equals(SWRL.DatavaluedPropertyAtom))
									{
										final RDFNode assertedProperty = _model.getUniqueObject(s, SWRL.propertyPredicate);
										final RDFNode assertedSubject = _model.getUniqueObject(s, SWRL.argument1);
										final RDFNode assertedObject = _model.getUniqueObject(s, SWRL.argument2);

										_OWLEntities.assumeDatatypeRole(assertedProperty);

										if (!_OWLEntities.containsIndividual(assertedSubject))
											_OWLEntities.assumeSWRLVariable(assertedSubject);
										if (!assertedObject.isLiteral())
											_OWLEntities.assumeSWRLVariable(assertedObject);
									}
									else
										if (o.equals(SWRL.SameIndividualAtom) || o.equals(SWRL.DifferentIndividualsAtom))
										{
											final RDFNode assertedObject1 = _model.getUniqueObject(s, SWRL.argument1);
											final RDFNode assertedObject2 = _model.getUniqueObject(s, SWRL.argument2);

											if (!_OWLEntities.containsIndividual(assertedObject1))
												_OWLEntities.assumeSWRLVariable(assertedObject1);
											if (!_OWLEntities.containsIndividual(assertedObject2))
												_OWLEntities.assumeSWRLVariable(assertedObject2);
										}
										else
											if (o.equals(OWL2.AllDisjointProperties) || o.equals(OWL2.AllDisjointClasses))
											{
												// Ignore, i don't think we want these things flagged.
											}
											else
												_OWLEntities.assumeClass(o);
		}
	}

	private void processRestrictions()
	{
		for (final Resource res : _OWLEntities.getAllRestrictions())
		{
			final RDFNode prop = _model.getUniqueObject(res, OWL.onProperty);

			if (prop == null)
				continue;

			RDFNode val = null;

			val = _model.getUniqueObject(res, OWL2.onClass);
			if (val != null && val.isResource())
			{
				_OWLEntities.assumeObjectRole(prop);
				_OWLEntities.assumeClass(val);
			}

			val = _model.getUniqueObject(res, OWL2.onDataRange);
			if (val != null && val.isResource())
			{
				_OWLEntities.assumeDatatypeRole(prop);
				_OWLEntities.assumeDatatype(val);
			}

			val = _model.getUniqueObject(res, OWL.hasValue);
			if (val != null)
				if (val.isResource())
					_OWLEntities.addIndividual(val);
				else
					_OWLEntities.assumeDatatypeRole(prop);

			if (!_OWLEntities.containsRole(prop))
				_OWLEntities.assumeObjectRole(prop);

			val = _model.getUniqueObject(res, OWL.someValuesFrom);
			if (val == null)
				val = _model.getUniqueObject(res, OWL.allValuesFrom);
			if (val != null && val.isResource())
				if (_OWLEntities.containsObjectRole(prop))
					_OWLEntities.assumeClass(val);
				else
					if (_OWLEntities.containsDatatypeRole(prop))
						_OWLEntities.assumeDatatype(val);

		}
	}

	private void processTriples()
	{
		for (final Statement stmt : _model.getStatements())
		{
			final Resource s = stmt.getSubject();
			final Property p = stmt.getPredicate();
			final RDFNode o = stmt.getObject();

			if (o.isLiteral())
				_OWLEntities.addLiteral(o);

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
								if (_model.containsStatement(s, RDF.type, OWL.AllDifferent))
								{
									if (_lists.containsKey(o))
										for (final RDFNode r : _lists.get(o))
											_OWLEntities.addIndividual(r);
									else
									{
										// TODO we probably want to warn about this case but it is not clear under which category
									}
								}
								else
									if (_model.containsStatement(s, RDF.type, OWL2.AllDisjointClasses))
									{
										if (_lists.containsKey(o))
											for (final RDFNode r : _lists.get(o))
												_OWLEntities.assumeClass(r);
										else
										{
											// TODO we probably want to warn about this case but it is not clear under which category
										}
									}
									else
										if (_model.containsStatement(s, RDF.type, OWL2.AllDisjointProperties))
										{
											if (_lists.containsKey(o))
												for (final RDFNode r : _lists.get(o))
													_OWLEntities.addUntypedRole(r);
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
											for (final RDFNode node : _lists.get(o))
												_OWLEntities.assumeClass(node);
										else
										{
											// TODO: _logger this
										}
									}
									else
										if (p.equals(OWL.complementOf))
										{
											if (_OWLEntities.containsDatatype(s))
												_OWLEntities.assumeDatatype(o);
											else
											{
												_OWLEntities.assumeClass(s);
												_OWLEntities.assumeClass(o);
											}
										}
										else
											if (p.equals(OWL.oneOf))
											{
												if (!_OWLEntities.containsDatatype(s))
												{
													_OWLEntities.assumeClass(s);

													if (o.isResource())
														for (final RDFNode node : _lists.get(o))
															_OWLEntities.addIndividual(node);
													else
													{
														// TODO: _logger this
													}
												}
											}
											else
												if (p.equals(OWL2.hasKey))
												{
													_OWLEntities.assumeClass(s);

													if (o.isResource())
														if (_lists.containsKey(o))
															for (final RDFNode aProp : _lists.get(o))
																_OWLEntities.addUntypedRole(aProp);
														else
														{
															// what is this case?  this is always supposed to be a list, maybe this never happens cause the parser
															// will catch it.
														}
												}
												else
													if (p.equals(RDFS.subClassOf))
													{
														_OWLEntities.assumeClass(s);
														_OWLEntities.assumeClass(o);
													}
													else
														if (p.equals(OWL.equivalentClass))
														{
															// fix for #438: do not assume that both arguments to owl:equivalentClass must automatically be classes
															// owl:equivalentClass can also be used to relate equivalent datatypes. Make such an assumption only
															// if both arguments are not datatypes

															if (!_OWLEntities.containsDatatype(s) && !_OWLEntities.containsDatatype(o))
															{
																_OWLEntities.assumeClass(s);
																_OWLEntities.assumeClass(o);
															}
														}
														else
															if (p.equals(OWL.disjointWith))
															{
																_OWLEntities.assumeClass(s);
																_OWLEntities.assumeClass(o);
															}
															else
																if (p.equals(OWL.equivalentProperty))
																{
																	// TODO: i dont think these should be assume object role
																	if (!_OWLEntities.containsRole(s))
																		_OWLEntities.assumeObjectRole(s);

																	if (!_OWLEntities.containsRole(o))
																		_OWLEntities.assumeObjectRole(o);
																}
																else
																	if (p.equals(RDFS.subPropertyOf))
																	{
																		// TODO: i dont think these should be assume object role either
																		if (!_OWLEntities.containsRole(s))
																			_OWLEntities.assumeObjectRole(s);

																		if (!_OWLEntities.containsRole(o))
																			_OWLEntities.assumeObjectRole(o);
																	}
																	else
																		if (p.equals(OWL2.propertyDisjointWith))
																		{
																			_OWLEntities.addUntypedRole(s);
																			_OWLEntities.addUntypedRole(o);
																		}
																		else
																			if (p.equals(OWL2.propertyChainAxiom))
																			{
																				_OWLEntities.assumeObjectRole(s);
																				if (o.isResource())
																					for (final RDFNode node : _lists.get(o))
																						_OWLEntities.assumeObjectRole(node);
																				else
																				{
																					// TODO: _logger this
																				}
																			}
																			else
																				if (p.equals(OWL2.onDatatype))
																				{
																					if (!_model.containsStatement(s, RDF.type, RDFS.Datatype))
																						_OWLEntities.assumeDatatype(s);
																					else
																						_OWLEntities.addDatatype(s);
																				}
																				else
																					if (p.equals(OWL2.withRestrictions))
																					{
																						if (!_model.containsStatement(s, RDF.type, RDFS.Datatype))
																							_OWLEntities.assumeDatatype(s);
																						else
																							_OWLEntities.addDatatype(s);

																						if (o.isResource() && _lists.containsKey(o))
																							for (final RDFNode aType : _lists.get(o))
																								processWithRestrictionNode(aType);
																						else
																							if (o.isResource())
																								// it's a resource, but not a list, maybe then we'll just assume this is a facet and we'll validate it
																								processWithRestrictionNode(o);
																							else
																							{
																								// TODO: _logger this? or would this be a parse error.  probably not.  this is probably a lint?
																							}
																					}
																					else
																						if (p.equals(OWL.inverseOf))
																						{
																							if (!_OWLEntities.containsRole(s))
																								if (s.isAnon())
																									_OWLEntities.addObjectRole(o);
																								else
																									_OWLEntities.assumeObjectRole(s);

																							if (!_OWLEntities.containsRole(o))
																								_OWLEntities.assumeObjectRole(o);
																						}
																						else
																							if (p.equals(OWL.sameAs))
																							{
																								_OWLEntities.addIndividual(s);
																								_OWLEntities.addIndividual(o);
																							}
																							else
																								if (p.equals(OWL2.onClass))
																									_OWLEntities.assumeClass(o);
																								else
																									if (p.equals(OWL.differentFrom))
																									{
																										// Nothing to do.
																									}
																									else
																										if (p.equals(RDFS.domain))
																										{
																											if (!s.isAnon())
																												if (s.getURI().equals(Namespaces.RDF.toString()) || s.getURI().equals(Namespaces.OWL.toString()))
																													// report.addMessage(FULL, "Invalid Domain Restriction",
																													// "rdfs:domain is used on built-in property %1%", st);
																													continue;

																											if (!_OWLEntities.containsRole(s))
																												_OWLEntities.assumeObjectRole(s);

																											_OWLEntities.assumeClass(o);
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

																												if (!_OWLEntities.containsResource(s))
																												{
																													if (_OWLEntities.containsDatatype(o))
																													{
																														if (!_OWLEntities.containsRole(s))
																															_OWLEntities.assumeDatatypeRole(s);
																													}
																													else
																														if (_OWLEntities.containsClass(o))
																														{
																															if (!_OWLEntities.containsRole(s))
																																_OWLEntities.assumeObjectRole(s);
																														}
																														else
																															if (_OWLEntities.containsIndividual(o) || _OWLEntities.containsRole(o))
																															{
																																// report.addMessage(FULL, "Untyped Resource", "%1% is
																																// used in an rdfs:range restriction", st, ot);
																															}
																															else
																															{
																																if (!_OWLEntities.containsRole(s))
																																	_OWLEntities.assumeObjectRole(s);
																																_OWLEntities.assumeClass(o);
																															}
																												}
																												else
																													if (!_OWLEntities.containsResource(o))
																														if (_OWLEntities.containsObjectRole(s))
																															_OWLEntities.assumeClass(o);
																														else
																															if (_OWLEntities.containsDatatypeRole(s))
																																_OWLEntities.assumeDatatype(o);
																											}
																											else
																												if (p.equals(OWL2.onDataRange))
																													_OWLEntities.assumeDatatype(s);
																												else
																													if (p.equals(OWL.distinctMembers))
																													{
																														if (o.isResource())
																															for (final RDFNode node : _lists.get(o))
																																_OWLEntities.addIndividual(node);
																														else
																														{
																															// TODO: _logger this
																														}
																													}
																													else
																														if (p.equals(OWL.imports))
																														{
																															_OWLEntities.assumeOntology(o);
																															_OWLEntities.assumeOntology(s);
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
																																	if (_OWLEntities.containsOntologyRole(p))
																																	{
																																		_OWLEntities.assumeOntology(o);
																																		_OWLEntities.assumeOntology(s);

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
																																				if (_OWLEntities.containsAnnotaionRole(p))
																																					continue;
																																				else
																																					if (!_OWLEntities.containsRole(p))
																																						if (o.isLiteral())
																																							_OWLEntities.assumeDatatypeRole(p);
																																						else
																																							if (!_OWLEntities.containsIndividual(s))
																																								_OWLEntities.assumeAnnotationRole(p);
																																							else
																																								_OWLEntities.assumeObjectRole(p);

																																				if (_OWLEntities.containsAnnotaionRole(p))
																																					continue;

																																				if (_OWLEntities.containsDatatypeRole(p))
																																				{
																																					if (o.isLiteral())
																																					{
																																						final Literal literal = o.asLiteral();
																																						final String datatypeURI = literal.getDatatypeURI();

																																						if (datatypeURI != null && !datatypeURI.equals(""))
																																						{
																																							final Resource datatype = ResourceFactory.createResource(datatypeURI);
																																							if (!_OWLEntities.containsDatatype(datatype))
																																								_OWLEntities.assumeDatatype(datatype);
																																						}
																																					}
																																					else
																																						_OWLEntities.addResourcesAsLiteral(o.asResource());

																																					_OWLEntities.assumeIndividual(s);
																																				}
																																				else
																																				{
																																					_OWLEntities.assumeIndividual(s);
																																					if (o.isLiteral())
																																						_OWLEntities.addLiteralAsIndividual(o.asLiteral());
																																					else
																																						_OWLEntities.assumeIndividual(o);
																																				}
																																			}
		}
	}

	/**
	 * This method isn't implemented.
	 * 
	 * @param theNode not used.
	 */
	private void processWithRestrictionNode(@SuppressWarnings("unused") final RDFNode theNode)
	{
		// TODO: implement me
		/*
		for now, this will do nothing.
		The intent here is that theNode is an item from the withRestriction.
		collection for an owl datatype restriction.  
		so we want this to validate that the facet described is valid.
		 */
	}
}
