package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class LinePanel extends JPanel {

	/**
	 * Generated ID
	 */
	private static final long serialVersionUID = -3051853365466972936L;

	private JTextArea powerArea;// Area in which is listed the line consumption
	private JTextArea informationsArea;// Area in which is listed the status
	private Boolean status; // false: not running, true running
	private JPanel topPanel;

	private JLabel labelStatus;

	private JButton button;

	private JPanel centralPanel;

	public LinePanel() {

		this.powerArea = new JTextArea();
		this.informationsArea = new JTextArea();
		this.status = false;

		this.setLayout(new BorderLayout());

		topPanel = new JPanel(new GridLayout(1, 2));
		topPanel.add(new JLabel("Status"));
		labelStatus = new JLabel(status ? "Running" : "Stopped");
		labelStatus.setForeground(Color.red);
		topPanel.add(labelStatus);

		centralPanel = new JPanel(new GridLayout(1, 2));
		centralPanel.add(this.powerArea);
		centralPanel.add(this.informationsArea);

		button = new JButton("Change");
		button.setActionCommand("change");
		button.addActionListener(new ButtonAction());

		this.add(topPanel, BorderLayout.NORTH);
		this.add(centralPanel, BorderLayout.CENTER);
		this.add(button, BorderLayout.SOUTH);
	}

	public void changeStatus() {
		this.status = !(this.status);
		if (status) {
			labelStatus.setText("Running");
			labelStatus.setForeground(Color.green);
		} else {
			labelStatus.setText("Stopped");
			labelStatus.setForeground(Color.red);
		}
		validate();
	}

	public JTextArea getPowerArea() {
		return powerArea;
	}

	public JTextArea getInformationsArea() {
		return informationsArea;
	}

}

class ButtonAction implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getActionCommand().equals("change")) {
			JButton c = (JButton) (e.getSource());
			((LinePanel) (c.getParent())).changeStatus();
		}
	}

}