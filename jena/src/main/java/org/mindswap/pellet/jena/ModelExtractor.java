// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena;

import static org.mindswap.pellet.jena.JenaUtils.makeGraphNode;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.jena.vocabulary.OWL2;
import org.mindswap.pellet.utils.iterator.IteratorUtils;

import aterm.ATermAppl;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

/**
 * Extract a Jena model that contains the information Pellet inferred. Models
 * can be generated about classes, properties or individuals. Note that
 * individual models do not contain any information about property assertions,
 * it just contains type assertions about individuals.
 * 
 * @author Evren Sirin
 * 
 */
public class ModelExtractor {

	/**
	 * Enumeration of types of statements that can be retrieved
	 */
	public enum StatementType {
		/**
		 * for individuals, rdf:type statements (includes super-classes)
		 */
		ALL_INSTANCE,

		/**
		 * for classes, rdfs:subClassOf statements (includes all super-classes)
		 */
		ALL_SUBCLASS,

		/**
		 * for properties, rdfs:subPropertyOf statements (includes all
		 * super-properties)
		 */
		ALL_SUBPROPERTY,

		/**
		 * for classes, owl:complementOf statements
		 */
		COMPLEMENT_CLASS,

		/**
		 * for individuals, data property value statements
		 */
		DATA_PROPERTY_VALUE,

		/**
		 * for individuals, owl:differentFrom statements
		 */
		DIFFERENT_FROM,

		/**
		 * for individuals, rdf:type statements (only the most specific classes)
		 */
		DIRECT_INSTANCE,

		/**
		 * for classes, rdfs:subClassOf statements (includes only direct
		 * super-classes)
		 */
		DIRECT_SUBCLASS,

		/**
		 * for properties, rdfs:subPropertyOf statements (includes only direct
		 * super-properties)
		 */
		DIRECT_SUBPROPERTY,

		/**
		 * for classes, owl:disjointWith statements
		 */
		DISJOINT_CLASS,

		/**
		 * for classes, owl:propertyDisjointWith statements
		 */
		DISJOINT_PROPERTY,
		
		/**
		 * for classes, owl:equivalentClass statements
		 */
		EQUIVALENT_CLASS,

		/**
		 * for properties, owl:equivalentProperty statements
		 */
		EQUIVALENT_PROPERTY,

		/**
		 * for properties, owl:inverseOf statements
		 */
		INVERSE_PROPERTY,

		/**
		 * for individuals, jena reasoner vocabulary direct rdf:type statements
		 */
		JENA_DIRECT_INSTANCE,

		/**
		 * for classes, jena reasoner vocabulary direct rdfs:subClassOf
		 * statements
		 */
		JENA_DIRECT_SUBCLASS,

		/**
		 * for properties, jena reasoner vocabulary direct rdfs:subPropertyOf
		 * statements
		 */
		JENA_DIRECT_SUBPROPERTY,

		/**
		 * for individuals, object property value statements
		 */
		OBJECT_PROPERTY_VALUE,

		/**
		 * for individuals, owl:sameAs statements
		 */
		SAME_AS;

		/**
		 * All statements about classes
		 */
		public static final EnumSet<StatementType>	ALL_CLASS_STATEMENTS;

		/**
		 * All statements about individuals
		 */
		public static final EnumSet<StatementType>	ALL_INDIVIDUAL_STATEMENTS;

		/**
		 * All statements about properties
		 */
		public static final EnumSet<StatementType>	ALL_PROPERTY_STATEMENTS;

		/**
		 * All statements (without Jena predicates for direct relations)
		 */
		public static final EnumSet<StatementType>	ALL_STATEMENTS;

		/**
		 * All statements (including Jena predicates for direct relations)
		 */
		public static final EnumSet<StatementType>	ALL_STATEMENTS_INCLUDING_JENA;

		/**
		 * All property values (both object and data)
		 */
		public static final EnumSet<StatementType>	PROPERTY_VALUE;
		
		/**
		 * Default statements
		 */
		public static final EnumSet<StatementType>	DEFAULT_STATEMENTS;
		
