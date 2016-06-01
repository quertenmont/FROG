package jfrog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

public class Camera {
	public double phi      = Math.PI/2;
	public double theta    = 0.0;
	public double R        = 2500.0;	
	public double target_x = 0.0;
	public double target_y = 0.0;
	public double target_z = 0.0;
	double vert_x;
	double vert_y;
	double vert_z;
	double dir_x;
	double dir_y;
	double dir_z;
	
	public void Reset(){
		target_x = 0.0;
		target_y = 0.0;
		target_z = 0.0;

		R        = 1000.0;
		phi      = 0.0;
		theta    = 0.0;	
	}
	
	public void LookAt(GL2 gl, boolean leftEye)
	{
        GLU glu = GLU.createGLU(gl);		
        
        if(leftEye){
			dir_x    = -1*Math.cos(phi)*Math.cos(theta);
			dir_y    =                  Math.sin(theta);
			dir_z    = -1*Math.sin(phi)*Math.cos(theta);

			vert_x   = Math.cos(phi)*Math.sin(theta);
			vert_y   =               Math.cos(theta);
			vert_z   = Math.sin(phi)*Math.sin(theta);
        }else{
        	double phiR = phi+Math.asin(jfrog.Common.stereo_Distance/100.0);
        	
			dir_x    = -1*Math.cos(phiR)*Math.cos(theta);
			dir_y    =                   Math.sin(theta);
			dir_z    = -1*Math.sin(phiR)*Math.cos(theta);

			vert_x   = Math.cos(phiR)*Math.sin(theta);
			vert_y   =                Math.cos(theta);
			vert_z   = Math.sin(phiR)*Math.sin(theta);        	
        }
        

		glu.gluLookAt (target_x + R*dir_x, target_y + R*dir_y, target_z + R*dir_z, 		       
					   target_x          , target_y          , target_z,
					   vert_x            , vert_y            , vert_z  );
		
		float [] FROG_LIGHTING_Local_Pos = { (float)(target_x + R*dir_x), (float)(target_y + R*dir_y), (float)(target_z + R*dir_z), 1.0f };		
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, FROG_LIGHTING_Local_Pos, 0);		
	}	
	
	public void Move(float dx, float dy){
		if(dy!=0){
			target_x += dy * vert_x;
			target_y += dy * vert_y;
			target_z += dy * vert_z;
		}
		if(dx!=0){
			target_x += dx * (dir_y*vert_z - dir_z*vert_y);
			target_y += dx * (dir_z*vert_x - dir_x*vert_z);
			target_z += dx * (dir_x*vert_y - dir_y*vert_x);
		}
		
		
	}	
	
	public class keyFrame {
		public keyFrame(float start_time, float start_target_x,
				float start_target_y, float start_target_z, float start_phi,
				float start_theta, float start_R, float end_time,
				float end_target_x, float end_target_y, float end_target_z,
				float end_phi, float end_theta, float end_R) {
			this.start_time = start_time;
			this.start_target_x = start_target_x;
			this.start_target_y = start_target_y;
			this.start_target_z = start_target_z;
			this.start_phi = start_phi;
			this.start_theta = start_theta;
			this.start_R = start_R;
			this.end_time = end_time;
			this.end_target_x = end_target_x;
			this.end_target_y = end_target_y;
			this.end_target_z = end_target_z;
			this.end_phi = end_phi;
			this.end_theta = end_theta;
			this.end_R = end_R;
		}
		float start_time;
		float start_target_x;
		float start_target_y;
		float start_target_z;
		float start_phi;
		float start_theta;
		float start_R;	
		float end_time;
		float end_target_x;
		float end_target_y;
		float end_target_z;
		float end_phi;
		float end_theta;
		float end_R;
	}	
	
	public class keyFrames{
		ArrayList<keyFrame> keys = new ArrayList<keyFrame>();
		final Comparator<keyFrame> keyComparator = new Comparator<keyFrame>() {
			public int compare(keyFrame k1, keyFrame k2) {				
				if(k1.start_time<k2.start_time)return -1;
				if(k1.start_time>k2.start_time)return 1;
				return 0;
			}
		};	
		
		void addKey(keyFrame key){									
			keys.add(key);	
			Collections.sort(keys, keyComparator);
		}
		
		void addKey(float end_time,
				float end_target_x, float end_target_y, float end_target_z,
				float end_phi, float end_theta, float end_R){
			if(keys.size()==0){
				keys.add(new keyFrame(end_time, end_target_x, end_target_y, end_target_z, end_phi, end_theta, end_R,
						              end_time, end_target_x, end_target_y, end_target_z, end_phi, end_theta, end_R));
			}else{
				keyFrame key = keys.get(keys.size()-1);
				keys.add(new keyFrame(key.end_time, key.end_target_x, key.end_target_y, key.end_target_z, key.end_phi, key.end_theta, key.end_R,
			              end_time, end_target_x, end_target_y, end_target_z, end_phi, end_theta, end_R));				
			}				
			Collections.sort(keys, keyComparator);
		}		
		
		float getStartTime(){  return keys.get(0).start_time;}
		float getEndTime  (){  return keys.get(keys.size()-1).end_time;}
		
		void moveCamera(Camera cam){
			float time = jfrog.Common.time;
			for(int i=0;i<keys.size();i++){				
				keyFrame key = keys.get(i);
				if(key.start_time<=time && time<=key.end_time){
				float timeRatio = (time - key.start_time) / (key.end_time - key.start_time);				
				cam.target_x = key.start_target_x + (key.end_target_x - key.start_target_x)*timeRatio;
				cam.target_y = key.start_target_y + (key.end_target_y - key.start_target_y)*timeRatio;
				cam.target_z = key.start_target_z + (key.end_target_z - key.start_target_z)*timeRatio;
				cam.R        = key.start_R        + (key.end_R        - key.start_R       )*timeRatio;
				cam.phi      = key.start_phi      + (key.end_phi      - key.start_phi     )*timeRatio;
				cam.theta    = key.start_theta    + (key.end_theta    - key.start_theta   )*timeRatio;
				}
			}
		}

	}
	
	
	
}
