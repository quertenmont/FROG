
package jfrog.object;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import com.jogamp.opengl.GL2;

import java.lang.Math;

import com.jogamp.opengl.util.GLBuffers;

public class Event_Jet extends Base{
	float p, eta, phi;
    long componentsColl;
    ArrayList<Long> componentsId = new ArrayList<Long>();
    ArrayList<Base> components = new ArrayList<Base>();	
	
	public int ChunkId() {	return 13100; }
	public boolean isCompactible() {return false;}    

	public String toString() {
		return String.format("Event_Jet [p=%s, eta=%s, phi=%s]", p, eta, phi);
	}
	
	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};
		p  		= Factory.getFloat (din, BytesRead);
		eta    	= Factory.getFloat (din, BytesRead);
		phi  	= Factory.getFloat (din, BytesRead);		
		if(BytesRead[0]>=bytesToRead)return BytesRead[0];		
		
		componentsColl = Factory.getUInt (din, BytesRead);
		int NComp = (bytesToRead-BytesRead[0])/4;
		for(int i=0;i<NComp;i++)componentsId.add(Factory.getUInt (din, BytesRead));		
		
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(16 + 4*componentsId.size());		
		Factory.putFloat (toReturn, p);
		Factory.putFloat (toReturn, eta);
		Factory.putFloat (toReturn, phi);		
		
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
			
			
			float [] positionData = new float[12*(3+3)*3]; 											
			float [] normalData   = new float[12*(3+3)*3]; 
			int  [] CubeIndex  	= new int[12*(3+3)  ]; 

			float dphi 	  = (float)Math.PI/6;
			float L      = (float)Math.log(1+p)*10;			
			float Radius = L*0.3f;
		
			int Index=0;
			for(float phi=0; phi<=6.2831f;phi+=dphi){
				positionData[Index+0 ] = 0;
				positionData[Index+1 ] = 0;
				positionData[Index+2 ] = 0;
				positionData[Index+3 ] = (float)(Radius*Math.cos(phi+dphi));
				positionData[Index+4 ] = (float)(Radius*Math.sin(phi+dphi));
				positionData[Index+5 ] = L;
				positionData[Index+6 ] = (float)(Radius*Math.cos(phi));
				positionData[Index+7 ] = (float)(Radius*Math.sin(phi));
				positionData[Index+8 ] = L;				
				Index+=9;
			}
			for(float phi=0; phi<=6.2831f;phi+=dphi){
				positionData[Index+0 ] = 0;
				positionData[Index+1 ] = 0;
				positionData[Index+2 ] = L;
				positionData[Index+3 ] = (float)(Radius*Math.cos(phi));
				positionData[Index+4 ] = (float)(Radius*Math.sin(phi));
				positionData[Index+5 ] = L;
				positionData[Index+6 ] = (float)(Radius*Math.cos(phi+dphi));
				positionData[Index+7 ] = (float)(Radius*Math.sin(phi+dphi));
				positionData[Index+8 ] = L;
				Index+=9;
			}

			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glRotatef(57.29f*phi                       		  ,0,0,1);
			gl.glRotatef(57.29f*jfrog.Common.Coord_EtaToTheta(eta),0,1,0);	
			TransformVerticesArray(gl, positionData);
			gl.glPopMatrix();
			
			for(short i=0;i<CubeIndex.length;i++){CubeIndex[i] = i;}
			
			SetNormalArray(positionData, CubeIndex,         0, 12*(3+3), 3, normalData);
			
			
			IntBuffer CubeIndexBuf    = GLBuffers.newDirectIntBuffer(CubeIndex);
			FloatBuffer positionDataBuf = GLBuffers.newDirectFloatBuffer(positionData);					
			FloatBuffer normalDataBuf   = GLBuffers.newDirectFloatBuffer(normalData);

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
				gl.glDrawElements   (    GL2.GL_TRIANGLES, 12*(3+3), GL2.GL_UNSIGNED_INT, CubeIndexBuf);								
				gl.glEndList();			
			}
		}else if(VBO_Vertices[0]>0){
			if(jfrog.Common.support_VBO){
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );	gl.glNormalPointer  (    GL2.GL_FLOAT, 0, 0 );							
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );	gl.glVertexPointer  ( 3, GL2.GL_FLOAT, 0, 0 );					
				gl.glDrawElements   (    GL2.GL_TRIANGLES, 12*(3+3), GL2.GL_UNSIGNED_INT, 0);
			}else{			
				gl.glCallList(VBO_Vertices[0]);						
			}
			
			
			//Draw Jet constituents
			for(int i=0;i<components.size();i++){
				components.get(i).draw(gl);
			}			
		}
	}	
	
		
	
}
