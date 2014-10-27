package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import threads.MasterSwitch;
import utils.InformationSet;
import utils.Tools;

public class MainWindows extends JFrame {

	/**
	 * Eclipse serial version UID
	 */
	private static final long serialVersionUID = 6410732228111728916L;

	private JTabbedPane tabbedPane;

	protected LinePanel line1;
	protected LinePanel line2;
	protected LinePanel line3;
	protected LinePanel line4;

	protected boolean status;

	private JPanel overviewPanel;

	protected DisplayLineInfoPanel ovLine1;

	protected DisplayLineInfoPanel ovLine2;

	protected DisplayLineInfoPanel ovLine3;

	protected DisplayLineInfoPanel ovLine4;

	private JPanel buttonPanel;

	private JButton startAllButton;

	private JButton stopAllButton;

	private JPanel firstPagePanel;
	
	protected List<LinePanel> lines;

	private JButton tempButton;

	private JPanel maxiPanel;

	private JPanel secondPagePanel;

	private JPanel sliderPane;

	protected JSlider lightSlider;

	private JPanel sliderPanel;

	public MainWindows(String title) {
		super(title);
		// Rectangle maximized_size = GraphicsEnvironment
		// .getLocalGraphicsEnvironment().getMaximumWindowBounds();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (UnsupportedLookAndFeelException e) {
		}
		
		sliderPanel = new JPanel(new BorderLayout());
		JTextField sliderTextField = new JTextField(3);
		lightSlider = new LightSlider(sliderTextField);
		sliderTextField.setText(""+lightSlider.getValue());
		sliderPanel.add(lightSlider,BorderLayout.CENTER);
		sliderPanel.add(sliderTextField,BorderLayout.EAST);
		lines = new ArrayList<LinePanel>();
		tabbedPane = new JTabbedPane();
		
		firstPagePanel = new JPanel(new BorderLayout());
		
		overviewPanel = new JPanel(new GridLayout(0, 1));

		ovLine1 = new DisplayLineInfoPanel("Line1");
		ovLine1.setBorder(BorderFactory.createLineBorder(Color.black));
		ovLine2 = new DisplayLineInfoPanel("Line2");
		ovLine2.setBorder(BorderFactory.createLineBorder(Color.black));
		ovLine3 = new DisplayLineInfoPanel("Line3");		
		ovLine3.setBorder(BorderFactory.createLineBorder(Color.black));
		ovLine4 = new DisplayLineInfoPanel("Line4");
		ovLine4.setBorder(BorderFactory.createLineBorder(Color.black));

		overviewPanel.add(ovLine1);
		overviewPanel.add(ovLine2);
		overviewPanel.add(ovLine3);
		overviewPanel.add(ovLine4);
		

		startAllButton = new JButton("Start all lines");
		startAllButton.setActionCommand("start_all");
		startAllButton.setForeground(Color.green);
		startAllButton.addActionListener(new ButtonListener());

		stopAllButton = new JButton("Stop all lines");
		stopAllButton.setActionCommand("stop_all");
		stopAllButton.setForeground(Color.red);
		stopAllButton.addActionListener(new ButtonListener());
		
		tempButton = new JButton("TESTING");
		tempButton.addActionListener(new TempListener(lightSlider));

		buttonPanel = new JPanel();
		buttonPanel.add(startAllButton);
		buttonPanel.add(stopAllButton);
		buttonPanel.add(tempButton);
		
		firstPagePanel.add(buttonPanel,BorderLayout.SOUTH);
		firstPagePanel.add(overviewPanel,BorderLayout.CENTER);
		try {
			InformationSet infoSet1 = Tools.loadFile("Line1",
					"effective_consumption");
			line1 = new LinePanel(infoSet1);
			InformationSet infoSet2 = Tools.loadFile("Line2",
					"effective_consumption");
			line2 = new LinePanel(infoSet2);
			InformationSet infoSet3 = Tools.loadFile("Line3",
					"effective_consumption");
			line3 = new LinePanel(infoSet3);
			InformationSet infoSet4 = Tools.loadFile("Line4",
					"effective_consumption");
			line4 = new LinePanel(infoSet4);

		} catch (FileNotFoundException e) {

		}
		//tabbedPane.add("Overview", firstPagePanel);
		maxiPanel = new JPanel(new GridLayout(1,2));
		maxiPanel.add(firstPagePanel);
		
		
		
