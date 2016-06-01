package jfrog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import jfrog.object.Base;
import jfrog.object.BaseColl_Name;
import jfrog.object.Event_Beam;

public class AnimatorCMS {
	
	final static long Id_DT  = 12100000; 
	final static long Id_CSC = 12200000;
	final static long Id_HE  = 14200000;
	final static long Id_HF  = 14400000;
	final static long Id_HFm = 14410000;	
	final static long Id_HFp = 14420000;	
	
	final static long Id_TEC = 11600000;
	final static long Id_TOB = 11500000;
	final static long Id_TID = 11400000;
	final static long Id_TIB = 11300000;
	final static long Id_PIE = 11200000;
	final static long Id_PIB = 11100000;
		
	AnimatorCMS(){
		JMenu menu;
		JMenu submenu;
		JMenuItem menuItem;
			
		menu = new JMenu("Plugins");
		jfrog.Common.menuBar.add(menu, jfrog.Common.menuBar.getComponentCount()-2);		
		
		submenu = new JMenu("CMS");
		menu.add(submenu);
		menu = submenu;
		
		menuItem = new JMenuItem("Animation Maker (11sec)");
		menu.add(menuItem);		
		menuItem.setToolTipText("Produce material for the 11sec animation");
		menuItem.addActionListener(new ActionListener() {public void actionPerformed(final ActionEvent e){
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				protected Void doInBackground() throws Exception {
					AnimationShort(e);
					System.exit(0);
					return null;
				}};
				worker.execute();
		}});	
		

