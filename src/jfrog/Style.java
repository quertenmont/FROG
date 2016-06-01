package jfrog;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.jogamp.opengl.GL2;

import jfrog.object.Base;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;


public class Style implements Cloneable {	
	public static ArrayList<Base> transparentObjects = null;
	
	public static float [] modelViewMatrix = new float [16];
	public static Texture texDefault       = null;
	
	public boolean isGeom = false;
	public ArrayList<Base> objList = new ArrayList<Base>();
	public String  texture   = null;
	public float [] color    = {1.0f, 1.0f, 1.0f, 1.0f};
	public float [] colorSave= null;
	public float thickness   = 1.0f;
	public short marker      = 0;
	public short markerSize  = 1;	
	public boolean showDet   = false;
	public boolean interpolate = false;
	public float   minE      = 0;
	public float   minPt     = 0;
	public Texture tex       = null;	
	public Texture texmarker = null;
	public byte [] strippleMask = null;
	public boolean hasValue = false;
	
	public StylePalette palette = null; 
	static public Map<Short, Texture> markerTex = null;
	
	public keyFrames fadingKeys = new keyFrames();
		
	public String toString() {
		return String.format(
				"Style [isGeom=%b color=%s, thickness=%s, marker=%s, markerSize=%s]",
				isGeom, Arrays.toString(color), thickness, marker, markerSize);
	}	
	
	public Object clone() {
		Style s 		= new Style();
		s.isGeom        = this.isGeom;
		s.texture		= this.texture;
		s.color 		= this.color.clone();
		s.thickness 	= this.thickness;
		s.marker		= this.marker;
		s.markerSize 	= this.markerSize;
		s.interpolate	= this.interpolate;
		return s;
	}
	
	public boolean isSameContent(Object obj){
		Style s = (Style)obj;
		if(obj==null)return false;		
		return (this.isGeom==s.isGeom) && this.texture.equals(s.texture) && Arrays.equals(color, s.color) && (this.thickness == s.thickness) && (this.marker == s.marker) && (this.markerSize == s.markerSize);		
	}		
	
	
	public void drawStart(GL2 gl, int shaderId){		
		
    	if(shaderId>0){
    		if(isGeom)Shader.sendUniform1f(gl, shaderId, "Time", 9999999);    		
    		else      Shader.sendUniform1f(gl, shaderId, "Time", jfrog.Common.time); 
    	}       		
		
		if(texture!=null){
			gl.glEnableClientState( GL2.GL_TEXTURE_COORD_ARRAY );
			gl.glEnable(GL2.GL_TEXTURE_2D); 
		}
		
		gl.glPointSize(markerSize);
		gl.glLineWidth(thickness);
		gl.glColor4f(color[0], color[1], color[2], color[3]);
				
		//if(color[3]<1){
		//	if(strippleMask==null)strippleMask = getStrippleMask(color[3]);			
		//	gl.glEnable(GL2.GL_POLYGON_STIPPLE);			
		//	gl.glPolygonStipple(strippleMask, 0);			
		//}

		if(marker>0 && jfrog.Common.support_PS){
			gl.glEnable(GL2.GL_TEXTURE_2D);
			gl.glEnable( GL2.GL_POINT_SPRITE );
			texmarker = markerTex.get(marker);
			//if(texmarker!=null)texmarker.bind(gl);
		}else{
			texmarker = null;
		}
		
		if(isGeom && jfrog.Common.clipping){
			if(jfrog.Common.clip0)gl.glEnable(GL2.GL_CLIP_PLANE0);	
			if(jfrog.Common.clip1)gl.glEnable(GL2.GL_CLIP_PLANE1);				
		}
	}
	
	public void drawEnd(GL2 gl){
		
//		if(color[3]<1)gl.glDisable(GL2.GL_POLYGON_STIPPLE);	}		
		
		if(texture!=null){
			gl.glDisable(GL2.GL_TEXTURE_2D); 
			gl.glDisableClientState( GL2.GL_TEXTURE_COORD_ARRAY );		
		}
		
		if(marker>0 && jfrog.Common.support_PS){
			gl.glDisable( GL2.GL_POINT_SPRITE );
			gl.glDisable(GL2.GL_TEXTURE_2D);
			//gl.glBindTexture(GL2.GL_TEXTURE_2D,0);
		}	
		
		if(isGeom && jfrog.Common.clipping){
			gl.glDisable(GL2.GL_CLIP_PLANE0);
			gl.glDisable(GL2.GL_CLIP_PLANE1);				
		}		
	}	
	
