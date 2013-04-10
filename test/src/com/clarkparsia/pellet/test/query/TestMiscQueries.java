package com.clarkparsia.pellet.test.query;

import static com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory.DatatypeAtom;
import static com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory.DomainAtom;
import static com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory.InverseOfAtom;
import static com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory.ObjectPropertyAtom;
import static com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory.PropertyValueAtom;
import static com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory.RangeAtom;
import static com.clarkparsia.pellet.utils.TermFactory.BOTTOM;
import static com.clarkparsia.pellet.utils.TermFactory.BOTTOM_DATA_PROPERTY;
import static com.clarkparsia.pellet.utils.TermFactory.BOTTOM_OBJECT_PROPERTY;
import static com.clarkparsia.pellet.utils.TermFactory.TOP;
import static com.clarkparsia.pellet.utils.TermFactory.TOP_DATA_PROPERTY;
import static com.clarkparsia.pellet.utils.TermFactory.TOP_OBJECT_PROPERTY;
import static com.clarkparsia.pellet.utils.TermFactory.literal;
import static com.clarkparsia.pellet.utils.TermFactory.var;

import org.junit.Test;
import org.mindswap.pellet.utils.Namespaces;

import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.Datatypes;
import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.parser.ARQParser;

public class TestMiscQueries extends AbstractQueryTest {

	@Test
	public void domainQuery1() {
		classes( C, D );
		objectProperties( p, q );
		dataProperties( r );
		
		kb.addSubProperty( q, p );
		kb.addDomain( p, C );
		
		ATermAppl pv = var( "pv" );
		ATermAppl cv = var( "cv" );
		
		Query query = query(
				select(pv, cv),
				where(DomainAtom(pv, cv)));
		
		testQuery( query, new ATermAppl[][] { 
				 {p, TOP}, {q, TOP}, {r, TOP}, {TOP_OBJECT_PROPERTY, TOP}, {TOP_DATA_PROPERTY, TOP}, {BOTTOM_OBJECT_PROPERTY, TOP}, {BOTTOM_DATA_PROPERTY, TOP},
				 {BOTTOM_DATA_PROPERTY, BOTTOM}, {BOTTOM_OBJECT_PROPERTY, BOTTOM},
				 {p, C}, {q, C}, {BOTTOM_DATA_PROPERTY, C}, {BOTTOM_OBJECT_PROPERTY, C},
				 {BOTTOM_DATA_PROPERTY, D}, {BOTTOM_OBJECT_PROPERTY, D}} );
		
	}
	
	@Test
	public void domainQuery2() {
		classes( C, D );
		objectProperties( p, q );
		dataProperties( r );		
		
		kb.addSubProperty( q, p );
		kb.addDomain( p, C );
		
		ATermAppl cv = var( "cv" );
		
		Query query = query(
				select(cv),
				where(DomainAtom(q, cv)));
		
		testQuery( query, new ATermAppl[][] {
				{TOP},
				{C}});
		
	}
	
	@Test
	public void domainQuery3() {
		classes( C, D );
		objectProperties( p, q );
		dataProperties( r );		
		
		kb.addSubProperty( q, p );
		kb.addDomain( p, C );
		
		ATermAppl pv = var( "pv" );
		
		Query query = query(
				select(pv),
				where(DomainAtom(pv, C)));
		
		testQuery( query, new ATermAppl[][] { {p}, {q}, {BOTTOM_OBJECT_PROPERTY}, {BOTTOM_DATA_PROPERTY}} );
		
	}
	
	@Test
	public void rangeQuery1() {
		classes( C, D );
		objectProperties( p, q );
		dataProperties( r );
		
		kb.addSubProperty( q, p );
		kb.addRange( p, C );
		
		ATermAppl pv = var( "pv" );
		ATermAppl cv = var( "cv" );
		
		Query query = query(
				select(pv, cv),
				where(RangeAtom(pv, cv), ObjectPropertyAtom(pv)));
		
		testQuery( query, new ATermAppl[][] { 
				 {p, TOP}, {q, TOP}, {TOP_OBJECT_PROPERTY, TOP}, {BOTTOM_OBJECT_PROPERTY, TOP},
				 {BOTTOM_OBJECT_PROPERTY, BOTTOM},
				 {p, C}, {q, C}, {BOTTOM_OBJECT_PROPERTY, C},
				 {BOTTOM_OBJECT_PROPERTY, D}
				} );
		
	}
	
	@Test
	public void rangeQuery2() {		
		classes( C, D );
		objectProperties( p, q );
		dataProperties( r );
				
		kb.addSubProperty( q, p );
		kb.addRange( p, C );
		
		ATermAppl cv = var( "cv" );
		
		Query query = query(
				select(cv),
				where(RangeAtom(q, cv)));
		
		testQuery( query, new ATermAppl[][] {
				{TOP},
				{C}});
		
	}
	
	@Test
	public void datatypeQuery() {		
		dataProperties( p );
		individuals( a, b, c );
				
		kb.addPropertyValue( p, a, literal( 3 ) );
		kb.addPropertyValue( p, b, literal( 300 ) );
		kb.addPropertyValue( p, b, literal( "3" ) );
		
		Query query1 = query(
				select(x),
				where(PropertyValueAtom( x, p, y ),
					  DatatypeAtom(y,Datatypes.INTEGER)));
		
		testQuery( query1, new ATermAppl[][] { { a }, { b } } );
		
		Query query2 = query(
				select(x),
				where(PropertyValueAtom( x, p, y ),
					  DatatypeAtom(y,Datatypes.BYTE)));
		
		testQuery( query2, new ATermAppl[][] { { a } } );
		
	}
	
	@Test
	public void classQuery() {		
		classes( A, B, C );
		
		Query query1 = new ARQParser().parse( 
			"PREFIX rdf: <" + Namespaces.RDF + "> " +
			"PREFIX owl: <" + Namespaces.OWL + "> " +
			"SELECT ?c WHERE { ?c rdf:type owl:Class }", kb );
		
		testQuery( query1, new ATermAppl[][] { { A }, { B }, { C }, { TOP }, { BOTTOM } } );	
	}
	
	@Test
	public void inverseQuery() {
		classes( C, D );
		objectProperties( p, q, r );
		
		kb.addInverseProperty( q, p );
		kb.addSymmetricProperty(r);		
		
		ATermAppl v = var( "v" );
		
		Query query = query(
				select(v),
				where(InverseOfAtom(q, v)));
		
		testQuery( query, new ATermAppl[][] {
				{p}});
		
	}

	
	@Test
	public void symmetricQuery() {
		classes( C, D );
		objectProperties( p, q, r );
		
		kb.addInverseProperty( q, p );
		kb.addSymmetricProperty(r);
		
		ATermAppl v = var( "v" );
		
		Query query = query(
				select(v),
				where(InverseOfAtom(v, v)));
		
		testQuery( query, new ATermAppl[][] {
				{r}, {TOP_OBJECT_PROPERTY}, {BOTTOM_OBJECT_PROPERTY}});
		
	}
}
