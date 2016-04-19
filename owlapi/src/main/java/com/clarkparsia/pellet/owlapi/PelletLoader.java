// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under the terms of the MIT License.
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

package com.clarkparsia.pellet.owlapi;

import aterm.ATermAppl;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.utils.Timer;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeVisitor;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.RemoveImport;
import org.semanticweb.owlapi.model.RemoveOntologyAnnotation;
import org.semanticweb.owlapi.model.SetOntologyID;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public class PelletLoader
{
	public static Logger log = Logger.getLogger(PelletLoader.class.getName());

	private KnowledgeBase kb;

	// private Set<URI> loadedFiles;

	private OWLOntologyManager manager;

	private final Set<OWLOntology> ontologies;

	/**
	 * Flag to check if imports will be automatically loaded/unloaded
	 */
	private boolean processImports;

	/**
	 * Ontologies that are loaded due to imports but they have not been included in an explicit load statement by the user
	 */
	private final Set<OWLOntology> notImported;

	/**
	 * This is the reverse mapping of imports. The key is an ontology and the value is a set of ontology that imports the ontology used as the key
	 */
	private final Map<OWLOntology, Set<OWLOntology>> importDependencies;

	private final PelletVisitor visitor;

	private final ChangeVisitor changeVisitor = new ChangeVisitor();

	private class ChangeVisitor implements OWLOntologyChangeVisitor
	{

		private boolean reloadRequired;

		public boolean isReloadRequired()
		{
			return reloadRequired;
		}

		/**
		 * Process a change, providing a single call for common reset,accept,isReloadRequired pattern.
		 *
		 * @param change the {@link OWLOntologyChange} to process
		 * @return <code>true</code> if change is handled, <code>false</code> if a reload is required
		 */
		public boolean process(final OWLOntologyChange change)
		{
			this.reset();
			change.accept(this);
			return !isReloadRequired();
		}

		public void reset()
		{
			visitor.reset();
			reloadRequired = false;
		}

		@Override
		public void visit(final AddAxiom change)
		{
			visitor.setAddAxiom(true);
			change.getAxiom().accept(visitor);
			reloadRequired = visitor.isReloadRequired();
		}

		@Override
		public void visit(final RemoveAxiom change)
		{
			visitor.setAddAxiom(false);
			change.getAxiom().accept(visitor);
			reloadRequired = visitor.isReloadRequired();
		}

		@Override
		public void visit(final AddImport change)
		{
			reloadRequired = getProcessImports();
		}

		@Override
		public void visit(final AddOntologyAnnotation change)
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void visit(final RemoveImport change)
		{
			reloadRequired = getProcessImports();
		}

		@Override
		public void visit(final RemoveOntologyAnnotation change)
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void visit(final SetOntologyID change)
		{
			// nothing to do here
		}

	}

	public PelletLoader(final KnowledgeBase kb)
	{
		this.kb = kb;

		visitor = new PelletVisitor(kb);

		processImports = true;

		ontologies = new HashSet<>();
		notImported = new HashSet<>();
		importDependencies = new HashMap<>();
	}

	/**
	 * @deprecated Use {@link #getProcessImports()} instead
	 */
	@Deprecated
	public boolean loadImports()
	{
		return getProcessImports();
	}

	/**
	 * @deprecated Use {@link #setProcessImports(boolean)} instead
	 */
	@Deprecated
	public void setLoadImports(final boolean loadImports)
	{
		setProcessImports(loadImports);
	}

	public boolean getProcessImports()
	{
		return processImports;
	}

	public void setProcessImports(final boolean processImports)
	{
		this.processImports = processImports;
	}

	public void clear()
	{
		visitor.clear();
		kb.clear();
		ontologies.clear();
		notImported.clear();
		importDependencies.clear();

		// loadedFiles = new HashSet();
		// loadedFiles.add( Namespaces.OWL );
		// loadedFiles.add( Namespaces.RDF );
		// loadedFiles.add( Namespaces.RDFS );
	}

	public KnowledgeBase getKB()
	{
		return kb;
	}

	public void setKB(final KnowledgeBase kb)
	{
		this.kb = kb;
	}

	public ATermAppl term(final OWLObject d)
	{
		visitor.reset();
		visitor.setAddAxiom(false);
		d.accept(visitor);

		final ATermAppl a = visitor.result();

		if (a == null)
			throw new InternalReasonerException("Cannot create ATerm from description " + d);

		return a;
	}

	public void reload()
	{
		log.fine("Reloading the ontologies");

		// copy the loaded ontologies
		final Set<OWLOntology> notImportedOnts = new HashSet<>(notImported);

		// clear everything
		clear();

		// load ontologies again
		load(notImportedOnts);
	}

	public void load(final Set<OWLOntology> ontologies)
	{
		final Timer timer = kb.timers.startTimer("load");

		int axiomCount = 0;
		final Collection<OWLOntology> toBeLoaded = new LinkedHashSet<>();
		for (final OWLOntology ontology : ontologies)
			axiomCount += load(ontology, false, toBeLoaded);

		visitor.reset();
		visitor.setAddAxiom(true);

		for (final OWLOntology ontology : toBeLoaded)
			ontology.accept(visitor);

		visitor.verify();

		timer.stop();
	}

	private int load(final OWLOntology ontology, final boolean imported, final Collection<OWLOntology> toBeLoaded)
	{
		// if not imported add it to notImported set
		if (!imported)
			notImported.add(ontology);

		// add to the loaded ontologies
		final boolean added = ontologies.add(ontology);

		// if it was already there, nothing more to do
		if (!added)
			return 0;

		int axiomCount = ontology.getAxioms().size();
		toBeLoaded.add(ontology);

		// if processing imports load the imported ontologies
		if (processImports)
			for (final OWLOntology importedOnt : ontology.getImports())
			{
				// load the importedOnt
				axiomCount += load(importedOnt, true, toBeLoaded);

				// update the import dependencies
				Set<OWLOntology> importees = importDependencies.get(importedOnt);
				if (importees == null)
				{
					importees = new HashSet<>();
					importDependencies.put(importedOnt, importees);
				}
				importees.add(ontology);
			}

		return axiomCount;
	}

	public Set<OWLAxiom> getUnsupportedAxioms()
	{
		return visitor.getUnsupportedAxioms();
	}

	public void unload(final Set<OWLOntology> ontologies)
	{
		for (final OWLOntology ontology : ontologies)
			unload(ontology);
	}

	private void unload(final OWLOntology ontology)
	{
		// remove the ontology from the set
		final boolean removed = ontologies.remove(ontology);

		// if it is not there silently return
		if (!removed)
			return;

		// remove it from notImported set, too
		notImported.remove(ontology);

		// if we are processing imports we might need to unload the
		// imported ontologies
		if (processImports)
			// go over the imports
			for (final OWLOntology importOnt : ontology.getImports())
			{
				// see if the importedOnt is imported by any other ontology
				final Set<OWLOntology> importees = importDependencies.get(importOnt);
				if (importees != null)
				{
					// remove the unloaded ontology from the dependencies
					importees.remove(ontology);
					// if nothing is left
					if (importees.isEmpty())
					{
						// remove the empty set from dependencies
						importDependencies.remove(importOnt);
						// only unload if this ontology was not loaded by the
						// user explicitly
						if (!notImported.contains(importOnt))
							unload(importOnt);
					}
				}
			}
	}

	/**
	 * @return Returns the loaded ontologies.
	 */
	public Set<OWLOntology> getOntologies()
	{
		return Collections.unmodifiableSet(ontologies);
	}

	public OWLOntologyManager getManager()
	{
		return manager;
	}

	public void setManager(final OWLOntologyManager manager)
	{
		this.manager = manager;
	}

	/**
	 * Apply the given changes to the Pellet KB.
	 *
	 * @param changes List of ontology changes to be applied
	 * @return <code>true</code> if changes applied successfully, <code>false</code> otherwise indicating a reload is required
	 * @throws OWLException
	 */
	public boolean applyChanges(final List<? extends OWLOntologyChange> changes)
	{

		for (final OWLOntologyChange change : changes)
		{
			if (!ontologies.contains(change.getOntology()))
				continue;

			if (!changeVisitor.process(change))
			{
				if (log.isLoggable(Level.FINE))
					log.fine("Reload required by ontology change " + change);

				return false;
			}
		}

		return true;
	}

}
