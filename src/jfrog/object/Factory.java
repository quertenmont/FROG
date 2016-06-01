package jfrog.object;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Factory {
	public static HashMap<Integer, Class<?> > objectTable = new HashMap<Integer,Class<?> >();
	public static int NCall=0;
	public static long CallTime=0;
	
	
	public Factory(){
	}		
	
	
	public static String chunkIdToString(int chunkId){
		switch(chunkId){
			case 55555:	return "Frog";
			case 20000:	return "Geometry";			
		}	
		return"";
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
		URL url = jfrog.Common.class.getResource("/jfrog/object/");
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
			listOfClass = resourceList("/jfrog/object/");
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
			if(listOfClass[i].equals("Events.class"))continue;			
				          	                    	    
		    String classNameToBeLoaded = "jfrog.object." + listOfClass[i].substring(0, listOfClass[i].lastIndexOf(".class"));
		  	try {
				Class<?> myClass = myClassLoader.loadClass(classNameToBeLoaded);
				jfrog.object.Base classInstance = (jfrog.object.Base)(myClass.newInstance());
				objectTable.put(classInstance.ChunkId(), myClass);				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		  				
		}		    

	    objectTable.put(20000,objectTable.get(55555));	//Just here for compatibility reasons

	    /*	    
	    Set<Integer> e = objectTable.keySet();
	    e = new TreeSet<Integer>(e);	    		
	    Iterator<Integer> it = e.iterator();
	    System.out.printf("The following (object) Classes have been loaded : \n");	    
	    while(it.hasNext()){	    	
	    	Integer el = (Integer)it.next();
	        System.out.printf("Id=%7d - %s\n",el.intValue(), (objectTable.get(el)).getName());
	    }	
	    System.out.printf("-----------------------------------------\n");
	    */   		
	}	
	

	
	public static int read(URL url, DataInputStream din, jfrog.object.Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		if(bytesToRead<6 && level>0)return 0;
		
		int TotalBytesRead = 0;		
		do{
			int [] BytesRead = {0};
			//System.out.printf("A Read=%d / %d\n", BytesRead,BytesToRead);
			//			try {			

			//BytesRead[0] += 6;				
			int chunkId = Factory.getUShort(din, BytesRead);
			int chunkSize = Factory.getInt(din, BytesRead);				
			String chunkName = chunkIdToString(chunkId);
			if(bytesToRead==0){
				//Reading main chunk					
				bytesToRead = chunkSize;
				//if(jfrog.Common.loading!=null){jfrog.Common.loading.setMaxValue(BytesToRead);}
			}
			//System.out.printf("L%d S Read (%d)=%d / %d ChunkSize = %d\n",level, chunkId, BytesRead[0],bytesToRead, chunkSize);				
			Class<?> ObjClass =  objectTable.get(chunkId);				
			if(ObjClass!=null){
				while(BytesRead[0]<chunkSize){						
					//System.out.println("Chunk = " + chunkId + " Size = " + chunkSize + " Read = " + BytesRead[0] + " toRead = " + bytesToRead);
					jfrog.object.Base obj = null;
					try {
						obj = (jfrog.object.Base)(ObjClass.newInstance());
						if(chunkName!="")obj.name = chunkName;
						//makeObjectLinks(obj, parent);						
						int tmpBytesRead = obj.read(url,din,parent, chunkSize-BytesRead[0], fileOffset+TotalBytesRead+BytesRead[0], level+1);						
						BytesRead[0] += tmpBytesRead; 
						//if(jfrog.Common.loading!=null && obj.daughters.size()==0)jfrog.Common.loading.setCurrentValue(jfrog.Common.loading.getCurrentValue() + tmpBytesRead);							

					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}						
				}
			}else{
				System.out.printf("Skip unknown chunk %d\n",chunkId);
				din.skipBytes(bytesToRead-BytesRead[0]);
				BytesRead[0] = bytesToRead;
				//System.exit(0);
				//if(jfrog.Common.loading!=null)jfrog.Common.loading.setCurrentValue(jfrog.Common.loading.getCurrentValue() + BytesRead);
			}			
			//System.out.printf("L%d E Read (%d)=%d / %d ChunkSize = %d\n",level, chunkId, BytesRead[0],bytesToRead, chunkSize);

			TotalBytesRead += BytesRead[0];
		}while(TotalBytesRead<bytesToRead);
		return TotalBytesRead;
	}	

	
	/*
	static void makeObjectLinks(Base obj, Base parent){
//		obj.mother = parent;
		if(parent!=null){
//			parent.daughters.add(obj);
			if(parent.style!=null){
				obj.style = parent.style;			
				obj.style.objList.add(obj);
			}
		}
	}	*/
	
	static byte  getByte  (DataInputStream din, int [] bytesRead) throws IOException{bytesRead[0]+=1; return din.readByte();	}			
	static float getFloat (DataInputStream din, int [] bytesRead) throws IOException{bytesRead[0]+=4; return Float.intBitsToFloat(Integer.reverseBytes(din.readInt()));	}	
	static int   getInt   (DataInputStream din, int [] bytesRead) throws IOException{bytesRead[0]+=4; return Integer.reverseBytes(din.readInt());	}			
	static long  getUInt  (DataInputStream din, int [] bytesRead) throws IOException{bytesRead[0]+=4; return (0x00000000FFFFFFFFL & (long)Integer.reverseBytes(din.readInt()));	}		
	static int   getUShort(DataInputStream din, int [] bytesRead) throws IOException{bytesRead[0]+=2; return (0x0000FFFF & (int)Short.reverseBytes(din.readShort()));}
	static long  getLong  (DataInputStream din, int [] bytesRead) throws IOException{bytesRead[0]+=8; return Long.reverseBytes(din.readLong());	}	
	
	static void  putByte  (ByteBuffer B, byte  arg){B.put     (arg);}
	static void  putFloat (ByteBuffer B, float arg){B.putInt  (Integer.reverseBytes(Float.floatToIntBits(arg)));}
	static void  putInt   (ByteBuffer B, int   arg){B.putInt  (Integer.reverseBytes(arg));}	
	static void  putUInt  (ByteBuffer B, long  arg){B.putInt  (Integer.reverseBytes((int)arg));}
	static void  putUShort(ByteBuffer B, int   arg){B.putShort(Short.reverseBytes((short)arg));}		
	static void  putLong  (ByteBuffer B, long  arg){B.putLong (Long.reverseBytes(arg));}	
}
