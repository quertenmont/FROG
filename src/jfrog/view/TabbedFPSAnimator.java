package jfrog.view;

import javax.swing.JTabbedPane;

import com.jogamp.opengl.util.FPSAnimator;


public class TabbedFPSAnimator extends FPSAnimator {
	/*    private Timer timer = null;
    private TimerTask task = null;
    private int fps;
    private boolean scheduleAtFixedRate;
    private volatile boolean shouldRun;
*/
    protected String getBaseName(String prefix) {
        return "FPS" + prefix + "Animator" ;
    }

    /** Creates an FPSAnimator with a given target frames-per-second
    value. Equivalent to <code>FPSAnimator(null, fps)</code>. */
    public TabbedFPSAnimator(JTabbedPane tab, int fps) {
    	super(fps, true);
    }
    
//    public void start(){}
//    public void stop(){}
//    public void pause(){}
//    public void resume(){}
    
    /** Called every frame to cause redrawing of all of the
    GLAutoDrawables this Animator manages. Subclasses should call
    this to get the most optimized painting behavior for the set of
    components this Animator manages, in particular when multiple
    lightweight widgets are continually being redrawn. */

    
/*    protected void display() {
        GLAutoDrawable drawable = (GLAutoDrawable)tab.getSelectedComponent();
        if(drawable!=null){
        	try {
        		drawable.display();
        	} catch (RuntimeException e) {
        		e.printStackTrace();
        		throw(e);
        	}
        }
//        fpsCounter.tickFPS();        
    }    
*/
}
