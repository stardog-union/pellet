// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under the terms of the MIT License.
//
// The MIT License
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package org.mindswap.pellet.datatypes;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSTypeDefinition;
import org.mindswap.pellet.exceptions.UnsupportedFeatureException;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;
import org.mindswap.pellet.utils.SetUtils;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;

import aterm.ATermAppl;
import aterm.ATermList;

/**
 * @author Evren Sirin
 * @deprecated Use {@link com.clarkparsia.pellet.datatypes.DatatypeReasoner} instead
 */
public class DatatypeReasoner {
    public static Logger log = Logger.getLogger( DatatypeReasoner.class.getName() );

    private Map<String, Datatype> uriToDatatype = new Hashtable<String, Datatype>();

    private Map<Datatype, String> datatypeToURI = new Hashtable<Datatype, String>();

    private Map<ATermAppl, Datatype> termToDatatype = new Hashtable<ATermAppl, Datatype>();

    private Map<Datatype, Datatype> normalized = new Hashtable<Datatype, Datatype>();

    private int datatypeCount = 0;

    public DatatypeReasoner() {
        defineDatatype( Namespaces.RDFS + "Literal", RDFSLiteral.instance );
        defineDatatype( Namespaces.RDF + "XMLLiteral", RDFXMLLiteral.instance );

        // register built-in primitive types
        defineDatatype( Namespaces.XSD + "decimal", XSDDecimal.instance );
        defineDatatype( Namespaces.XSD + "string", XSDString.instance );
        defineDatatype( Namespaces.XSD + "boolean", XSDBoolean.instance );
        defineDatatype( Namespaces.XSD + "float", XSDFloat.instance );
        defineDatatype( Namespaces.XSD + "double", XSDDouble.instance );
        defineDatatype( Namespaces.XSD + "dateTime", XSDDateTime.instance );
        defineDatatype( Namespaces.XSD + "date", XSDDate.instance );
        defineDatatype( Namespaces.XSD + "time", XSDTime.instance );
        defineDatatype( Namespaces.XSD + "gYear", XSDYear.instance );
        defineDatatype( Namespaces.XSD + "gMonth", XSDMonth.instance );
        defineDatatype( Namespaces.XSD + "gDay", XSDDay.instance );
        defineDatatype( Namespaces.XSD + "gYearMonth", XSDYearMonth.instance );
        defineDatatype( Namespaces.XSD + "gMonthDay", XSDMonthDay.instance );
        defineDatatype( Namespaces.XSD + "duration", XSDDuration.instance );
        // defineDatatype(Namespaces.XSD + "hexBinary", BaseAtomicDatatype.instance);
        // defineDatatype(Namespaces.XSD + "base64Binary", BaseAtomicDatatype.instance);
        // defineDatatype(Namespaces.XSD + "QName", BaseAtomicDatatype.instance);
        // defineDatatype(Namespaces.XSD + "NOTATION", BaseAtomicDatatype.instance);
        defineDatatype( Namespaces.XSD + "anyURI", XSDAnyURI.instance );

        defineDatatype( Namespaces.XSD + "anySimpleType", XSDSimpleType.instance );

        // register built-in derived types
        XSDDecimal decimal = XSDDecimal.instance;
        
        ValueSpace valueSpace = decimal.getValueSpace();
        Object zero = valueSpace.getMidValue();

        XSDAtomicType integer = XSDInteger.instance;
        defineDatatype( Namespaces.XSD + "integer", integer );
        
        XSDAtomicType nonPositiveInteger = integer.restrictMaxInclusive( zero );
        defineDatatype( Namespaces.XSD + "nonPositiveInteger", nonPositiveInteger );

        XSDAtomicType negativeInteger = nonPositiveInteger.restrictMaxExclusive( zero );        
        defineDatatype( Namespaces.XSD + "negativeInteger", negativeInteger );
        
        XSDAtomicType nonNegativeInteger = integer.restrictMinInclusive( zero );        
        defineDatatype( Namespaces.XSD + "nonNegativeInteger", nonNegativeInteger );
        
        XSDAtomicType positiveInteger = nonNegativeInteger.restrictMinExclusive( zero );        
        defineDatatype( Namespaces.XSD + "positiveInteger", positiveInteger );
        
        XSDAtomicType xsdLong = integer.
            restrictMinInclusive( Long.valueOf( Long.MIN_VALUE ) ).
            restrictMaxInclusive( Long.valueOf( Long.MAX_VALUE ) );        
        defineDatatype( Namespaces.XSD + "long", xsdLong );

        XSDAtomicType xsdInt = xsdLong.
            restrictMinInclusive( Integer.valueOf( Integer.MIN_VALUE ) ).
            restrictMaxInclusive( Integer.valueOf( Integer.MAX_VALUE ) );        
        defineDatatype( Namespaces.XSD + "int", xsdInt );
        
        XSDAtomicType xsdShort = xsdInt.
            restrictMinInclusive( Short.valueOf( Short.MIN_VALUE ) ).
            restrictMaxInclusive( Short.valueOf( Short.MAX_VALUE ) );        
        defineDatatype( Namespaces.XSD + "short", xsdShort );
        
        XSDAtomicType xsdByte = xsdShort.
            restrictMinInclusive( Byte.valueOf( Byte.MIN_VALUE ) ).
            restrictMaxInclusive( Byte.valueOf( Byte.MAX_VALUE ) );   
        defineDatatype( Namespaces.XSD + "byte", xsdByte );
                
        XSDAtomicType unsignedLong = nonNegativeInteger.restrictMaxInclusive( valueSpace.getValue( "18446744073709551615" ) );   
        defineDatatype( Namespaces.XSD + "unsignedLong", unsignedLong );

        XSDAtomicType unsignedInt = unsignedLong.restrictMaxInclusive( valueSpace.getValue( "4294967295" ) );   
        defineDatatype( Namespaces.XSD + "unsignedInt", unsignedInt );

        XSDAtomicType unsignedShort = unsignedInt.restrictMaxInclusive( valueSpace.getValue( "65535" ) );   
        defineDatatype( Namespaces.XSD + "unsignedShort", unsignedShort );
        
        XSDAtomicType unsignedByte = unsignedShort.restrictMaxInclusive( valueSpace.getValue( "255" ) );   
        defineDatatype( Namespaces.XSD + "unsignedByte", unsignedByte );

//        defineDatatype( Namespaces.XSD + "normalizedString", XSDString.instance );
//        defineDatatype( Namespaces.XSD + "token", XSDString.instance );
//        defineDatatype( Namespaces.XSD + "language", XSDString.instance );
//        defineDatatype( Namespaces.XSD + "NMTOKEN", XSDString.instance );
//        defineDatatype( Namespaces.XSD + "Name", XSDString.instance );
//        defineDatatype( Namespaces.XSD + "NCName", XSDString.instance );
    }

