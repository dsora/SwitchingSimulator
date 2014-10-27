package gui;

import javax.swing.JButton;

public class ElectricLoadButton extends JButton {

	/**
	 * Associates a load to a button
	 */
	private static final long serialVersionUID = -8027159788201037140L;
	
	private double load;
	
	public ElectricLoadButton(String text, double load){
		super(text);
		this.load = load;
	}
	
	public double getLoad(){
		return load;
	}

}
