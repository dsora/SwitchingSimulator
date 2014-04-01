package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import threads.UpdatesInformations;

public class DisplayLineInfoPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8002013219835870725L;
	private JLabel nameLabel;
	private JPanel rightPanel;
	private JButton detailsButton;
	protected LinePanel informations = null;
	protected boolean displayed = false;
	protected JFrame win;
	private GridPanel centralPanel;

	public DisplayLineInfoPanel(String name) {
		this.setLayout(new BorderLayout());
		nameLabel = new JLabel(name);
		nameLabel.setForeground(Color.blue);
		add(nameLabel, BorderLayout.NORTH);

		rightPanel = new JPanel();
		detailsButton = new JButton("Details");
		detailsButton.setForeground(Color.blue);
		detailsButton.addActionListener(new GetDetails());
		rightPanel.add(detailsButton);

		add(rightPanel, BorderLayout.EAST);

		centralPanel = new GridPanel();

		add(centralPanel, BorderLayout.CENTER);
	}

	public String getName() {
		return nameLabel.getText();
	}

	public void setLinePanel(LinePanel panel) {

		this.informations = panel;
		UpdatesInformations infoThread = new UpdatesInformations(centralPanel,
				informations);

		Thread t = new Thread(infoThread);
		t.start();
	}
}

class GetDetails implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton source = (JButton) e.getSource();
		Container parent = source.getParent();
		while (!(parent instanceof DisplayLineInfoPanel)) {
			parent = parent.getParent();
		}
		DisplayLineInfoPanel display = (DisplayLineInfoPanel) parent;
		final DisplayLineInfoPanel display2 = display;

		if (display.informations == null) {
			JOptionPane.showMessageDialog(display, "No information available",
					"No information available", JOptionPane.WARNING_MESSAGE);
			return;
		}

		if (display.displayed) {
			if (display.win != null) {
				display.win.dispose();
				display.displayed = false;
			} else {
				throw new RuntimeException(
						"This situation should not be possible!!!");
			}
		}

		String name = display.getName();
		JFrame win = new JFrame(name);
		String infos = display.informations.getDetails();
		JTextArea info = new JTextArea(infos);
		info.setEditable(false);
		win.setLayout(new GridLayout(1, 1));
		win.add(info);
		win.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		win.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				display2.displayed = false;
				e.getWindow().dispose();
			}
		});
		win.setSize(600, 400);
		win.setVisible(true);
		display.displayed = true;
		display.win = win;
	}
}