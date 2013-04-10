// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena;

import java.util.Collection;
import java.util.HashSet;

import org.mindswap.pellet.jena.vocabulary.OWL2;
import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.taxonomy.TaxonomyNode;
import org.mindswap.pellet.utils.TaxonomyUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import aterm.ATermAppl;

/**
 * Extracts a Jena Model from a Taxonomy (i.e., creates a Model that contains only the classes in the taxonomy and the subclass relationships among them).
 *
 * @author Blazej Bulka
 */
public class TaxonomyExtractor {
	private Taxonomy<ATermAppl> taxonomy;
	private Model model;
	private boolean includeIndividuals;
	
	public TaxonomyExtractor( Taxonomy<ATermAppl> taxonomy ) {
		this.taxonomy = taxonomy;
		this.includeIndividuals = false;
	}
	
	public void setIncludeIndividuals( boolean includeIndividuals ) {
		this.includeIndividuals = includeIndividuals;
	}
	
	public Model extractModel() {
		if( model == null ) {
			model = createExtractedModel();
		}
		
		return model;
	}
	
	private Model createExtractedModel() {
		Model model = ModelFactory.createDefaultModel();
		
		HashSet<ATermAppl> processedEquivalentClasses = new HashSet<ATermAppl>();
		
		for( TaxonomyNode<ATermAppl> taxonomyNode : taxonomy.getNodes() ) {
			if( processedEquivalentClasses.contains( taxonomyNode.getName() ) ) { 
				continue;
			}
			
			processedEquivalentClasses.addAll( taxonomyNode.getEquivalents() );
			
			for( ATermAppl aClass : taxonomyNode.getEquivalents() ) {
				model.add( classAssertion( model, aClass ) );
				
				for( TaxonomyNode<ATermAppl> superNode : taxonomyNode.getSupers() ) {
					model.add( subClassOfAssertion( model, aClass, superNode.getName() ) );
				}
				
				if( taxonomyNode.getEquivalents().size() > 1 ) {
					for( ATermAppl equivalentClass : taxonomyNode.getEquivalents() ) {
						if( !equivalentClass.equals( aClass ) ) {
							model.add( equivalentClassAssertion( model, aClass, equivalentClass ) );
						}
					}
				}
				
				if( includeIndividuals ) {
					Collection<ATermAppl> individuals = (Collection<ATermAppl>) taxonomyNode.getDatum( TaxonomyUtils.INSTANCES_KEY );
					
					if( ( individuals != null ) && !individuals.isEmpty() ) {
						for( ATermAppl individual : individuals ) {
							model.add( typeAssertion( model, individual, aClass ) );
						}
					}
				}
			} 
		}
		
		return model;
	}	
	
	private static Statement typeAssertion( Model model, ATermAppl individual, ATermAppl type ) {
		Resource individualResource = createResource( model, individual );
		Property typeProperty = RDF.type;
		Resource typeResource = createResource( model, type );
		
		return model.createStatement( individualResource, typeProperty, typeResource );
	}
	 
	private static Statement classAssertion( Model model, ATermAppl aClass ) {
		Resource classResource = createResource( model, aClass );
		Property typeProperty = RDF.type;
		Resource owlClassResource = OWL2.Class;
		
		return model.createStatement( classResource, typeProperty, owlClassResource );
	}
	
	private static Statement subClassOfAssertion( Model model, ATermAppl subClass, ATermAppl superClass ) {
		Resource subClassResource = createResource( model, subClass );
		Property subClassOfProperty = RDFS.subClassOf;
		Resource superClassResource = createResource( model, superClass );
		
		return model.createStatement( subClassResource, subClassOfProperty, superClassResource );
	}
	
	private static Statement equivalentClassAssertion( Model model, ATermAppl firstClass, ATermAppl secondClass ) {
		Resource firstClassResource = createResource( model, firstClass );
		Property equivalentClassProperty = OWL2.equivalentClass;
		Resource secondClassResource = createResource( model, secondClass );
		
		return model.createStatement( firstClassResource, equivalentClassProperty, secondClassResource );
	}
	
	private static Resource createResource( Model model, ATermAppl term ) {
		return JenaUtils.makeResource( term, model );
	}
}
