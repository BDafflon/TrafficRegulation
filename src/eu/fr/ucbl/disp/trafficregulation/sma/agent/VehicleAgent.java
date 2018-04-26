package eu.fr.ucbl.disp.trafficregulation.sma.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import org.arakhne.tinyMAS.core.AgentIdentifier;
import org.arakhne.tinyMAS.core.Message;
import org.arakhne.tinyMAS.situatedEnvironment.environment.SituatedObject;

import eu.fr.ucbl.disp.trafficregulation.sma.agent.behaviour.BehaviourOutput;
import eu.fr.ucbl.disp.trafficregulation.sma.agent.behaviour.steering.SteeringBehaviourOutput;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.AnimatBody;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.AnimatPerception;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.AnimatViewPerception;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.Info;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.objet.GoalEntity;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.objet.PerceptionType;
import eu.fr.ucbl.disp.trafficregulation.util.GeometryUtil;

@SuppressWarnings("restriction")

/**
 * Classe Visitor
 */
public class VehicleAgent extends Animat{



	private static double repulseNegative = .005;
	private static double repulseObstacle = 0.7; //0.7
	private static double repulseLimitObstacle = 0.0005;
	private static double attractGoal = .5; //.2
	private static int radius = 50; //2
	private double maxForce;
	private GoalEntity target;
	private double maxRepAgent=0.01;
	private double maxRepWay=0.02;
	private double maxSpeed=0.05;

	/**
	 * @param formationPosition
	 */


	public VehicleAgent() {
		super();


	}



	public double getMaxForce() {
		return maxForce;
	}



	public void setMaxForce(double maxForce) {
		this.maxForce = maxForce;
	}



	@Override
	/**
	 * Méthode Start()
	 */
	public void start() {

		this.getAgentBody().setType(PerceptionType.VEHICLE);
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

			List<AnimatPerception> viewPercepts =  getPerceptionFilter().getPerceivedObjects();


			BehaviourOutput output = new SteeringBehaviourOutput();

			Vector2d force = new Vector2d();
			Vector2d influence = new Vector2d();
			if(getPerceptionFilter().hasPerceivedObjects()){


				ArrayList<Vector3d> wayDelimiter = new ArrayList<Vector3d>();
				ArrayList<Vector3d> otherVehicle = new ArrayList<Vector3d>();
				ArrayList<Vector2d> roadDelimiter = new ArrayList<Vector2d>();

				//System.out.println(viewPercepts.size());
				// TRAITEMENT DES PERCEPTIONS
				for (AnimatPerception viewPercept : viewPercepts) {
					switch (viewPercept.getType()) {
					case ROADLIMIT:
						roadDelimiter.add(viewPercept.getData());
						break;
					case VEHICLE:
						Vector3d v = new Vector3d(viewPercept.getData().x,viewPercept.getData().y,viewPercept.getOrientation());
						otherVehicle.add(v);
						break;
					case WAYLIMIT:
						Vector3d w = new Vector3d(viewPercept.getData().x,viewPercept.getData().y,viewPercept.getOrientation());

						if(this.getAgentBody().getOrientation()*w.getZ() >0 ){
							wayDelimiter.add(w);
						}
						break;
					}
				}



				//force.add(this.target.getLocation());
				//force.sub(this.getAgentBody().getLocation());




				Vector2d target = findTarget(wayDelimiter);
				Vector2d otherAgenting = separation(otherVehicle);
				Vector2d wayRep = repulsion(wayDelimiter);
				influence.add(wayRep);
				//influence.add(otherAgenting);
				influence.add(target);

				//System.out.println(this.getId()+" "+ this.getAgentBody().getOrientation()+" "+ influence.x+" "+influence.y);
				if(influence.length()>this.maxForce){
					influence.normalize();
					influence.scale(this.maxForce);
				}

				this.getAgentBody().setOrientation(GeometryUtil.signedAngle(influence, new Vector2d(0,1)));


			}


			((SteeringBehaviourOutput) output).setLinearAcceleration(influence.x,influence.y);

			if (output != null) {
				body.influenceSteering(output.getLinear(), output.getAngular());
			}

		}
	}

	private Vector2d findTarget(ArrayList<Vector3d> wayDelimiter) {
		Vector2d p = new Vector2d(0,0);
		for (Vector3d v : wayDelimiter) {
			Vector2d x= new Vector2d(0,1);
			GeometryUtil.turnVector(x, -v.z);
			x.normalize();

			p.add(x);
		}

		if(p.length()>this.maxSpeed){
			p.normalize();
			p.scale(this.maxSpeed);
		}

		return p;
	}



	private Vector2d repulsion(ArrayList<Vector3d> otherVehicle) {
		Vector2d p = new Vector2d();
		Vector2d tmp = new Vector2d();

		for (Vector3d perception2d : otherVehicle) {
			if(perception2d == null)
				continue;


			tmp= new Vector2d(this.getAgentBody().getX() - perception2d.x,
					this.getAgentBody().getY()  -perception2d.y);

			if(tmp.lengthSquared()==0){


			}
			else
				tmp.scale(1/tmp.lengthSquared());
			//System.out.println(tmp.length());
			p.add(tmp);
		}


		if(p.length()>this.maxRepWay){
			p.normalize();
			p.scale(this.maxRepWay);
		}
		return p;
	}

	private Vector2d separation(ArrayList<Vector3d> agentPercept) {
		Vector2d p = new Vector2d();
		Vector2d tmp = new Vector2d();

		for (Vector3d perception2d : agentPercept) {
			tmp= new Vector2d(this.getAgentBody().getX() - perception2d.x,
					this.getAgentBody().getY()  -perception2d.y);

			if(tmp.lengthSquared()==0){

			}
			else{
				tmp.scale(1/tmp.lengthSquared());

				p.add(tmp);
			}
		}

		if(p.length()>this.maxRepAgent){
			p.normalize();
			p.scale(this.maxRepAgent);
		}

		return p;
	}



	public void setTarget(GoalEntity toward) {
		// TODO Auto-generated method stub
		this.target = toward;
	}



}