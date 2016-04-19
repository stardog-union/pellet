// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity;

import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.owlapi.OntologyUtils;
import com.clarkparsia.reachability.Node;
import com.clarkparsia.reachability.ReachabilityGraph;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import org.mindswap.pellet.utils.SetUtils;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;

/**
 * @author Evren Sirin
 */
public class GraphBuilder
{

	public static final Logger log = Logger.getLogger(GraphBuilder.class.getName());

	private class AxiomVisitor implements OWLAxiomVisitor
	{

		public AxiomVisitor()
		{
		}

		@Override
		public void visit(final OWLAsymmetricObjectPropertyAxiom axiom)
		{
			// unary axiom has no effect
		}

		@Override
		public void visit(final OWLAnnotationAssertionAxiom axiom)
		{
			// nothing to do for annotations
		}

		@Override
		public void visit(final OWLSubAnnotationPropertyOfAxiom axiom)
		{
			// nothing to do for annotations
		}

		@Override
		public void visit(final OWLAnnotationPropertyDomainAxiom axiom)
		{
			// nothing to do for annotations
		}

		@Override
		public void visit(final OWLAnnotationPropertyRangeAxiom axiom)
		{
			// nothing to do for annotations
		}

		@Override
		public void visit(final OWLClassAssertionAxiom axiom)
		{
			if (axiom.getIndividual().isAnonymous())
				return;
			final Node node = bottomEvaluator.evaluate(axiom.getClassExpression());
			addOutputs(node, axiom);
			addOutputs(graph.createEntityNode(axiom.getIndividual()), axiom);
		}

		@Override
		public void visit(final OWLDataPropertyAssertionAxiom axiom)
		{
			addOutputs(axiom);
		}

		@Override
		public void visit(final OWLDataPropertyDomainAxiom axiom)
		{
			final Set<Node> nodes = new HashSet<Node>();

			nodes.add(graph.createEntityNode(axiom.getProperty().asOWLDataProperty()));
			nodes.add(topEvaluator.evaluate(axiom.getDomain()));

			addOutputs(graph.createAndNode(nodes), axiom);
		}

		@Override
		public void visit(final OWLDataPropertyRangeAxiom axiom)
		{
			// do nothing
		}

		@Override
		public void visit(final OWLSubDataPropertyOfAxiom axiom)
		{
			final Node subNode = graph.createEntityNode(axiom.getSubProperty().asOWLDataProperty());
			final Node supNode = graph.createEntityNode(axiom.getSuperProperty().asOWLDataProperty());

			subNode.getOutputs().add(supNode);
		}

		@Override
		public void visit(final OWLDeclarationAxiom axiom)
		{
			// do nothing
		}

		@Override
		public void visit(final OWLDatatypeDefinitionAxiom axiom)
		{
			// do nothing
		}

		@Override
		public void visit(final OWLDifferentIndividualsAxiom axiom)
		{
			// do nothing
		}

		@Override
		public void visit(final OWLDisjointClassesAxiom axiom)
		{
			processDisjoints(axiom, axiom.getClassExpressions());
		}

		protected void processDisjoints(final OWLAxiom axiom, final Set<OWLClassExpression> desc)
		{
			final OWLClassExpression descriptions[] = desc.toArray(new OWLClassExpression[0]);
			final Set<Node> or = new HashSet<Node>();
			for (int i = 0; i < descriptions.length - 1; i++)
				for (int j = i; j < descriptions.length; j++)
				{
					final Node n1 = bottomEvaluator.evaluate(descriptions[i]);
					final Node n2 = bottomEvaluator.evaluate(descriptions[j]);

					or.add(graph.createAndNode(SetUtils.create(n1, n2)));
				}

			if (!or.isEmpty())
				if (or.size() == 1)
					addOutputs(or.iterator().next(), axiom);
				else
					addOutputs(graph.createOrNode(or), axiom);
		}

