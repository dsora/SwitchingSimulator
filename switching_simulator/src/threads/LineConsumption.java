package threads;

import generator.RandomGenerator;

import java.util.Date;

import utils.InformationSet;

public class LineConsumption implements Runnable {
	private final long SLEEP_TIME = 500;
	private InformationSet info;
    private volatile boolean running = false;
    private volatile double consumption = 0;
    
	public LineConsumption(InformationSet info){
		this.info=info;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		running = true;
		
		while(running){
			Date d = new Date();
			double sample = RandomGenerator.getNextConsumptionValue(d);
			info.addSample(d, sample);
			consumption = sample;
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				
				//e.printStackTrace();
				running = false;
			}
		}
	}
	
	public void stopThread(){
		running=false;
	}
	
	public double getConsumption(){
		return consumption;
	}

}
