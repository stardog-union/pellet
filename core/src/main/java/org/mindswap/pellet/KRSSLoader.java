// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC.
// <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms
// of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under
// the terms of the MIT License.
//
// The MIT License
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package org.mindswap.pellet;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;
import com.clarkparsia.pellet.datatypes.Facet;
import com.clarkparsia.pellet.datatypes.types.real.XSDInteger;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.SetUtils;

/**
 * Parse files written in KRSS format and loads into the given KB.
 *
 * @author Evren Sirin
 */
public class KRSSLoader extends KBLoader
{
	public final static Logger log = Logger.getLogger(KRSSLoader.class.getName());

	private final static ATermAppl XSD_INTEGER = XSDInteger.getInstance().getName();

	private StreamTokenizer in;

	private final KnowledgeBase kb;

	private ArrayList<ATermAppl> terms;

	private Map<ATermAppl, List<ATermAppl>> disjoints;

	private boolean forceUppercase;

	private static final int QUOTE = '|';

	public KRSSLoader()
	{
		this(new KnowledgeBase());
	}

	public KRSSLoader(final KnowledgeBase kb)
	{
		this.kb = kb;

		forceUppercase = false;
	}

	@Override
	public void clear()
	{
		kb.clear();
	}

	public boolean isForceUppercase()
	{
		return forceUppercase;
	}

	public void setForceUppercase(final boolean forceUppercase)
	{
		this.forceUppercase = forceUppercase;
	}

	private void initTokenizer(final Reader reader)
	{
		in = new StreamTokenizer(reader);
		in.lowerCaseMode(false);
		in.commentChar(';');
		in.wordChars('/', '/');
		in.wordChars('_', '_');
		in.wordChars('*', '*');
		in.wordChars('?', '?');
		in.wordChars('%', '%');
		in.wordChars('>', '>');
		in.wordChars('<', '<');
		in.wordChars('=', '=');
		in.quoteChar(QUOTE);
	}

	private void skipNext() throws IOException
	{
		in.nextToken();
	}

	private void skipNext(final int token) throws IOException
	{
		ATermUtils.assertTrue(token == in.nextToken());
	}

	private void skipNext(final String token) throws IOException
	{
		in.nextToken();
		ATermUtils.assertTrue(token.equals(in.sval));
	}

	private boolean peekNext(final int token) throws IOException
	{
		final int next = in.nextToken();
		in.pushBack();
		return (token == next);
	}

	private String nextString() throws IOException
	{
		in.nextToken();

		switch (in.ttype)
		{
			case StreamTokenizer.TT_WORD:
			case QUOTE:
				return in.sval;
			case StreamTokenizer.TT_NUMBER:
				return String.valueOf(in.nval);
			default:
				throw new RuntimeException("Expecting string found " + (char) in.ttype);
		}
	}

	private int nextInt() throws IOException
	{
		in.nextToken();

		return (int) in.nval;
	}

	private String nextNumber() throws IOException
	{
		in.nextToken();

		final String strVal = String.valueOf((long) in.nval);
		return strVal;
	}

	private ATermAppl nextTerm() throws IOException
	{
		String token = nextString();
		if (forceUppercase)
			token = token.toUpperCase();
		return ATermUtils.makeTermAppl(token);
	}

	private ATermAppl[] parseExprList() throws IOException
	{
		int count = 0;
		while (peekNext('('))
		{
			skipNext();
			count++;
		}

		final List<ATermAppl> terms = new ArrayList<>();
		while (true)
			if (peekNext(')'))
			{
				if (count == 0)
					break;
				skipNext();
				count--;
				if (count == 0)
					break;
			}
			else
				if (peekNext('('))
				{
					skipNext();
					count++;
				}
				else
					terms.add(parseExpr());

		return terms.toArray(new ATermAppl[terms.size()]);
	}

