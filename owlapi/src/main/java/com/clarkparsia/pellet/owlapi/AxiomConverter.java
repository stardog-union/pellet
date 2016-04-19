// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.owlapi;

import aterm.ATermAppl;
import aterm.ATermList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.SetUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLDArgument;
import org.semanticweb.owlapi.model.SWRLIArgument;

/**
 * <p>
 * Title: AxiomConverter
 * </p>
 * <p>
 * Description: Converts axioms expressed as ATerms to OWL-API structures.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public class AxiomConverter
{
	public static Logger log = Logger.getLogger(AxiomConverter.class.getName());

	private final ConceptConverter conceptConverter;
	private final OWLDataFactory factory;
	private final KnowledgeBase kb;

	public AxiomConverter(final PelletReasoner reasoner)
	{
		this(reasoner.getKB(), reasoner.getManager().getOWLDataFactory());
	}

	public AxiomConverter(final KnowledgeBase kb, final OWLDataFactory factory)
	{
		if (kb == null)
			throw new NullPointerException("KnowledgeBase is null");
		if (factory == null)
			throw new NullPointerException("OWLDataFactory is null");

		this.kb = kb;
		this.factory = factory;
		conceptConverter = new ConceptConverter(kb, factory);
	}

	public OWLAxiom convert(ATermAppl term)
	{
		OWLAxiom axiom = null;

		if (term.getAFun().equals(ATermUtils.EQCLASSFUN))
		{
			final OWLClassExpression c1 = (OWLClassExpression) conceptConverter.convert((ATermAppl) term.getArgument(0));
			final OWLClassExpression c2 = (OWLClassExpression) conceptConverter.convert((ATermAppl) term.getArgument(1));

			final Set<OWLClassExpression> descriptions = new HashSet<>();
			descriptions.add(c1);
			descriptions.add(c2);

			if (c1 != null && c2 != null)
				axiom = factory.getOWLEquivalentClassesAxiom(descriptions);
		}
		else
			if (term.getAFun().equals(ATermUtils.SUBFUN))
			{
				final OWLClassExpression c1 = (OWLClassExpression) conceptConverter.convert((ATermAppl) term.getArgument(0));
				final OWLClassExpression c2 = (OWLClassExpression) conceptConverter.convert((ATermAppl) term.getArgument(1));

				if (c1 != null && c2 != null)
					axiom = factory.getOWLSubClassOfAxiom(c1, c2);
			}
			else
				if (term.getAFun().equals(ATermUtils.DISJOINTSFUN))
				{
					final Set<OWLClassExpression> descriptions = new HashSet<>();

					ATermList concepts = (ATermList) term.getArgument(0);
					for (; !concepts.isEmpty(); concepts = concepts.getNext())
					{
						final ATermAppl concept = (ATermAppl) concepts.getFirst();
						final OWLClassExpression c = (OWLClassExpression) conceptConverter.convert(concept);
						if (c == null)
							break;

						descriptions.add(c);
					}

					// if no error occurred list will be empty
					if (concepts.isEmpty())
						axiom = factory.getOWLDisjointClassesAxiom(descriptions);
				}
				else
					if (term.getAFun().equals(ATermUtils.DISJOINTFUN))
					{
						final OWLClassExpression c1 = (OWLClassExpression) conceptConverter.convert((ATermAppl) term.getArgument(0));
						final OWLClassExpression c2 = (OWLClassExpression) conceptConverter.convert((ATermAppl) term.getArgument(1));

						final Set<OWLClassExpression> descriptions = new HashSet<>();
						descriptions.add(c1);
						descriptions.add(c2);

						if (c1 != null && c2 != null)
							axiom = factory.getOWLDisjointClassesAxiom(descriptions);
					}
					else
						if (term.getAFun().equals(ATermUtils.DISJOINTPROPSFUN))
						{
							final Set<OWLObjectProperty> objProperties = new HashSet<>();
							final Set<OWLDataProperty> dataProperties = new HashSet<>();

							ATermList props = (ATermList) term.getArgument(0);
							for (; !props.isEmpty(); props = props.getNext())
							{
								final OWLObject p = conceptConverter.convert((ATermAppl) term.getArgument(0));
								if (p == null)
									break;
								else
									if (p instanceof OWLObjectProperty)
									{
										if (!dataProperties.isEmpty())
											break;
										else
											objProperties.add((OWLObjectProperty) p);
									}
									else
										if (!objProperties.isEmpty())
											break;
										else
											dataProperties.add((OWLDataProperty) p);
							}

							// if no error occurred list will be empty
							if (props.isEmpty())
								if (!objProperties.isEmpty())
									axiom = factory.getOWLDisjointObjectPropertiesAxiom(objProperties);
								else
									axiom = factory.getOWLDisjointDataPropertiesAxiom(dataProperties);
						}
						else
							if (term.getAFun().equals(ATermUtils.DISJOINTPROPFUN))
							{
								final OWLObject p1 = conceptConverter.convert((ATermAppl) term.getArgument(0));
								final OWLObject p2 = conceptConverter.convert((ATermAppl) term.getArgument(1));

								if (p1 != null && p2 != null)
									if (p1 instanceof OWLObjectProperty && p2 instanceof OWLObjectProperty)
										axiom = factory.getOWLDisjointObjectPropertiesAxiom(SetUtils.create((OWLObjectProperty) p1, (OWLObjectProperty) p2));
									else
										if (p1 instanceof OWLDataProperty && p2 instanceof OWLDataProperty)
											axiom = factory.getOWLDisjointDataPropertiesAxiom(SetUtils.create((OWLDataProperty) p1, (OWLDataProperty) p2));
							}
							else
								if (term.getAFun().equals(ATermUtils.SUBPROPFUN))
								{
									if (term.getArgument(0) instanceof ATermList)
									{
										List<OWLObjectPropertyExpression> subs = new ArrayList<>();
										for (ATermList list = (ATermList) term.getArgument(0); !list.isEmpty(); list = list.getNext())
										{
											final OWLObjectPropertyExpression p = (OWLObjectPropertyExpression) conceptConverter.convert((ATermAppl) list.getFirst());
											if (p == null)
											{
												subs = null;
												break;
											}
											subs.add(p);
										}
										final OWLObjectProperty sup = (OWLObjectProperty) conceptConverter.convert((ATermAppl) term.getArgument(1));

										if (subs != null && sup != null)
											axiom = factory.getOWLSubPropertyChainOfAxiom(subs, sup);
									}
									else
									{
										final OWLObject p1 = conceptConverter.convert((ATermAppl) term.getArgument(0));
										final OWLObject p2 = conceptConverter.convert((ATermAppl) term.getArgument(1));

										if (p1 != null && p2 != null)
											if (p1 instanceof OWLObjectPropertyExpression && p2 instanceof OWLObjectPropertyExpression)
												axiom = factory.getOWLSubObjectPropertyOfAxiom((OWLObjectPropertyExpression) p1, (OWLObjectPropertyExpression) p2);
											else
												if (p1 instanceof OWLDataProperty && p2 instanceof OWLDataProperty)
													axiom = factory.getOWLSubDataPropertyOfAxiom((OWLDataProperty) p1, (OWLDataProperty) p2);
									}
								}
								else
									if (term.getAFun().equals(ATermUtils.EQPROPFUN))
									{
										final OWLObject p1 = conceptConverter.convert((ATermAppl) term.getArgument(0));
										final OWLObject p2 = conceptConverter.convert((ATermAppl) term.getArgument(1));

										if (p1 != null && p2 != null)
											if (p1 instanceof OWLObjectProperty && p2 instanceof OWLObjectProperty)
												axiom = factory.getOWLEquivalentObjectPropertiesAxiom(SetUtils.create((OWLObjectProperty) p1, (OWLObjectProperty) p2));
											else
												if (p1 instanceof OWLDataProperty && p2 instanceof OWLDataProperty)
													axiom = factory.getOWLEquivalentDataPropertiesAxiom(SetUtils.create((OWLDataProperty) p1, (OWLDataProperty) p2));
									}
									else
										if (term.getAFun().equals(ATermUtils.DOMAINFUN))
										{
											final OWLObject p = conceptConverter.convert((ATermAppl) term.getArgument(0));
											final OWLClassExpression c = (OWLClassExpression) conceptConverter.convert((ATermAppl) term.getArgument(1));

											if (c != null && p != null)
												if (p instanceof OWLObjectProperty)
													axiom = factory.getOWLObjectPropertyDomainAxiom((OWLObjectPropertyExpression) p, c);
												else
													axiom = factory.getOWLDataPropertyDomainAxiom((OWLDataPropertyExpression) p, c);
										}
										else
											if (term.getAFun().equals(ATermUtils.RANGEFUN))
											{
												final OWLPropertyRange e = (OWLPropertyRange) conceptConverter.convert((ATermAppl) term.getArgument(1));
												if (e != null)
													if (e instanceof OWLClassExpression)
													{
														final OWLObjectProperty p = (OWLObjectProperty) conceptConverter.convert((ATermAppl) term.getArgument(0));
														if (p != null)
															axiom = factory.getOWLObjectPropertyRangeAxiom(p, (OWLClassExpression) e);
													}
													else
													{
														final OWLDataProperty p = (OWLDataProperty) conceptConverter.convert((ATermAppl) term.getArgument(0));
														if (p != null)
															axiom = factory.getOWLDataPropertyRangeAxiom(p, (OWLDataRange) e);
													}
											}
											else
												if (term.getAFun().equals(ATermUtils.INVPROPFUN))
												{
													final OWLObjectProperty p1 = (OWLObjectProperty) conceptConverter.convert((ATermAppl) term.getArgument(0));
													final OWLObjectProperty p2 = (OWLObjectProperty) conceptConverter.convert((ATermAppl) term.getArgument(1));

													if (p1 != null && p2 != null)
														axiom = factory.getOWLInverseObjectPropertiesAxiom(p1, p2);
												}
												else
													if (term.getAFun().equals(ATermUtils.TRANSITIVEFUN))
													{
														final OWLObjectProperty p = (OWLObjectProperty) conceptConverter.convert((ATermAppl) term.getArgument(0));

														if (p != null)
															axiom = factory.getOWLTransitiveObjectPropertyAxiom(p);
													}
													else
														if (term.getAFun().equals(ATermUtils.FUNCTIONALFUN))
														{
															final OWLObject p = conceptConverter.convert((ATermAppl) term.getArgument(0));

															if (p != null)
																if (p instanceof OWLObjectProperty)
																	axiom = factory.getOWLFunctionalObjectPropertyAxiom((OWLObjectPropertyExpression) p);
																else
																	if (p instanceof OWLDataProperty)
																		axiom = factory.getOWLFunctionalDataPropertyAxiom((OWLDataPropertyExpression) p);
														}
														else
															if (term.getAFun().equals(ATermUtils.INVFUNCTIONALFUN))
															{
																final OWLObjectProperty p = (OWLObjectProperty) conceptConverter.convert((ATermAppl) term.getArgument(0));

																if (p != null)
																	axiom = factory.getOWLInverseFunctionalObjectPropertyAxiom(p);
															}
															else
																if (term.getAFun().equals(ATermUtils.SYMMETRICFUN))
																{
																	final OWLObject p = conceptConverter.convert((ATermAppl) term.getArgument(0));

																	if (p != null && p instanceof OWLObjectPropertyExpression)
																		axiom = factory.getOWLSymmetricObjectPropertyAxiom((OWLObjectPropertyExpression) p);
																}
																else
																	if (term.getAFun().equals(ATermUtils.ASYMMETRICFUN))
																	{
																		final OWLObject p = conceptConverter.convert((ATermAppl) term.getArgument(0));

																		if (p != null && p instanceof OWLObjectPropertyExpression)
																			axiom = factory.getOWLAsymmetricObjectPropertyAxiom((OWLObjectPropertyExpression) p);
																	}
																	else
																		if (term.getAFun().equals(ATermUtils.REFLEXIVEFUN))
																		{
																			final OWLObject p = conceptConverter.convert((ATermAppl) term.getArgument(0));

																			if (p != null && p instanceof OWLObjectPropertyExpression)
																				axiom = factory.getOWLReflexiveObjectPropertyAxiom((OWLObjectPropertyExpression) p);
																		}
																		else
																			if (term.getAFun().equals(ATermUtils.IRREFLEXIVEFUN))
																			{
																				final OWLObject p = conceptConverter.convert((ATermAppl) term.getArgument(0));

																				if (p != null && p instanceof OWLObjectPropertyExpression)
																					axiom = factory.getOWLIrreflexiveObjectPropertyAxiom((OWLObjectPropertyExpression) p);
																			}
																			else
																				if (term.getAFun().equals(ATermUtils.TYPEFUN))
																				{
																					final OWLIndividual i = conceptConverter.convertIndividual((ATermAppl) term.getArgument(0));
																					final OWLClassExpression c = (OWLClassExpression) conceptConverter.convert((ATermAppl) term.getArgument(1));

																					if (i != null && c != null)
																						axiom = factory.getOWLClassAssertionAxiom(c, i);
																				}
																				else
																					if (term.getAFun().equals(ATermUtils.PROPFUN))
																					{
																						final OWLIndividual subj = conceptConverter.convertIndividual((ATermAppl) term.getArgument(1));

																						if (subj == null)
																							axiom = null;
																						else
																							if (ATermUtils.isLiteral((ATermAppl) term.getArgument(2)))
																							{
																								final OWLDataProperty pred = (OWLDataProperty) conceptConverter.convert((ATermAppl) term.getArgument(0));
																								final OWLLiteral obj = (OWLLiteral) conceptConverter.convert((ATermAppl) term.getArgument(2));
																								if (pred != null && obj != null)
																									axiom = factory.getOWLDataPropertyAssertionAxiom(pred, subj, obj);
																							}
																							else
																							{
																								final OWLObjectProperty pred = (OWLObjectProperty) conceptConverter.convert((ATermAppl) term.getArgument(0));
																								final OWLIndividual obj = conceptConverter.convertIndividual((ATermAppl) term.getArgument(2));
																								if (pred != null && obj != null)
																									axiom = factory.getOWLObjectPropertyAssertionAxiom(pred, subj, obj);
																							}
																					}
																					else
																						if (term.getAFun().equals(ATermUtils.NOTFUN) && ((ATermAppl) term.getArgument(0)).getAFun().equals(ATermUtils.PROPFUN))
																						{
																							term = (ATermAppl) term.getArgument(0);
																							final OWLIndividual subj = conceptConverter.convertIndividual((ATermAppl) term.getArgument(1));

																							if (subj == null)
																								axiom = null;
																							else
																								if (ATermUtils.isLiteral((ATermAppl) term.getArgument(2)))
																								{
																									final OWLDataProperty pred = (OWLDataProperty) conceptConverter.convert((ATermAppl) term.getArgument(0));
																									final OWLLiteral obj = (OWLLiteral) conceptConverter.convert((ATermAppl) term.getArgument(2));
																									if (pred != null && obj != null)
																										axiom = factory.getOWLNegativeDataPropertyAssertionAxiom(pred, subj, obj);
																								}
																								else
																								{
																									final OWLObjectProperty pred = (OWLObjectProperty) conceptConverter.convert((ATermAppl) term.getArgument(0));
																									final OWLIndividual obj = conceptConverter.convertIndividual((ATermAppl) term.getArgument(2));
																									if (pred != null && obj != null)
																										axiom = factory.getOWLNegativeObjectPropertyAssertionAxiom(pred, subj, obj);
																								}
																						}
																						else
																							if (term.getAFun().equals(ATermUtils.SAMEASFUN))
																							{
																								final OWLIndividual ind1 = conceptConverter.convertIndividual((ATermAppl) term.getArgument(0));
																								final OWLIndividual ind2 = conceptConverter.convertIndividual((ATermAppl) term.getArgument(1));

																								final Set<OWLIndividual> inds = new HashSet<>();
																								inds.add(ind1);
																								inds.add(ind2);

																								if (ind1 != null && ind2 != null)
																									axiom = factory.getOWLSameIndividualAxiom(inds);
																							}
																							else
																								if (term.getAFun().equals(ATermUtils.DIFFERENTFUN))
																								{
																									final OWLIndividual ind1 = conceptConverter.convertIndividual((ATermAppl) term.getArgument(0));
																									final OWLIndividual ind2 = conceptConverter.convertIndividual((ATermAppl) term.getArgument(1));

																									final Set<OWLIndividual> inds = new HashSet<>();
																									inds.add(ind1);
																									inds.add(ind2);

																									if (ind1 != null && ind2 != null)
																										axiom = factory.getOWLDifferentIndividualsAxiom(inds);
																								}
																								else
																									if (term.getAFun().equals(ATermUtils.ALLDIFFERENTFUN))
																									{
																										final Set<OWLIndividual> individuals = new HashSet<>();

																										ATermList list = (ATermList) term.getArgument(0);
																										for (; !list.isEmpty(); list = list.getNext())
																										{
																											final ATermAppl ind = (ATermAppl) list.getFirst();
																											final OWLIndividual i = conceptConverter.convertIndividual(ind);
																											if (i == null)
																												break;

																											individuals.add(i);
																										}

																										// if no error occurred list will be empty
																										if (list.isEmpty())
																											axiom = factory.getOWLDifferentIndividualsAxiom(individuals);
																									}
																									else
																										if (term.getAFun().equals(ATermUtils.RULEFUN))
																										{
																											final Set<SWRLAtom> antecedent = new HashSet<>(); // Body
																											final Set<SWRLAtom> consequent = new HashSet<>(); // Head

																											ATermList head = (ATermList) term.getArgument(1);
																											ATermList body = (ATermList) term.getArgument(2);

																											for (; !body.isEmpty(); body = body.getNext())
																												antecedent.add(parseToSWRLAtom((ATermAppl) body.getFirst()));

																											for (; !head.isEmpty(); head = head.getNext())
																												consequent.add(parseToSWRLAtom((ATermAppl) head.getFirst()));

																											if (!antecedent.contains(null) && !consequent.contains(null))
																											{
																												final ATermAppl name = (ATermAppl) term.getArgument(0);
																												if (name == ATermUtils.EMPTY)
																													axiom = factory.getSWRLRule(antecedent, consequent);
																												else
																													if (ATermUtils.isBnode(name))
																														axiom = factory.getSWRLRule(antecedent, consequent);
																													else
																														axiom = factory.getSWRLRule(antecedent, consequent);
																											}
																										}
																										else
																											if (term.getAFun().equals(ATermUtils.DATATYPEDEFFUN))
																											{
																												final OWLDatatype d1 = (OWLDatatype) conceptConverter.convert((ATermAppl) term.getArgument(0));
																												final OWLDataRange d2 = (OWLDataRange) conceptConverter.convert((ATermAppl) term.getArgument(1));

																												if (d1 != null && d2 != null)
																													axiom = factory.getOWLDatatypeDefinitionAxiom(d1, d2);
																											}

		if (axiom == null)
			log.warning("Cannot convert to OWLAPI: " + term);

		return axiom;
	}

	private SWRLAtom parseToSWRLAtom(final ATermAppl term)
	{
		SWRLAtom atom = null;

		if (term.getAFun().equals(ATermUtils.TYPEFUN))
		{
			final ATermAppl i = (ATermAppl) term.getArgument(0);
			final OWLObject type = conceptConverter.convert((ATermAppl) term.getArgument(1));

			if (type instanceof OWLClassExpression)
			{
				final SWRLIArgument io = parseToAtomIObject(i);
				atom = factory.getSWRLClassAtom((OWLClassExpression) type, io);
			}
			else
				if (type instanceof OWLDataRange)
				{
					final SWRLDArgument io = parseToAtomDObject(i);
					atom = factory.getSWRLDataRangeAtom((OWLDataRange) type, io);
				}
				else
					throw new InternalReasonerException("Cannot convert to SWRL atom: " + ATermUtils.toString(term));
		}
		else
			if (term.getAFun().equals(ATermUtils.PROPFUN))
			{
				final ATermAppl p = (ATermAppl) term.getArgument(0);
				final ATermAppl i1 = (ATermAppl) term.getArgument(1);
				final ATermAppl i2 = (ATermAppl) term.getArgument(2);
				final SWRLIArgument io1 = parseToAtomIObject(i1);

				if (kb.isObjectProperty(p))
				{
					final SWRLIArgument io2 = parseToAtomIObject(i2);
					final OWLObjectProperty op = factory.getOWLObjectProperty(IRI.create(p.getName()));
					atom = factory.getSWRLObjectPropertyAtom(op, io1, io2);
				}
				else
					if (kb.isDatatypeProperty(p))
					{
						final SWRLDArgument do2 = parseToAtomDObject(i2);
						final OWLDataProperty dp = factory.getOWLDataProperty(IRI.create(p.getName()));
						atom = factory.getSWRLDataPropertyAtom(dp, io1, do2);
					}
			}
			else
				if (term.getAFun().equals(ATermUtils.SAMEASFUN))
				{
					final ATermAppl i1 = (ATermAppl) term.getArgument(0);
					final ATermAppl i2 = (ATermAppl) term.getArgument(1);
					final SWRLIArgument io1 = parseToAtomIObject(i1);
					final SWRLIArgument io2 = parseToAtomIObject(i2);

					atom = factory.getSWRLSameIndividualAtom(io1, io2);
				}
				else
					if (term.getAFun().equals(ATermUtils.DIFFERENTFUN))
					{
						final ATermAppl i1 = (ATermAppl) term.getArgument(0);
						final ATermAppl i2 = (ATermAppl) term.getArgument(1);
						final SWRLIArgument io1 = parseToAtomIObject(i1);
						final SWRLIArgument io2 = parseToAtomIObject(i2);

						atom = factory.getSWRLDifferentIndividualsAtom(io1, io2);
					}
					else
						if (term.getAFun().equals(ATermUtils.BUILTINFUN))
						{
							ATermList args = (ATermList) term.getArgument(0);
							final ATermAppl builtin = (ATermAppl) args.getFirst();
							final List<SWRLDArgument> list = new ArrayList<>();
							for (args = args.getNext(); !args.isEmpty(); args = args.getNext())
							{
								final ATermAppl arg = (ATermAppl) args.getFirst();
								list.add(parseToAtomDObject(arg));
							}
							atom = factory.getSWRLBuiltInAtom(IRI.create(builtin.getName()), list);
						}

		if (atom == null)
			log.warning("Cannot convert to SWRLAtom: " + term);

		return atom;
	}

	private SWRLIArgument parseToAtomIObject(final ATermAppl t)
	{
		if (ATermUtils.isVar(t))
			return factory.getSWRLVariable(IRI.create(((ATermAppl) t.getArgument(0)).getName()));
		if (kb.isIndividual(t))
			return factory.getSWRLIndividualArgument(conceptConverter.convertIndividual(t));

		throw new InternalReasonerException("Unrecognized term: " + t);
	}

	private SWRLDArgument parseToAtomDObject(final ATermAppl t)
	{
		if (ATermUtils.isVar(t))
			return factory.getSWRLVariable(IRI.create(((ATermAppl) t.getArgument(0)).getName()));
		else
			if (ATermUtils.isLiteral(t))
				return factory.getSWRLLiteralArgument((OWLLiteral) conceptConverter.convert(t));

		throw new InternalReasonerException("Unrecognized term: " + t);
	}
}
