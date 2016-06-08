// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.lintpattern.ontology;

import com.clarkparsia.pellint.format.LintFormat;
import com.clarkparsia.pellint.format.SimpleLintFormat;
import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.model.LintFactory;
import com.clarkparsia.pellint.model.Severity;
import com.clarkparsia.pellint.util.OptimizedDirectedMultigraph;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.KosarajuStrongConnectivityInspector;
import org.jgrapht.alg.TransitiveClosure;
import org.jgrapht.alg.interfaces.StrongConnectivityAlgorithm;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.StringNameProvider;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedSubgraph;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;

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
 * @author Harris Lin
 */
public class ExistentialExplosionPattern implements OntologyLintPattern
{
	private static final LintFormat DEFAULT_LINT_FORMAT = new SimpleLintFormat();

	private int _maxTreeSize = 10000;

	private List<Lint> _accumulatedLints;
	private LintFactory _LintFactory;

	@Override
	public String getName()
	{
		return getClass().getSimpleName() + " (MaxTreeSize = " + _maxTreeSize + ")";
	}

	@Override
	public String getDescription()
	{
		return "Concepts/Individuals are involved in a large some/min/exact value restrictions tree/loop - maximum recommended number of generated _nodes is " + _maxTreeSize;
	}

	@Override
	public boolean isFixable()
	{
		return false;
	}

	@Override
	public LintFormat getDefaultLintFormat()
	{
		return DEFAULT_LINT_FORMAT;
	}

	public void setMaxTreeSize(final int value)
	{
		_maxTreeSize = value;
	}

	@Override
	public List<Lint> match(final OWLOntology ontology)
	{
		_accumulatedLints = new ArrayList<>();
		_LintFactory = new LintFactory(this, ontology);

		//Stage 1 - strongly connected components on asserted existential relations
		final OptimizedDirectedMultigraph<OWLClass> existentialRestrictionGraph = extractGraphFromSubsumptionAxiomsWith(ontology, new ExistentialClassCollector());
		estimateTreeSizesForCycles(existentialRestrictionGraph);
		if (!_accumulatedLints.isEmpty())
			return _accumulatedLints;

		//Stage 2 - strongly connected components on asserted and inferred (through subclasses) existential relations
		//used as a SimpleDirectedGraph - ignoring weights
		final OptimizedDirectedMultigraph<OWLClass> toldSubsumptionGraph = extractGraphFromSubsumptionAxiomsWith(ontology, new NamedClassCollector());
		TransitiveClosure.INSTANCE.closeSimpleDirectedGraph(toldSubsumptionGraph);
		addInheritedEdges(existentialRestrictionGraph, toldSubsumptionGraph);
		estimateTreeSizesForCycles(existentialRestrictionGraph);

		if (!_accumulatedLints.isEmpty())
			return _accumulatedLints;

		//Stage 3 - strongly connected components on asserted and inferred (through subclasses) existential relations, multiplied by the number of individuals
		final Map<OWLClass, Integer> individualCounts = countIndividuals(ontology);
		estimateTreeSizesForCyclesWithIndividuals(existentialRestrictionGraph, toldSubsumptionGraph, individualCounts);

		if (!_accumulatedLints.isEmpty())
			return _accumulatedLints;

		//Stage 4 - remove cycles, then calculate the size of the weighted tree, multiplied by the number of individuals
		removeCyclesAndEstimateTreeSizesWithIndividuals(existentialRestrictionGraph, individualCounts);

		return _accumulatedLints;
	}

	@SuppressWarnings("unused")
	private static <V, E> void printGraph(final Graph<V, E> graph)
	{
		final DOTExporter<V, E> exp = new DOTExporter<>(new StringNameProvider<V>(), null, null);
		exp.export(new BufferedWriter(new PrintWriter(System.out)), graph);
	}

