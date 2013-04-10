// The MIT License
//
// Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
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

package org.mindswap.pellet.dig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mindswap.pellet.utils.SetUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import aterm.ATermAppl;

/*
 * Created on Jul 15, 2005
 */

/**
 * @author Evren Sirin
 *
 */
public class DIGAskHandler extends DIGTellHandler {
    private DIGResponse response;
    
    public DIGAskHandler() {
        response = new DIGResponse( DIGConstants.RESPONSES );
    }
    
    public Document asks( Element asks ) {
        Document doc = null;
        
        if( !kb.isConsistent() ) {
            doc = DIGResponse.createErrorResponse( DIGErrors.GENERAL_ASK_ERROR, "Inconsistent KB" );
        }
        else {
            response = new DIGResponse( DIGConstants.RESPONSES );

            ElementList askElements = getElements( asks );
            for(int i = 0; i < askElements.getLength(); i++) {
                Element ask = askElements.item( i );
                String tag = getTagName( ask );
                String id = getId( ask );
                
                Element[] params = getElementArray( ask );
                
                if( tag.equals( TOLD_VALUES ) ) {
                    toldValues( params[0], params[1], id );
                }
                else {
                    Element result = null;            
                    if( tag.equals( ALL_CONCEPT_NAMES ) ) {
                        result = allConceptNames();
                    }
                    else if( tag.equals( ALL_ROLE_NAMES ) ) {
                        result = allRoleNames();
                    }
                    else if( tag.equals( ALL_INDIVIDUALS ) ) {
                        result = allIndividuals();
                    }
                    else if( tag.equals( SATISFIABLE ) ) {
                        result = satisfiable( params[0] );
                    }
                    else if( tag.equals( SUBSUMES ) ) {
                        result = subsumes( params[0], params[1] );
                    }
                    else if( tag.equals( PARENTS ) ) {
                        result = parents( params[0] );
                    }
                    else if( tag.equals( ANCESTORS ) ) {
                        result = ancestors( params[0] );
                    }
                    else if( tag.equals( CHILDREN ) ) {
                        result = children( params[0] );
                    }
                    else if( tag.equals( DESCENDANTS ) ) {
                        result = descendants( params[0] );
                    }
                    else if( tag.equals( EQUIVALENTS ) ) {
                        result = equivalents( params[0] );
                    }
                    else if( tag.equals( DISJOINT ) ) {
                        result = disjoint( params[0], params[1] );
                    }
                    else if( tag.equals( RPARENTS ) ) {
                        result = rparents( params[0] );
                    }
                    else if( tag.equals( RANCESTORS ) ) {
                        result = rancestors( params[0] );
                    }
                    else if( tag.equals( RCHILDREN ) ) {
                        result = rchildren( params[0] );
                    }
                    else if( tag.equals( RDESCENDANTS ) ) {
                        result = rdescendants( params[0] );
                    }
                    else if( tag.equals( INSTANCES ) ) {
                        result = instances( params[0] );
                    }
                    else if( tag.equals( INSTANCE ) ) {
                        result = instance( params[0], params[1] );
                    }
                    else if( tag.equals( TYPES ) ) {
                        result = types( params[0] );
                    }
                    else if( tag.equals( ROLE_FILLERS ) ) {
                        result = roleFillers( params[0], params[1] );
                    }
                    else if( tag.equals( RELATED_INDIVIDUALS ) ) {
                        result = relatedIndividuals( params[0] );
                    }
                    else {
                        result = response.addError( 
                            DIGErrors.UNKNOWN_ASK_OPERATION, tag + " is not a known ask operation" );
                        log.severe( "ERROR (" + DIGErrors.UNKNOWN_ASK_OPERATION + ") : " + tag + " is not a known ask operation" );
                    }

                    result.setAttribute( ID, id );
                }
            }
            
            if( askElements.isEmpty() )
                response.addElement( DIGConstants.OK );
            
            doc = response.getDocument();            
        }
        
        return doc;
    }

    public Element allConceptNames() {
        Element conceptSet = response.addElement( CONCEPT_SET );
        
        List classes = new ArrayList( kb.getAllClasses() );
        for( int i = 0; i < classes.size(); i++ ) {
            ATermAppl c = (ATermAppl) classes.get( i );
            
            Set eqs = kb.getAllEquivalentClasses( c );
            classes.subList( i + 1, classes.size() ).removeAll( eqs );
            
            response.addSynonms( conceptSet, eqs, CATOM );
        }         
        
        return conceptSet;
    }    
    
