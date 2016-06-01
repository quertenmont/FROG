package jfrog.view;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.glu.GLU;

import jfrog.Shader;

public class ViewPicking extends View  {
	private static final long serialVersionUID = -2240353062198360779L;
	
	public boolean rotating = true;
	public float   dphi  =0.01f;
	public float   dtheta=0.0f;
			   	
	public ViewPicking(){}
	public ViewPicking(GLCapabilitiesImmutable caps, GLContext context){
		super(caps, context);			
	}
		
	public void ParseParameter(){		
		super.ParseParameter();
    	if(jfrog.Common.parser==null)return;
		String Tag;
		Tag = name + "_Animate_Rotate"    ;if(jfrog.Common.parser.tagExist(Tag))rotating = jfrog.Common.parser.toBoolean(Tag);
		Tag = name + "_Animate_dphi"      ;if(jfrog.Common.parser.tagExist(Tag))dphi     = jfrog.Common.parser.toFloat  (Tag);
		Tag = name + "_Animate_dtheta"    ;if(jfrog.Common.parser.tagExist(Tag))dtheta   = jfrog.Common.parser.toFloat  (Tag);		
	}
	
	void ProjectionMatrix(GL2 gl){
		GLU glu = GLU.createGLU(gl);
		glu.gluPerspective (45.0, Width/(float)Height, 1.0, 10000.0);
	}	
	
	
	public void displayScene(GL2 gl, boolean leftEye){
    	if(shaderIds[0]>0)Shader.sendUniform1i(gl, shaderIds[0], "pickingMode", 1);

		gl.glMatrixMode (GL2.GL_MODELVIEW);
		gl.glLoadIdentity();	
		gl.glDisable(GL2.GL_LIGHTING);
		cam.LookAt(gl, leftEye);

		jfrog.Common.geom.deepPickingDraw(gl);
		jfrog.Common.events.deepPickingDraw(gl);  
		
    	if(shaderIds[0]>0)Shader.sendUniform1i(gl, shaderIds[0], "pickingMode", 0);    	
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
	}

	public void mouseReleased(MouseEvent e) {			
		super.mouseReleased(e);
		
		if(e.getButton()==1)Picking(e.getX(), e.getY());
	}

	public void mouseDragged(MouseEvent e) {			
		if(mouse_states[MouseEvent.BUTTON2]){
			cam.Move(e.getX()-mouse_x, e.getY()-mouse_y);
		}else if(mouse_states[MouseEvent.BUTTON3]){
			cam.phi   -= 10.0*(e.getX()-mouse_x)/cam.R;
			cam.theta -= 10.0*(e.getY()-mouse_y)/cam.R;			
		}
		
		super.mouseDragged(e);
	}

	
	
}
