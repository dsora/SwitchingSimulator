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
	private final static String OUTPUT_FOLDER = System.getProperty("user.home")
			+ File.separator + "OfflineOutput";
	private final static String OUTPUT_FILE = "DailyInfoAnalysis.";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			createInput();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		File output;
		File check = new File(OUTPUT_FOLDER);
		if (!check.exists()) {
			check.mkdir();
		}
		PrintWriter outputWriter = null;
		BufferedReader br = null;
		String inLine = "";
		double percentage = 1;
				
		for (int i = 0; i < 5; i++) {
			

			output = new File(OUTPUT_FOLDER + File.separator + OUTPUT_FILE+percentage+".txt");
			if (!output.exists()) {
				try {
					output.createNewFile();
					br = new BufferedReader(new FileReader(new File(INPUT_FOLDER
							+ File.separator + INPUT_FILE)));
					inLine = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				outputWriter = new PrintWriter(output);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			double[] savedEnegy = new double[4];
			for (int j = 0; j < savedEnegy.length; j++) {
				savedEnegy[j] = 0;
			}
			boolean[] old = new boolean[4];
			int switchCount = 0;
			int wrongCount = 0;
			while (inLine != null) {
				String[] data = inLine.split(" ");
				double ideal = Double.parseDouble(data[6]); // real consumption ---> ideal environment
				double real = Double.parseDouble(data[5]); // predicted ---> real environment
															// consumption
				real *= percentage;

				double[] consumptions = new double[4];
				for (int j = 1; j <= consumptions.length; j++) {
					consumptions[j - 1] = Double.parseDouble(data[j]);
				}
				boolean[] resultReal = computeSwitch(real, consumptions);
				boolean[] resultIdeal = computeSwitch(ideal, consumptions);
				for (int j = 0; j < resultReal.length; j++) {
					if (!resultIdeal[j] && resultReal[j] && !old[j]) {
						wrongCount++;
					}
					if (resultReal[j] != old[j]) {
						switchCount++;
					}
					if (resultReal[j]) {
						savedEnegy[j] += consumptions[j];
					}
				}
				old = resultReal;
				try {
					inLine= br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			outputWriter.println("Percentage renewable used: " + (percentage*100) +"%"+
					"\nTotal switches: "+switchCount+
					"\nTotal errors: "+wrongCount+
					"\nSaved energy: ");
			String saved = "";
			double sum = 0;
			for(int j = 0; j < savedEnegy.length; j++){
				saved += "Line"+j+": "+savedEnegy[j]+"\t";
				sum += savedEnegy[j];
			}
			saved+="\nTot: "+sum;
//			saved+="\n";
//			for(int j = 0; j < savedEnegy.length; j++){
//				saved += savedEnegy[j]+"\t";
//			}
			outputWriter.println(saved);
			percentage -= 0.2;
			outputWriter.close();		
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static boolean[] computeSwitch(double renewable, double[] consume) {
		int size = consume.length;
		int upper = (int) renewable;
		boolean[] ret = new boolean[size];
		double[][] opt = new double[size + 1][upper + 1];
		// printMatrix(opt);
		boolean[][] sol = new boolean[size + 1][upper + 1];
		// printMatrix(sol);
		for (int n = 1; n <= size; n++) {
			for (int w = 1; w <= upper; w++) {
				// Hp1: Don't activate it
				double option1 = opt[n - 1][w];
				// Hp2: Activate it
				double option2 = -1;
				int augmentedConsumption = (int) (Math.ceil(consume[n - 1]));
				if (augmentedConsumption <= w)
					option2 = consume[n - 1]
							+ opt[n - 1][(w - augmentedConsumption)];

				// which better?
				opt[n][w] = Math.max(option1, option2);
				sol[n][w] = (option2 >= option1);
			}
		}
		// printMatrix(sol);

		// long step;
		for (int n = size, w = upper; n > 0; n--) {
			if (sol[n][w]) {
				ret[n - 1] = true;
				w = w - (int) (Math.ceil(consume[n - 1]));

			} else {
				ret[n - 1] = false;
			}
			// step = System.currentTimeMillis();
		}

		return ret;
	}

	private static void createInput() throws IOException {
		File folder = new File(INPUT_FOLDER);
		if (!folder.exists()) {
			folder.mkdir();
		}
		File input = new File(folder.getPath() + File.separator + INPUT_FILE);
		if (!input.exists()) {
			input.createNewFile();
		}else return;

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
		int index = ((int) (Math.random() * LOADS.length));
		return LOADS[index];
	}
}