	private ATermAppl parseExpr() throws IOException
	{
		ATermAppl a = null;

		int token = in.nextToken();
		String s = in.sval;
		if (token == StreamTokenizer.TT_WORD || token == QUOTE)
		{
			if (s.equalsIgnoreCase("TOP") || s.equalsIgnoreCase("*TOP*") || s.equalsIgnoreCase(":TOP"))
				a = ATermUtils.TOP;
			else
				if (s.equalsIgnoreCase("BOTTOM") || s.equalsIgnoreCase("*BOTTOM*"))
					a = ATermUtils.BOTTOM;
				else
				{
					if (forceUppercase)
						s = s.toUpperCase();
					a = ATermUtils.makeTermAppl(s);
				}
		}
		else
			if (token == StreamTokenizer.TT_NUMBER)
				a = ATermUtils.makeTermAppl(String.valueOf(in.nval));
			else
				if (token == ':')
				{
					s = nextString();
					if (s.equalsIgnoreCase("TOP"))
						a = ATermUtils.TOP;
					else
						if (s.equalsIgnoreCase("BOTTOM"))
							a = ATermUtils.BOTTOM;
						else
							throw new RuntimeException("Parse exception after ':' " + s);
				}
				else
					if (token == '(')
					{
						token = in.nextToken();
						ATermUtils.assertTrue(token == StreamTokenizer.TT_WORD);

						s = in.sval;
						if (s.equalsIgnoreCase("NOT"))
						{
							final ATermAppl c = parseExpr();
							a = ATermUtils.makeNot(c);

							if (ATermUtils.isPrimitive(c))
								kb.addClass(c);
						}
						else
							if (s.equalsIgnoreCase("AND"))
							{
								ATermList list = ATermUtils.EMPTY_LIST;

								while (!peekNext(')'))
								{
									final ATermAppl c = parseExpr();

									if (ATermUtils.isPrimitive(c))
										kb.addClass(c);
									list = list.insert(c);
								}
								a = ATermUtils.makeAnd(list);
							}
							else
								if (s.equalsIgnoreCase("OR"))
								{
									ATermList list = ATermUtils.EMPTY_LIST;

									while (!peekNext(')'))
									{
										final ATermAppl c = parseExpr();

										if (ATermUtils.isPrimitive(c))
											kb.addClass(c);
										list = list.insert(c);
									}
									a = ATermUtils.makeOr(list);
								}
								else
									if (s.equalsIgnoreCase("ONE-OF"))
									{
										ATermList list = ATermUtils.EMPTY_LIST;

										while (!peekNext(')'))
										{
											final ATermAppl c = parseExpr();

											kb.addIndividual(c);
											list = list.insert(ATermUtils.makeValue(c));
										}
										a = ATermUtils.makeOr(list);
									}
									else
										if (s.equalsIgnoreCase("ALL"))
										{
											final ATermAppl r = parseExpr();
											kb.addObjectProperty(r);
											final ATermAppl c = parseExpr();
											if (ATermUtils.isPrimitive(c))
												kb.addClass(c);

											a = ATermUtils.makeAllValues(r, c);
										}
										else
											if (s.equalsIgnoreCase("SOME"))
											{
												final ATermAppl r = parseExpr();
												kb.addObjectProperty(r);
												final ATermAppl c = parseExpr();
												if (ATermUtils.isPrimitive(c))
													kb.addClass(c);
												a = ATermUtils.makeSomeValues(r, c);
											}
											else
												if (s.equalsIgnoreCase("AT-LEAST") || s.equalsIgnoreCase("ATLEAST"))
												{
													final int n = nextInt();
													final ATermAppl r = parseExpr();
													kb.addObjectProperty(r);

													ATermAppl c = ATermUtils.TOP;
													if (!peekNext(')'))
														c = parseExpr();

													a = ATermUtils.makeMin(r, n, c);
												}
												else
													if (s.equalsIgnoreCase("AT-MOST") || s.equalsIgnoreCase("ATMOST"))
													{
														final int n = nextInt();
														final ATermAppl r = parseExpr();
														kb.addObjectProperty(r);

														ATermAppl c = ATermUtils.TOP;
														if (!peekNext(')'))
															c = parseExpr();

														a = ATermUtils.makeMax(r, n, c);
													}
													else
														if (s.equalsIgnoreCase("EXACTLY"))
														{
															final int n = nextInt();
															final ATermAppl r = parseExpr();
															kb.addObjectProperty(r);

															ATermAppl c = ATermUtils.TOP;
															if (!peekNext(')'))
																c = parseExpr();

															a = ATermUtils.makeCard(r, n, c);
														}
														else
															if (s.equalsIgnoreCase("A"))
															{
																final ATermAppl r = nextTerm();
																// TODO what does term 'A' stand for
																kb.addDatatypeProperty(r);
																kb.addFunctionalProperty(r);
																a = ATermUtils.makeMin(r, 1, ATermUtils.TOP_LIT);
															}
															else
																if (s.equalsIgnoreCase("MIN") || s.equals(">="))
																{
																	final ATermAppl r = nextTerm();
																	kb.addDatatypeProperty(r);
																	final String val = nextNumber();
																	final ATermAppl dr = ATermUtils.makeRestrictedDatatype(XSD_INTEGER, new ATermAppl[] { ATermUtils.makeFacetRestriction(Facet.XSD.MIN_INCLUSIVE.getName(), ATermUtils.makeTypedLiteral(val, XSD_INTEGER)) });
																	a = ATermUtils.makeAllValues(r, dr);
																}
																else
																	if (s.equalsIgnoreCase("MAX") || s.equals("<="))
																	{
																		final ATermAppl r = nextTerm();
																		kb.addDatatypeProperty(r);
																		final String val = nextNumber();
																		final ATermAppl dr = ATermUtils.makeRestrictedDatatype(XSD_INTEGER, new ATermAppl[] { ATermUtils.makeFacetRestriction(Facet.XSD.MAX_INCLUSIVE.getName(), ATermUtils.makeTypedLiteral(val, XSD_INTEGER)) });
																		a = ATermUtils.makeAllValues(r, dr);
																	}
																	else
																		if (s.equals("="))
																		{
																			final ATermAppl r = nextTerm();
																			kb.addDatatypeProperty(r);
																			final String val = nextNumber();
																			final ATermAppl dr = ATermUtils.makeOr(ATermUtils.makeList(ATermUtils.makeValue(ATermUtils.makeTypedLiteral(val, XSD_INTEGER))));
																			a = ATermUtils.makeAllValues(r, dr);
																		}
																		else
																			if (s.equalsIgnoreCase("INV"))
																			{
																				final ATermAppl r = parseExpr();
																				kb.addObjectProperty(r);
																				a = kb.getProperty(r).getInverse().getName();
																			}
																			else
																				throw new RuntimeException("Unknown expression " + s);

						if (in.nextToken() != ')')
							// if( s.equalsIgnoreCase( "AT-LEAST" ) || s.equalsIgnoreCase(
							// "AT-MOST" )
							// || s.equalsIgnoreCase( "ATLEAST" ) || s.equalsIgnoreCase(
							// "ATMOST" ) ) {
							// s = nextString();
							// if( s.equalsIgnoreCase( "TOP" ) || s.equalsIgnoreCase(
							// "*TOP*" )
							// || s.equalsIgnoreCase( ":TOP" ) )
							// skipNext( ')' );
							// else
							// throw new UnsupportedFeatureException( "Qualified cardinality
							// restrictions" );
							// }
							// else
							throw new RuntimeException("Parse exception at term " + s);
					}
					else
						if (token == '#')
						{
							final int n = nextInt();
							if (peekNext('#'))
							{
								skipNext();
								a = terms.get(n);
								if (a == null)
									throw new RuntimeException("Parse exception: #" + n + "# is not defined");
							}
							else
							{
								skipNext("=");
								a = parseExpr();

								while (terms.size() <= n)
									terms.add(null);

								terms.set(n, a);
							}
						}
						else
							if (token == StreamTokenizer.TT_EOF)
								a = null;
							else
								throw new RuntimeException("Invalid token");

		return a;
	}

