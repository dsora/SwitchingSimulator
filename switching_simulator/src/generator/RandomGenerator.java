package generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashSet;

import org.apache.commons.math3.distribution.NormalDistribution;

public class RandomGenerator {

	private static final int RENEWABLE_PREDICTION_MEAN = 200; // mean and
	private static final double RENEWABLE_PREDICTION_SD = 20; // standard deviation 
																// of the provided
																// renewable
																// energy

	private static final int SAMPLES = 17500; // number of samples.
												// 1sample/30min = 17520/year
	private static final int PREDICTIONS = 3600; // number of predictions.
													// 1prediction/30min =
													// 17520/year
	private static final double MEAN = 150;
	private static final double VARIANCE = 2.0;
	private static final long HALFHOUR = 30 * 60 * 1000;
	private static final long SECOND = 1000;	
	private static NormalDistribution renewableDistribution = null;
	
	private static final double weight_1 = 0.5;
	private static final double weight_2 = 0.1;
	private static final double weight_3 = 0.2;
	private static final double weight_4 = 0.2;
	
	private RandomGenerator() {

	}

	public static double getNextConsumptionValue(Date d) {
		HashSet<String> spring = new HashSet<String>();
		spring.add("mar");
		spring.add("apr");
		spring.add("may");

		HashSet<String> fall = new HashSet<String>();
		spring.add("sep");
		spring.add("oct");
		spring.add("nov");
		/*
		 * HashSet<String> winter = new HashSet<String>(); spring.add("dec");
		 * spring.add("gen"); spring.add("feb");
		 * 
		 * HashSet<String> summer = new HashSet<String>(); spring.add("jun");
		 * spring.add("jul"); spring.add("aug");
		 */

		String[] date = d.toString().split(" ");
		String[] hhmmss = date[3].split(":");

		/*
		 * In the following an estimation of the mean value will be computed for
		 * characterizing the consumption: fall and spring cause a -1 while
		 * winter and summer cause +1 (consumption for heating and cooling).
		 * Moreover morning and evening give a +0.5, meanwhile afternoon give a
		 * -0.5 and at night -1.5.
		 * 
		 * For the variance, the hypothesis are the following: -spring and fall
		 * -0.5 -summer and winter +0.5 -night -0.5 -morning, afternoon, evening
		 * +0.5
		 */

		double mean = MEAN;
		double variance = VARIANCE;
		if (spring.contains(date[1].toLowerCase())
				|| fall.contains(date[1].toLowerCase())) {
			mean--;
			variance = variance - 0.5;
		} else {
			mean++;
			variance++;
		}
		int h = Integer.parseInt(hhmmss[0]);
		if (h >= 0 && h < 8) {
			// CASE: night
			mean -= 2.5;
			variance -= 1.5;
		} else if (h >= 8 && h < 14) {
			// CASE: morning
			mean += 0.5;
			variance += 0.5;
		} else if (h >= 14 && h < 19) {
			// CASE: afternoon
			mean -= 0.5;
			variance += 0.5;
		} else if (h >= 19 && h < 24) {
			// CASE: evening
			mean += 0.5;
			variance += 0.5;
		}

		if (variance <= 0) // constraint: standard deviation > 0
			variance = 0.5;

		// System.out.println(mean + " "+variance+" "+d);
		return new NormalDistribution(mean, Math.sqrt(variance)).sample();
		// return 9;
	}

	public static boolean generateNormalTraining(String folderLine) {
		/*
		 * This method generates the training file for the simulation. The
		 * frequency is a sample each 30 minutes.
		 */
		File file = new File("Data" + File.separator + folderLine
				+ File.separator + "effective_consumption");
		if (!file.exists()) {
			System.err.println("FILE " + file.toString() + " NOT FOUND");
			return false;
		}
		Long now_msec = new Date().getTime();
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			System.err.println("FILE " + file.toString() + " NOT FOUND");
			e.printStackTrace();
			return false;
		}
		for (int i = 0; i < SAMPLES; i++) {
			// Date d = new Date(now_msec);
			double sample = getNextConsumptionValue(new Date(now_msec));
			if (writer != null) {
				writer.println(sample + " " + now_msec);// +" "+d.toString());
			} else {
				System.err.println("NULL WRITER");
				return false;
			}
			now_msec -= HALFHOUR;
		}
		writer.close();
		return true;
	}

	public static boolean generateNormalPrediction(String folderLine) {
		/*
		 * This method generates the training file for the simulation. The
		 * frequency is a sample each 30 minutes.
		 */
		File file = new File("Data" + File.separator + folderLine
				+ File.separator + "predicted_consumption");
		if (!file.exists()) {
			System.err.println("FILE " + file.toString() + " NOT FOUND");
			return false;
		}
		Long now_msec = System.currentTimeMillis();
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			System.err.println("FILE " + file.toString() + " NOT FOUND");
			e.printStackTrace();
			return false;
		}
		for (int i = 0; i < PREDICTIONS; i++) {
			// Date d = new Date(now_msec);
			double[] sample = getLinesConsumption(new Date(now_msec),0);
			if (writer != null) {
				writer.println(now_msec+" "+sample[0] + " "+ sample[1] + " " + sample[2] + " "+ sample[3]);
			} else {
				System.err.println("NULL WRITER");
				return false;
			}
			now_msec += SECOND;
		}
		writer.close();
		return true;
	}

	public static double getRenewableProvided() {

		if (renewableDistribution == null) {
			renewableDistribution = new NormalDistribution(
					RENEWABLE_PREDICTION_MEAN, RENEWABLE_PREDICTION_SD);
		}
		return renewableDistribution.sample();
	}

	public static double getRenewablePredicted() {

		return 200;
	}
	
	public static double[] getLinesConsumption(Date d, double stdDev){
		
		String[] date = d.toString().split(" ");
		String[] hhmmss = date[3].split(":");
		
		double mean;
		//double variance = 0;
		int h = Integer.parseInt(hhmmss[0]);
		if (h >= 0 && h < 8) {
			// CASE: night
			stdDev += 0.5;
		} else if (h >= 8 && h < 14) {
			// CASE: morning
			stdDev += 1;
		} else if (h >= 14 && h < 19) {
			// CASE: afternoon
			stdDev += 0.7;
		} else if (h >= 19 && h < 24) {
			// CASE: evening
			stdDev += 1;
		}
		
		mean = new NormalDistribution(MEAN,stdDev).sample();
		
		double[] ret = new double[4];
		ret[0] = weight_1*mean;
		ret[1] = weight_2*mean;
		ret[2] = weight_3*mean;
		ret[3] = weight_4*mean;
		return ret;
	}
	
	public static double getOnlineLineConsumption(Date d, int id){
		if(id < 0 || id > 3)
			return -1;
		double[] ret = getLinesConsumption(d,0);
		return ret[id];
	}
}
