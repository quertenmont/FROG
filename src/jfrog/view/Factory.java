package jfrog.view;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JTabbedPane;

import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.GLContext;

public class Factory {
	public static Factory factory = new Factory();
	
	public static HashMap<String, Class<?> > objectTable = new HashMap<String,Class<?> >();
	
	public Factory(){
	}			
			
	static String[] resourceList(String path) throws URISyntaxException, IOException {
		//URL dirURL = jfrog.Common.class.getClassLoader().getResource(path);
		URL dirURL = jfrog.Common.class.getResource(path);		
		if (dirURL != null && dirURL.getProtocol().equals("file")) {
			/* A file path: easy enough */
			return new File(dirURL.toURI()).list();
		} 

		if (dirURL == null) {
			/* 
			 * In case of a jar file, we can't actually find a directory.
			 * Have to assume the same jar as clazz.
			 */
			String me = jfrog.Common.class.getName().replace(".", "/")+".class";
			dirURL = jfrog.Common.class.getClassLoader().getResource(me);
		}

		if (dirURL.getProtocol().equals("jar")) {
			/* A JAR path */		
			JarURLConnection conn = (JarURLConnection)dirURL.openConnection();
		    JarFile jar = conn.getJarFile();							
			Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
			Set<String> result = new HashSet<String>(); //avoid duplicates in case it is a subdirectory
			if(path.indexOf("/")==0)path = path.substring(1);
			while(entries.hasMoreElements()) {
				String name = entries.nextElement().getName();
				if (name.startsWith(path)) { //filter according to the path
					String entry = name.substring(path.length());
					int checkSubdir = entry.indexOf("/");
					if (checkSubdir >= 0) {
						// if it is a subdirectory, we just return the directory name
						entry = entry.substring(0, checkSubdir);
					}
					result.add(entry);
				}
			}
			return result.toArray(new String[result.size()]);
		} 

		throw new UnsupportedOperationException("Cannot list files for URL "+dirURL);
	}	
	
	
	
	public void LoadClasses(){				
		URL url = jfrog.Common.class.getResource("/jfrog/view/");
		if (url.getProtocol().equals("jar")){			
			String jarPath = new String(url.getPath().substring(url.getPath().indexOf("jar:")+1, url.getPath().indexOf("!"))); //strip out only the JAR file
			try {
				url = new URL(jarPath);
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block				
				e1.printStackTrace();
			}			
		}
		
		String[] listOfClass = new String[0];
		try {
			listOfClass = resourceList("/jfrog/view/");
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
											
		URLClassLoader myClassLoader = URLClassLoader.newInstance(new URL[]{url}, this.getClass().getClassLoader()); 
		
		for (int i = 0; i < listOfClass.length; i++) {					
			if(!listOfClass[i].endsWith(".class"))continue;
			if(listOfClass[i].equals("Factory.class"))continue;					
			if(listOfClass[i].startsWith("SimpleTest"))continue;			
			if(listOfClass[i].equals("ViewOverlay.class"))continue;
			if(listOfClass[i].equals("ViewOverlay$CustomJoglOverlay.class"))continue;
			if(listOfClass[i].equals("TabbedFPSAnimator.class"))continue;
							          	                    					
		    String classNameToBeLoaded = "jfrog.view." + listOfClass[i].substring(0, listOfClass[i].lastIndexOf(".class"));
		  	try {
				Class<?> myClass = myClassLoader.loadClass(classNameToBeLoaded);
				jfrog.view.View classInstance = (jfrog.view.View) myClass.newInstance();
				if(classInstance==null)continue;
				objectTable.put(myClass.getSimpleName(), myClass);				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.printf("class %s does not extend the jfrog.view.View class or as no empty constructor, this class is skipped\n", listOfClass[i]);
				continue;
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		  				
		}		    

/*		
	    Set<String> e = objectTable.keySet();
	    e = new TreeSet<String>(e);	    		
	    Iterator<String> it = e.iterator();
	    System.out.printf("The following (view) Classes have been loaded : \n");	    
	    while(it.hasNext()){	    	
	    	String el = (String)it.next();
	        System.out.printf("Name=%15s - %s\n",el, (objectTable.get(el)).getName());
	    }	
	    System.out.printf("-----------------------------------------\n");
*/	    	    		
	}
	
	public static void createViews(GLCapabilitiesImmutable caps, GLContext context, JTabbedPane tab){		
		if( jfrog.Common.parser==null)return;
		
		String [] activeViews = jfrog.Common.parser.toStringArr("ActiveViews");
		for(int i=0;i<activeViews.length;i++){
			if(i>1)continue;
			
			String type  = jfrog.Common.parser.toString(activeViews[i] + "_Type");
			String label = jfrog.Common.parser.toString(activeViews[i] + "_Label");
			Class<?> viewClass = jfrog.view.Factory.objectTable.get(type);
			if(viewClass==null){System.out.printf("No class associated to viewType: %s\n", type); continue;}
			try {
				Constructor<?> cstr = viewClass.getConstructor(new Class [] {Class.forName("com.jogamp.opengl.GLCapabilitiesImmutable"), Class.forName("com.jogamp.opengl.GLContext")});
				View v = (View)cstr.newInstance(caps, context);		    	
		    	tab.add(v, label!=null ? label : activeViews[i]);
		    	v.name  = activeViews[i];
		    	v.label = label;		    	
		    	v.ParseParameter();		  
		    	jfrog.Common.animator.add(v);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	
	
}
