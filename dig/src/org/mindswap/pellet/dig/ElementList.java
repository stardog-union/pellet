// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.dig;

import java.util.Arrays;

import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Evren Sirin
 *
 */
public class ElementList {
    private Element[] nodes;
    private int size;

    public ElementList( NodeList nodeList ) {
        this( nodeList, Integer.MAX_VALUE );
    }
    
    public ElementList( NodeList nodeList, int limit ) {
        int capacity = Math.min( limit, nodeList.getLength() );
        nodes = new Element[ capacity ];
        
        size = 0;
        for(int i = 0; i < capacity; i++) {
            Node node = nodeList.item( i );
            
	        if( node.getNodeType() == Node.TEXT_NODE ) { 
	            String trimmedText = node.getNodeValue().trim();
	            if( trimmedText.equals( "" ) ) 
	                continue;	          
	            else
	                throw new RuntimeException( "Expecting an XML element. Found: " + node.getNodeValue() );
	        }
	        else if( node instanceof Element )
		        nodes[ size++ ] = (Element) node;
	        else if( node instanceof Comment ) {
                // ignore comments
            }
            else    
                throw new RuntimeException( "Expecting an XML element. Found: " + node );
        }
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public int getLength() {
        return size;
    }

    public Element item( int index ) {
        if( index > size )
            throw new IndexOutOfBoundsException();
        
        return nodes[ index ];
    }

    public Element[] getNodeArray() {
        return nodes;
    }
    
    public String toString() {
        return Arrays.toString( nodes );
    }
}
