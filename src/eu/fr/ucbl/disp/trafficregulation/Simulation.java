package eu.fr.ucbl.disp.trafficregulation;

import java.io.IOException;
import java.util.Random;

import javax.vecmath.Point2d;

import org.arakhne.tinyMAS.core.Kernel;
import org.arakhne.tinyMAS.core.MessageTransportService;
import org.arakhne.tinyMAS.core.YellowPages;
import org.jdom2.JDOMException;

import eu.fr.ucbl.disp.trafficregulation.sma.agent.Animat;
import eu.fr.ucbl.disp.trafficregulation.sma.agent.StandardAgent;
import eu.fr.ucbl.disp.trafficregulation.sma.agent.VehicleAgent;
import eu.fr.ucbl.disp.trafficregulation.sma.agent.VehicleAgentIDM;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.WorldModel;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.objet.GoalEntity;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.objet.geometry.IDMDelimiter;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.objet.geometry.RoadDelimiter;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.objet.geometry.WayDelimiter;
import eu.fr.ucbl.disp.trafficregulation.util.AgentUtil;
import eu.fr.ucbl.disp.trafficregulation.util.ThreadUtil;

@SuppressWarnings("restriction")
public class Simulation extends
Kernel<Animat, WorldModel, YellowPages, MessageTransportService> {

	public static double WORLD_SIZE_X = 1400;
	public static double WORLD_SIZE_Y = 1400;
	private WorldModel g;

	 



	public void setWorld(WorldModel g) {
		this.g = g;
	}

	public WorldModel getWorldModel() {
		return g;
	}


	 

	public Simulation() {
		super();

	}

	/**
	 * Programme Principal
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public void init()  {

		g = new WorldModel(WORLD_SIZE_X, WORLD_SIZE_Y);
		this.setEnvironment(g);
		
		AgentUtil au = new AgentUtil(500);

		GoalEntity towardEst = new GoalEntity();
		g.addObject(towardEst);
		towardEst.setLocation(new Point2d(-1000,20));
		
		GoalEntity towardWest = new GoalEntity();
		g.addObject(towardWest);
		towardWest.setLocation(new Point2d(1000,-20));
		
		/* Road */
		
		for (int i = -1000; i < 1000 ; i=i+5){
			RoadDelimiter wd = new RoadDelimiter( new Point2d(i,-35));
			g.addObject(wd);
		}
		
/* Road */
		
		for (int i = -1000; i < 1000 ; i=i+5){
			RoadDelimiter wd = new RoadDelimiter( new Point2d(i,35));
			g.addObject(wd);
		}

		/* Road Way 1 toward right */

		for (int i = -1000; i < 1000 ; i=i+5){
			WayDelimiter wd = new WayDelimiter( new Point2d(i,-30), Math.toRadians(45));
			g.addObject(wd);
		}

		
		for (int i = -1000; i < 1000 ; i=i+5){
			WayDelimiter wd = new WayDelimiter( new Point2d(i,-5), Math.toRadians(135));
			g.addObject(wd);
		}
		
		for (int i = -1000; i < 1000 ; i=i+20){
			IDMDelimiter wd = new IDMDelimiter( new Point2d(i,-17), Math.toRadians(0));
			g.addObject(wd);
		}
		
		/* Road Way 2 */

		for (int i = -1000; i < 1000 ; i=i+5){
			WayDelimiter wd = new WayDelimiter( new Point2d(i,30), Math.toRadians(-135));
			g.addObject(wd);
		}

		for (int i = -1000; i < 1000 ; i=i+5){
			WayDelimiter wd = new WayDelimiter( new Point2d(i,5), Math.toRadians(-45));
			g.addObject(wd);
		}

		for (int i = -1000; i < 1000 ; i=i+20){
			IDMDelimiter wd = new IDMDelimiter( new Point2d(i,17), Math.toRadians(-180));
			g.addObject(wd);
		}
		
	
		Random r = new Random();
		//double rangeMin =0.001;
		//double rangeMax = 0.01;
		
	
		for (int i = 0; i < 10; i++) {
			
			double speed = (13) * r.nextDouble() +1d;
			

			//VehicleAgent d = new VehicleAgent();
			VehicleAgentIDM d = new VehicleAgentIDM(speed);
			this.addAgent(d) ;
			d.getAgentBody().setPosition(-700,-20);
			//d.setTarget(towardWest);
			d.getAgentBody().freeze();
			d.getAgentBody().setOrientation(Math.toRadians(90));
			//double speed = (rangeMax - rangeMin) * r.nextDouble();
			//System.out.println(speed);
			//d.setMaxForce(speed);
			au.addAgent(d);
		}
	
		
		this.setWaitingDuration(10);
		
		au.setTimer(100);
		ThreadUtil.execute(au);

	}

	public void start(){



		this.run();
		// Force quit
		System.exit(0);
	}


}