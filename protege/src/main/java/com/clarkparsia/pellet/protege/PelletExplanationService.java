package com.clarkparsia.pellet.protege;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.BorderUIResource;

import com.clarkparsia.modularity.IncrementalReasoner;
import com.clarkparsia.owlapi.explanation.PelletExplanation;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.complexible.pellet.client.reasoner.SchemaOWLReasoner;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.protege.editor.owl.ui.OWLAxiomTypeFramePanel;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.explanation.ExplanationResult;
import org.protege.editor.owl.ui.explanation.ExplanationService;
import org.protege.editor.owl.ui.frame.AbstractOWLFrame;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.AxiomListFrame;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.protege.editor.owl.ui.framelist.OWLFrameListPopupMenuAction;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import uk.ac.manchester.cs.owl.explanation.JustificationFormattingManager;
import uk.ac.manchester.cs.owl.explanation.JustificationFrameListRenderer;


/**
 *
 * @author Evren Sirin
 */
public class PelletExplanationService extends ExplanationService {

	@Override
	public void initialise() throws Exception {

	}

	@Override
	public boolean hasExplanation(final OWLAxiom axiom) {
		OWLReasonerManager manager =  getOWLEditorKit().getOWLModelManager().getOWLReasonerManager();
		return axiom.isLogicalAxiom()
		       && manager.getCurrentReasonerFactory() instanceof PelletReasonerFactory
		       && manager.getCurrentReasonerName().startsWith("Pellet")
		       && PelletReasonerPreferences.getInstance().getExplanationCount() != 0;
	}

	@Override
	public ExplanationResult explain(final OWLAxiom axiom) {
		try {
			int explanationCount =  PelletReasonerPreferences.getInstance().getExplanationCount();
			List<Explanation<OWLAxiom>> explanations = getExplanations(axiom, explanationCount);

			return new StaticExplanationResult(getOWLEditorKit(), axiom, explanations);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw Throwables.propagate(e);
		}
	}

	private List<Explanation<OWLAxiom>> getExplanations(final OWLAxiom axiom, final int limit) {
		Set<Set<OWLAxiom>> explanations = explainInference(axiom, limit);

		List<Explanation<OWLAxiom>> result = Lists.newArrayList();
		for (Set<OWLAxiom> explanation : explanations) {
			result.add(new Explanation<OWLAxiom>(axiom, explanation));
		}
		return result;
	}

	private Set<Set<OWLAxiom>> explainInference(final OWLAxiom axiom, final int limit) {
		OWLReasoner reasoner = getOWLEditorKit().getOWLModelManager().getReasoner();
		System.out.println(reasoner.getClass());
		if (reasoner instanceof SchemaOWLReasoner) {
			return ((SchemaOWLReasoner) reasoner).explain(axiom, limit);
		}
		else {
			PelletReasoner pellet = reasoner instanceof PelletReasoner ? (PelletReasoner) reasoner : ((IncrementalReasoner) reasoner).getReasoner();
			PelletExplanation explanation = new PelletExplanation(pellet);
			try {
				return explanation.getEntailmentExplanations(axiom, limit);
			}
			finally {
				explanation.dispose();
			}
		}
	}

	@Override
	public void dispose() throws Exception {
	}

	public static class ExplanationFrame extends AbstractOWLFrame<Explanation<OWLAxiom>> {
		public ExplanationFrame(OWLEditorKit editorKit, Explanation<OWLAxiom> explanation, String label) {
			super(editorKit.getOWLModelManager().getOWLOntologyManager());
			setRootObject(explanation);
			this.addSection(new ExplanationFrameSection(editorKit, this, label));
		}
	}

	public static class ExplanationFrameSection extends AbstractOWLFrameSection<Explanation<OWLAxiom>, OWLAxiom, OWLAxiom> {
		private String label;

		public ExplanationFrameSection(OWLEditorKit editorKit, OWLFrame<? extends Explanation<OWLAxiom>> owlFrame, String label) {
			super(editorKit, label, owlFrame);
			this.label = label;
		}

		public String getLabel() {
			return label;
		}

		protected OWLAxiom createAxiom(OWLAxiom object) {
			return object;
		}

		public OWLObjectEditor<OWLAxiom> getObjectEditor() {
			return null;
		}

		protected void refill(OWLOntology ontology) {
			Explanation<OWLAxiom> explanation = getRootObject();
			if (explanation.getSize() == 1) {
				OWLAxiom axiom = explanation.getAxioms().iterator().next();
				ExplanationFrameSectionRow row = new ExplanationFrameSectionRow(this.getOWLEditorKit(), this, explanation, axiom, 0, 0);
				addRow(row);
			}
			else {
				JustificationFormattingManager formattingManager = JustificationFormattingManager.getManager();
				int count = 1;
				for (OWLAxiom axiom : formattingManager.getOrdering(explanation)) {
					int depth = formattingManager.getIndentation(explanation, axiom);
					ExplanationFrameSectionRow row = new ExplanationFrameSectionRow(this.getOWLEditorKit(), this, explanation, axiom, count++, depth);
					addRow(row);
				}
			}
		}

		protected void clear() {
		}

		public Comparator<OWLFrameSectionRow<Explanation<OWLAxiom>, OWLAxiom, OWLAxiom>> getRowComparator() {
			return null;
		}

		public boolean canAdd() {
			return false;
		}

		public boolean canAcceptDrop(List<OWLObject> objects) {
			return false;
		}
	}


	public static class ExplanationFrameSectionRow extends AbstractOWLFrameSectionRow<Explanation<OWLAxiom>, OWLAxiom, OWLAxiom> {
		private int index;
		private int depth;

