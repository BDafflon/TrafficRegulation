package eu.fr.ucbl.disp.trafficregulation.sma.environment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.arakhne.tinyMAS.core.AgentIdentifier;
import org.arakhne.tinyMAS.core.ConstantStepTimeManager;
import org.arakhne.tinyMAS.core.SimulationClock;
import org.arakhne.tinyMAS.situatedEnvironment.environment.AbstractSituatedEnvironment;
import org.arakhne.tinyMAS.situatedEnvironment.environment.SituatedObject;


import eu.fr.ucbl.disp.trafficregulation.sma.agent.Animat;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.objet.EnvironmentObject;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.objet.geometry.WayDelimiter;


@SuppressWarnings("restriction")
/**
 * Classe WorldModel
 */
public class WorldModel
extends
AbstractSituatedEnvironment<Animat, AnimatBody, SituatedObject, AnimatPerception, AnimatInfluence> {



	private ArrayList<AnimatBody> agents = new ArrayList<AnimatBody>();

	private ArrayList<EnvironmentObject> objects = new ArrayList<EnvironmentObject>();

	private final double width;
	private final double height;


	/**
	 * Constructeur avec paramètres
	 * @param width
	 * @param height
	 */
	public WorldModel(double width, double height) {
		super(new ConstantStepTimeManager(1, TimeUnit.SECONDS));
		this.width = width;
		this.height = height;
		//this.allBodyTree = new QuadTree(width, height);
	}



	/**
	 * Renvoi l'attribut Width
	 * @return
	 */
	public double getWidth() {
		return this.width;
	}

	/**
	 * Renvoi l'attribut Height
	 * @return
	 */
	public double getHeight() {
		return this.height;
	}

	@Override
	/**
	 * Ajoute un body passé en paramètre à l'arbre des Body (allBodyTree) avec une position aléatoire
	 */
	protected void onAgentBodyAdded(AnimatBody body) {

		double x, y;
		x =  0;
		y =  0;
		body.setPosition(x, y);
		this.agents.add(body);

	}

	@Override
	/**
	 * Supprime le body passé en paramètre de l'arbre des Body (allBodyTree)
	 */
	protected void onAgentBodyRemoved(AnimatBody body) {

	}

	/**
	 * Méthode decrivant la perception de chaque agent (auditive et visuelle)
	 */
	public AnimatPerception[] perceive(AgentIdentifier agent) {
		ArrayList<AnimatPerception> allPercepts = new ArrayList<AnimatPerception>();

		AnimatBody body = getAgentBody(agent);
		if (body!=null) {
			double x1 = body.getX();
			double y1 = body.getY();

			for(AnimatBody b1 : getAllAgentBodies()) {
				if (b1!=body) {
					double x2 = b1.getX();
					double y2 = b1.getY();
					double distance = new Vector2d(x2-x1,y2-y1).length();
					if(distance<body.getViewFustrum().radius){
						Vector2d v = new Vector2d(x2 ,y2);
						allPercepts.add(new AnimatPerception(v,b1.getOrientation(), b1.getCurrentLinearSpeed(),b1.getType()));
					}
				}
			}

			for (EnvironmentObject o : getObjects()) {
				double x2 = o.getPosX();
				double y2 = o.getPosY();
				double distance = new Vector2d(x2-x1,y2-y1).length();
				if(distance<body.getViewFustrum().radius){
					Vector2d v = new Vector2d(x2 ,y2);
					if(o instanceof WayDelimiter){
						WayDelimiter w = (WayDelimiter)o;
						allPercepts.add(new AnimatPerception(v,w.getOrientation(),0d,o.getPerceptionType()));
					}else{
						allPercepts.add(new AnimatPerception(v,0d,0d,o.getPerceptionType()));
					}
				}
			}
		}

		AnimatPerception[] tab = new AnimatPerception[allPercepts.size()];
		allPercepts.toArray(tab);
		allPercepts.clear();
		return tab;

	}

	@Override
	/**
	 * Applique les influences passées en paramètre
	 */
	protected boolean applyInfluences(Collection<AnimatInfluence> influences) {
		SimulationClock clock = getSimulationClock();

		List<AnimatInfluence> influenceList = new ArrayList<AnimatInfluence>(
				influences);
		List<AnimatAction> actions = new ArrayList<AnimatAction>(influenceList
				.size());

		// Compute actions
		for (int index1 = 0; index1 < influenceList.size(); index1++) {
			AnimatInfluence inf1 = influenceList.get(index1);
			AnimatBody body1 = getAgentBody(inf1.getEmitter());
			if (body1 != null) {
				Vector2d move;
				double rotation;

				move = body1.computeSteeringMove(
						((AnimatSteeringInfluence) inf1)
						.getLinearAcceleration(), clock);
				rotation = body1.computeSteeringRotation(
						((AnimatSteeringInfluence) inf1)
						.getAngularAcceleration(), clock);

				double x1 = body1.getX();
				double y1 = body1.getY();




				// Trivial collision detection
				/*
				 * 
				 * if(body1.getLocation().distance(new Point2d(0, 0))>10){
					body1.setPosition(0, 1);
					move.set(0, 0);
				}
				if(body1.getLocation().y<0){
					body1.setPosition(0, 1);
					move.set(0, 0);
				}
				for (int index2 = index1 + 1; index2 < influenceList.size(); index2++) {
					AnimatInfluence inf2 = influenceList.get(index2);
					AnimatBody body2 = getAgentBody(inf2.getEmitter());
					if (body2 != null) {
						double x2 = body2.getX();
						double y2 = body2.getY();

						double distance = new Vector2d(x2 - x1, y2 - y1)
						.length();

						if (distance < BODY_RADIUS) {
							move.set(0, 0);
							break;
						}
					}
				}*/

				actions.add(new AnimatAction(body1, move, rotation));

			}
		}

		// Apply the actions
		for (AnimatAction action : actions) {
			AnimatBody body = action.getObjecttoMove();
			if (body != null) {
				body.move(action.getTranslation().x, action.getTranslation().y,
						clock.getSimulationStepDuration());
				body.rotate(action.getRotation(), clock
						.getSimulationStepDuration());


			}
			if(Double.isNaN(body.getLocation().x ) || Double.isNaN(body.getLocation().y ) )
				body.setPosition(0, 0);
		}



		//System.out.println("World updating @ "+clock.getSimulationTime()); //$NON-NLS-1$

		return true;
	}

	/**
	 * Renvoi une map regroupant les positions des AnimatBody
	 * @return
	 */
	public Map<AgentIdentifier, EntityDescription> getState() {
		Collection<AnimatBody> bodies = getAllAgentBodies();
		Map<AgentIdentifier, EntityDescription> positions = new TreeMap<AgentIdentifier, EntityDescription>();
		EntityDescription desc;
		for (AnimatBody body : bodies) {

			desc = new EntityDescription(body.getViewPerceptions());
			desc.type = body.getType();
			desc.position.set(body.getX(), body.getY());
			desc.orientation
			.set(body.getOrientationX(), body.getOrientationY());
			positions.put(body.getAgent(), desc);
		}
		return positions;
	}

	/**
	 * Set les objets du worldModel
	 * @param objects
	 */
	public void setObjects(ArrayList<EnvironmentObject> objects) {
		this.objects = objects;
	}

	/**
	 * Renvoi les objets du WorldModel
	 * @return
	 */
	public ArrayList<EnvironmentObject> getObjects() {
		return objects;
	}

	/**
	 * Ajoute un objet passé en paramètre à la liste des objets du WorldModel
	 * @param o
	 */
	public void addObject(EnvironmentObject o) {
		this.objects.add(o);
	}

}