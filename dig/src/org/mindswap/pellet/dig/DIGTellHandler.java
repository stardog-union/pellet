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

package org.mindswap.pellet.dig;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.types.text.XSDString;

/**
 * @author Evren Sirin
 *
 */
public class DIGTellHandler extends DIGHandler {
    public DIGTellHandler() {        
    }

    public Document tells( Element tells ) {
        ElementList tellList = getElements( tells );
        for(int i = 0; i < tellList.getLength(); i++) {
            Element tell = tellList.item( i );
            String tag = tell.getTagName();
                        
            try {
                if( tag.equals( CLEARKB ) ) {
                    kb.clear();
                }
                else if( tag.equals( DEFCONCEPT ) ) {
                    kb.addClass( getNameTerm( tell ) );
                }
                else if( tag.equals( DEFROLE ) ) {
                    kb.addObjectProperty( getNameTerm( tell ) );
                }
                else if( tag.equals( DEFATTRIBUTE ) ) {
                    kb.addDatatypeProperty( getNameTerm( tell ) );
                }
                else if( tag.equals( DEFINDIVIDUAL ) ) {
                    kb.addIndividual( getNameTerm( tell ) );
                }
                else if( tag.equals( EQUALC ) ) {
                    ElementList pair = getElements( tell );
                    if( pair.getLength() != 2 )
                        continue;
                    
                    ATermAppl c1 = concept( pair.item( 0 ) );
                    ATermAppl c2 = concept( pair.item( 1 ) );
                    
                    kb.addClass( c1 );
                    kb.addClass( c2 );            

                    kb.addEquivalentClass( c1, c2 );
                }
                else if( tag.equals( IMPLIESC ) ) {
                    ElementList pair = getElements( tell );
                    if( pair.getLength() != 2 )
                        continue;
                    
                    ATermAppl c1 = concept( pair.item( 0 ) );
                    ATermAppl c2 = concept( pair.item( 1 ) );
                    
                    kb.addClass( c1 );
                    kb.addClass( c2 );
                    
                    kb.addSubClass( c1, c2 );
                }
                else if( tag.equals( DISJOINT ) ) {
                    ElementList children = getElements( tell );
                    int n = children.getLength();
                    if( n <= 1 )
                        continue;

                    int clsCount = 0;
                    int indCount = 0;
                    
                    ATermAppl[] classes = new ATermAppl[ n ];
                    ATermAppl[] inds = new ATermAppl[ n ];
                    for(int j = 0; j < n; j++) {
                        ATermAppl term = concept( children.item( j ) );  
                        if( ATermUtils.isNominal( term ) )
                            inds[indCount++] = (ATermAppl) term.getArgument( 0 );
                        else
                            classes[clsCount++] = term;
                    }

                    for(int j = 0; j < clsCount - 1; j++) {
                        for(int k = j + 1; k < clsCount; k++) 
                            kb.addDisjointClass( classes[ j ], classes[ k ] );                        
                    }
                    
                    for(int j = 0; j < indCount - 1; j++) {
                        for(int k = j + 1; k < indCount; k++) 
                            kb.addDifferent( inds[ j ], inds[ k ] );
                        
                        for(int k = 0; k < clsCount; k++) 
                            kb.addType( inds[ j ], ATermUtils.makeNot( classes[ k ] ) );
                        
                    }                    
                }            
                else if( tag.equals( EQUALR ) ) {
                    ElementList pair = getElements( tell );
                    if( pair.getLength() != 2 )
                        continue;
                   
                    ATermAppl inv1 = getInverse( pair.item( 0 ) );
                    ATermAppl inv2 = getInverse( pair.item( 1 ) );
                    if( inv1 != null ) {
                        if( inv2 != null )
                            kb.addEquivalentProperty( inv1, inv2 );        
                        else {
                            inv2 = property( pair.item( 1 ) );
                            kb.addObjectProperty( inv1 );
                            kb.addObjectProperty( inv2 );
                            kb.addInverseProperty( inv1, inv2 );
                        }
                    }
                    else if( inv2 != null ) {
                        inv1 = property( pair.item( 0 ) );
                        kb.addObjectProperty( inv1 );
                        kb.addObjectProperty( inv2 );
                        kb.addInverseProperty( inv1, inv2 );
                    }
                    else
                        kb.addEquivalentProperty( property( pair.item( 0 ) ), property( pair.item( 1 ) ) );        
                }
                else if( tag.equals( IMPLIESR ) ) {
                    ElementList pair = getElements( tell );
                    if( pair.getLength() != 2 )
                        continue;
                    
                    ATermAppl p1 = property( pair.item( 0 ) );
                    ATermAppl p2 = property( pair.item( 1 ) );
                    
                    kb.addProperty( p1 );
                    kb.addProperty( p2 );
                    
                    kb.addSubProperty( p1, p2 );
                }
                else if( tag.equals( FUNCTIONAL ) ) {
                    Element elem = getElement( tell ); 
                    if( elem == null )
                        continue;
                    
                    ATermAppl inv = getInverse( elem );
                    if( inv == null ) {
                    	ATermAppl p = property( elem );
                    	kb.addProperty( p );
                        kb.addFunctionalProperty( p );
                    }
                    else {
                    	kb.addProperty( inv );
                        kb.addInverseFunctionalProperty( inv );
                    }
                }
                else if( tag.equals( TRANSITIVE ) ) {
                    Element elem = getElement( tell ); 
                    if( elem == null )
                        continue;
                    
                    ATermAppl p = getInverse( elem );
                    if( p == null )
                    	p = property( elem );
                    
                    kb.addObjectProperty( p );                
                    kb.addTransitiveProperty( p );
                }
                else if( tag.equals( DOMAIN ) ) {
                    ElementList pair = getElements( tell );
                    if( pair.getLength() != 2 )
                        continue;
                    
                    ATermAppl p = property( pair.item( 0 ) );
                    ATermAppl c = concept( pair.item( 1 ) );
                                
                    kb.addObjectProperty( p );
                    kb.addClass( c );
                    
                    kb.addDomain( p, c );
                }
                else if( tag.equals( RANGE ) ) {
                    ElementList pair = getElements( tell );
                    if( pair.getLength() != 2 )
                        continue;
                    
                    ATermAppl p = property( pair.item( 0 ) );
                    ATermAppl c = concept( pair.item( 1 ) );
                                
                    kb.addObjectProperty( p );
                    kb.addClass( c );
                    
                    kb.addRange( p, c );
                }
                else if( tag.equals( RANGEINT ) ) {
                    Element elem = getElement( tell ); 
                    if( elem == null )
                        continue;
                    
                    ATermAppl p = property( elem );
                                
                    kb.addDatatypeProperty( p );
                    
                    kb.addRange( p, ATermUtils.makeTermAppl( Namespaces.XSD + "int" ) );
                }                
                else if( tag.equals( RANGESTRING ) ) {
                    Element elem = getElement( tell ); 
                    if( elem == null )
                        continue;
                    
                    ATermAppl p = property( elem );
                                
                    kb.addDatatypeProperty( p );
                    
                    kb.addRange( p, XSDString.getInstance().getName() );
                }                 
                else if( tag.equals( INSTANCEOF ) ) {
                    ElementList pair = getElements( tell );
                    if( pair.getLength() != 2 )
                        continue;
                    
                    ATermAppl ind = individual( pair.item( 0 ) );
                    ATermAppl c = concept( pair.item( 1 ) );
                                
                    kb.addIndividual( ind );
                    kb.addClass( c );

                    kb.addType( ind, c );
                }
                else if( tag.equals( RELATED ) ) {
                    ElementList list = getElements( tell );
                    if( list.getLength() != 3 )
                        continue;
                    
                    ATermAppl subj = individual( list.item( 0 ) );
                    ATermAppl pred = property( list.item( 1 ) );
                    ATermAppl obj = individual( list.item( 2 ) );
                    
                    kb.addIndividual( subj );
                    kb.addObjectProperty( pred );
                    kb.addIndividual( obj );
                    
                    kb.addPropertyValue( pred, subj, obj );
                }
                else if( tag.equals( VALUE ) ) {
                    ElementList list = getElements( tell );
                    if( list.getLength() != 3 )
                        continue;
                    
                    ATermAppl subj = individual( list.item( 0 ) );
                    ATermAppl pred = property( list.item( 1 ) );
                    ATermAppl obj = literal( list.item( 2 ) );

                    kb.addIndividual( subj );
                    kb.addDatatypeProperty( pred );

                    kb.addPropertyValue( pred, subj, obj );
                }          
                else {
                    log.severe( "ERROR (" + DIGErrors.UNKNOWN_TELL_OPERATION + ") : " + tag + " is not a known tell operation" );                    
                }
            }
            catch( RuntimeException e ) {
                log.severe( "Ignoring tell command:\n" + serialize( tell ) );
                StringWriter sw = new StringWriter();
                e.printStackTrace( new PrintWriter( sw ) );
                log.severe( "Exception: " + sw );
            }
        }
        
        return DIGResponse.createOkResponse();
    }
}
