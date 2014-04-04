package threads;

import gui.GridPanel;
import gui.LinePanel;

public class UpdatesInformations implements Runnable {
	private final int SLEEP_TIME = 500;
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
			String total = String.valueOf(informations.getSwitch_count());
			panel.updatePanel(status, source, consumption, total);
			
//			Container parent = panel.getParent();
//			if(parent == null)
//				continue;
//			GridPanel p2 = new GridPanel(status,source,consumption,total);
//			if(!panel.equals(p2)){ 
//				parent.remove(panel);
//				parent.add(p2,BorderLayout.CENTER);
//				panel=p2;
//				parent.validate();
//				parent.repaint();
//			}
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
