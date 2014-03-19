package utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InformationSet {
	
	private Map<Date,Double> samples;
	private double mean;
	private double variance;
	private double m2; //variable for the online computation of the variance; it cannot be gotten or set
	public InformationSet(){
		samples = new HashMap<Date, Double>();
		mean = 0;
		variance = 0;
		m2 = 0;
	}
	
	public void addSample(Date key, double value){
		samples.put(key,value);
		if(samples.size() < 2){
			mean = value;
			variance = 0;
		}
		updateMeanAndVariance(value);
	}
	

	private void updateMeanAndVariance(double value) {
		/*
		 * for the description of the following algorithms refer to 
		 * http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance
		 */
		
		double delta = mean-value;
		mean = mean + delta/samples.size(); //update mean
		m2 = m2+(value - mean)*delta;//update m2
		variance = m2/(samples.size() - 1); //update variance
		
	}

	public Map<Date, Double> getSamples() {
		return samples;
	}

	public void setSamples(Map<Date, Double> samples) {
		this.samples = samples;
	}

	public double getMean() {
		return mean;
	}

	public void setMean(double mean) {
		this.mean = mean;
	}

	public double getVariance() {
		return variance;
	}

	public void setVariance(double variance) {
		this.variance = variance;
	}

	@Override
	public String toString() {
		return "InformationManagement [samples=" + samples + ", mean=" + mean
				+ ", variance=" + variance + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(mean);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((samples == null) ? 0 : samples.hashCode());
		temp = Double.doubleToLongBits(variance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InformationSet other = (InformationSet) obj;
		if (Double.doubleToLongBits(mean) != Double
				.doubleToLongBits(other.mean))
			return false;
		if (samples == null) {
			if (other.samples != null)
				return false;
		} else if (!samples.equals(other.samples))
			return false;
		if (Double.doubleToLongBits(variance) != Double
				.doubleToLongBits(other.variance))
			return false;
		return true;
	}
}
