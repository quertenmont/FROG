
package jfrog.object;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import com.jogamp.opengl.GL2;

import com.jogamp.opengl.util.GLBuffers;

public class Event_Photon extends Base{
	public float p, eta, phi, vx, vy, vz, cx, cy, cz;
    long componentsColl;
    ArrayList<Long> componentsId = new ArrayList<Long>();
    ArrayList<Base> components = new ArrayList<Base>();	
	
	public int ChunkId() {	return 16100; }
	public boolean isCompactible() {return false;}    

	public String toString() {
		return "Event_Photon [p=" + p + ", eta=" + eta + ", phi=" + phi + ", vx="
				+ vx + ", vy=" + vy + ", vz=" + vz + ", cx=" + cx + ", cy="
				+ cy + ", cz=" + cz + "]";
	}
	
	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};
		p  		= Factory.getFloat (din, BytesRead);
		eta    	= Factory.getFloat (din, BytesRead);
		phi  	= Factory.getFloat (din, BytesRead);		
		vx  	= Factory.getFloat (din, BytesRead);
		vy  	= Factory.getFloat (din, BytesRead);
		vz  	= Factory.getFloat (din, BytesRead);
		cx  	= Factory.getFloat (din, BytesRead);
		cy  	= Factory.getFloat (din, BytesRead);
		cz  	= Factory.getFloat (din, BytesRead);		
		if(BytesRead[0]>=bytesToRead)return BytesRead[0];		
		
		componentsColl = Factory.getUInt (din, BytesRead);
		int NComp = (bytesToRead-BytesRead[0])/4;
		for(int i=0;i<NComp;i++)componentsId.add(Factory.getUInt (din, BytesRead));		
		
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(40 + 4*componentsId.size());		
		Factory.putFloat (toReturn, p);
		Factory.putFloat (toReturn, eta);
		Factory.putFloat (toReturn, phi);	
		Factory.putFloat (toReturn, vx);		
		Factory.putFloat (toReturn, vy);
		Factory.putFloat (toReturn, vz);			
		Factory.putFloat (toReturn, cx);		
		Factory.putFloat (toReturn, cy);
		Factory.putFloat (toReturn, cz);		
		
		if(componentsId.size()>0){
			Factory.putUInt (toReturn, componentsColl);
			for(int i=0;i<componentsId.size();i++){
			Factory.putUInt (toReturn, componentsId.get(i));				
			}
		}			
		return toReturn;		
	}
	

	public void draw(GL2 gl) {					
		if(VBO_Vertices[0]==0){
			Base coll = jfrog.Common.events.findId(componentsColl);
			if(coll==null)VBO_Vertices[0] = -1;
			for(int i=0;i<componentsId.size();i++){
				Base obj = coll.findId(componentsId.get(i));
				if(obj!=null)components.add(obj);				
			}			
			
			
			float [] positionData = new float[50*3];
			int   [] CubeIndex    = new int  [50];			
			 
			for(int i=0;i<50;i++){
			   positionData[3*i+0] = vx + (cx-vx)*i/50.0f;
			   positionData[3*i+1] = vy + (cy-vy)*i/50.0f;
			   positionData[3*i+2] = vz + (cz-vz)*i/50.0f;
			}
			for(short i=0;i<CubeIndex.length;i++){CubeIndex[i] = i;}
						
			IntBuffer CubeIndexBuf    = GLBuffers.newDirectIntBuffer(CubeIndex);
			FloatBuffer positionDataBuf = GLBuffers.newDirectFloatBuffer(positionData);					
			FloatBuffer normalDataBuf   = GLBuffers.newDirectFloatBuffer(positionData.length);

			if(jfrog.Common.support_VBO){					
				gl.glGenBuffers( 4, VBO_Vertices, 0);								// Get A Valid Name
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );	// Bind The Buffer
				gl.glBufferData( GL2.GL_ELEMENT_ARRAY_BUFFER, CubeIndexBuf.capacity() * 4,CubeIndexBuf, GL2.GL_STATIC_DRAW );				
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );			// Bind The Buffer
				gl.glBufferData( GL2.GL_ARRAY_BUFFER, positionDataBuf.capacity() * 4, positionDataBuf, GL2.GL_STATIC_DRAW );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );			// Bind The Buffer
				gl.glBufferData( GL2.GL_ARRAY_BUFFER, normalDataBuf.capacity() * 4, normalDataBuf, GL2.GL_STATIC_DRAW );							
			}else{
				VBO_Vertices[0] = gl.glGenLists(1);			
				gl.glNewList(VBO_Vertices[0], GL2.GL_COMPILE_AND_EXECUTE);

				gl.glNormalPointer  (    GL2.GL_FLOAT, 0, normalDataBuf );				
				gl.glVertexPointer  ( 3, GL2.GL_FLOAT, 0, positionDataBuf );			
				gl.glDrawElements   (    GL2.GL_LINES, 50, GL2.GL_UNSIGNED_INT, CubeIndexBuf);								
				gl.glEndList();			
			}
		}else if(VBO_Vertices[0]>0){
			if(jfrog.Common.support_VBO){
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );	gl.glNormalPointer  (    GL2.GL_FLOAT, 0, 0 );							
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );	gl.glVertexPointer  ( 3, GL2.GL_FLOAT, 0, 0 );					
				gl.glDrawElements   (    GL2.GL_LINES, 50, GL2.GL_UNSIGNED_INT, 0);
			}else{			
				gl.glCallList(VBO_Vertices[0]);						
			}
						
			//Draw constituents
			for(int i=0;i<components.size();i++){
				components.get(i).draw(gl);
			}			
		}
	}	
	
		
	
}