		@Override
		public void visit(final OWLDisjointDataPropertiesAxiom axiom)
		{
			// FIXME not implemented
		}

		@Override
		public void visit(final OWLDisjointObjectPropertiesAxiom axiom)
		{
			// FIXME not implemented
		}

		@Override
		public void visit(final OWLDisjointUnionAxiom axiom)
		{
			processDisjoints(axiom, axiom.getClassExpressions());
			processEquivalent(axiom, axiom.getOWLClass(), OWL.or(axiom.getClassExpressions()));
		}

		@Override
		public void visit(final OWLEquivalentClassesAxiom axiom)
		{
			final Iterator<OWLClassExpression> eqs = axiom.getClassExpressions().iterator();
			final OWLClassExpression c1 = eqs.next();

			// if the axiom is a singleton we can ignore it. a concept
			// being equivalent to itself has no effect.
			if (!eqs.hasNext())
				return;

			final OWLClassExpression c2 = eqs.next();

			if (eqs.hasNext())
				throw new UnsupportedOperationException("OWLEquivalentClassesAxiom with more than 2 elements");

			processEquivalent(axiom, c1, c2);
		}

		protected void processEquivalent(final OWLAxiom axiom, final OWLClassExpression c1, final OWLClassExpression c2)
		{
			final Set<Node> nodes1 = new HashSet<Node>();
			nodes1.add(topEvaluator.evaluate(c1));
			nodes1.add(topEvaluator.evaluate(c2));

			final Set<Node> nodes2 = new HashSet<Node>();
			nodes2.add(bottomEvaluator.evaluate(c1));
			nodes2.add(bottomEvaluator.evaluate(c2));

			final Node or1 = graph.createOrNode(nodes1);
			final Node or2 = graph.createOrNode(nodes2);

			final Node result = graph.createAndNode(SetUtils.create(or1, or2));

			addOutputs(result, axiom);
		}

		@Override
		public void visit(final OWLEquivalentDataPropertiesAxiom axiom)
		{
			addOutputs(axiom);
		}

		@Override
		public void visit(final OWLEquivalentObjectPropertiesAxiom axiom)
		{
			addOutputs(axiom);
		}

		@Override
		public void visit(final OWLFunctionalDataPropertyAxiom axiom)
		{
			// unary axiom has no effect
		}

		@Override
		public void visit(final OWLFunctionalObjectPropertyAxiom axiom)
		{
			// unary axiom has no effect
		}

		public void visit(final OWLImportsDeclaration axiom)
		{
			// nothing to do with declarations
		}

		@Override
		public void visit(final OWLInverseFunctionalObjectPropertyAxiom axiom)
		{
			// unary axiom has no effect
		}

		@Override
		public void visit(final OWLInverseObjectPropertiesAxiom axiom)
		{
			addOutputs(axiom);
		}

		@Override
		public void visit(final OWLHasKeyAxiom theOWLHasKeyAxiom)
		{
			// do nothing
		}

		@Override
		public void visit(final OWLIrreflexiveObjectPropertyAxiom axiom)
		{
			// unary axiom has no effect
		}

		@Override
		public void visit(final OWLNegativeDataPropertyAssertionAxiom axiom)
		{
			addOutputs(axiom);
		}

		@Override
		public void visit(final OWLNegativeObjectPropertyAssertionAxiom axiom)
		{
			addOutputs(axiom);
		}

		@Override
		public void visit(final OWLObjectPropertyAssertionAxiom axiom)
		{
			addOutputs(axiom);
		}

		@Override
		public void visit(final OWLSubPropertyChainOfAxiom axiom)
		{
			final Set<Node> nodes = new HashSet<Node>();

			for (final OWLObjectPropertyExpression p : axiom.getPropertyChain())
				nodes.add(graph.createEntityNode(p.getNamedProperty()));

			addOutputs(graph.createAndNode(nodes), axiom);
		}

