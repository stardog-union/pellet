package com.clarkparsia.pellet.owlapi;

import aterm.ATermAppl;
import com.clarkparsia.pellet.sparqldl.engine.QueryEngine;
import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory;
import com.clarkparsia.pellet.sparqldl.model.QueryImpl;
import com.clarkparsia.pellet.sparqldl.model.QueryResult;
import java.util.ArrayList;
import java.util.List;
import org.mindswap.pellet.utils.ATermUtils;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
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

public class EntailmentQueryVisitor implements OWLAxiomVisitor
{

	private final IndividualTermConverter indConv;

	private final PelletReasoner reasoner;

	private Query query;

	private class IndividualTermConverter implements OWLIndividualVisitor
	{

		private ATermAppl term;

		public ATermAppl getTerm(OWLIndividual individual)
		{
			term = null;
			individual.accept(this);
			return term;
		}

		//@Override
		@Override
		public void visit(OWLNamedIndividual individual)
		{
			term = reasoner.term(individual);
		}

		//@Override
		@Override
		public void visit(OWLAnonymousIndividual individual)
		{
			term = ATermUtils.makeVar(individual.toStringID());
		}

	}

	public EntailmentQueryVisitor(final PelletReasoner reasoner)
	{
		this.reasoner = reasoner;
		this.indConv = new IndividualTermConverter();
		reset();
	}

	public boolean isEntailed()
	{
		final QueryResult results = QueryEngine.exec(query);
		return !results.isEmpty();

	}

	public void reset()
	{
		query = new QueryImpl(reasoner.getKB(), false);
	}

	@Override
	public void visit(final OWLClassAssertionAxiom axiom)
	{
		final ATermAppl ind = indConv.getTerm(axiom.getIndividual());
		final ATermAppl cls = reasoner.term(axiom.getClassExpression());
		query.add(QueryAtomFactory.TypeAtom(ind, cls));
	}

	@Override
	public void visit(final OWLDataPropertyAssertionAxiom axiom)
	{
		final ATermAppl subj = indConv.getTerm(axiom.getSubject());
		final ATermAppl pred = reasoner.term(axiom.getProperty());
		final ATermAppl obj = reasoner.term(axiom.getObject());
		query.add(QueryAtomFactory.PropertyValueAtom(subj, pred, obj));
	}

	@Override
	public void visit(final OWLDifferentIndividualsAxiom axiom)
	{
		final List<ATermAppl> differents = new ArrayList<ATermAppl>();
		for (final OWLIndividual ind : axiom.getIndividuals())
		{
			final ATermAppl term = indConv.getTerm(ind);
			for (final ATermAppl dterm : differents)
			{
				query.add(QueryAtomFactory.DifferentFromAtom(term, dterm));
			}
		}
	}

	@Override
	public void visit(final OWLNegativeDataPropertyAssertionAxiom axiom)
	{
		final ATermAppl subj = indConv.getTerm(axiom.getSubject());
		final ATermAppl pred = reasoner.term(axiom.getProperty());
		final ATermAppl obj = reasoner.term(axiom.getObject());
		query.add(QueryAtomFactory.NegativePropertyValueAtom(subj, pred, obj));
	}

	@Override
	public void visit(final OWLNegativeObjectPropertyAssertionAxiom axiom)
	{
		final ATermAppl subj = indConv.getTerm(axiom.getSubject());
		final ATermAppl pred = reasoner.term(axiom.getProperty());
		final ATermAppl obj = indConv.getTerm(axiom.getObject());
		query.add(QueryAtomFactory.NegativePropertyValueAtom(subj, pred, obj));
	}

	@Override
	public void visit(final OWLObjectPropertyAssertionAxiom axiom)
	{
		final ATermAppl subj = indConv.getTerm(axiom.getSubject());
		final ATermAppl pred = reasoner.term(axiom.getProperty());
		final ATermAppl obj = indConv.getTerm(axiom.getObject());
		query.add(QueryAtomFactory.PropertyValueAtom(subj, pred, obj));
	}

	@Override
	public void visit(final OWLSameIndividualAxiom axiom)
	{
		ATermAppl head = null;
		for (final OWLIndividual ind : axiom.getIndividuals())
		{
			final ATermAppl term = indConv.getTerm(ind);
			if (head == null)
				head = term;
			else
				query.add(QueryAtomFactory.SameAsAtom(head, term));
		}
	}
}
