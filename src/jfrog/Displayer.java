package jfrog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.jogamp.opengl.util.FPSAnimator;



public class Displayer {
	
    public Displayer(){   	    	
   	
    	long lStartTime = new Date().getTime();
        	
    	jfrog.Common.loading = new Loading(); 
    	jfrog.Common.loading.setValue(0.05f, "Loading Classes");    	
    	            	
    	jfrog.object.Factory factory = new jfrog.object.Factory(); 
    	factory.LoadClasses();
    	
    	jfrog.view.Factory.factory.LoadClasses();    	
    	
    	jfrog.Common.loading.setValue(0.10f, "Loading Configuration");
   		jfrog.Common.parser = new CardParser(jfrog.Common.File_Config);    	
    		    	
    	jfrog.Common.loading.setValue(0.20f, "Downloading Remote Files");
    	
    	HashMap<String,  jfrog.Downloader> downloaderMap = new HashMap<String, jfrog.Downloader >();
    	
    	String [] GeometryURL = jfrog.Common.parser.toStringArr("InputGeom");
    	for(int i=0;i<GeometryURL.length;i++){
    		downloaderMap.put(GeometryURL[i], new jfrog.Downloader(GeometryURL[i]));
    	}
    	String [] EventsURL   = jfrog.Common.parser.toStringArr("InputVisFile");
    	for(int i=0;i<EventsURL.length;i++){
        	System.out.printf("InputEvents = %s\n", EventsURL[i]);
    		downloaderMap.put(EventsURL[i], new jfrog.Downloader(EventsURL[i]));
    	}
    	    	
    	jfrog.Common.loading.setValue(0.40f, "Loading Geometry");
    	jfrog.Common.root.name = "FROG root";
    	
    	for(int i=0;i<GeometryURL.length;i++){
    		downloaderMap.get(GeometryURL[i]).waitUntilDownloaded();
			URL geomURL = downloaderMap.get(GeometryURL[i]).outurl;
			if(geomURL==null){
				System.out.println("File " + GeometryURL[i] + " not found " + downloaderMap.get(GeometryURL[i]).outurl.toString());
			}else{				
				if(GeometryURL.length<=1){
					jfrog.Common.geom = new jfrog.object.BaseColl(geomURL);    		
					jfrog.Common.root.addDaughter(jfrog.Common.geom);
				}else{
					if(jfrog.Common.geom==null){
						jfrog.Common.geom = new jfrog.object.BaseColl();
						jfrog.Common.geom.name="Geometry";
						jfrog.Common.geom.setStyle(new Style(), true, true);
						jfrog.Common.root.addDaughter(jfrog.Common.geom);
					}
					jfrog.Common.geom.addDaughter(new jfrog.object.BaseColl(geomURL));					
				}
			}
    	}
    	if(jfrog.Common.geom!=null)jfrog.Common.geom.fillMapOfId(jfrog.Common.geomIdMap);
    	
    	jfrog.Common.loading.setValue(0.50f, "Loading Events");

		jfrog.Common.events = new jfrog.object.Events();    		
		jfrog.Common.root.addDaughter(jfrog.Common.events);    	
    	for(int i=0;i<EventsURL.length;i++){
    		downloaderMap.get(EventsURL[i]).waitUntilDownloaded();    		
			URL eventsUrl = downloaderMap.get(EventsURL[i]).outurl;
			System.out.printf("File %d = %s\n", i, EventsURL[i]);
			if(eventsUrl==null){
				System.out.println("File " + EventsURL[i] + " not found " + downloaderMap.get(EventsURL[i]).outurl.toString());
			}else{			
				jfrog.Common.events.LoadFile(eventsUrl);
			}
    	}    	    	
    	jfrog.Common.events.loadEvent(0, -1);
   	
    	    	     	
    	jfrog.Common.loading.setValue(0.70f, "Loading Configuration");    	
    	jfrog.Common.parser.applyConfig(jfrog.Common.root);
    	//jfrog.Common.parser.GenerateConfig("output_test.txt");    	
    	    	   	    

    	jfrog.Common.loading.setValue(0.85f, "Loading the Window");        
    	JFrame frame = new JFrame(jfrog.Common.version);
    	jfrog.Common.mainFrame = frame;
    	frame.setSize(500, 500);
    	
    	
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();		
		frame.setSize((int)screen.getWidth()-300,(int)screen.getHeight()-50);
		frame.setLocation(0,0);    	
    	jfrog.Common.menuBar = new jfrog.MenuBar(); 
    	frame.setJMenuBar(jfrog.Common.menuBar);    	
    	//frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    	
		jfrog.Common.textBox = new JTextField("");
		frame.add(jfrog.Common.textBox,BorderLayout.SOUTH);    	

    	frame.addWindowListener(new WindowAdapter() {
    		public void windowClosing(WindowEvent e) {
    			//frame.dispose();
    			System.exit(0);
    		}
    	});
    	
    	
    	jfrog.Common.loading.setValue(0.95f, "Setting up OpenGL");            	
//    	String [] profiles = GLProfile.GL_PROFILE_LIST_ALL;
//    	for(int i=0;i<profiles.length;i++){System.out.printf("DEBUG: Profiles%d = %s\n", i, profiles[i]);}
    	
    	GLProfile glp = GLProfile.getDefault();
    	GLCapabilities caps = new GLCapabilities(glp);
    	caps.setDoubleBuffered(true);
    	//caps.setStereo(true);
    	caps.setStencilBits(8);
    	System.out.printf("%s\n", caps.toString());
//    	View v = new View(caps, null);    	
//    	frame.add(v);    	
    	
  	
    	    
    	jfrog.Common.tab = new JTabbedPane();
    	frame.add(jfrog.Common.tab);    	
    	jfrog.Common.tab.addKeyListener(new KeyAdapter(){
    		public void keyPressed(KeyEvent ke){
    			if(ke.getKeyCode() == KeyEvent.VK_LEFT || ke.getKeyCode()==KeyEvent.VK_RIGHT)
    				jfrog.Common.menuBar.dispatchEvent(ke);    		        
    		}    		
    	});
    	        
    	
        frame.setVisible(true);        
        GLCanvas canvas = new GLCanvas(caps);
        frame.add(canvas); //common cancas    	
    	    	    	
    	jfrog.Common.animator = new FPSAnimator((GLAutoDrawable)canvas, 60);
//    	jfrog.Common.animator = new TabbedFPSAnimator(jfrog.Common.tab, 60);
    	jfrog.Common.animator.start();
    	
    	jfrog.view.Factory.createViews(caps, canvas.getContext(), jfrog.Common.tab);    	
    	
    	new AnimatorCMS();
       	    	    	    	
    	jfrog.Common.loading.setValue(0.99f, "Loading the Tree Menu");    	    	
    	jfrog.Common.treeMenu = new jfrog.Tree(jfrog.Common.version + " TreeMenu");    	
      	    	
    	jfrog.Common.loading.setVisible(false);
    	jfrog.Common.loading = null;
    	       	    	
    	long lEndTime = new Date().getTime(); 
    	System.out.println("Elapsed milliseconds: " + (lEndTime-lStartTime)); 
    }   
        
    
    public static void argsParser(String[] args){
    	for(int i=0;i<args.length; i++){    		
        	System.out.printf("Arg[%d]='%s'\n", i, args[i]);        	
    		if(args[i].startsWith("-open") && i+1<args.length){
    			if(args[i+1].endsWith("vis") || args[i+1].endsWith("vis.gz")){
    				jfrog.Common.Text_Config += String.format("\nInputVisFile={%s}", args[i+1]);
    			}else if(args[i+1].endsWith("geom") || args[i+1].endsWith("geom.gz")){
    				jfrog.Common.Text_Config += String.format("\nInputGeom={%s}", args[i+1]);    				
    			}else{
//    				jfrog.Downloader downloader = new jfrog.Downloader(args[i+1]);
//    				downloader.waitUntilDownloaded();
//    				jfrog.Common.File_Config = downloader.outurl.toString();
    				jfrog.Common.File_Config = args[i+1];
    			}
    		}else if(args[i].startsWith("-append") && i+1<args.length){
    			jfrog.Common.Text_Config += String.format("\n%s", args[i+1]);    				
    		}    		
    	}
    }
    
        
    public static void main(String[] args) {
    	argsParser(args);
    	//argsParser(new String [] {"-open",  "http://test-carrillo.web.cern.ch/test-carrillo/frog/config.jfrog"});
    	new Displayer();       	
    }    
}