    final public Set<String> getDatatypeURIs() {
        return uriToDatatype.keySet();
    }

    final public boolean isDefined( String datatypeURI ) {
        return uriToDatatype.containsKey( datatypeURI );
    }

    final public boolean isDefined( Datatype datatype ) {
        return datatypeToURI.containsKey( datatype );
    }

    public void defineDatatype( String name, Datatype dt ) {
        if( uriToDatatype.containsKey( name ) )
            throw new RuntimeException( name + " is already defined" );

        uriToDatatype.put( name, dt );
        datatypeToURI.put( dt, name );

        normalize( dt );
    }

    public void defineUnknownDatatype( String name ) {
        defineDatatype( name, UnknownDatatype.create(name) );
    }

    public XSNamedMap parseXMLSchema( URL url ) throws Exception {
    	if( log.isLoggable( Level.FINE ) )
            log.fine( "Parsing XML Schema " + url );

        // Use Xerces DOM Implementation
        System.setProperty( DOMImplementationRegistry.PROPERTY,
            "org.apache.xerces.dom.DOMXSImplementationSourceImpl " );
        // Get DOM Implementation Registry
        DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        // Get DOM Implementation using DOM Registry
        DOMImplementationLS ls = (DOMImplementationLS) registry.getDOMImplementation( "LS" );
        // create input
        LSInput input = ls.createLSInput();
        input.setCharacterStream( new InputStreamReader( url.openStream() ) );

        // Get XS Implementation
        XSImplementation impl = (XSImplementation) registry.getDOMImplementation( "XS-Loader" );
        // Load XMLSchema
        XSLoader schemaLoader = impl.createXSLoader( null );
        XSModel schema = schemaLoader.load( input );
        // Get simple type definitions
        XSNamedMap map = schema.getComponents( XSTypeDefinition.SIMPLE_TYPE );

        return map;
    }

