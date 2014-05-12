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
	private static double[] FREQUENT_LOADS = null;
	private static double[] SPARSE_LOADS = null;
	private static double[] vars = { 0, 10, 20, 30, 40 };
	private final static double PRODUCT = 30000;
	private final static String INPUT_FOLDER = "OfflineInput";
	private final static String INPUT_FILE = "DailyInfo";
	private final static String TXT = ".txt";
	private final static String OUTPUT_FOLDER = "OfflineOutput";
	private final static String OUTPUT_FILE = "DailyInfoAnalysis.";
	private static final double WStoKWH = 3600 * 1000;
	private static final double TO_EURO = 0.16 * 60;
	private static final double INITIAL_COST = 1000;
	private static final long MONTS_TO_MILLISEC = 1000L * 60L * 60L * 24L * 30L;
	private static final String STDDEV = "StdDev_";

	public static void main(String[] args) {
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
		
		for (double stdDev : vars) {
			PrintWriter outputWriter = null;
			BufferedReader br = null;
			String inLine = "";
			double percentage = 1;
			for (int i = 0; i < 5; i++) {

				File outFold = new File(OUTPUT_FOLDER + File.separator + STDDEV
						+ stdDev);
				if(!outFold.exists()){
					outFold.mkdirs();
				}
				output = new File(outFold.getPath()+File.separator+OUTPUT_FILE + (int) (percentage * 100) + TXT);
				if (!output.exists()) {
					try {
						output.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				try {
					br = new BufferedReader(new FileReader(new File(
							INPUT_FOLDER + File.separator +STDDEV+stdDev+File.separator+ INPUT_FILE+TXT)));
					inLine = br.readLine();
					outputWriter = new PrintWriter(output);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				long[][] sizes = new long[4][4]; //maintains number of samples
				double[][] means_matrix = new double[4][4];
				for(int j = 0; j < means_matrix.length;j++){
					means_matrix[j] = new double[4];
				}
				
				double[][] variances_matrix = new double[4][4];
				for(int j = 0; j < variances_matrix.length;j++){
					variances_matrix[j] = new double[4];
				}
				
				double[][] m2_s_matrix = new double[4][4];
				for(int j = 0; j < m2_s_matrix.length;j++){
					m2_s_matrix[j] = new double[4];
				}
				
				
				double[] totalEnergy = new double[4];
				double[] savedEnegy = new double[4];
				for (int j = 0; j < savedEnegy.length; j++) {
					savedEnegy[j] = 0;
				}
				boolean[] old = new boolean[4];
				int switchCount = 0;
				int wrongCount = 0;
				boolean[] resultReal = new boolean[4];
				// boolean[] resultIdeal = new boolean[4];
				double real = 0;
				
				while (inLine != null) {
				
					String[] data = inLine.split(" ");
					double ideal = Double.parseDouble(data[6]); // real
																// consumption
																// ---> ideal
																// environment
					// double real = Double.parseDouble(data[5]); // predicted
					// --->
					// real environment
					// consumption
					Date d = new Date(Long.parseLong(data[0]));
					String[] date = d.toString().split(" ");
					String[] hhmmss = date[3].split(":");
					int h = Integer.parseInt(hhmmss[0]);
					//System.out.println(h/6);
//					double[] means = means_matrix[h/6];
//					double[] variances = variances_matrix[h/6];
//					double[] m2_s = m2_s_matrix[h/6];
					
					real *= percentage;
					double consumed = 0;
					double[] consumptions = new double[4];
					for (int j = 1; j <= consumptions.length; j++) {
						consumptions[j - 1] = Double.parseDouble(data[j]);
						
						//mean and variances update
						sizes[h/6][j-1]++;
						double delta = consumptions[j - 1] - means_matrix[h/6][j-1];
						means_matrix[h/6][j-1] += (delta /(double)sizes[h/6][j-1]); // update mean
						m2_s_matrix[h/6][j-1] += (consumptions[j - 1] - means_matrix[h/6][j-1]) * delta;// update m2
						if(sizes[h/6][j-1]>1){
								variances_matrix[h/6][j-1] = m2_s_matrix[h/6][j-1] / (sizes[h/6][j-1] - 1.0); // update variance
						}
						//
						
						if (resultReal[j - 1])
							consumed += consumptions[j - 1];
					}
					
					if (consumed > ideal) {
						// CASE: ERROR
						for (int j = 0; j < resultReal.length; j++) {

							totalEnergy[j] += consumptions[j]; // consumption
																// total
																// for line

							// TO DELETE
							if (resultReal[j]) {
								savedEnegy[j] += consumptions[j]; // saved
																	// energy
																	// for line
							}
							//

							if (resultReal[j] && !old[j]) {// Errors count
								wrongCount++;
							}
							if (resultReal[j] != old[j]) {// total switches
															// count
								switchCount++;
							}
						}

						old = resultReal;
						resultReal = new boolean[4]; // emergency recovery: all
														// lines on the wires

					} else {
						for (int j = 0; j < resultReal.length; j++) {

							totalEnergy[j] += consumptions[j]; // consumption
																// total
																// for line

							if (resultReal[j]) {
								savedEnegy[j] += consumptions[j]; // saved
																	// energy
																	// for line
							}

							if (resultReal[j] != old[j]) {// total switches
															// count
								switchCount++;
							}
						}
						old = resultReal;
						resultReal = computeSwitch(real, consumptions);
					}
					real = Double.parseDouble(data[5]);
					try {
						inLine = br.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				outputWriter.println("Percentage renewable used: "
						+ (int) (percentage * 100) + "%" + "\nTotal switches: "
						+ switchCount + "\nTotal errors: " + wrongCount
						+ "\nError Percent: "
						+ (double) ((double) wrongCount / (double) switchCount)
						* 100 + "\nSaved energy per Line: ");
				String saved = "";
				double sum = 0;
				double totEn = 0;
				for (int j = 0; j < savedEnegy.length; j++) {
					saved += "Line" + j + ": " + savedEnegy[j] / WStoKWH + "\t";
					sum += savedEnegy[j];
					totEn += totalEnergy[j];
				}
				saved += "\nTotal per Line:\n";
				for (int j = 0; j < totalEnergy.length; j++) {
					saved += "Line" + j + ": " + totalEnergy[j] / WStoKWH
							+ "\t";
					sum += savedEnegy[j];
					totEn += totalEnergy[j];
				}
				saved += "\n<Mean,Variance> per line:\n";
				for (int j = 0; j < means_matrix.length; j++) {
					
//					saved += "Line" + j + ": <" +means[j]+","+variances[j] + ">\n";
					saved+= "Time slot"+j+":\n";
					for(int h = 0; h < means_matrix[j].length;h++){
						saved += "\tLine" + h + ": <" +means_matrix[j][h]+","+variances_matrix[j][h] + ">\n";
					}
				}
				
				double kwhTot = totEn / WStoKWH;
				double kwhSaved = sum / WStoKWH;
				saved += "\nTot Saved: " + kwhSaved;
				saved += "\nTot Consumed: " + kwhTot;
				saved += "\nPercent saved: " + (sum / totEn) * 100 + "%";
				saved += "\nSaved Euro: " + kwhSaved * TO_EURO;
				saved += "\nTotal Euro: " + kwhTot * TO_EURO;
				saved += "\nSystem initial cost:\t" + INITIAL_COST + "Euro";
				saved += "\nFull payback date:\t";
				double months = INITIAL_COST / (kwhSaved * TO_EURO);
				// System.out.println(Math.ceil(months));
				long timeForPayback = System.currentTimeMillis()
						+ ((long) (Math.ceil(months)) * MONTS_TO_MILLISEC * 2);
				System.out.println(MONTS_TO_MILLISEC);
				saved += new Date(timeForPayback);

				outputWriter.println(saved);
				percentage -= 0.2;
				if (percentage < 0)
					percentage = 0.01;
				outputWriter.close();
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
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
				if (augmentedConsumption <= w && augmentedConsumption >= 0)
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
		for (double stdDev : vars) {

			File folderVar = new File(folder.getPath() + File.separator
					+ STDDEV + stdDev);
			if (!folderVar.exists()) {
				folderVar.mkdirs();
			}
			File input = new File(folderVar.getPath() +File.separator+ INPUT_FILE+TXT);
			if (!input.exists()) {
				input.createNewFile();
			} else
				return;

			double[] loads = new double[4];
			double[] sub = new double[4];
			double[] temp = new double[4];
			long time = System.currentTimeMillis();
//			long start = time;
			PrintWriter wr = new PrintWriter(input);

			for (int i = 0; i < ITERATIONS; i++) {
				for (int j = 0; j < loads.length; j++) {
					if (temp[j] <= 0) {
						temp[j] = getRandomLoad(time);
						loads[j] = temp[j];
						sub[j] = (loads[j] * loads[j]) / PRODUCT;
					} else {
						temp[j] -= sub[j];
					}
				}
				Date d = new Date(time);
				double[] consumptions = RandomGenerator.getLinesConsumption(d,
						stdDev);
//				String[] date = d.toString().split(" ");

				// (time - start) / 1000

				String toPrint = "" + time + " ";
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
	}

	private static double getRandomLoad(long time) {
		if (SPARSE_LOADS == null) {
			SPARSE_LOADS = new double[20000];
			SPARSE_LOADS[0] = 50;
			SPARSE_LOADS[50] = 100;
			SPARSE_LOADS[100] = 300;
			SPARSE_LOADS[150] = 1000;
			// for(int i = 0; i < SPARSE_LOADS.length; i++){
			// if(i%(SPARSE_LOADS.length/4) != 0)
			// SPARSE_LOADS[i] = 0;
			// }

		}
		if (FREQUENT_LOADS == null) {
			FREQUENT_LOADS = new double[2000];
			FREQUENT_LOADS[0] = 50;
			FREQUENT_LOADS[50] = 100;
			FREQUENT_LOADS[100] = 300;
			FREQUENT_LOADS[150] = 1000;
			// for(int i = 0; i < SPARSE_LOADS.length; i++){
			// if(i%(SPARSE_LOADS.length/4) != 0)
			// SPARSE_LOADS[i] = 0;
			// }
		}
		double[] loads = null;
		Date d = new Date(time);
		String[] date = d.toString().split(" ");
		String[] hhmmss = date[3].split(":");
		int h = Integer.parseInt(hhmmss[0]);
		if ((h >= 0 && h <= 7) || (h >= 8 && h <= 16)) {
			loads = SPARSE_LOADS;
		} else {
			loads = FREQUENT_LOADS;
		}

		int index = ((int) (Math.random() * loads.length));
		// System.out.println(loads[index]);
		System.out.println(index);
		return loads[index];
	}

	// private static void printArray(double[] x){
	// for (double d : x) {
	// System.out.print(d+"\t");
	// }
	// System.out.println();
	// }
}
