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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.katk.tools.Log;
import org.mindswap.pellet.utils.SetUtils;
import org.semanticweb.owlapi.model.AsOWLNamedIndividual;
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
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
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
	public static final Logger _logger = Log.getLogger(GraphBuilder.class);

	private class AxiomVisitor implements OWLAxiomVisitor
	{

		public AxiomVisitor()
		{
		}

		@Override
		public void visit(final OWLClassAssertionAxiom axiom)
		{
			if (axiom.getIndividual().isAnonymous())
				return;
			final Node node = _bottomEvaluator.evaluate(axiom.getClassExpression());
			addOutputs(node, axiom);
			addOutputs(_graph.createEntityNode(axiom.getIndividual()), axiom);
		}

		@Override
		public void visit(final OWLDataPropertyAssertionAxiom axiom)
		{
			addOutputs(axiom);
		}

		@Override
		public void visit(final OWLDataPropertyDomainAxiom axiom)
		{
			final Set<Node> nodes = new HashSet<>();

			nodes.add(_graph.createEntityNode(axiom.getProperty().asOWLDataProperty()));
			nodes.add(_topEvaluator.evaluate(axiom.getDomain()));

			addOutputs(_graph.createAndNode(nodes), axiom);
		}

		@Override
		public void visit(final OWLSubDataPropertyOfAxiom axiom)
		{
			final Node subNode = _graph.createEntityNode(axiom.getSubProperty().asOWLDataProperty());
			final Node supNode = _graph.createEntityNode(axiom.getSuperProperty().asOWLDataProperty());

			subNode.getOutputs().add(supNode);
		}

		@Override
		public void visit(final OWLDisjointClassesAxiom axiom)
		{
			processDisjoints(axiom, axiom.classExpressions());
		}

		protected void processDisjoints(final OWLAxiom axiom, final Stream<OWLClassExpression> desc)
		{
			final OWLClassExpression descriptions[] = desc.toArray(OWLClassExpression[]::new);
			final Set<Node> or = new HashSet<>();
			for (int i = 0; i < descriptions.length - 1; i++)
				for (int j = i; j < descriptions.length; j++)
				{
					final Node n1 = _bottomEvaluator.evaluate(descriptions[i]);
					final Node n2 = _bottomEvaluator.evaluate(descriptions[j]);

					or.add(_graph.createAndNode(SetUtils.create(n1, n2)));
				}

			if (!or.isEmpty())
				if (or.size() == 1)
					addOutputs(or.iterator().next(), axiom);
				else
					addOutputs(_graph.createOrNode(or), axiom);
		}

		@Override
		public void visit(final OWLDisjointUnionAxiom axiom)
		{
			processDisjoints(axiom, axiom.classExpressions());
			processEquivalent(axiom, axiom.getOWLClass(), OWL.or(axiom.classExpressions()));
		}

		@Override
		public void visit(final OWLEquivalentClassesAxiom axiom)
		{
			final Iterator<OWLClassExpression> eqs = axiom.classExpressions().iterator();
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
			final Set<Node> nodes1 = new HashSet<>();
			nodes1.add(_topEvaluator.evaluate(c1));
			nodes1.add(_topEvaluator.evaluate(c2));

			final Set<Node> nodes2 = new HashSet<>();
			nodes2.add(_bottomEvaluator.evaluate(c1));
			nodes2.add(_bottomEvaluator.evaluate(c2));

			final Node or1 = _graph.createOrNode(nodes1);
			final Node or2 = _graph.createOrNode(nodes2);

			final Node result = _graph.createAndNode(SetUtils.create(or1, or2));

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
		public void visit(final OWLInverseObjectPropertiesAxiom axiom)
		{
			addOutputs(axiom);
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
			final Set<Node> nodes = new HashSet<>();

			for (final OWLObjectPropertyExpression p : axiom.getPropertyChain())
				nodes.add(_graph.createEntityNode(p.getNamedProperty()));

			addOutputs(_graph.createAndNode(nodes), axiom);
		}

		@Override
		public void visit(final OWLObjectPropertyDomainAxiom axiom)
		{
			final Set<Node> nodes = new HashSet<>();

			nodes.add(_graph.createEntityNode(axiom.getProperty().getNamedProperty()));
			nodes.add(_topEvaluator.evaluate(axiom.getDomain()));

			addOutputs(_graph.createAndNode(nodes), axiom);
		}

		@Override
		public void visit(final OWLObjectPropertyRangeAxiom axiom)
		{
			final Set<Node> nodes = new HashSet<>();

			nodes.add(_graph.createEntityNode(axiom.getProperty().getNamedProperty()));
			nodes.add(_topEvaluator.evaluate(axiom.getRange()));

			addOutputs(_graph.createAndNode(nodes), axiom);
		}

		@Override
		public void visit(final OWLSubObjectPropertyOfAxiom axiom)
		{
			final Node subNode = _graph.createEntityNode(axiom.getSubProperty().getNamedProperty());
			final Node supNode = _graph.createEntityNode(axiom.getSuperProperty().getNamedProperty());

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
			final Set<Node> nodes = new HashSet<>();

			nodes.add(_topEvaluator.evaluate(axiom.getSuperClass()));
			nodes.add(_bottomEvaluator.evaluate(axiom.getSubClass()));

			if (!nodes.isEmpty())
				addOutputs(_graph.createAndNode(nodes), axiom);
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
			node = desc.equals(OWL.Nothing) ? START_NODE : desc.equals(OWL.Thing) ? NULL_NODE : _graph.createEntityNode(desc);
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
			node = (desc.getCardinality() == 0) ? _graph.createEntityNode(desc.getProperty().asOWLDataProperty()) : START_NODE;
		}

		@Override
		public void visit(final OWLDataMinCardinality desc)
		{
			// TODO: Special handling for the n == 0 case
			node = _graph.createEntityNode(desc.getProperty().asOWLDataProperty());
		}

		@Override
		public void visit(final OWLDataSomeValuesFrom desc)
		{
			node = _graph.createEntityNode(desc.getProperty().asOWLDataProperty());
		}

		@Override
		public void visit(final OWLDataHasValue desc)
		{
			node = _graph.createEntityNode(desc.getProperty().asOWLDataProperty());
		}

		@Override
		public void visit(final OWLObjectAllValuesFrom desc)
		{
			node = START_NODE;
		}

		@Override
		public void visit(final OWLObjectComplementOf desc)
		{
			node = _topEvaluator.evaluate(desc.getOperand());
		}

		@Override
		public void visit(final OWLObjectExactCardinality desc)
		{
			node = START_NODE;
		}

		@Override
		public void visit(final OWLObjectIntersectionOf desc)
		{
			final Set<Node> inputNodes = desc.operands().map(this::evaluate).collect(Collectors.toSet());

			if (!inputNodes.isEmpty())
				node = _graph.createAndNode(inputNodes);
		}

		@Override
		public void visit(final OWLObjectMaxCardinality desc)
		{
			node = (desc.getCardinality() == 0) ? _graph.createEntityNode(desc.getProperty().getNamedProperty()) : START_NODE;
		}

		@Override
		public void visit(final OWLObjectMinCardinality desc)
		{
			// TODO: Special handling for the n == 0 case
			node = _graph.createEntityNode(desc.getProperty().getNamedProperty());
		}

		@Override
		public void visit(final OWLObjectOneOf desc)
		{
			node = START_NODE;
		}

		@Override
		public void visit(final OWLObjectHasSelf desc)
		{
			node = _graph.createEntityNode(desc.getProperty().getNamedProperty());
		}

		@Override
		public void visit(final OWLObjectSomeValuesFrom desc)
		{
			final Set<Node> inputNodes = new HashSet<>();

			inputNodes.add(_graph.createEntityNode(desc.getProperty().getNamedProperty()));
			inputNodes.add(evaluate(desc.getFiller()));

			node = _graph.createAndNode(inputNodes);
		}

		@Override
		public void visit(final OWLObjectUnionOf desc)
		{
			final Set<Node> inputNodes = desc.operands().map(this::evaluate).collect(Collectors.toSet());
			node = _graph.createOrNode(inputNodes);
		}

		@Override
		public void visit(final OWLObjectHasValue desc)
		{
			node = _graph.createEntityNode(desc.getProperty().getNamedProperty());
		}
	}

	private class TopEvaluator implements OWLClassExpressionVisitor
	{

		private Node _node;

		public TopEvaluator()
		{
		}

		public Node evaluate(final OWLClassExpression desc)
		{
			// reset the result first
			_node = null;

			desc.accept(this);

			// a null value indicates error
			if (_node == null)
				throw new IllegalStateException("Evaluation returned null");

			return _node;
		}

		@Override
		public void visit(final OWLClass desc)
		{
			_node = desc.equals(OWL.Thing) ? NULL_NODE : START_NODE;
		}

		@Override
		public void visit(final OWLDataAllValuesFrom desc)
		{
			_node = _graph.createEntityNode(desc.getProperty().asOWLDataProperty());
		}

		@Override
		public void visit(final OWLDataExactCardinality desc)
		{
			_node = START_NODE;
		}

		@Override
		public void visit(final OWLDataMaxCardinality desc)
		{
			// TODO: Special handling for the n == 0 case
			_node = _graph.createEntityNode(desc.getProperty().asOWLDataProperty());
		}

		@Override
		public void visit(final OWLDataMinCardinality desc)
		{
			_node = START_NODE;
		}

		@Override
		public void visit(final OWLDataSomeValuesFrom desc)
		{
			_node = START_NODE;
		}

		@Override
		public void visit(final OWLDataHasValue desc)
		{
			_node = START_NODE;
		}

		@Override
		public void visit(final OWLObjectAllValuesFrom desc)
		{
			final Set<Node> inputNodes = new HashSet<>();

			inputNodes.add(_graph.createEntityNode(desc.getProperty().getNamedProperty()));
			inputNodes.add(evaluate(desc.getFiller()));

			_node = _graph.createAndNode(inputNodes);
		}

		@Override
		public void visit(final OWLObjectComplementOf desc)
		{
			_node = _bottomEvaluator.evaluate(desc.getOperand());
		}

		@Override
		public void visit(final OWLObjectExactCardinality desc)
		{
			_node = START_NODE;
		}

		@Override
		public void visit(final OWLObjectIntersectionOf desc)
		{
			final Set<Node> inputNodes = desc.operands().map(this::evaluate).collect(Collectors.toSet());
			_node = _graph.createOrNode(inputNodes);
		}

		@Override
		public void visit(final OWLObjectMaxCardinality desc)
		{
			// TODO: Special handling for the n == 0 case
			_node = _graph.createEntityNode(desc.getProperty().getNamedProperty());
		}

		@Override
		public void visit(final OWLObjectMinCardinality desc)
		{
			_node = START_NODE;
		}

		@Override
		public void visit(final OWLObjectOneOf desc)
		{
			_node = START_NODE;
		}

		@Override
		public void visit(final OWLObjectHasSelf desc)
		{
			_node = NULL_NODE;
		}

		@Override
		public void visit(final OWLObjectSomeValuesFrom desc)
		{
			_node = START_NODE;
		}

		@Override
		public void visit(final OWLObjectUnionOf desc)
		{
			final Set<Node> inputNodes = desc.operands().map(this::evaluate).collect(Collectors.toSet());
			if (!inputNodes.isEmpty())
				_node = _graph.createAndNode(inputNodes);
		}

		@Override
		public void visit(final OWLObjectHasValue desc)
		{
			_node = START_NODE;
		}
	}

	private final ReachabilityGraph<AsOWLNamedIndividual> _graph = new ReachabilityGraph<>();

	private final AxiomVisitor _axiomVisitor = new AxiomVisitor();

	private final BottomEvaluator _bottomEvaluator = new BottomEvaluator();

	private final TopEvaluator _topEvaluator = new TopEvaluator();

	private final Node NULL_NODE = _graph.getNullNode();

	private final Node START_NODE = _graph.getStartNode();

	public void addAxiom(final OWLAxiom axiom)
	{
		axiom.accept(_axiomVisitor);
	}

	private void addOutputs(final Node node, final OWLAxiom axiom)
	{
		// the following if statement was added to be consistent
		// with earlier implementation that only considered axioms
		// whose signature had a common element with the _current
		// signature of the module. this behavior is not consistent
		// with the theoretical description of the modularity
		// algorithm and may cause incorrect results in incremental
		// classification (see deleteNonLocal() test inside
		// SimpleCorrectnessTest)
		//		if( _node.equals( START_NODE ) ) {
		//			_logger.warn( "Non-local axiom: " + axiom );
		//			addOutputs( axiom );
		//			return;
		//		}

		if (node.equals(NULL_NODE))
			return;

		OntologyUtils.signature(axiom).map(_graph::createEntityNode).forEach(node::addOutput);
	}

	private void addOutputs(final OWLAxiom axiom)
	{
		final OWLEntity[] entities = OntologyUtils.signature(axiom).toArray(OWLEntity[]::new);

		for (int i = 0, n = entities.length; i < n - 1; i++)
		{
			final Node n1 = _graph.createEntityNode(entities[i]);
			for (int j = i + 1; j < n; j++)
			{
				final Node n2 = _graph.createEntityNode(entities[j]);
				n1.addOutput(n2);
				n2.addOutput(n1);
			}
		}
	}

	public ReachabilityGraph<AsOWLNamedIndividual> build()
	{
		_graph.simplify();
		return _graph;
	}
}
