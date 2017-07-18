package eu.fr.ucbl.disp.trafficregulation.sma.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;

 
import org.arakhne.tinyMAS.core.AgentIdentifier;
import org.arakhne.tinyMAS.core.Message;
import org.arakhne.tinyMAS.situatedEnvironment.environment.SituatedObject;

import eu.fr.ucbl.disp.trafficregulation.sma.agent.behaviour.BehaviourOutput;
import eu.fr.ucbl.disp.trafficregulation.sma.agent.behaviour.steering.SteeringBehaviourOutput;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.AnimatBody;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.AnimatPerception;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.AnimatViewPerception;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.Info;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.objet.PerceptionType;

@SuppressWarnings("restriction")

/**
 * Classe Visitor
 */
public class StandardAgent extends Animat{



	private static double repulseNegative = .005;
	private static double repulseObstacle = 0.7; //0.7
	private static double repulseLimitObstacle = 0.0005;
	private static double attractGoal = .5; //.2
	private static int radius = 3; //2
	private static double maxForce=100;

	/**
	 * @param formationPosition
	 */


	public StandardAgent() {
		super();


	}

	@Override
	/**
	 * Méthode Start()
	 */
	public void start() {

		this.getAgentBody().setType(PerceptionType.STANDARDAGENT);
		this.getAgentBody().getViewFustrum().setRadius(radius);
	}

	@Override
	/**
	 * Méthode DoDecisionAndAction
	 */
	protected void doDecisionAndAction() {
		// compute static parameters
		AnimatBody body = getAgentBody();
		if (body.isFreezed()) {

		}
		else {

			Vector2d influence = new Vector2d();

			Point2d position = new Point2d(body.getX(), body.getY());
			Vector2d orientation = body.getOrientationUnitVector();
			double linearSpeed = body.getCurrentLinearSpeed();
			double angularSpeed = body.getCurrentAngularSpeed();

			List<AnimatPerception> viewPercepts =  getPerceptionFilter().getPerceivedObjects();


			BehaviourOutput output = new SteeringBehaviourOutput();

			Vector2d force = new Vector2d();

			if(getPerceptionFilter().hasPerceivedObjects()){


				ArrayList<Vector2d> agentPercept = new ArrayList<Vector2d>();
				ArrayList<Vector2d> obstaclePercept = new ArrayList<Vector2d>();
 
				//System.out.println(viewPercepts.size());
 				// TRAITEMENT DES PERCEPTIONS
				for (AnimatPerception viewPercept : viewPercepts) {
					switch (viewPercept.getType()) {
					case OBSTACLE:
						obstaclePercept.add(viewPercept.getData());
						break;
					case STANDARDAGENT:
						agentPercept.add(viewPercept.getData());
						break;
					case GOAL:
						viewPercept.getData().sub(this.getAgentBody().getLocation());
						force = new Vector2d(viewPercept.getData());
						force.normalize();
						force.scale(StandardAgent.attractGoal);
						influence.add(force);
						//System.out.println("goal "+ force);
						break;
					}
				}

 

			 

				force = new Vector2d();
				if(agentPercept.size()>0){
					force = separation(agentPercept);

					//System.out.println("sep "+force);
					force.scale(StandardAgent.repulseNegative);

					influence.add(force);
				}

				force = new Vector2d(0, 0);
				//System.out.println("size obstacle "+obstaclePercept.size());
				if(obstaclePercept.size()>0){
					force = repulsion(obstaclePercept);

					//System.out.println("repulsion "+force);
					force.scale(StandardAgent.repulseObstacle);

					influence.add(force);

				}
			}

			if(influence.length()>this.maxForce){
				influence.normalize();
				influence.scale(this.maxForce);
			}


			((SteeringBehaviourOutput) output).setLinearAcceleration(influence.x,influence.y);

			if (output != null) {
				body.influenceSteering(output.getLinear(), output.getAngular());
			}
		}

	}

	private Vector2d repulsion(ArrayList<Vector2d> obstaclePercept) {
		Vector2d p = new Vector2d();
		Vector2d tmp = new Vector2d();

		for (Vector2d perception2d : obstaclePercept) {
			if(perception2d == null)
				continue;

			tmp= new Vector2d(this.getAgentBody().getX() - perception2d.x,
					this.getAgentBody().getY()  -perception2d.y);

			if(tmp.lengthSquared()==0){
				Random randomGenerator = new Random();
				tmp = new Vector2d(randomGenerator.nextInt(100),randomGenerator.nextInt(100));
				tmp.normalize();
				tmp.scale(10);
			}
			else
				tmp.scale(1/tmp.lengthSquared());
			//System.out.println(tmp.length());
			p.add(tmp);
		}
		p.normalize();
		return p;
	}

	private Vector2d separation(ArrayList<Vector2d> agentPercept) {
		Vector2d p = new Vector2d();
		Vector2d tmp = new Vector2d();

		for (Vector2d perception2d : agentPercept) {
			tmp= new Vector2d(this.getAgentBody().getX() - perception2d.x,
					this.getAgentBody().getY()  -perception2d.y);

			if(tmp.lengthSquared()==0){
				Random randomGenerator = new Random();
				tmp = new Vector2d(randomGenerator.nextInt(100),randomGenerator.nextInt(100));
				tmp.normalize();
				tmp.scale(10);
			}
			else
				tmp.scale(1/tmp.lengthSquared());

			p.add(tmp);
		}

		p.normalize();


		return p;
	}



}