		@Override
		public void visit(final OWLObjectPropertyDomainAxiom axiom)
		{
			final Set<Node> nodes = new HashSet<Node>();

			nodes.add(graph.createEntityNode(axiom.getProperty().getNamedProperty()));
			nodes.add(topEvaluator.evaluate(axiom.getDomain()));

			addOutputs(graph.createAndNode(nodes), axiom);
		}

		@Override
		public void visit(final OWLObjectPropertyRangeAxiom axiom)
		{
			final Set<Node> nodes = new HashSet<Node>();

			nodes.add(graph.createEntityNode(axiom.getProperty().getNamedProperty()));
			nodes.add(topEvaluator.evaluate(axiom.getRange()));

			addOutputs(graph.createAndNode(nodes), axiom);
		}

		@Override
		public void visit(final OWLSubObjectPropertyOfAxiom axiom)
		{
			final Node subNode = graph.createEntityNode(axiom.getSubProperty().getNamedProperty());
			final Node supNode = graph.createEntityNode(axiom.getSuperProperty().getNamedProperty());

			subNode.addOutput(supNode);
		}

		@Override
		public void visit(final OWLReflexiveObjectPropertyAxiom axiom)
		{
			// unary axiom has no effect
		}

		@Override
		public void visit(final OWLSameIndividualAxiom axiom)
		{
			addOutputs(axiom);
		}

		@Override
		public void visit(final OWLSubClassOfAxiom axiom)
		{
			final Set<Node> nodes = new HashSet<Node>();

			nodes.add(topEvaluator.evaluate(axiom.getSuperClass()));
			nodes.add(bottomEvaluator.evaluate(axiom.getSubClass()));

			if (!nodes.isEmpty())
				addOutputs(graph.createAndNode(nodes), axiom);
		}

		@Override
		public void visit(final OWLSymmetricObjectPropertyAxiom axiom)
		{
			// unary axiom has no effect
		}

		@Override
		public void visit(final OWLTransitiveObjectPropertyAxiom axiom)
		{
			// unary axiom has no effect
		}

		@Override
		public void visit(final SWRLRule axiom)
		{
			// nothing to do with rules
		}
	}

	private class BottomEvaluator implements OWLClassExpressionVisitor
	{

		private Node node;

		public BottomEvaluator()
		{
		}

		public Node evaluate(final OWLClassExpression desc)
		{
			// reset the result first
			node = null;

			desc.accept(this);

			// a null value indicates error
			if (node == null)
				throw new IllegalStateException("Evaluation returned null");

			return node;
		}

		@Override
		public void visit(final OWLClass desc)
		{
			node = desc.equals(OWL.Nothing) ? START_NODE : desc.equals(OWL.Thing) ? NULL_NODE : graph.createEntityNode(desc);
		}

		@Override
		public void visit(final OWLDataAllValuesFrom desc)
		{
			node = START_NODE;
		}

		@Override
		public void visit(final OWLDataExactCardinality desc)
		{
			node = START_NODE;
		}

		@Override
		public void visit(final OWLDataMaxCardinality desc)
		{
			node = (desc.getCardinality() == 0) ? graph.createEntityNode(desc.getProperty().asOWLDataProperty()) : START_NODE;
		}

		@Override
		public void visit(final OWLDataMinCardinality desc)
		{
			// TODO: Special handling for the n == 0 case
			node = graph.createEntityNode(desc.getProperty().asOWLDataProperty());
		}

		@Override
		public void visit(final OWLDataSomeValuesFrom desc)
		{
			node = graph.createEntityNode(desc.getProperty().asOWLDataProperty());
		}

		@Override
		public void visit(final OWLDataHasValue desc)
		{
			node = graph.createEntityNode(desc.getProperty().asOWLDataProperty());
		}

		@Override
		public void visit(final OWLObjectAllValuesFrom desc)
		{
			node = START_NODE;
		}

