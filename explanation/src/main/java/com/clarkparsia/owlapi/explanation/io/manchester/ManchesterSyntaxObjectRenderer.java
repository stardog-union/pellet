// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.owlapi.explanation.io.manchester;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.semanticweb.owlapi.io.XMLUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLQuantifiedRestriction;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLUnaryPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLArgument;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDataPropertyAtom;
import org.semanticweb.owlapi.model.SWRLDataRangeAtom;
import org.semanticweb.owlapi.model.SWRLDifferentIndividualsAtom;
import org.semanticweb.owlapi.model.SWRLIndividualArgument;
import org.semanticweb.owlapi.model.SWRLLiteralArgument;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLSameIndividualAtom;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

public class ManchesterSyntaxObjectRenderer implements OWLObjectVisitor
{
	private boolean _wrapLines = true;
	private boolean _smartIndent = true;

	protected BlockWriter _writer;

	/**
	 * @param _writer
	 */
	public ManchesterSyntaxObjectRenderer(final BlockWriter writer)
	{
		this._writer = writer;
	}

	public boolean isSmartIndent()
	{
		return _smartIndent;
	}

	public boolean isWrapLines()
	{
		return _wrapLines;
	}

	public void setSmartIndent(final boolean smartIndent)
	{
		this._smartIndent = smartIndent;
	}

	public void setWrapLines(final boolean wrapLines)
	{
		this._wrapLines = wrapLines;
	}

	/**
	 * Return the short form (local name) for a URI identifier
	 *
	 * @param theIRI the URI
	 * @return the local name part of the URI identifier
	 */
	protected String shortForm(final IRI theIRI)
	{
		final String fragment = XMLUtils.getNCNameSuffix(theIRI);
		if (fragment != null)
			return fragment;
		final String str = theIRI.toString();
		final int lastSlashIndex = str.lastIndexOf('/');
		if (lastSlashIndex != -1)
			return str.substring(lastSlashIndex + 1, str.length());
		return str;
	}

	@Override
	public void visit(final OWLAsymmetricObjectPropertyAxiom theAxiom)
	{
		writeUnaryPropertyAxiom(theAxiom, Keyword.ASYMMETRIC_PROPERTY);
	}

	@Override
	public void visit(final OWLClass theOWLClass)
	{
		write(theOWLClass.getIRI());
	}

	@Override
	public void visit(final OWLClassAssertionAxiom theAxiom)
	{
		writeKeywordInfix(Keyword.TYPE, theAxiom.getIndividual(), theAxiom.getClassExpression());
	}

	@Override
	public void visit(final OWLAnnotation theAnnotation)
	{
		write(Keyword.ANNOTATION);
		writeSpace();
		write("(");
		write(theAnnotation.getProperty());
		write(" ");
		write(theAnnotation.getValue());
		write(")");
	}

	@Override
	public void visit(final OWLDataAllValuesFrom theDescription)
	{
		writeQuantifiedRestriction(theDescription, Keyword.ONLY);
	}

	@Override
	public void visit(final OWLDataComplementOf theDescription)
	{
		writeKeywordPrefix(Keyword.NOT, theDescription.getDataRange());
	}

	@Override
	public void visit(final OWLDataExactCardinality theDescription)
	{
		writeCardinalityRestriction(theDescription, Keyword.EXACTLY);
	}

	@Override
	public void visit(final OWLDataMaxCardinality theDescription)
	{
		writeCardinalityRestriction(theDescription, Keyword.MAX);
	}

	@Override
	public void visit(final OWLDataMinCardinality theDescription)
	{
		writeCardinalityRestriction(theDescription, Keyword.MIN);
	}

	@Override
	public void visit(final OWLDataOneOf theDescription)
	{
		writeEnumeration(theDescription.values());
	}

	@Override
	public void visit(final OWLDataProperty theProperty)
	{
		write(theProperty.getIRI());
	}

	@Override
	public void visit(final OWLDataPropertyAssertionAxiom theAxiom)
	{
		write(theAxiom.getSubject());
		writeSpace();
		write(theAxiom.getProperty());
		writeSpace();
		write(theAxiom.getObject());
	}

