package eu.fr.ucbl.disp.trafficregulation.sma.environment.objet.geometry;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import javax.vecmath.Point2d;

import eu.fr.ucbl.disp.trafficregulation.sma.environment.objet.EnvironmentObject;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.objet.PerceptionType;



/**
 * Classe Bombe
 */
public class IDMDelimiter extends EnvironmentObject {


	private double orientation;
	private final int radius = 200;
	public int getRadius() {
		return radius;
	}

	/**
	 * Constructeur
	 * @param time
	 */
	public IDMDelimiter( ){
		super();
		this.setLocation(new Point2d(0,10));

	}
	
	public IDMDelimiter(Point2d p, double o ){
		super();
		this.setLocation(p);
		this.setOrientation(o);
	}


	private void setOrientation(double o) {
		// TODO Auto-generated method stub
		this.orientation = o;
	}

	@Override
	/**
	 * Set le type de perception à "BOMBE"
	 */
	public PerceptionType getPerceptionType() {

		return PerceptionType.IDMPoint;
	}

	public int getIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setPosition(Point2d p, int i) {
		// TODO Auto-generated method stub
		this.setLocation(p);
	}

	public double getOrientation() {
		// TODO Auto-generated method stub
		return this.orientation;
	}



}
