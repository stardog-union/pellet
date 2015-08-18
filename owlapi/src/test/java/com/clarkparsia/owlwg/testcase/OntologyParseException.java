package com.clarkparsia.owlwg.testcase;

/**
 * <p>
 * Title: Ontology Parse Exception
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright &copy; 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <a
 * href="http://clarkparsia.com/"/>http://clarkparsia.com/</a>
 * </p>
 * 
 * @author Mike Smith &lt;msmith@clarkparsia.com&gt;
 */
public class OntologyParseException extends Exception {

	private static final long	serialVersionUID	= 1L;

	public OntologyParseException() {
	}

	public OntologyParseException(String message) {
		super( message );
	}

	public OntologyParseException(Throwable cause) {
		super( cause );
	}

	public OntologyParseException(String message, Throwable cause) {
		super( message, cause );
	}
}
