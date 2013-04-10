// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.lintpattern.ontology;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jgrapht.Graph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.alg.TransitiveClosure;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.StringNameProvider;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedSubgraph;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;

import com.clarkparsia.pellint.format.LintFormat;
import com.clarkparsia.pellint.format.SimpleLintFormat;
import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.model.LintFactory;
import com.clarkparsia.pellint.model.Severity;
import com.clarkparsia.pellint.util.OptimizedDirectedMultigraph;

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
public class ExistentialExplosionPattern implements OntologyLintPattern {
	private static final LintFormat DEFAULT_LINT_FORMAT = new SimpleLintFormat();
	
	private int m_MaxTreeSize = 10000;
	
	private List<Lint> m_AccumulatedLints;
	private LintFactory m_LintFactory;
	
	public String getName() {
		return getClass().getSimpleName() + " (MaxTreeSize = " + m_MaxTreeSize + ")";
	}
	
	public String getDescription() {
		return "Concepts/Individuals are involved in a large some/min/exact value restrictions tree/loop - maximum recommended number of generated nodes is " + m_MaxTreeSize;
	}
	
	public boolean isFixable() {
		return false;
	}
	
	public LintFormat getDefaultLintFormat() {
		return DEFAULT_LINT_FORMAT;
	}

	public void setMaxTreeSize(int value) {
		m_MaxTreeSize = value;
	}
	
	public List<Lint> match(OWLOntology ontology) {
		m_AccumulatedLints = new ArrayList<Lint>();
		m_LintFactory = new LintFactory(this, ontology);

		//Stage 1 - strongly connected components on asserted existential relations
		OptimizedDirectedMultigraph<OWLClass> existentialRestrictionGraph = extractGraphFromSubsumptionAxiomsWith(ontology, new ExistentialClassCollector());
		estimateTreeSizesForCycles(existentialRestrictionGraph);
		if (!m_AccumulatedLints.isEmpty()) return m_AccumulatedLints;

		//Stage 2 - strongly connected components on asserted and inferred (through subclasses) existential relations
		//used as a SimpleDirectedGraph - ignoring weights
		OptimizedDirectedMultigraph<OWLClass> toldSubsumptionGraph = extractGraphFromSubsumptionAxiomsWith(ontology, new NamedClassCollector());
		TransitiveClosure.INSTANCE.closeSimpleDirectedGraph(toldSubsumptionGraph);
		addInheritedEdges(existentialRestrictionGraph, toldSubsumptionGraph);
		estimateTreeSizesForCycles(existentialRestrictionGraph);
		
		if (!m_AccumulatedLints.isEmpty()) return m_AccumulatedLints;
		
		//Stage 3 - strongly connected components on asserted and inferred (through subclasses) existential relations, multiplied by the number of individuals
		Map<OWLClass, Integer> individualCounts = countIndividuals(ontology);
		estimateTreeSizesForCyclesWithIndividuals(existentialRestrictionGraph, toldSubsumptionGraph, individualCounts);

		if (!m_AccumulatedLints.isEmpty()) return m_AccumulatedLints;

		//Stage 4 - remove cycles, then calculate the size of the weighted tree, multiplied by the number of individuals
		removeCyclesAndEstimateTreeSizesWithIndividuals(existentialRestrictionGraph, individualCounts);

		return m_AccumulatedLints;
	}

	@SuppressWarnings("unused")
	private static <V,E> void printGraph(Graph<V,E> graph) {
		DOTExporter<V,E> exp = new DOTExporter<V,E>(new StringNameProvider<V>(), null, null);
		exp.export(new BufferedWriter(new PrintWriter(System.out)), graph);
	}

