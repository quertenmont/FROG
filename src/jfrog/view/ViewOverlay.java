package jfrog.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLDrawable;
import com.jogamp.opengl.GLException;

import com.jogamp.opengl.util.awt.TextureRenderer;


public class ViewOverlay {
	CustomJoglOverlay ol = null;	
	long FPS_startTime = 0;
	int  FPS_frameCounter = 0;	
	int width=0;
	int height=0;
	boolean mustUpdate = true;
	
	String collablogoFile  = "";
	Image  collabLogo      = null;	
	String textRaw     = "";
	String [] textProcessed = {};		
	
	Color Alpha  = new Color(0, 0, 0, 0);
	Color White  = new Color(255, 255, 255, 255);
	Font  myfont = new Font("Courier",Font.BOLD,18);

	ViewOverlay(GLAutoDrawable arg0){
		ol = new CustomJoglOverlay(arg0);
		this.setSize(arg0.getSurfaceWidth(), arg0.getSurfaceHeight());
		
		if(jfrog.Common.parser!=null){
			collablogoFile = jfrog.Common.parser.toString("ColabLogo");
			textRaw	       = jfrog.Common.parser.toString("OverlayText");		
		}
		
		if(collablogoFile!=null && collablogoFile!=""){
			collabLogo = Toolkit.getDefaultToolkit().getImage(jfrog.Common.GetURL(collablogoFile));
		}
		updateText();
		Draw();
		mustUpdate = true;
	}

	public void updateText(){
		if(textRaw==null || textRaw=="")return;
		String tmpOverlayText = "";	
		for(int i=0;i<textRaw.length();i++){
			char c = textRaw.charAt(i);
			if(c=='\\' && i+1<textRaw.length() && textRaw.charAt(i+1)=='n'){c='\n';i++;}
			tmpOverlayText += c;
		}
				
		if(jfrog.Common.events.getEvent()!=null){			
			tmpOverlayText = tmpOverlayText.replaceAll("%R", String.format("%d", jfrog.Common.events.getEvent().run  ));
			tmpOverlayText = tmpOverlayText.replaceAll("%E", String.format("%d", jfrog.Common.events.getEvent().event));			
			tmpOverlayText = tmpOverlayText.replaceAll("%T", String.format("%s", jfrog.Common.events.getEvent().name ));			
		}
		
		textProcessed = tmpOverlayText.split("\n");
		
		int FontSize = (int)(0.08*this.height/(textProcessed.length));
		if(collabLogo!=null) FontSize = (int)( Math.max(FontSize, collabLogo.getHeight(null)/(8*(textProcessed.length)) ) );		
		myfont = new Font("Times New Roman",Font.PLAIN,FontSize);
		
		mustUpdate = true;
	}

	public void setSize(int width, int height){
		this.width = width;
		this.height = height;
		ol.setSize(this.width, this.height);
		updateText();
		mustUpdate = true;
	}	
	
	public void refresh(){
		updateText();
		mustUpdate = true;		
	}