    public String defineDatatype( Datatype dt ) {
        String name = (dt.getName() == null) ? "datatype" + datatypeCount++ : dt.getName().getName();

        defineDatatype( name, dt );

        return name;
    }

    public void removeDatatype( String name ) {
        Datatype dt = getDatatype( name );

        // clean up the cached results
        uriToDatatype.remove( name );
        datatypeToURI.remove( dt );
        normalized.remove( dt );

        ATermAppl term = ATermUtils.makeTermAppl( name );
        termToDatatype.remove( term );
        ATermAppl not = ATermUtils.makeNot( term );
        termToDatatype.remove( not );
    }

    public Datatype getDatatype( String datatypeURI ) {
        if( datatypeURI == null || datatypeURI.length() == 0 )
            return XSDString.instance;// RDFSPlainLiteral.instance;
        else if( uriToDatatype.containsKey( datatypeURI ) )
            return uriToDatatype.get( datatypeURI );
        else
            return UnknownDatatype.instance;
    }

    public String getDatatypeURI( Datatype datatype ) {
        return datatypeToURI.get( datatype );
    }

    public Object getValue( ATermAppl lit ) {
        if( !ATermUtils.isLiteral( lit ) )
            return null;

        String lexicalValue = ((ATermAppl) lit.getArgument( 0 )).getName();
        String lang = ((ATermAppl) lit.getArgument( 1 )).getName();
        String datatypeURI = ((ATermAppl) lit.getArgument( 2 )).getName();

        if( !lang.equals( "" ) && !datatypeURI.equals( "" ) )
            throw new UnsupportedFeatureException(
                "A literal value cannot have both a datatype URI " + "and a language identifier "
                    + lit );

        Datatype datatype = getDatatype( datatypeURI );
        if( lang.equals( "" ) )
            return datatype.getValue( lexicalValue, datatypeURI );
        else
            return lit;
    }

    public Datatype singleton( ATermAppl term ) {
        ATermAppl lit = null;
        if( ATermUtils.isNominal( term ) )
            lit = (ATermAppl) term.getArgument( 0 );
        else if( ATermUtils.isLiteral( term ) )
            lit = term;
        else
            throw new RuntimeException( "An invalid data value is found " + term );

        String lexicalValue = ((ATermAppl) lit.getArgument( 0 )).getName();
        String lang = ((ATermAppl) lit.getArgument( 1 )).getName();
        String datatypeURI = ((ATermAppl) lit.getArgument( 2 )).getName();

        if( !lang.equals( "" ) && !datatypeURI.equals( "" ) )
            throw new UnsupportedFeatureException(
                "A literal value cannot have both a datatype URI " + "and a language identifier "
                    + lit );

        Datatype datatype = getDatatype( datatypeURI );
        Object value = lang.equals( "" )
            ? datatype.getValue( lexicalValue, datatypeURI )
            : lit;

        return datatype.singleton( value );
    }

    public Datatype enumeration( Set values ) {
        Datatype[] enums = new Datatype[values.size()];
        Iterator i = values.iterator();
        for( int index = 0; index < enums.length; index++ )
            enums[index] = singleton( (ATermAppl) i.next() );

        return normalize( new BaseUnionDatatype( enums ) );
    }
    
