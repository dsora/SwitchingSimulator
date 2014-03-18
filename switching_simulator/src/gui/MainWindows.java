package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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

		overviewPanel = new JPanel(new BorderLayout());

		line1 = new LinePanel();
		line2 = new LinePanel();
		line3 = new LinePanel();
		line4 = new LinePanel();

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
