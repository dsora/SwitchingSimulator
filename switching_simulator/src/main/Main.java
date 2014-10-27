package main;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import generator.RandomGenerator;
import gui.MainWindows;

public class Main {
	private final static int LINES = 4;

	public static void main(String[] args) {

		// Training of random data for the simulation
		if (!checkFolder(LINES)) {
			JOptionPane.showMessageDialog(null, "Problems with training files",
					"Error: can't create information set",
					JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		/*
		 * System.out.println(RandomGenerator.generateNormalTraining("Line1"));
		 * System
		 * .out.println(RandomGenerator.generateNormalPrediction("Line1"));
		 * System.out.println(RandomGenerator.generateNormalTraining("Line2"));
		 * System
		 * .out.println(RandomGenerator.generateNormalPrediction("Line2"));
		 * System.out.println(RandomGenerator.generateNormalTraining("Line3"));
		 * System
		 * .out.println(RandomGenerator.generateNormalPrediction("Line3"));
		 * System.out.println(RandomGenerator.generateNormalTraining("Line4"));
		 * System
		 * .out.println(RandomGenerator.generateNormalPrediction("Line4"));
		 */
		Rectangle maximized_size = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		MainWindows w = new MainWindows("SwitchingSimulator0.1");
		w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		w.setVisible(true);
		// w.setExtendedState(w.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		w.setPreferredSize(maximized_size.getSize());
		w.pack();

		// for (int i = 0; i < 20; i++) {
		// double[] toPrint = RandomGenerator.getLinesConsumption(new Date());
		// System.out.println(RandomGenerator.getRenewableProvided() + "\t"
		// + toPrint[0] + "\t" + toPrint[1] + "\t" + toPrint[2]+ "\t"+
		// toPrint[3] + "\t" +
		// (toPrint[0]+toPrint[1]+ toPrint[2]+ toPrint[3]));
		// }
	}

	private static boolean checkFolder(int lines) {
		File folder = new File("Data");
		if (!folder.exists()) {
			for (int i = 1; i <= lines; i++) {
				File f = new File("Data" + File.separator + "Line" + i);
				f.mkdirs();
				File file = new File(f.getPath() + File.separator
						+ "effective_consumption");
				File file2 = new File(f.getPath() + File.separator
						+ "predicted_consumption");
				try {
					file.createNewFile();
					file2.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (!(RandomGenerator.generateNormalPrediction("Line" + i) && RandomGenerator
						.generateNormalTraining("Line" + i))) {
					return false;
				}
			}
		}
		return true;
	}

}
