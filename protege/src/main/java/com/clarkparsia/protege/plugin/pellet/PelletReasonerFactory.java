package com.clarkparsia.protege.plugin.pellet;

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

	/**
     * {@inheritDoc}
     */
    public OWLReasonerFactory getReasonerFactory() {
	    return com.clarkparsia.pellet.owlapi.PelletReasonerFactory.getInstance();
    }


	/**
     * {@inheritDoc}
     */
    public BufferingMode getRecommendedBuffering() {
	    return BufferingMode.BUFFERING;
    }
}
