package com.clarkparsia.pellet.protege;

import com.clarkparsia.modularity.IncremantalReasonerFactory;
import com.clarkparsia.pellet.service.reasoner.SchemaReasonerFactory;
import com.complexible.pellet.client.ClientModule;
import com.complexible.pellet.client.reasoner.SchemaOWLReasonerFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.mindswap.pellet.PelletOptions;
import org.protege.editor.owl.model.inference.AbstractProtegeOWLReasonerInfo;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * 
 * @author Evren Sirin
 */
public class PelletReasonerFactory extends AbstractProtegeOWLReasonerInfo {
	static {
		// true = (default) Non DL axioms will be ignored (eg as use of complex
		// roles in cardinality restrictions)
		// false = pellet will throw an exception if non DL axioms are included
		PelletOptions.IGNORE_UNSUPPORTED_AXIOMS = false;

		PelletOptions.SILENT_UNDEFINED_ENTITY_HANDLING = true;
	}

	private final PelletReasonerPreferences prefs = PelletReasonerPreferences.getInstance();
	private OWLReasonerFactory factory = null;

	/**
     * {@inheritDoc}
     */
    public OWLReasonerFactory getReasonerFactory() {
	    if (factory == null) {
		    factory = createReasonerFactory();
	    }

	    return factory;
    }

	private OWLReasonerFactory createReasonerFactory() {
		PelletReasonerMode reasonerMode = prefs.getReasonerMode();
	    switch (reasonerMode) {
		    case REGULAR: return com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory.getInstance();
		    case INCREMENTAL: return IncremantalReasonerFactory.getInstance();
		    case REMOTE: {
			    final String serverURL = PelletReasonerPreferences.getInstance().getServerURL();

			    // TODO: read timeout from preferences too and pass to ClientModule, 3 min by default
			    final Injector aInjector = Guice.createInjector(new ClientModule(serverURL));

			    return new SchemaOWLReasonerFactory(aInjector.getInstance(SchemaReasonerFactory.class));
		    }
		    default: throw new UnsupportedOperationException("Unrecognized reasoner type: " + reasonerMode);
	    }
    }

	/**
     * {@inheritDoc}
     */
    public BufferingMode getRecommendedBuffering() {
	    return BufferingMode.BUFFERING;
    }

	public void preferencesUpdated() {
		factory = null;
	}
}
