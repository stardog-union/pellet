// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Pair;

import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import com.clarkparsia.pellet.datatypes.exceptions.UnrecognizedDatatypeException;
import com.clarkparsia.pellet.rules.model.AtomDConstant;
import com.clarkparsia.pellet.rules.model.AtomDVariable;
import com.clarkparsia.pellet.rules.model.AtomIConstant;
import com.clarkparsia.pellet.rules.model.AtomIVariable;
import com.clarkparsia.pellet.rules.model.AtomObject;
import com.clarkparsia.pellet.rules.model.AtomObjectVisitor;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;
import com.clarkparsia.pellet.rules.model.ClassAtom;
import com.clarkparsia.pellet.rules.model.DataRangeAtom;
import com.clarkparsia.pellet.rules.model.DatavaluedPropertyAtom;
import com.clarkparsia.pellet.rules.model.DifferentIndividualsAtom;
import com.clarkparsia.pellet.rules.model.IndividualPropertyAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.rules.model.RuleAtom;
import com.clarkparsia.pellet.rules.model.RuleAtomVisitor;
import com.clarkparsia.pellet.rules.model.SameIndividualAtom;
import com.clarkparsia.pellet.rules.rete.Compiler;
import com.clarkparsia.pellet.rules.rete.TermTuple;

import aterm.ATermAppl;

/**
 * <p>
 * Title: Rules To Rete Translator
 * </p>
 * <p>
 * Description: Translates from the rules package rule objects to the rete
 * package rules.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Ron Alford
 */
public class RulesToReteTranslator {
	private static Logger log = ABox.log;

	private class AtomObjectTranslator implements AtomObjectVisitor {
		private DependencySet dependency = DependencySet.INDEPENDENT;
		private ATermAppl result = null;

		public DependencySet getDependency() {
			return dependency;
		}

		public ATermAppl getResult() {
			return result;
		}

		public void visit(AtomDConstant constant) {
			ATermAppl canonical;
			final ATermAppl literal = constant.getValue();
			try {
				canonical = abox.getKB().getDatatypeReasoner()
						.getCanonicalRepresentation(literal);
			} catch( InvalidLiteralException e ) {
				final String msg = format( "Invalid literal (%s) in SWRL data constant: %s",
						literal, e.getMessage() );
				if( PelletOptions.INVALID_LITERAL_AS_INCONSISTENCY ) {
					log.fine( msg );
					canonical = literal;
				}
				else {
					log.severe( msg );
					throw new InternalReasonerException( msg, e );
				}
			} catch( UnrecognizedDatatypeException e ) {
				final String msg = format(
						"Unrecognized datatype in literal appearing (%s) in SWRL data constant: %s",
						literal, e.getMessage() );
				log.severe( msg );
				throw new InternalReasonerException( msg, e );
			}
			result = canonical;
		}

		public void visit(AtomDVariable variable) {
			result = ATermUtils.makeVar(variable.getName());
		}

		public void visit(AtomIConstant constant) {
			abox.copyOnWrite();
			Individual individual = abox.getIndividual(constant.getValue());
			if (individual.isMerged()) {
				dependency = individual.getMergeDependency(true);
				individual = individual.getSame();
			}

			result = individual.getName();
		}

		public void visit(AtomIVariable variable) {
			result = ATermUtils.makeVar(variable.getName());
		}

	}

	private class AtomTranslator implements RuleAtomVisitor {
		private DependencySet ds = null;
		private TermTuple result = null;

		public AtomTranslator(DependencySet ds) {
			this.ds = ds;
		}

		public TermTuple getResult() {
			return result;
		}

		public void visit(BuiltInAtom atom) {
			log.fine("Not translating built-in " + atom + " to rete triple.");
		}

		public void visit(ClassAtom atom) {
			Pair<ATermAppl, DependencySet> arg = translateAtomObject(atom
					.getArgument());
			DependencySet mergedDS = ds;
			if (arg.second != DependencySet.INDEPENDENT)
				mergedDS = ds.union(arg.second, abox.doExplanation());

			result = new TermTuple(mergedDS, Compiler.TYPE, arg.first, atom
					.getPredicate());
		}

		public void visit(DataRangeAtom atom) {
			log.fine("Not translating data range atom " + atom
					+ " to rete triple.");
		}

		public void visit(DatavaluedPropertyAtom atom) {
			Pair<ATermAppl, DependencySet> arg1 = translateAtomObject(atom
					.getArgument1());
			Pair<ATermAppl, DependencySet> arg2 = translateAtomObject(atom
					.getArgument2());
			DependencySet mergedDS = ds;
			if (arg1.second != DependencySet.INDEPENDENT)
				mergedDS = ds.union(arg1.second, abox.doExplanation());
			if (arg2.second != DependencySet.INDEPENDENT)
				mergedDS = ds.union(arg2.second, abox.doExplanation());

			result = new TermTuple(mergedDS, atom.getPredicate(), arg1.first,
					arg2.first);
		}