	@Override
	public void parseFile(final String fileURI)
	{
		try
		{
			final InputStream stream = URI.create(fileURI).toURL().openStream();
			parse(new InputStreamReader(stream));
		}
		catch (final Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public void parse(final Reader reader) throws IOException
	{
		initTokenizer(reader);

		terms = new ArrayList<>();
		disjoints = new HashMap<>();

		int token = in.nextToken();
		while (token != StreamTokenizer.TT_EOF)
		{
			if (token == '#')
			{
				in.ordinaryChar(QUOTE);
				token = in.nextToken();
				while (token != '#')
					token = in.nextToken();
				in.quoteChar(QUOTE);
				token = in.nextToken();
				if (token == StreamTokenizer.TT_EOF)
					break;
			}
			if (token != '(')
				throw new RuntimeException("Parsing error: Expecting '(' but found " + in);

			final String str = nextString();
			if (str.equalsIgnoreCase("DEFINE-ROLE") || str.equalsIgnoreCase("DEFINE-PRIMITIVE-ROLE") || str.equalsIgnoreCase("DEFPRIMROLE") || str.equalsIgnoreCase("DEFINE-ATTRIBUTE") || str.equalsIgnoreCase("DEFINE-PRIMITIVE-ATTRIBUTE") || str.equalsIgnoreCase("DEFPRIMATTRIBUTE") || str.equalsIgnoreCase("DEFINE-DATATYPE-PROPERTY"))
			{
				final ATermAppl r = nextTerm();

				final boolean dataProp = str.equalsIgnoreCase("DEFINE-DATATYPE-PROPERTY");
				final boolean functional = str.equalsIgnoreCase("DEFINE-PRIMITIVE-ATTRIBUTE") || str.equalsIgnoreCase("DEFPRIMATTRIBUTE");
				final boolean primDef = str.indexOf("PRIm") != -1;

				if (dataProp)
				{
					kb.addDatatypeProperty(r);
					if (log.isLoggable(Level.FINE))
						log.fine("DEFINE-DATATYPE-ROLE " + r);
				}
				else
				{
					kb.addObjectProperty(r);
					if (functional)
					{
						kb.addFunctionalProperty(r);
						if (log.isLoggable(Level.FINE))
							log.fine("DEFINE-PRIMITIVE-ATTRIBUTE " + r);
					}
					else
						if (log.isLoggable(Level.FINE))
							log.fine("DEFINE-PRIMITIVE-ROLE " + r);
				}

				while (!peekNext(')'))
					if (peekNext(':'))
					{
						skipNext(':');
						final String cmd = nextString();
						if (cmd.equalsIgnoreCase("parents"))
						{
							final boolean paren = peekNext('(');
							if (paren)
							{
								skipNext('(');
								while (!peekNext(')'))
								{
									final ATermAppl s = nextTerm();
									if (!s.getName().equals("NIL"))
									{
										kb.addObjectProperty(s);
										kb.addSubProperty(r, s);
										if (log.isLoggable(Level.FINE))
											log.fine("PARENT-ROLE " + r + " " + s);
									}
								}
								skipNext(')');
							}
							else
							{
								final ATermAppl s = nextTerm();
								if (!s.toString().equalsIgnoreCase("NIL"))
								{
									kb.addObjectProperty(s);
									kb.addSubProperty(r, s);
									if (log.isLoggable(Level.FINE))
										log.fine("PARENT-ROLE " + r + " " + s);
								}
							}
						}
						else
							if (cmd.equalsIgnoreCase("feature"))
							{
								ATermUtils.assertTrue(nextString().equalsIgnoreCase("T"));
								kb.addFunctionalProperty(r);
								if (log.isLoggable(Level.FINE))
									log.fine("FUNCTIONAL-ROLE " + r);
							}
							else
								if (cmd.equalsIgnoreCase("transitive"))
								{
									ATermUtils.assertTrue(nextString().equalsIgnoreCase("T"));
									kb.addTransitiveProperty(r);
									if (log.isLoggable(Level.FINE))
										log.fine("TRANSITIVE-ROLE " + r);
								}
								else
									if (cmd.equalsIgnoreCase("range"))
									{
										final ATermAppl range = parseExpr();
										kb.addClass(range);
										kb.addRange(r, range);
										if (log.isLoggable(Level.FINE))
											log.fine("RANGE " + r + " " + range);
									}
									else
										if (cmd.equalsIgnoreCase("domain"))
										{
											final ATermAppl domain = parseExpr();
											kb.addClass(domain);
											kb.addDomain(r, domain);
											if (log.isLoggable(Level.FINE))
												log.fine("DOMAIN " + r + " " + domain);
										}
										else
											if (cmd.equalsIgnoreCase("inverse"))
											{
												final ATermAppl inv = nextTerm();
												kb.addInverseProperty(r, inv);
												if (log.isLoggable(Level.FINE))
													log.fine("INVERSE " + r + " " + inv);
											}
											else
												throw new RuntimeException("Parsing error: Unrecognized keyword in role definition " + cmd);
					}
					else
						if (peekNext('('))
						{
							skipNext('(');
							final String cmd = nextString();
							if (cmd.equalsIgnoreCase("domain-range"))
							{
								final ATermAppl domain = nextTerm();
								final ATermAppl range = nextTerm();

								kb.addDomain(r, domain);
								kb.addRange(r, range);
								if (log.isLoggable(Level.FINE))
									log.fine("DOMAIN-RANGE " + r + " " + domain + " " + range);
							}
							else
								throw new RuntimeException("Parsing error: Unrecognized keyword in role definition");
							skipNext(')');
						}
						else
						{
							final ATermAppl s = parseExpr();

							if (dataProp)
								kb.addDatatypeProperty(s);
							else
								kb.addObjectProperty(r);

							if (primDef)
								kb.addSubProperty(r, s);
							else
								kb.addEquivalentProperty(r, s);

							log.fine("PARENT-ROLE " + r + " " + s);
						}
			}
			else
				if (str.equalsIgnoreCase("DEFINE-PRIMITIVE-CONCEPT") || str.equalsIgnoreCase("DEFPRIMCONCEPT"))
				{
					final ATermAppl c = nextTerm();
					kb.addClass(c);

					ATermAppl expr = null;
					if (!peekNext(')'))
					{
						expr = parseExpr();

						if (!expr.getName().equals("NIL"))
						{
							kb.addClass(expr);
							kb.addSubClass(c, expr);
						}
					}

					if (log.isLoggable(Level.FINE))
						log.fine("DEFINE-PRIMITIVE-CONCEPT " + c + " " + (expr == null ? "" : expr.toString()));
				}
				else
					if (str.equalsIgnoreCase("DEFINE-DISJOINT-PRIMITIVE-CONCEPT"))
					{
						final ATermAppl c = nextTerm();
						kb.addClass(c);

						skipNext('(');
						while (!peekNext(')'))
						{
							final ATermAppl expr = parseExpr();

							List<ATermAppl> prevDefinitions = disjoints.get(expr);
							if (prevDefinitions == null)
								prevDefinitions = new ArrayList<>();
							for (final ATermAppl d : prevDefinitions)
							{
								kb.addDisjointClass(c, d);
								if (log.isLoggable(Level.FINE))
									log.fine("DEFINE-PRIMITIVE-DISJOINT " + c + " " + d);
							}
							prevDefinitions.add(c);
						}
						skipNext(')');

						final ATermAppl expr = parseExpr();
						kb.addSubClass(c, expr);

						if (log.isLoggable(Level.FINE))
							log.fine("DEFINE-PRIMITIVE-CONCEPT " + c + " " + expr);
					}
					else
						if (str.equalsIgnoreCase("DEFINE-CONCEPT") || str.equalsIgnoreCase("DEFCONCEPT") || str.equalsIgnoreCase("EQUAL_C"))
						{
							final ATermAppl c = nextTerm();
							kb.addClass(c);

							final ATermAppl expr = parseExpr();
							kb.addEquivalentClass(c, expr);

							if (log.isLoggable(Level.FINE))
								log.fine("DEFINE-CONCEPT " + c + " " + expr);
						}
						else
							if (str.equalsIgnoreCase("IMPLIES") || str.equalsIgnoreCase("IMPLIES_C"))
							{
								final ATermAppl c1 = parseExpr();
								final ATermAppl c2 = parseExpr();
								kb.addClass(c1);
								kb.addClass(c2);
								kb.addSubClass(c1, c2);

								if (log.isLoggable(Level.FINE))
									log.fine("IMPLIES " + c1 + " " + c2);
							}
							else
								if (str.equalsIgnoreCase("IMPLIES_R"))
								{
									final ATermAppl p1 = parseExpr();
									final ATermAppl p2 = parseExpr();
									kb.addProperty(p1);
									kb.addProperty(p2);
									kb.addSubProperty(p1, p2);

									if (log.isLoggable(Level.FINE))
										log.fine("IMPLIES_R " + p1 + " " + p2);
								}
								else
									if (str.equalsIgnoreCase("EQUAL_R"))
									{
										final ATermAppl p1 = parseExpr();
										final ATermAppl p2 = parseExpr();
										kb.addObjectProperty(p1);
										kb.addObjectProperty(p2);
										kb.addEquivalentProperty(p1, p2);

										if (log.isLoggable(Level.FINE))
											log.fine("EQUAL_R " + p1 + " " + p2);
									}
									else
										if (str.equalsIgnoreCase("DOMAIN"))
										{
											final ATermAppl p = parseExpr();
											final ATermAppl c = parseExpr();
											kb.addProperty(p);
											kb.addClass(c);
											kb.addDomain(p, c);

											if (log.isLoggable(Level.FINE))
												log.fine("DOMAIN " + p + " " + c);
										}
										else
											if (str.equalsIgnoreCase("RANGE"))
											{
												final ATermAppl p = parseExpr();
												final ATermAppl c = parseExpr();
												kb.addProperty(p);
												kb.addClass(c);
												kb.addRange(p, c);

												if (log.isLoggable(Level.FINE))
													log.fine("RANGE " + p + " " + c);
											}
											else
												if (str.equalsIgnoreCase("FUNCTIONAL"))
												{
													final ATermAppl p = parseExpr();
													kb.addProperty(p);
													kb.addFunctionalProperty(p);

													if (log.isLoggable(Level.FINE))
														log.fine("FUNCTIONAL " + p);
												}
												else
													if (str.equalsIgnoreCase("TRANSITIVE"))
													{
														final ATermAppl p = parseExpr();
														kb.addObjectProperty(p);
														kb.addTransitiveProperty(p);

														if (log.isLoggable(Level.FINE))
															log.fine("TRANSITIVE " + p);
													}
													else
														if (str.equalsIgnoreCase("DISJOINT"))
														{
															final ATermAppl[] list = parseExprList();
															for (int i = 0; i < list.length - 1; i++)
															{
																final ATermAppl c1 = list[i];
																for (int j = i + 1; j < list.length; j++)
																{
																	final ATermAppl c2 = list[j];
																	kb.addClass(c2);
																	kb.addDisjointClass(c1, c2);
																	if (log.isLoggable(Level.FINE))
																		log.fine("DISJOINT " + c1 + " " + c2);
																}
															}
														}
														else
															if (str.equalsIgnoreCase("DEFINDIVIDUAL"))
															{
																final ATermAppl x = nextTerm();

																kb.addIndividual(x);
																if (log.isLoggable(Level.FINE))
																	log.fine("DEFINDIVIDUAL " + x);
															}
															else
																if (str.equalsIgnoreCase("INSTANCE"))
																{
																	final ATermAppl x = nextTerm();
																	final ATermAppl c = parseExpr();

																	kb.addIndividual(x);
																	kb.addType(x, c);
																	if (log.isLoggable(Level.FINE))
																		log.fine("INSTANCE " + x + " " + c);
																}
																else
																	if (str.equalsIgnoreCase("RELATED"))
																	{
																		final ATermAppl x = nextTerm();
																		final ATermAppl y = nextTerm();
																		final ATermAppl r = nextTerm();

																		kb.addIndividual(x);
																		kb.addIndividual(y);
																		kb.addPropertyValue(r, x, y);

																		if (log.isLoggable(Level.FINE))
																			log.fine("RELATED " + x + " - " + r + " -> " + y);
																	}
																	else
																		if (str.equalsIgnoreCase("DIFFERENT"))
																		{
																			final ATermAppl x = nextTerm();
																			final ATermAppl y = nextTerm();

																			kb.addIndividual(x);
																			kb.addIndividual(y);
																			kb.addDifferent(x, y);

																			if (log.isLoggable(Level.FINE))
																				log.fine("DIFFERENT " + x + " " + y);
																		}
																		else
																			if (str.equalsIgnoreCase("DATATYPE-ROLE-FILLER"))
																			{
																				final ATermAppl x = nextTerm();
																				final ATermAppl y = ATermUtils.makePlainLiteral(nextString());
																				final ATermAppl r = nextTerm();

																				kb.addIndividual(x);
																				kb.addIndividual(y);
																				kb.addPropertyValue(r, x, y);

																				if (log.isLoggable(Level.FINE))
																					log.fine("DATATYPE-ROLE-FILLER " + x + " - " + r + " -> " + y);
																			}
																			else
																				throw new RuntimeException("Parsing error: Unknown command " + str);
			skipNext(')');

			token = in.nextToken();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setIgnoreImports(final boolean ignoreImports)
	{
		// Nothing to do.
	}

	public void verifyTBox(final String file, final KnowledgeBase kb) throws Exception
	{
		initTokenizer(new FileReader(file));

		boolean failed = false;
		int verifiedCount = 0;
		int token = in.nextToken();
		while (token != ')' && token != StreamTokenizer.TT_EOF)
		{
			ATermUtils.assertTrue(token == '(');

			verifiedCount++;

			ATermAppl c = null;
			if (peekNext('('))
			{
				final ATermAppl[] list = parseExprList();
				c = list[0];
				final Set<ATermAppl> eqs = kb.getEquivalentClasses(c);
				for (int i = 1; i < list.length; i++)
				{
					final ATermAppl t = list[i];

					if (!eqs.contains(t))
					{
						log.severe(t + " is not equivalent to " + c);
						failed = true;
					}
				}
			}
			else
				c = parseExpr();

			final Set<ATermAppl> supers = SetUtils.union(kb.getSuperClasses(c, true));
			final Set<ATermAppl> subs = SetUtils.union(kb.getSubClasses(c, true));

			if (log.isLoggable(Level.FINE))
				log.fine("Verify (" + verifiedCount + ") " + c + " " + supers + " " + subs);

			if (peekNext('('))
			{
				final ATermAppl[] terms = parseExprList();
				for (final ATermAppl term : terms)
				{
					final ATerm t = term;

					if (!supers.contains(t))
					{
						log.severe(t + " is not a superclass of " + c + " ");
						failed = true;
					}
				}
			}
			else
				skipNext();

			if (peekNext('('))
			{
				final ATermAppl[] terms = parseExprList();
				for (final ATermAppl term : terms)
				{
					final ATermAppl t = term;

					if (!subs.contains(t))
					{
						final Set<ATermAppl> temp = new HashSet<>(subs);
						final Set<ATermAppl> sames = kb.getEquivalentClasses(t);
						temp.retainAll(sames);
						if (temp.size() == 0)
						{
							log.severe(t + " is not a subclass of " + c);
							failed = true;
						}
					}
				}
			}

			skipNext();

			token = in.nextToken();
		}

		ATermUtils.assertTrue(in.nextToken() == StreamTokenizer.TT_EOF);

		if (failed)
			throw new RuntimeException("Classification results are not correct!");
	}

	public void verifyABox(final String file, final KnowledgeBase kb) throws Exception
	{
		initTokenizer(new FileReader(file));

		final boolean longFormat = !peekNext('(');

		while (!peekNext(StreamTokenizer.TT_EOF))
		{
			if (longFormat)
			{
				skipNext("Command");
				skipNext('=');
			}

			skipNext('(');
			skipNext("INDIVIDUAL-INSTANCE?");

			final ATermAppl ind = nextTerm();
			final ATermAppl c = parseExpr();

			if (log.isLoggable(Level.FINE))
				log.fine("INDIVIDUAL-INSTANCE? " + ind + " " + c);

			skipNext(')');

			boolean isType;
			if (longFormat)
			{
				skipNext('-');
				skipNext('>');
				final String result = nextString();
				if (result.equalsIgnoreCase("T"))
					isType = true;
				else
					if (result.equalsIgnoreCase("NIL"))
						isType = false;
					else
						throw new RuntimeException("Unknown result " + result);
			}
			else
				isType = true;

			if (log.isLoggable(Level.FINE))
				log.fine(" -> " + isType);

			if (kb.isType(ind, c) != isType)
				throw new RuntimeException("Individual " + ind + " is " + (isType ? "not" : "") + " an instance of " + c);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public KnowledgeBase getKB()
	{
		return kb;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load()
	{
		// nothing to do here since we load to the KB directly
	}

}
