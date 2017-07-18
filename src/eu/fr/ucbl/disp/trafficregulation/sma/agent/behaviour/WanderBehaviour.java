package eu.fr.ucbl.disp.trafficregulation.sma.agent.behaviour;

import java.util.ArrayList;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import eu.fr.ucbl.disp.trafficregulation.sma.environment.AnimatViewPerception;

@SuppressWarnings("restriction")
/**
 * Interface WanderBehaviour
 */
public interface WanderBehaviour<OUT extends BehaviourOutput> {
	/**
	 * Méthode run()
	 * @param position
	 * @param orientation
	 * @param linearSpeed
	 * @param maxLinear
	 * @param angularSpeed
	 * @param maxAngular
	 * @param viewPercepts
	 * @return
	 */
	public OUT run(Point2d position, Vector2d orientation, double linearSpeed, double maxLinear, 
					double angularSpeed, double maxAngular, ArrayList<AnimatViewPerception> viewPercepts);
	
}