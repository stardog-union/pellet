package com.clarkparsia.pellet.utils;

import aterm.AFun;
import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;
import com.clarkparsia.pellet.rules.model.AtomDConstant;
import com.clarkparsia.pellet.rules.model.AtomDObject;
import com.clarkparsia.pellet.rules.model.AtomDVariable;
import com.clarkparsia.pellet.rules.model.AtomIConstant;
import com.clarkparsia.pellet.rules.model.AtomIObject;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;
import com.clarkparsia.pellet.rules.model.ClassAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.DifferentIndividualsAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.model.SameIndividualAtom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.output.ATermBaseVisitor;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: Creates a KnowledgeBase from ATerm axioms.
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
public class OntBuilder
{
	private class DefinitionVisitor extends ATermBaseVisitor
	{
		@Override
		public void visitAll(final ATermAppl term)
		{
			visitQR(term);
		}

		@Override
		public void visitAnd(final ATermAppl term)
		{
			visitList((ATermList) term.getArgument(0));
		}

		@Override
		public void visitCard(final ATermAppl term)
		{
			visitQCR(term);
		}

		@Override
		public void visitHasValue(final ATermAppl term)
		{
			visitQR(term);
		}

		@Override
		public void visitLiteral(final ATermAppl term)
		{
			return;
		}

		@Override
		public void visitMax(final ATermAppl term)
		{
			visitQCR(term);
		}

		@Override
		public void visitMin(final ATermAppl term)
		{
			visitQCR(term);
		}

		@Override
		public void visitNot(final ATermAppl term)
		{
			this.visit((ATermAppl) term.getArgument(0));
		}

		@Override
		public void visitOneOf(final ATermAppl term)
		{
			visitList((ATermList) term.getArgument(0));
		}

		@Override
		public void visitOr(final ATermAppl term)
		{
			visitList((ATermList) term.getArgument(0));
		}

		private void visitQCR(final ATermAppl term)
		{
			final ATermAppl p = (ATermAppl) term.getArgument(0);
			final ATermAppl q = (ATermAppl) term.getArgument(2);

			visitRestr(p, q);
		}

		private void visitQR(final ATermAppl term)
		{
			final ATermAppl p = (ATermAppl) term.getArgument(0);
			final ATermAppl q = (ATermAppl) term.getArgument(1);

			visitRestr(p, q);
		}

		private void visitRestr(final ATermAppl p, final ATermAppl q)
		{
			if (_originalKB.isObjectProperty(p))
			{
				_kb.addObjectProperty(p);
				visit(q);
			}
			else
				_kb.addDatatypeProperty(p);
		}

		@Override
		public void visitSelf(final ATermAppl term)
		{
			_kb.addObjectProperty(term.getArgument(0));
		}

		@Override
		public void visitSome(final ATermAppl term)
		{
			visitQR(term);
		}

		@Override
		public void visitTerm(final ATermAppl term)
		{
			_kb.addClass(term);
		}

		@Override
		public void visitValue(final ATermAppl term)
		{
			final ATermAppl nominal = (ATermAppl) term.getArgument(0);
			if (!ATermUtils.isLiteral(nominal))
				_kb.addIndividual(nominal);
		}

		@Override
		public void visitInverse(final ATermAppl term)
		{
			final ATermAppl p = (ATermAppl) term.getArgument(0);
			if (ATermUtils.isPrimitive(p))
				_kb.addObjectProperty(p);
			else
				visitInverse(p);
		}

		@Override
		public void visitRestrictedDatatype(final ATermAppl dt)
		{
			// do nothing
		}

	}

	private KnowledgeBase _kb;
	private final KnowledgeBase _originalKB;

	private final DefinitionVisitor _defVisitor = new DefinitionVisitor();

	public OntBuilder(final KnowledgeBase originalKB)
	{
		this._originalKB = originalKB;
	}

