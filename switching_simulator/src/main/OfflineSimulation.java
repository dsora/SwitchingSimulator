package main;

import generator.RandomGenerator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import utils.EnergyPair;
import utils.Tools;

public class OfflineSimulation {

	private final static int ITERATIONS = 60 * 60 * 24;
	private static double[] FREQUENT_LOADS = null;
	private static double[] SPARSE_LOADS = null;
//	private static double[] vars = { 0, 10, 20, 30, 40 }; //FOR SIMULATED DATA TESTING
	private static double[] vars = { 0 }; //FOR REAL DATA TESTING
	private static String REAL_INPUT_FILE = "RealData.txt";
	// private static double[] percents;
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

	private static final int TIME_SLOTS = 24;
	private static final int N_LINES = 4;

	public static void main(String[] args) {
		try {
//			createSimulatedInput();
			createInputFromRealData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int test = 1; test < 6; test++) {
			File output;
			File check = new File(OUTPUT_FOLDER + "_t" + test);
			if (!check.exists()) {
				check.mkdir();
			}

			for (double stdDev : vars) {
				PrintWriter outputWriter = null;
				BufferedReader br = null;
				String inLine = "";
				double percentage = 1;
				for (int i = 0; i < 5; i++) {

					File outFold = new File(check.getPath() + File.separator
							+ STDDEV + stdDev);
					if (!outFold.exists()) {
						outFold.mkdirs();
					}
					output = new File(outFold.getPath() + File.separator
							+ OUTPUT_FILE + (int) (percentage * 100) + TXT);
					if (!output.exists()) {
						try {
							output.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					File input = new File(INPUT_FOLDER + File.separator
							+ STDDEV + stdDev + File.separator + INPUT_FILE
							+ TXT);
					boolean stop = false;
					try {

						br = new BufferedReader(new FileReader(input));
						inLine = br.readLine();
						outputWriter = new PrintWriter(output);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					long[][] sizes = new long[TIME_SLOTS][4]; // maintains
																// number of
																// samples
					double[][] means_matrix = new double[TIME_SLOTS][4];
					for (int j = 0; j < means_matrix.length; j++) {
						means_matrix[j] = new double[4];
					}

					double[][] variances_matrix = new double[TIME_SLOTS][4];
					for (int j = 0; j < variances_matrix.length; j++) {
						variances_matrix[j] = new double[4];
					}

					double[][] m2_s_matrix = new double[TIME_SLOTS][4];
					for (int j = 0; j < m2_s_matrix.length; j++) {
						m2_s_matrix[j] = new double[4];
					}
					getHistory(input, variances_matrix, sizes, means_matrix,
							m2_s_matrix);

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
																	// --->
																	// ideal
																	// environment
						// double real = Double.parseDouble(data[5]); //
						// predicted
						// --->
						// real environment
						// consumption
						Date d = new Date(Long.parseLong(data[0]));
						String[] date = d.toString().split(" ");
						String[] hhmmss = date[3].split(":");
						int h = Integer.parseInt(hhmmss[0]);
//						int mm = Integer.parseInt(hhmmss[1]);
						// System.out.println(h/6);
						// double[] means = means_matrix[h/6];
						// double[] variances = variances_matrix[h/6];
						// double[] m2_s = m2_s_matrix[h/6];

						// if (TIME_SLOTS == 48 && mm >= 30) {
						// //case 1/2 hour
						// h = (TIME_SLOTS / 2) + h;
						// }

						double consumed = 0;
						double[] consumptions = new double[4];
						for (int j = 1; j <= consumptions.length; j++) {
							consumptions[j - 1] = Double.parseDouble(data[j]);

							// mean and variances update
							sizes[h][j - 1]++;
							double delta = consumptions[j - 1]
									- means_matrix[h][j - 1];
							means_matrix[h][j - 1] += (delta / (double) sizes[h][j - 1]); // update
																							// mean
							m2_s_matrix[h][j - 1] += (consumptions[j - 1] - means_matrix[h][j - 1])
									* delta;// update m2
							if (sizes[h][j - 1] > 1) {
								variances_matrix[h][j - 1] = m2_s_matrix[h][j - 1]
										/ (sizes[h][j - 1] - 1.0); // update
																	// variance
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
																		// for
																		// line
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
							resultReal = new boolean[4]; // emergency recovery:
															// all
															// lines on the
															// wires

						} else {
							for (int j = 0; j < resultReal.length; j++) {

								totalEnergy[j] += consumptions[j]; // consumption
																	// total
																	// for line

								if (resultReal[j]) {
									savedEnegy[j] += consumptions[j]; // saved
																		// energy
																		// for
																		// line
								}

								if (resultReal[j] != old[j]) {// total switches
																// count
									switchCount++;
								}
							}
							old = resultReal;
							switch (test) {

							case 1:
								// TEST1:
								real *= percentage;
								resultReal = Tools.computeSwitch(real,
										consumptions.clone());
								break;
							case 2:
								// TEST2:
								real *= percentage;
								resultReal = Tools.computeSwitch(real,
										consumptions.clone(),
										variances_matrix[h]);
								break;
							case 3:
								// TEST3:
								real *= percentage;
								resultReal = Tools.computeSwitch(real,
										consumptions.clone(),
										variances_matrix[h], means_matrix[h]);
								break;
							case 4:
								// TEST4:
								resultReal = Tools.computeAdaptativeSwitch(
										real, consumptions.clone(),
										variances_matrix[h]);
								stop = true;
								break;
							case 5:

								// TEST5:
								resultReal = Tools.computeAdaptativeSwitch(
										real, consumptions.clone(),
										variances_matrix[h], means_matrix[h]);
								stop = true;
								break;
							}
						}
						real = Double.parseDouble(data[5]);
						try {
							inLine = br.readLine();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					outputWriter
							.println("Percentage renewable used: "
									+ (int) (percentage * 100)
									+ "%"
									+ "\nTotal switches: "
									+ switchCount
									+ "\nTotal errors: "
									+ wrongCount
									+ "\nError Percent: "
									+ (double) ((double) wrongCount / (double) switchCount)
									* 100 + "\nSaved energy per Line: ");
					String saved = "";
					double sum = 0;
					double totEn = 0;
					for (int j = 0; j < savedEnegy.length; j++) {
						saved += "Line" + j + ": " + savedEnegy[j] / WStoKWH
								+ "\t";
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

						// saved += "Line" + j + ": <"
						// +means[j]+","+variances[j] +
						// ">\n";
						saved += "Time slot" + j + ":\n";
						for (int h = 0; h < means_matrix[j].length; h++) {
							saved += "\tLine" + h + ": <" + means_matrix[j][h]
									+ "," + variances_matrix[j][h] + ">\n";
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
					
					if(stop){
						break;
					}
				}
			}
		}
	}

	private static void createSimulatedInput() throws IOException {
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
			File input = new File(folderVar.getPath() + File.separator
					+ INPUT_FILE + TXT);
			if (!input.exists()) {
				input.createNewFile();
			} else
				return;

			double[] loads = new double[N_LINES];
			double[] sub = new double[N_LINES];
			double[] temp = new double[N_LINES];
			long time = System.currentTimeMillis();
			// long start = time;
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
				// String[] date = d.toString().split(" ");

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

	public static void getHistory(File input, double[][] variances_matrix,
			long[][] sizes, double[][] means_matrix, double[][] m2_s_matrix) {
		String inLine = null;
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(input));
			inLine = br.readLine();
			
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		while (inLine != null) {

			String[] data = inLine.split(" ");

			// real environment
			// consumption
			Date d = new Date(Long.parseLong(data[0].trim()));
			String[] date = d.toString().split(" ");
			String[] hhmmss = date[3].split(":");
			int h = Integer.parseInt(hhmmss[0]);
			// System.out.println(h/6);
			// double[] means = means_matrix[h/6];
			// double[] variances = variances_matrix[h/6];
			// double[] m2_s = m2_s_matrix[h/6];

			double[] consumptions = new double[4];
			for (int j = 1; j <= consumptions.length; j++) {
				
				consumptions[j - 1] = Double.parseDouble(data[j]);

				// mean and variances update

				sizes[h][j - 1]++;

				double delta = consumptions[j - 1] - means_matrix[h][j - 1];
				means_matrix[h][j - 1] += (delta / (double) sizes[h][j - 1]); // update
																				// mean

				m2_s_matrix[h][j - 1] += (consumptions[j - 1] - means_matrix[h][j - 1])
						* delta;// update m2
				if (sizes[h][j - 1] > 1) {
					variances_matrix[h][j - 1] = m2_s_matrix[h][j - 1]
							/ (sizes[h][j - 1] - 1.0); // update
														// variance
				}

			}
			try {
				inLine = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void createInputFromRealData() throws IOException {

		File realFile = new File(REAL_INPUT_FILE);
		if (!realFile.exists()) {
			System.err.println("NO REAL DATA FILE");
			System.exit(1);
		}
		
		File folder = new File(INPUT_FOLDER + File.separator + STDDEV + 0.0);
		if (!folder.exists()) {
			folder.mkdirs();
		}else{
			return;
		}
		
		File toCreate = new File(folder.getPath() + File.separator + INPUT_FILE
				+ TXT);
		if (!toCreate.exists()) {
			try {
				toCreate.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Map<Integer, Map<Integer, EnergyPair>> map = 
				new HashMap<Integer, Map<Integer, EnergyPair>>();
		
		//MAP<CENTRALE,MAP<LINEA,MAP.ENTRY<ARRAY_INDEX,ENERGY_VALUE>>>
		HashMap<Integer, EnergyPair> map1 = new HashMap<Integer,EnergyPair>();
		map1.put(3, new EnergyPair(0, 0.0));
		map1.put(4, new EnergyPair(1, 0.0));
		HashMap<Integer, EnergyPair> map2 = new HashMap<Integer,EnergyPair>();
		map2.put(2, new EnergyPair(2, 0.0));
		map2.put(5, new EnergyPair(3, 0.0));
	
		map.put(1,map1);
		map.put(3, map2);
		
		PrintWriter writer = new PrintWriter(new FileWriter(toCreate,true));
		
		String line = null;
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(realFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Real File Not Found");
			System.exit(1);
		}
		
		long systime = 0L;
		
		while((line = br.readLine())!= null){
			String[] dataLine = line.split(",");
			if(!dataLine[1].trim().equals("energy")){
				continue;
			}
			long now = Long.parseLong(dataLine[0].trim());
			
			if(systime == 0)
				systime = now;
//			
//			while(systime < now){
//				addLine(writer,systime,map);
//				systime+= 1000L;
//			}
			
			Integer key1 = new Integer(dataLine[2].trim());
			Map<Integer,EnergyPair> m = map.get(key1);
			
			if(m == null)
				continue;
			
			Integer key2 = new Integer(dataLine[3].trim());
			EnergyPair pair = m.get(key2);
			
			if(pair == null)
				continue;
//			if(key2 == 4 && key1 == 3)
//				System.err.println(dataLine[10].trim());
			pair.setEnergy(Double.parseDouble(dataLine[10].trim()));
			m.put(key2, pair);
			map.put(key1, m);
			
			addLine(writer, now, map);
			
			systime = now;
		}
		Set<Integer> x = map.keySet();
		for (Integer key : x) {
			System.out.println(key+":");
			printMap(map.get(key));
			System.out.println();
		}
		writer.close();
		br.close();
	}

	private static void addLine(PrintWriter writer, long systime,
			Map<Integer, Map<Integer, EnergyPair>> map) {
		Set<Integer> keys1 = map.keySet();
		int size = 0;
		for (Integer key : keys1) {
			size += map.get(key).size();
		}
		
		double[] consumptions = new double[size];
		
		for (Integer key : keys1) {
			Map<Integer,EnergyPair> m = map.get(key);
			Collection<EnergyPair> pairs = m.values();
			for (EnergyPair ep : pairs) {
				consumptions[ep.getIndex()] = ep.getEnergy();
			}
		}
		String toPrint = "";
		toPrint += systime+" ";
		for(int i = 0; i < consumptions.length; i++){
			toPrint += consumptions[i]+ " ";
		}
		toPrint+=RandomGenerator.getRenewablePredicted()+ " "+RandomGenerator.getRenewableProvided();
		writer.println(toPrint);
	}
	
	private static void printMap(Map<Integer,EnergyPair> x){
		Set<Integer> keyset = x.keySet();
		for (Integer key : keyset) {
			System.out.println(key+" "+x.get(key).getEnergy() + " " + x.get(key).getIndex());
		}
	}
}