	@Override
	public void visit(final OWLDataPropertyDomainAxiom theAxiom)
	{
		writeKeywordInfix(Keyword.DOMAIN, theAxiom.getProperty(), theAxiom.getDomain());
	}

	@Override
	public void visit(final OWLDataPropertyRangeAxiom theAxiom)
	{
		writeKeywordInfix(Keyword.RANGE, theAxiom.getProperty(), theAxiom.getRange());
	}

	@Override
	public void visit(final OWLFacetRestriction theRestriction)
	{
		write(theRestriction.getFacet().getSymbolicForm());
		writeSpace();
		write(theRestriction.getFacetValue());
	}

	@Override
	public void visit(final OWLDatatypeRestriction theRestriction)
	{

		write(theRestriction.getDatatype());
		write("[");

		boolean first = true;
		final Iterable<OWLFacetRestriction> it = theRestriction.facetRestrictions()::iterator;
		for (final OWLFacetRestriction restriction : it)
		{
			if (first)
				first = false;
			else
			{
				write(",");
				writeSpace();
			}
			write(restriction);
		}
		write("]");

	}

	@Override
	public void visit(final OWLDataSomeValuesFrom theDescription)
	{
		writeQuantifiedRestriction(theDescription, Keyword.SOME);
	}

	@Override
	public void visit(final OWLSubDataPropertyOfAxiom theAxiom)
	{
		writeKeywordInfix(Keyword.SUB_PROPERTY_OF, theAxiom.getSubProperty(), theAxiom.getSuperProperty());
	}

	@Override
	public void visit(final OWLDatatype node)
	{
		write(node.getIRI());
	}

	@Override
	public void visit(final OWLDataHasValue theDescription)
	{
		writeRestriction(theDescription.getProperty(), Keyword.VALUE, theDescription.getFiller());
	}

	@Override
	public void visit(final OWLDeclarationAxiom theAxiom)
	{
		writeKeywordPrefix(Keyword.DECLARATION, theAxiom.getEntity());
	}

	@Override
	public void visit(final OWLDifferentIndividualsAxiom theAxiom)
	{
		writeNaryAxiom(theAxiom.individuals(), Keyword.DIFFERENT_INDIVIDUAL, Keyword.DIFFERENT_INDIVIDUALS);
	}

	@Override
	public void visit(final OWLDisjointClassesAxiom theAxiom)
	{
		writeNaryAxiom(theAxiom.classExpressions(), Keyword.DISJOINT_CLASS, Keyword.DISJOINT_CLASSES);
	}

	@Override
	public void visit(final OWLDisjointDataPropertiesAxiom theAxiom)
	{
		writeNaryAxiom(theAxiom.properties(), Keyword.DISJOINT_PROPERTY, Keyword.DISJOINT_PROPERTIES);
	}

	@Override
	public void visit(final OWLDisjointObjectPropertiesAxiom theAxiom)
	{
		writeNaryAxiom(theAxiom.properties(), Keyword.DISJOINT_PROPERTY, Keyword.DISJOINT_PROPERTIES);
	}

	@Override
	public void visit(final OWLDisjointUnionAxiom theAxiom)
	{
		write(theAxiom.getOWLClass());
		writeSpace();
		write(Keyword.DISJOINT_UNION);
		writeSpace();
		writeNaryKeyword(Keyword.OR, theAxiom.classExpressions());
	}

	@Override
	public void visit(final OWLEquivalentClassesAxiom theAxiom)
	{
		writeNaryAxiom(theAxiom.classExpressions(), Keyword.EQUIVALENT_TO, Keyword.EQUIVALENT_CLASSES);
	}

	@Override
	public void visit(final OWLEquivalentDataPropertiesAxiom theAxiom)
	{
		writeNaryAxiom(theAxiom.properties(), Keyword.EQUIVALENT_TO, Keyword.EQUIVALENT_PROPERTIES);
	}

	@Override
	public void visit(final OWLEquivalentObjectPropertiesAxiom theAxiom)
	{
		writeNaryAxiom(theAxiom.properties(), Keyword.EQUIVALENT_TO, Keyword.EQUIVALENT_PROPERTIES);
	}

