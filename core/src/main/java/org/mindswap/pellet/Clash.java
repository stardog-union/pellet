// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATerm;
import aterm.ATermAppl;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: Clark & Parsia, LLC. <http://www.clarkparsia.com></p>
 *
 * @author Evren Sirin
 */
public class Clash {
	public enum ClashType {
		ATOMIC("An individual belongs to a type and its complement"),
		MIN_MAX("An individual contains a minCardinality restriction that is greater than a maxCardinality restriction"),
		MAX_CARD("The maxCardinality restriction is violated"),
		FUNC_MAX_CARD("An individual contains a minCardinality restriction that is greater than a maxCardinality restriction"),
		MAX_ZERO("The maxCardinality(0) restriction is violated"),
		NOMINAL("An individual is sameAs and differentFrom another individual at the same time"),
		EMPTY_DATATYPE("Range restrictions on a literal is inconsistent"),
		VALUE_DATATYPE("The literal value does not satisfy the datatype restriction"),
		MISSING_DATATYPE("Plain literal does not satisfy the datatype restriction (literal may be missing the rdf:datatype attribute)"),
		INVALID_LITERAL("Invalid literal for the rdf:datatype attribute"),
		DISJOINT_PROPS("Two disjoint properties have the same value"),
		BOTTOM_PROP("An individual has a value for bottom property"),
		UNEXPLAINED("Cannot explain");
        
        private String explanation;
        
        private ClashType(String explanation) {
			this.explanation = explanation;
		}
        
        public String getExplanation() {
        	return explanation;
        }
	}
	
	private DependencySet depends;
	private Node node;
	private ClashType type;
	ATerm[] args;
	String explanation;
	
	// TODO Make constructor privates and only use public creator functions
	private Clash(Node node, ClashType type, DependencySet depends) {
		this.setDepends( depends );
		this.setNode( node );
		this.setType( type );
	}

	private Clash(Node node, ClashType type, DependencySet depends, ATerm[] args) {
		this.setDepends( depends );
		this.setNode( node );
		this.setType( type );
		this.args = args;
	}
	
	private Clash(Node node, ClashType type, DependencySet depends, String explanation) {
		this.setDepends( depends );
		this.setNode( node );
		this.setType( type );
		this.explanation = explanation;
	}
	
	public Clash copyTo(ABox abox) {
		return new Clash( abox.getNode( getNode().getName() ), getType(), getDepends(), explanation );
	}
	
	public ClashType getClashType() {
		return getType();
	}
	
	public static Clash unexplained(Node node, DependencySet depends) {
		return new Clash(node, ClashType.UNEXPLAINED, depends);	    
	}

    public static Clash unexplained(Node node, DependencySet depends, String msg) {
        return new Clash(node, ClashType.UNEXPLAINED, depends, msg);       
    }
    
	public static Clash atomic(Node node, DependencySet depends) {
		return new Clash(node, ClashType.ATOMIC, depends);	    
	}
	
	public static Clash atomic(Node node, DependencySet depends, ATermAppl c) {
		return new Clash(node, ClashType.ATOMIC, depends, new ATerm[] {c});	    
	}
	
	public static Clash bottomProperty(Node node, DependencySet depends, ATermAppl p) {
		return new Clash(node, ClashType.BOTTOM_PROP, depends, new ATerm[] {p});	    
	}
	
	public static Clash maxCardinality(Node node, DependencySet depends) {
		return new Clash(node, ClashType.MAX_CARD, depends);	    
	}
	
	public static Clash maxCardinality(Node node, DependencySet depends, ATermAppl r, int n) {
		return new Clash(node, ClashType.MAX_CARD, depends, new ATerm[] {r, ATermUtils.getFactory().makeInt(n)});	    
	}
	
	public static Clash minMax(Node node, DependencySet depends) {
		return new Clash(node, ClashType.MIN_MAX, depends);	    
	}
	
	public static Clash functionalCardinality(Node node, DependencySet depends) {
		return new Clash(node, ClashType.FUNC_MAX_CARD, depends);	    
	}
	
