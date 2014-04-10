package main;

import generator.RandomGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class OfflineSimulation {

	private final static int ITERATIONS = 60 * 60 * 24;
	private final static double[] LOADS = { 0, 0, 0, 50, 0, 0, 0, 100, 0, 0, 0,
			300, 0, 0, 0, 1000, 0, 0, 0, 0 };
	private final static double PRODUCT = 20000;
	private final static String INPUT_FOLDER = "OfflineInput";
	private final static String INPUT_FILE = "DailyInfo.txt";
	private final static String OUTPUT_FOLDER = "OfflineInput";
	private final static String OUTPUT_FILE = "DailyInfoAnalysis.txt";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			createInput();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File output = new File(OUTPUT_FOLDER + File.separator + OUTPUT_FILE);
		if (!output.exists()) {
			new File(OUTPUT_FOLDER).mkdir();
			try {
				output.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		PrintWriter outputWriter = null;
		BufferedReader br = null;
		String inLine = "";
		try {
			outputWriter = new PrintWriter(output);
			br = new BufferedReader(new FileReader(new File(INPUT_FOLDER
					+ File.separator + INPUT_FILE)));
			inLine = br.readLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while(inLine != null) {
			String[] data= inLine.split(" ");
		}

	}

	private static void createInput() throws IOException {
		File folder = new File(INPUT_FOLDER);
		if (!folder.exists()) {
			folder.mkdir();
		}
		File input = new File(folder.getPath() + File.separator + INPUT_FILE);
		if (!input.exists()) {
			input.createNewFile();
		}

		double[] loads = new double[4];
		double[] sub = new double[4];
		double[] temp = new double[4];
		long time = System.currentTimeMillis();
		long start = time;
		PrintWriter wr = new PrintWriter(input);

		for (int i = 0; i < ITERATIONS; i++) {
			for (int j = 0; j < loads.length; j++) {
				if (temp[j] <= 0) {
					temp[j] = getRandomLoad();
					loads[j] = temp[j];
					sub[j] = (loads[j] * loads[j]) / PRODUCT;
				} else {
					temp[j] -= sub[j];
				}
			}
			double[] consumptions = RandomGenerator
					.getLinesConsumption(new Date(time));
			String toPrint = "" + (time - start) / 1000 + " ";
			for (int j = 0; j < loads.length; j++) {
				toPrint += (consumptions[j] + loads[j]) + " ";
			}
			toPrint += RandomGenerator.getRenewablePredicted() + " "
					+ RandomGenerator.getRenewableProvided();
			wr.println(toPrint);
			time += 1000;
		}
		wr.close();
	}

	private static double getRandomLoad() {
		int index = ((int) (Math.random() * PRODUCT) % LOADS.length);
		return LOADS[index];
	}
}
