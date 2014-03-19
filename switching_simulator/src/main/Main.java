package main;

import java.io.FileNotFoundException;

import utils.InformationSet;
import utils.Tools;
import generator.RandomGenerator;
import gui.MainWindows;

public class Main {

	public static void main(String[] args) {
		
		//Training of random data for the simulation
		System.out.println(RandomGenerator.generateNormalTraining("Line1"));
		System.out.println(RandomGenerator.generateNormalPrediction("Line1"));
		System.out.println(RandomGenerator.generateNormalTraining("Line2"));
		System.out.println(RandomGenerator.generateNormalPrediction("Line2"));
		System.out.println(RandomGenerator.generateNormalTraining("Line3"));
		System.out.println(RandomGenerator.generateNormalPrediction("Line3"));
		System.out.println(RandomGenerator.generateNormalTraining("Line4"));
		System.out.println(RandomGenerator.generateNormalPrediction("Line4"));
		
		//@SuppressWarnings("unused")
		//MainWindows w = new MainWindows("SwitchingSimulator0.1");
		InformationSet x = null;
		try {
			x = Tools.loadFile("Line1", "effective_consumption");
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			System.err.println("Missing file");
		}
		if(x != null){
			System.out.println("EXTREME AFTERNOON");
			System.out.println(x.getMean_ea());
			System.out.println(x.getVariance_ea());
			System.out.println(x.getExtreme_afternoon().size());
			System.out.println("EXTREME EVENING");
			System.out.println(x.getMean_ee());
			System.out.println(x.getVariance_ee());
			System.out.println(x.getExtreme_evening());
			System.out.println("EXTREME NIGHT");
			System.out.println(x.getMean_en());
			System.out.println(x.getVariance_en());
			System.out.println(x.getExtreme_night().size());
			System.out.println("EXTREME MORNING");
			System.out.println(x.getMean_em());
			System.out.println(x.getVariance_em());
			System.out.println(x.getExtreme_morning().size());
			System.out.println("MIDDLE MORNING");
			System.out.println(x.getMean_mm());
			System.out.println(x.getVariance_mm());
			System.out.println(x.getMiddle_morning().size());
			System.out.println("MIDDLE NIGHT");
			System.out.println(x.getMean_mn());
			System.out.println(x.getVariance_mn());
			System.out.println(x.getMiddle_night().size());
			System.out.println("MIDDLE EVENING");
			System.out.println(x.getMean_me());
			System.out.println(x.getVariance_me());
			System.out.println(x.getMiddle_evening().size());
			System.out.println("MIDDLE AFTERNOON");
			System.out.println(x.getMean_ma());
			System.out.println(x.getVariance_ma());
			System.out.println(x.getMiddle_afternoon().size());
		}
	}

}
