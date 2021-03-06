package eu.fr.ucbl.disp.trafficregulation.sma.agent.behaviour.steering;

import eu.fr.ucbl.disp.trafficregulation.sma.agent.behaviour.BehaviourOutput;
/**
 * Classe SteeringBehaviourOutput
 */
public final class SteeringBehaviourOutput extends BehaviourOutput {

	/**
	 * set l'acceleration linéaire
	 * @param xAccel
	 * @param yAccel
	 */
	public void setLinearAcceleration(double xAccel, double yAccel) {
		this.linear.set(xAccel,yAccel);
	}
	
	
	/**
	 * set l'acceleration angulaire
	 * @param accel
	 */
	public void setAngularAcceleration(double accel) {
		this.angular = accel;
	}

	@Override
	/**
	 * ??
	 */
	public boolean isAccelerationBehaviourOutput() {
		return true;
	}

}