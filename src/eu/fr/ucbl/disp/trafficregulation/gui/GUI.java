/* 
 * $Id$
 * 
 * Copyright (C) 2004-2007 St&eacute;phane GALLAND.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * This program is free software; you can redistribute it and/or modify
 */

package eu.fr.ucbl.disp.trafficregulation.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import javax.print.attribute.standard.JobMessageFromOperator;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.arakhne.tinyMAS.core.AgentIdentifier;
import org.arakhne.tinyMAS.core.Kernel;
import org.arakhne.tinyMAS.core.KernelAdapter;
import org.arakhne.tinyMAS.situatedEnvironment.body.AgentBody;

import eu.fr.ucbl.disp.trafficregulation.Simulation;

import eu.fr.ucbl.disp.trafficregulation.sma.environment.AnimatBody;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.AnimatPerception;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.AnimatViewPerception;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.EntityDescription;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.WorldModel;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.objet.EnvironmentObject;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.objet.GoalEntity;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.objet.PerceptionType;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.objet.StandardEntity;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.objet.geometry.IDMDelimiter;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.objet.geometry.RoadDelimiter;
import eu.fr.ucbl.disp.trafficregulation.sma.environment.objet.geometry.WayDelimiter;
import eu.fr.ucbl.disp.trafficregulation.util.GeometryUtil;

public class GUI extends JFrame {

	private static final long serialVersionUID = 5606976766146702008L;

	protected static final double SPOT_RADIUS = 20.;
	protected static final double DIRECTION_RADIUS = 60.;

	private static final boolean SHOW_ICON = true;


	private JLabel speed;
	private JLabel angle;
	private Vector2d target = new Vector2d();


	private final WeakReference<Kernel<?, ?, ?, ?>> kernel;
	protected final World world;