	public void add(ATermAppl axiom)
	{
		final AFun afun = axiom.getAFun();
		if (afun.equals(ATermUtils.EQCLASSFUN))
		{
			final ATermAppl c1 = (ATermAppl) axiom.getArgument(0);
			final ATermAppl c2 = (ATermAppl) axiom.getArgument(1);

			defineClass(c1);
			defineClass(c2);
			_kb.addEquivalentClass(c1, c2);
		}
		else
			if (afun.equals(ATermUtils.SUBFUN))
			{
				final ATermAppl c1 = (ATermAppl) axiom.getArgument(0);
				final ATermAppl c2 = (ATermAppl) axiom.getArgument(1);

				defineClass(c1);
				defineClass(c2);
				_kb.addSubClass(c1, c2);
			}
			else
				if (afun.equals(ATermUtils.DISJOINTSFUN))
				{
					final ATermList concepts = (ATermList) axiom.getArgument(0);

					for (ATermList l = concepts; !l.isEmpty(); l = l.getNext())
						defineClass((ATermAppl) l.getFirst());
					_kb.addDisjointClasses(concepts);
				}
				else
					if (afun.equals(ATermUtils.DISJOINTFUN))
					{
						final ATermAppl c1 = (ATermAppl) axiom.getArgument(0);
						final ATermAppl c2 = (ATermAppl) axiom.getArgument(1);

						defineClass(c1);
						defineClass(c2);
						_kb.addDisjointClass(c1, c2);
					}
					else
						if (afun.equals(ATermUtils.DISJOINTPROPSFUN))
						{
							final ATermList props = (ATermList) axiom.getArgument(0);

							for (ATermList l = props; !l.isEmpty(); l = l.getNext())
								defineProperty(l.getFirst());
							_kb.addDisjointProperties(props);
						}
						else
							if (afun.equals(ATermUtils.DISJOINTPROPFUN))
							{
								final ATermAppl p1 = (ATermAppl) axiom.getArgument(0);
								final ATermAppl p2 = (ATermAppl) axiom.getArgument(1);

								defineProperty(p1);
								defineProperty(p2);
								_kb.addDisjointProperty(p1, p2);
							}
							else
								if (afun.equals(ATermUtils.SUBPROPFUN))
								{
									final ATerm p1 = axiom.getArgument(0);
									final ATermAppl p2 = (ATermAppl) axiom.getArgument(1);

									defineProperty(p1);
									defineProperty(p2);
									_kb.addSubProperty(p1, p2);
								}
								else
									if (afun.equals(ATermUtils.EQPROPFUN))
									{
										final ATermAppl p1 = (ATermAppl) axiom.getArgument(0);
										final ATermAppl p2 = (ATermAppl) axiom.getArgument(1);

										defineProperty(p1);
										defineProperty(p2);
										_kb.addEquivalentProperty(p1, p2);
									}
									else
										if (afun.equals(ATermUtils.DOMAINFUN))
										{
											final ATermAppl p = (ATermAppl) axiom.getArgument(0);
											final ATermAppl c = (ATermAppl) axiom.getArgument(1);

											defineProperty(p);
											defineClass(c);
											_kb.addDomain(p, c);
										}
										else
											if (afun.equals(ATermUtils.RANGEFUN))
											{
												final ATermAppl p = (ATermAppl) axiom.getArgument(0);
												final ATermAppl c = (ATermAppl) axiom.getArgument(1);

												defineProperty(p);
												defineClass(c);
												_kb.addRange(p, c);
											}
											else
												if (afun.equals(ATermUtils.INVPROPFUN))
												{
													final ATermAppl p1 = (ATermAppl) axiom.getArgument(0);
													final ATermAppl p2 = (ATermAppl) axiom.getArgument(1);

													_kb.addObjectProperty(p1);
													_kb.addObjectProperty(p2);
													_kb.addInverseProperty(p1, p2);
												}
												else
													if (afun.equals(ATermUtils.TRANSITIVEFUN))
													{
														final ATermAppl p = (ATermAppl) axiom.getArgument(0);

														_kb.addObjectProperty(p);
														_kb.addTransitiveProperty(p);
													}
													else
														if (afun.equals(ATermUtils.FUNCTIONALFUN))
														{
															final ATermAppl p = (ATermAppl) axiom.getArgument(0);

															defineProperty(p);
															_kb.addFunctionalProperty(p);
														}
														else
															if (afun.equals(ATermUtils.INVFUNCTIONALFUN))
															{
																final ATermAppl p = (ATermAppl) axiom.getArgument(0);

																_kb.addObjectProperty(p);
																_kb.addInverseFunctionalProperty(p);
															}
															else
																if (afun.equals(ATermUtils.SYMMETRICFUN))
																{
																	final ATermAppl p = (ATermAppl) axiom.getArgument(0);

																	_kb.addObjectProperty(p);
																	_kb.addSymmetricProperty(p);
																}
																else
																	if (afun.equals(ATermUtils.ASYMMETRICFUN))
																	{
																		final ATermAppl p = (ATermAppl) axiom.getArgument(0);

																		_kb.addObjectProperty(p);
																		_kb.addAsymmetricProperty(p);
																	}
																	else
																		if (afun.equals(ATermUtils.REFLEXIVEFUN))
																		{
																			final ATermAppl p = (ATermAppl) axiom.getArgument(0);

																			_kb.addObjectProperty(p);
																			_kb.addReflexiveProperty(p);
																		}
																		else
																			if (afun.equals(ATermUtils.IRREFLEXIVEFUN))
																			{
																				final ATermAppl p = (ATermAppl) axiom.getArgument(0);

																				_kb.addObjectProperty(p);
																				_kb.addIrreflexiveProperty(p);
																			}
																			else
																				if (afun.equals(ATermUtils.TYPEFUN))
																				{
																					final ATermAppl ind = (ATermAppl) axiom.getArgument(0);
																					final ATermAppl cls = (ATermAppl) axiom.getArgument(1);

																					_kb.addIndividual(ind);
																					defineClass(cls);
																					_kb.addType(ind, cls);
																				}
																				else
																					if (afun.equals(ATermUtils.PROPFUN))
																					{
																						final ATermAppl p = (ATermAppl) axiom.getArgument(0);
																						final ATermAppl s = (ATermAppl) axiom.getArgument(1);
																						final ATermAppl o = (ATermAppl) axiom.getArgument(2);

																						_kb.addIndividual(s);
																						if (ATermUtils.isLiteral(o))
																							_kb.addDatatypeProperty(p);
																						else
																						{
																							_kb.addObjectProperty(p);
																							_kb.addIndividual(o);
																						}
																						_kb.addPropertyValue(p, s, o);
																					}
																					else
																						if (afun.equals(ATermUtils.NOTFUN) && ((ATermAppl) axiom.getArgument(0)).getAFun().equals(ATermUtils.PROPFUN))
																						{
																							axiom = (ATermAppl) axiom.getArgument(0);

																							final ATermAppl p = (ATermAppl) axiom.getArgument(0);
																							final ATermAppl s = (ATermAppl) axiom.getArgument(1);
																							final ATermAppl o = (ATermAppl) axiom.getArgument(2);

																							_kb.addIndividual(s);
																							if (ATermUtils.isLiteral(o))
																								_kb.addDatatypeProperty(p);
																							else
																							{
																								_kb.addObjectProperty(p);
																								_kb.addIndividual(o);
																							}
																							_kb.addNegatedPropertyValue(p, s, o);
																						}
																						else
																							if (afun.equals(ATermUtils.SAMEASFUN))
																							{
																								final ATermAppl ind1 = (ATermAppl) axiom.getArgument(0);
																								final ATermAppl ind2 = (ATermAppl) axiom.getArgument(1);

																								_kb.addIndividual(ind1);
																								_kb.addIndividual(ind2);
																								_kb.addSame(ind1, ind2);
																							}
																							else
																								if (afun.equals(ATermUtils.DIFFERENTFUN))
																								{
																									final ATermAppl ind1 = (ATermAppl) axiom.getArgument(0);
																									final ATermAppl ind2 = (ATermAppl) axiom.getArgument(1);

																									_kb.addIndividual(ind1);
																									_kb.addIndividual(ind2);
																									_kb.addDifferent(ind1, ind2);
																								}
																								else
																									if (afun.equals(ATermUtils.ALLDIFFERENTFUN))
																									{
																										final ATermList inds = (ATermList) axiom.getArgument(0);

																										for (ATermList l = inds; !l.isEmpty(); l = l.getNext())
																											_kb.addIndividual((ATermAppl) l.getFirst());
																										_kb.addAllDifferent(inds);
																									}
																									else
																										if (afun.equals(ATermUtils.RULEFUN))
																										{
																											final Set<RuleAtom> antecedent = new HashSet<>(); // Body
																											final Set<RuleAtom> consequent = new HashSet<>(); // Head

																											ATermList head = (ATermList) axiom.getArgument(1);
																											ATermList body = (ATermList) axiom.getArgument(2);

																											for (; !body.isEmpty(); body = body.getNext())
																												antecedent.add(convertRuleAtom((ATermAppl) body.getFirst()));

																											for (; !head.isEmpty(); head = head.getNext())
																												consequent.add(convertRuleAtom((ATermAppl) head.getFirst()));

																											if (!antecedent.contains(null) && !consequent.contains(null))
																											{
																												final ATermAppl name = (ATermAppl) axiom.getArgument(0);
																												final Rule rule = new Rule(name, consequent, antecedent);
																												_kb.addRule(rule);
																											}
																										}
																										else
																											throw new InternalReasonerException("Unknown axiom " + axiom);
	}