    /**
     * Returns the canonical representation of the given literal.
     * Datatyped literals are represented using their base type.
     * @TODO Error checking.
     */
    public ATermAppl getCanonicalRepresentation( ATermAppl literal ) {
    	String lexicalValue = ((ATermAppl) literal.getArgument( 0 )).getName();
		String lang = ((ATermAppl) literal.getArgument( 1 )).getName();
		String datatypeURI = ((ATermAppl) literal.getArgument( 2 )).getName();
		String canonicalDatatypeURI = null;

		if( datatypeURI.equals( "" ) ) {
			if( lang.equals( "" ) )	{
				canonicalDatatypeURI = Namespaces.XSD + "string";
			}
		}
		else {
			Datatype dt = getDatatype( datatypeURI );
			if( dt instanceof AtomicDatatype ) {
				AtomicDatatype primitive = ((AtomicDatatype) dt).getPrimitiveType();
				
				if( !dt.equals( primitive ) ) {
					canonicalDatatypeURI = primitive.getURI();
				}
			}			
		}
		
		return canonicalDatatypeURI == null 
			? literal
			: ATermUtils.makeTypedLiteral( lexicalValue, canonicalDatatypeURI );
    }
    
    public Datatype getRestrictedDatatype(ATermAppl restrictedDatatype) {
    	ATermAppl baseDatatype = (ATermAppl) restrictedDatatype.getArgument( 0 );
    	ATermList restrictions = (ATermList) restrictedDatatype.getArgument( 1 );
    	Datatype datatype = getDatatype( baseDatatype );
    	
    	if ( !(datatype instanceof XSDAtomicType) ) {
    		log.warning( "Restricted base datatype not of XSDAtomicType!: " + restrictedDatatype );
    		return UnknownDatatype.instance;
    	}
    	
    	XSDAtomicType xsdType = (XSDAtomicType) datatype;
    	for( ; !restrictions.isEmpty(); restrictions = restrictions.getNext() ) {
    		ATermAppl restriction = (ATermAppl) restrictions.getFirst();
    		String facetName = ((ATermAppl) restriction.getArgument( 0 )).getName();
    		Object facetValue = getValue( (ATermAppl) restriction.getArgument( 1 ) );
    		  
    		DatatypeFacet facet = DatatypeFacet.find( facetName );
    		xsdType = xsdType.deriveByRestriction( facet, facetValue );
    	}
    	
    	return xsdType;
    }

    public Datatype getDatatype( ATermAppl datatypeTerm ) {
        Datatype datatype = termToDatatype.get( datatypeTerm );

        if( datatype != null )
            return datatype;
        else if( ATermUtils.isNominal( datatypeTerm ) )
            datatype = singleton( datatypeTerm );
        else if( ATermUtils.isNot( datatypeTerm ) ) {
            ATermAppl negatedDatatype = (ATermAppl) datatypeTerm.getArgument( 0 );

            // FIXME check if negatedDatatype is and(...)
            if( ATermUtils.isAnd( negatedDatatype ) ) {
                ATermList list = ATermUtils.negate( (ATermList) negatedDatatype.getArgument( 0 ) );
                datatype = enumeration( ATermUtils.listToSet( list ) );
            }
            else {
                datatype = negate( getDatatype( negatedDatatype ) );
            }
        }
        else if( ATermUtils.isRestrictedDatatype( datatypeTerm ) ) {
        	datatype = getRestrictedDatatype( datatypeTerm );
        }
        else {
            datatype = getDatatype( datatypeTerm.getName() );
        }

        if( datatype == null )
            datatype = UnknownDatatype.instance;

        termToDatatype.put( datatypeTerm, datatype );

        return datatype;
    }

