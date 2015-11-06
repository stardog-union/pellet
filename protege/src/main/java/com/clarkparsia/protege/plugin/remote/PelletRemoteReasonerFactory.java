package com.clarkparsia.protege.plugin.remote;

import com.clarkparsia.pellet.service.reasoner.SchemaReasonerFactory;
import com.complexible.pellet.client.ClientModule;
import com.complexible.pellet.client.reasoner.SchemaOWLReasonerFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.protege.editor.owl.model.inference.AbstractProtegeOWLReasonerInfo;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * 
 * @author Evren Sirin
 */
public class PelletRemoteReasonerFactory extends AbstractProtegeOWLReasonerInfo {
	private final SchemaOWLReasonerFactory factory;

	public PelletRemoteReasonerFactory() {
		Injector aInjector = Guice.createInjector(new ClientModule());

		String serverURL = PelletRemoteReasonerPreferences.getInstance().getServerURL();
		// FIXME inject server URL
		factory = new SchemaOWLReasonerFactory(aInjector.getInstance(SchemaReasonerFactory.class));
	}

	/**
     * {@inheritDoc}
     */
    public OWLReasonerFactory getReasonerFactory() {
	    return factory;
    }

	/**
     * {@inheritDoc}
     */
    public BufferingMode getRecommendedBuffering() {
	    return BufferingMode.BUFFERING;
    }
}