		public void visit(DifferentIndividualsAtom atom) {
			Pair<ATermAppl, DependencySet> arg1 = translateAtomObject(atom
					.getArgument1());
			Pair<ATermAppl, DependencySet> arg2 = translateAtomObject(atom
					.getArgument2());
			DependencySet mergedDS = ds;
			if (arg1.second != DependencySet.INDEPENDENT)
				mergedDS = ds.union(arg1.second, abox.doExplanation());
			if (arg2.second != DependencySet.INDEPENDENT)
				mergedDS = ds.union(arg2.second, abox.doExplanation());

			result = new TermTuple(mergedDS, Compiler.DIFF_FROM, arg1.first,
					arg2.first);
		}

		public void visit(IndividualPropertyAtom atom) {
			Pair<ATermAppl, DependencySet> arg1 = translateAtomObject(atom
					.getArgument1());
			Pair<ATermAppl, DependencySet> arg2 = translateAtomObject(atom
					.getArgument2());
			DependencySet mergedDS = ds;
			if (arg1.second != DependencySet.INDEPENDENT)
				mergedDS = ds.union(arg1.second, abox.doExplanation());
			if (arg2.second != DependencySet.INDEPENDENT)
				mergedDS = ds.union(arg2.second, abox.doExplanation());

			result = new TermTuple(mergedDS, atom.getPredicate(), arg1.first,
					arg2.first);
		}

		public void visit(SameIndividualAtom atom) {
			Pair<ATermAppl, DependencySet> arg1 = translateAtomObject(atom
					.getArgument1());
			Pair<ATermAppl, DependencySet> arg2 = translateAtomObject(atom
					.getArgument2());
			DependencySet mergedDS = ds;
			if (arg1.second != DependencySet.INDEPENDENT)
				mergedDS = ds.union(arg1.second, abox.doExplanation());
			if (arg2.second != DependencySet.INDEPENDENT)
				mergedDS = ds.union(arg2.second, abox.doExplanation());

			result = new TermTuple(mergedDS, Compiler.SAME_AS, arg1.first,
					arg2.first);
		}

	}

	private ABox abox;

	public RulesToReteTranslator(ABox abox) {
		this.abox = abox;
	}

	/**
	 * Translates a rule atom into a rete triple. Returns null if the atom can't
	 * be translated.
	 */
	public TermTuple translateAtom(RuleAtom atom, DependencySet ds) {
		AtomTranslator translator = new AtomTranslator(ds);
		atom.accept(translator);
		return translator.getResult();
	}

	/**
	 * Return a pair of a term, and a dependency set. The dependency set will be
	 * non-null if the atom object represented a constant which was merged to
	 * another node.
	 */
	public Pair<ATermAppl, DependencySet> translateAtomObject(AtomObject obj) {
		AtomObjectTranslator translator = new AtomObjectTranslator();
		obj.accept(translator);
		return new Pair<ATermAppl, DependencySet>(translator.getResult(),
				translator.getDependency());
	}

	/**
	 * Translates a list of rule atoms into rete triples. Returns null if an
	 * atom can't be translated.
	 */
	public List<TermTuple> translateAtoms(Collection<? extends RuleAtom> atoms,
			DependencySet ds) {
		List<TermTuple> result = new ArrayList<TermTuple>(atoms.size());

		for (RuleAtom atom : atoms) {
			TermTuple triple = translateAtom(atom, ds);
			if (triple == null) {
				// Can't translate entire set
				return null;
			}
			result.add(triple);
		}

		return result;
	}

	/**
	 * Translates a rule.Rule to a rete.Rule. Returns null if the rule cannot be
	 * translated.
	 */
	public com.clarkparsia.pellet.rules.rete.Rule translateRule(Rule rule) {
		com.clarkparsia.pellet.rules.rete.Rule reteRule;

		DependencySet ds = DependencySet.INDEPENDENT; // TODO Make DS for rule
		List<TermTuple> head = translateAtoms(rule.getHead(), ds);
		List<TermTuple> body = translateAtoms(rule.getBody(), ds);

		if (head == null || body == null) {
			if( log.isLoggable( Level.FINE ) )
				log.fine("Not translating rule " + rule + " to rete format.");
			reteRule = null;
		} else {
			reteRule = new com.clarkparsia.pellet.rules.rete.Rule(body, head);
		}

		return reteRule;
	}
}
