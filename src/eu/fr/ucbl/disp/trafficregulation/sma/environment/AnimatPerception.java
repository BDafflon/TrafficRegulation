package eu.fr.ucbl.disp.trafficregulation.sma.environment;

import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector2d;
import org.arakhne.tinyMAS.situatedEnvironment.perception.Perception;

import eu.fr.ucbl.disp.trafficregulation.sma.environment.objet.PerceptionType;

/**
 * Classe AnimatPerception
 */
public class AnimatPerception extends Perception<Vector2d> {
	
	PerceptionType type;
	double orientation;
	
	public double getOrientation() {
		return orientation;
	}

	public void setOrientation(double orientation) {
		this.orientation = orientation;
	}

	public void setType(PerceptionType type) {
		this.type = type;
	}

	/**
	 * constructeur avec paramètres
	 * @param percepts
	 */
	public AnimatPerception(Vector2d percepts, PerceptionType type) {
		super(percepts);
		this.type=type;
	}
	
	public AnimatPerception(Vector2d percepts, Double orientation, PerceptionType type) {
		super(percepts);
		this.type=type;
		this.orientation = orientation;
	}
	
	
	 
	
	/**
	 * extrait les perceptions visuelles d'une liste de perceptions
	 * @param pList
	 * @return
	 */
	public static ArrayList<AnimatViewPerception> extractViewPerception(List<AnimatPerception> pList){
		ArrayList<AnimatViewPerception> result = new ArrayList<AnimatViewPerception>();
		if(pList!=null)
		{
			for(AnimatPerception p : pList)
			{
				if(p instanceof AnimatViewPerception)
				{
					result.add((AnimatViewPerception) p );
				}
			}
		}
		
		return result;
	}



	public PerceptionType getType() {
		// TODO Auto-generated method stub
		return this.type;
	}
}