    public Datatype negate( Datatype datatype ) {
        Datatype norm = normalize( datatype );

        Set<Datatype> atomicTypes = ((UnionDatatype) normalized.get( RDFSLiteral.instance )).getMembers();
        atomicTypes = SetUtils.create( atomicTypes );
        if( norm instanceof AtomicDatatype ) {
            AtomicDatatype atomicType = (AtomicDatatype) norm;
            AtomicDatatype primitiveType = atomicType.getPrimitiveType();

            atomicTypes.remove( primitiveType );

            AtomicDatatype not = atomicType.not();
            if( !not.isEmpty() )
                atomicTypes.add( not );
            
            Datatype[] members = new Datatype[atomicTypes.size()];
            atomicTypes.toArray( members );
            datatype = new BaseUnionDatatype( members );     
            
            return datatype;
        }
        else if( norm instanceof UnionDatatype ) {
            Map<AtomicDatatype, AtomicDatatype> groupedTypes = new HashMap<AtomicDatatype, AtomicDatatype>();
            UnionDatatype union = (UnionDatatype) norm;
            for( Iterator i = union.getMembers().iterator(); i.hasNext(); ) {
                AtomicDatatype member = (AtomicDatatype) i.next();
                AtomicDatatype normalizedMember = (AtomicDatatype) normalize( member );
                Datatype not = normalizedMember.not();
                if( !not.isEmpty() )
                    groupedTypes = unionWithGroup( groupedTypes, not );
            }
            Datatype[] datatypes = new Datatype[groupedTypes.size()];
            groupedTypes.values().toArray( datatypes );
            if( datatypes.length == 1 )
                norm = datatypes[0];
            else
                norm = new BaseUnionDatatype( datatypes );   
            
            return norm;
        }
        else
            throw new RuntimeException( "Error in datatype reasoning" );
    }
    
    private Datatype normalize( Datatype datatype ) {
        Datatype norm = (Datatype) normalized.get( datatype );

        if( norm != null )
            return norm;
        else if( datatype instanceof UnionDatatype ) {
            Map<AtomicDatatype, AtomicDatatype> groupedTypes = new HashMap<AtomicDatatype, AtomicDatatype>();
            UnionDatatype union = (UnionDatatype) datatype;
            for( Iterator i = union.getMembers().iterator(); i.hasNext(); ) {
                Datatype member = (Datatype) i.next();
                Datatype normalizedMember = normalize( member );

                groupedTypes = unionWithGroup( groupedTypes, normalizedMember );
            }
            Datatype[] datatypes = new Datatype[groupedTypes.size()];
            groupedTypes.values().toArray( datatypes );
            if( datatypes.length == 1 )
                norm = datatypes[0];
            else
                norm = new BaseUnionDatatype( datatypes );
        }
        else
            norm = datatype;

        normalized.put( datatype, norm );

        return norm;
    }

    /**
     * Check if a datatype is subsumed by another datatype
     * 
     * @param d1
     * @param d2
     * @return
     */
    public boolean isSubTypeOf( ATermAppl d1, ATermAppl d2 ) {
        ATermAppl notD2 = ATermUtils.makeNot( d2 );
        Datatype conjunction = intersection( new ATermAppl[] { d1, notD2 } );

        return conjunction.isEmpty();
    }

