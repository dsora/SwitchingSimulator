package utils;

import java.util.Date;

public class InformationSet {

//	private Map<Date, Double> middle_night;
//	private Map<Date, Double> middle_morning;
//	private Map<Date, Double> middle_afternoon;
//	private Map<Date, Double> middle_evening;
//	private Map<Date, Double> extreme_night;
//	private Map<Date, Double> extreme_morning;
//	private Map<Date, Double> extreme_afternoon;
//	private Map<Date, Double> extreme_evening;
	
	//number of elements in the maps
	private long mn_size;
	private long mm_size;
	private long ma_size;
	private long me_size;
	private long en_size;
	private long em_size;
	private long ee_size;
	private long ea_size;
	
	
	private double mean_mn;
	private double mean_mm;
	private double mean_ma;
	private double mean_me;
	private double mean_en;
	private double mean_em;
	private double mean_ea;
	private double mean_ee;

	private double variance_mn;
	private double variance_mm;
	private double variance_ma;
	private double variance_me;
	private double variance_en;
	private double variance_em;
	private double variance_ea;
	private double variance_ee;

	//m2 variables for the online computation of the variances; they cannot be gotten or set
	private double m2_mn; 
	private double m2_mm;
	private double m2_ma;
	private double m2_me;
	private double m2_en;
	private double m2_em;
	private double m2_ea;
	private double m2_ee;

	public InformationSet() {

//		this.middle_night = new HashMap<Date, Double>();
//		this.middle_morning = new HashMap<Date, Double>();
//		this.middle_afternoon = new HashMap<Date, Double>();
//		this.middle_evening = new HashMap<Date, Double>();
//		this.extreme_night = new HashMap<Date, Double>();
//		this.extreme_morning = new HashMap<Date, Double>();
//		this.extreme_afternoon = new HashMap<Date, Double>();
//		this.extreme_evening = new HashMap<Date, Double>();
		this.mean_mn = 0.0;
		this.mean_mm = 0.0;
		this.mean_ma = 0.0;
		this.mean_me = 0.0;
		this.mean_en = 0.0;
		this.mean_em = 0.0;
		this.mean_ea = 0.0;
		this.mean_ee = 0.0;
		this.variance_mn = 0.0;
		this.variance_mm = 0.0;
		this.variance_ma = 0.0;
		this.variance_me = 0.0;
		this.variance_en = 0.0;
		this.variance_em = 0.0;
		this.variance_ea = 0.0;
		this.variance_ee = 0.0;
		this.m2_mn = 0.0;
		this.m2_mm = 0.0;
		this.m2_ma = 0.0;
		this.m2_me = 0.0;
		this.m2_en = 0.0;
		this.m2_em = 0.0;
		this.m2_ea = 0.0;
		this.m2_ee = 0.0;
		this.mn_size = 0;
		this.mm_size = 0;
		this.ma_size = 0;
		this.me_size = 0;
		this.en_size = 0;
		this.em_size = 0;
		this.ee_size = 0;
		this.ea_size = 0;
	}


	public double getMean_mn() {
		return mean_mn;
	}

	public void setMean_mn(double mean_mn) {
		this.mean_mn = mean_mn;
	}

	public double getMean_mm() {
		return mean_mm;
	}

	public void setMean_mm(double mean_mm) {
		this.mean_mm = mean_mm;
	}

	public double getMean_ma() {
		return mean_ma;
	}

	public void setMean_ma(double mean_ma) {
		this.mean_ma = mean_ma;
	}

	public double getMean_me() {
		return mean_me;
	}

	public void setMean_me(double mean_me) {
		this.mean_me = mean_me;
	}

	public double getMean_en() {
		return mean_en;
	}

	public void setMean_en(double mean_en) {
		this.mean_en = mean_en;
	}

	public double getMean_em() {
		return mean_em;
	}

	public void setMean_em(double mean_em) {
		this.mean_em = mean_em;
	}

	public double getMean_ea() {
		return mean_ea;
	}

	public void setMean_ea(double mean_ea) {
		this.mean_ea = mean_ea;
	}

	public double getMean_ee() {
		return mean_ee;
	}

	public void setMean_ee(double mean_ee) {
		this.mean_ee = mean_ee;
	}

	public double getVariance_mn() {
		return variance_mn;
	}

	public void setVariance_mn(double variance_mn) {
		this.variance_mn = variance_mn;
	}

	public double getVariance_mm() {
		return variance_mm;
	}

	public void setVariance_mm(double variance_mm) {
		this.variance_mm = variance_mm;
	}

	public double getVariance_ma() {
		return variance_ma;
	}

	public void setVariance_ma(double variance_ma) {
		this.variance_ma = variance_ma;
	}

