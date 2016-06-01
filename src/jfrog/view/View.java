package jfrog.view;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.GLBuffers;

import jfrog.Camera;
import jfrog.Shader;
import jfrog.object.Base;

public class View extends GLCanvas implements GLEventListener, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener  {
	private static final long serialVersionUID = -8478133343657860790L;

	public jfrog.view.ViewOverlay overlay = null;
	
	protected int shaderIds[] = {0, 0, 0};		
	protected boolean mouse_states[] = new boolean[4];
	protected int  mouse_x, mouse_y;		
	
	private int [] FBO = new int [3];
	int Width, Height;

	public Camera cam = new Camera();	
	public String name;
	public String label;	
	public String vertexShader="", fragmentShader="";
	protected long [] geomToDisplay = {};
	protected Map<jfrog.Style, ArrayList<jfrog.object.Base> > BackgroundStyleMap = null;
	
	public boolean stereo             = false;
	public boolean stereo_Anaglyph 	  = false;
	public boolean stereo_Interleaved = false;
	public boolean stereo_InterleavedStencil = false;
	public boolean stereo_FlipEyes    = false;
	
	public boolean wireframe = false;
	
	public View(){}	
	public View(GLCapabilitiesImmutable caps, GLContext context){		
    	super(caps);
    	if(context!=null)setSharedContext(context);
    	addGLEventListener(this);
    	addKeyListener(this);
    	addMouseListener(this);
    	addMouseWheelListener(this);
    	addMouseMotionListener(this);    	
    	
    	System.out.printf("NewView %s\n", caps.toString());    	
	}
	
	public void ParseParameter(){
    	if(jfrog.Common.parser==null)return;    	
		String Tag;
		Tag = name + "_Cam_Pos_R"     ;if(jfrog.Common.parser.tagExist(Tag))cam.R         = jfrog.Common.parser.toFloat(Tag); 
		Tag = name + "_Cam_Pos_Theta" ;if(jfrog.Common.parser.tagExist(Tag))cam.theta     = jfrog.Common.parser.toFloat(Tag);
		Tag = name + "_Cam_Pos_Phi"   ;if(jfrog.Common.parser.tagExist(Tag))cam.phi       = jfrog.Common.parser.toFloat(Tag);
		
		Tag = name + "_Cam_Target_X"  ;if(jfrog.Common.parser.tagExist(Tag))cam.target_x  = jfrog.Common.parser.toFloat(Tag);
		Tag = name + "_Cam_Target_Y"  ;if(jfrog.Common.parser.tagExist(Tag))cam.target_y  = jfrog.Common.parser.toFloat(Tag);
		Tag = name + "_Cam_Target_Z"  ;if(jfrog.Common.parser.tagExist(Tag))cam.target_z  = jfrog.Common.parser.toFloat(Tag);
		    		    					
		//((FROG_ReadCards*)FROG::Card_)->GetBool(&ShowDet_			,"%s_ShowDet"		,Name_);
		
		Tag = name + "_VertexShader"  ;if(jfrog.Common.parser.tagExist(Tag))vertexShader  = jfrog.Common.parser.toString(Tag);
		Tag = name + "_FragmentShader";if(jfrog.Common.parser.tagExist(Tag))fragmentShader= jfrog.Common.parser.toString(Tag);    	
		Tag = name + "_Geom"          ;if(jfrog.Common.parser.tagExist(Tag))geomToDisplay = jfrog.Common.parser.toLongArr(Tag);
		    
	}
			
	void ProjectionMatrix(GL2 gl){
		gl.glOrtho(0, Width,0,Height,-1, 1);
	}	
	
	public void displayScene(GL2 gl, boolean leftEye){}

