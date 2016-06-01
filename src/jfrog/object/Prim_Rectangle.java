package jfrog.object;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL2;

import com.jogamp.opengl.util.GLBuffers;

public class Prim_Rectangle extends Base {
	float PosX;    float PosY;    float PosZ;					
	float WidthX;  float WidthY;  float WidthZ;
	float LengthX; float LengthY; float LengthZ;
		
	public int ChunkId() {	return 42020; }
	public boolean isCompactible() {return true;}	


	public String toString() {
		return "Prim_Rectangle [PosX=" + PosX + ", PosY=" + PosY + ", PosZ="
				+ PosZ + ", WidthX=" + WidthX + ", WidthY=" + WidthY
				+ ", WidthZ=" + WidthZ + ", LengthX=" + LengthX + ", LengthY="
				+ LengthY + ", LengthZ=" + LengthZ + "]";
	}
	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};
		id 			= Factory.getUInt  (din, BytesRead);
		PosX    	= Factory.getFloat (din, BytesRead);
		PosY    	= Factory.getFloat (din, BytesRead);
		PosZ    	= Factory.getFloat (din, BytesRead);
		WidthX    	= Factory.getFloat (din, BytesRead);
		WidthY    	= Factory.getFloat (din, BytesRead);
		WidthZ    	= Factory.getFloat (din, BytesRead);		
		LengthX    	= Factory.getFloat (din, BytesRead);
		LengthY    	= Factory.getFloat (din, BytesRead);
		LengthZ    	= Factory.getFloat (din, BytesRead);						
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(52);		
		Factory.putUInt  (toReturn, id);	
		Factory.putFloat (toReturn, PosX);
		Factory.putFloat (toReturn, PosY);
		Factory.putFloat (toReturn, PosZ);
		Factory.putFloat (toReturn, WidthX);		
		Factory.putFloat (toReturn, WidthY);
		Factory.putFloat (toReturn, WidthZ);		
		Factory.putFloat (toReturn, LengthX);		
		Factory.putFloat (toReturn, LengthY);
		Factory.putFloat (toReturn, LengthZ);			
		return toReturn;		
	}			
	
	public void draw(GL2 gl) {				
		if(VBO_Vertices[0]==0){
			float C1X = PosX+WidthX+LengthX;	float C1Y = PosY+WidthY+LengthY;	float C1Z = PosZ+WidthZ+LengthZ;
			float C2X = PosX+WidthX-LengthX;	float C2Y = PosY+WidthY-LengthY;	float C2Z = PosZ+WidthZ-LengthZ;				 
			float C3X = PosX-WidthX-LengthX;	float C3Y = PosY-WidthY-LengthY;	float C3Z = PosZ-WidthZ-LengthZ;
			float C4X = PosX-WidthX+LengthX;	float C4Y = PosY-WidthY+LengthY;	float C4Z = PosZ-WidthZ+LengthZ;
			
			float [] positionData   = new float[]{
			C1X, C1Y,	C1Z,	C2X, C2Y,	C2Z,	C3X, C3Y,	C3Z,	C4X, C4Y,	C4Z,
			C1X, C1Y,	C1Z,	C4X, C4Y,	C4Z,	C3X, C3Y,	C3Z,	C2X, C2Y,	C2Z,
	};							
			int [] CubeIndex  = {0,1,2,3, 4,5,6,7};
			int [] CubeIndexWF= {0,1,2,3};			
			float [] normalData = SetNormalArray(positionData, CubeIndex, 0, CubeIndex.length, 4);	
									
			IntBuffer CubeIndexBuf    = GLBuffers.newDirectIntBuffer(CubeIndex);
			IntBuffer CubeIndexWFBuf    = GLBuffers.newDirectIntBuffer(CubeIndexWF);
			FloatBuffer positionDataBuf = GLBuffers.newDirectFloatBuffer(positionData);			
			FloatBuffer normalDataBuf   = GLBuffers.newDirectFloatBuffer(normalData);		
									
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
				gl.glDrawElements   (    GL2.GL_QUADS, 8, GL2.GL_UNSIGNED_INT, CubeIndexBuf);
				gl.glEndList();							
				
				VBO_Vertices[4] = gl.glGenLists(1);			
				gl.glNewList(VBO_Vertices[4], GL2.GL_COMPILE_AND_EXECUTE);				
				gl.glNormalPointer  (    GL2.GL_FLOAT, 0, 0 );				
				gl.glVertexPointer  ( 3, GL2.GL_FLOAT, 0, positionDataBuf );			
				gl.glDrawElements   (    GL2.GL_LINE_LOOP, 4, GL2.GL_UNSIGNED_INT, CubeIndexWFBuf);
				gl.glEndList();								
			}
		}else if(VBO_Vertices[0]>0){
			if(jfrog.Common.support_VBO){	
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );	gl.glNormalPointer  (    GL2.GL_FLOAT, 0, 0 );							
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );	gl.glVertexPointer  ( 3, GL2.GL_FLOAT, 0, 0 );								
				gl.glDrawElements( GL2.GL_QUADS, 8, GL2.GL_UNSIGNED_INT, 0);												
			}else{			
				gl.glCallList(VBO_Vertices[0]);						
			}			
		}
					
	}
}

