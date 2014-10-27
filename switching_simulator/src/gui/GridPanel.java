package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class GridPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2648153338425725382L;
	private JLabel statusLabel;
	private JLabel sourceLabel;
	private JLabel consumptionLabel;
	private JLabel totalSwitchesLabel;
	private JLabel statusLabelValue;
	private JLabel sourceLabelValue;
	private JLabel consumptionLabelValue;
	private JLabel totalSwitchesLabelValue;
	
	public GridPanel(){
		super(new GridLayout(2,4));
		statusLabel = new JLabel("Status");
		sourceLabel = new JLabel("Source");
		consumptionLabel =  new JLabel("Consumption");
		totalSwitchesLabel = new JLabel("Total Switches");
		statusLabelValue = new JLabel();
		sourceLabelValue = new JLabel();
		consumptionLabelValue = new JLabel();
		totalSwitchesLabelValue = new JLabel();
		
		add(statusLabel);
		add(sourceLabel);
		add(consumptionLabel);
		add(totalSwitchesLabel);
		add(statusLabelValue);
		add(sourceLabelValue);
		add(consumptionLabelValue);
		add(totalSwitchesLabelValue);
		
	}
	
	public GridPanel(String status, String source, String consumption, String totalSwitches){
		super(new GridLayout(2,4));
		statusLabel = new JLabel("Status");
		statusLabel.setFont(Font.decode("Arial-ITALIC-12"));
		sourceLabel = new JLabel("Source");
		sourceLabel.setFont(Font.decode("Arial-ITALIC-12"));
		consumptionLabel =  new JLabel("Consumption");
		consumptionLabel.setFont(Font.decode("Arial-ITALIC-12"));
		totalSwitchesLabel = new JLabel("Total Switches");
		totalSwitchesLabel.setFont(Font.decode("Arial-ITALIC-12"));
		statusLabelValue = new JLabel(status);
		Color color;
		if(status.equals("Running")){
			color = Color.green;
		} else {
			color = Color.red;
		}
		statusLabelValue.setForeground(color);
		
		sourceLabelValue = new JLabel(source);
		Color color2;
		if(source.equals("Renewable")){
			color2 = Color.green;
		} else {
			color2 = Color.red;
		}
		sourceLabelValue.setForeground(color2);
		
		consumptionLabelValue = new JLabel(consumption);
		totalSwitchesLabelValue = new JLabel(totalSwitches);
		
		add(statusLabel);
		add(sourceLabel);
		add(consumptionLabel);
		add(totalSwitchesLabel);
		add(statusLabelValue);
		add(sourceLabelValue);
		add(consumptionLabelValue);
		add(totalSwitchesLabelValue);
		
	}
	
	public void updatePanel(String status, String source, String consumption, String totalSwitches){
		String oldStatus = statusLabelValue.getText();
		String oldSource = sourceLabelValue.getText();
		String oldConsumption = consumptionLabelValue.getText();
		String oldTotalSwitch = totalSwitchesLabelValue.getText();
		
		statusLabel.setFont(Font.decode("Arial-ITALIC-12"));
		sourceLabel.setFont(Font.decode("Arial-ITALIC-12"));
		consumptionLabel.setFont(Font.decode("Arial-ITALIC-12"));
		totalSwitchesLabel.setFont(Font.decode("Arial-ITALIC-12"));

		Color color;
		if(status.equals("Running")){
			color = Color.green;
		} else {
			color = Color.red;
		}
		statusLabelValue.setForeground(color);
		Color color2;
		if(source.equals("Renewable")){
			color2 = Color.green;
		} else {
			color2 = Color.red;
		}
		sourceLabelValue.setForeground(color2);
		
		if(!status.equals(oldStatus)){
			statusLabelValue.setText(status);
		}
		if(!source.equals(oldSource)){
			sourceLabelValue.setText(source);
		}
		if(!consumption.equals(oldConsumption)){
			consumptionLabelValue.setText(consumption);
		}
		if(!totalSwitches.equals(oldTotalSwitch)){
			totalSwitchesLabelValue.setText(totalSwitches);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((consumptionLabelValue == null) ? 0 : consumptionLabelValue
						.hashCode());
		result = prime
				* result
				+ ((sourceLabelValue == null) ? 0 : sourceLabelValue.hashCode());
		result = prime
				* result
				+ ((statusLabelValue == null) ? 0 : statusLabelValue.hashCode());
		result = prime
				* result
				+ ((totalSwitchesLabelValue == null) ? 0
						: totalSwitchesLabelValue.hashCode());
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
		GridPanel other = (GridPanel) obj;
		if (consumptionLabelValue == null) {
			if (other.consumptionLabelValue != null)
				return false;
		} else if (!consumptionLabelValue.getText().equals(other.consumptionLabelValue.getText()))
			return false;
		if (sourceLabelValue == null) {
			if (other.sourceLabelValue != null)
				return false;
		} else if (!sourceLabelValue.getText().equals(other.sourceLabelValue.getText()))
			return false;
		if (statusLabelValue == null) {
			if (other.statusLabelValue != null)
				return false;
		} else if (!statusLabelValue.getText().equals(other.statusLabelValue.getText()))
			return false;
		if (totalSwitchesLabelValue == null) {
			if (other.totalSwitchesLabelValue != null)
				return false;
		} else if (!totalSwitchesLabelValue.getText()
				.equals(other.totalSwitchesLabelValue.getText()))
			return false;
		return true;
	}
}