		@Override
		public void visit(final OWLObjectComplementOf desc)
		{
			node = topEvaluator.evaluate(desc.getOperand());
		}

		@Override
		public void visit(final OWLObjectExactCardinality desc)
		{
			node = START_NODE;
		}

		@Override
		public void visit(final OWLObjectIntersectionOf desc)
		{
			final Set<Node> inputNodes = new HashSet<Node>();
			for (final OWLClassExpression c : desc.getOperands())
			{
				final Node conjNode = evaluate(c);
				inputNodes.add(conjNode);
			}

			if (!inputNodes.isEmpty())
				node = graph.createAndNode(inputNodes);
		}

		@Override
		public void visit(final OWLObjectMaxCardinality desc)
		{
			node = (desc.getCardinality() == 0) ? graph.createEntityNode(desc.getProperty().getNamedProperty()) : START_NODE;
		}

		@Override
		public void visit(final OWLObjectMinCardinality desc)
		{
			// TODO: Special handling for the n == 0 case
			node = graph.createEntityNode(desc.getProperty().getNamedProperty());
		}

		@Override
		public void visit(final OWLObjectOneOf desc)
		{
			node = START_NODE;
		}

		@Override
		public void visit(final OWLObjectHasSelf desc)
		{
			node = graph.createEntityNode(desc.getProperty().getNamedProperty());
		}

		@Override
		public void visit(final OWLObjectSomeValuesFrom desc)
		{
			final Set<Node> inputNodes = new HashSet<Node>();

			inputNodes.add(graph.createEntityNode(desc.getProperty().getNamedProperty()));
			inputNodes.add(evaluate(desc.getFiller()));

			node = graph.createAndNode(inputNodes);
		}

		@Override
		public void visit(final OWLObjectUnionOf desc)
		{
			final Set<Node> inputNodes = new HashSet<Node>();
			for (final OWLClassExpression disj : desc.getOperands())
			{
				final Node disjNode = evaluate(disj);
				inputNodes.add(disjNode);
			}

			node = graph.createOrNode(inputNodes);
		}

		@Override
		public void visit(final OWLObjectHasValue desc)
		{
			node = graph.createEntityNode(desc.getProperty().getNamedProperty());
		}
	}

	private class TopEvaluator implements OWLClassExpressionVisitor
	{

		private Node node;

		public TopEvaluator()
		{
		}

		public Node evaluate(final OWLClassExpression desc)
		{
			// reset the result first
			node = null;

			desc.accept(this);

			// a null value indicates error
			if (node == null)
				throw new IllegalStateException("Evaluation returned null");

			return node;
		}

		@Override
		public void visit(final OWLClass desc)
		{
			node = desc.equals(OWL.Thing) ? NULL_NODE : START_NODE;
		}

		@Override
		public void visit(final OWLDataAllValuesFrom desc)
		{
			node = graph.createEntityNode(desc.getProperty().asOWLDataProperty());
		}

		@Override
		public void visit(final OWLDataExactCardinality desc)
		{
			node = START_NODE;
		}

		@Override
		public void visit(final OWLDataMaxCardinality desc)
		{
			// TODO: Special handling for the n == 0 case
			node = graph.createEntityNode(desc.getProperty().asOWLDataProperty());
		}

		@Override
		public void visit(final OWLDataMinCardinality desc)
		{
			node = START_NODE;
		}

		@Override
		public void visit(final OWLDataSomeValuesFrom desc)
		{
			node = START_NODE;
		}

		@Override
		public void visit(final OWLDataHasValue desc)
		{
			node = START_NODE;
		}

		@Override
		public void visit(final OWLObjectAllValuesFrom desc)
		{
			final Set<Node> inputNodes = new HashSet<Node>();

			inputNodes.add(graph.createEntityNode(desc.getProperty().getNamedProperty()));
			inputNodes.add(evaluate(desc.getFiller()));

			node = graph.createAndNode(inputNodes);
		}