	private static OptimizedDirectedMultigraph<OWLClass> extractGraphFromSubsumptionAxiomsWith(OWLOntology ontology, ClassCollector visitor) {
		OptimizedDirectedMultigraph<OWLClass> graph = new OptimizedDirectedMultigraph<OWLClass>();
		
		for (OWLSubClassOfAxiom axiom : ontology.getAxioms(AxiomType.SUBCLASS_OF)) {
			processSubsumption(graph, axiom.getSubClass(), axiom.getSuperClass(), visitor);
		}

		for (OWLEquivalentClassesAxiom axiom : ontology.getAxioms(AxiomType.EQUIVALENT_CLASSES)) {
			Set<OWLClassExpression> equivalences = axiom.getClassExpressions();
			for (OWLClassExpression equivalence1 : equivalences) {
				for (OWLClassExpression equivalence2 : equivalences) {
					if (equivalence1 != equivalence2) {
						processSubsumption(graph, equivalence1, equivalence2, visitor);
					}
				}
			}
		}
		
		return graph;
	}
	
	private static void processSubsumption(OptimizedDirectedMultigraph<OWLClass> graph, OWLClassExpression subDesc, OWLClassExpression superDesc, ClassCollector visitor) {
		if (subDesc.isAnonymous()) return;
		OWLClass subClass = subDesc.asOWLClass();
		
		visitor.reset();
		superDesc.accept(visitor);
		for (OWLClass superClass : visitor.getCollectedClasses()) {
			if (!subClass.equals(superClass)) {
				graph.addVertex(subClass);
				graph.addVertex(superClass);
				graph.addEdge(subClass, superClass);
			}
		}
	}
	
	private static void addInheritedEdges(OptimizedDirectedMultigraph<OWLClass> existentialRestrictionGraph, OptimizedDirectedMultigraph<OWLClass> toldSubsumptionGraph) {
		for (OWLClass superClass : toldSubsumptionGraph.vertexSet()) {
			if (!existentialRestrictionGraph.containsVertex(superClass)) continue;
			
			Set<DefaultWeightedEdge> ancestorEdges = existentialRestrictionGraph.outgoingEdgesOf(superClass);
			for (DefaultWeightedEdge subClassEdge : toldSubsumptionGraph.incomingEdgesOf(superClass)) {
				OWLClass subClass = toldSubsumptionGraph.getEdgeSource(subClassEdge);
				if (!existentialRestrictionGraph.containsVertex(subClass)) continue;
				
				for (DefaultWeightedEdge ancestorEdge : ancestorEdges) {
					int ancestorEdgeCount = existentialRestrictionGraph.getEdgeMultiplicity(ancestorEdge);
					OWLClass ancestorEdgeTarget = existentialRestrictionGraph.getEdgeTarget(ancestorEdge);
					if (!subClass.equals(ancestorEdgeTarget)) {
						existentialRestrictionGraph.addEdge(subClass, ancestorEdgeTarget, ancestorEdgeCount);
					}
				}
			}
		}
	}

	private static Map<OWLClass, Integer> countIndividuals(OWLOntology ontology) {
		Map<OWLClass, Integer> individualCount = new HashMap<OWLClass, Integer>();
		for (OWLClassAssertionAxiom axiom : ontology.getAxioms(AxiomType.CLASS_ASSERTION)) {
			OWLClassExpression desc = axiom.getClassExpression();
			if (!desc.isAnonymous()) {
				OWLClass assertedClass = desc.asOWLClass();
				Integer oldCount = individualCount.get(assertedClass);
				if (oldCount == null) {
					oldCount = 0;
				}
				individualCount.put(assertedClass, oldCount + 1);
			}
		}
		return individualCount;
	}

	private static int getMaxSizeOfCompleteGraphToIgnore(int maxTreeSize) {
		int i = 1;
		for (; Math.pow(i - 1, i) < maxTreeSize; i++) { }
		return i - 1;
	}


	
	private void estimateTreeSizesForCycles(OptimizedDirectedMultigraph<OWLClass> existentialRestrictionGraph) {
		int maxSizeOfCompleteGraphToIgnore = getMaxSizeOfCompleteGraphToIgnore(m_MaxTreeSize);
		
		StrongConnectivityInspector<OWLClass, DefaultWeightedEdge> connectivityInspector = new StrongConnectivityInspector<OWLClass, DefaultWeightedEdge>(existentialRestrictionGraph);
		for (Set<OWLClass> connectedSet : connectivityInspector.stronglyConnectedSets()) {
			if (connectedSet.size() <= maxSizeOfCompleteGraphToIgnore) continue;

			DirectedSubgraph<OWLClass, DefaultWeightedEdge> subgraph = new DirectedSubgraph<OWLClass, DefaultWeightedEdge>(existentialRestrictionGraph, connectedSet, null);
			double estimatedTreeSize = 1.0;
			for (OWLClass owlClass : connectedSet) {
				estimatedTreeSize *= subgraph.outDegreeOf(owlClass);
			}
			
			if (estimatedTreeSize > m_MaxTreeSize) {
				Lint lint = m_LintFactory.make();
				lint.addAllParticipatingClasses(connectedSet);
				lint.setSeverity(new Severity(estimatedTreeSize));
				m_AccumulatedLints.add(lint);
			}
		}
	}
	
