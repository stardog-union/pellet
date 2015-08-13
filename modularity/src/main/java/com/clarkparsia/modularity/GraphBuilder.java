// Copyright (c) 2006 - 2015, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.owlapiv3.OntologyUtils;
import com.clarkparsia.reachability.Node;
import com.clarkparsia.reachability.ReachabilityGraph;
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
import org.semanticweb.owlapi.model.OWLNamedIndividual;
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
public class GraphBuilder {

	public static final Logger log = Logger.getLogger(GraphBuilder.class.getName());

	private class AxiomVisitor implements OWLAxiomVisitor {

		public AxiomVisitor() {
		}

		public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
			// unary axiom has no effect
		}

		public void visit(OWLAnnotationAssertionAxiom axiom) {
			// nothing to do for annotations
		}

		@Override
		public void visit(final OWLSubAnnotationPropertyOfAxiom axiom) {
			// nothing to do for annotations
		}

		@Override
		public void visit(final OWLAnnotationPropertyDomainAxiom axiom) {
			// nothing to do for annotations
		}

		@Override
		public void visit(final OWLAnnotationPropertyRangeAxiom axiom) {
			// nothing to do for annotations
		}

		public void visit(OWLClassAssertionAxiom axiom) {
			if (axiom.getIndividual().isAnonymous()) {
				return;
			}
			Node node = bottomEvaluator.evaluate(axiom.getClassExpression());
			addOutputs(node, axiom);
			addOutputs(graph.createEntityNode((OWLNamedIndividual) axiom.getIndividual()), axiom);
		}

		public void visit(OWLDataPropertyAssertionAxiom axiom) {
			addOutputs(axiom);
		}

		public void visit(OWLDataPropertyDomainAxiom axiom) {
			Set<Node> nodes = new HashSet<Node>();

			nodes.add(graph.createEntityNode(axiom.getProperty().asOWLDataProperty()));
			nodes.add(topEvaluator.evaluate(axiom.getDomain()));

			addOutputs(graph.createAndNode(nodes), axiom);
		}

		public void visit(OWLDataPropertyRangeAxiom axiom) {
			// do nothing
		}

		public void visit(OWLSubDataPropertyOfAxiom axiom) {
			Node subNode = graph.createEntityNode(axiom.getSubProperty().asOWLDataProperty());
			Node supNode = graph.createEntityNode(axiom.getSuperProperty().asOWLDataProperty());

			subNode.getOutputs().add(supNode);
		}

		public void visit(OWLDeclarationAxiom axiom) {
			// do nothing
		}

		@Override
		public void visit(final OWLDatatypeDefinitionAxiom axiom) {
			// do nothing
		}

		public void visit(OWLDifferentIndividualsAxiom axiom) {
			// do nothing
		}

		public void visit(OWLDisjointClassesAxiom axiom) {
			processDisjoints(axiom, axiom.getClassExpressions());
		}

		protected void processDisjoints(OWLAxiom axiom, Set<OWLClassExpression> desc) {
			OWLClassExpression descriptions[] = desc.toArray(new OWLClassExpression[0]);
			Set<Node> or = new HashSet<Node>();
			for (int i = 0; i < descriptions.length - 1; i++) {
				for (int j = i; j < descriptions.length; j++) {
					Node n1 = bottomEvaluator.evaluate(descriptions[i]);
					Node n2 = bottomEvaluator.evaluate(descriptions[j]);

					or.add(graph.createAndNode(SetUtils.create(n1, n2)));
				}
			}

			if (!or.isEmpty()) {
				if (or.size() == 1) {
					addOutputs(or.iterator().next(), axiom);
				}
				else {
					addOutputs(graph.createOrNode(or), axiom);
				}
			}
		}

		public void visit(OWLDisjointDataPropertiesAxiom axiom) {
			// FIXME not implemented
		}

