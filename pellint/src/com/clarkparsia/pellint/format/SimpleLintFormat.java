// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.format;

import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;

import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.model.Severity;

/**
 * <p>
 * Title: Simple Lint Format
 * </p>
 * <p>
 * Description: The default Lint Format that tries to print a short but informative content for a Lint. 
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Harris Lin
 */
public class SimpleLintFormat implements LintFormat {

	private static final int CLASSES_LIMIT = 6;

	public String format(Lint lint) {
		Set<OWLClass> participatingClasses = lint.getParticipatingClasses();
		Set<OWLClassAxiom> participatingAxioms = lint.getParticipatingAxioms();
		if ((participatingClasses == null || participatingClasses.isEmpty())
				&& (participatingAxioms == null || participatingAxioms.isEmpty())) {
			return "";
		}

		Severity severity = lint.getSeverity();
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(" - ");
		
		if (severity != null) {
			strBuilder.append(severity).append(' ');
		}
			
		if (participatingClasses != null && !participatingClasses.isEmpty()) {
			int i = 0;
			for (Iterator<OWLClass> it = participatingClasses.iterator(); it.hasNext() && i < CLASSES_LIMIT; i++) {
				OWLClass participatingClass = it.next();
				strBuilder.append(participatingClass).append(' ');
			}
			
			if (participatingClasses.size() > CLASSES_LIMIT) {
				strBuilder.append("... and ");
				strBuilder.append(participatingClasses.size() - CLASSES_LIMIT);
				strBuilder.append(" more.");
			}
			
			strBuilder.append('\n');
		} else if (participatingAxioms != null && !participatingAxioms.isEmpty()) {
			for (OWLClassAxiom axiom : participatingAxioms) {
				strBuilder.append(axiom).append('\n');
			}
		}
		
		return strBuilder.toString();
	}

}
