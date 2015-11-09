package com.clarkparsia.pellet.protege;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

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
	private ButtonGroup reasonerModeSelection;
	private EnumMap<PelletReasonerMode, JRadioButton> reasonerMode = new EnumMap<PelletReasonerMode, JRadioButton>(PelletReasonerMode.class);

	private JTextField serverURL;

	@Override
	public void initialise() throws Exception {
		setLayout(new BorderLayout());
		add(createTypePanel(), BorderLayout.NORTH);
	}

	private JPanel createTypePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Reasoner mode"));
		GridBagConstraints c = new GridBagConstraints();

		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				serverURL.setEnabled(reasonerMode.get(PelletReasonerMode.REMOTE).isSelected());
			}
		};

		PelletReasonerPreferences prefs = PelletReasonerPreferences.getInstance();

		reasonerModeSelection = new ButtonGroup();

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(5,10,0,10);
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		panel.add(createButton(PelletReasonerMode.REGULAR, listener), c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5,10,0,10);
		panel.add(createButton(PelletReasonerMode.INCREMENTAL, listener), c);

		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.insets = new Insets(5,10,0,10);
		panel.add(createButton(PelletReasonerMode.REMOTE, listener), c);

		reasonerMode.get(prefs.getReasonerMode()).setSelected(true);

		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.insets = new Insets(5, 0, 0, 10);
		serverURL = new JTextField();
		serverURL.setText(prefs.getServerURL());
		serverURL.setEnabled(prefs.getReasonerMode() == PelletReasonerMode.REMOTE);
		panel.add(serverURL, c);

		return panel;
	}

	private JRadioButton createButton(PelletReasonerMode type, ActionListener listener) {
		String label = type.toString();
		JRadioButton button = new JRadioButton(label.charAt(0) + label.substring(1).toLowerCase());
		button.setActionCommand(label);
		button.addActionListener(listener);
		button.setAlignmentX(Component.LEFT_ALIGNMENT);

		reasonerModeSelection.add(button);
		reasonerMode.put(type, button);
		return button;
	}

	@Override
	public void applyChanges() {
		PelletReasonerPreferences prefs = PelletReasonerPreferences.getInstance();

		PelletReasonerMode selectedType = PelletReasonerMode.valueOf(reasonerModeSelection.getSelection().getActionCommand());
		prefs.setReasonerMode(selectedType);

		prefs.setServerURL(serverURL.getText());

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