	void Draw(){				
		if(jfrog.Common.show_FPS)FPS_frameCounter++;
		if(!mustUpdate && FPS_frameCounter!=30){ol.drawAll();return;}


		Graphics2D g = ol.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setComposite(AlphaComposite.Src);
		if(jfrog.Common.show_FPS){
			//FPS_frameCounter++;
			if(FPS_frameCounter==30){			
				g.setColor(Alpha);
				g.fillRect(this.width-50, this.height-20, 50, 20);
				g.setColor(White);
				g.drawString(String.format("FPS:% 3.0f",30.0f * 1000.0f / (System.currentTimeMillis()-FPS_startTime)), this.width-50, this.height-1);	   
				ol.markDirty(this.width-50, this.height-21, 50, 20);

				FPS_frameCounter=0;
				FPS_startTime = System.currentTimeMillis();						
			}
		}else{
			if(mustUpdate){
				g.setColor(Alpha);
				g.fillRect(this.width-50, this.height-20, 50, 20);				
				ol.markDirty(this.width-50, this.height-21, 50, 20);
			}
		}

		if(mustUpdate && jfrog.Common.show_FrogLogo && jfrog.Common.Logo_Frog!=null){
			float R = jfrog.Common.Logo_Frog.getHeight(null) / (float)jfrog.Common.Logo_Frog.getWidth(null);
//			int W = Math.max((int)(0.08 *this.width) ,jfrog.Common.Logo_Frog.getWidth(null)/8);
//			int H = java.lang.Math.min((int)(R*W),this.height);
			int H = Math.max((int)(0.05*this.height) ,jfrog.Common.Logo_Frog.getHeight(null)/8);
			int W = java.lang.Math.min((int)(H/R),this.width);			
			int X = 0;
			int Y = this.height - H;			
			g.setColor(Alpha);
			g.fillRect(X, Y, W, H);			
			g.drawImage(jfrog.Common.Logo_Frog, X, Y, W, H, null);
			ol.markDirty(X, Y, W, H);
		}


		if(mustUpdate && collabLogo!=null){
			float R = collabLogo.getHeight(null) / (float)collabLogo.getWidth(null);
//			int W = Math.max((int)(0.08 *this.width) ,Logo_Collab.getWidth(null)/8);
//			int H = java.lang.Math.min((int)(R*W),this.height);
			int H = Math.max((int)(0.08*this.height) ,collabLogo.getHeight(null)/8);			
			int W = java.lang.Math.min((int)(H/R),this.width);			
			//System.out.printf("WindowH =%d    H = %d\n",this.height, H);
			int X = this.width - W - 1;
			int Y = 1;			
			g.setColor(Alpha);
			g.fillRect(X, Y, W, H);			
			g.drawImage(collabLogo, X, Y, W, H, null);
			ol.markDirty(X, Y, W, H);


			g.setFont(myfont);			
			FontRenderContext frc = g.getFontRenderContext();
			X-=3;
			for(int s=0;s<textProcessed.length;s++){				
				Rectangle2D bounds = myfont.getStringBounds(textProcessed[s], frc);
				Y-=(bounds.getHeight()/3);
				g.setColor(Alpha);
				g.fillRect((int)(X-bounds.getWidth()-100), (int)(Y+bounds.getHeight()/3), (int)bounds.getWidth()+100, (int)bounds.getHeight());
				g.setColor(White);
				g.drawString(textProcessed[s], (int)(X-bounds.getWidth()), (int)(Y+bounds.getHeight()));
				ol.markDirty((int)(X-bounds.getWidth()-100), (int)(Y+bounds.getHeight()/3), (int)bounds.getWidth()+100, (int)bounds.getHeight());
				Y+=(bounds.getHeight()/3);
				Y+=bounds.getHeight();
			}
		}		

		g.dispose();
		ol.drawAll();	
		mustUpdate = false;		
	}






	public class CustomJoglOverlay{
		private TextureRenderer renderer;
		private boolean contentsLost;
		private int width;
		private int height;

		/** Creates a new Java 2D overlay on top of the specified
	      GLDrawable. */
		public CustomJoglOverlay(GLDrawable drawable) {
			this.width = drawable.getSurfaceWidth();
			this.height = drawable.getSurfaceHeight();
		}

		/** Creates a {@link java.awt.Graphics2D Graphics2D} instance for
	      rendering into the overlay. The returned object should be
	      disposed of using the normal {@link java.awt.Graphics#dispose()
	      Graphics.dispose()} method once it is no longer being used.

	      @return a new {@link java.awt.Graphics2D Graphics2D} object for
	        rendering into the backing store of this renderer
		 */
		public Graphics2D createGraphics() {
			// Validate the size of the renderer against the current size of
			// the drawable
			validateRenderer();
			return renderer.createGraphics();
		}

		/** Indicates whether the Java 2D contents of the overlay were lost
	      since the last time {@link #createGraphics} was called. This
	      method should be called immediately after calling {@link
	      #createGraphics} to see whether the entire contents of the
	      overlay need to be redrawn or just the region the application is
	      interested in updating.

	      @return whether the contents of the overlay were lost since the
	        last render
		 */
		public boolean contentsLost() {
			return contentsLost;
		}

