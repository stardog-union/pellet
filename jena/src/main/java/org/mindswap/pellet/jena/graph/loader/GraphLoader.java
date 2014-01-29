// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena.graph.loader;

import java.util.Set;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.exceptions.UnsupportedFeatureException;
import org.mindswap.pellet.utils.progress.ProgressMonitor;

import aterm.ATermAppl;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;

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
 * @author Evren Sirin
 */
public interface GraphLoader {
	/**
	 * Clear all internal caches (essentially used for mapping bnodes to ATerm
	 * structures)
	 */
	public void clear();

	/**
	 * Get the Jena graph used in the loader.
	 * 
	 * @return
	 */
	public Graph getGraph();

	/**
	 * Returns the unsupported axioms ignored by the loader.
	 * 
	 * @return
	 */
	public Set<String> getUnpportedFeatures();

	/**
	 * Load the axioms from the Jena graphs to the given KB.
	 * 
	 * @throws UnsupportedFeatureException
	 */
	public void load(Iterable<Graph> graphs) throws UnsupportedFeatureException;

	/**
	 * Translate the given graph node into an ATerm object.
	 * 
	 * @param node
	 * @return
	 */
	public ATermAppl node2term(Node node);

	/**
	 * Do the preprocessing steps necessary to cache any information that will
	 * be used for loading.
	 */
	public void preprocess();

	/**
	 * Set the graph that will be used during loading.
	 * 
	 * @param graph
	 */
	public void setGraph(Graph graph);
	
	/**
	 * Returns if the loader will load the Abox triples.
	 * 
	 * @return boolean value indicating if ABox triples will be loaded
	 * @see #setLoadABox(boolean)
	 */
	public boolean isLoadABox();
	
	/**
	 * Sets the flag that tells the loader to skip ABox (instance) statements.
	 * Only TBox (class) and RBox (property) axioms will be loaded. Improves
	 * loading performance even if there are no ABox statements because lets the
	 * loader to ignore annotations.
	 * 
	 * @param loadABox
	 */
	public void setLoadABox(boolean loadABox);
	
	/**
	 * Returns if the loader will preprocess rdf:type triples.
	 * 
	 * @return boolean value indicating if rdf:type triples will be preprocessed
	 * @see #setPreprocessTypeTriples(boolean)
	 */
	public boolean isPreprocessTypeTriples();

	/**
	 * This option forces the loader to process type triples before
	 * processing other triples. Not preprocessing the type triples improves
	 * loading time 5% to 10% but might cause problems too. For example, without
	 * preprocessing the type triples a triple (s p "o") might be loaded as a
	 * datatype assertion (thinking s is an individual and p is a datatype
	 * property) whereas (s rdf:type owl:Class) and (p rdf:type
	 * owl:AnnotiationProperty) triples have not yet been processed. These
	 * problems depend on the order triples are processed and highly
	 * unpredictable. Loading the schema first with preprocessing and loading
	 * the instance data without preprocessing would be a viable option if
	 * schema and instance data are in separate files.
	 */
	public void setPreprocessTypeTriples(boolean preprocessTypeTriples);
	
	/**
	 * Set the progress monitor that will show the load progress.
	 * 
	 * @param monitor
	 */
	public void setProgressMonitor(ProgressMonitor monitor);
	
	public void setKB(KnowledgeBase kb);
}