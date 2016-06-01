package jfrog;

import java.awt.Image;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jogamp.opengl.GL2;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.jogamp.opengl.util.FPSAnimator;

import jfrog.Style.StylePalette;
import jfrog.object.Base;

public class Common {
	public static String authorList = "Author: Loic.Quertenmont@cern.ch";
	public static String version    = "jFrog 3.06";
	
	public static float time = 999999;    		
	public static JFrame mainFrame = null; 
//	public static TabbedFPSAnimator animator = null;
	public static FPSAnimator animator = null;
	public static jfrog.object.Base root = new jfrog.object.Base();	
	public static jfrog.object.Base geom = null;
	public static jfrog.object.Events events = null;	
	public static jfrog.Loading loading = null;
	public static jfrog.Tree treeMenu = null;	
//	public static jfrog.view.View3D view = null;
	public static JTabbedPane tab = null;
	public static JTextField textBox = null;
	public static jfrog.MenuBar menuBar = null;
	public static jfrog.CardParser parser = null;	
	public static Map<Long, ArrayList<Base> > geomIdMap = new HashMap<Long, ArrayList<Base> >();	
	public static Map<Integer, Base> geomPickingMap = new HashMap<Integer, Base>();
	public static Map<Integer, Base> eventPickingMap = new HashMap<Integer, Base>();

	public static StylePalette stylePalette = new Style().new StylePalette();
	
//	public static Map<Long, Base> eventIdMap = new HashMap<Long, Base>();
	public static ArrayList<Style> styles = new ArrayList<Style>();	
	
	public static String File_Config      = "/rsc/config.jfrog";
	public static String Text_Config      = "";	
	

	
	public static boolean support_wasCheck = false;
	public static boolean support_PS  = true;	
	public static boolean support_VBO = true;
	public static boolean support_FBO = true;
	public static boolean support_Shader = true;
	
	public static boolean show_FPS = true;
	public static boolean show_FrogLogo = true;
	
	public static boolean clipping = true;
	public static boolean clip0    = false;
	public static boolean clip1    = false;	
	
	public static int     Screenshot_width  = -1;
	public static int     Screenshot_height = -1;
	
	public static float   stereo_Distance = 5;
	
	public static boolean debug = false;
	
	
	public static Image Logo_Frog    = null;
	public static Image Logo_FrogTxt = null;

	
	public static void CheckGLExtensionSupport(GL2 gl){
		if(support_wasCheck)return;
		support_wasCheck = true;

		// Check version.
		String versionStr = gl.glGetString( GL2.GL_VERSION );
		System.out.println( "GL version:"+versionStr ); 

		
		support_PS = 	support_PS && 
					 	gl.isExtensionAvailable("GL_ARB_point_sprite") && 
					 	gl.isFunctionAvailable("glPointParameterfARB") &&
					 	gl.isFunctionAvailable("glPointParameterfvARB");						
		if(!support_PS)System.out.println( "PointSprite not supported." );
		//Load the markers
		Style.initializeMarkers(gl);		

		// Check if VBO is available.
		support_VBO = 	support_VBO &&
						gl.isExtensionAvailable("GL_ARB_vertex_buffer_object") && 
						gl.isFunctionAvailable("glGenBuffersARB") &&
						gl.isFunctionAvailable("glBindBufferARB") &&
						gl.isFunctionAvailable("glBufferDataARB") &&
						gl.isFunctionAvailable("glDeleteBuffersARB");
		if(!support_VBO)System.out.println( "VBOs not supported." ); 
		
		
		// Check if FBO is available.
		support_FBO = 	support_FBO &&
						gl.isExtensionAvailable("GL_EXT_framebuffer_object") && 
						gl.isFunctionAvailable("glGenFramebuffersEXT") &&
						gl.isFunctionAvailable("glBindFramebufferEXT") &&						
						gl.isFunctionAvailable("glFramebufferTexture2DEXT") &&						
						gl.isFunctionAvailable("glGenRenderbuffersEXT") &&						
						gl.isFunctionAvailable("glBindRenderbufferEXT") &&
						gl.isFunctionAvailable("glRenderbufferStorageEXT") &&						
						gl.isFunctionAvailable("glFramebufferRenderbufferEXT") &&						
						gl.isFunctionAvailable("glCheckFramebufferStatusEXT") &&
						gl.isFunctionAvailable("glDeleteFramebuffersEXT");						
		if(!support_FBO)System.out.println( "FBOs not supported." );	


		// Check if FBO is available.
		support_Shader = 	support_Shader &&
						gl.isExtensionAvailable("GL_ARB_fragment_shader") && 
						gl.isExtensionAvailable("GL_ARB_shader_objects") &&
						gl.isExtensionAvailable("GL_ARB_shading_language_100") &&
						gl.isExtensionAvailable("GL_ARB_fragment_program") && 
						gl.isExtensionAvailable("GL_ARB_vertex_program") &&
						gl.isFunctionAvailable("glCreateShaderARB") &&
						gl.isFunctionAvailable("glShaderSourceARB") &&						
						gl.isFunctionAvailable("glCompileShaderARB") &&						
						gl.isFunctionAvailable("glCreateProgramARB") &&						
						gl.isFunctionAvailable("glAttachShaderARB") &&
						gl.isFunctionAvailable("glLinkProgramARB") &&						
						gl.isFunctionAvailable("glValidateProgramARB") &&						
						gl.isFunctionAvailable("glUseProgramARB");						
		if(!support_Shader)System.out.println( "Shaders not supported." );		
		
	}
		