	private void estimateTreeSizesForCyclesWithIndividuals(OptimizedDirectedMultigraph<OWLClass> existentialRestrictionGraph, OptimizedDirectedMultigraph<OWLClass> toldSubsumptionGraph, Map<OWLClass, Integer> individualCount) {
		StrongConnectivityInspector<OWLClass, DefaultWeightedEdge> connectivityInspector = new StrongConnectivityInspector<OWLClass, DefaultWeightedEdge>(existentialRestrictionGraph);
		for (Set<OWLClass> connectedSet : connectivityInspector.stronglyConnectedSets()) {
			if (connectedSet.size() <= 1) continue;

			DirectedSubgraph<OWLClass, DefaultWeightedEdge> subgraph = new DirectedSubgraph<OWLClass, DefaultWeightedEdge>(existentialRestrictionGraph, connectedSet, null);
			double estimatedTreeSize = 1.0;
			for (OWLClass owlClass : connectedSet) {
				estimatedTreeSize *= subgraph.outDegreeOf(owlClass);
			}
			
			Set<OWLClass> allSubclassesOfConnectedSet = new HashSet<OWLClass>(connectedSet);
			for (OWLClass owlClass : connectedSet) {
				if (!toldSubsumptionGraph.containsVertex(owlClass)) continue;
				
				for (DefaultWeightedEdge inEdge : toldSubsumptionGraph.incomingEdgesOf(owlClass)) {
					allSubclassesOfConnectedSet.add(toldSubsumptionGraph.getEdgeSource(inEdge));
				}
			}
			
			int totalInvolvedIndividuals = 0;
			for (Entry<OWLClass, Integer> entry : individualCount.entrySet()) {
				if (allSubclassesOfConnectedSet.contains(entry.getKey())) {
					totalInvolvedIndividuals += entry.getValue();
				}
			}
			
			estimatedTreeSize *= totalInvolvedIndividuals;
			
			if (estimatedTreeSize > m_MaxTreeSize) {
				Lint lint = m_LintFactory.make();
				lint.addAllParticipatingClasses(connectedSet);
				lint.setSeverity(new Severity(estimatedTreeSize));
				m_AccumulatedLints.add(lint);
			}
		}
	}
	