		menuItem = new JMenuItem("Animation Maker (20sec)");
		menu.add(menuItem);		
		menuItem.setToolTipText("Produce material for the 20sec animation");
		menuItem.addActionListener(new ActionListener() {public void actionPerformed(final ActionEvent e){
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				protected Void doInBackground() throws Exception {
					AnimationMiddle(e);
					System.exit(0);
					return null;
				}};
				worker.execute();
		}});			

		
		menuItem = new JMenuItem("Animation Maker (30sec)");
		menu.add(menuItem);		
		menuItem.setToolTipText("Produce material for the 30sec animation");
		menuItem.addActionListener(new ActionListener() {public void actionPerformed(final ActionEvent e){
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				protected Void doInBackground() throws Exception {
					AnimationLong(e);
					System.exit(0);
					return null;
				}};
				worker.execute();
		}});		

		
		
		menuItem = new JMenuItem("Animation Maker (fixed)");
		menu.add(menuItem);		
		menuItem.setToolTipText("Produce material for the fixed animation");
		menuItem.addActionListener(new ActionListener() {public void actionPerformed(final ActionEvent e){
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				protected Void doInBackground() throws Exception {
					AnimationFixed(e);
					System.exit(0);
					return null;
				}};
				worker.execute();
		}});				
			
		menuItem = new JMenuItem("Animation Geometry");
		menu.add(menuItem);		
		menuItem.setToolTipText("Produce material for the geometry animation");
		menuItem.addActionListener(new ActionListener() {public void actionPerformed(final ActionEvent e){
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				protected Void doInBackground() throws Exception {
					AnimationGeometry(e);
					return null;
				}};
				worker.execute();								
		}});			
		
		jfrog.Common.menuBar.updateUI();
	}
	
	static void AnimationShort(ActionEvent e){
		jfrog.view.View3D view = (jfrog.view.View3D)jfrog.Common.tab.getSelectedComponent();
		if(view==null)return;

		boolean Animation = false;					
		int option = JOptionPane.showOptionDialog(null, "Would you like to produce the Animation now ?",
				"What to do ?", 
				JOptionPane.YES_NO_OPTION,
				JOptionPane.YES_NO_OPTION, 
				null,
				new Object[]{"Animation", "Rehearsal"}, JOptionPane.DEFAULT_OPTION);

		if(option==JOptionPane.CLOSED_OPTION)return;					
		if(option==JOptionPane.YES_OPTION)Animation = true;

		String filePath = "screenshot";

		JDialog dialog = new JDialog(jfrog.Common.mainFrame, "Animator Progress");
		dialog.setSize(300, 100);
		JProgressBar progressBar = new JProgressBar(0, 100);
		progressBar.setBorderPainted(true);
		progressBar.setStringPainted(true);
		dialog.add(progressBar);
		dialog.setVisible(true);
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getComponent().setVisible(false);
			}
		});	    	

		jfrog.Common.show_FPS = false;
		view.overlay.refresh();
		view.rotating = false;
		jfrog.Common.animator.pause();
		geomFindAndSetVisible(Id_DT, 1);
		geomFindAndSetVisible(Id_CSC, 1);

		ArrayList<Base> visibleObjects = new ArrayList<Base>();
		jfrog.Common.events.fillVisibleObject(visibleObjects);
		jfrog.Common.events.setVisible((byte) 0);
		
		BaseColl_Name beams = new BaseColl_Name();
		beams.id = 5500;
		beams.name = "Beams";
		jfrog.Common.geom.addDaughter(beams);
		Event_Beam beam1 = new Event_Beam();
		beam1.id = 5501;
		beam1.pz = 1;
		beam1.sx = 0.2f;
		beam1.sy = 0.2f;
		beam1.sz = 5.0f;
		beam1.N  = 1000;
		Event_Beam beam2 = (Event_Beam)beam1.clone();
		beam2.id = 5502;
		beam2.pz = -1;
		beams.addDaughter(beam1);
		beams.addDaughter(beam2);					
		Style beamStyle = new Style();
		beamStyle.isGeom = true;
		beamStyle.color[0] = 0.0f;
		beamStyle.color[1] = 0.0f;					
		beamStyle.color[2] = 1.0f;
		beamStyle.color[3] = 1.0f;
		beams.setStyle(beamStyle, true, true);
		beams.setVisible((byte)1);

		Camera.keyFrames cameraFrames = view.cam.new keyFrames();
		cameraFrames.addKey(-40, 0,0,0, 2.0707f,0.08f,3500f);
		cameraFrames.addKey(-15, 0,0,0, 1.5707f,0.04f,350);
		cameraFrames.addKey( 10, 0,0,0, 0.0000f,0.00f,350);					
		cameraFrames.addKey( 35, 0,0,0,-1.5007f,0.04f,350);					
		cameraFrames.addKey( 60, 0,0,0,-3.1400f,0.08f,600);					
		cameraFrames.addKey( 85, 0,0,0,-4.2100f,0.12f,800);					

		progressBar.setMinimum((int)cameraFrames.getStartTime());
		progressBar.setMaximum((int)cameraFrames.getEndTime());
		progressBar.setValue(progressBar.getMinimum());

		Style.setStyleForEventCreation(jfrog.Common.styles);
		Style.cleanStyleForFading(jfrog.Common.styles);
		geomFindAndFade(Id_DT, 25, 1.0f);
		geomFindAndFade(Id_DT, 30, 0.0f);

		geomFindAndFade(Id_CSC, 25, 1.0f);
		geomFindAndFade(Id_CSC, 30, 0.0f);					
				
		int pictureIndex = 0;
		float dt = 1.0f;
		for(float t=cameraFrames.getStartTime(); t<cameraFrames.getEndTime() && dialog.isVisible();t+=dt){
			jfrog.Common.time = t;
			cameraFrames.moveCamera(view.cam);
			Style.updateStyleForFading(jfrog.Common.styles);						
			if(t<-2 || t>5){dt = 0.5f;}else{dt = 0.15f;}						

			if(t-dt<0 && t>=0)for(int i=0;i<visibleObjects.size();i++)visibleObjects.get(i).setVisible((byte) 1);			
			
			progressBar.setValue((int)t);
			progressBar.update(progressBar.getGraphics());
			view.update(view.getGraphics());						
			if(Animation){view.makeScreenShot(new File(String.format("%s_%04d.png", filePath, pictureIndex)) );	pictureIndex++;}
		}
		Style.clearStyleForEventCreation(jfrog.Common.styles);
		
		dialog.setVisible(false);

		jfrog.Common.animator.resume();
		return;
	}
	
	
	
	static void AnimationLong(ActionEvent e){
		jfrog.view.View3D view = (jfrog.view.View3D)jfrog.Common.tab.getSelectedComponent();
		if(view==null)return;

		boolean Animation = false;					
		int option = JOptionPane.showOptionDialog(null, "Would you like to produce the Animation now ?",
				"What to do ?", 
				JOptionPane.YES_NO_OPTION,
				JOptionPane.YES_NO_OPTION, 
				null,
				new Object[]{"Animation", "Rehearsal"}, JOptionPane.DEFAULT_OPTION);

		if(option==JOptionPane.CLOSED_OPTION)return;					
		if(option==JOptionPane.YES_OPTION)Animation = true;

		String filePath = "screenshot";

		JDialog dialog = new JDialog(jfrog.Common.mainFrame, "Animator Progress");
		dialog.setSize(300, 100);
		JProgressBar progressBar = new JProgressBar(0, 100);
		progressBar.setBorderPainted(true);
		progressBar.setStringPainted(true);
		dialog.add(progressBar);
		dialog.setVisible(true);
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getComponent().setVisible(false);
			}
		});	    	

		jfrog.Common.show_FPS = false;
		view.overlay.refresh();
		view.rotating = false;
		jfrog.Common.animator.pause();
		geomFindAndSetVisible(Id_DT, 1);
		geomFindAndSetVisible(Id_CSC, 1);
		geomFindAndSetVisible(Id_HF, 1);
		geomFindAndSetVisible(Id_TEC, 1);
		geomFindAndSetVisible(Id_TOB, 1);
		geomFindAndSetVisible(Id_TID, 1);
		geomFindAndSetVisible(Id_TIB, 1);		
		geomFindAndSetVisible(Id_PIE, 1);		
		geomFindAndSetVisible(Id_PIB, 1);
		
		ArrayList<Base> visibleObjects = new ArrayList<Base>();
		jfrog.Common.events.fillVisibleObject(visibleObjects);
		jfrog.Common.events.setVisible((byte) 0);

		BaseColl_Name beams = new BaseColl_Name();
		beams.id = 5500;
		beams.name = "Beams";
		jfrog.Common.geom.addDaughter(beams);
		Event_Beam beam1 = new Event_Beam();
		beam1.id = 5501;
		beam1.pz = 1;
		beam1.sx = 0.2f;
		beam1.sy = 0.2f;
		beam1.sz = 5.0f;
		beam1.N  = 1000;
		Event_Beam beam2 = (Event_Beam)beam1.clone();
		beam2.id = 5502;
		beam2.pz = -1;
		beams.addDaughter(beam1);
		beams.addDaughter(beam2);					
		Style beamStyle = new Style();
		beamStyle.isGeom = true;
		beamStyle.color[0] = 0.0f;
		beamStyle.color[1] = 0.0f;					
		beamStyle.color[2] = 1.0f;
		beamStyle.color[3] = 1.0f;
		beams.setStyle(beamStyle, true, true);
		beams.setVisible((byte)1);

		Camera.keyFrames cameraFrames = view.cam.new keyFrames();
		cameraFrames.addKey(-75, 0,0,0, 3.1415f,0.05f,2750);
		cameraFrames.addKey(-50, 0,0,0, 1.7545f,0.04f,2000);
		cameraFrames.addKey(-25, 0,0,0, 1.5000f,0.04f, 400);					
		cameraFrames.addKey(  0, 0,0,0, 0.0000f,0.00f, 350);					
		cameraFrames.addKey( 25, 0,0,0,-1.5000f,0.00f, 400);					
		cameraFrames.addKey( 55, 0,0,0,-1.7545f,0.04f,2000);					
		cameraFrames.addKey( 80, 0,0,0,-3.1415f,0.05f,2100);		
		cameraFrames.addKey(155, 0,0,0,-6.2831f,0.15f,2250);		
		cameraFrames.addKey(205, 0,0,0,-9.4247f,0.05f,2750);		
				
		progressBar.setMinimum((int)cameraFrames.getStartTime());
		progressBar.setMaximum((int)cameraFrames.getEndTime());
		progressBar.setValue(progressBar.getMinimum());

		Style.setStyleForEventCreation(jfrog.Common.styles);		
		Style.cleanStyleForFading(jfrog.Common.styles);
		geomFindAndFade(Id_DT,   70, 1.0f);
		geomFindAndFade(Id_DT,   75, 0.0f);		
		geomFindAndFade(Id_DT,  190, 0.0f);
		geomFindAndFade(Id_DT,  195, 1.0f);		
		
		geomFindAndFade(Id_CSC,  70, 1.0f);
		geomFindAndFade(Id_CSC,  75, 0.0f);
		geomFindAndFade(Id_CSC, 190, 0.0f);
		geomFindAndFade(Id_CSC, 195, 1.0f);		
		
		geomFindAndFade(Id_HFm, -50, 1.0f);
		geomFindAndFade(Id_HFm, -45, 0.0f);
		geomFindAndFade(Id_HFm, -25, 0.0f);
		geomFindAndFade(Id_HFm, -20, 1.0f);		
		geomFindAndFade(Id_HFm,  70, 1.0f);
		geomFindAndFade(Id_HFm,  75, 0.0f);
		geomFindAndFade(Id_HFm, 190, 0.0f);
		geomFindAndFade(Id_HFm, 195, 1.0f);		
		
		geomFindAndFade(Id_HFp,  15, 1.0f);
		geomFindAndFade(Id_HFp,  20, 0.0f);
		geomFindAndFade(Id_HFp,  60, 0.0f);
		geomFindAndFade(Id_HFp,  65, 1.0f);		
		geomFindAndFade(Id_HFp,  70, 1.0f);
		geomFindAndFade(Id_HFp,  75, 0.0f);
		geomFindAndFade(Id_HFp, 190, 0.0f);
		geomFindAndFade(Id_HFp, 195, 1.0f);		
		
		geomFindAndFade(Id_TEC, -45, 1.0f);		
		geomFindAndFade(Id_TEC, -40, 0.0f);
				
		geomFindAndFade(Id_TID, -25, 1.0f);		
		geomFindAndFade(Id_TID, -20, 0.0f);
			
		geomFindAndFade(Id_TOB, -20, 1.0f);		
		geomFindAndFade(Id_TOB, -15, 0.0f);
			
		geomFindAndFade(Id_TIB, -18, 1.0f);		
		geomFindAndFade(Id_TIB, -13, 0.0f);		
		
		geomFindAndFade(Id_PIE, -15, 1.0f);		
		geomFindAndFade(Id_PIE, -10, 0.0f);
		
		geomFindAndFade(Id_PIB, -10, 1.0f);		
		geomFindAndFade(Id_PIB,  -5, 0.0f);		
		
		int pictureIndex = 0;
		float dt = 1.0f;
		for(float t=cameraFrames.getStartTime(); t<cameraFrames.getEndTime() && dialog.isVisible();t+=dt){
			jfrog.Common.time = t;
			
			cameraFrames.moveCamera(view.cam);
			Style.updateStyleForFading(jfrog.Common.styles);						
			if(t<-2 || t>5){dt = 0.4f;}else{dt = 0.1f;}						

			if(t-dt<0 && t>=0)for(int i=0;i<visibleObjects.size();i++)visibleObjects.get(i).setVisible((byte) 1);				
						
			progressBar.setValue((int)t);
			progressBar.update(progressBar.getGraphics());
			view.update(view.getGraphics());						
			if(Animation){view.makeScreenShot(new File(String.format("%s_%04d.png", filePath, pictureIndex)) );	pictureIndex++;}
		}						
		Style.clearStyleForEventCreation(jfrog.Common.styles);		
		dialog.setVisible(false);		
				
		jfrog.Common.animator.resume();
		return;
	}	
	
	
	static void AnimationMiddle(ActionEvent e){
		jfrog.view.View3D view = (jfrog.view.View3D)jfrog.Common.tab.getSelectedComponent();
		if(view==null)return;

		boolean Animation = false;					
		int option = JOptionPane.showOptionDialog(null, "Would you like to produce the Animation now ?",
				"What to do ?", 
				JOptionPane.YES_NO_OPTION,
				JOptionPane.YES_NO_OPTION, 
				null,
				new Object[]{"Animation", "Rehearsal"}, JOptionPane.DEFAULT_OPTION);

		if(option==JOptionPane.CLOSED_OPTION)return;					
		if(option==JOptionPane.YES_OPTION)Animation = true;

		String filePath = "screenshot";

		JDialog dialog = new JDialog(jfrog.Common.mainFrame, "Animator Progress");
		dialog.setSize(300, 100);
		JProgressBar progressBar = new JProgressBar(0, 100);
		progressBar.setBorderPainted(true);
		progressBar.setStringPainted(true);
		dialog.add(progressBar);
		dialog.setVisible(true);
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getComponent().setVisible(false);
			}
		});	    	

		jfrog.Common.show_FPS = false;
		view.overlay.refresh();
		view.rotating = false;
		jfrog.Common.animator.pause();
		geomFindAndSetVisible(Id_DT, 1);
		geomFindAndSetVisible(Id_CSC, 1);
		
		ArrayList<Base> visibleObjects = new ArrayList<Base>();
		jfrog.Common.events.fillVisibleObject(visibleObjects);
		jfrog.Common.events.setVisible((byte) 0);

		BaseColl_Name beams = new BaseColl_Name();
		beams.id = 5500;
		beams.name = "Beams";
		jfrog.Common.geom.addDaughter(beams);
		Event_Beam beam1 = new Event_Beam();
		beam1.id = 5501;
		beam1.pz = 1;
		beam1.sx = 0.2f;
		beam1.sy = 0.2f;
		beam1.sz = 5.0f;
		beam1.N  = 1000;
		Event_Beam beam2 = (Event_Beam)beam1.clone();
		beam2.id = 5502;
		beam2.pz = -1;
		beams.addDaughter(beam1);
		beams.addDaughter(beam2);					
		Style beamStyle = new Style();
		beamStyle.isGeom = true;
		beamStyle.color[0] = 0.0f;
		beamStyle.color[1] = 0.0f;					
		beamStyle.color[2] = 1.0f;
		beamStyle.color[3] = 1.0f;
		beams.setStyle(beamStyle, true, true);
		beams.setVisible((byte)1);

		Camera.keyFrames cameraFrames = view.cam.new keyFrames();
		cameraFrames.addKey(-40, 0,0,0, 2.0707f,0.08f,3500);
		cameraFrames.addKey(-15, 0,0,0, 1.5707f,0.04f, 350);
		cameraFrames.addKey( 10, 0,0,0, 0.0000f,0.00f, 350);										
		cameraFrames.addKey( 35, 0,0,0,-1.5007f,0.04f, 350);					
		cameraFrames.addKey( 60, 0,0,0,-3.1400f,0.08f, 600);					
		cameraFrames.addKey( 85, 0,0,0,-4.2100f,0.12f, 800);		
		cameraFrames.addKey(105, 0,0,0,-6.2800f,0.25f,1400);	
			
				
		progressBar.setMinimum((int)cameraFrames.getStartTime());
		progressBar.setMaximum((int)cameraFrames.getEndTime());
		progressBar.setValue(progressBar.getMinimum());

		Style.setStyleForEventCreation(jfrog.Common.styles);		
		Style.cleanStyleForFading(jfrog.Common.styles);
		geomFindAndFade(Id_DT,   25, 1.0f);
		geomFindAndFade(Id_DT,   30, 0.0f);		
				
		geomFindAndFade(Id_CSC,  25, 1.0f);
		geomFindAndFade(Id_CSC,  30, 0.0f);
		
		
		int pictureIndex = 0;
		float dt = 1.0f;
		for(float t=cameraFrames.getStartTime(); t<cameraFrames.getEndTime() && dialog.isVisible();t+=dt){
			jfrog.Common.time = t;
			
			cameraFrames.moveCamera(view.cam);
			Style.updateStyleForFading(jfrog.Common.styles);						
			if(t<-2 || t>5){dt = 0.5f;}else{dt = 0.15f;}						

			if(t-dt<0 && t>=0)for(int i=0;i<visibleObjects.size();i++)visibleObjects.get(i).setVisible((byte) 1);				
						
			progressBar.setValue((int)t);
			progressBar.update(progressBar.getGraphics());
			view.update(view.getGraphics());						
			if(Animation){view.makeScreenShot(new File(String.format("%s_%04d.png", filePath, pictureIndex)) );	pictureIndex++;}
		}						
		Style.clearStyleForEventCreation(jfrog.Common.styles);		
		dialog.setVisible(false);		
				
		jfrog.Common.animator.resume();
		return;
	}
	
	
	static void AnimationFixed(ActionEvent e){
		jfrog.view.View3D view = (jfrog.view.View3D)jfrog.Common.tab.getSelectedComponent();
		if(view==null)return;

		boolean Animation = false;					
		int option = JOptionPane.showOptionDialog(null, "Would you like to produce the Animation now ?",
				"What to do ?", 
				JOptionPane.YES_NO_OPTION,
				JOptionPane.YES_NO_OPTION, 
				null,
				new Object[]{"Animation", "Rehearsal"}, JOptionPane.DEFAULT_OPTION);

		if(option==JOptionPane.CLOSED_OPTION)return;					
		if(option==JOptionPane.YES_OPTION)Animation = true;

		String filePath = "screenshot";

		JDialog dialog = new JDialog(jfrog.Common.mainFrame, "Animator Progress");
		dialog.setSize(300, 100);
		JProgressBar progressBar = new JProgressBar(0, 100);
		progressBar.setBorderPainted(true);
		progressBar.setStringPainted(true);
		dialog.add(progressBar);
		dialog.setVisible(true);
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getComponent().setVisible(false);
			}
		});	    	

		jfrog.Common.show_FPS = false;
		view.overlay.refresh();
		view.rotating = false;
		jfrog.Common.animator.pause();
		geomFindAndSetVisible((long)12211000, 1);
		geomFindAndSetVisible((long)12221000, 1);
		geomFindAndSetVisible((long)12110400, 1);
		geomFindAndSetVisible((long)12120400, 1);
		geomFindAndSetVisible((long)12130400, 1);
		geomFindAndSetVisible((long)12140400, 1);
		geomFindAndSetVisible((long)12150400, 1);
				
		ArrayList<Base> visibleObjects = new ArrayList<Base>();
		jfrog.Common.events.fillVisibleObject(visibleObjects);
		jfrog.Common.events.setVisible((byte) 0);

		BaseColl_Name beams = new BaseColl_Name();
		beams.id = 5500;
		beams.name = "Beams";
		jfrog.Common.geom.addDaughter(beams);
		Event_Beam beam1 = new Event_Beam();
		beam1.id = 5501;
		beam1.pz = 1;
		beam1.sx = 0.2f;
		beam1.sy = 0.2f;
		beam1.sz = 5.0f;
		beam1.N  = 1000;
		Event_Beam beam2 = (Event_Beam)beam1.clone();
		beam2.id = 5502;
		beam2.pz = -1;
		beams.addDaughter(beam1);
		beams.addDaughter(beam2);					
		Style beamStyle = new Style();
		beamStyle.isGeom = true;
		beamStyle.color[0] = 0.0f;
		beamStyle.color[1] = 0.0f;					
		beamStyle.color[2] = 1.0f;
		beamStyle.color[3] = 1.0f;
		beams.setStyle(beamStyle, true, true);
		beams.setVisible((byte)1);

		Camera.keyFrames cameraFrames = view.cam.new keyFrames();
		cameraFrames.addKey(-60, 0,0,0, 2.6965f,0.002f,2200);
		cameraFrames.addKey( 90, 0,0,0, 2.6965f,0.002f,2200);
							
		progressBar.setMinimum((int)cameraFrames.getStartTime());
		progressBar.setMaximum((int)cameraFrames.getEndTime());
		progressBar.setValue(progressBar.getMinimum());

		Style.setStyleForEventCreation(jfrog.Common.styles);		
		Style.cleanStyleForFading(jfrog.Common.styles);
		geomFindAndFade(Id_DT,   -60, 0.1f);
		geomFindAndFade(Id_DT,    90, 0.1f);		
				
		geomFindAndFade(Id_CSC,  -60, 0.1f);
		geomFindAndFade(Id_CSC,   90, 0.1f);
		
		
		int pictureIndex = 0;
		float dt = 1.0f;
		for(float t=cameraFrames.getStartTime(); t<cameraFrames.getEndTime() && dialog.isVisible();t+=dt){
			jfrog.Common.time = t;
			
			cameraFrames.moveCamera(view.cam);
			Style.updateStyleForFading(jfrog.Common.styles);						
			if(t<-1 || t>10){dt = 1.0f;}else{dt = 0.15f;}						

			if(t-dt<0 && t>=0)for(int i=0;i<visibleObjects.size();i++)visibleObjects.get(i).setVisible((byte) 1);				
						
			progressBar.setValue((int)t);
			progressBar.update(progressBar.getGraphics());
			view.update(view.getGraphics());						
			if(Animation){view.makeScreenShot(new File(String.format("%s_%04d.png", filePath, pictureIndex)) );	pictureIndex++;}
		}						
		Style.clearStyleForEventCreation(jfrog.Common.styles);		
		dialog.setVisible(false);		
				
		jfrog.Common.animator.resume();
		return;
	}
	
	
	
	static void AnimationGeometry(ActionEvent e){
		jfrog.view.View3D view = (jfrog.view.View3D)jfrog.Common.tab.getSelectedComponent();
		if(view==null)return;

		boolean Animation = false;					
		int option = JOptionPane.showOptionDialog(null, "Would you like to produce the Animation now ?",
				"What to do ?", 
				JOptionPane.YES_NO_OPTION,
				JOptionPane.YES_NO_OPTION, 
				null,
				new Object[]{"Animation", "Rehearsal"}, JOptionPane.DEFAULT_OPTION);

		if(option==JOptionPane.CLOSED_OPTION)return;					
		if(option==JOptionPane.YES_OPTION)Animation = true;

		String filePath = "screenshot";

		JDialog dialog = new JDialog(jfrog.Common.mainFrame, "Animator Progress");
		dialog.setSize(300, 100);
		JProgressBar progressBar = new JProgressBar(0, 100);
		progressBar.setBorderPainted(true);
		progressBar.setStringPainted(true);
		dialog.add(progressBar);
		dialog.setVisible(true);
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getComponent().setVisible(false);
			}
		});	    	

		jfrog.Common.show_FPS = false;
		view.overlay.refresh();
		view.rotating = false;
		jfrog.Common.animator.pause();
		jfrog.Common.geom.setVisible((byte)0);
		jfrog.Common.events.setVisible((byte) 0);

		Camera.keyFrames cameraFrames = view.cam.new keyFrames();
		cameraFrames.addKey(  0, 0,0,0, 1.5080f,0.000f,  60);
		cameraFrames.addKey(140, 0,0,0, 0.5300f,0.146f, 850);
		cameraFrames.addKey(320, 0,0,0,-0.8300f,0.225f,2750);		
		cameraFrames.addKey(450, 0,0,0,-3.1415f,0.050f,2750);		
				
		progressBar.setMinimum((int)cameraFrames.getStartTime());
		progressBar.setMaximum((int)cameraFrames.getEndTime());
		progressBar.setValue(progressBar.getMinimum());

		Style.setStyleForEventCreation(jfrog.Common.styles);		
		Style.cleanStyleForFading(jfrog.Common.styles);
		geomFindAndFade(11110000,    5, 0.0f);
		geomFindAndFade(11110000,   10, 1.0f);	
		//"Tracker Pixel Barrel (PIB): Layer 1\nCMS Contains ~65M Silicon Pixels"
				
		geomFindAndFade(11120000,   10, 0.0f);
		geomFindAndFade(11120000,   15, 1.0f);
		//"Tracker Pixel Barrel (PIB): Layer 2\nCMS Contains ~65M Silicon Pixels"
		
		geomFindAndFade(11130000,   15, 0.0f);
		geomFindAndFade(11130000,   20, 1.0f);
		//"Tracker Pixel Barrel (PIB): Layer 3\nCMS Contains ~65M Silicon Pixels"		
		
		geomFindAndFade(11211000,   20, 0.0f);
		geomFindAndFade(11211000,   25, 1.0f);
		geomFindAndFade(11221000,   20, 0.0f);		
		geomFindAndFade(11221000,   25, 1.0f);
		//"Tracker Pixel Endcaps (PIE): Disk 1\nCMS Contains ~65M Silicon Pixels"
		
		geomFindAndFade(11212000,   25, 0.0f);
		geomFindAndFade(11212000,   30, 1.0f);
		geomFindAndFade(11222000,   25, 0.0f);
		geomFindAndFade(11222000,   30, 1.0f);		
		//"Tracker Pixel Endcaps (PIE): Disk 2\nCMS Contains ~65M Silicon Pixels"		
				
		geomFindAndFade(11310000,   30, 0.0f);
		geomFindAndFade(11310000,   35, 1.0f);
		//"Tracker Inner Barrel (TIB): Layer 1\nCMS Contains ~10M Silicon Strips"		
		
		geomFindAndFade(11320000,   35, 0.0f);
		geomFindAndFade(11320000,   40, 1.0f);
		//"Tracker Inner Barrel (TIB): Layer 2\nCMS Contains ~10M Silicon Strips"
		
		geomFindAndFade(11330000,   40, 0.0f);
		geomFindAndFade(11330000,   45, 1.0f);
		//"Tracker Inner Barrel (TIB): Layer 3\nCMS Contains ~10M Silicon Strips"
		
		geomFindAndFade(11340000,   45, 0.0f);
		geomFindAndFade(11340000,   50, 1.0f);
		//"Tracker Inner Barrel (TIB): Layer 4\nCMS Contains ~10M Silicon Strips"		
		
		geomFindAndFade(11411000,   50, 0.0f);
		geomFindAndFade(11411000,   55, 1.0f);
		geomFindAndFade(11421000,   50, 0.0f);
		geomFindAndFade(11421000,   55, 1.0f);		
		//"Tracker Inner Disks (TID): Disk 1\nCMS Contains ~10M Silicon Strips"		
		
		geomFindAndFade(11412000,   55, 0.0f);
		geomFindAndFade(11412000,   60, 1.0f);
		geomFindAndFade(11422000,   55, 0.0f);
		geomFindAndFade(11422000,   60, 1.0f);		
		//"Tracker Inner Disks (TID): Disk 2\nCMS Contains ~10M Silicon Strips"
		
		geomFindAndFade(11413000,   60, 0.0f);
		geomFindAndFade(11413000,   65, 1.0f);
		geomFindAndFade(11423000,   60, 0.0f);
		geomFindAndFade(11423000,   65, 1.0f);		
		//"Tracker Inner Disks (TID): Disk 3\nCMS Contains ~10M Silicon Strips"	
		
		
		geomFindAndFade(11510000,   65, 0.0f);
		geomFindAndFade(11510000,   70, 1.0f);
		//"Tracker Outer Barrel (TOB): Layer 1\nCMS Contains ~10M Silicon Strips"		

		geomFindAndFade(11520000,   70, 0.0f);
		geomFindAndFade(11520000,   75, 1.0f);
		//"Tracker Outer Barrel (TOB): Layer 2\nCMS Contains ~10M Silicon Strips"
		
		geomFindAndFade(11530000,   75, 0.0f);
		geomFindAndFade(11530000,   80, 1.0f);
		//"Tracker Outer Barrel (TOB): Layer 3\nCMS Contains ~10M Silicon Strips"
		
		geomFindAndFade(11540000,   80, 0.0f);
		geomFindAndFade(11540000,   85, 1.0f);
		//"Tracker Outer Barrel (TOB): Layer 4\nCMS Contains ~10M Silicon Strips"
		
		geomFindAndFade(11550000,   85, 0.0f);
		geomFindAndFade(11550000,   90, 1.0f);
		//"Tracker Outer Barrel (TOB): Layer 5\nCMS Contains ~10M Silicon Strips"
		
		geomFindAndFade(11560000,   90, 0.0f);
		geomFindAndFade(11560000,   95, 1.0f);
		//"Tracker Outer Barrel (TOB): Layer 6\nCMS Contains ~10M Silicon Strips"		
		
		geomFindAndFade(11611000,   95, 0.0f);
		geomFindAndFade(11611000,  100, 1.0f);
		geomFindAndFade(11621000,   95, 0.0f);
		geomFindAndFade(11621000,  100, 1.0f);		
		//"Tracker EndCaps (TEC): Disk 1\nCMS Contains ~10M Silicon Strips"		
		
		geomFindAndFade(11612000,  100, 0.0f);
		geomFindAndFade(11612000,  105, 1.0f);
		geomFindAndFade(11622000,  100, 0.0f);
		geomFindAndFade(11622000,  105, 1.0f);		
		//"Tracker EndCaps (TEC): Disk 2\nCMS Contains ~10M Silicon Strips"
		
		geomFindAndFade(11613000,  105, 0.0f);
		geomFindAndFade(11613000,  110, 1.0f);
		geomFindAndFade(11623000,  105, 0.0f);
		geomFindAndFade(11623000,  110, 1.0f);		
		//"Tracker EndCaps (TEC): Disk 3\nCMS Contains ~10M Silicon Strips"

		geomFindAndFade(11614000,  110, 0.0f);
		geomFindAndFade(11614000,  115, 1.0f);
		geomFindAndFade(11624000,  110, 0.0f);
		geomFindAndFade(11624000,  115, 1.0f);		
		//"Tracker EndCaps (TEC): Disk 4\nCMS Contains ~10M Silicon Strips"

		geomFindAndFade(11615000,  115, 0.0f);
		geomFindAndFade(11615000,  120, 1.0f);
		geomFindAndFade(11625000,  115, 0.0f);
		geomFindAndFade(11625000,  120, 1.0f);		
		//"Tracker EndCaps (TEC): Disk 5\nCMS Contains ~10M Silicon Strips"
		
		geomFindAndFade(11616000,  120, 0.0f);
		geomFindAndFade(11616000,  125, 1.0f);
		geomFindAndFade(11626000,  120, 0.0f);
		geomFindAndFade(11626000,  125, 1.0f);		
		//"Tracker EndCaps (TEC): Disk 6\nCMS Contains ~10M Silicon Strips"
		
		geomFindAndFade(11617000,  125, 0.0f);
		geomFindAndFade(11617000,  130, 1.0f);
		geomFindAndFade(11627000,  125, 0.0f);
		geomFindAndFade(11627000,  130, 1.0f);		
		//"Tracker EndCaps (TEC): Disk 7\nCMS Contains ~10M Silicon Strips"
		
		geomFindAndFade(11618000,  130, 0.0f);
		geomFindAndFade(11618000,  135, 1.0f);
		geomFindAndFade(11628000,  130, 0.0f);
		geomFindAndFade(11628000,  135, 1.0f);		
		//"Tracker EndCaps (TEC): Disk 8\nCMS Contains ~10M Silicon Strips"
		
		geomFindAndFade(11619000,  135, 0.0f);
		geomFindAndFade(11619000,  140, 1.0f);
		geomFindAndFade(11629000,  135, 0.0f);
		geomFindAndFade(11629000,  140, 1.0f);		
		//"Tracker EndCaps (TEC): Disk 9\nCMS Contains ~10M Silicon Strips"
		
		geomFindAndFade(13311000,  160, 0.0f);
		geomFindAndFade(13311000,  165, 1.0f);		
		geomFindAndFade(13321000,  160, 0.0f);
		geomFindAndFade(13321000,  165, 1.0f);		
		//"Electromagnetic Preshower (ES): Plane 1"		
		
		geomFindAndFade(13312000,  170, 0.0f);
		geomFindAndFade(13312000,  175, 1.0f);		
		geomFindAndFade(13322000,  170, 0.0f);
		geomFindAndFade(13322000,  175, 1.0f);		
		//"Electromagnetic Preshower (ES): Plane 2"		
		
		geomFindAndFade(13100000,  180, 0.0f);
		geomFindAndFade(13100000,  185, 1.0f);		
		//"Electromagnetic Barrel Calorimeter (EB)\n~66,000 PbWo4 cystals"		
		
		geomFindAndFade(13200000,  190, 0.0f);
		geomFindAndFade(13200000,  195, 1.0f);		
		//"Electromagnetic Endcap Calorimeter (EE)\n~15,000 PbWo4 cystals"		
		
		geomFindAndFade(14200000,  200, 0.0f);
		geomFindAndFade(14200000,  205, 1.0f);		
		//"Hadronic Endcap Calorimeter (HE)"
		

		geomFindAndFade(14100000,  210, 0.0f);
		geomFindAndFade(14100000,  215, 1.0f);		
		//"Hadronic Barrel Calorimeter (HB)"
		
		geomFindAndFade(16000000,  240, 0.0f);
		geomFindAndFade(16000000,  241, 1.0f);	//bug with transparency for magnet		
		//"Supraconducting Solenoid Magnet\n4Tesla Magnetic Field"		
		
		geomFindAndFade(14300000,  260, 0.0f);
		geomFindAndFade(14300000,  265, 1.0f);		
		//"Hadronic Outer Calorimeter (HO)"		
		
		geomFindAndFade(12110000,  280, 0.0f);
		geomFindAndFade(12110000,  285, 1.0f);		
		//"Muon System, Drift Tube Chamber (DT): Wheel 1"
		
		geomFindAndFade(12120000,  285, 0.0f);
		geomFindAndFade(12120000,  290, 1.0f);		
		//"Muon System, Drift Tube Chamber (DT): Wheel 2"
		
		geomFindAndFade(12130000,  290, 0.0f);
		geomFindAndFade(12130000,  295, 1.0f);		
		//"Muon System, Drift Tube Chamber (DT): Wheel 3"
		
		geomFindAndFade(12140000,  295, 0.0f);
		geomFindAndFade(12140000,  300, 1.0f);		
		//"Muon System, Drift Tube Chamber (DT): Wheel 4"
		
		geomFindAndFade(12150000,  300, 0.0f);
		geomFindAndFade(12150000,  305, 1.0f);		
		//"Muon System, Drift Tube Chamber (DT): Wheel 5"		
		
		geomFindAndFade(12211000,  340, 0.0f);
		geomFindAndFade(12211000,  345, 1.0f);		
		geomFindAndFade(12221000,  340, 0.0f);
		geomFindAndFade(12221000,  345, 1.0f);		
		//"Muon System, Cathod Strip Chamber (CSC): Disk 1"
		
		geomFindAndFade(12212000,  350, 0.0f);
		geomFindAndFade(12212000,  355, 1.0f);		
		geomFindAndFade(12222000,  350, 0.0f);
		geomFindAndFade(12222000,  355, 1.0f);		
		//"Muon System, Cathod Strip Chamber (CSC): Disk 2"
		
		geomFindAndFade(12213000,  360, 0.0f);
		geomFindAndFade(12213000,  365, 1.0f);		
		geomFindAndFade(12223000,  360, 0.0f);
		geomFindAndFade(12223000,  365, 1.0f);		
		//"Muon System, Cathod Strip Chamber (CSC): Disk 3"		
				
		geomFindAndFade(12214000,  370, 0.0f);
		geomFindAndFade(12214000,  375, 1.0f);		
		geomFindAndFade(12224000,  370, 0.0f);
		geomFindAndFade(12224000,  375, 1.0f);		
		//"Muon System, Cathod Strip Chamber (CSC): Disk 4"		
		
		geomFindAndFade(12300000,  390, 0.0f);
		geomFindAndFade(12300000,  395, 1.0f);		
		//"Muon System, Resistive Plate Chamber (RPC)\nInterleaved between DT and CSC"		
		
		geomFindAndFade(14400000,  410, 0.0f);
		geomFindAndFade(14400000,  415, 1.0f);		
		//"Hadronic Forward Calorimeter (HF)"		
				
		//430
		//"The Compact Muon Solenoid (CMS)"
			
		int pictureIndex = 0;
		float dt = 0.3f;
		for(float t=cameraFrames.getStartTime(); t<cameraFrames.getEndTime() && dialog.isVisible();t+=dt){
			jfrog.Common.time = t;
			
			cameraFrames.moveCamera(view.cam);
			Style.updateStyleForFading(jfrog.Common.styles);						
														
			if(t-dt<200 && t>=200)geomFindAndSetVisible(11000000, (byte)0);
			if(t-dt<220 && t>=220)geomFindAndSetVisible(13000000, (byte)0);			
			
			progressBar.setValue((int)t);
			progressBar.update(progressBar.getGraphics());
			view.update(view.getGraphics());						
			if(Animation){view.makeScreenShot(new File(String.format("%s_%04d.png", filePath, pictureIndex)) );	pictureIndex++;}
		}						
		Style.clearStyleForEventCreation(jfrog.Common.styles);
		jfrog.Common.geom.setVisible((byte)0);
		dialog.setVisible(false);		
				
		jfrog.Common.animator.resume();
		return;
	}	
		
	static void geomFindAndSetVisible(long id, int state){
		ArrayList<Base> objs = jfrog.Common.getObjectsWidthId((long)(id), jfrog.Common.geomIdMap);
		if(objs==null)return;
		for(int j=0;j<objs.size();j++){
			Base obj = objs.get(j);
			obj.setVisible((byte)state);
		}
	}	
	
	static void geomFindAndFade(long id, float endTime, float endAlpha){
		ArrayList<Base> objs = jfrog.Common.getObjectsWidthId((long)(id), jfrog.Common.geomIdMap);
		if(objs==null)return;
		for(int j=0;j<objs.size();j++){
			Base obj = objs.get(j);
			if(obj.isVisible()==0)obj.setVisible((byte)1);
			if(obj.isStyleFromParent()){
				obj.getStyle().objList.remove(obj);				
				Style clonedStyle = (Style)obj.getStyle().clone();
				clonedStyle.objList.clear();
				clonedStyle.objList.add(obj);
				jfrog.Common.styles.add(clonedStyle);
				obj.setStyle(clonedStyle);				
			}					
			obj.getStyle().fadingKeys.addKey(endTime, endAlpha);
		}
	}		
		
	
}