	private static OptimizedDirectedMultigraph<OWLClass> extractGraphFromSubsumptionAxiomsWith(final OWLOntology ontology, final ClassCollector visitor)
	{
		final OptimizedDirectedMultigraph<OWLClass> graph = new OptimizedDirectedMultigraph<>();

		ontology.axioms(AxiomType.SUBCLASS_OF).forEach(axiom -> // 
				processSubsumption(graph, axiom.getSubClass(), axiom.getSuperClass(), visitor));

		ontology.axioms(AxiomType.EQUIVALENT_CLASSES).forEach(axiom ->
		{
			final Set<OWLClassExpression> equivalences = axiom.classExpressions().collect(Collectors.toSet());
			for (final OWLClassExpression equivalence1 : equivalences)
				for (final OWLClassExpression equivalence2 : equivalences)
					if (equivalence1 != equivalence2)
						processSubsumption(graph, equivalence1, equivalence2, visitor);
		});

		return graph;
	}

	private static void processSubsumption(final OptimizedDirectedMultigraph<OWLClass> graph, final OWLClassExpression subDesc, final OWLClassExpression superDesc, final ClassCollector visitor)
	{
		if (subDesc.isAnonymous())
			return;
		final OWLClass subClass = subDesc.asOWLClass();

		visitor.reset();
		superDesc.accept(visitor);
		for (final OWLClass superClass : visitor.getCollectedClasses())
			if (!subClass.equals(superClass))
			{
				graph.addVertex(subClass);
				graph.addVertex(superClass);
				graph.addEdge(subClass, superClass);
			}
	}

	private static void addInheritedEdges(final OptimizedDirectedMultigraph<OWLClass> existentialRestrictionGraph, final OptimizedDirectedMultigraph<OWLClass> toldSubsumptionGraph)
	{
		for (final OWLClass superClass : toldSubsumptionGraph.vertexSet())
		{
			if (!existentialRestrictionGraph.containsVertex(superClass))
				continue;

			final Set<DefaultWeightedEdge> ancestorEdges = existentialRestrictionGraph.outgoingEdgesOf(superClass);
			for (final DefaultWeightedEdge subClassEdge : toldSubsumptionGraph.incomingEdgesOf(superClass))
			{
				final OWLClass subClass = toldSubsumptionGraph.getEdgeSource(subClassEdge);
				if (!existentialRestrictionGraph.containsVertex(subClass))
					continue;

				for (final DefaultWeightedEdge ancestorEdge : ancestorEdges)
				{
					final int ancestorEdgeCount = existentialRestrictionGraph.getEdgeMultiplicity(ancestorEdge);
					final OWLClass ancestorEdgeTarget = existentialRestrictionGraph.getEdgeTarget(ancestorEdge);
					if (!subClass.equals(ancestorEdgeTarget))
						existentialRestrictionGraph.addEdge(subClass, ancestorEdgeTarget, ancestorEdgeCount);
				}
			}
		}
	}

	private static Map<OWLClass, Integer> countIndividuals(final OWLOntology ontology)
	{
		final Map<OWLClass, Integer> individualCount = new HashMap<>();
		ontology.axioms(AxiomType.CLASS_ASSERTION).forEach(axiom ->
		{
			final OWLClassExpression desc = axiom.getClassExpression();
			if (!desc.isAnonymous())
			{
				final OWLClass assertedClass = desc.asOWLClass();
				Integer oldCount = individualCount.get(assertedClass);
				if (oldCount == null)
					oldCount = 0;
				individualCount.put(assertedClass, oldCount + 1);
			}
		});
		return individualCount;
	}

	private static int getMaxSizeOfCompleteGraphToIgnore(final int maxTreeSize)
	{
		int i = 1;
		while (Math.pow(i - 1, i) < maxTreeSize)
			i++;
		return i - 1;
	}

