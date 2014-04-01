package threads;

import java.awt.BorderLayout;
import java.awt.Container;

import gui.GridPanel;
import gui.LinePanel;

public class UpdatesInformations implements Runnable {
	
	private GridPanel panel;
	private LinePanel informations;
	public UpdatesInformations(GridPanel panel, LinePanel informations){
		this.panel = panel;
		this.informations=informations;
	}
	@Override
	public void run() {
		while(true){
			String status = informations.getStatus()?"Running":"Not Running";
			String source = informations.getSource()?"Renewable":"Wired";
			String consumption = String.valueOf(informations.getConsumption());
			String total = "0";
			Container parent = panel.getParent();
			if(parent == null)
				continue;
			parent.remove(panel);
			panel = new GridPanel(status,source,consumption,total);
			parent.add(panel,BorderLayout.CENTER);
			parent.validate();
			parent.repaint();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}