	private RuleAtom convertRuleAtom(final ATermAppl term)
	{
		RuleAtom atom = null;

		if (term.getAFun().equals(ATermUtils.TYPEFUN))
		{
			final ATermAppl i = (ATermAppl) term.getArgument(0);
			final AtomIObject io = convertAtomIObject(i);
			final ATermAppl c = (ATermAppl) term.getArgument(1);

			defineClass(c);

			atom = new ClassAtom(c, io);
		}
		else
			if (term.getAFun().equals(ATermUtils.PROPFUN))
			{
				final ATermAppl p = (ATermAppl) term.getArgument(0);
				final ATermAppl i1 = (ATermAppl) term.getArgument(1);
				final ATermAppl i2 = (ATermAppl) term.getArgument(2);
				final AtomIObject io1 = convertAtomIObject(i1);

				defineProperty(p);

				if (_originalKB.isObjectProperty(p))
				{
					_kb.addObjectProperty(p);
					final AtomIObject io2 = convertAtomIObject(i2);
					atom = new IndividualPropertyAtom(p, io1, io2);
				}
				else
					if (_originalKB.isDatatypeProperty(p))
					{
						_kb.addDatatypeProperty(p);
						final AtomDObject do2 = convertAtomDObject(i2);
						atom = new DatavaluedPropertyAtom(p, io1, do2);
					}
					else
						throw new InternalReasonerException("Unknown property " + p);
			}
			else
				if (term.getAFun().equals(ATermUtils.SAMEASFUN))
				{
					final ATermAppl i1 = (ATermAppl) term.getArgument(0);
					final ATermAppl i2 = (ATermAppl) term.getArgument(1);
					final AtomIObject io1 = convertAtomIObject(i1);
					final AtomIObject io2 = convertAtomIObject(i2);

					atom = new SameIndividualAtom(io1, io2);
				}
				else
					if (term.getAFun().equals(ATermUtils.DIFFERENTFUN))
					{
						final ATermAppl i1 = (ATermAppl) term.getArgument(0);
						final ATermAppl i2 = (ATermAppl) term.getArgument(1);
						final AtomIObject io1 = convertAtomIObject(i1);
						final AtomIObject io2 = convertAtomIObject(i2);

						atom = new DifferentIndividualsAtom(io1, io2);
					}
					else
						if (term.getAFun().equals(ATermUtils.BUILTINFUN))
						{
							ATermList args = (ATermList) term.getArgument(0);
							final ATermAppl builtin = (ATermAppl) args.getFirst();
							final List<AtomDObject> list = new ArrayList<>();
							for (args = args.getNext(); !args.isEmpty(); args = args.getNext())
							{
								final ATermAppl arg = (ATermAppl) args.getFirst();
								list.add(convertAtomDObject(arg));
							}

							atom = new BuiltInAtom(builtin.toString(), list);
						}
						else
							throw new InternalReasonerException("Unknown rule atom " + term);

		return atom;
	}