	private void estimateTreeSizesForCycles(final OptimizedDirectedMultigraph<OWLClass> existentialRestrictionGraph)
	{
		final int maxSizeOfCompleteGraphToIgnore = getMaxSizeOfCompleteGraphToIgnore(_maxTreeSize);

		final StrongConnectivityAlgorithm<OWLClass, DefaultWeightedEdge> connectivityInspector = new KosarajuStrongConnectivityInspector<>(existentialRestrictionGraph);
		for (final Set<OWLClass> connectedSet : connectivityInspector.stronglyConnectedSets())
		{
			if (connectedSet.size() <= maxSizeOfCompleteGraphToIgnore)
				continue;

			final DirectedSubgraph<OWLClass, DefaultWeightedEdge> subgraph = new DirectedSubgraph<>(existentialRestrictionGraph, connectedSet, null);
			double estimatedTreeSize = 1.0;
			for (final OWLClass owlClass : connectedSet)
				estimatedTreeSize *= subgraph.outDegreeOf(owlClass);

			if (estimatedTreeSize > _maxTreeSize)
			{
				final Lint lint = _LintFactory.make();
				lint.addAllParticipatingClasses(connectedSet);
				lint.setSeverity(new Severity(estimatedTreeSize));
				_accumulatedLints.add(lint);
			}
		}
	}

	private void estimateTreeSizesForCyclesWithIndividuals(final OptimizedDirectedMultigraph<OWLClass> existentialRestrictionGraph, final OptimizedDirectedMultigraph<OWLClass> toldSubsumptionGraph, final Map<OWLClass, Integer> individualCount)
	{
		final StrongConnectivityAlgorithm<OWLClass, DefaultWeightedEdge> connectivityInspector = new KosarajuStrongConnectivityInspector<>(existentialRestrictionGraph);
		for (final Set<OWLClass> connectedSet : connectivityInspector.stronglyConnectedSets())
		{
			if (connectedSet.size() <= 1)
				continue;

			final DirectedSubgraph<OWLClass, DefaultWeightedEdge> subgraph = new DirectedSubgraph<>(existentialRestrictionGraph, connectedSet, null);
			double estimatedTreeSize = 1.0;
			for (final OWLClass owlClass : connectedSet)
				estimatedTreeSize *= subgraph.outDegreeOf(owlClass);

			final Set<OWLClass> allSubclassesOfConnectedSet = new HashSet<>(connectedSet);
			for (final OWLClass owlClass : connectedSet)
			{
				if (!toldSubsumptionGraph.containsVertex(owlClass))
					continue;

				for (final DefaultWeightedEdge inEdge : toldSubsumptionGraph.incomingEdgesOf(owlClass))
					allSubclassesOfConnectedSet.add(toldSubsumptionGraph.getEdgeSource(inEdge));
			}

			int totalInvolvedIndividuals = 0;
			for (final Entry<OWLClass, Integer> entry : individualCount.entrySet())
				if (allSubclassesOfConnectedSet.contains(entry.getKey()))
					totalInvolvedIndividuals += entry.getValue();

			estimatedTreeSize *= totalInvolvedIndividuals;

			if (estimatedTreeSize > _maxTreeSize)
			{
				final Lint lint = _LintFactory.make();
				lint.addAllParticipatingClasses(connectedSet);
				lint.setSeverity(new Severity(estimatedTreeSize));
				_accumulatedLints.add(lint);
			}
		}
	}

