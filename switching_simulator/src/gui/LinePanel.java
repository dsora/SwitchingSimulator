package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import threads.LineConsumption;
import utils.InformationSet;

public class LinePanel extends JPanel {

	/**
	 * Generated ID
	 */
	private static final long serialVersionUID = -3051853365466972936L;

	private JTextArea powerArea;// Area in which is listed the line consumption
	// private JTextArea informationsArea;// Area in which is listed the status
	private Boolean status; // false: not running, true: running
	private JPanel topPanel;
	protected LineConsumption lineConsumption;
	
	private JLabel labelStatus;

	private JButton button;

	private JPanel centralPanel;

	private JPanel loadsPanel;

	private ElectricLoadButton load0dot5;

	private ElectricLoadButton load1dot0;

	private ElectricLoadButton load1dot5;

	private ElectricLoadButton load2dot0;

	private ElectricLoadButton load2dot5;

	private JPanel rightPanel;

	protected JPanel toRemovePanel;

	private JPanel topRightPanel;

	private JPanel botRightPanel;
	
	public static final int DISPLAYED_VALUES = 15;
	
	public LinePanel(InformationSet informationSet) {
		
		this.powerArea = new JTextArea();
		// this.informationsArea = new JTextArea();
		this.status = false;
		this.lineConsumption = new LineConsumption(informationSet, this);
		this.setLayout(new BorderLayout());
		
		powerArea.setForeground(Color.BLUE);
		
		topPanel = new JPanel(new GridLayout(1, 2));
		topPanel.add(new JLabel("Status"));
		labelStatus = new JLabel(status ? "Running" : "Stopped");
		labelStatus.setForeground(Color.red);
		topPanel.add(labelStatus);

		centralPanel = new JPanel(new GridLayout(1, 2));
		centralPanel.add(this.powerArea);
		// centralPanel.add(this.informationsArea);
		rightPanel = new JPanel(new GridLayout(2, 1));
		loadsPanel = new JPanel();

		load0dot5 = new ElectricLoadButton("0.5", 0.5);
		load1dot0 = new ElectricLoadButton("1.0", 1.0);
		load1dot5 = new ElectricLoadButton("1.5", 1.5);
		load2dot0 = new ElectricLoadButton("2.0", 2.0);
		load2dot5 = new ElectricLoadButton("2.5", 2.5);

		load0dot5.setActionCommand("addLoad");
		load1dot0.setActionCommand("addLoad");
		load1dot5.setActionCommand("addLoad");
		load2dot0.setActionCommand("addLoad");
		load2dot5.setActionCommand("addLoad");

		load0dot5.addActionListener(new ButtonAction());
		load1dot0.addActionListener(new ButtonAction());
		load1dot5.addActionListener(new ButtonAction());
		load2dot0.addActionListener(new ButtonAction());
		load2dot5.addActionListener(new ButtonAction());
		
		topRightPanel = new JPanel(new BorderLayout());
		botRightPanel = new JPanel(new BorderLayout());
		
		loadsPanel.add(load0dot5);
		loadsPanel.add(load1dot0);
		loadsPanel.add(load1dot5);
		loadsPanel.add(load2dot0);
		loadsPanel.add(load2dot5);

		topRightPanel.add(new Label("Add an electric load:"),BorderLayout.NORTH);
		topRightPanel.add(loadsPanel,BorderLayout.CENTER);
	
		toRemovePanel = new JPanel();
		botRightPanel.add(toRemovePanel,BorderLayout.CENTER);
		botRightPanel.add(new JLabel("Remove a plugged load:"),BorderLayout.NORTH);
		
		rightPanel.add(topRightPanel);
		rightPanel.add(botRightPanel);

		centralPanel.add(rightPanel);

		button = new JButton("ON");
		button.setActionCommand("change");
		button.addActionListener(new ButtonAction());
		button.setForeground(Color.green);
		
		this.add(topPanel, BorderLayout.NORTH);
		this.add(centralPanel, BorderLayout.CENTER);
		this.add(button, BorderLayout.SOUTH);
	}

	public void changeStatus() {
		this.status = !(this.status);
		if (status) {
			button.setText("OFF");
			button.setForeground(Color.red);
			labelStatus.setText("Running");
			labelStatus.setForeground(Color.green);
		} else {
			button.setText("ON");
			button.setForeground(Color.green);
			labelStatus.setText("Stopped");
			labelStatus.setForeground(Color.red);
		}
		validate();
	}
	
	public void click(){
		button.doClick();
	}

	public JTextArea getPowerArea() {
		return powerArea;
	}

	// public JTextArea getInformationsArea() {
	// return informationsArea;
	// }

	public boolean getStatus() {
		return status;
	}
}

class ButtonAction implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getActionCommand().equals("change")) {
			JButton c = (JButton) (e.getSource());
			LinePanel panel = (LinePanel) (c.getParent());
			if (!panel.getStatus()) {
				Thread t = new Thread(panel.lineConsumption);
				t.start();
			} else {
				panel.lineConsumption.stopThread();
			}
			panel.changeStatus();

		} else if (e.getActionCommand().equals("addLoad")) {
			ElectricLoadButton source = (ElectricLoadButton) e.getSource();
			double load = source.getLoad();
			Container parent = source.getParent();
			while (!(parent instanceof LinePanel)) {
				parent = parent.getParent();
			}
			LinePanel panel = (LinePanel) parent;

//			if (!panel.getStatus()) {
//				return;
//			}
			panel.lineConsumption.addConsumptionElement(load);
			//String id = load + "-" + source.hashCode();
			JButton toRemove = new JButton("" + load);
			toRemove.setName(""+load);
			toRemove.addActionListener(new ButtonAction());
			toRemove.setActionCommand("remove");
			panel.toRemovePanel.add(toRemove);
			panel.validate();
		} else if (e.getActionCommand().equals("remove")) {
			
			JButton source = (JButton)e.getSource();
			Container parent = source.getParent();
			while (!(parent instanceof LinePanel)) {
				parent = parent.getParent();
			}
			LinePanel panel = (LinePanel) parent;
//			if (!panel.getStatus()) {
//				return;
//			}
			String id = source.getName();
			panel.toRemovePanel.remove(source);
			panel.lineConsumption.removeConsumptionElement(Double.parseDouble(id));
			panel.validate();
		}
	}
}