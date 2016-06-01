package jfrog;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

public class MenuBar extends JMenuBar{
	private static final long serialVersionUID = -9175970792244291530L;

	public MenuBar(){		
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F10"), "none"); 
		
		JMenu menu = new JMenu("File");
		this.add(menu);		
		menu.setMnemonic(KeyEvent.VK_F);
		
		//a group of JMenuItems
		JMenuItem menuItem = new JMenuItem("Exit",    KeyEvent.VK_E);
		menu.add(menuItem);		
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
		menuItem.setToolTipText("Exit Frog");		
		menuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			System.exit(0);										
		}});
			
		
		menu = new JMenu("Views");
		this.add(menu);		
		menu.setMnemonic(KeyEvent.VK_V);
		
		
		menuItem = new JMenuItem("Tree",    KeyEvent.VK_T);
		menu.add(menuItem);		
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		menuItem.setToolTipText("Open a browsable tree of the detector geometry and event content");
		menuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			if(jfrog.Common.treeMenu==null){					
				jfrog.Common.treeMenu = new jfrog.Tree(jfrog.Common.version + " TreeMenu");
			}else{
				jfrog.Common.treeMenu.setVisible(true);
				jfrog.Common.treeMenu.requestFocus();
			}										
		}});
		
		menuItem = new JMenuItem("Next Event",    KeyEvent.VK_N);
		menu.add(menuItem);		
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));
		menuItem.setToolTipText("Go to next event");
		menuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
				jfrog.Common.events.nextEvent();			
				for(int i=0;i<jfrog.Common.tab.getComponentCount();i++){
					jfrog.view.View view = (jfrog.view.View)jfrog.Common.tab.getComponent(i);
					if(view==null)continue;				
					if(view.overlay!=null)view.overlay.refresh();
				}
		}});
		
		menuItem = new JMenuItem("Previous Event",    KeyEvent.VK_P);
		menu.add(menuItem);		
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0));
		menuItem.setToolTipText("Go to previous event");
		menuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){					
				jfrog.Common.events.previousEvent();
				for(int i=0;i<jfrog.Common.tab.getComponentCount();i++){
					jfrog.view.View view = (jfrog.view.View)jfrog.Common.tab.getComponent(i);
					if(view==null)continue;				
					if(view.overlay!=null)view.overlay.refresh();
				}
		}});		
		
		menuItem = new JCheckBoxMenuItem("Rotation");
		menu.add(menuItem);		
		menuItem.setSelected(true);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
		menuItem.setToolTipText("Toogle the camera rotation from the current view (only apply to 3D views)");
		menuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			jfrog.view.View3D selView = (jfrog.view.View3D)jfrog.Common.tab.getSelectedComponent();
			if(selView!=null)selView.rotating = !selView.rotating;			
		}});		
		
		menuItem = new JCheckBoxMenuItem("Wireframe");
		menu.add(menuItem);			
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0));		
		menuItem.setToolTipText("switch wireframe display");		
		menuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){					
			jfrog.view.View selView = (jfrog.view.View)jfrog.Common.tab.getSelectedComponent();
			if(selView!=null)selView.wireframe = !selView.wireframe; 			
		}});	

		menuItem = new JCheckBoxMenuItem("Open Detector");
		menu.add(menuItem);			
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, 0));		
		menuItem.setToolTipText("Open detector display (only apply to 3D views)");
		menuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){					
			jfrog.view.View3D selView = (jfrog.view.View3D)jfrog.Common.tab.getSelectedComponent();
			if(selView!=null)selView.openned = !selView.openned; 			
		}});			
		
		
		menuItem = new JCheckBoxMenuItem("3D-Stereoscopic");
		menu.add(menuItem);		
		menuItem.setToolTipText("switch 3D-stereoscopic display");
		menuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){					
			jfrog.view.View selView = (jfrog.view.View)jfrog.Common.tab.getSelectedComponent();
			if(selView!=null)selView.stereo = !selView.stereo; 
			if(!selView.stereo && selView.stereo_Anaglyph)selView.stereo_Anaglyph = false;			
		}});			
		
		menuItem = new JCheckBoxMenuItem("3D-Anaglyph");
		menu.add(menuItem);		
		menuItem.setToolTipText("switch 3D-Anaglyph (Red/Cyan) display");
		menuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){					
			jfrog.view.View selView = (jfrog.view.View)jfrog.Common.tab.getSelectedComponent();
			if(selView!=null){
				selView.stereo_Anaglyph = !selView.stereo_Anaglyph;
				if(selView.stereo_Anaglyph && !selView.stereo)selView.stereo = true;
			}			
		}});		

		menuItem = new JCheckBoxMenuItem("3D-Interleaved");
		menu.add(menuItem);		
		menuItem.setToolTipText("switch 3D-Interleaved (poralized) display");
		menuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){					
			jfrog.view.View selView = (jfrog.view.View)jfrog.Common.tab.getSelectedComponent();
			if(selView!=null){
				//selView.interleavedStereo(gl, !selView.stereo_Interleaved);
				selView.stereo_Interleaved = !selView.stereo_Interleaved;
				if(selView.stereo_Interleaved && !selView.stereo)selView.stereo = true;
			}			
		}});				
		
		
				
		
		menu = new JMenu("Utils");
		this.add(menu);		
		menu.setMnemonic(KeyEvent.VK_U);
		
				
		menuItem = new JMenuItem("Take Screenshot",    KeyEvent.VK_S);
		menu.add(menuItem);		
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		menuItem.setToolTipText("Take a screenshot of the current view");
		menuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			  JFileChooser c = new JFileChooser();				  
			  c.setFileFilter(new ImageFileFilter());
		      int rVal = c.showSaveDialog(null); 			  			  
		      if (rVal == JFileChooser.APPROVE_OPTION) {
					jfrog.view.View selView = (jfrog.view.View)jfrog.Common.tab.getSelectedComponent();
					if(selView!=null)selView.makeScreenShot(c.getSelectedFile());
		      }
		}});
				
		
		menuItem = new JMenuItem("Reload Shaders");
		menu.add(menuItem);		
		menuItem.setSelected(true);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
		menuItem.setToolTipText("Reload the shaders associated to the active view");
		menuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			jfrog.view.View selView = (jfrog.view.View)jfrog.Common.tab.getSelectedComponent();
			if(selView!=null)selView.loadShaders(null, null, null, true);			
		}});
		
		
		menuItem = new JMenuItem("ShaderEditor");
		menu.add(menuItem);		
		menuItem.setSelected(true);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
		menuItem.setToolTipText("Open a shader editor window");
		menuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			jfrog.view.View selView = (jfrog.view.View)jfrog.Common.tab.getSelectedComponent();
			if(selView==null)return;			
						
			JDialog dialog = new JDialog();			
	    	dialog.setSize(500, 500);
	    	
	    	JTabbedPane tab = new JTabbedPane();
	    	dialog.add(tab);
	    		   	    		    		    	
	    	JTextArea vertTextArea = new JTextArea(Shader.loadSource(selView.vertexShader));
	    	JScrollPane vertScrollPane = new JScrollPane(vertTextArea); 
	    	vertTextArea.setTabSize(3);
	    	tab.addTab("Vertex Shader", vertScrollPane);
	    	
	    	JTextArea fragTextArea = new JTextArea(Shader.loadSource(selView.fragmentShader));
	    	JScrollPane fragScrollPane = new JScrollPane(fragTextArea); 
	    	fragTextArea.setTabSize(3);
	    	tab.addTab("Fragment Shader", fragScrollPane);	    	
	    	
	    	JButton b = new JButton("Reload Shaders");
	    	b.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
	    		JTabbedPane tab = (JTabbedPane) (((JButton)(e.getSource())).getParent().getComponent(0));
	    		String vertexShader = ((JTextArea)((JViewport)((JScrollPane)tab.getComponent(0)).getComponent(0)).getView()).getText();
	    		String fragShader   = ((JTextArea)((JViewport)((JScrollPane)tab.getComponent(1)).getComponent(0)).getView()).getText();	    		
				jfrog.view.View selView = (jfrog.view.View)jfrog.Common.tab.getSelectedComponent();
				if(selView!=null)selView.loadShaders(null, vertexShader, fragShader, false);	    		
	    	}});
	    	dialog.add(b, BorderLayout.SOUTH);

	    	
	    	dialog.setVisible(true);
		}});
			
		
		menuItem = new JMenuItem("Time Line");
		menu.add(menuItem);		
		menuItem.setSelected(true);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0));
		menuItem.setToolTipText("Open Time Line Window");
		menuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			JDialog dialog = new JDialog((Frame)(null), "Time Setter");			
	    	dialog.setSize(500, 100);			
	    	
	    	JSlider timeScale = new JSlider(JSlider.HORIZONTAL,-500, 500, (int)jfrog.Common.time);
	    	dialog.add(timeScale);
	    	timeScale.setMajorTickSpacing(100);
	    	timeScale.setMinorTickSpacing(25);	    	
	    	timeScale.setPaintTicks(true);
	    	timeScale.setPaintLabels(true);

	    	timeScale.addChangeListener(new ChangeListener() {public void stateChanged(ChangeEvent e){
	    		jfrog.Common.time = ((JSlider)e.getSource()).getValue()/10.0f;
	    		Style.updateStyleForFading(jfrog.Common.styles);
	    	}});
	    	dialog.setVisible(true);
			
