package com.clarkparsia.pellet.owlapiv3;

import java.util.ArrayList;
import java.util.List;

import org.mindswap.pellet.utils.ATermUtils;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLIndividualVisitor;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

import aterm.ATermAppl;

import com.clarkparsia.pellet.sparqldl.engine.QueryEngine;
import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory;
import com.clarkparsia.pellet.sparqldl.model.QueryImpl;
import com.clarkparsia.pellet.sparqldl.model.QueryResult;

public class EntailmentQueryVisitor extends OWLAxiomVisitorAdapter {
	
	private IndividualTermConverter indConv;
	
	private PelletReasoner reasoner;
	
	private Query query;
	
	private class IndividualTermConverter implements OWLIndividualVisitor {

		private ATermAppl term;
		
		public ATermAppl getTerm(OWLIndividual individual) {
			term = null;
			individual.accept(this);
			return term;
		}
		//@Override
		public void visit(OWLNamedIndividual individual) {
			term = reasoner.term(individual);
		}

		//@Override
		public void visit(OWLAnonymousIndividual individual) {
			term = ATermUtils.makeVar( individual.toStringID() );
		}
		
	}
	
	public EntailmentQueryVisitor(final PelletReasoner reasoner) {
		this.reasoner = reasoner;
		this.indConv = new IndividualTermConverter();
		reset();
	}
	
	public boolean isEntailed() {
		QueryResult results = QueryEngine.exec( query );
		return !results.isEmpty();
		
	}
	
	public void reset() {
		query = new QueryImpl(reasoner.getKB(), false);
	}
	
	@Override
	public void visit(final OWLClassAssertionAxiom axiom) {
		ATermAppl ind = indConv.getTerm( axiom.getIndividual() );
		ATermAppl cls = reasoner.term( axiom.getClassExpression() );
		query.add( QueryAtomFactory.TypeAtom(ind, cls) );
	}
	
	@Override
	public void visit(final OWLDataPropertyAssertionAxiom axiom) {
		ATermAppl subj = indConv.getTerm( axiom.getSubject() );
		ATermAppl pred = reasoner.term( axiom.getProperty() );
		ATermAppl obj = reasoner.term( axiom.getObject() );
		query.add( QueryAtomFactory.PropertyValueAtom(subj, pred, obj) );
	}
	
	@Override
	public void visit(final OWLDifferentIndividualsAxiom axiom) {
		List<ATermAppl> differents = new ArrayList<ATermAppl>();
		for (OWLIndividual ind : axiom.getIndividuals()) {
			ATermAppl term = indConv.getTerm( ind );
			for (ATermAppl dterm : differents) {
				query.add( QueryAtomFactory.DifferentFromAtom( term, dterm ) );
			}
		}
	}
	
	@Override
	public void visit(final OWLNegativeDataPropertyAssertionAxiom axiom) {
		ATermAppl subj = indConv.getTerm( axiom.getSubject() );
		ATermAppl pred = reasoner.term( axiom.getProperty() );
		ATermAppl obj = reasoner.term( axiom.getObject() );
		query.add( QueryAtomFactory.NegativePropertyValueAtom(subj, pred, obj) );
	}
	
	@Override
	public void visit(final OWLNegativeObjectPropertyAssertionAxiom axiom) {
		ATermAppl subj = indConv.getTerm( axiom.getSubject() );
		ATermAppl pred = reasoner.term( axiom.getProperty() );
		ATermAppl obj = indConv.getTerm( axiom.getObject() );
		query.add( QueryAtomFactory.NegativePropertyValueAtom(subj, pred, obj) );
	}
	
	@Override
	public void visit(final OWLObjectPropertyAssertionAxiom axiom) {
		ATermAppl subj = indConv.getTerm( axiom.getSubject() );
		ATermAppl pred = reasoner.term( axiom.getProperty() );
		ATermAppl obj = indConv.getTerm( axiom.getObject() );
		query.add( QueryAtomFactory.PropertyValueAtom(subj, pred, obj) );
	}
	
	@Override
	public void visit(final OWLSameIndividualAxiom axiom) {
		ATermAppl head = null;
		for (OWLIndividual ind : axiom.getIndividuals() ) {
			ATermAppl term = indConv.getTerm( ind );
			if ( head == null )
				head = term;
			else
				query.add( QueryAtomFactory.SameAsAtom( head, term ) );
		}
	}
}