		@Override
		public void visit(final OWLObjectComplementOf desc)
		{
			node = bottomEvaluator.evaluate(desc.getOperand());
		}

		@Override
		public void visit(final OWLObjectExactCardinality desc)
		{
			node = START_NODE;
		}

		@Override
		public void visit(final OWLObjectIntersectionOf desc)
		{
			final Set<Node> inputNodes = new HashSet<Node>();
			for (final OWLClassExpression conj : desc.getOperands())
			{
				final Node conjNode = evaluate(conj);
				inputNodes.add(conjNode);
			}

			node = graph.createOrNode(inputNodes);
		}

		@Override
		public void visit(final OWLObjectMaxCardinality desc)
		{
			// TODO: Special handling for the n == 0 case
			node = graph.createEntityNode(desc.getProperty().getNamedProperty());
		}

		@Override
		public void visit(final OWLObjectMinCardinality desc)
		{
			node = START_NODE;
		}

		@Override
		public void visit(final OWLObjectOneOf desc)
		{
			node = START_NODE;
		}

		@Override
		public void visit(final OWLObjectHasSelf desc)
		{
			node = NULL_NODE;
		}

		@Override
		public void visit(final OWLObjectSomeValuesFrom desc)
		{
			node = START_NODE;
		}

		@Override
		public void visit(final OWLObjectUnionOf desc)
		{
			final Set<Node> inputNodes = new HashSet<Node>();
			for (final OWLClassExpression disj : desc.getOperands())
			{
				final Node disjNode = evaluate(disj);
				inputNodes.add(disjNode);
			}

			if (!inputNodes.isEmpty())
				node = graph.createAndNode(inputNodes);
		}

		@Override
		public void visit(final OWLObjectHasValue desc)
		{
			node = START_NODE;
		}
	}

	private final ReachabilityGraph graph = new ReachabilityGraph();

	private final AxiomVisitor axiomVisitor = new AxiomVisitor();

	private final BottomEvaluator bottomEvaluator = new BottomEvaluator();

	private final TopEvaluator topEvaluator = new TopEvaluator();

	private final Node NULL_NODE = graph.getNullNode();

	private final Node START_NODE = graph.getStartNode();

	public void addAxiom(final OWLAxiom axiom)
	{
		axiom.accept(axiomVisitor);
	}

	private void addOutputs(final Node node, final OWLAxiom axiom)
	{
		// the following if statement was added to be consistent
		// with earlier implementation that only considered axioms
		// whose signature had a common element with the current
		// signature of the module. this behavior is not consistent
		// with the theoretical description of the modularity
		// algorithm and may cause incorrect results in incremental
		// classification (see deleteNonLocal() test inside
		// SimpleCorrectnessTest)
		//		if( node.equals( START_NODE ) ) {
		//			log.warn( "Non-local axiom: " + axiom );
		//			addOutputs( axiom );
		//			return;
		//		}

		if (node.equals(NULL_NODE))
			return;

		final Set<OWLEntity> entities = OntologyUtils.getSignature(axiom);

		for (final OWLEntity entity : entities)
		{
			final Node outNode = graph.createEntityNode(entity);
			node.addOutput(outNode);
		}
	}

	private void addOutputs(final OWLAxiom axiom)
	{
		final Set<OWLEntity> signature = OntologyUtils.getSignature(axiom);
		final OWLEntity[] entities = signature.toArray(new OWLEntity[signature.size()]);
		for (int i = 0, n = entities.length; i < n - 1; i++)
		{
			final Node n1 = graph.createEntityNode(entities[i]);
			for (int j = i + 1; j < n; j++)
			{
				final Node n2 = graph.createEntityNode(entities[j]);
				n1.addOutput(n2);
				n2.addOutput(n1);
			}
		}
	}

	public ReachabilityGraph build()
	{
		graph.simplify();
		return graph;
	}
}
