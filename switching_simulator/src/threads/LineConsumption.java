package threads;

import generator.RandomGenerator;
import gui.LinePanel;

import java.util.Date;

import utils.InformationSet;

public class LineConsumption implements Runnable {
	private final long SLEEP_TIME = 500;
	private InformationSet info;
	private volatile boolean running = false;
	private volatile double consumption = 0;
	private LinePanel panel;
	private volatile double additiveLoad;

	public LineConsumption(InformationSet info, LinePanel panel) {
		this.info = info;
		this.panel = panel;
		this.additiveLoad = 0.0;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		running = true;

		while (running) {
			Date d = new Date();
			double sample = RandomGenerator.getNextConsumptionValue(d);
			sample += additiveLoad;
			info.addSample(d, sample);
			consumption = sample;
			String st = panel.getPowerArea().getText();

			if (st.split("\n").length < LinePanel.DISPLAYED_VALUES) {
				st += ("\n" + consumption);
			} else {
				st = Double.toString(consumption);
			}

			panel.getPowerArea().setText(st);
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {

				// e.printStackTrace();
				running = false;
			}
		}
	}

	public void stopThread() {
		running = false;
	}

	public double getConsumption() {
		return consumption;
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
}