		static {
			ALL_CLASS_STATEMENTS = EnumSet.of( ALL_SUBCLASS, COMPLEMENT_CLASS, DIRECT_SUBCLASS,
					DISJOINT_CLASS, EQUIVALENT_CLASS );

			ALL_INDIVIDUAL_STATEMENTS = EnumSet.of( ALL_INSTANCE, DATA_PROPERTY_VALUE,
					DIFFERENT_FROM, DIRECT_INSTANCE, OBJECT_PROPERTY_VALUE, SAME_AS );

			ALL_PROPERTY_STATEMENTS = EnumSet.of( ALL_SUBPROPERTY, DIRECT_SUBPROPERTY,
					EQUIVALENT_PROPERTY, INVERSE_PROPERTY, DISJOINT_PROPERTY );

			ALL_STATEMENTS = EnumSet.complementOf( EnumSet.of( 
					JENA_DIRECT_INSTANCE, JENA_DIRECT_SUBCLASS, JENA_DIRECT_SUBPROPERTY ) );
			
			ALL_STATEMENTS_INCLUDING_JENA = EnumSet.allOf( StatementType.class );
			
			DEFAULT_STATEMENTS = EnumSet.of( StatementType.DIRECT_SUBCLASS,
					StatementType.EQUIVALENT_CLASS, StatementType.DIRECT_INSTANCE,
					StatementType.OBJECT_PROPERTY_VALUE, StatementType.DATA_PROPERTY_VALUE,
					StatementType.DIRECT_SUBPROPERTY, StatementType.EQUIVALENT_PROPERTY,
					StatementType.INVERSE_PROPERTY );
			
			PROPERTY_VALUE = EnumSet.of( StatementType.DATA_PROPERTY_VALUE, 
					StatementType.OBJECT_PROPERTY_VALUE );
		}
	}
	
	/**
	 * A filter that does not accept anything.
	 */
	public static final Filter<Triple> FILTER_NONE = new Filter<Triple>() {
		@Override
		public boolean accept(Triple o) {
			return false;
		}
	};

	/**
	 * Associated KB
	 */
	private KnowledgeBase			kb;
	
	/**
	 * Filter that will be used to drop inferences
	 */
	private Filter<Triple>			filter = FILTER_NONE;

	/**
	 * Controls the selected statements for methods where no selector is passed
	 * (initial setup intended to be backwards compatible)
	 */
	private EnumSet<StatementType>	selector	= StatementType.DEFAULT_STATEMENTS;

	/**
	 * Initialize an empty extractor
	 */
	public ModelExtractor() {
	}

	/**
	 * Initialize the extractor with a Jena model that is backed by PelletInfGraph.
	 * 
	 * @throws ClassCastException if the model.getGraph() does not return an instance of PelletInfGraph
	 */
	public ModelExtractor(Model model) throws ClassCastException {
		this( (PelletInfGraph) model.getGraph() );
	}

	/**
	 * Initialize the extractor with a PelletInfGraph
	 */
	public ModelExtractor(PelletInfGraph graph) {
		this( graph.getPreparedKB() );
	}
	
	/**
	 * Initialize the extractor with a reasoner
	 */
	public ModelExtractor(KnowledgeBase kb) {
		setKB( kb );
	}

	/**
	 * Creates and adds the triple to the given list if the triple passes
	 * the filter.
	 * 
	 * @param triples List to be added
	 * @param s subject of the triple
	 * @param p predicate of the triple
	 * @param o object of the triple
	 */
	private void addTriple(List<Triple> triples, Node s, Node p, Node o) {
		Triple triple = Triple.create( s, p, o );
		if( !filter.accept( triple ) )
			triples.add( triple );
	}
	
	public Model extractClassModel() {
		return extractClassModel( ModelFactory.createDefaultModel() );
	}