	private AtomIObject convertAtomIObject(final ATermAppl t)
	{
		if (ATermUtils.isVar(t))
			return new AtomIVariable(((ATermAppl) t.getArgument(0)).getName());
		else
			if (_originalKB.isIndividual(t))
				return new AtomIConstant(t);
			else
				if (ATermUtils.isAnon(t))
					return new AtomIConstant(t);

		throw new InternalReasonerException("Unrecognized term: " + t);
	}

	private AtomDObject convertAtomDObject(final ATermAppl t)
	{
		if (ATermUtils.isVar(t))
			return new AtomDVariable(((ATermAppl) t.getArgument(0)).getName());
		else
			if (ATermUtils.isLiteral(t))
				return new AtomDConstant(t);

		throw new InternalReasonerException("Unrecognized term: " + t);
	}

	public KnowledgeBase build(final Set<ATermAppl> axioms)
	{
		reset();

		for (final ATermAppl axiom : axioms)
			add(axiom);

		return _kb;
	}

	private void defineClass(final ATermAppl cls)
	{
		_defVisitor.visit(cls);
	}

	private void defineProperty(final ATerm p)
	{
		if (p instanceof ATermList)
			for (ATermList l = (ATermList) p; !l.isEmpty(); l = l.getNext())
			{
				final ATermAppl r = (ATermAppl) l.getFirst();
				defineProperty(r);
			}
		else
			if (ATermUtils.isInv((ATermAppl) p))
				_kb.addObjectProperty(((ATermAppl) p).getArgument(0));
			else
				if (_originalKB.isDatatypeProperty(p))
					_kb.addDatatypeProperty(p);
				else
					_kb.addObjectProperty(p);
	}

	public void reset()
	{
		_kb = new KnowledgeBase();
	}
}
