package eu.fr.ucbl.disp.trafficregulation.sma.environment.objet;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import javax.vecmath.Point2d;



/**
 * Classe Bombe
 */
public class GoalEntity extends EnvironmentObject {


	private final int radius = 200;
	public int getRadius() {
		return radius;
	}

	/**
	 * Constructeur
	 * @param time
	 */
	public GoalEntity( ){
		super();
		this.setLocation(new Point2d(0,10));

	}


	@Override
	/**
	 * Set le type de perception à "BOMBE"
	 */
	public PerceptionType getPerceptionType() {

		return PerceptionType.GOAL;
	}

	public int getIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setPosition(Point2d p, int i) {
		// TODO Auto-generated method stub
		this.setLocation(p);
	}



}