	@Override
	public void visit(final OWLFunctionalDataPropertyAxiom theAxiom)
	{
		writeUnaryPropertyAxiom(theAxiom, Keyword.FUNCTIONAL);
	}

	@Override
	public void visit(final OWLFunctionalObjectPropertyAxiom theAxiom)
	{
		writeUnaryPropertyAxiom(theAxiom, Keyword.FUNCTIONAL);
	}

	@Override
	public void visit(final OWLAnonymousIndividual theIndividual)
	{
		write(theIndividual.getID().getID());
	}

	@Override
	public void visit(final OWLNamedIndividual theIndividual)
	{
		write(theIndividual.getIRI());
	}

	@Override
	public void visit(final OWLInverseFunctionalObjectPropertyAxiom theAxiom)
	{
		writeUnaryPropertyAxiom(theAxiom, Keyword.INVERSE_FUNCTIONAL);
	}

	@Override
	public void visit(final OWLInverseObjectPropertiesAxiom theAxiom)
	{
		writeKeywordInfix(Keyword.INVERSE_OF, theAxiom.getFirstProperty(), theAxiom.getSecondProperty());
	}

	@Override
	public void visit(final OWLIrreflexiveObjectPropertyAxiom theAxiom)
	{
		writeUnaryPropertyAxiom(theAxiom, Keyword.IRREFLEXIVE);
	}

	@Override
	public void visit(final OWLNegativeDataPropertyAssertionAxiom theAxiom)
	{
		write(Keyword.NOT_RELATIONSHIP);
		writeSpace();
		write("(");
		write(theAxiom.getSubject());
		writeSpace();
		write(theAxiom.getProperty());
		writeSpace();
		write(theAxiom.getObject());
		write(")");
	}

	@Override
	public void visit(final OWLNegativeObjectPropertyAssertionAxiom theAxiom)
	{
		write(Keyword.NOT_RELATIONSHIP);
		writeSpace();
		write("(");
		write(theAxiom.getSubject());
		writeSpace();
		write(theAxiom.getProperty());
		writeSpace();
		write(theAxiom.getObject());
		write(")");
	}

	@Override
	public void visit(final OWLObjectAllValuesFrom theDescription)
	{
		writeQuantifiedRestriction(theDescription, Keyword.ONLY);
	}

	@Override
	public void visit(final OWLObjectComplementOf theDescription)
	{
		writeKeywordPrefix(Keyword.NOT, theDescription.getOperand());
	}

	@Override
	public void visit(final OWLObjectExactCardinality theDescription)
	{
		writeCardinalityRestriction(theDescription, Keyword.EXACTLY);
	}

	@Override
	public void visit(final OWLObjectIntersectionOf theDescription)
	{
		writeNaryKeyword(Keyword.AND, theDescription.operands());
	}

	@Override
	public void visit(final OWLObjectMaxCardinality theDescription)
	{
		writeCardinalityRestriction(theDescription, Keyword.MAX);
	}

	@Override
	public void visit(final OWLObjectMinCardinality theDescription)
	{
		writeCardinalityRestriction(theDescription, Keyword.MIN);
	}

	@Override
	public void visit(final OWLObjectOneOf theDescription)
	{
		writeEnumeration(theDescription.individuals());
	}

	@Override
	public void visit(final OWLObjectProperty theProperty)
	{
		write(theProperty.getIRI());
	}

	@Override
	public void visit(final OWLObjectPropertyAssertionAxiom theAxiom)
	{
		write(theAxiom.getSubject());
		writeSpace();
		write(theAxiom.getProperty());
		writeSpace();
		write(theAxiom.getObject());
	}

	@Override
	public void visit(final OWLSubPropertyChainOfAxiom theAxiom)
	{
		writeCollection(theAxiom.getPropertyChain(), " o", false);
		writeSpace();
		write(Keyword.SUB_PROPERTY_OF);
		writeSpace();
		write(theAxiom.getSuperProperty());
	}

	@Override
	public void visit(final OWLObjectPropertyDomainAxiom theAxiom)
	{
		writeKeywordInfix(Keyword.DOMAIN, theAxiom.getProperty(), theAxiom.getDomain());
	}

	@Override
	public void visit(final OWLObjectInverseOf theInverse)
	{
		writeKeywordPrefix(Keyword.INVERSE, theInverse.getInverse());
	}

