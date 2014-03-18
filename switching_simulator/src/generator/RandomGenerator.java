package generator;

import java.io.File;
import java.util.Date;
import java.util.HashSet;

import org.apache.commons.math3.distribution.NormalDistribution;

public class RandomGenerator {
	private static final int SAMPLES=17500; //number of samples. 1sample/30min = 17520/year
	private static final double MEAN = 10.0;//average kWh/day ---> 3650 kWh/year
	private static final double VARIANCE = 2.0;
	private static final long HALFHOUR = 30*60*1000;
	
	private RandomGenerator() {

	}
	
	private static double getNextValue(Date d){
		HashSet<String> spring = new HashSet<String>();
		spring.add("mar");
		spring.add("apr");
		spring.add("may");
		
		HashSet<String> summer = new HashSet<String>();
		spring.add("jun");
		spring.add("jul");
		spring.add("aug");
		
		HashSet<String> fall = new HashSet<String>();
		spring.add("sep");
		spring.add("oct");
		spring.add("nov");
		
		HashSet<String> winter = new HashSet<String>();
		spring.add("dec");
		spring.add("gen");
		spring.add("feb");
		
		String[] date = d.toString().split(" ");
		String[] hhmmss = date[3].split(":");
		
		/*
		 * In the following an estimation of the mean value will be computed for characterizing 
		 * the consumption: fall and spring cause a -1 while winter and summer cause +1 (consumption for
		 * heating and cooling).
		 * Moreover morning and evening give a +0.5, meanwhile afternoon and night give a -0.5.
		 * 
		 * For the variance, the hypothesis are the following:
		 * 	-spring and fall -0.5
		 *  -summer and winter +0.5
		 *  -night -0.5
		 *  -morning, afternoon, evening +0.5
		 */
		
		double mean = MEAN; 
		double variance = VARIANCE;
		if(spring.contains(date[1].toLowerCase())||fall.contains(date[1].toLowerCase())){
			mean --;
			variance = variance - 0.5;
		}else{
			mean++;
			variance ++;
		}
		int h = Integer.parseInt(hhmmss[0]);
		if(h >=0 && h < 8){
			//CASE: night
			mean -= 0.5;
			variance -= 0.5;
		}else if(h>=8 && h<14){
			//CASE: morning
			mean += 0.5;
			variance += 0.5;
		}else if(h>= 14 && h< 19){
			//CASE: afternoon
			mean -= 0.5;
			variance += 0.5;
		}else if(h>= 19 && h<24){
			//CASE: evening
			mean += 0.5;
			variance += 0.5;
		}
		
		return new NormalDistribution(mean,variance).sample();
	}

	public static boolean generateNormalTraining(String folderLine) {
		/*
		 * This method generates the training file for the simulation.
		 * The frequency is a sample each 30 minutes.  
		 */
		File file = new File("Data"+File.separator+folderLine+File.separator+"predicted_consumption");
		Long now_msec = new Date().getTime();
		
		for(int i = 0; i < SAMPLES; i++){
			double sample = getNextValue(new Date(now_msec));
			
		}
		
		return true;
	}
}
