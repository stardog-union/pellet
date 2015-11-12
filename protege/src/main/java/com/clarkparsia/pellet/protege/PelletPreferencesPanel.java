package com.clarkparsia.pellet.protege;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.protege.editor.owl.model.inference.ProtegeOWLReasonerInfo;
import org.protege.editor.owl.model.inference.ReasonerStatus;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;


/**
 *
 * @author Evren Sirin
 */
public class PelletPreferencesPanel extends OWLPreferencesPanel {
	private enum ExplanationMode {
		NONE("Disable explanations"), LIMITED("Limit explanations to"), ALL("Show all explanations");

		private final String label;

		ExplanationMode(final String theLabel) {
			label = theLabel;
		}

		@Override
		public String toString() {
			return label;
		}
	}

	private ButtonGroup reasonerModeGroup = new ButtonGroup();;
	private EnumMap<PelletReasonerMode, JRadioButton> reasonerModeButtons = new EnumMap<PelletReasonerMode, JRadioButton>(PelletReasonerMode.class);
	private JTextField serverURL = new JTextField();


	private ButtonGroup explanationModeGroup = new ButtonGroup();;
	private EnumMap<ExplanationMode, JRadioButton> explanationModeButtons = new EnumMap<ExplanationMode, JRadioButton>(ExplanationMode.class);
	private JSpinner explanationCount;

	@Override
	public void initialise() throws Exception {
		setLayout(new BorderLayout());

		Box box = Box.createVerticalBox();
		box.add(createTypePanel());
		box.add(Box.createVerticalStrut(15));
		box.add(createExplanationPanel());
		box.add(Box.createVerticalGlue());
		add(box, BorderLayout.NORTH);
	}

	private Border createTitledBorder(JPanel panel, String title) {
		Color color = panel.getBackground();
		Border shadow = BorderFactory.createMatteBorder(1, 0, 0, 0, color.darker());
		Border highlight = BorderFactory.createMatteBorder(1, 0, 0, 0, color.brighter());
		Border etchedLine = BorderFactory.createCompoundBorder(shadow, highlight);
		return BorderFactory.createTitledBorder(etchedLine, title);
	}

	private JPanel createTypePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(createTitledBorder(panel, "Reasoner mode"));

		GridBagConstraints c = new GridBagConstraints();

		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				serverURL.setEnabled(reasonerModeButtons.get(PelletReasonerMode.REMOTE).isSelected());
			}
		};

		PelletReasonerPreferences prefs = PelletReasonerPreferences.getInstance();

		reasonerModeGroup = new ButtonGroup();

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(5,10,0,10);
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		panel.add(createButton(PelletReasonerMode.REGULAR, listener), c);

		c.gridy = 1;
		panel.add(createButton(PelletReasonerMode.INCREMENTAL, listener), c);

		c.gridy = 2;
		panel.add(createButton(PelletReasonerMode.REMOTE, listener), c);

		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.insets = new Insets(5, 0, 0, 10);
		serverURL = new JTextField();
		serverURL.setText(prefs.getServerURL());
		serverURL.setEnabled(prefs.getReasonerMode() == PelletReasonerMode.REMOTE);
		panel.add(serverURL, c);

		reasonerModeButtons.get(prefs.getReasonerMode()).setSelected(true);

		return panel;
	}

	private JPanel createExplanationPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(createTitledBorder(panel, "Explanations"));
		GridBagConstraints c = new GridBagConstraints();

		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				explanationCount.setEnabled(explanationModeButtons.get(ExplanationMode.LIMITED).isSelected());
			}
		};

		PelletReasonerPreferences prefs = PelletReasonerPreferences.getInstance();

		int expCount = prefs.getExplanationCount();

		ButtonGroup group = new ButtonGroup();

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(5,10,0,10);
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		panel.add(createButton(ExplanationMode.NONE, explanationModeButtons, explanationModeGroup, listener), c);

		c.gridy = 1;
		panel.add(createButton(ExplanationMode.LIMITED, explanationModeButtons, explanationModeGroup, listener), c);

		c.gridy = 2;
		panel.add(createButton(ExplanationMode.ALL, explanationModeButtons, explanationModeGroup, listener), c);

		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(5, 0, 0, 10);
		explanationCount = new JSpinner(new SpinnerNumberModel(Math.max(1, expCount), 1, 99, 1));
		explanationCount.setEnabled(expCount > 0);
		panel.add(explanationCount, c);

		explanationModeButtons.get(explanationMode(expCount)).setSelected(true);

		return panel;
	}

	private ExplanationMode explanationMode(int expCount) {
		return expCount == 0 ? ExplanationMode.NONE : expCount < 0 ? ExplanationMode.ALL : ExplanationMode.LIMITED;
	}

	private int explanationCount(ExplanationMode explanationMode, int expCount) {
		return explanationMode == ExplanationMode.NONE ? 0 : explanationMode == ExplanationMode.ALL ? -1 : expCount;
	}

	private JRadioButton createButton(PelletReasonerMode type, ActionListener listener) {
		return createButton(type, reasonerModeButtons, reasonerModeGroup, listener);
	}

	private JRadioButton createButton(ExplanationMode type, ActionListener listener) {
		return createButton(type, explanationModeButtons, explanationModeGroup, listener);
	}

	private <E extends Enum<E>> JRadioButton createButton(E type, EnumMap<E, JRadioButton> buttons, ButtonGroup group, ActionListener listener) {
		String label = type.toString();
		JRadioButton button = new JRadioButton(label.charAt(0) + label.substring(1).toLowerCase());
		button.setActionCommand(type.name());
		button.addActionListener(listener);
		button.setAlignmentX(Component.LEFT_ALIGNMENT);

		group.add(button);
		buttons.put(type, button);
		return button;
	}

	@Override
	public void applyChanges() {
		PelletReasonerPreferences prefs = PelletReasonerPreferences.getInstance();

		PelletReasonerMode selectedType = PelletReasonerMode.valueOf(reasonerModeGroup.getSelection().getActionCommand());
		prefs.setReasonerMode(selectedType);

		prefs.setServerURL(serverURL.getText());

		ExplanationMode explanationMode = ExplanationMode.valueOf(explanationModeGroup.getSelection().getActionCommand());
		System.out.println(explanationCount(explanationMode, (Integer) explanationCount.getValue()));
		prefs.setExplanationCount(explanationCount(explanationMode, (Integer) explanationCount.getValue()));

		boolean preferencesUpdated = prefs.save();

		if (preferencesUpdated) {
			OWLModelManager modelManager = getOWLModelManager();
			OWLReasonerManager reasonerManager = modelManager.getOWLReasonerManager();
			ProtegeOWLReasonerInfo reasoner = reasonerManager.getCurrentReasonerFactory();
			ReasonerStatus reasonerStatus = reasonerManager.getReasonerStatus();

			// if pellet was already initialized, we need to reset it
			if (reasoner instanceof PelletReasonerFactory) {
				((PelletReasonerFactory) reasoner).preferencesUpdated();

				if (reasonerStatus == ReasonerStatus.INITIALIZED) {
					reasonerManager.killCurrentReasoner();
					modelManager.fireEvent(EventType.REASONER_CHANGED);
				}
			}
		}
	}

	@Override
	public void dispose() throws Exception {
	}
}