	public void draw(GL2 gl, int shaderId){
		drawStart(gl, shaderId);			
		if(hasValue==false || palette==null){		
			for(int i=0;i<objList.size();i++){
				if(objList.get(i).isVisible()>0){
					objList.get(i).deepDraw(gl);
				}
			}
		}else{
			for(int i=0;i<objList.size();i++){
				if(objList.get(i).isVisible()>0){
					objList.get(i).deepDrawWithValue(gl, palette);
				}
			}		
		}
		drawEnd(gl);
	}
	
	void doFading(){
		if(fadingKeys.keys.size()<=0)return;		
//		if(!fadingKeys.isFading()){						
//			if(colorSave!=null){color = colorSave.clone(); colorSave=null;}					
//		}else{								
			//save initial transparent color
			if(colorSave==null)colorSave = color.clone();							
			color[3] = fadingKeys.getFade();
		
//		}
	}

	void removeFading(){
		if(fadingKeys.keys.size()<=0)return;		
		if(colorSave!=null)color = colorSave;
		colorSave=null;							
		fadingKeys.keys.clear();
	}	
	
	
	byte [] getStrippleMask(float transparency){
		byte [] mask = new byte[4*32];

		
		for(int i=0;i<32*4;i++){
			byte line = 0; 
			for(int j=0;j<8;j++){
				if(Math.random()>transparency)line += (1<<j);
			}
			mask[i] = line;
		}
		return mask;
	}		
	
	public static void initializeMarkers(GL2 gl){
		if(markerTex==null)	markerTex = new HashMap<Short, Texture>();
		
		for(short i=1;i<20;i++){
			URL url = jfrog.Common.GetURL(String.format("/rsc/Marker/%d.png", i));
			if(url==null)continue;
			try {
				BufferedInputStream bis = new BufferedInputStream(url.openStream());
				Texture mark = TextureIO.newTexture(bis, false, null);								
				if(mark!=null){
					markerTex.put(i, mark);			
				}
			} catch (IOException e) {
				System.out.println("Skip marker " + url.toString() + " due to a IO exception");				
				//do nothing
			}				
		}
	}	
	

	public static void sortStyles(ArrayList<Style> styles){
		final Comparator<Style> StyleComparator = new Comparator<Style>() {
			public int compare(Style s1, Style s2) {
				if(s1.color[3]>=1 && s2.color[3]>=1)return s1.marker-s2.marker;				
				if(s1.color[3] > s2.color[3])return -1;
				if(s1.color[3] < s2.color[3])return  1;				
				return 0;
			}
		};
						
		Collections.sort(styles, StyleComparator);
	}	
	
	
	public static void drawAllObjects(GL2 gl, ArrayList<Style> styles, int shaderId){
		for(int s=0;s<styles.size();s++){
			if(styles.get(s).color[3]<1)continue;	
			styles.get(s).draw(gl, shaderId);
		}		
		drawTransparentObjects(gl, styles, shaderId);
	}
	

	
	public static void drawTransparentObjects(GL2 gl, ArrayList<Style> styles, int shaderId){
		final Comparator<Base> depthComparator = new Comparator<Base>() {
			public int compare(Base b1, Base b2) {				
				float D1 = modelViewMatrix[2 ]*b1.getPosX() + modelViewMatrix[6 ]*b1.getPosY() + modelViewMatrix[10]*b1.getPosZ() + modelViewMatrix[14];
				float D2 = modelViewMatrix[2 ]*b2.getPosX() + modelViewMatrix[6 ]*b2.getPosY() + modelViewMatrix[10]*b2.getPosZ() + modelViewMatrix[14];				
				if(D1<D2)return -1;
				if(D1>D2)return  1;
				return 0;
			}
		};			
		
		
		if(transparentObjects==null){
			// reset the modelview map to force a reordering of the objects
			//for(int i=0;i<16;i++)modelViewMatrix[i] = 0;
			modelViewMatrix = null;
			
			transparentObjects = new ArrayList<Base>(); 		
			for(int s=0;s<jfrog.Common.styles.size();s++){
				Style style = jfrog.Common.styles.get(s); 		
				if(style.color[3]>=1 || style.color[3]<=0)continue;
				for(int o=0;o<jfrog.Common.styles.get(s).objList.size();o++){
					jfrog.Common.styles.get(s).objList.get(o).fillObjectList(transparentObjects);
				}
			}
			//System.out.printf("relisting\n");
		}
										
		float [] tmpModelViewMatrix = new float[16];		
		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, tmpModelViewMatrix, 0);		
		if(!Arrays.equals(tmpModelViewMatrix, modelViewMatrix)){
			modelViewMatrix = tmpModelViewMatrix;
			Collections.sort(transparentObjects, depthComparator);
		}
		
