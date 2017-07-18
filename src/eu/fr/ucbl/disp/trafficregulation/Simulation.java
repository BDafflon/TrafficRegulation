package eu.fr.ucbl.disp.trafficregulation;

import java.io.IOException;
import org.arakhne.tinyMAS.core.Kernel;
import org.arakhne.tinyMAS.core.MessageTransportService;
import org.arakhne.tinyMAS.core.YellowPages;
import org.jdom2.JDOMException;

import eu.fr.ucbl.disp.trafficregulation.sma.agent.Animat;
import eu.fr.ucbl.disp.trafficregulation.sma.agent.StandardAgent;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.WorldModel;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.objet.GoalEntity;

@SuppressWarnings("restriction")
public class Simulation extends
Kernel<Animat, WorldModel, YellowPages, MessageTransportService> {

	public static double WORLD_SIZE_X = 700;
	public static double WORLD_SIZE_Y = 700;
	private WorldModel g;

	private GoalEntity goal;



	public void setWorld(WorldModel g) {
		this.g = g;
	}

	public WorldModel getWorldModel() {
		return g;
	}


	public GoalEntity getGoal() {
		return goal;
	}

	public void setGoal(GoalEntity goal) {
		this.goal = goal;
	}

	public Simulation() {
		super();

	}

	/**
	 * Programme Principal
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public void init(String xmlPath )  {



		g = new WorldModel(WORLD_SIZE_X, WORLD_SIZE_Y);
		this.setEnvironment(g);




		goal = new GoalEntity();
		g.addObject(goal);




		for (int i = 0; i < 300; i++) {
			StandardAgent d = new StandardAgent();
			this.addAgent(d) ;

		}
		this.setWaitingDuration(10);


	}


	/**
	 * Programme Principal
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public void init()  {



		g = new WorldModel(WORLD_SIZE_X, WORLD_SIZE_Y);
		this.setEnvironment(g);


	}

	public void start(){

		if(goal == null){
			goal = new GoalEntity();
			g.addObject(goal);
		}

		for (int i = 0; i < 300; i++) {
			StandardAgent d = new StandardAgent();
			this.addAgent(d) ;

		}


		this.setWaitingDuration(10);

		this.run();
		// Force quit
		System.exit(0);
	}


}