package eu.fr.ucbl.disp.trafficregulation.sma.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import org.arakhne.tinyMAS.core.AgentIdentifier;
import org.arakhne.tinyMAS.core.Message;
import org.arakhne.tinyMAS.situatedEnvironment.environment.SituatedObject;

import eu.fr.ucbl.disp.trafficregulation.sma.agent.behaviour.BehaviourOutput;
import eu.fr.ucbl.disp.trafficregulation.sma.agent.behaviour.steering.SteeringBehaviourOutput;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.AnimatBody;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.AnimatPerception;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.AnimatSteeringInfluence;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.AnimatViewPerception;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.Info;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.objet.GoalEntity;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.objet.PerceptionType;
import eu.fr.ucbl.disp.trafficregulation.util.GeometryUtil;

@SuppressWarnings("restriction")

/**
 * Classe Visitor
 */
public class VehicleAgentIDM extends Animat{


	private static int radius = 300; //2
	
	
	/***
	 * IDM parameters
	 */
	double v0 = 13.88;
	double a = 1.2;
	double b = 2.0;
	double T = 1.2; //s
	double s0 = 3+5;
	double s1 = 2;
	double d=4; // delta
	
	
	/**
	 * @param formationPosition
	 */


	public VehicleAgentIDM(double desiredSpeed) {
		super();
		this.v0 = Math.max(desiredSpeed, 0);

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

			Vector2d influence = new Vector2d();
			if(getPerceptionFilter().hasPerceivedObjects()){


				ArrayList<Vector3d> wayDelimiter = new ArrayList<Vector3d>();
				ArrayList<Vector4d> otherVehicle = new ArrayList<Vector4d>();
				ArrayList<Vector2d> roadDelimiter = new ArrayList<Vector2d>();

				//System.out.println(viewPercepts.size());
				// TRAITEMENT DES PERCEPTIONS
				for (AnimatPerception viewPercept : viewPercepts) {
					switch (viewPercept.getType()) {
					case ROADLIMIT:
						roadDelimiter.add(viewPercept.getData());
						break;
					case VEHICLE:
						Vector4d v = new Vector4d(viewPercept.getData().x,viewPercept.getData().y,viewPercept.getOrientation(), viewPercept.getSpeed());
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
				
				
				//find the leader (if there is one)
				Vector4d nearestLeader = null;
				double minDistance = Double.MAX_VALUE;
				
				for(Vector4d pos : otherVehicle) {
					if((pos.x - this.getAgentBody().getX()) > 0) {
						if((pos.x - this.getAgentBody().getX()) < minDistance) {
							minDistance = (pos.x - this.getAgentBody().getX());
							nearestLeader = pos;
						}
					}
				}

				if(nearestLeader!=null) {
					System.out.println("-----  "+ this.getId() +"  -----");
					System.out.println("min distance = " + minDistance);
					System.out.println("leader  = " + nearestLeader);
					System.out.println("speed  = " + nearestLeader.getW());
				
					double acc = this.acc(minDistance, nearestLeader.getW(), nearestLeader.getW()-this.getAgentBody().getCurrentLinearSpeed(), T, v0, a);
				
					
					System.out.println("my speed = " + this.getAgentBody().getCurrentLinearSpeed());
					System.out.println("acc = " + acc);
					System.out.println("max speed = " + v0);
			
					
					influence = new Vector2d(acc,0);
					
					
				} else {
					
					double acc = acc(300, 0, 0, T, v0, a);
			
					
					influence = new Vector2d(acc,0);
					
				}
						
				if(this.getAgentBody().getCurrentLinearSpeed()>this.v0) {
					influence.scale(0);
				}
				

				//if(this.getAgentBody().getCurrentLinearSpeed()<0)
				//System.out.println(this.getAgentBody().getCurrentLinearSpeed());
				
				if(this.getAgentBody().getOrientation()<0) {
					influence.scale(0);
					//this.getAgentBody().setLinearMove(new Vector2d());
				}
				
				
				
				//this.getAgentBody().setOrientation(GeometryUtil.signedAngle(influence, new Vector2d(0,1)));
			}


			//if(this.getAgentBody().getCurrentLinearSpeed()>this.maxSpeed)
			//	influence.scale(0);
			
			//((SteeringBehaviourOutput) output).setLinearAcceleration(influence.x,influence.y);

			body.move(influence.getX(), 0, 10);

			
			//body.influence(
			//new AnimatSteeringInfluence(this.getId(),
			//		influence,
			//		0d));
			
			//if (output != null) {
			//	body.influenceSteering(output.getLinear(), output.getAngular());
			//}

		}
	}

	
    private double acc(double s, double v, double dv, double TLocal, double v0Local, double aLocal) {
        // treat special case of v0=0 (standing obstacle)
        if (v0Local == 0.0) {
            return 0.0;
        }
       
        double sstar = s0 + TLocal * v + s1 * Math.sqrt((v + 0.0001) / v0Local) + (0.5 * v * dv)
                / Math.sqrt(aLocal * b);

        if (sstar < s0) {
            sstar = s0;
        }

        final double aWanted = aLocal * (1.0 - Math.pow((v / v0Local), d) - (sstar / s) * (sstar / s));

        return (aWanted<-b) ? -b: aWanted; 
    }






}