	public static Clash functionalCardinality(Node node, DependencySet depends, ATermAppl r) {
		return new Clash(node, ClashType.FUNC_MAX_CARD, depends, new ATerm[] {r});	    
	}
	
	public static Clash missingDatatype(Node node, DependencySet depends) {
		return new Clash(node, ClashType.MISSING_DATATYPE, depends);	    
	}
	
	public static Clash missingDatatype(Node node, DependencySet depends, ATermAppl value, ATermAppl datatype) {
		return new Clash(node, ClashType.MISSING_DATATYPE, depends, new ATermAppl[] {value, datatype});	    
	}	
	
	public static Clash nominal(Node node, DependencySet depends) {
		return new Clash(node, ClashType.NOMINAL, depends);	    
	}
	
	public static Clash nominal(Node node, DependencySet depends, ATermAppl other) {
		return new Clash(node, ClashType.NOMINAL, depends, new ATermAppl[] {other});	    
	}
	
	public static Clash valueDatatype(Node node, DependencySet depends) {
		return new Clash(node, ClashType.VALUE_DATATYPE, depends);	    
	}
	
	public static Clash valueDatatype(Node node, DependencySet depends, ATermAppl value, ATermAppl datatype) {
		return new Clash(node, ClashType.VALUE_DATATYPE, depends, new ATermAppl[] {value, datatype});	    
	}

    public static Clash emptyDatatype(Node node, DependencySet depends) {
        return new Clash(node, ClashType.EMPTY_DATATYPE, depends);        
    }
    
    public static Clash emptyDatatype(Node node, DependencySet depends, ATermAppl[] datatypes) {
        return new Clash(node, ClashType.EMPTY_DATATYPE, depends, datatypes);     
    }    

	public static Clash invalidLiteral(Node node, DependencySet depends) {
		return new Clash(node, ClashType.INVALID_LITERAL, depends);	    
	}
	
	public static Clash invalidLiteral(Node node, DependencySet depends, ATermAppl value) {
		return new Clash(node, ClashType.INVALID_LITERAL, depends, new ATermAppl[] {value});	    
	}

	public static Clash disjointProps(Node node, DependencySet depends) {
		return new Clash(node, ClashType.DISJOINT_PROPS, depends);	    
	}
	
	public static Clash disjointProps(Node node, DependencySet depends, ATermAppl p1, ATermAppl p2) {
		return new Clash(node, ClashType.DISJOINT_PROPS, depends, new ATermAppl[] {p1, p2});	    
	}
	
	public String detailedString() {
		String str;
		
		if(explanation != null) {
	        str = explanation;
        }
        else if(getType() == ClashType.UNEXPLAINED) {
	        str = "No explanation was generated.";
        }
        else if(args == null) {
	        str = "No specific explanation was generated. Generic explanation: " + getType().getExplanation();
        }
        else if(getType() == ClashType.ATOMIC) {
	        str = atomicExplanation();
        }
        else if(getType() == ClashType.BOTTOM_PROP) {
	        str = bottomExplanation();
        }
        else if(getType() == ClashType.MAX_CARD) {
	        str = maxCardinalityExplanation();
        }
        else if(getType() == ClashType.FUNC_MAX_CARD) {
	        str = functionalCardinalityExplanation();
        }
        else if(getType() == ClashType.NOMINAL) {
	        str = nominalExplanation();
        }
        else if(getType() == ClashType.MISSING_DATATYPE) {
	        str = missingDatatypeExplanation();
        }
        else if(getType() == ClashType.VALUE_DATATYPE) {
	        str = valueDatatypeExplanation();
        }
        else if(getType() == ClashType.INVALID_LITERAL) {
	        str = invalidLiteralExplanation();
        }
        else if(getType() == ClashType.EMPTY_DATATYPE) {
	        str = emptyDatatypeExplanation();
        }
        else {
	        str = explanation;
        }

		return str;
	}
	