		/** Marks the given region of the overlay as dirty. This region, and
	      any previously set dirty regions, will be automatically
	      synchronized with the underlying Texture during the next {@link
	      #draw draw} or {@link #drawAll drawAll} operation, at which
	      point the dirty region will be cleared. It is not necessary for
	      an OpenGL context to be current when this method is called.

	      @param x the x coordinate (in Java 2D coordinates -- relative to
	        upper left) of the region to update
	      @param y the y coordinate (in Java 2D coordinates -- relative to
	        upper left) of the region to update
	      @param width the width of the region to update
	      @param height the height of the region to update

	      @throws GLException If an OpenGL context is not current when this method is called */
		public void markDirty(int x, int y, int width, int height) {
			renderer.markDirty(x, y, width, height);
		}

		/** Draws the entire contents of the overlay on top of the OpenGL
	      drawable. This is a convenience method which encapsulates all
	      portions of the rendering process; if this method is used,
	      {@link #beginRendering}, {@link #endRendering}, etc. should not
	      be used. This method should be called while the OpenGL context
	      for the drawable is current, and after your OpenGL scene has
	      been rendered.

	      @throws GLException If an OpenGL context is not current when this method is called
		 */
		public void drawAll() throws GLException {
			beginRendering();
			draw(0, 0, this.width, this.height);
			endRendering();
		}

		/** Begins the OpenGL rendering process for the overlay. This is
	      separated out so advanced applications can render independent
	      pieces of the overlay to different portions of the drawable.

	      @throws GLException If an OpenGL context is not current when this method is called
		 */
		public void beginRendering() throws GLException {
			renderer.beginOrthoRendering(this.width, this.height);
		}

		/** Ends the OpenGL rendering process for the overlay. This is
	      separated out so advanced applications can render independent
	      pieces of the overlay to different portions of the drawable.

	      @throws GLException If an OpenGL context is not current when this method is called
		 */
		public void endRendering() throws GLException {
			renderer.endOrthoRendering();
		}

		/** Draws the specified sub-rectangle of the overlay on top of the
	      OpenGL drawable. {@link #beginRendering} and {@link
	      #endRendering} must be used in conjunction with this method to
	      achieve proper rendering results. This method should be called
	      while the OpenGL context for the drawable is current, and after
	      your OpenGL scene has been rendered.

	      @param x the lower-left x coordinate (relative to the lower left
	        of the overlay) of the rectangle to draw
	      @param y the lower-left y coordinate (relative to the lower left
	        of the overlay) of the rectangle to draw
	      @param width the width of the rectangle to draw
	      @param height the height of the rectangle to draw

	      @throws GLException If an OpenGL context is not current when this method is called
		 */
		public void draw(int x, int y, int width, int height) throws GLException {
			draw(x, y, x, y, width, height);
		}

		/** Draws the specified sub-rectangle of the overlay at the
	      specified x and y coordinate on top of the OpenGL drawable.
	      {@link #beginRendering} and {@link #endRendering} must be used
	      in conjunction with this method to achieve proper rendering
	      results. This method should be called while the OpenGL context
	      for the drawable is current, and after your OpenGL scene has
	      been rendered.

	      @param screenx the on-screen x coordinate at which to draw the rectangle
	      @param screeny the on-screen y coordinate (relative to lower left) at
	        which to draw the rectangle
	      @param overlayx the x coordinate of the pixel in the overlay of
	        the lower left portion of the rectangle to draw
	      @param overlayy the y coordinate of the pixel in the overlay
	        (relative to lower left) of the lower left portion of the
	        rectangle to draw
	      @param width the width of the rectangle to draw
	      @param height the height of the rectangle to draw

	      @throws GLException If an OpenGL context is not current when this method is called
		 */
		public void draw(int screenx, int screeny,
				int overlayx, int overlayy,
				int width, int height) throws GLException {
			renderer.drawOrthoRect(screenx, screeny,
					overlayx, overlayy,
					width, height);
		}


		public void setSize(int width, int height){
			this.width  = width;
			this.height = height;
		}


		//----------------------------------------------------------------------
		// Internals only below this point
		//

		private void validateRenderer() {
			if (renderer == null) {
				renderer = new TextureRenderer(this.width,
						this.height,
						true);
				contentsLost = true;
			} else if (renderer.getWidth() != this.width ||
					renderer.getHeight() != this.height) {
				renderer.setSize(this.width, this.height);
				contentsLost = true;
			} else {
				contentsLost = false;
			}
		}

	}	
}