	@Override
	public void visit(final OWLObjectPropertyRangeAxiom theAxiom)
	{
		writeKeywordInfix(Keyword.RANGE, theAxiom.getProperty(), theAxiom.getRange());
	}

	@Override
	public void visit(final OWLObjectHasSelf theRestriction)
	{
		writeRestriction(theRestriction.getProperty(), Keyword.SELF);
	}

	@Override
	public void visit(final OWLObjectSomeValuesFrom theDescription)
	{
		writeQuantifiedRestriction(theDescription, Keyword.SOME);
	}

	@Override
	public void visit(final OWLSubObjectPropertyOfAxiom theAxiom)
	{
		writeKeywordInfix(Keyword.SUB_PROPERTY_OF, theAxiom.getSubProperty(), theAxiom.getSuperProperty());
	}

	@Override
	public void visit(final OWLObjectUnionOf theDescription)
	{
		writeNaryKeyword(Keyword.OR, theDescription.operands());
	}

	@Override
	public void visit(final OWLObjectHasValue theDescription)
	{
		writeRestriction(theDescription.getProperty(), Keyword.VALUE, theDescription.getFiller());
	}

	@Override
	public void visit(final OWLOntology ontology)
	{
		write(ontology.getOntologyID().getOntologyIRI().orElse(null));
	}

	@Override
	public void visit(final OWLReflexiveObjectPropertyAxiom theAxiom)
	{
		writeUnaryPropertyAxiom(theAxiom, Keyword.REFLEXIVE_PROPERTY);
	}

	@Override
	public void visit(final OWLSameIndividualAxiom theAxiom)
	{
		writeNaryAxiom(theAxiom.individuals(), Keyword.SAME_INDIVIDUAL, Keyword.SAME_INDIVIDUALS);
	}

	@Override
	public void visit(final OWLSubClassOfAxiom theAxiom)
	{
		writeKeywordInfix(Keyword.SUB_CLASS_OF, theAxiom.getSubClass(), theAxiom.getSuperClass());
	}

	@Override
	public void visit(final OWLSymmetricObjectPropertyAxiom theAxiom)
	{
		writeUnaryPropertyAxiom(theAxiom, Keyword.SYMMETRIC);
	}

	@Override
	public void visit(final OWLTransitiveObjectPropertyAxiom theAxiom)
	{
		writeUnaryPropertyAxiom(theAxiom, Keyword.TRANSITIVE);
	}

	@Override
	public void visit(final OWLLiteral node)
	{
		if (node.isRDFPlainLiteral())
		{
			write("\"");
			write(node.getLiteral());
			write("\"");
			if (node.getLang() != null && !node.getLang().equals(""))
			{
				write("@");
				write(node.getLang());
			}
		}
		else
			if (node.getDatatype().getIRI().equals(XSDVocabulary.INTEGER.getIRI()) || node.getDatatype().getIRI().equals(XSDVocabulary.DECIMAL.getIRI()))
				write(node.getLiteral());
			else
				if (node.getDatatype().getIRI().equals(XSDVocabulary.FLOAT.getIRI()))
				{
					write(node.getLiteral());
					write("f");
				}
				else
				{
					write("\"");
					write(node.getLiteral());
					write("\"");
					write("^^");
					write(node.getDatatype());
				}
	}

	@Override
	public void visit(final SWRLLiteralArgument node)
	{
		write(node.getLiteral());
	}

	@Override
	public void visit(final SWRLIndividualArgument node)
	{
		write(node.getIndividual());
	}

	@Override
	public void visit(final SWRLVariable node)
	{
		write("?");
		write(node.getIRI());
	}

	@Override
	public void visit(final SWRLBuiltInAtom node)
	{
		write(node.getPredicate());
		write("(");
		for (final SWRLArgument arg : node.getArguments())
		{
			write(arg);
			write(" ");
		}
		write(")");
	}

	@Override
	public void visit(final SWRLClassAtom node)
	{
		write(node.getPredicate());
		write("(");
		write(node.getArgument());
		write(")");
	}

	/*
	 * this is all the SWRL rendering stuff that we'll provide some defaults for
	 * using evren's concise format stuff
	 */

