package utils;

import java.util.Date;

public class InformationSet {
	
	private static final int LENGTH = 24;
//	private Map<Date, Double> middle_night;
//	private Map<Date, Double> middle_morning;
//	private Map<Date, Double> middle_afternoon;
//	private Map<Date, Double> middle_evening;
//	private Map<Date, Double> extreme_night;
//	private Map<Date, Double> extreme_morning;
//	private Map<Date, Double> extreme_afternoon;
//	private Map<Date, Double> extreme_evening;
	
	//number of elements in the maps
	
	private int id;
	
	private int totalSwitches;
	
	private int wrongSwitches;
	
	private double save;
	
	private long[] sizes;
//
//	private long mn_size;
//	private long mm_size;
//	private long ma_size;
//	private long me_size;
//	private long en_size;
//	private long em_size;
//	private long ee_size;
//	private long ea_size;
	
	private double[] means;
	
//	private double mean_mn;
//	private double mean_mm;
//	private double mean_ma;
//	private double mean_me;
//	private double mean_en;
//	private double mean_em;
//	private double mean_ea;
//	private double mean_ee;

	private double[] variances;
	
//	private double variance_mn;
//	private double variance_mm;
//	private double variance_ma;
//	private double variance_me;
//	private double variance_en;
//	private double variance_em;
//	private double variance_ea;
//	private double variance_ee;

	private double m2_s[];
	//m2 variables for the online computation of the variances; they cannot be gotten or set
//	private double m2_mn; 
//	private double m2_mm;
//	private double m2_ma;
//	private double m2_me;
//	private double m2_en;
//	private double m2_em;
//	private double m2_ea;
//	private double m2_ee;

	public InformationSet(int id) {

//		this.middle_night = new HashMap<Date, Double>();
//		this.middle_morning = new HashMap<Date, Double>();
//		this.middle_afternoon = new HashMap<Date, Double>();
//		this.middle_evening = new HashMap<Date, Double>();
//		this.extreme_night = new HashMap<Date, Double>();
//		this.extreme_morning = new HashMap<Date, Double>();
//		this.extreme_afternoon = new HashMap<Date, Double>();
//		this.extreme_evening = new HashMap<Date, Double>();
		this.id = id;
		this.m2_s = new double[LENGTH];
		this.means = new double[LENGTH];
		this.sizes = new long[LENGTH];
		this.variances = new double[LENGTH];
		
		for(int i = 0; i < LENGTH; i++){
			m2_s[i] = 0;
			means[i] = 0;
			sizes[i] = 0;
			variances[i] = 0;
		}
		
		this.totalSwitches = 0;
		this.wrongSwitches = 0;
		this.save = 0;
//		this.mean_mn = 0.0;
//		this.mean_mm = 0.0;
//		this.mean_ma = 0.0;
//		this.mean_me = 0.0;
//		this.mean_en = 0.0;
//		this.mean_em = 0.0;
//		this.mean_ea = 0.0;
//		this.mean_ee = 0.0;
//		this.variance_mn = 0.0;
//		this.variance_mm = 0.0;
//		this.variance_ma = 0.0;
//		this.variance_me = 0.0;
//		this.variance_en = 0.0;
//		this.variance_em = 0.0;
//		this.variance_ea = 0.0;
//		this.variance_ee = 0.0;
//		this.m2_mn = 0.0;
//		this.m2_mm = 0.0;
//		this.m2_ma = 0.0;
//		this.m2_me = 0.0;
//		this.m2_en = 0.0;
//		this.m2_em = 0.0;
//		this.m2_ea = 0.0;
//		this.m2_ee = 0.0;
//		this.mn_size = 0;
//		this.mm_size = 0;
//		this.ma_size = 0;
//		this.me_size = 0;
//		this.en_size = 0;
//		this.em_size = 0;
//		this.ee_size = 0;
//		this.ea_size = 0;
	}
	

	public int getId() {
		return id;
	}

	public int getTotalSwitches() {
		return totalSwitches;
	}

	public void incTotalSwitches(){
		totalSwitches++;
	}

	public double getSave() {
		return save;
	}


	public void setSave(double save) {
		this.save = save;
	}

	public void incSave(double plus){
		save += plus;
	}

	public void setTotalSwitches(int totalSwitches) {
		this.totalSwitches = totalSwitches;
	}
	
	public int getWrongSwitches() {
		return wrongSwitches;
	}
	
	public void incWrongSwitches(){
		wrongSwitches++;
	}


	public void setWrongSwitches(int wrongSwitches) {
		this.wrongSwitches = wrongSwitches;
	}


	public long[] getSizes() {
		return sizes;
	}


	public double[] getMeans() {
		return means;
	}


	public double[] getVariances() {
		return variances;
	}


	public void addSample(Date key, double value) {
		/*
		 * This method maintains mean and variance informations
		 */
		String[] fields = key.toString().split(" ");
		String[] hhmmss = fields[3].split(":");

			
		int h = Integer.parseInt(hhmmss[0]);
		sizes[h]++;
		double delta = value - means[h];
		means[h] += (delta / sizes[h]); // update mean
		m2_s[h] += (value - means[h]) * delta;// update m2
		if(sizes[h]>1){
				variances[h] = m2_s[h] / (sizes[h] - 1.0); // update variance
		} 
	}
}