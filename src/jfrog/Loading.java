package jfrog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class Loading extends Frame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2808392875145418750L;
	float value = 0;
	String text = "Loading...";
	int maxValue = 0;	
	int currentValue = 0;
			
	final Font font_main  = new Font("Courier",Font.BOLD,18);
	final Font font_small = new Font("Courier",Font.PLAIN,12);

	public Loading(){    	
		super("Image Frame");
		MediaTracker mt = new MediaTracker(this);	 
		jfrog.Common.Logo_FrogTxt = Toolkit.getDefaultToolkit().getImage(jfrog.Common.GetURL("/rsc/Frog_LogoTxt.png"));
		mt.addImage(jfrog.Common.Logo_FrogTxt,0);
		jfrog.Common.Logo_Frog = Toolkit.getDefaultToolkit().getImage(jfrog.Common.GetURL("/rsc/Frog_Logo.png"));
		mt.addImage(jfrog.Common.Logo_Frog,1);		
			
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		
		setSize(620,440);
		setLocation((int)(screen.getWidth()/2-320),(int)(screen.getHeight()/2 - 200));
		//setAlwaysOnTop(true);
		setUndecorated(true);
		setVisible(true);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				dispose();	 		
			}
		});
		update(this.getGraphics());	
	}
	
	public void update(Graphics g){
		paint(g);
	}


	public void paint(Graphics g){
		g.clearRect(0, 0, getSize().width, getSize().height);		
		
		int W = 520;
		int H = 300;
		
		if(jfrog.Common.Logo_FrogTxt != null)g.drawImage(jfrog.Common.Logo_FrogTxt, 50, 40, W, H, this);
							
		g.setColor(new Color(160,192,0));
		g.drawRect(50, H+110, W,    20);
		g.fillRect(50, H+110, (int)(W*value), 20);

		g.setFont(font_main);
		g.drawString("FROG:", 50, H+35);		
		g.drawString("The Fast and Realistic OpenGl event displayer", 50, H+55);
				
		g.drawString(String.format("%2.0f%% : ", 100.0f*value) + text, 50, H+100);
		
		g.setColor(new Color(0,0,0));
		g.setFont(font_small);
		g.drawString(jfrog.Common.authorList, 5, 20);			
		g.drawString(jfrog.Common.version, W, 20);
	}

	public void setValue(float value, String text) {		
		this.value = value;
		this.text = text;
		update(this.getGraphics());	
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public int getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(int currentValue) {
		this.currentValue = currentValue;
		setValue(currentValue / (float) maxValue, "");
	}	
	


}
