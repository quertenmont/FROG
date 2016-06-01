package jfrog.object;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL2;

import com.jogamp.opengl.util.GLBuffers;

public class Prim_Line extends Base {
	float P1X;    float P1Y;  float P1Z;
	float P2X;    float P2Y;  float P2Z;	
		
	public int ChunkId() {	return 43020; }
	public boolean isCompactible() {return true;}	

	public String toString() {
		return "Prim_Line [P1X=" + P1X + ", P1Y=" + P1Y + ", P1Z=" + P1Z
				+ ", P2X=" + P2X + ", P2Y=" + P2Y + ", P2Z=" + P2Z + "]";
	}
	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};
		id 			= Factory.getUInt  (din, BytesRead);
		P1X    		= Factory.getFloat (din, BytesRead);
		P1Y    		= Factory.getFloat (din, BytesRead);
		P1Z 	   	= Factory.getFloat (din, BytesRead);
		P2X    		= Factory.getFloat (din, BytesRead);
		P2Y    		= Factory.getFloat (din, BytesRead);
		P2Z 	   	= Factory.getFloat (din, BytesRead);		
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(36);		
		Factory.putUInt  (toReturn, id);	
		Factory.putFloat (toReturn, P1X);
		Factory.putFloat (toReturn, P1Y);
		Factory.putFloat (toReturn, P1Z);
		Factory.putFloat (toReturn, P2X);
		Factory.putFloat (toReturn, P2Y);
		Factory.putFloat (toReturn, P2Z);				
		return toReturn;		
	}			
	
	public void draw(GL2 gl) {				
		if(VBO_Vertices[0]==0){
			float [] positionData   = new float[]{
			P1X, P1Y,	P1Z,	P2X, P2Y,	P2Z
	};							
			int [] CubeIndex  = {0,1};
			int [] CubeIndexWF= {0,1};			
										
			IntBuffer CubeIndexBuf    = GLBuffers.newDirectIntBuffer(CubeIndex);
			IntBuffer CubeIndexWFBuf    = GLBuffers.newDirectIntBuffer(CubeIndexWF);
			FloatBuffer positionDataBuf = GLBuffers.newDirectFloatBuffer(positionData);			
			FloatBuffer normalDataBuf   = GLBuffers.newDirectFloatBuffer(positionData.length);		
									
			if(jfrog.Common.support_VBO){					
				gl.glGenBuffers( 5, VBO_Vertices, 0);								// Get A Valid Name
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );	// Bind The Buffer
				gl.glBufferData( GL2.GL_ELEMENT_ARRAY_BUFFER, CubeIndexBuf.capacity() * 4,CubeIndexBuf, GL2.GL_STATIC_DRAW );				
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );			// Bind The Buffer
				gl.glBufferData( GL2.GL_ARRAY_BUFFER, positionDataBuf.capacity() * 4, positionDataBuf, GL2.GL_STATIC_DRAW );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );			// Bind The Buffer
				gl.glBufferData( GL2.GL_ARRAY_BUFFER, normalDataBuf.capacity() * 4, normalDataBuf, GL2.GL_STATIC_DRAW );							
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[4] );	// Bind The Buffer
				gl.glBufferData( GL2.GL_ELEMENT_ARRAY_BUFFER, CubeIndexWFBuf.capacity() * 4,CubeIndexBuf, GL2.GL_STATIC_DRAW );								
			}else{				
				VBO_Vertices[0] = gl.glGenLists(1);			
				gl.glNewList(VBO_Vertices[0], GL2.GL_COMPILE_AND_EXECUTE);				
				gl.glNormalPointer  (    GL2.GL_FLOAT, 0, normalDataBuf );				
				gl.glVertexPointer  ( 3, GL2.GL_FLOAT, 0, positionDataBuf );			
				gl.glDrawElements   (    GL2.GL_LINES, 2, GL2.GL_UNSIGNED_INT, CubeIndexBuf);
				gl.glEndList();							
				
				VBO_Vertices[4] = gl.glGenLists(1);			
				gl.glNewList(VBO_Vertices[4], GL2.GL_COMPILE_AND_EXECUTE);				
				gl.glNormalPointer  (    GL2.GL_FLOAT, 0, 0 );				
				gl.glVertexPointer  ( 3, GL2.GL_FLOAT, 0, positionDataBuf );			
				gl.glDrawElements   (    GL2.GL_LINES, 2, GL2.GL_UNSIGNED_INT, CubeIndexWFBuf);
				gl.glEndList();								
			}
		}else if(VBO_Vertices[0]>0){
			if(jfrog.Common.support_VBO){	
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );	gl.glNormalPointer  (    GL2.GL_FLOAT, 0, 0 );							
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );	gl.glVertexPointer  ( 3, GL2.GL_FLOAT, 0, 0 );								
				gl.glDrawElements( GL2.GL_LINES, 2, GL2.GL_UNSIGNED_INT, 0);												
			}else{			
				gl.glCallList(VBO_Vertices[0]);						
			}			
		}
					
	}
}