	public void display(GLAutoDrawable arg0) {				
        GL2 gl = arg0.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
                
        
        if(wireframe){gl.glPolygonMode( GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
        }else{		  gl.glPolygonMode( GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        }
        
    	if(shaderIds[0]>0){
    		gl.glUseProgram(shaderIds[0]);
    	}        
                
		gl.glMatrixMode (GL2.GL_PROJECTION);
		gl.glLoadIdentity ();  	
		ProjectionMatrix(gl);
		               
		initStencil(gl,stereo && stereo_Interleaved);
		if(stereo){
			if(stereo_Anaglyph){	gl.glColorMask(false, true, true, false);
			}else if(stereo_Interleaved){
			    gl.glStencilFunc(GL2.GL_NOTEQUAL, 0x1, 0x1);				
			}else{						gl.glDrawBuffer(GL2.GL_BACK_LEFT);
			}
		}
		displayScene(gl, !stereo_FlipEyes);

		if(stereo){		
			if(stereo_Anaglyph){	gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);
										gl.glColorMask(true, false, false, false);
			}else if(stereo_Interleaved){
							gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);
							gl.glStencilFunc(GL2.GL_EQUAL, 0x1, 0x1);				
			}else{						gl.glDrawBuffer(GL2.GL_BACK_RIGHT);
			}
			displayScene(gl, stereo_FlipEyes);
			if(stereo_Anaglyph){	gl.glColorMask(true, true, true, false);
			}else if(stereo_Interleaved){
			    gl.glStencilFunc(GL2.GL_ALWAYS, 0x1, 0x1);				
			}else{						gl.glDrawBuffer(GL2.GL_BACK);
			}
		}
						
    	if(shaderIds[0]>0)gl.glUseProgram(0);        								
		overlay.Draw();			
	}
	public void dispose(GLAutoDrawable arg0) {}
	public void init(GLAutoDrawable arg0) {
		if(overlay==null)overlay = new ViewOverlay(arg0);		
		GL2 gl = arg0.getGL().getGL2();
		jfrog.Common.CheckGLExtensionSupport(gl);
		gl.setSwapInterval(0);		
		gl.glClearColor(0, 0, 0, 0);				

		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);        

		gl.glFrontFace(GL2.GL_CCW);
		gl.glEnable(GL2.GL_CULL_FACE);        

		gl.glShadeModel(GL2.GL_FLAT);		

		gl.glEnable(GL2.GL_DEPTH_TEST);

