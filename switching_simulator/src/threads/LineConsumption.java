package threads;

import generator.RandomGenerator;
import gui.LinePanel;

import java.util.Date;

import utils.InformationSet;

public class LineConsumption implements Runnable {
	private final long SLEEP_TIME = 10000;
	private volatile InformationSet info;
	private volatile boolean running = false;
	private volatile double consumption = 0;
	private volatile boolean source = false;// true--> renewable, false--> wires
	private volatile int switch_count = 0;
	private LinePanel panel;
	private volatile double additiveLoad;
	//private volatile int dummy = 0;

	public LineConsumption(InformationSet info, LinePanel panel) {
		this.info = info;
		this.panel = panel;
		this.additiveLoad = 0.0;
	}

	@Override
	public void run() {

		running = true;
		//dummy = 0;
		while (running) {
			//if (dummy < 1) {
				Date d = new Date();
				double sample = RandomGenerator.getOnlineLineConsumption(d, info.getId()-1);
				consumption = sample;
				sample += additiveLoad;
				info.addSample(d, sample);
				String st = panel.getPowerArea().getText();

				if (st.split("\n").length < LinePanel.DISPLAYED_VALUES) {
					st += ("\n" + sample);
				} else {
					st = Double.toString(sample);
				}
				// source = evaulateSwitching();
				panel.getPowerArea().setText(st);
				//dummy = Integer.MAX_VALUE/8;
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {

					// e.printStackTrace();
					running = false;
				}
//			}else{
//				dummy--;
//			}
		}
	}

	public void setSource(boolean source) {
		this.source = source;
	}

	// private boolean evaulateSwitching() {
	//
	// return false;
	// }

	public void stopThread() {
		running = false;
		consumption = 0;		
	}

	public double getConsumption() {
		if(running){
			return consumption + additiveLoad;
		}else{
			return 0;
		}
	}

	public boolean getSource() {
		return source;
	}

	public InformationSet getInformationSet() {
		return info;
	}

	public void addConsumptionElement(double load) {
		additiveLoad += load;
	}

	public void removeConsumptionElement(double load) {
		if (additiveLoad > load) {
			additiveLoad -= load;
		} else {
			additiveLoad = 0;
		}
	}

	// public void waitScheduler(Thread master) {
	// System.out.println("Hello");
	// try {
	// master.join();
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	public void source(boolean source) {
		if (this.source == source)
			return;
		else {
			this.source = source;
			switch_count++;
			info.incTotalSwitches();
		}
	}
	
	public void incWrong(){
		info.incWrongSwitches();
	}

	public int getSwitch_count() {
		return switch_count;
	}

	public void setSwitch_count(int switch_count) {
		this.switch_count = switch_count;
	}
	
	public void saving(double save){
		info.incSave(save);
	}
}