    public Element allRoleNames() {
        Element roleSet = response.addElement( ROLE_SET );
        
        List properties = new ArrayList( kb.getProperties() );
        for( int i = 0; i < properties.size(); i++ ) {
            ATermAppl prop = (ATermAppl) properties.get( i );
            
            Set eqs = kb.getEquivalentProperties( prop );
            eqs.add( prop );
            properties.subList( i + 1, properties.size() ).removeAll( eqs );
            
            response.addSynonms( roleSet, eqs, RATOM );
        }         
        
        return roleSet;
    }  
    
    public Element allIndividuals() {
        Set individuals = kb.getIndividuals();
        
        return response.addIndividualSet( individuals );
    }    
    
    public Element satisfiable( Element node ) {
        ATermAppl c = concept( node );
        boolean result = kb.isSatisfiable( c );

        return response.addBoolean( result );
    }

    public Element subsumes( Element node1, Element node2 ) {
        ATermAppl c1 = concept( node1 );
        ATermAppl c2 = concept( node2 );
        boolean result = kb.isSubClassOf( c2, c1 );

        return response.addBoolean( result );
    }

    public Element disjoint( Element node1, Element node2 ) {
        ATermAppl c1 = concept( node1 );
        ATermAppl c2 = concept( node2 );
        boolean result = kb.isDisjoint( c1, c2 );

        return response.addBoolean( result );
    }

    public Element children( Element node ) {
        ATermAppl c = concept( node );
        Set children = kb.getSubClasses( c, true );
        
        return response.addConceptSet( children );
    }

    public Element descendants( Element node ) {
        ATermAppl c = concept( node );
        Set descendants = kb.getSubClasses( c, false );
        
        return response.addConceptSet( descendants );
    }

    public Element parents( Element node ) {
        ATermAppl c = concept( node );
        Set parents = kb.getSuperClasses( c, true );
        
        return response.addConceptSet( parents );
    }

    public Element ancestors( Element node ) {
        ATermAppl c = concept( node );
        Set ancestors = kb.getSuperClasses( c, false );
        
        return response.addConceptSet( ancestors );
    }

    public Element equivalents( Element node ) {
        ATermAppl c = concept( node );
        Set equivalents = SetUtils.singleton( kb.getAllEquivalentClasses( c ) );
        
        return response.addConceptSet( equivalents );        
    }
    
    public Element rchildren( Element node ) {
        ATermAppl prop = property( node );
        boolean isAttribute = kb.isDatatypeProperty( prop );
        Set rchildren = kb.getSubProperties( prop, true );
        
        return response.addRoleSet( rchildren, isAttribute );
    }

    public Element rdescendants( Element node ) {
        ATermAppl prop = property( node );
        boolean isAttribute = kb.isDatatypeProperty( prop );
        Set rdescendants = kb.getSubProperties( prop, false );
        rdescendants.add( kb.getAllEquivalentProperties( prop ) );
        
        return response.addRoleSet( rdescendants, isAttribute );
    }

    public Element rparents( Element node ) {
        ATermAppl prop = property( node );
        boolean isAttribute = kb.isDatatypeProperty( prop );
        Set rparents = kb.getSuperProperties( prop, true );
        
        return response.addRoleSet( rparents, isAttribute );
    }

    public Element rancestors( Element node ) {
        ATermAppl prop = property( node );
        boolean isAttribute = kb.isDatatypeProperty( prop );
        Set rancestors = kb.getSuperProperties( prop, false );
        rancestors.add( kb.getAllEquivalentProperties( prop ) );
        
        return response.addRoleSet( rancestors, isAttribute );
    }
    
    public Element instances( Element node ) {
        ATermAppl c = concept( node );
        Set instances = kb.getInstances( c );
        
        return response.addIndividualSet( instances );
    }

    public Element instance( Element node1, Element node2 ) {
        ATermAppl ind = individual( node1 );
        ATermAppl c = concept( node2 );
        boolean result = kb.isType( ind, c );

        return response.addBoolean( result );
    }

    public Element types( Element node ) {
        ATermAppl ind = individual( node );
        Set types = kb.getTypes( ind );
        
        return response.addConceptSet( types );
    }
    
    public Element relatedIndividuals( Element node ) {
        ATermAppl role = property( node );
        Map values = kb.getPropertyValues( role );
        
        return response.addIndividualPairSet( values );
    }
    
    public Element roleFillers( Element nodeInd, Element nodeRole ) {
        ATermAppl ind = individual( nodeInd );
        ATermAppl prop = property( nodeRole );
        List values = kb.getPropertyValues( prop, ind );
        
        return response.addIndividualSet( values );
    }
    
    public void toldValues( Element nodeInd, Element nodeRole, String id ) {
        ATermAppl ind = individual( nodeInd );
        ATermAppl prop = property( nodeRole );
        List values = kb.getPropertyValues( prop, ind );
        
        response.addValues( values, id );
    }
}
