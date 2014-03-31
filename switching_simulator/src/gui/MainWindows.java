package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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

	private JPanel ovLine1;

	private JPanel ovLine2;

	private JPanel ovLine3;

	private JPanel ovLine4;

	private JPanel buttonPanel;

	private JButton startAllButton;

	private JButton stopAllButton;

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

		tabbedPane = new JTabbedPane();

		overviewPanel = new JPanel(new GridLayout(5, 1));

		ovLine1 = new JPanel(new BorderLayout());
		ovLine1.add(new JLabel("Line1"), BorderLayout.NORTH);
		ovLine2 = new JPanel(new BorderLayout());
		ovLine2.add(new JLabel("Line2"), BorderLayout.NORTH);
		ovLine3 = new JPanel(new BorderLayout());
		ovLine3.add(new JLabel("Line3"), BorderLayout.NORTH);
		ovLine4 = new JPanel(new BorderLayout());
		ovLine4.add(new JLabel("Line4"), BorderLayout.NORTH);

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

		buttonPanel = new JPanel();
		buttonPanel.add(startAllButton);
		buttonPanel.add(stopAllButton);
		overviewPanel.add(buttonPanel);
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
		tabbedPane.add("Overview", overviewPanel);
		tabbedPane.add("Line 1", line1);
		tabbedPane.add("Line 2", line2);
		tabbedPane.add("Line 3", line3);
		tabbedPane.add("Line 4", line4);

		this.add(tabbedPane);

		// this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(800, 600));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		this.pack();
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
			}
			if (!mainWindows.line2.getStatus()) {
				mainWindows.line2.click();
			}
			if (!mainWindows.line3.getStatus()) {
				mainWindows.line3.click();
			}
			if (!mainWindows.line4.getStatus()) {
				mainWindows.line4.click();
			}
		} else if (e.getActionCommand().equals("stop_all")) {
			Container parent = (Container) e.getSource();
			while (!(parent instanceof MainWindows)) {
				parent = parent.getParent();
			}
			MainWindows mainWindows = (MainWindows) parent;
			if (mainWindows.line1.getStatus()) {
				mainWindows.line1.click();
			}
			if (mainWindows.line2.getStatus()) {
				mainWindows.line2.click();
			}
			if (mainWindows.line3.getStatus()) {
				mainWindows.line3.click();
			}
			if (mainWindows.line4.getStatus()) {
				mainWindows.line4.click();
			}
		}
	}
}