		final float [] FROG_LIGHTING_Global_ambient = { 0.5f, 0.5f, 0.5f, 1.0f };						
		gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, FROG_LIGHTING_Global_ambient, 0);
		gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_FALSE);
		gl.glColorMaterial ( GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE ) ;
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glEnable(GL2.GL_LIGHT0);         

		if(jfrog.Common.support_PS){
			gl.glPointParameterf( GL2.GL_POINT_SIZE_MIN, 1.0f );
			gl.glPointParameterf( GL2.GL_POINT_SIZE_MAX, 50.0f );
			float quadratic[] = { 1.0f, 0.0003f, 0.0f };
			gl.glPointParameterfv( GL2.GL_POINT_DISTANCE_ATTENUATION, quadratic, 0 );
			gl.glTexEnvf( GL2.GL_POINT_SPRITE, GL2.GL_COORD_REPLACE, GL2.GL_TRUE );    		        	
		}

        loadShaders(gl, null, null, true);

        gl.glEnableClientState( GL2.GL_ELEMENT_ARRAY_BUFFER );
        gl.glEnableClientState( GL2.GL_NORMAL_ARRAY );
        gl.glEnableClientState( GL2.GL_VERTEX_ARRAY );        
	}
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
		this.Width  = arg3;	this.Height = arg4;
		if(overlay!=null)overlay.setSize(this.Width, this.Height);		
        GL2 gl = arg0.getGL().getGL2();		
        gl.glViewport(0, 0, this.Width, this.Height);	      
	}

	public void mouseWheelMoved(MouseWheelEvent arg0){}
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseMoved(MouseEvent e){}	
	public void mousePressed(MouseEvent e) {
		if(e.getButton()>3)return;
		mouse_states[e.getButton()] = true;
		mouse_x = e.getX();
		mouse_y = e.getY();
	}
	public void mouseReleased(MouseEvent e) {
		if(e.getButton()>3)return;
		mouse_states[e.getButton()] = false;		
	}
	public void mouseDragged(MouseEvent e) {			
		mouse_x = e.getX();
		mouse_y = e.getY();		
	}		
	public void keyPressed(KeyEvent arg0){}
	public void keyReleased(KeyEvent arg0){}
	public void keyTyped(KeyEvent arg0){}
	
	
	
	public void makeScreenShot(File output) {
		try{		
			while (getContext().makeCurrent() == GLContext.CONTEXT_NOT_CURRENT)Thread.sleep(100);			
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
				
		
		int width  = jfrog.Common.Screenshot_width  > 0 ? jfrog.Common.Screenshot_width  : getWidth ();
		int height = jfrog.Common.Screenshot_height > 0 ? jfrog.Common.Screenshot_height : getHeight();
		ByteBuffer data = null;
		
		GL2 gl = getGL().getGL2();		
		if(jfrog.Common.support_FBO){
			
			if(FBO[0]==0){			
				gl.glGenFramebuffers(1, FBO, 0);
				gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, FBO[0]);
				
				gl.glGenRenderbuffers(1, FBO, 1);
				gl.glBindRenderbuffer(GL2.GL_RENDERBUFFER, FBO[1]);
				gl.glRenderbufferStorage(GL2.GL_RENDERBUFFER, GL2.GL_DEPTH_COMPONENT, width, height);
				gl.glBindRenderbuffer(GL2.GL_RENDERBUFFER, 0);			
				
				gl.glGenTextures(1, FBO, 2);
				gl.glBindTexture(GL2.GL_TEXTURE_2D, FBO[2]);
				gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
				gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
				gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
				gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);
				gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_GENERATE_MIPMAP, GL2.GL_TRUE); // automatic mipmap generation included in OpenGL v1.4
				gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA8, width, height, 0, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, null);
				gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);		
				

				gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0, GL2.GL_TEXTURE_2D, FBO[2], 0);
				gl.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER, GL2.GL_DEPTH_ATTACHMENT, GL2.GL_RENDERBUFFER, FBO[1]);
				
			
				// check FBO status
				if(gl.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER)!=GL2.GL_FRAMEBUFFER_COMPLETE){
					System.out.println("Error while creating FBO --> FBO disabled");
					jfrog.Common.support_FBO = false;
					return;
				}		
			}
			
			boolean stereoSAVE = stereo;
			stereo = false;
						
			gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, FBO[0]);			
			reshape(this, 0, 0, width, height);
			display(this);						
			
			gl.glBindTexture(GL2.GL_TEXTURE_2D, FBO[2]);
			gl.glPixelStorei( GL2.GL_PACK_ALIGNMENT, 1);						
			data = ByteBuffer.allocate(width*height*3);
			gl.glGetTexImage(GL2.GL_TEXTURE_2D,0,GL2.GL_RGB,GL2.GL_UNSIGNED_BYTE,data);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, 0); //get back to null texture			
			
			if(stereoSAVE){							
				stereo_FlipEyes = !stereo_FlipEyes;				
				display(this);
				stereo_FlipEyes = !stereo_FlipEyes;
				
				//needed because texture could have been changed during display
				gl.glBindTexture(GL2.GL_TEXTURE_2D, FBO[2]);					
				
				ByteBuffer dataLeft  = data;						
				ByteBuffer dataRight = ByteBuffer.allocate(width*height*3);
				gl.glGetTexImage(GL2.GL_TEXTURE_2D,0,GL2.GL_RGB,GL2.GL_UNSIGNED_BYTE,dataRight);
				data =ByteBuffer.allocate(width*height*3*2);
				for(int y=0;y<height;y++){
					for(int x=0;x<width*3;x++)data.put(dataLeft.get());
					for(int x=0;x<width*3;x++)data.put(dataRight.get());
				}
				width *= 2;				
			}
			stereo=stereoSAVE;

			reshape(this, 0, 0, getWidth(), getHeight());			
			gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);			
			gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
		}else{
			width  = getWidth();
			height = getHeight();
			data = ByteBuffer.allocate(width*height*3);
			gl.glReadBuffer(GL.GL_FRONT);
			gl.glPixelStorei(GL2.GL_PACK_ALIGNMENT, 1);
			gl.glReadPixels(0, 0, width, height, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, data);
		}
				
		int[] pixelInts = new int[ width * height ];
		int p = width * height * 3; 
		int q; // Index into ByteBuffer
		int i = 0; // Index into target int[]
		int w3 = width * 3; // Number of bytes in each row
		for (int row = 0; row < height; row++) {
			p -= w3;
			q = p;
			for (int col = 0; col < width; col++) {
				int iR = data.get(q++);
				int iG = data.get(q++);
				int iB = data.get(q++);
				pixelInts[i++] = 0xFF000000 | ((iR & 0x000000FF) << 16) | ((iG & 0x000000FF) << 8) | (iB & 0x000000FF);
			}
		}

		BufferedImage bufferedImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB);
		bufferedImage.setRGB( 0, 0, width, height, pixelInts, 0, width );
     						
		try {
			ImageIO.write(bufferedImage, "png", output);
		} catch(IOException e) { 
			System.out.println("Unable to write screenshot. Details: ");
			e.printStackTrace(); 
		}
		
		getContext().release();		
	} 	
	
	public void drawBackground(GL2 gl){
		if(BackgroundStyleMap==null){
			BackgroundStyleMap = new HashMap<jfrog.Style, ArrayList<jfrog.object.Base> >();
			for(int i=0;i<geomToDisplay.length;i++){
				if(geomToDisplay[i]==0)continue;
				ArrayList<jfrog.object.Base> objs = jfrog.Common.geomIdMap.get(geomToDisplay[i]);						
				if(objs==null){System.out.printf("BackgroundGeometry display: Id %d not found\n", geomToDisplay[i]); continue;}
				for(int j=0;j<objs.size();j++){
					jfrog.object.Base obj = objs.get(j);
					obj.fillStyleMap(BackgroundStyleMap);				
				}
			}
		}
		
		Set<jfrog.Style> keys = BackgroundStyleMap.keySet();
	    Iterator<jfrog.Style> it = keys.iterator();	    
	    while(it.hasNext()){	    		
	    	jfrog.Style style = (jfrog.Style)it.next();
	    	style.drawStart(gl, shaderIds[0]);
	    	ArrayList<jfrog.object.Base> tmp = BackgroundStyleMap.get(style);
	    	for(int i=0;i<tmp.size();i++){tmp.get(i).forceDeepDraw(gl);}
	    	style.drawEnd(gl);
	    }	
	}	
	

	
	
	
