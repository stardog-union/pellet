// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC.
// <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms
// of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under
// the terms of the MIT License.
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

package org.mindswap.pellet.jena.graph.loader;

import static org.mindswap.pellet.jena.graph.loader.SimpleProperty.ANTI_SYM;
import static org.mindswap.pellet.jena.graph.loader.SimpleProperty.CARDINALITY;
import static org.mindswap.pellet.jena.graph.loader.SimpleProperty.DISJOINT;
import static org.mindswap.pellet.jena.graph.loader.SimpleProperty.IRREFLEXIVE;
import static org.mindswap.pellet.jena.graph.loader.SimpleProperty.SELF;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.PropertyType;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.exceptions.UnsupportedFeatureException;
import org.mindswap.pellet.jena.BuiltinTerm;
import org.mindswap.pellet.jena.JenaUtils;
import org.mindswap.pellet.jena.vocabulary.OWL2;
import org.mindswap.pellet.jena.vocabulary.SWRL;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.AnnotationClasses;
import org.mindswap.pellet.utils.Bool;
import org.mindswap.pellet.utils.QNameProvider;
import org.mindswap.pellet.utils.SetUtils;
import org.mindswap.pellet.utils.Timer;
import org.mindswap.pellet.utils.progress.ProgressMonitor;
import org.mindswap.pellet.utils.progress.SilentProgressMonitor;

import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermList;

