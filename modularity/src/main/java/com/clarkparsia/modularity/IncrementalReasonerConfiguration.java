// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.modularity;

import java.io.File;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerConfiguration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

/**
 *
 * @author Evren Sirin
 */
public class IncrementalReasonerConfiguration extends PelletReasonerConfiguration {
	private ModuleExtractor moduleExtractor;
	private PelletReasoner reasoner;
	private File source;
	private boolean multiThreaded = true;

	public IncrementalReasonerConfiguration() {
	}

	public IncrementalReasonerConfiguration(OWLReasonerConfiguration source) {
		super(source);
	}

	public ModuleExtractor getModuleExtractor() {
		return moduleExtractor;
	}

	public IncrementalReasonerConfiguration extractor(final ModuleExtractor theModuleExtractor) {
		moduleExtractor = theModuleExtractor;
		return this;
	}

	public PelletReasoner getReasoner() {
		return reasoner;
	}

	public IncrementalReasonerConfiguration reasoner(final PelletReasoner theReasoner) {
		reasoner = theReasoner;
		return this;
	}

	public File getFile() {
		return source;
	}

	public IncrementalReasonerConfiguration file(final File theSource) {
		source = theSource;
		return this;
	}

	public boolean isMultiThreaded() {
		return multiThreaded;
	}

	/**
	 * Sets the multi-threading option. In multi-threaded mode, during the
	 * initial setup, the regular classification and module extraction are
	 * performed in two separate threads concurrently. Doing so might reduce
	 * overall processing time but it also increases the memory requirements
	 * because both processes need additional memory during running which will
	 * be freed at the end of the process.
	 */
	public IncrementalReasonerConfiguration multiThreaded(boolean multiThreaded) {
		this.multiThreaded = multiThreaded;
		return this;
	}

	@Override
	public IncrementalReasonerConfiguration progressMonitor(final ReasonerProgressMonitor theProgressMonitor) {
		super.progressMonitor(theProgressMonitor);
		return this;
	}

	@Override
	public IncrementalReasonerConfiguration timeout(final long theTimeout) {
		super.timeout(theTimeout);
		return this;
	}
	@Override
	public IncrementalReasonerConfiguration buffering(final BufferingMode theBufferingMode) {
		super.buffering(theBufferingMode);
		return this;
	}

	@Override
	public IncrementalReasonerConfiguration buffering(final boolean isBuffering) {
		super.buffering(isBuffering);
		return this;
	}

	@Override
	public IncrementalReasonerConfiguration manager(OWLOntologyManager theManager) {
		super.manager(theManager);
		return this;
	}

	@Override
	public IncrementalReasonerConfiguration listenChanges(final boolean isListenChanges) {
		super.listenChanges(isListenChanges);
		return this;
	}

	public IncrementalReasoner createIncrementalReasoner() {
		return createIncrementalReasoner(null);
	}

	public IncrementalReasoner createIncrementalReasoner(OWLOntology ont) {
		return new IncrementalReasoner(ont, this);
	}
}
