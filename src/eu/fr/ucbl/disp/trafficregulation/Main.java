package eu.fr.ucbl.disp.trafficregulation;

import eu.fr.ucbl.disp.trafficregulation.gui.GUI;
import eu.fr.ucbl.disp.trafficregulation.util.DebugUtil;
 
public class Main {

	public static void main(String [] args) {
		DebugUtil.setMode(true, true);
		
		Simulation simu = new Simulation();
		GUI  gui = null;

		gui= new GUI(simu);

		if(DebugUtil.isDisplayMode()){
			gui.setVisible(true);
		}else{
			gui.setVisible(false);
		}

		simu.init();
	 
		simu.start();



	}

	 
}
