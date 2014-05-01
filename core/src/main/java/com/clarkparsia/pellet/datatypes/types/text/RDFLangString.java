package com.clarkparsia.pellet.datatypes.types.text;

import aterm.ATermAppl;
import com.clarkparsia.pellet.datatypes.AbstractBaseDatatype;
import com.clarkparsia.pellet.datatypes.Datatype;
import com.clarkparsia.pellet.datatypes.RestrictedDatatype;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

import static com.clarkparsia.pellet.datatypes.types.text.RestrictedTextDatatype.LanguageTagPresence.*;

/**
 * <p>
 * Title: <code>rdf:langString</code>
 * </p>
 * <p>
 * Description: Singleton implementation of <code>rdf:langString</code>
 */
public class RDFLangString extends AbstractBaseDatatype<ATermAppl> {

	private static final RDFLangString instance;

	static {
		instance = new RDFLangString();
		RestrictedTextDatatype.addPermittedDatatype( instance.getName() );
	}

	public static RDFLangString getInstance() {
		return instance;
	}

	private final RestrictedTextDatatype	dataRange;

	private RDFLangString() {
		super( ATermUtils.makeTermAppl( Namespaces.RDF + "langString" ) );
		dataRange = new RestrictedTextDatatype( this, LANGUAGE_TAG_REQUIRED );
	}

	public RestrictedDatatype<ATermAppl> asDataRange() {
		return dataRange;
	}

	public ATermAppl getCanonicalRepresentation(ATermAppl input) throws InvalidLiteralException {
		return getValue( input );
	}

	public ATermAppl getLiteral(Object value) {
		if( value instanceof ATermAppl ) {
			final ATermAppl literal = (ATermAppl) value;
			try {
				return getCanonicalRepresentation( literal );
			} catch( InvalidLiteralException e ) {
				throw new IllegalStateException( e );
			}
		}
		else
			throw new IllegalArgumentException();
	}

	public Datatype<?> getPrimitiveDatatype() {
		return RDFPlainLiteral.getInstance();
	}

	public ATermAppl getValue(ATermAppl literal) throws InvalidLiteralException {
		/*
		 * This call checks that the input is a literal and the datatype name
		 * matches. The return value is not needed because plain literal values
		 * cannot be canonicalized.
		 */
		getLexicalForm( literal );

		return literal;
	}

	public boolean isPrimitive() {
		return false;
	}
}