	public Model extractClassModel(Model model) {
		final boolean allSubs = selector.contains( StatementType.ALL_SUBCLASS );
		final boolean jenaDirectSubs = selector.contains( StatementType.JENA_DIRECT_SUBCLASS );
		final boolean subs = allSubs || jenaDirectSubs
				|| selector.contains( StatementType.DIRECT_SUBCLASS );
		final boolean equivs = selector.contains( StatementType.EQUIVALENT_CLASS );
		final boolean disjs = selector.contains( StatementType.DISJOINT_CLASS );
		final boolean comps = selector.contains( StatementType.COMPLEMENT_CLASS );

		if( subs || equivs || disjs || comps )
			kb.classify();

		List<Triple> triples = new ArrayList<Triple>();

		Set<ATermAppl> classes = kb.getAllClasses();

		for( ATermAppl c : classes ) {

			triples.clear();

			Node s, p;

			s = makeGraphNode( c );

			addTriple( triples, s, RDF.type.asNode(), OWL.Class.asNode() );

			if( subs ) {
				p = RDFS.subClassOf.asNode();
				
				if( allSubs ) {
					Set<ATermAppl> eqs = kb.getAllEquivalentClasses( c );
					for( ATermAppl eq : eqs ) {
						Node o = makeGraphNode( eq );
						addTriple( triples, s, p, o );
					}
				}

				Set<Set<ATermAppl>> supers = allSubs
					? kb.getSuperClasses( c, false )
					: kb.getSuperClasses( c, true );

				Iterator<ATermAppl> i = IteratorUtils.flatten( supers.iterator() ); 
				while( i.hasNext() ) {
					Node o = makeGraphNode( i.next() );
					addTriple( triples, s, p, o );
				}

				if( jenaDirectSubs ) {

					p = ReasonerVocabulary.directSubClassOf.asNode();

					Set<Set<ATermAppl>> direct = allSubs
						? kb.getSuperClasses( c, true )
						: supers;

					i = IteratorUtils.flatten( direct.iterator() ); 
					while( i.hasNext() ) {
						Node o = makeGraphNode( i.next() );
						addTriple( triples, s, p, o );
					}
				}
			}

			if( equivs ) {

				p = OWL.equivalentClass.asNode();

				Set<ATermAppl> eqs = kb.getAllEquivalentClasses( c );
				for( ATermAppl a : eqs ) {
					Node o = makeGraphNode( a );
					addTriple( triples, s, p, o );
				}
			}

			if( disjs ) {
				Set<Set<ATermAppl>> disj = kb.getDisjointClasses( c );

				if( !disj.isEmpty() ) {
					p = OWL.disjointWith.asNode();
					
					Iterator<ATermAppl> i = IteratorUtils.flatten( disj.iterator() ); 
					while( i.hasNext() ) {
						ATermAppl a = i.next();
						if( classes.contains( a ) )
							addTriple( triples, s, p, makeGraphNode( a ) );
					}
				}
			}

			if( comps ) {
				Set<ATermAppl> comp = kb.getComplements( c );

				if( !comp.isEmpty() ) {
					p = OWL.complementOf.asNode();
					for( ATermAppl a : comp ) {
						if( classes.contains( a ) )
							addTriple( triples, s, p, makeGraphNode( a ) );
					}
				}
			}
			model.getGraph().getBulkUpdateHandler().add( triples );
		}

		return model;
	}

	/**
	 * Extract statements about individuals
	 */
	public Model extractIndividualModel() {
		return extractIndividualModel( ModelFactory.createDefaultModel() );
	}

