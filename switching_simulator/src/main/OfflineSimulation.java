package main;

import generator.RandomGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.EnergyPair;
import utils.Tools;
//import weka.classifiers.timeseries.WekaForecaster;
//import weka.core.Attribute;
//import weka.core.DenseInstance;
//import weka.core.Instance;
//import weka.core.Instances;
//import weka.core.SparseInstance;
//import weka.core.converters.CSVLoader;

public class OfflineSimulation {

	private final static int ITERATIONS = 60 * 60 * 24;
	private static double[] FREQUENT_LOADS = null;
	private static double[] SPARSE_LOADS = null;
	// private static double[] vars = { 0, 10, 20, 30, 40 }; // FOR SIMULATED
	// DATA TESTING
	private static double[] vars = { 0 }; // FOR REAL DATA TESTING
	private static String REAL_INPUT_FILE = "RealData.txt";
	private static String LOG = "./log";
	// private static double[] percents;
	private final static double PRODUCT = 30000;
	private final static String INPUT_FOLDER = "OfflineInput";
	private final static String HISTORY_FOLDER = "History";
	private final static String INPUT_FILE = "DailyInfo";
	private final static String TXT = ".txt";
	private final static String OUTPUT_FOLDER = "OfflineOutput";
	private final static String OUTPUT_FILE = "DailyInfoAnalysis.";
	private static final double WStoKWH = 3600 * 1000;
	private static final double TO_EURO = 0.16 * 60;
	private static final double INITIAL_COST = 1000;
	private static final long MONTHS_TO_MILLISEC = 1000L * 60L * 60L * 24L
			* 30L;
	private static final String STDDEV = "StdDev_";
	private static final int[] ts = { 24, 48, 30 * 24, 60 * 24 };
	// private static final int[] ts = { 24, 48, 30 * 24, 60 * 24 };
	// private static final int TIME_SLOTS = 30 * 24;
	// private static final int timeSlots = 48;
	private static final int N_LINES = 4;
	private static final int TRAINING_DAYS = 1;
	private static final double NUM_SEC = 30;
	private static final int WEKA_LAG = 40;
	private static int count = 0;
	private static double[] sum = new double[N_LINES];
	private static boolean[] res_temp = new boolean[N_LINES];
	private static int APPLIANCE1 = 19;
	private static int APPLIANCE2 = 9;
	private static int APPLIANCE3 = 14;
	private static int APPLIANCE4 = 11;