	private void removeCyclesAndEstimateTreeSizesWithIndividuals(final OptimizedDirectedMultigraph<OWLClass> existentialRestrictionGraph, final Map<OWLClass, Integer> individualCounts)
	{
		final Map<OWLClass, Double> accumulatedChildren = new HashMap<>();

		final CycleDetector<OWLClass, DefaultWeightedEdge> cycleDetector = new CycleDetector<>(existentialRestrictionGraph);
		final Set<OWLClass> nodesInACycle = cycleDetector.findCycles();

		for (final OWLClass child : nodesInACycle)
		{
			Double childValue = accumulatedChildren.get(child);
			if (childValue == null)
				childValue = existentialRestrictionGraph.outDegreeOf(child) + 1.0;

			for (final DefaultWeightedEdge inEdge : existentialRestrictionGraph.incomingEdgesOf(child))
			{
				final int inEdgeCount = existentialRestrictionGraph.getEdgeMultiplicity(inEdge);
				final OWLClass parent = existentialRestrictionGraph.getEdgeSource(inEdge);
				Double oldValue = accumulatedChildren.get(parent);
				if (oldValue == null)
					oldValue = (double) existentialRestrictionGraph.outDegreeOf(parent);
				accumulatedChildren.put(parent, oldValue + (childValue * inEdgeCount));
			}
		}
		existentialRestrictionGraph.removeAllVertices(nodesInACycle);

		if (!existentialRestrictionGraph.vertexSet().isEmpty())
		{
			final EdgeReversedGraph<OWLClass, DefaultWeightedEdge> reversedForest = new EdgeReversedGraph<>(existentialRestrictionGraph);
			final TopologicalOrderIterator<OWLClass, DefaultWeightedEdge> bottomUpIt = new TopologicalOrderIterator<>(reversedForest);
			while (bottomUpIt.hasNext())
			{
				final OWLClass node = bottomUpIt.next();
				Double nodeSize = accumulatedChildren.get(node);
				if (nodeSize == null)
					nodeSize = 1.0;

				for (final DefaultWeightedEdge outEdge : existentialRestrictionGraph.outgoingEdgesOf(node))
				{
					final int outEdgeCount = existentialRestrictionGraph.getEdgeMultiplicity(outEdge);
					final OWLClass child = existentialRestrictionGraph.getEdgeTarget(outEdge);
					Double childValue = accumulatedChildren.get(child);
					if (childValue == null)
						childValue = 1.0;
					nodeSize += childValue * outEdgeCount;
				}

				accumulatedChildren.put(node, nodeSize);
			}
		}

		final Set<OWLClass> participatingClasses = new HashSet<>();
		double estimatedTotalTreeSize = 0.0;
		for (final Entry<OWLClass, Integer> entry : individualCounts.entrySet())
		{
			final OWLClass owlClass = entry.getKey();
			final int individualCount = entry.getValue();

			final Double childCount = accumulatedChildren.get(owlClass);
			if (childCount != null)
			{
				final double totalCount = childCount * individualCount;
				estimatedTotalTreeSize += totalCount;
				if (totalCount > 0.0)
					participatingClasses.add(owlClass);
			}
		}

		if (estimatedTotalTreeSize > _maxTreeSize)
		{
			final Lint lint = _LintFactory.make();
			lint.addAllParticipatingClasses(participatingClasses);
			lint.setSeverity(new Severity(estimatedTotalTreeSize));
			_accumulatedLints.add(lint);
		}
	}
}

abstract class ClassCollector implements OWLClassExpressionVisitor
{
	protected Set<OWLClass> _classes;

	public ClassCollector()
	{
		_classes = new HashSet<>();
	}

	public void reset()
	{
		_classes.clear();
	}

	public Set<OWLClass> getCollectedClasses()
	{
		return _classes;
	}
}

class NamedClassCollector extends ClassCollector
{
	@Override
	public void visit(final OWLClass desc)
	{
		_classes.add(desc);
	}

	@Override
	public void visit(final OWLObjectIntersectionOf desc)
	{
		desc.operands().forEach(op -> op.accept(NamedClassCollector.this));
	}

	@Override
	public void visit(final OWLObjectUnionOf desc)
	{
		desc.operands().forEach(op -> op.accept(NamedClassCollector.this));
	}
}

class ExistentialClassCollector extends ClassCollector
{
	@Override
	public void visit(final OWLObjectSomeValuesFrom desc)
	{
		visitObject(desc.getFiller());
	}

	@Override
	public void visit(final OWLObjectMinCardinality desc)
	{
		if (desc.getCardinality() > 0)
			visitObject(desc.getFiller());
	}

	@Override
	public void visit(final OWLObjectExactCardinality desc)
	{
		if (desc.getCardinality() > 0)
			visitObject(desc.getFiller());
	}

	@Override
	public void visit(final OWLObjectIntersectionOf desc)
	{
		desc.operands().forEach(op -> op.accept(ExistentialClassCollector.this));
	}

	@Override
	public void visit(final OWLObjectUnionOf desc)
	{
		desc.operands().forEach(op -> op.accept(ExistentialClassCollector.this));
	}

	private void visitObject(final OWLClassExpression filler)
	{
		if (!filler.isAnonymous())
			_classes.add(filler.asOWLClass());
	}
}
