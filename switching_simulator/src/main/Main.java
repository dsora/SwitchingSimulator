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
		
		@SuppressWarnings("unused")
		MainWindows w = new MainWindows("SwitchingSimulator0.1");
		InformationSet x = null;
		try {
			x = Tools.loadFile("Line1", "predicted_consumption");
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			System.err.println("Missing file");
		}
		if(x != null){
			
			System.out.println(x.getMean());
			System.out.println(x.getVariance());
			System.out.println(x.getSamples().size());
		}
	}

}
