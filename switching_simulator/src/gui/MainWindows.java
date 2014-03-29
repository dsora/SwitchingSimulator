package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.tools.Tool;

import threads.LineConsumption;
import utils.InformationSet;
import utils.Tools;

public class MainWindows extends JFrame {

	/**
	 * Eclipse serial version UID
	 */
	private static final long serialVersionUID = 6410732228111728916L;

	private JTabbedPane tabbedPane;
	private JPanel line1;
	private JPanel line2;
	private JPanel line3;
	private JPanel line4;
	private JPanel overviewPanel;

	private JPanel ovLine1;

	private JPanel ovLine2;

	private JPanel ovLine3;

	private JPanel ovLine4;

	public MainWindows(String title) {
		super(title);
		Rectangle maximized_size = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (UnsupportedLookAndFeelException e) {
		}
		tabbedPane = new JTabbedPane();

		overviewPanel = new JPanel(new GridLayout(4, 1));

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

		try {
			line1 = new LinePanel(new LineConsumption(Tools.loadFile("Line1",
					"effective_consumption")));
			line2 = new LinePanel(new LineConsumption(Tools.loadFile("Line2",
					"effective_consumption")));
			line3 = new LinePanel(new LineConsumption(Tools.loadFile("Line3",
					"effective_consumption")));
			line4 = new LinePanel(new LineConsumption(Tools.loadFile("Line4",
					"effective_consumption")));
			
		} catch (FileNotFoundException e) {

		}
		tabbedPane.add("Overview", overviewPanel);
		tabbedPane.add("Line 1", line1);
		tabbedPane.add("Line 2", line2);
		tabbedPane.add("Line 3", line3);
		tabbedPane.add("Line 4", line4);

		this.add(tabbedPane);
		// this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(maximized_size.width,
				maximized_size.height));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		this.pack();
	}
}
