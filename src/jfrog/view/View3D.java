package jfrog.view;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.nio.DoubleBuffer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.glu.GLU;

import com.jogamp.opengl.util.GLBuffers;

import java.lang.Math;

public class View3D extends View  {
	private static final long serialVersionUID = 6927241534042335078L;
	
	public boolean rotating = true;
	public boolean openned = false;
	public float   openned_angle  = 135;
	public float   openned_angleS = 90;
	public float   dphi  =0.01f;
	public float   dtheta=0.0f;
			   	
	public View3D(){}
	public View3D(GLCapabilitiesImmutable caps, GLContext context){
		super(caps, context);			
	}
		
	public void ParseParameter(){		
		super.ParseParameter();
    	if(jfrog.Common.parser==null)return;
		String Tag;
		Tag = name + "_Animate_Rotate"    ;if(jfrog.Common.parser.tagExist(Tag))rotating = jfrog.Common.parser.toBoolean(Tag);
		Tag = name + "_Animate_dphi"      ;if(jfrog.Common.parser.tagExist(Tag))dphi     = jfrog.Common.parser.toFloat  (Tag);
		Tag = name + "_Animate_dtheta"    ;if(jfrog.Common.parser.tagExist(Tag))dtheta   = jfrog.Common.parser.toFloat  (Tag);
		
		Tag = "Stereoscopy"   			  ;if(jfrog.Common.parser.tagExist(Tag))stereo = jfrog.Common.parser.toBoolean(Tag);
		Tag = "Stereoscopy_Anaglyph"   	  ;if(jfrog.Common.parser.tagExist(Tag))stereo_Anaglyph = jfrog.Common.parser.toBoolean(Tag);
		Tag = "Stereoscopy_EyeDistance"   ;if(jfrog.Common.parser.tagExist(Tag))jfrog.Common.stereo_Distance = jfrog.Common.parser.toFloat(Tag);				
	}
	
	void ProjectionMatrix(GL2 gl){
		GLU glu = GLU.createGLU(gl);
		glu.gluPerspective (45.0, Width/(float)Height, 1.0, 10000.0);
	}	
		
	public void displayScene(GL2 gl, boolean leftEye){
		gl.glMatrixMode (GL2.GL_MODELVIEW);
		gl.glLoadIdentity();			
		cam.LookAt(gl, leftEye);
		if(leftEye){
		   if(rotating)cam.phi += 0.01;
		}
				
		if(openned){
			jfrog.Common.clip0 = true;
			
			double equation1[] = {Math.sin(openned_angleS/57.2958f),Math.cos(openned_angleS/57.2958f),0,0};
			DoubleBuffer eq1Buff =  GLBuffers.newDirectDoubleBuffer(equation1);
			gl.glClipPlane(GL2.GL_CLIP_PLANE0,eq1Buff);	

			if(openned_angle>180){
				jfrog.Common.clip1 = false;
				
				drawBackground(gl);					
				jfrog.Style.drawAllObjects(gl, jfrog.Common.styles, shaderIds[0]);
				
				double equation2[] = {-Math.sin((openned_angleS + openned_angle)/57.2958f),-Math.cos((openned_angleS + openned_angle)/57.2958f),0,0};
				DoubleBuffer eq2Buff =  GLBuffers.newDirectDoubleBuffer(equation2);
				gl.glClipPlane(GL2.GL_CLIP_PLANE0,eq2Buff);
			}else{
				jfrog.Common.clip1 = true;
				
				double equation2[] = {-Math.sin((openned_angleS + openned_angle)/57.2958f),-Math.cos((openned_angleS + openned_angle)/57.2958f),0,0};
				DoubleBuffer eq2Buff =  GLBuffers.newDirectDoubleBuffer(equation2);
				gl.glClipPlane(GL2.GL_CLIP_PLANE1,eq2Buff);
			}
			
			jfrog.Common.clipping = true;						
		}else{
			jfrog.Common.clipping = false;			
		}
		
		drawBackground(gl);			
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
		if(mouse_states[MouseEvent.BUTTON2]){
			cam.Move(e.getX()-mouse_x, e.getY()-mouse_y);
		}else if(mouse_states[MouseEvent.BUTTON3]){
			cam.phi   -= 10.0*(e.getX()-mouse_x)/cam.R;
			cam.theta -= 10.0*(e.getY()-mouse_y)/cam.R;			
		}
		
		super.mouseDragged(e);
	}

	
	
}