	private void removeCyclesAndEstimateTreeSizesWithIndividuals(OptimizedDirectedMultigraph<OWLClass> existentialRestrictionGraph, Map<OWLClass, Integer> individualCounts) {
		Map<OWLClass, Double> accumulatedChildren = new HashMap<OWLClass, Double>();
		
		CycleDetector<OWLClass, DefaultWeightedEdge> cycleDetector = new CycleDetector<OWLClass, DefaultWeightedEdge>(existentialRestrictionGraph);
		Set<OWLClass> nodesInACycle = cycleDetector.findCycles();

		for (OWLClass child : nodesInACycle) {
			Double childValue = accumulatedChildren.get(child);
			if (childValue == null) {
				childValue = existentialRestrictionGraph.outDegreeOf(child) + 1.0;
			}
			
			for (DefaultWeightedEdge inEdge : existentialRestrictionGraph.incomingEdgesOf(child)) {
				int inEdgeCount = existentialRestrictionGraph.getEdgeMultiplicity(inEdge);
				OWLClass parent = existentialRestrictionGraph.getEdgeSource(inEdge);
				Double oldValue = accumulatedChildren.get(parent);
				if (oldValue == null) {
					oldValue = (double)existentialRestrictionGraph.outDegreeOf(parent);
				}
				accumulatedChildren.put(parent, oldValue + (childValue * inEdgeCount));
			}
		}
		existentialRestrictionGraph.removeAllVertices(nodesInACycle);
		
		if (!existentialRestrictionGraph.vertexSet().isEmpty()) {
			EdgeReversedGraph<OWLClass, DefaultWeightedEdge> reversedForest = new EdgeReversedGraph<OWLClass, DefaultWeightedEdge>(existentialRestrictionGraph);
			TopologicalOrderIterator<OWLClass, DefaultWeightedEdge> bottomUpIt = new TopologicalOrderIterator<OWLClass, DefaultWeightedEdge>(reversedForest);
			while (bottomUpIt.hasNext()) {
				OWLClass node = bottomUpIt.next();
				Double nodeSize = accumulatedChildren.get(node);
				if (nodeSize == null) {
					nodeSize = 1.0;
				}
				
				for (DefaultWeightedEdge outEdge : existentialRestrictionGraph.outgoingEdgesOf(node)) {
					int outEdgeCount = existentialRestrictionGraph.getEdgeMultiplicity(outEdge);
					OWLClass child = existentialRestrictionGraph.getEdgeTarget(outEdge);
					Double childValue = accumulatedChildren.get(child);
					if (childValue == null) {
						childValue = 1.0;
					}
					nodeSize += childValue * outEdgeCount;
				}
				
				accumulatedChildren.put(node, nodeSize);
			}
		}
		
		Set<OWLClass> participatingClasses = new HashSet<OWLClass>();
		double estimatedTotalTreeSize = 0.0;
		for (Entry<OWLClass, Integer> entry : individualCounts.entrySet()) {
			OWLClass owlClass = entry.getKey();
			int individualCount = entry.getValue();
			
			Double childCount = accumulatedChildren.get(owlClass);
			if (childCount != null) {
				double totalCount = childCount * individualCount;
				estimatedTotalTreeSize += totalCount;
				if (totalCount > 0.0) {
					participatingClasses.add(owlClass);
				}
			}
		}

		if (estimatedTotalTreeSize > m_MaxTreeSize) {
			Lint lint = m_LintFactory.make();
			lint.addAllParticipatingClasses(participatingClasses);
			lint.setSeverity(new Severity(estimatedTotalTreeSize));
			m_AccumulatedLints.add(lint);
		}
	}
}



abstract class ClassCollector extends OWLClassExpressionVisitorAdapter {
	protected Set<OWLClass> m_Classes;
	
	public ClassCollector() {
		m_Classes = new HashSet<OWLClass>();
	}
	
	public void reset() {
		m_Classes.clear();
	}
	
	public Set<OWLClass> getCollectedClasses() {
		return m_Classes;
	}
}

class NamedClassCollector extends ClassCollector {
	public void visit(OWLClass desc) {
		m_Classes.add(desc);
	}
	
	public void visit(OWLObjectIntersectionOf desc) {
		for (OWLClassExpression op : desc.getOperands()) {
			op.accept(this);
		}
	}
	
	public void visit(OWLObjectUnionOf desc) {
		for (OWLClassExpression op : desc.getOperands()) {
			op.accept(this);
		}
	}
}

class ExistentialClassCollector extends ClassCollector {
	public void visit(OWLObjectSomeValuesFrom desc) {
		visitObject(desc.getFiller());
	}
	
	public void visit(OWLObjectMinCardinality desc) {
		if (desc.getCardinality() > 0) {
			visitObject(desc.getFiller());
		}
	}
	
	public void visit(OWLObjectExactCardinality desc) {
		if (desc.getCardinality() > 0) {
			visitObject(desc.getFiller());
		}
	}
	
	public void visit(OWLObjectIntersectionOf desc) {
		for (OWLClassExpression op : desc.getOperands()) {
			op.accept(this);
		}
	}
	
	public void visit(OWLObjectUnionOf desc) {
		for (OWLClassExpression op : desc.getOperands()) {
			op.accept(this);
		}
	}
	
	private void visitObject(OWLClassExpression filler) {
		if (!filler.isAnonymous()) {
			m_Classes.add(filler.asOWLClass());
		}
	}
}