	@Override
	public void visit(final SWRLDataRangeAtom node)
	{
		write(node.getPredicate());
		write("(");
		write(node.getArgument());
		write(")");
	}

	@Override
	public void visit(final SWRLDataPropertyAtom node)
	{
		write(node.getPredicate());
		write("(");
		write(node.getFirstArgument());
		write(", ");
		write(node.getSecondArgument());
		write(")");
	}

	@Override
	public void visit(final SWRLDifferentIndividualsAtom node)
	{
		write("differentFrom");
		write("(");
		write(node.getFirstArgument());
		write(", ");
		write(node.getSecondArgument());
		write(")");
	}

	@Override
	public void visit(final SWRLObjectPropertyAtom node)
	{
		write(node.getPredicate());
		write("(");
		write(node.getFirstArgument());
		write(", ");
		write(node.getSecondArgument());
		write(")");
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void visit(final SWRLRule rule)
	{
		write("Rule");
		write("(");

		//		if( !rule.isAnonymous() ) {
		//			write( rule.getIRI() );
		//			writeSpace();
		//		}
		{
			boolean first = true;
			final Iterable<SWRLAtom> it = rule.body()::iterator;
			for (final SWRLAtom at : it)
			{
				if (first)
					first = false;
				else
					write(", ");
				write(at);

			}
		}
		write(" -> ");
		{
			boolean first = true;
			final Iterable<SWRLAtom> it = rule.head()::iterator;
			for (final SWRLAtom at : it)
			{
				if (first)
					first = false;
				else
					write(", ");
				write(at);
			}
		}

		write(")");
	}

	@Override
	public void visit(final SWRLSameIndividualAtom node)
	{
		write("sameAs");
		write("(");
		write(node.getFirstArgument());
		write(", ");
		write(node.getSecondArgument());
		write(")");
	}

	protected void writeNaryKeyword(final Keyword theKeyword, Stream<? extends OWLObject> theObjects)
	{
		final Iterator<? extends OWLObject> aIter = theObjects.sorted().iterator();

		// write( "(" );

		if (_smartIndent)
			_writer.startBlock();

		write(aIter.next());
		while (aIter.hasNext())
		{
			if (_wrapLines)
				writeNewLine();
			else
				writeSpace();

			if (theKeyword != null)
			{
				write(theKeyword);
				writeSpace();
			}

			write(aIter.next());
		}

		if (_smartIndent)
			_writer.endBlock();

		// write( ")" );
	}

	protected void writeCardinalityRestriction(final OWLCardinalityRestriction<?> theRestriction, final Keyword theKeyword)
	{
		if (theRestriction.isQualified())
			writeRestriction(theRestriction.getProperty(), theKeyword, theRestriction.getCardinality(), theRestriction.getFiller());
		else
			writeRestriction(theRestriction.getProperty(), theKeyword, theRestriction.getCardinality());
	}

	/**
	 * Render an n-ary axiom with special handling for the binary case.
	 *
	 * @param objs objects to be rendered
	 * @param binary keyword used for binary case
	 * @param nary keyword used for n-ary case
	 */
	protected <T extends OWLObject> void writeNaryAxiom(final Stream<T> objs, final Keyword binary, final Keyword nary)
	{
		final List<T> args = objs.sorted().collect(Collectors.toList());

		if (args.size() == 2)
			writeKeywordInfix(binary, args.get(0), args.get(1));
		else
		{
			write(nary);
			write("(");
			writeNaryKeyword(null, args.stream());
			write(")");
		}
	}

	protected void writeNaryAxiom(final Set<? extends OWLObject> set, final Keyword binary, final Keyword nary)
	{
		writeNaryAxiom(set.stream(), binary, nary);
	}

	protected void writeQuantifiedRestriction(final OWLQuantifiedRestriction<?> theRestriction, final Keyword theKeyword)
	{
		writeRestriction(theRestriction.getProperty(), theKeyword, theRestriction.getFiller());
	}

	protected void writeRestriction(final OWLPropertyExpression theProperty, final Keyword theKeyword, final Object... theArgs)
	{
		// write( "(" );

		write(theProperty);
		writeSpace();
		write(theKeyword);
		for (final Object aObject : theArgs)
		{
			writeSpace();
			if (aObject instanceof OWLObject)
				write((OWLObject) aObject);
			else
				write(aObject.toString());
		}

		// write( ")" );

	}

	protected void writeUnaryPropertyAxiom(final OWLUnaryPropertyAxiom<?> theAxiom, final Keyword keyword)
	{
		writeKeywordPrefix(keyword, theAxiom.getProperty());
	}

	protected void writeKeywordPrefix(final Keyword keyword, final OWLObject arg)
	{
		write(keyword);
		writeSpace();
		//		write( "(" );
		write(arg);
		//		write( ")" );
	}

	protected void writeKeywordPrefix(final Keyword keyword, final OWLObject arg1, final OWLObject arg2)
	{
		write(keyword);
		writeSpace();
		write("(");
		write(arg1);
		writeSpace();
		write(arg2);
		write(")");
	}

	protected void writeKeywordInfix(final Keyword keyword, final OWLObject arg1, final OWLObject arg2)
	{
		write(arg1);
		writeSpace();
		write(keyword);
		writeSpace();
		write(arg2);
	}

	protected void writeEnumeration(final Set<? extends OWLObject> objects)
	{
		writeEnumeration(objects.stream());
	}

	protected void writeEnumeration(final Stream<? extends OWLObject> objects)
	{
		write(Keyword.OPEN_BRACE);
		writeCollection(objects, ",", true);
		write(Keyword.CLOSE_BRACE);
	}

	protected <T extends OWLObject> void writeCollection(final Stream<T> objects, final String separator, final boolean sort)
	{
		final Stream<T> sobjs = sort ? objects.sorted() : objects;

		boolean first = true;
		final Iterable<T> it = sobjs::iterator;
		for (final T ind : it)
		{
			if (first)
				first = false;
			else
			{
				write(separator);
				writeSpace();
			}
			write(ind);
		}
	}

	protected void writeCollection(Collection<? extends OWLObject> objects, final String separator, final boolean sort)
	{
		writeCollection(objects.stream(), separator, sort);
	}

	protected void write(final OWLObject object)
	{
		object.accept(this);
	}

	protected void write(final Keyword keyword)
	{
		write(keyword.getLabel());
	}

	protected void write(final String s)
	{
		_writer.print(s);
	}

	protected void write(final IRI iri)
	{
		write(shortForm(iri));
	}

	protected void writeNewLine()
	{
		_writer.println();
	}

	protected void writeSpace()
	{
		write(" ");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final OWLHasKeyAxiom theAxiom)
	{
		write(theAxiom.getClassExpression());
		writeSpace();
		write(Keyword.HAS_KEY);
		writeCollection(theAxiom.objectPropertyExpressions(), "", true);
		writeSpace();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final OWLDatatypeDefinitionAxiom theAxiom)
	{
		writeKeywordInfix(Keyword.EQUIVALENT_TO, theAxiom.getDatatype(), theAxiom.getDataRange());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final OWLAnnotationAssertionAxiom theAxiom)
	{
		write(theAxiom.getSubject());
		writeSpace();
		write(theAxiom.getProperty());
		writeSpace();
		write(theAxiom.getValue());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final OWLSubAnnotationPropertyOfAxiom theAxiom)
	{
		writeKeywordInfix(Keyword.SUB_PROPERTY_OF, theAxiom.getSubProperty(), theAxiom.getSuperProperty());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final OWLAnnotationPropertyDomainAxiom theAxiom)
	{
		writeKeywordInfix(Keyword.DOMAIN, theAxiom.getProperty(), theAxiom.getDomain());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final OWLAnnotationPropertyRangeAxiom theAxiom)
	{
		writeKeywordInfix(Keyword.RANGE, theAxiom.getProperty(), theAxiom.getRange());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final OWLDataIntersectionOf node)
	{
		writeNaryKeyword(Keyword.AND, node.operands());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final OWLDataUnionOf node)
	{
		writeNaryKeyword(Keyword.OR, node.operands());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final OWLAnnotationProperty property)
	{
		write(property.getIRI());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(final IRI iri)
	{
		write(iri);
	}
}
