package threads;

import generator.RandomGenerator;
import gui.LinePanel;

import java.util.List;

import javax.swing.JSlider;

public class MasterSwitch implements Runnable {

	private static final long SLEEP_TIME = 1000;
	private List<LinePanel> inputList;
//	private List<Double> inputList;

	// private boolean[] result;
	private JSlider energyIndicator;
	
	public MasterSwitch(JSlider slider){
		energyIndicator = slider;
	}
	
	@Override
	public void run() {
		while (true) {
			if (inputList == null) {
				return;
			}
			long start = System.currentTimeMillis();
			boolean[] ideal = computeSwitch(true); //considers no delay
			boolean[] real = computeSwitch(false); //considers the delay
			long end = System.currentTimeMillis();
			if ((end - start) > 0) {
				System.out.println("Time for operating switching: "
						+ (System.currentTimeMillis() - start));
			}
			for (int i = 0; i < real.length; i++) {
				LinePanel p = inputList.get(i);
				if(real[i]){
					p.source(true);
					p.getLineConsumption().saving(p.getLineConsumption().getConsumption());
				}else{
					p.source(false);
				}
				if(!ideal[i] && real[i]){
					p.getLineConsumption().incWrong();
				}
			}
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// i = 0;
		// for (boolean bs : result) {
		// System.out.println(i + " " + bs);
		// i++;
		// }
	}

	private boolean[] computeSwitch(boolean ideal) {
		int size = inputList.size();
		boolean[] ret = new boolean[size];
		LineConsumption[] in = new LineConsumption[size];
		double[] consume = new double[size];
		double renewable;
		if(ideal)
//			renewable = RandomGenerator.getRenewableProvided();
			renewable = (double)energyIndicator.getValue();
		else 
//			renewable = RandomGenerator.getRenewablePredicted();
			renewable = (double)energyIndicator.getValue();
		int upper = (int) renewable;
		int i = 0;
		for (LinePanel line : inputList) {
			in[i] = line.getLineConsumption();
			// in[i].waitScheduler(Thread.currentThread());
			consume[i] = in[i].getConsumption();
			i++;
			// System.err.println(i);
		}
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
				if (augmentedConsumption <= w)
					option2 = consume[n - 1]
							+ opt[n - 1][(w - augmentedConsumption)];

				// which better?
				opt[n][w] = Math.max(option1, option2);
				sol[n][w] = (option2 >= option1);
			}
		}
		// printMatrix(sol);

		//long step;
		for (int n = size, w = upper; n > 0; n--) {
			if (sol[n][w]) {
				ret[n-1] = true;
				w = w - (int) (Math.ceil(consume[n - 1]));

			} else {
				ret[n-1] = false;
			}
			//step = System.currentTimeMillis();
		}
		
		return ret;
	}

	// private void printMatrix(double[][] in) {
	//
	// for(int i = 0; i < in.length; i++){
	// for(int j = 0; j < in[i].length; j++){
	// System.out.print(in[i][j] + " ");
	// }
	// System.out.println();
	// }
	// }
	//
	// private void printMatrix(boolean[][] in){
	// for(int i = 0; i < in.length; i++){
	// for(int j = 0; j < in[i].length; j++){
	// System.out.print(in[i][j] + " ");
	// }
	// System.out.println();
	// }
	// }
	public void setInput(List<LinePanel> list) {
		inputList = list;
	}
}