	public static void main(String[] args) throws Exception {
		// try {
		// for (int i = 1; i <= TRAINING_DAYS; i++) {
		// createSimulatedConsumption(HISTORY_FOLDER + i);
		// }
		// // createSimulatedConsumption(INPUT_FOLDER);
		// createInputFromRealData(REAL_INPUT_FILE);
		// // System.exit(0); //FOR TESTING THE SIMULATOR OUTPUTF
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		File logDir = new File(LOG + File.separator);
		if (!logDir.exists()) {
			logDir.mkdir();
		}

		for (int timeSlots : ts) {
			File tsFile = new File("TS_" + timeSlots);
			if (!tsFile.exists()) {
				tsFile.mkdir();
			}
			for (int test = 1; test < 6; test++) {
				File output;
				File check = new File(tsFile.getPath() + File.separator
						+ OUTPUT_FOLDER + "_t" + test);
				if (!check.exists()) {
					check.mkdir();
				}

				for (double stdDev : vars) {
					PrintWriter outputWriter = null;
					BufferedReader br = null;
					String inLine = "";
					double percentage = 1;
					for (int i = 0; i < 5; i++) {

						PrintWriter logging = new PrintWriter(new File(LOG
								+ File.separator + LOG + " " + timeSlots + " "
								+ test + "_" + (1 - i * 0.2) + TXT));

						File outFold = new File(check.getPath()
								+ File.separator + STDDEV + stdDev);
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
							// int skip = 0;
							// while(skip < 100002){
							// br.readLine();
							// skip++;
							// }
							inLine = br.readLine();
							outputWriter = new PrintWriter(output);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						long[][] sizes = new long[timeSlots][N_LINES]; // maintains
																		// number
																		// of
																		// samples
						double[][] means_matrix = new double[timeSlots][N_LINES];
						for (int j = 0; j < means_matrix.length; j++) {
							means_matrix[j] = new double[N_LINES];
						}

						double[][] variances_matrix = new double[timeSlots][N_LINES];
						for (int j = 0; j < variances_matrix.length; j++) {
							variances_matrix[j] = new double[4];
						}

						double[][] m2_s_matrix = new double[timeSlots][4];
						for (int j = 0; j < m2_s_matrix.length; j++) {
							m2_s_matrix[j] = new double[4];
						}

						for (int td = 1; td <= TRAINING_DAYS; td++) {

							// FILE HISTORY FOR SIMULATED ENVIRONMENT
							// File history = new File(HISTORY_FOLDER + td
							// + File.separator + STDDEV + stdDev
							// + File.separator + INPUT_FILE + TXT);

							// FILE HISTORY FOR REAL MEASUREMENTS ANALISYS
							File history = new File(INPUT_FOLDER
									+ File.separator + STDDEV + vars[0]
									+ File.separator + INPUT_FILE + TXT);
							getHistory(history, variances_matrix, sizes,
									means_matrix, m2_s_matrix, timeSlots);
							// File history2 = new File(HISTORY_FOLDER + 2
							// + File.separator + STDDEV + stdDev
							// + File.separator + INPUT_FILE + TXT);
							//
							// File history3 = new File(HISTORY_FOLDER + 3
							// + File.separator + STDDEV + stdDev
							// + File.separator + INPUT_FILE + TXT);
						}

						// getHistory(history2, variances_matrix, sizes,
						// means_matrix, m2_s_matrix, timeSlots);
						// getHistory(history3, variances_matrix, sizes,
						// means_matrix, m2_s_matrix, timeSlots);
						// Instances[] series = null;
						// WekaForecaster[] forecasters = null;
//						boolean flag = false;
						File report = new File(System.getProperty("user.home")
								+ File.separator + "temp_report" + percentage
								+ ".txt");
						PrintWriter temp_writer = new PrintWriter(report);
						if (test == 6) {
							// forecasters = forecastersInitialize(
							// new File(
							// HISTORY_FOLDER + TRAINING_DAYS //simulated
							// History File
							//
							// + File.separator + STDDEV + stdDev
							// + File.separator + INPUT_FILE + TXT
							// INPUT_FOLDER + File.separator + STDDEV
							// + vars[0] + File.separator
							// + INPUT_FILE + TXT)
							// );
							// series = seriesInitialize(N_LINES);
						}
						double[] totalEnergy = new double[4];
						double[] savedEnegy = new double[4];
						int[] wrong = new int[N_LINES];
						// for (int j = 0; j < savedEnegy.length; j++) {
						// savedEnegy[j] = 0;
						// }
						boolean[] old = new boolean[4];
						int switchCount = 0;
						int wrongCount = 0;
						double totalRen = 0;
						double usedRen = 0;
						boolean[] resultReal = new boolean[4];
						// boolean[] resultIdeal = new boolean[4];
						double real = 0;

						// double[] report = new double[4];
						// PrintWriter rep = new PrintWriter(new
						// File("/home/daniele/report.csv"));
						while (inLine != null) {

							String[] data = inLine.split(",");
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
							int mm = Integer.parseInt(hhmmss[1]);
							// System.out.println(h/6);
							// double[] means = means_matrix[h/6];
							// double[] variances = variances_matrix[h/6];
							// double[] m2_s = m2_s_matrix[h/6];

							if (timeSlots == 48) {
								// TIME_SLOTS == 48
								if (mm >= 30) {
									// case 1/2 hour
									h = (timeSlots / 2) + h;
								}
							} else if (timeSlots == 30 * 24) {
								// // TIME SLOTS == 30 * 24
								// if (mm % 2 != 0) {
								// h = (h * 30) + mm / 2;
								// } else {
								// h = (h * 30) + mm;
								// }
								h = (h * 30) + mm / 2;
							} else if (timeSlots == 60 * 24) {
								// TIME SLOTS == 60 * 24
								h = (h * 60) + mm;
							}

							double consumed = 0;
							double[] consumptions = new double[4];
							for (int j = 1; j <= consumptions.length; j++) {
								consumptions[j - 1] = Double
										.parseDouble(data[j]);

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

							// if (test == 6) {
							// // int ppp;
							// // series = new Instances[4];
							// // for(int k = 0; k < series.length; k++){
							// // ArrayList<Attribute> atts = new
							// // ArrayList<Attribute>();
							// // Attribute a0 = new Attribute("time");
							// // Attribute a1 = new Attribute("value");
							// // atts.add(a0);
							// // atts.add(a1);
							// // series[k] = new Instances("temp"+k, atts,10);
							// // }
							//
							// //
							// rep.println(data[0]+","+consumptions[1]+","+report[1]);
							// if (count < NUM_SEC) {
							// for (int k = 0; k < consumptions.length; k++) {
							// sum[k] += consumptions[k];
							// }
							// count++;
							// } else {
							// for (int k = 0; k < consumptions.length; k++) {
							// sum[k] = (sum[k] + consumptions[k])
							// / (count + 1);
							// }
							// addInstances(series, sum, WEKA_LAG, data[0]);
							// count = 0;
							// sum = new double[N_LINES];
							// }
							// }
							
							if (consumed > ideal) {
								// CASE: ERROR
								wrongCount++;
								for (int j = 0; j < resultReal.length; j++) {
									
										totalEnergy[j] += consumptions[j]; // consumption
									 // total
										// for
										// line
									// TO DELETE
									// if (resultReal[j]) {
									// savedEnegy[j] += consumptions[j]; //
									// saved
									// // energy
									// // for
									// // line
									// }
									//
									
									if (resultReal[j] && !old[j]) {// Errors
										wrong[j]++;						// count
																// ORIGINAL
									}
									if (resultReal[j] != old[j]) {// total
																	// switches
																	// count
										switchCount++;
									}
								}
								logging.println("ERR: " + consumptions[0] + " "
										+ consumptions[1] + " "
										+ consumptions[2] + " "
										+ consumptions[3] + " Cons: "
										+ consumed + " Avail: " + ideal);
								old = resultReal;
								resultReal = new boolean[4]; // emergency
																// recovery:
																// all
																// lines on the
																// wires

							} else {
								usedRen += consumed;
								for (int j = 0; j < resultReal.length; j++) {

									totalEnergy[j] += consumptions[j]; // consumption
																		// total
																		// for
																		// line

									if (resultReal[j]) {
										savedEnegy[j] += consumptions[j]; // saved
																			// energy
																			// for
																			// line
									}

									if (resultReal[j] != old[j]) {// total
																	// switches
																	// count
										switchCount++;
									}
								}

								logging.println("OK: " + consumptions[0] + " "
										+ consumptions[1] + " "
										+ consumptions[2] + " "
										+ consumptions[3] + " Cons: "
										+ consumed + " Avail: " + ideal);
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
											variances_matrix[h],
											means_matrix[h]);
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
											variances_matrix[h],
											means_matrix[h]);
									stop = true;
									break;
								// case 6:
								// // TEST6
								// real *= percentage;
								// if (series[0].size() >= WEKA_LAG) {
								// // System.out.println(22222);
								// flag = true;
								// if (count == 0) {
								// // for (Instances e : series) {
								// // for (Instance instance : e) {
								// // System.out
								// // .print(instance.value(0)+"\t");
								// // }
								// // System.out.println();
								// // }
								// // resultReal = Tools.computeWekaSwitch(
								// // real, series,
								// // means_matrix[h],
								// // variances_matrix[h],
								// // consumptions,
								// // temp_writer);
								// //
								// // res_temp = resultReal;
								// } else {
								// resultReal = res_temp;
								// }
								// }
								// break;
								}

							}
							totalRen+=ideal;
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
							saved += "Line" + j + ": " + savedEnegy[j]
									/ WStoKWH + "\t";
							sum += savedEnegy[j];
							totEn += totalEnergy[j];
						}
						saved += "\nTotal per Line:\n";
						for (int j = 0; j < totalEnergy.length; j++) {
							saved += "Line" + j + ": " + totalEnergy[j]
									/ WStoKWH + "\t";
//							sum += savedEnegy[j];
//							totEn += totalEnergy[j];
						}
						saved += "\n<Mean,Variance> per line:\n";
						for (int j = 0; j < means_matrix.length; j++) {

							// saved += "Line" + j + ": <"
							// +means[j]+","+variances[j] +
							// ">\n";
							saved += "Time slot" + j + ":\n";
							for (int h = 0; h < means_matrix[j].length; h++) {
								saved += "\tLine" + h + ": <"
										+ means_matrix[j][h] + ","
										+ variances_matrix[j][h] + ">\n";
							}
						}

						double kwhTot = totEn / WStoKWH;
						double kwhSaved = sum / WStoKWH;
						double renwableProduced = totalRen / WStoKWH;
						System.err.println(sum+" "+usedRen);
						saved += "\nTot Saved: " + kwhSaved;
						saved += "\nTot Consumed: " + kwhTot;
						saved += "\nPercent saved: " + (sum / totEn) * 100
								+ "%";
						saved += "\nPercent used: " + (usedRen / totalRen) * 100
								+ "%";
						saved += "\nSaved Euro: " + kwhSaved * TO_EURO;
						saved += "\nTotal Euro: " + kwhTot * TO_EURO;
						saved += "\nSystem initial cost:\t" + INITIAL_COST
								+ "Euro";
						saved += "\nFull payback date:\t";
						double months = INITIAL_COST / (kwhSaved * TO_EURO);
						// System.out.println(Math.ceil(months));
						long timeForPayback = System.currentTimeMillis()
								+ ((long) (Math.ceil(months))
										* MONTHS_TO_MILLISEC * 2);
						System.out.println("TS" + timeSlots + " TEST " + test
								+ " STD " + stdDev);
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
						// rep.close();

						if (stop) {
							break;
						}
						logging.close();
					}
				}
				// if(test == 6)
				// return;

			}
		}
	}

	/*
	 * private static boolean addInstances(Instances[] series, double[]
	 * consumptions, int max, String ts) { if (series.length !=
	 * consumptions.length) return false;
	 * 
	 * // Attribute a0 = new Attribute("time","dd-MM-yyyy HH:mm:ss"); //
	 * Attribute a1 = new Attribute("value"); CSVLoader loader = new
	 * CSVLoader();
	 * 
	 * Instances ins = null; try { loader.setSource(new
	 * File("Models"+File.separator+"model_TemporizedCSV2_"+APPLIANCE1+".txt"));
	 * ins = loader.getStructure(); } catch (IOException e) {
	 * e.printStackTrace(); } for (int i = 0; i < series.length; i++) { //
	 * DenseInstance x = new DenseInstance(2); // try { // x.setValue(0,
	 * a0.parseDate(a0.formatDate(new Double(ts)))); // } catch
	 * (NumberFormatException e) { // e.printStackTrace(); // } catch
	 * (ParseException e) { // e.printStackTrace(); // } //
	 * System.err.println(a0.formatDate(new Double(ts).doubleValue())); //
	 * x.setValue(1, consumptions[i]); // if (series[i].size() < max) { //
	 * series[i].add(x); // } else { // insertInstance(series[i], x); // }
	 * Attribute a0 = new Attribute("time"); Attribute a1 = new
	 * Attribute("apparentPower"); SparseInstance inst = new
	 * SparseInstance(ins.numAttributes()); inst.setValue(1, new Long(ts));
	 * inst.setValue(2, consumptions[i]);
	 * 
	 * if (series[i].size() < max) { series[i].add(inst); } else {
	 * insertInstance(series[i], inst); } }
	 * 
	 * // for (Instances inst : series) { // System.out.print(inst.size()); //
	 * System.out.println(); // } return true;
	 * 
	 * }
	 */
	/*
	 * private static void insertInstance(Instances set, Instance x) { for (int
	 * i = 1; i < set.size(); i++) { set.set(i - 1, set.get(i)); }
	 * set.set(set.size() - 1, x); for (Instance instance : set) { //
	 * System.out.println(instance.value(0)); } }
	 */
	/*
	 * private static Instances[] seriesInitialize(int size) { //
	 * ArrayList<Attribute> atts = new ArrayList<Attribute>(); // Attribute a0 =
	 * new Attribute("time","dd-MM-yyyy HH:mm:ss"); // Attribute a1 = new
	 * Attribute("value"); // // atts.add(a0); // atts.add(a1); Instances[] ret
	 * = new Instances[size]; for (int i = 0; i < size; i++) { CSVLoader loader
	 * = new CSVLoader(); try { loader.setSource(new
	 * File("Models"+File.separator+"model_TemporizedCSV2_"+APPLIANCE1+".txt"));
	 * ret[i] = loader.getStructure(); } catch (IOException e) {
	 * e.printStackTrace(); }
	 * 
	 * // new Instances("temp" + i, atts, WEKA_LAG); } return ret; }
	 */
	// private static WekaForecaster[] forecastersInitialize()
	// throws Exception {
	// ArrayList<Attribute> atts = new ArrayList<Attribute>();
	// Attribute a0 = new Attribute("time","dd-MM-yyyy HH:mm:ss");
	// Attribute a1 = new Attribute("value");
	// atts.add(a0);
	// atts.add(a1);
	//
	// Instances[] instances = new Instances[4];
	// // for (int i = 0; i < instances.length; i++) {//INIZIALIZE EMPTY
	// INSTANCES SET
	// // instances[i] = new Instances("I" + i, atts, 50000);
	// // }
	//
	// // BufferedReader br = new BufferedReader(new FileReader(file));
	//
	// // String line = "";
	// // int j = 0;
	// // while ((line = br.readLine()) != null && j < 100000) {
	// // String[] elems = null;
	// // double count = 0;
	// // double[] sums = new double[N_LINES];
	// // while (count < NUM_SEC) {
	// // elems = line.split(",");
	// //
	// // for (int i = 1; i <= N_LINES; i++) {
	// // // System.out.println(elems.length);
	// // double el = new Double(elems[i]);
	// // sums[i - 1] += el;
	// // }
	// // count++;
	// // if ((line = br.readLine()) == null)
	// // break;
	// // }
	// // if (line == null && count < NUM_SEC)
	// // break;
	// // for (int i = 1; i <= 4; i++) {
	// // DenseInstance in = new DenseInstance(2);
	// // // System.out.println(elems[0]);
	// // in.setValue(a0, a0.parseDate(a0.formatDate(new Double(elems[0]))));
	// // // System.out.println(elems[i]);
	// // in.setValue(atts.get(1), new Double(sums[i - 1] / count));
	// // instances[i - 1].add(in);
	// // }
	// // j++;
	// // }
	// // br.close();
	// int[] appliances = {
	// APPLIANCE1,
	// APPLIANCE2,
	// APPLIANCE3,
	// APPLIANCE4
	// };
	//
	// WekaForecaster[] ret = new WekaForecaster[4];
	// for (int i = 0; i < ret.length; i++) {
	// CSVLoader loader = new CSVLoader();
	// loader.setSource(new
	// File("Models"+File.separator+"model_TemporizedCSV2_"+appliances[i]+".txt"));
	// instances[i] = loader.getDataSet();
	// WekaForecaster forecaster = new WekaForecaster();
	// forecaster.setFieldsToForecast("apparentPower");
	// List<String> fieldsToLag = new ArrayList<String>();
	// fieldsToLag.add("apparentPower");
	// forecaster.getTSLagMaker().setFieldsToLag(fieldsToLag);
	// forecaster.getTSLagMaker().setMinLag(1);
	// forecaster.getTSLagMaker().setMaxLag(WEKA_LAG);
	// forecaster.getTSLagMaker().setTimeStampField("time");
	// forecaster.getTSLagMaker().setAddAMIndicator(true);
	// forecaster.buildForecaster(instances[i], System.out);
	// ret[i] = forecaster;
	// }
	// return ret;
	// }

	private static void createSimulatedConsumption(String foldName)
			throws IOException {
		File folder = new File(foldName);
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

				String toPrint = "" + time + ",";
				for (int j = 0; j < loads.length; j++) {
					toPrint += (consumptions[j] + loads[j]) + ",";
				}
				toPrint += RandomGenerator.getRenewablePredicted() + ","
						+ RandomGenerator.getRenewableProvided();
				wr.println(toPrint);
				time += 1000;
			}
			wr.close();
		}
	}

	private static double getRandomLoad(long time) {
		if (SPARSE_LOADS == null) {
			SPARSE_LOADS = new double[9000];
			// for(int i = 1; i < SPARSE_LOADS.length; i++)
			// SPARSE_LOADS[i] = Math.random() * 5;
			SPARSE_LOADS[0] = 50;
			SPARSE_LOADS[50] = 100;
			SPARSE_LOADS[100] = 200;
			SPARSE_LOADS[150] = 100;
			SPARSE_LOADS[200] = 50;
			// for(int i = 0; i < SPARSE_LOADS.length; i++){
			// if(i%(SPARSE_LOADS.length/4) != 0)
			// SPARSE_LOADS[i] = 0;
			// }

		}
		if (FREQUENT_LOADS == null) {
			FREQUENT_LOADS = new double[3051];
			// for(int i = 1; i < FREQUENT_LOADS.length; i++)
			// FREQUENT_LOADS[i] = Math.random() * 5;
			FREQUENT_LOADS[0] = 50;
			FREQUENT_LOADS[50] = 100;
			FREQUENT_LOADS[100] = 300;
			FREQUENT_LOADS[150] = 1000;
			FREQUENT_LOADS[200] = 1000;
			FREQUENT_LOADS[250] = 50;
			FREQUENT_LOADS[300] = 100;
			FREQUENT_LOADS[350] = 300;
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
		if ((h >= 0 && h <= 7) || (h >= 14 && h <= 18)) {
			loads = SPARSE_LOADS;
		} else {
			if (h > 7 && h < 14) {
				loads = FREQUENT_LOADS;
				FREQUENT_LOADS[0] = 50;
				FREQUENT_LOADS[50] = 100;
				FREQUENT_LOADS[100] = 300;
				FREQUENT_LOADS[150] = 500;
				FREQUENT_LOADS[200] = 100;
				FREQUENT_LOADS[250] = 50;
				FREQUENT_LOADS[300] = 500;
				FREQUENT_LOADS[350] = 300;
			} else {
				loads = FREQUENT_LOADS;
				FREQUENT_LOADS[0] = 50;
				FREQUENT_LOADS[50] = 100;
				FREQUENT_LOADS[100] = 300;
				FREQUENT_LOADS[150] = 1000;
				FREQUENT_LOADS[200] = 1000;
				FREQUENT_LOADS[250] = 50;
				FREQUENT_LOADS[300] = 100;
				FREQUENT_LOADS[350] = 300;
			}

		}

		int index = ((int) (Math.random() * loads.length));
		// System.out.println(loads[index]);
		System.out.println(index);
		return loads[index];
	}

	public static void getHistory(File input, double[][] variances_matrix,
			long[][] sizes, double[][] means_matrix, double[][] m2_s_matrix,
			int timeSlots) {
		String inLine = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(input));
			while ((inLine = br.readLine()) != null) {
				try {
					String[] ctrl = inLine.split(",");
					Long.parseLong(ctrl[0]);
					break;
				} catch (NumberFormatException nfe) {
					continue;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		while (inLine != null) {

			String[] data = inLine.split(",");

			// real environment
			// consumption
			Date d = new Date(Long.parseLong(data[0].trim()));
			String[] date = d.toString().split(" ");
			String[] hhmmss = date[3].split(":");
			int h = Integer.parseInt(hhmmss[0]);

			if (timeSlots == 48) {
				int mm = Integer.parseInt(hhmmss[1]);
				// TIME_SLOTS == 48
				if (mm >= 30) {
					// case 1/2 hour
					h = (timeSlots / 2) + h;
				}
			}

			if (timeSlots == 30 * 24) {
				int mm = Integer.parseInt(hhmmss[1]);
				// TIME_SLOTS == 30*24
				// if (mm % 2 != 0) {
				// h = (h * 30) + (mm / 2);
				// } else {
				// h = (h * 30) + (mm / 2);
				// }
				h = (h * 30) + (mm / 2);
			}

			if (timeSlots == 60 * 24) {
				int mm = Integer.parseInt(hhmmss[1]);
				// TIME_SLOTS == 60*24
				// if (mm % 2 != 0) {
				// h = (h * 30) + (mm / 2);
				// } else {
				// h = (h * 30) + (mm / 2);
				// }
				h = (h * 60) + (mm);
			}

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

	public static void createInputFromRealData(String realDataFile)
			throws IOException {

		File realFile = new File(realDataFile);
		if (!realFile.exists()) {
			System.err.println("NO REAL DATA FILE");
			System.exit(1);
		}

		File folder = new File(INPUT_FOLDER + File.separator + STDDEV + 0.0);
		if (!folder.exists()) {
			folder.mkdirs();
		} else {
			return;
		}

		File toCreate = new File(folder.getPath() + File.separator + INPUT_FILE
				+ TXT);

		File toCreate2 = new File(folder.getPath() + File.separator
				+ INPUT_FILE + 2 + TXT);

		if (!toCreate.exists()) {
			try {
				toCreate.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Map<Integer, Map<Integer, EnergyPair>> map = new HashMap<Integer,
		// Map<Integer, EnergyPair>>();

		// MAP<CENTRALE,MAP<LINEA,MAP.ENTRY<ARRAY_INDEX,ENERGY_VALUE>>>
		HashMap<Integer, EnergyPair> map1 = new HashMap<Integer, EnergyPair>();
		map1.put(APPLIANCE1, new EnergyPair(0, 0.0));
		map1.put(APPLIANCE2, new EnergyPair(1, 0.0));
		map1.put(APPLIANCE3, new EnergyPair(2, 0.0));
		map1.put(APPLIANCE4, new EnergyPair(3, 0.0));
		//
		// HashMap<Integer, EnergyPair> map2 = new HashMap<Integer,
		// EnergyPair>();
		// map2.put(2, new EnergyPair(2, 0.0));
		// map2.put(5, new EnergyPair(3, 0.0));
		//
		// map.put(1, map1);
		// map.put(3, map2);

		PrintWriter writer = new PrintWriter(new FileWriter(toCreate, true));
		PrintWriter writer2 = new PrintWriter(new FileWriter(toCreate2, true));
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(realFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Real File Not Found");
			System.exit(1);
		}

		long systime = 0L;
		String line = br.readLine();
		int rows = 0;
		while ((line = br.readLine()) != null && rows < 300000) {
			line = line.replace('"', ' ');
			String[] dataLine = line.split(",");
			long now = Long.parseLong(dataLine[1].trim());

			if (systime == 0)
				systime = now;
			// if (dataLine[1].trim().equals("output")) { // CASE: OUTPUT
			//
			// Integer key1 = new Integer(dataLine[2].trim());
			// // Map<Integer, EnergyPair> m = map.get(key1);
			//
			// if (m == null)
			// continue;
			//
			// Integer key2 = new Integer(dataLine[3].trim());
			// EnergyPair pair = m.get(key2);
			//
			// if (pair == null)
			// continue;
			//
			// if (new Integer(dataLine[4].trim()) <= 0)
			// pair.setEnergy(0);
			// m.put(key2, pair);
			// map.put(key1, m);
			// addLine(writer, now, map);
			// continue;
			// }

			// if (!dataLine[1].trim().equals("energy")) { // CASE: energy
			// continue;
			// }

			// Integer key1 = new Integer(dataLine[0].trim());
			// Map<Integer, EnergyPair> m = map.get(key1);
			//
			// if (m == null)
			// continue;

			while (systime < now && rows < 300000) {
				addLine(writer, systime, map1);
				systime += 1000L;
				rows++;
			}

			Integer key2 = new Integer(dataLine[0].trim());
			EnergyPair pair = map1.get(key2);

			if (pair == null)
				continue;
			// if(key2 == 4 && key1 == 3)
			// System.err.println(dataLine[10].trim());
			pair.setEnergy(Double.parseDouble(dataLine[2].trim()));
			map1.put(key2, pair);
			// map.put(key1, m);
			systime += 1000L;

			addLine(writer, systime, map1);
			rows++;
			// systime = now;
		}
		// Set<Integer> x = map1.keySet();
		// for (Integer key : x) {
		// System.out.println(key + ":");
		// printMap(map1.get(key));
		// System.out.println();
		// }
		writer.close();
		br.close();
	}

	private static void addLine(PrintWriter writer, long systime,
			Map<Integer, EnergyPair> map) {
		Set<Integer> keys1 = map.keySet();
		int size = keys1.size();
		// for (Integer key : keys1) {
		// size += map.get(key).size();
		// }

		double[] consumptions = new double[size];

		// for (Integer key : keys1) {
		// Map<Integer, EnergyPair> m = map.get(key);
		Collection<EnergyPair> pairs = map.values();
		for (EnergyPair ep : pairs) {
			consumptions[ep.getIndex()] = ep.getEnergy();
		}
		// }
		String toPrint = "";
		toPrint += systime + ",";
		for (int i = 0; i < consumptions.length; i++) {
			toPrint += consumptions[i] + ",";
		}
		toPrint += RandomGenerator.getRenewablePredicted() + ","
				+ RandomGenerator.getRenewableProvided();
		writer.println(toPrint);
	}

	private static void printMap(Map<Integer, EnergyPair> x) {
		Set<Integer> keyset = x.keySet();
		for (Integer key : keyset) {
			System.out.println(key + " " + x.get(key).getEnergy() + " "
					+ x.get(key).getIndex());
		}
	}
}