	public String describeNode( Node node ) {
		StringBuffer str = new StringBuffer();
		if(node.getNameStr().startsWith("Any member of")) {
	        str.append( node.getNameStr() );
        }
        else if(node.isNamedIndividual()) {
	        str.append( "Individual " + node.getNameStr() );
        }
        else {
		    List<ATermAppl> path = node.getPath();
		    if(path.isEmpty()) {
		        str.append( "There is an anonymous individual which" );
		    }
		    else {
			    ATermAppl first = path.get(0);
			    Iterator<ATermAppl> i = path.iterator();		    
			    String nodeID = "";
			    if(first.getName().startsWith("Any member of")) {
			        nodeID = "Y";
			        str.append( first.getName() + ", X, is related to some " + nodeID + ", identified by this path (X " );
			        i.next();
			    }
			    else {
			        nodeID = "X";
			        str.append( "There is an anonymous individual X, identified by this path (" + i.next() + " " );
			    }
			    
			    while(i.hasNext()) {
			        str.append( i.next() + " " );
			        if(i.hasNext()) {
	                    str.append( "[ " );
                    }
			    }

			    str.append( nodeID );
			    for(int count = 0; count < path.size() - 2; count++) {
	                str.append( " ]" );
                }
			    str.append( "), which" );	
		    }
		}		    

		return str.toString();
	}
	
	public String atomicExplanation() {
		return describeNode( getNode() ) + " is forced to belong to class " + args[0] + " and its complement";
	}
		
	public String bottomExplanation() {
		return describeNode( getNode() ) + " has " + args[0] + " property";
	}

	public String maxCardinalityExplanation() {
		return describeNode( getNode() ) + " has more than " + 
			args[1] + " values for property " + args[0] + 
			" violating the cardinality restriction";
	}
	
	public String functionalCardinalityExplanation() {
		return describeNode( getNode() ) + " has more than " + 
			"one value for the functional property " + args[0];
	}
	
	public String missingDatatypeExplanation() {
	    return 
	    	"Plain literal " + ATermUtils.toString((ATermAppl) args[0]) + " does not belong to datatype " + args[1] + 
	    	". Literal value may be missing the rdf:datatype attribute.";
	}
	
	public String nominalExplanation() {
		return describeNode( getNode() ) + " is sameAs and differentFrom " + args[0] + "  at the same time ";
	}
	
	public String valueDatatypeExplanation() {
	    return "Literal value " + ATermUtils.toString((ATermAppl) args[0]) + " does not belong to datatype " +  ATermUtils.toString((ATermAppl) args[1]);
	}

    public String emptyDatatypeExplanation() {
        if( args.length == 1 ) {
	        return "Datatype " + ATermUtils.toString((ATermAppl) args[0]) + " is inconsistent";
        }
        else {
            StringBuffer buffer = new StringBuffer("Intersection of datatypes [");
            for( int i = 0; i < args.length; i++ ) {
                if( i > 0 ) {
	                buffer.append( ", " );
                }
                buffer.append( ATermUtils.toString((ATermAppl) args[i]) );
            }
            buffer.append( "] is inconsistent" );
            
            return buffer.toString();
        }
    }
    
	public String invalidLiteralExplanation() {
		ATermAppl literal = (ATermAppl) args[0];
		ATermAppl datatype = (ATermAppl) literal.getArgument(2);
	    return "Literal value " + ATermUtils.toString(literal) + " is not valid for the rdatatype " + ATermUtils.toString(datatype);
	}
	
	@Override
    public String toString() {
	    // TODO fix formatting
		return "[Clash " + getNode() + " " + getType() + " " + getDepends().toString() + " " + 
			((args==null) ? null : Arrays.asList(args)) + "]";
	}

	/**
	 * @param depends the depends to set
	 */
	public void setDepends(DependencySet depends) {
		this.depends = depends;
	}

	/**
	 * @return the depends
	 */
	public DependencySet getDepends() {
		return depends;
	}

	/**
	 * @param node the node to set
	 */
	public void setNode(Node node) {
		this.node = node;
	}

	/**
	 * @return the node
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(ClashType type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public ClashType getType() {
		return type;
	}
}