		tabbedPane.add("Line 1", line1);
		ovLine1.setLinePanel(line1);
		lines.add(line1);
		
		tabbedPane.add("Line 2", line2);
		ovLine2.setLinePanel(line2);
		lines.add(line2);
		
		tabbedPane.add("Line 3", line3);
		ovLine3.setLinePanel(line3);
		lines.add(line3);
		
		tabbedPane.add("Line 4", line4);
		ovLine4.setLinePanel(line4);
		lines.add(line4);
		
		sliderPane = new JPanel(new GridLayout(2,1));
		JLabel sliderLabel = new JLabel("Set Renewable Available");
		
		sliderPane.add(sliderLabel);
		
		sliderPane.add(sliderPanel,BorderLayout.SOUTH);
		
		secondPagePanel = new JPanel(new BorderLayout());
		secondPagePanel.add(tabbedPane,BorderLayout.CENTER);
		secondPagePanel.add(sliderPane,BorderLayout.SOUTH);
		
		maxiPanel.add(secondPagePanel);
		
//		maxiPanel.add(tabbedPane);
		
		this.add(maxiPanel);
		
		
		//this.setPreferredSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
	}
}

class ButtonListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("start_all")) {
			Container parent = (Container) e.getSource();
			while (!(parent instanceof MainWindows)) {
				parent = parent.getParent();
			}
			MainWindows mainWindows = (MainWindows) parent;
			if (!mainWindows.line1.getStatus()) {
				mainWindows.line1.click();
				mainWindows.ovLine1.updateOnOffButton();
			}
			if (!mainWindows.line2.getStatus()) {
				mainWindows.line2.click();
				mainWindows.ovLine2.updateOnOffButton();
			}
			if (!mainWindows.line3.getStatus()) {
				mainWindows.line3.click();
				mainWindows.ovLine3.updateOnOffButton();
			}
			if (!mainWindows.line4.getStatus()) {
				mainWindows.line4.click();
				mainWindows.ovLine4.updateOnOffButton();
			}
		} else if (e.getActionCommand().equals("stop_all")) {
			Container parent = (Container) e.getSource();
			while (!(parent instanceof MainWindows)) {
				parent = parent.getParent();
			}
			MainWindows mainWindows = (MainWindows) parent;
			if (mainWindows.line1.getStatus()) {
				mainWindows.line1.click();
				mainWindows.ovLine1.updateOnOffButton();
			}
			if (mainWindows.line2.getStatus()) {
				mainWindows.line2.click();
				mainWindows.ovLine2.updateOnOffButton();
			}
			if (mainWindows.line3.getStatus()) {
				mainWindows.line3.click();
				mainWindows.ovLine3.updateOnOffButton();
			}
			if (mainWindows.line4.getStatus()) {
				mainWindows.line4.click();
				mainWindows.ovLine4.updateOnOffButton();
			}
		}
	}
}

//DA ELIMINARE!!!
class TempListener implements ActionListener {
	protected JSlider lightSlider;
	public TempListener(JSlider lightSlider){
		this.lightSlider = lightSlider;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Container parent = (Container) e.getSource();
		((JButton)parent).setEnabled(false);
		while (!(parent instanceof MainWindows)) {
			parent = parent.getParent();
		}
		MainWindows mainWindows = (MainWindows) parent;
		MasterSwitch master = new MasterSwitch(lightSlider);
		master.setInput(mainWindows.lines);
		Thread t = new Thread(master);
		t.start();
	}
}
