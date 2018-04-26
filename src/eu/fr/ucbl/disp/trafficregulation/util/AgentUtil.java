package eu.fr.ucbl.disp.trafficregulation.util;

import java.util.ArrayList;
import java.util.Collections;

import eu.fr.ucbl.disp.trafficregulation.Simulation;
import eu.fr.ucbl.disp.trafficregulation.sma.agent.Animat;
import eu.fr.ucbl.disp.trafficregulation.sma.agent.VehicleAgent;

public class AgentUtil  implements Runnable{


	private ArrayList<Animat> agents = new ArrayList<Animat>();
	private double step;
	private int timer;

	public AgentUtil(double i) {

		this.step=i;
	}

	public void addAgent(Animat a){
		this.agents.add(a);

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		int t=this.timer;
		try {
			System.err.println("------Simulation randomization------");
			Collections.shuffle(this.agents);
			while (t>0){
				
				System.err.println("Simulation start :"+t);
				t-=100;
				Thread.sleep(100);

			}
			
			System.err.println("------Simulation start------");
			for (Animat animat : agents) {
				Thread.sleep((long) step);
				animat.getAgentBody().unfreeze();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setTimer(int i) {
		// TODO Auto-generated method stub
		this.timer = i;
	}

}