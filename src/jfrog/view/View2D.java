package jfrog.view;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.GLContext;

public class View2D extends View  {
	private static final long serialVersionUID = -2623648136179071957L;
	
	float  sliceDepth = 2000; 
	double orthoXMin;
	double orthoXMax;
	double orthoYMin;
	double orthoYMax;
	double orthoZMin;
	double orthoZMax;
			   		
	public View2D(){}
	public View2D(GLCapabilitiesImmutable caps, GLContext context){
		super(caps, context);
		//jfrog.Common.view = this;			
	}
	
	public void ParseParameter(){		
		super.ParseParameter();
    	if(jfrog.Common.parser==null)return;
		String Tag;
		Tag = name + "_Slice_Depth"       ;if(jfrog.Common.parser.tagExist(Tag))sliceDepth = jfrog.Common.parser.toFloat(Tag);
	}	
		
	void ProjectionMatrix(GL2 gl){
		orthoXMin = -1*cam.R;
		orthoXMax =  1*cam.R;
		orthoYMin = orthoXMin * ((double)this.Height/this.Width);
		orthoYMax = orthoXMax * ((double)this.Height/this.Width);
		orthoZMin = cam.R - sliceDepth;
		orthoZMax = cam.R + sliceDepth;
		gl.glOrtho(orthoXMin,orthoXMax,orthoYMin,orthoYMax,orthoZMin,orthoZMax);
	}	
	
	
	public void displayScene(GL2 gl, boolean leftEye){
		jfrog.Common.clipping = false;
		
		gl.glMatrixMode (GL2.GL_MODELVIEW);
		gl.glLoadIdentity();			
		cam.LookAt(gl, leftEye);
					
		if(leftEye){			
			gl.glPolygonMode( GL2.GL_FRONT_AND_BACK, GL2.GL_LINE );
			drawBackground(gl);
			gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);
		}
		        						
        gl.glPolygonMode( GL2.GL_FRONT_AND_BACK, GL2.GL_FILL );
        
		jfrog.Style.drawAllObjects(gl, jfrog.Common.styles, shaderIds[0]);           		                   
	}	
	
	public void mouseWheelMoved(MouseWheelEvent arg0) {		
		if(arg0.getWheelRotation()>0){
			cam.R *= 1.02;
		}else if (arg0.getWheelRotation()<0){
			cam.R /= 1.02;
		}
	}
	
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		
		if(e.getButton()==1)Picking(e.getX(), e.getY());
	}

	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
	}

	public void mouseDragged(MouseEvent e) {			
		if(mouse_states[MouseEvent.BUTTON2] || mouse_states[MouseEvent.BUTTON3]){
			cam.Move(e.getX()-mouse_x, e.getY()-mouse_y);
		}		
		super.mouseDragged(e);
	}

	
	
}
