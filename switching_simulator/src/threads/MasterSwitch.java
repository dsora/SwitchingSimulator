package threads;

import generator.RandomGenerator;
import gui.LinePanel;

import java.util.List;

public class MasterSwitch implements Runnable {

	private List<LinePanel> inputList;
	private boolean[] result;

	@Override
	public void run() {

		if (inputList == null || result == null) {
			return;
		}
		int size = inputList.size();
		LineConsumption[] in = new LineConsumption[size];
		double[] consume = new double[size];
		double renewable = RandomGenerator.getRenewableProvided();
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
		boolean[][] sol = new boolean[size + 1][upper + 1];

		for (int n = 1; n <= size; n++) {
			for (int w = 1; w <= upper; w++) {

				double option1 = opt[n - 1][w];

				
				double option2 = Double.MIN_VALUE;
				int augmentedConsumption = (int) (Math.ceil(consume[n - 1]));
				if (augmentedConsumption <= w)
					option2 = consume[n - 1]
							+ opt[n - 1][(w - augmentedConsumption)];

				// select better of two options
				opt[n][w] = Math.max(option1, option2);
				sol[n][w] = (option2 > option1);
			}
		}

		for (int n = size, w = upper; n > 0; n--) {
			if (sol[n][w]) {
				inputList.get(n - 1).source(true);
				w = w - (int) (Math.ceil(consume[n - 1]));
			} else {
				inputList.get(n - 1).source(false);
			}
		}
	}

	public void setInput(List<LinePanel> list) {
		inputList = list;
		result = new boolean[inputList.size()];
	}

}