	static public URL GetURL(String string){
		URL toReturn = null;
		try {				
			if(string.startsWith("http://")){	
				return new URL(string);	
			}			
			
			//Try absolute path
			File file = new File(string);
			//System.out.printf("File = %s & canRead=%s\n", file, file!=null ? file.canRead() : "");
			if(file!=null && file.canRead()){			
				toReturn = file.toURI().toURL();
				if(toReturn !=null)return toReturn;
			}	
			
			//Try local path with respect to config file directory
			File dir = new File(jfrog.Common.File_Config);
			if(dir!=null){
				file = new File(dir.getParentFile(),string);
				//System.out.printf("File = %s & canRead=%s\n", file, file!=null ? file.canRead() : "");
				if(file!=null && file.canRead()){			
					toReturn = file.toURI().toURL();
					if(toReturn !=null)return toReturn;
				}
			}
					
			
		} catch (MalformedURLException e) {
			//e.printStackTrace();			
		}		
		
		toReturn = jfrog.Common.class.getResource(string);		
		return toReturn;	
	}
	
	
	
	
	public static float Coord_EtaToTheta(float Eta){return (float)(2*Math.atan(Math.exp(-Eta)));}

	public static void  Coord_PhysicalToCartesian(Float P, Float Eta, Float Phi, float X, float Y  , float Z){		
		float Theta = Coord_EtaToTheta(Eta);

		X = (float)(P*Math.sin(Theta)*Math.cos(Phi));
		Y = (float)(P*Math.sin(Theta)*Math.sin(Phi));
		Z = (float)(P*Math.cos(Theta));
	}

	public static void  Coord_CartesianToPhysical(Float X, Float Y  , Float Z  , float P, float Eta, float Phi){
		P           = (float)(Math.sqrt(X*X+Y*Y+Z*Z));
		Phi         = (float)(Math.atan2(Y,X));
		float Theta = (float)(Math.atan2(Math.sqrt(X*X+Y*Y),Z));
		Eta         = (float)(-Math.log(Math.tan(Theta/2)));
	}


	public static float Coord_GetPt(float P, float Eta){
		return (float)(P*Math.sin(Coord_EtaToTheta(Eta)));
	}	
	

	public static ArrayList<Base> getObjectsWidthId(Long id, Map<Long, ArrayList<Base> > map){
		return map.get(id);		
	}
	
	public static Base getObjectWidthId(Long id, Map<Long, ArrayList<Base> > map){
		ArrayList<Base> entry = map.get(id);
		if(entry==null)return null;
		return entry.get(0);
	}		
	
	public static void putObjectsWithId(Long id, Map<Long, ArrayList<Base> > map, Base obj){
		ArrayList<Base> entry = getObjectsWidthId(id, map);		
		if(entry == null){
			entry = new ArrayList<Base>();
			entry.add(obj);
			map.put(id, entry);			
		}else{
			entry.add(obj);			
		}		
	}
	
	
	public static void fillPickingMap(Base obj, Map<Integer, Base> map){
		fillPickingMap(obj, map, 0);
	}
	public static void fillPickingMap(Base obj, Map<Integer, Base> map, int Offset){
		if(!obj.isCollection()){
			obj.setPickingId(map.size()+1+Offset);
			map.put(map.size()+1+Offset, obj);			
		}
		for(int i=0;i<obj.getDaughtersSize();i++){
			fillPickingMap(obj.getDaughter(i), map, Offset);
		}	
	}	

}
