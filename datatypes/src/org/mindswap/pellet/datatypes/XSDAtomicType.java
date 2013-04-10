
package org.mindswap.pellet.datatypes;


/**
 * @author Evren Sirin
 *
 */
public interface XSDAtomicType extends AtomicDatatype {
   	public XSDAtomicType deriveByRestriction( DatatypeFacet facet, Object value );
    
    public XSDAtomicType restrictMin( boolean inclusive, Object value ) ;

    public XSDAtomicType restrictMinInclusive( Object value );

    public XSDAtomicType restrictMinExclusive( Object value );

    public XSDAtomicType restrictMax( boolean inclusive, Object value ) ;

    public XSDAtomicType restrictMaxInclusive( Object value );

    public XSDAtomicType restrictMaxExclusive( Object value );
    
    public XSDAtomicType restrictLength( int length );

    public XSDAtomicType restrictMinLength( int length );
    
    public XSDAtomicType restrictMaxLength( int length );

    public XSDAtomicType restrictPattern( String pattern);

}
