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
}