/*
 * ALL THE FOLLOWING IS COMPLETELY BUGGY... CAN NOT UNDERSTAND WHY... SO FAR
 * 	
 */
/*	
	boolean backgroundTextureRefresh = true;
	int backgroundTextureWidth = 0;
	int backgroundTextureHeight = 0;
	int [] backgroundTextureId = {0};
	public void drawBackgroundFromTexture(GL2 gl){
		if(!backgroundTextureRefresh){
			System.out.printf("DisplayTexture\n");
			
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glOrtho(0, Width, 0, Height, -1.0f, 1.0f);
			
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			
			gl.glDisable(GL2.GL_DEPTH_TEST);
			gl.glEnable(GL2.GL_TEXTURE_2D);
			gl.glBindTexture(GL2.GL_TEXTURE_2D,backgroundTextureId[0]);			

			gl.glColor4f (0,1,0,1);
			gl.glBegin (GL2.GL_QUADS);
			gl.glTexCoord2i(0,0); gl.glVertex3i (Width/2+0 , 0, -1 );
			gl.glTexCoord2i(1,0); gl.glVertex3i (Width/2+Width/2, 0, -1 );
			gl.glTexCoord2i(1,1); gl.glVertex3i (Width/2+Width/2, Height/2, -1);
			gl.glTexCoord2i(0,1); gl.glVertex3i (Width/2+0 , Height/2, -1);
			gl.glEnd (); 
			
			gl.glBindTexture(GL2.GL_TEXTURE_2D,0);		
			
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glPopMatrix();			
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glPopMatrix();			
		}else{
			backgroundTextureRefresh  = false;
			System.out.printf("ReccordTexture\n");
		
			drawBackground(gl);
	
			int p2w = 1; while(p2w<Width )p2w = p2w<<1;
			int p2h = 1; while(p2h<Height)p2h = p2h<<1;		
			if(backgroundTextureWidth != p2w || backgroundTextureHeight != p2h){
				if(backgroundTextureId[0]>0) gl.glDeleteTextures(1, backgroundTextureId, 0);
				backgroundTextureId[0] = 0;
				backgroundTextureWidth  = p2w;
				backgroundTextureHeight = p2h;					
			}		

			if(backgroundTextureId[0]==0){									
				gl.glEnable(GL2.GL_TEXTURE_2D);
				gl.glReadBuffer(GL.GL_FRONT);
				gl.glPixelStorei(GL2.GL_PACK_ALIGNMENT, 1);				
				gl.glGenTextures(1, backgroundTextureId, 0);
				gl.glBindTexture   (GL2.GL_TEXTURE_2D, backgroundTextureId[0]);		
				gl.glTexParameteri (GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR  );
				gl.glTexParameteri (GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR  );
				gl.glTexParameteri (GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
				gl.glTexParameteri (GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);				
			}else{
				gl.glBindTexture   (GL2.GL_TEXTURE_2D, backgroundTextureId[0]);
			}
			//gl.glCopyTexImage2D(GL2.GL_TEXTURE_2D,0,GL2.GL_RGBA,0, 0, p2w, p2h,0);
			ByteBuffer data = ByteBuffer.allocate(p2w*p2h*4);
			gl.glReadPixels(0, 0, p2w, p2h, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, data);
			gl.glTexImage2D    (GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, p2w ,p2h , 0, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, data);			

			
			
			gl.glGetTexImage(GL2.GL_TEXTURE_2D,0,GL2.GL_RGB,GL2.GL_UNSIGNED_BYTE,data);			
			int[] pixelInts = new int[ p2w * p2h ];
			int p = p2w * p2h * 3; 
			int q; // Index into ByteBuffer
			int i = 0; // Index into target int[]
			int w3 = p2w * 3; // Number of bytes in each row
			for (int row = 0; row < p2h; row++) {
				p -= w3;
				q = p;
				for (int col = 0; col < p2w; col++) {
					int iR = data.get(q++);
					int iG = data.get(q++);
					int iB = data.get(q++);
					pixelInts[i++] = 0xFF000000 | ((iR & 0x000000FF) << 16) | ((iG & 0x000000FF) << 8) | (iB & 0x000000FF);
				}
			}

			BufferedImage bufferedImage = new BufferedImage( p2w, p2h, BufferedImage.TYPE_INT_ARGB);
			bufferedImage.setRGB( 0, 0, p2w, p2h, pixelInts, 0, p2w );
	     						
			try {
				ImageIO.write(bufferedImage, "png", new File("testtext.png"));
			} catch(IOException e) { 
				System.out.println("Unable to write screenshot. Details: ");
				e.printStackTrace(); 
			}			
			
			
			gl.glBindTexture   (GL2.GL_TEXTURE_2D, 0);						
		}
	}
*/	
	
	
	void Picking(int x, int y){
		if(jfrog.Common.geomPickingMap.size()==0){
			jfrog.Common.fillPickingMap(jfrog.Common.geom, jfrog.Common.geomPickingMap);
		}
//		if(jfrog.Common.eventPickingMap.size()==0){
			jfrog.Common.eventPickingMap.clear();
			jfrog.Common.fillPickingMap(jfrog.Common.events, jfrog.Common.eventPickingMap, jfrog.Common.geomPickingMap.size());
//		}		
		
		try{		
			while (getContext().makeCurrent() == GLContext.CONTEXT_NOT_CURRENT)Thread.sleep(100);			
		}catch (InterruptedException ie) {
			ie.printStackTrace();
		}
				
		GL2 gl = getGL().getGL2();											
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		int [] viewport = new int[4];
		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);        
		
    	if(shaderIds[0]>0){
    		gl.glUseProgram(shaderIds[0]);
    		Shader.sendUniform1f(gl, shaderIds[0], "Time", 9999999);
    		Shader.sendUniform1i(gl, shaderIds[0], "pickingMode", 1);
    		jfrog.Common.time += 0.1;
    	}
    	gl.glDisable(GL2.GL_LIGHTING);
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		//glu.gluPickMatrix(x-2, y-2, 5.0, 5.0, viewport, 0); 		          
		ProjectionMatrix(gl);

		gl.glMatrixMode (GL2.GL_MODELVIEW);
		gl.glLoadIdentity();		
		cam.LookAt(gl, !stereo_FlipEyes);
					
		jfrog.Common.geom.deepPickingDraw(gl);
		jfrog.Common.events.deepPickingDraw(gl);
						
		ByteBuffer pixel = ByteBuffer.allocate(4);
		gl.glReadPixels(x, viewport[3] - y -1, 1, 1, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, pixel);
		int pickingId = (int)((0x000000FF&pixel.get())<<16)  + (int)((0x000000FF&pixel.get())<<8) + (int)((0x000000FF&pixel.get()));
		
		//IF NOT OBJECT CLICKED --> CHECK TOP PIXEL
		if(pickingId==0){
			pixel.position(0);
			gl.glReadPixels(x, viewport[3] - y - 1 -1, 1, 1, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, pixel);
			pickingId = (int)((0x000000FF&pixel.get())<<16)  + (int)((0x000000FF&pixel.get())<<8) + (int)((0x000000FF&pixel.get()));			
		}
		
		//IF NOT OBJECT CLICKED --> CHECK BOTTOM PIXEL		
		if(pickingId==0){
			pixel.position(0);
			gl.glReadPixels(x, viewport[3] - y - 1 +1, 1, 1, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, pixel);
			pickingId = (int)((0x000000FF&pixel.get())<<16)  + (int)((0x000000FF&pixel.get())<<8) + (int)((0x000000FF&pixel.get()));			
		}		
		
		//IF NOT OBJECT CLICKED --> CHECK LEFT PIXEL
		if(pickingId==0){
			pixel.position(0);
			gl.glReadPixels(x-1, viewport[3] - y - 1, 1, 1, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, pixel);
			pickingId = (int)((0x000000FF&pixel.get())<<16)  + (int)((0x000000FF&pixel.get())<<8) + (int)((0x000000FF&pixel.get()));			
		}
		
		//IF NOT OBJECT CLICKED --> CHECK RIGHT PIXEL		
		if(pickingId==0){
			pixel.position(0);
			gl.glReadPixels(x+1, viewport[3] - y - 1, 1, 1, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, pixel);
			pickingId = (int)((0x000000FF&pixel.get())<<16)  + (int)((0x000000FF&pixel.get())<<8) + (int)((0x000000FF&pixel.get()));			
		}		
						
		//IF WE SELECTED AN OBJECT
		if(pickingId>0){
			Base obj = jfrog.Common.eventPickingMap.get(pickingId);;			
			if(obj==null)obj = jfrog.Common.geomPickingMap.get(pickingId);
			//System.out.printf("X=%d Y=%d pickingId = %d = %3d %3d %3d --> Object = %s\n",x, y, pickingId, ((pickingId&0xFF0000)>>16), ((pickingId&0x00FF00)>>8), (pickingId&0x0000FF), obj);
			if(obj!=null){
				if(obj.hasValue){jfrog.Common.textBox.setText(obj.toString() + " value=" + obj.value);					
				}else{			 jfrog.Common.textBox.setText(obj.toString()); }
			}
		}				
		
		gl.glEnable(GL2.GL_LIGHTING);
    	if(shaderIds[0]>0){
    		Shader.sendUniform1i(gl, shaderIds[0], "pickingMode", 0);
    		gl.glUseProgram(0);
    	}
		
    	
    	this.display(this);
    	
		getContext().release();
	}		
	
	public void loadShaders(GL2 gl, String vertexShaderPath, String fragmentShaderPath, boolean isPath){
		boolean mustReleaseContext = false;
		if(gl==null){
			try{		
				while (getContext().makeCurrent() == GLContext.CONTEXT_NOT_CURRENT)Thread.sleep(100);
				gl = getGL().getGL2();				
				mustReleaseContext = true;				
			}catch (InterruptedException ie) {
				ie.printStackTrace();
				return;
			}			
		}
				
		if(vertexShaderPath   == null)vertexShaderPath   = vertexShader;
		if(fragmentShaderPath == null)fragmentShaderPath = fragmentShader;
		
        if(jfrog.Common.support_Shader && !vertexShaderPath.equals("") && !fragmentShaderPath.equals("")){
        	jfrog.Shader.loadShaderProgram(gl,shaderIds, vertexShaderPath, fragmentShaderPath, isPath);
        }
        
        if(mustReleaseContext)getContext().release();
	}

	
	public void initStencil(GL2 gl, boolean state){
		if(stereo_InterleavedStencil==state)return;
		stereo_InterleavedStencil = state;
		
		//turn it on
		if(stereo_InterleavedStencil){
			System.out.printf("init stencil %d %d\n",Width, Height);
		    gl.glEnable(GL2.GL_STENCIL_TEST);
		    gl.glClearStencil(0x0);
		    gl.glClear(GL2.GL_STENCIL_BUFFER_BIT);	    
		    gl.glStencilFunc(GL2.GL_EQUAL, 0x1, 0x1);	    
		    gl.glStencilOp(GL2.GL_KEEP, GL2.GL_KEEP, GL2.GL_KEEP);			
			
			
		    //don't draw to the screen the pixel, but use it to increment the stencil counter
		    gl.glStencilFunc(GL2.GL_ALWAYS, 0x1, 0x1);
		    gl.glStencilOp(GL2.GL_REPLACE, GL2.GL_REPLACE, GL2.GL_REPLACE);
		    
		    byte dataStencilArray [] = new byte[Width*Height];
	    	for(int j=0;j<Height;j++){
	    		for(int i=0;i<Width;i++){
		    		if(j%2==0)dataStencilArray[j*Width+i] =  0;		    		
		    		else   dataStencilArray[j*Width+i] =  (byte)1;       
		    	}
		    }
	    	
		    ByteBuffer dataStencil = GLBuffers.newDirectByteBuffer(dataStencilArray);
		    gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 1);
		    gl.glDrawBuffer(GL2.GL_STENCIL);
		    gl.glDrawPixels(Width, Height, GL2.GL_STENCIL_INDEX, GL2.GL_UNSIGNED_BYTE, dataStencil);
		    
/*
		    ByteBuffer dataStencil2 = ByteBuffer.allocate(Width*Height);
		    gl.glPixelStorei(GL2.GL_PACK_ALIGNMENT, 1);
		    gl.glReadBuffer(GL2.GL_STENCIL);		    
		    gl.glReadPixels(0, 0, Width, Height, GL2.GL_STENCIL_INDEX, GL2.GL_UNSIGNED_BYTE, dataStencil2);

		    byte dataStencilArray2 [] = new byte[Width*Height];
		    dataStencil2.get(dataStencilArray2, 0, dataStencilArray2.length);
		    for(int j=0;j<5;j++){
		    	for(int i=0;i<5;i++){		    	
		    		System.out.printf("%d ", (int)dataStencilArray2[j*Width+i]);		    	
		    	}System.out.printf("\n");
		    }		    
*/		    
		    //don't change stencil value, but use it to test wether the pixel should be drawn or not
		    //gl.glStencilFunc(GL2.GL_NOTEQUAL, 0x1, 0x1);
		    gl.glStencilOp(GL2.GL_KEEP, GL2.GL_KEEP, GL2.GL_KEEP);				        
		//turn it off
		}else{
			gl.glDisable(GL2.GL_STENCIL_TEST);			
		}
	}
	
	
}




