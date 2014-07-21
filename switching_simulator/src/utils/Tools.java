package utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

public class Tools {
	private static double PERC_STEP = 0.6;
	private Tools(){
		//Not-instantiable class: only static methods
	}
	
	public static InformationSet loadFile(String folder, String name) throws FileNotFoundException{
		
		int id = Integer.parseInt(""+(folder.charAt(4)));
		InformationSet result = new InformationSet(id);
		File file = new File("Data"+File.separator+folder+File.separator+name);
		if(!file.exists()){
			throw new FileNotFoundException("Missing specified input file");
		}
		FileInputStream stream = new FileInputStream(file);
		DataInputStream inputStream = new DataInputStream(stream);
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		String line = null;
		try {
			while((line = br.readLine())!=null){
				String[] couple = line.split(" ");
				result.addSample(new Date(Long.valueOf(couple[1])), Double.valueOf(couple[0]));
				//System.out.println(Long.valueOf(couple[1])+ " " + Double.valueOf(couple[0]));
			}
			br.close();
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	public static boolean[] computeSwitch(double renewable, double[] consume) {
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
	
	public static boolean[] computeAdaptativeSwitch(double ren, double[] consume,
			double[] variances) {
		
		//SIDE EFFECT ON CONSUME!!!
		int size = consume.length;
		
		double[] percents = new double[100];
		double perc = 0;
		for (int i = 0; i < percents.length; i++) {
			perc += PERC_STEP;
			percents[i] = perc / 100.0;
		}
		int div = 0;
		double tot = 0;
		double max = 0;
		boolean[] filter = new boolean[size];
		for (int i = 0; i < variances.length; i++) {
			if (variances[i] / 5000 > 1) {
				consume[i] = 0;
				filter[i] = false;
			} else {
				if(variances[i] > max){
					max = variances[i];
				}
				tot += (variances[i]);
				div++;
				filter[i] = true;
			}
		}

		double renewable = percents[99 - ((int) (tot / (div * 50)))] * ren;
//		double renewable = percents[99 - ((int) ( max / 2))] * ren;
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
		for (int i = 0; i < ret.length; i++) {
			ret[i] = filter[i] && ret[i];
		}
		return ret;
	}
	
	public static boolean[] computeAdaptativeSwitch(double ren, double[] consume,
			double[] variances,double[] means) {
		
		//SIDE EFFECT ON CONSUME!!!
		int size = consume.length;
		
		double[] percents = new double[100];
		double perc = 0;
		for (int i = 0; i < percents.length; i++) {
			perc += PERC_STEP;
			percents[i] = perc / 100.0;
		}
		int div = 0;
		double tot = 0;
//		double max = 0;
		boolean[] filter = new boolean[size];
		for (int i = 0; i < variances.length; i++) {
			if (variances[i] / means[i] > 500) {
				consume[i] = 0;
				filter[i] = false;
			} else {
//				if(variances[i] > max){
//					max = variances[i];
//				}
				tot += (variances[i]/means[i]);
				div++;
				filter[i] = true;
			}
		}
		
		double renewable = percents[99 -(int)(((tot/div)/50)*9) ] * ren;
//		double renewable = percents[99 - ((int) (tot / (div * 1.5)))] * ren;
//		double renewable = percents[99 - ((int) ( max / 2))] * ren;
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
		for (int i = 0; i < ret.length; i++) {
			ret[i] = filter[i] && ret[i];
		}
		return ret;
	}

	public static boolean[] computeSwitch(double renewable, double[] consume,
			double[] variances) {
		
		//SIDE EFFECT ON CONSUME!!!
		int size = consume.length;
		
		boolean[] filter = new boolean[size];
		for (int i = 0; i < variances.length; i++) {
			if (variances[i] / 5000 > 1) {
				consume[i] = 0;
				filter[i] = false;
			} else {
				filter[i] = true;
			}
		}
		
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
		for (int i = 0; i < ret.length; i++) {
			ret[i] = filter[i] && ret[i];
		}
		return ret;
	}

	public static boolean[] computeSwitch(double renewable, double[] consume,
			double[] variances, double[] means) {
		
		//SIDE EFFECT ON CONSUME!!!
		int size = consume.length;
		boolean[] filter = new boolean[size];
		for (int i = 0; i < variances.length; i++) {
			if (variances[i] / consume[i] > 500) {
				consume[i] = 0;
				filter[i] = false;
			} else {
				filter[i] = true;
			}
		}
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
		for (int i = 0; i < ret.length; i++) {
			ret[i] = filter[i] && ret[i];
		}
		return ret;
	}
	
}
