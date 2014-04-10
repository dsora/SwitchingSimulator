package main;

import generator.RandomGenerator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class OfflineSimulation {

	private final static int ITERATIONS = 60 * 60 * 24;
	private static double[] LOADS = { 0, 50, 0, 100, 0, 300, 0, 1000, 0, 0 };
	private final static double PRODUCT = 2000;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			createInput();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createInput() throws IOException {
		File folder = new File("OfflineInput");
		if (!folder.exists()) {
			folder.mkdir();
		}
		File input = new File(folder.getPath() + File.separator
				+ "DailyInfo.txt");
		if (!input.exists()) {
			input.createNewFile();
		}

		double[] loads = new double[4];
		double[] sub = new double[4];
		double[] temp = new double[4];
		long time = System.currentTimeMillis();
		PrintWriter wr = new PrintWriter(input);

		for (int i = 0; i < ITERATIONS; i++) {
			for (int j = 0; j < loads.length; j++) {
				if (temp[j] == 0) {
					temp[j] = getRandomLoad();
					loads[j] = temp[j];
					sub[j] = (loads[j] * loads[j]) / PRODUCT;
				} else {
					temp[j] -= sub[j];
				}
			}
			double[] consumptions = RandomGenerator
					.getLinesConsumption(new Date(time));
			String toPrint = "" + time + " ";
			for (int j = 0; j < loads.length; j++) {
				toPrint += (consumptions[j] + loads[j]) + " ";
			}
			toPrint += RandomGenerator.getRenewablePredicted() + " "
					+ RandomGenerator.getRenewableProvided();
			wr.println(toPrint);
			time+=1000;
		}
		wr.close();
	}

	private static double getRandomLoad() {
		int index = ((int)(Math.random()*PRODUCT)%10);
		return LOADS[index];
	}
}