	public double getVariance_me() {
		return variance_me;
	}

	public void setVariance_me(double variance_me) {
		this.variance_me = variance_me;
	}

	public double getVariance_en() {
		return variance_en;
	}

	public void setVariance_en(double variance_en) {
		this.variance_en = variance_en;
	}

	public double getVariance_em() {
		return variance_em;
	}

	public void setVariance_em(double variance_em) {
		this.variance_em = variance_em;
	}

	public double getVariance_ea() {
		return variance_ea;
	}

	public void setVariance_ea(double variance_ea) {
		this.variance_ea = variance_ea;
	}

	public double getVariance_ee() {
		return variance_ee;
	}

	public void setVariance_ee(double variance_ee) {
		this.variance_ee = variance_ee;
	}
	

	public void addSample(Date key, double value) {
		/*
		 * This method populates the correct Map and manages his variance, mean and m2 values
		 */
		String[] fields = key.toString().split(" ");
		String[] hhmmss = fields[3].split(":");

		String temp = fields[1].toLowerCase();
		if (temp.equals("mar") || temp.equals("apr") || temp.equals("may")
				|| temp.equals("sep") || temp.equals("oct")
				|| temp.equals("nov")) {
			// middle case
			int h = Integer.parseInt(hhmmss[0]);
			if (h >= 0 && h < 8) {
				// CASE: night
				//middle_night.put(key, value);
				mn_size++;
				double delta = value - mean_mn;
				//double n = (double) (middle_night.size());
				mean_mn += (delta / mn_size); // update mean
				m2_mn += (value - mean_mn) * delta;// update m2
				if(mn_size>1)
					variance_mn = m2_mn / (mn_size - 1.0); // update variance
			} else if (h >= 8 && h < 14) {
				// CASE: morning
				//middle_morning.put(key, value);
				double delta = value - mean_mm;
				//double n = (double) (middle_morning.size());
				mm_size++;
				mean_mm += (delta / mm_size); // update mean
				m2_mm += (value - mean_mm) * delta;// update m2
				if(mm_size>1)
					variance_mm = m2_mm / (mm_size - 1.0); // update variance
			} else if (h >= 14 && h < 19) {
				// CASE: afternoon
				//middle_afternoon.put(key, value);
				double delta = value - mean_ma;
				ma_size++;
				//double n = (double) (middle_afternoon.size());
				mean_ma += (delta / ma_size); // update mean
				m2_ma += (value - mean_ma) * delta;// update m2
				if(ma_size>1)
					variance_ma = m2_ma / (ma_size - 1.0); // update variance
			} else if (h >= 19 && h < 24) {
				// CASE: evening
				//middle_evening.put(key, value);
				double delta = value - mean_me;
				//double n = (double) (middle_evening.size());
				me_size++;
				mean_me += (delta / me_size); // update mean
				m2_me += (value - mean_me) * delta;// update m2
				if(me_size>1)
					variance_me = m2_me / (me_size - 1.0); // update variance
			}

		} else {
			// extreme case
			int h = Integer.parseInt(hhmmss[0]);
			if (h >= 0 && h < 8) {
				// CASE: night
				//extreme_night.put(key, value);
				double delta = value - mean_en;
				//double n = (double) (extreme_night.size());
				en_size++;
				mean_en += (delta / en_size); // update mean
				m2_en += (value - mean_en) * delta;// update m2
				if(en_size>1)
					variance_en = m2_en / (en_size - 1.0); // update variance
			} else if (h >= 8 && h < 14) {
				// CASE: morning
				//extreme_morning.put(key, value);
				double delta = value - mean_em;
				//double n = (double) (extreme_morning.size());
				em_size++;
				mean_em += (delta / em_size); // update mean
				m2_em += (value - mean_em) * delta;// update m2
				if(em_size>1)
					variance_em = m2_em / (em_size - 1.0); // update variance
			} else if (h >= 14 && h < 19) {
				// CASE: afternoon
				//extreme_afternoon.put(key, value);
				double delta = value - mean_ea;
				ea_size++;
				//double n = (double) (extreme_afternoon.size());
				mean_ea += (delta / ea_size); // update mean
				m2_ea += (value - mean_ea) * delta;// update m2
				if(ea_size>1)
					variance_ea = m2_ea / (ea_size - 1.0); // update variance
			} else if (h >= 19 && h < 24) {
				// CASE: evening
				//extreme_evening.put(key, value);
				double delta = value - mean_ee;
				//double n = (double) (extreme_evening.size());
				ee_size++;
				mean_ee += (delta / ee_size); // update mean
				m2_ee += (value - mean_ee) * delta;// update m2
				if(ee_size>1)
					variance_ee = m2_ee / (ee_size - 1.0); // update variance
			}
		}
	}
}