	// Constructor
	public GUI(final Simulation kernel) {
		setTitle("OA"); //$NON-NLS-1$

		this.kernel = new WeakReference<Kernel<?, ?, ?, ?>>(kernel);

		Container content = getContentPane();

		content.setLayout(new BorderLayout());

		this.world = new World();

		JButton closeBt = new JButton("Quit"); //$NON-NLS-1$
		closeBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getKernel().stop();
			}
		});
		content.add(BorderLayout.SOUTH, closeBt);

		this.world.setPreferredSize(new Dimension(1400,1400));

		// Configuration du kernel pour la GUI
		kernel.addKernelListener(new KernelAdapter() {
			@Override
			public void kernelRefreshAllowed(Kernel<?, ?, ?, ?> aKernel) {
				if (aKernel == GUI.this.getKernel()) {
					changeState();
				}
			}

			@Override
			public void kernelStarted(Kernel<?, ?, ?, ?> aKernel) {
				if (aKernel == GUI.this.getKernel()) {
					changeState();
				}
			}
		});

		// ============ CENTER
		JScrollPane worldPanel = new JScrollPane(this.world);

		content.add(BorderLayout.CENTER, worldPanel);
		this.world.setPreferredSize(new Dimension(1400,1400));

		// ============ EAST
		JPanel controlPanel = new JPanel(new GridLayout(3, 1));
		content.add(BorderLayout.EAST, controlPanel);




		// === Options
		JPanel optionsDisplayPanel = new JPanel();
		GridBagConstraints gbConst = new GridBagConstraints();
		gbConst.fill = GridBagConstraints.HORIZONTAL;

		GridBagLayout gridBagLayout = new GridBagLayout();
		// gridBagLayout.setConstraints(optionsDisplayPanel, gbConst);
		optionsDisplayPanel.setLayout(gridBagLayout);

		speed = new JLabel("speed :");
		angle = new JLabel("angle :");
		optionsDisplayPanel.setBorder(BorderFactory
				.createTitledBorder("info"));

		gbConst.weighty = 0.5;
		gbConst.gridx = 0;
		gbConst.gridy = 0;
		gridBagLayout.setConstraints(speed, gbConst);
		optionsDisplayPanel.add(speed);

		gbConst.weighty = 0.5;
		gbConst.gridx = 0;
		gbConst.gridy = 1;
		gridBagLayout.setConstraints(angle, gbConst);
		optionsDisplayPanel.add(angle);


		// Button Play
		JPanel playControlPanel = new JPanel();
		final JButton startPauseBtn = new JButton("Pause");
		startPauseBtn.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent arg0) {
				if (arg0.getSource() == startPauseBtn) {
					if (startPauseBtn.getText().compareTo("Play") == 0) {
						startPauseBtn.setText("Pause");
					} else {
						kernel.pause();
						startPauseBtn.setText("Play");
					}
				}
			}
		});

		// Button Restart
		final JButton restartStopBtn = new JButton("Stop");
		restartStopBtn.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {
				if (restartStopBtn.getText().compareTo("Stop") == 0) {
					restartStopBtn.setText("Restart");
				} else {
					restartStopBtn.setText("Stop");
				}
			}
		});

		// Button Quit
		JButton quitBtn = new JButton("Quitter");
		quitBtn.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {
				getKernel().stop();
			}
		});
		playControlPanel.add(startPauseBtn);
		playControlPanel.add(restartStopBtn);
		playControlPanel.add(quitBtn);

		controlPanel.add(optionsDisplayPanel);
		controlPanel.add(playControlPanel);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				Kernel<?, ?, ?, ?> bindKernel = GUI.this.getKernel();
				bindKernel.stop();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				Kernel<?, ?, ?, ?> bindedKernel = GUI.this.getKernel();
				bindedKernel.stop();
			}
		});

		/*
		 * this.world.addMouseMotionListener(new MouseMotionListener() { public
		 * void mouseDragged(MouseEvent e) { if (GUI.this.target!=null)
		 * GUI.this.target.setPosition(e.getX(), e.getY()); } public void
		 * mouseMoved(MouseEvent e) { if (GUI.this.target!=null)
		 * GUI.this.target.setPosition(e.getX(), e.getY()); } });
		 * 
		 * this.world.addMouseListener(new MouseListener() { public void
		 * mouseClicked(MouseEvent e) {} public void mouseEntered(MouseEvent e)
		 * { GUI.this.target = new Target(e.getX(), e.getY());
		 * getEnvironment().setTarget(GUI.this.target); } public void
		 * mouseExited(MouseEvent e) { GUI.this.target = null;
		 * getEnvironment().setTarget(null); } public void
		 * mousePressed(MouseEvent e) {} public void mouseReleased(MouseEvent e)
		 * {} });
		 */
		pack();
	}

	public JLabel getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed.setText(speed);

	}

	public JLabel getAngle() {
		return angle;
	}

	public void setAngle(String angle) {
		this.angle.setText( angle );
	}


	protected WorldModel getEnvironment() {
		return (WorldModel) getKernel().getEnvironment();
	}

	protected void changeState() {
		WorldModel environment = getEnvironment();
		if (environment != null) {
			this.world.setPositions(environment.getState());
		}
	}

	protected Kernel<?, ?, ?, ?> getKernel() {
		return this.kernel.get();
	}

	public Vector2d getTarget() {
		return this.world.target = target;
	}

	public void setTarget(Vector2d direction) {
		this.world.target = new Vector2d(direction);
	}

	private class World extends JPanel {

		public Vector2d target= new Vector2d();

		private static final long serialVersionUID = -3664274989335722916L;

		private Map<AgentIdentifier, EntityDescription> positions = null;

		private int scaleFactor=1;

		private int s=1400/2;

		public World() {
			//
		}

		public void setPositions(
				Map<AgentIdentifier, EntityDescription> positions) {
			this.positions = positions;
			repaint();
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			Graphics2D g2d = (Graphics2D) g;

			drawObject(g2d);
			drawAgents(g2d);

		}

		private void drawObject(Graphics2D g2d ) {
			EntityDescription p;
			ArrayList<EnvironmentObject> grid = new ArrayList<EnvironmentObject>(getEnvironment().getObjects());

			for (EnvironmentObject environmentObject : grid) {

				if (environmentObject instanceof RoadDelimiter) {
					RoadDelimiter rd = (RoadDelimiter) environmentObject;
					g2d.setColor(Color.black);

					int x = (int) (rd.getPosX()*scaleFactor);
					int y = (int)( rd.getPosY()*scaleFactor);

					((Graphics2D) g2d).fill(new Ellipse2D.Double(s+x-(scaleFactor*1)/2,s-y-(scaleFactor*1)/2,6,6));

				}
				if (environmentObject instanceof WayDelimiter) {
					WayDelimiter wd = (WayDelimiter) environmentObject;
					g2d.setColor(Color.red);


					int x = (int) (wd.getPosX()*scaleFactor);
					int y = (int) (wd.getPosY()*scaleFactor);

					Vector2d v = new Vector2d(0,50);
					GeometryUtil.turnVector(v, Math.toRadians(wd.getOrientation()));


					((Graphics2D) g2d).fill(new Ellipse2D.Double(s+x-(scaleFactor*1)/2,s-y-(scaleFactor*1)/2,6,6));

					//((Graphics2D) g2d).drawLine(s+x-(scaleFactor*1)/2,s-y-(scaleFactor*1)/2,s+x-(scaleFactor*1)/2+(int)v.x,s-y-(scaleFactor*1)/2+(int)v.y);
				}
				if (environmentObject instanceof IDMDelimiter) {
					IDMDelimiter wd = (IDMDelimiter) environmentObject;
					g2d.setColor(Color.cyan);


					int x = (int) (wd.getPosX()*scaleFactor);
					int y = (int) (wd.getPosY()*scaleFactor);

					Vector2d v = new Vector2d(0,50);
					GeometryUtil.turnVector(v, Math.toRadians(wd.getOrientation()));


					((Graphics2D) g2d).fill(new Ellipse2D.Double(s+x-(scaleFactor*1)/2,s-y-(scaleFactor*1)/2,6,6));

					//((Graphics2D) g2d).drawLine(s+x-(scaleFactor*1)/2,s-y-(scaleFactor*1)/2,s+x-(scaleFactor*1)/2+(int)v.x,s-y-(scaleFactor*1)/2+(int)v.y);
				}
				if (environmentObject instanceof GoalEntity) {
					GoalEntity g = (GoalEntity) environmentObject;
					g2d.setColor(Color.blue);


					int x = (int) (g.getPosX()*scaleFactor);
					int y = (int) (g.getPosY()*scaleFactor);

					Vector2d v = new Vector2d(0,50);
				 


					((Graphics2D) g2d).fill(new Ellipse2D.Double(s+x-(scaleFactor*1)/2,s-y-(scaleFactor*1)/2,6,6));

					//((Graphics2D) g2d).drawLine(s+x-(scaleFactor*1)/2,s-y-(scaleFactor*1)/2,s+x-(scaleFactor*1)/2+(int)v.x,s-y-(scaleFactor*1)/2+(int)v.y);
				}


			}
			//draw direction
			 

		}

		private void drawAgents(Graphics2D g2d) {
			EntityDescription p;
			WorldModel grid = getEnvironment();


			for (AnimatBody body : grid.getAllAgentBodies()) {
				g2d.setColor(Color.GREEN);
				//g2d.drawLine(x, y, x + dx, y + dy);

				int x = (int) (body.getLocation().x * scaleFactor);
				int y =  (int) (body.getLocation().y * scaleFactor);

				((Graphics2D) g2d).fill(new Ellipse2D.Double(s+x-(scaleFactor*1)/2,s-y-(scaleFactor*1)/2,6,6));


			}

		}



		private void drawAgent(Graphics2D g2d, int x, int y, int dx, int dy, boolean leader,int radius, ArrayList<AnimatViewPerception> viewPerception, PerceptionType type, EntityDescription eDesc) {


			g2d.setColor(Color.GREEN);
			//g2d.drawLine(x, y, x + dx, y + dy);


			x = (int) x*scaleFactor;
			y = (int)y*scaleFactor;

			((Graphics2D) g2d).fill(new Ellipse2D.Double(s-x-(scaleFactor*1)/2,s-y-(scaleFactor*1)/2,2,2));


			/*g2d.fillOval(x-1+(int) getEnvironment()
					.getWidth()/2, y-1+(int) getEnvironment()
					.getHeight()/2, 4,4);*/



		}



	}

}