		public ExplanationFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection<Explanation<OWLAxiom>, OWLAxiom, OWLAxiom> section, Explanation<OWLAxiom> rootObject, OWLAxiom axiom, int index, int depth) {
			super(owlEditorKit, section, null, rootObject, axiom);
			this.index = index;
			this.depth = depth;
		}

		public int getDepth() {
			return this.depth;
		}

		public String getRendering() {
			return /**index + ") " +**/ Strings.repeat("        ", depth) + super.getRendering().replaceAll("\\s", " ");
		}

		public List<MListButton> getAdditionalButtons() {
			return Collections.emptyList();
		}

		protected OWLObjectEditor<OWLAxiom> getObjectEditor() {
			return null;
		}

		protected OWLAxiom createAxiom(OWLAxiom editedObject) {
			return null;
		}

		public List<? extends OWLObject> getManipulatableObjects() {
			return Arrays.asList(new OWLAxiom[] { this.getAxiom() });
		}

		public boolean isEditable() {
			return false;
		}

		public boolean isDeleteable() {
			return false;
		}

		public boolean isInferred() {
			return false;
		}
	}

	private static class ExplanationFrameList extends OWLFrameList<Explanation<OWLAxiom>> {
		public ExplanationFrameList(OWLEditorKit editorKit, OWLFrame<Explanation<OWLAxiom>> frame) {
			super(editorKit, frame);
			setWrap(false);
			setCellRenderer(new JustificationFrameListRenderer(editorKit));
		}

		@Override
		protected List<MListButton> getButtons(Object value) {
			return Collections.emptyList();
		}

		@Override
		public void addToPopupMenu(OWLFrameListPopupMenuAction<Explanation<OWLAxiom>> action) {
		}

		@Override
		protected List<MListButton> getListItemButtons(MListItem item) {
			return Collections.emptyList();
		}
	}

	private static JComponent createExplanationDisplay(OWLEditorKit editorKit, Explanation<OWLAxiom> explanation, String title) {
		ExplanationFrame frame = new ExplanationFrame(editorKit, explanation, title);
		frame.refill();

		ExplanationFrameList frameList = new ExplanationFrameList(editorKit, frame);
		frameList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		frameList.refreshComponent();

		return frameList;
	}

	public class StaticExplanationResult extends ExplanationResult {
		private OWLEditorKit editorKit;

		private JComponent explanationDisplayHolder;

		public StaticExplanationResult(OWLEditorKit editorKit, OWLAxiom axiom, List<Explanation<OWLAxiom>> explanations) {
			this.editorKit = editorKit;

			setLayout(new BorderLayout());

			explanationDisplayHolder = new Box(BoxLayout.Y_AXIS);
			explanationDisplayHolder.add(Box.createVerticalStrut(5));
			int count = 1;
			for (Explanation<OWLAxiom> explanation : explanations) {
				JComponent display = createExplanationDisplay(editorKit, explanation, "Explanation " + count++);
				explanationDisplayHolder.add(display);
				explanationDisplayHolder.add(Box.createVerticalStrut(20));
			}

			JScrollPane scrollPane = new JScrollPane(new HolderPanel(explanationDisplayHolder));
			scrollPane.setBorder(null);
			scrollPane.getViewport().setOpaque(false);
			scrollPane.getViewport().setBackground(null);
			scrollPane.setOpaque(false);

			JPanel explanationListPanel = new JPanel(new BorderLayout());
			explanationListPanel.add(scrollPane);
			explanationListPanel.setMinimumSize(new Dimension(10, 10));
			explanationListPanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(10, 0, 15, 0), createTitledBorder(explanationListPanel, "Explanations")));

			String title = "Inference";
			JPanel topPanel = new JPanel(new BorderLayout());
			topPanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(10, 0, 15, 0), createTitledBorder(explanationListPanel,title)));
			JComponent display = createExplanationDisplay(editorKit, new Explanation<OWLAxiom>(axiom, ImmutableSet.of(axiom)), title);
			topPanel.add(display);

			add(topPanel, BorderLayout.NORTH);
			add(explanationListPanel, BorderLayout.CENTER);
		}

		@Override
		public Dimension getMinimumSize() {
			return new Dimension(10, 10);
		}

		@Override
		public void dispose() {
			for (Component component : explanationDisplayHolder.getComponents()) {
				if (component instanceof ExplanationFrameList) {
					((ExplanationFrameList) component).dispose();
				}
			}
		}

		@Override
		public Dimension getPreferredSize() {
			Dimension workspaceSize = editorKit.getWorkspace().getSize();
			int width = (int) (workspaceSize.getWidth() * 0.8);
			int height = (int) (workspaceSize.getHeight() * 0.7);
			return new Dimension(width, height);
		}
	}

	private static class HolderPanel extends JPanel implements Scrollable {
		public HolderPanel(JComponent component) {
			super(new BorderLayout());
			setOpaque(false);
			add(component, BorderLayout.NORTH);
		}

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return super.getPreferredSize();
		}

		@Override
		public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
			return 30;
		}

		@Override
		public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
			return 30;
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {
			return true;
		}

		@Override
		public boolean getScrollableTracksViewportHeight() {
			return false;
		}
	}

	private static Border createTitledBorder(JComponent component, String title) {
		Color color = component.getBackground();
		Border shadow = BorderFactory.createMatteBorder(1, 0, 0, 0, color.darker());
		Border highlight = BorderFactory.createMatteBorder(1, 0, 0, 0, color.brighter());
		Border etchedLine = BorderFactory.createCompoundBorder(shadow, highlight);
		return BorderFactory.createTitledBorder(etchedLine, title);
	}
}