    /**
     * Return a datatype that represents the intersection of a set of (possibly negated) datatypes.
     * 
     * @param datatypeTerms
     * @return
     */
    public Datatype intersection( ATermAppl[] datatypeTerms ) {
        if( datatypeTerms.length == 0 )
            return EmptyDatatype.instance;
        else if( datatypeTerms.length == 1 && ATermUtils.isPrimitive( datatypeTerms[0] ) )
            return getDatatype( datatypeTerms[0] );

        ATermList list = ATermUtils.makeList( datatypeTerms );
        ATermAppl and = ATermUtils.normalize( ATermUtils.makeAnd( list ) );
        if( and.equals( ATermUtils.BOTTOM ) )
        	return EmptyDatatype.instance;
        else if( and.equals( ATermUtils.TOP ) )
        	return RDFSLiteral.instance;
        Datatype intersection = termToDatatype.get( and );
        if( intersection != null )  
	        return intersection;        

        Datatype[] datatypes = null;
        if( ATermUtils.isAnd( and ) ) {
            list = (ATermList) and.getArgument( 0 );
            datatypes = new Datatype[list.getLength()];
            for( int i = 0; !list.isEmpty(); list = list.getNext() )
                datatypes[i++] = getDatatype( (ATermAppl) list.getFirst() );
        }
        else {
            datatypes = new Datatype[1];
            datatypes[0] = getDatatype( (ATermAppl) list.getFirst() );
        }

        // Datatype[] datatypes = new Datatype[datatypeTerms.length];
        // for(int i = 0; i < datatypeTerms.length; i++)
        //			datatypes[i] = getDatatype(datatypeTerms[i]);			

        Map<AtomicDatatype, Datatype> groupedTypes = new HashMap<AtomicDatatype, Datatype>();
        // TODO initialize the groupedTypes with the first datatype in the array
        Set atomicTypes = ((UnionDatatype) normalized.get( RDFSLiteral.instance )).getMembers();
        for( Iterator i = atomicTypes.iterator(); i.hasNext(); ) {
            AtomicDatatype primitiveType = (AtomicDatatype) i.next();
            groupedTypes.put( primitiveType, primitiveType );
        }

        for( int i = 0; i < datatypes.length; i++ )
            groupedTypes = intersectWithGroup( groupedTypes, datatypes[i] );

        if( groupedTypes.size() == 1 )
            intersection = (Datatype) groupedTypes.values().iterator().next();
        else
            intersection = new BaseUnionDatatype( new HashSet<Datatype>( groupedTypes.values() ) );
        termToDatatype.put( and, intersection );

        return intersection;
    }

    private Map<AtomicDatatype, Datatype> intersectWithGroup( Map groupedTypes, Datatype datatype ) {
        Map<AtomicDatatype, Datatype> newGroup = new HashMap<AtomicDatatype, Datatype>();
        if( datatype instanceof AtomicDatatype ) {
            AtomicDatatype atomicType = (AtomicDatatype) datatype;
            AtomicDatatype primitiveType = atomicType.getPrimitiveType();
            AtomicDatatype type = (AtomicDatatype) groupedTypes.get( primitiveType );
            if( type != null ) {
                type = type.intersection( atomicType );
                newGroup.put( primitiveType, type );
            }
        }
        else if( datatype instanceof UnionDatatype ) {
            UnionDatatype union = (UnionDatatype) datatype;
            for( Iterator i = union.getMembers().iterator(); i.hasNext(); ) {
                Datatype member = (Datatype) i.next();

                newGroup.putAll( intersectWithGroup( groupedTypes, member ) );
            }
        }
        else
            throw new RuntimeException( "Error in datatype reasoning" );

        return newGroup;
    }

    private Map<AtomicDatatype, AtomicDatatype> unionWithGroup( Map<AtomicDatatype, AtomicDatatype> groupedTypes, Datatype datatype ) {
        Map<AtomicDatatype, AtomicDatatype> newGroup = groupedTypes;
        if( datatype instanceof AtomicDatatype ) {
            AtomicDatatype atomicType = (AtomicDatatype) datatype;
            AtomicDatatype primitiveType = atomicType.getPrimitiveType();
            AtomicDatatype type = (AtomicDatatype) groupedTypes.get( primitiveType );
            if( type == null )
                type = atomicType;
            else
                type = type.union( atomicType );

            newGroup.put( primitiveType, type );
        }
        else if( datatype instanceof UnionDatatype ) {
            UnionDatatype union = (UnionDatatype) datatype;
            for( Iterator i = union.getMembers().iterator(); i.hasNext(); ) {
                Datatype member = (Datatype) i.next();

                newGroup = unionWithGroup( groupedTypes, member );
            }
        }
        else
            throw new RuntimeException( "Error in datatype reasoning" );

        return newGroup;
    }
}
