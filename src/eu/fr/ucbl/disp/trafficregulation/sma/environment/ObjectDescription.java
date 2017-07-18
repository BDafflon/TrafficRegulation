package eu.fr.ucbl.disp.trafficregulation.sma.environment;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import eu.fr.ucbl.disp.trafficregulation.sma.environment.objet.PerceptionType;
/**
 * Classe ObjectDescription : description d'un objet (batiment, scene...)
 */
public class ObjectDescription implements Comparable<EntityDescription> {
	/**
	 * Constructeur
	 */
	public ObjectDescription() {
		
	}
	 
	/**
	 * Méthode de comparaison
	 */
	public int compareTo(EntityDescription o) {
		return o.hashCode() - hashCode();
	}

}
