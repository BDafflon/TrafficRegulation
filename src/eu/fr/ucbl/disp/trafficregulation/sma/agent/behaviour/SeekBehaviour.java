package eu.fr.ucbl.disp.trafficregulation.sma.agent.behaviour;

import java.util.ArrayList;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;

import eu.fr.ucbl.disp.trafficregulation.sma.environment.AnimatViewPerception;

@SuppressWarnings("restriction")
/**
 * Interface SeekBehaviour
 */
public interface SeekBehaviour <OUT extends BehaviourOutput> {
	/**
	 * Méthode run()
	 * @param position
	 * @param linearSpeed
	 * @param maxLinear
	 * @param target
	 * @param viewPercepts
	 * @return
	 */
	public OUT run(Point2d position, double linearSpeed, double maxLinear, Tuple2d target, ArrayList<AnimatViewPerception> viewPercepts);
	
}