import com.clarkparsia.pellet.rules.model.AtomDConstant;
import com.clarkparsia.pellet.rules.model.AtomDObject;
import com.clarkparsia.pellet.rules.model.AtomDVariable;
import com.clarkparsia.pellet.rules.model.AtomIConstant;
import com.clarkparsia.pellet.rules.model.AtomIObject;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;
import com.clarkparsia.pellet.rules.model.ClassAtom;
import com.clarkparsia.pellet.rules.model.DataRangeAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.DifferentIndividualsAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.model.SameIndividualAtom;
import com.clarkparsia.pellet.vocabulary.BuiltinNamespace;
import com.hp.hpl.jena.graph.Factory;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.util.iterator.ClosableIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class DefaultGraphLoader implements GraphLoader {
	public static final Logger					log						= Logger
																				.getLogger( DefaultGraphLoader.class
																						.getName() );

	protected static final Node[]				TBOX_TYPES;
	protected static final Node[]				TBOX_PREDICATES;

	static {
		ArrayList<Node> predicates = new ArrayList<Node>();
		ArrayList<Node> types = new ArrayList<Node>();

		for( BuiltinTerm builtinTerm : BuiltinTerm.values() ) {
			if( builtinTerm.isABox() || builtinTerm.isSyntax() ) {
	            continue;
            }

			if( builtinTerm.isPredicate() ) {
	            predicates.add( builtinTerm.getNode() );
            }
            else {
	            types.add( builtinTerm.getNode() );
            }
		}

		TBOX_PREDICATES = predicates.toArray( new Node[predicates.size()] );
		TBOX_TYPES = types.toArray( new Node[types.size()] );
	}

	private static final EnumSet<BuiltinTerm>	OWL_MEMBERS_TYPES		= EnumSet
																				.of(
																						BuiltinTerm.OWL_AllDifferent,
																						BuiltinTerm.OWL2_AllDisjointClasses,
																						BuiltinTerm.OWL2_AllDisjointProperties );
	
	private static final Graph EMPTY_GRAPH = Factory.createGraphMem();

	public static QNameProvider					qnames					= new QNameProvider();

	protected KnowledgeBase						kb;

	protected Graph								graph;

	protected Map<Node, ATermAppl>				terms;

	protected Map<Node, ATermList>				lists;
	
	protected Set<Node>							anonDatatypes;

	protected Map<Node, BuiltinTerm>			naryDisjoints;

	private Map<ATermAppl, SimpleProperty>		simpleProperties;

	private Set<String>							unsupportedFeatures;

	private boolean								loadABox				= true;

	private boolean								preprocessTypeTriples	= true;

	protected ProgressMonitor					monitor					= new SilentProgressMonitor();

	public DefaultGraphLoader() {
		clear();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setProgressMonitor(ProgressMonitor monitor) {
		if( monitor == null ) {
	        this.monitor = new SilentProgressMonitor();
        }
        else {
	        this.monitor = monitor;
        }
	}

	/**
	 * {@inheritDoc}
	 */
	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	/**
	 * {@inheritDoc}
	 */
	public Graph getGraph() {
		return graph;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<String> getUnpportedFeatures() {
		return unsupportedFeatures;
	}

	protected void addSimpleProperty(ATermAppl p, SimpleProperty why) {
		simpleProperties.put( p, why );
		Role role = kb.getRBox().getRole( p );
		role.setForceSimple( true );
	}

	protected void addUnsupportedFeature(String msg) {
		if( !PelletOptions.IGNORE_UNSUPPORTED_AXIOMS ) {
	        throw new UnsupportedFeatureException( msg );
        }

		if( unsupportedFeatures.add( msg ) ) {
	        log.warning( "Unsupported axiom: " + msg );
        }
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear() {
		terms = new HashMap<Node, ATermAppl>();
		terms.put( OWL.Thing.asNode(), ATermUtils.TOP );
		terms.put( OWL.Nothing.asNode(), ATermUtils.BOTTOM );
		terms.put( OWL2.topDataProperty.asNode(), ATermUtils.TOP_DATA_PROPERTY );
		terms.put( OWL2.bottomDataProperty.asNode(), ATermUtils.BOTTOM_DATA_PROPERTY );
		terms.put( OWL2.topObjectProperty.asNode(), ATermUtils.TOP_OBJECT_PROPERTY );
		terms.put( OWL2.bottomObjectProperty.asNode(), ATermUtils.BOTTOM_OBJECT_PROPERTY );

		lists = new HashMap<Node, ATermList>();
		lists.put( RDF.nil.asNode(), ATermUtils.EMPTY_LIST );
		
		anonDatatypes = new HashSet<Node>();

		simpleProperties = new HashMap<ATermAppl, SimpleProperty>();

		unsupportedFeatures = new HashSet<String>();

		naryDisjoints = new HashMap<Node, BuiltinTerm>();
	}

	private Node getObject(Node subj, Node pred) {
		ClosableIterator<Triple> i = graph.find( subj, pred, null );

		if( i.hasNext() ) {
			Triple triple = i.next();
			i.close();
			return triple.getObject();
		}

		return null;
	}

	private boolean hasObject(Node subj, Node pred, Node obj) {
		return graph.contains( subj, pred, obj );
	}

	private class RDFListIterator implements Iterator<Node> {
		private Node list;
		
		public RDFListIterator(Node list) {
			this.list = list;
		}
		
		public boolean hasNext() {			
			return !list.equals( RDF.nil.asNode() );
		}
		
		public Node next() {
			Node first = getFirst( list );
			monitor.incrementProgress();
			Node rest = getRest( list );
			monitor.incrementProgress();

			if( first == null || rest == null ) {
				addUnsupportedFeature( "Invalid list structure: List " + list + " does not have a "
						+ (first == null
							? "rdf:first"
							: "rdf:rest") + " property. Ignoring rest of the list." );
				return null;
			}
			
			list = rest;
			
			return first;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	protected Node getFirst(Node list) {
		return getObject( list, RDF.first.asNode() );
	}	
	
	protected Node getRest(Node list) {
		return getObject( list, RDF.rest.asNode() );
	}
	
	protected ATermList createList(Node node) {
		if( lists.containsKey( node ) ) {
	        return lists.get( node );
        }
		
		ATermList list = createList( new RDFListIterator( node ) );

		lists.put( node, list );
		
		return list;
	}
	
	protected ATermList createList(RDFListIterator i) {
		if( !i.hasNext() ) {
			return ATermUtils.EMPTY_LIST;
		}
		
		Node node = i.next();
		if( node == null ) {
			return ATermUtils.EMPTY_LIST;
		}
		
		ATermAppl first = node2term( node );
		ATermList rest = createList( i );
		ATermList list = ATermUtils.makeList( first, rest );

		return list;
	}

	protected boolean isRestriction(Node node) {
		return getObject( node, OWL.onProperty.asNode() ) != null;
	}

	protected ATermAppl createRestriction(Node node) throws UnsupportedFeatureException {
		Node restrictionType = null;
		Node p = null;
		Node filler = null;
		Node qualification = null;
		Bool isObjectRestriction = Bool.UNKNOWN;

		ClosableIterator<Triple> i = graph.find( node, null, null );
		while( i.hasNext() ) {
			monitor.incrementProgress();

			Triple t = i.next();
			Node pred = t.getPredicate();
			BuiltinTerm builtinTerm = BuiltinTerm.find( pred );
			if( builtinTerm == null ) {
	            continue;
            }

			switch ( builtinTerm ) {
			case OWL_someValuesFrom:
			case OWL_allValuesFrom:
			case OWL_cardinality:
			case OWL_minCardinality:
			case OWL_maxCardinality:
			case OWL_hasValue:
			case OWL2_hasSelf:
			case OWL2_qualifiedCardinality:
			case OWL2_minQualifiedCardinality:
			case OWL2_maxQualifiedCardinality:
				restrictionType = pred;
				filler = t.getObject();
				break;

			case OWL_onProperty:
				p = t.getObject();
				break;

			case RDF_type:
				if( t.getObject().equals( OWL2.SelfRestriction.asNode() ) ) {
					restrictionType = OWL2.hasSelf.asNode();
					filler = JenaUtils.XSD_BOOLEAN_TRUE.asNode();
				}
				break;

			case OWL2_onClass:
				isObjectRestriction = Bool.TRUE;
				qualification = t.getObject();
				break;

			case OWL2_onDataRange:
				isObjectRestriction = Bool.FALSE;
				qualification = t.getObject();
				break;

			default:
				break;

			}
		}
		i.close();

		return createRestriction( restrictionType, p, filler, qualification, isObjectRestriction );
	}

	protected ATermAppl createRestriction(Node restrictionType, Node p, Node filler,
			Node qualification, Bool isObjectRestriction) throws UnsupportedFeatureException {
		ATermAppl aTerm = ATermUtils.TOP;

		if( restrictionType == null || filler == null ) {
			addUnsupportedFeature( "Skipping invalid restriction" );
			return aTerm;
		}

		ATermAppl pt = node2term( p );

		if( restrictionType.equals( OWL2.hasSelf.asNode() ) ) {
			Object value = null;
			try {
	            value = kb.getDatatypeReasoner().getValue( node2term( filler ) );					
            }
            catch (Exception e) {
            	log.log(Level.FINE, "Invalid hasSelf value: " + filler, e );
            }
            
            if( Boolean.TRUE.equals( value ) ) {
				aTerm = ATermUtils.makeSelf( pt );
				defineObjectProperty( pt );
				addSimpleProperty( pt, SELF );
			}
            else {
	            addUnsupportedFeature( "Invalid value for " + OWL2.hasSelf.getLocalName()
						+ " restriction. Expecting \"true\"^^xsd:boolean but found: " + filler );
            }
		}
		else if( restrictionType.equals( OWL.hasValue.asNode() ) ) {
			ATermAppl ot = node2term( filler );

			if( filler.isLiteral() ) {
	            defineDatatypeProperty( pt );
            }
            else {
				defineObjectProperty( pt );
				defineIndividual( ot );
			}

			aTerm = ATermUtils.makeHasValue( pt, ot );
		}
		else if( restrictionType.equals( OWL.allValuesFrom.asNode() ) ) {
			ATermAppl ot = node2term( filler );

			if( kb.isClass( ot ) ) {
	            defineObjectProperty( pt );
            }
            else if( kb.isDatatype( ot ) ) {
	            defineDatatypeProperty( pt );
            }

			aTerm = ATermUtils.makeAllValues( pt, ot );
		}
		else if( restrictionType.equals( OWL.someValuesFrom.asNode() ) ) {
			ATermAppl ot = node2term( filler );

			if( kb.isClass( ot ) ) {
	            defineObjectProperty( pt );
            }
            else if( kb.isDatatype( ot ) ) {
	            defineDatatypeProperty( pt );
            }

			aTerm = ATermUtils.makeSomeValues( pt, ot );
		}
		else if( restrictionType.equals( OWL.minCardinality.asNode() )
				|| restrictionType.equals( OWL.maxCardinality.asNode() )
				|| restrictionType.equals( OWL.cardinality.asNode() )
				|| restrictionType.equals( OWL2.minQualifiedCardinality.asNode() )
				|| restrictionType.equals( OWL2.maxQualifiedCardinality.asNode() )
				|| restrictionType.equals( OWL2.qualifiedCardinality.asNode() ) ) {

			try {
				ATermAppl c = null;
				if( isObjectRestriction.isTrue() ) {
					c = node2term( qualification );
					defineObjectProperty( pt );
				}
				else if( isObjectRestriction.isFalse() ) {
					c = node2term( qualification );
					defineDatatypeProperty( pt );
				}
				else {
					PropertyType propType = kb.getPropertyType( pt );
					if( propType == PropertyType.OBJECT ) {
	                    c = ATermUtils.TOP;
                    }
                    else if( propType == PropertyType.DATATYPE ) {
	                    c = ATermUtils.TOP_LIT;
                    }
                    else {
						defineObjectProperty( pt );
						c = ATermUtils.TOP;
					}
				}

				int cardinality =
					Integer.parseInt( filler.getLiteral().getLexicalForm().trim() );

				if( restrictionType.equals( OWL.minCardinality.asNode() )
						|| restrictionType.equals( OWL2.minQualifiedCardinality.asNode() ) ) {
	                aTerm = ATermUtils.makeMin( pt, cardinality, c );
                }
                else if( restrictionType.equals( OWL.maxCardinality.asNode() )
						|| restrictionType.equals( OWL2.maxQualifiedCardinality.asNode() ) ) {
	                aTerm = ATermUtils.makeMax( pt, cardinality, c );
                }
                else {
	                aTerm = ATermUtils.makeCard( pt, cardinality, c );
                }

				addSimpleProperty( pt, CARDINALITY );
			} catch( Exception ex ) {
				addUnsupportedFeature( "Invalid value for the owl:"
						+ restrictionType.getLocalName() + " restriction: " + filler );
				log.log( Level.WARNING, "Invalid cardinality", ex );
			}
		}

		else {
			addUnsupportedFeature( "Ignoring invalid restriction on " + p );
		}

		return aTerm;
	}

	/**
	 * {@inheritDoc}
	 */
	public ATermAppl node2term(Node node) {
		ATermAppl aTerm = terms.get( node );

		if( aTerm == null ) {
			boolean canCache = true;
			if( isRestriction( node ) ) {
				aTerm = createRestriction( node );
			}
			else if( node.isBlank() ) {
				Triple expr = getExpression( node );
				if( expr != null ) {
					Node exprType = expr.getPredicate();
					Node exprValue = expr.getObject();

					if( exprType.equals( OWL.intersectionOf.asNode() ) ) {
						ATermList list = createList( exprValue );
						aTerm = ATermUtils.makeAnd( list );
					}
					else if( exprType.equals( OWL.unionOf.asNode() ) ) {
						ATermList list = createList( exprValue );
						aTerm = ATermUtils.makeOr( list );
					}
					else if( exprType.equals( OWL.complementOf.asNode() )
							|| exprType.equals( OWL2.datatypeComplementOf.asNode() ) ) {
						ATermAppl complement = node2term( exprValue );
						aTerm = ATermUtils.makeNot( complement );
					}
					else if( exprType.equals( OWL.inverseOf.asNode() ) ) {
						ATermAppl inverse = node2term( exprValue );
						aTerm = ATermUtils.makeInv( inverse );
					}
					else if( exprType.equals( OWL.oneOf.asNode() ) ) {
						ATermList list = createList( exprValue );
						ATermList result = ATermUtils.EMPTY_LIST;
						if( list.isEmpty() ) {
							aTerm = ATermUtils.BOTTOM;
						}
						else {
							for( ATermList l = list; !l.isEmpty(); l = l.getNext() ) {
								ATermAppl c = (ATermAppl) l.getFirst();
								ATermAppl nominal = ATermUtils.makeValue( c );
								result = result.insert( nominal );
							}

							aTerm = ATermUtils.makeOr( result );
						}
					}
					else if( exprType.equals( OWL2.onDatatype.asNode() ) ) {
						aTerm = parseDataRange( node, exprValue );
					}
					else if( exprType.equals( OWL2.onDataRange.asNode() ) ) {
						aTerm = parseDataRangeLegacy( node, exprValue );
					}
					else if( exprType.equals( OWL2.propertyChain.asNode() ) ) {
						// do nothing because we cannot return an ATermList here
					}
					else {
						addUnsupportedFeature( "Unexpected bnode " + node + " " + expr );
					}
				}
				else {
					canCache = false;
					aTerm = JenaUtils.makeATerm( node );
				}
			}
			else {
				aTerm = JenaUtils.makeATerm( node );
			}
			if( canCache ) {
	            terms.put( node, aTerm );
            }
		}

		return aTerm;
	}

	protected Triple getExpression(Node node) {
		for( BuiltinTerm expressionPredicate : BuiltinTerm.EXPRESSION_PREDICATES ) {
			ClosableIterator<Triple> i = graph.find( node, expressionPredicate.getNode(), null );
			if( i.hasNext() ) {
				monitor.incrementProgress();

				Triple t = i.next();
				i.close();
				return t;
			}
		}

		return null;
	}

	
	private ATermAppl parseDataRangeLegacy(Node s, Node definition) {
		if( !definition.isURI() ) {
			addUnsupportedFeature( "Invalid datatype definition, expected URI but found " + s );
			return ATermUtils.BOTTOM_LIT;
		}
		
		ATermAppl baseDatatype = ATermUtils.makeTermAppl( definition.getURI() );
		
		Property[] datatypeFacets = new Property[] { OWL2.minInclusive, OWL2.maxInclusive,  
				OWL2.minExclusive, OWL2.maxExclusive, OWL2.totalDigits, OWL2.fractionDigits,
				OWL2.pattern };			

		List<ATermAppl> restrictions = new ArrayList<ATermAppl>();
		for( Property datatypeFacet : datatypeFacets ) {
			Node facetValue = getObject( s, datatypeFacet.asNode() );
			if( facetValue != null ) {
				ATermAppl restriction = ATermUtils.makeFacetRestriction( ATermUtils
						.makeTermAppl( datatypeFacet.getURI() ), JenaUtils.makeATerm( facetValue ) );
				restrictions.add( restriction );
			}
		}
		
		if( restrictions.isEmpty() ) {
			addUnsupportedFeature( "A data range is defined without XSD facet restrictions "
					+ s );
			return ATermUtils.BOTTOM_LIT;
		}
		else {
			return ATermUtils.makeRestrictedDatatype( baseDatatype, restrictions
					.toArray( new ATermAppl[restrictions.size()] ) );
		}		
	}
	
	private ATermAppl parseDataRange(Node s, Node definition) {
		if( !definition.isURI() ) {
			addUnsupportedFeature( "Invalid datatype definition, expected URI but found " + s );
			return ATermUtils.BOTTOM_LIT;
		}
		
		ATermAppl baseDatatype = ATermUtils.makeTermAppl( definition.getURI() );
						
		Property[] datatypeFacets = new Property[] {
				OWL2.minInclusive, OWL2.maxInclusive, OWL2.minExclusive, OWL2.maxExclusive,
				OWL2.totalDigits, OWL2.fractionDigits, OWL2.pattern };

		List<ATermAppl> restrictions = new ArrayList<ATermAppl>();
		Node restrictionList = getObject( s, OWL2.withRestrictions.asNode() );
		RDFListIterator i = new RDFListIterator( restrictionList );
		while( i.hasNext() ) {
			Node restrictionNode = i.next();
			if( restrictionNode != null ) {
				for( Property datatypeFacet : datatypeFacets ) {
					Node facetValue = getObject( restrictionNode, datatypeFacet.asNode() );
					if( facetValue != null ) {
						ATermAppl restriction = ATermUtils.makeFacetRestriction( ATermUtils
								.makeTermAppl( datatypeFacet.getURI() ), JenaUtils
								.makeATerm( facetValue ) );
						restrictions.add( restriction );
					}
				}
			}
		}

		if( restrictions.isEmpty() ) {
			addUnsupportedFeature( "A data range is defined without XSD facet restrictions "
					+ s );
			return ATermUtils.BOTTOM_LIT;
		}
		else {
			return ATermUtils.makeRestrictedDatatype( baseDatatype, restrictions
					.toArray( new ATermAppl[restrictions.size()] ) );
		}
	}
	
	private void defineRule(Node node) {
		List<RuleAtom> head = parseAtomList( getObject( node, SWRL.head.asNode() ) );
		List<RuleAtom> body = parseAtomList( getObject( node, SWRL.body.asNode() ) );

		if( head == null || body == null ) {
			String whichPart = "head and body";
			if( head != null ) {
	            whichPart = "body";
            }
            else if( body != null ) {
	            whichPart = "head";
            }
			addUnsupportedFeature( "Ignoring SWRL rule (unsupported " + whichPart + "): " + node );

			return;
		}

		ATermAppl name = JenaUtils.makeATerm( node );
		Rule rule = new Rule( name, head, body );
		kb.addRule( rule );
	}

	private AtomDObject createRuleDObject(Node node) {

		if( !node.isLiteral() ) {
			ATermAppl name = node2term( node );
			if( !ATermUtils.isPrimitive( name ) ) {
				addUnsupportedFeature( "Cannot create rule data variable out of " + node );
				return null;
			}
			return new AtomDVariable( name.toString() );
		}
		else {
			return new AtomDConstant( node2term( node ) );
		}
	}

	private AtomIObject createRuleIObject(Node node) {
		if( hasObject( node, RDF.type.asNode(), SWRL.Variable.asNode() ) ) {
			return new AtomIVariable( node.getURI() );
		}
		else {
			ATermAppl term = node2term( node );
			if( defineIndividual( term ) ) {
	            return new AtomIConstant( node2term( node ) );
            }
            else {
				addUnsupportedFeature( "Cannot create rule individual object for node " + node );
				return null;
			}
		}
	}

	private List<RuleAtom> parseAtomList(Node atomList) {
		Node obj = null;
		List<RuleAtom> atoms = new ArrayList<RuleAtom>();

		while( atomList != null && !atomList.equals( RDF.nil.asNode() ) ) {
			String atomType = "unsupported atom";
			Node atomNode = getObject( atomList, RDF.first.asNode() );

			RuleAtom atom = null;
			if( hasObject( atomNode, RDF.type.asNode(), SWRL.ClassAtom.asNode() ) ) {
				ATermAppl description = null;
				AtomIObject argument = null;
				atomType = "ClassAtom";

				if( (obj = getObject( atomNode, SWRL.classPredicate.asNode() )) != null ) {
	                description = node2term( obj );
                }

				if( (obj = getObject( atomNode, SWRL.argument1.asNode() )) != null ) {
	                argument = createRuleIObject( obj );
                }

				if( description == null ) {
	                addUnsupportedFeature( "Error on " + SWRL.classPredicate );
                }
                else if( argument == null ) {
	                addUnsupportedFeature( "Error on" + SWRL.argument1 );
                }
                else {
	                atom = new ClassAtom( description, argument );
                }
			}
			else if( hasObject( atomNode, RDF.type.asNode(), SWRL.IndividualPropertyAtom.asNode() ) ) {
				ATermAppl pred = null;
				AtomIObject argument1 = null;
				AtomIObject argument2 = null;
				atomType = "IndividualPropertyAtom";

				if( (obj = getObject( atomNode, SWRL.propertyPredicate.asNode() )) != null ) {
	                pred = node2term( obj );
                }

				if( (obj = getObject( atomNode, SWRL.argument1.asNode() )) != null ) {
	                argument1 = createRuleIObject( obj );
                }

				if( (obj = getObject( atomNode, SWRL.argument2.asNode() )) != null ) {
	                argument2 = createRuleIObject( obj );
                }

				if( pred == null || !defineObjectProperty( pred ) ) {
	                addUnsupportedFeature( "Cannot define datatype property " + pred );
                }
                else if( argument1 == null ) {
	                addUnsupportedFeature( "Term not found: " + SWRL.argument1 );
                }
                else if( argument2 == null ) {
	                addUnsupportedFeature( "Term not found " + SWRL.argument2 );
                }
                else {
	                atom = new IndividualPropertyAtom( pred, argument1, argument2 );
                }
			}
			else if( hasObject( atomNode, RDF.type.asNode(), SWRL.DifferentIndividualsAtom.asNode() ) ) {
				AtomIObject argument1 = null;
				AtomIObject argument2 = null;
				atomType = "DifferentIndividualsAtom";

				if( (obj = getObject( atomNode, SWRL.argument1.asNode() )) != null ) {
	                argument1 = createRuleIObject( obj );
                }

				if( (obj = getObject( atomNode, SWRL.argument2.asNode() )) != null ) {
	                argument2 = createRuleIObject( obj );
                }

				if( argument1 == null ) {
	                addUnsupportedFeature( "Term not found " + SWRL.argument1 );
                }
                else if( argument2 == null ) {
	                addUnsupportedFeature( "Term not found " + SWRL.argument2 );
                }
                else {
	                atom = new DifferentIndividualsAtom( argument1, argument2 );
                }
			}
			else if( hasObject( atomNode, RDF.type.asNode(), SWRL.SameIndividualAtom.asNode() ) ) {
				AtomIObject argument1 = null;
				AtomIObject argument2 = null;
				atomType = "SameIndividualAtom";

				if( (obj = getObject( atomNode, SWRL.argument1.asNode() )) != null ) {
	                argument1 = createRuleIObject( obj );
                }

				if( (obj = getObject( atomNode, SWRL.argument2.asNode() )) != null ) {
	                argument2 = createRuleIObject( obj );
                }

				if( argument1 == null ) {
	                addUnsupportedFeature( "Term not found " + SWRL.argument1 );
                }
                else if( argument2 == null ) {
	                addUnsupportedFeature( "Term not found " + SWRL.argument2 );
                }
                else {
	                atom = new SameIndividualAtom( argument1, argument2 );
                }
			}
			else if( hasObject( atomNode, RDF.type.asNode(), SWRL.DatavaluedPropertyAtom.asNode() ) ) {
				ATermAppl pred = null;
				AtomIObject argument1 = null;
				AtomDObject argument2 = null;
				atomType = "DatavaluedPropertyAtom";

				if( (obj = getObject( atomNode, SWRL.propertyPredicate.asNode() )) != null ) {
	                pred = node2term( obj );
                }

				if( (obj = getObject( atomNode, SWRL.argument1.asNode() )) != null ) {
	                argument1 = createRuleIObject( obj );
                }

				if( (obj = getObject( atomNode, SWRL.argument2.asNode() )) != null ) {
	                argument2 = createRuleDObject( obj );
                }

				if( pred == null || !defineDatatypeProperty( pred ) ) {
	                addUnsupportedFeature( "Cannot define datatype property " + pred );
                }
                else if( argument1 == null ) {
	                addUnsupportedFeature( "Term not found " + SWRL.argument1 );
                }
                else if( argument2 == null ) {
	                addUnsupportedFeature( "Term not found " + SWRL.argument2 );
                }
                else {
	                atom = new DatavaluedPropertyAtom( pred, argument1, argument2 );
                }
			}
			else if( hasObject( atomNode, RDF.type.asNode(), SWRL.BuiltinAtom.asNode() ) ) {
				atomType = "BuiltinAtom";
				Node builtInNode = null;
				List<AtomDObject> arguments = null;

				if( (obj = getObject( atomNode, SWRL.arguments.asNode() )) != null ) {
	                arguments = parseArgumentList( obj );
                }

				builtInNode = getObject( atomNode, SWRL.builtin.asNode() );

				if( arguments == null ) {
	                addUnsupportedFeature( "Term not found " + SWRL.arguments );
                }
                else if( builtInNode != null && builtInNode.isURI() ) {
	                atom = new BuiltInAtom( builtInNode.getURI(), arguments );
                }
			}
			else if( hasObject( atomNode, RDF.type.asNode(), SWRL.DataRangeAtom.asNode() ) ) {
				atomType = "DataRangeAtom";
				ATermAppl datatype = null;
				AtomDObject argument = null;

				if( (obj = getObject( atomNode, SWRL.dataRange.asNode() )) != null ) {
	                datatype = node2term( obj );
                }

				if( (obj = getObject( atomNode, SWRL.argument1.asNode() )) != null ) {
	                argument = createRuleDObject( obj );
                }

				if( datatype == null ) {
	                addUnsupportedFeature( "Term not found " + SWRL.dataRange );
                }
                else if( argument == null ) {
	                addUnsupportedFeature( "Term not found " + SWRL.argument1 );
                }
                else {
	                atom = new DataRangeAtom( datatype, argument );
                }
			}

			if( atom == null ) {
				addUnsupportedFeature( "Ignoring SWRL " + atomType + ": " + atomNode );
				return null;
			}

			atoms.add( atom );

			atomList = getObject( atomList, RDF.rest.asNode() );

		}

		if( atomList == null ) {
			addUnsupportedFeature( "Not nil-terminated list in atom list! (Seen " + atoms + " )" );
			return null;
		}

		return atoms;
	}

	private List<AtomDObject> parseArgumentList(Node argumentList) {
		List<AtomDObject> arguments = new ArrayList<AtomDObject>();

		while( argumentList != null && !argumentList.equals( RDF.nil.asNode() ) ) {
			Node argumentNode = getObject( argumentList, RDF.first.asNode() );

			if( argumentNode == null ) {
	            addUnsupportedFeature( "Term in list not found " + RDF.first );
            }
            else {
				arguments.add( createRuleDObject( argumentNode ) );

				argumentList = getObject( argumentList, RDF.rest.asNode() );
			}
		}

		return arguments;
	}

	private boolean addNegatedAssertion(Node stmt) {
		Node s = getObject( stmt, OWL2.sourceIndividual.asNode() );
		if( s == null ) {
			addUnsupportedFeature( "Negated property value is missing owl:sourceIndividual value" );
			return false;
		}

		Node p = getObject( stmt, OWL2.assertionProperty.asNode() );
		if( p == null ) {
			addUnsupportedFeature( "Negated property value is missing owl:assertionProperty value" );
			return false;
		}

		Node oi = getObject( stmt, OWL2.targetIndividual.asNode() );
		Node ov = getObject( stmt, OWL2.targetValue.asNode() );
		if( oi == null && ov == null ) {
			addUnsupportedFeature( "Negated property value is missing owl:targetIndividual or owl:targetValue value" );
			return false;
		}
		if( oi != null && ov != null ) {
			addUnsupportedFeature( "Negated property value must not have owl:targetIndividual and owl:targetValue value" );
			return false;
		}

		ATermAppl st = node2term( s );
		ATermAppl pt = node2term( p );
		ATermAppl ot;

		defineIndividual( st );
		if( oi != null ) {
			ot = node2term( oi );
			if( oi.isURI() || oi.isBlank() ) {
				defineObjectProperty( pt );
				defineIndividual( ot );
			}
			else {
				addUnsupportedFeature( "Invalid negated property target individual " + stmt );
				return false;
			}
		}
		else {
			ot = node2term( ov );
			if( ov.isLiteral() ) {
				defineDatatypeProperty( pt );
			}
			else {
				addUnsupportedFeature( "Invalid negated property target value " + stmt );
				return false;
			}
		}

		if( !kb.addNegatedPropertyValue( pt, st, ot ) ) {
			addUnsupportedFeature( "Skipping invalid negated property value " + stmt );
			return false;
		}

		return true;
	}

	protected boolean defineClass(ATermAppl c) {
		if( ATermUtils.isPrimitive( c ) ) {
			kb.addClass( c );
			return true;
		}
        else {
	        return ATermUtils.isComplexClass( c );
        }
	}

	protected boolean defineDatatype(ATermAppl dt) {
		if( ATermUtils.isPrimitive( dt ) ) {
			kb.addDatatype( dt );
			return true;
		}
		else {
			return kb.isDatatype( dt );
		}
	}

	/**
	 * There are two properties that are used in a subPropertyOf or
	 * equivalentProperty axiom. If one of them is defined as an Object (or
	 * Data) Property the other should also be defined as an Object (or Data)
	 * Property
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	private boolean defineProperties(ATermAppl p1, ATermAppl p2) {
		PropertyType type1 = kb.getPropertyType( p1 );
		PropertyType type2 = kb.getPropertyType( p2 );
		if( type1 != type2 ) {
			if( type1 == PropertyType.UNTYPED ) {
				if( type2 == PropertyType.OBJECT ) {
	                defineObjectProperty( p1 );
                }
                else if( type2 == PropertyType.DATATYPE ) {
	                defineDatatypeProperty( p1 );
                }
			}
			else if( type2 == PropertyType.UNTYPED ) {
				if( type1 == PropertyType.OBJECT ) {
	                defineObjectProperty( p2 );
                }
                else if( type1 == PropertyType.DATATYPE ) {
	                defineDatatypeProperty( p2 );
                }
			}
			else {
				// addWarning("Properties " + p1 + ", " + p2
				// + " are related but first is " + PropertyType.TYPES[type1]
				// + "Property and second is " + PropertyType.TYPES[type2]);
				return false;
			}
		}
		else if( type1 == PropertyType.UNTYPED ) {
			defineProperty( p1 );
			defineProperty( p2 );
		}

		return true;
	}

	protected boolean defineObjectProperty(ATermAppl c) {
		if( !ATermUtils.isPrimitive( c ) && !ATermUtils.isInv( c ) ) {
	        return false;
        }

		return kb.addObjectProperty( c );
	}

	protected boolean defineDatatypeProperty(ATermAppl c) {
		if( !ATermUtils.isPrimitive( c ) ) {
	        return false;
        }

		return kb.addDatatypeProperty( c );
	}

	private boolean defineAnnotationProperty(ATermAppl c) {
		if( !ATermUtils.isPrimitive( c ) ) {
	        return false;
        }

		return kb.addAnnotationProperty( c );
	}

	protected boolean defineProperty(ATermAppl c) {
		if( ATermUtils.isInv( c ) ) {
			kb.addObjectProperty( c.getArgument( 0 ) );
			return true;
		}
		else if( !ATermUtils.isPrimitive( c ) ) {
			return false;
		}

		kb.addProperty( c );
		return true;
	}

	protected boolean defineIndividual(ATermAppl c) {
		kb.addIndividual( c );
		return true;
	}

	@SuppressWarnings("unused")
	private PropertyType guessPropertyType(ATermAppl p, Node prop) {
		PropertyType roleType = kb.getPropertyType( p );
		if( roleType != PropertyType.UNTYPED ) {
	        return roleType;
        }

		defineProperty( p );

		Iterator<?> i = graph.find( prop, RDF.type.asNode(), null );
		while( i.hasNext() ) {
			Triple stmt = (Triple) i.next();
			Node o = stmt.getObject();

			if( o.equals( OWL.ObjectProperty.asNode() ) ) {
	            return PropertyType.OBJECT;
            }
            else if( o.equals( OWL.DatatypeProperty.asNode() ) ) {
	            return PropertyType.DATATYPE;
            }
            else if( o.equals( OWL.AnnotationProperty.asNode() ) ) {
	            return PropertyType.ANNOTATION;
            }
            else if( o.equals( OWL.OntologyProperty.asNode() ) ) {
	            return PropertyType.ANNOTATION;
            }
		}

		return PropertyType.UNTYPED;
	}

	/**
	 * Process all triples with <code>rdf:type</code> predicate. If
	 * {@link PelletOptions#PREPROCESS_TYPE_TRIPLES} option is <code>true</code>
	 * this function is a noop.
	 */
	protected void processTypes() {
		if( preprocessTypeTriples ) {
			log.fine( "processTypes" );
			if( isLoadABox() ) {
				processTypes( Node.ANY );
			}
			else {
				for( Node type : TBOX_TYPES ) {
					processTypes( type );
				}
			}
		}
	}

	/**
	 * Process triples with <code>rdf:type</code> predicate and given object.
	 * Type can be {@link Node.ANY} to indicate all type triples should be
	 * processed.
	 * 
	 * @param type
	 *            the object of <code>rdf:type</code> triples to be processed
	 */
	protected void processTypes(Node type) {
		ClosableIterator<Triple> i = graph.find( null, RDF.type.asNode(), type );
		while( i.hasNext() ) {
			Triple stmt = i.next();
			processType( stmt );
		}
		i.close();
	}

	/**
	 * Process a single <code>rdf:type</code> triple. Type triples that are part
	 * of the OWL syntax, e.g. <code>_:x rdf:type owl:Restriction</code> will
	 * not be processed since they are handled by the {@link #node2term(Node)}
	 * function.
	 * 
	 * @param triple
	 *            Type triple that will be processed
	 */
	protected void processType(Triple triple) {
		Node s = triple.getSubject();
		Node o = triple.getObject();

		BuiltinTerm builtinTerm = BuiltinTerm.find( o );

		if( builtinTerm != null ) {
			if( builtinTerm.isSyntax() ) {
				return;
			}
			
			// If we have a triple _:x rdf:type owl:Class then this is a noop
			// that would only cache class expression for _:x. However, since
			// we did not complete process all type triples unqualified cardinality
			// restrictions would cause issues here since they require property
			// to be either data or object property. Therefore, we stop processing
			// this triple immediately before calling node2term function.
			if( s.isBlank() && builtinTerm.equals( BuiltinTerm.OWL_Class ) ) {
				return;
			}
		}

		monitor.incrementProgress();

		ATermAppl st = node2term( s );

		if( builtinTerm == null ) {
			if( PelletOptions.FREEZE_BUILTIN_NAMESPACES && o.isURI() ) {
				String nameSpace = o.getNameSpace();
				if( nameSpace != null ) {
					BuiltinNamespace builtin = BuiltinNamespace.find( nameSpace );
					if( builtin != null ) {
						addUnsupportedFeature( "Ignoring triple with unknown term from " + builtin
								+ " namespace: " + triple );
						return;
					}
				}
			}

			return;
		}

		switch ( builtinTerm ) {

		case RDF_Property:
			defineProperty( st );
			break;

		case RDFS_Class:
			defineClass( st );
			break;

		case RDFS_Datatype:
		case OWL_DataRange:
			if( s.isURI() ) {
	            defineDatatype( st );
            }
            else {
	            anonDatatypes.add( s );
            }
			
			break;

		case OWL_Class:
			defineClass( st );
			break;

		case OWL_Thing:
		case OWL2_NamedIndividual:
			defineIndividual( st );
			break;

		case OWL_Nothing:
			defineIndividual( st );
			kb.addType( st, ATermUtils.BOTTOM );
			break;

		case OWL_ObjectProperty:
			if( s.isURI() && !defineObjectProperty( st ) ) {
				addUnsupportedFeature( "Property " + st
						+ " is defined both as an ObjectProperty and a "
						+ kb.getPropertyType( st ) + "Property" );
			}
			break;

		case OWL_DatatypeProperty:
			if( !defineDatatypeProperty( st ) ) {
	            addUnsupportedFeature( "Property " + st
						+ " is defined both as a DatatypeProperty and a "
						+ kb.getPropertyType( st ) + "Property" );
            }
			break;

		case OWL_FunctionalProperty:
			defineProperty( st );
			kb.addFunctionalProperty( st );
			addSimpleProperty( st, CARDINALITY );
			break;

		case OWL_InverseFunctionalProperty:
			if( defineProperty( st ) ) {
				kb.addInverseFunctionalProperty( st );
				addSimpleProperty( st, CARDINALITY );
			}
            else {
	            addUnsupportedFeature( "Ignoring InverseFunctionalProperty axiom for " + st + " ("
						+ kb.getPropertyType( st ) + "Property)" );
            }
			break;

		case OWL_TransitiveProperty:
			if( defineObjectProperty( st ) ) {
				kb.addTransitiveProperty( st );
			}
            else {
	            addUnsupportedFeature( "Ignoring TransitiveProperty axiom for " + st + " ("
						+ kb.getPropertyType( st ) + "Property)" );
            }

			break;

		case OWL_SymmetricProperty:
			if( defineObjectProperty( st ) ) {
	            kb.addSymmetricProperty( st );
            }
            else {
	            addUnsupportedFeature( "Ignoring SymmetricProperty axiom for " + st + " ("
						+ kb.getPropertyType( st ) + "Property)" );
            }
			break;

		case OWL_AnnotationProperty:
			if( !defineAnnotationProperty( st ) ) {
				addUnsupportedFeature( "Property " + st
						+ " is defined both as an AnnotationProperty and a "
						+ kb.getPropertyType( st ) + "Property" );
			}
			break;

		case OWL2_ReflexiveProperty:
			if( defineObjectProperty( st ) ) {
	            kb.addReflexiveProperty( st );
            }
            else {
	            addUnsupportedFeature( "Ignoring ReflexiveProperty axiom for " + st + " ("
						+ kb.getPropertyType( st ) + "Property)" );
            }
			break;

		case OWL2_IrreflexiveProperty:
			if( defineObjectProperty( st ) ) {
				kb.addIrreflexiveProperty( st );
				addSimpleProperty( st, IRREFLEXIVE );
			}
            else {
	            addUnsupportedFeature( "Ignoring IrreflexiveProperty axiom for " + st + " ("
						+ kb.getPropertyType( st ) + "Property)" );
            }
			break;

		case OWL2_AsymmetricProperty:
			if( defineObjectProperty( st ) ) {
				kb.addAsymmetricProperty( st );
				addSimpleProperty( st, ANTI_SYM );
			}
            else {
	            addUnsupportedFeature( "Ignoring AntisymmetricProperty axiom for " + st + " ("
						+ kb.getPropertyType( st ) + "Property)" );
            }
			break;

		case OWL2_NegativePropertyAssertion:
			addNegatedAssertion( s );
			break;

		case SWRL_Imp:
			if( PelletOptions.DL_SAFE_RULES ) {
	            defineRule( s );
            }
			break;

		case OWL_AllDifferent:
		case OWL2_AllDisjointClasses:
		case OWL2_AllDisjointProperties:
			naryDisjoints.put( s, builtinTerm );
			break;

		default:
			throw new InternalReasonerException( "Unexpected term: " + o );
		}
	}

	/**
	 * Process all the triples in the raw graph. If
	 * {@link PelletOptions#PREPROCESS_TYPE_TRIPLES} option is <code>true</code>
	 * all <code>rdf:type</code> will be ignored since they have already been
	 * processed with {@link #processTypes()} function.
	 */
	protected void processTriples() {
		log.fine( "processTriples" );
		if( isLoadABox() ) {
			processTriples( Node.ANY );
		}
		else {
			for( Node predicate : TBOX_PREDICATES ) {
				processTriples( predicate );
			}
		}
	}

	/**
	 * Process triples with the given predicate. Predicate can be
	 * {@link Node.ANY} to indicate all triples should be processed.
	 * 
	 * @param predicate
	 *            Predicate of the triples that will be processed
	 */
	protected void processTriples(Node predicate) {
		ClosableIterator<Triple> i = graph.find( null, predicate, null );
		while( i.hasNext() ) {
			Triple triple = i.next();
			processTriple( triple );
		}
		i.close();
	}

	/**
	 * Process a single triple that corresponds to an axiom (or a fact). This
	 * means triples that are part of OWL syntax, e.g. a triple with
	 * <code>owl:onProperty</code> predicate, will not be processed since they
	 * are handled by the {@link #node2term(Node)} function. Also, if
	 * {@link PelletOptions#PREPROCESS_TYPE_TRIPLES} option is <code>true</code>
	 * any triple with <code>rdf:type</code> predicate will be ignored.
	 * 
	 * @param triple
	 *            Triple to be processed.
	 */
	protected void processTriple(Triple triple) {
		Node p = triple.getPredicate();
		Node s = triple.getSubject();
		Node o = triple.getObject();

		BuiltinTerm builtinTerm = BuiltinTerm.find( p );

		if( builtinTerm != null ) {
			if( builtinTerm.isSyntax() ) {
				return;
			}

			if( builtinTerm.equals( BuiltinTerm.RDF_type ) ) {
				if( BuiltinTerm.find( o ) == null ) {
					if( isLoadABox() ) {
						ATermAppl ot = node2term( o );
						
						if (!AnnotationClasses.contains(ot)) {
							defineClass( ot );

							ATermAppl st = node2term( s );
							defineIndividual( st );
							kb.addType( st, ot );
						}
					}
				}
				else if( !preprocessTypeTriples ) {
					processType( triple );
				}

				return;
			}
		}

		monitor.incrementProgress();

		ATermAppl st = node2term( s );
		ATermAppl ot = node2term( o );

		if( builtinTerm == null ) {
			ATermAppl pt = node2term( p );
			Role role = kb.getProperty( pt );
			PropertyType type = (role == null)
				? PropertyType.UNTYPED
				: role.getType();

			if( type == PropertyType.ANNOTATION ) {
				// Skip ontology annotations
				if( graph.contains( s, RDF.type.asNode(), OWL.Ontology.asNode() ) ) {
	                return;
                }

				if( defineAnnotationProperty( pt ) ) {
	                kb.addAnnotation( st, pt, ot );
                }

				return;
			}

			if( PelletOptions.FREEZE_BUILTIN_NAMESPACES ) {
				String nameSpace = p.getNameSpace();
				if( nameSpace != null ) {
					BuiltinNamespace builtin = BuiltinNamespace.find( nameSpace );
					if( builtin != null ) {
						addUnsupportedFeature( "Ignoring triple with unknown property from "
								+ builtin + " namespace: " + triple );
						return;
					}
				}
			}

			if( o.isLiteral() ) {
				if( defineDatatypeProperty( pt ) ) {
					String datatypeURI = ((ATermAppl) ot.getArgument( 2 )).getName();

					if( defineIndividual( st ) ) {
						defineDatatypeProperty( pt );
						if( !datatypeURI.equals( "" ) ) {
	                        defineDatatype( ATermUtils.makeTermAppl( datatypeURI ) );
                        }

						kb.addPropertyValue( pt, st, ot );
					}
					else if( type == PropertyType.UNTYPED ) {
	                    defineAnnotationProperty( pt );
                    }
                    else {
	                    addUnsupportedFeature( "Ignoring ObjectProperty used with a class expression: "
								+ triple );
                    }
				}
                else {
	                addUnsupportedFeature( "Ignoring literal value used with ObjectProperty : "
							+ triple );
                }
			}
			else {
				if( !defineObjectProperty( pt ) ) {
	                addUnsupportedFeature( "Ignoring object value used with DatatypeProperty: "
							+ triple );
                }
                else if( !defineIndividual( st ) ) {
	                addUnsupportedFeature( "Ignoring class expression used in subject position: "
							+ triple );
                }
                else if( !defineIndividual( ot ) ) {
	                addUnsupportedFeature( "Ignoring class expression used in object position: "
							+ triple );
                }
                else {
	                kb.addPropertyValue( pt, st, ot );
                }
			}
			return;
		}

		switch ( builtinTerm ) {

		case RDFS_subClassOf:
			if( !defineClass( st ) ) {
	            addUnsupportedFeature( "Ignoring subClassOf axiom because the subject is not a class "
						+ st + " rdfs:subClassOf " + ot );
            }
            else if( !defineClass( ot ) ) {
	            addUnsupportedFeature( "Ignoring subClassOf axiom because the object is not a class "
						+ st + " rdfs:subClassOf " + ot );
            }
            else {
	            kb.addSubClass( st, ot );
            }
			break;

		case RDFS_subPropertyOf:
			ATerm subProp = null;
			if( s.isBlank() ) {
				Triple expr = getExpression( s );
				if( expr == null ) {
					addUnsupportedFeature( "Bnode in rdfs:subProperty axioms is not a valid property expression" );
				}
				else if( expr.getPredicate().equals( OWL2.inverseOf.asNode() ) ) {
					if( defineObjectProperty( (ATermAppl) st.getArgument( 0 ) )
							&& defineObjectProperty( ot ) ) {
	                    subProp = st;
                    }
				}
				else if( expr.getPredicate().equals( OWL2.propertyChain.asNode() ) ) {
					subProp = createList( expr.getObject() );
					ATermList list = (ATermList) subProp;
					while( !list.isEmpty() ) {
						if( !defineObjectProperty( (ATermAppl) list.getFirst() ) ) {
							break;
						}
						list = list.getNext();
					}
					if( !list.isEmpty() || !defineObjectProperty( ot ) ) {
	                    subProp = null;
                    }
				}
				else {
					addUnsupportedFeature( "Bnode in rdfs:subProperty axioms is not a valid property expression" );
				}
			}
			else if( defineProperties( st, ot ) ) {
				subProp = st;
			}

			if( subProp != null ) {
	            kb.addSubProperty( subProp, ot );
            }
            else {
	            addUnsupportedFeature( "Ignoring subproperty axiom between " + st + " ("
						+ kb.getPropertyType( st ) + "Property) and " + ot + " ("
						+ kb.getPropertyType( ot ) + "Property)" );
            }

			break;

		case RDFS_domain:
			if( kb.isAnnotationProperty( st ) ) {
				addUnsupportedFeature( "Ignoring domain axiom for AnnotationProperty " + st );
			}
			else {
				defineProperty( st );
				defineClass( ot );
				kb.addDomain( st, ot );
			}
			break;

		case RDFS_range:
			if( kb.isAnnotationProperty( st ) ) {
				addUnsupportedFeature( "Ignoring range axiom for AnnotationProperty " + st );
				break;
			}

			if( kb.isDatatype( ot ) ) {
	            defineDatatypeProperty( st );
            }
            else if( kb.isClass( ot ) ) {
	            defineObjectProperty( st );
            }
            else {
	            defineProperty( st );
            }

			if( kb.isDatatypeProperty( st ) ) {
	            defineDatatype( ot );
            }
            else if( kb.isObjectProperty( st ) ) {
	            defineClass( ot );
            }

			kb.addRange( st, ot );

			break;

		case OWL_intersectionOf:
			ATermList list = createList( o );

			defineClass( st );
			ATermAppl conjunction = ATermUtils.makeAnd( list );

			kb.addEquivalentClass( st, conjunction );
			break;

		case OWL_unionOf:
			list = createList( o );

			defineClass( st );
			ATermAppl disjunction = ATermUtils.makeOr( list );
			kb.addEquivalentClass( st, disjunction );

			break;

		case OWL2_disjointUnionOf:
			list = createList( o );

			kb.addDisjointClasses( list );

			defineClass( st );
			disjunction = ATermUtils.makeOr( list );
			kb.addEquivalentClass( st, disjunction );

			break;

		case OWL_complementOf:
			if( !defineClass( st ) ) {
	            addUnsupportedFeature( "Ignoring complementOf axiom because the subject is not a class "
						+ st + " owl:complementOf " + ot );
            }
            else if( !defineClass( ot ) ) {
	            addUnsupportedFeature( "Ignoring complementOf axiom because the object is not a class "
						+ st + " owl:complementOf " + ot );
            }
            else {
				kb.addComplementClass( st, ot );
			}
			break;

		case OWL_equivalentClass:
			if( kb.isDatatype( ot ) || anonDatatypes.contains( o ) ) {
				if( !defineDatatype( st ) ) {
	                addUnsupportedFeature( "Ignoring equivalentClass axiom because the subject is not a datatype "
							+ st + " owl:equivalentClass " + ot );
                }
                else {
	                kb.addDatatypeDefinition( st, ot );
                }
			}
			else if( !defineClass( st ) ) {
	            addUnsupportedFeature( "Ignoring equivalentClass axiom because the subject is not a class "
						+ st + " owl:equivalentClass " + ot );
            }
            else if( !defineClass( ot ) ) {
	            addUnsupportedFeature( "Ignoring equivalentClass axiom because the object is not a class "
						+ st + " owl:equivalentClass " + ot );
            }
            else {
	            kb.addEquivalentClass( st, ot );
            }
		
			break;

		case OWL_disjointWith:
			if( !defineClass( st ) ) {
	            addUnsupportedFeature( "Ignoring disjointWith axiom because the subject is not a class "
						+ st + " owl:disjointWith " + ot );
            }
            else if( !defineClass( ot ) ) {
	            addUnsupportedFeature( "Ignoring disjointWith axiom because the object is not a class "
						+ st + " owl:disjointWith " + ot );
            }
            else {
				kb.addDisjointClass( st, ot );
			}
			break;

		case OWL2_propertyDisjointWith:
			if( defineProperties( st, ot ) ) {
				kb.addDisjointProperty( st, ot );

				addSimpleProperty( st, DISJOINT );
				addSimpleProperty( ot, DISJOINT );
			}
            else {
	            addUnsupportedFeature( "Ignoring disjoint property axiom between " + st + " ("
						+ kb.getPropertyType( st ) + "Property) and " + ot + " ("
						+ kb.getPropertyType( ot ) + "Property)" );
            }
			break;

		case OWL2_propertyChainAxiom:
			ATermAppl superProp = null;
			if( s.isBlank() ) {
				Triple expr = getExpression( s );
				if( expr == null ) {
					addUnsupportedFeature( "Bnode in owl:propertyChainAxiom axiom is not a valid property expression" );
				}
				else if( expr.getPredicate().equals( OWL2.inverseOf.asNode() ) ) {
					if( defineObjectProperty( (ATermAppl) st.getArgument( 0 ) ) ) {
	                    superProp = st;
                    }
				}
				else {
					addUnsupportedFeature( "Bnode in owl:propertyChainAxiom axiom is not a valid property expression" );
				}
			}
			else if( defineObjectProperty( st ) ) {
				superProp = st;
			}

			subProp = createList( o );
			list = (ATermList) subProp;
			while( !list.isEmpty() ) {
				if( !defineObjectProperty( (ATermAppl) list.getFirst() ) ) {
					break;
				}
				list = list.getNext();
			}
			if( !list.isEmpty() ) {
	            subProp = null;
            }

			if( subProp != null && superProp != null ) {
	            kb.addSubProperty( subProp, superProp );
            }
            else {
	            addUnsupportedFeature( "Ignoring property chain axiom between " + st + " ("
						+ kb.getPropertyType( st ) + "Property) and " + ot );
            }
			break;

		case OWL_equivalentProperty:
			if( defineProperties( st, ot ) ) {
	            kb.addEquivalentProperty( st, ot );
            }
            else {
	            addUnsupportedFeature( "Ignoring equivalent property axiom between " + st + " ("
						+ kb.getPropertyType( st ) + "Property) and " + ot + " ("
						+ kb.getPropertyType( ot ) + "Property)" );
            }

			break;

		case OWL_inverseOf:
			if( defineObjectProperty( st ) && defineObjectProperty( ot ) ) {
	            kb.addInverseProperty( st, ot );
            }
            else {
	            addUnsupportedFeature( "Ignoring inverseOf axiom between " + st + " ("
						+ kb.getPropertyType( st ) + "Property) and " + ot + " ("
						+ kb.getPropertyType( ot ) + "Property)" );
            }

			break;

		case OWL_sameAs:
			if( defineIndividual( st ) && defineIndividual( ot ) ) {
	            kb.addSame( st, ot );
            }
            else {
	            addUnsupportedFeature( "Ignoring sameAs axiom between " + st + " and " + ot );
            }
			break;

		case OWL_differentFrom:
			if( defineIndividual( st ) && defineIndividual( ot ) ) {
	            kb.addDifferent( st, ot );
            }
            else {
	            addUnsupportedFeature( "Ignoring differentFrom axiom between " + st + " and " + ot );
            }
			break;

		case OWL_distinctMembers:
			list = createList( o );
			for( ATermList l = list; !l.isEmpty(); l = l.getNext() ) {
				ATermAppl c = (ATermAppl) l.getFirst();
				defineIndividual( c );
			}

			kb.addAllDifferent( list );
			break;
			
		case OWL_members:
			BuiltinTerm entityType = null;
			if( preprocessTypeTriples ) {
				entityType = naryDisjoints.get( s );
			}
			else {
				Node type = getObject( s, RDF.type.asNode() );
				if( type != null ) {
	                entityType = BuiltinTerm.find( type );
                }
			}

			if( entityType == null ) {
				addUnsupportedFeature( "There is no valid rdf:type for an owl:members assertion: "
						+ s );
			}
			else if( !OWL_MEMBERS_TYPES.contains( entityType ) ) {
				addUnsupportedFeature( "The rdf:type for an owl:members assertion is not recognized: "
						+ entityType );
			}
			else {
				list = createList( o );
				for( ATermList l = list; !l.isEmpty(); l = l.getNext() ) {
					ATermAppl c = (ATermAppl) l.getFirst();
					switch ( entityType ) {
					case OWL_AllDifferent:
						defineIndividual( c );
						break;
					case OWL2_AllDisjointClasses:
						defineClass( c );
						break;
					case OWL2_AllDisjointProperties:
						defineProperty( c );
						break;
					}
				}

				switch ( entityType ) {
				case OWL_AllDifferent:
					kb.addAllDifferent( list );
					break;
				case OWL2_AllDisjointClasses:
					kb.addDisjointClasses( list );
					break;
				case OWL2_AllDisjointProperties:
					kb.addDisjointProperties( list );
					break;
				}
			}
			break;

		case OWL_oneOf:
			ATermList resultList = ATermUtils.EMPTY_LIST;

			if( kb.isDatatype( st ) ) {
	            return;
            }

			// assert the subject is a class
			defineClass( st );

			disjunction = null;
			list = createList( o );
			if( o.equals( RDF.nil.asNode() ) ) {
	            disjunction = ATermUtils.BOTTOM;
            }
            else {
				for( ATermList l = list; !l.isEmpty(); l = l.getNext() ) {
					ATermAppl c = (ATermAppl) l.getFirst();

					if( PelletOptions.USE_PSEUDO_NOMINALS ) {
						ATermAppl nominal = ATermUtils.makeTermAppl( c.getName() + "_nominal" );
						resultList = resultList.insert( nominal );

						defineClass( nominal );
						defineIndividual( c );
						kb.addType( c, nominal );
					}
					else {
						defineIndividual( c );

						resultList = resultList.insert( ATermUtils.makeValue( c ) );
					}
				}
				disjunction = ATermUtils.makeOr( resultList );
			}
			kb.addEquivalentClass( st, disjunction );
			break;

		case OWL2_hasKey:
			if( o.equals( RDF.nil.asNode() ) ) {
	            return;
            }

			Set<ATermAppl> properties = new HashSet<ATermAppl>();
			// assert the subject is a class
			defineClass( st );
			list = createList( o );

			for( ATermList l = list; !l.isEmpty(); l = l.getNext() ) {
				ATermAppl f = (ATermAppl) l.getFirst();
				defineProperty( f );
				properties.add( f );
			}

			kb.addKey( st, properties );
			break;

		case OWL2_topDataProperty:
		case OWL2_bottomDataProperty:
		case OWL2_topObjectProperty:
		case OWL2_bottomObjectProperty:
			defineIndividual( st );
			kb.addPropertyValue( node2term(p), st, ot );
			break;
		
		default:
			throw new InternalReasonerException( "Unrecognized term: " + p );

		}
	}

	protected void processUntypedResources() {
		log.fine( "processUntypedResource" );
		
		for( Role r : kb.getRBox().getRoles().toArray( new Role[0] ) ) {
			SimpleProperty why = simpleProperties.get( r.getName() );
			if( why != null ) {
				String msg = null;
				if( r.isTransitive() ) {
					msg = "transitivity axiom";
				}
				else if( r.hasComplexSubRole() ) {
					msg = "complex sub property axiom";
				}

				if( msg != null ) {
					msg = "Ignoring " + msg + " due to an existing " + why + " for property " + r;
					addUnsupportedFeature( msg );
					r.removeSubRoleChains();
				}
			}

			if( r.isUntypedRole() ) {
				/*
				 * Untyped roles are made object properties unless they have
				 * datatype super or sub-roles 
				 */
				boolean rangeToDatatype = false;
				Set<Role> roles = SetUtils.union( r.getSubRoles(), r.getSuperRoles() );
				for( Role sub: roles ) {
					switch ( sub.getType() ) {
					case OBJECT:
						defineObjectProperty( r.getName() );
						break;
					case DATATYPE:
						defineDatatypeProperty( r.getName() );
						rangeToDatatype = true;
						break;
					default:
						continue;
					}
				}

				if (!rangeToDatatype) {
					defineObjectProperty( r.getName() );
				}
				
				/*
				 * If a typing assumption has been made, carry over to any
				 * untyped range entity
				 */
				Set<ATermAppl> ranges = r.getRanges();
				if( ranges != null ) {
					if( rangeToDatatype ) {
						for( ATermAppl range : ranges ) {
							if( (range.getAFun().getArity() == 0) && (!kb.isDatatype( range )) ) {
	                            defineDatatype( range );
                            }
						}
					}
					else {
						for( ATermAppl range : ranges ) {
							if( (range.getAFun().getArity() == 0) && (!kb.isClass( range )) ) {
	                            defineClass( range );
                            }
						}
					}
				}
			}
		}
	}

	public void setKB(KnowledgeBase kb) {
		this.kb = kb;
	}

	private void defineBuiltinProperties() {
		defineAnnotationProperty( node2term( RDFS.label.asNode() ) );
		defineAnnotationProperty( node2term( RDFS.comment.asNode() ) );
		defineAnnotationProperty( node2term( RDFS.seeAlso.asNode() ) );
		defineAnnotationProperty( node2term( RDFS.isDefinedBy.asNode() ) );
		defineAnnotationProperty( node2term( OWL.versionInfo.asNode() ) );

		defineAnnotationProperty( node2term( OWL.backwardCompatibleWith.asNode() ) );
		defineAnnotationProperty( node2term( OWL.priorVersion.asNode() ) );
		defineAnnotationProperty( node2term( OWL.incompatibleWith.asNode() ) );
	}

	/**
	 * {@inheritDoc}
	 */
	public void load(Iterable<Graph> graphs) throws UnsupportedFeatureException {
		Timer timer = kb.timers.startTimer( "load" );

		monitor.setProgressTitle( "Loading" );
		monitor.taskStarted();
		
		graph = EMPTY_GRAPH;
		preprocess();

		for (Graph g : graphs) {
			graph = g;
			processTypes();    
        }

		for (Graph g : graphs) {
			graph = g;
			processTriples();  
        }
		
		processUntypedResources();

		monitor.taskFinished();

		timer.stop();
	}

	/**
	 * {@inheritDoc}
	 */
	public void preprocess() {
		defineBuiltinProperties();
	}

	public boolean isLoadABox() {
		return loadABox;
	}

	public void setLoadABox(boolean loadABox) {
		this.loadABox = loadABox;
	}

	public boolean isPreprocessTypeTriples() {
		return preprocessTypeTriples;
	}

	public void setPreprocessTypeTriples(boolean preprocessTypeTriples) {
		this.preprocessTypeTriples = preprocessTypeTriples;
	}
}
