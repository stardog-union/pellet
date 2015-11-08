package com.clarkparsia.pellet.protege;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.semanticweb.owlapi.reasoner.OWLReasoner;


/**
 *
 * @author Evren Sirin
 */
public class PelletPreferencesPanel extends OWLPreferencesPanel {
	private ButtonGroup reasonerType;

	private JTextField serverURL;

	@Override
	public void initialise() throws Exception {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		reasonerType = new ButtonGroup();

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(5,10,0,10);
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		add(createButton(PelletReasonerType.REGULAR), c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5,10,0,10);
		add(createButton(PelletReasonerType.INCREMENTAL), c);

		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.insets = new Insets(5,10,0,10);
		add(createButton(PelletReasonerType.REMOTE), c);

		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.insets = new Insets(5,0,0,10);
		serverURL = new JTextField();
		add(serverURL, c);
	}

	private JRadioButton createButton(PelletReasonerType type) {
		String label = type.toString();
		JRadioButton button = new JRadioButton(label.charAt(0) + label.substring(1).toLowerCase());
		button.setActionCommand(label);

		reasonerType.add(button);
		return button;
	}

	@Override
	public void applyChanges() {
	}

	@Override
	public void dispose() throws Exception {
		OWLReasoner reasoner = getOWLModelManager().getOWLReasonerManager().getCurrentReasoner();
		// FIXME if reasoner is already created display a warning
	}

}