	/**
	 * Extract statements about individuals
	 */
	public Model extractIndividualModel(Model model) {

		/*
		 * Initialize booleans that reflect the selector parameter - this avoids
		 * doing set contains evaluations for each pass of the loop.
		 */
		final boolean allClasses = selector.contains( StatementType.ALL_INSTANCE );
		final boolean jenaDirectClasses = selector.contains( StatementType.JENA_DIRECT_INSTANCE );
		final boolean classes = allClasses || jenaDirectClasses
				|| selector.contains( StatementType.DIRECT_INSTANCE );
		final boolean sames = selector.contains( StatementType.SAME_AS );
		final boolean diffs = selector.contains( StatementType.DIFFERENT_FROM );
		final boolean objValues = selector.contains( StatementType.OBJECT_PROPERTY_VALUE );
		final boolean dataValues = selector.contains( StatementType.DATA_PROPERTY_VALUE );

		if( classes )
			kb.realize();

		List<Triple> triples = new ArrayList<Triple>();

		for( ATermAppl ind : kb.getIndividuals() ) {

			triples.clear();

			Node s, p;
			
			s = makeGraphNode( ind );

			if( classes ) {

				p = RDF.type.asNode();

				Set<Set<ATermAppl>> types = kb.getTypes( ind, !allClasses );

				Iterator<ATermAppl> i = IteratorUtils.flatten( types.iterator() ); 
				while( i.hasNext() ) {
					Node o = makeGraphNode( i.next() );
					addTriple( triples, s, p, o );
				}

				if( jenaDirectClasses ) {

					p = ReasonerVocabulary.directRDFType.asNode();

					Set<Set<ATermAppl>> directTypes = allClasses
						? kb.getTypes( ind, true )
						: types;

					i = IteratorUtils.flatten( directTypes.iterator() ); 
					while( i.hasNext() ) {
						Node o = makeGraphNode( i.next() );
						addTriple( triples, s, p, o );
					}
				}
			}

			if( sames ) {
				p = OWL.sameAs.asNode();
				addTriple( triples, s, p, s );
				for( ATermAppl a : kb.getSames( ind ) ) {
					addTriple( triples, s, p, makeGraphNode( a ) );
				}
			}

			if( diffs ) {
				p = OWL.differentFrom.asNode();
				for( ATermAppl a : kb.getDifferents( ind ) ) {
					addTriple( triples, s, p, makeGraphNode( a ) );
				}
			}

			if( dataValues || objValues ) {

				for( Role role : kb.getRBox().getRoles() ) {

					if( role.isAnon() )
						continue;

					List<ATermAppl> values;
					ATermAppl name = role.getName();
					if( role.isDatatypeRole() ) {
						if( dataValues )
							values = kb.getDataPropertyValues( name, ind );
						else
							continue;
					}
					else if( role.isObjectRole() ) {
						if( objValues )
							values = kb.getObjectPropertyValues( name, ind );
						else
							continue;
					}
					else
						continue;

					if( values.isEmpty() )
						continue;

					p = makeGraphNode( name );

					for( ATermAppl value : values ) {
						addTriple( triples, s, p, makeGraphNode( value ) );
					}
				}
			}

			model.getGraph().getBulkUpdateHandler().add( triples );
		}

		return model;
	}

	public Model extractModel() {
		return extractModel( ModelFactory.createDefaultModel() );
	}

	public Model extractModel(Model model) {
		extractClassModel( model );
		extractPropertyModel( model );
		extractIndividualModel( model );

		return model;

	}

	public Model extractPropertyModel() {
		return extractPropertyModel( ModelFactory.createDefaultModel() );
	}