		Style previousStyle = null;	
		for(int o=0;o<transparentObjects.size();o++){
			Style currentStyle = transparentObjects.get(o).getStyle();
			if(!currentStyle.equals(previousStyle)){
				if(previousStyle!=null)previousStyle.drawEnd(gl);
				currentStyle.drawStart(gl, shaderId);
				previousStyle = currentStyle;
			}
						
			if(currentStyle.hasValue && currentStyle.palette!=null){
				transparentObjects.get(o).deepDrawWithValue(gl, currentStyle.palette);
			}else{
				transparentObjects.get(o).draw(gl);
			}
		}
		if(previousStyle!=null)previousStyle.drawEnd(gl);
	}	
	
	public static void updateStyleForFading(ArrayList<Style> styles){		
		for(int s=0;s<styles.size();s++){
			Style style = styles.get(s);
			style.doFading();			
		}		
		Style.sortStyles(styles);
		transparentObjects = null;		
	}	
	
	public static void cleanStyleForFading(ArrayList<Style> styles){		
		for(int s=0;s<styles.size();s++){
			Style style = styles.get(s);
			style.removeFading();			
		}		
		transparentObjects = null;		
	}	
	
	public static void setStyleForEventCreation(ArrayList<Style> styles){		
		for(int s=0;s<styles.size();s++){			
			Style style = styles.get(s);
			if(style.isGeom)continue;
			if(style.color[3]>=1.0f)style.color[3]=0.999f;			
		}				
	}		

	public static void clearStyleForEventCreation(ArrayList<Style> styles){		
		for(int s=0;s<styles.size();s++){
			Style style = styles.get(s);
			if(style.isGeom)continue;			
			if(style.color[3]==0.999f)style.color[3]=1.0f;			
		}				
	}			
	
	
	public class keyFrame {
		public keyFrame(float start_time, float start_alpha, float end_time,float end_alpha) {
			super();
			this.start_time = start_time;
			this.start_alpha = start_alpha;
			this.end_time = end_time;
			this.end_alpha = end_alpha;
		}

		float start_time  = 0;
		float start_alpha = 1;
		float end_time    = 5;		
		float end_alpha   = 0;	
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
		
		void addKey(float end_time, float end_alpha){
			if(keys.size()==0){
				keys.add(new keyFrame(end_time, end_alpha, end_time, end_alpha));
			}else{
				keyFrame key = keys.get(keys.size()-1);
				keys.add(new keyFrame(key.end_time, key.end_alpha,  end_time, end_alpha));				
			}				
			Collections.sort(keys, keyComparator);
		}		
		
		boolean isFading(){
			float time = jfrog.Common.time;			
			if(time<getStartTime())return false;
			if(time>getEndTime())  return false;
			return true;
		}
		
		float getStartTime(){  return keys.get(0).start_time;}
		float getEndTime  (){  return keys.get(keys.size()-1).end_time;}
		
		float getFade(){
			if(keys.size()<=0)return -1;
			float time = jfrog.Common.time;
			for(int i=0;i<keys.size();i++){				
				keyFrame key = keys.get(i);
				if(key.start_time<=time && time<key.end_time){
					float timeRatio = (time - key.start_time) / (key.end_time - key.start_time);
					return key.start_alpha + (key.end_alpha - key.start_alpha)*timeRatio;				
				}
			}
			if(time<getStartTime())return keys.get(0).start_alpha;
			return keys.get(keys.size()-1).end_alpha;
		}
	}	
	
	
	
	public class StylePalette{
		float colors [];
		int nColor = 0;
		public double minValue =  1E100;
		public double maxValue = -1E100;
		
		public StylePalette(){
			this.colors = new float[]{0,0,1,0,1,0,1,0,0};
			this.nColor = (colors.length/3) -1;
		}		
				
		public void setColors(float [] colors){
			this.colors = colors;
			this.nColor = (colors.length/3) - 1;
		}
		
		public void updateRange(double value){
			if(value<minValue)minValue = value;
			if(value>maxValue)maxValue = value;
		}
		
		public void colorFromValue(GL2 gl, double value){
			value = (value-minValue)/(maxValue-minValue);
			if(value<0)value=0;
			if(value>1)value=1;
			int c = (int)Math.floor(value*nColor);
			float b = (float)((value*nColor) - c);
			if(c>=nColor){
				c = c-1;
				b = 1.0f;
			}
			gl.glColor4f(colors[3*(c)+0]+b*(colors[3*(c+1)+0]-colors[3*(c)+0]), colors[3*(c)+1]+b*(colors[3*(c+1)+1]-colors[3*(c)+1]), colors[3*(c)+2]+b*(colors[3*(c+1)+2]-colors[3*(c)+2]), 1.0f);
		}
	}
	
}