//			System.out.printf("time = %f", jfrog.Common.time);
//			jfrog.Common.time = 0.0f;
//			System.out.printf("--> %f\n", jfrog.Common.time);
		}});		
		
		
		menu = new JMenu("Help");
		this.add(Box.createHorizontalGlue());		
		this.add(menu);		
		menu.setMnemonic(KeyEvent.VK_H);

				
		menuItem = new JMenuItem("debug",    KeyEvent.VK_D);
		menu.add(menuItem);		
		menuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			jfrog.Common.support_PS = !jfrog.Common.support_PS; 
		}});		
		
				
		menuItem = new JMenuItem("About Frog",    KeyEvent.VK_A);
		menu.add(menuItem);		
		menuItem.setToolTipText("open Frog website");
		menuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			try {
				URI uri = new URI( "http://projects.hepforge.org/frog/" );
		        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
		        desktop.browse( uri );			
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}			
		}});			


	}

	





	public class ImageFileFilter extends FileFilter
	{
		public boolean accept(File f){
			if(f.isDirectory())return true;
			String [] supportedTypes = ImageIO.getWriterFileSuffixes();
			for(int i=0;i<supportedTypes.length;i++){
				if(f.getName().endsWith(supportedTypes[i]))return true;
			}return false;
		}

		public String getDescription(){
			String [] supportedTypes = ImageIO.getWriterFileSuffixes();
			String allInOne = "";
			for(int i=0;i<supportedTypes.length;i++){
				if(i!=0)allInOne += ", ";
				allInOne += "*." + supportedTypes[i];
			}						
			return "Image files (" + allInOne + ")";
		}
	}	
	
	
	public class FrogFileFilter extends FileFilter
	{
		public boolean accept(File f){
			if(f.isDirectory())return true;
			String [] supportedTypes = {"vis", "geom", "frog"};
			for(int i=0;i<supportedTypes.length;i++){
				if(f.getName().endsWith(supportedTypes[i]))return true;
			}return false;
		}

		public String getDescription(){
			String [] supportedTypes = {"vis", "geom", "frog"};
			String allInOne = "";
			for(int i=0;i<supportedTypes.length;i++){
				if(i!=0)allInOne += ", ";
				allInOne += "*." + supportedTypes[i];
			}						
			return "Frog files (" + allInOne + ")";
		}
	}		
	
	
	
}