	public Model extractPropertyModel(Model model) {

		final boolean allSubs = selector.contains( StatementType.ALL_SUBPROPERTY );
		final boolean jenaDirectSubs = selector.contains( StatementType.JENA_DIRECT_SUBPROPERTY );
		final boolean subs = allSubs || jenaDirectSubs
				|| selector.contains( StatementType.DIRECT_SUBPROPERTY );
		final boolean equivs = selector.contains( StatementType.EQUIVALENT_PROPERTY );
		final boolean invs = selector.contains( StatementType.INVERSE_PROPERTY );
		final boolean disjs = selector.contains( StatementType.DISJOINT_PROPERTY );

		kb.prepare();

		List<Triple> triples = new ArrayList<Triple>();

		for( Role role : kb.getRBox().getRoles() ) {

			triples.clear();

			if( role.isAnon() )
				continue;

			ATermAppl name = role.getName();

			Node s, p;

			s = makeGraphNode( name );
			p = RDF.type.asNode();

			if( role.isDatatypeRole() )
				addTriple( triples, s, p, OWL.DatatypeProperty.asNode() );
			else if( role.isObjectRole() )
				addTriple( triples, s, p, OWL.ObjectProperty.asNode() );
			else
				continue;

			if( role.isFunctional() )
				addTriple( triples, s, p, OWL.FunctionalProperty.asNode() );
			if( role.isInverseFunctional() )
				addTriple( triples, s, p, OWL.InverseFunctionalProperty.asNode() );
			if( role.isTransitive() )
				addTriple( triples, s, p, OWL.TransitiveProperty.asNode() );
			if( role.isSymmetric() )
				addTriple( triples, s, p, OWL.SymmetricProperty.asNode() );

			if( equivs ) {
				p = OWL.equivalentProperty.asNode();
				for( ATermAppl eq : kb.getAllEquivalentProperties( name ) ) {
					Node o = makeGraphNode( eq );
					addTriple( triples, s, p, o );
					if( allSubs )
						addTriple( triples, s, RDFS.subPropertyOf.asNode(), o );
				}
			}

			if( invs ) {
				Set<ATermAppl> inverses = kb.getInverses( name );
				if( !inverses.isEmpty() ) {
					p = OWL.inverseOf.asNode();
					for( ATermAppl inverse : inverses ) {
						addTriple( triples, s, p, makeGraphNode( inverse ) );
					}
				}
			}
			
			if( disjs ) {
				Set<Set<ATermAppl>> disjoints = kb.getDisjointProperties( name );
				if( !disjoints.isEmpty() ) {
					p = OWL2.propertyDisjointWith.asNode();
					
					Iterator<ATermAppl> i = IteratorUtils.flatten( disjoints.iterator() ); 
					while( i.hasNext() ) {
						Node o = makeGraphNode( i.next() );						
						addTriple( triples, s, p, o );						
					}
				}
			}

			if( subs ) {
				p = RDFS.subPropertyOf.asNode();

				if( allSubs ) {
					Set<ATermAppl> eqs = kb.getAllEquivalentProperties( name );
					for( ATermAppl eq : eqs ) {
						Node o = makeGraphNode( eq );
						addTriple( triples, s, p, o );
					}
				}
				
				Set<Set<ATermAppl>> supers = kb.getSuperProperties( name, !allSubs );

				if( !supers.isEmpty() ) {
					Iterator<ATermAppl> i = IteratorUtils.flatten( supers.iterator() ); 
					while( i.hasNext() ) {
						Node o = makeGraphNode( i.next() );
						addTriple( triples, s, p, o );
					}

					if( jenaDirectSubs ) {
						p = ReasonerVocabulary.directSubPropertyOf.asNode();
						
						Set<Set<ATermAppl>> direct = allSubs
							? kb.getSuperProperties( name, true )
							: supers;
						i = IteratorUtils.flatten( direct.iterator() ); 
						while( i.hasNext() ) {
							Node o = makeGraphNode( i.next() );
							addTriple( triples, s, p, o );
						}
					}
				}
			}

			// FIXME: Add domain statements

			// FIXME: Add range statements

			model.getGraph().getBulkUpdateHandler().add( triples );
		}

		return model;
	}

	/**
	 * Get the selector
	 */
	public EnumSet<StatementType> getSelector() {
		return selector;
	}
	
	/**
	 * Sets the selector
	 */
	public void setSelector(EnumSet<StatementType> selector) {
		this.selector = selector;
	}
	
	/**
	 * @return Returns the reasoner.
	 */
	public KnowledgeBase getKB() {
		return kb;
	}

	/**
	 * @param reasoner
	 *            The reasoner to set.
	 */
	public void setKB(KnowledgeBase kb) {
		this.kb = kb;
	}

	/**
	 * Get the filter used to filter out any unwanted inferences from the result. 
	 * 
	 * @return
	 */
	public Filter<Triple> getFilter() {
		return filter;
	}

	/**
	 * Sets the filter that will filter out any unwanted inferences from the result. The
	 * filter should process {@link Triple} objects and return <code>true</code> for any
	 * triple that should not be included in the result. Use {@link #FILTER_NONE} to disable
	 * filtering.
	 * 
	 * @param filter
	 */
	public void setFilter(Filter<Triple> filter) {
		if( filter == null )
			throw new NullPointerException( "Filter cannot be null" );
		
		this.filter = filter;
	}		
}