		public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
			// FIXME not implemented
		}

		public void visit(OWLDisjointUnionAxiom axiom) {
			processDisjoints(axiom, axiom.getClassExpressions());
			processEquivalent(axiom, axiom.getOWLClass(), OWL.or(axiom.getClassExpressions()));
		}

		public void visit(OWLEquivalentClassesAxiom axiom) {
			Iterator<OWLClassExpression> eqs = axiom.getClassExpressions().iterator();
			OWLClassExpression c1 = eqs.next();

			// if the axiom is a singleton we can ignore it. a concept
			// being equivalent to itself has no effect.
			if (!eqs.hasNext()) {
				return;
			}

			OWLClassExpression c2 = eqs.next();

			if (eqs.hasNext()) {
				throw new UnsupportedOperationException(
					                                       "OWLEquivalentClassesAxiom with more than 2 elements");
			}

			processEquivalent(axiom, c1, c2);
		}

		protected void processEquivalent(OWLAxiom axiom, OWLClassExpression c1, OWLClassExpression c2) {
			Set<Node> nodes1 = new HashSet<Node>();
			nodes1.add(topEvaluator.evaluate(c1));
			nodes1.add(topEvaluator.evaluate(c2));

			Set<Node> nodes2 = new HashSet<Node>();
			nodes2.add(bottomEvaluator.evaluate(c1));
			nodes2.add(bottomEvaluator.evaluate(c2));

			Node or1 = graph.createOrNode(nodes1);
			Node or2 = graph.createOrNode(nodes2);

			Node result = graph.createAndNode(SetUtils.create(or1, or2));

			addOutputs(result, axiom);
		}

		public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
			addOutputs(axiom);
		}

		public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
			addOutputs(axiom);
		}

		public void visit(OWLFunctionalDataPropertyAxiom axiom) {
			// unary axiom has no effect
		}

		public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
			// unary axiom has no effect
		}

		public void visit(OWLImportsDeclaration axiom) {
			// nothing to do with declarations
		}

		public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
			// unary axiom has no effect
		}

		public void visit(OWLInverseObjectPropertiesAxiom axiom) {
			addOutputs(axiom);
		}

		@Override
		public void visit(final OWLHasKeyAxiom theOWLHasKeyAxiom) {
			// do nothing
		}

		public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
			// unary axiom has no effect
		}

		public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
			addOutputs(axiom);
		}

		public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
			addOutputs(axiom);
		}

		public void visit(OWLObjectPropertyAssertionAxiom axiom) {
			addOutputs(axiom);
		}

		public void visit(OWLSubPropertyChainOfAxiom axiom) {
			Set<Node> nodes = new HashSet<Node>();

			for (OWLObjectPropertyExpression p : axiom.getPropertyChain()) {
				nodes.add(graph.createEntityNode(p.getNamedProperty()));
			}

			addOutputs(graph.createAndNode(nodes), axiom);
		}

		public void visit(OWLObjectPropertyDomainAxiom axiom) {
			Set<Node> nodes = new HashSet<Node>();

			nodes.add(graph.createEntityNode(axiom.getProperty().getNamedProperty()));
			nodes.add(topEvaluator.evaluate(axiom.getDomain()));

			addOutputs(graph.createAndNode(nodes), axiom);
		}

		public void visit(OWLObjectPropertyRangeAxiom axiom) {
			Set<Node> nodes = new HashSet<Node>();

			nodes.add(graph.createEntityNode(axiom.getProperty().getNamedProperty()));
			nodes.add(topEvaluator.evaluate(axiom.getRange()));

			addOutputs(graph.createAndNode(nodes), axiom);
		}

		public void visit(OWLSubObjectPropertyOfAxiom axiom) {
			Node subNode = graph.createEntityNode(axiom.getSubProperty().getNamedProperty());
			Node supNode = graph.createEntityNode(axiom.getSuperProperty().getNamedProperty());

			subNode.addOutput(supNode);
		}

		public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
			// unary axiom has no effect
		}

		public void visit(OWLSameIndividualAxiom axiom) {
			addOutputs(axiom);
		}

		public void visit(OWLSubClassOfAxiom axiom) {
			Set<Node> nodes = new HashSet<Node>();

			nodes.add(topEvaluator.evaluate(axiom.getSuperClass()));
			nodes.add(bottomEvaluator.evaluate(axiom.getSubClass()));

			if (!nodes.isEmpty()) {
				addOutputs(graph.createAndNode(nodes), axiom);
			}
		}

		public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
			// unary axiom has no effect
		}

		public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
			// unary axiom has no effect
		}

		public void visit(SWRLRule axiom) {
			// nothing to do with rules
		}
	}

	private class BottomEvaluator implements OWLClassExpressionVisitor {

		private Node node;

		public BottomEvaluator() {
		}

		public Node evaluate(OWLClassExpression desc) {
			// reset the result first
			node = null;

			desc.accept(this);

			// a null value indicates error
			if (node == null) {
				throw new IllegalStateException("Evaluation returned null");
			}

			return node;
		}

		public void visit(OWLClass desc) {
			node = desc.equals(OWL.Nothing)
			       ? START_NODE
			       : desc.equals(OWL.Thing)
			         ? NULL_NODE
			         : graph.createEntityNode(desc);
		}

		public void visit(OWLDataAllValuesFrom desc) {
			node = START_NODE;
		}

		public void visit(OWLDataExactCardinality desc) {
			node = START_NODE;
		}

		public void visit(OWLDataMaxCardinality desc) {
			node = (desc.getCardinality() == 0)
			       ? graph.createEntityNode(desc.getProperty().asOWLDataProperty())
			       : START_NODE;
		}

		public void visit(OWLDataMinCardinality desc) {
			// TODO: Special handling for the n == 0 case
			node = graph.createEntityNode(desc.getProperty().asOWLDataProperty());
		}

		public void visit(OWLDataSomeValuesFrom desc) {
			node = graph.createEntityNode(desc.getProperty().asOWLDataProperty());
		}

		public void visit(OWLDataHasValue desc) {
			node = graph.createEntityNode(desc.getProperty().asOWLDataProperty());
		}

		public void visit(OWLObjectAllValuesFrom desc) {
			node = START_NODE;
		}

		public void visit(OWLObjectComplementOf desc) {
			node = topEvaluator.evaluate(desc.getOperand());
		}

		public void visit(OWLObjectExactCardinality desc) {
			node = START_NODE;
		}

		public void visit(OWLObjectIntersectionOf desc) {
			Set<Node> inputNodes = new HashSet<Node>();
			for (OWLClassExpression c : desc.getOperands()) {
				Node conjNode = evaluate(c);
				inputNodes.add(conjNode);
			}

			if (!inputNodes.isEmpty()) {
				node = graph.createAndNode(inputNodes);
			}
		}

		public void visit(OWLObjectMaxCardinality desc) {
			node = (desc.getCardinality() == 0)
			       ? graph.createEntityNode(desc.getProperty().getNamedProperty())
			       : START_NODE;
		}

		public void visit(OWLObjectMinCardinality desc) {
			// TODO: Special handling for the n == 0 case
			node = graph.createEntityNode(desc.getProperty().getNamedProperty());
		}

		public void visit(OWLObjectOneOf desc) {
			node = START_NODE;
		}

		public void visit(OWLObjectHasSelf desc) {
			node = graph.createEntityNode(desc.getProperty().getNamedProperty());
		}

		public void visit(OWLObjectSomeValuesFrom desc) {
			Set<Node> inputNodes = new HashSet<Node>();

			inputNodes.add(graph.createEntityNode(desc.getProperty().getNamedProperty()));
			inputNodes.add(evaluate(desc.getFiller()));

			node = graph.createAndNode(inputNodes);
		}

		public void visit(OWLObjectUnionOf desc) {
			Set<Node> inputNodes = new HashSet<Node>();
			for (OWLClassExpression disj : desc.getOperands()) {
				Node disjNode = evaluate(disj);
				inputNodes.add(disjNode);
			}

			node = graph.createOrNode(inputNodes);
		}

		public void visit(OWLObjectHasValue desc) {
			node = graph.createEntityNode(desc.getProperty().getNamedProperty());
		}
	}

	private class TopEvaluator implements OWLClassExpressionVisitor {

		private Node node;

		public TopEvaluator() {
		}

		public Node evaluate(OWLClassExpression desc) {
			// reset the result first
			node = null;

			desc.accept(this);

			// a null value indicates error
			if (node == null) {
				throw new IllegalStateException("Evaluation returned null");
			}

			return node;
		}

		public void visit(OWLClass desc) {
			node = desc.equals(OWL.Thing)
			       ? NULL_NODE
			       : START_NODE;
		}

		public void visit(OWLDataAllValuesFrom desc) {
			node = graph.createEntityNode(desc.getProperty().asOWLDataProperty());
		}

		public void visit(OWLDataExactCardinality desc) {
			node = START_NODE;
		}

		public void visit(OWLDataMaxCardinality desc) {
			// TODO: Special handling for the n == 0 case
			node = graph.createEntityNode(desc.getProperty().asOWLDataProperty());
		}

		public void visit(OWLDataMinCardinality desc) {
			node = START_NODE;
		}

		public void visit(OWLDataSomeValuesFrom desc) {
			node = START_NODE;
		}

		public void visit(OWLDataHasValue desc) {
			node = START_NODE;
		}

		public void visit(OWLObjectAllValuesFrom desc) {
			Set<Node> inputNodes = new HashSet<Node>();

			inputNodes.add(graph.createEntityNode(desc.getProperty().getNamedProperty()));
			inputNodes.add(evaluate(desc.getFiller()));

			node = graph.createAndNode(inputNodes);
		}

		public void visit(OWLObjectComplementOf desc) {
			node = bottomEvaluator.evaluate(desc.getOperand());
		}

		public void visit(OWLObjectExactCardinality desc) {
			node = START_NODE;
		}

		public void visit(OWLObjectIntersectionOf desc) {
			Set<Node> inputNodes = new HashSet<Node>();
			for (OWLClassExpression conj : desc.getOperands()) {
				Node conjNode = evaluate(conj);
				inputNodes.add(conjNode);
			}

			node = graph.createOrNode(inputNodes);
		}

		public void visit(OWLObjectMaxCardinality desc) {
			// TODO: Special handling for the n == 0 case
			node = graph.createEntityNode(desc.getProperty().getNamedProperty());
		}

		public void visit(OWLObjectMinCardinality desc) {
			node = START_NODE;
		}

		public void visit(OWLObjectOneOf desc) {
			node = START_NODE;
		}

		public void visit(OWLObjectHasSelf desc) {
			node = NULL_NODE;
		}

		public void visit(OWLObjectSomeValuesFrom desc) {
			node = START_NODE;
		}

		public void visit(OWLObjectUnionOf desc) {
			Set<Node> inputNodes = new HashSet<Node>();
			for (OWLClassExpression disj : desc.getOperands()) {
				Node disjNode = evaluate(disj);
				inputNodes.add(disjNode);
			}

			if (!inputNodes.isEmpty()) {
				node = graph.createAndNode(inputNodes);
			}
		}

		public void visit(OWLObjectHasValue desc) {
			node = START_NODE;
		}
	}

	private ReachabilityGraph graph = new ReachabilityGraph();

	private AxiomVisitor axiomVisitor = new AxiomVisitor();

	private BottomEvaluator bottomEvaluator = new BottomEvaluator();

	private TopEvaluator topEvaluator = new TopEvaluator();

	private final Node NULL_NODE = graph.getNullNode();

	private final Node START_NODE = graph.getStartNode();

	public void addAxiom(OWLAxiom axiom) {
		axiom.accept(axiomVisitor);
	}

	private void addOutputs(Node node, OWLAxiom axiom) {
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

		if (node.equals(NULL_NODE)) {
			return;
		}

		Set<OWLEntity> entities = OntologyUtils.getSignature(axiom);

		for (OWLEntity entity : entities) {
			Node outNode = graph.createEntityNode(entity);
			node.addOutput(outNode);
		}
	}

	private void addOutputs(OWLAxiom axiom) {
		Set<OWLEntity> signature = OntologyUtils.getSignature(axiom);
		OWLEntity[] entities = signature.toArray(new OWLEntity[signature.size()]);
		for (int i = 0, n = entities.length; i < n - 1; i++) {
			Node n1 = graph.createEntityNode(entities[i]);
			for (int j = i + 1; j < n; j++) {
				Node n2 = graph.createEntityNode(entities[j]);
				n1.addOutput(n2);
				n2.addOutput(n1);
			}
		}
	}

	public ReachabilityGraph build() {
		graph.simplify();
		return graph;
	}
}