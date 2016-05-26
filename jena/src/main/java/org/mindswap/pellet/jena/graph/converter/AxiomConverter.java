package org.mindswap.pellet.jena.graph.converter;

import aterm.ATermAppl;
import aterm.ATermList;
import org.apache.jena.graph.BlankNodeId;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.JenaUtils;
import org.mindswap.pellet.jena.vocabulary.OWL2;
import org.mindswap.pellet.jena.vocabulary.SWRL;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: Converts axioms from ATerms to Jena triples.
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
	private final KnowledgeBase _kb;
	private final ConceptConverter _converter;
	private final Graph _graph;

	public AxiomConverter(final KnowledgeBase kb, final Graph g)
	{
		this._kb = kb;
		_graph = g;
		_converter = new ConceptConverter(_graph);
	}

	public void convert(ATermAppl axiomParam)
	{
		ATermAppl axiom = axiomParam;

		if (axiom.getAFun().equals(ATermUtils.EQCLASSFUN))
			convertBinary(axiom, OWL.equivalentClass);
		else
			if (axiom.getAFun().equals(ATermUtils.SUBFUN))
				convertBinary(axiom, RDFS.subClassOf);
			else
				if (axiom.getAFun().equals(ATermUtils.DISJOINTFUN))
					convertBinary(axiom, OWL.disjointWith);
				else
					if (axiom.getAFun().equals(ATermUtils.DISJOINTSFUN))
						convertNary(axiom, OWL2.AllDisjointClasses, OWL2.members);
					else
						if (axiom.getAFun().equals(ATermUtils.EQPROPFUN))
							convertBinary(axiom, OWL.equivalentProperty);
						else
							if (axiom.getAFun().equals(ATermUtils.SUBPROPFUN))
							{
								if (axiom.getArgument(0) instanceof ATermList)
								{
									final Node s = _converter.convert(axiom.getArgument(1));
									final Node o = _converter.convert(axiom.getArgument(0));

									TripleAdder.add(_graph, s, OWL2.propertyChainAxiom, o);
								}
								else
									convertBinary(axiom, RDFS.subPropertyOf);
							}
							else
								if (axiom.getAFun().equals(ATermUtils.DISJOINTPROPFUN))
									convertBinary(axiom, OWL2.propertyDisjointWith);
								else
									if (axiom.getAFun().equals(ATermUtils.DISJOINTPROPSFUN))
										convertNary(axiom, OWL2.AllDisjointProperties, OWL2.members);
									else
										if (axiom.getAFun().equals(ATermUtils.DOMAINFUN))
											convertBinary(axiom, RDFS.domain);
										else
											if (axiom.getAFun().equals(ATermUtils.RANGEFUN))
												convertBinary(axiom, RDFS.range);
											else
												if (axiom.getAFun().equals(ATermUtils.INVPROPFUN))
													convertBinary(axiom, OWL.inverseOf);
												else
													if (axiom.getAFun().equals(ATermUtils.TRANSITIVEFUN))
														convertUnary(axiom, OWL.TransitiveProperty);
													else
														if (axiom.getAFun().equals(ATermUtils.FUNCTIONALFUN))
															convertUnary(axiom, OWL.FunctionalProperty);
														else
															if (axiom.getAFun().equals(ATermUtils.INVFUNCTIONALFUN))
																convertUnary(axiom, OWL.InverseFunctionalProperty);
															else
																if (axiom.getAFun().equals(ATermUtils.SYMMETRICFUN))
																	convertUnary(axiom, OWL.SymmetricProperty);
																else
																	if (axiom.getAFun().equals(ATermUtils.ASYMMETRICFUN))
																		convertUnary(axiom, OWL2.AsymmetricProperty);
																	else
																		if (axiom.getAFun().equals(ATermUtils.REFLEXIVEFUN))
																			convertUnary(axiom, OWL2.ReflexiveProperty);
																		else
																			if (axiom.getAFun().equals(ATermUtils.IRREFLEXIVEFUN))
																				convertUnary(axiom, OWL2.IrreflexiveProperty);
																			else
																				if (axiom.getAFun().equals(ATermUtils.TYPEFUN))
																					convertBinary(axiom, RDF.type);
																				else
																					if (axiom.getAFun().equals(ATermUtils.SAMEASFUN))
																						convertBinary(axiom, OWL.sameAs);
																					else
																						if (axiom.getAFun().equals(ATermUtils.DIFFERENTFUN))
																							convertBinary(axiom, OWL.differentFrom);
																						else
																							if (axiom.getAFun().equals(ATermUtils.ALLDIFFERENTFUN))
																								convertNary(axiom, OWL.AllDifferent, OWL2.members);
																							else
																								if (axiom.getAFun().equals(ATermUtils.NOTFUN))
																								{
																									axiom = (ATermAppl) axiom.getArgument(0);

																									final Node p = _converter.convert(axiom.getArgument(0));
																									final Node s = _converter.convert(axiom.getArgument(1));
																									final Node o = _converter.convert(axiom.getArgument(2));

																									final Node n = NodeFactory.createBlankNode();
																									TripleAdder.add(_graph, n, RDF.type, OWL2.NegativePropertyAssertion);
																									TripleAdder.add(_graph, n, RDF.subject, s);
																									TripleAdder.add(_graph, n, RDF.predicate, p);
																									TripleAdder.add(_graph, n, RDF.object, o);
																								}
																								else
																									if (axiom.getAFun().equals(ATermUtils.PROPFUN))
																									{
																										final Node p = _converter.convert(axiom.getArgument(0));
																										final Node s = _converter.convert(axiom.getArgument(1));
																										final Node o = _converter.convert(axiom.getArgument(2));

																										TripleAdder.add(_graph, s, p, o);
																									}
																									else
																										if (axiom.getAFun().equals(ATermUtils.RULEFUN))
																										{
																											Node node = null;

																											final ATermAppl name = (ATermAppl) axiom.getArgument(0);
																											if (name == ATermUtils.EMPTY)
																												node = NodeFactory.createBlankNode();
																											else
																												if (ATermUtils.isBnode(name))
																													node = NodeFactory.createBlankNode(new BlankNodeId(((ATermAppl) name.getArgument(0)).getName()));
																												else
																													node = NodeFactory.createURI(name.getName());

																											TripleAdder.add(_graph, node, RDF.type, SWRL.Imp);

																											ATermList head = (ATermList) axiom.getArgument(1);
																											if (head.isEmpty())
																												TripleAdder.add(_graph, node, SWRL.head, RDF.nil);
																											else
																											{
																												Node list = null;
																												for (; !head.isEmpty(); head = head.getNext())
																												{
																													final Node atomNode = convertAtom((ATermAppl) head.getFirst());
																													final Node newList = NodeFactory.createBlankNode();
																													TripleAdder.add(_graph, newList, RDF.type, SWRL.AtomList);
																													TripleAdder.add(_graph, newList, RDF.first, atomNode);
																													if (list != null)
																														TripleAdder.add(_graph, list, RDF.rest, newList);
																													else
																														TripleAdder.add(_graph, node, SWRL.head, newList);
																													list = newList;
																												}
																												TripleAdder.add(_graph, list, RDF.rest, RDF.nil);
																											}

																											ATermList body = (ATermList) axiom.getArgument(2);
																											if (body.isEmpty())
																												TripleAdder.add(_graph, node, SWRL.body, RDF.nil);
																											else
																											{
																												Node list = null;
																												for (; !body.isEmpty(); body = body.getNext())
																												{
																													final Node atomNode = convertAtom((ATermAppl) body.getFirst());
																													final Node newList = NodeFactory.createBlankNode();
																													TripleAdder.add(_graph, newList, RDF.type, SWRL.AtomList);
																													TripleAdder.add(_graph, newList, RDF.first, atomNode);
																													if (list != null)
																														TripleAdder.add(_graph, list, RDF.rest, newList);
																													else
																														TripleAdder.add(_graph, node, SWRL.body, newList);
																													list = newList;
																												}
																												TripleAdder.add(_graph, list, RDF.rest, RDF.nil);
																											}
																										}
	}

	private Node convertAtom(final ATermAppl term)
	{
		final Node atom = NodeFactory.createBlankNode();

		if (term.getAFun().equals(ATermUtils.TYPEFUN))
		{
			final ATermAppl ind = (ATermAppl) term.getArgument(0);
			final ATermAppl cls = (ATermAppl) term.getArgument(1);

			final Node indNode = convertAtomObject(ind);
			final Node clsNode = _converter.convert(cls);

			TripleAdder.add(_graph, atom, RDF.type, SWRL.ClassAtom);
			TripleAdder.add(_graph, atom, SWRL.classPredicate, clsNode);
			TripleAdder.add(_graph, atom, SWRL.argument1, indNode);
		}
		else
			if (term.getAFun().equals(ATermUtils.PROPFUN))
			{
				final ATermAppl prop = (ATermAppl) term.getArgument(0);
				final ATermAppl arg1 = (ATermAppl) term.getArgument(1);
				final ATermAppl arg2 = (ATermAppl) term.getArgument(2);

				final Node propNode = JenaUtils.makeGraphNode(prop);
				final Node node1 = convertAtomObject(arg1);
				final Node node2 = convertAtomObject(arg2);

				if (_kb.isObjectProperty(prop))
					TripleAdder.add(_graph, atom, RDF.type, SWRL.IndividualPropertyAtom);
				else
					if (_kb.isDatatypeProperty(prop))
						TripleAdder.add(_graph, atom, RDF.type, SWRL.DatavaluedPropertyAtom);
					else
						throw new UnsupportedOperationException("Unknown property: " + prop);

				TripleAdder.add(_graph, atom, SWRL.propertyPredicate, propNode);
				TripleAdder.add(_graph, atom, SWRL.argument1, node1);
				TripleAdder.add(_graph, atom, SWRL.argument2, node2);

			}
			else
				if (term.getAFun().equals(ATermUtils.SAMEASFUN))
				{
					final ATermAppl arg1 = (ATermAppl) term.getArgument(1);
					final ATermAppl arg2 = (ATermAppl) term.getArgument(2);

					final Node node1 = convertAtomObject(arg1);
					final Node node2 = convertAtomObject(arg2);

					TripleAdder.add(_graph, atom, RDF.type, SWRL.SameIndividualAtom);
					TripleAdder.add(_graph, atom, SWRL.argument1, node1);
					TripleAdder.add(_graph, atom, SWRL.argument2, node2);
				}
				else
					if (term.getAFun().equals(ATermUtils.DIFFERENTFUN))
					{
						final ATermAppl arg1 = (ATermAppl) term.getArgument(1);
						final ATermAppl arg2 = (ATermAppl) term.getArgument(2);

						final Node node1 = convertAtomObject(arg1);
						final Node node2 = convertAtomObject(arg2);

						TripleAdder.add(_graph, atom, RDF.type, SWRL.DifferentIndividualsAtom);
						TripleAdder.add(_graph, atom, SWRL.argument1, node1);
						TripleAdder.add(_graph, atom, SWRL.argument2, node2);
					}
					else
						if (term.getAFun().equals(ATermUtils.BUILTINFUN))
						{
							ATermList args = (ATermList) term.getArgument(0);
							final ATermAppl builtin = (ATermAppl) args.getFirst();
							args = args.getNext();

							TripleAdder.add(_graph, atom, RDF.type, SWRL.BuiltinAtom);
							TripleAdder.add(_graph, atom, SWRL.builtin, NodeFactory.createURI(builtin.toString()));

							if (args.isEmpty())
								TripleAdder.add(_graph, atom, SWRL.arguments, RDF.nil);
							else
							{
								Node list = null;
								for (; !args.isEmpty(); args = args.getNext())
								{
									final Node atomNode = convertAtomObject((ATermAppl) args.getFirst());
									final Node newList = NodeFactory.createBlankNode();
									TripleAdder.add(_graph, newList, RDF.first, atomNode);
									if (list != null)
										TripleAdder.add(_graph, list, RDF.rest, newList);
									else
										TripleAdder.add(_graph, atom, SWRL.arguments, newList);
									list = newList;
								}
								TripleAdder.add(_graph, list, RDF.rest, RDF.nil);
							}
						}
						else
							throw new UnsupportedOperationException("Unsupported atom: " + atom);

		return atom;
	}

	private Node convertAtomObject(final ATermAppl t)
	{
		Node node;
		if (ATermUtils.isVar(t))
		{
			node = JenaUtils.makeGraphNode((ATermAppl) t.getArgument(0));
			TripleAdder.add(_graph, node, RDF.type, SWRL.Variable);
		}
		else
			node = JenaUtils.makeGraphNode(t);

		return node;
	}

	private void convertNary(final ATermAppl axiom, final Resource type, final Property p)
	{
		final Node n = NodeFactory.createBlankNode();
		TripleAdder.add(_graph, n, RDF.type, type);

		final ATermList concepts = (ATermList) axiom.getArgument(0);
		_converter.visitList(concepts);

		TripleAdder.add(_graph, n, p, _converter.getResult());
	}

	private void convertBinary(final ATermAppl axiom, final Property p)
	{
		final Node s = _converter.convert(axiom.getArgument(0));
		final Node o = _converter.convert(axiom.getArgument(1));

		TripleAdder.add(_graph, s, p, o);
	}

	private void convertUnary(final ATermAppl axiom, final Resource o)
	{
		final Node s = _converter.convert(axiom.getArgument(0));

		TripleAdder.add(_graph, s, RDF.type.asNode(), o.asNode());
	}

}
