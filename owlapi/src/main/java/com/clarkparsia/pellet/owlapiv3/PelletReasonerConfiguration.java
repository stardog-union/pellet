// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.owlapiv3;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.FreshEntityPolicy;
import org.semanticweb.owlapi.reasoner.IndividualNodeSetPolicy;
import org.semanticweb.owlapi.reasoner.NullReasonerProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

/**
 *
 * @author Evren Sirin
 */
public class PelletReasonerConfiguration implements OWLReasonerConfiguration  {
	private ReasonerProgressMonitor progressMonitor = new NullReasonerProgressMonitor();
	private FreshEntityPolicy freshEntityPolicy = org.mindswap.pellet.PelletOptions.SILENT_UNDEFINED_ENTITY_HANDLING
	                                              ? FreshEntityPolicy.ALLOW
	                                              : FreshEntityPolicy.DISALLOW;
	private IndividualNodeSetPolicy individualNodeSetPolicy = IndividualNodeSetPolicy.BY_SAME_AS;
	private long timeOut = 0;
	private BufferingMode bufferingMode = BufferingMode.BUFFERING;
	private OWLOntologyManager manager = null;
	private boolean listenChanges = true;

	public PelletReasonerConfiguration() {
	}

	public PelletReasonerConfiguration(OWLReasonerConfiguration source) {
		this.progressMonitor = source.getProgressMonitor();
		this.freshEntityPolicy = source.getFreshEntityPolicy();
		this.individualNodeSetPolicy = source.getIndividualNodeSetPolicy();
		this.timeOut = source.getTimeOut();
	}

	@Nonnull
	@Override
	public ReasonerProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}

	public PelletReasonerConfiguration progressMonitor(final ReasonerProgressMonitor theProgressMonitor) {
		progressMonitor = theProgressMonitor;
		return this;
	}

	@Nonnull
	@Override
	public FreshEntityPolicy getFreshEntityPolicy() {
		return freshEntityPolicy;
	}

	public PelletReasonerConfiguration freshEntityPolicy(final FreshEntityPolicy theFreshEntityPolicy) {
		freshEntityPolicy = theFreshEntityPolicy;
		return this;
	}

	@Nonnull
	@Override
	public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
		return individualNodeSetPolicy;
	}

	public PelletReasonerConfiguration individualNodeSetPolicy(final IndividualNodeSetPolicy theIndividualNodeSetPolicy) {
		individualNodeSetPolicy = theIndividualNodeSetPolicy;
		return this;
	}

	@Override
	public long getTimeOut() {
		return timeOut;
	}

	public PelletReasonerConfiguration timeout(final long theTimeOut) {
		timeOut = theTimeOut;
		return this;
	}

	public BufferingMode getBufferingMode() {
		return bufferingMode;
	}

	public PelletReasonerConfiguration buffering(final BufferingMode theBufferingMode) {
		bufferingMode = theBufferingMode;
		return this;
	}

	public PelletReasonerConfiguration buffering(final boolean isBuffering) {
		return buffering(isBuffering ? BufferingMode.BUFFERING : BufferingMode.NON_BUFFERING);
	}

	public OWLOntologyManager getManager() {
		return manager;
	}

	public PelletReasonerConfiguration manager(OWLOntologyManager theManager) {
		manager = theManager;
		return this;
	}

	public boolean isListenChanges() {
		return listenChanges;
	}

	public PelletReasonerConfiguration listenChanges(boolean isListenChanges) {
		listenChanges = isListenChanges;
		return this;
	}

	public PelletReasoner createReasoner(OWLOntology ont) {
		return new PelletReasoner(ont, this);
	}
}
